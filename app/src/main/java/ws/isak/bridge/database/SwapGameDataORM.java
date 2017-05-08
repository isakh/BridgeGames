package ws.isak.bridge.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;


import java.util.ArrayList;
import java.util.HashMap;

import ws.isak.bridge.common.Shared;
import ws.isak.bridge.model.SwapGameData;

/*
 *
 *
 * @author isak
 */

public class SwapGameDataORM {

    private static final String TAG = "SwapGameDataORM";

    private static final String DELIMITER = ", ";

    private static final String TABLE_NAME = "swap_game_data";
    private static final String COMMA_SEP = ", ";

    private static final String COLUMN_GAME_START_TIMESTAMP_TYPE = "INTEGER PRIMARY KEY";
    private static final String COLUMN_GAME_START_TIMESTAMP = "gameStartTimestamp";

    private static final String COLUMN_PLAYER_USERNAME_TYPE = "STRING";
    private static final String COLUMN_PLAYER_USERNAME = "playerUserName";

    private static final String COLUMN_DIFFICULTY_TYPE = "INTEGER";
    private static final String COLUMN_DIFFICULTY = "difficultyLevel";

    private static final String COLUMN_GAME_DURATION_ALLOCATED_TYPE = "INTEGER";
    private static final String COLUMN_GAME_DURATION_ALLOCATED = "gameDurationAllocated";

    private static final String COLUMN_GAME_STARTED_TYPE = "INTEGER";
    private static final String COLUMN_GAME_STARTED = "gameStarted";

    private static final String COLUMN_NUM_TURNS_TAKEN_IN_GAME_TYPE = "INTEGER";
    private static final String COLUMN_NUM_TURNS_TAKEN_IN_GAME = "numTurnsTakenInGame";

    private static final String COLUMN_GAME_PLAY_DURATIONS_TYPE = "STRING";
    private static final String COLUMN_GAME_PLAY_DURATIONS = "gamePlayDurations";

    private static final String COLUMN_TURN_DURATIONS_TYPE = "STRING";
    private static final String COLUMN_TURN_DURATIONS = "turnDurations";

    //FIXME store SwapGameMapList which is an array list of hashmaps of <coord, card> objects as a string? or better serialize and use BLOB?
    private static final String COLUMN_SWAP_BOARD_MAP_LIST_TYPE = "STRING";
    private static final String COLUMN_SWAP_BOARD_MAP_LIST = "swapGameMapList"

    public static final String SQL_CREATE_TABLE = "CREATE TABLE " +
            TABLE_NAME +
            " (" +
            COLUMN_PLAYER_USERNAME + " " + COLUMN_PLAYER_USERNAME_TYPE + COMMA_SEP +
            COLUMN_GAME_START_TIMESTAMP + " " + COLUMN_GAME_START_TIMESTAMP_TYPE + COMMA_SEP +
            COLUMN_DIFFICULTY + " " + COLUMN_DIFFICULTY_TYPE + COMMA_SEP +
            COLUMN_GAME_DURATION_ALLOCATED + " " + COLUMN_GAME_DURATION_ALLOCATED_TYPE + COMMA_SEP +
            COLUMN_GAME_STARTED + " " + COLUMN_GAME_STARTED_TYPE + COMMA_SEP +
            COLUMN_NUM_TURNS_TAKEN_IN_GAME + " " + COLUMN_NUM_TURNS_TAKEN_IN_GAME_TYPE + COMMA_SEP +
            COLUMN_GAME_PLAY_DURATIONS + " " + COLUMN_GAME_PLAY_DURATIONS_TYPE + COMMA_SEP +
            COLUMN_TURN_DURATIONS + " " + COLUMN_TURN_DURATIONS_TYPE + COMMA_SEP +
            COLUMN_SWAP_BOARD_MAP_LIST + " " + COLUMN_SWAP_BOARD_MAP_LIST_TYPE +
            ")";

    public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    //===============================================================================

    //public methods for interacting with tables in the database:

