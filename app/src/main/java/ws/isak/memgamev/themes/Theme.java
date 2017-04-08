package ws.isak.memgamev.themes;

import java.util.ArrayList;

import ws.isak.memgamev.common.CardData;

public class Theme {

	public int themeID;						//Each Theme has a numeric ID
	public String name;						//and each Theme has a name
	public String backgroundImageUrl;		//this is the board background image for the theme
	public boolean pairedImagesDiffer;		//this boolean tracks whether paired images are identical
	public ArrayList<CardData> cardObjs;	//Theme contains a list of card objects where each of
											//the card objects links for all possible image urls and an audio url
											//as well as flags for the current state of the card
}
