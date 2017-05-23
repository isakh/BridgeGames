package ws.isak.bridge.common;

import android.graphics.Bitmap;

import ws.isak.bridge.R;

/*
 * Class ComposeSampleData holds the information associated with each Sample in the Library for the
 * Composition game. Each sample needs: an audio file, with a given duration; an image file associated
 * with the audio file.  Samples are identified by a Species string
 *
 * @author isak
 */

public class ComposeSampleData {

    private final String TAG = "ComposeSampleData";

    private String speciesName;
    private String spectroURI;
    private String audioURI;
    private long sampleDuration;
    private Bitmap sampleImageBitmap;


    //[1] Methods to set and get the speciesName associated with the sample.  There are two ways that
    // the name can be set, either by switching an input iterator (id) and matching to a string value
    // stored in strings.xml containing the common and scientific name of the species, or by providing
    // an name string.
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

    //FIXME Overloaded method call for retrieval from database? is this necessary here?
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

    //[2] set/getter methods for spectroURI
    public void setSpectroURI(String sURI) { spectroURI = sURI; }

    public String getSpectroURI () { return spectroURI; }

    //[3] set/getter methods for audioURI
    public void setAudioURI (String aURI) { audioURI = aURI; }

    public String getAudioURI () { return audioURI; }

    //[4] set/getter methods for sampleDuration
    public void setSampleDuration (long sDur) { sampleDuration = sDur; }

    public long getSampleDuration () { return sampleDuration; }

    //[5] set/getter methods for sampleImageBitmap
    public void setCardBitmap (Bitmap bitmap) {
        sampleImageBitmap = bitmap;
    }

    public Bitmap getCardBitmap () { return sampleImageBitmap; }
}