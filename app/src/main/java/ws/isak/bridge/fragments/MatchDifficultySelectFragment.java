package ws.isak.bridge.fragments;

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

import ws.isak.bridge.R;
import ws.isak.bridge.common.Memory;
import ws.isak.bridge.common.Shared;
import ws.isak.bridge.events.ui.MatchDifficultySelectedEvent;
import ws.isak.bridge.themes.MatchTheme;
import ws.isak.bridge.ui.MatchDifficultyView;

/*
 * Class MatchDifficultySelectFragment creates the view for selecting the difficultyLevel for the matching game.
 * See MatchDifficultyView for more.
 *
 * @author isak
 */

public class MatchDifficultySelectFragment extends Fragment {

    public static final String TAG="DifficultySelect";
    MatchDifficultyView matchDifficulty1;
    MatchDifficultyView matchDifficulty2;
    MatchDifficultyView matchDifficulty3;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = LayoutInflater.from(Shared.context).inflate(R.layout.match_difficulty_select_fragment, container, false);
		MatchTheme matchTheme = Shared.engine.getSelectedTheme();

        matchDifficulty1 = (MatchDifficultyView) view.findViewById(R.id.select_match_difficulty_1);
		matchDifficulty1.setMatchDifficulty(1, Memory.getMatchHighStars(matchTheme.themeID, 1));
		setOnClick(matchDifficulty1, 1);

        matchDifficulty2 = (MatchDifficultyView) view.findViewById(R.id.select_match_difficulty_2);
		matchDifficulty2.setMatchDifficulty(2, Memory.getMatchHighStars(matchTheme.themeID, 2));
		setOnClick(matchDifficulty2, 2);

        matchDifficulty3 = (MatchDifficultyView) view.findViewById(R.id.select_match_difficulty_3);
		matchDifficulty3.setMatchDifficulty(3, Memory.getMatchHighStars(matchTheme.themeID, 3));
		setOnClick(matchDifficulty3, 3);

		animate(matchDifficulty1, matchDifficulty2, matchDifficulty3);

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
		animatorSet.setDuration(500);               //TODO make this constant a variable?
		animatorSet.setInterpolator(new BounceInterpolator());
		animatorSet.start();
	}

	private void setOnClick(View view, final int difficulty) {
		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                Log.d (TAG, "method setOnClick , calling new MatchDifficultySelectedEvent");
				Shared.eventBus.notify(new MatchDifficultySelectedEvent(difficulty));
			}
		});
	}
}
