package ws.isak.bridge.fragments;

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

import ws.isak.bridge.R;
import ws.isak.bridge.common.Audio;
import ws.isak.bridge.common.Shared;
import ws.isak.bridge.events.ui.SwapStartEvent;
import ws.isak.bridge.ui.PopupManager;
import ws.isak.bridge.utils.Utils;

/*
 * Class SwapMenuFragment sets up the fragment that allows the user to either choose settings for
 * the swapping game, or to launch the difficulty selection fragment prior to starting game play.  It
 * contains the swap_menu_title, start game button/lights/tooltip and the settings button and includes the methods
 * for animating them.  When this fragment is open, the background music is playing.
 * 
 * TODO shall we have different background audio for different games?
 * 
 * @author isak
 */

public class SwapMenuFragment extends Fragment {

    private static final String TAG = "MatchMenuFragment";

    private ImageView swapTitle;
    private ImageView swapStartGameButton;
    private ImageView swapStartButtonLights;
    private ImageView swapTooltip;
    private ImageView swapSettingsButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.swap_menu_fragment, container, false);
        swapTitle = (ImageView) view.findViewById(R.id.swap_menu_title);
        swapStartGameButton = (ImageView) view.findViewById(R.id.start_swap_game_button);
        swapSettingsButton = (ImageView) view.findViewById(R.id.settings_swap_game_button);
        swapSettingsButton.setSoundEffectsEnabled(false);
        swapSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d (TAG, "method onCreateView: swapSettingsButton: onClick");
                PopupManager.showSwapPopupSettings();
            }
        });
        swapStartButtonLights = (ImageView) view.findViewById(R.id.start_swap_game_button_lights);
        swapTooltip = (ImageView) view.findViewById(R.id.swap_menu_tooltip);
        swapStartGameButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d (TAG, "method onCreateView: swapStartGameButton: onClick");
                // animate match_menu_title from place and navigation buttons from place
                animateAllAssetsOff(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        Log.d (TAG, "method onCreateView: swapStartGameButton: onClick: animateAllAssetsOff: onAnimationEnd");
                        Shared.eventBus.notify(new SwapStartEvent());
                    }
                });
            }
        });

        startLightsAnimation();
        startTooltipAnimation();

        // play background music
        Audio.playBackgroundMusic();
        return view;
    }

    protected void animateAllAssetsOff(AnimatorListenerAdapter adapter) {
        // match_menu_title
        // 120dp + 50dp + buffer(30dp)
        ObjectAnimator titleAnimator = ObjectAnimator.ofFloat(swapTitle, "translationY", Utils.px(-200));  //TODO constant to variable
        titleAnimator.setInterpolator(new AccelerateInterpolator(2));
        titleAnimator.setDuration(300);

        // lights
        ObjectAnimator lightsAnimatorX = ObjectAnimator.ofFloat(swapStartButtonLights, "scaleX", 0f);
        ObjectAnimator lightsAnimatorY = ObjectAnimator.ofFloat(swapStartButtonLights, "scaleY", 0f);

        // tooltip
        ObjectAnimator tooltipAnimator = ObjectAnimator.ofFloat(swapTooltip, "alpha", 0f);
        tooltipAnimator.setDuration(100);

        // settings button
        ObjectAnimator settingsAnimator = ObjectAnimator.ofFloat(swapSettingsButton, "translationY", Utils.px(120));
        settingsAnimator.setInterpolator(new AccelerateInterpolator(2));
        settingsAnimator.setDuration(300);

        // start button
        ObjectAnimator startButtonAnimator = ObjectAnimator.ofFloat(swapStartGameButton, "translationY", Utils.px(130));
        startButtonAnimator.setInterpolator(new AccelerateInterpolator(2));
        startButtonAnimator.setDuration(300);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(titleAnimator, lightsAnimatorX, lightsAnimatorY, tooltipAnimator, settingsAnimator, startButtonAnimator);
        animatorSet.addListener(adapter);
        animatorSet.start();
    }

    private void startTooltipAnimation() {
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(swapTooltip, "scaleY", 0.8f);
        scaleY.setDuration(200);
        ObjectAnimator scaleYBack = ObjectAnimator.ofFloat(swapTooltip, "scaleY", 1f);
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
        swapTooltip.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        animatorSet.start();
    }

    private void startLightsAnimation() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(swapStartButtonLights, "rotation", 0f, 360f);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(6000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        swapStartButtonLights.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        animator.start();
    }
}
