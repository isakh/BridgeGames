package ws.isak.bridge.model;

import ws.isak.bridge.themes.MatchTheme;
import ws.isak.bridge.utils.Clock;

/**
 * This is the instance of an active playing match game.  A given game is comprised of: a board configuration
 * which is defined by the dimensions of the board based on the difficultyLevel selected; a board
 * arrangement which covers mapping tile pairs and tile IDs to bitmaps; a matchTheme which defines the
 * set of images (and whether they are identical pairs or non-identical) and audio corresponding to
 * the matchTheme; and game state which tracks the current performance in terms of time, stars earned, and
 * corresponding score.
 *
 * @author isak
 */
public class MatchGame {

	public MatchBoardConfiguration matchBoardConfiguration;
	public MatchBoardArrangement matchBoardArrangement;
	public MatchTheme matchTheme;
	public GameState gameState;
    public Clock gameClock;

}
