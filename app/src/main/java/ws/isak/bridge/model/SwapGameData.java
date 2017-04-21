package ws.isak.bridge.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import ws.isak.bridge.common.Shared;
import ws.isak.bridge.common.SwapCardData;
import ws.isak.bridge.utils.SwapTileCoordinates;

/*
 * The SwapGameData class hold information about each swap game played
 *
 * @author isak
 */

public class SwapGameData {

    private static final String TAG = "SwapGameData";

    private SwapGame mPlayingSwapGame;

    // Map variable holds the Map representing the current game board - NOTE: not to be stored in database
    private Map <SwapTileCoordinates, SwapCardData> curSwapBoardMap;
    //Parameters to be saved for database matching game to user
    private long gameStartTimestamp;        //keep track of the timestamp for the start of the game - database primary key
    private String userPlayingName;         //FIXME database foreign key?
    //Parameters to be saved for analysis:
    //defined at SwapDifficultySelectedEvent - the following are fixed for the duration of the game
    private int difficulty;                 //difficultyLevel level for the current game
    private long gameDurationAllocated;     //This is the fixed time allocated for playing the game
    private boolean gameStarted;            //Set to Boolean false, becomes true when first card is clicked - triggers gameStarTimeStamp
    //the following respond to user input during the game
    private int numTurnsTakenInGame;        //Initialize number of turns in game to 0 and increment on each click.
    private ArrayList<Long> gamePlayDurations;  //Time the player spent on the game so far (sum of turnDurations) at each turn (can it be greater than allocated time?)
    private ArrayList <Long> turnDurations;      //a list of durations of each turn - a turn is defined as a single click, implemented as ArrayList //TODO should we also have a measure of paired click turns?
    private ArrayList <Map <SwapTileCoordinates, SwapCardData>> swapGameMapList;   //a list of Map objects showing the remapping of the board on each swap


    //constructor method describes the information that is stored about each game played
    public SwapGameData () {
        //Log.d (TAG, "Constructor: initializing game data fields");
        setSwapBoardMap (Shared.currentSwapGame.swapBoardArrangement.swapBoardMap);
        setUserPlayingName(Shared.userData.getUserName());
        setGameDifficulty(-1);
        setGameDurationAllocated(0);
        //initialize to 0 or null as necessary
        setGameStarted(false);      //initialize to false on setup
        setGameStartTimestamp(0);
        setNumTurnsTaken(0);
        initTurnDurationsArray();       //null array at start
        initGamePlayDurationsArray();   //null array at start
        initSwapGameMapList();
    }

