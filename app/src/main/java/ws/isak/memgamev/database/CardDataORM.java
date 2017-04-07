package ws.isak.memgamev.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import ws.isak.memgamev.model.Game;
import ws.isak.memgamev.common.CardData;

/*
 *
 * @author isak
 */

public class CardDataORM {

    private static final String TAG="Class: CardDataORM";

    private static final String TABLE_NAME = "user_data";

    private static final String COMMA_SEP = ", ";


    private static final String COLUMN_CARD_ID_TYPE = "INTEGER PRIMARY KEY"; //TODO does this make sense?
    private static final String COLUMN_CARD_ID = "cardID";

    private static final String COLUMN_SPECIES_NAME_TYPE = "STRING";
    private static final String COLUMN_SPECIES_NAME = "speciesName";

    //TODO sqlite cannot deal with booleans, pairedImagesDiffer and firstImageUsed become INTEGERS
    private static final String COLUMN_PAIRED_IMAGES_DIFFER_TYPE = "INTEGER";
    private static final String COLUMN_PAIRED_IMAGES_DIFFER = "pairedImagesDiffer";

    private static final String COLUMN_FIRST_IMAGE_USED_TYPE = "INTEGER";
    private static final String COLUMN_FIRST_IMAGE_USED = "firstImageUsed";

    private static final String COLUMN_IMAGE_URI1_TYPE = "STRING";
    private static final String COLUMN_IMAGE_URI1 = "imageURI1";

    private static final String COLUMN_IMAGE_URI2_TYPE = "STRING";
    private static final String COLUMN_IMAGE_URI2 = "imageURI2";

    private static final String COLUMN_AUDIO_URI_TYPE = "STRING";
    private static final String COLUMN_AUDIO_URI = "audioURI";

    private static final String COLUMN_SAMPLE_DURATION_TYPE = "INTEGER";        //SQLite Integer can handle 8 byte long
    private static final String COLUMN_SAMPLE_DURATION = "sampleDuration";

    public static final String SQL_CREATE_TABLE = "CREATE TABLE " +
            TABLE_NAME +
            " (" +
            COLUMN_CARD_ID + " " + COLUMN_CARD_ID_TYPE +
            COLUMN_SPECIES_NAME + " " + COLUMN_SPECIES_NAME_TYPE +
            COLUMN_PAIRED_IMAGES_DIFFER + " " + COLUMN_PAIRED_IMAGES_DIFFER_TYPE +
            COLUMN_FIRST_IMAGE_USED + " " + COLUMN_FIRST_IMAGE_USED_TYPE +
            COLUMN_IMAGE_URI1 + " " + COLUMN_IMAGE_URI1_TYPE +
            COLUMN_IMAGE_URI2 + " " + COLUMN_IMAGE_URI2_TYPE +
            COLUMN_AUDIO_URI + " " + COLUMN_AUDIO_URI_TYPE +
            COLUMN_SAMPLE_DURATION + " " + COLUMN_SAMPLE_DURATION_TYPE +
            ")";

    // ===========================================================================================

    //method userDataToContentValues packs a UserData object into a ContentValues map for
    //use with SQL inserts
    private static ContentValues cardDataToContentValues (CardData cardData) {
        Log.d (TAG, "private method cardDataToContentValues");
        ContentValues values = new ContentValues();

        values.put (COLUMN_CARD_ID, cardData.getCardID());
        values.put (COLUMN_SPECIES_NAME, cardData.getSpeciesName());

        if (!cardData.getPairedImageDiffer()) { values.put (COLUMN_PAIRED_IMAGES_DIFFER, 0);}       //TODO Check logic
        else{ values.put (COLUMN_PAIRED_IMAGES_DIFFER, 1); }
        if (!cardData.getFirstImageUsed()) { values.put (COLUMN_FIRST_IMAGE_USED, 0); }             //TODO Check logic
        else { values.put (COLUMN_FIRST_IMAGE_USED, 1); }

        values.put (COLUMN_IMAGE_URI1, cardData.getImageURI1());
        values.put (COLUMN_IMAGE_URI2, cardData.getImageURI2());
        values.put (COLUMN_AUDIO_URI, cardData.getAudioURI());
        values.put (COLUMN_SAMPLE_DURATION, cardData.getSampleDuration());
        return values;
    }


    //method cursorToUserData populates a UserData object with data from the cursor
    private static CardData cursorToCardData (Cursor cursor) {
        Log.d (TAG, "method cursorToCardData");
        CardData cursorAtCardData = new CardData();

        cursorAtCardData.setCardID (cursor.getInt(cursor.getColumnIndex(COLUMN_CARD_ID)));
        cursorAtCardData.setSpeciesName(cursor.getString(cursor.getColumnIndex(COLUMN_SPECIES_NAME)));      //FIXME solve string approach to species loading - currently overloading constructor

        if (cursor.getInt(cursor.getColumnIndex(COLUMN_PAIRED_IMAGES_DIFFER)) == 1) { cursorAtCardData.setPairedImageDiffer(true); }
        else if (cursor.getInt(cursor.getColumnIndex(COLUMN_PAIRED_IMAGES_DIFFER)) == 0) { cursorAtCardData.setPairedImageDiffer(false); }
        else {
            Log.d (TAG, "ERROR: method cursorAtCardData: mixerState: " + cursor.getInt(cursor.getColumnIndex(COLUMN_PAIRED_IMAGES_DIFFER)));
        }
        if (cursor.getInt(cursor.getColumnIndex(COLUMN_FIRST_IMAGE_USED)) == 1) { cursorAtCardData.setFirstImageUsed(true); }
        else if (cursor.getInt(cursor.getColumnIndex(COLUMN_FIRST_IMAGE_USED)) == 0) { cursorAtCardData.setFirstImageUsed(false); }
        else {
            Log.d (TAG, "ERROR: method cursorAtCardData: isFirstImageUsed: " + cursor.getInt(cursor.getColumnIndex(COLUMN_FIRST_IMAGE_USED)));
        }

        cursorAtCardData.setImageURI1(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_URI1)));
        cursorAtCardData.setImageURI2(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_URI2)));
        cursorAtCardData.setAudioURI(cursor.getString(cursor.getColumnIndex(COLUMN_AUDIO_URI)));
        cursorAtCardData.setSampleDuration((long) cursor.getInt(cursor.getColumnIndex(COLUMN_SAMPLE_DURATION)));

        return  cursorAtCardData;
    }

}