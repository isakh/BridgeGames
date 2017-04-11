package ws.isak.bridge.model;

import java.util.ArrayList;

import ws.isak.bridge.common.Shared;


/*
 * The NenGameData class contains information about each specific game played by the user including
 * the difficulty level and a list of the timings and cards selected on each move.  It includes
 * methods for constructing the MemGameData object at the beginning of each game being played which
 * contains the difficulty of the game, the duration of the game (based on the difficulty of the game
 * and the duration of the samples placed on the board)
 *
 * @author isak
 */

public class MemGameData {

    private final String TAG = "MemGameData";

    private MatchGame mPlayingMatchGame;
    //Parameters to be saved for database matching game to user
    private long gameStartTimestamp;        //keep track of the timestamp for the start of the game - database primary key
    private String userPlayingName;         //FIXME database foreign key?
    //Parameters to be saved for analysis:
    //defined at MatchDifficultySelectedEvent - the following are fixed for the duration of the game
    private int themeID;                    //set/get string from selection
    private int difficulty;                 //difficulty level for the current game
    private long gameDurationAllocated;     //This is the fixed time allocated for playing the game
    private boolean mixerState;             //keep track of mix state for each game,
    private boolean gameStarted;            //Set to Boolean false, becomes true when first card is clicked - triggers gameStarTimeStamp
    //the following respond to user input during the game
    private int numTurnsTakenInGame;        //Initialize number of turns in game to 0 and increment on each click.
    private ArrayList <Long> gamePlayDurations;  //Time the player spent on the game so far (sum of turnDurations) at each turn (can it be greater than allocated time?)
    private ArrayList <Long> turnDurations;      //a list of durations of each turn - a turn is defined as a single click, implemented as ArrayList //TODO should we also have a measure of paired click turns?
    private ArrayList <Integer> cardSelectedOrder;   //a list of cardData object IDs selected on each turn, implemented as ArrayList
    //TODO should/could we add a array of booleans that tracks whether a match that could be made has been missed? (For now keep this in post)

    //constructor method describes the information that is stored about each game played
    public MemGameData () {
        //Log.d (TAG, "Constructor: initializing game data fields");
        setUserPlayingName(Shared.userData.getUserName());
        //setNumGamesUserPlayed(Shared.userData.sizeOfMemGameDataList());     //FIXME, should this include the current game?, do we need this? or is timestamp enough to solve game play order??
        //
        setThemeID(-1);
        setGameDifficulty(-1);
        setGameDurationAllocated(0);
        setMixerState(false);
        //initialize to 0 or null as necessary
        setGameStarted(false);      //initialize to false on setup
        setGameStartTimestamp(0);
        setNumTurnsTaken(0);
        initTurnDurationsArray();       //null array at start
        initCardsSelectedArray();       //null array at start
        initGamePlayDurationsArray();   //null array at start
        //update before closing and returning MemGameData to UserData
    }

    /*
     * The following are used for key and access when retrieving from that database
     */
    public void setUserPlayingName (String userName) { userPlayingName = userName; }

    public String getUserPlayingName () { return userPlayingName; }

    /*
     * The following are set to game specific values on setup
     */
    //[1] set/get themeID
    public void setThemeID (int theme) {
        //Log.d (TAG, "Overloaded method setThemeID: matchTheme: " + matchTheme);
        themeID = theme;
    }


    public int getThemeID () {
        //Log.d (TAG, "method getThemeID: themeId: " + themeID);
        return themeID;
    }

    //[2] set/get gameDifficulty
    public void setGameDifficulty (int diff) {
        //Log.d (TAG, "Overloaded method setGameDifficulty: difficulty: " + diff);
        difficulty = diff;
    }

    public int getGameDifficulty () {
        //Log.d (TAG, "method getGameDifficulty: difficulty: " + difficulty);
        return difficulty;
    }

    //[3] set/get gameDurationAllocated
    public void setGameDurationAllocated (long gameDuration) {
        //Log.d (TAG, "method setGameDurationAllocated");
        gameDurationAllocated = gameDuration;
    }

    public long getGameDurationAllocated () {
        //Log.d (TAG, "method getGameDuration: this returns the total duration ");
        return gameDurationAllocated;
    }

    //[4] set/get mixerState
    public void setMixerState (boolean state) {
        mixerState = state;
    }

    public boolean getMixerState () {
        //Log.d (TAG, "method getMixerState: mixerState: " + mixerState);
        return mixerState;
    }

    /*
     * The following are set to appropriate null values at startup
     */

    //[1] set/get gameStartTimestamp - this is unique and functions as a key?
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

    //[3] control methods for  gamePlayDurations
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

    //[4] control methods for the turnsDurationArray
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

    //[5] control methods for the cardsSelectedArray
    private void initCardsSelectedArray () {
        //Log.d (TAG, "method initCardsSelectedOrderArray array list");
        cardSelectedOrder = new ArrayList<Integer>();
    }

    public void appendToCardsSelected (int cardId) {
        //Log.d (TAG, "method addCardToCardsSelectedArray");
        cardSelectedOrder.add(cardId);
    }

    public int queryCardsSelectedArray (int locToQuery) {
        //Log.d (TAG, "method queryCardsSelectedArray");
        return cardSelectedOrder.get(locToQuery);
    }

    public int sizeOfCardSelectionArray () {
        //Log.d (TAG, "method sizeOfCardSelectionArray);
        return cardSelectedOrder.size();
    }

    public ArrayList <Integer> getCardsSelectedArray () {
        //
        return cardSelectedOrder;
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
