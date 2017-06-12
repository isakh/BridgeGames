package ws.isak.bridge.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import ws.isak.bridge.common.ComposeSampleData;
import ws.isak.bridge.common.Shared;

/**
 * Created by isakherman on 6/12/17.
 */

public class ComposeSampleDataORM {

    private static final String TAG = "ComposeSampleDataORM";

    private static final String TABLE_NAME = "compose_sample_data";

    private static final String COMMA_SEP = ", ";

    private static final String COLUMN_SPECIES_NAME_TYPE = "STRING PRIMARY KEY";
    private static final String COLUMN_SPECIES_NAME = "speciesName";

    private static final String COLUMN_SPECTRO_URI_TYPE = "STRING";
    private static final String COLUMN_SPECTRO_URI = "spectroURI";

    private static final String COLUMN_AUDIO_URI_TYPE = "STRING";
    private static final String COLUMN_AUDIO_URI = "audioURI";

    private static final String COLUMN_SAMPLE_DURATION_TYPE = "INTEGER";        //SQLite Integer can handle 8 byte long
    private static final String COLUMN_SAMPLE_DURATION = "sampleDuration";   

    public static final String SQL_CREATE_TABLE = "CREATE TABLE " +
            TABLE_NAME +
            " (" +
            COLUMN_SPECIES_NAME + " " + COLUMN_SPECIES_NAME_TYPE + COMMA_SEP +
            COLUMN_SPECTRO_URI + " " + COLUMN_SPECTRO_URI_TYPE + COMMA_SEP +
            COLUMN_AUDIO_URI + " " + COLUMN_AUDIO_URI_TYPE + COMMA_SEP +
            COLUMN_SAMPLE_DURATION + " " + COLUMN_SAMPLE_DURATION_TYPE + 
            ")";

    public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    // ===========================================================================================

