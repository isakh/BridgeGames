package ws.isak.memgamev.common;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import ws.isak.memgamev.engine.Engine;
import ws.isak.memgamev.events.EventBus;

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
    public static UserData userData;        //TODO does this make sense?

    //TODO make sense of static initialization blocks! see http://softwareengineering.stackexchange.com/questions/228242/working-with-static-constructor-in-java
    static {
        Log.d (TAG, "static initialization block");
    }

	//TODO add anything else here??

}
