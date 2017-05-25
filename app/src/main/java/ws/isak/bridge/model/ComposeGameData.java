package ws.isak.bridge.model;

import java.util.ArrayList;

import ws.isak.bridge.common.Shared;

/*
 * The ComposeGameData class hold information about each Composition game played by a given user.
 * Compared to the other two games, less information is stored here - for the time being at least,
 * we are not concerned with the state of the tracker as something to store, because there is no
 * particular direction that play needs to take.  Rather, we are interested only in storing whether
 * and when the game has started, and the number of movements on the board that have occurred.  For
 * later analysis, success will be measured by the amount of time the user plays, and to a lesser
 * degree, the number of changes they instigate before getting bored.
 *
 * @author isak
 */

public class ComposeGameData {

    private static final String TAG = "SwapGameData";

    //Parameters to be saved for database matching game to user
    private String userPlayingName;
    //setup parameters
    private long gameStartTimestamp;            //keep track of the timestamp for the start of the game - database primary key
    private boolean gameStarted;                //Set to Boolean false, becomes true when first card is placed - triggers gameStarTimeStamp
    private int gameDifficulty;                 //a misnomer, but it defines the number of steps in the loops
    //the following respond to user input during the game
    private int numTurnsTakenInGame;            //Initialize number of turns in game to 0 and increment on each event.
    private ArrayList<Long> gamePlayDurations;  //Time the player spent on the game so far (sum of turnDurations) at each turn (can it be greater than allocated time?)
    private ArrayList<Long> turnDurations;      //a list of durations of each turn - a turn is defined as a single click, implemented as ArrayList //TODO should we also have a measure of paired click turns?

    public ComposeGameData () {
        //Log.d (TAG, "Constructor: initializing game data fields");
        setUserPlayingName(Shared.userData.getUserName());
        //initialize to 0 or null as necessary
        setGameStarted(false);      //initialize to false on setup
        setGameStartTimestamp(0);
        setGameDifficulty(0);       //initialize to 0 and reset to actual value on selection
        setNumTurnsTaken(0);
        initTurnDurationsArray();       //null array at start
        initGamePlayDurationsArray();   //null array at start
    }

    //[0]
    public void setUserPlayingName (String userName) {
        //Log.d (TAG, "");
        userPlayingName = userName;
    }

    public String getUserPlayingName () {
        //Log.d (TAG, "");
        return userPlayingName;
    }


    //[1] set/get gameStartTimestamp - this is unique and useful for sorting
    public void setGameStartTimestamp (long gameStartTime) {
        //Log.d (TAG, "method setGameStartTimestamp");
        gameStartTimestamp = gameStartTime;
    }

    public long getGameStartTimestamp () {
        //Log.d (TAG, "method getGameStartTimestamp");
        return gameStartTimestamp;
    }
    //[2] set/get gameStarted
    public void setGameStarted (boolean startedYet) {
        //Log.d (TAG, "method setGameStarted");
        gameStarted = startedYet;
    }

    public boolean isGameStarted () {
        //Log.d (TAG, "method isGameStarted");
        return gameStarted;
    }

    //[3] set/get gameDifficulty
    public void setGameDifficulty (int diff) {
        //
        gameDifficulty = diff;
    }

    public int getGameDifficulty () {
        //
        return gameDifficulty;
    }

    //[4] control methods for  gamePlayDurations
    public void initGamePlayDurationsArray () {
        //Log.d (TAG, "method initGamePlayDurationsArray");
        gamePlayDurations = new ArrayList<Long>();
    }
    public void appendToGamePlayDurations (long durToAdd) {
        //Log.d (TAG, "method appendToGamePlayDurations");
        gamePlayDurations.add(durToAdd);
    }

    public long queryGamePlayDurations (int locToQuery) {
        //Log.d (TAG, "method getGamePlayDuration: gamePlayDuration: " + gamePlayDuration);
        return gamePlayDurations.get(locToQuery);
    }

    public int sizeOfPlayDurationsArray () {
        //Log.d (TAG, "method sizeOfPlayDurationsArray");
        return gamePlayDurations.size();
    }

    public ArrayList <Long> getGamePlayDurations () {
        //
        return gamePlayDurations;
    }

    //[5] control methods for the turnsDurationArray
    private void initTurnDurationsArray () {
        //Log.d (TAG, "method initTurnDurations array list");
        turnDurations = new ArrayList<Long>();
    }

    public void appendToTurnDurations (long durToAdd) {
        //Log.d (TAG, "method addDurationToTurnDurations");
        turnDurations.add(durToAdd);
    }

    public long queryTurnDurationsArray (int locToQuery) {
        //Log.d (TAG, "method queryTurnDurationArray: location to query: " + locToQuery);
        return turnDurations.get(locToQuery);
    }

    public int sizeOfTurnDurationsArray (){
        //Log.d (TAG, "method sizeOfTurnDurationsArray);
        return turnDurations.size();
    }

    public ArrayList <Long> getTurnDurationsArray () {
        //
        return turnDurations;
    }

    //[6] set/get/increment numTurnsTaken
    public void setNumTurnsTaken (int numTurns) {
        //Log.d (TAG, "method setNumTurnsTaken: This is called on init as 0");
        numTurnsTakenInGame = numTurns;
    }

    public void incrementNumTurnsTaken () {
        //Log.d (TAG, "method incrementNumTurnsTaken: prior to increment: " + numTurnsTakenInGame);
        numTurnsTakenInGame++;
        //Log.d (TAG, "                             : post increment is: " + numTurnsTakenInGame);
    }

    public int getNumTurnsTaken () {
        //Log.d (TAG, "method getNumTurnsTaken");
        return numTurnsTakenInGame;
    }
}