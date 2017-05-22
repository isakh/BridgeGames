package ws.isak.bridge.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;

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

    public ComposeTrackerBoardView (Context context) {
        this(context, null);
        Log.d (TAG, "constructor");
    }

    public ComposeTrackerBoardView (Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        Log.d (TAG, "overloaded constructor");
        setOrientation(GridLayout.HORIZONTAL);
        //FIXME setGravity(Gravity.CENTER);

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

    public static ComposeTrackerBoardView fromXml(Context context, ViewGroup parent) {
        Log.d (TAG, "method fromXml: inflating swap_controls_view: " +
                LayoutInflater.from(context).inflate(R.layout.compose_tracker_board_view, parent, false));
        return (ComposeTrackerBoardView) LayoutInflater.from(context).inflate(R.layout.compose_tracker_board_view, parent, false);
    }

}
