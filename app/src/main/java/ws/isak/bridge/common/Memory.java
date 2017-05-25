package ws.isak.bridge.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.Locale;


/*
 * The memory class holds data about how each user has performed at each of the games.  If a new user
 * is playing, then this should be set to zero across all games.  If a user has previously played ,this
 * should load their current best performances
 */

public class Memory {

    private static final String TAG = "Memory";

	private static final String SHARED_PREFERENCES_NAME = "ws.isak.bridge";
	private static String matchHighStarKey = "theme_%d_difficulty_%d";
    private static String swapHighStarKey = "difficulty_%d";
    private static String composeHighStarKey = "difficulty_%d";


    public static void saveMatch (int theme, int difficulty, int stars) {
		int matchHighStars = getMatchHighStars(theme, difficulty);
		if (stars > matchHighStars) {
			SharedPreferences sharedPreferences = Shared.context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
			Editor edit = sharedPreferences.edit();
			String key = String.format(Locale.ENGLISH, matchHighStarKey, theme, difficulty);
			edit.putInt(key, stars).commit();
		}
	}

    public static void saveSwap (int difficulty, int stars) {
        int swapHighStars = getSwapHighStars(difficulty);
        if (stars > swapHighStars) {
            SharedPreferences sharedPreferences = Shared.context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
            Editor edit = sharedPreferences.edit();
            String key = String.format(Locale.ENGLISH, swapHighStarKey, difficulty);
            edit.putInt(key, stars).commit();
        }
    }

    public static void saveCompose (int difficulty, int stars) {
        switch (difficulty) {
            case 1:
                if (stars > Shared.userData.getComposeHighStarsDifficulty1()) {
                    Shared.userData.setComposeHighStarsDifficulty1(stars);
                }
                break;
            case 2:
                if (stars > Shared.userData.getComposeHighStarsDifficulty2()) {
                    Shared.userData.setComposeHighStarsDifficulty2(stars);
                }
                break;
            case 3:
                if (stars > Shared.userData.getComposeHighStarsDifficulty3()) {
                    Shared.userData.setComposeHighStarsDifficulty3(stars);
                }
        }
    }

	public static int getMatchHighStars(int theme, int difficulty) {
		SharedPreferences sharedPreferences = Shared.context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		String key = String.format(matchHighStarKey, theme, difficulty);
		return sharedPreferences.getInt(key, 0);
	}

	public static int getSwapHighStars (int difficulty) {
        SharedPreferences sharedPreferences = Shared.context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        String key = String.format(swapHighStarKey, difficulty);
        return sharedPreferences.getInt(key, 0);
    }

    public static int getComposeHighStars (int difficulty) {
        int starsAcheived = 0;
        switch (difficulty) {
            case 1:
                starsAcheived = Shared.userData.getComposeHighStarsDifficulty1();
            case 2:
                starsAcheived = Shared.userData.getComposeHighStarsDifficulty2();
            case 3:
                starsAcheived = Shared.userData.getComposeHighStarsDifficulty3();
        }
        return starsAcheived;
    }
}
