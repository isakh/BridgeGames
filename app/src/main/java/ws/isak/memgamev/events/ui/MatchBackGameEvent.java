package ws.isak.memgamev.events.ui;

import android.util.Log;

import ws.isak.memgamev.events.AbstractEvent;
import ws.isak.memgamev.events.EventObserver;

/*
 * This event is triggered when the player wants to replay a matching game
 * TODO check that this is called when difficulty and matchTheme are kept the same?
 *
 * @author isak
 */
public class MatchBackGameEvent extends AbstractEvent {

    private static final String TAG = "MatchBackGameEvent";

	public static final String TYPE = MatchBackGameEvent.class.getName();

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
