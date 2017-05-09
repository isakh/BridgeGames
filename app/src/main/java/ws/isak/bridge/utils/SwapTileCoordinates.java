package ws.isak.bridge.utils;

import android.util.Log;

/*
 * Class Coordinates defines a place on the Swap Board in terms of <row, column>
 *
 * @author isak
 */

public class SwapTileCoordinates {

    private static final String TAG = "SwapCoordinates";

    private double coordsID;
    private int row;
    private int col;

    public SwapTileCoordinates (int row, int col) {
        //Log.d (TAG, "constructor");
        this.row = row;
        this.col = col;
        setSwapTileCoordsID (row, col);
    }

    public void setSwapTileCoordsID (int row, int col) {
        //TODO - decimal double hack to contain both values - how to resolve if grid over 10x10?
        coordsID = row + ((double) col/10);
    }

    public void setSwapTileCoordsID (double rowColID) {
        //overloaded version that sets the ID from a known double
        Log.d (TAG, "method setSwapTileCoordsID - overloaded version takes a double: " + rowColID);
        coordsID = rowColID;
    }

    public double getSwapTileCoordsID () {
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