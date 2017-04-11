package ws.isak.memgamev.fragments;

import android.support.v4.app.Fragment;
import android.util.Log;

import ws.isak.memgamev.events.EventObserver;

import ws.isak.memgamev.events.engine.MatchFlipDownCardsEvent;
import ws.isak.memgamev.events.engine.MatchGameWonEvent;
import ws.isak.memgamev.events.engine.MatchHidePairCardsEvent;
import ws.isak.memgamev.events.engine.PlayCardAudioEvent;

import ws.isak.memgamev.events.ui.MatchBackGameEvent;
import ws.isak.memgamev.events.ui.MatchFlipCardEvent;
import ws.isak.memgamev.events.ui.MatchDifficultySelectedEvent;
import ws.isak.memgamev.events.ui.MatchNextGameEvent;
import ws.isak.memgamev.events.ui.MatchResetBackgroundEvent;
import ws.isak.memgamev.events.ui.MatchThemeSelectedEvent;
import ws.isak.memgamev.events.ui.MatchStartEvent;
import ws.isak.memgamev.events.ui.SwapStartEvent;

/*
 * Class BaseFragment defines the core of each fragment behavior when an event occurs
 *
 * @author isak
 */

public class BaseFragment extends Fragment implements EventObserver {

    private static final String TAG = "BaseFragment";

	@Override
	public void onEvent(MatchFlipCardEvent event) {
        Log.d (TAG, "method onEvent: MatchFlipCardEvent");
        throw new UnsupportedOperationException();
	}

	@Override
	public void onEvent(MatchDifficultySelectedEvent event) {
        //Log.d (TAG, "");
        throw new UnsupportedOperationException();
	}

	@Override
	public void onEvent(MatchHidePairCardsEvent event) {
        //Log.d (TAG, "");
        throw new UnsupportedOperationException();
	}

	@Override
	public void onEvent(MatchFlipDownCardsEvent event) {
        //Log.d (TAG, "");
        throw new UnsupportedOperationException();
	}

	@Override
	public void onEvent(MatchStartEvent event) {
        //Log.d (TAG, "");
        throw new UnsupportedOperationException();
	}

    @Override
    public void onEvent(SwapStartEvent event) {
        //Log.d (TAG, "");
        throw new UnsupportedOperationException();
    }

	@Override
	public void onEvent(MatchThemeSelectedEvent event) {
        //Log.d (TAG, "");
        throw new UnsupportedOperationException();
	}

	@Override
	public void onEvent(MatchGameWonEvent event) {
        //Log.d (TAG, "");
        throw new UnsupportedOperationException();
	}

	@Override
	public void onEvent(MatchBackGameEvent event) {
        //Log.d (TAG, "");
        throw new UnsupportedOperationException();
	}

	@Override
	public void onEvent(MatchNextGameEvent event) {
        //Log.d (TAG, "");
        throw new UnsupportedOperationException();
	}

	@Override
	public void onEvent(MatchResetBackgroundEvent event) {
        //Log.d (TAG, "");
        throw new UnsupportedOperationException();
	}

	@Override
	public void onEvent(PlayCardAudioEvent event) {
        //Log.d (TAG, "");
        throw new UnsupportedOperationException();
	}
}