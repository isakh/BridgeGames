package ws.isak.bridge.model;

import java.util.Map;

import android.graphics.Bitmap;
import android.util.Log;

import ws.isak.bridge.common.MatchCardData;
import ws.isak.bridge.common.Shared;
import ws.isak.bridge.themes.MatchThemes;
import ws.isak.bridge.utils.ImageScaling;

/**
 * Before game starts, engine builds a new board - this involves setting up the mappings for tiles
 * to id, image, and audio.  This is done by creating a map between location id pairs and a map from
 * a tile id to a card object in the MatchBoardArrangement class.
 *
 * @author isak
 */
public class MatchBoardArrangement {

	public final String TAG = "MatchBoardArrangement";

	// Map pairs of tile IDs (in range of 0 to n-1 tiles
	// like {0-2, 4-3, 1-(n-1}
	public Map<Integer, Integer> pairs;		//This shows where the matched pairs are
	//Map tile IDs to card data objects
	public Map <Integer, MatchCardData> cardObjs;

	/**
	 * Method getMatchTileBitmap returns a bitmap from a URI associated with a card ID and the settings of
	 * flags which check whether we want the first or second image associated with the card based on
     * the current matchTheme being played.
	 *
	 * @param curTileID
	 *            The id is the number between 0 and number of possible cards of
	 *            this matchTheme i.e. 6 for beginner, 8 for intermediate, & 10 for advanced (i.e. tiles/2)
	 *            FIXME : note if these are to change they must be even
	 * @return A Bitmap from the card to be placed on the tile
	 */

	public Bitmap getMatchTileBitmap(int curTileID, int size) {

		String imageUri = null;		//string to store image uri, varies depending on whether first or second if necessary

        MatchCardData cardOnTile = cardObjs.get(curTileID);

        Log.d (TAG, "method getMatchTileBitmap: ADDING NEW BITMAP: curTileID: " + curTileID +
                " | tile image size: " + size + " | cardOnTile id: " + cardOnTile.getCardID() +
		        " | cardOnTile.getPairedImageDiffer: " + cardOnTile.getPairedImageDiffer() +
                " | cardOnTile.getFirstImageUsed: " + cardOnTile.getFirstImageUsed());

        switch (Shared.userData.getCurMatchGame().getThemeID()) {
            case 1:                                                                         //MatchTheme is birds
                if (cardOnTile.getFirstImageUsed()) {  //first card used is true
                    Log.d (TAG, "method getMatchTileBitmap: switch themeID: " + Shared.userData.getCurMatchGame().getThemeID() +
                            " pairedImagesDiffer: " + cardOnTile.getPairedImageDiffer() +
                            " firstImageUsed: " + cardOnTile.getFirstImageUsed());
                    imageUri = cardOnTile.getImageURI2();
                }
                else {
                    Log.d (TAG, "method getMatchTileBitmap: switch themeID: " + Shared.userData.getCurMatchGame().getThemeID() +
                            " pairedImagesDiffer: " + cardOnTile.getPairedImageDiffer() +
                            " firstImageUsed: " + cardOnTile.getFirstImageUsed());
                    imageUri = cardOnTile.getImageURI1();                                   //use second card
                    cardOnTile.setFirstImageUsed(true);
                }
                break;
            case 2:                                                                         //MatchTheme is spectrograms
                Log.d (TAG, "method getMatchTileBitmap: switch themeID: " + Shared.userData.getCurMatchGame().getThemeID());
                imageUri = cardOnTile.getImageURI3();
                break;
            case 3:                                                                         //MatchTheme is blank
                Log.d (TAG, "method getMatchTileBitmap: switch themeID: " + Shared.userData.getCurMatchGame().getThemeID());
                imageUri = cardOnTile.getImageURI0();
                break;
        }
		Log.d (TAG, "					 : imageURI: " + imageUri);
		//Log.d (TAG, "					 : MatchThemes.URI_DRAWABLE: " + MatchThemes.URI_DRAWABLE);
		if (imageUri.contains(MatchThemes.URI_DRAWABLE)) {
			String drawableResourceName = imageUri.substring(MatchThemes.URI_DRAWABLE.length());
			Log.d (TAG, "                : drawableResourceName: " + drawableResourceName);
			int drawableResourceId = Shared.context.getResources().getIdentifier(drawableResourceName, "drawable", Shared.context.getPackageName());
			Log.d (TAG, "                : drawableResourceID: " + drawableResourceId);
			Bitmap bitmap = ImageScaling.scaleDown(drawableResourceId, size, size);
            Bitmap croppedBitmap = ImageScaling.crop(bitmap, size, size);
            Log.d (TAG, "method getMatchTileBitmap: SUCCESS: returning a cropped bitmap: " +
                    croppedBitmap + " | from original bitmap: " + bitmap);
            return croppedBitmap;
        }
		return null;
	}

	/*
	 * Method isPair returns a boolean for the state of equality between two tile id's passed in.
	 */

	public boolean isPair(int id1, int id2) {
		Integer integer = pairs.get(id1);
		if (integer == null) {
			// TODO Report this bug
			return false;
		}
		return integer.equals(id2);
	}

}