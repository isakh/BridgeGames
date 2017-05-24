package ws.isak.bridge.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
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
        FontLoader.setTypeface(Shared.context, new TextView[] { mComposeDifficultyTitle }, FontLoader.Font.ANGRYBIRDS);

        mComposeDifficultyImage = (ImageView) findViewById(R.id.compose_difficulty_image);
        mComposeDifficultyImage.setBackgroundColor(0xFFFF0000); //just a placeholder in this case
        mComposeDifficultyStar1 = (ImageView) findViewById(R.id.compose_difficulty_star_1);
        mComposeDifficultyStar1.setBackgroundResource(R.drawable.circle_80px);
        mComposeDifficultyStar2 = (ImageView) findViewById(R.id.compose_difficulty_star_2);
        mComposeDifficultyStar1.setBackgroundResource(R.drawable.circle_80px);
        mComposeDifficultyStar3 = (ImageView) findViewById(R.id.compose_difficulty_star_3);
        mComposeDifficultyStar1.setBackgroundResource(R.drawable.circle_80px);
    }

    //Method setComposeDifficulty
    public void setComposeDifficulty(int stars) {
        Log.d (TAG, "method setComposeDifficulty");
        if (stars >= 1) {
            mComposeDifficultyStar1.setBackgroundResource(R.drawable.circle_with_star_80px);
        }
        if (stars >= 2) {
            mComposeDifficultyStar2.setBackgroundResource(R.drawable.circle_with_star_80px);
        }
        if (stars == 3) {
            mComposeDifficultyStar3.setBackgroundResource(R.drawable.circle_with_star_80px);
        }
        else {
            Log.v (TAG, "setComposeDifficulty: the user has not acheived any stars yet");
        }
    }
}
