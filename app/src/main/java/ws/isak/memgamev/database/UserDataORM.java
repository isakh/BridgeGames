package ws.isak.memgamev.database;

import java.util.List;

import android.content.Context;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.util.Log;

import ws.isak.memgamev.common.DatabaseWrapper;
import ws.isak.memgamev.common.UserData;

/*
 *
 * @author isak
 */

public class UserDataORM {

    private static final String TAG="Class: UserDataORM";

    private static final String TABLE_NAME = "user_data";

    private static final String COMMA_SEP = ", ";

    private static final String COLUMN_ID_TYPE = "INTEGER PRIMARY KEY";
    private static final String COLUMN_ID = "id";

    public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + COLUMN_ID + ")";

    public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    //==================================================================================

    //method getUserData fetches the full list of UserData objects in the local Database
    public static List <UserData> getUserData (Context context) {
        Log.d (TAG, "method getUserData returns a list of UserData objects");

        DatabaseWrapper databaseWrapper = new DatabaseWrapper(context);
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        List <UserData> userDataList = null;

        //TODO all the work :)

        return  userDataList;
    }

    //method findUserDataByID identifies and returns a single UserData object based on ID
    public static UserData findUserDataByID (Context context, int userId) {
        Log.d (TAG, "method findUserDataByID: userId: " + userId);
        DatabaseWrapper databaseWrapper = new DatabaseWrapper(context);
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        UserData userData = null;

        //TODO all the work :)

        return userData;
    }

    //method insertUserData inserts a new UserData object into the database if it doesn't already exist
    public static boolean insertUserData (Context context, UserData userData) {
        Log.d (TAG, "method insertUserData tries to insert a new UserData object into the database");
        if (findUserDataByID(context, userData.getHearIsSeeLikert()) != null) { //FIXME! this should be an int but and ID rather than random
            Log.d (TAG, "userData already exists in database, not inserting");
            return updateUserData (context , userData);
        }

        //TODO figure out ContentValues?
        ContentValues values = userDataToContentValues (userData);

        DatabaseWrapper databaseWrapper = new DatabaseWrapper(context);
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        boolean success = false;

        //TODO all the work :)

        return success;
    }

    //method updateUserData updates an existing UserData in the database
    public static boolean updateUserData (Context context, UserData userData) {
        Log.d (TAG, "method updateUserData");
        ContentValues values = userDataToContentValues (userData);
        DatabaseWrapper databaseWrapper = new DatabaseWrapper(context);
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        boolean success = false;

        //TODO all the work :)

        return success;
    }

    //method userDataToContentValues packs a UserData object into a ContentValues map for
    //use with SQL inserts
    private static ContentValues userDataToContentValues (UserData userData) {
        Log.d (TAG, "private method userDataToContentValues");
        ContentValues values = new ContentValues();

        //TODO all the work :)

        return values;
    }

    //method cursorToUserData populates a UserData object with data from the cursor
    private static UserData cursorToUserData (Cursor cursor) {
        Log.d (TAG, "method cursorToUserData");
        UserData userData = new UserData();

        //TODO all the work :)

        return  userData;
    }
}
