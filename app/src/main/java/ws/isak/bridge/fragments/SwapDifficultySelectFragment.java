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
import ws.isak.bridge.events.ui.SwapDifficultySelectedEvent;
import ws.isak.bridge.ui.SwapDifficultyView;
import ws.isak.bridge.utils.FontLoader;

/*
 *
 *
 * @author isak
 */

public class SwapDifficultySelectFragment extends Fragment {

    public static final String TAG="SwapDiffSelectFrag";

    TextView swapDifficultyTitle;            //TODO make sure this inflates and set font
    SwapDifficultyView swapDifficulty1;
    SwapDifficultyView swapDifficulty2;
    SwapDifficultyView swapDifficulty3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d (TAG, "method onCreateView");
        View view = LayoutInflater.from(Shared.context).inflate(R.layout.swap_difficulty_select_fragment, container, false);
        //add the title to the fragment
        swapDifficultyTitle = (TextView) view.findViewById(R.id.swap_select_difficulty_title);
        FontLoader.setTypeface(Shared.context, new TextView[] { swapDifficultyTitle }, FontLoader.Font.ANGRYBIRDS);
        Log.d (TAG, "method onCreateView: swapDifficultyTitle set");

        //add the first SwapDifficultyView - Level Easy
        swapDifficulty1 = (SwapDifficultyView) view.findViewById(R.id.select_swap_difficulty_1);
        swapDifficulty1.setSwapDifficultyLevelTitle(1);                           //set title
        swapDifficulty1.setmSwapDifficultyLevelImage(1);                          //set image
        swapDifficulty1.setSwapDifficultyStars(1, Shared.userData.getSwapHighStarsDifficulty1()); //set achieved stars - FIXME - this has no meaning for this game
        setOnClick(swapDifficulty1, 1);
        Log.d (TAG, "method onCreateView: swapDifficulty1 set");

        //add the second SwapDifficultyView - Level Medium
        swapDifficulty2 = (SwapDifficultyView) view.findViewById(R.id.select_swap_difficulty_2);
        swapDifficulty2.setSwapDifficultyLevelTitle(2);                           //set title
        swapDifficulty2.setmSwapDifficultyLevelImage(2);                          //set image
        swapDifficulty2.setSwapDifficultyStars(2, Shared.userData.getSwapHighStarsDifficulty2()); //set achieved stars - FIXME - this has no meaning for this game
        setOnClick(swapDifficulty2, 2);
        Log.d (TAG, "method onCreateView: swapDifficulty2 set");

        //add the third SwapDifficultyView - Level Hard
        swapDifficulty3 = (SwapDifficultyView) view.findViewById(R.id.select_swap_difficulty_3);
        swapDifficulty3.setSwapDifficultyLevelTitle(3);                           //set title
        swapDifficulty3.setmSwapDifficultyLevelImage(3);                          //set image
        swapDifficulty3.setSwapDifficultyStars(3, Shared.userData.getSwapHighStarsDifficulty3()); //set achieved stars - FIXME - this has no meaning for this game
        setOnClick(swapDifficulty3, 3);
        Log.d (TAG, "method onCreateView: swapDifficulty3 set");

        animate(swapDifficulty1, swapDifficulty2, swapDifficulty3);

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
                Shared.eventBus.notify(new SwapDifficultySelectedEvent(difficulty));
            }
        });
    }
}
