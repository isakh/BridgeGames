package ws.isak.memgamev.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;

import java.util.Locale;

import ws.isak.memgamev.R;
import ws.isak.memgamev.common.Shared;
import ws.isak.memgamev.database.MemGameDataORM;
import ws.isak.memgamev.events.engine.FlipDownCardsEvent;
import ws.isak.memgamev.events.engine.GameWonEvent;
import ws.isak.memgamev.events.engine.HidePairCardsEvent;
import ws.isak.memgamev.model.Game;
import ws.isak.memgamev.ui.BoardView;
import ws.isak.memgamev.ui.PopupManager;
import ws.isak.memgamev.utils.Clock;
import ws.isak.memgamev.utils.Clock.OnTimerCount;
import ws.isak.memgamev.utils.FontLoader;
import ws.isak.memgamev.utils.FontLoader.Font;

/*
 *
 *
 * @author isak
 */

public class GameFragment extends BaseFragment {

    public final String TAG = "Class: Game Fragment";

	private BoardView mBoardView;
	private TextView mTime;
	private ImageView mTimeImage;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d (TAG, "method onCreateView");
		ViewGroup view = (ViewGroup) inflater.inflate(R.layout.game_fragment, container, false);
		view.setClipChildren(false);
		((ViewGroup)view.findViewById(R.id.game_board)).setClipChildren(false);
		mTime = (TextView) view.findViewById(R.id.time_bar_text);                       //FIXME is this id right?
		mTimeImage = (ImageView) view.findViewById(R.id.time_bar_image);
		FontLoader.setTypeface(Shared.context, new TextView[] {mTime}, Font.ANGRYBIRDS);
		mBoardView = BoardView.fromXml(getActivity().getApplicationContext(), view);
		FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.game_container);
		frameLayout.addView(mBoardView);
		frameLayout.setClipChildren(false);

		// build board
		buildBoard();
		Shared.eventBus.listen(FlipDownCardsEvent.TYPE, this);
		Shared.eventBus.listen(HidePairCardsEvent.TYPE, this);
		Shared.eventBus.listen(GameWonEvent.TYPE, this);
		
		return view;
	}
	
	@Override
	public void onDestroy() {
		Shared.eventBus.unlisten(FlipDownCardsEvent.TYPE, this);
		Shared.eventBus.unlisten(HidePairCardsEvent.TYPE, this);
		Shared.eventBus.unlisten(GameWonEvent.TYPE, this);
		super.onDestroy();
	}

	private void buildBoard() {
		Game game = Shared.engine.getActiveGame();
		long time = game.boardConfiguration.time;
		setTime(time);
		mBoardView.setBoard(game);
		
		startClock(time);
	}

    /*
     * Method setTime converts the gameplay time in millis to minutes and seconds in order to set
     * the countdown clock on the screen //TODO CHECK THIS IS TRUE?!
     */
	private void setTime(long time) {
        //Log.d (TAG, "method setTime: input time (ms): " + time);
		int timeInSeconds = (int) Math.ceil ((double) time / 1000);
        //Log.d (TAG, "              : timeInSeconds: " + timeInSeconds);
		int min = timeInSeconds / 60;
        //Log.d (TAG, "              : min: " + min);
		int sec = timeInSeconds - min*60;
        //Log.d (TAG, "              : sec: " + sec + " | calling mTime.setText");
		mTime.setText(" " + String.format(Locale.ENGLISH, "%02d", min) + ":" + String.format(Locale.ENGLISH, "%02d", sec));
	}

	private void startClock(long time) {
        Log.d (TAG, "method startClock: intput time(ms): " + time);
		//TODO remove int sec = (int) Math.ceil ((double) time / 1000);
		Clock clock = Clock.getInstance();
		//TODO remove clock.startTimer(sec*1000, 1000, new OnTimerCount() {
        clock.startTimer(time, 1000, new OnTimerCount() {

            @Override
			public void onTick(long millisUntilFinished) {
                //Log.d (TAG, "method startClock: overriding onTick: input millisUntilFinished: " + millisUntilFinished);
				setTime((int) (millisUntilFinished/1000));
			}
			
			@Override
			public void onFinish() {
                Log.d (TAG, "method startClock: overriding onFinish");
				setTime(0);
			}
		});
	}

	@Override
	public void onEvent(GameWonEvent event) {
        //Log.d (TAG, "overriding method onEvent (GameWonEvent)");
        //We print out all of the collected array data here?
        for (int i = 0; i < Shared.userData.getCurMemGame().getNumTurnsTaken(); i++) {
            if (i < 10) {
                Log.d(TAG, "*****| Turn: 0" + i + " | Turn Time: " + Shared.userData.getCurMemGame().queryTurnDurationsArray(i) +
                        " | Play Duration: " + Shared.userData.getCurMemGame().queryGamePlayDurations(i) +
                        " | CardID: " + Shared.userData.getCurMemGame().queryCardsSelectedArray(i));
                        //" | Species on Card: " + Shared.userData.getCurMemGame().queryCardsSelectedArray(i).getSpeciesName());
            } else {
                Log.d(TAG, "*****| Turn: " + i + " | Turn Time: " + Shared.userData.getCurMemGame().queryTurnDurationsArray(i) +
                        " | Play Duration: " + Shared.userData.getCurMemGame().queryGamePlayDurations(i) +
                        " | CardID: " + Shared.userData.getCurMemGame().queryCardsSelectedArray(i));
                        //" | Species on Card: " + Shared.userData.getCurMemGame().queryCardsSelectedArray(i).getSpeciesName());
            }
        }

        /*
        //this can be removed once validated
        Log.d (TAG, "... Shared.curMemGameData: themeID: " + Shared.userData.getCurMemGame().getThemeID() +
                " | difficulty selected: " + Shared.userData.getCurMemGame().getGameDifficulty() +
                " | gameDurationAllocated: " + Shared.userData.getCurMemGame().getGameDurationAllocated() +
                " | mixerState: " + Shared.userData.getCurMemGame().getMixerState() +
                " | gameStarted: " + Shared.userData.getCurMemGame().isGameStarted() +
                " | gameStartTimeStamp: " + Shared.userData.getCurMemGame().getGameStartTimestamp() +
                " | numPlayDurationsRecorded: " + Shared.userData.getCurMemGame().sizeOfTurnDurationsArray() +
                " | numTurnDurationsRecorded: " + Shared.userData.getCurMemGame().sizeOfTurnDurationsArray() +
                " | numCardsSelectionsRecorded: " + Shared.userData.getCurMemGame().sizeOfCardSelectionArray() +
                " | numTurnsTaken:  " + Shared.userData.getCurMemGame().getNumTurnsTaken());
        //TODO validation block doesn't include information from playDurationArray
        for (int i = 0; i < Shared.userData.getCurMemGame().sizeOfTurnDurationsArray(); i++ ) {
            Log.d(TAG, "... turnDurations[" + i + "] : " + Shared.userData.getCurMemGame().queryTurnDurationsArray(i));
        }
        for (int i = 0; i < Shared.userData.getCurMemGame().sizeOfCardSelectionArray(); i++ ) {
            Log.d(TAG, "... cardSelections[" + i + "].getCardId(): " + Shared.userData.getCurMemGame().queryCardsSelectedArray(i));
        }
        //end validation block
        */

        //append MemGameData to userData array
        Shared.userData.appendMemGameData(Shared.userData.getCurMemGame());     //append the MemGameData for completed game to

        //insert current memGameData into database
        MemGameDataORM.insertMemGameData(Shared.userData.getCurMemGame());

        //reset flags
        Shared.userData.getCurMemGame().setGameStarted(false);                  //reset the gameStarted boolean to false
        //null the pointer to curMemGame once it has been appended to the UserData array
        Shared.userData.setCurMemGame(null);
		mTime.setVisibility(View.GONE);
		mTimeImage.setVisibility(View.GONE);
		PopupManager.showPopupWon(event.gameState);
	}

	@Override
	public void onEvent(FlipDownCardsEvent event) {
        //Log.d (TAG, "overriding method onEvent (FlipDownCardsEvent)");
		mBoardView.flipDownAll();
	}

	@Override
	public void onEvent(HidePairCardsEvent event) {
        //Log.d (TAG, "overriding method onEvent (HidePairCardsEvent)");
		mBoardView.hideCards(event.id1, event.id2);
	}
}