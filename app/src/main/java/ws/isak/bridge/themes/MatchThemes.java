package ws.isak.bridge.themes;

import java.util.ArrayList;
import java.util.Collections;

import android.graphics.Bitmap;

import ws.isak.bridge.common.MatchCardData;
import ws.isak.bridge.common.Shared;
import ws.isak.bridge.utils.ImageScaling;
import ws.isak.bridge.R;

/*
 * The MatchThemes class describes each of the themes.  From the MatchTheme description, each will have a name
 * and ID, a boolean to reflect whether paired image files for the matchTheme are duplicates of the same
 * image file or pairs of images of the same species, and a list of card objects that contain the
 * Urls for the image(s) and audio that match the species described by the card.
 *
 * TODO should we come up with a text list of species of interest so that when image and audio files
 * TODO ... are created for each locale, we can run a find/replace on file names with a list of the
 * TODO ... relevant species for the locale??
 *
 * @author isak
 */

public class MatchThemes {

	private static final String TAG = "MatchThemes";
    public static String URI_DRAWABLE = "drawable://";
    public static String URI_AUDIO = "raw://";

    public static MatchTheme createBlankTheme() {
        MatchTheme matchTheme = new MatchTheme();
        matchTheme.themeID = 0;
        matchTheme.name = Shared.context.getString(R.string.match_themes_blank_name);
        matchTheme.pairedImagesDiffer = false;
        matchTheme.backgroundImageUrl = URI_DRAWABLE + "background_match_blank";
        matchTheme.cardObjs = new ArrayList<MatchCardData>();
        Collections.copy(Shared.matchCardDataList, matchTheme.cardObjs);      //FIXME this isn't necessary - can replace all instances of matchTheme.swapBoardMap with Shared.matchCardDataList
        return matchTheme;
    }

	public static MatchTheme createBirdsTheme() {
		MatchTheme matchTheme = new MatchTheme();
		matchTheme.themeID = 1;
		matchTheme.name = Shared.context.getString(R.string.match_themes_birds_name);
		matchTheme.pairedImagesDiffer = true;
		matchTheme.backgroundImageUrl = URI_DRAWABLE + "background_match_birds";
		matchTheme.cardObjs = new ArrayList<MatchCardData>();		//ArrayList of type MatchCardData
        Collections.copy(Shared.matchCardDataList, matchTheme.cardObjs);
		return matchTheme;
	}

	public static MatchTheme createSpectrogramsTheme() {
		MatchTheme matchTheme = new MatchTheme();
		matchTheme.themeID = 2;
		matchTheme.name = Shared.context.getString(R.string.match_themes_spectrograms_name);
		matchTheme.pairedImagesDiffer = false;
		matchTheme.backgroundImageUrl = URI_DRAWABLE + "background_match_spectrograms";
		matchTheme.cardObjs = new ArrayList<MatchCardData>();		//ArrayList of MatchCardData objects
        Collections.copy(Shared.matchCardDataList, matchTheme.cardObjs);
		return matchTheme;
	}
	
	public static Bitmap getBackgroundImage(MatchTheme matchTheme) {
		String drawableResourceName = matchTheme.backgroundImageUrl.substring(MatchThemes.URI_DRAWABLE.length());
		int drawableResourceId = Shared.context.getResources().getIdentifier(drawableResourceName, "drawable", Shared.context.getPackageName());
		Bitmap bitmap = ImageScaling.scaleDown(drawableResourceId, ImageScaling.screenWidth(), ImageScaling.screenHeight());
		return bitmap;
	}
}