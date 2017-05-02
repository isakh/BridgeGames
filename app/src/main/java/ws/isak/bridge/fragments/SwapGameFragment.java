package ws.isak.bridge.fragments;

import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;
import android.content.res.AssetFileDescriptor;

import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import ws.isak.bridge.R;
import ws.isak.bridge.common.Audio;
import ws.isak.bridge.common.Memory;
import ws.isak.bridge.common.Shared;

import ws.isak.bridge.common.SwapCardData;
import ws.isak.bridge.events.engine.SwapPlayPauseRowAudioEvent;
import ws.isak.bridge.events.engine.SwapResetRowAudioEvent;
import ws.isak.bridge.events.engine.SwapSelectedCardsEvent;
import ws.isak.bridge.events.engine.SwapUnselectCardsEvent;
import ws.isak.bridge.events.engine.SwapGameWonEvent;

import ws.isak.bridge.model.GameState;
import ws.isak.bridge.model.SwapGame;
import ws.isak.bridge.ui.SwapBoardView;
import ws.isak.bridge.ui.SwapControlsView;
import ws.isak.bridge.ui.PopupManager;
import ws.isak.bridge.utils.Clock;
import ws.isak.bridge.utils.SwapTileCoordinates;
import ws.isak.bridge.utils.TimerCountdown;
import ws.isak.bridge.utils.FontLoader;
import ws.isak.bridge.utils.FontLoader.Font;

/*
 * Class SwapGameFragment creates the view for the swapping game fragment, including the clock, and
 * overrides the events for unselecting the cards to swap, switching the cards to swap, and winning
 * the game.
 *
 * @author isak
 */

public class SwapGameFragment extends BaseFragment implements View.OnClickListener {

    public final String TAG = "SwapGameFragment";
    public static String URI_AUDIO = "raw://";
    
    private SwapBoardView mSwapBoardView;
    private SwapControlsView mSwapControlsView;

