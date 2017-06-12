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
import ws.isak.bridge.database.MatchGameDataORM;
import ws.isak.bridge.database.UserDataORM;
import ws.isak.bridge.events.engine.MatchFlipDownCardsEvent;
import ws.isak.bridge.events.engine.MatchGameWonEvent;
import ws.isak.bridge.events.engine.MatchHidePairCardsEvent;
import ws.isak.bridge.model.MatchGame;
import ws.isak.bridge.ui.MatchBoardView;
import ws.isak.bridge.ui.PopupManager;
import ws.isak.bridge.utils.TimerCountdown;
import ws.isak.bridge.utils.FontLoader;
import ws.isak.bridge.utils.FontLoader.Font;

/*
 * Class MatchGameFragment creates the view for the match game fragment, including the clock, and
 * overrides the events for flipping cards down, hiding a pair, and winning the game.
 *
 * @author isak
 */

public class MatchGameFragment extends BaseFragment implements View.OnClickListener {

    public final String TAG = "MatchGameFragment";

	private MatchBoardView mMatchBoardView;

    //timer controls/content
    private TextView mTime;
	private ImageView mTimeImage;
    private ImageView mTimerPlayPause;
    private ImageView mTimerRestart;        //TODO later, this functionality may not be necessary

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d (TAG, "method onCreateView");

		ViewGroup view = (ViewGroup) inflater.inflate(R.layout.match_game_fragment, container, false);
		view.setClipChildren(false);
		((ViewGroup)view.findViewById(R.id.match_game_board)).setClipChildren(false);
		mTime = (TextView) view.findViewById(R.id.time_bar_text_view);
		mTimeImage = (ImageView) view.findViewById(R.id.time_bar_image);
        mTimerPlayPause = (ImageView) view.findViewById(R.id.time_bar_play_pause_button);
        mTimerRestart = (ImageView) view.findViewById(R.id.time_bar_restart_button);
        mTimerRestart.setVisibility(View.INVISIBLE);
        mTimerPlayPause.setOnClickListener(this);
        mTimerRestart.setOnClickListener(this);

