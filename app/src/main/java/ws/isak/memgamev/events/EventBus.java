package ws.isak.memgamev.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Handler;
import android.util.Log;

/**
 * The gateway for all events running in the game from ui to engine components
 * and back.
 *
 * @author isak
 */
public class EventBus {

    private static final String TAG = "EventBus";

	private Handler mHandler;
	private static EventBus mInstance = null;
	private final Map<String, List<EventObserver>> events = Collections.synchronizedMap(new HashMap<String, List<EventObserver>>());
	private Object obj = new Object();

	private EventBus() {
        Log.d (TAG, "Constructor");
		mHandler = new Handler();
	}

	public static EventBus getInstance() {
		if (mInstance == null) {
			mInstance = new EventBus();
		}
		return mInstance;
	}

	synchronized public void listen(String eventType, EventObserver eventObserver) {
		List<EventObserver> observers = events.get(eventType);
		if (observers == null) {
			observers = Collections.synchronizedList(new ArrayList<EventObserver>());
		}
		observers.add(eventObserver);
		events.put(eventType, observers);
	}

	synchronized public void unlisten(String eventType, EventObserver eventObserver) {
		List<EventObserver> observers = events.get(eventType);
		if (observers != null) {
			observers.remove(eventObserver);
		}
	}

	public void notify(Event event) {
		synchronized (obj) {
			List<EventObserver> observers = events.get(event.getType());
			if (observers != null) {
				for (EventObserver observer : observers) {
					AbstractEvent abstractEvent = (AbstractEvent) event;
					abstractEvent.fire(observer);
				}
			}
		}
	}
	
	public void notify(final Event event, long delay) {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
                Log.d (TAG, "method notify: overriding method run: on event: " + event);
                EventBus.this.notify(event);
			}
		}, delay);
	}

}