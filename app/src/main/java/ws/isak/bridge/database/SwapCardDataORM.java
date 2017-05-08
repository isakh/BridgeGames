package ws.isak.bridge.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.widget.Toast;

import ws.isak.bridge.common.SwapCardData;
import ws.isak.bridge.common.Shared;
import ws.isak.bridge.utils.SwapCardID;

/*
 * Swap Card Data 
 * 
 * @author isak
 */

public class SwapCardDataORM {

    private static final String TAG = "SwapCardDataORM";

    private static final String TABLE_NAME = "swap_card_data";

    private static final String COMMA_SEP = ", ";

    private static final String COLUMN_CARD_ID_TYPE = "REAL PRIMARY KEY";    //this float comes from SwapCardID float key
    private static final String COLUMN_CARD_ID = "cardID";

    private static final String COLUMN_SPECIES_NAME_TYPE = "STRING";
    private static final String COLUMN_SPECIES_NAME = "speciesName";

    private static final String COLUMN_SPECTRO_URI0_TYPE = "STRING";
    private static final String COLUMN_SPECTRO_URI0 = "spectroURI0";

    private static final String COLUMN_SPECTRO_URI1_TYPE = "STRING";
    private static final String COLUMN_SPECTRO_URI1 = "spectroURI1";

    private static final String COLUMN_SPECTRO_URI2_TYPE = "STRING";
    private static final String COLUMN_SPECTRO_URI2 = "spectroURI2";

    private static final String COLUMN_SPECTRO_URI3_TYPE = "STRING";
    private static final String COLUMN_SPECTRO_URI3 = "spectroURI3";

    private static final String COLUMN_AUDIO_URI0_TYPE = "STRING";
    private static final String COLUMN_AUDIO_URI0 = "audioURI0";

    private static final String COLUMN_AUDIO_URI1_TYPE = "STRING";
    private static final String COLUMN_AUDIO_URI1 = "audioURI1";

    private static final String COLUMN_AUDIO_URI2_TYPE = "STRING";
    private static final String COLUMN_AUDIO_URI2 = "audioURI2";

    private static final String COLUMN_AUDIO_URI3_TYPE = "STRING";
    private static final String COLUMN_AUDIO_URI3 = "audioURI3";

    private static final String COLUMN_SAMPLE_DURATION0_TYPE = "INTEGER";        //SQLite Integer can handle 8 byte long
    private static final String COLUMN_SAMPLE_DURATION0 = "sampleDuration0";     //so we use INTEGER for all sample durations
                                                                                 //...
    private static final String COLUMN_SAMPLE_DURATION1_TYPE = "INTEGER";
    private static final String COLUMN_SAMPLE_DURATION1 = "sampleDuration1";

    private static final String COLUMN_SAMPLE_DURATION2_TYPE = "INTEGER";
    private static final String COLUMN_SAMPLE_DURATION2 = "sampleDuration2";

    private static final String COLUMN_SAMPLE_DURATION3_TYPE = "INTEGER";
    private static final String COLUMN_SAMPLE_DURATION3 = "sampleDuration3";

    public static final String SQL_CREATE_TABLE = "CREATE TABLE " +
            TABLE_NAME +
            " (" +
            COLUMN_CARD_ID + " " + COLUMN_CARD_ID_TYPE + COMMA_SEP +
            COLUMN_SPECIES_NAME + " " + COLUMN_SPECIES_NAME_TYPE + COMMA_SEP +

            COLUMN_SPECTRO_URI0 + " " + COLUMN_SPECTRO_URI0_TYPE + COMMA_SEP +
            COLUMN_SPECTRO_URI1 + " " + COLUMN_SPECTRO_URI1_TYPE + COMMA_SEP +
            COLUMN_SPECTRO_URI2 + " " + COLUMN_SPECTRO_URI2_TYPE + COMMA_SEP +
            COLUMN_SPECTRO_URI3 + " " + COLUMN_SPECTRO_URI3_TYPE + COMMA_SEP +

            COLUMN_AUDIO_URI0 + " " + COLUMN_AUDIO_URI0_TYPE + COMMA_SEP +
            COLUMN_AUDIO_URI1 + " " + COLUMN_AUDIO_URI1_TYPE + COMMA_SEP +
            COLUMN_AUDIO_URI2 + " " + COLUMN_AUDIO_URI2_TYPE + COMMA_SEP +
            COLUMN_AUDIO_URI3 + " " + COLUMN_AUDIO_URI3_TYPE + COMMA_SEP +

            COLUMN_SAMPLE_DURATION0 + " " + COLUMN_SAMPLE_DURATION0_TYPE + COMMA_SEP +
            COLUMN_SAMPLE_DURATION1 + " " + COLUMN_SAMPLE_DURATION1_TYPE + COMMA_SEP +
            COLUMN_SAMPLE_DURATION2 + " " + COLUMN_SAMPLE_DURATION2_TYPE + COMMA_SEP +
            COLUMN_SAMPLE_DURATION3 + " " + COLUMN_SAMPLE_DURATION3_TYPE +
            ")";

