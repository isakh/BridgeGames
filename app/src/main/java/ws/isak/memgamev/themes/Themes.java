package ws.isak.memgamev.themes;

import java.util.ArrayList;
import java.util.Locale;

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
 * TODO (should we come up with a text list of species of interest so that when image and audio files
 * TODO (are created for each locale, we can run a find/replace on file names with a list of the
 * TODO (relevant species for the locale??
 *
 * @author isak
 */

public class Themes {

	private static final String TAG = "Class: Themes";
	public static String URI_DRAWABLE = "drawable://";
	public static String URI_AUDIO = "raw://";

	public static Theme createBirdsTheme() {
		Theme theme = new Theme();
		theme.themeID = 1;
		theme.name = Shared.context.getString(R.string.themes_birds_name);
		theme.pairedImagesDiffer = true;
		theme.backgroundImageUrl = URI_DRAWABLE + "back_birds";
		theme.cardObjs = new ArrayList<CardData>();		//ArrayList of type CardData
		// 10 drawables (max needed for 20 tiles: 4 x 5 board) //TODO update to size max defined in BoardConfiguration
		for (int i = 1; i <= 10; i++) {	//TODO update this with more birds
			//theme.tileImageUrls.add(URI_DRAWABLE + String.format("bird_%d", i) + "a");
			CardData curCard = new CardData();
			curCard.setCardID(i);
            curCard.setSpeciesName(curCard.getCardID());
			curCard.setPairedImageDiffer(true);
			curCard.setFirstImageUsed(false);
			curCard.setImageURI1(URI_DRAWABLE + String.format(Locale.ENGLISH, "bird_%d", i) + "a");
			curCard.setImageURI2(URI_DRAWABLE + String.format(Locale.ENGLISH, "bird_%d", i) + "b");
			curCard.setAudioURI(URI_AUDIO + String.format(Locale.ENGLISH, "example%d", i));
			curCard.setSampleDuration (Music.getAudioDuration(Shared.context.getResources().getIdentifier(curCard.getAudioURI().substring(URI_AUDIO.length()), "raw", Shared.context.getPackageName())));
			Log.d (TAG, "method createBirdsTheme: getCardID: " + curCard.getCardID() + " | getSpeciesName: " + curCard.getSpeciesName() + " | getPairedImageDiffer: " + curCard.getPairedImageDiffer() + " | getFirstImageUsed: " + curCard.getFirstImageUsed());
            Log.d (TAG, "method createBirdsTheme: adding URLS: curCard.setImageURI1: " + URI_DRAWABLE + String.format("bird_%d", i) + "a | curCard.setImageURI2: " + URI_DRAWABLE + String.format("bird_%d", i) + "b" + " | curCard.setAudioURI: " + URI_DRAWABLE + String.format("example%d", i) + " | curCard.getSampleDuration: " + curCard.getSampleDuration());
			theme.cardObjs.add(curCard);
			}
		return theme;
	}

	public static Theme createSpectrogramsTheme() {
		Theme theme = new Theme();
		theme.themeID = 2;
		theme.name = Shared.context.getString(R.string.themes_spectrograms_name);
		theme.pairedImagesDiffer = false;
		theme.backgroundImageUrl = URI_DRAWABLE + "back_spectrograms";
		theme.cardObjs = new ArrayList<CardData>();		//ArrayList of CardData objects
		// 10 drawables (max needed for 20 tiles: 4 x 5 board) //TODO update to size max
		for (int i = 1; i <= 10; i++) {
			CardData curCard = new CardData();
			curCard.setCardID(i);
            curCard.setSpeciesName(curCard.getCardID());
            curCard.setPairedImageDiffer(false);		//cards in this set only have one image
			curCard.setImageURI1(URI_DRAWABLE + String.format("spectrogram_%d", i));
			curCard.setAudioURI(URI_AUDIO + String.format("example%d", i));
            curCard.setSampleDuration (Music.getAudioDuration(Shared.context.getResources().getIdentifier(curCard.getAudioURI().substring(URI_AUDIO.length()), "raw", Shared.context.getPackageName())));
            Log.d (TAG, "method createBirdsTheme: getCardID: " + curCard.getCardID() + " | getSpeciesName: " + curCard.getSpeciesName() + " | getPairedImageDiffer: " + curCard.getPairedImageDiffer() + " | getFirstImageUsed: " + curCard.getFirstImageUsed());
            Log.d (TAG, "method createSpectrogramsTheme: adding URLS: curCard.setImageURI1: " + URI_DRAWABLE + String.format("spectrogram_%d", i) + " | curCard.setAudioURI: " + URI_DRAWABLE + String.format("example%d", i) + " | curCard.getSampleDuration: " + curCard.getSampleDuration());
			theme.cardObjs.add(curCard);
		}
		return theme;
	}
	
	public static Bitmap getBackgroundImage(Theme theme) {
		String drawableResourceName = theme.backgroundImageUrl.substring(Themes.URI_DRAWABLE.length());
		int drawableResourceId = Shared.context.getResources().getIdentifier(drawableResourceName, "drawable", Shared.context.getPackageName());
		Bitmap bitmap = Utils.scaleDown(drawableResourceId, Utils.screenWidth(), Utils.screenHeight());
		return bitmap;
	}
}