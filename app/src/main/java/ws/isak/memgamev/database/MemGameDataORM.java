package ws.isak.memgamev.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;


import java.util.ArrayList;
import java.util.Arrays;

import ws.isak.memgamev.common.Shared;
import ws.isak.memgamev.model.MemGameData;

/*
 *
 * @author isak
 */

public class MemGameDataORM {

    private static final String TAG="Class: MemGameDataORM";
    private static final String DELIMITER=", ";

    private static final String TABLE_NAME="mem_game_data";
    private static final String COMMA_SEP= ", ";

    private static final String COLUMN_GAME_START_TIMESTAMP_TYPE = "INTEGER PRIMARY KEY";
    private static final String COLUMN_GAME_START_TIMESTAMP = "gameStartTimestamp";

    private static final String COLUMN_PLAYER_USERNAME_TYPE = "STRING";
    private static final String COLUMN_PLAYER_USERNAME = "playerUserName";

    private static final String COLUMN_THEME_ID_TYPE = "INTEGER";
    private static final String COLUMN_THEME_ID = "themeID";

    private static final String COLUMN_DIFFICULTY_TYPE = "INTEGER";
    private static final String COLUMN_DIFFICULTY = "difficulty";

    private static final String COLUMN_GAME_DURATION_ALLOCATED_TYPE = "INTEGER";
    private static final String COLUMN_GAME_DURATION_ALLOCATED = "gameDurationAllocated";

    //NOTE SQLite cannot handle booleans, so mixerState and gameStarted are stored as INTEGER
    private static final String COLUMN_MIXER_STATE_TYPE = "INTEGER";
    private static final String COLUMN_MIXER_STATE = "mixerState";

    private static final String COLUMN_GAME_STARTED_TYPE = "INTEGER";
    private static final String COLUMN_GAME_STARTED = "gameStarted";

    private static final String COLUMN_GAME_PLAY_DURATIONS_TYPE = "STRING";       //FIXME, maybe STRING? see http://stackoverflow.com/a/34767584/1443674
    private static final String COLUMN_GAME_PLAY_DURATIONS = "gamePlayDurations";

    private static final String COLUMN_TURN_DURATIONS_TYPE = "STRING";
    private static final String COLUMN_TURN_DURATIONS = "turnDurations";

    private static final String COLUMN_CARD_SELECTED_ORDER_TYPE = "STRING";       //FIXME is BLOB ok since this is an array of CardData objects?
    private static final String COLUMN_CARD_SELECTED_ORDER = "cardSelectedOrder";

    private static final String COLUMN_NUM_TURNS_TAKEN_IN_GAME_TYPE = "INTEGER";
    private static final String COLUMN_NUM_TURNS_TAKEN_IN_GAME = "numTurnsTakenInGame";

    public static final String SQL_CREATE_TABLE = "CREATE TABLE " +
            TABLE_NAME +
            " (" +
            COLUMN_PLAYER_USERNAME + " " + COLUMN_PLAYER_USERNAME_TYPE + COMMA_SEP +
            COLUMN_GAME_START_TIMESTAMP + " " + COLUMN_GAME_START_TIMESTAMP_TYPE + COMMA_SEP +
            COLUMN_THEME_ID + " " + COLUMN_THEME_ID_TYPE + COMMA_SEP +
            COLUMN_DIFFICULTY + " " + COLUMN_DIFFICULTY_TYPE + COMMA_SEP +
            COLUMN_GAME_DURATION_ALLOCATED + " " + COLUMN_GAME_DURATION_ALLOCATED_TYPE + COMMA_SEP +
            COLUMN_MIXER_STATE + " " + COLUMN_MIXER_STATE_TYPE + COMMA_SEP +
            COLUMN_GAME_STARTED + " " + COLUMN_GAME_STARTED_TYPE + COMMA_SEP +
            COLUMN_GAME_PLAY_DURATIONS + " " + COLUMN_GAME_PLAY_DURATIONS_TYPE + COMMA_SEP +
            COLUMN_TURN_DURATIONS + " " + COLUMN_TURN_DURATIONS_TYPE + COMMA_SEP +
            COLUMN_CARD_SELECTED_ORDER + " " + COLUMN_CARD_SELECTED_ORDER_TYPE + COMMA_SEP +
            COLUMN_NUM_TURNS_TAKEN_IN_GAME + " " + COLUMN_NUM_TURNS_TAKEN_IN_GAME_TYPE +
            ")";