    //timer controls/content
    private TextView mTime;
    private ImageView mTimeImage;
    private ImageView mTimerPlayPause;
    private ImageView mTimerRestart;        //TODO later, this functionality may not be necessary?


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "method onCreateView");
        //create the view for the swap game fragment
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.swap_game_fragment, container, false);
        view.setClipChildren(false);
        ((ViewGroup) view.findViewById(R.id.swap_game_board)).setClipChildren(false);
        mTime = (TextView) view.findViewById(R.id.time_bar_text_view);
        mTimeImage = (ImageView) view.findViewById(R.id.time_bar_image);
        mTimerPlayPause = (ImageView) view.findViewById(R.id.time_bar_play_pause_button);
        mTimerRestart = (ImageView) view.findViewById(R.id.time_bar_restart_button);
        mTimerRestart.setVisibility(View.INVISIBLE);
        mTimerPlayPause.setOnClickListener(this);
        mTimerRestart.setOnClickListener(this);
        FontLoader.setTypeface(Shared.context, new TextView[]{mTime}, Font.ANGRYBIRDS);
        //the swap game audio controls - TODO Does it make sense for these to be in a separate View from the board?
        mSwapControlsView = SwapControlsView.fromXml(getActivity().getApplicationContext(), view);
        FrameLayout controlsFrameLayout = (FrameLayout) view.findViewById(R.id.swap_game_audio_controls);
        controlsFrameLayout.addView(mSwapControlsView);
        controlsFrameLayout.setClipChildren(false);
        Log.d(TAG, "method onCreateView: created layout controlsFrameLayout: " + controlsFrameLayout);
        //the swap game board
        mSwapBoardView = SwapBoardView.fromXml(getActivity().getApplicationContext(), view);
        FrameLayout playFrameLayout = (FrameLayout) view.findViewById(R.id.swap_game_play_container);
        playFrameLayout.addView(mSwapBoardView);
        playFrameLayout.setClipChildren(false);
        Log.d(TAG, "method onCreateView: created layout playFrameLayout: " + playFrameLayout);

        // build board
        buildBoard();

        Shared.eventBus.listen(SwapSelectedCardsEvent.TYPE, this);
        Shared.eventBus.listen(SwapGameWonEvent.TYPE, this);
        //FIXME - what does this event do? Shared.eventBus.listen(SwapUnselectCardsEvent.TYPE, this);
        Shared.eventBus.listen(SwapPlayPauseRowAudioEvent.TYPE, this);
        Shared.eventBus.listen(SwapResetRowAudioEvent.TYPE, this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.time_bar_play_pause_button:
                pauseTimerButton();
                break;
            case R.id.time_bar_restart_button:
                restartGameButton();
                break;
        }
    }

    private void pauseTimerButton() {
        if (!Shared.currentSwapGame.gameClock.isClockPaused()) {   //isClockPaused false means playing
            mTimerRestart.setVisibility(View.VISIBLE);
            Shared.currentSwapGame.gameClock.pauseClock();         //call pauseClock and make next button restart play
            mTimerPlayPause.setImageResource(R.drawable.timer_play_button);
        } else {
            mTimerRestart.setVisibility(View.INVISIBLE);
            Shared.currentSwapGame.gameClock.resumeClock();
            mTimerPlayPause.setImageResource(R.drawable.timer_pause_button);
        }
    }

    private void restartGameButton() {
        //TODO decide if this button should do anything - if so how does database handle incomplete games?
        // - Shared.currentMatchGame.gameClock.cancelClock();
    }

    @Override
    public void onDestroy() {
        Shared.eventBus.unlisten(SwapSelectedCardsEvent.TYPE, this);
        Shared.eventBus.unlisten(SwapGameWonEvent.TYPE, this);
        //FIXME - do we need this? Shared.eventBus.unlisten(SwapUnselectCardsEvent.TYPE, this);
        Shared.eventBus.unlisten(SwapPlayPauseRowAudioEvent.TYPE, this);
        Shared.eventBus.unlisten(SwapResetRowAudioEvent.TYPE, this);

        super.onDestroy();
    }

    private void buildBoard() {
        Log.d(TAG, "method buildBoard");
        SwapGame swapGame = Shared.engine.getActiveSwapGame();
        Log.d(TAG, "method buildBoard: time: " + Shared.currentSwapGame.swapBoardConfiguration.getGameTime());
        long time = Shared.currentSwapGame.swapBoardConfiguration.getGameTime();
        setTime(time);
        Log.d(TAG, "method buildBoard: check pointer to game: Shared.engine.getActiveSwapGame: " +
                Shared.engine.getActiveSwapGame() + " | Shared.currentSwapGame : " + Shared.currentSwapGame);
        mSwapControlsView.populateControls(swapGame);
        mSwapBoardView.setBoard(swapGame);
        startClock(time);
        Log.d(TAG, "method buildBoard: calling method debugHashMaps");
        debugHashMaps("method buildBoard");
    }

    /*
     * Method setTime converts the gameplay time in millis to minutes and seconds in order to set
     * the countdown clock on the screen //TODO CHECK THIS IS TRUE?!
     */
    private void setTime(long time) {
        //Log.d (TAG, "method setTime: input time (ms): " + time);
        int timeInSeconds = (int) Math.ceil((double) time / 1000);
        //Log.d (TAG, "              : timeInSeconds: " + timeInSeconds);
        int min = timeInSeconds / 60;
        //Log.d (TAG, "              : min: " + min);
        int sec = timeInSeconds - min * 60;
        //Log.d (TAG, "              : sec: " + sec + " | calling mTime.setText");
        mTime.setText(" " + String.format(Locale.ENGLISH, "%02d", min) + ":" + String.format(Locale.ENGLISH, "%02d", sec));
    }

    private void startClock(long time) {
        Log.d(TAG, "method startClock: input time(ms): " + time);
        Shared.currentSwapGame.gameClock.startClock(time, 1000, new TimerCountdown() {

            @Override
            public void onTick(long millisUntilFinished) {
                //Log.d (TAG, "Clock: onTick: millisUntilFinished: " + millisUntilFinished);
                setTime(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                Log.d(TAG, "method startClock: overriding onFinish");
                mTimerPlayPause.setVisibility(View.INVISIBLE);          //TODO - does this make controls disappear on finish?
                mTimerRestart.setVisibility(View.INVISIBLE);
                setTime(0);
            }
        });
    }

    public void onEvent(SwapSelectedCardsEvent event) {
        //
        Log.d(TAG, "onEvent SwapSelectedCardsEvent @ start: calling debugHashMaps");
        debugHashMaps(event.TAG);
        //prior to swap, append the current board Map to Shared.swapGameData.swapGameMapList
        Shared.userData.getCurSwapGameData().appendToSwapGameMapList(Shared.userData.getCurSwapGameData().getSwapBoardMap());
        //debug state of list of maps representing board on each turn
        for (int i = 0; i < Shared.userData.getCurSwapGameData().sizeOfSwapGameMapList(); i++) {
            Log.d(TAG, "Map in List @ i: " + i + " | @ pointer: " + Shared.userData.getCurSwapGameData().querySwapGameMapList(i));
            debugCoordsDataMap(Shared.userData.getCurSwapGameData().querySwapGameMapList(i), "STATE OF MAP" + i + " IN GAMEDATA LIST");
        }
        //TODO - make a new copy of the board map ?
        HashMap<SwapTileCoordinates, SwapCardData> nextTurnMap = Shared.userData.getCurSwapGameData().getSwapBoardMap();
        Shared.userData.getCurSwapGameData().setSwapBoardMap(nextTurnMap);
        // start of SwapSelectedCardsEvent - this method swaps the SwapCardData associated with the
        // tile coordinates received by the event in the Board setup HashMap found at Shared.userData.
        // getCurSwapGameData.curSwapBoardMap.  Additionally, this also calls mSwapBoardView.
        Log.d(TAG, "onEvent(SwapSelectedCardsEvent): event.id1: " + event.id1 + " event.id2: " + event.id2 + " *** AT START OF method ***");
        SwapTileCoordinates card1Coords = event.id1;
        SwapTileCoordinates card2Coords = event.id2;
        SwapCardData card1Data = Shared.userData.getCurSwapGameData().getSwapCardDataFromSwapBoardMap(card1Coords);
        SwapCardData card2Data = Shared.userData.getCurSwapGameData().getSwapCardDataFromSwapBoardMap(card2Coords);
        Log.d(TAG, "onEvent(SwapSelectedCardsEvent): card1Coords: " + card1Coords + " | card2Coords: " + card2Coords + " | card1Data ID: " + card1Data.getCardID() + " | card2Data ID: " + card2Data.getCardID());

        //Swap the coordinates associated with the two SwapCardData objects
        switchDataAtTileCoordinates(card1Coords, card2Coords, card1Data, card2Data);

        //push the new cards back into the board on the new Map
        Shared.currentSwapGame.swapBoardArrangement.setCardOnBoard(card1Coords, card1Data);
        Shared.currentSwapGame.swapBoardArrangement.setCardOnBoard(card2Coords, card2Data);

        //redraw the board FIXME ************

        //swap tile images
        Log.d (TAG, "*** ... GET BITMAPS TO SWAP ... ***");
        Log.d (TAG, "Card for bitmap1: < " + Shared.userData.getCurSwapGameData().getSwapCardDataFromSwapBoardMap(mSwapBoardView.selectedTiles.get(0)).getCardID().getSwapCardSpeciesID() +
                    "," + Shared.userData.getCurSwapGameData().getSwapCardDataFromSwapBoardMap(mSwapBoardView.selectedTiles.get(0)).getCardID().getSwapCardSegmentID() + " >");
        Bitmap tile0 = Shared.userData.getCurSwapGameData().getSwapCardDataFromSwapBoardMap(mSwapBoardView.selectedTiles.get(0)).getCardBitmap();
        Log.d (TAG, "Card for bitmap2: < " + Shared.userData.getCurSwapGameData().getSwapCardDataFromSwapBoardMap(mSwapBoardView.selectedTiles.get(1)).getCardID().getSwapCardSpeciesID() +
                "," + Shared.userData.getCurSwapGameData().getSwapCardDataFromSwapBoardMap(mSwapBoardView.selectedTiles.get(1)).getCardID().getSwapCardSegmentID() + " >");
        Bitmap tile1 = Shared.userData.getCurSwapGameData().getSwapCardDataFromSwapBoardMap(mSwapBoardView.selectedTiles.get(1)).getCardBitmap();
        Log.d (TAG, " SWAPPING BITMAPS ... tile0: " + tile0 + " | tile1: " + tile1);
        mSwapBoardView.mTileViewMap.get(mSwapBoardView.selectedTiles.get(0)).setTileImage(tile1);
        mSwapBoardView.mTileViewMap.get(mSwapBoardView.selectedTiles.get(1)).setTileImage(tile0);
        Log.d (TAG, "Calling mSwapBoardView.invalidate() to push redrawing of screen");
        mSwapBoardView.postInvalidate();
        //mSwapBoardView.invalidate();        //FIXME does this work?

        //TODO - remove debugging text when working
        //and get debug text to swap
        SwapCardData card1ForText = Shared.userData.getCurSwapGameData().getSwapCardDataFromSwapBoardMap(mSwapBoardView.selectedTiles.get(0));
        SwapCardData card2ForText = Shared.userData.getCurSwapGameData().getSwapCardDataFromSwapBoardMap(mSwapBoardView.selectedTiles.get(1));
        //and update TextViews with appropriate text
        Log.d (TAG, " SWAPPING TEXT");
        mSwapBoardView.mTileViewMap.get(mSwapBoardView.selectedTiles.get(0)).setTileDebugText(card1ForText);
        mSwapBoardView.mTileViewMap.get(mSwapBoardView.selectedTiles.get(1)).setTileDebugText(card2ForText);
        mSwapBoardView.invalidate();        //FIXME does this work?

        //TODO end testing ******************

        //Check if game is won
        boolean winning = true;     //TODO is this safe to default to true?
        for (int i = 0; i < Shared.currentSwapGame.swapBoardConfiguration.getSwapDifficulty(); i++) {        //for each row on the board
            for (int j = 0; j < 4; j++) {       //for each tile in row
                SwapTileCoordinates targetCoords = new SwapTileCoordinates(i, j);
                SwapCardData cardOnTile = Shared.userData.getCurSwapGameData().getSwapCardDataFromSwapBoardMap(targetCoords);
                if (cardOnTile.getCardID().getSwapCardSpeciesID() != i || cardOnTile.getCardID().getSwapCardSegmentID() != j) {
                    winning = false;
                }
            }
        }
        if (winning) {
            int passedSeconds = (int) (Shared.currentSwapGame.gameClock.getPassedTime() / 1000);
            Log.d(TAG, "onEvent SwapSelectedCardsEvent: winning: " + winning + " | passedSeconds: " + passedSeconds);     //TODO check passed time is right
            Clock.getInstance().pauseClock();
            long totalTimeInMillis = Shared.currentSwapGame.swapBoardConfiguration.getGameTime();
            int totalTime = (int) Math.ceil((double) totalTimeInMillis / 1000); //TODO is this enough or should we convert all to long ms
            GameState gameState = new GameState();
            Shared.currentSwapGame.gameState = gameState;
            // remained seconds
            gameState.remainingTimeInSeconds = totalTime - passedSeconds;

            // calculate stars and score from the amount of time that has elapsed as a ratio
            // of total time allotted for the game.  When calculating this we still have incorporated
            // the time based on the difficultyLevel as well as the time to play back the samples
            if (passedSeconds <= totalTime / 2) {
                gameState.achievedStars = 3;
            } else if (passedSeconds <= totalTime - totalTime / 5) {
                gameState.achievedStars = 2;
            } else if (passedSeconds < totalTime) {
                gameState.achievedStars = 1;
            } else {
                gameState.achievedStars = 0;
            }
            // calculate the score
            gameState.achievedScore = Shared.currentSwapGame.swapBoardConfiguration.getSwapDifficulty() * gameState.remainingTimeInSeconds;     //FIXME - was difficultyLevel, now getSwapDifficulty() , check consistency
            // save to memory
            Memory.saveSwap(Shared.currentSwapGame.swapBoardConfiguration.difficultyLevel, gameState.achievedStars);
            //trigger the MatchGameWonEvent
            Shared.eventBus.notify(new SwapGameWonEvent(gameState), 1200);      //TODO what is 1200 doing here? convert to xml
        }
    }

    private void switchDataAtTileCoordinates(SwapTileCoordinates tile1, SwapTileCoordinates tile2,
                                             SwapCardData data1, SwapCardData data2) {
        Log.d(TAG, "*** method switchDataAtTileCoordinates:  ");
        Log.d(TAG, "   ... before switch .... tile1: " + tile1 + " coords: < " +
                tile1.getSwapCoordRow() + "," + tile1.getSwapCoordCol() + " > | tile2: " + tile2 +
                " coords: < " + tile2.getSwapCoordRow() + "," + tile2.getSwapCoordCol() + " > | data1: " +
                data1 + " cardID - species: " + data1.getCardID().getSwapCardSpeciesID() +
                " | cardID - segment: " + data1.getCardID().getSwapCardSegmentID() + " | data2: " +
                data2 + " cardID - species: " + data2.getCardID().getSwapCardSpeciesID() + " | cardID - segment: " +
                data2.getCardID().getSwapCardSegmentID());
        //todo check that both coordinate keys already exist?
        Shared.userData.getCurSwapGameData().getSwapBoardMap().put(tile1, data2);
        Shared.userData.getCurSwapGameData().getSwapBoardMap().put(tile2, data1);
        //
        debugHashMaps("***** AFTER METHOD: switchDataAtTileCoordinates *****");
    }

    @Override
    public void onEvent(SwapGameWonEvent event) {
        //Log.d (TAG, "overriding method onEvent (SwapGameWonEvent)");
        //We print out all of the collected array data here?
        for (int i = 0; i < Shared.userData.getCurSwapGameData().getNumTurnsTaken(); i++) {
            if (i < 10) {
                Log.d(TAG, "*****| Turn: 0" + i + " | Turn Time: " + Shared.userData.getCurSwapGameData().queryTurnDurationsArray(i) +
                        " | Play Duration: " + Shared.userData.getCurSwapGameData().queryGamePlayDurations(i) +
                        " | BoardMap: " + Shared.userData.getCurSwapGameData().querySwapGameMapList(i));
            } else {
                Log.d(TAG, "*****| Turn: " + i + " | Turn Time: " + Shared.userData.getCurSwapGameData().queryTurnDurationsArray(i) +
                        " | Play Duration: " + Shared.userData.getCurSwapGameData().queryGamePlayDurations(i) +
                        " | BoardMap: " + Shared.userData.getCurSwapGameData().querySwapGameMapList(i));
            }
        }
        //append SwapGameData to userData array
        Shared.userData.appendSwapGameData(Shared.userData.getCurSwapGameData());     //append the MatchGameData for completed game to

        //TODO insert current swapGameData into database
        //SwapGameDataORM.insertSwapGameData(Shared.userData.getCurSwapGameData());

        //reset flags
        Shared.userData.getCurSwapGameData().setGameStarted(false);                  //reset the gameStarted boolean to false
        //null the pointer to curSwapGameData once it has been appended to the UserData array of SwapGameData objects
        Shared.userData.setCurSwapGameData(null);
        mTime.setVisibility(View.GONE);
        mTimeImage.setVisibility(View.GONE);
        PopupManager.showPopupWon(event.gameState);
    }

    @Override
    public void onEvent(SwapUnselectCardsEvent event) {
        //Log.d (TAG, "overriding method onEvent (MatchFlipDownCardsEvent)");
        mSwapBoardView.unSelectAll();
    }

    private void debugHashMaps(String callingMethod) {
        Log.d(TAG, " \n ... \n");
        Log.d(TAG, "method debugHashMaps ... called by: " + callingMethod);
        debugCoordsDataMap();
        mSwapBoardView.debugCoordsTileViewsMap(callingMethod);
    }

    private void debugCoordsDataMap() {
        //FIXME - DEBUGGING CODE - check state of map before starting swap
        Log.d(TAG, " \n ... \n");
        Log.d(TAG, "***** method debugCoordsDataMap: BoardMap <Coords, CardID>");
        Iterator iterator = Shared.userData.getCurSwapGameData().getSwapBoardMap().entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            //System.out.println(pair.getKey() + " maps to " + pair.getValue());
            SwapTileCoordinates coords = (SwapTileCoordinates) pair.getKey();
            SwapCardData cardData = (SwapCardData) pair.getValue();
            Log.d(TAG, "... debugCoordsDataMap: | coords: < " +
                    coords.getSwapCoordRow() + "," + coords.getSwapCoordCol() +
                    " > | MAPS TO | cardID: < " + cardData.getCardID().getSwapCardSpeciesID() +
                    "," + cardData.getCardID().getSwapCardSegmentID() + " > | address of Map.entry: " + pair);
        }
        Log.d(TAG, " \n ... \n");
    }


    //overloaded version takes a HashMap as input so we can iterate through List of Board Maps
    private void debugCoordsDataMap(HashMap map, String callingMethod) {
        //FIXME - DEBUGGING CODE - check state of map before starting swap
        Log.d(TAG, " \n ... \n");
        Log.d(TAG, "***** method debugCoordsDataMap: BoardMap <Coords, CardID> | Called From: " + callingMethod);
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            //System.out.println(pair.getKey() + " maps to " + pair.getValue());
            SwapTileCoordinates coords = (SwapTileCoordinates) pair.getKey();
            SwapCardData cardData = (SwapCardData) pair.getValue();
            Log.d(TAG, "... debugCoordsDataMap: | coords: < " +
                    coords.getSwapCoordRow() + "," + coords.getSwapCoordCol() +
                    " > | MAPS TO | cardID: < " + cardData.getCardID().getSwapCardSpeciesID() +
                    "," + cardData.getCardID().getSwapCardSegmentID() + " > | address of Map.entry: " + pair);
        }
        Log.d(TAG, " \n ... \n");
    }

    @Override
    public void onEvent (SwapPlayPauseRowAudioEvent event) {
        ArrayList <Integer> audioResourceIdList;
        int row = event.id;
        Log.d (TAG, "onEvent SwapPlayPauseRowAudioEvent: row to playback: " + row +
                    " | state of Audio.getIsAudioPlaying " + Audio.getIsAudioPlaying());
        if (!Audio.getIsAudioPlaying()) {
            audioResourceIdList = getAudioFilesInOrderByID(row);
            playAudioFromFileList (audioResourceIdList);
        }
        else {
            //stop playing audio
        }
    }

    @Override
    public void onEvent (SwapResetRowAudioEvent event) {
        //
        //TODO
        //
    }

    private ArrayList <Integer> getAudioFilesInOrderByID(int targetRow) {
        ArrayList <Integer> audioResourceIDs = new ArrayList<Integer>(0);
        for (int i = 0; i < Shared.currentSwapGame.swapBoardConfiguration.swapNumTilesInRow; i++) {
            SwapTileCoordinates targetTileCoords = Shared.userData.getCurSwapGameData().getMapSwapTileCoordinatesFromLoc(new SwapTileCoordinates(targetRow, i));
            int segmentID = Shared.userData.getCurSwapGameData().getSwapCardDataFromSwapBoardMap(targetTileCoords).getCardID().getSwapCardSegmentID();
            switch (segmentID) {
                case 0:
                    String audioResourceName0 = Shared.userData.getCurSwapGameData().getSwapCardDataFromSwapBoardMap(targetTileCoords).getAudioURI0().substring(URI_AUDIO.length());
                    int audioResourceId0 = Shared.context.getResources().getIdentifier(audioResourceName0, "raw", Shared.context.getPackageName());
                    audioResourceIDs.add(audioResourceId0);
                    break;
                case 1:
                    String audioResourceName1 = Shared.userData.getCurSwapGameData().getSwapCardDataFromSwapBoardMap(targetTileCoords).getAudioURI1().substring(URI_AUDIO.length());
                    int audioResourceId1 = Shared.context.getResources().getIdentifier(audioResourceName1, "raw", Shared.context.getPackageName());
                    audioResourceIDs.add(audioResourceId1);
                    break;
                case 2:
                    String audioResourceName2 = Shared.userData.getCurSwapGameData().getSwapCardDataFromSwapBoardMap(targetTileCoords).getAudioURI2().substring(URI_AUDIO.length());
                    int audioResourceId2 = Shared.context.getResources().getIdentifier(audioResourceName2, "raw", Shared.context.getPackageName());
                    audioResourceIDs.add(audioResourceId2);
                    break;
                case 3:
                    String audioResourceName3 = Shared.userData.getCurSwapGameData().getSwapCardDataFromSwapBoardMap(targetTileCoords).getAudioURI3().substring(URI_AUDIO.length());
                    int audioResourceId3 = Shared.context.getResources().getIdentifier(audioResourceName3, "raw", Shared.context.getPackageName());
                    audioResourceIDs.add(audioResourceId3);
                    break;
            }
        }
        return audioResourceIDs;
    }

    public void playAudioFromFileList (ArrayList<Integer> audioResourceIdList) {
        ArrayList <MediaPlayer> mPlayerList = new ArrayList<MediaPlayer>();
        mPlayerList.clear();
        for (int i = 0; i < audioResourceIdList.size(); i++) {
            try {
                MediaPlayer curMediaPlayer = new MediaPlayer();

                AssetFileDescriptor afd = Shared.context.getResources().openRawResourceFd(audioResourceIdList.get(i));
                if (afd == null) return;
                curMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                afd.close();

                curMediaPlayer.prepare();
                curMediaPlayer.setVolume(1f, 1f);
                curMediaPlayer.setLooping(false);
                mPlayerList.add(curMediaPlayer);
                Log.d(TAG, "mPlayerList.size(): " + mPlayerList.size());
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < (mPlayerList.size() - 1); i++) {
            mPlayerList.get(i).setNextMediaPlayer(mPlayerList.get(i+1));
        }
        mPlayerList.get(0).start();
    }

    //TODO Put this somewhere |  Toast.makeText(Shared.context, "Please turn on game audio to play in this mode, you can do this under settings", Toast.LENGTH_SHORT).show();
    //TODO put this somewhere |  ScreenController.getInstance().openScreen(ScreenController.Screen.MENU_SWAP);
}

