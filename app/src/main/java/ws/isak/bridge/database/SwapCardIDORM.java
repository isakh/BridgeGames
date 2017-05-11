package ws.isak.bridge.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.widget.Toast;

import ws.isak.bridge.common.Shared;
import ws.isak.bridge.utils.SwapCardID;

/*
 *
 *
 * @author isak
 */

public class SwapCardIDORM {

    private static final String TAG = "SwapCardIDORM";

    private static final String TABLE_NAME = "swap_card_id";

    private static final String COMMA_SEP = ", ";

    private static final String COLUMN_SWAP_CARD_ID_KEY_TYPE = "REAL PRIMARY KEY";
    private static final String COLUMN_SWAP_CARD_ID_KEY = "swapCardIDKey";

    private static final String COLUMN_SPECIES_ID_TYPE = "INTEGER";
    private static final String COLUMN_SPECIES_ID = "speciesID";

    private static final String COLUMN_SEGMENT_ID_TYPE = "INTEGER";
    private static final String COLUMN_SEGMENT_ID = "segmentID";

    public static final String SQL_CREATE_TABLE = "CREATE TABLE " +
            TABLE_NAME +
            " (" +
            COLUMN_SWAP_CARD_ID_KEY + " " + COLUMN_SWAP_CARD_ID_KEY_TYPE + COMMA_SEP +
            COLUMN_SPECIES_ID + " " + COLUMN_SPECIES_ID_TYPE + COMMA_SEP +
            COLUMN_SEGMENT_ID + " " + COLUMN_SEGMENT_ID_TYPE +
            ")";

    public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;


// ===========================================================================================

    public static boolean swapCardIDRecordsInDatabase(Context context) {
        //Log.d (TAG, "method swapCardIDRecordsInDatabase");

        DatabaseWrapper databaseWrapper = Shared.databaseWrapper;
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        boolean recordsExist = false;

        if (database != null) {
            Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME, null);
            Log.d(TAG, "method swapCardIDRecordsInDatabase: Checked " + cursor.getCount() + " SwapCardID records...");

            if (cursor.getCount() > 0) {
                recordsExist = true;
            }
            cursor.close();
        }
        database.close();
        return recordsExist;
    }

    //TODO these methods can be made generic if the TypeOfDataORM.TABLE_NAME is passed in
    public static int numSwapCardIDRecordsInDatabase(Context context) {
        //Log.d (TAG, "method numSwapCardIDRecordsInDatabase");

        DatabaseWrapper databaseWrapper = Shared.databaseWrapper;
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        int numRecords = 0;

        if (database != null) {
            Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME, null);
            Log.d(TAG, "method numSwapCardIDRecordsInDatabase: There are: " + cursor.getCount() + " SwapCardID records...");

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

    //method isSwapCardIDInDB takes a SwapCardID object and checks whether it has been used as a
    //primary key yet in the database - used to check existence and uniqueness when loading cards.
    public static boolean isSwapCardIDInDB(SwapCardID cardIDToCheck) {
        Log.d(TAG, "method isSwapCardIDInDB: check cardID: " + cardIDToCheck);
        DatabaseWrapper databaseWrapper = Shared.databaseWrapper;
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        boolean cardExists = false;     //false unless found in database
        Double cardID = cardIDToCheck.getCardIDKey();
        if (database != null) {
            Log.d(TAG, "method isSwapCardIDInDB: searching...");
            //FIXME !!! - this isn't an integer at the moment - ∆ to a swapCardID object?
            Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_SWAP_CARD_ID_KEY + " ='" + cardID + "'", null);
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

    public static SwapCardID getSwapCardID (float cardID) {
        Log.d(TAG, "method getSwapCardID returns a list of swapCardID objects with cardID " + cardID);

        DatabaseWrapper databaseWrapper = Shared.databaseWrapper;
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        SwapCardID swapCardIDToReturn = null;

        if (database != null) {
            Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_SWAP_CARD_ID_KEY + " ='" + cardID + "'", null);
            //FIXME - this prevent's SQL injection: Cursor cursor = database.rawQuery("SELECT * FROM " + UserDataORM.TABLE_NAME + " WHERE " + UserDataORM.COLUMN_USER_NAME_ID + " =?", userName);
            Log.d(TAG, "method getSwapCardData: Loaded " + cursor.getCount() + " SwapCardData records... this should always only be 1");
            if (swapCardIDRecordsInDatabase(Shared.context)) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    SwapCardID swapCardIDAtCursor = cursorToSwapCardID(cursor);
                    //Check the state of all MatchCardData fields here
                    Log.d(TAG, "... PARSE: SwapCardID object to return:" +
                            " | cardID: " + swapCardIDAtCursor.getCardIDKey() +
                            " | species number: " + swapCardIDAtCursor.getSwapCardSpeciesID() +
                            " | segment number: " + swapCardIDAtCursor.getSwapCardSegmentID());
                    swapCardIDToReturn = swapCardIDAtCursor;
                    cursor.moveToNext();
                }
                cursor.close();
                database.close();
            }
        }
        return swapCardIDToReturn;
    }


    public static boolean insertSwapCardID(SwapCardID swapCardID) {
        Log.d(TAG, "method insertSwapCardID tries to insert a new SwapCardID object into the database");

        boolean success = false;

        Log.d(TAG, "method insertSwapCardID: creating ContentValues");
        ContentValues values = swapCardIDToContentValues(swapCardID);

        DatabaseWrapper databaseWrapper = Shared.databaseWrapper;
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        try {
            if (database != null) {
                long rowID = database.insert(TABLE_NAME, "null", values);
                Log.d(TAG, "method insertSwapCardID: Inserted new SwapCardID into rowID: " + rowID);
                success = true;
            }
        } catch (SQLiteException sqlex) {
            Log.e(TAG, "method insertSwapCardID: Failed to insert SwapCardID[" + swapCardID.getCardIDKey() + "] due to: " + sqlex);
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
    private static ContentValues swapCardIDToContentValues(SwapCardID swapCardID) {
        Log.d(TAG, "private method swapCardIDToContentValues");
        ContentValues values = new ContentValues();

        values.put(COLUMN_SWAP_CARD_ID_KEY, swapCardID.getCardIDKey());
        values.put(COLUMN_SPECIES_ID, swapCardID.getSwapCardSpeciesID());
        values.put(COLUMN_SEGMENT_ID, swapCardID.getSwapCardSegmentID());

        return values;
    }

    //method cursorToUserData populates a UserData object with data from the cursor
    private static SwapCardID cursorToSwapCardID( Cursor cursor) {
        Log.d(TAG, "method cursorToSwapCardID");
        SwapCardID cursorAtSwapCardID = new SwapCardID(-1, -1);     //FIXME - is there a better way to null this constructor?

        cursorAtSwapCardID.setCardIDKey(cursor.getDouble(cursor.getColumnIndex(COLUMN_SWAP_CARD_ID_KEY)));
        cursorAtSwapCardID.setSwapCardSpeciesID(cursor.getInt(cursor.getColumnIndex(COLUMN_SPECIES_ID)));
        cursorAtSwapCardID.setSwapCardSegmentID(cursor.getInt(cursor.getColumnIndex(COLUMN_SEGMENT_ID)));

        return cursorAtSwapCardID;
    }
}