    public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    //===============================================================================

    //public methods for interacting with tables in the database:

    // method recordsInDatabase returns true if there are records of type MemGameData
    // in the DB, otherwise, false
    public static boolean memGameRecordsInDatabase (Context context) {
        //Log.d (TAG, "method memGameRecordsInDatabase");

        DatabaseWrapper databaseWrapper = Shared.databaseWrapper;
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        boolean recordsExist = false;

        if (database != null) {
            Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME, null);     //FIXME was MemGameDataORM.TABLE_NAME
            Log.d(TAG, "method recordsInDatabase: Checked " + cursor.getCount() + " MemGameData records...");

            if (cursor.getCount() > 0) {
                recordsExist = true;
            }
            cursor.close();
        }
        database.close();
        return recordsExist;
    }


    // method numRecordsInDatabase returns the number of records in the MemGameDataORM table
    // in the DB, otherwise, false
    public static int numMemGameRecordsInDatabase (Context context) {
        //Log.d (TAG, "method numMemGameRecordsInDatabase");

        DatabaseWrapper databaseWrapper = Shared.databaseWrapper;
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        int numRecords = 0;

        if (database != null) {
            Cursor cursor = database.rawQuery("SELECT * FROM " + MemGameDataORM.TABLE_NAME, null);
            Log.d(TAG, "method numMemGameRecordsInDatabase: There are: " + cursor.getCount() + " MemGameData records...");

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

    //return all of the MemGameData rows for the targetUser, sorted by TimeStamp
    public static ArrayList<MemGameData> getMemGameData (String targetUser) {
        Log.d (TAG, "method getMemGameData returns a list of MemGameData objects with type " + targetUser);

        DatabaseWrapper databaseWrapper =  Shared.databaseWrapper;
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        ArrayList <MemGameData> memGameDataList = null;

        if (database !=  null) {
        Cursor cursor = database.rawQuery("SELECT * FROM " + MemGameDataORM.TABLE_NAME + " WHERE " + MemGameDataORM.COLUMN_PLAYER_USERNAME + " ='" + targetUser + "'", null);
            //FIXME - this prevent's SQL injection: Cursor cursor = database.rawQuery("SELECT * FROM " + UserDataORM.TABLE_NAME + " WHERE " + UserDataORM.COLUMN_USER_NAME_ID + " =?", userName);

            Log.d (TAG, "method getUserData: Loaded " + cursor.getCount() + " UserData records...");
            if (memGameRecordsInDatabase(Shared.context)) {
                memGameDataList = new ArrayList<MemGameData>(numMemGameRecordsInDatabase(Shared.context));
                cursor.moveToFirst();
                int rowCount = 0;
                while (!cursor.isAfterLast()) {
                    MemGameData memGameDataAtCursor = cursorToMemGameData(cursor);
                    //Check the state of all MemGameData fields here
                    Log.d (TAG, "... PARSE: Database row: " + rowCount +
                                " | gameStartTimestamp: " + memGameDataAtCursor.getGameStartTimestamp() +
                                " | playerUserName: " + memGameDataAtCursor.getUserPlayingName() +
                                " | themeID: " + memGameDataAtCursor.getThemeID() +
                                " | difficulty: " + memGameDataAtCursor.getGameDifficulty() +
                                " | gameDurationAllocated: " + memGameDataAtCursor.getGameDurationAllocated() +
                                " | mixerState: " + memGameDataAtCursor.getMixerState() +
                                " | gameStarted: " + memGameDataAtCursor.isGameStarted() +
                                " | numTurnsTakenInGame: "+ memGameDataAtCursor.getNumTurnsTaken());
                    if (memGameDataAtCursor.sizeOfPlayDurationsArray() != memGameDataAtCursor.sizeOfTurnDurationsArray()) {
                        //TODO extend to compare with cardObjects array size as well
                        Log.d (TAG, " ***** ERROR! Size of play durations and turn durations not returned as equal");
                    }
                    for (int i = 0; i < memGameDataAtCursor.sizeOfPlayDurationsArray(); i++) {
                        Log.d(TAG, " ... PARSE ARRAYS in Database row " + rowCount +
                                " | current array element i: " + i +
                                " | gamePlayDuration(i): " + memGameDataAtCursor.queryGamePlayDurations(i) +
                                " | turnDurations(i): " + memGameDataAtCursor.queryTurnDurationsArray(i) +
                                " | cardSelectedOrder: " + memGameDataAtCursor.queryCardsSelectedArray(i));
                    }
                    memGameDataList.add(memGameDataAtCursor);
                    Log.d (TAG, "!!! userDataList.get(rowCount) userData Object @: " + memGameDataList.get(rowCount));
                    //move cursor to start of next row
                    rowCount++;
                    cursor.moveToNext();
                }
                cursor.close();
                database.close();
            }
        }
        return  memGameDataList;
    }

    //method insertMemGameData inserts a new UserData object into the database if it doesn't already exist
    public static boolean insertMemGameData (MemGameData memGameData) {
        Log.d (TAG, "method insertMemGameData tries to insert a new MemGameData object into the database");

        boolean success = false;

        Log.d(TAG, "method insertMemGameData: creating ContentValues");
        ContentValues values = memGameDataToContentValues(memGameData);

        DatabaseWrapper databaseWrapper = Shared.databaseWrapper;
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        try {
            if (database != null) {
                long rowID = database.insert(TABLE_NAME, "null", values);
                Log.d(TAG, "method insertMemGameData: Inserted new MemGameData into rowID: " + rowID);
                success = true;
            }
        } catch (SQLiteException sqlex) {
            Log.e(TAG, "method insertMemGameData: Failed to insert MemGameData[" + memGameData.getGameStartTimestamp() + "] due to: " + sqlex);
            sqlex.printStackTrace();
        } finally {
            if (database != null) {
                database.close();
            }
        }
        return success;
    }

    //method memGameDataToContentValues packs a MemGame object into a ContentValues map for
    //use with SQL inserts
    private static ContentValues memGameDataToContentValues (MemGameData memGameData) {
        Log.d (TAG, "private method memGameDataToContentValues");
        ContentValues values = new ContentValues();

        values.put (COLUMN_PLAYER_USERNAME, memGameData.getUserPlayingName());
        values.put (COLUMN_GAME_START_TIMESTAMP, memGameData.getGameStartTimestamp());
        values.put (COLUMN_THEME_ID, memGameData.getThemeID());
        values.put (COLUMN_DIFFICULTY, memGameData.getGameDifficulty());
        values.put (COLUMN_GAME_DURATION_ALLOCATED, memGameData.getGameDurationAllocated());

        if (!memGameData.getMixerState()) values.put (COLUMN_MIXER_STATE, 0);
        else values.put (COLUMN_MIXER_STATE, 1);
        if (!memGameData.isGameStarted()) values.put (COLUMN_GAME_STARTED, 0);
        else values.put (COLUMN_GAME_STARTED, 1);

        StringBuilder gamePlayDurationsString = new StringBuilder();
        for (Long elementInGamePlayDurationsArrayList : memGameData.getGamePlayDurations()) {
            gamePlayDurationsString.append(elementInGamePlayDurationsArrayList);
            gamePlayDurationsString.append(DELIMITER);
            }
        values.put (COLUMN_GAME_PLAY_DURATIONS, gamePlayDurationsString.toString());

        StringBuilder turnDurationsString = new StringBuilder();
        for (Long elementInTurnDurationsArrayList : memGameData.getTurnDurationsArray()) {
            turnDurationsString.append(elementInTurnDurationsArrayList);
            turnDurationsString.append(DELIMITER);
        }
        values.put (COLUMN_TURN_DURATIONS, turnDurationsString.toString());

        StringBuilder cardSelectionOrderString = new StringBuilder();
        for (Integer elementInCardSelectionOrderArrayList : memGameData.getCardsSelectedArray()) {
            //Log.d (TAG, "*******: method memGameDataToContentValues: iterating on memGameData.getCardsSelectedArray: elementInCardSelectionOrderArrayList: " + elementInCardSelectionOrderArrayList);
            cardSelectionOrderString.append(elementInCardSelectionOrderArrayList);
            cardSelectionOrderString.append(DELIMITER);
        }
        Log.d (TAG, "******** setting cardSelectionOrderString: " + cardSelectionOrderString);
        values.put (COLUMN_CARD_SELECTED_ORDER, cardSelectionOrderString.toString());

        values.put (COLUMN_NUM_TURNS_TAKEN_IN_GAME, memGameData.getNumTurnsTaken());
        return values;
    }


    //method cursorToUserData populates a UserData object with data from the cursor
    private static MemGameData cursorToMemGameData (Cursor cursor) {
        Log.d (TAG, "method cursorToMemGameData");
        MemGameData cursorAtMemGameData = new MemGameData();

        cursorAtMemGameData.setUserPlayingName(cursor.getString(cursor.getColumnIndex(COLUMN_PLAYER_USERNAME)));
        cursorAtMemGameData.setGameStartTimestamp ((long) cursor.getInt(cursor.getColumnIndex(COLUMN_GAME_START_TIMESTAMP)));
        cursorAtMemGameData.setThemeID(cursor.getInt(cursor.getColumnIndex(COLUMN_THEME_ID)));
        cursorAtMemGameData.setGameDifficulty(cursor.getInt(cursor.getColumnIndex(COLUMN_DIFFICULTY)));
        cursorAtMemGameData.setGameDurationAllocated((long) cursor.getInt(cursor.getColumnIndex(COLUMN_GAME_DURATION_ALLOCATED)));

        if (cursor.getInt(cursor.getColumnIndex(COLUMN_MIXER_STATE)) == 1) { cursorAtMemGameData.setMixerState(true); }
        else if (cursor.getInt(cursor.getColumnIndex(COLUMN_MIXER_STATE)) == 0) { cursorAtMemGameData.setMixerState(false); }
        else {
            Log.d (TAG, "ERROR: method cursorAtMemGameData: mixerState: " + cursor.getInt(cursor.getColumnIndex(COLUMN_MIXER_STATE)));
        }
        if (cursor.getInt(cursor.getColumnIndex(COLUMN_GAME_STARTED)) == 1) { cursorAtMemGameData.setGameStarted(true); }
        else if (cursor.getInt(cursor.getColumnIndex(COLUMN_GAME_STARTED)) == 0) { cursorAtMemGameData.setGameStarted(false); }
        else {
            Log.d (TAG, "ERROR: method cursorAtMemGameData: isGameStarted: " + cursor.getInt(cursor.getColumnIndex(COLUMN_GAME_STARTED)));
        }

        String gamePlayDurationsString = cursor.getString(cursor.getColumnIndex(COLUMN_GAME_PLAY_DURATIONS));
        for (String s : gamePlayDurationsString.split(DELIMITER)) cursorAtMemGameData.appendToGamePlayDurations(Long.valueOf(s));

        String turnDurationsString = cursor.getString(cursor.getColumnIndex(COLUMN_TURN_DURATIONS));
        //Log.d (TAG, "******** method cursorToMemGameData: turnDurationsString: " + turnDurationsString);
        for (String s : turnDurationsString.split(DELIMITER)) cursorAtMemGameData.appendToTurnDurations(Long.valueOf(s));

        String cardSelectionOrderString = cursor.getString(cursor.getColumnIndex(COLUMN_CARD_SELECTED_ORDER));
        Log.d (TAG, "******** method cursorToMemGameData: cardSelectionOrderString: " + cardSelectionOrderString);
        for (String s : cardSelectionOrderString.split(DELIMITER)) {
            cursorAtMemGameData.appendToCardsSelected(Integer.valueOf(s));
        }
        cursorAtMemGameData.setNumTurnsTaken(cursor.getInt(cursor.getColumnIndex(COLUMN_NUM_TURNS_TAKEN_IN_GAME)));
        return  cursorAtMemGameData;
    }
}