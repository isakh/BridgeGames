package ws.isak.memgamev.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.ImageView;
import android.util.Log;
import android.widget.Toast;

import ws.isak.memgamev.R;

import ws.isak.memgamev.common.Audio;
import ws.isak.memgamev.common.Memory;
import ws.isak.memgamev.common.Shared;
import ws.isak.memgamev.common.CardData;

import ws.isak.memgamev.engine.ScreenController.Screen;
import ws.isak.memgamev.events.engine.MatchFlipDownCardsEvent;
import ws.isak.memgamev.events.engine.MatchGameWonEvent;
import ws.isak.memgamev.events.engine.PlayCardAudioEvent;
import ws.isak.memgamev.events.ui.MatchDifficultySelectedEvent;
import ws.isak.memgamev.events.ui.MatchNextGameEvent;
import ws.isak.memgamev.events.ui.MatchResetBackgroundEvent;
import ws.isak.memgamev.events.ui.MatchThemeSelectedEvent;
import ws.isak.memgamev.events.ui.SwapStartEvent;
import ws.isak.memgamev.model.MatchBoardConfiguration;
import ws.isak.memgamev.model.MatchGame;
import ws.isak.memgamev.model.MatchBoardArrangement;
import ws.isak.memgamev.themes.MatchTheme;
import ws.isak.memgamev.themes.MatchThemes;
import ws.isak.memgamev.ui.PopupManager;

import ws.isak.memgamev.events.EventObserverAdapter;
import ws.isak.memgamev.events.engine.MatchHidePairCardsEvent;
import ws.isak.memgamev.events.ui.MatchBackGameEvent;
import ws.isak.memgamev.events.ui.MatchFlipCardEvent;
import ws.isak.memgamev.events.ui.MatchStartEvent;

import ws.isak.memgamev.model.GameState;
import ws.isak.memgamev.model.MemGameData;

import ws.isak.memgamev.utils.Clock;
import ws.isak.memgamev.utils.Utils;

/*
 * Class Engine contains the core behavior of the app.
 *
 * @author isak
 */

public class Engine extends EventObserverAdapter {

	private static final String TAG = "Engine";
	private static Engine mInstance = null;			//instance of Engine for current use of app
	private MatchGame mPlayingMatchGame = null;				//instance of MatchGame for current game being played
    private MemGameData currentGameData;
	private int mFlippedId = -1;					//id of the tile (? or event?) with the card being flipped
	private int mToFlip = -1;
	private ScreenController mScreenController;
	private MatchTheme mSelectedMatchTheme;
	private ImageView mBackgroundImage;
	private Handler mHandler;

	private Engine() {
        Log.d (TAG, "***** Constructor *****");
        Log.d (TAG, "calling ScreenController.getInstance");
		mScreenController = ScreenController.getInstance();
		mHandler = new Handler();
	}

	public static Engine getInstance() {
        Log.d (TAG, "method getInstance called for Engine");
		if (mInstance == null) {
			mInstance = new Engine();
		}
		return mInstance;
	}

	public void start() {
        Log.d (TAG, "method start");
        Log.d (TAG, " *******: Shared.eventBus @: " + Shared.eventBus);
		Shared.eventBus.listen(MatchDifficultySelectedEvent.TYPE, this);
		Shared.eventBus.listen(MatchFlipCardEvent.TYPE, this);
		Shared.eventBus.listen(MatchStartEvent.TYPE, this);
		Shared.eventBus.listen(MatchThemeSelectedEvent.TYPE, this);
		Shared.eventBus.listen(MatchBackGameEvent.TYPE, this);
		Shared.eventBus.listen(MatchNextGameEvent.TYPE, this);
		Shared.eventBus.listen(MatchResetBackgroundEvent.TYPE, this);
		Shared.eventBus.listen(PlayCardAudioEvent.TYPE, this);
	}

