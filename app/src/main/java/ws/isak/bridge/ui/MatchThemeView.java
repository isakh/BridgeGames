package ws.isak.bridge.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ws.isak.bridge.R;
import ws.isak.bridge.common.Shared;
import ws.isak.bridge.utils.FontLoader;

/*
 *
 *
 * @author isak
 */

public class MatchThemeView extends LinearLayout {

    public static final String TAG = "MatchThemeView";

    private TextView mMatchThemeTitle;
    private TextView difficultyEasyTitle;
    private TextView difficultyMediumTitle;
    private TextView difficultyHardTitle;

    private ImageView mMatchThemeImage;
    private ImageView mMatchThemeStar1;
    private ImageView mMatchThemeStar2;
    private ImageView mMatchThemeStar3;

    // Constructor DifficultyView sets context
    public MatchThemeView(Context context) {
        this(context, null);
        Log.d (TAG, "constructor");
    }

    //Overloaded constructor sets context and attributes
    public MatchThemeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d (TAG, "overloaded constructor, includes AttributeSet");
        LayoutInflater.from(context).inflate(R.layout.match_theme_view, this, true);
        setOrientation(LinearLayout.VERTICAL);

        mMatchThemeTitle = (TextView) findViewById(R.id.match_theme_title);

        FontLoader.setTypeface(Shared.context, new TextView[] {
                mMatchThemeTitle , difficultyEasyTitle, difficultyMediumTitle, difficultyHardTitle
        }, FontLoader.Font.ANGRYBIRDS);
    }

    //Method setMatchThemeTitle sets the title for the view
    public void setMatchThemeTitle (int theme) {
        switch (theme) {
            case 1:
                difficultyEasyTitle = (TextView) findViewById(R.id.match_theme_title);
                difficultyEasyTitle.setText(R.string.match_themes_birds_name);
                difficultyEasyTitle.setTextSize(Shared.context.getResources().getDimension(R.dimen.match_theme_select_level_title_size));
                difficultyEasyTitle.setGravity(Gravity.CENTER);
                break;
            case 2:
                difficultyMediumTitle = (TextView) findViewById(R.id.match_theme_title);
                difficultyMediumTitle.setText(R.string.match_themes_spectrograms_name);
                difficultyMediumTitle.setTextSize(Shared.context.getResources().getDimension(R.dimen.match_theme_select_level_title_size));
                difficultyMediumTitle.setGravity(Gravity.CENTER);
                break;
            case 3:
                difficultyHardTitle = (TextView) findViewById(R.id.match_theme_title);
                difficultyHardTitle.setText(R.string.match_themes_blank_name);
                difficultyHardTitle.setTextSize(Shared.context.getResources().getDimension(R.dimen.match_theme_select_level_title_size));
                difficultyHardTitle.setGravity(Gravity.CENTER);
                break;
        }
    }

    //Method setMatchThemeImage sets the image for the view
    public void setMatchThemeImage (int theme) {
        switch (theme) {
            case 1:
                mMatchThemeImage = (ImageView) findViewById(R.id.match_theme_image);
                mMatchThemeImage.setBackgroundResource(R.drawable.theme_birds_200px);
                break;
            case 2:
                mMatchThemeImage = (ImageView) findViewById(R.id.match_theme_image);
                mMatchThemeImage.setBackgroundResource(R.drawable.theme_spectro_200px);
                break;
            case 3:
                mMatchThemeImage = (ImageView) findViewById(R.id.match_theme_image);
                mMatchThemeImage.setBackgroundResource(R.drawable.theme_blank_200px);
                break;
        }
    }

    //Method setMatchTheme sets the highest achieved stars for each theme as stored in memory.  Since
    //each theme offers three difficulty levels, a star for a theme results when a given difficulty
    //has be solved to three stars
    public void setMatchThemeStars(int theme, int stars) {
        Log.d(TAG, "method setMatchTheme: theme: " + theme + " | stars: " + stars);
        LinearLayout starsLayout = (LinearLayout) findViewById(R.id.match_theme_stars);
        starsLayout.setOrientation(LinearLayout.HORIZONTAL);
        mMatchThemeStar1 = (ImageView) findViewById(R.id.match_theme_star_1);
        mMatchThemeStar2 = (ImageView) findViewById(R.id.match_theme_star_2);
        mMatchThemeStar3 = (ImageView) findViewById(R.id.match_theme_star_3);
        switch (theme) {
            case 1:
                switch (stars) {
                    case 0:
                        Log.d (TAG, "setMatchThemeStars: case theme: 1 | stars: 0");
                        mMatchThemeStar1.setImageResource(R.drawable.circle_80px);
                        mMatchThemeStar2.setImageResource(R.drawable.circle_80px);
                        mMatchThemeStar3.setImageResource(R.drawable.circle_80px);
                        break;
                    case 1:
                        Log.d (TAG, "setMatchThemeStars: case theme: 1 | stars: 1");
                        mMatchThemeStar1.setImageResource(R.drawable.circle_with_star_80px);
                        mMatchThemeStar2.setImageResource(R.drawable.circle_80px);
                        mMatchThemeStar3.setImageResource(R.drawable.circle_80px);
                        break;
                    case 2:
                        Log.d (TAG, "setMatchThemeStars: case theme: 1 | stars: 2");
                        mMatchThemeStar1.setImageResource(R.drawable.circle_with_star_80px);
                        mMatchThemeStar2.setImageResource(R.drawable.circle_with_star_80px);
                        mMatchThemeStar3.setImageResource(R.drawable.circle_80px);
                        break;
                    case 3:
                        Log.d (TAG, "setMatchThemeStars: case theme: 1 | stars: 3");
                        mMatchThemeStar1.setImageResource(R.drawable.circle_with_star_80px);
                        mMatchThemeStar2.setImageResource(R.drawable.circle_with_star_80px);
                        mMatchThemeStar3.setImageResource(R.drawable.circle_with_star_80px);
                        break;
                }
                break;
            case 2:
                switch (stars) {
                    case 0:
                        Log.d (TAG, "setMatchThemeStars: case theme: 2 | stars: 0");
                        mMatchThemeStar1.setImageResource(R.drawable.circle_80px);
                        mMatchThemeStar2.setImageResource(R.drawable.circle_80px);
                        mMatchThemeStar3.setImageResource(R.drawable.circle_80px);
                        break;
                    case 1:
                        Log.d (TAG, "setMatchThemeStars: case theme: 2 | stars: 1");
                        mMatchThemeStar1.setImageResource(R.drawable.circle_with_star_80px);
                        mMatchThemeStar2.setImageResource(R.drawable.circle_80px);
                        mMatchThemeStar3.setImageResource(R.drawable.circle_80px);
                        break;
                    case 2:
                        Log.d (TAG, "setMatchThemeStars: case theme: 2 | stars: 2");
                        mMatchThemeStar1.setImageResource(R.drawable.circle_with_star_80px);
                        mMatchThemeStar2.setImageResource(R.drawable.circle_with_star_80px);
                        mMatchThemeStar3.setImageResource(R.drawable.circle_80px);
                        break;
                    case 3:
                        Log.d (TAG, "setMatchThemeStars: case theme: 2 | stars: 3");
                        mMatchThemeStar1.setImageResource(R.drawable.circle_with_star_80px);
                        mMatchThemeStar2.setImageResource(R.drawable.circle_with_star_80px);
                        mMatchThemeStar3.setImageResource(R.drawable.circle_with_star_80px);
                        break;
                }
                break;
            case 3:
                switch (stars) {
                    case 0:
                        Log.d (TAG, "setMatchThemeStars: case theme: 3 | stars: 0");
                        mMatchThemeStar1.setImageResource(R.drawable.circle_80px);
                        mMatchThemeStar2.setImageResource(R.drawable.circle_80px);
                        mMatchThemeStar3.setImageResource(R.drawable.circle_80px);
                        break;
                    case 1:
                        Log.d (TAG, "setMatchThemeStars: case theme: 3 | stars: 1");
                        mMatchThemeStar1.setImageResource(R.drawable.circle_with_star_80px);
                        mMatchThemeStar2.setImageResource(R.drawable.circle_80px);
                        mMatchThemeStar3.setImageResource(R.drawable.circle_80px);
                        break;
                    case 2:
                        Log.d (TAG, "setMatchThemeStars: case theme: 3 | stars: 2");
                        mMatchThemeStar1.setImageResource(R.drawable.circle_with_star_80px);
                        mMatchThemeStar2.setImageResource(R.drawable.circle_with_star_80px);
                        mMatchThemeStar3.setImageResource(R.drawable.circle_80px);
                        break;
                    case 3:
                        Log.d (TAG, "setMatchThemeStars: case theme: 3 | stars: 3");
                        mMatchThemeStar1.setImageResource(R.drawable.circle_with_star_80px);
                        mMatchThemeStar2.setImageResource(R.drawable.circle_with_star_80px);
                        mMatchThemeStar3.setImageResource(R.drawable.circle_with_star_80px);
                        break;
                }
                break;
        }
    }
}