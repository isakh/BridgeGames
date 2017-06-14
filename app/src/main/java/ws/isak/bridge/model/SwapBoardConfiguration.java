package ws.isak.bridge.model;

import android.util.Log;

import ws.isak.bridge.R;
import ws.isak.bridge.common.Shared;
import ws.isak.bridge.common.SwapCardData;
import ws.isak.bridge.utils.SwapTileCoordinates;

/**
 * Class SwapBoardConfiguration. It provides a switch for the three difficultyLevel levels and the
 * corresponding number of tiles that are displayed for each. This class defines the configuration
 * for the board for each game being played by the user.  It is initially set in the engine in method
 * onEvent (SwapGameDifficultyEvent event) where it is passed the difficulty from the users' selection.
 *
 * @author isak
 */

public class SwapBoardConfiguration {

    private static final String TAG = "SwapBoardConfiguration";

    private static final int easy = Shared.context.getResources().getInteger(R.integer.swap_board_size_easy);
    private static final int medium = Shared.context.getResources().getInteger(R.integer.swap_board_size_intermediate);
    private static final int hard = Shared.context.getResources().getInteger(R.integer.swap_board_size_hard);

    public static final int swapNumTilesInRow = Shared.context.getResources().getInteger(R.integer.swap_board_num_tiles_in_row);

    public int difficultyLevel;             //this is the difficulty 1-3
    public int numSpecies;
    public final int numTiles;
    public final int numRows;
    public long time;                    //TODO this will include sample duration variables so will be in millis - need to use floor (time/1000) for timer in UI

    /*
     * Constructor SwapBoardConfiguration sets up a blank board with a given difficultyLevel (i.e. defines
     * the number of spaces on the board where tiles will go, additionally difficultyLevel can be used to
     * define the number of species of interest.  For the time being this includes a call to
     * CalculateSwapGameDuration which queries the number of tiles on the board and determines how
     * many sets of cards are place and the duration of the samples associated with those cards.
     */
    public SwapBoardConfiguration(int difficultyLevel) {
        Log.d(TAG, "constructor: difficultyLevel: " + difficultyLevel);
        setNumSpecies(difficultyLevel);  //FIXME make less of a hack - the difficultyLevel mode is one less than the number of species
        setSwapDifficulty(difficultyLevel);
        switch (getSwapDifficulty()) {
            case 1:
                numTiles = easy;
                numRows = 2;
                setGameTime (CalculateSwapGameDuration(numTiles, Shared.context.getResources().getInteger(R.integer.baseline_swap_time_difficulty_easy)));
                break;
            case 2:
                numTiles = medium;
                numRows = 3;
                setGameTime (CalculateSwapGameDuration(numTiles, Shared.context.getResources().getInteger(R.integer.baseline_swap_time_difficulty_medium)));
                break;
            case 3:
                numTiles = hard;
                numRows = 4;
                setGameTime (CalculateSwapGameDuration(numTiles, Shared.context.getResources().getInteger(R.integer.baseline_swap_time_difficulty_hard)));
                break;

            default:
                throw new IllegalArgumentException("Select one of the predefined sizes");
        }
    }

    //set/get the game time
    public void setGameTime (long t) {
        time = t;
    }

    public long getGameTime () {
        return time;
    }

    //set by user (value 1-3)
    public void setSwapDifficulty(int diff) {
        difficultyLevel = diff;
    }

    public int getSwapDifficulty() {
        return difficultyLevel;
    }

    //always one more than difficulty level for the time being
    public void setNumSpecies(int diff) {
        Log.d(TAG, "method setNumSpecies: we need difficulty level + 1 species: difficulty: " + diff);
        numSpecies = diff + 1;
    }

    public int getNumSpecies() {
        return numSpecies;
    }

    /*  FIXME !! this is a better way to sort out timing, however, we need this after the board has been arranged and Shared.
    //total time of tiles on board - this version wont work here as the tiles haven't yet been laid on the board
    private long CalculateSwapGameDuration (int numTiles, int difficultyBaseline) {
        Log.d (TAG, "method CalculateSwapGameDuration: numTiles: " + numTiles + " | difficultyBaseLine: " + difficultyBaseline);
        long cumulativeTime = (long) difficultyBaseline;
        for (int i = 0; i < getNumSpecies(); i++) {
            for (int j = 0; j < swapNumTilesInRow; j++) {
                SwapTileCoordinates tempCoords = new SwapTileCoordinates(i, j);
                SwapTileCoordinates targetCoords = Shared.userData.getCurSwapGameData().getMapSwapTileCoordinatesFromLoc(tempCoords);
                SwapCardData targetData = Shared.userData.getCurSwapGameData().getSwapCardDataFromSwapBoardMap(targetCoords);
                long cardDur = 0;
                switch (targetData.getCardIDKey().getSwapCardSegmentID()) {
                    case 0:
                        cardDur = targetData.getSampleDuration0();
                        break;
                    case 1:
                        cardDur = targetData.getSampleDuration1();
                        break;
                    case 2:
                        cardDur = targetData.getSampleDuration2();
                        break;
                    case 3:
                        cardDur = targetData.getSampleDuration3();
                        break;
                }
                cumulativeTime += cardDur;
                Log.d (TAG, "method CalculateSwapGameDuration: cardDur: " + cardDur + " added ... " +
                            " | cumulativeTime now: " + cumulativeTime);
            }

        }
        Log.d (TAG, "method CalculateSwapGameDuration: totalGameTime: " + cumulativeTime + "ms");
        return  cumulativeTime;
    }
    */

    private long CalculateSwapGameDuration(int numTiles, int difficultyBaseline) {
        Log.d(TAG, "method CalculateSwapGameDuration: numTiles: " + numTiles + " | difficultyBaseLine: " + difficultyBaseline);
        long cumulativeTime = (long) difficultyBaseline;
        cumulativeTime += numTiles * 2120; //FIXME this is a hack for now - each sample should be 2.12 seconds? or get from tiles??
        Log.d(TAG, "method CalculateSwapGameDuration: totalGameTime: " + cumulativeTime + "ms");
        cumulativeTime = (cumulativeTime * (Shared.context.getResources().getInteger(R.integer.swap_timer_multiplier_percent) / 100));
        return cumulativeTime;
    }
}