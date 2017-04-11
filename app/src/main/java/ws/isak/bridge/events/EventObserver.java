package ws.isak.bridge.events;

import ws.isak.bridge.events.engine.MatchFlipDownCardsEvent;
import ws.isak.bridge.events.engine.MatchGameWonEvent;
import ws.isak.bridge.events.engine.MatchHidePairCardsEvent;
import ws.isak.bridge.events.engine.PlayCardAudioEvent;

import ws.isak.bridge.events.ui.MatchBackGameEvent;
import ws.isak.bridge.events.ui.MatchDifficultySelectedEvent;
import ws.isak.bridge.events.ui.SwapDifficultySelectedEvent;
import ws.isak.bridge.events.ui.MatchFlipCardEvent;
import ws.isak.bridge.events.ui.MatchNextGameEvent;
import ws.isak.bridge.events.ui.MatchResetBackgroundEvent;
import ws.isak.bridge.events.ui.MatchStartEvent;
import ws.isak.bridge.events.ui.MatchThemeSelectedEvent;
import ws.isak.bridge.events.ui.SwapStartEvent;


/*
 * Interface EventObserver defines the onEvent methods for each type of event - these
 * methods will be overridden whenever the event is triggered.
 *
 * @author isak
 */

public interface EventObserver {

	void onEvent(MatchFlipCardEvent event);

	void onEvent(MatchDifficultySelectedEvent event);

    void onEvent(SwapDifficultySelectedEvent event);

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
