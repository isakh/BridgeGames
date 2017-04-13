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

/**
 * Class TileView contains the code for generating a single tile in the match board array.
 * This includes methods for flipping a tile and verifying the state of a tile (face up/ down)
 *
 * @author isak
 */

public class SwapTileView extends FrameLayout {

    public final String TAG = "SwapTileView";

    private ImageView mTileImage;
    private boolean mSelected = false;

    public SwapTileView(Context context) {
        this(context, null);
        Log.d (TAG, "constructor");
    }

    public SwapTileView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //Log.d (TAG, "overloaded constructor");
    }

    public static SwapTileView fromXml(Context context, ViewGroup parent) {
        return (SwapTileView) LayoutInflater.from(context).inflate(R.layout.swap_tile_view, parent, false);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTileImage = (ImageView) findViewById(R.id.swap_tile_image);
    }

    public void setTileImage(Bitmap bitmap) {
        //Log.d (TAG, "method setTileImage");
        mTileImage.setImageBitmap(bitmap);
    }

    public void select() {
        //Log.d (TAG, "method flipUp ... at start");
        mSelected = true;
        //TODO addSelectionBounds();
    }

    public void unSelect() {
        //Log.d (TAG, "method flipDown ... at start");
        mSelected = false;
        //TODO removeSelectionBounds();
    }


}