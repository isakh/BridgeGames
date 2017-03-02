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

import ws.isak.memgamev.common.Memory;
import ws.isak.memgamev.common.Music;
import ws.isak.memgamev.common.Shared;
import ws.isak.memgamev.common.CardData;

import ws.isak.memgamev.engine.ScreenController.Screen;
import ws.isak.memgamev.events.engine.PlayCardAudioEvent;
import ws.isak.memgamev.ui.PopupManager;

import ws.isak.memgamev.events.EventObserverAdapter;
import ws.isak.memgamev.events.engine.FlipDownCardsEvent;
import ws.isak.memgamev.events.engine.GameWonEvent;
import ws.isak.memgamev.events.engine.HidePairCardsEvent;
import ws.isak.memgamev.events.ui.BackGameEvent;
import ws.isak.memgamev.events.ui.DifficultySelectedEvent;
import ws.isak.memgamev.events.ui.FlipCardEvent;
import ws.isak.memgamev.events.ui.NextGameEvent;
import ws.isak.memgamev.events.ui.ResetBackgroundEvent;
import ws.isak.memgamev.events.ui.StartEvent;
import ws.isak.memgamev.events.ui.ThemeSelectedEvent;

import ws.isak.memgamev.model.BoardArrangement;
import ws.isak.memgamev.model.BoardConfiguration;
import ws.isak.memgamev.model.Game;
import ws.isak.memgamev.model.GameState;

import ws.isak.memgamev.themes.Theme;
import ws.isak.memgamev.themes.Themes;

import ws.isak.memgamev.utils.Clock;
import ws.isak.memgamev.utils.Utils;

/*
 * Class Engine contains the core behavior of the app.
 *
 * @author isak
 */

public class Engine extends EventObserverAdapter {

	private final String TAG = "Class: Engine";		//a string used for logging
	private static Engine mInstance = null;			//instance of Engine for current use of app
	private Game mPlayingGame = null;				//instance of Game for current game being played
	private int mFlippedId = -1;					//id of the tile (? or event?) with the card being flipped
	private int mToFlip = -1;
	private ScreenController mScreenController;
	private Theme mSelectedTheme;
	private ImageView mBackgroundImage;
	private Handler mHandler;

	private Engine() {
		mScreenController = ScreenController.getInstance();
		mHandler = new Handler();
	}

	public static Engine getInstance() {
		if (mInstance == null) {
			mInstance = new Engine();
		}
		return mInstance;
	}

	public void start() {
		Shared.eventBus.listen(DifficultySelectedEvent.TYPE, this);
		Shared.eventBus.listen(FlipCardEvent.TYPE, this);
		Shared.eventBus.listen(StartEvent.TYPE, this);
		Shared.eventBus.listen(ThemeSelectedEvent.TYPE, this);
		Shared.eventBus.listen(BackGameEvent.TYPE, this);
		Shared.eventBus.listen(NextGameEvent.TYPE, this);
		Shared.eventBus.listen(ResetBackgroundEvent.TYPE, this);
		Shared.eventBus.listen(PlayCardAudioEvent.TYPE, this);
	}

	public void stop() {
		mPlayingGame = null;
		mBackgroundImage.setImageDrawable(null);
		mBackgroundImage = null;
		mHandler.removeCallbacksAndMessages(null);
		mHandler = null;

		Shared.eventBus.unlisten(DifficultySelectedEvent.TYPE, this);
		Shared.eventBus.unlisten(FlipCardEvent.TYPE, this);
		Shared.eventBus.unlisten(StartEvent.TYPE, this);
		Shared.eventBus.unlisten(ThemeSelectedEvent.TYPE, this);
		Shared.eventBus.unlisten(BackGameEvent.TYPE, this);
		Shared.eventBus.unlisten(NextGameEvent.TYPE, this);
		Shared.eventBus.unlisten(ResetBackgroundEvent.TYPE, this);
		Shared.eventBus.unlisten(PlayCardAudioEvent.TYPE, this);

		mInstance = null;
	}

