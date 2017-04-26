package ws.isak.bridge.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;

import android.os.AsyncTask;

import android.util.Log;
import android.util.AttributeSet;

import android.content.Context;
import android.graphics.Bitmap;

import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;

import ws.isak.bridge.R;
import ws.isak.bridge.common.Audio;
import ws.isak.bridge.common.Shared;
import ws.isak.bridge.events.ui.MatchFlipCardEvent;
import ws.isak.bridge.events.engine.PlayCardAudioEvent;
import ws.isak.bridge.model.MatchBoardConfiguration;
import ws.isak.bridge.model.MatchGame;
import ws.isak.bridge.model.MatchBoardArrangement;
import ws.isak.bridge.utils.ImageScaling;
import ws.isak.bridge.utils.TimerCountdown;

/*
 * Class MatchBoardView comprises the code which builds the match game board according to the dimensions
 * found in the xml dimens file and according to the the ratios of tiles to rows/columns that is defined
 * in the MatchBoardConfiguration class given the users' difficultyLevel selection.  The board is a 2D array
 * of tiles each tile mapped to a card object.
 *
 * @author isak
 */

public class MatchBoardView extends LinearLayout {

	public static final String TAG = "MatchBoardView";

	private LinearLayout.LayoutParams mRowLayoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	private LinearLayout.LayoutParams mTileLayoutParams;
	private int mScreenWidth;
	private int mScreenHeight;
	private MatchBoardConfiguration mMatchBoardConfiguration;				//an instance of the board configuration for the current game
	private MatchBoardArrangement mMatchBoardArrangement;					//an instance of the board arrangement for the current game
	private Map<Integer, MatchTileView> mViewReference;				//a mapping of each tile ID (integer curTileOnBoard) to a view TileView
	private List<Integer> flippedUp = new ArrayList<Integer>();		//an array list to hold the id's of the currently flipped up cards
	private boolean mLocked = false;							//a flag to keep track of whether one or two cards has been flipped
	private int mSize;											//the dimension of the tile to be drawn

	public MatchBoardView(Context context) {
		this(context, null);
        Log.d (TAG, "constructor BoardView");
	}

