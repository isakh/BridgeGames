package ws.isak.bridge.events;


/**
 * This is the interface for Events.  Regardless of where the event is invoked from
 * it must have a type.
 *
 * @author isak
 */
public interface Event {

	String getType();
	
}
