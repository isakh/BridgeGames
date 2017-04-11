package ws.isak.bridge.events;

/*
 * This abstract class defines AbstractEvents for the games.  It defines an abstract method
 * fire that needs to be overridden by each event.
 *
 * @author isak
 */

public abstract class AbstractEvent implements Event {

	protected abstract void fire(EventObserver eventObserver);

}