	public MatchBoardView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		setOrientation(LinearLayout.VERTICAL);
		setGravity(Gravity.CENTER);
		int margin = getResources().getDimensionPixelSize(R.dimen.match_margin_top_timer);
		int padding = getResources().getDimensionPixelSize(R.dimen.match_board_padding);
		mScreenHeight = getResources().getDisplayMetrics().heightPixels - margin - padding*2;
		mScreenWidth = getResources().getDisplayMetrics().widthPixels - padding*2 - ImageScaling.px(20);
		mViewReference = new HashMap<Integer, MatchTileView>();
		setClipToPadding(false);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
        Log.d (TAG, "method onFinishInflate");
	}

	public static MatchBoardView fromXml(Context context, ViewGroup parent) {
        Log.d (TAG, "(MatchBoardView) LayoutInflater.from(context).inflate(R.layout.match_board_view, parent, false): " +
                LayoutInflater.from(context).inflate(R.layout.match_board_view, parent, false));
		return (MatchBoardView) LayoutInflater.from(context).inflate(R.layout.match_board_view, parent, false);
	}

	//method setBoard is called from MatchGameFragment method buildBoard
	public void setBoard(MatchGame matchGame) {
		Log.d (TAG, "method setBoard ... at start");
		mMatchBoardConfiguration = matchGame.matchBoardConfiguration;
		mMatchBoardArrangement = matchGame.matchBoardArrangement;
		// calc preferred tiles' width and height based on display size
		int singleMargin = getResources().getDimensionPixelSize(R.dimen.match_card_margin);
		float density = getResources().getDisplayMetrics().density;
		singleMargin = Math.max((int) (1 * density), (int) (singleMargin - mMatchBoardConfiguration.difficulty * 2 * density));
		int sumMargin = 0;
		for (int row = 0; row < mMatchBoardConfiguration.numRows; row++) {
			sumMargin += singleMargin * 2;
		}
		int tilesHeight = (mScreenHeight - sumMargin) / mMatchBoardConfiguration.numRows;
		int tilesWidth = (mScreenWidth - sumMargin) / mMatchBoardConfiguration.numTilesInRow;
		mSize = Math.min(tilesHeight, tilesWidth);

		mTileLayoutParams = new LinearLayout.LayoutParams(mSize, mSize);
		mTileLayoutParams.setMargins(singleMargin, singleMargin, singleMargin, singleMargin);

		// build the ui
		Log.d (TAG, "method setBoard ... calling method buildBoard");
		buildBoard();
	}

	/*
	 * Build the board
	 */
	private void buildBoard() {
		//Log.d (TAG, "method: buildBoard");
		for (int row = 0; row < mMatchBoardConfiguration.numRows; row++) {
			// add row
			addBoardRow(row);
		}

		setClipChildren(false);
	}

	/*
	 * Add each row to the board.
	 */
	private void addBoardRow(int rowNum) {
		//Log.d (TAG, "method: addBoardRow");
		LinearLayout linearLayout = new LinearLayout(getContext());
		linearLayout.setOrientation(LinearLayout.HORIZONTAL);
		linearLayout.setGravity(Gravity.CENTER);

		for (int curTileInRow = 0; curTileInRow < mMatchBoardConfiguration.numTilesInRow; curTileInRow++) {
			addTile(rowNum * mMatchBoardConfiguration.numTilesInRow + curTileInRow, linearLayout);
		}

		// add to this view
		addView(linearLayout, mRowLayoutParams);
		linearLayout.setClipChildren(false);
	}

	/*
	 * Add each tile to the row.
	 */
	private void addTile(final int curTileOnBoard, ViewGroup parent) {
		Log.d (TAG, "method addTile init: parent.getVisibility: " + parent.getVisibility() + " | parent.isShown: " + parent.isShown());
		final MatchTileView matchTileView = MatchTileView.fromXml(getContext(), parent);
		matchTileView.setLayoutParams(mTileLayoutParams);
		parent.addView(matchTileView);
		parent.setClipChildren(false);
		mViewReference.put(curTileOnBoard, matchTileView);

		new AsyncTask<Void, Void, Bitmap>() {

			@Override
			protected Bitmap doInBackground(Void... params) {
				//Log.d (TAG, "*** method: addTile: new AsyncTask: override doInBackground: calling getMatchTileBitmap: curTileOnBoard is: " + curTileOnBoard + " mSize is: " + mSize);
				return mMatchBoardArrangement.getMatchTileBitmap(curTileOnBoard, mSize);  //this gets one of two bitmaps depending on flags
			}
			
			@Override
			protected void onPostExecute(Bitmap result) {
				matchTileView.setTileImage(result);
                Log.d (TAG, "... addTile: onPostExecute: matchTileView.getVisibility: " +
                        matchTileView.getVisibility() + " | matchTileView.isShown: " + matchTileView.isShown());
			}
		}.execute();
		
		matchTileView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
                //if the game has been started and the timer is paused, the game cannot be played until play resumes
                if (Shared.userData.getCurMatchGame().isGameStarted() && Shared.currentMatchGame.gameClock.isClockPaused()) {
                    Toast.makeText(Shared.context, "Please Restart The Timer To Play", Toast.LENGTH_LONG).show();
                    //FIXME animate alpha on button to make it flash? does this do it??
                    /*
                    ImageView mTimerPlayPause = (ImageView) findViewById(R.id.time_bar_play_pause_button);
                    AlphaAnimation animation1 = new AlphaAnimation(1.0f, 0.5f);
                    animation1.setDuration(500);
                    mTimerPlayPause.setAlpha(0f);
                    mTimerPlayPause.startAnimation(animation1);
                    mTimerPlayPause.setImageAlpha(128);
                    AlphaAnimation animation2 = new AlphaAnimation(0.5f, 1.0f);
                    animation2.setDuration(500);
                    mTimerPlayPause.setAlpha(0f);
                    mTimerPlayPause.startAnimation(animation2);
                    mTimerPlayPause.setImageAlpha(128);
                    */
                }
                //if the game has not been started, or is in play and the timer is not paused:
                else {
                    //keep local track of click time
                    long now = System.currentTimeMillis();
                    // allow click in two instances: one with Mix ON vs one with Mix OFF and a check that no other audio is playing
                    if ((!mLocked && matchTileView.isFlippedDown() && Audio.MIX) ||
                            (!mLocked && matchTileView.isFlippedDown() && (!Audio.MIX && !Audio.getIsAudioPlaying())) &&
                                    !Shared.currentMatchGame.gameClock.isClockPaused()) {
                        Log.d(TAG, "			   : curTileOnBoard is: " + curTileOnBoard);
                        Log.d(TAG, "			   : curCardOnTile is: " + mMatchBoardArrangement.cardObjs.get(curTileOnBoard).getCardID());
                        Log.d(TAG, " 			   : curCardOnTile.getAudioURI is: " + mMatchBoardArrangement.cardObjs.get(curTileOnBoard).getAudioURI());
                        Log.d(TAG, " 			   : curCardOnTile.getImageURI1 is: " + mMatchBoardArrangement.cardObjs.get(curTileOnBoard).getImageURI1());
                        Log.d(TAG, " 			   : curCardOnTile.getPairedImageDiffer is: " + mMatchBoardArrangement.cardObjs.get(curTileOnBoard).getPairedImageDiffer());
                        Log.d(TAG, " 			   : curCardOnTile.getFirstImageUsed is: " + mMatchBoardArrangement.cardObjs.get(curTileOnBoard).getFirstImageUsed());
                        Log.d(TAG, " 			   : curCardOnTile.getImageURI2 is: " + mMatchBoardArrangement.cardObjs.get(curTileOnBoard).getImageURI2());

                        //TODO If this is the first time a tile is clicked, start the timer counting down and set the timer_pause_button
                        //If this is the first tile being clicked, we need to change the state of MatchGameData.isGameStarted()
                        //If this is the first tile we need to set the MatchGameData.setGameStartTimeStamp()
                        //Whether this is not the first tile or not, we need to record that click has been made, it's time, and update accordingly
                        Log.d(TAG, "**** Update MatchGameData with current timing information (and card info) ****");
                        //do the following if it is the first click in a game
                        if (!Shared.userData.getCurMatchGame().isGameStarted() && !Shared.currentMatchGame.gameClock.isClockPaused()) {     //if this is the first card being flipped
                            Log.d(TAG, "This is the First Tile Flipped In MatchGame");
                            Shared.userData.getCurMatchGame().setGameStarted(true);
                            Log.d(TAG, "   ***: getGameStarted: " + Shared.userData.getCurMatchGame().isGameStarted());
                            Shared.userData.getCurMatchGame().setGameStartTimestamp(now);
                            Log.d(TAG, "   ***: getGameStartTimestamp: " + Shared.userData.getCurMatchGame().getGameStartTimestamp());
                            //FIXME Shared.currentMatchGame.gameClock.resumeClock();
                        }
                        //do the following on each click:
                        //TODO include a check to see if the timer is paused? if so break out and toast
                        //  - set the gamePlayDuration to (now - startTimeStamp)
                        Shared.userData.getCurMatchGame().appendToGamePlayDurations(now - Shared.userData.getCurMatchGame().getGameStartTimestamp());
                        Log.d(TAG, "   ***: queryGamePlayDuration @ array location numTurnsTaken: " + Shared.userData.getCurMatchGame().queryGamePlayDurations(Shared.userData.getCurMatchGame().getNumTurnsTaken()));
                        //  - time to append is (current time - queryGamePlayDuration[numTurns - 1] unless first turn in which case 0))
                        if (Shared.userData.getCurMatchGame().getNumTurnsTaken() == 0) {
                            Shared.userData.getCurMatchGame().appendToTurnDurations(0);
                            Log.d(TAG, " *****: | System time: " + now +
                                    " | gameStartTimeStamp: " + Shared.userData.getCurMatchGame().getGameStartTimestamp() +
                                    " | numTurnsTaken: " + Shared.userData.getCurMatchGame().getNumTurnsTaken() +
                                    " | gamePlayDuration @ numTurnsTaken: " + Shared.userData.getCurMatchGame().queryGamePlayDurations(Shared.userData.getCurMatchGame().getNumTurnsTaken()) +
                                    " | elapsed turn time: " + (Shared.userData.getCurMatchGame().queryGamePlayDurations(Shared.userData.getCurMatchGame().getNumTurnsTaken())));
                        } else {
                            Shared.userData.getCurMatchGame().appendToTurnDurations(now - (Shared.userData.getCurMatchGame().getGameStartTimestamp() + Shared.userData.getCurMatchGame().queryGamePlayDurations(Shared.userData.getCurMatchGame().getNumTurnsTaken() - 1)));
                            Log.d(TAG, " *****: | System time: " + now +
                                    " | gameStartTimeStamp: " + Shared.userData.getCurMatchGame().getGameStartTimestamp() +
                                    " | numTurnsTaken: " + Shared.userData.getCurMatchGame().getNumTurnsTaken() +
                                    " | gamePlayDuration @ numTurnsTaken: " + Shared.userData.getCurMatchGame().queryGamePlayDurations(Shared.userData.getCurMatchGame().getNumTurnsTaken()) +
                                    " | elapsed turn time: " + (Shared.userData.getCurMatchGame().queryGamePlayDurations(Shared.userData.getCurMatchGame().getNumTurnsTaken()) - Shared.userData.getCurMatchGame().queryGamePlayDurations(Shared.userData.getCurMatchGame().getNumTurnsTaken() - 1)));
                        }
                        //  - append the clicked card to array
                        Shared.userData.getCurMatchGame().appendToCardsSelected(mMatchBoardArrangement.cardObjs.get(curTileOnBoard).getCardID());
                        Log.d(TAG, "   ***: method addTile:  appended to cards selected array: cardObjArray[numTurnsTaken].cardID: " + Shared.userData.getCurMatchGame().queryCardsSelectedArray(Shared.userData.getCurMatchGame().getNumTurnsTaken()));
                        //  - update the number of turns taken
                        Shared.userData.getCurMatchGame().incrementNumTurnsTaken();
                        Log.d(TAG, "   ***: numTurnsTaken postIncrement: " + Shared.userData.getCurMatchGame().getNumTurnsTaken());

                        //flip the current tile up
                        matchTileView.flipUp();
                        //Log.d (TAG, " *** method addTile: onClick: called tileView.flipUp() *** ");
                        flippedUp.add(curTileOnBoard);
                        //Log.d (TAG, " *** flippedUp.size() is: " + flippedUp.size() + " *** ");
                        if (flippedUp.size() == 2) {
                            mLocked = true;
                        }
                        Log.d(TAG, "method addTile: tileView.setOnClickListener: Overriding onClick: new MatchFlipCardEvent");
                        Shared.eventBus.notify(new MatchFlipCardEvent(curTileOnBoard));
                        Log.d(TAG, "method addTile: tileView.setOnClickListener: Overriding onClick: new PlayCardAudioEvent");
                        Shared.eventBus.notify(new PlayCardAudioEvent(curTileOnBoard));
                    } else if (mLocked) {                                     //error check if locked
                        Log.d(TAG, "   : onClick Failed: mLocked: " + mLocked);
                        Toast.makeText(Shared.context, "cannot flip card, mLocked", Toast.LENGTH_SHORT).show();
                    } else if (!matchTileView.isFlippedDown()) {                   //error check if card flipped
                        Log.d(TAG, "   : onClick Failed: !tileView.isFlippedDown(): " + !matchTileView.isFlippedDown());
                        Toast.makeText(Shared.context, "cannot flip card, already flipped", Toast.LENGTH_SHORT).show();
                    } else if (!Audio.MIX && Audio.getIsAudioPlaying()) {     //error mix is off and audio already playing
                        Log.d(TAG, "   : onClick Failed: Audio.Mix is: " + Audio.MIX + " & Audio.getIsAudioPlaying() is: " + Audio.getIsAudioPlaying());
                        Toast.makeText(Shared.context, "cannot flip card, wait for audio to finish or set mix ON in settings", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(matchTileView, "scaleX", 0.8f, 1f);
        scaleXAnimator.setInterpolator(new BounceInterpolator());
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(matchTileView, "scaleY", 0.8f, 1f);
        scaleYAnimator.setInterpolator(new BounceInterpolator());
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator);
        animatorSet.setDuration(500);
        matchTileView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        animatorSet.start();
	}

	public void flipDownAll() {
		Log.d (TAG, "method flipDownAll ... at start");
		for (Integer id : flippedUp) {
		//for (int id = 0; id < flippedUp.size(); id++) {
			mViewReference.get(id).flipDown();
			Log.d (TAG, "method flipDownAll: current id in list flippedUp is: " + id);
		}
		flippedUp.clear();
		mLocked = false;
	}

	public void hideCards(int id1, int id2) {
		Log.d (TAG, "method hideCards ... at start");
		animateHide(mViewReference.get(id1));
		animateHide(mViewReference.get(id2));
		flippedUp.clear();
		mLocked = false;
	}

	protected void animateHide(final MatchTileView view) {
		ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 0f);
		animator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				view.setLayerType(View.LAYER_TYPE_NONE, null);
				view.setVisibility(View.INVISIBLE);
			}
		});
		animator.setDuration(100);                          //TODO set this duration in xml
		view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
		animator.start();
	}
}