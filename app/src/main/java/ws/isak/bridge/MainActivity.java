package ws.isak.bridge;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import ws.isak.bridge.common.MatchCardData;
import ws.isak.bridge.common.Audio;
import ws.isak.bridge.common.Shared;
import ws.isak.bridge.common.UserData;
import ws.isak.bridge.database.MatchCardDataORM;
import ws.isak.bridge.database.MatchGameDataORM;
import ws.isak.bridge.engine.Engine;
import ws.isak.bridge.engine.ScreenController;
import ws.isak.bridge.engine.ScreenController.Screen;
import ws.isak.bridge.events.EventBus;
import ws.isak.bridge.events.ui.MatchBackGameEvent;
import ws.isak.bridge.model.MatchGameData;
import ws.isak.bridge.ui.PopupManager;
import ws.isak.bridge.utils.Utils;
import ws.isak.bridge.database.DatabaseWrapper;
import ws.isak.bridge.database.UserDataORM;

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
        loadDatabase();

        Shared.engine.start();
        Shared.engine.setBackgroundImageView(mBackgroundImage);

        //build the list of MatchCardData objects based on resources
        // TODO can this become dynamic if I can load variable resources?
        buildCardDataList();

        // set background
        setBackgroundImage();

        // open to User setup screen
        //Log.d(TAG, "               : get instance of user setup screen");
        ScreenController.getInstance().openScreen(Screen.USER_SETUP);
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
    private void buildCardDataList () {
        Shared.matchCardDataList = new ArrayList<MatchCardData>();

        for (int i = 1; i <= 10; i++) {                             //FIXME - make this constant into a variable
            MatchCardData curCard = new MatchCardData();
            curCard.setCardID(i);
            curCard.setSpeciesName(curCard.getCardID());
            curCard.setPairedImageDiffer(false);
            curCard.setFirstImageUsed(false);
            curCard.setImageURI0(URI_DRAWABLE + "blank_card");
            curCard.setImageURI1(URI_DRAWABLE + String.format(Locale.ENGLISH, "bird_%d", i) + "a");
            curCard.setImageURI2(URI_DRAWABLE + String.format(Locale.ENGLISH, "bird_%d", i) + "b");
            curCard.setImageURI3(URI_DRAWABLE + String.format(Locale.ENGLISH, "spectrogram_%d", i));
            curCard.setAudioURI(URI_AUDIO + String.format(Locale.ENGLISH, "example%d", i));
            curCard.setSampleDuration(Audio.getAudioDuration(Shared.context.getResources().getIdentifier(curCard.getAudioURI().substring(URI_AUDIO.length()), "raw", Shared.context.getPackageName())));
            //insert matchCardData object into Database and local storage
            Shared.matchCardDataList.add(curCard);
            if (!MatchCardDataORM.isCardDataInDB(curCard)) {
                MatchCardDataORM.insertCardData(curCard);
            }
        }
    }

    private void setBackgroundImage() {
        Log.d(TAG, "method setBackgroundImage");
        Bitmap bitmap = Utils.scaleDown(R.drawable.background, Utils.screenWidth(), Utils.screenHeight());
        bitmap = Utils.crop(bitmap, Utils.screenHeight(), Utils.screenWidth());
        bitmap = Utils.downscaleBitmap(bitmap, 2);
        mBackgroundImage.setImageBitmap(bitmap);
    }


    //private methods check that the ORM's have correctly populated the  shared data records of prior
    //users and their games from the database with a matchCardData for each

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
                    loadUsersMemGameDataRecords(Shared.userDataList.get(i));
                }
            }
        } else if (UserDataORM.getUserData(Shared.context) == null) {
            //
            Log.d(TAG, "*!*!* no UserData objects in database, please create one");
        }
    }

    private void loadUsersMemGameDataRecords(UserData userData) {
        if (MatchGameDataORM.matchGameRecordsInDatabase(Shared.context)) {
            int dbLength = MatchGameDataORM.numMemGameRecordsInDatabase(Shared.context);
            Shared.matchGameDataList = new ArrayList<MatchGameData>(dbLength);
            while (Shared.matchGameDataList.size() < dbLength) {
                Shared.matchGameDataList.add(new MatchGameData());
            }
            Log.d(TAG, "**** Shared.matchGameDataList.size(): " + Shared.matchGameDataList.size() +
                    " | MatchGameDataORM.getMemGameData(Shared.context).size(): " + dbLength);
            Collections.copy(Shared.matchGameDataList, MatchGameDataORM.getMemGameData(userData.getUserName()));
            Log.d(TAG, "... Shared.matchGameDataList.size(): " + Shared.matchGameDataList.size() + " | @: " + Shared.matchGameDataList);
            if (Shared.matchGameDataList != null) {
                for (int i = 0; i < Shared.matchGameDataList.size(); i++) {
                    Log.d(TAG, "... MAIN: MatchGameData table: Database row: " + i +
                            " | gameStartTimestamp: " + Shared.matchGameDataList.get(i).getGameStartTimestamp() +
                            " | playerUserName: " + Shared.matchGameDataList.get(i).getUserPlayingName() +
                            " | themeID: " + Shared.matchGameDataList.get(i).getThemeID() +
                            " | difficulty: " + Shared.matchGameDataList.get(i).getGameDifficulty() +
                            " | gameDurationAllocated: " + Shared.matchGameDataList.get(i).getGameDurationAllocated() +
                            " | mixerState: " + Shared.matchGameDataList.get(i).getMixerState() +
                            " | gameStarted: " + Shared.matchGameDataList.get(i).isGameStarted() +
                            " | numTurnsTakenInGame: " + Shared.matchGameDataList.get(i).getNumTurnsTaken());
                    if (Shared.matchGameDataList.get(i).sizeOfPlayDurationsArray() == Shared.matchGameDataList.get(i).sizeOfTurnDurationsArray()) {
                        for (int j = 0; j < Shared.matchGameDataList.get(i).sizeOfPlayDurationsArray(); j++) {
                            Log.d(TAG, " ... MAIN: GAME ARRAYS in MemGame Table, " + j +
                                    " | current array element i: " + j +
                                    " | gamePlayDuration(i): " + Shared.matchGameDataList.get(i).queryGamePlayDurations(j) +
                                    " | turnDurations(i): " + Shared.matchGameDataList.get(i).queryTurnDurationsArray(j) +
                                    " | cardsSelected(i): " + Shared.matchGameDataList.get(i).queryCardsSelectedArray(j));
                            loadCardSelectedData (Shared.matchGameDataList.get(i).queryCardsSelectedArray(j));
                        }
                    } else {
                        Log.d(TAG, " ***** ERROR! Size of play durations and turn durations not returned as equal");
                    }
                    Shared.userData.appendMemGameData(Shared.matchGameDataList.get(i));
                }
            } else if (MatchGameDataORM.getMemGameData(userData.getUserName()) == null) {
                //
                Log.d(TAG, "*!*!* no MatchGameData objects for userData.getUserName: " + userData.getUserName());
            }
        }
    }

    //this should print out the information associated with the matchCardData object with id cardID
    private void loadCardSelectedData (int cardID) {
        if (MatchCardDataORM.cardDataRecordsInDatabase(Shared.context)) {
            int dbLength = MatchCardDataORM.numCardDataRecordsInDatabase(Shared.context);
            Shared.matchCardDataList = new ArrayList<MatchCardData>(dbLength);
            while (Shared.matchCardDataList.size() < dbLength) {
                Shared.matchCardDataList.add(new MatchCardData());
            }
            //Log.d(TAG, "**** Shared.matchCardDataList.size(): " + Shared.matchCardDataList.size() +
            //           " | MatchCardDataORM.getCardData(Shared.context).size(): " + dbLength);
            Shared.matchCardData = MatchCardDataORM.getCardData(cardID);
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
            } else if (MatchCardDataORM.getCardData(cardID) == null) {
                //
                Log.d(TAG, "*!*!* no MatchCardData object for cardID: " + cardID);
            }
        }
    }
}