package ws.isak.bridge.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;

import ws.isak.bridge.common.Shared;
import ws.isak.bridge.model.ComposeGameData;

/**
 * Created by isakherman on 6/12/17.
 */

public class ComposeGameDataORM {

    private static final String TAG = "ComposeGameDataORM";

    private static final String DELIMITER = ", ";

    private static final String TABLE_NAME = "compose_game_data";
    private static final String COMMA_SEP = ", ";

    private static final String COLUMN_GAME_START_TIMESTAMP_TYPE = "INTEGER PRIMARY KEY";
    private static final String COLUMN_GAME_START_TIMESTAMP = "gameStartTimestamp";

    private static final String COLUMN_PLAYER_USERNAME_TYPE = "STRING";                 //TODO foreign key?
    private static final String COLUMN_PLAYER_USERNAME = "playerUserName";

    private static final String COLUMN_NUM_TURNS_TAKEN_IN_GAME_TYPE = "INTEGER";
    private static final String COLUMN_NUM_TURNS_TAKEN_IN_GAME = "numTurnsTakenInGame";

    private static final String COLUMN_GAME_PLAY_DURATIONS_TYPE = "STRING";
    private static final String COLUMN_GAME_PLAY_DURATIONS = "gamePlayDurations";

    private static final String COLUMN_TURN_DURATIONS_TYPE = "STRING";
    private static final String COLUMN_TURN_DURATIONS = "turnDurations";

    public static final String SQL_CREATE_TABLE = "CREATE TABLE " +
            TABLE_NAME +
            " (" +
            COLUMN_PLAYER_USERNAME + " " + COLUMN_PLAYER_USERNAME_TYPE + COMMA_SEP +
            COLUMN_GAME_START_TIMESTAMP + " " + COLUMN_GAME_START_TIMESTAMP_TYPE + COMMA_SEP +
            COLUMN_NUM_TURNS_TAKEN_IN_GAME + " " + COLUMN_NUM_TURNS_TAKEN_IN_GAME_TYPE + COMMA_SEP +
            COLUMN_GAME_PLAY_DURATIONS + " " + COLUMN_GAME_PLAY_DURATIONS_TYPE + COMMA_SEP +
            COLUMN_TURN_DURATIONS + " " + COLUMN_TURN_DURATIONS_TYPE  +
            ")";

    public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    //===============================================================================
    //public methods for interacting with tables in the database:

    // method recordsInDatabase returns true if there are records of type SwapGameData
    // in the DB, otherwise, false

