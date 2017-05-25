package ws.isak.bridge.common;

import java.util.ArrayList;

import android.util.Log;

import ws.isak.bridge.model.MatchGameData;
import ws.isak.bridge.model.SwapGameData;
import ws.isak.bridge.model.ComposeGameData;

/*
 * The UserData class contains creation and accessor methods for the data collected about a particular
 * user of the game including their name (checked against a database(?) of names to avoid collisions
 * as well as a list of the GameData objects containing timing and move selection information about
 * each game that they have played.
 *
 * @author isak
 */

public class UserData {

    private static final String TAG = "UserData";

    private static UserData mInstance = null;

    private String userName;               //FIXME for now userName is TEXT_PRIMARY_KEY

    //TODO private String passWord;             //userName + passWord is used as TEXT_PRIMARY_KEY in UserDataORM?
    //TODO private long userCreateTimeStamp;

    //data from pre game survey
    private String ageRange;

    //SURVEY RELATED DATA - NO LONGER KEPT for children, may bring back for adults
    private String yearsTwitchingRange;
    private String speciesKnownRange;
    private String audibleRecognizedRange;
    private String interfaceExperienceRange;
    private boolean hearingEqualsSeeing;
    private boolean hasUsedSmartPhone;

    //data from post game survey
    private static boolean spectrogramFamiliar;
    private static int hearIsSeeLikert;
    private static int hearIsPredictLikert;


    //data from match game/s played
    private MatchGameData curMemGame;                       //current game being played
    private ArrayList<MatchGameData> matchGameDataList;     //list of all games user has played

    //data from swap game/s played
    private SwapGameData curSwapGameData;                   //current game being played
    private ArrayList<SwapGameData> swapGameDataList;       //list of all games user has played

    //data from compose game/s played
    private ComposeGameData curComposeGameData;             //current game being played
    private ArrayList<ComposeGameData> composeGameDataList; //list of all games user has played

    //TODO data about user's prior performance at the games
    private int matchGameTheme1Difficulty1HighStars;
    private int matchGameTheme1Difficulty2HighStars;
    private int matchGameTheme1Difficulty3HighStars;
    private int matchGameTheme2Difficulty1HighStars;
    private int matchGameTheme2Difficulty2HighStars;
    private int matchGameTheme2Difficulty3HighStars;
    private int matchGameTheme3Difficulty1HighStars;
    private int matchGameTheme3Difficulty2HighStars;
    private int matchGameTheme3Difficulty3HighStars;

    private int swapGameDifficulty1HighStars;
    private int swapGameDifficulty2HighStars;
    private int swapGameDifficulty3HighStars;

    private int composeGameDifficulty1HighStars;
    private int composeGameDifficulty2HighStars;
    private int composeGameDifficulty3HighStars;


    // Constructor - this should be called once when a userData object instance needs to be instantiated
    // the instantiator will pass all relevant nulled values
    public UserData() {
        //Log.d(TAG, "***** CONSTRUCTOR *****");
        //set strings to null
        //pre
        setUserName(null);
        setAgeRange(null);
        setYearsTwitchingRange(null);
        setSpeciesKnownRange(null);
        setAudibleRecognizedRange(null);
        setInterfaceExperienceRange(null);
        //set ints to 0 (Likert null)
            //post
        setHearIsSeeLikert(0);
        setHearIsPredictLikert(0);
        //set booleans to false
            //pre
        setHearingEqualsSeeing(false);
        setHasUsedSmartPhone(false);
            //post
        setSpectrogramFamiliar(false);
        //set game data lists
        initMatchGameDataList();
        initSwapGameDataList();
        initComposeGameDataList();

        //TODO set game performance results (game-difficulty-stars)?
        initMatchHighStars();
        initSwapHighStars();
        initComposeHighStars();
    }

    /*
     * Method getInstance returns an instance of an empty  UserData object - this is called only
     * when a new userData object needs to be created.
     */
    public static UserData getInstance() {
        Log.d(TAG, "method getInstance");
        if (mInstance == null) {
            mInstance = new UserData();
        }
        return mInstance;
    }

    //***** USER SETUP DATA *****

    //[0] set and get the userName string parameter - this is used in part to define the userData object
    public void setUserName(String user) {
        //Log.d(TAG, "method setUserName: user name is: " + user);
        userName = user;
    }

    public String getUserName() {
        //Log.d(TAG, "method getUserName returns: " + userName);
        return userName;
    }

    //***** PRE SURVEY DATA *****

    //[1] set and get the user's ageRange
    public void setAgeRange(String age) {
        //Log.d(TAG, "method setAgeRange: age: " + age);
        ageRange = age;
    }

