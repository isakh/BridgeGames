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
 * in the SwapBoardConfiguration class given the users' difficultyLevel selection, and the size of the
 * screen of the device.  The board is a 2D array (stored as a HashMap) of tiles coordinate objects,
 * each tile mapped to a card object.
 *
 * @author isak
 */

public class SwapBoardView extends LinearLayout {

    public static final String TAG = "SwapBoardView";

    private LinearLayout.LayoutParams mRowLayoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    private LinearLayout.LayoutParams mTileLayoutParams;
    private int mScreenWidth;           //FIXME - for now this is 80% of the board width hard coded, make dynamic? or set in xml
    private int mScreenHeight;
    //an instance of the board configuration for the current game
    private SwapBoardConfiguration mSwapBoardConfiguration;
    //an instance of the board arrangement for the current game
    private SwapBoardArrangement mSwapBoardArrangement;
    //a mapping of each tile ID to a view TileView
    //TODO verify that this remains unchanged within each game
    public Map<SwapTileCoordinates, SwapTileView> mTileViewMap;
    //an array list holds the id's of the currently selected cards
    public List<SwapTileCoordinates> selectedTiles = new ArrayList<SwapTileCoordinates>(0);
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
        int margin = getResources().getDimensionPixelSize(R.dimen.swap_game_timer_margin_top);
        int padding = getResources().getDimensionPixelSize(R.dimen.swap_board_padding);
        mScreenHeight = getResources().getDisplayMetrics().heightPixels - margin - padding*2;
        mScreenWidth = (int) Math.floor((getResources().getDisplayMetrics().widthPixels - padding*2 - ImageScaling.px(20)) * 0.8);    //TODO * proportion of screen for view - make less of a hack
        Log.d (TAG, " ... mScreenHeight: " + mScreenHeight + " | mScreenWidth: " + mScreenWidth);
        mTileViewMap = new HashMap<SwapTileCoordinates, SwapTileView>();        //TODO something with this!
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
        int singleMargin = getResources().getDimensionPixelSize(R.dimen.swap_card_margin); //FIXME - how to get borders working?  + getResources().getDimensionPixelSize(R.dimen.swap_game_tile_border);
        float density = getResources().getDisplayMetrics().density;
        singleMargin = Math.max((int) (1 * density), (int) (singleMargin - mSwapBoardConfiguration.difficultyLevel * 2 * density));
        int sumMargin = 0;
        for (int row = 0; row < mSwapBoardConfiguration.numRows; row++) {
            sumMargin += singleMargin * 2;
        }
        int tilesHeight = (mScreenHeight - sumMargin) / mSwapBoardConfiguration.numRows;
        int tilesWidth = (mScreenWidth - sumMargin) / SwapBoardConfiguration.swapNumTilesInRow;
        mSize = Math.min(tilesHeight, tilesWidth); //TODO could subtracting a 'border width' (say 5dp) solve the border issues?
        Shared.currentSwapGame.swapBoardArrangement.setTileSize(mSize);

        mTileLayoutParams = new LinearLayout.LayoutParams(mSize, mSize);
        mTileLayoutParams.setMargins(singleMargin, singleMargin, singleMargin, singleMargin);

        // build the ui
        Log.d (TAG, "method setBoard ... calling method buildBoard");
        buildBoard();
        Log.d (TAG, "method setBoard: calling method debugCoordsTileViewsMap");
        debugCoordsTileViewsMap("method setBoard");
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

        Log.d (TAG, "method addBoardRow: Shared.userData.getCurSwapGameData.getSwapBoardMap @: " + Shared.userData.getCurSwapGameData().getSwapBoardMap());
        for (int curTileInRow = 0; curTileInRow < SwapBoardConfiguration.swapNumTilesInRow; curTileInRow++) {
            SwapTileCoordinates coords = Shared.userData.getCurSwapGameData().getMapSwapTileCoordinatesFromLoc(new SwapTileCoordinates(rowNum,curTileInRow));
            addTile (coords, linearLayout);
        }

