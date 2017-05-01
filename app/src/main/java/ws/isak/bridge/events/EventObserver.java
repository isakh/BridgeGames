package ws.isak.bridge.events;

import ws.isak.bridge.events.engine.MatchFlipDownCardsEvent;
import ws.isak.bridge.events.engine.SwapPlayPauseRowAudioEvent;
import ws.isak.bridge.events.engine.SwapResetRowAudioEvent;
import ws.isak.bridge.events.engine.SwapSelectedCardsEvent;
import ws.isak.bridge.events.engine.SwapUnselectCardsEvent;

import ws.isak.bridge.events.engine.MatchGameWonEvent;
import ws.isak.bridge.events.engine.SwapGameWonEvent;

import ws.isak.bridge.events.engine.MatchHidePairCardsEvent;
import ws.isak.bridge.events.engine.PlayCardAudioEvent;

import ws.isak.bridge.events.ui.MatchBackGameEvent;
//TODO SwapBackGameEvent
import ws.isak.bridge.events.ui.MatchDifficultySelectedEvent;
import ws.isak.bridge.events.ui.SwapDifficultySelectedEvent;

import ws.isak.bridge.events.ui.MatchFlipCardEvent;

import ws.isak.bridge.events.ui.MatchNextGameEvent;
//TODO SwapNextGameEvent

import ws.isak.bridge.events.ui.MatchResetBackgroundEvent;
//TODO ?? SwapResetBackgroundEvent

import ws.isak.bridge.events.ui.MatchStartEvent;
import ws.isak.bridge.events.ui.SwapStartEvent;

import ws.isak.bridge.events.ui.MatchThemeSelectedEvent;


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

	void onEvent(MatchThemeSelectedEvent event);

	void onEvent(MatchGameWonEvent event);

	void onEvent(MatchBackGameEvent event);

	void onEvent(MatchNextGameEvent event);

	void onEvent(MatchResetBackgroundEvent event);

    void onEvent(SwapSelectedCardsEvent event);

    void onEvent(SwapGameWonEvent event);

    void onEvent(SwapStartEvent event);

    void onEvent(SwapUnselectCardsEvent event);

    void onEvent(SwapDifficultySelectedEvent event);

    void onEvent(SwapResetRowAudioEvent event);

    void onEvent(SwapPlayPauseRowAudioEvent event);

    void onEvent(PlayCardAudioEvent event);

}