    public String getAgeRange() {
        //Log.d(TAG, "method getAgeRange: ageRange: " + ageRange);
        return ageRange;
    }

    //[2] set and get yearsTwitchingRange variable
    public void setYearsTwitchingRange(String yearsTwitching) {
        //Log.d(TAG, "method setYearsTwitchingRange: yearsTwitching: " + yearsTwitching);
        yearsTwitchingRange = yearsTwitching;
    }

    public String getYearsTwitchingRange() {
        //Log.d(TAG, "method getYearsTwitchingRange: yearsTwitchingRange: " + yearsTwitchingRange);
        return yearsTwitchingRange;
    }

    //[3] set and get the speciesKnownRange variable
    public void setSpeciesKnownRange(String speciesKnown) {
        //Log.d(TAG, "method getKnownSpeciesRange: speciesKnown: " + speciesKnown);
        speciesKnownRange = speciesKnown;
    }

    public String getSpeciesKnownRange() {
        //Log.d(TAG, "method setKnownSpeciesRange: speciesKnownRange: " + speciesKnownRange);
        return speciesKnownRange;
    }

    //[4] set and get the audibleRecognizedRange
    public void setAudibleRecognizedRange(String audibleRecognized) {
        //Log.d(TAG, "method setAudibleRecognizedRange: audibleRecognized: " + audibleRecognized);
        audibleRecognizedRange = audibleRecognized;
    }

    public String getAudibleRecognizedRange() {
        //Log.d(TAG, "method getAudibleRecognizedRange: audibleRecognizedRange: " + audibleRecognizedRange);
        return audibleRecognizedRange;
    }

    //[5] set and get the interfaceExperienceRange
    public void setInterfaceExperienceRange(String interfaceExperience) {
        //Log.d(TAG, "method setInterfaceExperienceRange: interfaceExperience: " + interfaceExperience);
        interfaceExperienceRange = interfaceExperience;
    }

    public String getInterfaceExperienceRange() {
        //Log.d(TAG, "method getInterfaceExperienceRange: interfaceExperienceRange: " + interfaceExperienceRange);
        return interfaceExperienceRange;
    }

    //[6] set and get the hearingEqualsSeeing boolean
    public void setHearingEqualsSeeing(boolean isHearingSeeing) {
        //Log.d(TAG, "method setHearingEqualsSeeing: isHearingSeeing: " + isHearingSeeing);
        hearingEqualsSeeing = isHearingSeeing;
    }

    public boolean getHearingEqualsSeeing() {
        //Log.d(TAG, "method getHearingEqualsSeeing: hearingEqualsSeeing: " + hearingEqualsSeeing);
        return hearingEqualsSeeing;
    }

    //[7] set and get the hasUsedSmartPhone boolean
    public void setHasUsedSmartPhone(boolean usedSmartPhone) {
        //Log.d(TAG, "method setHasUsedSmartPhone: " + usedSmartPhone);
        hasUsedSmartPhone = usedSmartPhone;
    }

    public boolean getHasUsedSmartphone() {
        //Log.d(TAG, "method getHasUsedSmartphone: hasUsedSmartPhone: " + hasUsedSmartPhone);
        return hasUsedSmartPhone;
    }

    //****** POST SURVEY DATA *****
    //TODO is there a way we can 'force' the user to provide this information?
    //[8] get and set spectrogramFamiliar boolean
    public void setSpectrogramFamiliar(boolean isFamiliar) {
        //Log.d(TAG, "method setSpectrogramFamiliar: isFamiliar: " + isFamiliar);
        spectrogramFamiliar = isFamiliar;
    }

    public boolean getSpectrogramFamiliar() {
        //Log.d(TAG, "method getSpectrogramFamiliar: spectrogramFamiliar: " + spectrogramFamiliar);
        return spectrogramFamiliar;
    }

    //[9] get and set the hearIsSeeLikert integer value
    public void setHearIsSeeLikert(int likertValue) {
        //Log.d(TAG, "method setHearIsSeeLikert: likertValue: " + likertValue);
        hearIsSeeLikert = likertValue;
    }

    public int getHearIsSeeLikert() {
        //Log.d(TAG, "method getHearIsSeeLikert: hearIsSeeLikert: " + hearIsSeeLikert);
        return hearIsSeeLikert;
    }

    //[10] get and set the hearIsPredictLikert integer value
    public void setHearIsPredictLikert(int likertValue) {
        //Log.d(TAG, "method setHearIsPredictLikert: likertValue: " + likertValue);
        hearIsPredictLikert = likertValue;
    }

