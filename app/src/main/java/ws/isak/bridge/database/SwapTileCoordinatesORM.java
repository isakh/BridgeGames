package ws.isak.bridge.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.widget.Toast;

import ws.isak.bridge.common.Shared;
import ws.isak.bridge.utils.SwapTileCoordinates;

/*
 *
 *
 * @author isak
 */

public class SwapTileCoordinatesORM {

    private static final String TAG = "SwapTileCoordsORM";

    private static final String TABLE_NAME = "swap_tile_coords";

    private static final String COMMA_SEP = ", ";

    private static final String COLUMN_SWAP_TILE_COORDS_KEY_TYPE = "REAL PRIMARY KEY";
    private static final String COLUMN_SWAP_TILE_COORDS_KEY = "coordsIDKey";

    private static final String COLUMN_COORDS_ROW_TYPE = "INTEGER";
    private static final String COLUMN_COORDS_ROW = "row";

    private static final String COLUMN_COORDS_COL_TYPE = "INTEGER";
    private static final String COLUMN_COORDS_COL = "col";

    public static final String SQL_CREATE_TABLE = "CREATE TABLE " +
            TABLE_NAME +
            " (" +
            COLUMN_SWAP_TILE_COORDS_KEY + " " + COLUMN_SWAP_TILE_COORDS_KEY_TYPE + COMMA_SEP +
            COLUMN_COORDS_ROW + " " + COLUMN_COORDS_ROW_TYPE + COMMA_SEP +
            COLUMN_COORDS_COL + " " + COLUMN_COORDS_COL_TYPE +
            ")";

    public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;


// ===========================================================================================

    public static boolean swapTileCoordsRecordsInDatabase(Context context) {
        //Log.d (TAG, "method swapTileCoordsRecordsInDatabase");

        DatabaseWrapper databaseWrapper = Shared.databaseWrapper;
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        boolean recordsExist = false;

        if (database != null) {
            Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME, null);
            Log.d(TAG, "method swapTileCoordsRecordsInDatabase: Checked " + cursor.getCount() + " SwapTileCoords records...");

            if (cursor.getCount() > 0) {
                recordsExist = true;
            }
            cursor.close();
        }
        database.close();
        return recordsExist;
    }

    //TODO these methods can be made generic if the TypeOfDataORM.TABLE_NAME is passed in
    public static int numSwapTileCoordsRecordsInDatabase(Context context) {
        //Log.d (TAG, "method numSwapTileCoordsRecordsInDatabase");

        DatabaseWrapper databaseWrapper = Shared.databaseWrapper;
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        int numRecords = 0;

        if (database != null) {
            Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME, null);
            Log.d(TAG, "method numSwapTileCoordsRecordsInDatabase: There are: " + cursor.getCount() + " SwapTileCoords records...");

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

    //method isSwapTileCoordsInDB takes a SwapTileCoords object and checks whether it has been used as a
    //primary key yet in the database - used to check existence and uniqueness when building BoardMaps
    public static boolean isSwapTileCoordsInDB(SwapTileCoordinates coordsToCheck) {
        Log.d(TAG, "method isSwapTileCoordsInDB: check tileCoords: " + coordsToCheck);
        DatabaseWrapper databaseWrapper = Shared.databaseWrapper;
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        boolean coordsExist = false;     //false unless found in database
        Float tileCoords = coordsToCheck.getSwapTileCoordsID();
        if (database != null) {
            Log.d(TAG, "method isSwapTileCoordsInDB: searching...");
            Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_SWAP_TILE_COORDS_KEY + " ='" + tileCoords + "'", null);
            //FIXME - to prevent SQL injection: Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_SWAP_TILE_COORDS_KEY + " =?", tileCoords);
            if (cursor.getCount() > 0) {
                coordsExist = true;
            } else {
                Toast.makeText(Shared.context, "new tile coordinates added to database", Toast.LENGTH_SHORT).show();
            }
            cursor.close();
            database.close();
        }
        return coordsExist;
    }

    public static SwapTileCoordinates getSwapTileCoords (float tileCoords) {
        Log.d(TAG, "method getSwapTileCoords returns a list of getSwapTileCoords objects with coords " + tileCoords);

        DatabaseWrapper databaseWrapper = Shared.databaseWrapper;
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        SwapTileCoordinates coordsToReturn = null;

        if (database != null) {
            Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_SWAP_TILE_COORDS_KEY + " ='" + tileCoords + "'", null);
            //FIXME - prevent SQL injection
            Log.d(TAG, "method getSwapCardData: Loaded " + cursor.getCount() + " SwapCardData records... this should always only be 1");
            if (swapTileCoordsRecordsInDatabase(Shared.context)) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    SwapTileCoordinates swapCoordsAtCursor = cursorToSwapTileCoords(cursor);
                    //Check the state of all MatchCardData fields here
                    Log.d(TAG, "... PARSE: SwapCardID object to return:" +
                            " | coordsKey: " + swapCoordsAtCursor.getSwapTileCoordsID() +
                            " | row: " + swapCoordsAtCursor.getSwapCoordRow() +
                            " | col: " + swapCoordsAtCursor.getSwapCoordCol());
                    coordsToReturn = swapCoordsAtCursor;
                    cursor.moveToNext();
                }
                cursor.close();
                database.close();
            }
        }
        return coordsToReturn;
    }


    public static boolean insertSwapTileCoords (SwapTileCoordinates tileCoords) {
        Log.d(TAG, "method insertSwapCardID tries to insert a new SwapTileCoordinates object into the database");

        boolean success = false;

        Log.d(TAG, "method insertSwapTileCoords: creating ContentValues");
        ContentValues values = swapCoordsToContentValues(tileCoords);

        DatabaseWrapper databaseWrapper = Shared.databaseWrapper;
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        try {
            if (database != null) {
                long rowID = database.insert(TABLE_NAME, "null", values);
                Log.d(TAG, "method insertSwapTileCoords: Inserted new SwapTileCoordinates into rowID: " + rowID);
                success = true;
            }
        } catch (SQLiteException sqlex) {
            Log.e(TAG, "method insertSwapTileCoords: Failed to insert SwapTileCoordinates[" + tileCoords.getSwapTileCoordsID() + "] due to: " + sqlex);
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
    private static ContentValues swapCoordsToContentValues(SwapTileCoordinates tileCoordinates) {
        Log.d(TAG, "private method swapCardIDToContentValues");
        ContentValues values = new ContentValues();

        values.put(COLUMN_SWAP_TILE_COORDS_KEY, tileCoordinates.getSwapTileCoordsID());
        values.put(COLUMN_COORDS_ROW, tileCoordinates.getSwapCoordRow());
        values.put(COLUMN_COORDS_COL, tileCoordinates.getSwapCoordCol());

        return values;
    }


    //method cursorToUserData populates a UserData object with data from the cursor
    private static SwapTileCoordinates cursorToSwapTileCoords( Cursor cursor) {
        Log.d(TAG, "method cursorToSwapCardID");
        SwapTileCoordinates cursorAtSwapCoords = new SwapTileCoordinates(-1, -1);     //FIXME - is there a better way to null this constructor?

        cursorAtSwapCoords.setSwapTileCoordsID(cursor.getFloat(cursor.getColumnIndex(COLUMN_SWAP_TILE_COORDS_KEY)));
        cursorAtSwapCoords.setSwapCoordRow(cursor.getInt(cursor.getColumnIndex(COLUMN_COORDS_ROW)));
        cursorAtSwapCoords.setSwapCoordCol(cursor.getInt(cursor.getColumnIndex(COLUMN_COORDS_COL)));

        return cursorAtSwapCoords;
    }
}