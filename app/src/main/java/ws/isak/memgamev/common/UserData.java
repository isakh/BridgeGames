package ws.isak.memgamev.common;

import android.util.Log;
import java.util.ArrayList;

import ws.isak.memgamev.model.MemGameData;

/*
 * The UserData class contains creation and accessor methods for the data collected about a particular
 * user of the game including their name (checked against a database(?) of names to avoid collisions
 * as well as a list of the GameData objects containing timing and move selection information about
 * each game that they have played.
 *
 * @author isak
 */

public class UserData {

    private final String TAG = "Class: UserData";

    private String userName;
    private ArrayList <MemGameData> memGameDataList = new ArrayList<MemGameData>();
    //TODO private ArrayList swapGameDataList<SwapGameData>;   this will cover when the user plays the tile swapping game

    public void setUserName (String user) {
        //TODO method CheckUserNameUnique (user, )
        Log.d (TAG, "method setUserName: user name is: " + user);
        userName = user;
    }

    public String getUserName () {
        Log.d (TAG, "method getUserName returns: " + userName);
        return userName;
    }

    public void createMemGameDataList () {
        Log.d (TAG, "method createMemGameDataList");
        memGameDataList = new ArrayList();
    }

    public void addToMemGameDataList (MemGameData game) {
        Log.d (TAG, "method addToGameDataList: adding game data to list");
        memGameDataList.add (game);        //TODO add try/catch block here
    }

    /*
     * Method queryMemGameData returns a GameData object at position in the list gameDataRecord
     */
    public MemGameData queryMemGameDataList (int gameDataRecord) {
        return memGameDataList.get(gameDataRecord);
    }
}
