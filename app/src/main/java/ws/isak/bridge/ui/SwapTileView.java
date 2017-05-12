package ws.isak.bridge.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.AttributeSet;

import android.graphics.Bitmap;
import android.graphics.PorterDuff;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import ws.isak.bridge.R;
import ws.isak.bridge.common.Shared;
import ws.isak.bridge.common.SwapCardData;

/**
 * Class SwapTileView contains the code for generating a single tile in the match board array.
 * This includes methods for flipping a tile and verifying the state of a tile (face up/ down)
 *
 * @author isak
 */

public class SwapTileView extends FrameLayout {

    public static final String TAG = "SwapTileView";

    private ImageView mTileImage;
    private TextView mTileText;         //TODO this is for debugging - comment out when functioning
    private boolean isSelected = false;

    public SwapTileView(Context context) {
        this(context, null);
        Log.d (TAG, "constructor");
    }

    public SwapTileView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //Log.d (TAG, "overloaded constructor");
    }

    public static SwapTileView fromXml(Context context, ViewGroup parent) {
        Log.d (TAG, "method fromXml: inflating swap_tile_view: " +
                LayoutInflater.from(context).inflate(R.layout.swap_tile_view, parent, false));
        return (SwapTileView) LayoutInflater.from(context).inflate(R.layout.swap_tile_view, parent, false);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //Log.d (TAG, "method onFinishInflate");
        mTileImage = (ImageView) findViewById(R.id.swap_tile_image);
        mTileText = (TextView) findViewById(R.id.debug_image_view_text);
        Log.d (TAG, " ... ImageView mTileImage: " + mTileImage);
    }

    //FIXME - use asynctask here? instead of setImageBitmap directly in method?
    public void setTileImage(final Bitmap bitmap) {
        Log.d (TAG, "method setTileImage: bitmap: " + bitmap +
                " | mTileImage: " + mTileImage + " | calling new AsyncTask");
        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... params) {
                Log.d (TAG, "method setTileImage: AsyncTask: doInBackground: returning bitmap: " + bitmap);
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                mTileImage.setImageBitmap(result);
                mTileImage.setVisibility(VISIBLE);
                mTileImage.invalidate();
                Log.d (TAG, "method setTileImage: AsyncTask: onPostExecute: mTileImage @: " +
                        mTileImage + " | set with bitmap: " + result +
                        " | mTileImage.getVisibility(): " + mTileImage.getVisibility());
            }
        }.execute();
    }

    public void setTileDebugText(SwapCardData tileData) {
        Log.d (TAG, "method setTileDebugText");
        mTileText.setText("");
        String tileBitmapAddress = Integer.toString(System.identityHashCode(tileData.getCardBitmap()));
        Log.w(TAG, "method setTileDebugText: tileBitmapAddress: " + tileBitmapAddress +
                " | tileData.getCardBitmap: " + tileData.getCardBitmap());
        //String[] bitmapAdd = tileBitmapAddress.split("@");
        //String loc = bitmapAdd[1];
        String tileText = "CardID: <" +
                tileData.getCardID().getSwapCardSpeciesID() +
                "," +
                tileData.getCardID().getSwapCardSegmentID() +
                ">" +
                "\n"; // + loc;
        //Log.d (TAG, "Tile Debug Text: " + tileText + " | location of bitmap: " + loc);
        Log.d (TAG, "Tile Debug Text: " + tileText);
        mTileText.setText(tileText);
    }

    public void select() {
        Log.d (TAG, "method select ... at start: mTileImage: " + mTileImage);
        isSelected = true;
        //can this be as simple as adding a border? see @drawable/border_***.xml and changing the border color on selection
        //current implementation involves an overlay of transparent red FIXME - set color in xml
        mTileImage.setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
        mTileImage.invalidate();

    }

    public void unSelect() {
        Log.d (TAG, "method unSelect ... at start: mTileImage: " + mTileImage);
        isSelected = false;
        mTileImage.setColorFilter(0, PorterDuff.Mode.MULTIPLY);    //FIXME set color in xml
        mTileImage.postInvalidate();        //FIXME does this work?
    }

    public boolean isSelected() {
        Log.d (TAG, "method isSelected");
        return isSelected;
    }
}