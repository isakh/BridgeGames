package ws.isak.bridge.fragments;

import android.support.v4.app.Fragment;
import android.util.Log;

import ws.isak.bridge.events.EventObserver;

import ws.isak.bridge.events.engine.MatchFlipDownCardsEvent;
import ws.isak.bridge.events.engine.MatchGameWonEvent;
import ws.isak.bridge.events.engine.MatchHidePairCardsEvent;
import ws.isak.bridge.events.engine.SwapPauseRowAudioEvent;
import ws.isak.bridge.events.engine.SwapPlayRowAudioEvent;
import ws.isak.bridge.events.engine.SwapSelectedCardsEvent;
import ws.isak.bridge.events.engine.SwapUnselectCardsEvent;
import ws.isak.bridge.events.engine.SwapGameWonEvent;
import ws.isak.bridge.events.engine.PlayCardAudioEvent;

import ws.isak.bridge.events.ui.MatchBackGameEvent;
import ws.isak.bridge.events.ui.MatchFlipCardEvent;
import ws.isak.bridge.events.ui.MatchDifficultySelectedEvent;
import ws.isak.bridge.events.ui.SwapDifficultySelectedEvent;
import ws.isak.bridge.events.ui.MatchNextGameEvent;
import ws.isak.bridge.events.ui.MatchResetBackgroundEvent;
import ws.isak.bridge.events.ui.MatchThemeSelectedEvent;
import ws.isak.bridge.events.ui.MatchStartEvent;
import ws.isak.bridge.events.ui.SwapStartEvent;

/*
 * Class BaseFragment defines the core of each fragment behavior when an event occurs
 *
 * @author isak
 */

public class BaseFragment extends Fragment implements EventObserver {

    private static final String TAG = "BaseFragment";

    //Match Game Events

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

	//Swap Game Events

    public void onEvent(SwapSelectedCardsEvent event) {
        Log.d (TAG, "method onEvent: MatchFlipCardEvent");
        throw new UnsupportedOperationException();
    }

    public void onEvent(SwapUnselectCardsEvent event) {
        //Log.d (TAG, "");
        throw new UnsupportedOperationException();
    }

    @Override
    public void onEvent(SwapGameWonEvent event) {
        //Log.d (TAG, "");
        throw new UnsupportedOperationException();
    }

    @Override
    public void onEvent(SwapStartEvent event) {
        //Log.d (TAG, "");
        throw new UnsupportedOperationException();
    }

    @Override
    public void onEvent(SwapDifficultySelectedEvent event) {
        //Log.d (TAG, "");
        throw new UnsupportedOperationException();
    }

    //Audio Playback Events

    @Override
    public void onEvent(SwapPauseRowAudioEvent event) {
        //Log.d (TAG, "");
        throw new UnsupportedOperationException();
    }

    @Override
    public void onEvent(SwapPlayRowAudioEvent event) {
        //Log.d (TAG, "");
        throw new UnsupportedOperationException();
    }

    @Override
	public void onEvent(PlayCardAudioEvent event) {
        //Log.d (TAG, "");
        throw new UnsupportedOperationException();
	}
}