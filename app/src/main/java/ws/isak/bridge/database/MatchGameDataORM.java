package ws.isak.bridge.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;


import java.util.ArrayList;

import ws.isak.bridge.common.Shared;
import ws.isak.bridge.model.MatchGameData;

/*
 *
 * @author isak
 */

public class MatchGameDataORM {

    private static final String TAG="MatchGameDataORM";

    private static final String DELIMITER=", ";

    private static final String TABLE_NAME="match_game_data";
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

    private static final String COLUMN_CARD_SELECTED_ORDER_TYPE = "STRING";       //FIXME is BLOB ok since this is an array of MatchCardData objects?
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

    // method recordsInDatabase returns true if there are records of type MatchGameData
    // in the DB, otherwise, false
    public static boolean matchGameRecordsInDatabase (Context context) {
        //Log.d (TAG, "method matchGameRecordsInDatabase");

        DatabaseWrapper databaseWrapper = Shared.databaseWrapper;
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        boolean recordsExist = false;

        if (database != null) {
            Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME, null);     //FIXME was MatchGameDataORM.TABLE_NAME
            Log.d(TAG, "method recordsInDatabase: Checked " + cursor.getCount() + " MatchGameData records...");

            if (cursor.getCount() > 0) {
                recordsExist = true;
            }
            cursor.close();
        }
        database.close();
        return recordsExist;
    }


    // method numRecordsInDatabase returns the number of records in the MatchGameDataORM table
    // in the DB, otherwise, false
    public static int numMemGameRecordsInDatabase (Context context) {
        //Log.d (TAG, "method numMemGameRecordsInDatabase");

        DatabaseWrapper databaseWrapper = Shared.databaseWrapper;
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        int numRecords = 0;

        if (database != null) {
            Cursor cursor = database.rawQuery("SELECT * FROM " + MatchGameDataORM.TABLE_NAME, null);
            Log.d(TAG, "method numMemGameRecordsInDatabase: There are: " + cursor.getCount() + " MatchGameData records...");

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

    //return all of the MatchGameData rows for the targetUser, sorted by TimeStamp
    public static ArrayList<MatchGameData> getMemGameData (String targetUser) {
        Log.d (TAG, "method getMemGameData returns a list of MatchGameData objects with type " + targetUser);

        DatabaseWrapper databaseWrapper =  Shared.databaseWrapper;
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        ArrayList <MatchGameData> matchGameDataList = null;

        if (database !=  null) {
        Cursor cursor = database.rawQuery("SELECT * FROM " + MatchGameDataORM.TABLE_NAME + " WHERE " + MatchGameDataORM.COLUMN_PLAYER_USERNAME + " ='" + targetUser + "'", null);
            //FIXME - this prevent's SQL injection: Cursor cursor = database.rawQuery("SELECT * FROM " + UserDataORM.TABLE_NAME + " WHERE " + UserDataORM.COLUMN_USER_NAME_ID + " =?", userName);

            Log.d (TAG, "method getUserData: Loaded " + cursor.getCount() + " UserData records...");
            if (matchGameRecordsInDatabase(Shared.context)) {
                matchGameDataList = new ArrayList<MatchGameData>(numMemGameRecordsInDatabase(Shared.context));
                cursor.moveToFirst();
                int rowCount = 0;
                while (!cursor.isAfterLast()) {
                    MatchGameData matchGameDataAtCursor = cursorToMemGameData(cursor);
                    //Check the state of all MatchGameData fields here
                    Log.d (TAG, "... PARSE: Database row: " + rowCount +
                                " | gameStartTimestamp: " + matchGameDataAtCursor.getGameStartTimestamp() +
                                " | playerUserName: " + matchGameDataAtCursor.getUserPlayingName() +
                                " | themeID: " + matchGameDataAtCursor.getThemeID() +
                                " | difficulty: " + matchGameDataAtCursor.getGameDifficulty() +
                                " | gameDurationAllocated: " + matchGameDataAtCursor.getGameDurationAllocated() +
                                " | mixerState: " + matchGameDataAtCursor.getMixerState() +
                                " | gameStarted: " + matchGameDataAtCursor.isGameStarted() +
                                " | numTurnsTakenInGame: "+ matchGameDataAtCursor.getNumTurnsTaken());
                    if (matchGameDataAtCursor.sizeOfPlayDurationsArray() != matchGameDataAtCursor.sizeOfTurnDurationsArray()) {
                        //TODO extend to compare with cardObjects array size as well
                        Log.d (TAG, " ***** ERROR! Size of play durations and turn durations not returned as equal");
                    }
                    for (int i = 0; i < matchGameDataAtCursor.sizeOfPlayDurationsArray(); i++) {
                        Log.d(TAG, " ... PARSE ARRAYS in Database row " + rowCount +
                                " | current array element i: " + i +
                                " | gamePlayDuration(i): " + matchGameDataAtCursor.queryGamePlayDurations(i) +
                                " | turnDurations(i): " + matchGameDataAtCursor.queryTurnDurationsArray(i) +
                                " | cardSelectedOrder: " + matchGameDataAtCursor.queryCardsSelectedArray(i));
                    }
                    matchGameDataList.add(matchGameDataAtCursor);
                    Log.d (TAG, "!!! userDataList.get(rowCount) userData Object @: " + matchGameDataList.get(rowCount));
                    //move cursor to start of next row
                    rowCount++;
                    cursor.moveToNext();
                }
                cursor.close();
                database.close();
            }
        }
        return matchGameDataList;
    }

    //method insertMemGameData inserts a new UserData object into the database if it doesn't already exist
    public static boolean insertMemGameData (MatchGameData matchGameData) {
        Log.d (TAG, "method insertMemGameData tries to insert a new MatchGameData object into the database");

        boolean success = false;

        Log.d(TAG, "method insertMemGameData: creating ContentValues");
        ContentValues values = matchGameDataToContentValues(matchGameData);

        DatabaseWrapper databaseWrapper = Shared.databaseWrapper;
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        try {
            if (database != null) {
                long rowID = database.insert(TABLE_NAME, "null", values);
                Log.d(TAG, "method insertMemGameData: Inserted new MatchGameData into rowID: " + rowID);
                success = true;
            }
        } catch (SQLiteException sqlex) {
            Log.e(TAG, "method insertMemGameData: Failed to insert MatchGameData[" + matchGameData.getGameStartTimestamp() + "] due to: " + sqlex);
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
    private static ContentValues matchGameDataToContentValues (MatchGameData matchGameData) {
        Log.d (TAG, "private method matchGameDataToContentValues");
        ContentValues values = new ContentValues();

        values.put (COLUMN_PLAYER_USERNAME, matchGameData.getUserPlayingName());
        values.put (COLUMN_GAME_START_TIMESTAMP, matchGameData.getGameStartTimestamp());
        values.put (COLUMN_THEME_ID, matchGameData.getThemeID());
        values.put (COLUMN_DIFFICULTY, matchGameData.getGameDifficulty());
        values.put (COLUMN_GAME_DURATION_ALLOCATED, matchGameData.getGameDurationAllocated());

        if (!matchGameData.getMixerState()) values.put (COLUMN_MIXER_STATE, 0);
        else values.put (COLUMN_MIXER_STATE, 1);
        if (!matchGameData.isGameStarted()) values.put (COLUMN_GAME_STARTED, 0);
        else values.put (COLUMN_GAME_STARTED, 1);

        StringBuilder gamePlayDurationsString = new StringBuilder();
        for (Long elementInGamePlayDurationsArrayList : matchGameData.getGamePlayDurations()) {
            gamePlayDurationsString.append(elementInGamePlayDurationsArrayList);
            gamePlayDurationsString.append(DELIMITER);
            }
        values.put (COLUMN_GAME_PLAY_DURATIONS, gamePlayDurationsString.toString());

        StringBuilder turnDurationsString = new StringBuilder();
        for (Long elementInTurnDurationsArrayList : matchGameData.getTurnDurationsArray()) {
            turnDurationsString.append(elementInTurnDurationsArrayList);
            turnDurationsString.append(DELIMITER);
        }
        values.put (COLUMN_TURN_DURATIONS, turnDurationsString.toString());

        StringBuilder cardSelectionOrderString = new StringBuilder();
        for (Integer elementInCardSelectionOrderArrayList : matchGameData.getCardsSelectedArray()) {
            //Log.d (TAG, "*******: method matchGameDataToContentValues: iterating on matchGameData.getCardsSelectedArray: elementInCardSelectionOrderArrayList: " + elementInCardSelectionOrderArrayList);
            cardSelectionOrderString.append(elementInCardSelectionOrderArrayList);
            cardSelectionOrderString.append(DELIMITER);
        }
        Log.d (TAG, "******** setting cardSelectionOrderString: " + cardSelectionOrderString);
        values.put (COLUMN_CARD_SELECTED_ORDER, cardSelectionOrderString.toString());

        values.put (COLUMN_NUM_TURNS_TAKEN_IN_GAME, matchGameData.getNumTurnsTaken());
        return values;
    }


    //method cursorToUserData populates a UserData object with data from the cursor
    private static MatchGameData cursorToMemGameData (Cursor cursor) {
        Log.d (TAG, "method cursorToMemGameData");
        MatchGameData cursorAtMatchGameData = new MatchGameData();

        cursorAtMatchGameData.setUserPlayingName(cursor.getString(cursor.getColumnIndex(COLUMN_PLAYER_USERNAME)));
        cursorAtMatchGameData.setGameStartTimestamp ((long) cursor.getInt(cursor.getColumnIndex(COLUMN_GAME_START_TIMESTAMP)));
        cursorAtMatchGameData.setThemeID(cursor.getInt(cursor.getColumnIndex(COLUMN_THEME_ID)));
        cursorAtMatchGameData.setGameDifficulty(cursor.getInt(cursor.getColumnIndex(COLUMN_DIFFICULTY)));
        cursorAtMatchGameData.setGameDurationAllocated((long) cursor.getInt(cursor.getColumnIndex(COLUMN_GAME_DURATION_ALLOCATED)));

        if (cursor.getInt(cursor.getColumnIndex(COLUMN_MIXER_STATE)) == 1) { cursorAtMatchGameData.setMixerState(true); }
        else if (cursor.getInt(cursor.getColumnIndex(COLUMN_MIXER_STATE)) == 0) { cursorAtMatchGameData.setMixerState(false); }
        else {
            Log.d (TAG, "ERROR: method cursorAtMatchGameData: mixerState: " + cursor.getInt(cursor.getColumnIndex(COLUMN_MIXER_STATE)));
        }
        if (cursor.getInt(cursor.getColumnIndex(COLUMN_GAME_STARTED)) == 1) { cursorAtMatchGameData.setGameStarted(true); }
        else if (cursor.getInt(cursor.getColumnIndex(COLUMN_GAME_STARTED)) == 0) { cursorAtMatchGameData.setGameStarted(false); }
        else {
            Log.d (TAG, "ERROR: method cursorAtMatchGameData: isGameStarted: " + cursor.getInt(cursor.getColumnIndex(COLUMN_GAME_STARTED)));
        }

        String gamePlayDurationsString = cursor.getString(cursor.getColumnIndex(COLUMN_GAME_PLAY_DURATIONS));
        for (String s : gamePlayDurationsString.split(DELIMITER)) cursorAtMatchGameData.appendToGamePlayDurations(Long.valueOf(s));

        String turnDurationsString = cursor.getString(cursor.getColumnIndex(COLUMN_TURN_DURATIONS));
        //Log.d (TAG, "******** method cursorToMemGameData: turnDurationsString: " + turnDurationsString);
        for (String s : turnDurationsString.split(DELIMITER)) cursorAtMatchGameData.appendToTurnDurations(Long.valueOf(s));

        String cardSelectionOrderString = cursor.getString(cursor.getColumnIndex(COLUMN_CARD_SELECTED_ORDER));
        Log.d (TAG, "******** method cursorToMemGameData: cardSelectionOrderString: " + cardSelectionOrderString);
        for (String s : cardSelectionOrderString.split(DELIMITER)) {
            cursorAtMatchGameData.appendToCardsSelected(Integer.valueOf(s));
        }
        cursorAtMatchGameData.setNumTurnsTaken(cursor.getInt(cursor.getColumnIndex(COLUMN_NUM_TURNS_TAKEN_IN_GAME)));
        return cursorAtMatchGameData;
    }
}