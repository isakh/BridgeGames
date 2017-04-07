package ws.isak.memgamev.common;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import java.util.List;

import ws.isak.memgamev.engine.Engine;
import ws.isak.memgamev.events.EventBus;
import ws.isak.memgamev.database.DatabaseWrapper;
import ws.isak.memgamev.model.Game;
import ws.isak.memgamev.model.MemGameData;

/*
 * Class Shared
 *
 * @author isak
 */

public class Shared {

    public static final String TAG = "Class: Shared";

	public static Context context;           // FIXME can we make these non-static?
	public static FragmentActivity activity; // TODO: move to weak reference
	public static Engine engine;
	public static EventBus eventBus;
    public static Game currentGame;

    //database
    //FIXME - can't use context here why? public static final String DATABASE_NAME = context.getResources().getString(R.string.database_name);
    public static final String DATABASE_NAME = "bridge.db";
    public static DatabaseWrapper databaseWrapper;

    //data classes / methods
    public static UserData userData;        //This holds the current active UserData
    public static List <UserData> userDataList;
    public static List <MemGameData> memGameDataList;
    
    public static void setUserData (UserData user) {
        Log.d (TAG, "method setUserData");
        userData = user;
    }

    public static UserData getUserData () {
        Log.d (TAG, "method getUserData");
        return userData;
    }

    //TODO make sense of static initialization blocks! see http://softwareengineering.stackexchange.com/questions/228242/working-with-static-constructor-in-java
    static {
        Log.d (TAG, "static initialization block");
    }

	//TODO add anything else here??

}
