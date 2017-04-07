package ws.isak.memgamev.database;

import android.content.Context;
import android.util.Log;

import java.util.List;

import ws.isak.memgamev.common.CardData;

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

    private static final String COLUMN_CARD_SELECTED_ORDER_TYPE = "BLOB";
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


}
