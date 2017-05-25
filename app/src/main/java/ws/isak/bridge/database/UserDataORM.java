package ws.isak.bridge.database;

import java.util.ArrayList;

import android.database.sqlite.SQLiteException;
import android.widget.Toast;
import android.content.Context;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.util.Log;

import ws.isak.bridge.common.Shared;
import ws.isak.bridge.common.UserData;

/*
 *
 * @author isak
 */

public class UserDataORM {

    private static final String TAG="UserDataORM";

    private static final String TABLE_NAME = "user_data";

    private static final String COMMA_SEP = ", ";

    //TODO construct userNameId from input userName and passWord, currently just userName
    private static final String COLUMN_USER_NAME_ID_TYPE = " TEXT PRIMARY KEY";
    private static final String COLUMN_USER_NAME_ID = "userNameID";


    private static final String COLUMN_AGE_RANGE_TYPE = " TEXT";
    private static final String COLUMN_AGE_RANGE = "ageRange";

    private static final String COLUMN_YEARS_TWITCHING_RANGE_TYPE = " TEXT";
    private static final String COLUMN_YEARS_TWITCHING_RANGE = "yearsTwitchingRange";

    private static final String COLUMN_SPECIES_KNOWN_RANGE_TYPE = " TEXT";
    private static final String COLUMN_SPECIES_KNOWN_RANGE = "speciesKnownRange";

    private static final String COLUMN_AUDIBLE_RECOGNIZED_RANGE_TYPE = " TEXT";
    private static final String COLUMN_AUDIBLE_RECOGNIZED_RANGE = "audibleRecognizedRange";

    private static final String COLUMN_INTERFACE_EXPERIENCE_RANGE_TYPE = " TEXT";
    private static final String COLUMN_INTERFACE_EXPERIENCE_RANGE = "interfaceExperienceRange";

    //sqlite cannot store booleans: private static boolean hearingEqualsSeeing; becomes int
    private static final String COLUMN_HEARING_EQUALS_SEEING_TYPE = " INTEGER";
    private static final String COLUMN_HEARING_EQUALS_SEEING = "hearingEqualsSeeing";

    //sqlite cannot store booleans: private static boolean hasUsedSmartPhone; becomes int
    private static final String COLUMN_HAS_USED_SMARTPHONE_TYPE = " INTEGER";
    private static final String COLUMN_HAS_USED_SMARTPHONE = "hasUsedSmartPhone";

    //store all of the state for the games previous scores
    //MATCH GAME STARS
    private static final String COLUMN_MATCH_GAME_THEME_1_DIFFICULTY_1_HIGH_STARS_TYPE = " INTEGER";
    private static final String COLUMN_MATCH_GAME_THEME_1_DIFFICULTY_1_HIGH_STARS = "matchGameTheme1Difficulty1HighStars";

    private static final String COLUMN_MATCH_GAME_THEME_1_DIFFICULTY_2_HIGH_STARS_TYPE = " INTEGER";
    private static final String COLUMN_MATCH_GAME_THEME_1_DIFFICULTY_2_HIGH_STARS = "matchGameTheme1Difficulty2HighStars";

    private static final String COLUMN_MATCH_GAME_THEME_1_DIFFICULTY_3_HIGH_STARS_TYPE = " INTEGER";
    private static final String COLUMN_MATCH_GAME_THEME_1_DIFFICULTY_3_HIGH_STARS = "matchGameTheme1Difficulty3HighStars";

    private static final String COLUMN_MATCH_GAME_THEME_2_DIFFICULTY_1_HIGH_STARS_TYPE = " INTEGER";
    private static final String COLUMN_MATCH_GAME_THEME_2_DIFFICULTY_1_HIGH_STARS = "matchGameTheme2Difficulty1HighStars";

    private static final String COLUMN_MATCH_GAME_THEME_2_DIFFICULTY_2_HIGH_STARS_TYPE = " INTEGER";
    private static final String COLUMN_MATCH_GAME_THEME_2_DIFFICULTY_2_HIGH_STARS = "matchGameTheme2Difficulty2HighStars";

