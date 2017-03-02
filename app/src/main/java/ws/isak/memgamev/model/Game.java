package ws.isak.memgamev.model;

import ws.isak.memgamev.themes.Theme;

/**
 * This is the instance of an active playing game
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
