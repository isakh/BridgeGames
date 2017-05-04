package ws.isak.bridge.events.engine;

import android.util.Log;

import ws.isak.bridge.events.AbstractEvent;
import ws.isak.bridge.events.EventObserver;

/** FIXME _ this event is never called - can remove it
 * This method is called when the cards in the match game need to be flipped down
 *
 * @author isak
 */
public class SwapUnselectCardsEvent extends AbstractEvent {

    private static final String TAG = "SwapUnselectCardsEvent";

    public static final String TYPE = SwapUnselectCardsEvent.class.getName();

    public SwapUnselectCardsEvent() {
        Log.d (TAG, "constructor - empty");
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