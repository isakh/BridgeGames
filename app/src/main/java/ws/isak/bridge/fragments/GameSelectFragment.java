package ws.isak.bridge.fragments;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import ws.isak.bridge.R;
import ws.isak.bridge.common.Shared;
import ws.isak.bridge.engine.ScreenController;
import ws.isak.bridge.engine.ScreenController.Screen;

/*
 * Class GameSelectFragment provides the view for the fragment where the user decides which of
 * the games to play.  This view contains clickable buttons for each of the games with onClick
 * listeners that animate the click action and open the appropriate subsequent screen.
 *
 *  @author isak
 */

public class GameSelectFragment extends Fragment implements  View.OnClickListener{

    public static final String TAG="GameSelectFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "overriding method onCreateView");
        View view = LayoutInflater.from(Shared.context).inflate(R.layout.game_select_fragment, container, false);

        //creating views for matchGameLaunch and swapGameLaunch
        Log.d (TAG, "       : creating views for matchGameLaunch and swapGameLaunch");
        View matchGameLaunch = view.findViewById(R.id.game_match_container);
        View swapGameLaunch = view.findViewById(R.id.game_swap_container);

        //setting click listeners for game launch views
        Log.d (TAG, "setting click listeners for game launch views");
        matchGameLaunch.setOnClickListener(this);
        swapGameLaunch.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick (View view) {
        Log.d (TAG, "overriding method onClick: game launch buttons animate and move to appropriate subsequent screen");
        switch (view.getId()) {
            case R.id.game_match_container:
                Log.d(TAG, "overridden method onClick: case game_match: switch to MENU_MATCH screen");
                animateShow(view);
                ScreenController.getInstance().openScreen(Screen.MENU_MATCH);
                break;
            case R.id.game_swap_container:
                Log.d(TAG, "overridden method onClick: case game_swap: switch to MENU_SWAP screen");
                animateShow(view);
                ScreenController.getInstance().openScreen(Screen.MENU_SWAP);
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
}