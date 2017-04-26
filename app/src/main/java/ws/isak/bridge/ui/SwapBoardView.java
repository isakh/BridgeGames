package ws.isak.bridge.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

import ws.isak.bridge.common.SwapCardData;
import ws.isak.bridge.events.engine.SwapSelectedCardsEvent;

import ws.isak.bridge.model.SwapBoardConfiguration;
import ws.isak.bridge.model.SwapGame;
import ws.isak.bridge.model.SwapBoardArrangement;

import ws.isak.bridge.utils.ImageScaling;
import ws.isak.bridge.utils.SwapTileCoordinates;

/*
 * Class SwapBoardView comprises the code which builds the swap game board according to the 
 * dimensions found in the xml dimens file, the ratio of tiles to rows/columns that is defined
 * in the MatchBoardConfiguration class given the users' difficultyLevel selection, and the size of the
 * screen of the device.  The board is a 2D array of tiles, each tile mapped to a card object.
 *
 * @author isak
 */

public class SwapBoardView extends LinearLayout {

    public static final String TAG = "SwapBoardView";

    private LinearLayout.LayoutParams mRowLayoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    private LinearLayout.LayoutParams mTileLayoutParams;
    private int mScreenWidth;
    private int mScreenHeight;
    //an instance of the board configuration for the current game
    private SwapBoardConfiguration mSwapBoardConfiguration;
    //an instance of the board arrangement for the current game
    private SwapBoardArrangement mSwapBoardArrangement;
    //a mapping of each tile ID to a view TileView
    private Map<SwapTileCoordinates, SwapTileView> mTileViewMap;
    //an array list to hold the id's of the currently selected cards
    private List<SwapTileCoordinates> selectedTiles = new ArrayList<SwapTileCoordinates>();
    //a flag to keep track of whether zero or one cards has been selected already
    private boolean mSelected = false;      //false means that no card has been selected, true means one has
    //TODO do we need another flag to prevent a card from being selected twice?
    //the dimension of the tile to be drawn
    private int mSize;

    public SwapBoardView(Context context) {
        this(context, null);
        Log.d (TAG, "constructor BoardView");
    }

