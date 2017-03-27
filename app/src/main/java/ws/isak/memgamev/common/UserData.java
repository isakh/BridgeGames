package ws.isak.memgamev.common;

import android.util.Log;
import java.util.ArrayList;

import ws.isak.memgamev.model.MemGameData;

/*
 * The UserData class contains creation and accessor methods for the data collected about a particular
 * user of the game including their name (checked against a database(?) of names to avoid collisions
 * as well as a list of the GameData objects containing timing and move selection information about
 * each game that they have played.
 *
 * @author isak
 */

public class UserData {

    private static final String TAG = "Class: UserData";

    private static UserData mInstance = null;

    //TODO should there be some form of UserDataID that is unique for each user?
    //TODO can this be combined with userName to make a userID for retrieval?

    private String userName;

    //data from pre game survey
    private static String ageRange;
    private static String yearsTwitchingRange;
    private static String speciesKnownRange;
    private static String audibleRecognizedRange;
    private static String interfaceExperienceRange;
    private static boolean hearingEqualsSeeing;
    private static boolean hasUsedSmartPhone;

    //data from post game survey
    private static boolean spectrogramFamiliar;
    private static int hearIsSeeLikert;
    private static int hearIsPredictLikert;

    private ArrayList <MemGameData> memGameDataList = new ArrayList<MemGameData>();
    //TODO private ArrayList swapGameDataList<SwapGameData>;   this will cover when the user plays the tile swapping game


    // Constructor - this should be called once when a userData object instance needs to be instantiated
    // the instantiator will pass all relevant nulled values
    public UserData () {
        //TODO what do we need on construct? or can we leave this blank and simply have a placeholder for calls to getInstance
        Log.d (TAG, "***** CONSTRUCTOR *****");
        Log.d (TAG, "*** WHAT GOES HERE? ***");
    }

    /*
     * Method getInstance returns an instance of an empty  UserData object - this is called once
     * when a new userData object needs to be created.
     */
    public static UserData getInstance() {
        Log.d (TAG, "method getInstance");
        if (mInstance == null) {
            mInstance = new UserData();
        }
        return mInstance;
    }

    /* FIXME!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     * Overloaded method getInstance (String loginName) returns a pre-existing instance of a userData
     * object assuming that the loginName has been checked and can return an object from storage.
     */
    public static UserData getInstance (String loginName) {
        //TODO return the USER_DATA object with key loginName from storage
        return null;
    }

    //***** USER SETUP DATA *****

    //[0] set and get the userName string parameter - this is used in part to define the userData object
    public void setUserName (String user) {
        //TODO method CheckUserNameUnique (user, )
        Log.d (TAG, "method setUserName: user name is: " + user);
        userName = user;
    }

    public String getUserName () {
        Log.d (TAG, "method getUserName returns: " + userName);
        return userName;
    }

    //***** PRE SURVEY DATA *****

    //[1] set and get the user's ageRange
    public void setAgeRange (String age) {
        Log.d (TAG, "method setAgeRange: age: " + age);
        ageRange = age;
    }

    public String getAgeRange () {
        Log.d (TAG, "method getAgeRange: ageRange: " + ageRange);
        return ageRange;
    }

    //[2] set and get yearsTwitchingRange variable
    public void setYearsTwitchingRange (String yearsTwitching) {
        Log.d (TAG, "method setYearsTwitchingRange: yearsTwitching: " + yearsTwitching);
        yearsTwitchingRange = yearsTwitching;
    }

    public String getYearsTwitchingRange () {
        Log.d (TAG, "method getYearsTwitchingRange: yearsTwitchingRange: " + yearsTwitchingRange);
        return yearsTwitchingRange;
    }

    //[3] set and get the speciesKnownRange variable
    public void setSpeciesKnownRange (String speciesKnown) {
        Log.d (TAG, "method getKnownSpeciesRange: speciesKnown: " + speciesKnown);
        speciesKnownRange = speciesKnown;
    }

    public String getSpeciesKnownRange () {
        Log.d (TAG, "method setKnownSpeciesRange: speciesKnownRange: " + speciesKnownRange);
        return speciesKnownRange;
    }