	public void stop() {
		mPlayingMatchGame = null;
		mBackgroundImage.setImageDrawable(null);
		mBackgroundImage = null;
		mHandler.removeCallbacksAndMessages(null);
		mHandler = null;

		Shared.eventBus.unlisten(MatchDifficultySelectedEvent.TYPE, this);
		Shared.eventBus.unlisten(MatchFlipCardEvent.TYPE, this);
		Shared.eventBus.unlisten(MatchStartEvent.TYPE, this);
		Shared.eventBus.unlisten(MatchThemeSelectedEvent.TYPE, this);
		Shared.eventBus.unlisten(MatchBackGameEvent.TYPE, this);
		Shared.eventBus.unlisten(MatchNextGameEvent.TYPE, this);
		Shared.eventBus.unlisten(MatchResetBackgroundEvent.TYPE, this);
		Shared.eventBus.unlisten(PlayCardAudioEvent.TYPE, this);

		mInstance = null;
	}

	@Override
	public void onEvent(MatchResetBackgroundEvent event) {
		Drawable drawable = mBackgroundImage.getDrawable();
		if (drawable != null) {
			((TransitionDrawable) drawable).reverseTransition(2000);
		} else {
			new AsyncTask<Void, Void, Bitmap>() {

				@Override
				protected Bitmap doInBackground(Void... params) {
					Bitmap bitmap = Utils.scaleDown(R.drawable.background, Utils.screenWidth(), Utils.screenHeight());
					return bitmap;
				}
				protected void onPostExecute(Bitmap bitmap) {
                    //
					mBackgroundImage.setImageBitmap(bitmap);
				}
			}.execute();
		}
	}

	@Override
	public void onEvent(MatchStartEvent event) {
        Log.d (TAG, "override onEvent for MatchStartEvent: calling screen controller to open THEME_SELECT_MEM screen");
        PopupManager.closePopup();
		mScreenController.openScreen(Screen.THEME_SELECT_MEM);
	}

	@Override
    public void onEvent (SwapStartEvent event) {
        Log.d (TAG, "override inEvent for SwapStarEvent: Calling screen controller to open DIFFICULTY_SWAP");
        PopupManager.closePopup();
        //FIXME!!! mScreenController.openScreen(Screen.DIFFICULTY_SWAP);
        mScreenController.openScreen(Screen.POST_SURVEY);      //TODO - this is just for debugging
    }

	@Override
	public void onEvent(MatchNextGameEvent event) {
		PopupManager.closePopup();
		int difficulty = mPlayingMatchGame.matchMatchBoardConfiguration.difficulty;
		if (mPlayingMatchGame.gameState.achievedStars == 3 && difficulty < 3) {  //TODO set these numbers in values.xml?
			difficulty++;
		}
		Shared.eventBus.notify(new MatchDifficultySelectedEvent(difficulty));
	}

	@Override
	public void onEvent(MatchBackGameEvent event) {
		PopupManager.closePopup();
		mScreenController.openScreen(Screen.DIFFICULTY_MEM);
        //TODO verify that adding the following lines to reset the difficulty on backGameEvent worked [initially yes]
        int difficulty = mPlayingMatchGame.matchMatchBoardConfiguration.difficulty;
        Shared.eventBus.notify (new MatchDifficultySelectedEvent(difficulty));
	}

	@Override
	public void onEvent(MatchThemeSelectedEvent event) {
		mSelectedMatchTheme = event.matchTheme;
		mScreenController.openScreen(Screen.DIFFICULTY_MEM);
		AsyncTask<Void, Void, TransitionDrawable> task = new AsyncTask<Void, Void, TransitionDrawable>() {

			@Override
			protected TransitionDrawable doInBackground(Void... params) {
				Bitmap bitmap = Utils.scaleDown(R.drawable.background, Utils.screenWidth(), Utils.screenHeight());
				Bitmap backgroundImage = MatchThemes.getBackgroundImage(mSelectedMatchTheme);
				backgroundImage = Utils.crop(backgroundImage, Utils.screenHeight(), Utils.screenWidth());
				Drawable backgrounds[] = new Drawable[2];
				backgrounds[0] = new BitmapDrawable(Shared.context.getResources(), bitmap);
				backgrounds[1] = new BitmapDrawable(Shared.context.getResources(), backgroundImage);
				TransitionDrawable imageCrossFader = new TransitionDrawable(backgrounds);
				return imageCrossFader;
			}

			@Override
			protected void onPostExecute(TransitionDrawable result) {
				super.onPostExecute(result);
				mBackgroundImage.setImageDrawable(result);
				result.startTransition(2000);
			}
		};
		task.execute();
	}