    public static boolean composeSampleDataRecordsInDatabase(Context context) {
        //Log.d (TAG, "method matchSampleDataRecordsInDatabase");

        DatabaseWrapper databaseWrapper = Shared.databaseWrapper;
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        boolean recordsExist = false;

        if (database != null) {
            Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME, null);
            Log.d(TAG, "method composeSampleDataRecordsInDatabase: Checked " + cursor.getCount() + " ComposeSampleData records...");

            if (cursor.getCount() > 0) {
                recordsExist = true;
            }
            cursor.close();
        }
        database.close();
        return recordsExist;
    }

    public static int numComposeSampleDataRecordsInDatabase(Context context) {
        //Log.d (TAG, "method numComposeSampleDataRecordsInDatabase");

        DatabaseWrapper databaseWrapper = Shared.databaseWrapper;
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        int numRecords = 0;

        if (database != null) {
            Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME, null);
            Log.d(TAG, "method numComposeSampleDataRecordsInDatabase: There are: " + cursor.getCount() + " ComposeSampleData records...");

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

    //method isComposeSampleDataInDB takes a ComposeSampleData object and checks whether it has been used as a
    //primary key yet in the database - used to check existence and uniqueness when loading samples.
    public static boolean isComposeSampleDataInDB(ComposeSampleData sampleData) {
        Log.d(TAG, "method isComposeSampleDataInDB: check sampleData: " + sampleData);
        DatabaseWrapper databaseWrapper = Shared.databaseWrapper;
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        boolean sampleExists = false;     //false unless found in database
        String speciesName = sampleData.getSpeciesName();
        //Log.d (TAG, " ... checking speciesName: " + speciesName);
        if (database != null) {
            //Log.d(TAG, "method isComposeSampleDataInDB: searching.....");
            Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_SPECIES_NAME + " ='" + speciesName + "'", null);
            //FIXME - this prevent's SQL injection: Cursor cursor = database.rawQuery("SELECT * FROM " + UserDataORM.TABLE_NAME + " WHERE " + UserDataORM.COLUMN_USER_NAME_ID + " =?", userName);
            if (cursor.getCount() > 0) {
                sampleExists = true;
            } else {
                //Toast.makeText(Shared.context, "new compose game sample added to database", Toast.LENGTH_SHORT).show();
            }
            cursor.close();
            database.close();
        }
        Log.d (TAG, " ..... searching ..... sampleExists: " + sampleExists);
        return sampleExists;
    }


    public static ComposeSampleData getComposeSampleData(String speciesName) {
        Log.d(TAG, "method getComposeSampleData returns a list of composeSampleData objects with speciesName " + speciesName);

        DatabaseWrapper databaseWrapper = Shared.databaseWrapper;
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        ComposeSampleData composeSampleDataToReturn = null;

        if (database != null) {
            Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_SPECIES_NAME + " ='" + speciesName + "'", null);
            //FIXME - this prevent's SQL injection: Cursor cursor = database.rawQuery("SELECT * FROM " + UserDataORM.TABLE_NAME + " WHERE " + UserDataORM.COLUMN_USER_NAME_ID + " =?", userName);
            Log.d(TAG, "method getComposeSampleData: Loaded " + cursor.getCount() + " ComposeSampleData records... this should always only be 1");
            if (composeSampleDataRecordsInDatabase(Shared.context)) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    ComposeSampleData composeSampleDataAtCursor = cursorToComposeSampleData(cursor);
                    //Check the state of all ComposeSampleData fields here
                    Log.d(TAG, "... PARSE: ComposeSampleData object to return:" +
                            " | speciesName: " + composeSampleDataAtCursor.getSpeciesName() +
                            " | imageURI: " + composeSampleDataAtCursor.getSpectroURI() +
                            " | audioURI: " + composeSampleDataAtCursor.getAudioURI() +
                            " | sampleDuration: " + composeSampleDataAtCursor.getSampleDuration());
                    composeSampleDataToReturn = composeSampleDataAtCursor;
                    cursor.moveToNext();
                }
                cursor.close();
                database.close();
            }
        }
        return composeSampleDataToReturn;
    }


    public static boolean insertComposeSampleData(ComposeSampleData composeSampleData) {
        Log.d(TAG, "method insertComposeSampleData tries to insert a new ComposeSampleData object into the database");

        boolean success = false;

        Log.d(TAG, "method insertComposeSampleData: creating ContentValues");
        ContentValues values = composeSampleDataToContentValues(composeSampleData);

        DatabaseWrapper databaseWrapper = Shared.databaseWrapper;
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        try {
            if (database != null) {
                long rowID = database.insert(TABLE_NAME, "null", values);
                Log.d(TAG, "method insertComposeSampleData: Inserted new ComposeSampleData into rowID: " + rowID);
                success = true;
            }
        } catch (SQLiteException sqlex) {
            Log.e(TAG, "method insertComposeSampleData: Failed to insert ComposeSampleData[" + composeSampleData.getSpeciesName() + "] due to: " + sqlex);
            sqlex.printStackTrace();
        } finally {
            if (database != null) {
                database.close();
            }
        }
        return success;
    }

    //method composeSampleDataToContentValues packs a ComposeSampleData object into a ContentValues map for
    //use with SQL inserts
    private static ContentValues composeSampleDataToContentValues(ComposeSampleData composeSampleData) {
        Log.d(TAG, "private method composeSampleDataToContentValues: creating values: putting ... ");
        ContentValues values = new ContentValues();
        Log.d (TAG, "... putting composeSampleData.getSpeciesName(): " + composeSampleData.getSpeciesName());
        values.put(COLUMN_SPECIES_NAME, composeSampleData.getSpeciesName());
        Log.d (TAG, "... putting composeSampleData.getSpectroURI0(): " + composeSampleData.getSpectroURI());
        values.put(COLUMN_SPECTRO_URI, composeSampleData.getSpectroURI());
        Log.d (TAG, "... putting composeSampleData.getAudioURI0(): " + composeSampleData.getAudioURI());
        values.put(COLUMN_AUDIO_URI, composeSampleData.getAudioURI());
        Log.d (TAG, "... putting composeSampleData.getSampleDuration0(): " + composeSampleData.getSampleDuration());
        values.put(COLUMN_SAMPLE_DURATION, composeSampleData.getSampleDuration());
        Log.d (TAG, " ... returning values ... ");
        return values;
    }


    //method cursorToUserData populates a UserData object with data from the cursor
    private static ComposeSampleData cursorToComposeSampleData(Cursor cursor) {
        Log.d(TAG, "method cursorToComposeSampleData");
        ComposeSampleData cursorAtComposeSampleData = new ComposeSampleData();

        cursorAtComposeSampleData.setSpeciesName(cursor.getString(cursor.getColumnIndex(COLUMN_SPECIES_NAME)));      //FIXME solve string approach to species loading - currently overloading constructor
        cursorAtComposeSampleData.setSpectroURI(cursor.getString(cursor.getColumnIndex(COLUMN_SPECTRO_URI)));
        cursorAtComposeSampleData.setAudioURI(cursor.getString(cursor.getColumnIndex(COLUMN_AUDIO_URI)));
        cursorAtComposeSampleData.setSampleDuration((long) cursor.getInt(cursor.getColumnIndex(COLUMN_SAMPLE_DURATION)));

        return cursorAtComposeSampleData;
    }
}
