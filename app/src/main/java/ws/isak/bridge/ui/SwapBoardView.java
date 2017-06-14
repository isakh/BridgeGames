package ws.isak.bridge.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;

import android.graphics.drawable.GradientDrawable;
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

import ws.isak.bridge.events.ui.SwapSelectedCardsEvent;

import ws.isak.bridge.events.ui.SwapUnselectCardsEvent;
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
    private int mScreenWidth;           //FIXME - for now this is 85% of the board width hard coded, make dynamic? or set in xml
    private int mScreenHeight;
    //an instance of the board configuration for the current game
    private SwapBoardConfiguration mSwapBoardConfiguration;
    //an instance of the board arrangement for the current game
    private SwapBoardArrangement mSwapBoardArrangement;
    //a mapping of each tile ID to a view TileView, this remains unchanged within the scope of each game
    public HashMap<SwapTileCoordinates, SwapTileView> mTileViewMap;
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
        mScreenWidth = (int) Math.floor((getResources().getDisplayMetrics().widthPixels - padding*2 - ImageScaling.px(20)) * 0.85);    //TODO * proportion of screen for view - make less of a hack
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
        //Log.d (TAG, "method setBoard: calling method debugCoordsTileViewsMap");
        //debugCoordsTileViewsMap("class SwapBoardView: method setBoard after call to buildBoard...");
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
        GradientDrawable border = new GradientDrawable();
        border.setColor(0xFF000000);
        border.setStroke(1, 0xFF000000); //black border with full opacity
        linearLayout.setBackground(border);

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
        //log state of inputs - coords, and parent
        Log.d (TAG, "method addTile: address of curTileOnBoard: " + curTileOnBoard +
                " | curTileOnBoard coords: < " + curTileOnBoard.getSwapCoordRow() + "," +
                curTileOnBoard.getSwapCoordCol() + " >" + " | parent.getVisibility: " + parent.getVisibility() +
                " | parent.isShown: " + parent.isShown() + " | ViewGroup parent: " + parent);
        //create the SwapTileView - this must be final for it to be called from the AsyncTask
        final SwapTileView swapTileView = SwapTileView.fromXml(getContext(), parent);
        swapTileView.setLayoutParams(mTileLayoutParams);
        //add the view to the parent ViewGroup
        parent.addView(swapTileView);
        parent.setClipChildren(false);
        //update the HashMap of <coords, SwapTileViews>
        setSwapTileMap(curTileOnBoard, swapTileView);           //effectively mTileViewMap.put with debugging code

        //asynchronously load the bitmaps of the card images onto their respective tile views
        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... params) {
                Log.d (TAG, "*** method addTile: new AsyncTask: override doInBackground: curTileOnBoard is: " +
                        curTileOnBoard + "| coords are: < " + curTileOnBoard.getSwapCoordRow() +
                        "," + curTileOnBoard.getSwapCoordCol() + " > | mSize is: " + mSize);
                //gets one of four bitmaps depending on flags at current coordinates
                Bitmap croppedBitmap = mSwapBoardArrangement.getSwapTileBitmap(curTileOnBoard, mSize);
                Shared.userData.getCurSwapGameData().getSwapCardDataFromSwapBoardMap(curTileOnBoard).setCardBitmap(croppedBitmap);
                Log.d (TAG, "*** addTile: doInBackground: create bitmap: " +
                        Shared.userData.getCurSwapGameData().getSwapCardDataFromSwapBoardMap(curTileOnBoard).getCardBitmap() +
                        " FOR CARD @: < " + curTileOnBoard.getSwapCoordRow() + "," + curTileOnBoard.getSwapCoordCol() + " > ");
                return croppedBitmap;
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                Log.d (TAG, "*** method addTile: Overriding onPostExecute: setting bitmap 'result'");
                //swapTileView.buildDrawingCache();    //FIXME does this help with later retrieval of bitmap?
                swapTileView.setTileImage(result, "[class SwapBoardView: method addTile: initial setup]");
                Log.d (TAG, "... addTile: onPostExecute: swapTileView.getVisibility: " +
                        swapTileView.getVisibility() + " | swapTileView.isShown: " + swapTileView.isShown());
                swapTileView.setVisibility(VISIBLE);
                Log.d (TAG, "... addTile: onPostExecute: set VISIBLE: swapTileView.getVisibility: " +
                        swapTileView.getVisibility() + " | swapTileView.isShown: " + swapTileView.isShown());
                swapTileView.invalidate();
                //TODO - remove this debugging code when all is functional
                //FIXME - swapTileView.setTileDebugText(mTileViewMap, curTileOnBoard);
                debugCoordsTileViewsMap("class SwapBoardView: method addTile: onPostExecute completion");

                //set the onClickListener for the view - this will respond to various interactions from clicking the tile
                swapTileView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        //keep local track of click time
                        long now = System.currentTimeMillis();

                        //debug at start of onClick
                        Log.d(TAG, "*** tile onClick: tile being clicked is swapTileView @: " + swapTileView +
                                " | swapTileView.isSelected (if true the second click is unselect): " + swapTileView.isSelected() +
                                " | Audio.getIsAudioPlaying: " + Audio.getIsAudioPlaying() +
                                " | selectedTiles.size: " + selectedTiles.size() +
                                " | now: " + now);


                        //[0] if MIX is OFF and audio is playing - Toast and break out - user needs to click again
                        if (!Audio.MIX && Audio.getIsAudioPlaying()) {
                            Toast.makeText(Shared.context, "PLEASE WAIT FOR SOUND TO FINISH PLAYING", Toast.LENGTH_SHORT).show();
                            return;     //TODO check does return here break all the way out?
                        }

                        //[1] if the view for the tile has already been selected, the second click unSelects it
                        // FIXME - In the case where the first tile to be selected is then unselected, do we count this as a game not yet started?
                        if (swapTileView.isSelected()) {
                            Toast.makeText(Shared.context, "SECOND CLICK DE-SELECTS THE TILE", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "***** ... DOUBLE CLICKING: un-select tile: <" +
                                    curTileOnBoard.getSwapCoordRow() + "," + curTileOnBoard.getSwapCoordCol() + ">");
                            //FIXME - try this with event instead - see next line instead - swapTileView.unSelect();
                            Shared.eventBus.notify(new SwapUnselectCardsEvent(selectedTiles));
                            swapTileView.invalidate();
                            Log.d(TAG, " ... unSelecting tile: swapTileView: " + swapTileView);
                            selectedTiles.clear();
                            Log.d(TAG, " ... clearing selectedTiles: selectedTiles @: " + selectedTiles +
                                    " | selectedTiles.size(): " + selectedTiles.size());
                        }

                        //[2] else this is a viable turn
                        else {
                            //Toast.makeText(Shared.context, "Coordinates: < " + curTileOnBoard.getSwapCoordRow() +
                            //        "," + curTileOnBoard.getSwapCoordCol() + " >", Toast.LENGTH_SHORT).show();  //TODO remove toast?

                            //[2.0] if this tile being clicked is the first tile to be clicked on the board we
                            //change the state of SwapGameData.isGameStarted() and set the SwapGameData.setGameStartTimeStamp()
                            Log.d(TAG, "**** Update SwapGameData with current timing information (and card info) ****");
                            if (!Shared.userData.getCurSwapGameData().isGameStarted()) {     //if this is the first card being flipped
                                //debugCoordsTileViewsMap ("class SwapBoardView: method addTile: onClick: [2] start of first viable turn");
                                //Toast.makeText(Shared.context, "FIRST CARD IN GAME", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "This is the First Tile Selected In SwapGame");
                                Shared.userData.getCurSwapGameData().setGameStarted(true);
                                Log.d(TAG, "   ***: getGameStarted: " + Shared.userData.getCurSwapGameData().isGameStarted());
                                Shared.userData.getCurSwapGameData().setGameStartTimestamp(now);
                                Log.d(TAG, "   ***: getGameStartTimestamp: " + Shared.userData.getCurSwapGameData().getGameStartTimestamp());
                                Log.d (TAG, " ..... appending 0 to initialize gamePlayDurations");
                                Shared.userData.getCurSwapGameData().appendToGamePlayDurations(now - Shared.userData.getCurSwapGameData().getGameStartTimestamp());
                                Log.d (TAG, " ..... appending 0 to initialize turnDurations");
                                Shared.userData.getCurSwapGameData().appendToTurnDurations(0);
                                //output log of current SwapGameData State
                                Log.d(TAG, " *****: | System time: " + now +
                                        " | gameStartTimeStamp: " + Shared.userData.getCurSwapGameData().getGameStartTimestamp() +
                                        " | numTurnsTaken: " + Shared.userData.getCurSwapGameData().getNumTurnsTaken() +
                                        " | gamePlayDuration @ numTurnsTaken: " + Shared.userData.getCurSwapGameData().queryGamePlayDurations(Shared.userData.getCurSwapGameData().getNumTurnsTaken()) +
                                        " | elapsed turn time: " + (Shared.userData.getCurSwapGameData().queryGamePlayDurations(Shared.userData.getCurSwapGameData().getNumTurnsTaken())));
                                //  - update the number of turns taken
                                Shared.userData.getCurSwapGameData().incrementNumTurnsTaken();
                                Log.d(TAG, "   ***: numTurnsTaken postIncrement: " + Shared.userData.getCurSwapGameData().getNumTurnsTaken());
                            }

                            //[2.1] If this is the first card in a pair to be selected
                            if (!swapTileView.isSelected() && selectedTiles.size() == 0) {
                                debugCoordsTileViewsMap ("class SwapBoardView: method addTile: onClick: [2.1] start of viable turn");
                                //Toast.makeText(Shared.context, "FIRST CARD IN PAIR", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, " ***** ... FIRST OF PAIR selected: swapTileView: " + swapTileView);
                                swapTileView.select("SwapBoardView: addTile: [2.1] - first card in pair to be selected");
                                Log.d(TAG, " ... post select() - swapTileView.isSelected: " + swapTileView.isSelected());
                                selectedTiles.add(curTileOnBoard);
                                Log.d(TAG, " ... curTileOnBoard added to selectedTiles: selectedTiles.size(): " + selectedTiles.size());
                            }

                            //[2.2] If this is the second card in any subsequent pair to be selected - highlight second tile
                            // and animate swap (call via event for CoordToCard HashMap updates and perform Coord to TileView image
                            // updates here)
                            else {      //FIXME - should this case still be an 'else if' with a final 'else' as catch option?
                                debugCoordsTileViewsMap ("class SwapBoardView: method addTile: onClick: [2.2] second card of viable turn");
                                //Toast.makeText(Shared.context, "SECOND CARD IN PAIR", Toast.LENGTH_SHORT).show();
                                //for now we want to count turns only when pairs are to be swapped
                                Log.d (TAG, " ..... appending [" + Shared.userData.getCurSwapGameData().queryGamePlayDurations(Shared.userData.getCurSwapGameData().getNumTurnsTaken()-1)+
                                        "]to PlayDurations: turn: [" +
                                        Shared.userData.getCurSwapGameData().getNumTurnsTaken() +
                                        "] | now: " + now + " | startTimeStamp: " +
                                        Shared.userData.getCurSwapGameData().getGameStartTimestamp() +
                                        " | now - startTimeStamp: " + (now - Shared.userData.getCurSwapGameData().getGameStartTimestamp()));
                                Shared.userData.getCurSwapGameData().appendToGamePlayDurations(now - Shared.userData.getCurSwapGameData().getGameStartTimestamp());
                                Log.d (TAG, " ..... appending [" + Shared.userData.getCurSwapGameData().queryTurnDurationsArray(Shared.userData.getCurSwapGameData().getNumTurnsTaken()-1)+
                                        "]to TurnDurations: turn: [" +
                                        Shared.userData.getCurSwapGameData().getNumTurnsTaken() +
                                        "] | now: " + now + " | startTimeStamp: " +
                                        Shared.userData.getCurSwapGameData().getGameStartTimestamp() +
                                        " | queryGamePlayDurations[turn - 1]: " +
                                        Shared.userData.getCurSwapGameData().queryGamePlayDurations(Shared.userData.getCurSwapGameData().getNumTurnsTaken() - 1));
                                Shared.userData.getCurSwapGameData().appendToTurnDurations(now - (Shared.userData.getCurSwapGameData().getGameStartTimestamp() +
                                        Shared.userData.getCurSwapGameData().queryGamePlayDurations(Shared.userData.getCurSwapGameData().getNumTurnsTaken() - 1)));
                                Log.d(TAG, "***** ... SECOND OF PAIR selected: swapTileView@: " + swapTileView);
                                swapTileView.select("SwapBoardView: addTile: [2.2] - second card in pair to be selected");
                                swapTileView.postInvalidate();
                                swapTileView.invalidate();      //Fixme - only one of these should be necessary?
                                Log.d(TAG, " ... post select() - swapTileView.isSelected: " + swapTileView.isSelected());
                                selectedTiles.add(curTileOnBoard);
                                Log.d(TAG, " ... curTileOnBoard added to selectedTiles: selectedTiles.size(): " + selectedTiles.size());
                                Log.d(TAG, "\n ... \n");

                                //call swap event - //TODO ∆ wait 1 sec to xml - this should allow us one second to observe the selection before unselect both happens?
                                Shared.eventBus.notify(new SwapSelectedCardsEvent(selectedTiles.get(0), selectedTiles.get(1)), 1000);

                                //display current game stats
                                Log.d(TAG, " *****: | System time: " + now +
                                        " | gameStartTimeStamp: " + Shared.userData.getCurSwapGameData().getGameStartTimestamp() +
                                        " | numTurnsTaken: " + Shared.userData.getCurSwapGameData().getNumTurnsTaken() +
                                        " | gamePlayDuration @ numTurnsTaken: " + Shared.userData.getCurSwapGameData().queryGamePlayDurations(Shared.userData.getCurSwapGameData().getNumTurnsTaken()) +
                                        " | elapsed turn time: " + (Shared.userData.getCurSwapGameData().queryGamePlayDurations(Shared.userData.getCurSwapGameData().getNumTurnsTaken()) -
                                        Shared.userData.getCurSwapGameData().queryGamePlayDurations(Shared.userData.getCurSwapGameData().getNumTurnsTaken() - 1)));

                                //  - update the number of turns taken
                                Shared.userData.getCurSwapGameData().incrementNumTurnsTaken();
                                Log.d(TAG, "   ***: numTurnsTaken postIncrement: " + Shared.userData.getCurSwapGameData().getNumTurnsTaken());
                            }
                        }
                    }
                });
            }
        }.execute();

        //FIXME - what does this do here?
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

    public HashMap <SwapTileCoordinates, SwapTileView> getSwapTileMap () {
        return mTileViewMap;
    }

    //method unSelectAll iterates over the coordinates targeted in the selectedTiles array and unSelects them
    public void unSelectAll() {
        Log.d (TAG, "method unSelectAll ... at start");
        for (SwapTileCoordinates id : selectedTiles) {
            mTileViewMap.get(id).unSelect();
            Log.d (TAG, "method unSelectAll: current id in list selectedTiles is: " + id);
        }
        selectedTiles.clear();
        Log.d (TAG, "method unSelectAll completed: selectedTiles: " + selectedTiles);
    }

    public SwapTileView getSwapTileViewFromCoordsViewMap (SwapTileCoordinates loc) {
        /*
        Log.d (TAG, "method getSwapTileViewFromTileViewMap: loc: " + loc + " < " +
                loc.getSwapCoordRow() + "," + loc.getSwapCoordCol() + " > " +
                " | mTileViewMap.get(loc)" + mTileViewMap.get(loc));
        */
        return mTileViewMap.get(loc);
    }

    //this method is for debugging the state of the game board HashMap
    public void debugCoordsTileViewsMap(String callingMethod) {
        Log.d (TAG, "##################################################################################");
        Log.d (TAG, "method debugCoordsTileViewsMap: < Coords, TileViews> : called by: " + callingMethod);
        Log.d (TAG, "##################################################################################");
        //Log.d (TAG, "   ...mTileViewMap @: " + mTileViewMap);
        Iterator iterator = mTileViewMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            //System.out.println(pair.getKey() + " maps to " + pair.getValue());
            SwapTileCoordinates coords = (SwapTileCoordinates) pair.getKey();
            SwapTileView tileView = (SwapTileView) pair.getValue();
            Log.v(TAG, "method debugCoordsTileViewsMap: Searching... coords: < " +
                    coords.getSwapCoordRow() + "," + coords.getSwapCoordCol() +
                    "> | Bitmap @ Shared.currentSwapGame.swapBoardArrangement.swapBoardMap.get(coords).getCardBitmap(): " +
                    " | Bitmap @ Shared.userData.getCurSwapGameData().getSwapBoardMap().get(coords).getCardBitmap(): " +
                    Shared.userData.getCurSwapGameData().getSwapBoardMap().get(coords).getCardBitmap() +
                    " | Map.Entry pair: " + pair);
        }
        Log.d (TAG, "\n ... \n");
    }
}