package ws.isak.memgamev.events;

import ws.isak.memgamev.events.engine.FlipDownCardsEvent;
import ws.isak.memgamev.events.engine.GameWonEvent;
import ws.isak.memgamev.events.engine.HidePairCardsEvent;
import ws.isak.memgamev.events.engine.PlayCardAudioEvent;

import ws.isak.memgamev.events.ui.BackGameEvent;
import ws.isak.memgamev.events.ui.DifficultySelectedEvent;
import ws.isak.memgamev.events.ui.FlipCardEvent;
import ws.isak.memgamev.events.ui.NextGameEvent;
import ws.isak.memgamev.events.ui.ResetBackgroundEvent;
import ws.isak.memgamev.events.ui.StartEvent;
import ws.isak.memgamev.events.ui.ThemeSelectedEvent;


public interface EventObserver {

	void onEvent(FlipCardEvent event);

	void onEvent(DifficultySelectedEvent event);

	void onEvent(HidePairCardsEvent event);

	void onEvent(FlipDownCardsEvent event);

	void onEvent(StartEvent event);

	void onEvent(ThemeSelectedEvent event);

	void onEvent(GameWonEvent event);

	void onEvent(BackGameEvent event);

	void onEvent(NextGameEvent event);

	void onEvent(ResetBackgroundEvent event);

	void onEvent(PlayCardAudioEvent event);

}
