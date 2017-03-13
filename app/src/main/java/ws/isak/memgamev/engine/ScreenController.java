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
import ws.isak.memgamev.fragments.PreSurveyFragment;

/*
 * Class ScreenController instantiates a list of currently openedScreens and a fragmentManager
 * ... //TODO
 *
 * @author isak
 */

public class ScreenController {

    public static final String TAG = "Class: ScreenController";

	private static ScreenController mInstance = null;
	private static List<Screen> openedScreens = new ArrayList<Screen>();
	private FragmentManager mFragmentManager;

	private ScreenController() {
        Log.d (TAG, "constructor does nothing");
	}

	public static ScreenController getInstance() {
        Log.d (TAG, "method getInstance of ScreenController");
		if (mInstance == null) {
			mInstance = new ScreenController();
		}
		return mInstance;
	}

	public enum Screen {        //FIXME? was: public static enum Screen
        USER_SETUP,
        PRE_SURVEY,
        SELECT_GAME,            //choose between memory game and swap game
		MENU_MEM,               //menu allows choices of audio playback
        MENU_SWAP,              //TODO - audio playback? required??
        THEME_SELECT,           //theme is only relevant to memory game
        DIFFICULTY_MEM,         //three levels of difficulty available
        DIFFICULTY_SWAP,        //TODO - start with two levels
        GAME_MEM,
        GAME_SWAP,               //TODO
        POST_SURVEY              //TODO should we have different ones for each game? and/or one for all?
    }
	
	public static Screen getLastScreen() {
        Log.d (TAG, "method getLastScreen");
		return openedScreens.get(openedScreens.size() - 1);
	}

	public void openScreen(Screen screen) {
        Log.d (TAG, "Method openScreen: creating mFragmentManager");
		mFragmentManager = Shared.activity.getSupportFragmentManager();
		
		if (screen == Screen.GAME_MEM && openedScreens.get(openedScreens.size() - 1) == Screen.GAME_MEM) {
			openedScreens.remove(openedScreens.size() - 1);
		} else if (screen == Screen.DIFFICULTY_MEM && openedScreens.get(openedScreens.size() - 1) == Screen.GAME_MEM) {
			openedScreens.remove(openedScreens.size() - 1);
			openedScreens.remove(openedScreens.size() - 1);
		}
		Fragment fragment = getFragment(screen);
		FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.fragment_container, fragment);
		fragmentTransaction.commit();
		openedScreens.add(screen);
	}

	public boolean onBack() {               //FIXME lots of new options for how far back to go with added screens
		if (openedScreens.size() > 0) {
			Screen screenToRemove = openedScreens.get(openedScreens.size() - 1);
			openedScreens.remove(openedScreens.size() - 1);
			if (openedScreens.size() == 0) {
				return true;
			}
			Screen screen = openedScreens.get(openedScreens.size() - 1);
			openedScreens.remove(openedScreens.size() - 1);
			openScreen(screen);
			if ((screen == Screen.THEME_SELECT || screen == Screen.MENU_MEM) &&
					(screenToRemove == Screen.DIFFICULTY_MEM || screenToRemove == Screen.GAME_MEM)) {
				Shared.eventBus.notify(new ResetBackgroundEvent());
			}
			return false;
		}
		return true;
	}

	private Fragment getFragment(Screen screen) {
		switch (screen) {
            case USER_SETUP:
                return new UserSetupFragment();
            case PRE_SURVEY:
                return new PreSurveyFragment();
            //case SELECT_GAME:
            //    return new SelectGameFragment();
		    case MENU_MEM:
			    return new MenuFragment();
            //case MENU_SWAP:
            //    return new SwapMenuFragment();
            case THEME_SELECT:
                return new ThemeSelectFragment();
            case DIFFICULTY_MEM:
			    return new DifficultySelectFragment();
            //case DIFFICULTY_SWAP:
            //    return new SwapDifficultyFragment();
            case GAME_MEM:
	    		return new GameFragment();
            //case GAME_SWAP:
            //    return new SwapGameFragment();
            //case POST_SURVEY:
            //    return new PostSurveyFragment();
		    default:
			    break;
		}
		return null;
	}
}
