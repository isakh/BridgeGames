package ws.isak.bridge.events.ui;

import android.util.Log;

import ws.isak.bridge.events.AbstractEvent;
import ws.isak.bridge.events.EventObserver;

/*
 * This event triggers when the difficultyLevel of the swapping game is selected
 *
 * @author isak
 */
public class SwapDifficultySelectedEvent extends AbstractEvent {

    private static final String TAG = "SwapDiffSelectedEvent";

    public static final String TYPE = SwapDifficultySelectedEvent.class.getName();

    public final int difficulty;

    public SwapDifficultySelectedEvent(int difficulty) {
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