	@Override
	public void onEvent(MatchDifficultySelectedEvent event) {
		mFlippedId = -1;
		mPlayingMatchGame = new MatchGame();
        mPlayingMatchGame.matchTheme = mSelectedMatchTheme;
		mPlayingMatchGame.matchMatchBoardConfiguration = new MatchBoardConfiguration(event.difficulty, mSelectedMatchTheme);
		mToFlip = mPlayingMatchGame.matchMatchBoardConfiguration.numTiles;
        Shared.currentMatchGame = mPlayingMatchGame;

		// arrange board
		arrangeBoard();

        //instantiating the currentGameData object - some fields default to 0 || null
        currentGameData = new MemGameData();
        currentGameData.setThemeID(Shared.currentMatchGame.matchTheme.themeID);
        currentGameData.setGameDifficulty(Shared.currentMatchGame.matchMatchBoardConfiguration.difficulty);
        currentGameData.setGameDurationAllocated(Shared.currentMatchGame.matchMatchBoardConfiguration.time);
        currentGameData.setMixerState(Audio.MIX);
        //check setup of memGameData - these should return current states
        Log.d (TAG, "******* New MemGameData Instantiated *******");
        Log.d (TAG, "event MatchDifficultySelectedEvent: create currentGameData: currentGameData.getUserPlayingName : " + currentGameData.getUserPlayingName());
        Log.d (TAG, "                                                     : currentGameData.getThemeID : " + currentGameData.getThemeID());
        Log.d (TAG, "                                                     : currentGameData.getGameDifficulty: " + currentGameData.getGameDifficulty());
        Log.d (TAG, "                                                     : currentGameData.getGameDurationAllocated: " + currentGameData.getGameDurationAllocated());
        Log.d (TAG, "                                                     : currentGameData.getMixerState : " + currentGameData.getMixerState());
        //these should reflect that the game is not yet started (don't check start timeStamp as it hasn't been used?)
        Log.d (TAG, "                             			              : currentGameData.isGameStarted: " + currentGameData.isGameStarted());
        Log.d (TAG, "                                                     : currentGameData.getGameStartTimestamp: " + currentGameData.getGameStartTimestamp());
        Log.d (TAG, "                                                     : currentGameData.numPlayDurationsRecorded: " + currentGameData.sizeOfPlayDurationsArray());
        Log.d (TAG, "                                                     : currentGameData.numTurnDurationsRecorded: " + currentGameData.sizeOfTurnDurationsArray());
        Log.d (TAG, "                                                     : currentGameData.numCardSelectionsRecorded: " + currentGameData.sizeOfCardSelectionArray());
        Log.d (TAG, "                             			              : currentGameData.getNumTurnsTaken: " + currentGameData.getNumTurnsTaken());

        //debug Shared.userData
        Log.d (TAG, " ******* : Shared.userData @ : " + Shared.userData);
        Log.d (TAG, " ******* : userData.getCurMemGame @ : " + Shared.userData.getCurMemGame());

        Shared.userData.setCurMemGame(currentGameData);
        // start the screen - This call to screen controller causes the screen controller to select
        // a new MatchGameFragment from the screen controller.  Opening the new MatchGameFragment leads to a
        // call to buildBoard() a private method in the MatchGame Fragment. buildBoard calls setBoard in
        // the BoardView ui class. setBoard in BoardView propagates through a local buildBoard method
        // and eventually calls addTile for each of the tiles on the board to be built.   This leads
        // to a thread for each tile which calls getTileBitmap.
		mScreenController.openScreen(Screen.GAME_MEM);
    }

