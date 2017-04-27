package ws.isak.bridge.ui;

import android.content.Context;

import android.util.AttributeSet;
import android.util.Log;

import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.LinearLayout;

import android.view.Gravity;
import android.view.ViewGroup;
import android.view.LayoutInflater;

import ws.isak.bridge.R;
import ws.isak.bridge.utils.ImageScaling;

/*
 * The SwapControlsView class describes the UI for the audio playback control buttons associated
 * with each row in the Swap Game.
 *
 * @author isak
 */

public class SwapControlsView extends FrameLayout{

    public static final String TAG = "SwapControlsView";
    private RelativeLayout controlsLayout;
    private ImageView restartButton;
    private ImageView playPauseButton;
    private int mScreenWidth;           //TODO make this a function of the 20% of the screen width for the board
    private int mScreenHeight;

    public SwapControlsView(Context context) {
        this(context, null);
        Log.d (TAG, "constructor");
    }

    public SwapControlsView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        Log.d (TAG, "overloaded constructor");
        //FIXME setOrientation(LinearLayout.VERTICAL);
        //FIXME setGravity(Gravity.CENTER);

        int margin = getResources().getDimensionPixelSize(R.dimen.swap_game_timer_margin_top);
        int padding = getResources().getDimensionPixelSize(R.dimen.swap_board_padding);
        mScreenHeight = getResources().getDisplayMetrics().heightPixels - margin - padding*2;
        mScreenWidth = (int) Math.floor((getResources().getDisplayMetrics().widthPixels - padding*2 - ImageScaling.px(20)) * 0.2);    //TODO * proportion of screen for view - make less of a hack
        Log.d (TAG, " ... mScreenHeight: " + mScreenHeight + " | mScreenWidth: " + mScreenWidth);
        setClipToPadding(false);
    }

    public static SwapControlsView fromXml(Context context, ViewGroup parent) {
        Log.d (TAG, "method fromXml: inflating swap_controls_view: " +
                LayoutInflater.from(context).inflate(R.layout.swap_controls_view, parent, false));
        return (SwapControlsView) LayoutInflater.from(context).inflate(R.layout.swap_controls_view, parent, false);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.d (TAG, "method onFinishInflate");
        controlsLayout = (RelativeLayout) findViewById(R.id.swap_controls_view );
        restartButton = (ImageView) findViewById(R.id.swap_controls_reset_playback);
        playPauseButton = (ImageView) findViewById(R.id.swap_controls_play_pause);
        Log.d (TAG, " ... restartButton: " + restartButton + " | playPauseButton: " + playPauseButton);
    }
}
