package ws.isak.memgamev.events.ui;

import android.util.Log;

import ws.isak.memgamev.events.AbstractEvent;
import ws.isak.memgamev.events.EventObserver;

/**
 * When a card is flipped over this event is triggered. When this happens events are triggered which
 * flip the card over and (if it is the second card in a pair, checks whether the pair is a match)
 * and plays the audio file associated with the flipped card.
 *
 * @author isak
 */
public class FlipCardEvent extends AbstractEvent {

	public  final String TAG = "Class: FlipCardEvent";

	public static final String TYPE = FlipCardEvent.class.getName();

	public final int id;
	
	public FlipCardEvent(int id) {
		//Log.d (TAG, "constructor method: FlipCardEvent: param id is: " + id);
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