    public int getHearIsPredictLikert() {
        //Log.d(TAG, "method getHearIsPredictLikert: hearIsPredictLikert: " + hearIsPredictLikert);
        return hearIsPredictLikert;
    }

    //***** Match GAME DATA *****

    /*
     * MatchGameData constructor, accessor, mutator follow:
     */
    public void initMatchGameDataList() {
        Log.d(TAG, "method initMatchGameDataList");
        matchGameDataList = new ArrayList<MatchGameData>();
    }

    public void appendMatchGameData(MatchGameData game) {
        Log.d(TAG, "method addToGameDataList: adding game data to list");
        matchGameDataList.add(game);        //TODO add try/catch block here
    }

    /*
     * Method queryMemGameData returns a MatchGameData object at position in the list gameDataRecord.
     */
    public MatchGameData queryMatchGameDataList(int gameDataRecord) {
        Log.d(TAG, "method queryMatchGameDataList");
        return matchGameDataList.get(gameDataRecord);
    }

    public void setCurMatchGame(MatchGameData gameData) {
        Log.d(TAG, "method setCurMatchGame");
        curMemGame = gameData;
    }

    public MatchGameData getCurMatchGame() {
        //Log.d(TAG, "method getCurMatchGame");
        return curMemGame;
    }

    public int sizeOfMatchGameDataList () {
        //
        return matchGameDataList.size();
    }

    //***** SWAP GAME DATA *****

    /*
     * SwapGameData constructor, accessor, mutator follow:
     */
    public void initSwapGameDataList() {
        Log.d(TAG, "method initSwapGameDataList");
        swapGameDataList = new ArrayList<SwapGameData>();
    }

    public void appendSwapGameData(SwapGameData game) {
        Log.d(TAG, "method addToGameDataList: adding game data to list");
        swapGameDataList.add(game);        //TODO add try/catch block here
    }

    /*
     * Method querySwapGameData returns a SwapGameData object at position in the list gameDataRecord.
     */
    public SwapGameData querySwapGameDataList(int gameDataRecord) {
        Log.d(TAG, "method querySwapGameDataList");
        return swapGameDataList.get(gameDataRecord);
    }

    public void setCurSwapGameData(SwapGameData gameData) {
        Log.d(TAG, "method setCurSwapGameData");
        curSwapGameData = gameData;
    }

    public SwapGameData getCurSwapGameData() {
        //Log.d(TAG, "method getCurSwapGameData");
        return curSwapGameData;
    }

    public int sizeOfSwapGameDataList () {
        //
        return swapGameDataList.size();
    }

    //***** COMPOSE GAME DATA *****

    /*
     * ComposeGameData constructor, accessor, mutator follow:
     */
    public void initComposeGameDataList() {
        Log.d(TAG, "method initComposeGameDataList");
        composeGameDataList = new ArrayList<ComposeGameData>();
    }

    public void appendComposeGameData(ComposeGameData game) {
        Log.d(TAG, "method addToGameDataList: adding game data to list");
        composeGameDataList.add(game);        //TODO add try/catch block here
    }

    /*
     * Method queryComposeGameDataList returns a ComposeGameData object at position in the list gameDataRecord.
     */
    public ComposeGameData queryComposeGameDataList(int gameDataRecord) {
        Log.d(TAG, "method queryComposeGameDataList");
        return composeGameDataList.get(gameDataRecord);
    }

    public void setCurComposeGameData(ComposeGameData gameData) {
        Log.d(TAG, "method setCurComposeGameData");
        curComposeGameData = gameData;
    }

    public ComposeGameData getCurComposeGameData() {
        //Log.d(TAG, "method getCurComposeGameData");
        return curComposeGameData;
    }

    public int sizeOfComposeGameDataList () {
        //
        return composeGameDataList.size();
    }

    //==============================================================================================
    //Methods to initialize the number of stars a user has achieved for each game/difficulty

    //When a new UserData is created, they haven't played so all high stars are at zero

    //MATCH GAME RESULTS
    //==================
    public void initMatchHighStars () {
        setMatchHighStarsTheme1();
        setMatchHighStarsTheme2();
        setMatchHighStarsTheme3();
    }

    public void setMatchHighStarsTheme1 () {
        setMatchHighStarsTheme1Difficulty1(0);
        setMatchHighStarsTheme1Difficulty2(0);
        setMatchHighStarsTheme1Difficulty3(0);
    }

    public void setMatchHighStarsTheme1Difficulty1 (int diff) { matchGameTheme1Difficulty1HighStars = diff; }
    public int getMatchTheme1Difficulty1HighStars() { return matchGameTheme1Difficulty1HighStars; }