		FontLoader.setTypeface(Shared.context, new TextView[] {mTime}, Font.ANGRYBIRDS);
		mMatchBoardView = MatchBoardView.fromXml(getActivity().getApplicationContext(), view);
		FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.match_game_container);
		frameLayout.addView(mMatchBoardView);
		frameLayout.setClipChildren(false);
        //pauseTimerButton();
        //restartGameButton();

        // build board
		buildBoard();
		Shared.eventBus.listen(MatchFlipDownCardsEvent.TYPE, this);
		Shared.eventBus.listen(MatchHidePairCardsEvent.TYPE, this);
		Shared.eventBus.listen(MatchGameWonEvent.TYPE, this);

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
		Shared.eventBus.unlisten(MatchFlipDownCardsEvent.TYPE, this);
		Shared.eventBus.unlisten(MatchHidePairCardsEvent.TYPE, this);
		Shared.eventBus.unlisten(MatchGameWonEvent.TYPE, this);
		super.onDestroy();
	}

	private void buildBoard() {
		MatchGame matchGame = Shared.engine.getActiveMatchGame();
		long time = matchGame.matchBoardConfiguration.time;
		setTime(time);
		mMatchBoardView.setBoard(matchGame);
        startClock(time);
	}

    /*
     * Method setTime converts the gameplay time in millis to minutes and seconds in order to set
     * the countdown clock on the screen //TODO CHECK THIS IS TRUE?!
     */
	private void setTime(long time) {
        Log.v (TAG, "method setTime: input time (ms): " + time);
		int timeInSeconds = (int) Math.ceil ((double) time / 1000);
        Log.v (TAG, "              : timeInSeconds: " + timeInSeconds);
		int min = timeInSeconds / 60;
        Log.v (TAG, "              : min: " + min);
		int sec = timeInSeconds - min*60;
        Log.v (TAG, "              : sec: " + sec + " | calling mTime.setText");
		mTime.setText(" " + String.format(Locale.ENGLISH, "%02d", min) + ":" + String.format(Locale.ENGLISH, "%02d", sec));
	}

	private void startClock(long time) {
        Log.d (TAG, "method startClock: input time(ms): " + time);
        Shared.currentMatchGame.gameClock.startClock(time, 1000, new TimerCountdown() {

            @Override
			public void onTick(long millisUntilFinished) {
                Log.v (TAG, "Clock: onTick: millisUntilFinished: " + millisUntilFinished);
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

	@Override
	public void onEvent(MatchGameWonEvent event) {
        //Log.d (TAG, "overriding method onEvent (MatchGameWonEvent)");
        //We print out all of the collected array data here?
        for (int i = 0; i < Shared.userData.getCurMatchGame().getNumTurnsTaken(); i++) {
            if (i < 10) {
                Log.d(TAG, "*****| Turn: 0" + i + " | Turn Time: " + Shared.userData.getCurMatchGame().queryTurnDurationsArray(i) +
                        " | Play Duration: " + Shared.userData.getCurMatchGame().queryGamePlayDurations(i) +
                        " | CardID: " + Shared.userData.getCurMatchGame().queryCardsSelectedArray(i));
                        //" | Species on Card: " + Shared.userData.getCurMatchGame().queryCardsSelectedArray(i).getSpeciesName());
            } else {
                Log.d(TAG, "*****| Turn: " + i + " | Turn Time: " + Shared.userData.getCurMatchGame().queryTurnDurationsArray(i) +
                        " | Play Duration: " + Shared.userData.getCurMatchGame().queryGamePlayDurations(i) +
                        " | CardID: " + Shared.userData.getCurMatchGame().queryCardsSelectedArray(i));
                        //" | Species on Card: " + Shared.userData.getCurMatchGame().queryCardsSelectedArray(i).getSpeciesName());
            }
        }

        /*
        //this can be removed once validated
        Log.d (TAG, "... Shared.curMemGameData: themeID: " + Shared.userData.getCurMatchGame().getThemeID() +
                " | difficultyLevel selected: " + Shared.userData.getCurMatchGame().getGameDifficulty() +
                " | gameDurationAllocated: " + Shared.userData.getCurMatchGame().getGameDurationAllocated() +
                " | mixerState: " + Shared.userData.getCurMatchGame().getMixerState() +
                " | gameStarted: " + Shared.userData.getCurMatchGame().isGameStarted() +
                " | gameStartTimeStamp: " + Shared.userData.getCurMatchGame().getGameStartTimestamp() +
                " | numPlayDurationsRecorded: " + Shared.userData.getCurMatchGame().sizeOfTurnDurationsArray() +
                " | numTurnDurationsRecorded: " + Shared.userData.getCurMatchGame().sizeOfTurnDurationsArray() +
                " | numCardsSelectionsRecorded: " + Shared.userData.getCurMatchGame().sizeOfCardSelectionArray() +
                " | numTurnsTaken:  " + Shared.userData.getCurMatchGame().getNumTurnsTaken());
        //TODO validation block doesn't include information from playDurationArray
        for (int i = 0; i < Shared.userData.getCurMatchGame().sizeOfTurnDurationsArray(); i++ ) {
            Log.d(TAG, "... turnDurations[" + i + "] : " + Shared.userData.getCurMatchGame().queryTurnDurationsArray(i));
        }
        for (int i = 0; i < Shared.userData.getCurMatchGame().sizeOfCardSelectionArray(); i++ ) {
            Log.d(TAG, "... cardSelections[" + i + "].getCardId(): " + Shared.userData.getCurMatchGame().queryCardsSelectedArray(i));
        }
        //end validation block
        */


        //append MatchGameData to userData array
        Shared.userData.appendMatchGameData(Shared.userData.getCurMatchGame());     //append the MatchGameData for completed game to

        //debug state of UserData before updating Database
        Shared.userData.DebugUserData("MatchGameWonEvent");
        //update userData in array - this makes sure that stars remain up to date
        UserDataORM.updateUserData(Shared.userData);

        //insert current matchGameData into database
        MatchGameDataORM.insertMatchGameData(Shared.userData.getCurMatchGame());

        //reset flags
        Shared.userData.getCurMatchGame().setGameStarted(false);                  //reset the gameStarted boolean to false
        //null the pointer to curMemGame once it has been appended to the UserData array
        Shared.userData.setCurMatchGame(null);
		mTime.setVisibility(View.GONE);
		mTimeImage.setVisibility(View.GONE);
		PopupManager.showMatchPopupWon(event.gameState);
	}

	@Override
	public void onEvent(MatchFlipDownCardsEvent event) {
        //Log.d (TAG, "overriding method onEvent (MatchFlipDownCardsEvent)");
		mMatchBoardView.flipDownAll();
	}

	@Override
	public void onEvent(MatchHidePairCardsEvent event) {
        //Log.d (TAG, "overriding method onEvent (MatchHidePairCardsEvent)");
		mMatchBoardView.hideCards(event.id1, event.id2);
	}
}