    //[0] for now this is a deep copy (?) of the map 'swapBoardMap' initially set up in SwapBoardArrangment
    // this gets updated on each turn of the game so that it always holds the current Map of the board
    public void setSwapBoardMap (Map curBoardMap) {
        curSwapBoardMap = new HashMap<>();
        Iterator iterator = curBoardMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            //System.out.println(pair.getKey() + " maps to " + pair.getValue());
            SwapTileCoordinates coords = (SwapTileCoordinates) pair.getKey();
            SwapCardData cardData = (SwapCardData) pair.getValue();
            Log.d(TAG, "method setSwapBoardMap: Copying... coords: < " + coords.getSwapCoordRow() + "," + coords.getSwapCoordCol() +
                    " > | MAPS TO | cardID: < " + cardData.getCardID().getSwapCardSpeciesID() + "," +
                    cardData.getCardID().getSwapCardSegmentID() + " > | coords @: " + coords +
                    " | cardData @: " + cardData);
            curSwapBoardMap.put(coords, cardData);
        }
        //create a set view for the map
        Set set = curSwapBoardMap.entrySet();
        //check set values TODO remove
        //System.out.println("Set values: " + set);
        //Log.d (TAG, "method setSwapBoardMap: created Map curSwapBoardMap @: " + curSwapBoardMap);
    }

    //return a pointer to the local deep copy of the map
    public Map <SwapTileCoordinates, SwapCardData> getSwapBoardMap () {
        Log.d (TAG, "method getSwapBoardMap: curSwapBoardMap @: " + curSwapBoardMap +
                " | curSwapBoardMap.size(): " + curSwapBoardMap.size());
        return curSwapBoardMap;
    }

    //get a pointer to a SwapTileCoordinates key from a SwapCardData value in swapGameMap
    public SwapTileCoordinates getSwapTileCoordinatesFromSwapBoardMap (SwapCardData card) {
        SwapTileCoordinates coordsToReturn = new SwapTileCoordinates(-1, -1);
        Iterator iterator = curSwapBoardMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            //System.out.println(pair.getKey() + " maps to " + pair.getValue());
            SwapTileCoordinates coords = (SwapTileCoordinates) pair.getKey();
            SwapCardData cardData = (SwapCardData) pair.getValue();
            Log.d(TAG, "method getSwapTileCoordinatesFromSwapBoardMap: Searching... coords: < " + coords.getSwapCoordRow() + "," + coords.getSwapCoordCol() +
                    " > | MAPS TO | cardID: < " + cardData.getCardID().getSwapCardSpeciesID() + "," +
                    cardData.getCardID().getSwapCardSegmentID() + " >");
            if (cardData == card) {
                coordsToReturn = coords;
            }
        }
        return coordsToReturn;
    }

    //get a pointer to a SwapCardData value from a SwapTileCoordinates key in swapGameMap
    public SwapCardData getSwapCardDataFromSwapBoardMap (SwapTileCoordinates targetCoords) {
        SwapCardData cardToReturn = new SwapCardData();
        Iterator iterator = curSwapBoardMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            //System.out.println(pair.getKey() + " maps to " + pair.getValue());
            SwapTileCoordinates coords = (SwapTileCoordinates) pair.getKey();
            SwapCardData cardData = (SwapCardData) pair.getValue();
            Log.d(TAG, "method getSwapCardDataFromSwapBoardMap: Searching... coords: < " + coords.getSwapCoordRow() + "," + coords.getSwapCoordCol() +
                    " > | MAPS TO | cardID: < " + cardData.getCardID().getSwapCardSpeciesID() + "," +
                    cardData.getCardID().getSwapCardSegmentID() + " >");
            if (targetCoords == coords) {
                Log.d (TAG, " *** returning card from targetCoords @: " + targetCoords + " | with coords @: " + coords);
                cardToReturn = cardData;
            }
            //iterator.remove(); // TODO remove from here as well: avoids a ConcurrentModificationException
        }
        return cardToReturn;
    }

    //get a pointer to a SwapTileCoordinates key in swapGameMap from a target loc
    public SwapTileCoordinates getMapSwapTileCoordinatesFromLoc (SwapTileCoordinates loc) {
        SwapTileCoordinates coordsToReturn = new SwapTileCoordinates(-1, -1);
        Iterator iterator = curSwapBoardMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            //System.out.println(pair.getKey() + " maps to " + pair.getValue());
            SwapTileCoordinates coords = (SwapTileCoordinates) pair.getKey();
            SwapCardData cardData = (SwapCardData) pair.getValue();
            Log.d(TAG, "method getSwapTileCoordinatesFromSwapBoardMap: Searching... coords: < " + coords.getSwapCoordRow() + "," + coords.getSwapCoordCol() +
                    " > | MAPS TO | cardID: < " + cardData.getCardID().getSwapCardSpeciesID() + "," +
                    cardData.getCardID().getSwapCardSegmentID() + " >");
            if (coords.getSwapCoordRow() == loc.getSwapCoordRow() && coords.getSwapCoordCol() == loc.getSwapCoordCol()) {
                coordsToReturn = coords;
            }
        }
        return coordsToReturn;
    }

    //[2]
    public void setUserPlayingName (String userName) {
        //Log.d (TAG, "");
        userPlayingName = userName;
    }

    public String getUserPlayingName () {
        //Log.d (TAG, "");
        return userPlayingName;
    }

    //[3]
    public void setGameDifficulty (int diff) {
        //Log.d (TAG, "");
        difficulty = diff;
    }

    public int getGameDifficulty () {
        //Log.d (TAG, "");
        return difficulty;
    }

    //[4]
    public void setGameDurationAllocated (long dur) {
        //Log.d (TAG, "");
        gameDurationAllocated = dur;
    }

    public long getGameDurationAllocated () {
        //Log.d (TAG, "");
        return gameDurationAllocated;
    }

    /*
     * The following are set to appropriate null values at startup
     */

    //[1] set/get gameStartTimestamp - this is unique and useful for sorting
    public void setGameStartTimestamp (long gameStartTime) {
        //Log.d (TAG, "method setGameStartTimestamp");
        gameStartTimestamp = gameStartTime;
    }

    public long getGameStartTimestamp () {
        //Log.d (TAG, "method getGameStartTimestamp");
        return gameStartTimestamp;
    }
    //[2] set/get gameStarted
    public void setGameStarted (boolean startedYet) {
        //Log.d (TAG, "method setGameStarted");
        gameStarted = startedYet;
    }

    public boolean isGameStarted () {
        //Log.d (TAG, "method isGameStarted");
        return gameStarted;
    }

    //[3] control methods for  gamePlayDurations
    public void initGamePlayDurationsArray () {
        //Log.d (TAG, "method initGamePlayDurationsArray");
        gamePlayDurations = new ArrayList<Long>();
    }
    public void appendToGamePlayDurations (long durToAdd) {
        //Log.d (TAG, "method appendToGamePlayDurations");
        gamePlayDurations.add(durToAdd);
    }

    public long queryGamePlayDurations (int locToQuery) {
        //Log.d (TAG, "method getGamePlayDuration: gamePlayDuration: " + gamePlayDuration);
        return gamePlayDurations.get(locToQuery);
    }

    public int sizeOfPlayDurationsArray () {
        //Log.d (TAG, "method sizeOfPlayDurationsArray");
        return gamePlayDurations.size();
    }

    public ArrayList <Long> getGamePlayDurations () {
        //
        return gamePlayDurations;
    }

    //[4] control methods for the turnsDurationArray
    private void initTurnDurationsArray () {
        //Log.d (TAG, "method initTurnDurations array list");
        turnDurations = new ArrayList<Long>();
    }

    public void appendToTurnDurations (long durToAdd) {
        //Log.d (TAG, "method addDurationToTurnDurations");
        turnDurations.add(durToAdd);
    }

    public long queryTurnDurationsArray (int locToQuery) {
        //Log.d (TAG, "method queryTurnDurationArray: location to query: " + locToQuery);
        return turnDurations.get(locToQuery);
    }

    public int sizeOfTurnDurationsArray (){
        //Log.d (TAG, "method sizeOfTurnDurationsArray);
        return turnDurations.size();
    }

    public ArrayList <Long> getTurnDurationsArray () {
        //
        return turnDurations;
    }

    //[5] control methods for the swapGameMapList
    private void initSwapGameMapList () {
        //Log.d (TAG, "method initCardsSelectedOrderArray array list");
        swapGameMapList = new ArrayList<Map<SwapTileCoordinates, SwapCardData>>();
    }

    public void appendToSwapGameMapList (Map <SwapTileCoordinates, SwapCardData> curSwapGameMap) {
        //Log.d (TAG, "method appendToSwapGameMapList");
        swapGameMapList.add(curSwapGameMap);
    }

    public Map <SwapTileCoordinates, SwapCardData> querySwapGameMapList (int locToQuery) {
        //Log.d (TAG, "method queryCardsSelectedArray");
        return swapGameMapList.get(locToQuery);
    }

    public int sizeOfSwapGameMapList () {
        //Log.d (TAG, "method sizeOfCardSelectionArray);
        return swapGameMapList.size();
    }

    public ArrayList <Map <SwapTileCoordinates, SwapCardData>> getSwapGameMapList () {
        //
        return swapGameMapList;
    }

    //[6] set/get/increment numTurnsTaken
    public void setNumTurnsTaken (int numTurns) {
        //Log.d (TAG, "method setNumTurnsTaken: This is called on init as 0");
        numTurnsTakenInGame = numTurns;
    }

    public void incrementNumTurnsTaken () {
        //Log.d (TAG, "method incrementNumTurnsTaken: prior to increment: " + numTurnsTakenInGame);
        numTurnsTakenInGame++;
        //Log.d (TAG, "                             : post increment is: " + numTurnsTakenInGame);
    }

    public int getNumTurnsTaken () {
        //Log.d (TAG, "method getNumTurnsTaken");
        return numTurnsTakenInGame;
    }
}