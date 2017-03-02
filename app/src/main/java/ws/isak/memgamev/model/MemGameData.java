package ws.isak.memgamev.model;

import android.util.Log;

import java.util.ArrayList;

import ws.isak.memgamev.model.Game;
import ws.isak.memgamev.model.BoardConfiguration;

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

    private final String TAG = "Class: GameData";

    private Game mPlayingGame;
    private int difficulty;
    private long gameDurationAllocated;     //This is the time allocated for playing the game
    private long gamePlayDuration;          //Time the player spent on the game (sum of turnDurations) (TODO can it be greater than allocated?)
    private int numTurnsTakenInGame;        //Initialize number of turns in game to 0 and increment on each click.
    private ArrayList <Long> turnDurations;

    //constructor method
    public MemGameData (Game currentGame) {
        Log.d (TAG, "Constructor: initializing game data fields");
        mPlayingGame = currentGame;
        setGameDifficulty();
        setGameDuration();
        setNumTurnsTaken();
        initTurnDurationsArray();
    }

    private void setGameDifficulty () {
        Log.d (TAG, "method setGameDifficulty");
        difficulty = mPlayingGame.boardConfiguration.difficulty;
    }

    private int getGameDifficulty () {
        Log.d (TAG, "method getGameDifficulty: difficulty: " + difficulty);
        return difficulty;
    }

    private void setGameDuration () {
        //TODO figure our how to pass in the total time for the current game to last
    }

    private long getGameDuration () {
        Log.d (TAG, "method getGameDuration: this returns the total duration ")
        return gameDurationAllocated;
    }

    private void setNumTurnsTaken () {
        Log.d (TAG, "method setNumTurnsTaken: This is called once on init and is 0");
        numTurnsTakenInGame = 0;
    }

    private void incrementNumTurnsTaken (int numTurnsTaken) {
        Log.d (TAG, "method incrementNumTurnsTaken: prior to increment: " + numTurnsTakenInGame)
        numTurnsTakenInGame = numTurnsTaken++;
        Log.d (TAG, "                             : post increment is: " + numTurnsTakenInGame);
    }

    private int getNumTurnsTaken () {
        Log.d (TAG, "method getNumTurnsTaken");
        return numTurnsTakenInGame;
    }


    private void initTurnDurationsArray () {
        Log.d (TAG, "method initTurnDurations array list");
        turnDurations = new ArrayList<Long>();
    }

    private void addDurationToTurnDurations (long durToAdd) {
        Log.d (TAG, "method addDurationToTurnDurations");
        turnDurations.add(durToAdd);
    }

    private long queryTurnDurationsArray (int locToQuery) {
        Log.d (TAG, "method queryTurnDurationArray: location to query: " + locToQuery);
        return turnDurations.get(locToQuery);
    }
}
