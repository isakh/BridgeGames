package ws.isak.bridge.utils;

import android.util.Log;

/*
 * This is a tool for running a timer clock with the option to pauseClock.
 */

public class Clock {

    public static final String TAG = "Clock";

	public static PauseTimer mPauseTimer = null;
	private static Clock mInstance = null;

	//constructor method
	private Clock() {
		Log.d(TAG, "constructor Clock() creates new instance of Clock");
	}

	public static Clock getInstance() {
        Log.d (TAG, "method getInstance returns Clock object");
		if (mInstance == null) {
			mInstance = new Clock();
		}
		return mInstance;
	}

	// method startClock creates a new PauseTimer (if one already exists it cancels it first)
	public void startClock(long millisOnTimer, long countDownInterval, TimerCountdown timerCountdown) {
		if (mPauseTimer != null) {
			mPauseTimer.cancel();
		}
		mPauseTimer = new PauseTimer(millisOnTimer, countDownInterval, true, timerCountdown);
		mPauseTimer.create();
	}

	//pause the timer
	public void pauseClock() {
		if (mPauseTimer != null) {
			mPauseTimer.pause();
		}
	}

	//resume the timer
	public void resumeClock() {
		if (mPauseTimer != null) {
			mPauseTimer.resume();
		}
	}

	public boolean isClockPaused () {
        return mPauseTimer.isPaused();
    }

	// stop and cancel the timer
	public void cancelClock() {
		if (mPauseTimer != null) {
			mPauseTimer.mTimerCountdown = null;
			mPauseTimer.cancel();
		}
	}

	public long getPassedTime() {
        //Log.d (TAG, "method getPassedTime returns the time that has passed on the current timer");
		return mPauseTimer.timePassed();
	}
}