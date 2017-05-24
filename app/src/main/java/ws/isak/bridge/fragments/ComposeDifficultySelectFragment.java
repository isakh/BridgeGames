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

    public static final String TAG="ComposeDifficultySelect";

    TextView composeDifficultyTitle;            //TODO make sure this inflates and set font
    ComposeDifficultyView composeDifficulty1;
    ComposeDifficultyView composeDifficulty2;
    ComposeDifficultyView composeDifficulty3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(Shared.context).inflate(R.layout.compose_difficulty_select_fragment, container, false);

        composeDifficultyTitle = (TextView) view.findViewById(R.id.compose_select_difficulty_title);

        FontLoader.setTypeface(Shared.context, new TextView[] { composeDifficultyTitle }, FontLoader.Font.ANGRYBIRDS);

        ComposeDifficultyView composeDifficulty1 = (ComposeDifficultyView) view.findViewById(R.id.select_compose_difficulty_1);
        composeDifficulty1.setComposeDifficulty(Memory.getComposeHighStars(1));
        setOnClick(composeDifficulty1, 1);

        ComposeDifficultyView composeDifficulty2 = (ComposeDifficultyView) view.findViewById(R.id.select_compose_difficulty_2);
        composeDifficulty2.setComposeDifficulty(Memory.getComposeHighStars(2));
        setOnClick(composeDifficulty2, 2);

        ComposeDifficultyView composeDifficulty3 = (ComposeDifficultyView) view.findViewById(R.id.select_compose_difficulty_3);
        composeDifficulty3.setComposeDifficulty(Memory.getComposeHighStars(3));
        setOnClick(composeDifficulty3, 3);

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
