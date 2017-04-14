package ws.isak.bridge.events.engine;

import android.util.Log;

import ws.isak.bridge.events.AbstractEvent;
import ws.isak.bridge.events.EventObserver;

/**
 * Class PlayCardAudioEvent contains the methods that trigger a playCardAudio event which occurs
 * whenever the onClick method of a tileView is performed.
 *
 * @author isak
 */

public class SwapPlayRowAudioEvent extends AbstractEvent {
    public  final String TAG = "SwapPlayRowAudioEvent";

    public static final String TYPE = SwapPlayRowAudioEvent.class.getName();

    public final int id;

    public SwapPlayRowAudioEvent(int id) {
        Log.d (TAG, "constructor method: SwapPlayRowAudioEvent: param id is: " + id);
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