    public static boolean composeGameRecordsInDatabase(Context context) {
        //Log.d (TAG, "method composeGameRecordsInDatabase");

        DatabaseWrapper databaseWrapper = Shared.databaseWrapper;
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        boolean recordsExist = false;

        if (database != null) {
            Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME, null);
            Log.d(TAG, "method recordsInDatabase: Checked " + cursor.getCount() + " ComposeGameData records...");

            if (cursor.getCount() > 0) {
                recordsExist = true;
            }
            cursor.close();
        }
        database.close();
        return recordsExist;
    }

    // method numComposeGameRecordsInDatabase returns the number of records in the ComposeGameDataORM table
    // in the DB, otherwise, false
    public static int numComposeGameRecordsInDatabase(Context context) {
        //Log.d (TAG, "method numComposeGameRecordsInDatabase");

        DatabaseWrapper databaseWrapper = Shared.databaseWrapper;
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        int numRecords = 0;

        if (database != null) {
            Cursor cursor = database.rawQuery("SELECT * FROM " + ComposeGameDataORM.TABLE_NAME, null);
            Log.d(TAG, "method numComposeGameRecordsInDatabase: There are: " + cursor.getCount() + " ComposeGameData records...");

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

    //return all of the SwapGameData rows for the targetUser, sorted by TimeStamp
    public static ArrayList<ComposeGameData> getComposeGameData(String targetUser) {
        Log.d (TAG, "method getMatchGameData returns a list of MatchGameData objects with type " + targetUser);

        DatabaseWrapper databaseWrapper =  Shared.databaseWrapper;
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        ArrayList <ComposeGameData> composeGameDataList = null;

        if (database !=  null) {
            Cursor cursor = database.rawQuery("SELECT * FROM " + ComposeGameDataORM.TABLE_NAME + " WHERE " + ComposeGameDataORM.COLUMN_PLAYER_USERNAME + " ='" + targetUser + "'", null);
            //FIXME - this prevent's SQL injection: Cursor cursor = database.rawQuery("SELECT * FROM " + UserDataORM.TABLE_NAME + " WHERE " + UserDataORM.COLUMN_USER_NAME_ID + " =?", userName);

            Log.d (TAG, "method getUserData: Loaded " + cursor.getCount() + " UserData records...");
            if (composeGameRecordsInDatabase(Shared.context)) {
                composeGameDataList = new ArrayList<ComposeGameData>(numComposeGameRecordsInDatabase(Shared.context));
                cursor.moveToFirst();
                int rowCount = 0;
                while (!cursor.isAfterLast()) {
                    ComposeGameData composeGameDataAtCursor = cursorToComposeGameData(cursor);
                    //Check the state of all SwapGameData fields here
                    Log.d (TAG, " method getSwapGameData:  PARSE: Database row: " + rowCount +
                            " | gameStartTimestamp: " + composeGameDataAtCursor.getGameStartTimestamp() +
                            " | playerUserName: " + composeGameDataAtCursor.getUserPlayingName() +
                            " | numTurnsTakenInGame: "+ composeGameDataAtCursor.getNumTurnsTaken());
                    if (composeGameDataAtCursor.sizeOfPlayDurationsArray() != composeGameDataAtCursor.sizeOfTurnDurationsArray()) {
                        Log.d (TAG, " ***** ERROR! Size of play durations and turn durations not returned as equal");
                    }
                    for (int i = 0; i < composeGameDataAtCursor.sizeOfPlayDurationsArray(); i++) {
                        Log.d(TAG, " method getComposeGameData: PARSE ARRAYS in Database row " + rowCount +
                                " | current array element i: " + i +
                                " | gamePlayDuration(i): " + composeGameDataAtCursor.queryGamePlayDurations(i) +
                                " | turnDurations(i): " + composeGameDataAtCursor.queryTurnDurationsArray(i));

                    }
                    composeGameDataList.add(composeGameDataAtCursor);
                    Log.d (TAG, "method getComposeGameData: composeGameDataList.get(rowCount) composeGameData Object @: " +
                            composeGameDataList.get(rowCount));
                    //move cursor to start of next row
                    rowCount++;
                    Log.d (TAG, "method getComposeGameData: rowCount incremented: rowCount: " + rowCount);
                    cursor.moveToNext();
                }
                cursor.close();
                database.close();
            }
        }
        return composeGameDataList;
    }

    //method insertMatchGameData inserts a new SwapGameData object into the database if it doesn't already exist
    public static boolean insertComposeGameData(ComposeGameData composeGameData) {
        Log.d (TAG, "method insertComposeGameData: insert a new ComposeGameData object into the database");

        boolean success = false;

        Log.d(TAG, "method insertComposeGameData: creating ContentValues");
        ContentValues values = composeGameDataToContentValues(composeGameData);

        DatabaseWrapper databaseWrapper = Shared.databaseWrapper;
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        try {
            if (database != null) {
                long rowID = database.insert(TABLE_NAME, "null", values);
                Log.d(TAG, "method insertComposeGameData: Inserted new ComposeGameData into rowID: " + rowID);
                success = true;
            }
        } catch (SQLiteException sqlex) {
            Log.e(TAG, "method insertComposeGameData: Failed to insert ComposeGameData[" + composeGameData.getGameStartTimestamp() + "] due to: " + sqlex);
            sqlex.printStackTrace();
        } finally {
            if (database != null) {
                database.close();
            }
        }
        return success;
    }

    //method matchGameDataToContentValues packs a MemGame object into a ContentValues map for
    //use with SQL inserts
    private static ContentValues composeGameDataToContentValues (ComposeGameData composeGameData) {
        Log.d (TAG, "private method composeGameDataToContentValues");
        ContentValues values = new ContentValues();

        Log.d (TAG, " ... composeGameDataToContentValues: putting UserName: " + composeGameData.getUserPlayingName());
        values.put (COLUMN_PLAYER_USERNAME, composeGameData.getUserPlayingName());

        Log.d (TAG, " ... composeGameDataToContentValues: putting GameStartTimeStamp: " + composeGameData.getGameStartTimestamp());
        values.put (COLUMN_GAME_START_TIMESTAMP, composeGameData.getGameStartTimestamp());

        StringBuilder gamePlayDurationsString = new StringBuilder();
        for (Long elementInGamePlayDurationsArrayList : composeGameData.getGamePlayDurations()) {
            gamePlayDurationsString.append(elementInGamePlayDurationsArrayList);
            gamePlayDurationsString.append(DELIMITER);
        }
        Log.d (TAG, " ... swapGameDataToContentValues: putting PlayDurationsString: " + gamePlayDurationsString.toString());
        values.put (COLUMN_GAME_PLAY_DURATIONS, gamePlayDurationsString.toString());

        StringBuilder turnDurationsString = new StringBuilder();
        for (Long elementInTurnDurationsArrayList : composeGameData.getTurnDurationsArray()) {
            turnDurationsString.append(elementInTurnDurationsArrayList);
            turnDurationsString.append(DELIMITER);
        }
        Log.d (TAG, " ... swapGameDataToContentValues: putting TurnDurationsString: " + turnDurationsString.toString());
        values.put (COLUMN_TURN_DURATIONS, turnDurationsString.toString());

        Log.d (TAG, " ... swapGameDataToContentValues: putting NumTurnsTaken: " + composeGameData.getNumTurnsTaken());
        values.put (COLUMN_NUM_TURNS_TAKEN_IN_GAME, composeGameData.getNumTurnsTaken());
        Log.d (TAG, "private method composeGameDataToContentValues: returning values");
        return values;
    }

    //method cursorToUserData populates a UserData object with data from the cursor
    private static ComposeGameData cursorToComposeGameData(Cursor cursor) {
        Log.d (TAG, "method cursorToComposeGameData: creating new ComposeGameData with null boardMap");
        ComposeGameData cursorAtComposeGameData = new ComposeGameData(0, 0); //FIXME - how to load without knowing dimens?
        Log.d (TAG, "method cursorToComposeGameData: setting data to ComposeGameDataObject from cursor...");
        cursorAtComposeGameData.setUserPlayingName(cursor.getString(cursor.getColumnIndex(COLUMN_PLAYER_USERNAME)));
        Log.d (TAG, " ... cursorAtComposeGameData.getUserPlayingName(): " + cursorAtComposeGameData.getUserPlayingName());
        cursorAtComposeGameData.setGameStartTimestamp ((long) cursor.getInt(cursor.getColumnIndex(COLUMN_GAME_START_TIMESTAMP)));
        Log.d (TAG, " ... cursorAtComposeGameData.getGameStartTimestamp(): " + cursorAtComposeGameData.getGameStartTimestamp());

        String gamePlayDurationsString = cursor.getString(cursor.getColumnIndex(COLUMN_GAME_PLAY_DURATIONS));
        Log.d (TAG, " ... gamePlayDurationsString: " + gamePlayDurationsString);
        for (String s : gamePlayDurationsString.split(DELIMITER)) {
            cursorAtComposeGameData.appendToGamePlayDurations(Long.valueOf(s));
        }
        String turnDurationsString = cursor.getString(cursor.getColumnIndex(COLUMN_TURN_DURATIONS));
        Log.d (TAG, " ... turnDurationsString: " + turnDurationsString);
        for (String s : turnDurationsString.split(DELIMITER)) {
            cursorAtComposeGameData.appendToTurnDurations(Long.valueOf(s));
        }
        cursorAtComposeGameData.setNumTurnsTaken(cursor.getInt(cursor.getColumnIndex(COLUMN_NUM_TURNS_TAKEN_IN_GAME)));
        return cursorAtComposeGameData;
    }
}