	private void arrangeBoard() {
		MatchBoardConfiguration matchMatchBoardConfiguration = mPlayingMatchGame.matchMatchBoardConfiguration;
		MatchBoardArrangement matchBoardArrangement = new MatchBoardArrangement();

		// list all n tiles  {0,1,2,...n-1} /
		List<Integer> tileIDs = new ArrayList<Integer>();
		for (int i = 0; i < matchMatchBoardConfiguration.numTiles; i++) {
			tileIDs.add(i);
		}
		// shuffle
		// result {4,10,2,39,...}
		Collections.shuffle(tileIDs);

		// map the paired tiles to each other as well as the card for each pair of tiles
		matchBoardArrangement.pairs = new HashMap<Integer, Integer>();
		matchBoardArrangement.cardObjs = new HashMap<Integer, CardData>();
		int j = 0;
		for (int i = 0; i < tileIDs.size(); i++) {		//Iterate over all of the tiles
			if (i + 1 < tileIDs.size()) {				//check that we haven't filled all tile pairs
				// take pairs of tile IDs in order from the shuffled list and insert into pairs HashMap
				matchBoardArrangement.pairs.put(tileIDs.get(i), tileIDs.get(i + 1));
				// and ensure that the mapping is bi-directional
				matchBoardArrangement.pairs.put(tileIDs.get(i + 1), tileIDs.get(i));
				// map each of the paired tile IDs to the same card object ID
				matchBoardArrangement.cardObjs.put(tileIDs.get(i), Shared.cardDataList.get(j));
				matchBoardArrangement.cardObjs.put(tileIDs.get(i + 1), Shared.cardDataList.get(j));
				//debug report: state of tile id's paired on board, and card id for the tile pair
				Log.d (TAG, "method arrangeBoard: Map Tile Pairs: Tile id1: " + tileIDs.get(i) + " |  Tile id2: " + tileIDs.get(i + 1) + " | Mapped Card id: " + Shared.cardDataList.get(j).getCardID());
				//Log.d (TAG, "method arrangeBoard: Mapping cardObjs to IDs: ID is: " + tileIDs.get(i) + " | Card Object ID is: " + mSelectedMatchTheme.cardObjs.get(j).getCardID());
				//Log.d (TAG, "method arrangeBoard: 		Card Object Image URI 1 is : " + mSelectedMatchTheme.cardObjs.get(j).getImageURI1());
				//Log.d (TAG, "method arrangeBoard: 		Card Object Image URI 2 is : " + mSelectedMatchTheme.cardObjs.get(j).getImageURI2());
				//Log.d (TAG, "method arrangeBoard: 		Card Object Audio URI is : " + mSelectedMatchTheme.cardObjs.get(j).getAudioURI());
				i++;
				j++;
			}
		}
		mPlayingMatchGame.matchBoardArrangement = matchBoardArrangement;
	}

