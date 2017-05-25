package ws.isak.bridge.fragments;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.TextView;

import ws.isak.bridge.R;
import ws.isak.bridge.common.Shared;
import ws.isak.bridge.events.ui.MatchDifficultySelectedEvent;
import ws.isak.bridge.ui.MatchDifficultyView;
import ws.isak.bridge.utils.FontLoader;

/*
 *
 *
 * @author isak
 */

public class MatchDifficultySelectFragment extends Fragment {

    public static final String TAG="MatchDiffSelectFrag";

    TextView matchDifficultyTitle;            //TODO make sure this inflates and set font
    MatchDifficultyView matchDifficulty1;
    MatchDifficultyView matchDifficulty2;
    MatchDifficultyView matchDifficulty3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d (TAG, "method onCreateView");
        View view = LayoutInflater.from(Shared.context).inflate(R.layout.match_difficulty_select_fragment, container, false);
        //add the title to the fragment
        matchDifficultyTitle = (TextView) view.findViewById(R.id.match_select_difficulty_title);
        FontLoader.setTypeface(Shared.context, new TextView[] { matchDifficultyTitle }, FontLoader.Font.ANGRYBIRDS);
        Log.d (TAG, "method onCreateView: matchDifficultyTitle set");

        //add the first MatchDifficultyView - Level Easy
        matchDifficulty1 = (MatchDifficultyView) view.findViewById(R.id.select_match_difficulty_1);
        matchDifficulty1.setMatchDifficultyLevelTitle(1);                           //set title
        matchDifficulty1.setmMatchDifficultyLevelImage(1);                          //set image
        if (Shared.currentMatchTheme.themeID == 1) {
            matchDifficulty1.setMatchDifficultyStars(1, Shared.userData.getMatchTheme1Difficulty1HighStars());
        }
        if (Shared.currentMatchTheme.themeID == 2) {
            matchDifficulty1.setMatchDifficultyStars(1, Shared.userData.getMatchTheme2Difficulty1HighStars());
        }
        if (Shared.currentMatchTheme.themeID == 3) {
            matchDifficulty1.setMatchDifficultyStars(1, Shared.userData.getMatchTheme3Difficulty1HighStars());
        }
        setOnClick(matchDifficulty1, 1);
        Log.d (TAG, "method onCreateView: matchDifficulty1 set");

        //add the second MatchDifficultyView - Level Medium
        matchDifficulty2 = (MatchDifficultyView) view.findViewById(R.id.select_match_difficulty_2);
        matchDifficulty2.setMatchDifficultyLevelTitle(2);                           //set title
        matchDifficulty2.setmMatchDifficultyLevelImage(2);                          //set image
        if (Shared.currentMatchTheme.themeID == 1) {
            matchDifficulty2.setMatchDifficultyStars(1, Shared.userData.getMatchTheme1Difficulty2HighStars());
        }
        if (Shared.currentMatchTheme.themeID == 2) {
            matchDifficulty2.setMatchDifficultyStars(1, Shared.userData.getMatchTheme2Difficulty2HighStars());
        }
        if (Shared.currentMatchTheme.themeID == 3) {
            matchDifficulty2.setMatchDifficultyStars(1, Shared.userData.getMatchTheme3Difficulty2HighStars());
        }
        setOnClick(matchDifficulty2, 2);
        Log.d (TAG, "method onCreateView: matchDifficulty2 set");

        //add the third MatchDifficultyView - Level Hard
        matchDifficulty3 = (MatchDifficultyView) view.findViewById(R.id.select_match_difficulty_3);
        matchDifficulty3.setMatchDifficultyLevelTitle(3);                           //set title
        matchDifficulty3.setmMatchDifficultyLevelImage(3);                          //set image
        if (Shared.currentMatchTheme.themeID == 1) {
            matchDifficulty3.setMatchDifficultyStars(1, Shared.userData.getMatchTheme1Difficulty3HighStars());
        }
        if (Shared.currentMatchTheme.themeID == 2) {
            matchDifficulty3.setMatchDifficultyStars(1, Shared.userData.getMatchTheme2Difficulty3HighStars());
        }
        if (Shared.currentMatchTheme.themeID == 3) {
            matchDifficulty3.setMatchDifficultyStars(1, Shared.userData.getMatchTheme3Difficulty3HighStars());
        }
        setOnClick(matchDifficulty3, 3);
        Log.d (TAG, "method onCreateView: matchDifficulty3 set");

        animate(matchDifficulty1, matchDifficulty2, matchDifficulty3);

        return view;
    }

    private void animate(View... view) {
        AnimatorSet animatorSet = new AnimatorSet();
        AnimatorSet.Builder builder = animatorSet.play(new AnimatorSet());
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
