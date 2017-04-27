package ws.isak.bridge.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;

import java.util.Locale;

import ws.isak.bridge.R;
import ws.isak.bridge.common.Memory;
import ws.isak.bridge.common.Shared;

import ws.isak.bridge.common.SwapCardData;
import ws.isak.bridge.events.engine.SwapSelectedCardsEvent;
import ws.isak.bridge.events.engine.SwapUnselectCardsEvent;
import ws.isak.bridge.events.engine.SwapGameWonEvent;

import ws.isak.bridge.model.GameState;
import ws.isak.bridge.model.SwapGame;
import ws.isak.bridge.ui.SwapBoardView;
import ws.isak.bridge.ui.SwapControlsView;
import ws.isak.bridge.ui.PopupManager;
import ws.isak.bridge.utils.Clock;
import ws.isak.bridge.utils.SwapTileCoordinates;
import ws.isak.bridge.utils.TimerCountdown;
import ws.isak.bridge.utils.FontLoader;
import ws.isak.bridge.utils.FontLoader.Font;

/*
 * Class SwapGameFragment creates the view for the swapping game fragment, including the clock, and
 * overrides the events for unselecting the cards to swap, switching the cards to swap, and winning
 * the game.
 *
 * @author isak
 */

public class SwapGameFragment extends BaseFragment implements View.OnClickListener {

    public final String TAG = "SwapGameFragment";

    private SwapBoardView mSwapBoardView;
    private SwapControlsView mSwapControlsView;

