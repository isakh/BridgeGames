package ws.isak.memgamev.engine;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import ws.isak.memgamev.R;
import ws.isak.memgamev.common.Shared;
import ws.isak.memgamev.events.ui.ResetBackgroundEvent;
import ws.isak.memgamev.fragments.DifficultySelectFragment;
import ws.isak.memgamev.fragments.GameFragment;
import ws.isak.memgamev.fragments.MenuFragment;
import ws.isak.memgamev.fragments.ThemeSelectFragment;
import ws.isak.memgamev.fragments.UserSetupFragment;

public class ScreenController {

    public static final String TAG = "Class: ScreenController";

	private static ScreenController mInstance = null;
	private static List<Screen> openedScreens = new ArrayList<Screen>();
	private FragmentManager mFragmentManager;

	private ScreenController() {
        Log.d (TAG, "constructor does nothing");
	}

	public static ScreenController getInstance() {
		if (mInstance == null) {
			mInstance = new ScreenController();
		}
		return mInstance;
	}

	public static enum Screen {
		MENU,
		GAME,
		DIFFICULTY,
		THEME_SELECT,
        USER_SETUP
        //TODO add GAME_SELECT SCREEN
	}
	
	public static Screen getLastScreen() {
        Log.d (TAG, "method getLastScreen");
		return openedScreens.get(openedScreens.size() - 1);
	}

	public void openScreen(Screen screen) {
		mFragmentManager = Shared.activity.getSupportFragmentManager();
		
		if (screen == Screen.GAME && openedScreens.get(openedScreens.size() - 1) == Screen.GAME) {
			openedScreens.remove(openedScreens.size() - 1);
		} else if (screen == Screen.DIFFICULTY && openedScreens.get(openedScreens.size() - 1) == Screen.GAME) {
			openedScreens.remove(openedScreens.size() - 1);
			openedScreens.remove(openedScreens.size() - 1);
		}
		Fragment fragment = getFragment(screen);
		FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.fragment_container, fragment);
		fragmentTransaction.commit();
		openedScreens.add(screen);
	}

	public boolean onBack() {
		if (openedScreens.size() > 0) {
			Screen screenToRemove = openedScreens.get(openedScreens.size() - 1);
			openedScreens.remove(openedScreens.size() - 1);
			if (openedScreens.size() == 0) {
				return true;
			}
			Screen screen = openedScreens.get(openedScreens.size() - 1);
			openedScreens.remove(openedScreens.size() - 1);
			openScreen(screen);
			if ((screen == Screen.THEME_SELECT || screen == Screen.MENU) && 
					(screenToRemove == Screen.DIFFICULTY || screenToRemove == Screen.GAME)) {
				Shared.eventBus.notify(new ResetBackgroundEvent());
			}
			return false;
		}
		return true;
	}

	private Fragment getFragment(Screen screen) {
		switch (screen) {
		    case MENU:
			    return new MenuFragment();
		    case DIFFICULTY:
			    return new DifficultySelectFragment();
    		case GAME:
	    		return new GameFragment();
    		case THEME_SELECT:
	    		return new ThemeSelectFragment();
            case USER_SETUP:
                return new UserSetupFragment();
            //TODO case GAME_SELECT
            //TODO      return new GameSelectFragment();

		    default:
			    break;
		}
		return null;
	}
}
