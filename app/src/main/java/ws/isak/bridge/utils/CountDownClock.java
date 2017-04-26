package ws.isak.bridge.utils;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

@SuppressLint("HandlerLeak")


/*
 * Class CountDownClock
 *
 * @author isak
 */

public abstract class CountDownClock {
    public final String TAG = "CountDownClock";

    private long mStopTimeInFuture;		    //Milliseconds since boot when alarm should stop.
	private long mMillisOnTimer;            //Real time remaining until timer completes
	private final long mTotalCountdown;     //Total time on timer at start
	private final long mCountdownInterval;  //The interval in millis that the user receives callbacks, default to 1000ms - TODO set in xml
	private long mPausedTimeRemaining;      //Time remaining on the timer when it was paused, if currently paused, otherwise 0.
	private boolean timerIsRunning;            //True if timer has started running, false if not.
    private static final int MSG = 1;       //integer message for the handler

	/**
	 * Method CountDownClock is the constructor for the countdown clock abstract class (which is inherited
	 * by the PauseTimer constructor in the PauseTimer class (in Clock.java).  The input parameters
	 * are:
	 *
	 * @param millisOnTimer, a long which represent the time remaining until the timer completes (at
	 * instantiation this is the duration allocated for the game, when it reaches 0 onFinish() is called).
	 *
	 * @param countDownInterval, a long which represents the interval in millis at which to execute
	 *            onTick(mMillisOnTimer) callbacks
	 * @param runAtStart, a boolean: True if timer should start running, false if not
	 */
	public CountDownClock(long millisOnTimer, long countDownInterval, boolean runAtStart) {
		mMillisOnTimer = millisOnTimer;

        mTotalCountdown = mMillisOnTimer;
		mCountdownInterval = countDownInterval;
		timerIsRunning = runAtStart;
        //Log.d (TAG, "CountDownClock constructor: millisOnTimer: " + millisOnTimer + " | countDownInterval: " + countDownInterval + " | runAtStart: " + runAtStart);
	}

	//Cancel the countdown and clears all remaining messages
	public final void cancel() {
        //Log.d (TAG, "method cancel: tells the handler to remove all callbacks and messages in the queue");
		mHandler.removeCallbacksAndMessages(null);
	}

	//Create the timer object.
	public synchronized final CountDownClock create() {
        //Log.d (TAG, "method create: create the timer object if millis remain on the  timer");
		if (mMillisOnTimer <= 0) {
			onFinish();
		} else {
			mPausedTimeRemaining = mMillisOnTimer;
		}
		if (timerIsRunning) {
			resume();
		}
		return this;
	}

	//Pauses the countdown timer.
	public void pause() {
        //Log.d (TAG, "method pauseClock: if boolean isRunning is true, set the pauseClock time remaining to timeLeft and cancel the countdown clock");
		if (isRunning()) {
			mPausedTimeRemaining = timeLeft();
			cancel();
		}
	}

	//Resume the timer counting down
	public void resume() {
        Log.d (TAG, "method resume: if isPaused is true");
		if (isPaused()) {
            Log.d (TAG, "             : set millisOnTimer to pauseTimeRemaining: " + mPausedTimeRemaining);
			mMillisOnTimer = mPausedTimeRemaining;
            Log.d (TAG, "             : SystemClock.elapsedRealTime(): " + SystemClock.elapsedRealtime());
            Log.d (TAG, "             : set stopTimeInFuture to SystemClock.elapsedRealTime() + millisOnTimer: " + (SystemClock.elapsedRealtime() + mMillisOnTimer));
			mStopTimeInFuture = SystemClock.elapsedRealtime() + mMillisOnTimer;
            Log.d (TAG, "             : ***CHECK*** system call delay reflects difference with stopTimeInFuture: " + mStopTimeInFuture);
			mHandler.sendMessage(mHandler.obtainMessage(MSG));
			mPausedTimeRemaining = 0;
		}
	}

	//method isPaused returns boolean for pauseClock state
	public boolean isPaused() {
        //Log.d (TAG, "method isPaused: returns true if pauseTimeRemains i.e. > 0");
		return (mPausedTimeRemaining > 0);
	}

	//Method isRunning is negation on inPaused()
	public boolean isRunning() {
        Log.d (TAG, "method isRunning: returns true on logical negation of isPause()");
		return (!isPaused());
	}

	//Method timeLeft returns the number of milliseconds remaining until the timer is finished
	public long timeLeft() {
        //Log.d (TAG, "method timeLeft");
		long millisUntilFinished;
		if (isPaused()) {
			millisUntilFinished = mPausedTimeRemaining;
            Log.d (TAG, "       :isPaused() true: millisUntilFinished: " + millisUntilFinished);
		} else {
			millisUntilFinished = mStopTimeInFuture - SystemClock.elapsedRealtime();
            //Log.d (TAG, "       : isPaused() false: millisUntilFinished: " + millisUntilFinished);
			if (millisUntilFinished < 0)
				millisUntilFinished = 0;
		}
		return millisUntilFinished;
	}

	// Method totalCountdown returns the number of milliseconds in total that the timer was set to run
	public long totalCountdown() {
        Log.d (TAG, "method totalCountdown: return total number of milliseconds timer was set to run: " + mTotalCountdown);
		return mTotalCountdown;
	}

	// Method timePassed returns the number of milliseconds that have elapsed on the timer.
	public long timePassed() {
        Log.d (TAG, "method timePassed: returning totalCountdown - timeLeft(): " + (mTotalCountdown - timeLeft()));
		return mTotalCountdown - timeLeft();
	}

	// Method hasBeenStarted returns boolean true if the timer has been started, false otherwise.
	public boolean hasBeenStarted() {
		Log.d (TAG, "method hasBeenStarted: returns boolean result of whether pauseClock time remaining < millis on timer: " + (mPausedTimeRemaining <= mMillisOnTimer));
        return (mPausedTimeRemaining <= mMillisOnTimer);
	}

	//Abstract method onTick is the callback declared here and implemented in class PauseTimer (in Clock.java)
	public abstract void onTick(long millisUntilFinished);

	//Abstract method onFinish is the callback fired when the time is up. Implemented in PauseTimer.
	public abstract void onFinish();

	// This handler handles counting down the remaining time
    private final Handler mHandler = new Handler(Looper.getMainLooper()) {

        @Override
		public void handleMessage(Message msg) {

            Log.d (TAG, "Override handleMessage");

			synchronized (CountDownClock.this) {
				long millisLeft = timeLeft();

				if (millisLeft <= 0) {          //handler behaviour if we have reached end of alloted time
					cancel();
					onFinish();
				} else if (millisLeft < mCountdownInterval) {   //if we haven't reached the countdown interval, no tick, delay until done
                    sendMessageDelayed(obtainMessage(MSG), millisLeft);
				} else {                        //if more than countdown interval time has passed
					long lastTickStart = SystemClock.elapsedRealtime();
					onTick(millisLeft);

					// take into account user's onTick taking time to execute
					long delay = mCountdownInterval - (SystemClock.elapsedRealtime() - lastTickStart);

					// special case: user's onTick took more than
					// mCountdownInterval to
					// complete, skip to next interval
					while (delay < 0)
						delay += mCountdownInterval;

					sendMessageDelayed(obtainMessage(MSG), delay);
				}
			}
		}
	};
}
