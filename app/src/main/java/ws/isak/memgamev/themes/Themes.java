package ws.isak.memgamev.themes;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Collections;

import android.util.Log;

import android.graphics.Bitmap;

import ws.isak.memgamev.common.Music;
import ws.isak.memgamev.common.Shared;
import ws.isak.memgamev.utils.Utils;
import ws.isak.memgamev.common.CardData;
import ws.isak.memgamev.R;

/*
 * The Themes class describes each of the themes.  From the Theme description, each will have a name
 * and ID, a boolean to reflect whether paired image files for the theme are duplicates of the same
 * image file or pairs of images of the same species, and a list of card objects that contain the
 * Urls for the image(s) and audio that match the species described by the card.
 *
 * TODO should we come up with a text list of species of interest so that when image and audio files
 * TODO ... are created for each locale, we can run a find/replace on file names with a list of the
 * TODO ... relevant species for the locale??
 *
 * @author isak
 */

public class Themes {

	private static final String TAG = "Class: Themes";
    public static String URI_DRAWABLE = "drawable://";
    public static String URI_AUDIO = "raw://";

    public static Theme createBlankTheme() {
        Theme theme = new Theme();
        theme.themeID = 0;
        theme.name = Shared.context.getString(R.string.themes_blank_name);
        theme.pairedImagesDiffer = false;
        theme.backgroundImageUrl = URI_DRAWABLE + "back_blank";
        theme.cardObjs = new ArrayList<CardData>();
        Collections.copy(Shared.cardDataList, theme.cardObjs);      //FIXME this isn't necessary - can replace all instances of theme.cardObjs with Shared.cardDataList
        return theme;
    }

	public static Theme createBirdsTheme() {
		Theme theme = new Theme();
		theme.themeID = 1;
		theme.name = Shared.context.getString(R.string.themes_birds_name);
		theme.pairedImagesDiffer = true;
		theme.backgroundImageUrl = URI_DRAWABLE + "back_birds";
		theme.cardObjs = new ArrayList<CardData>();		//ArrayList of type CardData
        Collections.copy(Shared.cardDataList, theme.cardObjs);
		return theme;
	}

	public static Theme createSpectrogramsTheme() {
		Theme theme = new Theme();
		theme.themeID = 2;
		theme.name = Shared.context.getString(R.string.themes_spectrograms_name);
		theme.pairedImagesDiffer = false;
		theme.backgroundImageUrl = URI_DRAWABLE + "back_spectrograms";
		theme.cardObjs = new ArrayList<CardData>();		//ArrayList of CardData objects
        Collections.copy(Shared.cardDataList, theme.cardObjs);
		return theme;
	}
	
	public static Bitmap getBackgroundImage(Theme theme) {
		String drawableResourceName = theme.backgroundImageUrl.substring(Themes.URI_DRAWABLE.length());
		int drawableResourceId = Shared.context.getResources().getIdentifier(drawableResourceName, "drawable", Shared.context.getPackageName());
		Bitmap bitmap = Utils.scaleDown(drawableResourceId, Utils.screenWidth(), Utils.screenHeight());
		return bitmap;
	}
}