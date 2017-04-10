package ws.isak.memgamev.utils;

import android.util.Log;

/*
 * This is a tool for running a timer clock with the option to pause.
 * Important: If you run this tool on a new thread, don't forget to add:
 *
 * Looper.prepare() and Looper.loop()
 * 
 * {@code}
 * new Thread(new Runnable()
 * {
 * 	public void run()
 * 	{
 * 		Looper.prepare();
 * 		ClockTools.getInstance().startTimer(pTimerSeconds * 1000, 1000, new ClockTools.OnTimerCount()
 * 		{
 * 			public void onTick(long millisUntilFinished)
 * 			{
 * 				// do something
 * 			}
 * 
 * 			public void onFinish()
 * 			{
 * 				// do something
 * 			}
 * 		});
 * 		Looper.loop();
 * 	}
 * }).start();
 * 
 * <pre>
 * 
 * @author isak
 * 
 */


public class Clock {
	public static final String TAG = "Clock";

	private static PauseTimer mPauseTimer = null;
	private static Clock mInstance = null;
    public static long gameStartTimestamp;      //TODO this needs to be set not when Clock is instantiated but when game play starts

	//constructor method
	private Clock() {
		Log.d(TAG, "constructor Clock() creates new instance of Clock");
		//TODO does it make sense to start a listener that will return the timestamp of the start
		//TODO of the game?
	}

	/************************************************************************
	 * Class PauseTimer keeps track of game play time when the game is paused
	 */

    public static class PauseTimer extends CountDownClock {

        public static final String TAG2 = "Class: PauseTimer";
		private OnTimerCount mOnTimerCount = null;

		/*
		 * Method PauseTimer is an overloaded constructor inheriting from the constructor for the CountDownClock
		 * class with additional input of on OnTimerCount object.
		 */
        public PauseTimer(long millisOnTimer, long countDownInterval, boolean runAtStart, OnTimerCount onTimerCount) {
			super(millisOnTimer, countDownInterval, runAtStart);
			mOnTimerCount = onTimerCount;
            Log.d (TAG2, "constructor PauseTimer: millisOnTimer: " + millisOnTimer + " | countDownInterval: " + countDownInterval + " | runAtStart: " + runAtStart + " | onTimerCount: " + onTimerCount);
        }

		@Override
		public void onTick(long millisUntilFinished) {
            //Log.d (TAG2, "overriding onTick from CountDownClock");
			if (mOnTimerCount != null) {
				mOnTimerCount.onTick(millisUntilFinished);
			}
		}

		@Override
		public void onFinish() {
            Log.d (TAG2, "overriding onFinish from CountDownClock");
			if (mOnTimerCount != null) {
				mOnTimerCount.onFinish();
			}
		}
	}

    //************ Back to top level class *************************

	public static Clock getInstance() {
        Log.d (TAG, "method getInstance returns Clock object");
		if (mInstance == null) {
			mInstance = new Clock();
		}
		return mInstance;
	}

	/*
	 * Start timer
	 * 
	 * @param millisOnTimer
	 * @param countDownInterval
	 */
	public void startTimer(long millisOnTimer, long countDownInterval, OnTimerCount onTimerCount) {
		if (mPauseTimer != null) {
			mPauseTimer.cancel();
		}
		mPauseTimer = new PauseTimer(millisOnTimer, countDownInterval, true, onTimerCount);
		mPauseTimer.create();
	}

	/*
	 * Method Pause: pauses the current timer
	 */
	public void pause() {
		if (mPauseTimer != null) {
			mPauseTimer.pause();
		}
	}

	/*
	 * Method Resume: resumes the current timer
	 */
	public void resume() {
		if (mPauseTimer != null) {
			mPauseTimer.resume();
		}
	}

	/*
	 * Method cancel: Stops and cancels the timer
	 */
	public void cancel() {
		if (mPauseTimer != null) {
			mPauseTimer.mOnTimerCount = null;
			mPauseTimer.cancel();
		}
	}

	public long getPassedTime() {
        //Log.d (TAG, "method getPassedTime returns the time that has passed on the current timer");
		return mPauseTimer.timePassed();
	}

	public interface OnTimerCount {
		public void onTick(long millisUntilFinished);

		public void onFinish();
	}

}
