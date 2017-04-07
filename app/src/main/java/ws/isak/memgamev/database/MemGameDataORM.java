package ws.isak.memgamev.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.List;

import ws.isak.memgamev.common.CardData;
import ws.isak.memgamev.common.UserData;
import ws.isak.memgamev.model.Game;
import ws.isak.memgamev.model.MemGameData;

/*
 *
 * @author isak
 */

public class MemGameDataORM {

    private static final String TAG="Class: MemGameDataORM";

    private static final String TABLE_NAME="mem_game_data";
    private static final String COMMA_SEP= ", ";

    //FIXME should this be the key? or do we need another approach
    private static final String COLUMN_GAME_START_TIMESTAMP_TYPE = "INTEGER PRIMARY KEY";
    private static final String COLUMN_GAME_START_TIMESTAMP = "gameStartTimestamp";

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

    private static final String COLUMN_GAME_PLAY_DURATIONS_TYPE = "BLOB";       //FIXME, maybe STRING? see http://stackoverflow.com/a/34767584/1443674
    private static final String COLUMN_GAME_PLAY_DURATIONS = "gamePlayDurations";

    private static final String COLUMN_TURN_DURATIONS_TYPE = "BLOB";
    private static final String COLUMN_TURN_DURATIONS = "turnDurations";

    private static final String COLUMN_CARD_SELECTED_ORDER_TYPE = "BLOB";       //FIXME is BLOB ok since this is an array of CardData objects?
    private static final String COLUMN_CARD_SELECTED_ORDER = "cardSelectedOrder";

    private static final String COLUMN_NUM_TURNS_TAKEN_IN_GAME_TYPE = "INTEGER";
    private static final String COLUMN_NUM_TURNS_TAKEN_IN_GAME = "numTurnsTakenInGame";

    public static final String SQL_CREATE_TABLE = "CREATE TABLE " +
            TABLE_NAME +
            " (" +
            COLUMN_GAME_START_TIMESTAMP + " " + COLUMN_GAME_START_TIMESTAMP_TYPE +
            COLUMN_THEME_ID + " " + COLUMN_THEME_ID_TYPE +
            COLUMN_DIFFICULTY + " " + COLUMN_DIFFICULTY_TYPE +
            COLUMN_GAME_DURATION_ALLOCATED + " " + COLUMN_GAME_DURATION_ALLOCATED_TYPE +
            COLUMN_MIXER_STATE + " " + COLUMN_MIXER_STATE_TYPE +
            COLUMN_GAME_STARTED + " " + COLUMN_GAME_STARTED_TYPE +
            COLUMN_GAME_PLAY_DURATIONS + " " + COLUMN_GAME_PLAY_DURATIONS_TYPE +
            COLUMN_TURN_DURATIONS + " " + COLUMN_TURN_DURATIONS_TYPE +
            COLUMN_CARD_SELECTED_ORDER + " " + COLUMN_CARD_SELECTED_ORDER_TYPE +
            COLUMN_NUM_TURNS_TAKEN_IN_GAME + " " + COLUMN_NUM_TURNS_TAKEN_IN_GAME_TYPE +
            ")";

    public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    //===============================================================================



    //method userDataToContentValues packs a UserData object into a ContentValues map for
    //use with SQL inserts
    private static ContentValues memGameDataToContentValues (MemGameData memGameData) {
        Log.d (TAG, "private method memGameDataToContentValues");
        ContentValues values = new ContentValues();

        values.put (COLUMN_GAME_START_TIMESTAMP, memGameData.getGameStartTimestamp());
        values.put (COLUMN_THEME_ID, memGameData.getThemeID());
        values.put (COLUMN_DIFFICULTY, memGameData.getGameDifficulty());
        values.put (COLUMN_GAME_DURATION_ALLOCATED, memGameData.getGameDurationAllocated());

        if (!memGameData.getMixerState()) { values.put (COLUMN_MIXER_STATE, 0);}        //TODO Check logic
        else{ values.put (COLUMN_MIXER_STATE, 1); }
        if (!memGameData.isGameStarted()) { values.put (COLUMN_GAME_STARTED, 0); }      //TODO Check logic
        else { values.put (COLUMN_GAME_STARTED, 1); }

        values.put (COLUMN_GAME_PLAY_DURATIONS, "WHAT GOES HERE");      //TODO SOLVE SERIALIZATION? OR OTHERWISE
        values.put (COLUMN_TURN_DURATIONS, "WHAT GOES HERE");           //TODO SOLVE SERIALIZATION? OR OTHERWISE
        values.put (COLUMN_CARD_SELECTED_ORDER, "WHAT GOES HERE");      //TODO SOLVE SERIALIZATION? OR OTHERWISE

        values.put (COLUMN_NUM_TURNS_TAKEN_IN_GAME, memGameData.getNumTurnsTaken());
        return values;
    }


    //method cursorToUserData populates a UserData object with data from the cursor
    private static MemGameData cursorToMemGameData (Cursor cursor) {
        Log.d (TAG, "method cursorToMemGameData");
        Game gameToLoad;                                            //FIXME do we need an empty Game object instantiated?
        MemGameData cursorAtMemGameData = new MemGameData(gameToLoad); //FIXME write gameToLoad method for empty game

        cursorAtMemGameData.setGameStartTimestamp ((long) cursor.getInt(cursor.getColumnIndex(COLUMN_GAME_START_TIMESTAMP)));
        cursorAtMemGameData.setThemeID(cursor.getInt(cursor.getColumnIndex(COLUMN_THEME_ID)));         //TODO fix setTheme so doesn't need mPlayingGame
        cursorAtMemGameData.setGameDifficulty(cursor.getInt(cursor.getColumnIndex(COLUMN_DIFFICULTY)));//TODO fix setTheme so doesn't need mPlayingGame
        cursorAtMemGameData.setGameDurationAllocated((long) cursor.getInt(cursor.getColumnIndex(COLUMN_GAME_DURATION_ALLOCATED)));

        if (cursor.getInt(cursor.getColumnIndex(COLUMN_MIXER_STATE)) == 1) { cursorAtMemGameData.setMixerState(true); }
        else if (cursor.getInt(cursor.getColumnIndex(COLUMN_MIXER_STATE)) == 0) { cursorAtMemGameData.setMixerState(false); }
        else {
            Log.d (TAG, "ERROR: method cursorAtMemGameData: mixerState: " + cursor.getInt(cursor.getColumnIndex(COLUMN_MIXER_STATE)));
        }
        if (cursor.getInt(cursor.getColumnIndex(COLUMN_GAME_STARTED)) == 1) { cursorAtMemGameData.isGameStarted(true); }
        else if (cursor.getInt(cursor.getColumnIndex(COLUMN_GAME_STARTED)) == 0) { cursorAtMemGameData.isGameStarted(false); }
        else {
            Log.d (TAG, "ERROR: method cursorAtMemGameData: isGameStarted: " + cursor.getInt(cursor.getColumnIndex(COLUMN_GAME_STARTED)));
        }

        //TODO figure out how to pull back in Lists for COLUMN_GAME_PLAY_DURATIONS
        //TODO figure out how to pull back in Lists for COLUMN_TURN_DURATIONS
        //TODO figure out how to pull back in Lists for COLUMN_CARD_SELECTED_ORDER

        cursorAtMemGameData.setNumTurnsTaken(cursor.getInt(cursor.getColumnIndex(COLUMN_NUM_TURNS_TAKEN_IN_GAME)));
        return  cursorAtMemGameData;
    }
}