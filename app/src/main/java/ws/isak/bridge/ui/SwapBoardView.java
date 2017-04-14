package ws.isak.bridge.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;

import android.os.AsyncTask;

import android.util.Log;
import android.util.AttributeSet;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.LinearLayout;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.Toast;

import ws.isak.bridge.R;

import ws.isak.bridge.common.Audio;
import ws.isak.bridge.common.Shared;

import ws.isak.bridge.events.engine.SwapSelectedCardsEvent;

import ws.isak.bridge.model.SwapBoardConfiguration;
import ws.isak.bridge.model.SwapGame;
import ws.isak.bridge.model.SwapBoardArrangement;

import ws.isak.bridge.utils.ImageScaling;
import ws.isak.bridge.utils.SwapTileCoordinates;

/*
 * Class SwapBoardView comprises the code which builds the swap game board according to the 
 * dimensions found in the xml dimens file, the the ratios of tiles to rows/columns that is defined
 * in the MatchBoardConfiguration class given the users' difficulty selection, and the size of the 
 * screen of the device.  The board is a 2D array of tiles, each tile mapped to a card object.
 *
 * @author isak
 */

public class SwapBoardView extends LinearLayout {

    public final String TAG = "BoardView";

    private LinearLayout.LayoutParams mRowLayoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    private LinearLayout.LayoutParams mTileLayoutParams;
    private int mScreenWidth;
    private int mScreenHeight;
    //an instance of the board configuration for the current game
    private SwapBoardConfiguration mSwapBoardConfiguration;
    //an instance of the board arrangement for the current game
    private SwapBoardArrangement mSwapBoardArrangement;
    //a mapping of each tile ID (integer curTileOnBoard) to a view TileView
    private Map<SwapTileCoordinates, SwapTileView> mTileViewReference;
    //an array list to hold the id's of the currently selected cards
    private List<SwapTileCoordinates> selectedTiles = new ArrayList<SwapTileCoordinates>();
    //a flag to keep track of whether one or two cards has been selected already
    private boolean mSelected = false;
    //the dimension of the tile to be drawn
    private int mSize;

    public SwapBoardView(Context context) {
        this(context, null);
        Log.d (TAG, "constructor BoardView");
    }

