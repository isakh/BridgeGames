package ws.isak.memgamev.model;

import android.util.Log;

import java.util.ArrayList;

import ws.isak.memgamev.common.CardData;


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
    private int difficulty;                 //difficulty level for the current game
    private long gameDurationAllocated;     //This is the time allocated for playing the game
    private boolean gameStarted;            //Set to Boolean false, becomes true when first card is clicked - triggers gameStarTimeStamp
    private long gameStartTimestamp;        //keep track of the timestamp for the start of the game
    private ArrayList <Long> turnDurations;             //a list of durations of each turn
    private long gamePlayDuration;          //Time the player spent on the game (sum of turnDurations) (TODO can it be greater than allocated time?)
    private ArrayList <CardData> cardSelectedOrder;   //a list of cardData objects selected on each turn //TODO!!! should type be CardData, or (int) CardData.cardID
    private int numTurnsTakenInGame;        //Initialize number of turns in game to 0 and increment on each click.

    //constructor method describes the information that is stored about each game played
    public MemGameData (Game currentGame) {
        Log.d (TAG, "Constructor: initializing game data fields");
        mPlayingGame = currentGame;
        setGameDifficulty();
        setGameDurationAllocated(mPlayingGame.boardConfiguration.time);
        setGameStarted(false);      //initialize to false on setup
        //initialize to 0 or null as necessary
        setGameStartTimestamp(0);
        setNumTurnsTaken();
        initTurnDurationsArray();
        initCardsSelectedArray();
    }

    //TODO decide if class methods are public or private
    private void setGameDifficulty () {
        //Log.d (TAG, "method setGameDifficulty");
        difficulty = mPlayingGame.boardConfiguration.difficulty;
    }

    public int getGameDifficulty () {
        //Log.d (TAG, "method getGameDifficulty: difficulty: " + difficulty);
        return difficulty;
    }

    public void setGameDurationAllocated (long gameDuration) {
        //Log.d (TAG, "method setGameDurationAllocated");
        gameDurationAllocated = gameDuration;
    }

    public long getGameDurationAllocated () {
        //Log.d (TAG, "method getGameDuration: this returns the total duration ");
        return gameDurationAllocated;
    }

    public void setGameStarted (boolean startedYet) {
        //Log.d (TAG, "method setGameStarted");
        gameStarted = startedYet;
    }

    public boolean isGameStarted () {
        //Log.d (TAG, "method isGameStarted");
        return gameStarted;
    }

    public void setGameStartTimestamp (long gameStartTime) {
        Log.d (TAG, "method setGameStartTimestamp");
        gameStartTimestamp = gameStartTime;
    }

    public long getGameStartTimestamp () {
        Log.d (TAG, "method getGameStartTimestamp");
        return gameStartTimestamp;
    }

    private void initTurnDurationsArray () {
        Log.d (TAG, "method initTurnDurations array list");
        turnDurations = new ArrayList<Long>();
    }

    public void addDurationToTurnDurations (long durToAdd) {
        Log.d (TAG, "method addDurationToTurnDurations");
        turnDurations.add(durToAdd);
    }

    public long queryTurnDurationsArray (int locToQuery) {
        Log.d (TAG, "method queryTurnDurationArray: location to query: " + locToQuery);
        return turnDurations.get(locToQuery);
    }

    public void setGamePlayDuration (long gamePlayStartTime) {            //TODO FIX THIS METHOD
        Log.d (TAG, "method setGamePlayDuration: initialize to 0ms");
        gamePlayDuration = gamePlayStartTime;   //TODO WE NEED ACCESS TO START TIME OF GAME FROM CLOCK
    }

    public long getGamePlayDuration () {
        //
        return gamePlayDuration;
    }

    private void initCardsSelectedArray () {
        Log.d (TAG, "method initCardsSelectedOrderArray array list");
        cardSelectedOrder = new ArrayList<CardData>();
    }

    public void addCardToCardsSelected (CardData cardToAdd) {
        Log.d (TAG, "method addCardToCardsSelectedArray");
        cardSelectedOrder.add(cardToAdd);
    }

    public CardData queryCardsSelectedArray (int locToQuery) {
        Log.d (TAG, "method queryCardsSelectedArray");
        return cardSelectedOrder.get(locToQuery);
    }

    private void setNumTurnsTaken () {
        //Log.d (TAG, "method setNumTurnsTaken: This is called on init as 0");
        numTurnsTakenInGame = 0;
    }

    public void incrementNumTurnsTaken (int numTurnsTaken) {
        Log.d (TAG, "method incrementNumTurnsTaken: prior to increment: " + numTurnsTakenInGame);
        numTurnsTakenInGame = numTurnsTaken++;
        Log.d (TAG, "                             : post increment is: " + numTurnsTakenInGame);
    }

    public int getNumTurnsTaken () {
        //Log.d (TAG, "method getNumTurnsTaken");
        return numTurnsTakenInGame;
    }
}