    public SwapBoardView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        Log.d (TAG, "SwapBoardView constructor");
        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.CENTER);
        int margin = getResources().getDimensionPixelSize(R.dimen.swap_margin_top);
        int padding = getResources().getDimensionPixelSize(R.dimen.swap_board_padding);
        mScreenHeight = getResources().getDisplayMetrics().heightPixels - margin - padding*2;               //TODO * proportion of screen for view
        mScreenWidth = getResources().getDisplayMetrics().widthPixels - padding*2 - ImageScaling.px(20);    //TODO * proportion of screen for view
        Log.d (TAG, " ... mScreenHeight: " + mScreenHeight + " | mScreenWidth: " + mScreenWidth);
        mTileViewMap = new HashMap<SwapTileCoordinates, SwapTileView>();
        setClipToPadding(false);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.d (TAG, "method onFinishInflate");
    }

    public static SwapBoardView fromXml(Context context, ViewGroup parent) {
        Log.d (TAG, "method fromXml: inflating layout for swap_board_view" +
                LayoutInflater.from(context).inflate(R.layout.swap_board_view, parent, false));
        return (SwapBoardView) LayoutInflater.from(context).inflate(R.layout.swap_board_view, parent, false);
    }

    //method setBoard is called from SwapGameFragment method buildBoard
    public void setBoard(SwapGame swapGame) {
        Log.d (TAG, "method setBoard ... at start");
        mSwapBoardConfiguration = swapGame.swapBoardConfiguration;
        mSwapBoardArrangement = swapGame.swapBoardArrangement;
        // calc preferred tiles' width and height based on display size
        int singleMargin = getResources().getDimensionPixelSize(R.dimen.swap_card_margin);
        float density = getResources().getDisplayMetrics().density;
        singleMargin = Math.max((int) (1 * density), (int) (singleMargin - mSwapBoardConfiguration.difficultyLevel * 2 * density));
        int sumMargin = 0;
        for (int row = 0; row < mSwapBoardConfiguration.numRows; row++) {
            sumMargin += singleMargin * 2;
        }
        int tilesHeight = (mScreenHeight - sumMargin) / mSwapBoardConfiguration.numRows;
        int tilesWidth = (mScreenWidth - sumMargin) / SwapBoardConfiguration.swapNumTilesInRow;
        mSize = Math.min(tilesHeight, tilesWidth);

        mTileLayoutParams = new LinearLayout.LayoutParams(mSize, mSize);
        mTileLayoutParams.setMargins(singleMargin, singleMargin, singleMargin, singleMargin);

        //TODO REMOVE WHEN DONE ***** DEBUGGING CODE - comment out when working:
        Log.d (TAG, "***** method setBoard: CHECK VALID BOARD ... iterate over swapBoardMap:");
        Iterator iterator = Shared.userData.getCurSwapGameData().getSwapBoardMap().entrySet().iterator();
        Log.d (TAG,  "*****             : iterator created: iterator.hasNext: " + iterator.hasNext());
        while (iterator.hasNext()) {
            Log.d (TAG, "*****              : inside iterator");
            Map.Entry pair = (Map.Entry)iterator.next();
            //System.out.println(pair.getKey() + " maps to " + pair.getValue());
            SwapTileCoordinates coords = (SwapTileCoordinates) pair.getKey();
            SwapCardData cardData = (SwapCardData) pair.getValue();
            Log.d (TAG, "... address of Map.entry: " + pair + " | coords: < " + coords.getSwapCoordRow() + "," + coords.getSwapCoordCol() +
                    " > | MAPS TO | cardID: < " + cardData.getCardID().getSwapCardSpeciesID() + "," +
                    cardData.getCardID().getSwapCardSegmentID());
        }
        //*****
        // build the ui
        Log.d (TAG, "method setBoard ... calling method buildBoard");
        buildBoard();
    }

    /*
     * Build the board
     */
    private void buildBoard() {
        Log.d (TAG, "method: buildBoard: mSwapBoardConfiguration.numRows: " + mSwapBoardConfiguration.numRows);
        for (int row = 0; row < mSwapBoardConfiguration.numRows; row++) {
            // add row
            Log.d (TAG, "... calling addBoardRow");
            addBoardRow(row);
        }
        setClipChildren(false);
    }

    //Add each row to the board.
    private void addBoardRow(int rowNum) {
        Log.d (TAG, "method: addBoardRow");
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER);

        addRowControls(linearLayout);     //this method will draw a pair of buttons to the left of each row (play/pauseClock?)
        Log.d (TAG, "method addBoardRow: Shared.userData.getCurSwapGameData.getSwapBoardMap @: " + Shared.userData.getCurSwapGameData().getSwapBoardMap());

        for (int curTileInRow = 0; curTileInRow < SwapBoardConfiguration.swapNumTilesInRow; curTileInRow++) {
            SwapTileCoordinates coords = Shared.userData.getCurSwapGameData().getMapSwapTileCoordinatesFromLoc(new SwapTileCoordinates(rowNum,curTileInRow));
            addTile (coords, linearLayout);
        }

        // add to this view
        addView(linearLayout, mRowLayoutParams);
        linearLayout.setClipChildren(false);
    }

    private void addRowControls (LinearLayout rowLayout) {
        final SwapControlsView swapControlsView = SwapControlsView.fromXml(getContext(), parent);
    }

    // Add each tile to the board at position curTileOnBoard
    private void addTile(final SwapTileCoordinates curTileOnBoard, ViewGroup parent) {
        Log.d (TAG, "method addTile: address of curTileOnBoard: " + curTileOnBoard +
                " | curTileOnBoard coords: < " + curTileOnBoard.getSwapCoordRow() + "," +
                curTileOnBoard.getSwapCoordCol() + " >" + " | parent.getVisibility: " + parent.getVisibility() +
                " | parent.isShown: " + parent.isShown());
        final SwapTileView swapTileView = SwapTileView.fromXml(getContext(), parent);
        swapTileView.setLayoutParams(mTileLayoutParams);
        parent.addView(swapTileView);
        parent.setClipChildren(false);
        mTileViewMap.put(curTileOnBoard, swapTileView);

        //FIXME debugging code - remove when solved
        Log.d (TAG, "... mTileViewMap @: " + mTileViewMap);
        Iterator iterator = mTileViewMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            //System.out.println(pair.getKey() + " maps to " + pair.getValue());
            SwapTileCoordinates coords = (SwapTileCoordinates) pair.getKey();
            SwapTileView tileView = (SwapTileView) pair.getValue();
            Log.d(TAG, "method getSwapCardDataFromSwapBoardMap: Searching... coords: < " +
                    coords.getSwapCoordRow() + "," + coords.getSwapCoordCol() +
                    " > | MAPS TO | SwapTileView: " + tileView);
        }
        //TODO end debug block

        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... params) {
                Log.d (TAG, "*** method addTile: new AsyncTask: override doInBackground: curTileOnBoard is: " +
                        curTileOnBoard + "| coords are: < " + curTileOnBoard.getSwapCoordRow() +
                        "," + curTileOnBoard.getSwapCoordCol() + " > | mSize is: " + mSize);
                //gets one of four bitmaps depending on flags at current coordinates
                return mSwapBoardArrangement.getSwapTileBitmap(curTileOnBoard, mSize);
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                Log.d (TAG, "*** method addTile: Overriding onPostExecute: setting bitmap 'result'");
                swapTileView.setTileImage(result);
                Log.d (TAG, "... addTile: onPostExecute: swapTileView.getVisibility: " +
                        swapTileView.getVisibility() + " | swapTileView.isShown: " + swapTileView.isShown());
                swapTileView.setVisibility(VISIBLE);
                Log.d (TAG, "... addTile: onPostExecute: set VISIBLE: swapTileView.getVisibility: " +
                        swapTileView.getVisibility() + " | swapTileView.isShown: " + swapTileView.isShown());
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
                    Log.d(TAG, "			   : curCardOnTile is: " + mSwapBoardArrangement.swapBoardMap.get(curTileOnBoard).getCardID());

                    //If this is the first tile being clicked, we need to change the state of SwapGameData.isGameStarted()
                    //If this is the first tile we need to set the SwapGameData.setGameStartTimeStamp()
                    //Whether this is not the first tile or not, we need to record that click has been made, it's time, and update accordingly
                    Log.d(TAG, "**** Update SwapGameData with current timing information (and card info) ****");
                    //do the following if it is the first click in a game
                    if (!Shared.userData.getCurSwapGameData().isGameStarted()) {     //if this is the first card being flipped
                        Log.d (TAG, "This is the First Tile Flipped In SwapGame");
                        Shared.userData.getCurSwapGameData().setGameStarted(true);
                        Log.d (TAG, "   ***: getGameStarted: " + Shared.userData.getCurSwapGameData().isGameStarted());
                        Shared.userData.getCurSwapGameData().setGameStartTimestamp(now);
                        Log.d (TAG, "   ***: getGameStartTimestamp: " + Shared.userData.getCurSwapGameData().getGameStartTimestamp());
                    }
                    //do the following on each click:
                    //  - set the gamePlayDuration to (now - startTimeStamp)
                    Shared.userData.getCurSwapGameData().appendToGamePlayDurations(now - Shared.userData.getCurSwapGameData().getGameStartTimestamp());
                    Log.d (TAG, "   ***: queryGamePlayDuration @ array location numTurnsTaken: " + Shared.userData.getCurSwapGameData().queryGamePlayDurations(Shared.userData.getCurSwapGameData().getNumTurnsTaken()));
                    //  - time to append is (current time - queryGamePlayDuration[numTurns - 1] unless first turn in which case 0))
                    if (Shared.userData.getCurSwapGameData().getNumTurnsTaken() == 0) {
                        Shared.userData.getCurSwapGameData().appendToTurnDurations(0);
                        Log.d(TAG, " *****: | System time: " + now +
                                " | gameStartTimeStamp: " + Shared.userData.getCurSwapGameData().getGameStartTimestamp() +
                                " | numTurnsTaken: " + Shared.userData.getCurSwapGameData().getNumTurnsTaken() +
                                " | gamePlayDuration @ numTurnsTaken: " + Shared.userData.getCurSwapGameData().queryGamePlayDurations(Shared.userData.getCurSwapGameData().getNumTurnsTaken()) +
                                " | elapsed turn time: " + (Shared.userData.getCurSwapGameData().queryGamePlayDurations(Shared.userData.getCurSwapGameData().getNumTurnsTaken())));
                    }
                    else {
                        Shared.userData.getCurSwapGameData().appendToTurnDurations(now - (Shared.userData.getCurSwapGameData().getGameStartTimestamp() + Shared.userData.getCurSwapGameData().queryGamePlayDurations(Shared.userData.getCurSwapGameData().getNumTurnsTaken() - 1)));
                        Log.d(TAG, " *****: | System time: " + now +
                                " | gameStartTimeStamp: " + Shared.userData.getCurSwapGameData().getGameStartTimestamp() +
                                " | numTurnsTaken: " + Shared.userData.getCurSwapGameData().getNumTurnsTaken() +
                                " | gamePlayDuration @ numTurnsTaken: " + Shared.userData.getCurSwapGameData().queryGamePlayDurations(Shared.userData.getCurSwapGameData().getNumTurnsTaken()) +
                                " | elapsed turn time: " + (Shared.userData.getCurSwapGameData().queryGamePlayDurations(Shared.userData.getCurSwapGameData().getNumTurnsTaken()) - Shared.userData.getCurSwapGameData().queryGamePlayDurations(Shared.userData.getCurSwapGameData().getNumTurnsTaken() - 1)));
                    }

                    //if this tile being clicked is the first tile to be clicked on the board
                    if (mSelected == false) {
                        mSelected = true;
                        swapTileView.select();
                        selectedTiles.add(curTileOnBoard);
                    }
                    else {
                        //TODO if one tile already selected - highlight second tile and animate swap (call engine via event)
                        //FIXME - shall the swap event involve creating an updated map and pushing the old one to the SwapCardGameData list of Maps?
                        swapTileView.select();
                        Log.d(TAG, "method addTile: tileView.setOnClickListener: Overriding onClick: new SwapSelectCardEvent");
                        Shared.eventBus.notify(new SwapSelectedCardsEvent(selectedTiles.get(0), selectedTiles.get(1)));
                        mSelected = false;
                        selectedTiles = null;
                    }

                    //  - update the number of turns taken
                    Shared.userData.getCurSwapGameData().incrementNumTurnsTaken();
                    Log.d (TAG, "   ***: numTurnsTaken postIncrement: " + Shared.userData.getCurSwapGameData().getNumTurnsTaken());


                    //TODO Remove this? or should it be an option: Shared.eventBus.notify(new PlayCardAudioEvent(curTileOnBoard));
                } else if (1 == 2 ) { //TODO variable to check if card has been selected already //error check if locked
                    Log.d(TAG, "   : onClick Failed: the card has already been mSelected: " + mSelected); //TODO should we allow double click to be unselect?
                } else if (!swapTileView.isSelected()) {                   //error check if card already selected
                    Log.d(TAG, "   : onClick Failed: !swapTileView.isSelected(): " + !swapTileView.isSelected());
                    Toast.makeText(Shared.context, "cannot select card, already selected", Toast.LENGTH_SHORT).show(); //TODO should we allow double click to be unselect?
                } else if (!Audio.MIX && Audio.getIsAudioPlaying()) {     //error mix is off and audio already playing
                    Log.d(TAG, "   : onClick Failed: Audio.Mix is: " + Audio.MIX + " & Audio.getIsAudioPlaying() is: " + Audio.getIsAudioPlaying());
                    //TODO - does this make sense? Toast.makeText(Shared.context, "cannot select card, wait for audio to finish or set mix ON in settings", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(swapTileView, "scaleX", 0.8f, 1f);
        scaleXAnimator.setInterpolator(new BounceInterpolator());
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(swapTileView, "scaleY", 0.8f, 1f);
        scaleYAnimator.setInterpolator(new BounceInterpolator());
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator);
        animatorSet.setDuration(500);                                   //FIXME - make this timing a const in xml
        swapTileView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        animatorSet.start();
    }

    public void unSelectAll() {
        Log.d (TAG, "method unSelectAll ... at start");
        for (SwapTileCoordinates id : selectedTiles) {
            //for (int id = 0; id < flippedUp.size(); id++) {
            mTileViewMap.get(id).unSelect();
            Log.d (TAG, "method unSelectAll: current id in list selectedTiles is: " + id);
        }
        selectedTiles.clear();
        mSelected = false;
    }
}