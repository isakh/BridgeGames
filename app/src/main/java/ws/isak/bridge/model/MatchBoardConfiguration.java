package ws.isak.bridge.model;

import android.util.Log;

import ws.isak.bridge.R;
import ws.isak.bridge.common.Shared;
import ws.isak.bridge.themes.MatchTheme;

/**
 * Class MatchBoardConfiguration provides a switch for the three difficulty levels and the corresponding
 * number of tiles that are displayed for each.
 *
 * @author isak
 */

public class MatchBoardConfiguration {

    private static final String TAG = "MatchBoardConfiguration";

    private static final int easy = Shared.context.getResources().getInteger(R.integer.match_board_size_easy);
	private static final int medium = Shared.context.getResources().getInteger(R.integer.match_board_size_intermediate);
	private static final int hard = Shared.context.getResources().getInteger(R.integer.match_board_size_hard);

	public final int difficulty;
	public final int numTiles;
	public final int numTilesInRow;
	public final int numRows;
	public final long time;					//TODO this will include sample duration variables so will be in millis - need to use floor (time/1000) for timer in UI

	/*
	 * Constructor MatchBoardConfiguration sets up a board with a given difficulty.  For the time being
	 * this includes a call to CalculateMatchGameDuration which queries the number of tiles on the board
	 * and determines how many pairs of cards are place and the duration of the samples associated
	 * with those cards.
	 */
	public MatchBoardConfiguration(int difficulty, MatchTheme matchTheme) {
        Log.d (TAG, "constructor: difficulty: " + difficulty + " | matchTheme.name: " + matchTheme.name);
		this.difficulty = difficulty;
		switch (difficulty) {
		case 1:
			numTiles = easy;
			numTilesInRow = 4;
			numRows = 3;
			time = CalculateMatchGameDuration (numTiles, Shared.context.getResources().getInteger(R.integer.baseline_match_time_difficulty_easy), matchTheme);
			break;
		case 2:
			numTiles = medium;
			numTilesInRow = 4;
			numRows = 4;
            time = CalculateMatchGameDuration (numTiles, Shared.context.getResources().getInteger(R.integer.baseline_match_time_difficulty_medium), matchTheme);
			break;
		case 3:
			numTiles = hard;
			numTilesInRow = 5;
			numRows = 4;
            time = CalculateMatchGameDuration (numTiles, Shared.context.getResources().getInteger(R.integer.baseline_match_time_difficulty_hard), matchTheme);
			break;

		default:
			throw new IllegalArgumentException("Select one of predefined sizes");
		}
	}

	//TODO should we have a theme offset for time, i.e. blank gets longer for example?? FIXME
    private long CalculateMatchGameDuration (int numTiles, int difficultyBaseline, MatchTheme matchTheme) {
        Log.d (TAG, "method CalculateMatchGameDuration: numTiles: " + numTiles + " | difficultyBaseLine: " + difficultyBaseline + "ms | matchTheme.name: " + matchTheme.name);
        long cumulativeTime = (long) difficultyBaseline;
        for (int i = 0; i < numTiles/2; i++) {
            //Log.d (TAG, "method CalculateMatchGameDuration: matchTheme.cardObjs.get(i).getCardId(): " + matchTheme.cardObjs.get(i).getCardID());
            long curCardDur = Shared.matchCardDataList.get(i).getSampleDuration();       //FIXME !!!!!!!!!! WAS matchTheme.cardObjs instead of Shared.matchCardDataList, if this works we can get rid of field in MatchTheme
            //Log.d (TAG, "                            : matchTheme.cardObjs.get(i).getSampleDuration(): " + matchTheme.cardObjs.get(i).getSampleDuration());
            cumulativeTime = cumulativeTime + (2 * curCardDur);
            //Log.d (TAG, "                            : cumulativeTime: " + cumulativeTime);
        }
        Log.d (TAG, "method CalculateMatchGameDuration: totalGameTime: " + cumulativeTime + "ms");
        return  cumulativeTime;
    }
}