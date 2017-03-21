package ws.isak.memgamev.common;

import android.util.Log;


/*
 * Class TileData constructs the object that comprises one tile element in a theme.  This is one
 * or more image files (pairs of images are needed for the birds theme, currently the spectrograms
 * theme will only ask users to match identical spectrograms) and an audio file.
 * TODO an audio only theme will have all cards showing the same (or no) image and only audio will play
 *
 * @author isak
 */

public class CardData
{
    private final String TAG = "Class: CardData";

    private int cardID;                     //Tile ID between 1 and the number of tiles objects in resources

    private boolean pairedImagesDiffer;     //boolean true if there is a second image in the pair
    private boolean firstImageUsed;         //switches to true when the object is accessed a second time

    private String imageURI1;               //path to the first image file if the object requires paired images
    private String imageURI2;               //path to second image file of pair if necessary

    private String audioURI;                //path to audio file for object
    private long sampleDuration;

    /*
     * Method setCardID sets the ID value for a card object to @param id
     */
    public void setCardID (int id) {
        try {
            //Log.d (TAG, "method setCardID: id : " + id);
            cardID = id;
        }
        catch (IllegalArgumentException e) {
            e.getStackTrace();
        }
    }

    /*
     * Method getCardID returns the @param id associated with a card object
     */
    public int getCardID () {
        try {
            //Log.d(TAG, "method: getCardID: return: " + cardID);
            return cardID;
        }
        catch (NullPointerException npe) {
            npe.printStackTrace();
            return -1;
        }
    }

    public void setImageURI1 (String URI1) {
        //Log.d (TAG, "method setImageURI1: imageURI1 : " + URI1);
        imageURI1 = URI1;

    }

    public String getImageURI1 () {
        try {
            //Log.d (TAG, "method: getImageURI1: return: " + imageURI1);
            return imageURI1;
        } catch (NullPointerException npe) {
            npe.printStackTrace();
            return null;
        }
    }

    public void setPairedImageDiffer (boolean pairDifferent) {
        //Log.d (TAG, "method: setPairedImagedDiffer: var pairDifferent is : " + pairDifferent);
        pairedImagesDiffer = pairDifferent;
    }

    public boolean getPairedImageDiffer () {
        try {
            //Log.d (TAG, "method: getPairedImageDiffer: returns: " + pairedImagesDiffer);
            return pairedImagesDiffer;
        } catch (NullPointerException npe) {
            npe.printStackTrace();
            return false;       //TODO does this make sense?
        }
    }

    public void setFirstImageUsed (boolean usedOnce) {
        //Log.d (TAG, "method: setFirstImageUsed: var usedOnce is: " + usedOnce);
        firstImageUsed = usedOnce;
    }

    public boolean getFirstImageUsed () {
        try {
            //Log.d (TAG, "method: getFirstImageUsed: returns: " + firstImageUsed);
            return firstImageUsed;
        }
    catch (NullPointerException npe) {
        npe.printStackTrace();
        return false;           //TODO does this make sense as a return value for an attempt to return from a null pointer?
        }
    }

    public void setImageURI2 (String URI2) {
        //Log.d (TAG, "method: setImageURI2: imageURI2 : " + URI2);
        imageURI2 = URI2;
    }

    public String getImageURI2 () {
        try {
            //Log.d (TAG, "method: getImageURI2: returns:: " + imageURI2);
            return imageURI2;
        }
        catch (NullPointerException npe) {
            npe.printStackTrace();
            return null;
        }
    }

    public void setAudioURI (String URI) {
        //Log.d (TAG, "method: setAudioURI: audioURI : " + URI);
        audioURI = URI;
    }

    public String getAudioURI () {
        try {
            //Log.d (TAG, "method: getAudioURI: returns: " + audioURI);
            return audioURI;
        }
        catch (NullPointerException npe) {
            npe.printStackTrace();
            return null;
        }
    }

    public void setSampleDuration (long dur) {
        //Log.d (TAG, "method: setSampleDuration: sampleDuration: " + dur);
        sampleDuration = dur;
    }

    public long getSampleDuration () {
        try {
            //Log.d (TAG, "method getSampleDuration: returns: " + sampleDuration);
            return sampleDuration;
        }
        catch (NullPointerException npe) {
            npe.printStackTrace();
            return -1;
        }
    }
}