package ws.isak.bridge.utils;

/**
 * Created by isakherman on 4/25/17.
 */

public interface TimerCountdown {

    void onTick(long millisUntilFinished);

    void onFinish();
}
