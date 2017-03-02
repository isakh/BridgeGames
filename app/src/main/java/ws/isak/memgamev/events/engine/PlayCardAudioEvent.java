package ws.isak.memgamev.events.engine;

import android.util.Log;

import ws.isak.memgamev.events.AbstractEvent;
import ws.isak.memgamev.events.EventObserver;

/**
 * Class PlayCardAudioEvent contains the methods that trigger a playCardAudio event which occurs
 * whenever the onClick method of a tileView is performed.
 *
 * @author isak
 */

public class PlayCardAudioEvent extends AbstractEvent {
    public  final String TAG = "Class: PlayCardAudio";

    public static final String TYPE = PlayCardAudioEvent.class.getName();

    public final int id;

    public PlayCardAudioEvent(int id) {
        Log.d (TAG, "constructor method: PlayCardAudioEvent: param id is: " + id);
        this.id = id;
    }

    @Override
    protected void fire(EventObserver eventObserver) {
        eventObserver.onEvent(this);
    }

    @Override
    public String getType() {
        return TYPE;
    }

}

