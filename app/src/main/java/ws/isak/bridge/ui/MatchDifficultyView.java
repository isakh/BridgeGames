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

public class MatchDifficultyView extends LinearLayout {

    public static final String TAG = "MatchDifficultyView";

    private TextView mMatchDifficultyTitle;
    private TextView difficultyEasyTitle;
    private TextView difficultyMediumTitle;
    private TextView difficultyHardTitle;

    private ImageView mMatchDifficultyImage;
    private ImageView mMatchDifficultyStar1;
    private ImageView mMatchDifficultyStar2;
    private ImageView mMatchDifficultyStar3;

    // Constructor DifficultyView sets context
    public MatchDifficultyView(Context context) {
        this(context, null);
        Log.d (TAG, "constructor");
    }

    //Overloaded constructor sets context and attributes
    public MatchDifficultyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d (TAG, "overloaded constructor, includes AttributeSet");
        LayoutInflater.from(context).inflate(R.layout.match_difficulty_view, this, true);
        setOrientation(LinearLayout.VERTICAL);

        mMatchDifficultyTitle = (TextView) findViewById(R.id.match_difficulty_level_title);

        FontLoader.setTypeface(Shared.context, new TextView[] {
                mMatchDifficultyTitle , difficultyEasyTitle, difficultyMediumTitle, difficultyHardTitle
        }, FontLoader.Font.ANGRYBIRDS);
    }

    //Method setMatchDifficultyLevelTitle sets the title for the view
    public void setMatchDifficultyLevelTitle (int diff) {
        switch (diff) {
            case 1:
                difficultyEasyTitle = (TextView) findViewById(R.id.match_difficulty_level_title);
                difficultyEasyTitle.setText(R.string.match_difficulty_level_1);
                difficultyEasyTitle.setTextSize(Shared.context.getResources().getDimension(R.dimen.match_difficulty_level_title_size));
                difficultyEasyTitle.setTextColor(Shared.context.getResources().getColor(R.color.generic_text_color));
                difficultyEasyTitle.setGravity(Gravity.CENTER);
                break;
            case 2:
                difficultyMediumTitle = (TextView) findViewById(R.id.match_difficulty_level_title);
                difficultyMediumTitle.setText(R.string.match_difficulty_level_2);
                difficultyMediumTitle.setTextSize(Shared.context.getResources().getDimension(R.dimen.match_difficulty_level_title_size));
                difficultyEasyTitle.setTextColor(Shared.context.getResources().getColor(R.color.generic_text_color));
                difficultyMediumTitle.setGravity(Gravity.CENTER);
                break;
            case 3:
                difficultyHardTitle = (TextView) findViewById(R.id.match_difficulty_level_title);
                difficultyHardTitle.setText(R.string.match_difficulty_level_3);
                difficultyHardTitle.setTextSize(Shared.context.getResources().getDimension(R.dimen.match_difficulty_level_title_size));
                difficultyEasyTitle.setTextColor(Shared.context.getResources().getColor(R.color.generic_text_color));
                difficultyHardTitle.setGravity(Gravity.CENTER);
                break;
        }
    }

    //Method setMatchDifficultyLevelImage sets the image for the view
    public void setmMatchDifficultyLevelImage (int diff) {
        switch (diff) {
            case 1:
                mMatchDifficultyImage = (ImageView) findViewById(R.id.match_difficulty_image);
                mMatchDifficultyImage.setBackgroundResource(R.drawable.match_difficulty_easy_rectangle);
                break;
            case 2:
                mMatchDifficultyImage = (ImageView) findViewById(R.id.match_difficulty_image);
                mMatchDifficultyImage.setBackgroundResource(R.drawable.match_difficulty_medium_rectangle);
                break;
            case 3:
                mMatchDifficultyImage = (ImageView) findViewById(R.id.match_difficulty_image);
                mMatchDifficultyImage.setBackgroundResource(R.drawable.match_difficulty_hard_rectangle);
                break;
        }
    }

    //Method setMatchDifficulty sets the highest achieved stars for each level as stored in memory
    public void setMatchDifficultyStars(int diff, int stars) {
        Log.d(TAG, "method setMatchDifficulty: diff: " + diff + " | stars: " + stars);
        LinearLayout starsLayout = (LinearLayout) findViewById(R.id.match_difficulty_stars);
        starsLayout.setOrientation(LinearLayout.HORIZONTAL);
        mMatchDifficultyStar1 = (ImageView) findViewById(R.id.match_difficulty_star_1);
        mMatchDifficultyStar2 = (ImageView) findViewById(R.id.match_difficulty_star_2);
        mMatchDifficultyStar3 = (ImageView) findViewById(R.id.match_difficulty_star_3);
        switch (diff) {
            case 1:
                switch (stars) {
                    case 0:
                        Log.d (TAG, "setMatchDifficultyStars: case diff: 1 | stars: 0");
                        mMatchDifficultyStar1.setImageResource(R.drawable.circle_80px);
                        mMatchDifficultyStar2.setImageResource(R.drawable.circle_80px);
                        mMatchDifficultyStar3.setImageResource(R.drawable.circle_80px);
                        break;
                    case 1:
                        Log.d (TAG, "setMatchDifficultyStars: case diff: 1 | stars: 1");
                        mMatchDifficultyStar1.setImageResource(R.drawable.circle_with_star_80px);
                        mMatchDifficultyStar2.setImageResource(R.drawable.circle_80px);
                        mMatchDifficultyStar3.setImageResource(R.drawable.circle_80px);
                        break;
                    case 2:
                        Log.d (TAG, "setMatchDifficultyStars: case diff: 1 | stars: 2");
                        mMatchDifficultyStar1.setImageResource(R.drawable.circle_with_star_80px);
                        mMatchDifficultyStar2.setImageResource(R.drawable.circle_with_star_80px);
                        mMatchDifficultyStar3.setImageResource(R.drawable.circle_80px);
                        break;
                    case 3:
                        Log.d (TAG, "setMatchDifficultyStars: case diff: 1 | stars: 3");
                        mMatchDifficultyStar1.setImageResource(R.drawable.circle_with_star_80px);
                        mMatchDifficultyStar2.setImageResource(R.drawable.circle_with_star_80px);
                        mMatchDifficultyStar3.setImageResource(R.drawable.circle_with_star_80px);
                        break;
                }
                break;
            case 2:
                switch (stars) {
                    case 0:
                        Log.d (TAG, "setMatchDifficultyStars: case diff: 2 | stars: 0");
                        mMatchDifficultyStar1.setImageResource(R.drawable.circle_80px);
                        mMatchDifficultyStar2.setImageResource(R.drawable.circle_80px);
                        mMatchDifficultyStar3.setImageResource(R.drawable.circle_80px);
                        break;
                    case 1:
                        Log.d (TAG, "setMatchDifficultyStars: case diff: 2 | stars: 1");
                        mMatchDifficultyStar1.setImageResource(R.drawable.circle_with_star_80px);
                        mMatchDifficultyStar2.setImageResource(R.drawable.circle_80px);
                        mMatchDifficultyStar3.setImageResource(R.drawable.circle_80px);
                        break;
                    case 2:
                        Log.d (TAG, "setMatchDifficultyStars: case diff: 2 | stars: 2");
                        mMatchDifficultyStar1.setImageResource(R.drawable.circle_with_star_80px);
                        mMatchDifficultyStar2.setImageResource(R.drawable.circle_with_star_80px);
                        mMatchDifficultyStar3.setImageResource(R.drawable.circle_80px);
                        break;
                    case 3:
                        Log.d (TAG, "setMatchDifficultyStars: case diff: 2 | stars: 3");
                        mMatchDifficultyStar1.setImageResource(R.drawable.circle_with_star_80px);
                        mMatchDifficultyStar2.setImageResource(R.drawable.circle_with_star_80px);
                        mMatchDifficultyStar3.setImageResource(R.drawable.circle_with_star_80px);
                        break;
                }
                break;
            case 3:
                switch (stars) {
                    case 0:
                        Log.d (TAG, "setMatchDifficultyStars: case diff: 3 | stars: 0");
                        mMatchDifficultyStar1.setImageResource(R.drawable.circle_80px);
                        mMatchDifficultyStar2.setImageResource(R.drawable.circle_80px);
                        mMatchDifficultyStar3.setImageResource(R.drawable.circle_80px);
                        break;
                    case 1:
                        Log.d (TAG, "setMatchDifficultyStars: case diff: 3 | stars: 1");
                        mMatchDifficultyStar1.setImageResource(R.drawable.circle_with_star_80px);
                        mMatchDifficultyStar2.setImageResource(R.drawable.circle_80px);
                        mMatchDifficultyStar3.setImageResource(R.drawable.circle_80px);
                        break;
                    case 2:
                        Log.d (TAG, "setMatchDifficultyStars: case diff: 3 | stars: 2");
                        mMatchDifficultyStar1.setImageResource(R.drawable.circle_with_star_80px);
                        mMatchDifficultyStar2.setImageResource(R.drawable.circle_with_star_80px);
                        mMatchDifficultyStar3.setImageResource(R.drawable.circle_80px);
                        break;
                    case 3:
                        Log.d (TAG, "setMatchDifficultyStars: case diff: 3 | stars: 3");
                        mMatchDifficultyStar1.setImageResource(R.drawable.circle_with_star_80px);
                        mMatchDifficultyStar2.setImageResource(R.drawable.circle_with_star_80px);
                        mMatchDifficultyStar3.setImageResource(R.drawable.circle_with_star_80px);
                        break;
                }
                break;
        }
    }
}