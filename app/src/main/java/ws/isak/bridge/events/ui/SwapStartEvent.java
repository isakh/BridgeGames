package ws.isak.bridge.events.ui;

import android.util.Log;

import ws.isak.bridge.events.AbstractEvent;
import ws.isak.bridge.events.EventObserver;

/*
 * This event is triggered when a new swap game is started.
  *
  * @author isak
 */
public class SwapStartEvent extends AbstractEvent {

    private static final String TAG = "SwapStartEvent";

    public static final String TYPE = SwapStartEvent.class.getName();

    @Override
    protected void fire(EventObserver eventObserver) {
        Log.d (TAG, "overriding method fire");
        eventObserver.onEvent(this);
    }

    @Override
    public String getType() {
        //Log.d (TAG, "");
        return TYPE;
    }

}
