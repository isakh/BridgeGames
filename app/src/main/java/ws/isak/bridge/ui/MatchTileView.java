package ws.isak.bridge.ui;

import android.content.Context;
import android.util.Log;
import android.util.AttributeSet;

import android.graphics.Bitmap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import ws.isak.bridge.R;
import ws.isak.bridge.utils.FlipAnimation;

/**
 * Class TileView contains the code for generating a single tile in the match board array.
 * This includes methods for flipping a tile and verifying the state of a tile (face up/ down)
 *
 * @author isak
 */

public class MatchTileView extends FrameLayout {

	public final String TAG = "MatchTileView";

	private RelativeLayout mTopImage;
	private ImageView mTileImage;
	private boolean mFlippedDown = true;

	public MatchTileView(Context context) {
		this(context, null);
        Log.d (TAG, "constructor");
	}

	public MatchTileView(Context context, AttributeSet attrs) {
		super(context, attrs);
        //Log.d (TAG, "overloaded constructor");
	}

	public static MatchTileView fromXml(Context context, ViewGroup parent) {
		return (MatchTileView) LayoutInflater.from(context).inflate(R.layout.match_tile_view, parent, false);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mTopImage = (RelativeLayout) findViewById(R.id.match_image_top);
		mTileImage = (ImageView) findViewById(R.id.match_image);
	}

	public void setTileImage(Bitmap bitmap) {
        //Log.d (TAG, "method setTileImage");
		mTileImage.setImageBitmap(bitmap);
	}

	public void flipUp() {
		//Log.d (TAG, "method flipUp ... at start");
		mFlippedDown = false;
		flip();
	}

	public void flipDown() {
		//Log.d (TAG, "method flipDown ... at start");
		mFlippedDown = true;
		flip();
	}
	
	private void flip() {
		FlipAnimation flipAnimation = new FlipAnimation(mTopImage, mTileImage);
		if (mTopImage.getVisibility() == View.GONE) {
			flipAnimation.reverse();
		}
		startAnimation(flipAnimation);
	}

	public boolean isFlippedDown() {
		return mFlippedDown;
	}
}