	/*
	 * Override method onEvent when the event being passed is a MatchFlipCardEvent.
	 */
	@Override
	public void onEvent(MatchFlipCardEvent event) {

		Log.d (TAG, "onEvent MatchFlipCardEvent: event.id is: " + event.id + " *** AT START OF method ***");
		int id = event.id;
        Log.d (TAG, "                     : Card ID is: " + mPlayingMatchGame.matchBoardArrangement.cardObjs.get(id).getCardID());
        Log.d (TAG, "                     : species is: " + mPlayingMatchGame.matchBoardArrangement.cardObjs.get(id).getSpeciesName());

		if (mFlippedId == -1) {		//This is -1 when no cards are flipped up
			mFlippedId = id;		//set id of flipped up card to id of tile from event
			Log.i(TAG, "onEvent MatchFlipCardEvent: mFlippedId == -1: now set mFlippedId to: " + id);
			Log.d(TAG, "			waiting for second tile to be selected...");
		}
		else {		//this code covers behaviour when the second tile of a pair is clicked
			//check if mFlippedId is set from first card and compare to id of this tile flipped at event.
			if (mPlayingMatchGame.matchBoardArrangement.isPair(mFlippedId, id)) {
				 Log.i(TAG, "onEvent MatchFlipCardEvent: mFlippedId != -1 (one card is already flipped): and isPair: mFlippedID is: " + mFlippedId + ", " + id + " returns true");
				// send event - hide id1, id2
				Shared.eventBus.notify(new MatchHidePairCardsEvent(mFlippedId, id), 1000);//TODO make delay variable and incorporate into total game time

                //display the matched species name in a toast to the user
                Toast.makeText(Shared.context, mPlayingMatchGame.matchBoardArrangement.cardObjs.get(id).getSpeciesName(), Toast.LENGTH_LONG).show();

                Log.d (TAG, "onEvent MatchFlipCardEvent: isPair returned TRUE: calling MatchHidePairCardsEvent");
                // play music
				mHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						Log.d (TAG, "onEvent MatchFlipCardEvent: isPair returns TRUE: calling Audio.playCorrect()");
						//play the correct match audio
                        Audio.playCorrect();
					}
				}, 1000);		//TODO instead of fixed delay 1000ms use duration of sample
				mToFlip -= 2;			//remaining number of tiles to flip..
				if (mToFlip == 0) {		//when this gets to 0, we have flipped all pairs and can compute the score
					int passedSeconds = (int) (Clock.getInstance().getPassedTime() / 1000);
					Clock.getInstance().pause();
					long totalTimeInMillis = mPlayingMatchGame.matchMatchBoardConfiguration.time;
                    int totalTime = (int) Math.ceil((double) totalTimeInMillis / 1000); //TODO is this enough or should we convert all to long ms
					GameState gameState = new GameState();
					mPlayingMatchGame.gameState = gameState;
					// remained seconds
					gameState.remainingTimeInSeconds = totalTime - passedSeconds;

					// calculate stars and score from the amount of time that has elapsed as a ratio
					// of total time allotted for the game.  When calculating this we still have incorporated
                    // the time based on the difficulty as well as the time to play back the samples
					if (passedSeconds <= totalTime / 2) {gameState.achievedStars = 3; }
					else if (passedSeconds <= totalTime - totalTime / 5) {gameState.achievedStars = 2; }
					else if (passedSeconds < totalTime) {gameState.achievedStars = 1; }
					else {gameState.achievedStars = 0;}
					// calculate the score
					gameState.achievedScore = mPlayingMatchGame.matchMatchBoardConfiguration.difficulty * gameState.remainingTimeInSeconds * mPlayingMatchGame.matchTheme.themeID;
					// save to memory
					Memory.save(mPlayingMatchGame.matchTheme.themeID, mPlayingMatchGame.matchMatchBoardConfiguration.difficulty, gameState.achievedStars);
					//trigger the MatchGameWonEvent
					Shared.eventBus.notify(new MatchGameWonEvent(gameState), 1200);
				}
			} else {
				Log.d(TAG, "onEvent MatchFlipCardEvent: mFlippedID != -1: and !isPair: mFlippedID is:  " + mFlippedId);
				Log.d(TAG, "onEvent: MatchFlipCardEvent: Flip: all down");
				// send event - flip all down
				Shared.eventBus.notify(new MatchFlipDownCardsEvent(), 1000);
			}
			mFlippedId = -1;
			Log.d(TAG, "onEvent MatchFlipCardEvent: reset mFlippedId to -1 check: " + mFlippedId);
		}
	}

	public void onEvent (PlayCardAudioEvent event) {
		int id = event.id;
		Log.i (TAG, "onEvent PlayCardAudioEvent: event.id: " + id);
		playTileAudio(id);
	}

	/*
	 * Method playTileAudio is called when a tile is turned over and the game mode involves hearing
	 * the audio of the bird / spectrogram in question.  In order to play the correct audio, the
	 * method takes input of:
	 * 	@param tileID
	 * which is the the integer tile ID  associated with the given tile.  If the audio
	 * is turned off and the mode expects playback, a toast is sent to the user.
	 */

	private void playTileAudio(int tileID) {
		/*
        Log.d (TAG, "method playTileAudio: tileID: " + tileID);
		Log.d (TAG, "					 : curCardOnTile is: " + mPlayingMatchGame.matchBoardArrangement.cardObjs.get(tileID).getCardID());
		Log.d (TAG, " 					 : curCardOnTile.getAudioURI is: " + mPlayingMatchGame.matchBoardArrangement.cardObjs.get(tileID).getAudioURI());
		Log.d (TAG, " 					 : curCardOnTile.getImageURI1 is: " + mPlayingMatchGame.matchBoardArrangement.cardObjs.get(tileID).getImageURI1());
		Log.d (TAG, " 					 : curCardOnTile.getPairedImageDiffer is: " + mPlayingMatchGame.matchBoardArrangement.cardObjs.get(tileID).getPairedImageDiffer());
		Log.d (TAG, " 					 : curCardOnTile.getFirstImageUsed is: " + mPlayingMatchGame.matchBoardArrangement.cardObjs.get(tileID).getFirstImageUsed());
		Log.d (TAG, " 					 : curCardOnTile.getImageURI2 is: " + mPlayingMatchGame.matchBoardArrangement.cardObjs.get(tileID).getImageURI2());
        */
        if (!Audio.OFF) {
            String audioResourceName = mPlayingMatchGame.matchBoardArrangement.cardObjs.get(tileID).getAudioURI().substring(MatchThemes.URI_AUDIO.length());
            Log.d (TAG, "                    : audioResourceName: " + audioResourceName);
            int audioResourceId = Shared.context.getResources().getIdentifier(audioResourceName, "raw", Shared.context.getPackageName());
            Log.d (TAG, "                    : audioResourceId: " + audioResourceId);
            MediaPlayer curTileAudio = MediaPlayer.create (Shared.context, audioResourceId);
            curTileAudio.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer curTileAudio) {
						Log.d (TAG, "method playTileAudio: overriding onCompletion");
						Audio.setIsAudioPlaying(false);
                        Log.d (TAG, "method playTileAudio: Audio.setIsAudioPlaying(false) called. Audio.getIsAudioPlaying(): " + Audio.getIsAudioPlaying());
						curTileAudio.reset();
						curTileAudio.release();
						curTileAudio = null;
					}
            });
            curTileAudio.start();
			Audio.setIsAudioPlaying (true);
            Log.d (TAG, "method playTileAudio: curTileAudio.start() called, Audio.getIsAudioPlaying(): " + Audio.getIsAudioPlaying());
            long sampleDuration = Audio.getAudioDuration (audioResourceId);
            Log.d (TAG, "                    : sampleDuration: " + sampleDuration);

		}
		else {
			Toast.makeText(Shared.context, "Please turn on game audio to play in this mode, you can do this under settings", Toast.LENGTH_SHORT).show();
            mScreenController.openScreen(Screen.MENU_MEM);
		}
	}

	public MatchGame getActiveGame() {
		//Log.d (TAG, "method getActiveGame");
		return mPlayingMatchGame;
	}

	public MatchTheme getSelectedTheme() {
		//Log.d (TAG, "method getSelectedTheme);
		return mSelectedMatchTheme;
	}

	public void setBackgroundImageView(ImageView backgroundImage) {
		//Log.d (TAG, "method setBackgroundImageView");
		mBackgroundImage = backgroundImage;
	}
}