package ws.isak.bridge.model;

import android.util.Log;

import ws.isak.bridge.R;
import ws.isak.bridge.common.Shared;

/**
 * Class SwapBoardConfiguration provides a switch for the three difficulty levels and the corresponding
 * number of tiles that are displayed for each.
 *
 * @author isak
 */

public class SwapBoardConfiguration {

    private static final String TAG = "SwapBoardConfiguration";

    private static final int easy = Shared.context.getResources().getInteger(R.integer.swap_board_size_easy);
    private static final int medium = Shared.context.getResources().getInteger(R.integer.swap_board_size_intermediate);
    private static final int hard = Shared.context.getResources().getInteger(R.integer.swap_board_size_hard);

    public static final int swapNumTilesInRow = Shared.context.getResources().getInteger(R.integer.swap_board_num_tiles_in_row);

    public int difficulty;
    public final int numTiles;
    public final int numRows;
    public final long time;					//TODO this will include sample duration variables so will be in millis - need to use floor (time/1000) for timer in UI

    /*
     * Constructor SwapBoardConfiguration sets up a blank board with a given difficulty (i.e. defines
     * the number of spaces on the board where tiles will go, additionally difficulty can be used to
     * define the number of species of interest.  For the time being this includes a call to
     * CalculateSwapGameDuration which queries the number of tiles on the board and determines how
     * many sets of cards are place and the duration of the samples associated with those cards.
     */
    public SwapBoardConfiguration(int difficulty) {
        Log.d (TAG, "constructor: difficulty: " + difficulty);
        setSwapDifficulty (difficulty);
        switch (getSwapDifficulty()) {
            case 1:
                numTiles = easy;
                numRows = 2;
                time = CalculateSwapGameDuration (numTiles, Shared.context.getResources().getInteger(R.integer.baseline_swap_time_difficulty_easy));
                break;
            case 2:
                numTiles = medium;
                numRows = 3;
                time = CalculateSwapGameDuration (numTiles, Shared.context.getResources().getInteger(R.integer.baseline_swap_time_difficulty_medium));
                break;
            case 3:
                numTiles = hard;
                numRows = 4;
                time = CalculateSwapGameDuration (numTiles, Shared.context.getResources().getInteger(R.integer.baseline_swap_time_difficulty_hard));
                break;

            default:
                throw new IllegalArgumentException("Select one of the predefined sizes");
        }
    }

    public void setSwapDifficulty (int diff) {
        difficulty = diff;
    }

    public int getSwapDifficulty () {
        return difficulty;
    }


    //FIXME - this needs to reflect the limitation that the sum of tiles in a row is fixed - so constant
    private long CalculateSwapGameDuration (int numTiles, int difficultyBaseline) {
        Log.d (TAG, "method CalculateSwapGameDuration: numTiles: " + numTiles + " | difficultyBaseLine: " + difficultyBaseline);
        long cumulativeTime = (long) difficultyBaseline;
        for (int i = 0; i < numTiles/2; i++) {
            //Log.d (TAG, "method CalculateSwapGameDuration: swapTheme.cardObjs.get(i).getCardId(): " + swapTheme.cardObjs.get(i).getCardID());
            long curCardDur = Shared.matchCardDataList.get(i).getSampleDuration();       //FIXME !!!!!!!!!! WAS swapTheme.cardObjs instead of Shared.matchCardDataList, if this works we can get rid of field in SwapTheme
            //Log.d (TAG, "                            : swapTheme.cardObjs.get(i).getSampleDuration(): " + swapTheme.cardObjs.get(i).getSampleDuration());
            cumulativeTime = cumulativeTime + (2 * curCardDur);
            //Log.d (TAG, "                            : cumulativeTime: " + cumulativeTime);
        }
        Log.d (TAG, "method CalculateSwapGameDuration: totalGameTime: " + cumulativeTime + "ms");
        return  cumulativeTime;
    }
}
