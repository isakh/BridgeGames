package ws.isak.bridge.events.ui;

import android.util.Log;

import java.util.List;

import ws.isak.bridge.events.AbstractEvent;
import ws.isak.bridge.events.EventObserver;
import ws.isak.bridge.utils.SwapTileCoordinates;

/** FIXME _ this event is never called - can remove it
 * This method is called when the cards in the match game need to be flipped down
 *
 * @author isak
 */
public class SwapUnselectCardsEvent extends AbstractEvent {

    private static final String TAG = "SwapUnselectCardsEvent";

    public static final String TYPE = SwapUnselectCardsEvent.class.getName();

    public List<SwapTileCoordinates> selectedTiles;

    public SwapUnselectCardsEvent(List<SwapTileCoordinates> selectedTiles) {
        Log.d (TAG, "constructor: selectedTiles: " + selectedTiles);
        this.selectedTiles = selectedTiles;

    }

    @Override
    protected void fire(EventObserver eventObserver) {
        Log.d (TAG, "overriding fire from AbstractEvent class");
        eventObserver.onEvent(this);
    }

    @Override
    public String getType() {
        Log.d (TAG, "overriding getType from Event interface");
        return TYPE;
    }
}