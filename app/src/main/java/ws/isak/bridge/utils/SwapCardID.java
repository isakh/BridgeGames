package ws.isak.bridge.utils;

import android.util.Log;

/*
 * Class Coordinates defines a place on the Swap Board in terms of <row, column>
 *
 * @author isak
 */

public class SwapCardID {

    private static final String TAG = "SwapCardID";

    private int speciesID;
    private int segmentID;

    public SwapCardID (int speciesID, int segmentID) {
        Log.d (TAG, "constructor");
        this.speciesID = speciesID;
        this.segmentID = segmentID;
    }


    public void setSwapCardSpeciesID (int id) {
        //
        speciesID = id;
    }

    public int getSwapCardSpeciesID () {
        //
        return speciesID;
    }

    public void setSwapCardSegmentID (int id) {
        //
        segmentID = segmentID;
    }

    public int getSwapCardSegmentID () {
        //
        return segmentID;
    }
}