    public void setMatchHighStarsTheme1Difficulty2 (int diff) { matchGameTheme1Difficulty2HighStars = diff; }
    public int getMatchTheme1Difficulty2HighStars() { return matchGameTheme1Difficulty2HighStars; }

    public void setMatchHighStarsTheme1Difficulty3 (int diff) { matchGameTheme1Difficulty3HighStars = diff; }
    public int getMatchTheme1Difficulty3HighStars() { return matchGameTheme1Difficulty3HighStars; }

    public void setMatchHighStarsTheme2 () {
        setMatchHighStarsTheme2Difficulty1(0);
        setMatchHighStarsTheme2Difficulty2(0);
        setMatchHighStarsTheme2Difficulty3(0);
    }

    public void setMatchHighStarsTheme2Difficulty1 (int diff) { matchGameTheme2Difficulty1HighStars = diff; }
    public int getMatchTheme2Difficulty1HighStars() { return matchGameTheme2Difficulty1HighStars; }

    public void setMatchHighStarsTheme2Difficulty2 (int diff) { matchGameTheme2Difficulty2HighStars = diff; }
    public int getMatchTheme2Difficulty2HighStars() { return matchGameTheme2Difficulty2HighStars; }

    public void setMatchHighStarsTheme2Difficulty3 (int diff) { matchGameTheme2Difficulty3HighStars = diff; }
    public int getMatchTheme2Difficulty3HighStars() { return matchGameTheme2Difficulty3HighStars; }

    public void setMatchHighStarsTheme3 () {
        setMatchHighStarsTheme3Difficulty1(0);
        setMatchHighStarsTheme3Difficulty2(0);
        setMatchHighStarsTheme3Difficulty3(0);
    }

    public void setMatchHighStarsTheme3Difficulty1 (int diff) { matchGameTheme3Difficulty1HighStars = diff; }
    public int getMatchTheme3Difficulty1HighStars() { return matchGameTheme3Difficulty1HighStars; }

    public void setMatchHighStarsTheme3Difficulty2 (int diff) { matchGameTheme3Difficulty2HighStars = diff; }
    public int getMatchTheme3Difficulty2HighStars() { return matchGameTheme3Difficulty2HighStars; }

    public void setMatchHighStarsTheme3Difficulty3 (int diff) { matchGameTheme3Difficulty3HighStars = diff; }
    public int getMatchTheme3Difficulty3HighStars() { return matchGameTheme3Difficulty3HighStars; }

    //SWAP GAME RESULTS
    //=================
    public void initSwapHighStars() {
        setSwapHighStarsDifficulty1(0);
        setSwapHighStarsDifficulty2(0);
        setSwapHighStarsDifficulty3(0);
    }

    public void setSwapHighStarsDifficulty1(int diff) { swapGameDifficulty1HighStars = diff; }
    public int getSwapHighStarsDifficulty1() { return swapGameDifficulty1HighStars; }

    public void setSwapHighStarsDifficulty2(int diff) { swapGameDifficulty2HighStars = diff; }
    public int getSwapHighStarsDifficulty2() { return swapGameDifficulty2HighStars; }

    public void setSwapHighStarsDifficulty3(int diff) { swapGameDifficulty3HighStars = diff; }
    public int getSwapHighStarsDifficulty3() { return swapGameDifficulty3HighStars; }

    //COMPOSE GAME RESULTS
    //====================
    public void initComposeHighStars () {
        setComposeHighStarsDifficulty1(3);  //FIXME - this is just for debugging - stars have no meaning?
        setComposeHighStarsDifficulty2(3);  //FIXME - this is just for debugging
        setComposeHighStarsDifficulty3(3);  //FIXME - this is just for debugging
    }

    public void setComposeHighStarsDifficulty1 (int stars) {
        Log.d (TAG, "method setComposeHighStarsDifficulty1: setting stars: " + stars);
        composeGameDifficulty1HighStars = stars;
    }

    public int getComposeHighStarsDifficulty1 () { return composeGameDifficulty1HighStars; }

    public void setComposeHighStarsDifficulty2 (int stars) {
        Log.d (TAG, "method setComposeHighStarsDifficulty2: setting stars: " + stars);
        composeGameDifficulty2HighStars = stars;
    }

    public int getComposeHighStarsDifficulty2 () { return composeGameDifficulty2HighStars; }

    public void setComposeHighStarsDifficulty3 (int stars) {
        Log.d (TAG, "method setComposeHighStarsDifficulty3: setting stars: " + stars);
        composeGameDifficulty3HighStars = stars;
    }

    public int getComposeHighStarsDifficulty3 () { return composeGameDifficulty3HighStars; }
}