package ws.isak.bridge.events.engine;

import android.util.Log;

import ws.isak.bridge.events.AbstractEvent;
import ws.isak.bridge.events.EventObserver;

/**
 * Class PauseCardAudioEvent contains the methods that trigger a playCardAudio event which occurs
 * whenever the onClick method of a tileView is performed.
 *
 * @author isak
 */

public class SwapPauseRowAudioEvent extends AbstractEvent {
    public  final String TAG = "SwapPauseRowAudio";

    public static final String TYPE = SwapPauseRowAudioEvent.class.getName();

    public final int id;

    public SwapPauseRowAudioEvent(int id) {
        Log.d (TAG, "constructor method: SwapPauseRowAudioEvent: param id is: " + id);
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