package ws.isak.bridge.common;

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;
import android.net.Uri;

import java.io.File;

import ws.isak.bridge.R;

/**
 * Class Audio handles all of the media player events for the game.  During the game this includes
 * both the audio sounds triggered for each correct match (method playCorrect) and for each star
 * achieved at the end of the game (method showStar).
 *
 * Additionally, background audio is played (method playBackgroundMusic) when the menu fragment calls
 * for it.  TODO this should check to ensure that the background doesn't interfere with game play audio
 *
 * As well as this, there is the method for playing the specific audio associated with each given tile
 * (playTileAudio.)
 *
 * @author isak
 */

public class Audio {

	public static final String TAG = "Audio";
	public static boolean OFF = false;      //games on start default to setting where audio is enabled in preferences
    public static boolean MIX = true;       //games on start default to setting where mixing is enabled in preferences
    public static boolean LOOPER = false;   //swap game on start defaults to looping being off
    public static boolean isPlaying;        //a boolean to keep track of whether audio is currently playing or not

	public static void playCorrect() {
		if (!OFF) {
			Log.d (TAG, "Method: playCorrect");
			MediaPlayer mp = MediaPlayer.create(Shared.context, R.raw.correct_answer);
			mp.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					mp.reset();
					mp.release();
					mp = null;
				}

			});
			mp.start();
		}
	}

	/*
	 * Method playBackgroundMusic creates the mediaPlayer object necessary to play
	 * the background music that is loaded on startup and exists in the opening
	 * UI fragments.
	 */

	public static void playBackgroundMusic() {
		if (!OFF) {
			MediaPlayer mp = MediaPlayer.create (Shared.context, R.raw.background_music);
			mp.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					mp.reset();
					mp.release();
					mp = null;
				}
			});
			mp.start();
		}
	}

	public static void showStar() {
		if (!OFF) {
			MediaPlayer mp = MediaPlayer.create(Shared.context, R.raw.star);
			mp.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					mp.reset();
					mp.release();
					mp = null;
				}

			});
			mp.start();
		}
	}

	/*
	 * Method getAudioDuration returns a long (millisecond duration) for the audio file found
	 * at location passed with @param audioResourceID
	 */
	public static long getAudioDuration (int audioResourceID) {
		//Log.d (TAG, "method getAudioDuration: audioResourceID: " + audioResourceID);
		//load audio data file
        //Log.d (TAG, "                       : create new MediaMetadataRetriever");
		MediaMetadataRetriever audioMetaData = new MediaMetadataRetriever();
        //Log.d (TAG, "                       : setting the data source");
        audioMetaData.setDataSource(Shared.context, Uri.parse("android.resource://" + Shared.context.getPackageName() + File.separator + audioResourceID));
		String duration = audioMetaData.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        //Log.d (TAG, "                       : duration (string): " + duration);
		long dur = Long.parseLong(duration);
        //Log.d (TAG, "                       : duration (long): " + dur);

		audioMetaData.release();
		return dur;
	}

	/*
	 * Method isAudioPlaying is a boolean set to true while a tile's audio is being played and reset
	 * to false upon completion - this ensures that only one tile's audio is played at a time
	 */
    public static void setIsAudioPlaying (boolean playbackOn) {
        Log.d (TAG, "method setIsAudioPlaying: current state: " + playbackOn);
        isPlaying = playbackOn;
    }

    public static boolean getIsAudioPlaying () {
        Log.d (TAG, "method getIsAudioPlaying: returns isPlaying: " + isPlaying);
        return isPlaying;
    }

}
