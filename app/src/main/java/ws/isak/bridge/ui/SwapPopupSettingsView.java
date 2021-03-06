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
import ws.isak.bridge.common.SwapPreferences;
import ws.isak.bridge.utils.FontLoader;
import ws.isak.bridge.utils.FontLoader.Font;

/*
 * The class MatchPopupSettingsView defines the settings popup for the matching game.
 *
 * @author isak
 */

public class SwapPopupSettingsView extends LinearLayout implements View.OnClickListener{

    public static final String TAG="SwapPopupSettingsView";

    private ImageView mLooperImage;
    private ImageView mMixImage;
    private ImageView mWinningDifficultyImage;
    private TextView mLooperText;
    private TextView mMixText;
    private TextView mWinningDifficultyText;

    /*
     * constructor
     */
    public SwapPopupSettingsView(Context context) {
        this(context, null);
        Log.d (TAG, "constructor SwapPopupSettingsView");
    }

    /*
     * overloaded constructor takes attribute set as parameter
     */
    public SwapPopupSettingsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d (TAG, "overloaded constructor SwapPopupSettingsView");
        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.swap_popup_settings_view, this, true);
        //Load images and text from xml
        mLooperText = (TextView) findViewById(R.id.looper_on_off_text);
        mMixText = (TextView) findViewById(R.id.mix_on_off_text);
        mWinningDifficultyText = (TextView) findViewById(R.id.win_easy_hard_text);
        mLooperImage = (ImageView) findViewById(R.id.looper_on_off_image);
        mMixImage = (ImageView) findViewById(R.id.mix_on_off_image);
        mWinningDifficultyImage = (ImageView) findViewById(R.id.win_easy_hard_image);
        FontLoader.setTypeface(context, new TextView[] { mLooperText, mMixText, mWinningDifficultyText }, Font.ANGRYBIRDS);
        mLooperImage.setOnClickListener(this);
        mMixImage.setOnClickListener(this);
        mWinningDifficultyImage.setOnClickListener(this);
        setLooperButton();                       //initialize
        setMixerButton();
        setWinningDifficultyButton();
    }

    @Override
    public void onClick (View view) {
        switch (view.getId()) {
            case R.id.looper_on_off_image:
                Audio.LOOPER = !Audio.LOOPER;
                setLooperButton();
                break;
            case R.id.mix_on_off_image:
                Audio.MIX = !Audio.MIX;
                setMixerButton();
                break;
            case R.id.win_easy_hard_image:
                int winningDifficulty = SwapPreferences.getWinningDifficulty();
                int newWinningDifficulty = (winningDifficulty + 1) % 2;     //(0+1)%2 = 1; (1+1)%2 = 0
                SwapPreferences.setWinningDifficulty(newWinningDifficulty);
                setWinningDifficultyButton();
        }
    }

    private void setLooperButton() {
        Log.d (TAG, "method setLooperButton");
        if (Audio.LOOPER) {
            Log.d (TAG, "method setLooperButton: Audio.Looper (should be ON): " + Audio.LOOPER +
                        " text to display: " + Shared.context.getResources().getText(R.string.swap_popup_settings_looper_on_text));
            mLooperText.setText(Shared.context.getResources().getText(R.string.swap_popup_settings_looper_on_text));
            mLooperImage.setImageResource(R.drawable.button_looper_on);
        } else {
            Log.d (TAG, "method setLooperButton: Audio.Looper (should be OFF): " + Audio.LOOPER +
                    " text to display: " + Shared.context.getResources().getText(R.string.swap_popup_settings_looper_off_text));
            mLooperText.setText(Shared.context.getResources().getText(R.string.swap_popup_settings_looper_off_text));
            mLooperImage.setImageResource(R.drawable.button_looper_off);
        }
    }

    private void setMixerButton() {
        if (Audio.MIX) {       //mixing is on
            mMixText.setText(Shared.context.getResources().getText(R.string.swap_popup_settings_mixer_on_text));
            mMixImage.setImageResource(R.drawable.button_mixer_on);
        } else {               //mixing is off
            mMixText.setText(Shared.context.getResources().getText(R.string.swap_popup_settings_mixer_off_text));
            mMixImage.setImageResource(R.drawable.button_mixer_off);
        }
    }

    private void setWinningDifficultyButton() {
        if (SwapPreferences.getWinningDifficulty() == 0) { //set to easy
            mWinningDifficultyText.setText(Shared.context.getResources().getText(R.string.swap_popup_settings_winning_easy_text));
            mWinningDifficultyImage.setImageResource(R.drawable.swap_winning_easy);
        }
        else {
            mWinningDifficultyText.setText(Shared.context.getResources().getText(R.string.swap_popup_settings_winning_hard_text));
            mWinningDifficultyImage.setImageResource(R.drawable.swap_winning_hard);
        }
    }
}