package ws.isak.memgamev.model;

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
	public final int time;					//TODO should this be the baseline time for the board difficulty or already include sample duration variables

	public BoardConfiguration(int difficulty) {
		this.difficulty = difficulty;
		switch (difficulty) {
		case 1:
			numTiles = _12;
			numTilesInRow = 4;
			numRows = 3;
			time = 60; //TODO adjust and make a function of audio durations
			break;
		case 2:
			numTiles = _16;
			numTilesInRow = 4;
			numRows = 4;
			time = 90; //TODO adjust and make a function of audio durations
			break;
		case 3:
			numTiles = _18;
			numTilesInRow = 6;
			numRows = 3;
			time = 120; //TODO adjust and make a function of audio durations
			break;

		default:
			throw new IllegalArgumentException("Select one of predefined sizes");
		}
	}
}
