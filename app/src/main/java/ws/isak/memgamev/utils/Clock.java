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
	public final String TAG = "Class: Clock";

	private static PauseTimer mPauseTimer = null;
	private static Clock mInstance = null;

	private Clock() {
		Log.d("TAG", "constructor Clock() creates new instance ");
	}

	public static class PauseTimer extends CountDownClock {
		private OnTimerCount mOnTimerCount = null;

		public PauseTimer(long millisOnTimer, long countDownInterval, boolean runAtStart, OnTimerCount onTimerCount) {
			super(millisOnTimer, countDownInterval, runAtStart);
			mOnTimerCount = onTimerCount;
		}

		@Override
		public void onTick(long millisUntilFinished) {
			if (mOnTimerCount != null) {
				mOnTimerCount.onTick(millisUntilFinished);
			}
		}

		@Override
		public void onFinish() {
			if (mOnTimerCount != null) {
				mOnTimerCount.onFinish();
			}
		}
	}

	public static Clock getInstance() {
		if (mInstance == null) {
			mInstance = new Clock();
		}
		return mInstance;
	}

	/**
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

	/**
	 * Method Pause: pauses the current timer
	 */
	public void pause() {
		if (mPauseTimer != null) {
			mPauseTimer.pause();
		}
	}

	/**
	 * Method Resume: resumes the current timer
	 */
	public void resume() {
		if (mPauseTimer != null) {
			mPauseTimer.resume();
		}
	}

	/**
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
