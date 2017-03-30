package ws.isak.memgamev.model;

import android.util.Log;

import java.util.ArrayList;

import ws.isak.memgamev.common.CardData;
import ws.isak.memgamev.common.Music;


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

    private final String TAG = "Class: MemGameData";

    private Game mPlayingGame;
    //Parameters to be saved for analysis:
    //defined at DifficultySelectedEvent - the following are fixed for the duration of the game
    private int themeID;                    //set/get string from selection
    private int difficulty;                 //difficulty level for the current game
    private long gameDurationAllocated;     //This is the fixed time allocated for playing the game
    private boolean mixerState;             //keep track of mix state for each game,
    //the following respond to user input during the game
    private boolean gameStarted;            //Set to Boolean false, becomes true when first card is clicked - triggers gameStarTimeStamp
    private long gameStartTimestamp;        //keep track of the timestamp for the start of the game  //TODO move to top (header timestamp is unique)
    private int numTurnsTakenInGame;        //Initialize number of turns in game to 0 and increment on each click.
    private ArrayList <Long> turnDurations; //a list of durations of each turn
    private ArrayList <CardData> cardSelectedOrder;   //a list of cardData objects selected on each turn //TODO!!! should type be CardData, or (int) CardData.cardID
    private long gamePlayDuration;          //Time the player spent on the game (sum of turnDurations) (TODO can it be greater than allocated time?)

    //constructor method describes the information that is stored about each game played
    public MemGameData (Game currentGame) {
        Log.d (TAG, "Constructor: initializing game data fields");
        mPlayingGame = currentGame;
        setThemeID();
        setGameDifficulty();
        setGameDurationAllocated(mPlayingGame.boardConfiguration.time);
        setMixerState();
        //initialize to 0 or null as necessary
        setGameStarted(false);      //initialize to false on setup
        setGameStartTimestamp(0);
        setNumTurnsTaken(0);
        setGamePlayDuration(0);
        initTurnDurationsArray();   //null array at start
        initCardsSelectedArray();   //null array at start
    }

    /*
     * The following are set to game specific values on setup
     */
    //[1] set/get themeID
    public void setThemeID () {
        //Log.d (TAG, "method setThemeID");
        themeID = mPlayingGame.theme.themeID;
    }

    public int getThemeID () {
        //Log.d (TAG, "method getThemeID: themeId: " + themeID);
        return themeID;
    }

    //[2] set/get gameDifficulty
    public void setGameDifficulty () {
        //Log.d (TAG, "method setGameDifficulty");
        difficulty = mPlayingGame.boardConfiguration.difficulty;
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
    public void setMixerState () {
        //Log.d (TAG, "method setMixerState);
        mixerState = Music.MIX;
    }

    public boolean getMixerState () {
        //Log.d (TAG, "method getMixerState: mixerState: " + mixerState);
        return mixerState;
    }

    /*
     * The following are set to appropriate null values at startup
     */
    //[1] set/get gameStarted
    public void setGameStarted (boolean startedYet) {
        //Log.d (TAG, "method setGameStarted");
        gameStarted = startedYet;
    }

    public boolean isGameStarted () {
        //Log.d (TAG, "method isGameStarted");
        return gameStarted;
    }

    //[2] set/get gameStartTimestamp
    public void setGameStartTimestamp (long gameStartTime) {
        Log.d (TAG, "method setGameStartTimestamp");
        gameStartTimestamp = gameStartTime;
    }

    public long getGameStartTimestamp () {
        //Log.d (TAG, "method getGameStartTimestamp");
        return gameStartTimestamp;
    }

    //[3] set/get/increment numTurnsTaken
    private void setNumTurnsTaken (int numTurns) {
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

    //[4] set/get gamePlayDuration
    public void setGamePlayDuration (long gamePlayStartTime) {            //TODO FIX THIS METHOD
        //Log.d (TAG, "method setGamePlayDuration: initialize to 0ms");
        gamePlayDuration = gamePlayStartTime;   //TODO WE NEED ACCESS TO START TIME OF GAME FROM CLOCK
    }

    public long getGamePlayDuration () {
        //Log.d (TAG, "method getGamePlayDuration: gamePlayDuration: " + gamePlayDuration);
        return gamePlayDuration;
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

    public long queryTurnDurationsArray (int locToQuery) {                //TODO this will be used for later analysis only?
        //Log.d (TAG, "method queryTurnDurationArray: location to query: " + locToQuery);
        return turnDurations.get(locToQuery);
    }

    //[6] control methods for the cardsSelectedArray
    private void initCardsSelectedArray () {
        //Log.d (TAG, "method initCardsSelectedOrderArray array list");
        cardSelectedOrder = new ArrayList<CardData>();
    }

    public void appendToCardsSelected (CardData cardToAdd) {
        //Log.d (TAG, "method addCardToCardsSelectedArray");
        cardSelectedOrder.add(cardToAdd);
    }

    public CardData queryCardsSelectedArray (int locToQuery) {
        //Log.d (TAG, "method queryCardsSelectedArray");
        return cardSelectedOrder.get(locToQuery);
    }
}
