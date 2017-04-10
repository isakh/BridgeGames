package ws.isak.memgamev.events.ui;

import android.util.Log;

import ws.isak.memgamev.events.AbstractEvent;
import ws.isak.memgamev.events.EventObserver;

/*
 * This event triggers when the difficulty of the matching game is selected
 *
 * @author isak
 */
public class MatchDifficultySelectedEvent extends AbstractEvent {

    private static final String TAG = "MatchDifficultySelectedEvent";

	public static final String TYPE = MatchDifficultySelectedEvent.class.getName();

	public final int difficulty;
	
	public MatchDifficultySelectedEvent(int difficulty) {
        Log.d (TAG, "constructor");
        this.difficulty = difficulty;
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
