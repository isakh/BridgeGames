package ws.isak.memgamev.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.util.Log;

import ws.isak.memgamev.R;
import ws.isak.memgamev.common.Audio;
import ws.isak.memgamev.common.Shared;
import ws.isak.memgamev.events.ui.MatchStartEvent;
import ws.isak.memgamev.ui.PopupManager;
import ws.isak.memgamev.utils.Utils;

/*
 * Class MatchMenuFragment sets up the fragment that allows the user to either choose settings for
 * the matching game, or to launch the difficulty selection fragment prior to starting game play.  It
 * contains the match_menu_title, start game button/lights/tooltip and the settings button and includes the methods
 * for animating them.  When this fragment is open, the background music is playing.
 *
 * @author isak
 */

public class MatchMenuFragment extends Fragment {

    private static final String TAG = "MatchMenuFragment";

	private ImageView mTitle;
	private ImageView mStartGameButton;
	private ImageView mStartButtonLights;
	private ImageView mTooltip;
	private ImageView mSettingsGameButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.match_menu_fragment, container, false);
		mTitle = (ImageView) view.findViewById(R.id.match_menu_title);
		mStartGameButton = (ImageView) view.findViewById(R.id.start_match_game_button);
		mSettingsGameButton = (ImageView) view.findViewById(R.id.settings_match_game_button);
		mSettingsGameButton.setSoundEffectsEnabled(false);
		mSettingsGameButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                Log.d (TAG, "method onCreateView: mSettingsGameButton: onClick");
				PopupManager.showMatchPopupSettings();
			}
		});
		mStartButtonLights = (ImageView) view.findViewById(R.id.start_match_game_button_lights);
		mTooltip = (ImageView) view.findViewById(R.id.match_menu_tooltip);
		mStartGameButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
                Log.d (TAG, "method onCreateView: mStartGameButton: onClick");
                // animate match_menu_title from place and navigation buttons from place
				animateAllAssetsOff(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
                        Log.d (TAG, "method onCreateView: mStartGameButton: onClick: animateAllAssetsOff: onAnimationEnd");
                        Shared.eventBus.notify(new MatchStartEvent());
					}
				});
			}
		});

		startLightsAnimation();
		startTootipAnimation();

		// play background music
		Audio.playBackgroundMusic();
		return view;
	}

	protected void animateAllAssetsOff(AnimatorListenerAdapter adapter) {
		// match_menu_title
		// 120dp + 50dp + buffer(30dp)
		ObjectAnimator titleAnimator = ObjectAnimator.ofFloat(mTitle, "translationY", Utils.px(-200));  //TODO constant to variable
		titleAnimator.setInterpolator(new AccelerateInterpolator(2));
		titleAnimator.setDuration(300);

		// lights
		ObjectAnimator lightsAnimatorX = ObjectAnimator.ofFloat(mStartButtonLights, "scaleX", 0f);
		ObjectAnimator lightsAnimatorY = ObjectAnimator.ofFloat(mStartButtonLights, "scaleY", 0f);

		// tooltip
		ObjectAnimator tooltipAnimator = ObjectAnimator.ofFloat(mTooltip, "alpha", 0f);
		tooltipAnimator.setDuration(100);

		// settings button
		ObjectAnimator settingsAnimator = ObjectAnimator.ofFloat(mSettingsGameButton, "translationY", Utils.px(120));
		settingsAnimator.setInterpolator(new AccelerateInterpolator(2));
		settingsAnimator.setDuration(300);

		// start button
		ObjectAnimator startButtonAnimator = ObjectAnimator.ofFloat(mStartGameButton, "translationY", Utils.px(130));
		startButtonAnimator.setInterpolator(new AccelerateInterpolator(2));
		startButtonAnimator.setDuration(300);

		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.playTogether(titleAnimator, lightsAnimatorX, lightsAnimatorY, tooltipAnimator, settingsAnimator, startButtonAnimator);
		animatorSet.addListener(adapter);
		animatorSet.start();
	}

	private void startTootipAnimation() {
		ObjectAnimator scaleY = ObjectAnimator.ofFloat(mTooltip, "scaleY", 0.8f);
		scaleY.setDuration(200);
		ObjectAnimator scaleYBack = ObjectAnimator.ofFloat(mTooltip, "scaleY", 1f);
		scaleYBack.setDuration(500);
		scaleYBack.setInterpolator(new BounceInterpolator());
		final AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.setStartDelay(1000);
		animatorSet.playSequentially(scaleY, scaleYBack);
		animatorSet.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				animatorSet.setStartDelay(2000);
				animatorSet.start();
			}
		});
		mTooltip.setLayerType(View.LAYER_TYPE_HARDWARE, null);
		animatorSet.start();
	}

	private void startLightsAnimation() {
		ObjectAnimator animator = ObjectAnimator.ofFloat(mStartButtonLights, "rotation", 0f, 360f);
		animator.setInterpolator(new AccelerateDecelerateInterpolator());
		animator.setDuration(6000);
		animator.setRepeatCount(ValueAnimator.INFINITE);
		mStartButtonLights.setLayerType(View.LAYER_TYPE_HARDWARE, null);
		animator.start();
	}
}