    // method recordsInDatabase returns true if there are records of type SwapGameData
    // in the DB, otherwise, false

    public static boolean swapGameRecordsInDatabase(Context context) {
        //Log.d (TAG, "method matchGameRecordsInDatabase");

        DatabaseWrapper databaseWrapper = Shared.databaseWrapper;
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        boolean recordsExist = false;

        if (database != null) {
            Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME, null);
            Log.d(TAG, "method recordsInDatabase: Checked " + cursor.getCount() + " SwapGameData records...");

            if (cursor.getCount() > 0) {
                recordsExist = true;
            }
            cursor.close();
        }
        database.close();
        return recordsExist;
    }

    // method numRecordsInDatabase returns the number of records in the SwapGameDataORM table
    // in the DB, otherwise, false
    public static int numSwapGameRecordsInDatabase(Context context) {
        //Log.d (TAG, "method numSwapGameRecordsInDatabase");

        DatabaseWrapper databaseWrapper = Shared.databaseWrapper;
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        int numRecords = 0;

        if (database != null) {
            Cursor cursor = database.rawQuery("SELECT * FROM " + SwapGameDataORM.TABLE_NAME, null);
            Log.d(TAG, "method numSwapGameRecordsInDatabase: There are: " + cursor.getCount() + " SwapGameData records...");

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
    public static ArrayList<SwapGameData> getSwapGameData(String targetUser) {
        Log.d (TAG, "method getMatchGameData returns a list of MatchGameData objects with type " + targetUser);

        DatabaseWrapper databaseWrapper =  Shared.databaseWrapper;
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        ArrayList <SwapGameData> swapGameDataList = null;

        if (database !=  null) {
            Cursor cursor = database.rawQuery("SELECT * FROM " + SwapGameDataORM.TABLE_NAME + " WHERE " + SwapGameDataORM.COLUMN_PLAYER_USERNAME + " ='" + targetUser + "'", null);
            //FIXME - this prevent's SQL injection: Cursor cursor = database.rawQuery("SELECT * FROM " + UserDataORM.TABLE_NAME + " WHERE " + UserDataORM.COLUMN_USER_NAME_ID + " =?", userName);

            Log.d (TAG, "method getUserData: Loaded " + cursor.getCount() + " UserData records...");
            if (swapGameRecordsInDatabase(Shared.context)) {
                swapGameDataList = new ArrayList<SwapGameData>(numSwapGameRecordsInDatabase(Shared.context));
                cursor.moveToFirst();
                int rowCount = 0;
                while (!cursor.isAfterLast()) {
                    SwapGameData swapGameDataAtCursor = cursorToSwapGameData(cursor);
                    //Check the state of all SwapGameData fields here
                    Log.d (TAG, "... PARSE: Database row: " + rowCount +
                            " | gameStartTimestamp: " + swapGameDataAtCursor.getGameStartTimestamp() +
                            " | playerUserName: " + swapGameDataAtCursor.getUserPlayingName() +
                            " | difficultyLevel: " + swapGameDataAtCursor.getGameDifficulty() +
                            " | gameDurationAllocated: " + swapGameDataAtCursor.getGameDurationAllocated() +
                            " | gameStarted: " + swapGameDataAtCursor.isGameStarted() +
                            " | numTurnsTakenInGame: "+ swapGameDataAtCursor.getNumTurnsTaken());
                    if (swapGameDataAtCursor.sizeOfPlayDurationsArray() != swapGameDataAtCursor.sizeOfTurnDurationsArray()) {
                        Log.d (TAG, " ***** ERROR! Size of play durations and turn durations not returned as equal");
                    }
                    //FIXME extend to compare with BoardMaps array size as well? for both? or is just comparing to PlayDurations enough
                    if (swapGameDataAtCursor.sizeOfPlayDurationsArray() != swapGameDataAtCursor.sizeOfSwapGameMapList()) {
                        Log.d (TAG, " ***** ERROR! Size of play durations and number of BoardMaps in list not returned as equal");
                    }
                    for (int i = 0; i < swapGameDataAtCursor.sizeOfPlayDurationsArray(); i++) {
                        Log.d(TAG, " ... PARSE ARRAYS in Database row " + rowCount +
                                " | current array element i: " + i +
                                " | gamePlayDuration(i): " + swapGameDataAtCursor.queryGamePlayDurations(i) +
                                " | turnDurations(i): " + swapGameDataAtCursor.queryTurnDurationsArray(i) +
                                " | boardMapsList(i): " + swapGameDataAtCursor.querySwapGameMapList(i));

                    }
                    swapGameDataList.add(swapGameDataAtCursor);
                    Log.d (TAG, "!!! userDataList.get(rowCount) userData Object @: " + swapGameDataList.get(rowCount));
                    //move cursor to start of next row
                    rowCount++;
                    cursor.moveToNext();
                }
                cursor.close();
                database.close();
            }
        }
        return swapGameDataList;
    }

    //method insertMatchGameData inserts a new SwapGameData object into the database if it doesn't already exist
    public static boolean insertSwapGameData(SwapGameData swapGameData) {
        Log.d (TAG, "method insertSwapGameData tries to insert a new SwapGameData object into the database");

        boolean success = false;

        Log.d(TAG, "method insertSwapGameData: creating ContentValues");
        ContentValues values = swapGameDataToContentValues(swapGameData);

        DatabaseWrapper databaseWrapper = Shared.databaseWrapper;
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        try {
            if (database != null) {
                long rowID = database.insert(TABLE_NAME, "null", values);
                Log.d(TAG, "method insertSwapGameData: Inserted new SwapGameData into rowID: " + rowID);
                success = true;
            }
        } catch (SQLiteException sqlex) {
            Log.e(TAG, "method insertSwapGameData: Failed to insert SwapGameData[" + swapGameData.getGameStartTimestamp() + "] due to: " + sqlex);
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
    private static ContentValues swapGameDataToContentValues (SwapGameData swapGameData) {
        Log.d (TAG, "private method swapGameDataToContentValues");
        ContentValues values = new ContentValues();
        Log.d (TAG, " ... swapGameDataToContentValues: putting UserName: " + swapGameData.getUserPlayingName());
        values.put (COLUMN_PLAYER_USERNAME, swapGameData.getUserPlayingName());
        Log.d (TAG, " ... swapGameDataToContentValues: putting GameStartTimeStamp: " + swapGameData.getGameStartTimestamp());
        values.put (COLUMN_GAME_START_TIMESTAMP, swapGameData.getGameStartTimestamp());
        Log.d (TAG, " ... swapGameDataToContentValues: putting Difficulty: " + swapGameData.getGameDifficulty());
        values.put (COLUMN_DIFFICULTY, swapGameData.getGameDifficulty());
        Log.d (TAG, " ... swapGameDataToContentValues: putting GameDuration: " + swapGameData.getGameDurationAllocated());
        values.put (COLUMN_GAME_DURATION_ALLOCATED, swapGameData.getGameDurationAllocated());
        Log.d (TAG, " ... swapGameDataToContentValues: putting GameStarted (always 1?): " + swapGameData.isGameStarted());
        if (!swapGameData.isGameStarted()) values.put (COLUMN_GAME_STARTED, 0);
        else values.put (COLUMN_GAME_STARTED, 1);

        StringBuilder gamePlayDurationsString = new StringBuilder();
        for (Long elementInGamePlayDurationsArrayList : swapGameData.getGamePlayDurations()) {
            gamePlayDurationsString.append(elementInGamePlayDurationsArrayList);
            gamePlayDurationsString.append(DELIMITER);
        }
        Log.d (TAG, " ... swapGameDataToContentValues: putting PlayDurationsString: " + gamePlayDurationsString.toString());
        values.put (COLUMN_GAME_PLAY_DURATIONS, gamePlayDurationsString.toString());

        StringBuilder turnDurationsString = new StringBuilder();
        for (Long elementInTurnDurationsArrayList : swapGameData.getTurnDurationsArray()) {
            turnDurationsString.append(elementInTurnDurationsArrayList);
            turnDurationsString.append(DELIMITER);
        }
        Log.d (TAG, " ... swapGameDataToContentValues: putting TurnDurationsString: " + turnDurationsString.toString());
        values.put (COLUMN_TURN_DURATIONS, turnDurationsString.toString());

        //FIXME sort out BoardMaps - for now we make a comma seperated string where each element is a hashmap of objects, does this work?
        //StringBuilder swapBoardMapListString = new StringBuilder();
        //for (HashMap curBoardMap : swapGameData.getSwapGameMapList()) {
        //    swapBoardMapListString.append(curBoardMap);
        //    swapBoardMapListString.append(DELIMITER);
        //}
        //Log.d (TAG, " ... swapGameDataToContentValues: putting swapBoardMapListString: " + swapBoardMapListString.toString());
        //values.put (COLUMN_SWAP_BOARD_MAP_LIST, swapBoardMapListString.toString());

        values.put (COLUMN_NUM_TURNS_TAKEN_IN_GAME, swapGameData.getNumTurnsTaken());
        Log.d (TAG, "private method swapGameDataToContentValues: returning values");
        return values;
    }

    //method cursorToUserData populates a UserData object with data from the cursor
    private static SwapGameData cursorToSwapGameData(Cursor cursor) {
        Log.d (TAG, "method cursorToSwapGameData");
        SwapGameData cursorAtSwapGameData = new SwapGameData();

        cursorAtSwapGameData.setUserPlayingName(cursor.getString(cursor.getColumnIndex(COLUMN_PLAYER_USERNAME)));
        cursorAtSwapGameData.setGameStartTimestamp ((long) cursor.getInt(cursor.getColumnIndex(COLUMN_GAME_START_TIMESTAMP)));
        cursorAtSwapGameData.setGameDifficulty(cursor.getInt(cursor.getColumnIndex(COLUMN_DIFFICULTY)));
        cursorAtSwapGameData.setGameDurationAllocated((long) cursor.getInt(cursor.getColumnIndex(COLUMN_GAME_DURATION_ALLOCATED)));

        if (cursor.getInt(cursor.getColumnIndex(COLUMN_GAME_STARTED)) == 1) { cursorAtSwapGameData.setGameStarted(true); }
        else if (cursor.getInt(cursor.getColumnIndex(COLUMN_GAME_STARTED)) == 0) { cursorAtSwapGameData.setGameStarted(false); }
        else {
            Log.d (TAG, "ERROR: method cursorAtSwapGameData: isGameStarted: " + cursor.getInt(cursor.getColumnIndex(COLUMN_GAME_STARTED)));
        }

        String gamePlayDurationsString = cursor.getString(cursor.getColumnIndex(COLUMN_GAME_PLAY_DURATIONS));
        for (String s : gamePlayDurationsString.split(DELIMITER)) cursorAtSwapGameData.appendToGamePlayDurations(Long.valueOf(s));

        String turnDurationsString = cursor.getString(cursor.getColumnIndex(COLUMN_TURN_DURATIONS));
        //Log.d (TAG, "******** method cursorToSwapGameData: turnDurationsString: " + turnDurationsString);
        for (String s : turnDurationsString.split(DELIMITER)) cursorAtSwapGameData.appendToTurnDurations(Long.valueOf(s));
        
        //FIXME
        //String swapBoardMapListString = cursor.getString(cursor.getColumnIndex(COLUMN_SWAP_BOARD_MAP_LIST));
        //Log.d (TAG, "******** method cursorToSwapGameData: swapBoardMapListString: " + swapBoardMapListString);
        //for (String s : swapBoardMapListString.split(DELIMITER)) cursorAtSwapGameData.appendToSwapGameMapList(HashMap.valueOf(s));

        cursorAtSwapGameData.setNumTurnsTaken(cursor.getInt(cursor.getColumnIndex(COLUMN_NUM_TURNS_TAKEN_IN_GAME)));
        return cursorAtSwapGameData;
    }
}
