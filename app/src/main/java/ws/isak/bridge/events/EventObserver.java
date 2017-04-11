package ws.isak.memgamev.events;

import ws.isak.memgamev.events.engine.MatchFlipDownCardsEvent;
import ws.isak.memgamev.events.engine.MatchGameWonEvent;
import ws.isak.memgamev.events.engine.MatchHidePairCardsEvent;
import ws.isak.memgamev.events.engine.PlayCardAudioEvent;

import ws.isak.memgamev.events.ui.MatchBackGameEvent;
import ws.isak.memgamev.events.ui.MatchDifficultySelectedEvent;
import ws.isak.memgamev.events.ui.MatchFlipCardEvent;
import ws.isak.memgamev.events.ui.MatchNextGameEvent;
import ws.isak.memgamev.events.ui.MatchResetBackgroundEvent;
import ws.isak.memgamev.events.ui.MatchStartEvent;
import ws.isak.memgamev.events.ui.MatchThemeSelectedEvent;
import ws.isak.memgamev.events.ui.SwapStartEvent;


/*
 * Interface EventObserver defines the onEvent methods for each type of event - these
 * methods will be overridden whenever the event is triggered.
 *
 * @author isak
 */

public interface EventObserver {

	void onEvent(MatchFlipCardEvent event);

	void onEvent(MatchDifficultySelectedEvent event);

	void onEvent(MatchHidePairCardsEvent event);

	void onEvent(MatchFlipDownCardsEvent event);

	void onEvent(MatchStartEvent event);

    void onEvent(SwapStartEvent event);

	void onEvent(MatchThemeSelectedEvent event);

	void onEvent(MatchGameWonEvent event);

	void onEvent(MatchBackGameEvent event);

	void onEvent(MatchNextGameEvent event);

	void onEvent(MatchResetBackgroundEvent event);

	void onEvent(PlayCardAudioEvent event);

}