    //[4] set and get the audibleRecognizedRange
    public void setAudibleRecognizedRange (String audibleRecognized) {
        Log.d (TAG, "method setAudibleRecognizedRange: audibleRecognized: " + audibleRecognized);
        audibleRecognizedRange = audibleRecognized;
    }

    public String getAudibleRecognizedRange () {
        Log.d (TAG, "method getAudibleRecognizedRange: audibleRecognizedRange: " + audibleRecognizedRange);
        return audibleRecognizedRange;
    }

    //[5] set and get the interfaceExperienceRange
    public void setInterfaceExperienceRange (String interfaceExperience) {
        Log.d (TAG, "method setInterfaceExperienceRange: interfaceExperience: " + interfaceExperience);
        interfaceExperienceRange = interfaceExperience;
    }

    public String getInterfaceExperienceRange () {
        Log.d (TAG, "method getInterfaceExperienceRange: interfaceExperienceRange: " + interfaceExperienceRange);
        return interfaceExperienceRange;
    }

    //[6] set and get the hearingEqualsSeeing boolean
    public void setHearingEqualsSeeing (boolean isHearingSeeing) {
        Log.d (TAG, "method setHearingEqualsSeeing: isHearingSeeing: " + isHearingSeeing);
        hearingEqualsSeeing = isHearingSeeing;
    }

    public boolean getHearingEqualsSeeing () {
        Log.d (TAG, "method getHearingEqualsSeeing: hearingEqualsSeeing: " + hearingEqualsSeeing);
        return hearingEqualsSeeing;
    }

    //[7] set and get the hasUsedSmartPhone boolean
    public void setHasUsedSmartPhone (boolean usedSmartPhone) {
        Log.d (TAG, "method setHasUsedSmartPhone: " + usedSmartPhone);
        hasUsedSmartPhone = usedSmartPhone;
    }

    public boolean getHasUsedSmartphone () {
        Log.d (TAG, "method getHasUsedSmartphone: hasUsedSmartPhone: " + hasUsedSmartPhone);
        return hasUsedSmartPhone;
    }

    //****** POST SURVEY DATA *****
    //TODO is there a way we can 'force' the user to provide this information?
    //[8] get and set spectrogramFamiliar boolean
    public void setSpectrogramFamiliar (boolean isFamiliar) {
        Log.d (TAG, "method setSpectrogramFamiliar: isFamiliar: " + isFamiliar);
        spectrogramFamiliar = isFamiliar;
    }

    public boolean getSpectrogramFamiliar () {
        Log.d (TAG, "method getSpectrogramFamiliar: spectrogramFamiliar: " + spectrogramFamiliar);
        return spectrogramFamiliar;
    }

    //[9] get and set the hearIsSeeLikert integer value
    public void setHearIsSeeLikert (int likertValue) {
        Log.d (TAG, "method setHearIsSeeLikert: likertValue: " + likertValue);
        hearIsSeeLikert = likertValue;
    }

    public int getHearIsSeeLikert () {
        Log.d (TAG, "method getHearIsSeeLikert: hearIsSeeLikert: " + hearIsSeeLikert);
        return hearIsSeeLikert;
    }

    //[10] get and set the hearIsPredictLikert integer value
    public void setHearIsPredictLikert (int likertValue) {
        Log.d (TAG, "method setHearIsPredictLikert: likertValue: " + likertValue);
        hearIsPredictLikert = likertValue;
    }

    public int getHearIsPredictLikert () {
        Log.d (TAG, "method getHearIsPredictLikert: hearIsPredictLikert: " + hearIsPredictLikert);
        return hearIsPredictLikert;
    }

    //***** MEMORY GAME DATA *****

    public void createMemGameDataList () {
        Log.d (TAG, "method createMemGameDataList");
        memGameDataList = new ArrayList();
    }

    public void addToMemGameDataList (MemGameData game) {
        Log.d (TAG, "method addToGameDataList: adding game data to list");
        memGameDataList.add (game);        //TODO add try/catch block here
    }

    /*
     * Method queryMemGameData returns a GameData object at position in the list gameDataRecord
     */
    public MemGameData queryMemGameDataList (int gameDataRecord) {
        return memGameDataList.get(gameDataRecord);
    }

    //TODO ***** SWAP GAME DATA *****
}