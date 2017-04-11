package ws.isak.bridge.events.engine;

import android.util.Log;

import ws.isak.bridge.events.AbstractEvent;
import ws.isak.bridge.events.EventObserver;

/**
 * When a matched pair of cards is selected, hide them from the screen.
 *
 * @author  isak
 */
public class MatchHidePairCardsEvent extends AbstractEvent {

	public final String TAG = "MatchHidePairCardsEvent";

	public static final String TYPE = MatchHidePairCardsEvent.class.getName();

	public int id1;
	public int id2;

	public MatchHidePairCardsEvent(int id1, int id2) {
		Log.d (TAG, "constructor method MatchHidePairCardsEvent: MATCH!!!: param id1 is: " + id1 + " param id2 is: " + id2);
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
