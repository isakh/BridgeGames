package ws.isak.memgamev.model;

import java.util.Map;

import android.graphics.Bitmap;
import android.util.Log;

import ws.isak.memgamev.common.Shared;
import ws.isak.memgamev.themes.MatchThemes;
import ws.isak.memgamev.utils.Utils;
import ws.isak.memgamev.common.CardData;

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
	public Map <Integer, CardData> cardObjs;

	/**
	 * Method getTileBitmap returns a bitmap from a URI associated with a card ID and the settings of
	 * flags which check whether we want the first or second image associated with the card based on
     * the current matchTheme being played.
	 *
	 * @param curTileID
	 *            The id is the number between 0 and number of possible cards of
	 *            this matchTheme i.e. 6 for beginner, 8 for intermediate, & 10 for advanced (i.e. tiles/2)
	 *            FIXME : note if these are to change they must be even
	 * @return A Bitmap from the card to be placed on the tile
	 */

	public Bitmap getTileBitmap(int curTileID, int size) {

		String imageUri = null;		//string to store image uri, varies depending on whether first or second if necessary

		Log.d (TAG, "method getTileBitmap: *** ADDING NEW BITMAP ***");
		Log.d (TAG, "                    : curTileID: " + curTileID + " tile image size: " + size);
		CardData cardOnTile = cardObjs.get(curTileID);
		Log.d (TAG, "                    : cardOnTile id: " + cardOnTile.getCardID());
		Log.d (TAG, "					 : cardOnTile.getPairedImagedDiffer: " + cardOnTile.getPairedImageDiffer());
		Log.d (TAG, " 					 : cardOnTile.getFirstImageUsed: " + cardOnTile.getFirstImageUsed());

        switch (Shared.userData.getCurMemGame().getThemeID()) {
            case 0:                                                                         //MatchTheme is blank
                Log.d (TAG, "method getTileBitmap: switch themeID: " + Shared.userData.getCurMemGame().getThemeID());
                imageUri = cardOnTile.getImageURI0();
                break;
            case 1:                                                                         //MatchTheme is birds
                if (cardOnTile.getPairedImageDiffer() && cardOnTile.getFirstImageUsed()) {  //first card used
                    Log.d (TAG, "method getTileBitmap: switch themeID: " + Shared.userData.getCurMemGame().getThemeID() +
                            " pairedImagesDiffer: " + cardOnTile.getPairedImageDiffer() +
                            " firstImageUsed: " + cardOnTile.getFirstImageUsed());
                    imageUri = cardOnTile.getImageURI2();
                }
                else {
                    Log.d (TAG, "method getTileBitmap: switch themeID: " + Shared.userData.getCurMemGame().getThemeID() +
                            " pairedImagesDiffer: " + cardOnTile.getPairedImageDiffer() +
                            " firstImageUsed: " + cardOnTile.getFirstImageUsed());
                    imageUri = cardOnTile.getImageURI1();                                   //use second card
                    cardOnTile.setFirstImageUsed(true);
                }
                break;
            case 2:                                                                         //MatchTheme is spectrograms
                Log.d (TAG, "method getTileBitmap: switch themeID: " + Shared.userData.getCurMemGame().getThemeID());
                imageUri = cardOnTile.getImageURI3();
                break;
        }
		Log.d (TAG, "					 : imageURI: " + imageUri);
		//Log.d (TAG, "					 : MatchThemes.URI_DRAWABLE: " + MatchThemes.URI_DRAWABLE);
		if (imageUri.contains(MatchThemes.URI_DRAWABLE)) {
			String drawableResourceName = imageUri.substring(MatchThemes.URI_DRAWABLE.length());
			Log.d (TAG, "                : drawableResourceName: " + drawableResourceName);
			int drawableResourceId = Shared.context.getResources().getIdentifier(drawableResourceName, "drawable", Shared.context.getPackageName());
			Log.d (TAG, "                : drawableResourceID: " + drawableResourceId);
			Bitmap bitmap = Utils.scaleDown(drawableResourceId, size, size);
			return Utils.crop(bitmap, size, size);
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