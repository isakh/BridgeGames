package ws.isak.bridge.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import ws.isak.bridge.R;

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

    public SwapControlsView(Context context) {
        this(context, null);
        Log.d (TAG, "constructor");
    }

    public SwapControlsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d (TAG, "overloaded constructor");
    }

    public static SwapControlsView fromXml(Context context, ViewGroup parent) {
        Log.d (TAG, "method fromXml: inflating swap_tile_view: " +
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
