package ws.isak.memgamev.database;

import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.content.Context;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.util.Log;

import ws.isak.memgamev.common.Shared;
import ws.isak.memgamev.common.UserData;

/*
 *
 * @author isak
 */

public class UserDataORM {

    private static final String TAG="Class: UserDataORM";

    private static final String TABLE_NAME = "user_data";

    private static final String COMMA_SEP = ", ";

    //FIXME construct userNameId from input userName and passWord
    private static final String COLUMN_USER_NAME_ID_TYPE = "TEXT PRIMARY KEY";
    private static final String COLUMN_USER_NAME_ID = "userNameID";


    private static final String COLUMN_AGE_RANGE_TYPE = "TEXT";
    private static final String COLUMN_AGE_RANGE = "ageRange";

    private static final String COLUMN_YEARS_TWITCHING_RANGE_TYPE = "TEXT";
    private static final String COLUMN_YEARS_TWITCHING_RANGE = "yearsTwitchingRange";

    private static final String COLUMN_SPECIES_KNOWN_RANGE_TYPE = "TEXT";
    private static final String COLUMN_SPECIES_KNOWN_RANGE = "speciesKnownRange";

    private static final String COLUMN_AUDIBLE_RECOGNIZED_RANGE_TYPE = "TEXT";
    private static final String COLUMN_AUDIBLE_RECOGNIZED_RANGE = "audibleRecognizedRange";

    private static final String COLUMN_INTERFACE_EXPERIENCE_RANGE_TYPE = "TEXT";
    private static final String COLUMN_INTERFACE_EXPERIENCE_RANGE = "interfaceExperienceRange";

    //TODO sqlite cannot deal with booleans: private static boolean hearingEqualsSeeing; becomes int
    private static final String COLUMN_HEARING_EQUALS_SEEING_TYPE = "INTEGER";
    private static final String COLUMN_HEARING_EQUALS_SEEING = "hearingEqualsSeeing";

    //TODO sqlite cannot deal with booleans: private static boolean hasUsedSmartPhone; becomes int
    private static final String COLUMN_HAS_USED_SMARTPHONE_TYPE = "INTEGER";
    private static final String COLUMN_HAS_USED_SMARTPHONE = "hasUsedSmartPhone";

    public static final String SQL_CREATE_TABLE = "CREATE TABLE " +
            TABLE_NAME +
            " (" +
            COLUMN_USER_NAME_ID + " " + COLUMN_USER_NAME_ID_TYPE + COMMA_SEP +
            COLUMN_AGE_RANGE + " " + COLUMN_AGE_RANGE_TYPE + COMMA_SEP +
            COLUMN_YEARS_TWITCHING_RANGE + " " + COLUMN_YEARS_TWITCHING_RANGE_TYPE + COMMA_SEP +
            COLUMN_SPECIES_KNOWN_RANGE + " " + COLUMN_SPECIES_KNOWN_RANGE_TYPE + COMMA_SEP +
            COLUMN_AUDIBLE_RECOGNIZED_RANGE + " " + COLUMN_AUDIBLE_RECOGNIZED_RANGE_TYPE + COMMA_SEP +
            COLUMN_INTERFACE_EXPERIENCE_RANGE + " " + COLUMN_INTERFACE_EXPERIENCE_RANGE_TYPE + COMMA_SEP +
            COLUMN_HEARING_EQUALS_SEEING + " " + COLUMN_HEARING_EQUALS_SEEING_TYPE + COMMA_SEP +
            COLUMN_HAS_USED_SMARTPHONE + " " + COLUMN_HAS_USED_SMARTPHONE_TYPE + COMMA_SEP +
            ")";

    public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    private static final SimpleDateFormat _dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ", Locale.ENGLISH);

    //==================================================================================