    public SwapBoardView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.CENTER);
        int margin = getResources().getDimensionPixelSize(R.dimen.swap_margin_top);
        int padding = getResources().getDimensionPixelSize(R.dimen.swap_board_padding);
        mScreenHeight = getResources().getDisplayMetrics().heightPixels - margin - padding*2;
        mScreenWidth = getResources().getDisplayMetrics().widthPixels - padding*2 - ImageScaling.px(20);
        mTileViewReference = new HashMap<SwapTileCoordinates, SwapTileView>();
        setClipToPadding(false);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public static SwapBoardView fromXml(Context context, ViewGroup parent) {
        return (SwapBoardView) LayoutInflater.from(context).inflate(R.layout.swap_board_view, parent, false);
    }

    //method setBoard is called from MatchGameFragment method buildBoard
    public void setBoard(SwapGame swapGame) {
        Log.d (TAG, "method setBoard ... at start");
        mSwapBoardConfiguration = swapGame.swapBoardConfiguration;
        mSwapBoardArrangement = swapGame.swapBoardArrangement;
        // calc preferred tiles' width and height based on display size
        int singleMargin = getResources().getDimensionPixelSize(R.dimen.swap_card_margin);
        float density = getResources().getDisplayMetrics().density;
        singleMargin = Math.max((int) (1 * density), (int) (singleMargin - mSwapBoardConfiguration.difficulty * 2 * density));
        int sumMargin = 0;
        for (int row = 0; row < mSwapBoardConfiguration.numRows; row++) {
            sumMargin += singleMargin * 2;
        }
        int tilesHeight = (mScreenHeight - sumMargin) / mSwapBoardConfiguration.numRows;
        int tilesWidth = (mScreenWidth - sumMargin) / SwapBoardConfiguration.swapNumTilesInRow;
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
        for (int row = 0; row < mSwapBoardConfiguration.numRows; row++) {
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

        for (int curTileInRow = 0; curTileInRow < SwapBoardConfiguration.swapNumTilesInRow; curTileInRow++) {
            addTile (new SwapTileCoordinates(rowNum, curTileInRow), linearLayout);
        }

        // add to this view
        addView(linearLayout, mRowLayoutParams);
        linearLayout.setClipChildren(false);
    }

    // Add each tile to the board at position curTileOnBoard
    private void addTile(final SwapTileCoordinates curTileOnBoard, ViewGroup parent) {
        //Log.d (TAG, "method addTile init");
        final SwapTileView swapTileView = SwapTileView.fromXml(getContext(), parent);
        swapTileView.setLayoutParams(mTileLayoutParams);
        parent.addView(swapTileView);
        parent.setClipChildren(false);
        mTileViewReference.put(curTileOnBoard, swapTileView);

        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... params) {
                //Log.d (TAG, "*** method: addTile: new AsyncTask: override doInBackground: calling getMatchTileBitmap: curTileOnBoard is: " + curTileOnBoard + " mSize is: " + mSize);
                return mSwapBoardArrangement.getSwapTileBitmap(curTileOnBoard, mSize);  //this gets one of four bitmaps depending on flags
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                swapTileView.setTileImage(result);
            }
        }.execute();

        swapTileView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //keep local track of click time
                long now = System.currentTimeMillis();
                // allow click in two instances: one with Mix ON vs one with Mix OFF and a check that no other audio is playing
                if (!mSelected && !Audio.getIsAudioPlaying()) {         //if tile is selected can't select again? TODO maybe second click is unselect??
                    Log.d(TAG, "			   : curTileOnBoard is: " + curTileOnBoard);
                    Log.d(TAG, "			   : curCardOnTile is: " + mSwapBoardArrangement.cardObjs.get(curTileOnBoard).getCardID());
                    //FIXME Log.d(TAG, " 			   : curCardOnTile.segmentActive is: " + mSwapBoardArrangement.cardObjs.get(curTileOnBoard).getSegmentActive());

                    //If this is the first tile being clicked, we need to change the state of SwapGameData.isGameStarted()
                    //If this is the first tile we need to set the SwapGameData.setGameStartTimeStamp()
                    //Whether this is not the first tile or not, we need to record that click has been made, it's time, and update accordingly
                    Log.d(TAG, "**** Update MatchGameData with current timing information (and card info) ****");
                    //do the following if it is the first click in a game
                    if (!Shared.userData.getCurSwapGame().isGameStarted()) {     //if this is the first card being flipped
                        Log.d (TAG, "This is the First Tile Flipped In MatchGame");
                        Shared.userData.getCurSwapGame().setGameStarted(true);
                        Log.d (TAG, "   ***: getGameStarted: " + Shared.userData.getCurSwapGame().isGameStarted());
                        Shared.userData.getCurSwapGame().setGameStartTimestamp(now);
                        Log.d (TAG, "   ***: getGameStartTimestamp: " + Shared.userData.getCurSwapGame().getGameStartTimestamp());
                    }
                    //do the following on each click:
                    //  - set the gamePlayDuration to (now - startTimeStamp)
                    Shared.userData.getCurSwapGame().appendToGamePlayDurations(now - Shared.userData.getCurSwapGame().getGameStartTimestamp());
                    Log.d (TAG, "   ***: queryGamePlayDuration @ array location numTurnsTaken: " + Shared.userData.getCurSwapGame().queryGamePlayDurations(Shared.userData.getCurSwapGame().getNumTurnsTaken()));
                    //  - time to append is (current time - queryGamePlayDuration[numTurns - 1] unless first turn in which case 0))
                    if (Shared.userData.getCurSwapGame().getNumTurnsTaken() == 0) {
                        Shared.userData.getCurSwapGame().appendToTurnDurations(0);
                        Log.d(TAG, " *****: | System time: " + now +
                                " | gameStartTimeStamp: " + Shared.userData.getCurSwapGame().getGameStartTimestamp() +
                                " | numTurnsTaken: " + Shared.userData.getCurSwapGame().getNumTurnsTaken() +
                                " | gamePlayDuration @ numTurnsTaken: " + Shared.userData.getCurSwapGame().queryGamePlayDurations(Shared.userData.getCurSwapGame().getNumTurnsTaken()) +
                                " | elapsed turn time: " + (Shared.userData.getCurSwapGame().queryGamePlayDurations(Shared.userData.getCurSwapGame().getNumTurnsTaken())));
                    }
                    else {
                        Shared.userData.getCurSwapGame().appendToTurnDurations(now - (Shared.userData.getCurSwapGame().getGameStartTimestamp() + Shared.userData.getCurSwapGame().queryGamePlayDurations(Shared.userData.getCurSwapGame().getNumTurnsTaken() - 1)));
                        Log.d(TAG, " *****: | System time: " + now +
                                " | gameStartTimeStamp: " + Shared.userData.getCurSwapGame().getGameStartTimestamp() +
                                " | numTurnsTaken: " + Shared.userData.getCurSwapGame().getNumTurnsTaken() +
                                " | gamePlayDuration @ numTurnsTaken: " + Shared.userData.getCurSwapGame().queryGamePlayDurations(Shared.userData.getCurSwapGame().getNumTurnsTaken()) +
                                " | elapsed turn time: " + (Shared.userData.getCurSwapGame().queryGamePlayDurations(Shared.userData.getCurSwapGame().getNumTurnsTaken()) - Shared.userData.getCurSwapGame().queryGamePlayDurations(Shared.userData.getCurSwapGame().getNumTurnsTaken() - 1)));
                    }
                    //  - append the clicked card to array
                    Shared.userData.getCurSwapGame().appendToCardsSelected(mSwapBoardArrangement.cardObjs.get(curTileOnBoard).getCardID());
                    Log.d (TAG, "   ***: method addTile:  appended to cards selected array: cardObjArray[numTurnsTaken].cardID: " + Shared.userData.getCurSwapGame().queryCardsSelectedArray(Shared.userData.getCurSwapGame().getNumTurnsTaken()));
                    //  - update the number of turns taken
                    Shared.userData.getCurSwapGame().incrementNumTurnsTaken();
                    Log.d (TAG, "   ***: numTurnsTaken postIncrement: " + Shared.userData.getCurSwapGame().getNumTurnsTaken());

                    //select the current tile
                    swapTileView.select();
                    selectedTiles.add(curTileOnBoard);
                    if (selectedTiles.size() == 2) {        //change 2 to xml num tiles to select as for swapping?
                        //TODO set a boolean here so no more cards can be selected!
                        //FIXME method swapTiles ... should it go here?
                        // no need to call, will be called by event SwapSelectedCardsEvent swapTiles (selectedTiles.get(0), selectedTiles.get(1));
                    }
                    Log.d(TAG, "method addTile: tileView.setOnClickListener: Overriding onClick: new SwapSelectCardEvent");
                    Shared.eventBus.notify(new SwapSelectedCardsEvent(selectedTiles.get(0), selectedTiles.get(1)));
                    //TODO Remove this? or should it be an option: Shared.eventBus.notify(new PlayCardAudioEvent(curTileOnBoard));
                } else if (mSelected) {                                     //error check if locked
                    Log.d(TAG, "   : onClick Failed: the card has already been mSelected: " + mSelected); //TODO should we allow double click to be unselect?
                } else if (!swapTileView.isSelected()) {                   //error check if card already selected
                    Log.d(TAG, "   : onClick Failed: !swapTileView.isSelected(): " + !swapTileView.isSelected());
                    Toast.makeText(Shared.context, "cannot select card, already selected", Toast.LENGTH_SHORT).show(); //TODO should we allow double click to be unselect?
                } else if (!Audio.MIX && Audio.getIsAudioPlaying()) {     //error mix is off and audio already playing
                    Log.d(TAG, "   : onClick Failed: Audio.Mix is: " + Audio.MIX + " & Audio.getIsAudioPlaying() is: " + Audio.getIsAudioPlaying());
                    Toast.makeText(Shared.context, "cannot select card, wait for audio to finish or set mix ON in settings", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(swapTileView, "scaleX", 0.8f, 1f);
        scaleXAnimator.setInterpolator(new BounceInterpolator());
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(swapTileView, "scaleY", 0.8f, 1f);
        scaleYAnimator.setInterpolator(new BounceInterpolator());
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator);
        animatorSet.setDuration(500);
        swapTileView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        animatorSet.start();
    }

    public void unSelectAll() {
        Log.d (TAG, "method unSelectAll ... at start");
        for (SwapTileCoordinates id : selectedTiles) {
            //for (int id = 0; id < flippedUp.size(); id++) {
            mTileViewReference.get(id).unSelect();
            Log.d (TAG, "method unSelectAll: current id in list selectedTiles is: " + id);
        }
        selectedTiles.clear();
        mSelected = false;
    }
}