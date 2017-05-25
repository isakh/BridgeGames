package ws.isak.bridge.fragments;

import java.util.Locale;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.util.Log;
import android.widget.TextView;

import ws.isak.bridge.R;
import ws.isak.bridge.common.Memory;
import ws.isak.bridge.common.Shared;
import ws.isak.bridge.events.ui.MatchThemeSelectedEvent;
import ws.isak.bridge.events.ui.SwapDifficultySelectedEvent;
import ws.isak.bridge.themes.MatchTheme;
import ws.isak.bridge.themes.MatchThemes;
import ws.isak.bridge.ui.MatchThemeView;
import ws.isak.bridge.ui.SwapDifficultyView;
import ws.isak.bridge.utils.FontLoader;

/*
 * Class MatchThemeSelectFragment defines the view and behavior of the match matchTheme select fragment
 * including creating the view and defining behavior for button clicks.
 *
 * @author isak
 */

public class MatchThemeSelectFragment extends Fragment {

    public static final String TAG = "ThemeSelectFrag";

    TextView matchThemeTitle;            //TODO make sure this inflates and set font
    MatchThemeView birdsThemeView;
    MatchThemeView spectroThemeView;
    MatchThemeView blankThemeView;

    public static MatchTheme matchThemeBlank;
    public static MatchTheme matchThemeBirds;
    public static MatchTheme matchThemeSpectrograms;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d(TAG, "method onCreateView");
        View view = LayoutInflater.from(Shared.context).inflate(R.layout.match_theme_select_fragment, container, false);
        //add the title to the fragment
        matchThemeTitle = (TextView) view.findViewById(R.id.match_theme_select_title);
        FontLoader.setTypeface(Shared.context, new TextView[]{matchThemeTitle}, FontLoader.Font.ANGRYBIRDS);

        //add the first Theme: BIRDS
        matchThemeBirds = MatchThemes.createBirdsTheme();
        birdsThemeView = (MatchThemeView) view.findViewById(R.id.select_match_theme_1);
        birdsThemeView.setMatchThemeTitle(1);                          //set title
        birdsThemeView.setMatchThemeImage(1);                          //set image
        int theme1Stars = 0;
        if (Shared.userData.getMatchTheme1Difficulty1HighStars() == 3) { theme1Stars++; }
        if (Shared.userData.getMatchTheme1Difficulty2HighStars() == 3) { theme1Stars++; }
        if (Shared.userData.getMatchTheme1Difficulty3HighStars() == 3) { theme1Stars++; }
        birdsThemeView.setMatchThemeStars(1, theme1Stars);             //set achieved stars
        setOnClick(birdsThemeView, matchThemeBirds);
        Log.d(TAG, "method onCreateView: swapDifficulty1 set");

        //add the second Theme: SPECTROGRAMS
        matchThemeSpectrograms = MatchThemes.createSpectrogramsTheme();
        spectroThemeView = (MatchThemeView) view.findViewById(R.id.select_match_theme_2);
        spectroThemeView.setMatchThemeTitle(2);                           //set title
        spectroThemeView.setMatchThemeImage(2);                           //set image
        int theme2Stars = 0;
        if (Shared.userData.getMatchTheme2Difficulty1HighStars() == 3) { theme2Stars++; }
        if (Shared.userData.getMatchTheme2Difficulty2HighStars() == 3) { theme2Stars++; }
        if (Shared.userData.getMatchTheme2Difficulty3HighStars() == 3) { theme2Stars++; }
        spectroThemeView.setMatchThemeStars(2, theme2Stars);              //set achieved stars
        setOnClick(spectroThemeView, matchThemeSpectrograms);
        Log.d(TAG, "method onCreateView: swapDifficulty2 set");

        //add the third Theme: BLANK
        matchThemeBlank = MatchThemes.createBlankTheme();
        blankThemeView = (MatchThemeView) view.findViewById(R.id.select_match_theme_3);
        blankThemeView.setMatchThemeTitle(3);                           //set title
        blankThemeView.setMatchThemeImage(3);                           //set image
        int theme3Stars = 0;
        if (Shared.userData.getMatchTheme3Difficulty1HighStars() == 3) { theme3Stars++; }
        if (Shared.userData.getMatchTheme3Difficulty2HighStars() == 3) { theme3Stars++; }
        if (Shared.userData.getMatchTheme3Difficulty3HighStars() == 3) { theme3Stars++; }
        blankThemeView.setMatchThemeStars(3, theme3Stars);              //set achieved stars
        setOnClick(blankThemeView, matchThemeBlank);
        Log.d(TAG, "method onCreateView: swapDifficulty3 set");

        animate(birdsThemeView, spectroThemeView, blankThemeView);

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

    private void setOnClick (View view, final MatchTheme theme) {
        Log.d (TAG, "method setOnClick: called with themeID: " + theme.themeID);
        Shared.currentMatchTheme = theme;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                switch (theme.themeID) {
                    case 1:
                        Log.d (TAG, "method setOnClick: new birds theme");
                        Shared.eventBus.notify(new MatchThemeSelectedEvent(matchThemeBirds));
                        break;
                    case 2:
                        Log.d (TAG, "method setOnClick: new spectro theme");
                        Shared.eventBus.notify(new MatchThemeSelectedEvent(matchThemeSpectrograms));
                        break;
                    case 3:
                        Log.d (TAG, "method setOnClick: new blank theme");
                        Shared.eventBus.notify(new MatchThemeSelectedEvent(matchThemeBlank));
                        break;
                }
            }
        });
    }
}