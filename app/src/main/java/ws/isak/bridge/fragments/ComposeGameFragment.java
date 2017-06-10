package ws.isak.bridge.fragments;

import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import ws.isak.bridge.R;
import ws.isak.bridge.common.Audio;
import ws.isak.bridge.common.Shared;
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
    private ImageButton mComposePlayButton;
    private ImageButton mComposePauseButton;
    private Button mComposeFinishedButton;

    private ComposeLibraryView mComposeLibraryView;
    private ComposeTrackerBoardView mComposeTrackerView;

    boolean playButtonPressed;
    boolean pauseButtonPressed;
    boolean stopButtonPressed;

    private ArrayList<MediaPlayer> preppedColumnAudio;
    private ArrayList<Integer> prepColAudioIDs;
    private ArrayList<MediaPlayer> playingColumnAudio;

    private long cellDurMillis = 2120;

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
        mComposePlayButton = (ImageButton) view.findViewById(R.id.compose_game_play_button);
        mComposePauseButton = (ImageButton) view.findViewById(R.id.compose_game_pause_button);
        mComposeFinishedButton = (Button) view.findViewById(R.id.compose_game_finished_button);

        //on click listeners
        Log.d(TAG, "method onCreateView: control button listeners...");
        mComposeStopButton.setOnClickListener(this);
        mComposePlayButton.setOnClickListener(this);
        mComposePauseButton.setOnClickListener(this);
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

        return view;
    }

    //enumerate the commands that the mediaPlayers can respond to on their threads
    private enum mediaPlayerCommands {
        START,
        STOP,
        PAUSE
    }

    @Override
    public void onClick(View view) {
        mediaPlayerCommands curCommand;

        //Log.d(TAG, "method onClick");
        switch (view.getId()) {
            case R.id.compose_game_play_button:
                Log.d(TAG, "method onClick: play button pressed");
                if (!Shared.userData.getCurComposeGameData().isGameStarted()) {
                    Toast.makeText(Shared.context, "Place a Sample On The Board To Play", Toast.LENGTH_SHORT).show();
                    break;
                }
                else {
                    playButtonPressed = true;
                    pauseButtonPressed = false;
                    stopButtonPressed = false;
                    curCommand = mediaPlayerCommands.START;
                    final mediaPlayerCommands cmd = curCommand;
                    final Thread audioThread = new Thread () {
                        @Override
                        public void run() {
                            Log.i (TAG, "method onClick: play button clicked: launching audioThread");
                            PlayBackTrackerAudio(cmd);
                        }
                    };
                    audioThread.start();
                    break;
                }
            case R.id.compose_game_pause_button:
                Log.d(TAG, "method onClick: pause button pressed");
                if (!Shared.userData.getCurComposeGameData().isGameStarted()) {
                    Toast.makeText(Shared.context, "Place a Sample On The Board To Play", Toast.LENGTH_SHORT).show();
                    break;
                }
                else {
                    playButtonPressed = false;
                    pauseButtonPressed = true;
                    stopButtonPressed = false;
                    curCommand = mediaPlayerCommands.PAUSE;
                    PlayBackTrackerAudio(curCommand);
                    break;
                }
            case R.id.compose_game_stop_button:
                Log.d(TAG, "method onClick: stop button pressed");
                if (!Shared.userData.getCurComposeGameData().isGameStarted()) {
                    Toast.makeText(Shared.context, "Place a Sample On The Board To Play", Toast.LENGTH_SHORT).show();
                    break;
                }
                else {
                    playButtonPressed = false;
                    pauseButtonPressed = false;
                    stopButtonPressed = true;
                    curCommand = mediaPlayerCommands.STOP;
                    PlayBackTrackerAudio(curCommand);
                    break;
                }
            case R.id.compose_game_finished_button:
                //TODO Finished button pressed - end game, store data as needed to database - go to game select screen?
                break;
        }
    }

    //Method PlayBackTrackerAudio plays the audio files currently on the tracker board.  This is done
    //as a set of numRow threads that concurrently play the files in a given column.  First a column
    //is processed, then as it plays back, the next column is prepped.  Between each column, a check
    //is sent to see the current state of the Playback buttons.
    //
    private void PlayBackTrackerAudio(mediaPlayerCommands curCommand) {
        switch (curCommand) {
            case START:
                Log.d (TAG, "method PlayBackTrackerAudio: case curCommand: " + curCommand);

                //iterate on this until another button is pressed
                while (playButtonPressed) {

                    Log.i (TAG, "method PlayBackTrackerAudio: playButtonPressed: " + playButtonPressed +
                            " | ITERATING OVER COLUMNS: curCol: " + Shared.userData.getCurComposeGameData().getCurPlayBackCol());

                    //[1] if this is the first column on the board (either game start or after STOP has reset)
                    if (Shared.userData.getCurComposeGameData().getCurPlayBackCol() == 0 &&
                            Shared.userData.getCurComposeGameData().getNextPlayBackCol() == 0 &&
                            !Audio.isPlaying) {

                        //DebugState(" START [1] ");

                        //[1.1] if there are no samples in the first column
                        if (GetNumActiveSamplesInCurColumn() == 0) {
                            //increment to and process nextCol - from cold start only
                            Shared.userData.getCurComposeGameData().setNextPlayBackCol();
                            preppedColumnAudio = PrepNextColumnAudio();

                            DebugState(" PLAYING [1.1] SILENCE ");
                            PlaySilentColumn();

                            Shared.userData.getCurComposeGameData().setCurPlayBackCol (Shared.userData.getCurComposeGameData().getNextPlayBackCol());
                            playingColumnAudio = preppedColumnAudio;
                            Shared.userData.getCurComposeGameData().setNextPlayBackCol();
                            preppedColumnAudio = null;
                        }
                        //[1.2] if there are samples in the first column to play
                        else {
                            preppedColumnAudio = PrepNextColumnAudio();                     //next column is still curColumn
                            Shared.userData.getCurComposeGameData().setNextPlayBackCol();   //update nextPlaybackCol so it leads curCol
                            playingColumnAudio = preppedColumnAudio;

                            DebugState(" PLAYING [1.2] ");
                            PlayPreppedColAudio();

                            preppedColumnAudio = PrepNextColumnAudio();
                            Shared.userData.getCurComposeGameData().setCurPlayBackCol (Shared.userData.getCurComposeGameData().getNextPlayBackCol());
                            playingColumnAudio = preppedColumnAudio;
                            Shared.userData.getCurComposeGameData().setNextPlayBackCol();
                            preppedColumnAudio = null;
                        }
                    }
                    //[2] else this is not the first column - either playback continues, or starting from PAUSE
                    else if (!Audio.isPlaying) {

                        DebugState(" CHECK [2] - UPDATE playingColumnAudio && UPDATE preppedColumnAudio");

                        //[2.1] if a the next playback column has been prepared
                        if (PlaybackColReady()) {

                            //DebugState(" START [2.1] ");

                            // [2.1.1] if when the next column has been prepared there are no samples to play in the current column
                            if (GetNumActiveSamplesInCurColumn() == 0) {
                                preppedColumnAudio = PrepNextColumnAudio();
                                DebugState(" PLAYING [2.1.1] ");

                                PlaySilentColumn();


                                Shared.userData.getCurComposeGameData().setCurPlayBackCol (Shared.userData.getCurComposeGameData().getNextPlayBackCol());
                                playingColumnAudio = preppedColumnAudio;
                                Shared.userData.getCurComposeGameData().setNextPlayBackCol();
                                preppedColumnAudio = null;
                            }
                            // [2.1.2] if there are samples to playback
                            else {
                                preppedColumnAudio = PrepNextColumnAudio();

                                DebugState(" PLAYING [2.1.2] ");

                                Log.d(TAG, "method PlayBackTrackerAudio: Ready to PlayPreppedColAudio");
                                PlayPreppedColAudio();

                                Shared.userData.getCurComposeGameData().setCurPlayBackCol (Shared.userData.getCurComposeGameData().getNextPlayBackCol());
                                playingColumnAudio = preppedColumnAudio;
                                Shared.userData.getCurComposeGameData().setNextPlayBackCol();
                                preppedColumnAudio = null;
                            }
                        }
                        // [2.2] THIS SHOULD NEVER TRIGGER
                        else {

                            DebugState (" ERROR [2.2] ");

                            Log.e (TAG, "[2.2] ERROR");
                            try { Thread.sleep (2500); }            //Debug delay
                            catch (InterruptedException e) { e.printStackTrace(); }
                        }
                    }
                }
                break;
            case PAUSE:
                Log.d (TAG, "method PlayBackTrackerAudio: case curCommand: " + curCommand);


                break;
            case STOP:
                Log.d (TAG, "method PlayBackTrackerAudio: case curCommand: " + curCommand);
                Shared.userData.getCurComposeGameData().setCurPlayBackCol(0);
                Shared.userData.getCurComposeGameData().setNextPlayBackCol(0);
                preppedColumnAudio = null;
                break;
        }

    }

    private void PlaySilentColumn () {
        Log.d (TAG, "method PlaySilentColumn: column <" + Shared.userData.getCurComposeGameData().getCurPlayBackCol() +
                "> has no samples, playing silence...");
        //play silence
        final MediaPlayer playSilence = new MediaPlayer();
        playSilence.create(Shared.context, R.raw.silence2);     //FIXME revert to R.raw.silence post debug

        playSilence.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                Audio.setIsAudioPlaying(true);
                Log.d (TAG, "method PlaySilentColumn: onPrepared: isAudioPlaying: " + Audio.getIsAudioPlaying());
                playSilence.start();

            }
        });

        playSilence.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                //notify the completion of playback here
                Audio.setIsAudioPlaying(false);
                Log.d (TAG, "method PlaySilentColumn: onCompletion: isAudioPlaying: " + Audio.getIsAudioPlaying());
                playSilence.release();
                playSilence.reset();
            }
        });
    }

    private void PlayPreppedColAudio () {
        Log.d(TAG, "method PlayPreppedColAudio: MediaPlayers in playingColumnAudio: " + playingColumnAudio);
        //TODO all the playback code goes here
        //
        //

        playingColumnAudio = null;
        try { Thread.sleep (1000); }
        catch (InterruptedException e) {e.printStackTrace(); }
    }

    //this method will return true when the call to PrepNextColumnAudio has processed the same number
    //of samples as returned by GetNumActiveSamplesInCurColumn (because by the time it is called,
    //nextCol has been set to curCol
    private boolean PlaybackColReady() {

        boolean ready = false;

        Log.i (TAG, "method PlaybackColReady: playingColumnAudio.size(): " + playingColumnAudio.size() +
                " | GetNumActiveSamplesInCurColumn: " + GetNumActiveSamplesInCurColumn());
        //DebugState(" From PlaybackColReady:...");
        if (playingColumnAudio.size() == GetNumActiveSamplesInCurColumn()) {
            ready = true;
        }
        Log.i (TAG, "method PlaybackColReady: ready: " + ready);
        return ready;
    }

    private int GetNumActiveSamplesInCurColumn () {
        int numSamps = 0;
        int targetCol = Shared.userData.getCurComposeGameData().getCurPlayBackCol();

        for (int i = 0; i < Shared.userData.getCurComposeGameData().getGameRows(); i++) {
            if (Shared.userData.getCurComposeGameData().retrieveDataInTrackerCellsArray(i, targetCol)
                    .getCellSampleData() != null) {
                numSamps++;
            }
        }
        Log.i (TAG, "method GetNumActiveSamplesInColumn: numSamps: [" + numSamps +
                "] in targetCol: " + targetCol);
        return numSamps;
    }

    private int GetNumActiveSamplesInNextColumn () {
        int numSamps = 0;
        int nextCol = Shared.userData.getCurComposeGameData().getNextPlayBackCol();

        for (int i = 0; i < Shared.userData.getCurComposeGameData().getGameRows(); i++) {
            if (Shared.userData.getCurComposeGameData().retrieveDataInTrackerCellsArray(i, nextCol).getCellSampleData() != null) {
                numSamps++;
            }
        }
        Log.d (TAG, "method GetNumActiveSamplesInNextColumn: numSamps: " + numSamps + " in nextCol: " + nextCol);
        return numSamps;
    }

    private ArrayList<MediaPlayer> PrepNextColumnAudio() {

        Shared.userData.getCurComposeGameData().setNumCellsInColPreparedForPlayback(0);

        Log.d (TAG, "method PrepNextColumnAudio: @Start: curCol Being Prepped: " +
                Shared.userData.getCurComposeGameData().getNextPlayBackCol() +
                " | reset NumCellsInColPreparedForPlayback to zero: " +
                Shared.userData.getCurComposeGameData().getNumCellsInColPreparedForPlayback() + " = 0 ");

        prepColAudioIDs = getColAudioFileIDs(Shared.userData.getCurComposeGameData().getNextPlayBackCol());
        Log.d (TAG, "method PrepNextColumnAudio: prepColAudioIDs: " + prepColAudioIDs);
        ArrayList <MediaPlayer> prepColumnAudio = new ArrayList<MediaPlayer>(0);
        for (int i = 0; i < prepColAudioIDs.size(); i++) {
            MediaPlayer mp = new MediaPlayer();
            Log.d (TAG, "method PrepNextColumnAudio: instantiated new MediaPlayer: " + mp);
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            AssetFileDescriptor afd = Shared.context.getResources().openRawResourceFd(prepColAudioIDs.get(i));
            if (afd == null) {
                return null;
            }
            try {
                mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                afd.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    Shared.userData.getCurComposeGameData().incrementNumCellsInColPreparedForPlayback();
                    Log.d (TAG, "method PrepNextColumnAudio: onPrepared: numCellsInColPreparedForPlayback: " +
                        Shared.userData.getCurComposeGameData().getNumCellsInColPreparedForPlayback() +
                        " | FINISHED PREPARING MediaPlayer: " + mediaPlayer);
                }
            });

            //mp.prepareAsync(); //FIXME - since raw files on device is using prepare ok?
            try { mp.prepare(); }
            catch (IOException e) { e.printStackTrace(); }

            Log.d (TAG, "method PrepNextColumnAudio: called prepare on mp: " + mp);
            ///*
            mp.setOnCompletionListener(new OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.d (TAG, "method PrepNextColumnAudio: onCompletion: mp: " + mp);
                    mp.reset();
                    mp.release();
                    Log.d (TAG, "method PrepNextColumnAudio: onCompletion: mp RELEASE and RESET");
                }
            });
            //*/
            prepColumnAudio.add(mp);
        }

        //FIXME REMOVE while (Shared.userData.getCurComposeGameData().getNumCellsInColPreparedForPlayback() < GetNumActiveSamplesInNextColumn()) {
        //check that all samples added to list have prepared
        while (Shared.userData.getCurComposeGameData().getNumCellsInColPreparedForPlayback() < prepColumnAudio.size()) {
            try { Thread.sleep (50); }
            catch (InterruptedException e) { e.printStackTrace(); }
        }

        Log.i (TAG, "method PrepNextColumnAudio: RETURNING prepColumnAudio: " + prepColumnAudio +
            " | PREPARED: [" + GetNumActiveSamplesInNextColumn() + "] SAMPLES" +
            " | getNumCellsInColPreparedForPlayback: " + Shared.userData.getCurComposeGameData().getNumCellsInColPreparedForPlayback() );
        return prepColumnAudio;
    }

    private ArrayList<Integer> getColAudioFileIDs(int targetCol) {
        //debugging code - check state of tracker board
        //Shared.userData.getCurComposeGameData().debugDataInTrackerCellsArray("ComposeGameFragment: getAudioFilesInColByID");

        //for debugging, local array of string for audio resources to be printed before return of method
        ArrayList<String> audioResourceNames = new ArrayList<String>(0);

        //return this list
        ArrayList<Integer> audioResourceIDs = new ArrayList<Integer>(0);

        Log.v (TAG, "method getColAudioFileIDs: iterating over: " + Shared.userData.getCurComposeGameData().getGameRows() + " rows");

        //iterate over the cells in the target column
        for (int i = 0; i < Shared.userData.getCurComposeGameData().getGameRows(); i++) {

            Log.v(TAG, "method getColAudioFileIDs: row i: " + i + " | col target: " + targetCol);

            Log.v(TAG, "method getColAudioFileIDs: Shared.userData.getCurComposeGameData().retrieveDataInTrackerCellsArray(i, targetCol).getCellSampleData(): " +
                    Shared.userData.getCurComposeGameData().retrieveDataInTrackerCellsArray(i, targetCol).getCellSampleData());
            if (Shared.userData.getCurComposeGameData().retrieveDataInTrackerCellsArray(i, targetCol).getCellSampleData() != null) {

                Log.v(TAG, "method getColAudioFileIDs: Shared.userData.getCurComposeGameData().retrieveDataInTrackerCellsArray(i, targetCol).getCellSampleData().getAudioURI(): " +
                        Shared.userData.getCurComposeGameData().retrieveDataInTrackerCellsArray(i, targetCol).getCellSampleData().getAudioURI());

                String cellAudioURI = Shared.userData.getCurComposeGameData().
                        retrieveDataInTrackerCellsArray(i, targetCol).getCellSampleData().getAudioURI();

                Log.v(TAG, "method getColAudioFileIDs: cellAudioURI: " + cellAudioURI);

                if (cellAudioURI != null) {
                    Log.v(TAG, "method getColAudioFileIDs: adding sample i: " + i + " to lists");
                    audioResourceNames.add(cellAudioURI.substring(URI_AUDIO.length()));
                    Log.v(TAG, "method getColAudioFileIDs: " + audioResourceNames.size() + " audioResourceNames recorded");
                    audioResourceIDs.add(Shared.context.getResources().getIdentifier(audioResourceNames.get(audioResourceNames.size() - 1), "raw", Shared.context.getPackageName()));
                }
            }
        }
        Log.d(TAG, "method getColAudioFileIDs: ..... AudioResourceNames and IDs in Target Row: Num Samples: " + audioResourceNames.size() + " .....");
        for (int i = 0; i < audioResourceNames.size(); i++) {
            Log.d(TAG, "method getColAudioFileIDs: ..... load column: " + Shared.userData.getCurComposeGameData().getCurPlayBackCol() +
                    " | resource @ row i: " + i +
                    " | Name: " + audioResourceNames.get(i) +
                    " | ID: " + audioResourceIDs.get(i));
        }
        return audioResourceIDs;
    }

    //debug state of variables in PlayBackTrackerAudio [x.x]
    private void DebugState (String opt) {
        //debug: review state at end of loop
        Log.i(TAG, "method DebugState: " + opt +
                " \n| curCol: " + Shared.userData.getCurComposeGameData().getCurPlayBackCol() +
                " | nextCol: " + Shared.userData.getCurComposeGameData().getNextPlayBackCol() +
                " | curCol numSamps: " + GetNumActiveSamplesInCurColumn() +
                " | nextCol numSamps: " + GetNumActiveSamplesInNextColumn() +
                " \n| cur column playingColumnAudio: " + playingColumnAudio +
                " | next column preppedColumnAudio: " + preppedColumnAudio + "\n");
    }

    /*
    public static MediaPlayer deepCloneMediaPlayer (MediaPlayer mp) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(mp);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (MediaPlayer) ois.readObject();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    */
}