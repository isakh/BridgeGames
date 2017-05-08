package ws.isak.bridge.utils;

import android.util.Log;

/*
 * Class Coordinates defines a place on the Swap Board in terms of <row, column>
 *
 * @author isak
 */

public class SwapCardID {

    private static final String TAG = "SwapCardID";

    private float cardID;
    private int speciesID;
    private int segmentID;

    public SwapCardID (int speciesID, int segmentID) {
        Log.d (TAG, "constructor");
        this.speciesID = speciesID;
        this.segmentID = segmentID;
        setCardID (speciesID, segmentID);
        setSwapCardSpeciesID(speciesID);
        setSwapCardSegmentID(segmentID);
    }

    //TODO - can we make this private and use the public setter when retrieving from database?
    public void setCardID (int species, int segment) {
        //this creates a decimal of species.segment - FIXME - how would we deal with more than 10 segments?
        cardID = species + (segment/10);
    }

    //overloaded constructor takes a decimalized form of the id and uses that.
    public void setCardID (float specSegDec) {
        cardID = specSegDec;
    }

    public float getCardID () {
        return cardID;
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
