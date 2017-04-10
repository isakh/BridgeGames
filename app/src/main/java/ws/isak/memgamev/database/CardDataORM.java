package ws.isak.memgamev.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.util.ArrayList;

import ws.isak.memgamev.common.Shared;
import ws.isak.memgamev.common.CardData;

/*
 *
 * @author isak
 */

public class CardDataORM {

    private static final String TAG="CardDataORM";

    private static final String TABLE_NAME = "card_data";

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

    private static final String COLUMN_IMAGE_URI0_TYPE = "STRING";
    private static final String COLUMN_IMAGE_URI0 = "imageURI0";

    private static final String COLUMN_IMAGE_URI1_TYPE = "STRING";
    private static final String COLUMN_IMAGE_URI1 = "imageURI1";

    private static final String COLUMN_IMAGE_URI2_TYPE = "STRING";
    private static final String COLUMN_IMAGE_URI2 = "imageURI2";

    private static final String COLUMN_IMAGE_URI3_TYPE = "STRING";
    private static final String COLUMN_IMAGE_URI3 = "imageURI3";

    private static final String COLUMN_AUDIO_URI_TYPE = "STRING";
    private static final String COLUMN_AUDIO_URI = "audioURI";

    private static final String COLUMN_SAMPLE_DURATION_TYPE = "INTEGER";        //SQLite Integer can handle 8 byte long
    private static final String COLUMN_SAMPLE_DURATION = "sampleDuration";

    public static final String SQL_CREATE_TABLE = "CREATE TABLE " +
            TABLE_NAME +
            " (" +
            COLUMN_CARD_ID + " " + COLUMN_CARD_ID_TYPE + COMMA_SEP +
            COLUMN_SPECIES_NAME + " " + COLUMN_SPECIES_NAME_TYPE + COMMA_SEP +
            COLUMN_PAIRED_IMAGES_DIFFER + " " + COLUMN_PAIRED_IMAGES_DIFFER_TYPE + COMMA_SEP +
            COLUMN_FIRST_IMAGE_USED + " " + COLUMN_FIRST_IMAGE_USED_TYPE + COMMA_SEP +
            COLUMN_IMAGE_URI0 + " " + COLUMN_IMAGE_URI0_TYPE + COMMA_SEP +
            COLUMN_IMAGE_URI1 + " " + COLUMN_IMAGE_URI1_TYPE + COMMA_SEP +
            COLUMN_IMAGE_URI2 + " " + COLUMN_IMAGE_URI2_TYPE + COMMA_SEP +
            COLUMN_IMAGE_URI3 + " " + COLUMN_IMAGE_URI3_TYPE + COMMA_SEP +
            COLUMN_AUDIO_URI + " " + COLUMN_AUDIO_URI_TYPE + COMMA_SEP +
            COLUMN_SAMPLE_DURATION + " " + COLUMN_SAMPLE_DURATION_TYPE +
            ")";

    public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    // ===========================================================================================

