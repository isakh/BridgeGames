package ws.isak.memgamev.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.Locale;


/*
 * The memory class holds data that is stored for each player which can be accessed from their
 * userID (unique name field in UserData)
 * TODO based on a user name setup in a UserSetup Fragment?
 * 			TODO How to organize the user data storage on device
 * 			TODO what information is to be collected for each game played?:
 *				TODO Total time for given game (function of samples selected + difficulty constants)
 *				TODO Total number of moves for completion (e.g. num tiles would be 'perfect' game, any additional of interest
 *				TODO keep track of the sample time for duplicate tiles heard so we can see whether longer or shorter samples are more error prone
 *				TODO ??? WHAT ADDITIONAL INFORMATION NEEDS TO BE STORED
 *			see http://stackoverflow.com/questions/9986734/which-android-data-storage-technique-to-use for more info
 */

public class Memory {

    private static final String TAG = "Memory";

	private static final String SHARED_PREFERENCES_NAME = "ws.isak.memgamev";
	private static String highStarKey = "theme_%d_difficulty_%d";

	public static void save(int theme, int difficulty, int stars) {
		int highStars = getHighStars(theme, difficulty);
		if (stars > highStars) {
			SharedPreferences sharedPreferences = Shared.context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
			Editor edit = sharedPreferences.edit();
			String key = String.format(Locale.ENGLISH, highStarKey, theme, difficulty);
			edit.putInt(key, stars).commit();
		}
	}

	public static int getHighStars(int theme, int difficulty) {
		SharedPreferences sharedPreferences = Shared.context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		String key = String.format(highStarKey, theme, difficulty);
		return sharedPreferences.getInt(key, 0);
	}
}
