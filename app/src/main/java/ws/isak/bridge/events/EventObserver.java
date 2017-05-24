package ws.isak.bridge.events;

//Match Game Events
import ws.isak.bridge.events.engine.MatchFlipDownCardsEvent;
import ws.isak.bridge.events.engine.MatchGameWonEvent;
import ws.isak.bridge.events.engine.MatchHidePairCardsEvent;
import ws.isak.bridge.events.ui.MatchBackGameEvent;
import ws.isak.bridge.events.ui.MatchDifficultySelectedEvent;
import ws.isak.bridge.events.ui.MatchFlipCardEvent;
import ws.isak.bridge.events.ui.MatchNextGameEvent;
import ws.isak.bridge.events.ui.MatchResetBackgroundEvent;
import ws.isak.bridge.events.ui.MatchStartEvent;
import ws.isak.bridge.events.ui.MatchThemeSelectedEvent;

//Swap Game Events
import ws.isak.bridge.events.engine.SwapPlayRowAudioEvent;
import ws.isak.bridge.events.engine.SwapGameWonEvent;
import ws.isak.bridge.events.ui.SwapDifficultySelectedEvent;
import ws.isak.bridge.events.ui.SwapBackGameEvent;
import ws.isak.bridge.events.ui.SwapNextGameEvent;
//TODO ?? SwapResetBackgroundEvent - will this fix the bug where onpopupwon if missed crashes as we are clicking on the swap background?
import ws.isak.bridge.events.ui.SwapStartEvent;
import ws.isak.bridge.events.ui.SwapSelectedCardsEvent;
import ws.isak.bridge.events.ui.SwapUnselectCardsEvent;

//Compose Game Events
import ws.isak.bridge.events.ui.ComposeDifficultySelectEvent;

//General Audio Events
import ws.isak.bridge.events.engine.PlayCardAudioEvent;
/*
 * Interface EventObserver defines the onEvent methods for each type of event - these
 * methods will be overridden whenever the event is triggered. These interface methods are
 * implemented in class EventObserverAdapter
 *
 * @author isak
 */

public interface EventObserver {

    //MATCH GAME EVENTS
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

    //SWAP GAME EVENTS
    void onEvent(SwapSelectedCardsEvent event);

    void onEvent(SwapGameWonEvent event);

    void onEvent(SwapStartEvent event);

    void onEvent(SwapUnselectCardsEvent event);

    void onEvent(SwapDifficultySelectedEvent event);

    void onEvent(SwapNextGameEvent event);

    void onEvent(SwapBackGameEvent event);

    void onEvent(SwapPlayRowAudioEvent event);

    //COMPOSE GAME EVENTS
    void onEvent(ComposeDifficultySelectEvent event);


    //GENERAL AUDIO EVENTS
    void onEvent(PlayCardAudioEvent event);
}