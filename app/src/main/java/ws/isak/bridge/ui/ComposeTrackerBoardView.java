package ws.isak.bridge.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import ws.isak.bridge.R;
import ws.isak.bridge.common.Shared;
import ws.isak.bridge.utils.ImageScaling;

/*
 *
 *
 * @author isak
 */

public class ComposeTrackerBoardView extends GridLayout {

    public static final String TAG = "ComposeTrackerBoardView";

    public static String URI_DRAWABLE = "drawable://";

    private int mScreenWidth;           //TODO make this a function (80%) of the screen width for the board
    private int mScreenHeight;

    GridLayout trackerLayout;

    public ComposeTrackerBoardView(Context context) {
        this(context, null);
        Log.d(TAG, "constructor");
    }

    public ComposeTrackerBoardView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        Log.d(TAG, "overloaded constructor");
        setOrientation(GridLayout.HORIZONTAL);

        int margin = Shared.context.getResources().getDimensionPixelSize(R.dimen.compose_game_controls_margin_top);
        int padding = Shared.context.getResources().getDimensionPixelSize(R.dimen.compose_board_padding);

        mScreenHeight = getResources().getDisplayMetrics().heightPixels - margin - padding * 2;
        mScreenWidth = (int) Math.floor((getResources().getDisplayMetrics().widthPixels - padding * 2 - ImageScaling.px(20)) * 0.8);    //TODO * proportion (currently 20%) of screen for view - make less of a hack
        Log.d(TAG, " ... mScreenHeight: " + mScreenHeight + " | mScreenWidth: " + mScreenWidth);
        setClipToPadding(false);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.d(TAG, "method onFinishInflate");
    }

    public static ComposeTrackerBoardView fromXml(Context context, ViewGroup parent) {
        Log.d(TAG, "method fromXml: inflating swap_controls_view: " +
                LayoutInflater.from(context).inflate(R.layout.compose_tracker_board_view, parent, false));
        return (ComposeTrackerBoardView) LayoutInflater.from(context).inflate(R.layout.compose_tracker_board_view, parent, false);
    }

    public void constructTrackerBoard() {
        Log.i(TAG, "method constructTrackerBoard: board dimensions to load");
        //load the layout defined in xml
        trackerLayout = (GridLayout) findViewById(R.id.compose_game_tracker_board);
        trackerLayout.setBackgroundColor(0xAA00FFFF);       //FIXME set in xml
        trackerLayout.setRowCount(Shared.userData.getCurComposeGameData().getGameRows());
        trackerLayout.setColumnCount(Shared.userData.getCurComposeGameData().getGameCols());
        //define the size of the cell based on filling the height available
        final int cellSize = (int) Math.floor ( mScreenHeight / trackerLayout.getRowCount());
        //iterate over rows in tracker
        for (int r = 0; r < trackerLayout.getRowCount(); r++) {
            //and iterate over columns - columns align in time
            for (int c = 0; c < trackerLayout.getColumnCount(); c++) {
                final int curRow = r;
                final int curCol = c;
                //create an ImageView that fills the cell
                final ImageView sampleCell = new ImageView(Shared.context);
                sampleCell.setImageResource(R.drawable.blank_tracker_cell);     //FIXME since drawing from xml resource do we want this in a separate thread?
                sampleCell.setLayoutParams(new ViewGroup.LayoutParams(cellSize, cellSize));
                sampleCell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick (View view) {
                        //[-1] Error mode, clicking if there is no active sample will Toast to get a sample
                        Log.v (TAG, "methodConstructTrackerBoard: sampleCell.onClick: getActiveSample: " +
                                Shared.userData.getCurComposeGameData().getActiveSample());
                        Log.v (TAG, "methodConstructTrackerBoard: pre ∆ calling debugDataInTrackerCellsArray");
                        Shared.userData.getCurComposeGameData().debugDataInTrackerCellsArray();
                        if (Shared.userData.getCurComposeGameData().getActiveSample() == null) {
                            Toast.makeText(Shared.context, "Please Select a Sample From The Library", Toast.LENGTH_SHORT).show();
                        }
                        //[0] Cell has sample - trigger Toast: "Please remove before replacing"
                        if (Shared.userData.getCurComposeGameData().retrieveDataInTrackerCellsArray(curRow, curCol).getCellSampleData() != null) {
                            Toast.makeText(Shared.context, "Long Press To Delete This Cell Before Placing New Sample", Toast.LENGTH_SHORT).show();
                        }
                        //[1] Clicking on a cell, if there is an activeSample, and an empty cell, will place it on the board
                        else if ((Shared.userData.getCurComposeGameData().getActiveSample() != null) &&
                                (Shared.userData.getCurComposeGameData().retrieveDataInTrackerCellsArray(curRow, curCol).getCellSampleData() == null)) {
                            //places the sample in the trackerCellsArray
                            Shared.userData.getCurComposeGameData().insertDataToTrackerCellsArray(curRow, curCol, sampleCell, Shared.userData.getCurComposeGameData().getActiveSample());
                            //start a thread to redraw the cell
                            new AsyncTask<Void, Void, Bitmap>() {

                                @Override
                                protected Bitmap doInBackground(Void... params) {
                                    //retrieve the bitmap for the cell
                                    String sampleBitmapName = Shared.userData.getCurComposeGameData().getActiveSample().getSpectroURI().substring(URI_DRAWABLE.length());
                                    int sampleBitmapResourceID = Shared.context.getResources().getIdentifier(sampleBitmapName, "drawable", Shared.context.getPackageName());
                                    //scale it to size
                                    Bitmap sampleBitmap = ImageScaling.scaleDown(sampleBitmapResourceID, cellSize, cellSize);   //third parameter ensure square output - shouldn't be an issue as source files are square
                                    Bitmap scaledSampleBitmap = ImageScaling.crop(sampleBitmap, cellSize, cellSize);
                                    return scaledSampleBitmap;
                                }

                                @Override
                                protected void onPostExecute(Bitmap result) {
                                    //update the sampleCell.setImageResource with an appropriately scaled bitmap
                                    sampleCell.setImageBitmap(result);
                                    sampleCell.invalidate();
                                }
                            }.execute();

                            //FIXME and null the activeSample so a new one can be selected. do we want this?
                            //FIXME what if a user wants to place multiple copies of the selected sample?

                            Log.v (TAG, "methodConstructTrackerBoard: onClick: post ∆ calling debugDataInTrackerCellsArray");
                            Shared.userData.getCurComposeGameData().debugDataInTrackerCellsArray();
                        }
                    }
                });
                sampleCell.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick (View view) {
                        //Long click on a cell should:
                        //[-1] if the cell is empty, Toast and do nothing
                        if (Shared.userData.getCurComposeGameData().retrieveDataInTrackerCellsArray(curRow, curCol).getCellSampleData() == null) {
                            Toast.makeText(Shared.context, "No Sample To Delete", Toast.LENGTH_SHORT).show();
                            Log.v (TAG, "methodConstructTrackerBoard: calling return true");
                            return false;
                        }
                        //[0] if the cell contains a sample
                        if (Shared.userData.getCurComposeGameData().retrieveDataInTrackerCellsArray(curRow, curCol).getCellSampleData() != null) {
                            //remove the sample from the trackerCellsArray
                            Shared.userData.getCurComposeGameData().insertDataToTrackerCellsArray(curRow, curCol, sampleCell, null);
                            //redraw the ImageView
                            sampleCell.setImageResource(R.drawable.blank_tracker_cell);     //FIXME since drawing from xml resource do we want this in a separate thread?
                            sampleCell.postInvalidate();
                            Log.v (TAG, "methodConstructTrackerBoard: onLongClick: post ∆ calling debugDataInTrackerCellsArray");
                            Shared.userData.getCurComposeGameData().debugDataInTrackerCellsArray();
                            Log.v (TAG, "methodConstructTrackerBoard: calling return true");
                            return true;
                        }
                        Log.v (TAG, "methodConstructTrackerBoard: calling return true");
                        return false;
                    }
                });
                trackerLayout.addView(sampleCell);
                Shared.userData.getCurComposeGameData().insertDataToTrackerCellsArray(r, c, sampleCell, null);
            }
        }
        Shared.userData.getCurComposeGameData().debugDataInTrackerCellsArray();
    }
}