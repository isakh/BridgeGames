package ws.isak.bridge.fragments;

import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;
import android.content.res.AssetFileDescriptor;
import android.widget.Toast;

import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import ws.isak.bridge.R;
import ws.isak.bridge.common.Audio;
import ws.isak.bridge.common.Memory;
import ws.isak.bridge.common.Shared;
import ws.isak.bridge.database.SwapGameDataORM;

import ws.isak.bridge.common.SwapCardData;
import ws.isak.bridge.events.engine.SwapPauseRowAudioEvent;
import ws.isak.bridge.events.engine.SwapPlayRowAudioEvent;
import ws.isak.bridge.events.engine.SwapResetRowAudioEvent;
import ws.isak.bridge.events.engine.SwapGameWonEvent;

import ws.isak.bridge.events.ui.SwapSelectedCardsEvent;
import ws.isak.bridge.events.ui.SwapUnselectCardsEvent;

import ws.isak.bridge.model.GameState;
import ws.isak.bridge.model.SwapGame;
import ws.isak.bridge.ui.SwapBoardView;
import ws.isak.bridge.ui.SwapTileView;
import ws.isak.bridge.ui.SwapControlsView;
import ws.isak.bridge.ui.PopupManager;
import ws.isak.bridge.utils.Clock;
import ws.isak.bridge.utils.SwapTileCoordinates;
import ws.isak.bridge.utils.TimerCountdown;
import ws.isak.bridge.utils.FontLoader;
import ws.isak.bridge.utils.FontLoader.Font;

/*
 * Class SwapGameFragment hosts the view for the swapping game fragment, including the clock, and
 * overrides the events for unselecting the cards to swap, switching the cards to swap, and winning
 * the game.
 *
 * @author isak
 */

public class SwapGameFragment extends BaseFragment implements View.OnClickListener {

    public final String TAG = "SwapGameFragment";
    public static String URI_AUDIO = "raw://";
    
    private SwapBoardView mSwapBoardView;
    private SwapControlsView mSwapControlsView;

