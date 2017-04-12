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

    public void SwapTileCoordinates (int row, int col) {
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
}