	@Override
	public void onEvent(ResetBackgroundEvent event) {
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
					mBackgroundImage.setImageBitmap(bitmap);
				};

			}.execute();
		}
	}

	@Override
	public void onEvent(StartEvent event) {
		mScreenController.openScreen(Screen.THEME_SELECT);
	}

	@Override
	public void onEvent(NextGameEvent event) {
		PopupManager.closePopup();
		int difficulty = mPlayingGame.boardConfiguration.difficulty;
		if (mPlayingGame.gameState.achievedStars == 3 && difficulty < 6) {
			difficulty++;
		}
		Shared.eventBus.notify(new DifficultySelectedEvent(difficulty));
	}

	@Override
	public void onEvent(BackGameEvent event) {
		PopupManager.closePopup();
		mScreenController.openScreen(Screen.DIFFICULTY);
	}

	@Override
	public void onEvent(ThemeSelectedEvent event) {
		mSelectedTheme = event.theme;
		mScreenController.openScreen(Screen.DIFFICULTY);
		AsyncTask<Void, Void, TransitionDrawable> task = new AsyncTask<Void, Void, TransitionDrawable>() {

			@Override
			protected TransitionDrawable doInBackground(Void... params) {
				Bitmap bitmap = Utils.scaleDown(R.drawable.background, Utils.screenWidth(), Utils.screenHeight());
				Bitmap backgroundImage = Themes.getBackgroundImage(mSelectedTheme);
				backgroundImage = Utils.crop(backgroundImage, Utils.screenHeight(), Utils.screenWidth());
				Drawable backgrounds[] = new Drawable[2];
				backgrounds[0] = new BitmapDrawable(Shared.context.getResources(), bitmap);
				backgrounds[1] = new BitmapDrawable(Shared.context.getResources(), backgroundImage);
				TransitionDrawable crossfader = new TransitionDrawable(backgrounds);
				return crossfader;
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
	public void onEvent(DifficultySelectedEvent event) {
		mFlippedId = -1;
		mPlayingGame = new Game();
		mPlayingGame.boardConfiguration = new BoardConfiguration(event.difficulty);
		mPlayingGame.theme = mSelectedTheme;
		mToFlip = mPlayingGame.boardConfiguration.numTiles;

		// arrange board
		arrangeBoard();

		// start the screen
		mScreenController.openScreen(Screen.GAME);
	}

	private void arrangeBoard() {
		BoardConfiguration boardConfiguration = mPlayingGame.boardConfiguration;
		BoardArrangement boardArrangement = new BoardArrangement();

		// list all n tiles  {0,1,2,...n-1} /
		List<Integer> tileIDs = new ArrayList<Integer>();
		for (int i = 0; i < boardConfiguration.numTiles; i++) {
			tileIDs.add(i);
		}
		// shuffle
		// result {4,10,2,39,...}
		Collections.shuffle(tileIDs);

		// map the paired tiles to each other as well as the card for each pair of tiles
		boardArrangement.pairs = new HashMap<Integer, Integer>();
		boardArrangement.cardObjs = new HashMap<Integer, CardData>();
		int j = 0;
		for (int i = 0; i < tileIDs.size(); i++) {		//Iterate over all of the tiles
			if (i + 1 < tileIDs.size()) {				//check that we haven't filled all tile pairs
				// take pairs of tile IDs in order from the shuffled list and insert into pairs HashMap
				boardArrangement.pairs.put(tileIDs.get(i), tileIDs.get(i + 1));
				// and ensure that the mapping is bi-directional
				boardArrangement.pairs.put(tileIDs.get(i + 1), tileIDs.get(i));
				// map each of the paired tile IDs to the same card object ID
				boardArrangement.cardObjs.put(tileIDs.get(i), mSelectedTheme.cardObjs.get(j));
				boardArrangement.cardObjs.put(tileIDs.get(i + 1), mSelectedTheme.cardObjs.get(j));
				//debug report: state of tile id's paired on board, and card id for the tile pair
				Log.d (TAG, "method arrangeBoard: Map Tile Pairs: Tile id1: " + tileIDs.get(i) + " |  Tile id2: " + tileIDs.get(i + 1) + " | Mapped Card id: " + mSelectedTheme.cardObjs.get(j).getCardID());
				//Log.d (TAG, "method arrangeBoard: Mapping cardObjs to IDs: ID is: " + tileIDs.get(i) + " | Card Object ID is: " + mSelectedTheme.cardObjs.get(j).getCardID());
				//Log.d (TAG, "method arrangeBoard: 		Card Object Image URI 1 is : " + mSelectedTheme.cardObjs.get(j).getImageURI1());
				//Log.d (TAG, "method arrangeBoard: 		Card Object Image URI 2 is : " + mSelectedTheme.cardObjs.get(j).getImageURI2());
				//Log.d (TAG, "method arrangeBoard: 		Card Object Audio URI is : " + mSelectedTheme.cardObjs.get(j).getAudioURI());
				i++;
				j++;
			}
		}
		mPlayingGame.boardArrangement = boardArrangement;
	}

	/*
	 * Override method onEvent when the event being passed is a FlipCardEvent.
	 */
	@Override
	public void onEvent(FlipCardEvent event) {

		Log.i(TAG, "onEvent FlipCardEvent: event.id is: " + event.id + " *** AT START OF method ***");
		int id = event.id;

		if (mFlippedId == -1) {		//This is -1 when no cards are flipped up
			mFlippedId = id;		//set id of flipped up card to id of tile from event
			Log.i(TAG, "onEvent FlipCardEvent: mFlippedId == -1: now set mFlippedId to: " + id);
			Log.d(TAG, "			waiting for second tile to be selected...");
		}
		else {		//this code covers behaviour when the second tile of a pair is clicked
			//check if mFlippedId is set from first card and compare to id of this tile flipped at event.
			if (mPlayingGame.boardArrangement.isPair(mFlippedId, id)) {
				 Log.i(TAG, "onEvent FlipCardEvent: mFlippedId != -1 (one card is already flipped): and isPair: mFlippedID is: " + mFlippedId + ", " + id + " returns true");
				// send event - hide id1, id2
				Shared.eventBus.notify(new HidePairCardsEvent(mFlippedId, id), 1000);		//TODO make delay variable and incorporate into total game time
				//TODO add a toast to show the user the name of the species that they have matched??
                Log.i (TAG, "onEvent FlipCardEvent: isPair returned TRUE: calling HidePairCardsEvent");
				// play music
				mHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						Log.d (TAG, "onEvent FlipCardEvent: isPair returns TRUE: calling Music.playCorrect()");
						Music.playCorrect();
					}
				}, 1000);		//TODO instead of fixed delay 1000ms use duration of sample
				mToFlip -= 2;			//remaining number of tiles to flip..
				if (mToFlip == 0) {		//when this gets to 0, we have flipped all pairs and can compute the score
					int passedSeconds = (int) (Clock.getInstance().getPassedTime() / 1000);
					Clock.getInstance().pause();
					int totalTime = mPlayingGame.boardConfiguration.time;
					GameState gameState = new GameState();
					mPlayingGame.gameState = gameState;
					// remained seconds
					gameState.remainingTimeInSeconds = totalTime - passedSeconds;

					// calculate stars and score from the amount of time that has elapsed as a ratio
					// of total time allotted for the game.  When calculating this we still have...
					// TODO solve time for audio playback and add it in
					if (passedSeconds <= totalTime / 2) {gameState.achievedStars = 3; }
					else if (passedSeconds <= totalTime - totalTime / 5) {gameState.achievedStars = 2; }
					else if (passedSeconds < totalTime) {gameState.achievedStars = 1; }
					else {gameState.achievedStars = 0;}
					// calculate the score
					gameState.achievedScore = mPlayingGame.boardConfiguration.difficulty * gameState.remainingTimeInSeconds * mPlayingGame.theme.themeID;
					// save to memory
					Memory.save(mPlayingGame.theme.themeID, mPlayingGame.boardConfiguration.difficulty, gameState.achievedStars);
					//trigger the GameWonEvent
					Shared.eventBus.notify(new GameWonEvent(gameState), 1200);
				}
			} else {
				Log.i(TAG, "onEvent FlipCardEvent: mFlippedID != -1: and !isPair: mFlippedID is:  " + mFlippedId);
				Log.i(TAG, "onEvent: FlipCardEvent: Flip: all down");
				// send event - flip all down
				Shared.eventBus.notify(new FlipDownCardsEvent(), 1000);
			}
			mFlippedId = -1;
			Log.i(TAG, "onEvent FlipCardEvent: reset mFlippedId to -1 check: " + mFlippedId);
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
	 * 	@param curCard
	 * which is the the CardData object associated with the given tile.  If the audio
	 * is turned off and the mode expects playback, a toast is sent to the user.
	 */

	private void playTileAudio(int tileID) {
		Log.d (TAG, "method playTileAudio: tileID: " + tileID);
		Log.d (TAG, "					 : curCardOnTile is: " + mPlayingGame.boardArrangement.cardObjs.get(tileID).getCardID());
		Log.d (TAG, " 					 : curCardOnTile.getAudioURI is: " + mPlayingGame.boardArrangement.cardObjs.get(tileID).getAudioURI());
		Log.d (TAG, " 					 : curCardOnTile.getImageURI1 is: " + mPlayingGame.boardArrangement.cardObjs.get(tileID).getImageURI1());
		Log.d (TAG, " 					 : curCardOnTile.getPairedImageDiffer is: " + mPlayingGame.boardArrangement.cardObjs.get(tileID).getPairedImageDiffer());
		Log.d (TAG, " 					 : curCardOnTile.getFirstImageUsed is: " + mPlayingGame.boardArrangement.cardObjs.get(tileID).getFirstImageUsed());
		Log.d (TAG, " 					 : curCardOnTile.getImageURI2 is: " + mPlayingGame.boardArrangement.cardObjs.get(tileID).getImageURI2());

        if (!Music.OFF) {
            String audioResourceName = mPlayingGame.boardArrangement.cardObjs.get(tileID).getAudioURI().substring(Themes.URI_AUDIO.length());
            Log.d (TAG, "                    : audioResourceName: " + audioResourceName);
            int audioResourceId = Shared.context.getResources().getIdentifier(audioResourceName, "raw", Shared.context.getPackageName());
            Log.d (TAG, "                    : audioResourceId: " + audioResourceId);
            MediaPlayer curTileAudio = MediaPlayer.create (Shared.context, audioResourceId);
            curTileAudio.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer curTileAudio) {
						curTileAudio.reset();
						curTileAudio.release();
						curTileAudio = null;
					}
            });
            curTileAudio.start();
            long sampleDuration = Music.getAudioDuration (audioResourceId);
            Log.d (TAG, "                    : sampleDuration: " + sampleDuration);

		}
		else {
			Toast.makeText(Shared.context, "Please turn on game audio to play in this mode", Toast.LENGTH_LONG).show();
		}
	}

	public Game getActiveGame() {
		//Log.d (TAG, "method getActiveGame");
		return mPlayingGame;
	}

	public Theme getSelectedTheme() {
		//Log.d (TAG, "method getSelectedTheme);
		return mSelectedTheme;
	}

	public void setBackgroundImageView(ImageView backgroundImage) {
		//Log.d (TAG, "method setBackgroundImageView");
		mBackgroundImage = backgroundImage;
	}
}