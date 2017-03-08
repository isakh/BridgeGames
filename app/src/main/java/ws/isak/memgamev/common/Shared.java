package ws.isak.memgamev.common;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

import ws.isak.memgamev.engine.Engine;
import ws.isak.memgamev.events.EventBus;

public class Shared {

	public static Context context;
	public static FragmentActivity activity; // TODO: move to weak reference
	public static Engine engine;
	public static EventBus eventBus;

	//TODO add anything else here??

}