    //timer controls/content
    private TextView mTime;
    private ImageView mTimeImage;
    private ImageView mTimerPlayPause;
    private ImageView mTimerRestart;        //TODO later, this functionality may not be necessary?


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "method onCreateView");
        //create the view for the swap game fragment
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.swap_game_fragment, container, false);
        view.setClipChildren(false);
        ((ViewGroup) view.findViewById(R.id.swap_game_board)).setClipChildren(false);
        mTime = (TextView) view.findViewById(R.id.time_bar_text_view);
        mTimeImage = (ImageView) view.findViewById(R.id.time_bar_image);
        mTimerPlayPause = (ImageView) view.findViewById(R.id.time_bar_play_pause_button);
        mTimerRestart = (ImageView) view.findViewById(R.id.time_bar_restart_button);
        mTimerRestart.setVisibility(View.INVISIBLE);
        mTimerPlayPause.setOnClickListener(this);
        mTimerRestart.setOnClickListener(this);
        FontLoader.setTypeface(Shared.context, new TextView[]{mTime}, Font.ANGRYBIRDS);
        //the swap game audio controls - TODO Does it make sense for these to be in a separate View from the board?
        mSwapControlsView = SwapControlsView.fromXml(getActivity().getApplicationContext(), view);
        FrameLayout controlsFrameLayout = (FrameLayout) view.findViewById(R.id.swap_game_audio_controls);
        controlsFrameLayout.addView(mSwapControlsView);
        controlsFrameLayout.setClipChildren(false);
        Log.d(TAG, "method onCreateView: created layout controlsFrameLayout: " + controlsFrameLayout);
        //the swap game board
        mSwapBoardView = SwapBoardView.fromXml(getActivity().getApplicationContext(), view);
        FrameLayout playFrameLayout = (FrameLayout) view.findViewById(R.id.swap_game_play_container);
        playFrameLayout.addView(mSwapBoardView);
        playFrameLayout.setClipChildren(false);
        Log.d(TAG, "method onCreateView: created layout playFrameLayout: " + playFrameLayout);

        // build board
        buildBoard();

        Shared.eventBus.listen(SwapSelectedCardsEvent.TYPE, this);
        Shared.eventBus.listen(SwapUnselectCardsEvent.TYPE, this);
        Shared.eventBus.listen(SwapGameWonEvent.TYPE, this);
        Shared.eventBus.listen(SwapPlayRowAudioEvent.TYPE, this);
        Shared.eventBus.listen(SwapResetRowAudioEvent.TYPE, this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.time_bar_play_pause_button:
                pauseTimerButton();
                break;
            case R.id.time_bar_restart_button:
                restartGameButton();
                break;
        }
    }

    private void pauseTimerButton() {
        if (!Shared.currentSwapGame.gameClock.isClockPaused()) {   //isClockPaused false means playing
            mTimerRestart.setVisibility(View.VISIBLE);
            Shared.currentSwapGame.gameClock.pauseClock();         //call pauseClock and make next button restart play
            mTimerPlayPause.setImageResource(R.drawable.timer_play_button);
        } else {
            mTimerRestart.setVisibility(View.INVISIBLE);
            Shared.currentSwapGame.gameClock.resumeClock();
            mTimerPlayPause.setImageResource(R.drawable.timer_pause_button);
        }
    }

    private void restartGameButton() {
        // TODO decide if this button should do anything - if so how does database handle incomplete games?
        // - Shared.currentSwapGame.gameClock.cancelClock();
    }

    @Override
    public void onDestroy() {
        Shared.eventBus.unlisten(SwapSelectedCardsEvent.TYPE, this);
        Shared.eventBus.unlisten(SwapUnselectCardsEvent.TYPE, this);
        Shared.eventBus.unlisten(SwapGameWonEvent.TYPE, this);
        Shared.eventBus.unlisten(SwapPlayRowAudioEvent.TYPE, this);
        Shared.eventBus.unlisten(SwapResetRowAudioEvent.TYPE, this);

        super.onDestroy();
    }

    private void buildBoard() {
        Log.d(TAG, "method buildBoard");
        SwapGame swapGame = Shared.engine.getActiveSwapGame();
        Log.d(TAG, "method buildBoard: time: " + Shared.currentSwapGame.swapBoardConfiguration.getGameTime());
        long time = Shared.currentSwapGame.swapBoardConfiguration.getGameTime();
        setTime(time);
        Log.d(TAG, "method buildBoard: check pointer to game: Shared.engine.getActiveSwapGame: " +
                Shared.engine.getActiveSwapGame() + " | Shared.currentSwapGame : " + Shared.currentSwapGame);
        mSwapControlsView.populateControls(swapGame);
        mSwapBoardView.setBoard(swapGame);
        startClock(time);
        Log.d(TAG, "method buildBoard: calling method debugHashMaps");
        debugHashMaps("class SwapGameFragment: method buildBoard");
    }

    /*
     * Method setTime converts the gameplay time in millis to minutes and seconds in order to set
     * the countdown clock on the screen //TODO CHECK THIS IS TRUE?!
     */
    private void setTime(long time) {
        //Log.d (TAG, "method setTime: input time (ms): " + time);
        int timeInSeconds = (int) Math.ceil((double) time / 1000);
        //Log.d (TAG, "              : timeInSeconds: " + timeInSeconds);
        int min = timeInSeconds / 60;
        //Log.d (TAG, "              : min: " + min);
        int sec = timeInSeconds - min * 60;
        //Log.d (TAG, "              : sec: " + sec + " | calling mTime.setText");
        mTime.setText(" " + String.format(Locale.ENGLISH, "%02d", min) + ":" + String.format(Locale.ENGLISH, "%02d", sec));
    }

    private void startClock(long time) {
        Log.d(TAG, "method startClock: input time(ms): " + time);
        Shared.currentSwapGame.gameClock.startClock(time, 1000, new TimerCountdown() {

            @Override
            public void onTick(long millisUntilFinished) {
                //Log.d (TAG, "Clock: onTick: millisUntilFinished: " + millisUntilFinished);
                setTime(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                Log.d(TAG, "method startClock: overriding onFinish");
                mTimerPlayPause.setVisibility(View.INVISIBLE);          //TODO - does this make controls disappear on finish?
                mTimerRestart.setVisibility(View.INVISIBLE);
                setTime(0);
            }
        });
    }

    // this method is called when two different tiles have been selected: it takes the current state
    // of the hashmap showing the boards (coords, cardData) and switches the cardData objects associated
    // with the selected coordinates.
    @Override
    public void onEvent(SwapSelectedCardsEvent event) {

        //check state of board hashmaps <coords, cardData> & <coords, TileViews>
        Log.i(TAG, "onEvent SwapSelectedCardsEvent @ start: calling debugHashMaps - set logging to verbose to read these in logcat");
        debugHashMaps("class SwapGameFragment THIS IS THE START: " + event.TAG);

        //verify state of <coords, cards> at start
        debugCoordsDataMap(Shared.userData.getCurSwapGameData().getSwapBoardMap(), "start SwapSelectedCardsEvent, check state of Shared.userData.getCurSwapGameData().getSwapBoardMap");

        //prior to swap, append the current SwapBoardMap (coords, data) to Shared.swapGameData.swapGameMapList
        Shared.userData.getCurSwapGameData().appendToSwapGameMapList(Shared.userData.getCurSwapGameData().getSwapBoardMap());
        //verify that last SwapBoardMap in list is now the current one
        debugCoordsDataMap(Shared.userData.getCurSwapGameData().querySwapGameMapList(Shared.userData.getCurSwapGameData().sizeOfSwapGameMapList() - 1),
                "start SwapSelectedCardsEvent, check successful push of Shared.userData.getCurSwapGameData().getSwapBoardMap to swapGameMapList");

        //make a new copy of the swapBoardMap - for now this is our active map
        HashMap<SwapTileCoordinates, SwapCardData> nextTurnMap = Shared.userData.getCurSwapGameData().getSwapBoardMap();
        debugCoordsDataMap(nextTurnMap, "start SwapSelectedCardsEvent, check nextTurnMap");

        // the swap selected cards event takes two SwapTileCoordinates IDs as input - log them here
        Log.d(TAG, "onEvent(SwapSelectedCardsEvent): event.id1: " + event.id1 + " event.id2: " + event.id2 + " *** AT START OF method ***");

        //create local copies of the target coordinates whose data and images need swapping - passed from calling of event
        //the order of first/second here doesn't change after swap as it is the order in which they were selected
        SwapTileCoordinates firstCardCoords = event.id1;
        SwapTileCoordinates secondCardCoords = event.id2;

        //create local copies of the cardData stored a the coordinates
        //the order first/second here will be switched after the swap
        SwapCardData firstCardData = Shared.userData.getCurSwapGameData().getSwapCardDataFromSwapBoardMap(firstCardCoords);
        SwapCardData secondCardData = Shared.userData.getCurSwapGameData().getSwapCardDataFromSwapBoardMap(secondCardCoords);
        Log.d(TAG, "onEvent(SwapSelectedCardsEvent): firstCardCoords: " + firstCardCoords + " | secondCardCoords: " + secondCardCoords + " | firstCardData ID: " + firstCardData.getCardID() + " | secondCardData ID: " + secondCardData.getCardID());

        //Swap the the two SwapCardData objects associated with the selected coordinates
        nextTurnMap.put(firstCardCoords, secondCardData);
        nextTurnMap.put(secondCardCoords, firstCardData);
        debugCoordsDataMap(nextTurnMap, "SwapSelectedCardsEvent: check UPDATED nextTurnMap AFTER SWAP");

        //set the Shared.userData.getCurSwapGameData swapBoardMap to the new state of nextTurnMap
        Shared.userData.getCurSwapGameData().setSwapBoardMap(nextTurnMap);
        debugCoordsDataMap(Shared.userData.getCurSwapGameData().getSwapBoardMap(), "SwapSelectedCardsEvent, check UPDATED Shared.userData.getCurSwapGameData swapBoardMap after REASSIGNMENT");

        //call debugHashMaps here repeats the previous call to debugCoordsDataMap and adds debugCoordsTileViewMap
        Log.v (TAG, "SwapSelectedCardsEvent: CHECK that <card, coords> maps ABOVE and BELOW are IDENTICAL");
        //FIXME - the bitmaps shouldn't be swapped yet ?????
        Log.v (TAG, "SwapSelectedCardsEvent: CHECK that <card, tiles> maps AT START and BELOW are IDENTICAL still - we will ∆ BITMAPS NEXT");

        debugHashMaps("SwapSelectedCardsEvent: <card, coords> hashmap udpated... time to redraw the board");

        // redraw the board on the screen - this updates the bitmaps (and debugging text) associated
        // with the tileViews found on the swapBoardViewMap (coords, tileViews)
        Log.i (TAG, "onEvent SwapSelectedCardsEvent: GET BITMAPS TO SWAP");
        // store the bitmap associated with the (now new) cardData object on the (original) first selected tile
        Bitmap tile0Bitmap = Shared.userData.getCurSwapGameData().getSwapCardDataFromSwapBoardMap(mSwapBoardView.selectedTiles.get(0)).getCardBitmap();
        //verbose output identifies the <species, segment> on the first tile selected and the bitmap associated with it
        Log.v (TAG, "onEvent SwapSelectedCardsEvent: CardID for bitmap0: < " +
                Shared.userData.getCurSwapGameData().getSwapCardDataFromSwapBoardMap(mSwapBoardView.selectedTiles.get(0)).getCardID().getSwapCardSpeciesID() +
                "," +
                Shared.userData.getCurSwapGameData().getSwapCardDataFromSwapBoardMap(mSwapBoardView.selectedTiles.get(0)).getCardID().getSwapCardSegmentID() +
                " > | bitmap:" + tile0Bitmap);
        // store the bitmap associated with the cardData object on the second selected tile
        Bitmap tile1Bitmap = Shared.userData.getCurSwapGameData().getSwapCardDataFromSwapBoardMap(mSwapBoardView.selectedTiles.get(1)).getCardBitmap();
        //verbose output identifies the <species, segment> on the first tile selected and the bitmap associated with it
        Log.v (TAG, "onEvent SwapSelectedCardsEvent: Card for bitmap1: < " +
                Shared.userData.getCurSwapGameData().getSwapCardDataFromSwapBoardMap(mSwapBoardView.selectedTiles.get(1)).getCardID().getSwapCardSpeciesID() +
                "," +
                Shared.userData.getCurSwapGameData().getSwapCardDataFromSwapBoardMap(mSwapBoardView.selectedTiles.get(1)).getCardID().getSwapCardSegmentID() +
                " > | bitmap:" + tile1Bitmap);

        //get the tileView objects associated with the selected tiles' coordinates on the tileViewMap
        SwapTileView tile0View = mSwapBoardView.mTileViewMap.get(mSwapBoardView.selectedTiles.get(0));
        SwapTileView tile1View = mSwapBoardView.mTileViewMap.get(mSwapBoardView.selectedTiles.get(1));
        Log.d (TAG, " SwapTileViews to receive swapped bitmaps: tile0View: " + tile0View + " | tile1View: " + tile1View);

        //set the corresponding switched bitmaps on the tileViews - at first this will also swap their filters
        Log.d (TAG, " onEvent SwapSelectedCardsEvent: ... SWAPPING BITMAPS");
        tile0View.setTileImage(tile0Bitmap, "[class SwapGameFragment: SwapSelectedCardsEvent: setting tile0View to bitmap from old tile1View]");
        tile1View.setTileImage(tile1Bitmap, "[class SwapGameFragment: SwapSelectedCardsEvent: setting tile1View to bitmap from old tile0View]");
        tile0View.postInvalidate();
        tile1View.postInvalidate();
        Log.d (TAG, "onEvent SwapSelectedCardsEvent: setTileImage called on tiles to swap with updated bitmaps");

        //TODO - remove debugging text when working
        //and update TextViews with appropriate text
        Log.d (TAG, " onEvent SwapSelectedCardsEvent: ... SWAPPING TEXT");
        tile0View.setTileDebugText(mSwapBoardView.getSwapTileMap(), mSwapBoardView.selectedTiles.get(0));
        tile1View.setTileDebugText(mSwapBoardView.getSwapTileMap(), mSwapBoardView.selectedTiles.get(1));
        mSwapBoardView.postInvalidate();
        //TODO end debugging text

        //FIXME - need to verify if the AsyncTasks have completed updating the UI bitmaps
        debugHashMaps("SwapSelectedCardsEvent: CHECK IF AsyncTasks have completed... BITMAPS UPDATED");

        //TODO - this is outputting before the AsyncTask completes so the logging is out of sync- fix? comment in logs
        //unselect both cards in the pair to swap - this should remove their filters
        Log.d (TAG, "onEvent SwapSelectedCardsEvent: TILES SWAPPED (with possible AsyncTask delay), calling SwapUnselectCardsEvent");
        Shared.eventBus.notify(new SwapUnselectCardsEvent(mSwapBoardView.selectedTiles), 200);      //TODO keep a .2 second delay here? is this redundant? I want tiles to switch color on swap, then go white

        //testing whether game has been won
        //if we only need to win in easy mode
        if (Shared.userData.getCurSwapGameData().getWinningDifficulty() == 0) {
            boolean winningEasy = checkWinningEasy();

            if (winningEasy) {
                //append the updated map to the map list
                Shared.userData.getCurSwapGameData().appendToSwapGameMapList(Shared.userData.getCurSwapGameData().getSwapBoardMap());
                //calculate current game state variables
                int passedSeconds = (int) (Shared.currentSwapGame.gameClock.getPassedTime() / 1000);
                Log.d(TAG, "onEvent SwapSelectedCardsEvent: winningEasy: " + winningEasy + " | passedSeconds: " + passedSeconds);     //TODO check passed time is right
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
                if (passedSeconds <= totalTime / 2) {
                    gameState.achievedStars = 3;
                } else if (passedSeconds <= totalTime - totalTime / 5) {
                    gameState.achievedStars = 2;
                } else if (passedSeconds < totalTime) {
                    gameState.achievedStars = 1;
                } else {
                    gameState.achievedStars = 0;
                }
                // calculate the score
                gameState.achievedScore = Shared.currentSwapGame.swapBoardConfiguration.getSwapDifficulty() * gameState.remainingTimeInSeconds;     //FIXME - was difficultyLevel, now getSwapDifficulty() , check consistency
                // save to memory
                Memory.saveSwap(Shared.currentSwapGame.swapBoardConfiguration.difficultyLevel, gameState.achievedStars);
                //trigger the SwapGameWonEvent
                Shared.eventBus.notify(new SwapGameWonEvent(gameState), 1000);      //TODO convert delay to xml
            }
        }
        else if (Shared.userData.getCurSwapGameData().getWinningDifficulty() == 1) {
            boolean winningHard = checkWinningHard();

            if (winningHard) {
                //append the updated map to the map list
                Shared.userData.getCurSwapGameData().appendToSwapGameMapList(Shared.userData.getCurSwapGameData().getSwapBoardMap());
                //calculate current game state variables
                int passedSeconds = (int) (Shared.currentSwapGame.gameClock.getPassedTime() / 1000);
                Log.d(TAG, "onEvent SwapSelectedCardsEvent: winningHard: " + winningHard + " | passedSeconds: " + passedSeconds);     //TODO check passed time is right
                Clock.getInstance().pauseClock();
                long totalTimeInMillis = Shared.currentSwapGame.swapBoardConfiguration.getGameTime();
                int totalTime = (int) Math.ceil((double) totalTimeInMillis / 1000); //TODO is this enough or should we convert all to long ms
                GameState gameState = new GameState();
                Shared.currentSwapGame.gameState = gameState;
                // remained seconds
                gameState.remainingTimeInSeconds = totalTime - passedSeconds;

                // calculate stars and score from the amount of time that has elapsed as a ratio
                // of total time allotted for the game.
                // TODO calculate from time based on the difficultyLevel, time to play back samples, & and winningDifficulty level

                if (passedSeconds <= totalTime / 2) {
                    gameState.achievedStars = 3;
                } else if (passedSeconds <= totalTime - totalTime / 5) {
                    gameState.achievedStars = 2;
                } else if (passedSeconds < totalTime) {
                    gameState.achievedStars = 1;
                } else {
                    gameState.achievedStars = 0;
                }
                // calculate the score
                gameState.achievedScore = Shared.currentSwapGame.swapBoardConfiguration.getSwapDifficulty() * gameState.remainingTimeInSeconds;     //FIXME - was difficultyLevel, now getSwapDifficulty() , check consistency
                // save to memory
                Memory.saveSwap(Shared.currentSwapGame.swapBoardConfiguration.difficultyLevel, gameState.achievedStars);
                //trigger the SwapGameWonEvent
                Shared.eventBus.notify(new SwapGameWonEvent(gameState), 1000);      //TODO convert delay to xml
            }
        }
    }

    private boolean checkWinningEasy () {
        //Check if game is won on easy mode where we will define winningEasy has having one species per row
        boolean winningEasy = true;
        //iterate over each row
        Log.d (TAG, "method checkWinningEasy");
        for (int i = 0; i < Shared.currentSwapGame.swapBoardConfiguration.getSwapDifficulty(); i++) {        //for each row on the board
            //get the species of the first tile as the target for the row
            SwapTileCoordinates targetCoords = new SwapTileCoordinates(i, 0);
            SwapCardData cardOnTile = Shared.userData.getCurSwapGameData().getSwapCardDataFromSwapBoardMap(targetCoords);
            int targetSpecies = cardOnTile.getCardID().getSwapCardSpeciesID();
            for (int j = 1; j < Shared.currentSwapGame.swapBoardConfiguration.swapNumTilesInRow; j++) {
                SwapTileCoordinates nextTileCoords = new SwapTileCoordinates(i, j);
                SwapCardData nextCardOnTile = Shared.userData.getCurSwapGameData().getSwapCardDataFromSwapBoardMap(nextTileCoords);
                int compareSpecies = nextCardOnTile.getCardID().getSwapCardSpeciesID();
                if (targetSpecies != compareSpecies) {
                    Log.d (TAG, "looping checking state of winningEasy: " + winningEasy);
                    winningEasy = false;
                    return winningEasy;
                }
            }
            Log.d (TAG, "finished checking state of winningEasy: " + winningEasy + " for row: i = " + i);
            if (winningEasy) {
                //if a given row is correctly matched, toast the species for the row - FIXME this re-toasts the first one(s) each subsequent time...
                String speciesName = cardOnTile.getSpeciesName();
                Toast.makeText(Shared.context, "Row " + (i + 1) + " correct: " + speciesName, Toast.LENGTH_SHORT).show();
            }
        }
        return winningEasy;
    }

    private boolean checkWinningHard () {
        boolean winningHard = true;
        //iterate over each row
        Log.d(TAG, "method checkWinningHard");
        for (int i = 0; i < Shared.currentSwapGame.swapBoardConfiguration.getSwapDifficulty(); i++) {      //for each row on board
            //get the species of the first tile as the target for the row
            SwapTileCoordinates targetCoords = new SwapTileCoordinates(i, 0);
            SwapCardData cardOnTile = Shared.userData.getCurSwapGameData().getSwapCardDataFromSwapBoardMap(targetCoords);
            int targetSpecies = cardOnTile.getCardID().getSwapCardSpeciesID();
            //check that the target segment is 0, if not we have already failed
            int targetSegment = cardOnTile.getCardID().getSwapCardSegmentID();
            if (targetSegment != 0) {
                winningHard = false;
                return winningHard;
            } else {          //if at the first card in the row is the correct segment... check the rest for species and segment
                for (int j = 1; j < Shared.currentSwapGame.swapBoardConfiguration.swapNumTilesInRow; j++) {
                    SwapTileCoordinates nextTileCoords = new SwapTileCoordinates(i, j);
                    SwapCardData nextCardOnTile = Shared.userData.getCurSwapGameData().getSwapCardDataFromSwapBoardMap(nextTileCoords);
                    if (nextCardOnTile.getCardID().getSwapCardSpeciesID() != targetSpecies) {
                        Log.v(TAG, "method checkWinningHard: next card in row doesn't match species");
                        winningHard = false;
                        return winningHard;
                    } else if (nextCardOnTile.getCardID().getSwapCardSegmentID() != j) {
                        Log.v(TAG, "method checkWinningHard: next card in row segment doesn't match counter 'j'");
                        winningHard = false;
                        return winningHard;
                    }
                }
                Toast.makeText(Shared.context, "", Toast.LENGTH_SHORT).show();
            }
        }
        return winningHard;
    }


    @Override
    public void onEvent(SwapGameWonEvent event) {
        Log.i (TAG, "overriding method onEvent (SwapGameWonEvent): set logging mode to debug for detailed output");
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
        Shared.userData.appendSwapGameData(Shared.userData.getCurSwapGameData());     //append the SwapGameData for completed game to

        //TODO this can be removed once validated
        //display single variable values for game
        Log.d (TAG, "\n\n ..................................... \n\n");
        Log.d (TAG, " SWAP GAME WON EVENT - DISPLAYING ALL DATA COLLECTED ABOUT THE GAME:...........");
        Log.d (TAG, "... Shared.userData.getCurSwapGameDataData: " +
                " | gameStartTimeStamp: " + Shared.userData.getCurSwapGameData().getGameStartTimestamp() +
                " | difficultyLevel selected: " + Shared.userData.getCurSwapGameData().getGameDifficulty() +
                " | winningDifficulty level: " + Shared.userData.getCurSwapGameData().getWinningDifficulty() +
                " | gameDurationAllocated: " + Shared.userData.getCurSwapGameData().getGameDurationAllocated() +
                " | gameStarted: " + Shared.userData.getCurSwapGameData().isGameStarted() +
                " | numTurnsTaken:  " + Shared.userData.getCurSwapGameData().getNumTurnsTaken() +
                " | numPlayDurationsRecorded: " + Shared.userData.getCurSwapGameData().sizeOfPlayDurationsArray() +
                " | numTurnDurationsRecorded: " + Shared.userData.getCurSwapGameData().sizeOfTurnDurationsArray() +
                " | num game maps recorded: " + Shared.userData.getCurSwapGameData().sizeOfSwapGameMapList());
        //display list of turn durations
        for (int i = 0; i < Shared.userData.getCurSwapGameData().sizeOfTurnDurationsArray(); i++ ) {
            Log.d(TAG, "... turnDurations[" + i + "] : " + Shared.userData.getCurSwapGameData().queryTurnDurationsArray(i));
        }
        //display list of elapsed game time durations
        for (int i = 0; i < Shared.userData.getCurSwapGameData().sizeOfPlayDurationsArray(); i++ ) {
            Log.d(TAG, "... playDurations[" + i + "] : " + Shared.userData.getCurSwapGameData().queryGamePlayDurations(i));
        }
        //display all game hashmaps in
        for (int i = 0; i < Shared.userData.getCurSwapGameData().sizeOfSwapGameMapList(); i++) {
            Log.d(TAG, "Map in List @ i: " + i + " | @ pointer: " + Shared.userData.getCurSwapGameData().querySwapGameMapList(i));
            debugCoordsDataMap(Shared.userData.getCurSwapGameData().querySwapGameMapList(i), "STATE OF MAP" + i + " IN GAMEDATA LIST");
        }
        //end validation block

        // insert current swapGameData into database
        SwapGameDataORM.insertSwapGameData(Shared.userData.getCurSwapGameData());

        //reset flags
        Shared.userData.getCurSwapGameData().setGameStarted(false);         //reset the gameStarted boolean to false
        //null the pointer to curSwapGameData once it has been appended to the UserData array of SwapGameData objects
        Shared.userData.setCurSwapGameData(null);
        mTime.setVisibility(View.GONE);
        mTimeImage.setVisibility(View.GONE);
        PopupManager.showSwapPopupWon(event.gameState);
    }

    @Override
    public void onEvent(SwapUnselectCardsEvent event) {
        Log.i (TAG, "overriding method onEvent (SwapUnselectCardsEvent): calling swapBoardView.unSelectAll");
        if (mSwapBoardView.selectedTiles != event.selectedTiles) {
            Log.e (TAG, "ERROR: tiles to unselect do not match");
        }
        else {
            mSwapBoardView.unSelectAll();
            //mSwapBoardView.selectedTiles.clear();
        }
    }

    private void debugHashMaps(String calledFrom) {
        //Log.d(TAG, "#################################################");
        //Log.d(TAG, "method debugHashMaps ... called by: " + calledFrom);
        //Log.d(TAG, "#################################################");
        debugCoordsDataMap(calledFrom);
        mSwapBoardView.debugCoordsTileViewsMap(calledFrom);
    }

    // private method prints verbose output of the current state of the board HashMap that describes
    // the <coords, cardData> relationship
    private void debugCoordsDataMap(String calledFrom) {
        Log.d (TAG, "###############################################################################");
        Log.d (TAG, "method debugCoordsDataMap: BoardMap <Coords, CardID> : called on map: Shared.userData.getCurSwapGameData().getSwapBoardMap(): called by: " + calledFrom);
        Log.d (TAG, "###############################################################################");
        Iterator iterator = Shared.userData.getCurSwapGameData().getSwapBoardMap().entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            //System.out.println(pair.getKey() + " maps to " + pair.getValue());
            SwapTileCoordinates coords = (SwapTileCoordinates) pair.getKey();
            SwapCardData cardData = (SwapCardData) pair.getValue();
            Log.v(TAG, "... debugCoordsDataMap: | coords: < " +
                    coords.getSwapCoordRow() + "," + coords.getSwapCoordCol() +
                    " > | MAPS TO | cardID: < " + cardData.getCardID().getSwapCardSpeciesID() +
                    "," + cardData.getCardID().getSwapCardSegmentID() +
                    " > | bitmap on card: " + cardData.getCardBitmap() +
                    " | address of Map.entry: " + pair);

        }
        Log.d(TAG, " \n ... \n");
    }


    //overloaded version takes a HashMap as input so we can iterate through List of Board Maps
    private void debugCoordsDataMap(HashMap map, String callingMethod) {
        Log.d (TAG, "###############################################################################");
        Log.d (TAG, "method debugCoordsDataMap: BoardMap <Coords, CardID> : called on map: " +
                map + " | called by: " + callingMethod);
        Log.d (TAG, "###############################################################################");
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            //System.out.println(pair.getKey() + " maps to " + pair.getValue());
            SwapTileCoordinates coords = (SwapTileCoordinates) pair.getKey();
            SwapCardData cardData = (SwapCardData) pair.getValue();
            Log.d(TAG, "... debugCoordsDataMap: | coords: < " +
                    coords.getSwapCoordRow() + "," + coords.getSwapCoordCol() +
                    " > | MAPS TO | cardID: < " + cardData.getCardID().getSwapCardSpeciesID() +
                    "," + cardData.getCardID().getSwapCardSegmentID() +
                    " > | bitmap on card: " + cardData.getCardBitmap() +
                    " | address of Map.entry: " + pair);
        }
        //Log.d(TAG, " \n ... \n");
    }

    @Override
    public void onEvent (SwapPlayRowAudioEvent event) {
        ArrayList <Integer> audioResourceIdList;
        int row = event.id;
        Log.d (TAG, "onEvent SwapPlayRowAudioEvent: row to playback: " + row +
                    " | state of Audio.getIsAudioPlaying " + Audio.getIsAudioPlaying());
        //if no other audio is playing
        if (!Audio.getIsAudioPlaying()) {
            audioResourceIdList = getAudioFilesInOrderByID(row);
            playAudioFromFileList (audioResourceIdList);
        }
        //if another row of audio is currently playing
        else {
            Toast.makeText(Shared.context, "please pause playing audio or wait for it to finish", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onEvent (SwapPauseRowAudioEvent event) {
        //
        //TODO
        //
    }

    private ArrayList <Integer> getAudioFilesInOrderByID(int targetRow) {
        ArrayList <Integer> audioResourceIDs = new ArrayList<Integer>(0);
        //for debugging, local array of string for audio resources to be printed before return of method
        ArrayList <String> audioResourceNames = new ArrayList <String>(0);
        for (int i = 0; i < Shared.currentSwapGame.swapBoardConfiguration.swapNumTilesInRow; i++) {
            SwapTileCoordinates targetTileCoords = Shared.userData.getCurSwapGameData().getMapSwapTileCoordinatesFromLoc(new SwapTileCoordinates(targetRow, i));
            int segmentID = Shared.userData.getCurSwapGameData().getSwapCardDataFromSwapBoardMap(targetTileCoords).getCardID().getSwapCardSegmentID();
            switch (segmentID) {
                case 0:
                    String audioResourceName0 = Shared.userData.getCurSwapGameData().getSwapCardDataFromSwapBoardMap(targetTileCoords).getAudioURI0().substring(URI_AUDIO.length());
                    int audioResourceId0 = Shared.context.getResources().getIdentifier(audioResourceName0, "raw", Shared.context.getPackageName());
                    audioResourceNames.add(audioResourceName0);
                    audioResourceIDs.add(audioResourceId0);
                    break;
                case 1:
                    String audioResourceName1 = Shared.userData.getCurSwapGameData().getSwapCardDataFromSwapBoardMap(targetTileCoords).getAudioURI1().substring(URI_AUDIO.length());
                    int audioResourceId1 = Shared.context.getResources().getIdentifier(audioResourceName1, "raw", Shared.context.getPackageName());
                    audioResourceNames.add(audioResourceName1);
                    audioResourceIDs.add(audioResourceId1);
                    break;
                case 2:
                    String audioResourceName2 = Shared.userData.getCurSwapGameData().getSwapCardDataFromSwapBoardMap(targetTileCoords).getAudioURI2().substring(URI_AUDIO.length());
                    int audioResourceId2 = Shared.context.getResources().getIdentifier(audioResourceName2, "raw", Shared.context.getPackageName());
                    audioResourceNames.add(audioResourceName2);
                    audioResourceIDs.add(audioResourceId2);
                    break;
                case 3:
                    String audioResourceName3 = Shared.userData.getCurSwapGameData().getSwapCardDataFromSwapBoardMap(targetTileCoords).getAudioURI3().substring(URI_AUDIO.length());
                    int audioResourceId3 = Shared.context.getResources().getIdentifier(audioResourceName3, "raw", Shared.context.getPackageName());
                    audioResourceNames.add(audioResourceName3);
                    audioResourceIDs.add(audioResourceId3);
                    break;
            }
        }
        Log.d (TAG, "..... AudioResourceNames in Target Row: .....");
        for (int i = 0; i < audioResourceNames.size(); i++) {
            Log.d (TAG, "..... resource @ i: " + i + " : " + audioResourceNames.get(i));
        }
        return audioResourceIDs;
    }

    public void playAudioFromFileList (ArrayList<Integer> audioResourceIdList) {
        ArrayList <MediaPlayer> mPlayerList = new ArrayList<MediaPlayer>();
        mPlayerList.clear();
        for (int i = 0; i < audioResourceIdList.size(); i++) {
            try {
                final MediaPlayer curMediaPlayer = new MediaPlayer();

                AssetFileDescriptor afd = Shared.context.getResources().openRawResourceFd(audioResourceIdList.get(i));
                if (afd == null) return;
                curMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                afd.close();

                curMediaPlayer.prepare();
                curMediaPlayer.setVolume(1f, 1f);
                curMediaPlayer.setLooping(false);
                mPlayerList.add(curMediaPlayer);
                Log.d(TAG, "mPlayerList.size(): " + mPlayerList.size());

                curMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        //todo - can we use this to handle pause button
                        curMediaPlayer.release();
                    }
                });
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < (mPlayerList.size() - 1); i++) {
            mPlayerList.get(i).setNextMediaPlayer(mPlayerList.get(i+1));
        }
        mPlayerList.get(0).start();
    }
}