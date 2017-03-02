package ws.isak.memgamev.model;

import ws.isak.memgamev.themes.Theme;

/**
 * This is the instance of an active playing game.  A given game is comprised of: a board configuration
 * which is defined by the dimensions of the board based on the difficulty selected; a board
 * arrangement which covers mapping tile pairs and tile IDs to bitmaps; a theme which defines the
 * set of images (and whether they are identical pairs or non-identical) and audio corresponding to
 * the theme; and game state which tracks the current performance in terms of time, stars earned, and
 * corresponding score.
 *
 * @author isak
 */
public class Game {

	/**
	 * The board configuration
	 */
	public BoardConfiguration boardConfiguration;

	/**
	 * The board arrangement
	 */
	public BoardArrangement boardArrangement;

	/**
	 * The selected theme
	 */
	public Theme theme;

	public GameState gameState;

}