    private static final String COLUMN_MATCH_GAME_THEME_2_DIFFICULTY_3_HIGH_STARS_TYPE = " INTEGER";
    private static final String COLUMN_MATCH_GAME_THEME_2_DIFFICULTY_3_HIGH_STARS = "matchGameTheme2Difficulty3HighStars";

    private static final String COLUMN_MATCH_GAME_THEME_3_DIFFICULTY_1_HIGH_STARS_TYPE = " INTEGER";
    private static final String COLUMN_MATCH_GAME_THEME_3_DIFFICULTY_1_HIGH_STARS = "matchGameTheme3Difficulty1HighStars";

    private static final String COLUMN_MATCH_GAME_THEME_3_DIFFICULTY_2_HIGH_STARS_TYPE = " INTEGER";
    private static final String COLUMN_MATCH_GAME_THEME_3_DIFFICULTY_2_HIGH_STARS = "matchGameTheme3Difficulty2HighStars";

    private static final String COLUMN_MATCH_GAME_THEME_3_DIFFICULTY_3_HIGH_STARS_TYPE = " INTEGER";
    private static final String COLUMN_MATCH_GAME_THEME_3_DIFFICULTY_3_HIGH_STARS = "matchGameTheme3Difficulty3HighStars";

    //SWAP GAME STARS
    private static final String COLUMN_SWAP_GAME_DIFFICULTY_1_HIGH_STARS_TYPE = " INTEGER";
    private static final String COLUMN_SWAP_GAME_DIFFICULTY_1_HIGH_STARS = "swapGameDifficulty1HighStars";

    private static final String COLUMN_SWAP_GAME_DIFFICULTY_2_HIGH_STARS_TYPE = " INTEGER";
    private static final String COLUMN_SWAP_GAME_DIFFICULTY_2_HIGH_STARS = "swapGameDifficulty2HighStars";

    private static final String COLUMN_SWAP_GAME_DIFFICULTY_3_HIGH_STARS_TYPE = " INTEGER";
    private static final String COLUMN_SWAP_GAME_DIFFICULTY_3_HIGH_STARS = "swapGameDifficulty3HighStars";

    private static final String COLUMN_COMPOSE_GAME_DIFFICULTY_1_HIGH_STARS_TYPE = " INTEGER";
    private static final String COLUMN_COMPOSE_GAME_DIFFICULTY_1_HIGH_STARS = "composeGameDifficulty1HighStars";

    private static final String COLUMN_COMPOSE_GAME_DIFFICULTY_2_HIGH_STARS_TYPE = " INTEGER";
    private static final String COLUMN_COMPOSE_GAME_DIFFICULTY_2_HIGH_STARS = "composeGameDifficulty2HighStars";

    private static final String COLUMN_COMPOSE_GAME_DIFFICULTY_3_HIGH_STARS_TYPE = " INTEGER";
    private static final String COLUMN_COMPOSE_GAME_DIFFICULTY_3_HIGH_STARS = "composeGameDifficulty3HighStars";


