package ws.isak.bridge.utils;

import android.util.Log;

/*
 * Class Coordinates defines a place on the Swap Board in terms of <row, column>
 *
 * @author isak
 */

public class SwapCardID {

    private static final String TAG = "SwapCardID";

    private double cardIDKey;
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
        cardIDKey = species + ((double) segment/10);
    }

    //overloaded constructor takes a decimalized form of the id and uses that.
    public void setCardIDKey(double specSegDec) {
        cardIDKey = specSegDec;
    }

    public double getCardIDKey() {
        return cardIDKey;
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
