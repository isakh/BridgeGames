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
import ws.isak.bridge.common.Shared;

import ws.isak.bridge.events.engine.SwapSelectedCardsEvent;
import ws.isak.bridge.events.engine.SwapUnselectCardsEvent;
import ws.isak.bridge.events.engine.SwapGameWonEvent;

import ws.isak.bridge.model.SwapGame;
import ws.isak.bridge.ui.SwapBoardView;
import ws.isak.bridge.ui.PopupManager;
import ws.isak.bridge.utils.Clock;
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
    private TextView mTime;
    private ImageView mTimeImage;
    private ImageView mTimerPlayPause;
    private ImageView mTimerRestart;        //TODO later, this functionality may not be necessary


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
        //the swap game play container
        mSwapBoardView = SwapBoardView.fromXml(getActivity().getApplicationContext(), view);
        FrameLayout playFrameLayout = (FrameLayout) view.findViewById(R.id.swap_game_play_container);
        playFrameLayout.addView(mSwapBoardView);
        playFrameLayout.setClipChildren(false);

        // build board
        buildBoard();

        Shared.eventBus.listen(SwapSelectedCardsEvent.TYPE, this);
        Shared.eventBus.listen(SwapGameWonEvent.TYPE, this);
        Shared.eventBus.listen(SwapUnselectCardsEvent.TYPE, this);

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
        if (!Shared.currentMatchGame.gameClock.isClockPaused()) {   //isClockPaused false means playing
            mTimerRestart.setVisibility(View.VISIBLE);
            Shared.currentMatchGame.gameClock.pauseClock();         //call pauseClock and make next button restart play
            mTimerPlayPause.setImageResource(R.drawable.timer_play_button);
        }
        else {
            mTimerRestart.setVisibility(View.INVISIBLE);
            Shared.currentMatchGame.gameClock.resumeClock();
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
        Shared.eventBus.unlisten(SwapUnselectCardsEvent.TYPE, this);
        super.onDestroy();
    }

    private void buildBoard() {
        Log.d (TAG, "method buildBoard");
        SwapGame swapGame = Shared.engine.getActiveSwapGame();
        Log.d (TAG, "method buildBoard: time: " + Shared.currentSwapGame.swapBoardConfiguration.getGameTime());
        long time = Shared.currentSwapGame.swapBoardConfiguration.getGameTime();
        setTime(time);
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
                setTime(0);
            }
        });
        //FIXME Shared.currentMatchGame.gameClock.pauseClock();
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