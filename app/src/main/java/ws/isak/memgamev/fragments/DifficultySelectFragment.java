package ws.isak.memgamev.fragments;

import android.animation.AnimatorSet;
import android.animation.AnimatorSet.Builder;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.util.Log;

import ws.isak.memgamev.R;
import ws.isak.memgamev.common.Memory;
import ws.isak.memgamev.common.Shared;
import ws.isak.memgamev.events.ui.DifficultySelectedEvent;
import ws.isak.memgamev.themes.Theme;
import ws.isak.memgamev.ui.DifficultyView;

public class DifficultySelectFragment extends Fragment {

    public static final String TAG="Class: DifficultySelect";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = LayoutInflater.from(Shared.context).inflate(R.layout.difficulty_select_fragment, container, false);
		Theme theme = Shared.engine.getSelectedTheme();

		DifficultyView difficulty1 = (DifficultyView) view.findViewById(R.id.select_difficulty_1);
		difficulty1.setDifficulty(1, Memory.getHighStars(theme.themeID, 1));
		setOnClick(difficulty1, 1);

		DifficultyView difficulty2 = (DifficultyView) view.findViewById(R.id.select_difficulty_2);
		difficulty2.setDifficulty(2, Memory.getHighStars(theme.themeID, 2));
		setOnClick(difficulty2, 2);

		DifficultyView difficulty3 = (DifficultyView) view.findViewById(R.id.select_difficulty_3);
		difficulty3.setDifficulty(3, Memory.getHighStars(theme.themeID, 3));
		setOnClick(difficulty3, 3);

		animate(difficulty1, difficulty2, difficulty3);

		return view;
	}

	private void animate(View... view) {
		AnimatorSet animatorSet = new AnimatorSet();
		Builder builder = animatorSet.play(new AnimatorSet());
		for (int i = 0; i < view.length; i++) {
			ObjectAnimator scaleX = ObjectAnimator.ofFloat(view[i], "scaleX", 0.8f, 1f);
			ObjectAnimator scaleY = ObjectAnimator.ofFloat(view[i], "scaleY", 0.8f, 1f);
			builder.with(scaleX).with(scaleY);
		}
		animatorSet.setDuration(500);
		animatorSet.setInterpolator(new BounceInterpolator());
		animatorSet.start();
	}

	private void setOnClick(View view, final int difficulty) {
		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                Log.d (TAG, "method setOnClick , calling new DifficultySelectedEvent");
				Shared.eventBus.notify(new DifficultySelectedEvent(difficulty));
			}
		});
	}
}
