package ws.isak.bridge.model;

import java.util.Map;

import ws.isak.bridge.common.MatchCardData;
import ws.isak.bridge.utils.SwapTileCoordinates;

/**
 * Before game starts, engine builds a new board - this involves setting up the mappings for tiles
 * to id, image, and audio.  The cards can be placed anywhere on the board, for each card, its row
 * and column location need to be known.  When cards are swapped, so are these coordinates.
 *
 * @author isak
 */

public class SwapBoardArrangement {

    public final String TAG = "MatchBoardArrangement";

    //Map of Coordinates objects to card data objects: this tells us where each card is on the board
    //TODO update MatchCardData to contain 4 audio and 4 image files for swap game
    public Map <SwapTileCoordinates, MatchCardData> cardObjs;


}