    public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_USER_NAME_ID + " " + COLUMN_USER_NAME_ID_TYPE + COMMA_SEP +
            //COLUMN_AGE_RANGE + " " + COLUMN_AGE_RANGE_TYPE + COMMA_SEP +
            //COLUMN_YEARS_TWITCHING_RANGE + " " + COLUMN_YEARS_TWITCHING_RANGE_TYPE + COMMA_SEP +
            //COLUMN_SPECIES_KNOWN_RANGE + " " + COLUMN_SPECIES_KNOWN_RANGE_TYPE + COMMA_SEP +
            //COLUMN_AUDIBLE_RECOGNIZED_RANGE + " " + COLUMN_AUDIBLE_RECOGNIZED_RANGE_TYPE + COMMA_SEP +
            //COLUMN_INTERFACE_EXPERIENCE_RANGE + " " + COLUMN_INTERFACE_EXPERIENCE_RANGE_TYPE + COMMA_SEP +
            //COLUMN_HEARING_EQUALS_SEEING + " " + COLUMN_HEARING_EQUALS_SEEING_TYPE + COMMA_SEP +
            //COLUMN_HAS_USED_SMARTPHONE + " " + COLUMN_HAS_USED_SMARTPHONE_TYPE + COMMA_SEP +
            COLUMN_MATCH_GAME_THEME_1_DIFFICULTY_1_HIGH_STARS + " " + COLUMN_MATCH_GAME_THEME_1_DIFFICULTY_1_HIGH_STARS_TYPE + COMMA_SEP +
            COLUMN_MATCH_GAME_THEME_1_DIFFICULTY_2_HIGH_STARS + " " + COLUMN_MATCH_GAME_THEME_1_DIFFICULTY_2_HIGH_STARS_TYPE + COMMA_SEP +
            COLUMN_MATCH_GAME_THEME_1_DIFFICULTY_3_HIGH_STARS + " " + COLUMN_MATCH_GAME_THEME_1_DIFFICULTY_3_HIGH_STARS_TYPE + COMMA_SEP +
            COLUMN_MATCH_GAME_THEME_2_DIFFICULTY_1_HIGH_STARS + " " + COLUMN_MATCH_GAME_THEME_2_DIFFICULTY_1_HIGH_STARS_TYPE + COMMA_SEP +
            COLUMN_MATCH_GAME_THEME_2_DIFFICULTY_2_HIGH_STARS + " " + COLUMN_MATCH_GAME_THEME_2_DIFFICULTY_2_HIGH_STARS_TYPE + COMMA_SEP +
            COLUMN_MATCH_GAME_THEME_2_DIFFICULTY_3_HIGH_STARS + " " + COLUMN_MATCH_GAME_THEME_2_DIFFICULTY_3_HIGH_STARS_TYPE + COMMA_SEP +
            COLUMN_MATCH_GAME_THEME_3_DIFFICULTY_1_HIGH_STARS + " " + COLUMN_MATCH_GAME_THEME_3_DIFFICULTY_1_HIGH_STARS_TYPE + COMMA_SEP +
            COLUMN_MATCH_GAME_THEME_3_DIFFICULTY_2_HIGH_STARS + " " + COLUMN_MATCH_GAME_THEME_3_DIFFICULTY_2_HIGH_STARS_TYPE + COMMA_SEP +
            COLUMN_MATCH_GAME_THEME_3_DIFFICULTY_3_HIGH_STARS + " " + COLUMN_MATCH_GAME_THEME_3_DIFFICULTY_3_HIGH_STARS_TYPE + COMMA_SEP +
            COLUMN_SWAP_GAME_DIFFICULTY_1_HIGH_STARS + " " + COLUMN_SWAP_GAME_DIFFICULTY_1_HIGH_STARS_TYPE + COMMA_SEP +
            COLUMN_SWAP_GAME_DIFFICULTY_2_HIGH_STARS + " " + COLUMN_SWAP_GAME_DIFFICULTY_2_HIGH_STARS_TYPE + COMMA_SEP +
            COLUMN_SWAP_GAME_DIFFICULTY_3_HIGH_STARS + " " + COLUMN_SWAP_GAME_DIFFICULTY_3_HIGH_STARS_TYPE + COMMA_SEP +
            COLUMN_COMPOSE_GAME_DIFFICULTY_1_HIGH_STARS + " " + COLUMN_COMPOSE_GAME_DIFFICULTY_1_HIGH_STARS_TYPE + COMMA_SEP +
            COLUMN_COMPOSE_GAME_DIFFICULTY_2_HIGH_STARS + " " + COLUMN_COMPOSE_GAME_DIFFICULTY_2_HIGH_STARS_TYPE + COMMA_SEP +
            COLUMN_COMPOSE_GAME_DIFFICULTY_3_HIGH_STARS + " " + COLUMN_COMPOSE_GAME_DIFFICULTY_3_HIGH_STARS_TYPE +
            ")";

