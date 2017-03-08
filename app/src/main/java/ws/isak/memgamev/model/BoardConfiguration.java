package ws.isak.memgamev.model;

import android.util.Log;

import ws.isak.memgamev.R;
import ws.isak.memgamev.common.Shared;
import ws.isak.memgamev.themes.Theme;

/**
 * Class BoardConfiguration provides a switch for the three difficulty levels and the corresponding
 * number of tiles that are displayed for each.
 *
 * @author isak
 */

public class BoardConfiguration {

    private static final String TAG = "Class: BoardConfig";

    private static final int easy = Shared.context.getResources().getInteger(R.integer.board_size_easy);
	private static final int medium = Shared.context.getResources().getInteger(R.integer.board_size_intermediate);
	private static final int hard = Shared.context.getResources().getInteger(R.integer.board_size_hard);

	public final int difficulty;
	public final int numTiles;
	public final int numTilesInRow;
	public final int numRows;
	public final long time;					//TODO this will include sample duration variables so will be in millis - need to use floor (time/1000) for timer in UI

	/*
	 * Constructor BoardConfiguration sets up a board with a given difficulty.  For the time being
	 * this includes a call to CalculateGameDuration which queries the number of tiles on the board
	 * and determines how many pairs of cards are place and the duration of the samples associated
	 * with those cards.
	 */
	public BoardConfiguration(int difficulty, Theme theme) {
        Log.d (TAG, "constructor: difficulty: " + difficulty + " | theme.name: " + theme.name);
		this.difficulty = difficulty;
		switch (difficulty) {
		case 1:
			numTiles = easy;
			numTilesInRow = 4;
			numRows = 3;
			time = CalculateGameDuration (numTiles, Shared.context.getResources().getInteger(R.integer.baseline_time_difficulty_easy), theme);
			break;
		case 2:
			numTiles = medium;
			numTilesInRow = 4;
			numRows = 4;
            time = CalculateGameDuration (numTiles, Shared.context.getResources().getInteger(R.integer.baseline_time_difficulty_medium), theme);
			break;
		case 3:
			numTiles = hard;
			numTilesInRow = 6;
			numRows = 3;
            time = CalculateGameDuration (numTiles, Shared.context.getResources().getInteger(R.integer.baseline_time_difficulty_hard), theme);
			break;

		default:
			throw new IllegalArgumentException("Select one of predefined sizes");
		}
	}

    private long CalculateGameDuration (int numTiles, int difficultyBaseline, Theme theme) {
        Log.d (TAG, "method CalculateGameDuration: numTiles: " + numTiles + " | difficultyBaseLine: " + difficultyBaseline + " | theme.name: " + theme.name);
        long cumulativeTime = (long) difficultyBaseline;
        for (int i = 0; i < numTiles/2; i++) {
            //Log.d (TAG, "method CalculateGameDuration: theme.cardObjs.get(i).getCardId(): " + theme.cardObjs.get(i).getCardID());
            long curCardDur = theme.cardObjs.get(i).getSampleDuration();
            Log.d (TAG, "                            : theme.cardObjs.get(i).getSampleDuration(): " + theme.cardObjs.get(i).getSampleDuration());
            cumulativeTime = cumulativeTime + (2 * curCardDur);
            Log.d (TAG, "                            : cumulativeTime: " + cumulativeTime);
        }
        Log.d (TAG, "method CalculateGameDuration: totalGameTime: " + cumulativeTime);
        return  cumulativeTime;
    }
}
