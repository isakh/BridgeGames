package ws.isak.bridge.events.ui;

import android.util.Log;

import ws.isak.bridge.events.AbstractEvent;
import ws.isak.bridge.events.EventObserver;
import ws.isak.bridge.themes.MatchTheme;

public class MatchThemeSelectedEvent extends AbstractEvent {

    private static final String TAG = "MatchThemeSelectedEvent";

	public static final String TYPE = MatchThemeSelectedEvent.class.getName();
	public final MatchTheme matchTheme;

	public MatchThemeSelectedEvent(MatchTheme matchTheme) {
        Log.d (TAG, "constructor");
        this.matchTheme = matchTheme;
	}

	@Override
	protected void fire(EventObserver eventObserver) {
        //Log.d (TAG, "method fire");
        eventObserver.onEvent(this);
	}

	@Override
	public String getType() {
        //Log.d (TAG, "method getType");
        return TYPE;
	}

}
