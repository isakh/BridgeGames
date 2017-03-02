package ws.isak.memgamev.model;

import ws.isak.memgamev.R;
import ws.isak.memgamev.common.Shared;
import ws.isak.memgamev.common.Music;

/**
 * Class BoardConfiguration provides a switch for the three difficulty levels and the corresponding
 * number of tiles that are displayed for each.
 *
 * @author isak
 */

public class BoardConfiguration {

	private static final int _12 = 12;
	private static final int _16 = 16;
	private static final int _18 = 18;		//TODO adjust these numbers to relevant limits for resources

	public final int difficulty;
	public final int numTiles;
	public final int numTilesInRow;
	public final int numRows;
	public final long time;					//TODO this will include sample duration variables so will be in millis

	/*
	 * Constructor BoardConfiguration sets up a board with a given difficulty.  For the time being
	 * (and maybe as a permanent solution) TODO redefine the time variable to incorporate the samples as well
	 */
	public BoardConfiguration(int difficulty) {
		this.difficulty = difficulty;
		switch (difficulty) {
		case 1:
			numTiles = _12;
			numTilesInRow = 4;
			numRows = 3;
			time = CalculateGameDuration (numTiles, Shared.context.getResources().getInteger(R.integer.baseline_time_difficulty_easy)));
			break;
		case 2:
			numTiles = _16;
			numTilesInRow = 4;
			numRows = 4;
            time = CalculateGameDuration (numTiles, Shared.context.getResources().getInteger(R.integer.baseline_time_difficulty_medium)));
			break;
		case 3:
			numTiles = _18;
			numTilesInRow = 6;
			numRows = 3;
            time = CalculateGameDuration (numTiles, Shared.context.getResources().getInteger(R.integer.baseline_time_difficulty_hard)));
			break;

		default:
			throw new IllegalArgumentException("Select one of predefined sizes");
		}
	}

    private long CalculateGameDuration (int numTiles, int difficultyBaseline) {
        long cumulativeTime = (long) difficultyBaseline;
        for (int i = 0; i < numTiles/2; i++) {
            cumulativeTime = cumulativeTime + (2 * Music.getAudioDuration(TODO));   //TODO how do we get the duration of audio resources directly from here?
        }
        return  cumulativeTime;
    }
}
