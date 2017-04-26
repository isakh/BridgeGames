package ws.isak.bridge.utils;

import android.util.Log;

/*
 * Class PauseTimer keeps track of game play time when the game is paused
 */

public class PauseTimer extends CountDownClock {

    public static final String TAG = "Class: PauseTimer";
    public TimerCountdown mTimerCountdown = null;

    // Method PauseTimer is an overloaded constructor inheriting from the constructor for the CountDownClock
    // class with additional input of a TimerCountdown object.
    public PauseTimer(long millisOnTimer, long countDownInterval, boolean runAtStart, TimerCountdown timerCountdown) {
        super(millisOnTimer, countDownInterval, runAtStart);
        mTimerCountdown = timerCountdown;
        Log.d (TAG, "constructor PauseTimer: millisOnTimer: " + millisOnTimer + " | countDownInterval: " + countDownInterval + " | runAtStart: " + runAtStart + " | timerCountdown: " + timerCountdown);
    }

    @Override
    public void onTick(long millisUntilFinished) {
        Log.d (TAG, "overriding onTick from CountDownClock");
        if (mTimerCountdown != null) {
            mTimerCountdown.onTick(millisUntilFinished);
        }
    }

    @Override
    public void onFinish() {
        Log.d (TAG, "overriding onFinish from CountDownClock");
        if (mTimerCountdown != null) {
            mTimerCountdown.onFinish();
        }
    }
}