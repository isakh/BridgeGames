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
    private long gameDuration;
    private int numTurnsTakenInGame = 0;
    private ArrayList <Long> turnDurations = new ArrayList<Long>();

    //constructor method
    public MemGameData (Game currentGame) {
        Log.d (TAG, "Constructor: initializing game data fields");
        mPlayingGame = currentGame;
        setGameDifficulty();
        setGameDuration();
        setNumTurnsTaken();
        initTurnDurations();
    }

    private void setGameDifficulty () {
        difficulty = mPlayingGame.boardConfiguration.difficulty;
    }

    private int getGameDifficulty () {
        return difficulty;
    }

    private void setGameDuration () {
        //TODO figure our how to pass in the total time for the current game to last
    }

    private long getGameDuration () {
        return gameDuration;
    }

    private void setNumTurnsTaken () {}

    private void initTurnDurations () {}
}
