package ws.isak.memgamev;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;

import ws.isak.memgamev.common.Shared;
import ws.isak.memgamev.engine.Engine;
import ws.isak.memgamev.engine.ScreenController;
import ws.isak.memgamev.engine.ScreenController.Screen;
import ws.isak.memgamev.events.EventBus;
import ws.isak.memgamev.events.ui.BackGameEvent;
import ws.isak.memgamev.ui.PopupManager;
import ws.isak.memgamev.utils.Utils;

/*
 * THe main activity class of the app.  This instantiates the shared context, engine
 * and eventBus that guide the flow of the games.  On creation, the screen will open
 * with the Menu screen (TODO should we ∆ this to USER_SETUP screen?, should USER_SETUP be Setup or Login??
 */

public class  MainActivity extends FragmentActivity {

	private ImageView mBackgroundImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Shared.context = getApplicationContext();
		Shared.engine = Engine.getInstance();
		Shared.eventBus = EventBus.getInstance();

		setContentView(R.layout.activity_main);
		mBackgroundImage = (ImageView) findViewById(R.id.background_image);

		Shared.activity = this;
		Shared.engine.start();
		Shared.engine.setBackgroundImageView(mBackgroundImage);

		// set background
		setBackgroundImage();

		// TODO figure out if we need this or can open to User setup screen
		//ScreenController.getInstance().openScreen(Screen.MENU);

        // open to User setup screen
        ScreenController.getInstance().openScreen(Screen.USER_SETUP);
	}

	@Override
	protected void onDestroy() {
		Shared.engine.stop();
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		if (PopupManager.isShown()) {
			PopupManager.closePopup();
			if (ScreenController.getLastScreen() == Screen.GAME) {
				Shared.eventBus.notify(new BackGameEvent());
			}
		} else if (ScreenController.getInstance().onBack()) {
			super.onBackPressed();
		}
	}

	private void setBackgroundImage() {
		Bitmap bitmap = Utils.scaleDown(R.drawable.background, Utils.screenWidth(), Utils.screenHeight());
		bitmap = Utils.crop(bitmap, Utils.screenHeight(), Utils.screenWidth());
		bitmap = Utils.downscaleBitmap(bitmap, 2);
		mBackgroundImage.setImageBitmap(bitmap);
	}
}