    //timer controls/content
    private TextView mTime;
    private ImageView mTimeImage;
    private ImageView mTimerPlayPause;
    private ImageView mTimerRestart;        //TODO later, this functionality may not be necessary?


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d (TAG, "method onCreateView");
        //create the view for the swap game fragment
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.swap_game_fragment, container, false);
        view.setClipChildren(false);
        ((ViewGroup)view.findViewById(R.id.swap_game_board)).setClipChildren(false);
        mTime = (TextView) view.findViewById(R.id.time_bar_text_view);
        mTimeImage = (ImageView) view.findViewById(R.id.time_bar_image);
        mTimerPlayPause = (ImageView) view.findViewById(R.id.time_bar_play_pause_button);
        mTimerRestart = (ImageView) view.findViewById(R.id.time_bar_restart_button);
        mTimerRestart.setVisibility(View.INVISIBLE);
        mTimerPlayPause.setOnClickListener(this);
        mTimerRestart.setOnClickListener(this);
        FontLoader.setTypeface(Shared.context, new TextView[] {mTime}, Font.ANGRYBIRDS);
        //the swap game audio controls - TODO Does it make sense for these to be in a separate View from the board?
        mSwapControlsView = SwapControlsView.fromXml(getActivity().getApplicationContext(), view);
        FrameLayout controlsFrameLayout = (FrameLayout) view.findViewById(R.id.swap_game_audio_controls);
        controlsFrameLayout.addView(mSwapControlsView);
        controlsFrameLayout.setClipChildren(false);
        Log.d (TAG, "method onCreateView: created layout controlsFrameLayout: " + controlsFrameLayout);
        //the swap game board
        mSwapBoardView = SwapBoardView.fromXml(getActivity().getApplicationContext(), view);
        FrameLayout playFrameLayout = (FrameLayout) view.findViewById(R.id.swap_game_play_container);
        playFrameLayout.addView(mSwapBoardView);
        playFrameLayout.setClipChildren(false);
        Log.d (TAG, "method onCreateView: created layout playFrameLayout: " + playFrameLayout);

        // build board
        buildBoard();

        Shared.eventBus.listen(SwapSelectedCardsEvent.TYPE, this);
        Shared.eventBus.listen(SwapGameWonEvent.TYPE, this);
        //FIXME - what does this event do? Shared.eventBus.listen(SwapUnselectCardsEvent.TYPE, this);

        //TODO build audio controls
        //Shared.eventBus.listen(SwapPlayRowAudioEvent.TYPE, this);
        //Shared.eventBus.listen(SwapPauseRowAudioEvent.TYPE, this);

        return view;
    }

    @Override
    public void onClick (View view) {
        switch (view.getId()) {
            case R.id.time_bar_play_pause_button:
                pauseTimerButton();
                break;
            case R.id.time_bar_restart_button:
                restartGameButton();
                break;
        }
    }

    private void pauseTimerButton () {
        if (!Shared.currentSwapGame.gameClock.isClockPaused()) {   //isClockPaused false means playing
            mTimerRestart.setVisibility(View.VISIBLE);
            Shared.currentSwapGame.gameClock.pauseClock();         //call pauseClock and make next button restart play
            mTimerPlayPause.setImageResource(R.drawable.timer_play_button);
        }
        else {
            mTimerRestart.setVisibility(View.INVISIBLE);
            Shared.currentSwapGame.gameClock.resumeClock();
            mTimerPlayPause.setImageResource(R.drawable.timer_pause_button);
        }
    }

    private void restartGameButton () {
        //TODO decide if this button should do anything - if so how does database handle incomplete games?
        // - Shared.currentMatchGame.gameClock.cancelClock();
    }

    @Override
    public void onDestroy() {
        Shared.eventBus.unlisten(SwapSelectedCardsEvent.TYPE, this);
        Shared.eventBus.unlisten(SwapGameWonEvent.TYPE, this);
        //FIXME - do we need this? Shared.eventBus.unlisten(SwapUnselectCardsEvent.TYPE, this);

        //TODO build audio controls
        //Shared.eventBus.unlisten(SwapPlayRowAudioEvent.TYPE, this);
        //Shared.eventBus.unlisten(SwapPauseRowAudioEvent.TYPE, this);

        super.onDestroy();
    }

    private void buildBoard() {
        Log.d (TAG, "method buildBoard");
        SwapGame swapGame = Shared.engine.getActiveSwapGame();
        Log.d (TAG, "method buildBoard: time: " + Shared.currentSwapGame.swapBoardConfiguration.getGameTime());
        long time = Shared.currentSwapGame.swapBoardConfiguration.getGameTime();
        setTime(time);
        Log.d (TAG, "method buildBoard: check pointer to game: Shared.engine.getActiveSwapGame: " +
                    Shared.engine.getActiveSwapGame() + " | Shared.currentSwapGame : " + Shared.currentSwapGame);
        mSwapBoardView.setBoard(swapGame);
        startClock(time);
    }

    /*
     * Method setTime converts the gameplay time in millis to minutes and seconds in order to set
     * the countdown clock on the screen //TODO CHECK THIS IS TRUE?!
     */
    private void setTime(long time) {
        Log.d (TAG, "method setTime: input time (ms): " + time);
        int timeInSeconds = (int) Math.ceil ((double) time / 1000);
        Log.d (TAG, "              : timeInSeconds: " + timeInSeconds);
        int min = timeInSeconds / 60;
        Log.d (TAG, "              : min: " + min);
        int sec = timeInSeconds - min*60;
        Log.d (TAG, "              : sec: " + sec + " | calling mTime.setText");
        mTime.setText(" " + String.format(Locale.ENGLISH, "%02d", min) + ":" + String.format(Locale.ENGLISH, "%02d", sec));
    }

    private void startClock(long time) {
        Log.d (TAG, "method startClock: input time(ms): " + time);
        Shared.currentSwapGame.gameClock.startClock(time, 1000, new TimerCountdown() {

            @Override
            public void onTick(long millisUntilFinished) {
                Log.d (TAG, "Clock: onTick: millisUntilFinished: " + millisUntilFinished);
                setTime (millisUntilFinished);
            }

            @Override
            public void onFinish() {
                Log.d (TAG, "method startClock: overriding onFinish");
                mTimerPlayPause.setVisibility(View.INVISIBLE);          //TODO - does this make controls disappear on finish?
                mTimerRestart.setVisibility(View.INVISIBLE);
                setTime(0);
            }
        });
    }

    public void onEvent (SwapSelectedCardsEvent event) {

        // start of SwapSelectedCardsEvent
        Log.d (TAG, "onEvent SwapSelectedCardsEvent: event.id1: " + event.id1 + " event.id2: " + event.id2 + " *** AT START OF method ***");
        SwapTileCoordinates card1Coords = event.id1;
        SwapTileCoordinates card2Coords = event.id2;
        SwapCardData card1Data = Shared.userData.getCurSwapGameData().getSwapCardDataFromSwapBoardMap(card1Coords);
        SwapCardData card2Data = Shared.userData.getCurSwapGameData().getSwapCardDataFromSwapBoardMap(card2Coords);
        Log.d (TAG, "onEventSwapSelectedCardsEvent: card1Coords: " + card1Coords + " | card2Coords: " + card2Coords + " | card1Data ID: " + card1Data.getCardID() + " | card2Data ID: " + card2Data.getCardID());

        //TODO prior to swap, append the current board Map to Shared.swapGameData.swapGameMapList
        //TODO make a new copy of the board map
        //Swap the coordinates associated with the two SwapCardData objects
        switchTileCoordinates(card1Coords, card2Coords);

        //FIXME - push the new cards back into the board on the new Map?
        Shared.currentSwapGame.swapBoardArrangement.setCardOnBoard(card1Coords, card1Data);
        Shared.currentSwapGame.swapBoardArrangement.setCardOnBoard(card2Coords, card2Data);

        //TODO what do I need to do to either animate their swapping or at least redraw all the cards on the board?
        //FIXME - does this work ? - do I still need to redraw the board?
        mSwapBoardView.swapCards(card1Coords, card2Coords);

        //Check if game is won
        boolean winning = true;     //TODO is this safe to default to true?
        for (int i = 0; i < Shared.currentSwapGame.swapBoardConfiguration.getSwapDifficulty(); i++ ) {        //for each row on the board
            for (int j = 0; j < 4; j++) {       //for each tile in row
                SwapTileCoordinates targetCoords = new SwapTileCoordinates(i, j);
                SwapCardData cardOnTile = Shared.userData.getCurSwapGameData().getSwapCardDataFromSwapBoardMap(targetCoords);
                if (cardOnTile.getCardID().getSwapCardSpeciesID() != i || cardOnTile.getCardID().getSwapCardSegmentID() != j) {
                    winning = false;
                }
            }
        }
        if (winning) {
            int passedSeconds = (int) (Shared.currentSwapGame.gameClock.getPassedTime() / 1000);
            Log.d (TAG, "onEvent SwapSelectedCardsEvent: winning: " + winning + " | passedSeconds: " + passedSeconds);     //TODO check passed time is right
            Clock.getInstance().pauseClock();
            long totalTimeInMillis = Shared.currentSwapGame.swapBoardConfiguration.getGameTime();
            int totalTime = (int) Math.ceil((double) totalTimeInMillis / 1000); //TODO is this enough or should we convert all to long ms
            GameState gameState = new GameState();
            Shared.currentSwapGame.gameState = gameState;
            // remained seconds
            gameState.remainingTimeInSeconds = totalTime - passedSeconds;

            // calculate stars and score from the amount of time that has elapsed as a ratio
            // of total time allotted for the game.  When calculating this we still have incorporated
            // the time based on the difficultyLevel as well as the time to play back the samples
            if (passedSeconds <= totalTime / 2) {gameState.achievedStars = 3; }
            else if (passedSeconds <= totalTime - totalTime / 5) {gameState.achievedStars = 2; }
            else if (passedSeconds < totalTime) {gameState.achievedStars = 1; }
            else {gameState.achievedStars = 0;}
            // calculate the score
            gameState.achievedScore = Shared.currentSwapGame.swapBoardConfiguration.getSwapDifficulty() * gameState.remainingTimeInSeconds;     //FIXME - was difficultyLevel, now getSwapDifficulty() , check consistency
            // save to memory
            Memory.saveSwap(Shared.currentSwapGame.swapBoardConfiguration.difficultyLevel, gameState.achievedStars);
            //trigger the MatchGameWonEvent
            Shared.eventBus.notify(new SwapGameWonEvent(gameState), 1200);      //TODO what is 1200 doing here? convert to xml
        }
    }

    private void switchTileCoordinates (SwapTileCoordinates tile1, SwapTileCoordinates tile2) {
        //create temp coordinates, initialized to off the board
        SwapTileCoordinates temp = new SwapTileCoordinates(-1, -1);
        //copy tile 2 to temp
        temp.setSwapCoordRow (tile2.getSwapCoordRow());
        temp.setSwapCoordCol (tile2.getSwapCoordCol());
        //copy tile 1 to tile 2
        tile2.setSwapCoordRow (tile1.getSwapCoordRow());
        tile2.setSwapCoordCol (tile1.getSwapCoordCol());
        //copy temp to tile 1
        tile1.setSwapCoordRow (temp.getSwapCoordRow());
        tile1.setSwapCoordCol (temp.getSwapCoordCol());
    }

    @Override
    public void onEvent(SwapGameWonEvent event) {
        //Log.d (TAG, "overriding method onEvent (SwapGameWonEvent)");
        //We print out all of the collected array data here?
        for (int i = 0; i < Shared.userData.getCurSwapGameData().getNumTurnsTaken(); i++) {
            if (i < 10) {
                Log.d(TAG, "*****| Turn: 0" + i + " | Turn Time: " + Shared.userData.getCurSwapGameData().queryTurnDurationsArray(i) +
                        " | Play Duration: " + Shared.userData.getCurSwapGameData().queryGamePlayDurations(i) +
                        " | BoardMap: " + Shared.userData.getCurSwapGameData().querySwapGameMapList(i));
            } else {
                Log.d(TAG, "*****| Turn: " + i + " | Turn Time: " + Shared.userData.getCurSwapGameData().queryTurnDurationsArray(i) +
                        " | Play Duration: " + Shared.userData.getCurSwapGameData().queryGamePlayDurations(i) +
                        " | BoardMap: " + Shared.userData.getCurSwapGameData().querySwapGameMapList(i));
            }
        }
        //append SwapGameData to userData array
        Shared.userData.appendSwapGameData(Shared.userData.getCurSwapGameData());     //append the MatchGameData for completed game to

        //TODO insert current swapGameData into database
        //SwapGameDataORM.insertSwapGameData(Shared.userData.getCurSwapGameData());

        //reset flags
        Shared.userData.getCurSwapGameData().setGameStarted(false);                  //reset the gameStarted boolean to false
        //null the pointer to curSwapGameData once it has been appended to the UserData array of SwapGameData objects
        Shared.userData.setCurSwapGameData(null);
        mTime.setVisibility(View.GONE);
        mTimeImage.setVisibility(View.GONE);
        PopupManager.showPopupWon(event.gameState);
    }

    @Override
    public void onEvent(SwapUnselectCardsEvent event) {
        //Log.d (TAG, "overriding method onEvent (MatchFlipDownCardsEvent)");
        mSwapBoardView.unSelectAll();
    }
}