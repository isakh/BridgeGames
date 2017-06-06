package ws.isak.bridge.fragments;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;

import ws.isak.bridge.R;
import ws.isak.bridge.common.Audio;
import ws.isak.bridge.common.Shared;
import ws.isak.bridge.utils.AudioPlayThread;
import ws.isak.bridge.ui.ComposeLibraryView;
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
    private Button mComposeFinishedButton;

    private ComposeLibraryView mComposeLibraryView;
    private ComposeTrackerBoardView mComposeTrackerView;

    boolean playButtonPressed;
    boolean pauseButtonPressed;
    boolean stopButtonPressed;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "method onCreateView");
        //create the view for the compose game fragment
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.compose_game_fragment, container, false);
        view.setClipChildren(false);
        ((ViewGroup) view.findViewById(R.id.compose_game_board)).setClipChildren(false);

        //the compose game audio controls
        Log.d(TAG, "method onCreateView: control buttons instantiated");
        mComposeStopButton = (ImageButton) view.findViewById(R.id.compose_game_stop_button);
        mComposePauseButton = (ImageButton) view.findViewById(R.id.compose_game_pause_button);
        mComposePlayButton = (ImageButton) view.findViewById(R.id.compose_game_play_button);
        mComposeFinishedButton = (Button) view.findViewById(R.id.compose_game_finished_button);

        //on click listeners
        Log.d(TAG, "method onCreateView: control button listeners...");
        mComposeStopButton.setOnClickListener(this);
        mComposePauseButton.setOnClickListener(this);
        mComposePlayButton.setOnClickListener(this);
        mComposeFinishedButton.setOnClickListener(this);

        //a vertical scrolling images view: the library of sample images
        Log.d(TAG, "method onCreateView: create a frame for the library");
        mComposeLibraryView = ComposeLibraryView.fromXml(getActivity().getApplicationContext(), view);
        FrameLayout composeScrollingImagesFrameLayout = (FrameLayout) view.findViewById(R.id.compose_game_library);
        composeScrollingImagesFrameLayout.addView(mComposeLibraryView);
        composeScrollingImagesFrameLayout.setClipChildren(false);

        //the swap game board: the tracker layout //
        Log.d(TAG, "method onCreateView: create a frame for the tracker");
        mComposeTrackerView = ComposeTrackerBoardView.fromXml(getActivity().getApplicationContext(), view);
        FrameLayout composeTrackerFrameLayout = (FrameLayout) view.findViewById(R.id.compose_game_tracker);
        composeTrackerFrameLayout.addView(mComposeTrackerView);
        composeTrackerFrameLayout.setClipChildren(false);

        //build the library frame
        Log.d(TAG, "method onCreateView: populate the library frame");
        mComposeLibraryView.populateSampleLibrary();

        //build the tracker frame
        Log.d(TAG, "method onCreateView: build the tracker frame");
        mComposeTrackerView.constructTrackerBoard();

        //instantiate all of the event listeners
        //TODO LibrarySampleSelectedEvent - do we need this since it is handled by the onClick of Library ImageView
        //TODO SamplePlacedOnTrackerEvent - do we need this since it is handled by onCLick of Tracker cell ImageView
        //TODO SampleRemovedFromTrackerEvent    - handled by onLongClick on Tracker cell ImageView
        //TODO ComposePlaybackAudioEvent    - do this as an event? which is called by the onClicksListeners overriden below

        return view;
    }

    //enumerate the commands that the mediaPlayers can respond to on their threads
    private enum mediaPlayerCommands {
        START,
        STOP,
        PAUSE
    }

    @Override
    public void onClick (View view) {
        mediaPlayerCommands curCommand;

        Log.d(TAG, "method onClick");
        switch (view.getId()) {
            case R.id.compose_game_play_button:
                Log.d(TAG, "method onClick: play button pressed");
                playButtonPressed = true;
                pauseButtonPressed = false;
                stopButtonPressed = false;
                curCommand = mediaPlayerCommands.START;
                PlayBackTrackerAudio(curCommand);
                break;
            case R.id.compose_game_pause_button:
                Log.d(TAG, "method onClick: pause button pressed");
                playButtonPressed = false;
                pauseButtonPressed = true;
                stopButtonPressed = false;
                curCommand = mediaPlayerCommands.PAUSE;
                PlayBackTrackerAudio(curCommand);
                break;
            case R.id.compose_game_stop_button:
                Log.d(TAG, "method onClick: stop button pressed");
                playButtonPressed = false;
                pauseButtonPressed = false;
                stopButtonPressed = true;
                curCommand = mediaPlayerCommands.STOP;
                PlayBackTrackerAudio(curCommand);
                break;
            case R.id.compose_game_finished_button:
                //TODO Finished button pressed - end game, store data as needed to database - go to game select screen?
                break;
        }
    }

    //Method PlayBackTrackerAudio plays the audio files currently on the tracker board.  This is done
    //as a set of numRow threads that concurrently play the files in a given column, when the column
    //is completed, the next column is processed.
    //
    private void PlayBackTrackerAudio (mediaPlayerCommands curCommand) {
        Log.d(TAG, "method PlayBackTrackerAudio: curCommand: " + curCommand);
        //while playback is true iterate through the columns and get the audio to play
        while (curCommand == mediaPlayerCommands.START &&
                Shared.userData.getCurComposeGameData().getNumCellsInColLeftToPlayBack() == 0) {                                 //user has requested playback
            Log.d (TAG, "method PlayBackTrackerAudio: curPlaybackCol: " + Shared.userData.getCurComposeGameData().getCurPlayBackCol());
            PlayTrackerColumnAudio(curCommand);
            //TODO - check that column playback has completed before incrementing curCol for playback
            int curPlaybackCol = ((Shared.userData.getCurComposeGameData().getCurPlayBackCol() + 1) % Shared.userData.getCurComposeGameData().getGameCols());
            Shared.userData.getCurComposeGameData().setCurPlayBackCol(curPlaybackCol);
        }
    }

    private void PlayTrackerColumnAudio (mediaPlayerCommands command) {

        Log.d(TAG, "method PlayTrackerColumnAudio");
        //keep track of current playback column on tracker
        int curCol = Shared.userData.getCurComposeGameData().getCurPlayBackCol();
        Log.d(TAG, "method PlayTrackerColumnAudio: curCol: " + curCol + " | command: " + command);
        //check how many cells contain samples
        int numActiveCellsInCol = 0;
        for (int i = 0; i < Shared.userData.getCurComposeGameData().getGameRows(); i++) {
            if (Shared.userData.getCurComposeGameData().retrieveDataInTrackerCellsArray(i, curCol).getCellSampleData() != null) {
                numActiveCellsInCol++;
            }
        }
        if (numActiveCellsInCol == 0) {
            Log.d (TAG, "method PlayTrackerColumnAudio: current column numActiveCells: " + numActiveCellsInCol + "| Check is 0");
            //TODO - get tracker to wait for one cell duration (2.12sec) before playing the next column
        }
        else {
            Shared.userData.getCurComposeGameData().setNumCellsInColLeftToPlayBack(numActiveCellsInCol);
            Log.d(TAG, "method PlayTrackerColumnAudio: numActiveCellsInCol: " + numActiveCellsInCol);
            //create the players for each cell with a sample in the current column
            MediaPlayer[] players = new MediaPlayer[numActiveCellsInCol];
            Log.d(TAG, "method PlayTrackerColumnAudio: players allocated: " + players);
            //make a list of the IDs associated with the audio on the cells
            ArrayList<Integer> audioResourceIdList = getAudioFilesInColByID(curCol);
            Log.d(TAG, "method PlayTrackerColumnAudio: audioResourceIdList: " + audioResourceIdList);

            //assign sources to each of the players
            for (int j = 0; j < numActiveCellsInCol; j++) {
                AssetFileDescriptor afd = Shared.context.getResources().openRawResourceFd(audioResourceIdList.get(j));
                try {
                    players[j].setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                    Log.d(TAG, "method PlayTrackerColumnAudio: source set: players[j].getDuration: " + players[j].getDuration());
                    afd.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                players[j].setOnCompletionListener(new OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        //TODO add a counter to ensure that all mediaplayers stop before starting the next column
                        Log.d(TAG, "method PlayTrackerColumnAudio: players[j] onCompletion");
                        int numActiveCells = Shared.userData.getCurComposeGameData().getNumCellsInColLeftToPlayBack();
                        numActiveCells--;
                        Shared.userData.getCurComposeGameData().setNumCellsInColLeftToPlayBack(numActiveCells);
                    }
                });
            }
            //
            Log.d(TAG, "method PlayTrackerColumnAudio: calling playSyncedAudio");
            playSyncedAudio(players, command);
        }
    }

    private ArrayList<Integer> getAudioFilesInColByID(int targetCol) {
        ArrayList <Integer> audioResourceIDs = new ArrayList<Integer>(0);
        //for debugging, local array of string for audio resources to be printed before return of method
        ArrayList <String> audioResourceNames = new ArrayList <String>(0);
        //iterate over the cells in the target column
        for (int i = 0; i < Shared.userData.getCurComposeGameData().getGameRows(); i++) {
            String cellAudioURI = Shared.userData.getCurComposeGameData().retrieveDataInTrackerCellsArray(i, targetCol).getCellSampleData().getAudioURI();
            if (cellAudioURI != null) {
                audioResourceNames.add(cellAudioURI);
                int cellAudioResourceID = Shared.context.getResources().getIdentifier(cellAudioURI, "raw", Shared.context.getPackageName());
                audioResourceIDs.add(cellAudioResourceID);
            }
        }
        Log.d (TAG, "..... AudioResourceNames in Target Row: .....");
        for (int i = 0; i < audioResourceNames.size(); i++) {
            Log.d (TAG, "..... resource @ i: " + i + " : " + audioResourceNames.get(i));
        }
        return audioResourceIDs;
    }

    /**
     * Threads to execute synced commands for the current players associated with all of the
     * cells in a tracker column
     */
    public void playSyncedAudio (MediaPlayer[] players, mediaPlayerCommands command) {
        Log.d(TAG, "method playSyncedAudio");
        //set up a barrier that waits for all of the players to be ready
        final CyclicBarrier commandBarrier = new CyclicBarrier(players.length);
        //iterate over the players and add a new thread for each
        for (int i = 0; i < players.length; i++) {
            Log.d(TAG, "method playSyncedAudio: launching thread: command: " + command);
            new Thread(new SyncedCommandService(commandBarrier, players[i], command)).start();
        }
    }

    //Inner class that starts a given media player synchronously with other threads
    private class SyncedCommandService implements Runnable {
        private final CyclicBarrier mCommandBarrier;
        private mediaPlayerCommands mCommand;
        private MediaPlayer mMediaPlayer;

        public SyncedCommandService(CyclicBarrier barrier, MediaPlayer player, mediaPlayerCommands command) {
            mCommandBarrier = barrier;
            mMediaPlayer = player;
            mCommand = command;
        }

        @Override
        public void run() {
            try {
                mCommandBarrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }

            switch (mCommand) {
                case START:
                    Log.d (TAG, "class SyncedCommandService: Overriding run: mCommand: " + mCommand);
                    mMediaPlayer.start();
                    break;
                case STOP:
                    Log.d (TAG, "class SyncedCommandService: Overriding run: mCommand: " + mCommand);
                    mMediaPlayer.stop();
                    break;
                case PAUSE:
                    Log.d (TAG, "class SyncedCommandService: Overriding run: mCommand: " + mCommand);
                    mMediaPlayer.pause();
                    break;
                default:
                    break;
            }
        }
    }
}