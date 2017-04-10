package ws.isak.memgamev.common;

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.widget.Toast;
import android.util.Log;
import android.net.Uri;

import java.io.File;

import ws.isak.memgamev.R;


//TODO remove? import android.provider.MediaStore;

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
	public static boolean OFF = false;      //on start defaults to setting where audio is enabled
    public static boolean MIX = true;       //on start defaults to setting where mixing is enabled
    public static boolean isPlaying;

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
		MediaMetadataRetriever audioMetaData = new MediaMetadataRetriever();
        //.d (TAG, "                       : create new MediaMetadataRetriever");
        audioMetaData.setDataSource(Shared.context, Uri.parse("android.resource://" + Shared.context.getPackageName() + File.separator + audioResourceID));
        //Log.d (TAG, "                       : setting the data source");
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