    //method getUserData fetches the full list of UserData objects in the local Database
    public static List <UserData> getUserData (Context context) {
        Log.d (TAG, "method getUserData returns a list of UserData objects");

        DatabaseWrapper databaseWrapper = Shared.databaseWrapper;   //FIXME is this right? or below
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        List <UserData> userDataList = null;

        if (database !=  null) {
            Cursor cursor = database.rawQuery("SELECT * FROM " + UserDataORM.TABLE_NAME, null);
            Log.d (TAG, "method getUserData: Loaded " + cursor.getCount() + " UserData records...");
            if (cursor.getCount() > 0) {
                userDataList = new ArrayList<UserData>();
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    UserData userData = cursorToUserData(cursor);
                    //TODO userData.getGames (MemGameDataORM.getGamesForUserData (context, userData));
                    userDataList.add(userData);
                    cursor.moveToNext();
                }
                Log.d (TAG, "method getUserData: UserData objects loaded successfully");
            }
            database.close();
        }
        return  userDataList;
    }

    //method findUserDataByID identifies and returns a single UserData object based on ID
    public static UserData findUserDataByID (Context context, String userId) {
        Log.d (TAG, "method findUserDataByID: userId: " + userId);
        DatabaseWrapper databaseWrapper = new DatabaseWrapper(context);     //FIXME is this right? or above
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        UserData userData = null;

        if (database != null) {
            Log.d (TAG, "method findUserDataById: Loading User: " + userId);
            Cursor cursor = database.rawQuery("SELECT * FROM " + UserDataORM.TABLE_NAME + " WHERE " + UserDataORM.COLUMN_USER_NAME_ID + " = " + userId, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                userData = cursorToUserData(cursor);
                Log.d(TAG, "method findUserDataByID: UserData loaded successfully");
            }
            database.close();
        }
        return userData;
    }

    //method insertUserData inserts a new UserData object into the database if it doesn't already exist
    public static boolean insertUserData (Context context, UserData userData) {
        Log.d (TAG, "method insertUserData tries to insert a new UserData object into the database");
        if (findUserDataByID(context, userData.getUserName()) != null) {
            Log.d (TAG, "userData already exists in database, not inserting");
            return updateUserData (context , userData);
        }

        ContentValues values = userDataToContentValues (userData);

        DatabaseWrapper databaseWrapper = new DatabaseWrapper(context);
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        boolean success = false;

        try {
            if (database != null) {
                long userDataID = database.insert(UserDataORM.TABLE_NAME, "null", values);
                Log.d (TAG, "method insertUserData: Inserted new UserData with ID: " + userDataID);
                // TODO iterate over GameDataORM.insertGames to set new Games in UserData
                success = true;
            }
        } catch (NullPointerException npe) {
            Log.e (TAG, "method insertUserData: Failed to insert UserData[" + userData.getUserName() + "] due to: " + npe);
        } finally {
            if (database != null) {
                database.close();
            }
        }
        return success;
    }

    //method updateUserData updates an existing UserData in the database
    public static boolean updateUserData (Context context, UserData userData) {
        Log.d (TAG, "method updateUserData");
        ContentValues values = userDataToContentValues (userData);
        DatabaseWrapper databaseWrapper = new DatabaseWrapper(context);
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        boolean success = false;
        try {
            if (database != null) {
                Log.d (TAG, "method updateUserData: Updating UserData[" + userData.getUserName() + "]...");
                database.update(UserDataORM.TABLE_NAME, values, UserDataORM.COLUMN_USER_NAME_ID + " = " + userData.getUserName(), null);
                success = true;
            }
        } catch (NullPointerException npe) {
            Log.e (TAG, "method updateUserData: Failed to update UserData[" + userData.getUserName() + "] due to: " npe);
        } finally {
            if (database != null) {
                database.close();
            }
        }
        return success;
    }

    //method userDataToContentValues packs a UserData object into a ContentValues map for
    //use with SQL inserts
    private static ContentValues userDataToContentValues (UserData userData) {
        Log.d (TAG, "private method userDataToContentValues");
        ContentValues values = new ContentValues();

        values.put (UserDataORM.COLUMN_USER_NAME_ID, userData.getUserName());
        values.put (UserDataORM.COLUMN_AGE_RANGE, userData.getAgeRange());
        values.put (UserDataORM.COLUMN_YEARS_TWITCHING_RANGE, userData.getYearsTwitchingRange());
        values.put (UserDataORM.COLUMN_SPECIES_KNOWN_RANGE, userData.getSpeciesKnownRange());
        values.put (UserDataORM.COLUMN_AUDIBLE_RECOGNIZED_RANGE, userData.getAudibleRecognizedRange());
        values.put (UserDataORM.COLUMN_INTERFACE_EXPERIENCE_RANGE, userData.getInterfaceExperienceRange());
        if (userData.getHearingEqualsSeeing() == false) {
            values.put (UserDataORM.COLUMN_HEARING_EQUALS_SEEING, 0);
        }
        else{
            values.put (UserDataORM.COLUMN_HEARING_EQUALS_SEEING, 1);
        }
        if (userData.getHasUsedSmartphone() == false) {
            values.put (UserDataORM.COLUMN_HAS_USED_SMARTPHONE, 0);
        }
        else {
            values.put (UserDataORM.COLUMN_HAS_USED_SMARTPHONE, 1);
        }
        return values;
    }

    //method cursorToUserData populates a UserData object with data from the cursor
    private static UserData cursorToUserData (Cursor cursor) {
        Log.d (TAG, "method cursorToUserData");
        UserData userData = new UserData();

        userData.setUserName(cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME_ID)));
        userData.setAgeRange(cursor.getString(cursor.getColumnIndex(COLUMN_AGE_RANGE)));
        userData.setYearsTwitchingRange(cursor.getString(cursor.getColumnIndex(COLUMN_YEARS_TWITCHING_RANGE)));
        userData.setSpeciesKnownRange(cursor.getString(cursor.getColumnIndex(COLUMN_SPECIES_KNOWN_RANGE)));
        userData.setAudibleRecognizedRange(cursor.getString(cursor.getColumnIndex(COLUMN_AUDIBLE_RECOGNIZED_RANGE)));
        userData.setInterfaceExperienceRange(cursor.getString(cursor.getColumnIndex(COLUMN_INTERFACE_EXPERIENCE_RANGE)));
        if (cursor.getInt(cursor.getColumnIndex(COLUMN_HEARING_EQUALS_SEEING)) == 1) { userData.setHearingEqualsSeeing(true); }
        if (cursor.getInt(cursor.getColumnIndex(COLUMN_HEARING_EQUALS_SEEING)) == 0) { userData.setHearingEqualsSeeing(false); }
        else {
            Log.d (TAG, "method cursorToUserData: hearingEqualsSeeing: " + cursor.getInt(cursor.getColumnIndex(COLUMN_HEARING_EQUALS_SEEING)));
        }
        if (cursor.getInt(cursor.getColumnIndex(COLUMN_HAS_USED_SMARTPHONE)) == 1) { userData.setHasUsedSmartPhone(true); }
        if (cursor.getInt(cursor.getColumnIndex(COLUMN_HAS_USED_SMARTPHONE)) == 0) { userData.setHasUsedSmartPhone(false); }
        else {
            Log.d (TAG, "method cursorToUserData: hasUsedSmartphone: " + cursor.getInt(cursor.getColumnIndex(COLUMN_HAS_USED_SMARTPHONE)));
        }
        return  userData;
    }
}