    public static boolean cardDataRecordsInDatabase (Context context) {
        //Log.d (TAG, "method cardDataRecordsInDatabase");

        DatabaseWrapper databaseWrapper = Shared.databaseWrapper;
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        boolean recordsExist = false;

        if (database != null) {
            Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME, null);
            Log.d(TAG, "method cardDataRecordsInDatabase: Checked " + cursor.getCount() + " CardData records...");

            if (cursor.getCount() > 0) {
                recordsExist = true;
            }
            cursor.close();
        }
        database.close();
        return recordsExist;
    }

    //FIXME these methods can be made generic if the TypeOfDataORM.TABLE_NAME is passed in
    public static int numCardDataRecordsInDatabase (Context context) {
        //Log.d (TAG, "method numCardDataRecordsInDatabase");

        DatabaseWrapper databaseWrapper = Shared.databaseWrapper;
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        int numRecords = 0;

        if (database != null) {
            Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME, null);
            Log.d(TAG, "method numCardDataRecordsInDatabase: There are: " + cursor.getCount() + " CardData records...");

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


    public static CardData getCardData (int cardID) {
        Log.d (TAG, "method getMemGameData returns a list of cardData objects with cardID " + cardID);

        DatabaseWrapper databaseWrapper =  Shared.databaseWrapper;
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        CardData cardDataToReturn = null;

        if (database !=  null) {
            Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_CARD_ID + " ='" + cardID + "'", null);
            //FIXME - this prevent's SQL injection: Cursor cursor = database.rawQuery("SELECT * FROM " + UserDataORM.TABLE_NAME + " WHERE " + UserDataORM.COLUMN_USER_NAME_ID + " =?", userName);
            Log.d (TAG, "method getCardData: Loaded " + cursor.getCount() + " CardData records... this should always only be 1");
            if (cardDataRecordsInDatabase(Shared.context)) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    CardData cardDataAtCursor = cursorToCardData(cursor);
                    //Check the state of all CardData fields here
                    Log.d (TAG, "... PARSE: CardData object to return:" +
                            " | cardID: " + cardDataAtCursor.getCardID() +
                            " | speciesName: " + cardDataAtCursor.getSpeciesName() +
                            " | pairedImagesDiffer: " + cardDataAtCursor.getPairedImageDiffer() +
                            " | firstImageUsed: " + cardDataAtCursor.getFirstImageUsed() +
                            " | imageURI0: " + cardDataAtCursor.getImageURI0() +
                            " | imageURI1: " + cardDataAtCursor.getImageURI1() +
                            " | imageURI2: " + cardDataAtCursor.getImageURI2() +
                            " | imageURI3: " + cardDataAtCursor.getImageURI3() +
                            " | audioURI: " + cardDataAtCursor.getAudioURI() +
                            " | sampleDuration: "+ cardDataAtCursor.getSampleDuration());
                    cardDataToReturn = cardDataAtCursor;
                    cursor.moveToNext();
                }
                cursor.close();
                database.close();
            }
        }
        return  cardDataToReturn;
    }


    public static boolean insertCardData (CardData cardData){
        Log.d (TAG, "method insertCardData tries to insert a new CardData object into the database");

        boolean success = false;

        Log.d(TAG, "method insertCardData: creating ContentValues");
        ContentValues values = cardDataToContentValues(cardData);

        DatabaseWrapper databaseWrapper = Shared.databaseWrapper;
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        try {
            if (database != null) {
                long rowID = database.insert(TABLE_NAME, "null", values);
                Log.d(TAG, "method insertCardData: Inserted new CardData into rowID: " + rowID);
                success = true;
            }
        } catch (SQLiteException sqlex) {
            Log.e(TAG, "method insertCardData: Failed to insert CardData[" + cardData.getCardID() + "] due to: " + sqlex);
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
    private static ContentValues cardDataToContentValues (CardData cardData) {
        Log.d (TAG, "private method cardDataToContentValues");
        ContentValues values = new ContentValues();

        values.put (COLUMN_CARD_ID, cardData.getCardID());
        values.put (COLUMN_SPECIES_NAME, cardData.getSpeciesName());

        if (!cardData.getPairedImageDiffer()) { values.put (COLUMN_PAIRED_IMAGES_DIFFER, 0);}       //TODO Check logic
        else{ values.put (COLUMN_PAIRED_IMAGES_DIFFER, 1); }
        if (!cardData.getFirstImageUsed()) { values.put (COLUMN_FIRST_IMAGE_USED, 0); }             //TODO Check logic
        else { values.put (COLUMN_FIRST_IMAGE_USED, 1); }

        values.put (COLUMN_IMAGE_URI0, cardData.getImageURI0());
        values.put (COLUMN_IMAGE_URI1, cardData.getImageURI1());
        values.put (COLUMN_IMAGE_URI2, cardData.getImageURI2());
        values.put (COLUMN_IMAGE_URI3, cardData.getImageURI3());
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

        cursorAtCardData.setImageURI0(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_URI0)));
        cursorAtCardData.setImageURI1(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_URI1)));
        cursorAtCardData.setImageURI2(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_URI2)));
        cursorAtCardData.setImageURI3(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_URI3)));
        cursorAtCardData.setAudioURI(cursor.getString(cursor.getColumnIndex(COLUMN_AUDIO_URI)));
        cursorAtCardData.setSampleDuration((long) cursor.getInt(cursor.getColumnIndex(COLUMN_SAMPLE_DURATION)));

        return  cursorAtCardData;
    }

}