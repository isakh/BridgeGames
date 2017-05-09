package ws.isak.bridge.utils;

import android.util.Log;

/*
 * Class Coordinates defines a place on the Swap Board in terms of <row, column>
 *
 * @author isak
 */

public class SwapTileCoordinates {

    private static final String TAG = "SwapCoordinates";

    private float coordsID;
    private int row;
    private int col;

    public SwapTileCoordinates (int row, int col) {
        //Log.d (TAG, "constructor");
        this.row = row;
        this.col = col;
        setSwapTileCoordsID (row, col);
    }

    public void setSwapTileCoordsID (int row, int col) {
        //TODO - decimal float hack to contain both values - how to resolve if grid over 10x10?
        coordsID = row + (col/10);
    }

    public void setSwapTileCoordsID (float rowColID) {
        //overloaded version that sets the ID from a known float
        Log.d (TAG, "method setSwapTileCoordsID - overloaded version takes a float: " + rowColID);
        coordsID = rowColID;
    }

    public float getSwapTileCoordsID () {
         return coordsID;
    }

    public void setSwapCoordRow (int r) {
        //
        row = r;
    }

    public int getSwapCoordRow () {
        //
        return row;
    }

    public void setSwapCoordCol (int c) {
        //
        col = c;
    }

    public int getSwapCoordCol () {
        //
        return col;
    }
}