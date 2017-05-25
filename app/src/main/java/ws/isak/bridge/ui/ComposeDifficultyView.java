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

public class ComposeDifficultyView extends LinearLayout {

    public static final String TAG = "ComposeDifficultyView";

    private TextView mComposeDifficultyTitle;
    private TextView difficultyEasyTitle;
    private TextView difficultyMediumTitle;
    private TextView difficultyHardTitle;

    private ImageView mComposeDifficultyImage;
    private ImageView mComposeDifficultyStar1;
    private ImageView mComposeDifficultyStar2;
    private ImageView mComposeDifficultyStar3;

    // Constructor DifficultyView sets context
    public ComposeDifficultyView(Context context) {
        this(context, null);
        Log.d (TAG, "constructor");
    }

    //Overloaded constructor sets context and attributes
    public ComposeDifficultyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d (TAG, "overloaded constructor, includes AttributeSet");
        LayoutInflater.from(context).inflate(R.layout.compose_difficulty_view, this, true);
        setOrientation(LinearLayout.VERTICAL);

        mComposeDifficultyTitle = (TextView) findViewById(R.id.compose_difficulty_level_title);

        FontLoader.setTypeface(Shared.context, new TextView[] {
                mComposeDifficultyTitle , difficultyEasyTitle, difficultyMediumTitle, difficultyHardTitle
        }, FontLoader.Font.ANGRYBIRDS);
    }

    //Method setComposeDifficultyLevelTitle sets the title for the view
    public void setComposeDifficultyLevelTitle (int diff) {
        switch (diff) {
            case 1:
                difficultyEasyTitle = (TextView) findViewById(R.id.compose_difficulty_level_title);
                difficultyEasyTitle.setText(R.string.compose_difficulty_level_1);
                difficultyEasyTitle.setTextSize(Shared.context.getResources().getDimension(R.dimen.compose_difficulty_level_title_size));
                difficultyEasyTitle.setGravity(Gravity.CENTER);
                break;
            case 2:
                difficultyMediumTitle = (TextView) findViewById(R.id.compose_difficulty_level_title);
                difficultyMediumTitle.setText(R.string.compose_difficulty_level_2);
                difficultyMediumTitle.setTextSize(Shared.context.getResources().getDimension(R.dimen.compose_difficulty_level_title_size));
                difficultyMediumTitle.setGravity(Gravity.CENTER);
                break;
            case 3:
                difficultyHardTitle = (TextView) findViewById(R.id.compose_difficulty_level_title);
                difficultyHardTitle.setText(R.string.compose_difficulty_level_3);
                difficultyHardTitle.setTextSize(Shared.context.getResources().getDimension(R.dimen.compose_difficulty_level_title_size));
                difficultyHardTitle.setGravity(Gravity.CENTER);
                break;
        }
    }

    //Method setComposeDifficultyLevelImage sets the image for the view
    public void setmComposeDifficultyLevelImage (int diff) {
        switch (diff) {
            case 1:
                mComposeDifficultyImage = (ImageView) findViewById(R.id.compose_difficulty_image);
                mComposeDifficultyImage.setBackgroundResource(R.drawable.compose_difficulty_1_200px);
                break;
            case 2:
                mComposeDifficultyImage = (ImageView) findViewById(R.id.compose_difficulty_image);
                mComposeDifficultyImage.setBackgroundResource(R.drawable.compose_difficulty_2_200px);
                break;
            case 3:
                mComposeDifficultyImage = (ImageView) findViewById(R.id.compose_difficulty_image);
                mComposeDifficultyImage.setBackgroundResource(R.drawable.compose_difficulty_3_200px);
                break;

        }
    }

    //Method setComposeDifficulty sets the highest achieved stars for each level as stored in memory
    public void setComposeDifficultyStars(int diff, int stars) {
        Log.d(TAG, "method setComposeDifficulty: diff: " + diff + " | stars: " + stars);
        LinearLayout starsLayout = (LinearLayout) findViewById(R.id.compose_difficulty_stars);
        starsLayout.setOrientation(LinearLayout.HORIZONTAL);
        mComposeDifficultyStar1 = (ImageView) findViewById(R.id.compose_difficulty_star_1);
        mComposeDifficultyStar2 = (ImageView) findViewById(R.id.compose_difficulty_star_2);
        mComposeDifficultyStar3 = (ImageView) findViewById(R.id.compose_difficulty_star_3);
        switch (diff) {
            case 1:
                switch (stars) {
                    case 0:
                        Log.d (TAG, "setComposeDifficultyStars: case diff: 1 | stars: 0");
                        mComposeDifficultyStar1.setImageResource(R.drawable.circle_80px);
                        mComposeDifficultyStar2.setImageResource(R.drawable.circle_80px);
                        mComposeDifficultyStar3.setImageResource(R.drawable.circle_80px);
                        break;
                    case 1:
                        Log.d (TAG, "setComposeDifficultyStars: case diff: 1 | stars: 1");
                        mComposeDifficultyStar1.setImageResource(R.drawable.circle_with_star_80px);
                        mComposeDifficultyStar2.setImageResource(R.drawable.circle_80px);
                        mComposeDifficultyStar3.setImageResource(R.drawable.circle_80px);
                        break;
                    case 2:
                        Log.d (TAG, "setComposeDifficultyStars: case diff: 1 | stars: 2");
                        mComposeDifficultyStar1.setImageResource(R.drawable.circle_with_star_80px);
                        mComposeDifficultyStar2.setImageResource(R.drawable.circle_with_star_80px);
                        mComposeDifficultyStar3.setImageResource(R.drawable.circle_80px);
                        break;
                    case 3:
                        Log.d (TAG, "setComposeDifficultyStars: case diff: 1 | stars: 3");
                        mComposeDifficultyStar1.setImageResource(R.drawable.circle_with_star_80px);
                        mComposeDifficultyStar2.setImageResource(R.drawable.circle_with_star_80px);
                        mComposeDifficultyStar3.setImageResource(R.drawable.circle_with_star_80px);
                        break;
                }
                break;
            case 2:
                switch (stars) {
                    case 0:
                        Log.d (TAG, "setComposeDifficultyStars: case diff: 2 | stars: 0");
                        mComposeDifficultyStar1.setImageResource(R.drawable.circle_80px);
                        mComposeDifficultyStar2.setImageResource(R.drawable.circle_80px);
                        mComposeDifficultyStar3.setImageResource(R.drawable.circle_80px);
                        break;
                    case 1:
                        Log.d (TAG, "setComposeDifficultyStars: case diff: 2 | stars: 1");
                        mComposeDifficultyStar1.setImageResource(R.drawable.circle_with_star_80px);
                        mComposeDifficultyStar2.setImageResource(R.drawable.circle_80px);
                        mComposeDifficultyStar3.setImageResource(R.drawable.circle_80px);
                        break;
                    case 2:
                        Log.d (TAG, "setComposeDifficultyStars: case diff: 2 | stars: 2");
                        mComposeDifficultyStar1.setImageResource(R.drawable.circle_with_star_80px);
                        mComposeDifficultyStar2.setImageResource(R.drawable.circle_with_star_80px);
                        mComposeDifficultyStar3.setImageResource(R.drawable.circle_80px);
                        break;
                    case 3:
                        Log.d (TAG, "setComposeDifficultyStars: case diff: 2 | stars: 3");
                        mComposeDifficultyStar1.setImageResource(R.drawable.circle_with_star_80px);
                        mComposeDifficultyStar2.setImageResource(R.drawable.circle_with_star_80px);
                        mComposeDifficultyStar3.setImageResource(R.drawable.circle_with_star_80px);
                        break;
                }
                break;
            case 3:
                switch (stars) {
                    case 0:
                        Log.d (TAG, "setComposeDifficultyStars: case diff: 3 | stars: 0");
                        mComposeDifficultyStar1.setImageResource(R.drawable.circle_80px);
                        mComposeDifficultyStar2.setImageResource(R.drawable.circle_80px);
                        mComposeDifficultyStar3.setImageResource(R.drawable.circle_80px);
                        break;
                    case 1:
                        Log.d (TAG, "setComposeDifficultyStars: case diff: 3 | stars: 1");
                        mComposeDifficultyStar1.setImageResource(R.drawable.circle_with_star_80px);
                        mComposeDifficultyStar2.setImageResource(R.drawable.circle_80px);
                        mComposeDifficultyStar3.setImageResource(R.drawable.circle_80px);
                        break;
                    case 2:
                        Log.d (TAG, "setComposeDifficultyStars: case diff: 3 | stars: 2");
                        mComposeDifficultyStar1.setImageResource(R.drawable.circle_with_star_80px);
                        mComposeDifficultyStar2.setImageResource(R.drawable.circle_with_star_80px);
                        mComposeDifficultyStar3.setImageResource(R.drawable.circle_80px);
                        break;
                    case 3:
                        Log.d (TAG, "setComposeDifficultyStars: case diff: 3 | stars: 3");
                        mComposeDifficultyStar1.setImageResource(R.drawable.circle_with_star_80px);
                        mComposeDifficultyStar2.setImageResource(R.drawable.circle_with_star_80px);
                        mComposeDifficultyStar3.setImageResource(R.drawable.circle_with_star_80px);
                        break;
                }
                break;
        }
    }
}