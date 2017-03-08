package ws.isak.memgamev.ui;

import java.util.Locale;
import android.util.Log;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import ws.isak.memgamev.R;
import ws.isak.memgamev.common.Shared;

public class DifficultyView extends LinearLayout {

    public static final String TAG = "Class: DifficultyView";
	private ImageView mTitle;
	
	public DifficultyView(Context context) {
		this(context, null);
        Log.d (TAG, "constructor");
	}
	
	public DifficultyView(Context context, AttributeSet attrs) {
		super(context, attrs);
        Log.d (TAG, "overloaded constructor, includes AttributeSet");
		LayoutInflater.from(context).inflate(R.layout.difficulty_view, this, true);
		setOrientation(LinearLayout.VERTICAL);
		mTitle = (ImageView) findViewById(R.id.title);
	}
	
	public void setDifficulty(int difficulty, int stars) {
        Log.d (TAG, "method setDifficulty");
		String titleResource = String.format(Locale.US, "button_difficulty_%d_star_%d", difficulty, stars);
		int drawableResourceId = Shared.context.getResources().getIdentifier(titleResource, "drawable", Shared.context.getPackageName());
		mTitle.setImageResource(drawableResourceId);
	}
	
}
