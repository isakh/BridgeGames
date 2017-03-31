package ws.isak.memgamev.common;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.util.Log;


/*
 *
 * @author isak
 */


public class DatabaseWrapper extends SQLiteOpenHelper {

    private static final String TAG="Class: DatabaseWrapper";

    private static final String DATABASE_NAME = "Bridge.db";
    private static final int DATABASE_VERSION = 1;

    //constructor
    public DatabaseWrapper (Context context) {
        super (context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Called if the database with given name doesn't exist in order to create it
    @Override
    public void onCreate (SQLiteDatabase sqliteDB) {
        Log.d (TAG, "Creating Database [" + DATABASE_NAME + " v." + DATABASE_VERSION + "]...");
        //TODO create the database
    }

    //Called when database version is increased
    @Override
    public void onUpgrade (SQLiteDatabase sqliteDB, int oldVersion, int newVersion) {
        Log.d (TAG, "Upgrading Database [" + DATABASE_NAME + " v." + oldVersion + "] to [" + DATABASE_NAME + " v." + newVersion);
    }


}
