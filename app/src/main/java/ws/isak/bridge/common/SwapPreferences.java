package ws.isak.bridge.common;

/*
 *
 *
 * @author isak
 */

public class SwapPreferences {

    private static int winningDifficulty;               //the winningDifficulty is easy (order doesn't matter) or hard (order does)

    public static void setWinningDifficulty (int difficulty) {
        //Log.d (TAG, "");
        winningDifficulty = difficulty;
    }

    public static int getWinningDifficulty () {
        //
        return winningDifficulty;
    }
}
