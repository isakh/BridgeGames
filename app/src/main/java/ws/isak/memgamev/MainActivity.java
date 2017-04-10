package ws.isak.memgamev;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import ws.isak.memgamev.common.CardData;
import ws.isak.memgamev.common.Audio;
import ws.isak.memgamev.common.Shared;
import ws.isak.memgamev.common.UserData;
import ws.isak.memgamev.database.CardDataORM;
import ws.isak.memgamev.database.MemGameDataORM;
import ws.isak.memgamev.engine.Engine;
import ws.isak.memgamev.engine.ScreenController;
import ws.isak.memgamev.engine.ScreenController.Screen;
import ws.isak.memgamev.events.EventBus;
import ws.isak.memgamev.events.ui.MatchBackGameEvent;
import ws.isak.memgamev.model.MemGameData;
import ws.isak.memgamev.ui.PopupManager;
import ws.isak.memgamev.utils.Utils;
import ws.isak.memgamev.database.DatabaseWrapper;
import ws.isak.memgamev.database.UserDataORM;

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

        //build the list of CardData objects based on resources
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
            if (ScreenController.getLastScreen() == Screen.GAME_MEM) {
                Shared.eventBus.notify(new MatchBackGameEvent());
            }
        } else if (ScreenController.getInstance().onBack()) {
            super.onBackPressed();
        }
    }

    //build the cardData list in for access by themes
    private void buildCardDataList () {
        Shared.cardDataList = new ArrayList<CardData>();

        for (int i = 1; i <= 10; i++) {                             //FIXME - make this constant into a variable
            CardData curCard = new CardData();
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
            //insert cardData object into Database and local storage
            Shared.cardDataList.add(curCard);
            if (!CardDataORM.isCardDataInDB(curCard)) {
                CardDataORM.insertCardData(curCard);
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
    //users and their games from the database with a cardData for each

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
        if (MemGameDataORM.memGameRecordsInDatabase(Shared.context)) {
            int dbLength = MemGameDataORM.numMemGameRecordsInDatabase(Shared.context);
            Shared.memGameDataList = new ArrayList<MemGameData>(dbLength);
            while (Shared.memGameDataList.size() < dbLength) {
                Shared.memGameDataList.add(new MemGameData());
            }
            Log.d(TAG, "**** Shared.memGameDataList.size(): " + Shared.memGameDataList.size() +
                    " | MemGameDataORM.getMemGameData(Shared.context).size(): " + dbLength);
            Collections.copy(Shared.memGameDataList, MemGameDataORM.getMemGameData(userData.getUserName()));
            Log.d(TAG, "... Shared.memGameDataList.size(): " + Shared.memGameDataList.size() + " | @: " + Shared.memGameDataList);
            if (Shared.memGameDataList != null) {
                for (int i = 0; i < Shared.memGameDataList.size(); i++) {
                    Log.d(TAG, "... MAIN: MemGameData table: Database row: " + i +
                            " | gameStartTimestamp: " + Shared.memGameDataList.get(i).getGameStartTimestamp() +
                            " | playerUserName: " + Shared.memGameDataList.get(i).getUserPlayingName() +
                            " | themeID: " + Shared.memGameDataList.get(i).getThemeID() +
                            " | difficulty: " + Shared.memGameDataList.get(i).getGameDifficulty() +
                            " | gameDurationAllocated: " + Shared.memGameDataList.get(i).getGameDurationAllocated() +
                            " | mixerState: " + Shared.memGameDataList.get(i).getMixerState() +
                            " | gameStarted: " + Shared.memGameDataList.get(i).isGameStarted() +
                            " | numTurnsTakenInGame: " + Shared.memGameDataList.get(i).getNumTurnsTaken());
                    if (Shared.memGameDataList.get(i).sizeOfPlayDurationsArray() == Shared.memGameDataList.get(i).sizeOfTurnDurationsArray()) {
                        for (int j = 0; j < Shared.memGameDataList.get(i).sizeOfPlayDurationsArray(); j++) {
                            Log.d(TAG, " ... MAIN: GAME ARRAYS in MemGame Table, " + j +
                                    " | current array element i: " + j +
                                    " | gamePlayDuration(i): " + Shared.memGameDataList.get(i).queryGamePlayDurations(j) +
                                    " | turnDurations(i): " + Shared.memGameDataList.get(i).queryTurnDurationsArray(j) +
                                    " | cardsSelected(i): " + Shared.memGameDataList.get(i).queryCardsSelectedArray(j));
                            loadCardSelectedData (Shared.memGameDataList.get(i).queryCardsSelectedArray(j));
                        }
                    } else {
                        Log.d(TAG, " ***** ERROR! Size of play durations and turn durations not returned as equal");
                    }
                    Shared.userData.appendMemGameData(Shared.memGameDataList.get(i));
                }
            } else if (MemGameDataORM.getMemGameData(userData.getUserName()) == null) {
                //
                Log.d(TAG, "*!*!* no MemGameData objects for userData.getUserName: " + userData.getUserName());
            }
        }
    }

    //this should print out the information associated with the cardData object with id cardID
    private void loadCardSelectedData (int cardID) {
        if (CardDataORM.cardDataRecordsInDatabase(Shared.context)) {
            int dbLength = CardDataORM.numCardDataRecordsInDatabase(Shared.context);
            Shared.cardDataList = new ArrayList<CardData>(dbLength);
            while (Shared.cardDataList.size() < dbLength) {
                Shared.cardDataList.add(new CardData());
            }
            //Log.d(TAG, "**** Shared.cardDataList.size(): " + Shared.cardDataList.size() +
            //           " | CardDataORM.getCardData(Shared.context).size(): " + dbLength);
            Shared.cardData = CardDataORM.getCardData(cardID);
            //Log.d(TAG, "... Shared.cardDataList.size(): " + Shared.cardDataList.size() + " | @: " + Shared.cardDataList);
            if (Shared.cardDataList != null) {
                Log.d (TAG, "... PARSE: CardData table: " +
                        " | cardID: " + Shared.cardData.getCardID() +
                        " | speciesName: " + Shared.cardData.getSpeciesName() +
                        " | pairedImagesDiffer: " + Shared.cardData.getPairedImageDiffer() +
                        " | firstImageUsed: " + Shared.cardData.getFirstImageUsed() +
                        " | imageURI0: " + Shared.cardData.getImageURI0() +
                        " | imageURI1: " + Shared.cardData.getImageURI1() +
                        " | imageURI2: " + Shared.cardData.getImageURI2() +
                        " | imageURI3: " + Shared.cardData.getImageURI3() +
                        " | audioURI: " + Shared.cardData.getAudioURI() +
                        " | sampleDuration: "+ Shared.cardData.getSampleDuration());
            } else if (CardDataORM.getCardData(cardID) == null) {
                //
                Log.d(TAG, "*!*!* no CardData object for cardID: " + cardID);
            }
        }
    }
}