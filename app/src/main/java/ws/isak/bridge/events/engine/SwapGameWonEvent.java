package ws.isak.bridge.events.engine;

import android.util.Log;

import ws.isak.bridge.events.AbstractEvent;
import ws.isak.bridge.events.EventObserver;
import ws.isak.bridge.model.GameState;

/*
 * This event is called when the matching game is won
 *
 * @author isak
 */
public class SwapGameWonEvent extends AbstractEvent {

    private static final String TAG = "SwapGameWonEvent";

    public static final String TYPE = SwapGameWonEvent.class.getName();

    public GameState gameState;

    public SwapGameWonEvent(GameState gameState) {
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