        // add to this view
        addView(linearLayout, mRowLayoutParams);
        linearLayout.setClipChildren(false);
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
        setSwapTileMap(curTileOnBoard, swapTileView);           //effectively mTileVieMap.put with debugging code
        Log.d (TAG, "SwapCardData for current location: " + Shared.userData.getCurSwapGameData().getSwapCardDataFromSwapBoardMap(curTileOnBoard));
        SwapCardData cardDataAtCurTile = Shared.userData.getCurSwapGameData().getSwapCardDataFromSwapBoardMap(curTileOnBoard);
        swapTileView.setTileDebugText(cardDataAtCurTile);

        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... params) {
                Log.d (TAG, "*** method addTile: new AsyncTask: override doInBackground: curTileOnBoard is: " +
                        curTileOnBoard + "| coords are: < " + curTileOnBoard.getSwapCoordRow() +
                        "," + curTileOnBoard.getSwapCoordCol() + " > | mSize is: " + mSize);
                //gets one of four bitmaps depending on flags at current coordinates
                Bitmap croppedBitmap = mSwapBoardArrangement.getSwapTileBitmap(curTileOnBoard, mSize);      //FIXME - making mSize smaller to leave room for border?
                Shared.userData.getCurSwapGameData().getSwapCardDataFromSwapBoardMap(curTileOnBoard).setCardBitmap(croppedBitmap);
                Log.d (TAG, "***** addTile: doInBackground: create bitmap: " +
                        Shared.userData.getCurSwapGameData().getSwapCardDataFromSwapBoardMap(curTileOnBoard).getCardBitmap() +
                        " FOR CARD @: < " + curTileOnBoard.getSwapCoordRow() + "," + curTileOnBoard.getSwapCoordCol() + " > ");
                return croppedBitmap;
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                Log.d (TAG, "*** method addTile: Overriding onPostExecute: setting bitmap 'result'");
                swapTileView.buildDrawingCache();    //FIXME does this help with later retrieval of bitmap?
                swapTileView.setTileImage(result);
                Log.d (TAG, "... addTile: onPostExecute: swapTileView.getVisibility: " +
                        swapTileView.getVisibility() + " | swapTileView.isShown: " + swapTileView.isShown());
                swapTileView.setVisibility(VISIBLE);
                Log.d (TAG, "... addTile: onPostExecute: set VISIBLE: swapTileView.getVisibility: " +
                        swapTileView.getVisibility() + " | swapTileView.isShown: " + swapTileView.isShown());
                swapTileView.invalidate();
            }
        }.execute();

        swapTileView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {        //FIXME, was (View v)
                //debug at start of onClick
                Log.d(TAG, "*** tile onClick: tile being clicked is swapTileView @: " + swapTileView +
                        " | swapTileView.isSelected (if true the second click is unselect): " + swapTileView.isSelected() +
                        " | Audio.getIsAudioPlaying: " + Audio.getIsAudioPlaying() +
                        " | selectedTiles.size: " + selectedTiles.size());


                //[0] if MIX is OFF and audio is playing - Toast and break out - user needs to click again
                if (!Audio.MIX && Audio.getIsAudioPlaying()) {
                    Toast.makeText(Shared.context, "PLEASE WAIT FOR SOUND TO FINISH PLAYING", Toast.LENGTH_SHORT).show();
                    return;     //TODO does return here break all the way out?
                }

                //[1] if the view for the tile has already been selected, the second click unSelects it
                //TODO how do we deal with timing here? or should we only time swaps as moves (current implementation)?
                if (swapTileView.isSelected()) {
                    Log.d(TAG, "***** ... DOUBLE CLICKING on a tile un-selects it");
                    swapTileView.unSelect();
                    Log.d(TAG, " ... unSelecting tile: swapTileView: " + swapTileView);
                    selectedTiles.clear();
                    Log.d(TAG, " ... clearing selectedTiles: selectedTiles @: " + selectedTiles +
                            " | selectedTiles.size(): " + selectedTiles.size());
                }

                //[2] else this is a viable turn
                else {
                    //keep local track of click time
                    long now = System.currentTimeMillis();

                    Toast.makeText(Shared.context, "Coordinates: < " + curTileOnBoard.getSwapCoordRow() +
                            "," + curTileOnBoard.getSwapCoordCol() + " >", Toast.LENGTH_SHORT).show();  //TODO remove toast?

                    //[2.0] if this tile being clicked is the first tile to be clicked on the board we
                    //change the state of SwapGameData.isGameStarted() and set the SwapGameData.setGameStartTimeStamp()
                    Log.d(TAG, "**** Update SwapGameData with current timing information (and card info) ****");
                    if (!Shared.userData.getCurSwapGameData().isGameStarted()) {     //if this is the first card being flipped
                        Log.d(TAG, "This is the First Tile Selected In SwapGame");
                        Shared.userData.getCurSwapGameData().setGameStarted(true);
                        Log.d(TAG, "   ***: getGameStarted: " + Shared.userData.getCurSwapGameData().isGameStarted());
                        Shared.userData.getCurSwapGameData().setGameStartTimestamp(now);
                        Log.d(TAG, "   ***: getGameStartTimestamp: " + Shared.userData.getCurSwapGameData().getGameStartTimestamp());
                        Shared.userData.getCurSwapGameData().appendToGamePlayDurations(now - Shared.userData.getCurSwapGameData().getGameStartTimestamp());
                        Shared.userData.getCurSwapGameData().appendToTurnDurations(0);
                        //output log of current SwapGameData State
                        Log.d(TAG, " *****: | System time: " + now +
                                " | gameStartTimeStamp: " + Shared.userData.getCurSwapGameData().getGameStartTimestamp() +
                                " | numTurnsTaken: " + Shared.userData.getCurSwapGameData().getNumTurnsTaken() +
                                " | gamePlayDuration @ numTurnsTaken: " + Shared.userData.getCurSwapGameData().queryGamePlayDurations(Shared.userData.getCurSwapGameData().getNumTurnsTaken()) +
                                " | elapsed turn time: " + (Shared.userData.getCurSwapGameData().queryGamePlayDurations(Shared.userData.getCurSwapGameData().getNumTurnsTaken())));
                    }

                    //[2.1] If this is the first card in a pair to be selected
                    if (!swapTileView.isSelected() && selectedTiles.size() == 0) {
                        Log.d(TAG, " ***** ... FIRST OF PAIR selected: swapTileView: " + swapTileView);
                        swapTileView.select();
                        Log.d(TAG, " ... post select() - swapTileView.isSelected: " + swapTileView.isSelected());
                        selectedTiles.add(curTileOnBoard);
                        Log.d(TAG, " ... curTileOnBoard added to selectedTiles: selectedTiles.size(): " + selectedTiles.size());
                    }

                    //[2.2.1] If this is the second card in the first pair to be selected - highlight second tile
                    // and animate swap (call via event for CoordToCard HashMap updates and perform Coord to TileView image
                    // updates here) and set the number of turns taken to 1
                    else if (Shared.userData.getCurSwapGameData().getNumTurnsTaken() == 0){
                        // we want to count turns only when pairs are to be swapped - first time turn duration is now - game start timestamp
                        Shared.userData.getCurSwapGameData().appendToTurnDurations(now - Shared.userData.getCurSwapGameData().getGameStartTimestamp());
                        Shared.userData.getCurSwapGameData().appendToGamePlayDurations(now - Shared.userData.getCurSwapGameData().getGameStartTimestamp());
                        Log.d(TAG, "***** ... SECOND OF PAIR selected: swapTileView@: " + swapTileView);
                        swapTileView.select();
                        Log.d(TAG, " ... post select() - swapTileView.isSelected: " + swapTileView.isSelected());
                        selectedTiles.add(curTileOnBoard);
                        Log.d(TAG, " ... curTileOnBoard added to selectedTiles: selectedTiles.size(): " + selectedTiles.size());
                        Log.d(TAG, "\n ... \n");

                        //call swap event
                        Shared.eventBus.notify(new SwapSelectedCardsEvent(selectedTiles.get(0), selectedTiles.get(1)));

                        //display current game stats
                        Log.d(TAG, " *****: | System time: " + now +
                                " | gameStartTimeStamp: " + Shared.userData.getCurSwapGameData().getGameStartTimestamp() +
                                " | numTurnsTaken: " + Shared.userData.getCurSwapGameData().getNumTurnsTaken() +
                                " | gamePlayDuration @ numTurnsTaken: " + Shared.userData.getCurSwapGameData().queryGamePlayDurations(Shared.userData.getCurSwapGameData().getNumTurnsTaken()) +
                                " | elapsed turn time: " + (Shared.userData.getCurSwapGameData().queryGamePlayDurations(Shared.userData.getCurSwapGameData().getNumTurnsTaken())));

                        Log.d(TAG, " ... unSelect both tile bitmaps");
                        mTileViewMap.get(selectedTiles.get(0)).unSelect();
                        mTileViewMap.get(selectedTiles.get(1)).unSelect();

                        selectedTiles.clear();

                        debugCoordsTileViewsMap("method addTile, onClick, post redraw");

                        //  - update the number of turns taken
                        Shared.userData.getCurSwapGameData().incrementNumTurnsTaken();
                        Log.d(TAG, "   ***: numTurnsTaken postIncrement: " + Shared.userData.getCurSwapGameData().getNumTurnsTaken());
                    }

                    //[2.2.2] If this is the second card in any subsequent pair to be selected - highlight second tile
                    // and animate swap (call via event for CoordToCard HashMap updates and perform Coord to TileView image
                    // updates here)
                    else {
                        //FIXME we want to count turns only when pairs are to be swapped?
                        Shared.userData.getCurSwapGameData().appendToTurnDurations(now - Shared.userData.getCurSwapGameData().getGameStartTimestamp() +
                                Shared.userData.getCurSwapGameData().queryGamePlayDurations(Shared.userData.getCurSwapGameData().getNumTurnsTaken() - 1));
                        Shared.userData.getCurSwapGameData().appendToGamePlayDurations(now - Shared.userData.getCurSwapGameData().getGameStartTimestamp());
                        Log.d(TAG, "***** ... SECOND OF PAIR selected: swapTileView@: " + swapTileView);
                        swapTileView.select();
                        Log.d(TAG, " ... post select() - swapTileView.isSelected: " + swapTileView.isSelected());
                        selectedTiles.add(curTileOnBoard);
                        Log.d(TAG, " ... curTileOnBoard added to selectedTiles: selectedTiles.size(): " + selectedTiles.size());
                        Log.d(TAG, "\n ... \n");

                        //call swap event
                        Shared.eventBus.notify(new SwapSelectedCardsEvent(selectedTiles.get(0), selectedTiles.get(1)));

                        //display current game stats
                        Log.d(TAG, " *****: | System time: " + now +
                                " | gameStartTimeStamp: " + Shared.userData.getCurSwapGameData().getGameStartTimestamp() +
                                " | numTurnsTaken: " + Shared.userData.getCurSwapGameData().getNumTurnsTaken() +
                                " | gamePlayDuration @ numTurnsTaken: " + Shared.userData.getCurSwapGameData().queryGamePlayDurations(Shared.userData.getCurSwapGameData().getNumTurnsTaken()) +
                                " | elapsed turn time: " + (Shared.userData.getCurSwapGameData().queryGamePlayDurations(Shared.userData.getCurSwapGameData().getNumTurnsTaken()) -
                                Shared.userData.getCurSwapGameData().queryGamePlayDurations(Shared.userData.getCurSwapGameData().getNumTurnsTaken() - 1)));

                        Log.d(TAG, " ... unSelect both tile bitmaps");
                        mTileViewMap.get(selectedTiles.get(0)).unSelect();
                        mTileViewMap.get(selectedTiles.get(1)).unSelect();

                        selectedTiles.clear();

                        debugCoordsTileViewsMap("method addTile, onClick, post redraw");

                        //  - update the number of turns taken
                        Shared.userData.getCurSwapGameData().incrementNumTurnsTaken();
                        Log.d(TAG, "   ***: numTurnsTaken postIncrement: " + Shared.userData.getCurSwapGameData().getNumTurnsTaken());
                    }
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

    public void setSwapTileMap (SwapTileCoordinates loc, SwapTileView swapTileView) {
        Log.d (TAG, "method setSwapTileMap: loc: " + loc + " < " +
                loc.getSwapCoordRow() + "," + loc.getSwapCoordCol() + " > " +
                " | swapTileView: " + swapTileView);
        mTileViewMap.put(loc, swapTileView);
    }

    public void unSelectAll() {
        Log.d (TAG, "method unSelectAll ... at start");
        for (SwapTileCoordinates id : selectedTiles) {
            //for (int id = 0; id < flippedUp.size(); id++) {
            mTileViewMap.get(id).unSelect();
            Log.d (TAG, "method unSelectAll: current id in list selectedTiles is: " + id);
        }
        selectedTiles.clear();
    }

    public SwapTileView getSwapTileViewFromTileViewMap (SwapTileCoordinates loc) {
        /*
        Log.d (TAG, "method getSwapTileViewFromTileViewMap: loc: " + loc + " < " +
                loc.getSwapCoordRow() + "," + loc.getSwapCoordCol() + " > " +
                " | mTileViewMap.get(loc)" + mTileViewMap.get(loc));
        */
        return mTileViewMap.get(loc);
    }

    public void debugCoordsTileViewsMap(String callingMethod) {
        Log.d (TAG, "\n\n... method debugCoordsTileViewsMap: < Coords, TileViews> : called by: " + callingMethod);
        //Log.d (TAG, "   ...mTileViewMap @: " + mTileViewMap);
        Iterator iterator = mTileViewMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            //System.out.println(pair.getKey() + " maps to " + pair.getValue());
            SwapTileCoordinates coords = (SwapTileCoordinates) pair.getKey();
            SwapTileView tileView = (SwapTileView) pair.getValue();
            Log.d(TAG, "method debugCoordsTileViewsMap: Searching... coords: < " +
                    coords.getSwapCoordRow() + "," + coords.getSwapCoordCol() +
                    " > | Bitmap @ tileView: " +  tileView.getDrawingCache() +
                    " | Map.Entry pair: " + pair);
        }
        Log.d (TAG, "\n ... \n");
    }
}