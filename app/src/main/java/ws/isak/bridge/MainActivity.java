package ws.isak.bridge;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import ws.isak.bridge.common.ComposeSampleData;
import ws.isak.bridge.common.MatchCardData;
import ws.isak.bridge.common.Audio;
import ws.isak.bridge.common.Shared;
import ws.isak.bridge.common.SwapCardData;
import ws.isak.bridge.common.UserData;

import ws.isak.bridge.database.ComposeGameDataORM;
import ws.isak.bridge.database.ComposeSampleDataORM;
import ws.isak.bridge.engine.Engine;
import ws.isak.bridge.engine.ScreenController;
import ws.isak.bridge.engine.ScreenController.Screen;

import ws.isak.bridge.events.EventBus;
import ws.isak.bridge.events.ui.MatchBackGameEvent;

import ws.isak.bridge.model.ComposeGameData;
import ws.isak.bridge.model.MatchGameData;
import ws.isak.bridge.model.SwapGameData;

import ws.isak.bridge.ui.PopupManager;

import ws.isak.bridge.utils.ImageScaling;
import ws.isak.bridge.utils.SwapCardID;

import ws.isak.bridge.database.DatabaseWrapper;
import ws.isak.bridge.database.UserDataORM;
import ws.isak.bridge.database.MatchCardDataORM;
import ws.isak.bridge.database.MatchGameDataORM;
import ws.isak.bridge.database.SwapGameDataORM;
import ws.isak.bridge.database.SwapCardDataORM;

/*
 * The main activity class of the app.  This activity class is called from the AndroidManifest.xml
 * as the application activity on launch.  This class instantiates the shared context, engine
 * and eventBus that guide the flow of the games. In addition it handles (loads and checks)the database
 * that stores information about users and previously played games On creation, the screen will open
 * with the USER_SETUP screen.
 *
 * @author isak
 */

public class  MainActivity extends FragmentActivity {

    private static final String TAG = "MainActivity";

    private static String URI_AUDIO = "raw://";
    private static String URI_DRAWABLE = "drawable://";

    private ImageView mBackgroundImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mBackgroundImage = (ImageView) findViewById(R.id.background_image);

        //Log.d(TAG, "method onCreate: setting Shared data");
        Shared.context = getApplicationContext();
        Shared.activity = this;

        Shared.userData = UserData.getInstance();
        //Log.d(TAG, " *******: Shared.userData @: " + Shared.userData);
        Shared.engine = Engine.getInstance();
        //Log.d(TAG, " *******: Shared.engine @: " + Shared.engine);
        Shared.eventBus = EventBus.getInstance();
        //Log.d(TAG, " *******: Shared.eventBus @: " + Shared.eventBus);

        //instantiate a DatabaseWrapper and load the database into memory
        Shared.databaseWrapper= new DatabaseWrapper(this);

        Shared.engine.start();
        Shared.engine.setBackgroundImageView(mBackgroundImage);

        //build the list of MatchCardData objects based on resources
        buildMatchCardDataList();

        Log.w (TAG, "-----------------------------------------------------");
        Log.w (TAG, ".....................................................");
        Log.w (TAG, ".....=====***** MATCH CARD DECK BUILT *****=====.....");
        Log.w (TAG, ".....................................................");
        Log.w (TAG, "-----------------------------------------------------");

        //build the list of SwapCardData objects based on resources
        buildSwapCardDataList();

        Log.w (TAG, "----------------------------------------------------");
        Log.w (TAG, "....................................................");
        Log.w (TAG, ".....=====***** SWAP CARD DECK BUILT *****=====.....");
        Log.w (TAG, "....................................................");
        Log.w (TAG, "----------------------------------------------------");

        //build the list of ComposeSampleData objects based on resources
        buildComposeSampeDataList();

        Log.w (TAG, "---------------------------------------------------------");
        Log.w (TAG, ".........................................................");
        Log.w (TAG, ".....=====***** COMPOSE SAMPLE LIST BUILT *****=====.....");
        Log.w (TAG, ".........................................................");
        Log.w (TAG, "---------------------------------------------------------");