    public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    // ===========================================================================================

    public static boolean swapCardDataRecordsInDatabase(Context context) {
        //Log.d (TAG, "method matchCardDataRecordsInDatabase");

        DatabaseWrapper databaseWrapper = Shared.databaseWrapper;
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        boolean recordsExist = false;

        if (database != null) {
            Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME, null);
            Log.d(TAG, "method swapCardDataRecordsInDatabase: Checked " + cursor.getCount() + " SwapCardData records...");

            if (cursor.getCount() > 0) {
                recordsExist = true;
            }
            cursor.close();
        }
        database.close();
        return recordsExist;
    }

    //TODO these methods can be made generic if the TypeOfDataORM.TABLE_NAME is passed in
    public static int numSwapCardDataRecordsInDatabase(Context context) {
        //Log.d (TAG, "method numSwapCardDataRecordsInDatabase");

        DatabaseWrapper databaseWrapper = Shared.databaseWrapper;
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        int numRecords = 0;

        if (database != null) {
            Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME, null);
            Log.d(TAG, "method numSwapCardDataRecordsInDatabase: There are: " + cursor.getCount() + " SwapCardData records...");

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    numRecords++;
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
        database.close();
        return numRecords;
    }

    //method isCardDataInDB takes a MatchCardData object and checks whether it has been used as a
    //primary key yet in the database - used to check existence and uniqueness when loading cards.
    public static boolean isSwapCardDataInDB(SwapCardData cardToCheck) {
        Log.d(TAG, "method isSwapCardDataInDB: check cardToCheck: " + cardToCheck);
        DatabaseWrapper databaseWrapper = Shared.databaseWrapper;
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        boolean cardExists = false;     //false unless found in database
        SwapCardID cardID = cardToCheck.getCardID();
        if (database != null) {
            Log.d(TAG, "method isCardDataInDB: searching...");
            //FIXME !!! - this isn't an integer at the moment - ∆ to a swapCardID object? 
            Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_CARD_ID + " ='" + cardID + "'", null);
            //FIXME - this prevent's SQL injection: Cursor cursor = database.rawQuery("SELECT * FROM " + UserDataORM.TABLE_NAME + " WHERE " + UserDataORM.COLUMN_USER_NAME_ID + " =?", userName);
            if (cursor.getCount() > 0) {
                cardExists = true;
            } else {
                Toast.makeText(Shared.context, "new card added to database", Toast.LENGTH_SHORT).show();
            }
            cursor.close();
            database.close();
        }
        return cardExists;
    }


    public static SwapCardData getSwapCardData(SwapCardID cardID) {
        Log.d(TAG, "method getMatchGameData returns a list of matchCardData objects with cardID " + cardID);

        DatabaseWrapper databaseWrapper = Shared.databaseWrapper;
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        SwapCardData swapCardDataToReturn = null;

        if (database != null) {
            Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_CARD_ID + " ='" + cardID + "'", null);
            //FIXME - this prevent's SQL injection: Cursor cursor = database.rawQuery("SELECT * FROM " + UserDataORM.TABLE_NAME + " WHERE " + UserDataORM.COLUMN_USER_NAME_ID + " =?", userName);
            Log.d(TAG, "method getSwapCardData: Loaded " + cursor.getCount() + " SwapCardData records... this should always only be 1");
            if (swapCardDataRecordsInDatabase(Shared.context)) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    SwapCardData swapCardDataAtCursor = cursorToSwapCardData(cursor);
                    //Check the state of all MatchCardData fields here
                    Log.d(TAG, "... PARSE: MatchCardData object to return:" +
                            " | cardID: " + swapCardDataAtCursor.getCardID() +
                            " | speciesName: " + swapCardDataAtCursor.getSpeciesName() +
                            " | imageURI0: " + swapCardDataAtCursor.getSpectroURI0() +
                            " | imageURI1: " + swapCardDataAtCursor.getSpectroURI1() +
                            " | imageURI2: " + swapCardDataAtCursor.getSpectroURI2() +
                            " | imageURI3: " + swapCardDataAtCursor.getSpectroURI3() +
                            " | audioURI0: " + swapCardDataAtCursor.getAudioURI0() +
                            " | audioURI1: " + swapCardDataAtCursor.getAudioURI1() +
                            " | audioURI2: " + swapCardDataAtCursor.getAudioURI2() +
                            " | audioURI3: " + swapCardDataAtCursor.getAudioURI3() +
                            " | sampleDuration0: " + swapCardDataAtCursor.getSampleDuration0() +
                            " | sampleDuration1: " + swapCardDataAtCursor.getSampleDuration1() +
                            " | sampleDuration2: " + swapCardDataAtCursor.getSampleDuration2() +
                            " | sampleDuration3: " + swapCardDataAtCursor.getSampleDuration3());
                    swapCardDataToReturn = swapCardDataAtCursor;
                    cursor.moveToNext();
                }
                cursor.close();
                database.close();
            }
        }
        return swapCardDataToReturn;
    }


    public static boolean insertSwapCardData(SwapCardData swapCardData) {
        Log.d(TAG, "method insertSwapCardData tries to insert a new SwapCardData object into the database");

        boolean success = false;

        Log.d(TAG, "method insertSwapCardData: creating ContentValues");
        ContentValues values = swapCardDataToContentValues(swapCardData);

        DatabaseWrapper databaseWrapper = Shared.databaseWrapper;
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        try {
            if (database != null) {
                long rowID = database.insert(TABLE_NAME, "null", values);
                Log.d(TAG, "method insertSwapCardData: Inserted new SwapCardData into rowID: " + rowID);
                success = true;
            }
        } catch (SQLiteException sqlex) {
            Log.e(TAG, "method insertSwapCardData: Failed to insert SwapCardData[" + swapCardData.getCardID() + "] due to: " + sqlex);
            sqlex.printStackTrace();
        } finally {
            if (database != null) {
                database.close();
            }
        }
        return success;
    }

    //method cardDataToContentValues packs a UserData object into a ContentValues map for
    //use with SQL inserts
    private static ContentValues swapCardDataToContentValues(SwapCardData swapCardData) {
        Log.d(TAG, "private method cardDataToContentValues");
        ContentValues values = new ContentValues();

        values.put(COLUMN_CARD_ID, swapCardData.getCardIDKey());      //FIXME - column needs a string not an ID object
        values.put(COLUMN_SPECIES_NAME, swapCardData.getSpeciesName());

        values.put(COLUMN_SPECTRO_URI0, swapCardData.getSpectroURI0());
        values.put(COLUMN_SPECTRO_URI1, swapCardData.getSpectroURI1());
        values.put(COLUMN_SPECTRO_URI2, swapCardData.getSpectroURI2());
        values.put(COLUMN_SPECTRO_URI3, swapCardData.getSpectroURI3());
        values.put(COLUMN_AUDIO_URI0, swapCardData.getAudioURI0());
        values.put(COLUMN_AUDIO_URI1, swapCardData.getAudioURI1());
        values.put(COLUMN_AUDIO_URI2, swapCardData.getAudioURI2());
        values.put(COLUMN_AUDIO_URI3, swapCardData.getAudioURI3());
        values.put(COLUMN_SAMPLE_DURATION0, swapCardData.getSampleDuration0());
        values.put(COLUMN_SAMPLE_DURATION1, swapCardData.getSampleDuration1());
        values.put(COLUMN_SAMPLE_DURATION2, swapCardData.getSampleDuration2());
        values.put(COLUMN_SAMPLE_DURATION3, swapCardData.getSampleDuration3());
        return values;
    }


    //method cursorToUserData populates a UserData object with data from the cursor
    private static SwapCardData cursorToSwapCardData(Cursor cursor) {
        Log.d(TAG, "method cursorToSwapCardData");
        SwapCardData cursorAtSwapCardData = new SwapCardData();

        cursorAtSwapCardData.setCardIDKey(cursor.getInt(cursor.getColumnIndex(COLUMN_CARD_ID)));      //FIXME!!!!! - column needs a string?
        cursorAtSwapCardData.setSpeciesName(cursor.getString(cursor.getColumnIndex(COLUMN_SPECIES_NAME)));      //FIXME solve string approach to species loading - currently overloading constructor

        cursorAtSwapCardData.setSpectroURI0(cursor.getString(cursor.getColumnIndex(COLUMN_SPECTRO_URI0)));
        cursorAtSwapCardData.setSpectroURI1(cursor.getString(cursor.getColumnIndex(COLUMN_SPECTRO_URI1)));
        cursorAtSwapCardData.setSpectroURI2(cursor.getString(cursor.getColumnIndex(COLUMN_SPECTRO_URI2)));
        cursorAtSwapCardData.setSpectroURI3(cursor.getString(cursor.getColumnIndex(COLUMN_SPECTRO_URI3)));
        cursorAtSwapCardData.setAudioURI0(cursor.getString(cursor.getColumnIndex(COLUMN_AUDIO_URI0)));
        cursorAtSwapCardData.setAudioURI1(cursor.getString(cursor.getColumnIndex(COLUMN_AUDIO_URI1)));
        cursorAtSwapCardData.setAudioURI2(cursor.getString(cursor.getColumnIndex(COLUMN_AUDIO_URI2)));
        cursorAtSwapCardData.setAudioURI3(cursor.getString(cursor.getColumnIndex(COLUMN_AUDIO_URI3)));
        cursorAtSwapCardData.setSampleDuration0((long) cursor.getInt(cursor.getColumnIndex(COLUMN_SAMPLE_DURATION0)));
        cursorAtSwapCardData.setSampleDuration1((long) cursor.getInt(cursor.getColumnIndex(COLUMN_SAMPLE_DURATION1)));
        cursorAtSwapCardData.setSampleDuration2((long) cursor.getInt(cursor.getColumnIndex(COLUMN_SAMPLE_DURATION2)));
        cursorAtSwapCardData.setSampleDuration3((long) cursor.getInt(cursor.getColumnIndex(COLUMN_SAMPLE_DURATION3)));

        return cursorAtSwapCardData;
    }
}

