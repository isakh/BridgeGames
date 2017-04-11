package ws.isak.memgamev.themes;

import java.util.ArrayList;

import ws.isak.memgamev.common.CardData;

/*
 * Class MatchTheme contains the information about an abstract match game matchTheme
 *
 * @author isak
 */

public class MatchTheme {

	public int themeID;						//Each MatchTheme has a numeric ID
	public String name;						//and each MatchTheme has a name
	public String backgroundImageUrl;		//this is the board background image for the matchTheme
	public boolean pairedImagesDiffer;		//this boolean tracks whether paired images are identical
	public ArrayList<CardData> cardObjs;	//MatchTheme contains a list of card objects where each of
											//the card objects links for all possible image urls and an audio url
											//as well as flags for the current state of the card
}
