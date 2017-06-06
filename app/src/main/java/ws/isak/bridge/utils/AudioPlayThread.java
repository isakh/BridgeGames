package ws.isak.bridge.utils;

import android.media.MediaPlayer;
import android.os.AsyncTask;

import ws.isak.bridge.common.Audio;

/*
 *
 * @author isak
 */

public class AudioPlayThread extends AsyncTask<MediaPlayer, Void, Void>
{
    @Override
    protected Void doInBackground(MediaPlayer... player) {

        Audio.isPlaying = true;
        player[0].start();
        return null;
    }
}
