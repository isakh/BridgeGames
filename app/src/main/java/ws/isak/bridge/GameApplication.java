package ws.isak.bridge;

import android.app.Application;

import ws.isak.bridge.utils.FontLoader;

public class GameApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		FontLoader.loadFonts(this);

	}
}
