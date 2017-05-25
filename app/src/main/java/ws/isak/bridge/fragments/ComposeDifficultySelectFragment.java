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
import ws.isak.bridge.common.Memory;
import ws.isak.bridge.common.Shared;
import ws.isak.bridge.events.ui.ComposeDifficultySelectEvent;
import ws.isak.bridge.ui.ComposeDifficultyView;
import ws.isak.bridge.utils.FontLoader;

/*
 *
 *
 * @author isak
 */

public class ComposeDifficultySelectFragment extends Fragment {

    public static final String TAG="ComposeDiffSelectFrag";

    TextView composeDifficultyTitle;            //TODO make sure this inflates and set font
    ComposeDifficultyView composeDifficulty1;
    ComposeDifficultyView composeDifficulty2;
    ComposeDifficultyView composeDifficulty3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d (TAG, "method onCreateView");
        View view = LayoutInflater.from(Shared.context).inflate(R.layout.compose_difficulty_select_fragment, container, false);
        //add the title to the fragment
        composeDifficultyTitle = (TextView) view.findViewById(R.id.compose_select_difficulty_title);
        FontLoader.setTypeface(Shared.context, new TextView[] { composeDifficultyTitle }, FontLoader.Font.ANGRYBIRDS);
        Log.d (TAG, "method onCreateView: composeDifficultyTitle set");

        //add the first ComposeDifficultyView - Level Easy
        composeDifficulty1 = (ComposeDifficultyView) view.findViewById(R.id.select_compose_difficulty_1);
        composeDifficulty1.setComposeDifficultyLevelTitle(1);                           //set title
        composeDifficulty1.setmComposeDifficultyLevelImage(1);                          //set image
        Log.d (TAG, "method onCreateView: calling Memory.getComposeHighStars(1): " + Memory.getComposeHighStars(1));
        composeDifficulty1.setComposeDifficultyStars(1, Shared.userData.getComposeHighStarsDifficulty1()); //set achieved stars - FIXME - this has no meaning for this game
        setOnClick(composeDifficulty1, 1);
        Log.d (TAG, "method onCreateView: composeDifficulty1 set");

        //add the second ComposeDifficultyView - Level Medium
        composeDifficulty2 = (ComposeDifficultyView) view.findViewById(R.id.select_compose_difficulty_2);
        composeDifficulty2.setComposeDifficultyLevelTitle(2);                           //set title
        composeDifficulty2.setmComposeDifficultyLevelImage(2);                          //set image
        Log.d (TAG, "method onCreateView: calling Memory.getComposeHighStars(1): " + Memory.getComposeHighStars(2));
        composeDifficulty2.setComposeDifficultyStars(2, Shared.userData.getComposeHighStarsDifficulty2()); //set achieved stars - FIXME - this has no meaning for this game
        setOnClick(composeDifficulty2, 2);
        Log.d (TAG, "method onCreateView: composeDifficulty2 set");

        //add the third ComposeDifficultyView - Level Hard
        composeDifficulty3 = (ComposeDifficultyView) view.findViewById(R.id.select_compose_difficulty_3);
        composeDifficulty3.setComposeDifficultyLevelTitle(3);                           //set title
        composeDifficulty3.setmComposeDifficultyLevelImage(3);                          //set image
        Log.d (TAG, "method onCreateView: calling Memory.getComposeHighStars(1): " + Memory.getComposeHighStars(3));
        composeDifficulty3.setComposeDifficultyStars(3, Shared.userData.getComposeHighStarsDifficulty3()); //set achieved stars - FIXME - this has no meaning for this game
        setOnClick(composeDifficulty3, 3);
        Log.d (TAG, "method onCreateView: composeDifficulty3 set");

        animate(composeDifficulty1, composeDifficulty2, composeDifficulty3);

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
                Log.d (TAG, "method setOnClick , calling new SwapDifficultySelectedEvent");
                Shared.eventBus.notify(new ComposeDifficultySelectEvent(difficulty));
            }
        });
    }
}