    public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    //==================================================================================

    // method userDataRecordsInDatabase returns true if there are records in the UserDataORM table
    // in the DB, otherwise, false
    public static boolean userDataRecordsInDatabase (Context context) {
        //Log.d (TAG, "method userDataRecordsInDatabase");

        DatabaseWrapper databaseWrapper = Shared.databaseWrapper;
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        boolean recordsExist = false;

        if (database != null) {
            Cursor cursor = database.rawQuery("SELECT * FROM " + UserDataORM.TABLE_NAME, null);
            Log.d(TAG, "method userDataRecordsInDatabase: Checked " + cursor.getCount() + " UserData records...");

            if (cursor.getCount() > 0) {
                recordsExist = true;
            }
            cursor.close();
        }
        database.close();
        return recordsExist;
    }

    // method numUserDataRecordsInDatabase returns the number of records in the UserDataORM table
    // in the DB, otherwise, false
    public static int numUserDataRecordsInDatabase (Context context) {
        //Log.d (TAG, "method recordsInDatabase");

        DatabaseWrapper databaseWrapper = Shared.databaseWrapper;
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        int numRecords = 0;

        if (database != null) {
            Cursor cursor = database.rawQuery("SELECT * FROM " + UserDataORM.TABLE_NAME, null);
            Log.d(TAG, "method numUserDataRecordsInDatabase: There are: " + cursor.getCount() + " UserData records...");

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

    //method getUserData fetches the full list of UserData objects in the local Database
    public static ArrayList <UserData> getUserData (Context context) {
        Log.d (TAG, "method getUserData returns a list of UserData objects");

        DatabaseWrapper databaseWrapper =  Shared.databaseWrapper;
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        ArrayList <UserData> userDataList = null;

        if (database !=  null) {
            Cursor cursor = database.rawQuery("SELECT * FROM " + UserDataORM.TABLE_NAME, null);
            Log.d (TAG, "method getUserData: Loaded " + cursor.getCount() + " UserData records...");
            if (userDataRecordsInDatabase(Shared.context)) {
                userDataList = new ArrayList<UserData>(numUserDataRecordsInDatabase(Shared.context));
                cursor.moveToFirst();
                int rowCount = 0;
                while (!cursor.isAfterLast()) {
                    UserData userDataAtCursor = cursorToUserData(cursor);
                    //Check the state of all userData fields here
                    /*
                    Log.d (TAG, "... PARSE: method getUserData: Database row: " + rowCount +
                            " | userName: " + userDataAtCursor.getUserName() +
                            " | userAge: " + userDataAtCursor.getAgeRange() +
                            " | yearsTwitching: " + userDataAtCursor.getYearsTwitchingRange() +
                            " | speciesKnown: " + userDataAtCursor.getSpeciesKnownRange() +
                            " | audibleRecognized: " + userDataAtCursor.getAudibleRecognizedRange() +
                            " | interfaceExperience: " + userDataAtCursor.getInterfaceExperienceRange() +
                            " | hearingIsSeeing: " + userDataAtCursor.getHearingEqualsSeeing() +
                            " | usedSmartphone: " + userDataAtCursor.getHasUsedSmartphone());
                    */
                    userDataList.add(userDataAtCursor);
                    Log.d (TAG, "!!! userDataList.get(rowCount) userData Object @: " + userDataList.get(rowCount));
                    //move cursor to start of next row
                    rowCount++;
                    cursor.moveToNext();
                }
                cursor.close();
                database.close();
            }
        }
        return  userDataList;
    }

    //method isUserNameInDB takes a userNameString and checks whether it has been used as a
    //primary key yet in the database - used to check existence and uniqueness of login names.
    public static boolean isUserNameInDB (Context context, String userName) {
        Log.d (TAG, "method isUserNameInDB: check userName: " + userName);
        DatabaseWrapper databaseWrapper = Shared.databaseWrapper;
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        boolean userExists = false;     //false unless found in database
        if (database != null) {
            Log.d (TAG, "method isUserNameInDB: searching...");
            Cursor cursor = database.rawQuery("SELECT * FROM " + UserDataORM.TABLE_NAME + " WHERE " + UserDataORM.COLUMN_USER_NAME_ID + " ='" + userName + "'", null);
            //FIXME - this prevent's SQL injection: Cursor cursor = database.rawQuery("SELECT * FROM " + UserDataORM.TABLE_NAME + " WHERE " + UserDataORM.COLUMN_USER_NAME_ID + " =?", userName);
            if (cursor.getCount() > 0) {
                userExists = true;
                Toast.makeText(Shared.context, "userName is in Database" , Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(Shared.context, "new userName is available" , Toast.LENGTH_SHORT).show();
            }
            cursor.close();
            database.close();
        }
        return  userExists;
    }

    //method findUserDataByID identifies and returns a single UserData object based on ID
    public static UserData findUserDataByID (Context context, String userNameId) {
        Log.d (TAG, "method findUserDataByID: userId: " + userNameId);
        DatabaseWrapper databaseWrapper = new DatabaseWrapper(context); //FIXME Shared.databaseWrapper;
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        UserData userData = null;

        if (database != null) {
            Log.d (TAG, "method findUserDataById: Loading User: " + userNameId);
            try {
                Cursor cursor = database.rawQuery("SELECT * FROM " + UserDataORM.TABLE_NAME + " WHERE " + UserDataORM.COLUMN_USER_NAME_ID + " ='" + userNameId + "'", null);
                //FIXME - this prevent's SQL injection: Cursor cursor = database.rawQuery("SELECT * FROM " + UserDataORM.TABLE_NAME + " WHERE " + UserDataORM.COLUMN_USER_NAME_ID + " =?", userNameId, null);
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    userData = cursorToUserData(cursor);
                    Log.d(TAG, "method findUserDataByID: UserData loaded successfully");
                }
                cursor.close();
            } catch (SQLiteException sqlex) {
                //Toast.makeText (Shared.context, "Failed to Find User", Toast.LENGTH_SHORT).show();
                sqlex.printStackTrace();
            }
            database.close();
        }
        //TODO REMOVE: Check the state of all userData fields here
        try {
            Log.d(TAG, "** method findUserDataById: userData.getUserName: " + userData.getUserName());
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
        try {
            Log.d(TAG, "** method findUserDataById: userData.getAgeRange: " + userData.getAgeRange());
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
        try {
            Log.d(TAG, "** method findUserDataById: userData.getYearsTwitching: " + userData.getYearsTwitchingRange());
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
        try {
            Log.d(TAG, "** method findUserDataById: userData.getSpeciesKnownRange: " + userData.getSpeciesKnownRange());
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
        try {
            Log.d(TAG, "** method findUserDataById: userData.getAudibleRecognized: " + userData.getAudibleRecognizedRange());
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
        try {
            Log.d(TAG, "** method findUserDataById: userData.getInterfaceExperience: " + userData.getInterfaceExperienceRange());
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
        try {
            Log.d(TAG, "** method findUserDataById: userData.getHearingEqualsSeeing: " + userData.getHearingEqualsSeeing());
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
        try {
            Log.d(TAG, "** method findUserDataById: userData.getHasUsedSmartphone: " + userData.getHasUsedSmartphone());
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
        return userData;
    }

    //method insertUserData inserts a new UserData object into the database if it doesn't already exist
    public static boolean insertUserData (Context context, UserData userData) {
        Log.d (TAG, "method insertUserData tries to insert a new UserData object into the database");

        boolean success = false;

        if (findUserDataByID(context, userData.getUserName()) == null) {
            Log.d(TAG, "method insertUserData: creating ContentValues");
            ContentValues values = userDataToContentValues(userData);

            DatabaseWrapper databaseWrapper = Shared.databaseWrapper;
            SQLiteDatabase database = databaseWrapper.getReadableDatabase();

            try {
                if (database != null) {
                    long rowID = database.insert(TABLE_NAME, "null", values);
                    Log.d(TAG, "method insertUserData: Inserted new UserData into rowID: " + rowID);
                    success = true;
                }
            } catch (SQLiteException sqlex) {
                Log.e(TAG, "method insertUserData: Failed to insert UserData[" + userData.getUserName() + "] due to: " + sqlex);
                sqlex.printStackTrace();
            } finally {
                if (database != null) {
                    database.close();
                }
            }
            return success;
        }

        else {
            Log.d (TAG, "userData already exists in database, instead of inserting, updating");
            return updateUserData (context , userData);
        }
    }

    //method updateUserData updates an existing UserData in the database
    public static boolean updateUserData (Context context, UserData userData) {
        Log.d (TAG, "method updateUserData: creating ContentValues");
        ContentValues values = userDataToContentValues (userData);
        DatabaseWrapper databaseWrapper = Shared.databaseWrapper;
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        boolean success = false;
        try {
            if (database != null) {
                Log.d (TAG, "method updateUserData: Updating UserData[" + userData.getUserName() + "]...");
                database.update(UserDataORM.TABLE_NAME, values, UserDataORM.COLUMN_USER_NAME_ID + " = " + userData.getUserName(), null);
                // TODO iterate over GameDataORM.insertGames to set new Games in UserData? or only on update
                success = true;
            }
        } catch (NullPointerException npe) {
            Log.e (TAG, "method updateUserData: Failed to update UserData[" + userData.getUserName() + "] due to: " + npe);
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

        Log.d (TAG, "values.put... userData.getUserName(): " + userData.getUserName() + " to COLUMN_USER_NAME_ID: " + COLUMN_USER_NAME_ID);
        values.put (COLUMN_USER_NAME_ID, userData.getUserName());
        Log.d (TAG, "values.put... userData.getAgeRange(): " + userData.getAgeRange() + " to COLUMN_AGE_RANGE: " + COLUMN_AGE_RANGE);
        //values.put (COLUMN_AGE_RANGE, userData.getAgeRange());
        //values.put (COLUMN_YEARS_TWITCHING_RANGE, userData.getYearsTwitchingRange());
        //values.put (COLUMN_SPECIES_KNOWN_RANGE, userData.getSpeciesKnownRange());
        //values.put (COLUMN_AUDIBLE_RECOGNIZED_RANGE, userData.getAudibleRecognizedRange());
        //values.put (COLUMN_INTERFACE_EXPERIENCE_RANGE, userData.getInterfaceExperienceRange());
        //if (!userData.getHearingEqualsSeeing()) { values.put (COLUMN_HEARING_EQUALS_SEEING, 0);}
        //else{ values.put (COLUMN_HEARING_EQUALS_SEEING, 1); }
        //if (!userData.getHasUsedSmartphone()) { values.put (COLUMN_HAS_USED_SMARTPHONE, 0); }
        //else { values.put (COLUMN_HAS_USED_SMARTPHONE, 1); }
        values.put (COLUMN_MATCH_GAME_THEME_1_DIFFICULTY_1_HIGH_STARS, userData.getMatchTheme1Difficulty1HighStars());
        values.put (COLUMN_MATCH_GAME_THEME_1_DIFFICULTY_2_HIGH_STARS, userData.getMatchTheme1Difficulty2HighStars());
        values.put (COLUMN_MATCH_GAME_THEME_1_DIFFICULTY_3_HIGH_STARS, userData.getMatchTheme1Difficulty3HighStars());
        values.put (COLUMN_MATCH_GAME_THEME_2_DIFFICULTY_1_HIGH_STARS, userData.getMatchTheme2Difficulty1HighStars());
        values.put (COLUMN_MATCH_GAME_THEME_2_DIFFICULTY_2_HIGH_STARS, userData.getMatchTheme2Difficulty2HighStars());
        values.put (COLUMN_MATCH_GAME_THEME_2_DIFFICULTY_3_HIGH_STARS, userData.getMatchTheme2Difficulty3HighStars());
        values.put (COLUMN_MATCH_GAME_THEME_3_DIFFICULTY_1_HIGH_STARS, userData.getMatchTheme3Difficulty1HighStars());
        values.put (COLUMN_MATCH_GAME_THEME_3_DIFFICULTY_2_HIGH_STARS, userData.getMatchTheme3Difficulty2HighStars());
        values.put (COLUMN_MATCH_GAME_THEME_3_DIFFICULTY_3_HIGH_STARS, userData.getMatchTheme3Difficulty3HighStars());
        values.put (COLUMN_SWAP_GAME_DIFFICULTY_1_HIGH_STARS, userData.getSwapHighStarsDifficulty1());
        values.put (COLUMN_SWAP_GAME_DIFFICULTY_2_HIGH_STARS, userData.getSwapHighStarsDifficulty2());
        values.put (COLUMN_SWAP_GAME_DIFFICULTY_3_HIGH_STARS, userData.getSwapHighStarsDifficulty3());
        values.put (COLUMN_COMPOSE_GAME_DIFFICULTY_1_HIGH_STARS, userData.getComposeHighStarsDifficulty1());
        values.put (COLUMN_COMPOSE_GAME_DIFFICULTY_2_HIGH_STARS, userData.getComposeHighStarsDifficulty2());
        values.put (COLUMN_COMPOSE_GAME_DIFFICULTY_3_HIGH_STARS, userData.getComposeHighStarsDifficulty3());
        return values;
    }

    //method cursorToUserData populates a UserData object with data from the cursor
    private static UserData cursorToUserData (Cursor cursor) {
        Log.d (TAG, "method cursorToUserData");
        UserData cursorAtUserData = new UserData();     //FIXME this or UserData.getInstance()
        Log.d (TAG, "!!!!! cursorAtUserData @: " + cursorAtUserData);

        cursorAtUserData.setUserName(cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME_ID)));
        //cursorAtUserData.setAgeRange(cursor.getString(cursor.getColumnIndex(COLUMN_AGE_RANGE)));
        //cursorAtUserData.setYearsTwitchingRange(cursor.getString(cursor.getColumnIndex(COLUMN_YEARS_TWITCHING_RANGE)));
        //cursorAtUserData.setSpeciesKnownRange(cursor.getString(cursor.getColumnIndex(COLUMN_SPECIES_KNOWN_RANGE)));
        //cursorAtUserData.setAudibleRecognizedRange(cursor.getString(cursor.getColumnIndex(COLUMN_AUDIBLE_RECOGNIZED_RANGE)));
        //cursorAtUserData.setInterfaceExperienceRange(cursor.getString(cursor.getColumnIndex(COLUMN_INTERFACE_EXPERIENCE_RANGE)));
        //if (cursor.getInt(cursor.getColumnIndex(COLUMN_HEARING_EQUALS_SEEING)) == 1) { cursorAtUserData.setHearingEqualsSeeing(true); }
        //else if (cursor.getInt(cursor.getColumnIndex(COLUMN_HEARING_EQUALS_SEEING)) == 0) { cursorAtUserData.setHearingEqualsSeeing(false); }
        //else {
        //    Log.d (TAG, "ERROR: method cursorToUserData: hearingEqualsSeeing: " + cursor.getInt(cursor.getColumnIndex(COLUMN_HEARING_EQUALS_SEEING)));
        //}
        //if (cursor.getInt(cursor.getColumnIndex(COLUMN_HAS_USED_SMARTPHONE)) == 1) { cursorAtUserData.setHasUsedSmartPhone(true); }
        //else if (cursor.getInt(cursor.getColumnIndex(COLUMN_HAS_USED_SMARTPHONE)) == 0) { cursorAtUserData.setHasUsedSmartPhone(false); }
        //else {
        //    Log.d (TAG, "ERROR: method cursorToUserData: hasUsedSmartphone: " + cursor.getInt(cursor.getColumnIndex(COLUMN_HAS_USED_SMARTPHONE)));
        //}
        cursorAtUserData.setMatchHighStarsTheme1Difficulty1(cursor.getInt(cursor.getColumnIndex(COLUMN_MATCH_GAME_THEME_1_DIFFICULTY_1_HIGH_STARS)));
        cursorAtUserData.setMatchHighStarsTheme1Difficulty2(cursor.getInt(cursor.getColumnIndex(COLUMN_MATCH_GAME_THEME_1_DIFFICULTY_2_HIGH_STARS)));
        cursorAtUserData.setMatchHighStarsTheme1Difficulty3(cursor.getInt(cursor.getColumnIndex(COLUMN_MATCH_GAME_THEME_1_DIFFICULTY_3_HIGH_STARS)));
        cursorAtUserData.setMatchHighStarsTheme2Difficulty1(cursor.getInt(cursor.getColumnIndex(COLUMN_MATCH_GAME_THEME_2_DIFFICULTY_1_HIGH_STARS)));
        cursorAtUserData.setMatchHighStarsTheme2Difficulty2(cursor.getInt(cursor.getColumnIndex(COLUMN_MATCH_GAME_THEME_2_DIFFICULTY_2_HIGH_STARS)));
        cursorAtUserData.setMatchHighStarsTheme2Difficulty3(cursor.getInt(cursor.getColumnIndex(COLUMN_MATCH_GAME_THEME_2_DIFFICULTY_3_HIGH_STARS)));
        cursorAtUserData.setMatchHighStarsTheme3Difficulty1(cursor.getInt(cursor.getColumnIndex(COLUMN_MATCH_GAME_THEME_3_DIFFICULTY_1_HIGH_STARS)));
        cursorAtUserData.setMatchHighStarsTheme3Difficulty2(cursor.getInt(cursor.getColumnIndex(COLUMN_MATCH_GAME_THEME_3_DIFFICULTY_2_HIGH_STARS)));
        cursorAtUserData.setMatchHighStarsTheme3Difficulty3(cursor.getInt(cursor.getColumnIndex(COLUMN_MATCH_GAME_THEME_3_DIFFICULTY_3_HIGH_STARS)));
        cursorAtUserData.setSwapHighStarsDifficulty1(cursor.getInt(cursor.getColumnIndex(COLUMN_SWAP_GAME_DIFFICULTY_1_HIGH_STARS)));
        cursorAtUserData.setSwapHighStarsDifficulty2(cursor.getInt(cursor.getColumnIndex(COLUMN_SWAP_GAME_DIFFICULTY_2_HIGH_STARS)));
        cursorAtUserData.setSwapHighStarsDifficulty3(cursor.getInt(cursor.getColumnIndex(COLUMN_SWAP_GAME_DIFFICULTY_3_HIGH_STARS)));
        cursorAtUserData.setComposeHighStarsDifficulty1(cursor.getInt(cursor.getColumnIndex(COLUMN_COMPOSE_GAME_DIFFICULTY_1_HIGH_STARS)));
        cursorAtUserData.setComposeHighStarsDifficulty2(cursor.getInt(cursor.getColumnIndex(COLUMN_COMPOSE_GAME_DIFFICULTY_2_HIGH_STARS)));
        cursorAtUserData.setComposeHighStarsDifficulty3(cursor.getInt(cursor.getColumnIndex(COLUMN_COMPOSE_GAME_DIFFICULTY_3_HIGH_STARS)));
        return  cursorAtUserData;
    }
}