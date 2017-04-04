package ws.isak.memgamev.common;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import ws.isak.memgamev.R;
import ws.isak.memgamev.engine.Engine;
import ws.isak.memgamev.events.EventBus;
import ws.isak.memgamev.database.DatabaseWrapper;

/*
 * Class Shared
 *
 * @author isak
 */

public class Shared {

    public static final String TAG = "Class: Shared";

	public static Context context;
    //FIXME public Context context;
	public static FragmentActivity activity; // TODO: move to weak reference
    //FIXME public FragmentActivity activity;
	public static Engine engine;
    //FIXME public Engine engine;
	public static EventBus eventBus;

    //data class
    public static UserData userData;        //This holds the current active UserData

    //database
    public static final String DATABASE_NAME = context.getResources().getString(R.string.database_name);
    public static DatabaseWrapper databaseWrapper;

    //TODO make sense of static initialization blocks! see http://softwareengineering.stackexchange.com/questions/228242/working-with-static-constructor-in-java
    static {
        Log.d (TAG, "static initialization block");
    }

	//TODO add anything else here??

}