        //load the database after we have built the (Card/Sample)Data Lists
        loadDatabase();

        // set background
        setBackgroundImage();

        Log.w (TAG, "-----------------------------------------");
        Log.w (TAG, ".........................................");
        Log.w (TAG, ".....=====***** END SETUP *****=====.....");
        Log.w (TAG, ".........................................");
        Log.w (TAG, "-----------------------------------------");

        // open to User setup screen
        //Log.d(TAG, "               : get instance of user setup screen");
        ScreenController.getInstance().openScreen(Screen.DIFFICULTY_COMPOSE);
    }

    @Override
    protected void onDestroy() {
        Shared.engine.stop();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        //do something here             //TODO sort out paused game mode?
        super.onPause();
    }

    @Override
    protected void onStop() {
        //do something here
        super.onStop();
    }


    /*
	 * Overriding method onBackPressed - this defines the characteristic behaviors if the hardware
	 * back button is pressed.  If a popup is open, this closes the popup (and if the last screen
	 * before the popup was the MatchGame screen (implying that the popup is popup_won), then this triggers
	 * a MatchBackGameEvent.
	 *
	 * (TODO it is better to perform this action with the appropriate button on the popup_won popup - should we prevent this behavior?)
	 *
	 * If we are not backing up from a popup, the ScreenController onBack() method takes precedence.
	 */
    @Override
    public void onBackPressed() {
        Log.d(TAG, "overriding method onBackPressed: produces various events based on game state");
        if (PopupManager.isShown()) {
            PopupManager.closePopup();
            if (ScreenController.getLastScreen() == Screen.GAME_MATCH) {
                Shared.eventBus.notify(new MatchBackGameEvent());
            }
        } else if (ScreenController.getInstance().onBack()) {
            super.onBackPressed();
        }
    }

    //build the matchCardData list in for access by themes
    private void buildMatchCardDataList() {
        //create the local list to hold the cards in the matching deck
        Shared.matchCardDataList = new ArrayList<MatchCardData>();
        //iterate over the number of cards to put in the deck
        for (int i = 1; i <= 10; i++) {                             //FIXME - make this constant into a variable
            MatchCardData curCard = new MatchCardData();
            curCard.setCardID(i);
            curCard.setSpeciesName(curCard.getCardID());
            //curCard.setPairedImageDiffer(false);
            //curCard.setFirstImageUsed(false);
            curCard.setImageURI0(URI_DRAWABLE + "blank_card");
            curCard.setImageURI1(URI_DRAWABLE + String.format(Locale.ENGLISH, "match_bird_%d", i) + "_a");
            curCard.setImageURI2(URI_DRAWABLE + String.format(Locale.ENGLISH, "match_bird_%d", i) + "_b");
            curCard.setImageURI3(URI_DRAWABLE + String.format(Locale.ENGLISH, "match_spectrogram_%d", i));
            curCard.setAudioURI(URI_AUDIO + String.format(Locale.ENGLISH, "match_audio_%d", i));
            curCard.setSampleDuration(Audio.getAudioDuration(Shared.context.getResources().getIdentifier(curCard.getAudioURI().substring(URI_AUDIO.length()), "raw", Shared.context.getPackageName())));
            //insert matchCardData object into local storage and if not already in database add it to the database
            Shared.matchCardDataList.add(curCard);
            Log.d (TAG, "method buildMatchCardDataList: added curCard: " + Shared.matchCardDataList);
            if (!MatchCardDataORM.isMatchCardDataInDB(curCard)) {
                Log.i (TAG, "method buildMatchCardDataList: card not previously in database: adding...");
                MatchCardDataORM.insertMatchCardData(curCard);
            }
        }
        Shared.debugStateOfMatchCardDataList("Class MainActivity: method buildMatchCardDataList");
    }

    private void buildSwapCardDataList() {
        Shared.swapCardDataList = new ArrayList<SwapCardData>();

        for (int i = 1; i <= 10; i++) {                             //FIXME - make numSpecies 10 an xml value?
            for (int j = 0; j < 4; j++) {                           //FIXME - make numSegments 4 and xml value?
                SwapCardData curCard = new SwapCardData();
                curCard.setCardIDKey(i + ((double)j/10));
                //Log.d (TAG, "^^^ (i+((double)j/10)): " + (i+((double)j/10)) + " | curCard.getCardIDKey: " + curCard.getCardIDKey());
                curCard.setCardID(new SwapCardID(i, j));
                curCard.setSpeciesName(curCard.getCardID().getSwapCardSpeciesID());
                switch (j) {
                    case 0:
                        //set audio
                        curCard.setAudioURI0(URI_AUDIO + String.format(Locale.ENGLISH, "swap_audio_%d", i) + "_a");
                        curCard.setAudioURI1(null);
                        curCard.setAudioURI2(null);
                        curCard.setAudioURI3(null);
                        //set images
                        curCard.setSpectroURI0(URI_DRAWABLE + String.format(Locale.ENGLISH, "swap_spectro_%d", i) + "_a");
                        curCard.setSpectroURI1(null);
                        curCard.setSpectroURI2(null);
                        curCard.setSpectroURI3(null);
                        //set duration
                        curCard.setSampleDuration0(Audio.getAudioDuration(Shared.context.getResources().getIdentifier(curCard.getAudioURI0().substring(URI_AUDIO.length()), "raw", Shared.context.getPackageName())));
                        curCard.setSampleDuration1(0);
                        curCard.setSampleDuration2(0);
                        curCard.setSampleDuration3(0);
                        break;
                    case 1:
                        //set audio
                        curCard.setAudioURI0(null);
                        curCard.setAudioURI1(URI_AUDIO + String.format(Locale.ENGLISH, "swap_audio_%d", i) + "_b");
                        curCard.setAudioURI2(null);
                        curCard.setAudioURI3(null);
                        //set images
                        curCard.setSpectroURI0(null);
                        curCard.setSpectroURI1(URI_DRAWABLE + String.format(Locale.ENGLISH, "swap_spectro_%d", i) + "_b");
                        curCard.setSpectroURI2(null);
                        curCard.setSpectroURI3(null);
                        //set duration
                        curCard.setSampleDuration0(0);
                        curCard.setSampleDuration1(Audio.getAudioDuration(Shared.context.getResources().getIdentifier(curCard.getAudioURI1().substring(URI_AUDIO.length()), "raw", Shared.context.getPackageName())));
                        curCard.setSampleDuration2(0);
                        curCard.setSampleDuration3(0);
                        break;
                    case 2:
                        //set audio
                        curCard.setAudioURI0(null);
                        curCard.setAudioURI1(null);
                        curCard.setAudioURI2(URI_AUDIO + String.format(Locale.ENGLISH, "swap_audio_%d", i) + "_c");
                        curCard.setAudioURI3(null);
                        //set images
                        curCard.setSpectroURI0(null);
                        curCard.setSpectroURI1(null);
                        curCard.setSpectroURI2(URI_DRAWABLE + String.format(Locale.ENGLISH, "swap_spectro_%d", i) + "_c");
                        curCard.setSpectroURI3(null);
                        //set duration
                        curCard.setSampleDuration0(0);
                        curCard.setSampleDuration1(0);
                        curCard.setSampleDuration2(Audio.getAudioDuration(Shared.context.getResources().getIdentifier(curCard.getAudioURI2().substring(URI_AUDIO.length()), "raw", Shared.context.getPackageName())));
                        curCard.setSampleDuration3(0);
                        break;
                    case 3:
                        //set audio
                        curCard.setAudioURI0(null);
                        curCard.setAudioURI1(null);
                        curCard.setAudioURI2(null);
                        curCard.setAudioURI3(URI_AUDIO + String.format(Locale.ENGLISH, "swap_audio_%d", i) + "_d");
                        //set images
                        curCard.setSpectroURI0(null);
                        curCard.setSpectroURI1(null);
                        curCard.setSpectroURI2(null);
                        curCard.setSpectroURI3(URI_DRAWABLE + String.format(Locale.ENGLISH, "swap_spectro_%d", i) + "_d");
                        //set duration
                        curCard.setSampleDuration0(0);
                        curCard.setSampleDuration1(0);
                        curCard.setSampleDuration2(0);
                        curCard.setSampleDuration3(Audio.getAudioDuration(Shared.context.getResources().getIdentifier(curCard.getAudioURI3().substring(URI_AUDIO.length()), "raw", Shared.context.getPackageName())));
                        break;
                }
                Shared.swapCardDataList.add(curCard);
                Log.v(TAG, "method buildSwapCardDataList: added: cardID.getCardIDKey: " + curCard.getCardIDKey() + " | cardID.species: " + curCard.getCardID().getSwapCardSpeciesID() + " | species: " + curCard.getSpeciesName() + " | active segment: " + curCard.getCardID().getSwapCardSegmentID());
                Log.v(TAG, "                            : audio0: " + curCard.getAudioURI0() + " | audio1: " + curCard.getAudioURI1() + " | audio2: " + curCard.getAudioURI2() + " | audio3: " + curCard.getAudioURI3());
                Log.v(TAG, "                            : dur0: " + curCard.getSampleDuration0() + " | dur1: " + curCard.getSampleDuration1() + " | dur2: " + curCard.getSampleDuration2() + " | dur3: " + curCard.getSampleDuration3());
                Log.v(TAG, "                            : image0: " + curCard.getSpectroURI0() + " | image1: " + curCard.getSpectroURI1() + " | image2: " + curCard.getSpectroURI2() + " | image3: " + curCard.getSpectroURI3());
                //insert swapCardData object into Database and local storage
                if (!SwapCardDataORM.isSwapCardDataInDB(curCard)) {
                    SwapCardDataORM.insertSwapCardData(curCard);
                }
            }
        }
        Log.d(TAG, "method buildSwapCardDataList: Shared.swapCardDataList.size(): " + Shared.swapCardDataList.size());
    }

    //build the composeSampleData list in for access by Compose Game Library and reuse in Tracker
    private void buildComposeSampeDataList() {
        //create the local list to hold the samples in the compose sample library
        Shared.composeSampleDataList = new ArrayList<ComposeSampleData>();
        //iterate over the number of samples to put in the library
        for (int i = 1; i <= 10; i++) {                             //FIXME - make this constant into a variable
            ComposeSampleData curSample = new ComposeSampleData();
            curSample.setSpeciesName(i);

            curSample.setSpectroURI(URI_DRAWABLE + String.format(Locale.ENGLISH, "compose_sample_image_%d", i));
            curSample.setAudioURI(URI_AUDIO + String.format(Locale.ENGLISH, "compose_sample_audio_%d", i));
            curSample.setSampleDuration(Audio.getAudioDuration(Shared.context.getResources().getIdentifier(curSample.getAudioURI().substring(URI_AUDIO.length()), "raw", Shared.context.getPackageName())));
            //insert matchCardData object into local storage and if not already in database add it to the database
            Shared.composeSampleDataList.add(curSample);
            Log.d (TAG, "method buildComposeSampeDataList: added curSample: " + Shared.composeSampleDataList);
            //ADD ComposeSampleDataORM
            if (!ComposeSampleDataORM.isComposeSampleDataInDB(curSample)) {
                Log.i (TAG, "method buildComposeSampleDataList: sample not previously in database: adding...");
                ComposeSampleDataORM.insertComposeSampleData(curSample);
            }
        }
    }

    private void setBackgroundImage() {
        Log.d(TAG, "method setBackgroundImage");
        Bitmap bitmap = ImageScaling.scaleDown(R.drawable.background_match, ImageScaling.screenWidth(), ImageScaling.screenHeight());
        bitmap = ImageScaling.crop(bitmap, ImageScaling.screenHeight(), ImageScaling.screenWidth());
        bitmap = ImageScaling.downscaleBitmap(bitmap, 2);
        mBackgroundImage.setImageBitmap(bitmap);
        AlphaAnimation animation1 = new AlphaAnimation(0.1f, 0.5f);
        animation1.setDuration(500);
        mBackgroundImage.setAlpha(0f);
        mBackgroundImage.startAnimation(animation1);
        mBackgroundImage.setImageAlpha(128);
    }


    //private methods check that the ORM's have correctly populated the  shared data records of prior
    //users and their match and swap games from the database with a matchCardData or swapBoardMap
    //specific data for each

    private void loadDatabase() {
        if (UserDataORM.userDataRecordsInDatabase(Shared.context)) {
            int dbLength = UserDataORM.numUserDataRecordsInDatabase(Shared.context);
            Shared.userDataList = new ArrayList<UserData>(dbLength);
            while (Shared.userDataList.size() < dbLength) {
                Shared.userDataList.add(UserData.getInstance());
            }
            Log.d(TAG, "**** Shared.userDataList.size(): " + Shared.userDataList.size() +
                    " | UserDataORM.getUserData(Shared.context).size(): " + dbLength);
            Collections.copy(Shared.userDataList, UserDataORM.getUserData(Shared.context));
            Log.d(TAG, "... Shared.userDataList.size(): " + Shared.userDataList.size() + " | @: " + Shared.userDataList);
            if (Shared.userDataList != null) {
                for (int i = 0; i < Shared.userDataList.size(); i++) {
                    Log.d(TAG, "... MAIN: UserData table: Database row: " + i +
                            " | userName: " + Shared.userDataList.get(i).getUserName() +
                            " | userAge: " + Shared.userDataList.get(i).getAgeRange() +
                            " | yearsTwitching: " + Shared.userDataList.get(i).getYearsTwitchingRange() +
                            " | speciesKnown: " + Shared.userDataList.get(i).getSpeciesKnownRange() +
                            " | audibleRecognized: " + Shared.userDataList.get(i).getAudibleRecognizedRange() +
                            " | interfaceExperience: " + Shared.userDataList.get(i).getInterfaceExperienceRange() +
                            " | hearingIsSeeing: " + Shared.userDataList.get(i).getHearingEqualsSeeing() +
                            " | usedSmartPhone: " + Shared.userDataList.get(i).getHasUsedSmartphone());
                    loadUsersMatchGameDataRecords(Shared.userDataList.get(i));
                    loadUsersSwapGameDataRecords(Shared.userDataList.get(i));
                    loadUsersComposeGameDataRecords(Shared.userDataList.get(i));
                }
            }
        } else if (UserDataORM.getUserData(Shared.context) == null) {
            //
            Log.d(TAG, "*!*!* no UserData objects in database, please create one");
        }
    }

    private void loadUsersMatchGameDataRecords(UserData userData) {
        if (MatchGameDataORM.matchGameRecordsInDatabase(Shared.context)) {
            int dbLength = MatchGameDataORM.numMatchGameRecordsInDatabase(Shared.context);
            Shared.matchGameDataList = new ArrayList<MatchGameData>(dbLength);
            while (Shared.matchGameDataList.size() < dbLength) {
                Shared.matchGameDataList.add(new MatchGameData());
            }
            Log.d(TAG, "**** Shared.matchGameDataList.size(): " + Shared.matchGameDataList.size() +
                    " | MatchGameDataORM.getMatchGameData(Shared.context).size(): " + dbLength);
            Collections.copy(Shared.matchGameDataList, MatchGameDataORM.getMatchGameData(userData.getUserName()));
            Log.d(TAG, "... Shared.matchGameDataList.size(): " + Shared.matchGameDataList.size() + " | @: " + Shared.matchGameDataList);
            if (Shared.matchGameDataList != null) {
                for (int i = 0; i < Shared.matchGameDataList.size(); i++) {
                    Log.d(TAG, "... MAIN: MatchGameData table: Database row: " + i +
                            " | gameStartTimestamp: " + Shared.matchGameDataList.get(i).getGameStartTimestamp() +
                            " | playerUserName: " + Shared.matchGameDataList.get(i).getUserPlayingName() +
                            " | themeID: " + Shared.matchGameDataList.get(i).getThemeID() +
                            " | difficultyLevel: " + Shared.matchGameDataList.get(i).getGameDifficulty() +
                            " | gameDurationAllocated: " + Shared.matchGameDataList.get(i).getGameDurationAllocated() +
                            " | mixerState: " + Shared.matchGameDataList.get(i).getMixerState() +
                            " | gameStarted: " + Shared.matchGameDataList.get(i).isGameStarted() +
                            " | numTurnsTakenInGame: " + Shared.matchGameDataList.get(i).getNumTurnsTaken());
                    if (Shared.matchGameDataList.get(i).sizeOfPlayDurationsArray() == Shared.matchGameDataList.get(i).sizeOfTurnDurationsArray()) {
                        for (int j = 0; j < Shared.matchGameDataList.get(i).sizeOfPlayDurationsArray(); j++) {
                            Log.d(TAG, " ... MAIN: GAME ARRAYS in MatchGame Table, " + j +
                                    " | current array element i: " + j +
                                    " | gamePlayDuration(i): " + Shared.matchGameDataList.get(i).queryGamePlayDurations(j) +
                                    " | turnDurations(i): " + Shared.matchGameDataList.get(i).queryTurnDurationsArray(j) +
                                    " | cardsSelected(i): " + Shared.matchGameDataList.get(i).queryCardsSelectedArray(j));
                            //FIXME - this method seems unecessary since all cards are loaded already into the game
                            //loadMatchCardSelectedData(Shared.matchGameDataList.get(i).queryCardsSelectedArray(j));
                        }
                    } else {
                        Log.d(TAG, " ***** ERROR! Size of play durations and turn durations not returned as equal");
                    }
                    Shared.userData.appendMatchGameData(Shared.matchGameDataList.get(i));
                }
            } else if (MatchGameDataORM.getMatchGameData(userData.getUserName()) == null) {
                //
                Log.d(TAG, "*!*!* no MatchGameData objects for userData.getUserName: " + userData.getUserName());
            }
        }
    }

    /* TODO did this method have any overarching purpose? it overwrites Shared.matchCardDataList to 0s
    //this private method iterates over the match data cards in the database
    private void loadMatchCardSelectedData(int cardID) {
        if (MatchCardDataORM.matchCardDataRecordsInDatabase(Shared.context)) {
            int dbLength = MatchCardDataORM.numMatchCardDataRecordsInDatabase(Shared.context);
            Shared.matchCardDataList = new ArrayList<MatchCardData>(dbLength);
            while (Shared.matchCardDataList.size() < dbLength) {
                Shared.matchCardDataList.add(new MatchCardData());
            }
            //Log.d(TAG, "**** Shared.matchCardDataList.size(): " + Shared.matchCardDataList.size() +
            //           " | MatchCardDataORM.getMatchCardData(Shared.context).size(): " + dbLength);
            Shared.matchCardData = MatchCardDataORM.getMatchCardData(cardID);
            //Log.d(TAG, "... Shared.matchCardDataList.size(): " + Shared.matchCardDataList.size() + " | @: " + Shared.matchCardDataList);
            if (Shared.matchCardDataList != null) {
                Log.d (TAG, "... PARSE: MatchCardData table: " +
                        " | cardID: " + Shared.matchCardData.getCardID() +
                        " | speciesName: " + Shared.matchCardData.getSpeciesName() +
                        " | pairedImagesDiffer: " + Shared.matchCardData.getPairedImageDiffer() +
                        " | firstImageUsed: " + Shared.matchCardData.getFirstImageUsed() +
                        " | imageURI0: " + Shared.matchCardData.getImageURI0() +
                        " | imageURI1: " + Shared.matchCardData.getImageURI1() +
                        " | imageURI2: " + Shared.matchCardData.getImageURI2() +
                        " | imageURI3: " + Shared.matchCardData.getImageURI3() +
                        " | audioURI: " + Shared.matchCardData.getAudioURI() +
                        " | sampleDuration: "+ Shared.matchCardData.getSampleDuration());
            } else if (MatchCardDataORM.getMatchCardData(cardID) == null) {
                //
                Log.d(TAG, "*!*!* no MatchCardData object for cardID: " + cardID);
            }
        }
        Shared.debugStateOfMatchCardDataList("Class MainActivity: private method loadMatchCardSelectedData");
    }
    */

    private void loadUsersSwapGameDataRecords(UserData userData) {
        //if there are records of previous swap games in the database for the given user
        if (SwapGameDataORM.swapGameRecordsInDatabase(Shared.context)) {
            //dbLength will hold the number of swap games played by the user
            int dbLength = SwapGameDataORM.numSwapGameRecordsInDatabase(Shared.context);
            //create a list of size dbLength to hold the SwapGameData objects for each game played
            Shared.swapGameDataList = new ArrayList<SwapGameData>(dbLength);
            //check that the sizes correspond
            Log.d(TAG, "**** Shared.swapGameDataList.size(): " + Shared.swapGameDataList.size() +
                    " | SwapGameDataORM.getSwapGameData(Shared.context).size(): " + dbLength);
            //copy the collection of SwapGameData objects returned from the ORM to the Shared list
            Shared.swapGameDataList = SwapGameDataORM.getSwapGameData(userData.getUserName());
            //check the size and memory location for the list THE REST IS FOR DEBUGGING
            Log.d(TAG, "... Shared.swapGameDataList.size(): " + Shared.swapGameDataList.size() + " | @: " + Shared.swapGameDataList);
            //iterate over the list to verify contents
            if (Shared.swapGameDataList != null) {
                for (int i = 0; i < Shared.swapGameDataList.size(); i++) {
                    Log.d(TAG, "... MAIN: SwapGameData table: Database row: " + i +
                            " | gameStartTimestamp: " + Shared.swapGameDataList.get(i).getGameStartTimestamp() +
                            " | playerUserName: " + Shared.swapGameDataList.get(i).getUserPlayingName() +
                            " | difficultyLevel: " + Shared.swapGameDataList.get(i).getGameDifficulty() +
                            " | gameDurationAllocated: " + Shared.swapGameDataList.get(i).getGameDurationAllocated() +
                            " | gameStarted: " + Shared.swapGameDataList.get(i).isGameStarted() +
                            " | numTurnsTakenInGame: " + Shared.swapGameDataList.get(i).getNumTurnsTaken());
                    if (Shared.swapGameDataList.get(i).sizeOfPlayDurationsArray() == Shared.swapGameDataList.get(i).sizeOfTurnDurationsArray()) {
                        for (int j = 0; j < Shared.swapGameDataList.get(i).sizeOfPlayDurationsArray(); j++) {
                            Log.d(TAG, " ... MAIN: GAME ARRAYS in SwapGameData Table, " + j +
                                    " | current array element i: " + j +
                                    " | gamePlayDuration(i): " + Shared.swapGameDataList.get(i).queryGamePlayDurations(j) +
                                    " | turnDurations(i): " + Shared.swapGameDataList.get(i).queryTurnDurationsArray(j) +
                                    " | swapBoardMaps(i): " + Shared.swapGameDataList.get(i).querySwapGameMapList(j));
                            //for each turn we return a hash map relating all of the <coords, cards> on the board
                            //TODO fixme? is this necessary? loadSwapBoardMap(Shared.swapGameDataList.get(i).querySwapGameMapList(j));
                        }
                    } else {
                        Log.d(TAG, " ***** ERROR! Size of play durations and turn durations not returned as equal");
                    }
                    Shared.userData.appendSwapGameData(Shared.swapGameDataList.get(i));
                }
            } else if (SwapGameDataORM.getSwapGameData(userData.getUserName()) == null) {
                //
                Log.e(TAG, "*!*!* no SwapGameData objects for userData.getUserName: " + userData.getUserName());
            }
        }
    }

    /*  //TODO can we print out the data associated with the hashmaps that make up the board turns for now most of
        //TODO this functionality is in the associated methods in SwapGameDataORM where the database is parsed back in
    //this should print out the data associated with the hashmap depicting the board at a given turn
    private void loadSwapBoardMap (HashMap curSwapGameCurTurnMap) {
        Log.d (TAG, "method loadSwapBoardMap...");
        Iterator iterator = curSwapGameCurTurnMap.entrySet().iterator();
        //iterate over the current turn board map
        while (iterator.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry) iterator.next();
            System.out.println(pair.getKey() + " maps to " + pair.getValue());
            SwapTileCoordinates coords = (SwapTileCoordinates) pair.getKey();
            SwapCardData cardData = (SwapCardData) pair.getValue();

            if (SwapCardDataORM.swapCardDataRecordsInDatabase(Shared.context)) {
                int numSwapCardRecords = SwapCardDataORM.numSwapCardDataRecordsInDatabase(Shared.context);
                Shared.swapCardDataList = new ArrayList<SwapCardData>(numSwapCardRecords);
                while (Shared.swapCardDataList.size() < numSwapCardRecords) {
                    Shared.swapCardDataList.add(new SwapCardData());
                }
                Shared.swapCardData = SwapCardDataORM.getSwapCardData(cardData.getCardID());
                if (Shared.swapCardDataList == cardData) {
                    Log.d(TAG, " ... PARSE Turn HashMap " +
                            " | coords.row: " + coords.getSwapCoordRow() +
                            " | coords.col: " + coords.getSwapCoordCol() +
                            " | cardID.species: " + Shared.swapCardData.getCardID().getSwapCardSpeciesID() +
                            " | cardID.segment: " + Shared.swapCardData.getCardID().getSwapCardSegmentID());
                }
            }
        }
    }
    */

    private void loadUsersComposeGameDataRecords (UserData userData) {
        //if there are records of previous compose games in the database for the given user
        if (ComposeGameDataORM.composeGameRecordsInDatabase(Shared.context)) {

            //dbLength will hold the number of swap games played by the user
            int dbLength = ComposeGameDataORM.numComposeGameRecordsInDatabase(Shared.context);

            //create a list of size dbLength to hold the ComposeGameData objects for each game played
            Shared.composeGameDataList = new ArrayList<ComposeGameData>(dbLength);

            //check that the sizes correspond
            Log.d(TAG, "**** Shared.composeGameDataList.size(): " + Shared.composeGameDataList.size() +
                    " | ComposeGameDataORM.getComposeGameData(Shared.context).size(): " + dbLength);

            //copy the collection of SwapGameData objects returned from the ORM to the Shared list
            Shared.swapGameDataList = SwapGameDataORM.getSwapGameData(userData.getUserName());

            //check the size and memory location for the list: THE REST IS FOR DEBUGGING
            Log.d(TAG, "... Shared.composeGameDataList.size(): " + Shared.composeGameDataList.size() + " | @: " + Shared.composeGameDataList);
            //iterate over the list to verify contents
            if (Shared.composeGameDataList != null) {
                for (int i = 0; i < Shared.composeGameDataList.size(); i++) {
                    Log.d(TAG, "... MAIN: ComposeGameData table: Database row: " + i +
                            " | gameStartTimestamp: " + Shared.composeGameDataList.get(i).getGameStartTimestamp() +
                            " | playerUserName: " + Shared.composeGameDataList.get(i).getUserPlayingName() +
                            " | numTurnsTakenInGame: " + Shared.composeGameDataList.get(i).getNumTurnsTaken());
                    if (Shared.composeGameDataList.get(i).sizeOfPlayDurationsArray() == Shared.composeGameDataList.get(i).sizeOfTurnDurationsArray()) {
                        for (int j = 0; j < Shared.composeGameDataList.get(i).sizeOfPlayDurationsArray(); j++) {
                            Log.d(TAG, " ... MAIN: GAME ARRAYS in ComposeGameData Table, " + j +
                                    " | current array element i: " + j +
                                    " | gamePlayDuration(i): " + Shared.composeGameDataList.get(i).queryGamePlayDurations(j) +
                                    " | turnDurations(i): " + Shared.composeGameDataList.get(i).queryTurnDurationsArray(j));
                        }
                    } else {
                        Log.d(TAG, " ***** ERROR! Size of play durations and turn durations not returned as equal");
                    }
                    Shared.userData.appendComposeGameData(Shared.composeGameDataList.get(i));
                }
            } else if (ComposeGameDataORM.getComposeGameData(userData.getUserName()) == null) {
                //
                Log.e(TAG, "*!*!* no ComposeGameData objects for userData.getUserName: " + userData.getUserName());
            }
        }
    }
}