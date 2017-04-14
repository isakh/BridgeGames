package ws.isak.bridge.events.engine;

import android.util.Log;

import ws.isak.bridge.events.AbstractEvent;
import ws.isak.bridge.events.EventObserver;
import ws.isak.bridge.utils.SwapTileCoordinates;

/**
 * When a matched pair of cards is selected, hide them from the screen.
 *
 * @author  isak
 */
public class SwapSelectedCardsEvent extends AbstractEvent {

    public final String TAG = "MatchHidePairCardsEvent";

    public static final String TYPE = MatchHidePairCardsEvent.class.getName();

    public SwapTileCoordinates id1;
    public SwapTileCoordinates id2;

    public SwapSelectedCardsEvent (SwapTileCoordinates id1, SwapTileCoordinates id2) {
        Log.d (TAG, "constructor method SwapSelectedCardsEvent: param id1 is: " + id1 + " param id2 is: " + id2);
        this.id1 = id1;
        this.id2 = id2;
    }

    @Override
    protected void fire(EventObserver eventObserver) {
        //Log.d (TAG, "");
        eventObserver.onEvent(this);
    }

    @Override
    public String getType() {
        //Log.d (TAG, "");
        return TYPE;
    }
}