package ws.isak.bridge.common;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ws.isak.bridge.R;

import ws.isak.bridge.engine.Engine;
import ws.isak.bridge.events.EventBus;
import ws.isak.bridge.database.DatabaseWrapper;

import ws.isak.bridge.model.MatchGame;
import ws.isak.bridge.model.SwapGame;
import ws.isak.bridge.model.MatchGameData;
import ws.isak.bridge.model.SwapGameData;
import ws.isak.bridge.model.ComposeGameData;

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
    public static SwapGame currentSwapGame;

    //database
    public static final String DATABASE_NAME = "bridge.db";  //FIXME context.getResources().getString(R.string.database_name);
    public static DatabaseWrapper databaseWrapper;

    //data classes / methods
    public static UserData userData;                        //This holds the current active UserData for the current playing user
    public static ArrayList<UserData> userDataList;         //a list of all data about all users who have login credentials

    public static ArrayList <MatchGameData> matchGameDataList;   //holds a list of the match games played by the (TODO?? -current user ??)
    public static ArrayList <MatchCardData> matchCardDataList;   //holds a list of the match cards currently loaded on the device

    public static ArrayList <SwapGameData> swapGameDataList;     //holds a list of the swap games played by the (TODO?? -current user ??)
    public static ArrayList <SwapCardData> swapCardDataList;     //holds a list of the swap cards currently loaded on the device

    public static ArrayList <ComposeGameData> composeGameDataList;      //holds a list of the compose games played by the (TODO?? -current user ??)
    public static ArrayList <ComposeSampleData> composeSampleDataList;  //holds a list of the samples available for the compose game

    public static void setUserData (UserData user) {
        Log.d (TAG, "method setUserData");
        userData = user;
    }

    public static UserData getUserData () {
        Log.d (TAG, "method getUserData");
        return userData;
    }

    //TODO make sense of static initialization blocks!?! see http://softwareengineering.stackexchange.com/questions/228242/working-with-static-constructor-in-java
    static {
        Log.d (TAG, "static initialization block");
    }

    public static void debugStateOfMatchCardDataList(String calledFrom) {
        Log.d (TAG, "method debugStateOfMatchCardDataList called from: " + calledFrom + " | set verbose output to see list");
        //verbose debugging of state of matchCardDataList
        Log.v (TAG, "method buildMatchCardDataList: Shared.matchCardDataList.size(): " + Shared.matchCardDataList.size());
        for (int j = 0; j < Shared.matchCardDataList.size(); j++) {
            Log.v (TAG, "method debugStateOfMatchCardDataList: Shared.matchCardDataList @: " + j +
                    " cardID is: " + Shared.matchCardDataList.get(j).getCardID());
        }
    }
}