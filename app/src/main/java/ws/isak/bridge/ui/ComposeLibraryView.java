package ws.isak.bridge.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import ws.isak.bridge.R;
import ws.isak.bridge.common.Shared;
import ws.isak.bridge.utils.ImageScaling;

/*
 * The Compose Library View class inflates the frame in the Compose Game Fragment where the library
 * is drawn (left hand side, currently 15% of screen width).  It is represented as a scrolling list
 * of images of spectrograms of the the audio samples in the library.
 *
 * @author isak
 */

public class ComposeLibraryView extends LinearLayout implements View.OnClickListener {

    public static final String TAG = "ComposeLibraryView";

    public static String URI_DRAWABLE = "drawable://";

    private int mScreenWidth;
    private int mScreenHeight;


    public ComposeLibraryView(Context context) {
        this(context, null);
        Log.d (TAG, "constructor");
    }

    public ComposeLibraryView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        Log.d (TAG, "overloaded constructor");
        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.CENTER);

        int margin = Shared.context.getResources().getDimensionPixelSize(R.dimen.compose_game_controls_margin_top);
        int padding = Shared.context.getResources().getDimensionPixelSize(R.dimen.compose_board_padding);

        mScreenHeight = getResources().getDisplayMetrics().heightPixels - margin - padding * 2;
        mScreenWidth = (int) Math.floor((getResources().getDisplayMetrics().widthPixels - padding*2 - ImageScaling.px(20)) * 0.2);    //TODO * proportion (currently 20%) of screen for view - make less of a hack
        Log.d (TAG, " ... mScreenHeight: " + mScreenHeight + " | mScreenWidth: " + mScreenWidth);
        setClipToPadding(false);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.d (TAG, "method onFinishInflate");
    }

    public static ComposeLibraryView fromXml(Context context, ViewGroup parent) {
        Log.d (TAG, "method fromXml: inflating swap_controls_view: " +
                LayoutInflater.from(context).inflate(R.layout.compose_library_view, parent, false));
        return (ComposeLibraryView) LayoutInflater.from(context).inflate(R.layout.compose_library_view, parent, false);
    }

    @Override
    public void onClick (View v) {}

    //method populateSampleLibrary will fill the ComposeScrollingImageView LinearLayout with sample ImageView
    //this method will be called from ComposeGameFragment //build the library frame
    public void populateSampleLibrary () {
        Log.i(TAG, "method populateSampleLibrary: num samples to load: " + Shared.composeSampleDataList.size());
        //load the linear layout defined in xml
        LinearLayout libraryLayout = (LinearLayout) findViewById(R.id.compose_game_scrolling_images_linear_layout);
        libraryLayout.setGravity(Gravity.CENTER);
        libraryLayout.setBackgroundColor(0xAA00FF00);       //FIXME set in xml


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(5, 5, 5, 5);

        //iterate over the samples loaded in the library
        for (int sampleNum = 0; sampleNum < Shared.composeSampleDataList.size(); sampleNum++) {
            addSample(sampleNum, libraryLayout, params);
        }
        libraryLayout.setClipChildren(false);
    }


    private void addSample (final int sampleNum, ViewGroup parent, LinearLayout.LayoutParams params) {

        Log.i (TAG, "method addSample: int sampleNum: " + sampleNum +
                " | init: parent.getVisibility: " + parent.getVisibility() +
                " | parent.isShown: " + parent.isShown());
        final ImageButton libraryEntry = new ImageButton (Shared.context);
        libraryEntry.setLayoutParams(params);
        parent.addView(libraryEntry);
        parent.setClipChildren(false);

        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... params) {
                //create an image view for each sample
                int imgWidth;

                imgWidth = (int) (mScreenWidth - (Shared.context.getResources().getDimension(R.dimen.compose_game_library_image_padding)));
                Log.i (TAG, " method addSample: AsyncTask doInBackground: target imgWidth: " + imgWidth);
                String libEntryResourceName = Shared.composeSampleDataList.get(sampleNum).getSpectroURI().substring(URI_DRAWABLE.length());
                int libEntryResourceID = Shared.context.getResources().getIdentifier(libEntryResourceName, "drawable", Shared.context.getPackageName());
                Bitmap libEntryBitmap = ImageScaling.scaleDown(libEntryResourceID, imgWidth, imgWidth);   //third parameter ensure square output - shouldn't be an issue as source files are square
                Bitmap scaledLibEntryBitmap = ImageScaling.crop(libEntryBitmap, imgWidth, imgWidth);
                //FIXME REMOVE DEBUGGING CODE
                int w = scaledLibEntryBitmap.getWidth();
                int h = scaledLibEntryBitmap.getHeight();
                Log.i (TAG, "method addSample: AsyncTask doInBackground:: libEntryResourceName: " + libEntryResourceName +
                        " | bitmap width: " + w + " | bitmap height: " + h);
                //End debug code
                return scaledLibEntryBitmap;
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                //
                BitmapDrawable libEntryBitmapDrawable = new BitmapDrawable(Shared.context.getResources(), result);
                libraryEntry.setBackground(libEntryBitmapDrawable);
            }
        }.execute();

        libraryEntry.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //[-1] If another Sample is Active, cancel that before doing anything with this one
                // As another sample is explicitly already active, this cannot be the first sample
                if (Shared.userData.getCurComposeGameData().getActiveSample() != null) {
                    Toast.makeText(Shared.context, "Switching Active Sample", Toast.LENGTH_SHORT).show();
                    Shared.userData.getCurComposeGameData().setActiveSample(Shared.composeSampleDataList.get(sampleNum));
                }
                //[0] This is the first time a sample is selected
                if (!Shared.userData.getCurComposeGameData().isGameStarted()) {
                    //set game timestamp
                    Shared.userData.getCurComposeGameData().setGameStartTimestamp(System.currentTimeMillis());
                    //set setGameStarted boolean true
                    Shared.userData.getCurComposeGameData().setGameStarted(true);
                }
                //[1] If no other sample is active and game is already started...
                else if (Shared.userData.getCurComposeGameData().isGameStarted() &&
                        Shared.userData.getCurComposeGameData().getActiveSample() == null) {
                    //make current selected ComposeSampleData active
                    Shared.userData.getCurComposeGameData().setActiveSample(Shared.composeSampleDataList.get(sampleNum));

                    //we now await the next press coming from ComposeTrackerBoardView, subsequent clicks
                    //in LibraryView will just hit step [-1] and keep switching the active sample.
                }
            }
        });
    }
}