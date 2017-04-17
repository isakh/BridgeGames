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
import ws.isak.bridge.events.ui.SwapDifficultySelectedEvent;
import ws.isak.bridge.ui.SwapDifficultyView;

/*
 * Class SwapDifficultySelectFragment creates the view for selecting the difficultyLevel for the swapping game.
 * See SwapDifficultyView for more.
 *
 * @author isak
 */

public class SwapDifficultySelectFragment extends Fragment {

    public static final String TAG="DifficultySelect";


    //FIXME - make constant values for difficultyLevel xml references?
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(Shared.context).inflate(R.layout.swap_difficulty_select_fragment, container, false);

        SwapDifficultyView swapDifficulty1 = (SwapDifficultyView) view.findViewById(R.id.select_swap_difficulty_1);
        swapDifficulty1.setSwapDifficulty(1, Memory.getSwapHighStars(1));
        setOnClick(swapDifficulty1, 1);

        SwapDifficultyView swapDifficulty2 = (SwapDifficultyView) view.findViewById(R.id.select_swap_difficulty_2);
        swapDifficulty2.setSwapDifficulty(2, Memory.getSwapHighStars(2));
        setOnClick(swapDifficulty2, 2);

        SwapDifficultyView swapDifficulty3 = (SwapDifficultyView) view.findViewById(R.id.select_swap_difficulty_3);
        swapDifficulty3.setSwapDifficulty(3, Memory.getSwapHighStars(3));
        setOnClick(swapDifficulty3, 3);

        animate(swapDifficulty1, swapDifficulty2, swapDifficulty3);

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
                Log.d (TAG, "method setOnClick , calling new SwapDifficultySelectedEvent");
                Shared.eventBus.notify(new SwapDifficultySelectedEvent(difficulty));
            }
        });
    }
}
