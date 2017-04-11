package ws.isak.bridge.fragments;

import java.util.Locale;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.util.Log;

import ws.isak.bridge.R;
import ws.isak.bridge.common.Memory;
import ws.isak.bridge.common.Shared;
import ws.isak.bridge.events.ui.MatchThemeSelectedEvent;
import ws.isak.bridge.themes.MatchTheme;
import ws.isak.bridge.themes.MatchThemes;

/*
 * Class MatchThemeSelectFragment defines the view and behavior of the match matchTheme select fragment
 * including creating the view and defining behavior for button clicks.
 *
 * @author isak
 */

public class MatchThemeSelectFragment extends Fragment implements View.OnClickListener{

    public static final String TAG="ThemeSelectFrag";

    public static MatchTheme matchThemeBlank;
    public static MatchTheme matchThemeBirds;
    public static MatchTheme matchThemeSpectrograms;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d (TAG, "overriding method onCreateView");
        View view = LayoutInflater.from(Shared.context).inflate(R.layout.match_theme_select_fragment, container, false);

        View blank = view.findViewById(R.id.theme_blank_container);
        View birds = view.findViewById(R.id.theme_birds_container);
		View spectrograms = view.findViewById(R.id.theme_spectrograms_container);

        matchThemeBlank = MatchThemes.createBlankTheme();
        setStars((ImageView) blank.findViewById(R.id.theme_blank), matchThemeBlank, "blank");
		matchThemeBirds = MatchThemes.createBirdsTheme();
		setStars((ImageView) birds.findViewById(R.id.theme_birds), matchThemeBirds, "birds");
		matchThemeSpectrograms = MatchThemes.createSpectrogramsTheme();
		setStars((ImageView) spectrograms.findViewById(R.id.theme_spectrograms), matchThemeSpectrograms, "spectrograms");
        //set on click listeners
        blank.setOnClickListener(this);
        birds.setOnClickListener(this);
        spectrograms.setOnClickListener(this);
        //animate views
        animateShow(blank);
		animateShow(birds);
		animateShow(spectrograms);

		return view;
	}

	@Override
    public void onClick (View view) {
        switch (view.getId()) {
            case R.id.theme_blank_container:
                Shared.eventBus.notify(new MatchThemeSelectedEvent(matchThemeBlank));
                break;
            case R.id.theme_birds_container:
                Shared.eventBus.notify(new MatchThemeSelectedEvent(matchThemeBirds));
                break;
            case R.id.theme_spectrograms_container:
                Shared.eventBus.notify(new MatchThemeSelectedEvent(matchThemeSpectrograms));
                break;
        }
    }

	private void animateShow(View view) {
		ObjectAnimator animatorScaleX = ObjectAnimator.ofFloat(view, "scaleX", 0.5f, 1f);
		ObjectAnimator animatorScaleY = ObjectAnimator.ofFloat(view, "scaleY", 0.5f, 1f);
		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.setDuration(300);
		animatorSet.playTogether(animatorScaleX, animatorScaleY);
		animatorSet.setInterpolator(new DecelerateInterpolator(2));
		view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
		animatorSet.start();
	}


	//TODO Memory will be a function of UserData so need to store stars to each userData and
    //TODO ... extract them from there
	private void setStars(ImageView imageView, MatchTheme matchTheme, String type) {
		int sum = 0;
		for (int difficulty = 1; difficulty <= 6; difficulty++) {
			sum += Memory.getHighStars(matchTheme.themeID, difficulty);
		}
		int num = sum / 6;
		if (num != 0) {
			String drawableResourceName = String.format(Locale.US, type + "_theme_star_%d", num);
			int drawableResourceId = Shared.context.getResources().getIdentifier(drawableResourceName, "drawable", Shared.context.getPackageName());
			imageView.setImageResource(drawableResourceId);
		}
	}
}