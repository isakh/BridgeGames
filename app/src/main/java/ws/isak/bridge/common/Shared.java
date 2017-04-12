package ws.isak.bridge.common;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import java.util.List;

import ws.isak.bridge.engine.Engine;
import ws.isak.bridge.events.EventBus;
import ws.isak.bridge.database.DatabaseWrapper;
import ws.isak.bridge.model.MatchGame;
import ws.isak.bridge.model.MatchGameData;
import ws.isak.bridge.model.SwapGameData;

/*
 * Class Shared
 *
 * @author isak
 */

public class Shared {

    public static final String TAG = "Shared";

	public static Context context;           // FIXME can we make these non-static?
	public static FragmentActivity activity; // TODO: move to weak reference
	public static Engine engine;
	public static EventBus eventBus;
    public static MatchGame currentMatchGame;

    //database
    //FIXME - can't use context here why? public static final String DATABASE_NAME = context.getResources().getString(R.string.database_name);
    public static final String DATABASE_NAME = "bridge.db";
    public static DatabaseWrapper databaseWrapper;

    //data classes / methods    FIXME - should these be static???
    public static UserData userData;        //This holds the current active UserData
    public static List <UserData> userDataList;
    public static MatchGameData matchGameData;  //FIXME - is this necessary? we can generally access the current matchGameData from userData.getCurMemGameData()
    public static List <MatchGameData> matchGameDataList;
    public static MatchCardData matchCardData;        //placeholder matchCardData for return from database call
    public static List <MatchCardData> matchCardDataList;
    public static SwapGameData swapGameData;
    public static List <SwapGameData> swapGameDataList;
    
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
