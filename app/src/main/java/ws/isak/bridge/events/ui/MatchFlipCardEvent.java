package ws.isak.bridge.events.ui;

import android.util.Log;

import ws.isak.bridge.events.AbstractEvent;
import ws.isak.bridge.events.EventObserver;

/**
 * When a card is flipped over this event is triggered. When this happens events are triggered which
 * flip the card over and (if it is the second card in a pair, checks whether the pair is a match)
 * and plays the audio file associated with the flipped card.
 *
 * @author isak
 */
public class MatchFlipCardEvent extends AbstractEvent {

	public  final String TAG = "MatchFlipCardEvent";

	public static final String TYPE = MatchFlipCardEvent.class.getName();

	public final int id;
	
	public MatchFlipCardEvent(int id) {
		Log.d (TAG, "constructor method: MatchFlipCardEvent: param id is: " + id);
		this.id = id;
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
