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

public class SwapDifficultyView extends LinearLayout {

    public static final String TAG = "SwapDifficultyView";

    private TextView mSwapDifficultyTitle;
    private TextView difficultyEasyTitle;
    private TextView difficultyMediumTitle;
    private TextView difficultyHardTitle;

    private ImageView mSwapDifficultyImage;
    private ImageView mSwapDifficultyStar1;
    private ImageView mSwapDifficultyStar2;
    private ImageView mSwapDifficultyStar3;

    // Constructor DifficultyView sets context
    public SwapDifficultyView(Context context) {
        this(context, null);
        Log.d (TAG, "constructor");
    }

    //Overloaded constructor sets context and attributes
    public SwapDifficultyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d (TAG, "overloaded constructor, includes AttributeSet");
        LayoutInflater.from(context).inflate(R.layout.swap_difficulty_view, this, true);
        setOrientation(LinearLayout.VERTICAL);

        mSwapDifficultyTitle = (TextView) findViewById(R.id.swap_difficulty_level_title);

        FontLoader.setTypeface(Shared.context, new TextView[] {
                mSwapDifficultyTitle , difficultyEasyTitle, difficultyMediumTitle, difficultyHardTitle
        }, FontLoader.Font.ANGRYBIRDS);
    }

    //Method setSwapDifficultyLevelTitle sets the title for the view
    public void setSwapDifficultyLevelTitle (int diff) {
        switch (diff) {
            case 1:
                difficultyEasyTitle = (TextView) findViewById(R.id.swap_difficulty_level_title);
                difficultyEasyTitle.setText(R.string.swap_difficulty_level_1);
                difficultyEasyTitle.setTextSize(Shared.context.getResources().getDimension(R.dimen.swap_difficulty_level_title_size));
                difficultyEasyTitle.setGravity(Gravity.CENTER);
                break;
            case 2:
                difficultyMediumTitle = (TextView) findViewById(R.id.swap_difficulty_level_title);
                difficultyMediumTitle.setText(R.string.swap_difficulty_level_2);
                difficultyMediumTitle.setTextSize(Shared.context.getResources().getDimension(R.dimen.swap_difficulty_level_title_size));
                difficultyMediumTitle.setGravity(Gravity.CENTER);
                break;
            case 3:
                difficultyHardTitle = (TextView) findViewById(R.id.swap_difficulty_level_title);
                difficultyHardTitle.setText(R.string.swap_difficulty_level_3);
                difficultyHardTitle.setTextSize(Shared.context.getResources().getDimension(R.dimen.swap_difficulty_level_title_size));
                difficultyHardTitle.setGravity(Gravity.CENTER);
                break;
        }
    }

    //Method setSwapDifficultyLevelImage sets the image for the view
    public void setmSwapDifficultyLevelImage (int diff) {
        switch (diff) {
            case 1:
                mSwapDifficultyImage = (ImageView) findViewById(R.id.swap_difficulty_image);
                mSwapDifficultyImage.setBackgroundResource(R.drawable.swap_difficulty_easy_200px);
                break;
            case 2:
                mSwapDifficultyImage = (ImageView) findViewById(R.id.swap_difficulty_image);
                mSwapDifficultyImage.setBackgroundResource(R.drawable.swap_difficulty_medium_200px);
                break;
            case 3:
                mSwapDifficultyImage = (ImageView) findViewById(R.id.swap_difficulty_image);
                mSwapDifficultyImage.setBackgroundResource(R.drawable.swap_difficulty_hard_200px);
                break;
        }
    }

    //Method setSwapDifficulty sets the highest achieved stars for each level as stored in memory
    public void setSwapDifficultyStars(int diff, int stars) {
        Log.d(TAG, "method setSwapDifficulty: diff: " + diff + " | stars: " + stars);
        LinearLayout starsLayout = (LinearLayout) findViewById(R.id.swap_difficulty_stars);
        starsLayout.setOrientation(LinearLayout.HORIZONTAL);
        mSwapDifficultyStar1 = (ImageView) findViewById(R.id.swap_difficulty_star_1);
        mSwapDifficultyStar2 = (ImageView) findViewById(R.id.swap_difficulty_star_2);
        mSwapDifficultyStar3 = (ImageView) findViewById(R.id.swap_difficulty_star_3);
        switch (diff) {
            case 1:
                switch (stars) {
                    case 0:
                        Log.d (TAG, "setSwapDifficultyStars: case diff: 1 | stars: 0");
                        mSwapDifficultyStar1.setImageResource(R.drawable.circle_80px);
                        mSwapDifficultyStar2.setImageResource(R.drawable.circle_80px);
                        mSwapDifficultyStar3.setImageResource(R.drawable.circle_80px);
                        break;
                    case 1:
                        Log.d (TAG, "setSwapDifficultyStars: case diff: 1 | stars: 1");
                        mSwapDifficultyStar1.setImageResource(R.drawable.circle_with_star_80px);
                        mSwapDifficultyStar2.setImageResource(R.drawable.circle_80px);
                        mSwapDifficultyStar3.setImageResource(R.drawable.circle_80px);
                        break;
                    case 2:
                        Log.d (TAG, "setSwapDifficultyStars: case diff: 1 | stars: 2");
                        mSwapDifficultyStar1.setImageResource(R.drawable.circle_with_star_80px);
                        mSwapDifficultyStar2.setImageResource(R.drawable.circle_with_star_80px);
                        mSwapDifficultyStar3.setImageResource(R.drawable.circle_80px);
                        break;
                    case 3:
                        Log.d (TAG, "setSwapDifficultyStars: case diff: 1 | stars: 3");
                        mSwapDifficultyStar1.setImageResource(R.drawable.circle_with_star_80px);
                        mSwapDifficultyStar2.setImageResource(R.drawable.circle_with_star_80px);
                        mSwapDifficultyStar3.setImageResource(R.drawable.circle_with_star_80px);
                        break;
                }
                break;
            case 2:
                switch (stars) {
                    case 0:
                        Log.d (TAG, "setSwapDifficultyStars: case diff: 2 | stars: 0");
                        mSwapDifficultyStar1.setImageResource(R.drawable.circle_80px);
                        mSwapDifficultyStar2.setImageResource(R.drawable.circle_80px);
                        mSwapDifficultyStar3.setImageResource(R.drawable.circle_80px);
                        break;
                    case 1:
                        Log.d (TAG, "setSwapDifficultyStars: case diff: 2 | stars: 1");
                        mSwapDifficultyStar1.setImageResource(R.drawable.circle_with_star_80px);
                        mSwapDifficultyStar2.setImageResource(R.drawable.circle_80px);
                        mSwapDifficultyStar3.setImageResource(R.drawable.circle_80px);
                        break;
                    case 2:
                        Log.d (TAG, "setSwapDifficultyStars: case diff: 2 | stars: 2");
                        mSwapDifficultyStar1.setImageResource(R.drawable.circle_with_star_80px);
                        mSwapDifficultyStar2.setImageResource(R.drawable.circle_with_star_80px);
                        mSwapDifficultyStar3.setImageResource(R.drawable.circle_80px);
                        break;
                    case 3:
                        Log.d (TAG, "setSwapDifficultyStars: case diff: 2 | stars: 3");
                        mSwapDifficultyStar1.setImageResource(R.drawable.circle_with_star_80px);
                        mSwapDifficultyStar2.setImageResource(R.drawable.circle_with_star_80px);
                        mSwapDifficultyStar3.setImageResource(R.drawable.circle_with_star_80px);
                        break;
                }
                break;
            case 3:
                switch (stars) {
                    case 0:
                        Log.d (TAG, "setSwapDifficultyStars: case diff: 3 | stars: 0");
                        mSwapDifficultyStar1.setImageResource(R.drawable.circle_80px);
                        mSwapDifficultyStar2.setImageResource(R.drawable.circle_80px);
                        mSwapDifficultyStar3.setImageResource(R.drawable.circle_80px);
                        break;
                    case 1:
                        Log.d (TAG, "setSwapDifficultyStars: case diff: 3 | stars: 1");
                        mSwapDifficultyStar1.setImageResource(R.drawable.circle_with_star_80px);
                        mSwapDifficultyStar2.setImageResource(R.drawable.circle_80px);
                        mSwapDifficultyStar3.setImageResource(R.drawable.circle_80px);
                        break;
                    case 2:
                        Log.d (TAG, "setSwapDifficultyStars: case diff: 3 | stars: 2");
                        mSwapDifficultyStar1.setImageResource(R.drawable.circle_with_star_80px);
                        mSwapDifficultyStar2.setImageResource(R.drawable.circle_with_star_80px);
                        mSwapDifficultyStar3.setImageResource(R.drawable.circle_80px);
                        break;
                    case 3:
                        Log.d (TAG, "setSwapDifficultyStars: case diff: 3 | stars: 3");
                        mSwapDifficultyStar1.setImageResource(R.drawable.circle_with_star_80px);
                        mSwapDifficultyStar2.setImageResource(R.drawable.circle_with_star_80px);
                        mSwapDifficultyStar3.setImageResource(R.drawable.circle_with_star_80px);
                        break;
                }
                break;
        }
    }
}