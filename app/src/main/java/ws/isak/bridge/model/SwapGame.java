package ws.isak.bridge.model;

import ws.isak.bridge.utils.Clock;

/**
 * This is the instance of an active playing swap game.  A given game is comprised of: a board
 * configuration which is defined by the dimensions of the board based on the difficultyLevel selected;
 * a board arrangement which covers mapping tile IDs to bitmaps; and game state which tracks the
 * current performance in terms of time, stars earned, and corresponding score.
 *
 * @author isak
 */
public class SwapGame {

    public SwapBoardConfiguration swapBoardConfiguration;
    public SwapBoardArrangement swapBoardArrangement;
    public GameState gameState;
    public Clock gameClock;
}
