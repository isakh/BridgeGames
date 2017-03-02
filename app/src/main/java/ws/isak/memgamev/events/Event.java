package ws.isak.memgamev.events;


/**
 * The event that is invoked from the low levels of this game (like engine) and
 * not from the ui.
 */
public interface Event {

	String getType();
	
}
