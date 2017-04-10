package ws.isak.memgamev.events.ui;

import android.util.Log;

import ws.isak.memgamev.events.AbstractEvent;
import ws.isak.memgamev.events.EventObserver;

/*
 * This event is triggered when the MatchNextGameEvent is called on a matching game
 *
 * @author isak
 */
public class MatchNextGameEvent extends AbstractEvent {

    private static final String TAG = "MatchNextGameEvent";

	public static final String TYPE = MatchNextGameEvent.class.getName();

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
