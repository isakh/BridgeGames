package ws.isak.bridge.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import android.view.Gravity;
import android.view.ViewGroup;
import android.view.LayoutInflater;

import ws.isak.bridge.R;
import ws.isak.bridge.common.Shared;
import ws.isak.bridge.common.Audio;
import ws.isak.bridge.events.engine.SwapPlayPauseRowAudioEvent;
import ws.isak.bridge.events.engine.SwapResetRowAudioEvent;
import ws.isak.bridge.model.SwapGame;
import ws.isak.bridge.utils.ImageScaling;

//FIXME
import android.widget.Button;
import android.widget.Toast;

/*
 * The SwapControlsView class describes the UI for the audio playback control buttons associated
 * with each row in the Swap Game.
 *
 * @author isak
 */

public class SwapControlsView extends LinearLayout implements View.OnClickListener{

    public static final String TAG = "SwapControlsView";

    private static final int NUM_BUTTONS = 2;
    private int mScreenWidth;           //TODO make this a function of the 20% of the screen width for the board
    private int mScreenHeight;

    private Button playPauseButtons [] = new Button[Shared.currentSwapGame.swapBoardConfiguration.numRows];
    private Button resetPlaybackButtons [] = new Button[Shared.currentSwapGame.swapBoardConfiguration.numRows];

    public SwapControlsView(Context context) {
        this(context, null);
        Log.d (TAG, "constructor");
    }

    public SwapControlsView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        Log.d (TAG, "overloaded constructor");
        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.CENTER);

        //TODO - ∆ margin and padding xml references
        int margin = getResources().getDimensionPixelSize(R.dimen.swap_game_timer_margin_top);
        int padding = getResources().getDimensionPixelSize(R.dimen.swap_board_padding);

        mScreenHeight = getResources().getDisplayMetrics().heightPixels - margin - padding*2;
        mScreenWidth = (int) Math.floor((getResources().getDisplayMetrics().widthPixels - padding*2 - ImageScaling.px(20)) * 0.2);    //TODO * proportion of screen for view - make less of a hack
        Log.d (TAG, " ... mScreenHeight: " + mScreenHeight + " | mScreenWidth: " + mScreenWidth);
        setClipToPadding(false);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.d (TAG, "method onFinishInflate");
    }

    public static SwapControlsView fromXml(Context context, ViewGroup parent) {
        Log.d (TAG, "method fromXml: inflating swap_controls_view: " +
                LayoutInflater.from(context).inflate(R.layout.swap_controls_view, parent, false));
        return (SwapControlsView) LayoutInflater.from(context).inflate(R.layout.swap_controls_view, parent, false);
    }

    //method populateControls is called from SwapGameFragment method buildBoard
    public void populateControls(SwapGame curSwapGame) {
        TableLayout table = (TableLayout) findViewById(R.id.swap_audio_buttons_table);

        for (int row = 0; row < Shared.currentSwapGame.swapBoardConfiguration.numRows; row++) {
            TableRow tableRow = new TableRow(Shared.context);
            tableRow.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.MATCH_PARENT,
                    1.0f));
            table.addView(tableRow);

            final int FINAL_ROW = row;

            //-------------------------

            Button buttonR = new Button(Shared.context);
            buttonR.setLayoutParams(new TableRow.LayoutParams(
                    (int) Math.floor(Shared.context.getResources().getDimension(R.dimen.swap_control_button_width)),
                    (int) Math.floor(Shared.context.getResources().getDimension(R.dimen.swap_control_button_height)),
                    1.0f));

            Bitmap originalBitmapR = BitmapFactory.decodeResource(getResources(), R.drawable.swap_playback_reset_button);
            Bitmap scaledBitmapR = Bitmap.createScaledBitmap(originalBitmapR,
                    (ImageScaling.px ((int) Math.floor(Shared.context.getResources().getDimension(R.dimen.swap_control_button_width)))),
                    (ImageScaling.px ((int) Math.floor(Shared.context.getResources().getDimension(R.dimen.swap_control_button_height)))),
                    true);
            Resources resourceR = getResources();
            buttonR.setBackground(new BitmapDrawable(resourceR, scaledBitmapR));

            // Make text not clip on small buttons
            buttonR.setPadding(0, 25, 0, 0);

            buttonR.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resetSwapRowPlaybackButton(FINAL_ROW);
                }
            });

            tableRow.addView(buttonR);
            resetPlaybackButtons[row] = buttonR;

            //
            Button buttonPP = new Button(Shared.context);
            buttonPP.setLayoutParams(new TableRow.LayoutParams(
                    (int) Math.floor(Shared.context.getResources().getDimension(R.dimen.swap_control_button_width)),
                    (int) Math.floor(Shared.context.getResources().getDimension(R.dimen.swap_control_button_height)),
                    1.0f));

            //buttonPP.setText("PLAY " + row);
            Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.swap_playback_play_button);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap,
                    (ImageScaling.px ((int) Math.floor(Shared.context.getResources().getDimension(R.dimen.swap_control_button_width)))),
                    (ImageScaling.px ((int) Math.floor(Shared.context.getResources().getDimension(R.dimen.swap_control_button_height)))),
                    true);
            Resources resource = getResources();
            buttonPP.setBackground(new BitmapDrawable(resource, scaledBitmap));

            // Make text not clip on small buttons
            buttonPP.setPadding(0, 25, 0, 0);

            buttonPP.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pauseSwapRowPlaybackButton(FINAL_ROW);
                }
            });

            tableRow.addView(buttonPP);
            playPauseButtons[row] = buttonPP;
        }
    }

    @Override
    public void onClick (View v) {}

    public void pauseSwapRowPlaybackButton (int activeRow) {
        Log.d (TAG, "method pauseSwapRowPlaybackButton");

        Button currentPlayPauseButton = playPauseButtons[activeRow];
        if (Audio.OFF) {
            Audio.OFF = false;
            currentPlayPauseButton.setBackgroundResource(R.drawable.swap_playback_pause_button);
            //TODO playback the audio
            Shared.eventBus.notify(new SwapPlayPauseRowAudioEvent(activeRow));
        }
        else {
            Audio.OFF = true;
            currentPlayPauseButton.setBackgroundResource(R.drawable.swap_playback_play_button);
            //TODO stop audio playback
            Shared.eventBus.notify(new SwapPlayPauseRowAudioEvent(activeRow));
        }
    }

    public void resetSwapRowPlaybackButton (int activeRow) {
        Log.d (TAG, "method resetSwapRowPlaybackButton");

        Button currentRestartButton = resetPlaybackButtons[activeRow];
        Audio.OFF = true;
        currentRestartButton.setAlpha(0.5f);
        //TODO stop playback and reset cursor if necessary?
        Shared.eventBus.notify(new SwapResetRowAudioEvent(activeRow));
    }
}