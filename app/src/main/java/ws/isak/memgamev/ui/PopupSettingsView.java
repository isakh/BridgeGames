package ws.isak.memgamev.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.util.Log;

import ws.isak.memgamev.R;
import ws.isak.memgamev.common.Music;
import ws.isak.memgamev.utils.FontLoader;
import ws.isak.memgamev.utils.FontLoader.Font;

/*
 *
 *
 * @author isak
 */

public class PopupSettingsView extends LinearLayout {

    public static final String TAG="Class PopupSettingsView";

	private ImageView mSoundImage;
	private TextView mSoundText;

    /*
     * constructor
     */
	public PopupSettingsView(Context context) {
		this(context, null);
        Log.d (TAG, "constructor PopupSettingsView");
	}

    /*
     * overloaded constructor takes attribute set as parameter
     */
	public PopupSettingsView(Context context, AttributeSet attrs) {
		super(context, attrs);
        Log.d (TAG, "overloaded constructor PopupSettingsView");
		setOrientation(LinearLayout.VERTICAL);
		setBackgroundResource(R.drawable.settings_popup);
		LayoutInflater.from(getContext()).inflate(R.layout.popup_settings_view, this, true);
		mSoundText = (TextView) findViewById(R.id.sound_off_text);
		FontLoader.setTypeface(context, new TextView[] { mSoundText }, Font.ANGRYBIRDS);
		mSoundImage = (ImageView) findViewById(R.id.sound_image);
		View soundOff = findViewById(R.id.sound_off);
		soundOff.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Music.OFF = !Music.OFF;
				setMusicButton();
			}
		});
		setMusicButton();
	}

	private void setMusicButton() {
		if (Music.OFF) {
			mSoundText.setText("Sound OFF");
			mSoundImage.setImageResource(R.drawable.button_music_off);
		} else {
			mSoundText.setText("Sound ON");
			mSoundImage.setImageResource(R.drawable.button_music_on);
		}
	}
}
