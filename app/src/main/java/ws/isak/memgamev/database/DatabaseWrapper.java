package ws.isak.memgamev.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.util.Log;

import java.io.File;

import ws.isak.memgamev.common.CardData;
import ws.isak.memgamev.common.Shared;


/*
 *
 * @author isak
 */


public class DatabaseWrapper extends SQLiteOpenHelper {

    private static final String TAG="Class: DatabaseWrapper";

    private static final int DATABASE_VERSION = 1;


    //constructor
    public DatabaseWrapper (Context context) {
        super (context, Shared.DATABASE_NAME, null, DATABASE_VERSION);
        //Log.d (TAG, "constructor DatabaseWrapper: check doesDatabaseExist: " + doesDatabaseExist(context));
    }

    //Called if the database with given name doesn't exist in order to create it
    @Override
    public void onCreate (SQLiteDatabase sqliteDB) {
        Log.d (TAG, "method onCreate: Creating Database [" + Shared.DATABASE_NAME + " v." + DATABASE_VERSION + "]...");
        //create the database
        sqliteDB.execSQL(UserDataORM.SQL_CREATE_TABLE);
        Log.d (TAG, "created UserData table");
        sqliteDB.execSQL(MemGameDataORM.SQL_CREATE_TABLE);
        Log.d (TAG, "created MemGameData table");
        sqliteDB.execSQL(CardDataORM.SQL_CREATE_TABLE);
        Log.d (TAG, "created CardData table");
        Log.d (TAG, "method onCreate: check doesDatabaseExist: " + doesDatabaseExist(Shared.context));
    }

    //Called when database version is increased
    //TODO see solution from https://thebhwgroup.com/blog/how-android-sqlite-onupgrade
    @Override
    public void onUpgrade (SQLiteDatabase sqliteDB, int oldVersion, int newVersion) {
        Log.d (TAG, "Upgrading Database [" + Shared.DATABASE_NAME + " v." + oldVersion + "] to [" + Shared.DATABASE_NAME + " v." + newVersion);
        sqliteDB.execSQL(UserDataORM.SQL_DROP_TABLE);
        sqliteDB.execSQL(MemGameDataORM.SQL_DROP_TABLE);
        sqliteDB.execSQL(CardDataORM.SQL_DROP_TABLE);
        onCreate(sqliteDB);
    }

    private static boolean doesDatabaseExist(Context context) {
        File dbFile = context.getDatabasePath(Shared.DATABASE_NAME);
        return dbFile.exists();
    }
}
