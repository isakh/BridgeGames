package ws.isak.bridge.utils;

import android.util.Log;

/*
 * Class Coordinates defines a place on the Swap Board in terms of <row, column>
 *
 * @author isak
 */

public class SwapTileCoordinates {

    private static final String TAG = "SwapCoordinates";

    private int row;
    private int col;

    public SwapTileCoordinates (int row, int col) {
        Log.d (TAG, "constructor");
        this.row = row;
        this.col = col;
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

    public void switchTileCoordinates (SwapTileCoordinates tile1, SwapTileCoordinates tile2) {
        //create temp coordinates, initialized to off the board
        SwapTileCoordinates temp = new SwapTileCoordinates(-1, -1);
        //copy tile 2 to temp
        temp.setSwapCoordRow (tile2.getSwapCoordRow());
        temp.setSwapCoordCol (tile2.getSwapCoordCol());
        //copy tile 1 to tile 2
        tile2.setSwapCoordRow (tile1.getSwapCoordRow());
        tile2.setSwapCoordCol (tile1.getSwapCoordCol());
        //copy temp to tile 1
        tile1.setSwapCoordRow (temp.getSwapCoordRow());
        tile1.setSwapCoordCol (temp.getSwapCoordCol());
    }
}
