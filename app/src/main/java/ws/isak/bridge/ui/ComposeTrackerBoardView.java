package ws.isak.bridge.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;

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
        int cellSize = (int) Math.floor ( mScreenHeight / trackerLayout.getRowCount());
        //iterate over rows in tracker
        for (int r = 0; r < trackerLayout.getRowCount(); r++) {
            //and iterate over columns - columns align in time
            for (int c = 0; c < trackerLayout.getColumnCount(); c++) {
                //create an ImageView that fills the cell
                ImageView sampleCell = new ImageView(Shared.context);
                sampleCell.setImageResource(R.drawable.blank_tracker_cell);
                sampleCell.setLayoutParams(new ViewGroup.LayoutParams(cellSize, cellSize)); //TODO Fix these numbers so they are dynamic to screen
                trackerLayout.addView(sampleCell);
                Shared.userData.getCurComposeGameData().insertDataToTrackerCellsArray(r, c, sampleCell, null);
            }
        }
        Shared.userData.getCurComposeGameData().debugDataInTrackerCellsArray();
    }
}