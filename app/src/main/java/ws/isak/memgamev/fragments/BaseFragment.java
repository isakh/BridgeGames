package ws.isak.memgamev.fragments;

import android.support.v4.app.Fragment;

import ws.isak.memgamev.events.EventObserver;

import ws.isak.memgamev.events.engine.FlipDownCardsEvent;
import ws.isak.memgamev.events.engine.GameWonEvent;
import ws.isak.memgamev.events.engine.HidePairCardsEvent;
import ws.isak.memgamev.events.engine.PlayCardAudioEvent;

import ws.isak.memgamev.events.ui.BackGameEvent;
import ws.isak.memgamev.events.ui.FlipCardEvent;
import ws.isak.memgamev.events.ui.NextGameEvent;
import ws.isak.memgamev.events.ui.ResetBackgroundEvent;
import ws.isak.memgamev.events.ui.ThemeSelectedEvent;
import ws.isak.memgamev.events.ui.DifficultySelectedEvent;
import ws.isak.memgamev.events.ui.StartEvent;

public class BaseFragment extends Fragment implements EventObserver {

	@Override
	public void onEvent(FlipCardEvent event) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void onEvent(DifficultySelectedEvent event) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void onEvent(HidePairCardsEvent event) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void onEvent(FlipDownCardsEvent event) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void onEvent(StartEvent event) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void onEvent(ThemeSelectedEvent event) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void onEvent(GameWonEvent event) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void onEvent(BackGameEvent event) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void onEvent(NextGameEvent event) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void onEvent(ResetBackgroundEvent event) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void onEvent(PlayCardAudioEvent event) {
		throw new UnsupportedOperationException();
	}

}
