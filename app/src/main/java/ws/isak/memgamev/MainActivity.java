package ws.isak.memgamev;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

import ws.isak.memgamev.common.Shared;
import ws.isak.memgamev.common.UserData;
import ws.isak.memgamev.database.MemGameDataORM;
import ws.isak.memgamev.engine.Engine;
import ws.isak.memgamev.engine.ScreenController;
import ws.isak.memgamev.engine.ScreenController.Screen;
import ws.isak.memgamev.events.EventBus;
import ws.isak.memgamev.events.ui.BackGameEvent;
import ws.isak.memgamev.model.MemGameData;
import ws.isak.memgamev.ui.PopupManager;
import ws.isak.memgamev.utils.Utils;
import ws.isak.memgamev.database.DatabaseWrapper;
import ws.isak.memgamev.database.UserDataORM;

/*
 * The main activity class of the app.  This activity class is called from the AndroidManifest.xml
 * as the application activity on launch.  This class instantiates the shared context, engine
 * and eventBus that guide the flow of the games.  On creation, the screen will open
 * with the USER_SETUP screen.
 *
 * @author isak
 */

public class  MainActivity extends FragmentActivity {

    public static final String TAG = "Class: MainActivity";
    private ImageView mBackgroundImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mBackgroundImage = (ImageView) findViewById(R.id.background_image);

        Log.d(TAG, "method onCreate: setting Shared data");
        Shared.context = getApplicationContext();
        Shared.activity = this;

        Shared.userData = UserData.getInstance();       //FIXME this is a place-keeper, set to specific on login
        Log.d(TAG, " *******: Shared.userData @: " + Shared.userData);
        Shared.engine = Engine.getInstance();
        Log.d(TAG, " *******: Shared.engine @: " + Shared.engine);
        Shared.eventBus = EventBus.getInstance();
        Log.d(TAG, " *******: Shared.eventBus @: " + Shared.eventBus);

        //instantiate a DatabaseWrapper
        DatabaseWrapper db = new DatabaseWrapper(this);
        Shared.databaseWrapper = db;
        loadDatabase();

        Shared.engine.start();
        Shared.engine.setBackgroundImageView(mBackgroundImage);

        // set background
        setBackgroundImage();

        // TODO figure out if we need this or can open to User setup screen... remove this?
        //ScreenController.getInstance().openScreen(Screen.MENU); //TODO...    remove this ?

        // open to User setup screen
        Log.d(TAG, "               : get instance of user setup screen");
        ScreenController.getInstance().openScreen(Screen.USER_SETUP);
    }

    @Override
    protected void onDestroy() {
        Shared.engine.stop();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        //do something here
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
	 * before the popup was the Game screen (implying that the popup is popup_won), then this triggers
	 * a BackGameEvent.
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
                Shared.eventBus.notify(new BackGameEvent());
            }
        } else if (ScreenController.getInstance().onBack()) {
            super.onBackPressed();
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
    //users and their games from the database TODO and card information?

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
                            " | usedSmartphone: " + Shared.userDataList.get(i).getHasUsedSmartphone());
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
                                    " | turnDurations(i): " + Shared.memGameDataList.get(i).queryTurnDurationsArray(j));
                            //" | cardSelectedOrder: " + memGameDataAtCursor. +) //TODO add cardSelectedOrder
                        }
                    } else {
                        //TODO extend to compare with cardObjects array size as well
                        Log.d(TAG, " ***** ERROR! Size of play durations and turn durations not returned as equal");
                    }
                    Shared.userData.appendMemGameData(Shared.memGameDataList.get(i));       //FIXME - is this how to add for testing?
                }
            } else if (MemGameDataORM.getMemGameData(userData.getUserName()) == null) {
                //
                Log.d(TAG, "*!*!* no MemGameData objects for userData.getUserName: " + userData.getUserName());
            }
        }
    }
}

