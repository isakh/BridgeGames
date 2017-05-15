package ws.isak.bridge.ui;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
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

import java.util.HashMap;

import ws.isak.bridge.R;
import ws.isak.bridge.common.Shared;
import ws.isak.bridge.common.SwapCardData;
import ws.isak.bridge.utils.SwapTileCoordinates;

/**
 * Class SwapTileView contains the code for generating a single tile in the match board array.
 * This includes methods for flipping a tile and verifying the state of a tile (face up/ down)
 *
 * @author isak
 */

public class SwapTileView extends FrameLayout{

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

    // use an asynctask here instead of setImageBitmap directly in method - onPostExecute is the UI thread
    public void setTileImage(final Bitmap bitmap, final String calledFrom) {
        //Log.d (TAG, "method setTileImage: bitmap: " + bitmap +
        //        " | mTileImage: " + mTileImage + " | calling new AsyncTask");
        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... params) {
                Log.v (TAG, "method setTileImage: AsyncTask: doInBackground: returning bitmap: " + bitmap);
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                //mTileImage.setImageDrawable(null);      //FIXME - testing - seems to do nothing, remove later?
                //mTileImage.invalidate();                //FIXME - testing

                BitmapDrawable bitmapDrawable = new BitmapDrawable(Shared.context.getResources(), bitmap);

                mTileImage.setImageDrawable(bitmapDrawable);
                //mTileImage.setBackground(bitmapDrawable);
                mTileImage.invalidate();                //FIXME - testing
                mTileImage.setVisibility(VISIBLE);

                //TODO remove debugging code when functional
                Log.d (TAG, "method setTileImage: onPostExecute: calledFrom: " + calledFrom +
                        " | mTileImage @: " + mTileImage +
                        " | mTileImage.getImageDrawable: " + mTileImage.getDrawable() +
                        " | set with bitmap: " + result +
                        " | mTileImage.getVisibility(): " + mTileImage.getVisibility());
                //degbug colours yellow for the tile that should now have the second card selected displayed
                if (calledFrom == "[class SwapGameFragment: SwapSelectedCardsEvent: setting tile0View to bitmap from old tile1View]") {
                    mTileImage.setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);
                }
                //degbug colours blue for the tile that should now have the first card selected displayed
                else if (calledFrom == "[class SwapGameFragment: SwapSelectedCardsEvent: setting tile1View to bitmap from old tile0View]") {
                    mTileImage.setColorFilter(0xFF0000FF, PorterDuff.Mode.MULTIPLY);
                }
            }
        }.execute();
    }

    public void setTileDebugText(HashMap <SwapTileCoordinates, SwapTileView> coordsViewsMap, SwapTileCoordinates curTileOnBoard) {
        Log.d (TAG, "method setTileDebugText");
        mTileText.setText("");

        SwapCardData tileData = Shared.userData.getCurSwapGameData().getSwapCardDataFromSwapBoardMap(curTileOnBoard);
        SwapTileView tileView = coordsViewsMap.get(curTileOnBoard);
        String tileText =
                "Coords: <" +
                curTileOnBoard.getSwapCoordRow() +
                "," +
                curTileOnBoard.getSwapCoordCol() +
                ">" +
                "\nCardID: <" +
                tileData.getCardID().getSwapCardSpeciesID() +
                "," +
                tileData.getCardID().getSwapCardSegmentID() +
                ">" +
                "\nBitmap @: " + tileData.getCardBitmap() +
                "\nTileView @: " + tileView;
        //Log.d (TAG, "Tile Debug Text: " + tileText + " | location of bitmap: " + loc);
        Log.d (TAG, "Tile Debug Text: " + tileText);
        mTileText.setText(tileText);
    }

    public void select(String calledFrom) {
        Log.d (TAG, "method select ... at start: mTileImage: " + mTileImage + " | calledFrom: " + calledFrom);
        isSelected = true;
        //current implementation involves an overlay of transparent red FIXME - set color in xml
        mTileImage.setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
        mTileImage.invalidate();

    }

    public void unSelect() {
        Log.d (TAG, "method unSelect ... at start: mTileImage: " + mTileImage);
        isSelected = false;
        mTileImage.clearColorFilter();
        mTileImage.postInvalidate();        //FIXME is this necessary? should it just be .invalidate()
    }

    public boolean isSelected() {
        Log.d (TAG, "method isSelected");
        return isSelected;
    }
}