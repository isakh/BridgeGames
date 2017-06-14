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
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import ws.isak.bridge.R;
import ws.isak.bridge.common.Audio;
import ws.isak.bridge.common.Shared;
import ws.isak.bridge.database.ComposeGameDataORM;
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
    boolean finishedButtonPressed = false;

    private ArrayList<MediaPlayer> preppedColumnAudio;
    private ArrayList<Integer> prepColAudioIDs;
    private ArrayList<MediaPlayer> playingColumnAudio;

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
                finishedButtonPressed = true;

                //Finished button pressed - end game, store data as needed to database - go to game select screen?
                PlayBackTrackerAudio(mediaPlayerCommands.STOP);

                // insert current swapGameData into database
                ComposeGameDataORM.insertComposeGameData(Shared.userData.getCurComposeGameData());

                FinishComposeGame();
                break;
        }
    }

    //Method PlayBackTrackerAudio plays the audio files currently on the tracker board.  This is done
    //as a set of numRow threads that concurrently play the files in a given column.  First a column
    //is processed.  Between each column, a check is sent to see the current state of the Playback buttons.
    private void PlayBackTrackerAudio(mediaPlayerCommands curCommand) {
         if (finishedButtonPressed == false) {
             switch (curCommand) {
                 case START:
                     Log.d(TAG, "method PlayBackTrackerAudio: case curCommand: " + curCommand);

                     //iterate on this until another button is pressed
                     while (playButtonPressed) {

                         //[0] if audio is playing back, wait for it to finish
                         if (Audio.getIsAudioPlaying("PlayBackTrackerAudio: play - skip Thread.sleep and continue")) {
                             try {
                                 Thread.sleep(2000);
                             } catch (InterruptedException e) {
                                 e.printStackTrace();
                             }
                         }

                         //[1] else, if no audio is playing, process the current column on the board
                         else {
                             int cCNumSamps = GetNumActiveSamplesInCurColumn();
                             Log.d(TAG, "method PlayBackTrackerAudio: playButtonPressed: " + playButtonPressed +
                                     " | getIsAudioPlaying: " + Audio.isPlaying +
                                     " SHOULD BE FALSE | ITERATING OVER COLUMNS: curCol: " +
                                     Shared.userData.getCurComposeGameData().getCurPlayBackCol() +
                                     " | num active samples in curCol: " + cCNumSamps);

                             //DebugState(" START [1] ");

                             //[1.1] if there are no samples in the  column
                             if (cCNumSamps == 0) {

                                 DebugState(" PLAYING [1.1] SILENCE ");
                                 PlaySilentColumn();
                                 Shared.userData.getCurComposeGameData().incrementCurPlayBackCol();

                                 DebugState("*** FINISHED [1.1] - waiting for silence to complete playback");
                             }
                             //[1.2] if there are samples in the column to play
                             else {
                                 playingColumnAudio = PrepColumnAudio();

                                 DebugState(" PLAYING [1.2] ");
                                 PlayPreppedColAudio();
                                 Shared.userData.getCurComposeGameData().incrementCurPlayBackCol();

                                 DebugState("*** FINISHED [1.2] - waiting for sample(s) to complete playback");

                             }
                         }
                     }
                     break;
                 case PAUSE:
                     Log.d(TAG, "method PlayBackTrackerAudio: case curCommand: " + curCommand);


                     break;
                 case STOP:
                     Log.d(TAG, "method PlayBackTrackerAudio: case curCommand: " + curCommand);
                     Shared.userData.getCurComposeGameData().setCurPlayBackCol(0);
                     //Shared.userData.getCurComposeGameData().setNextPlayBackCol(0);
                     preppedColumnAudio = null;
                     break;
             }
         }
         else {
             Log.d (TAG, "method PlayBackTrackerAudio: finishedButtonPressed: " + finishedButtonPressed);
         }
    }

    private void PlaySilentColumn () {
        Audio.setIsAudioPlaying(true);
        Log.d (TAG, "method PlaySilentColumn: column <" + Shared.userData.getCurComposeGameData().getCurPlayBackCol() +
                "> has no samples, playing silence...");
        //play silence
        MediaPlayer playSilence = MediaPlayer.create(Shared.context, R.raw.silence);     //FIXME revert to R.raw.silence post debug

        playSilence.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                Log.d (TAG, "method PlaySilentColumn: onPrepared: isAudioPlaying: " + Audio.getIsAudioPlaying("PlaySilentColumn: onPrepared"));
                mediaPlayer.start();

            }
        });

        playSilence.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                //notify the completion of playback here
                Audio.setIsAudioPlaying(false);
                Log.d (TAG, "method PlaySilentColumn: onCompletion: isAudioPlaying: " + Audio.getIsAudioPlaying("PlaySilentColumn: onCompletion"));
                mediaPlayer.reset();
                mediaPlayer.release();
            }
        });
    }

    private void PlayPreppedColAudio () {
        Log.d(TAG, "***** method PlayPreppedColAudio: MediaPlayers in playingColumnAudio: " + playingColumnAudio);
        Log.d(TAG, "***** method PlayPreppedColAudio: getNumCellsInColPreparedForPlayback: " +
                Shared.userData.getCurComposeGameData().getNumCellsInColPreparedForPlayback());

        Shared.userData.getCurComposeGameData().setNumCellsInColLeftToPlayBack
                (Shared.userData.getCurComposeGameData().getNumCellsInColPreparedForPlayback());

        Log.w(TAG, "***** method PlayPreppedColAudio: just setNumCellsInColLeftToPlayback to getNumCellsPreparedForPlayback: "+
                + Shared.userData.getCurComposeGameData().getNumCellsInColPreparedForPlayback() +
                " | getNumCellsInColLeftForPlayback: " +
                Shared.userData.getCurComposeGameData().getNumCellsInColLeftToPlayBack());

        SyncPlaybackColumnAudio(playingColumnAudio);
    }

    //threads execute synced commands launching all of the samples' MediaPlayers
    public void SyncPlaybackColumnAudio(ArrayList<MediaPlayer> colPlayers) {
        final CyclicBarrier playbackBarrier = new CyclicBarrier(colPlayers.size());
        for (int i = 0; i < colPlayers.size(); i++) {
            Log.d (TAG, "method SyncPlaybackColumnAudio: current MediaPlayer: " + colPlayers.get(i) +
                    " | calling new Thread... ");
            new Thread(new SyncPlaybackCommand(playbackBarrier, colPlayers.get(i))).start();
        }
    }

    /* FIXME - remove, only useful if we are looking ahead to prep the next column
    //this method will return true when the call to PrepColumnAudio has processed the same number
    //of samples as returned by GetNumActiveSamplesInCurColumn (because by the time it is called,
    //nextCol has been set to curCol; it will also return true if playingColumnAudio is null because
    //there are no samples to be played back
    private boolean PlaybackColReady() {

        boolean ready = false;
        int cCNumSamps = GetNumActiveSamplesInCurColumn();

        if (playingColumnAudio != null) {
            Log.i(TAG, "method PlaybackColReady: playingColumnAudio.size(): " + playingColumnAudio.size());
        }
        Log.i (TAG, "method PlaybackColReady: GetNumActiveSamplesInCurColumn: " + GetNumActiveSamplesInCurColumn());
        //DebugState(" From PlaybackColReady:...");
        if (playingColumnAudio != null) {
            if (playingColumnAudio.size() == GetNumActiveSamplesInCurColumn()) {
                ready = true;
            }
        }
        else if (playingColumnAudio == null && cCNumSamps == 0) {
            ready = true;
        }
        Log.i (TAG, "method PlaybackColReady: ready: " + ready);
        return ready;
    }
    */

    private int GetNumActiveSamplesInCurColumn () {
        if (finishedButtonPressed == false) {
            int numSamps = 0;
            int targetCol = Shared.userData.getCurComposeGameData().getCurPlayBackCol();

            for (int i = 0; i < Shared.userData.getCurComposeGameData().getGameRows(); i++) {
                if (Shared.userData.getCurComposeGameData().retrieveDataInTrackerCellsArray(i, targetCol)
                        .getCellSampleData() != null) {
                    numSamps++;
                }
            }
            Log.d(TAG, "method GetNumActiveSamplesInColumn: RETURNS numSamps: [" + numSamps +
                    "] in targetCol: [" + targetCol + "]");
            return numSamps;
        }
        else {
            return -1;
        }
    }

    /* FIXME - remove only useful when pre-processing samples
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
    */

    private ArrayList<MediaPlayer> PrepColumnAudio() {

        Shared.userData.getCurComposeGameData().setNumCellsInColPreparedForPlayback(0);

        Log.d (TAG, "method PrepColumnAudio: @Start: curCol Being Prepped: " +
                Shared.userData.getCurComposeGameData().getCurPlayBackCol() +
                " | reset NumCellsInColPreparedForPlayback to zero: CHECK: " +
                Shared.userData.getCurComposeGameData().getNumCellsInColPreparedForPlayback() + " = 0 ");

        prepColAudioIDs = getColAudioFileIDs(Shared.userData.getCurComposeGameData().getCurPlayBackCol());
        Log.d (TAG, "method PrepColumnAudio: prepColAudioIDs: " + prepColAudioIDs);
        ArrayList <MediaPlayer> prepColumnAudio = new ArrayList<MediaPlayer>(0);
        for (int i = 0; i < prepColAudioIDs.size(); i++) {
            MediaPlayer mp = new MediaPlayer();
            Log.d (TAG, "method PrepColumnAudio: instantiated new MediaPlayer: " + mp);
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
                    Log.w (TAG, "***** method PrepColumnAudio: onPrepared: just incremented numCellsInColPreparedForPlayback: " +
                        Shared.userData.getCurComposeGameData().getNumCellsInColPreparedForPlayback() +
                        " | FINISHED PREPARING MediaPlayer: " + mediaPlayer);
                }
            });

            try { mp.prepare(); }       //FIXME - if we implement look-forward planning, can use prepareAsync
            catch (IOException e) { e.printStackTrace(); }

            Log.d (TAG, "method PrepColumnAudio: called prepare on mp: " + mp);
            ///*
            mp.setOnCompletionListener(new OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.d (TAG, "*** method PrepColumnAudio: onCompletion: mp: " + mp);
                    Shared.userData.getCurComposeGameData().decrementNumCellsInColLeftToPlayback();
                    Log.w (TAG, "***** method PrepColumnAudio: onCompletion: just decremented: getNumCellsInColLeftToPlayBack: " +
                            Shared.userData.getCurComposeGameData().getNumCellsInColLeftToPlayBack());
                    mp.reset();
                    mp.release();
                    Log.d (TAG, "method PrepColumnAudio: onCompletion: mp RELEASE and RESET");

                    //check if this is the final sample to complete
                    if (Shared.userData.getCurComposeGameData().getNumCellsInColLeftToPlayBack() == 0) {
                        Audio.setIsAudioPlaying(false);
                        Log.d (TAG, "method PrepColumnAudio: onCompletion: isAudioPlaying: " + Audio.getIsAudioPlaying("PrepColumnAudio: onCompletion"));
                        playingColumnAudio = null;
                    }

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

        Log.i (TAG, "method PrepColumnAudio: RETURNING prepColumnAudio: " + prepColumnAudio +
            //" | PREPARED: [" + GetNumActiveSamplesInNextColumn() + "] SAMPLES" +
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
                //" | nextCol: " + Shared.userData.getCurComposeGameData().getNextPlayBackCol() +
                " | curCol numSamps: " + GetNumActiveSamplesInCurColumn() +
                //" | nextCol numSamps: " + GetNumActiveSamplesInNextColumn() +
                " \n| cur column playingColumnAudio: " + playingColumnAudio );
                //" | next column preppedColumnAudio: " + preppedColumnAudio + "\n");
    }

    /* FIXME - removed, doesn't work as MediaPlayer is not Serializable
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

    /* ========================================================================================== */

    // Inner class sends command to each media player synchronously with others utilizing SyncedStartService

    private class SyncPlaybackCommand implements Runnable {

        public final String TAG2 = "SyncPlaybackCommand";


        private final CyclicBarrier curCommandBarrier;
        private mediaPlayerCommands curCommand;
        private MediaPlayer curMP;

        public SyncPlaybackCommand(CyclicBarrier barrier, MediaPlayer player) {
            Log.d (TAG2, "method SyncPlaybackCommand: MediaPlayer: " + player + " | CyclicBarrier: " + barrier);
            curCommandBarrier = barrier;
            curMP = player;
            if (!Audio.isPlaying) Audio.setIsAudioPlaying(true);
            Log.d (TAG2, "method SyncPlaybackCommand: SET isAudioPlaying TRUE: " + Audio.getIsAudioPlaying("SyncPlaybackCommand"));
        }

        @Override
        public void run() {
            try {
                curCommandBarrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
            curMP.start();

            /*  FIXME - do we want to decrement here? or in the onCompletion Listener
            Shared.userData.getCurComposeGameData().decrementNumCellsInColLeftToPlayback();
            Log.d(TAG, "method run: case START: post decrement: num cells to play in col remaining: " +
                    Shared.userData.getCurComposeGameData().getNumCellsInColLeftToPlayBack());
            */

            //if this is the last concurrent track to spawn
            if (Shared.userData.getCurComposeGameData().getNumCellsInColLeftToPlayBack() == 0) {
                //reset the number of cells prepared
                Shared.userData.getCurComposeGameData().setNumCellsInColPreparedForPlayback(0);
                Log.d (TAG2, "method run: last concurrent track in column spawned, setNumCellsInColPreparedForPlayback(0): check: getNumCellsInColPreparedForPlayback: " +
                    Shared.userData.getCurComposeGameData().getNumCellsInColPreparedForPlayback());
            }
        }
    }

    public void FinishComposeGame () {
        //wait for current tracks to finish playing
        while (Audio.getIsAudioPlaying("FinishComposeGame")) {
            try {
                Thread.sleep (200);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //reset flags
        Shared.userData.getCurComposeGameData().setGameStarted(false);         //reset the gameStarted boolean to false
        //null the pointer to curComposeGameData once it has been appended to the UserData array of SwapGameData objects
        Shared.userData.setCurComposeGameData(null);

        //TODO - where do we want the screen controller to send us next?
    }
}