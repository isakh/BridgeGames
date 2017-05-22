package ws.isak.bridge.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import ws.isak.bridge.R;
import ws.isak.bridge.ui.ComposeScrollingImagesView;
import ws.isak.bridge.ui.ComposeTrackerBoardView;

/*
 *
 *
 * @author isak
 */


public class ComposeGameFragment extends BaseFragment implements View.OnClickListener {

    public final String TAG = "ComposeGameFragment";
    public static String URI_AUDIO = "raw://";

    private ImageButton mComposeStopButton;
    private ImageButton mComposePauseButton;
    private ImageButton mComposePlayButton;

    private ComposeScrollingImagesView mComposeScrollingImagesView;
    private ComposeTrackerBoardView mComposeTrackerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "method onCreateView");
        //create the view for the compose game fragment
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.compose_game_fragment, container, false);
        view.setClipChildren(false);
        ((ViewGroup) view.findViewById(R.id.compose_game_board)).setClipChildren(false);

        //the compose game audio controls
        mComposeStopButton = (ImageButton) view.findViewById(R.id.compose_game_stop_button);
        mComposePauseButton = (ImageButton) view.findViewById(R.id.compose_game_pause_button);
        mComposePlayButton = (ImageButton) view.findViewById(R.id.compose_game_play_button);
        mComposeStopButton.setOnClickListener(this);
        mComposePauseButton.setOnClickListener(this);
        mComposePlayButton.setOnClickListener(this);

        //the scrolling images view: the library of sample images
        mComposeScrollingImagesView = ComposeScrollingImagesView.fromXml(getActivity().getApplicationContext(), view);
        FrameLayout composeScrollingImagesFrameLayout = (FrameLayout) view.findViewById(R.id.compose_game_scrolling_images);
        composeScrollingImagesFrameLayout.addView(mComposeScrollingImagesView);
        composeScrollingImagesFrameLayout.setClipChildren(false);

        //the swap game board: the tracker layout //TODO build a draggable grid view?
        mComposeTrackerView = ComposeTrackerBoardView.fromXml(getActivity().getApplicationContext(), view);
        FrameLayout composeTrackerFrameLayout = (FrameLayout) view.findViewById(R.id.compose_game_tracker_board);
        composeTrackerFrameLayout.addView(mComposeTrackerView);
        composeTrackerFrameLayout.setClipChildren(false);

        //build the library frame


        //build the tracker frame


        //instantiate all of the event listeners

        return view;
    }

    @Override
    public void onClick (View view) {
        //TODO handle the compose audio control buttons here
    }

}
