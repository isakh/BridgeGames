package ws.isak.bridge.model;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import android.graphics.Bitmap;

import ws.isak.bridge.common.Shared;
import ws.isak.bridge.common.SwapCardData;
import ws.isak.bridge.utils.ImageScaling;
import ws.isak.bridge.utils.SwapTileCoordinates;

/**
 * Before game starts, engine builds a new board - this involves setting up the mappings for tiles
 * to id, image, and audio.  The cards can be placed anywhere on the board, for each card, its row
 * and column location need to be known.  When cards are swapped, so are these coordinates.  The
 * swapBoardMap that is introduced here is organized initially by the Engine in the method
 * arrangeBoard - when it has been created, it is then passed by reference to the curSwapBoardMap
 * Map which is stored in the instance of SwapGameData created for Shared.userData.getCurSwapGameData
 *
 * @author isak
 */

public class SwapBoardArrangement {

    public final String TAG = "SwapBoardArrangement";
    public static String URI_DRAWABLE = "drawable://";
    public int tileSize;


    //Map of Coordinates objects to card data objects: this tells us where each card is on the board

    public Map <SwapTileCoordinates, SwapCardData> swapBoardMap = new HashMap<>();

    public void setCardOnBoard (SwapTileCoordinates coords, SwapCardData card) {
        Log.d (TAG, "   *** method setCardOnBoard: coords @: " + coords + " | tile coords: < " +
                    coords.getSwapCoordRow() + "," + coords.getSwapCoordCol() + " > | cardID: < " +
                    card.getCardID().getSwapCardSpeciesID() + " , "
                    + card.getCardID().getSwapCardSegmentID() + " > | card @: " + card);
        swapBoardMap.put(coords, card);
    }

    //return the bitmap at location on board with given size
    public Bitmap getSwapTileBitmap (SwapTileCoordinates loc, int size) {
        Log.d (TAG, "method getSwapTileBitmap: loc @: " + loc + " | target location: < " + loc.getSwapCoordRow() +
                    "," + loc.getSwapCoordCol() + " > | size: " + size );
        String imageURI = null;
        SwapCardData cardOnTile = Shared.userData.getCurSwapGameData().getSwapCardDataFromSwapBoardMap(loc);
        Log.d (TAG, "method getSwapTileBitmap: getSwapTileCoords from Map: " +
                    Shared.userData.getCurSwapGameData().getSwapCardDataFromSwapBoardMap(loc) +
                    " | cardOnTile: " +cardOnTile + " | cardOnTile IDs: < " +
                    cardOnTile.getCardID().getSwapCardSpeciesID() +
                    "," + cardOnTile.getCardID().getSwapCardSegmentID() + " >");

        switch (cardOnTile.getCardID().getSwapCardSegmentID()) {
            case 0:
                imageURI = cardOnTile.getSpectroURI0();
                break;
            case 1:
                imageURI = cardOnTile.getSpectroURI1();
                break;
            case 2:
                imageURI = cardOnTile.getSpectroURI2();
                break;
            case 3:
                imageURI = cardOnTile.getSpectroURI3();
                break;
        }
        Log.d (TAG, "method getSwapTileBitmap: imageURI: " + imageURI);
        if (imageURI.contains(URI_DRAWABLE)) {
            String drawableResourceName = imageURI.substring(URI_DRAWABLE.length());
            Log.d (TAG, "                       : drawableResourceName: " + drawableResourceName);
            int drawableResourceID = Shared.context.getResources().getIdentifier(drawableResourceName, "drawable", Shared.context.getPackageName());
            //Log.d (TAG, "                       : drawableResourceID: " + drawableResourceID);
            Bitmap bitmap = ImageScaling.scaleDown(drawableResourceID, size, size);
            Bitmap croppedBitmap = ImageScaling.crop(bitmap, size, size);
            Log.d (TAG, "method getSwapTileBitmap: SUCCESS: returning a cropped bitmap: " +
                    croppedBitmap + " | from original bitmap: " + bitmap);
            return croppedBitmap;
        }
        Log.d (TAG, "method getSwapTileBitmap: ERROR: returning no bitmap");
        return null;
    }

    public void setTileSize (int size) { tileSize = size; }

    public int getTileSize () { return tileSize; }
}