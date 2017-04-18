package ws.isak.bridge.model;

import java.util.HashMap;
import java.util.Iterator;
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
 * and column location need to be known.  When cards are swapped, so are these coordinates.
 *
 * @author isak
 */

public class SwapBoardArrangement {

    public final String TAG = "SwapBoardArrangement";
    public static String URI_DRAWABLE = "drawable://";


    //Map of Coordinates objects to card data objects: this tells us where each card is on the board
    //TODO update SwapCardData to contain 4 audio and 4 image files for swap game

    public Map <SwapTileCoordinates, SwapCardData> cardObjs = new HashMap<>();

    public void setCardOnBoard (SwapTileCoordinates coords, SwapCardData card) {
        Log.d (TAG, "method setCardOnBoard: tile coords: < " + coords.getSwapCoordRow() + " ,  " +
                    coords.getSwapCoordCol() + " > | cardID: < " + card.getCardID().getSwapCardSpeciesID() +
                    " , " + card.getCardID().getSwapCardSegmentID() + " >");
        cardObjs.put(coords, card);
    }

    public SwapCardData getSwapCardDataFromCoords (SwapTileCoordinates loc) {
        Log.d (TAG, "method getSwapCardDataFromCoords: key loc: < " + loc.getSwapCoordRow() + "," + loc.getSwapCoordCol() + " >");
        SwapCardData cardOnTile = null;
        Iterator iterator = Shared.currentSwapGame.swapBoardArrangement.cardObjs.entrySet().iterator();
        Log.d (TAG, "   created Iterator");
        while (iterator.hasNext()) {
            Log.d (TAG, "   Iterating over cardObjs to find match");
            Map.Entry pair = (Map.Entry)iterator.next();
            System.out.println(pair.getKey() + " maps to " + pair.getValue());
            SwapTileCoordinates coords = (SwapTileCoordinates) pair.getKey();
            Log.d (TAG, "           key coords: < " + coords.getSwapCoordRow() + "," + coords.getSwapCoordCol() + " >");
            SwapCardData cardData = (SwapCardData) pair.getValue();
            Log.d (TAG, "           value card ID: < " + cardData.getCardID().getSwapCardSpeciesID() + "," + cardData.getCardID().getSwapCardSegmentID() + " >");
            if (coords == loc) {
                Log.d (TAG, "   *** coords == loc *** ");
                cardOnTile = cardData;
            }
            iterator.remove(); // avoids a ConcurrentModificationException
        }
        Log.d (TAG, "       : returning cardOnTile: < " + cardOnTile.getCardID().getSwapCardSpeciesID() +
                    "," + cardOnTile.getCardID().getSwapCardSegmentID() + " >");
        return cardOnTile;
    }

    //return the bitmap at location on board with given size
    public Bitmap getSwapTileBitmap (SwapTileCoordinates loc, int size) {
        Log.d (TAG, "method getSwapTileBitmap: target location: <" + loc.getSwapCoordRow() +
                    "," + loc.getSwapCoordCol() + " > | size: " + size );
        String imageURI = null;
        SwapCardData cardOnTile = getSwapCardDataFromCoords(loc);
        Log.d (TAG, "method getSwapTileBitma: cardOnTile: < " + cardOnTile.getCardID().getSwapCardSpeciesID() +
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
            Log.d (TAG, "                       : drawableResourceID: " + drawableResourceID);
            Bitmap bitmap = ImageScaling.scaleDown(drawableResourceID, size, size);
            return ImageScaling.crop(bitmap, size, size);
        }
        return null;
    }
}