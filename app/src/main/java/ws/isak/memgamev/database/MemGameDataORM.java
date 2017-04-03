package ws.isak.memgamev.database;

import android.content.Context;
import android.util.Log;

/*
 *
 * @author isak
 */

public class MemGameDataORM {

    private static final String TAG="Class: MemGameDataORM";

    private static final String TABLE_NAME="mem_game_data";
    private static final String COMMA_SEP= ", ";

    private static final String COLUMN_THEME_ID_TYPE = "INTEGER";
    private static final String COLUMN_THEME_ID = "themeID";

    public static final String SQL_CREATE_TABLE = "CREATE TABLE " +
            TABLE_NAME +
            " (" +
            COLUMN_THEME_ID +
            " " +
            COLUMN_THEME_ID_TYPE +
            ")";

    public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    //===============================================================================


}
