package ws.isak.memgamev.database;

import android.content.Context;
import android.util.Log;

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

}
