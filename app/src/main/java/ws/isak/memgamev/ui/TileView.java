package ws.isak.memgamev.ui;

import android.content.Context;
import android.util.Log;
import android.util.AttributeSet;

import android.graphics.Bitmap;
import android.graphics.Camera;		//Used to calculate 3D transforms to be applied to animations
import android.graphics.Matrix;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import ws.isak.memgamev.R;

/**
 * Class TileView contains the code for generating a single tile in the board array.  This includes
 * methods for flipping a tile and verifying the state of a tile (face up/ down)
 *
 * @author isak
 */

public class TileView extends FrameLayout {

	public final String TAG = "Class: TileView";

	private RelativeLayout mTopImage;
	private ImageView mTileImage;
	private boolean mFlippedDown = true;

	public TileView(Context context) {
		this(context, null);
        Log.d (TAG, "constructor");
	}

	public TileView(Context context, AttributeSet attrs) {
		super(context, attrs);
        //Log.d (TAG, "overloaded constructor");
	}

	public static TileView fromXml(Context context, ViewGroup parent) {
		return (TileView) LayoutInflater.from(context).inflate(R.layout.tile_view, parent, false);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mTopImage = (RelativeLayout) findViewById(R.id.image_top);
		mTileImage = (ImageView) findViewById(R.id.image);
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


    // **************************************************************************************

	public class FlipAnimation extends Animation {
		private Camera camera;

		private View fromView;
		private View toView;

		private float centerX;
		private float centerY;

		private boolean forward = true;

		/**
		 * Creates a 3D flip animation between two views.
		 * 
		 * @param fromView
		 *            First view in the transition.
		 * @param toView
		 *            Second view in the transition.
		 */
		public FlipAnimation(View fromView, View toView) {
			this.fromView = fromView;
			this.toView = toView;

			setDuration(1000);			//TODO was 700 - try various durations for the animation and amend game time accordingly
			setFillAfter(false);
			setInterpolator(new AccelerateDecelerateInterpolator());
		}

		public void reverse() {
			forward = false;
			View switchView = toView;
			toView = fromView;
			fromView = switchView;
		}

		@Override
		public void initialize(int width, int height, int parentWidth, int parentHeight) {
			super.initialize(width, height, parentWidth, parentHeight);
			centerX = width / 2;
			centerY = height / 2;
			camera = new Camera();
		}

		@Override
		protected void applyTransformation(float interpolatedTime, Transformation t) {
			// Angle around the y-axis of the rotation at the given time
			// calculated both in radians and degrees.
			final double radians = Math.PI * interpolatedTime;
			float degrees = (float) (180.0 * radians / Math.PI);

			// Once we reach the midpoint in the animation, we need to hide the
			// source view and show the destination view. We also need to change
			// the angle by 180 degrees so that the destination does not come in
			// flipped around
			if (interpolatedTime >= 0.5f) {
				degrees -= 180.f;
				fromView.setVisibility(View.GONE);
				toView.setVisibility(View.VISIBLE);
			}

			if (forward)
				degrees = -degrees; // determines direction of rotation when
									// flip begins

			final Matrix matrix = t.getMatrix();
			camera.save();
			camera.rotateY(degrees);
			camera.getMatrix(matrix);
			camera.restore();
			matrix.preTranslate(-centerX, -centerY);
			matrix.postTranslate(centerX, centerY);
		}
	}
}