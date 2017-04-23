package ws.isak.bridge.common;

import ws.isak.bridge.R;
import ws.isak.bridge.utils.SwapCardID;
import ws.isak.bridge.utils.SwapTileCoordinates;


/*
 * Class SwapCardData constructs the object that comprises one tile element in a swap game.  Each
 * card has an ID, a species name, and a set of four images and audio files that will be placed
 * at random on the board to start.
 *
 * @author isak
 */

public class SwapCardData
{
    private final String TAG = "SwapCardData";

    private SwapCardID cardID;                  //cardID is a two-tuple built from <speciesID, segmentActiveID>
    private String speciesName;                 //this string stores the species name from R.strings.species_bird_% FIXME make dynamic?

    private String spectroURI0;
    private String spectroURI1;
    private String spectroURI2;
    private String spectroURI3;

    private String audioURI0;
    private String audioURI1;
    private String audioURI2;
    private String audioURI3;

    private long sampleDuration0;
    private long sampleDuration1;
    private long sampleDuration2;
    private long sampleDuration3;


    // [0]
    // Methods to set and get the cardID
    public void setCardID (SwapCardID id) {
        try {
            //Log.d (TAG, "method setCardID: id : " + id);
            cardID = id;
        }
        catch (IllegalArgumentException e) {
            e.getStackTrace();
        }
    }

    public SwapCardID getCardID () {
        try {
            //Log.d(TAG, "method: getCardID: return: " + cardID);
            return cardID;
        }
        catch (NullPointerException npe) {
            npe.printStackTrace();
            return null;
        }
    }

    public SwapCardData getCardFromIDAndSegment (int id, int seg) {
        SwapCardData toReturn = null;
        for (int i = 0; i < 10 * 4; i++) {      //
            if (Shared.swapCardDataList.get(i).getCardID().getSwapCardSpeciesID() == id && Shared.swapCardDataList.get(i).getCardID().getSwapCardSegmentID() == seg) {
                toReturn = Shared.swapCardDataList.get(i);
            }
        }
        return toReturn;
    }

    // [1]
    // methods to set and get the species name
    public void setSpeciesName (int id) {
        try {
            //Log.d (TAG, "method setSpeciesName: species: " + species);
            switch (id) {
                case 1:
                    speciesName = Shared.context.getResources().getString(R.string.species_bird_1);
                    break;
                case 2:
                    speciesName = Shared.context.getResources().getString(R.string.species_bird_2);
                    break;
                case 3:
                    speciesName = Shared.context.getResources().getString(R.string.species_bird_3);
                    break;
                case 4:
                    speciesName = Shared.context.getResources().getString(R.string.species_bird_4);
                    break;
                case 5:
                    speciesName = Shared.context.getResources().getString(R.string.species_bird_5);
                    break;
                case 6:
                    speciesName = Shared.context.getResources().getString(R.string.species_bird_6);
                    break;
                case 7:
                    speciesName = Shared.context.getResources().getString(R.string.species_bird_7);
                    break;
                case 8:
                    speciesName = Shared.context.getResources().getString(R.string.species_bird_8);
                    break;
                case 9:
                    speciesName = Shared.context.getResources().getString(R.string.species_bird_9);
                    break;
                case 10:
                    speciesName = Shared.context.getResources().getString(R.string.species_bird_10);
                    break;
            }
        }
        catch (IllegalArgumentException e) {
            e.getStackTrace();
        }
    }

    //FIXME Overloaded method call for retrieval from database?
    public void setSpeciesName (String name) {
        speciesName = name;
    }

    public String getSpeciesName () {
        try {
            //Log.d (TAG, "method getSpeciesName: return: species: " + speciesName);
            return speciesName;
        }
        catch (NullPointerException npe) {
            npe.printStackTrace();
            return null;
        }
    }

    /* FIXME REMOVE - card doesn't need to know where it is, this is held in the Map in SwapGameData
    // [2]
    // methods to set and get the cardOnTileID which keeps track of which tile the card is currently on
    public void setCardOnTileID (SwapTileCoordinates tileID) { cardOnTileID = tileID; }

    public SwapTileCoordinates getCardOnTileID () { return cardOnTileID; }
    FIXME end remove */

    // [3]
    // methods to get and set the URI for the 0th segment
    public void setspectroURI0 (String sURI0) { spectroURI0 = sURI0; }

    public String getSpectroURI0 () { return spectroURI0; }

    // [4]
    // methods to get and set the URI for the 1st segment
    public void setspectroURI1 (String sURI1) { spectroURI1 = sURI1; }

    public String getSpectroURI1 () { return spectroURI1; }

    // [5]
    // methods to get and set the URI for the 2nd  segment
    public void setspectroURI2 (String sURI2) { spectroURI2 = sURI2; }

    public String getSpectroURI2 () { return spectroURI2; }

    // [6]
    // methods to get and set the URI for the 3rd segment
    public void setspectroURI3 (String sURI3) { spectroURI3 = sURI3; }

    public String getSpectroURI3 () { return spectroURI3; }

    // [7]
    // methods to get and set the URI for the 0th audio file
    public void setAudioURI0 (String aURI0) { audioURI0 = aURI0; }

    public String getAudioURI0 () { return audioURI0; }

    // [8]
    // methods to get and set the URI for the 1st audio file
    public void setAudioURI1 (String aURI1) { audioURI1 = aURI1; }

    public String getAudioURI1 () { return audioURI1; }

    // [9]
    // methods to get and set the URI for the 2nd audio file
    public void setAudioURI2 (String aURI2) { audioURI2 = aURI2; }

    public String getAudioURI2 () { return audioURI2; }

    // [10]
    // methods to get and set the URI for the 3rd audio file
    public void setAudioURI3 (String aURI3) { audioURI3 = aURI3; }

    public String getAudioURI3 () { return audioURI3; }

    // [11]
    // methods to get and set the sample duration length for the 0th audio file
    public void setSampleDuration0 (long sDur0) { sampleDuration0 = sDur0; }

    public long getSampleDuration0 () { return sampleDuration0; }

    // [12]
    // methods to get and set the sample duration length for the 1st audio file
    public void setSampleDuration1 (long sDur1) { sampleDuration1 = sDur1; }

    public long getSampleDuration1 () { return sampleDuration1; }

    // [13]
    // methods to get and set the sample duration length for the 2nd audio file
    public void setSampleDuration2 (long sDur2) { sampleDuration2 = sDur2; }

    public long getSampleDuration2 () { return sampleDuration2; }

    // [14]
    // methods to get and set the sample duration length for the 3rd audio file
    public void setSampleDuration3 (long sDur3) { sampleDuration3 = sDur3; }

    public long getSampleDuration3 () { return sampleDuration3; }
}