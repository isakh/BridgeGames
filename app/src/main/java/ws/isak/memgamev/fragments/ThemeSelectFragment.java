package ws.isak.memgamev.fragments;

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

import ws.isak.memgamev.R;
import ws.isak.memgamev.common.Memory;
import ws.isak.memgamev.common.Shared;
import ws.isak.memgamev.events.ui.ThemeSelectedEvent;
import ws.isak.memgamev.themes.Theme;
import ws.isak.memgamev.themes.Themes;

public class ThemeSelectFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = LayoutInflater.from(Shared.context).inflate(R.layout.theme_select_fragment, container, false);
		View birds = view.findViewById(R.id.theme_birds_container);
		View spectrograms = view.findViewById(R.id.theme_spectrograms_container);

		final Theme themeBirds = Themes.createBirdsTheme();
		setStars((ImageView) birds.findViewById(R.id.theme_birds), themeBirds, "birds");
		final Theme themeSpectrograms = Themes.createSpectrogramsTheme();
		setStars((ImageView) spectrograms.findViewById(R.id.theme_spectrograms), themeSpectrograms, "spectrograms");

		birds.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Shared.eventBus.notify(new ThemeSelectedEvent(themeBirds));
			}
		});

		spectrograms.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Shared.eventBus.notify(new ThemeSelectedEvent(themeSpectrograms));
			}
		});

		animateShow(birds);
		animateShow(spectrograms);

		return view;
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

	private void setStars(ImageView imageView, Theme theme, String type) {
		int sum = 0;
		for (int difficulty = 1; difficulty <= 6; difficulty++) {
			sum += Memory.getHighStars(theme.themeID, difficulty);
		}
		int num = sum / 6;
		if (num != 0) {
			String drawableResourceName = String.format(Locale.US, type + "_theme_star_%d", num);
			int drawableResourceId = Shared.context.getResources().getIdentifier(drawableResourceName, "drawable", Shared.context.getPackageName());
			imageView.setImageResource(drawableResourceId);
		}
	}
}
