package ws.isak.memgamev;

import android.app.Application;

import ws.isak.memgamev.utils.FontLoader;

public class GameApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		FontLoader.loadFonts(this);

	}
}
