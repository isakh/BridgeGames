package ws.isak.bridge.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.util.Log;

import ws.isak.bridge.R;
import ws.isak.bridge.common.Audio;
import ws.isak.bridge.common.Shared;
import ws.isak.bridge.utils.FontLoader;
import ws.isak.bridge.utils.FontLoader.Font;

/*
 * The class MatchPopupSettingsView defines the settings popup for the matching game.
 *
 * @author isak
 */

public class MatchPopupSettingsView extends LinearLayout implements View.OnClickListener{

    public static final String TAG="MatchPopupSettingsView";

	private ImageView mSoundImage;
    private ImageView mMixImage;
	private TextView mSoundText;
    private TextView mMixText;

    /*
     * constructor
     */
	public MatchPopupSettingsView(Context context) {
		this(context, null);
        Log.d (TAG, "constructor PopupSettingsView");
	}

    /*
     * overloaded constructor takes attribute set as parameter
     */
	public MatchPopupSettingsView(Context context, AttributeSet attrs) {
		super(context, attrs);
        Log.d (TAG, "overloaded constructor PopupSettingsView");
		setOrientation(LinearLayout.VERTICAL);
		LayoutInflater.from(getContext()).inflate(R.layout.match_popup_settings_view, this, true);
		//Load images and text from xml
        mSoundText = (TextView) findViewById(R.id.sound_on_off_text);
        mMixText = (TextView) findViewById(R.id.mix_on_off_text);
		mSoundImage = (ImageView) findViewById(R.id.sound_on_off_image);
        mMixImage = (ImageView) findViewById(R.id.mix_on_off_image);
        FontLoader.setTypeface(context, new TextView[] { mSoundText, mMixText }, Font.ANGRYBIRDS);
		mSoundImage.setOnClickListener(this);
        mMixImage.setOnClickListener(this);
        setMusicButton();                       //initialize
        setMixerButton();
	}

    @Override
    public void onClick (View view) {
        switch (view.getId()) {
            case R.id.sound_on_off_image:
                Audio.OFF = !Audio.OFF;
                setMusicButton();
                break;
            case R.id.mix_on_off_image:
                Audio.MIX = !Audio.MIX;
                setMixerButton();
                break;
        }
    }

	private void setMusicButton() {
		if (Audio.OFF) {
			mSoundText.setText(Shared.context.getResources().getText(R.string.match_popup_settings_sound_off_text));
			mSoundImage.setImageResource(R.drawable.button_music_off);
		} else {
			mSoundText.setText(Shared.context.getResources().getText(R.string.match_popup_settings_sound_on_text));
			mSoundImage.setImageResource(R.drawable.button_music_on);
		}
	}

    private void setMixerButton() {
        if (Audio.MIX) {       //mixing is on
            mMixText.setText(Shared.context.getResources().getText(R.string.match_popup_settings_mixer_on_text));
            mMixImage.setImageResource(R.drawable.button_mixer_on);
        } else {               //mixing is off
            mMixText.setText(Shared.context.getResources().getText(R.string.match_popup_settings_mixer_off_text));
            mMixImage.setImageResource(R.drawable.button_mixer_off);
        }
    }
}
