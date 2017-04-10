package ws.isak.memgamev.events.engine;

import android.util.Log;

import ws.isak.memgamev.events.AbstractEvent;
import ws.isak.memgamev.events.EventObserver;
import ws.isak.memgamev.model.GameState;

/*
 * This event is called when the matching game is won
 *
 * @author isak
 */
public class MatchGameWonEvent extends AbstractEvent {

    private static final String TAG = "MatchGameWonEvent";

	public static final String TYPE = MatchGameWonEvent.class.getName();

	public GameState gameState;
	
	public MatchGameWonEvent(GameState gameState) {
        Log.d (TAG, "constructor");
		this.gameState = gameState;
	}

	@Override
	protected void fire(EventObserver eventObserver) {
        Log.d (TAG, "overriding method fire");
		eventObserver.onEvent(this);
	}

	@Override
	public String getType() {
		return TYPE;
	}

}
