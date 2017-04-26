package ws.isak.bridge.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;

import android.content.Context;
import android.os.Handler;

import android.util.AttributeSet;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.BounceInterpolator;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Locale;

import ws.isak.bridge.R;
import ws.isak.bridge.common.Audio;
import ws.isak.bridge.common.Shared;
import ws.isak.bridge.engine.ScreenController;
import ws.isak.bridge.events.ui.MatchBackGameEvent;
import ws.isak.bridge.events.ui.MatchNextGameEvent;
import ws.isak.bridge.events.ui.MatchStartEvent;
import ws.isak.bridge.model.GameState;
import ws.isak.bridge.utils.Clock;
import ws.isak.bridge.utils.TimerCountdown;
import ws.isak.bridge.utils.FontLoader;
import ws.isak.bridge.utils.FontLoader.Font;

/*
 * Class PopupsWonView provides a RelativeLayout that describes the popup created via the
 * PopupManager with defined dimensions
 *
 * TODO figure out if this is sufficiently generic for all games or needs to be set for each
 *
 * @author isak
 */

public class PopupWonView extends RelativeLayout implements View.OnClickListener{

	public static final String TAG = "PopupWonView";

	private TextView mTime;
	private TextView mScore;
	private ImageView mStar1;
	private ImageView mStar2;
	private ImageView mStar3;
	private ImageView mNextLevelButton;
	private ImageView mTryAgainButton;
    private ImageView mChangeThemeButton;
    private ImageView mChangeGameButton;

	private Button gotoPostSurveyBtn;
    private Handler mHandler;


	public PopupWonView(Context context) {
		this(context, null);
        //Log.d (TAG, "constructor);
	}

	public PopupWonView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.d (TAG, "overloaded constructor PopupWonView");
		LayoutInflater.from(context).inflate(R.layout.popup_won_view, this, true);
		//Load text time and score from xml - TODO make this dynamic?
        mTime = (TextView) findViewById(R.id.popop_won_time_left_countdown);
		mScore = (TextView) findViewById(R.id.popup_won_score_bar_text);
		mStar1 = (ImageView) findViewById(R.id.popup_won_star_1);
		mStar2 = (ImageView) findViewById(R.id.popup_won_star_2);
		mStar3 = (ImageView) findViewById(R.id.popup_won_star_3);
		//Load image and text buttons for replaying game/ playing next difficultyLevel/ and finishing play
        mTryAgainButton = (ImageView) findViewById(R.id.popup_won_view_button_try_again);
		mNextLevelButton = (ImageView) findViewById(R.id.popup_won_view_button_next_level);
        mChangeThemeButton = (ImageView) findViewById(R.id.popup_won_view_button_change_theme);
        mChangeGameButton = (ImageView) findViewById(R.id.popup_won_view_button_change_game);
		gotoPostSurveyBtn = (Button) findViewById(R.id.popup_won_goto_post_survey_button);

        //FIXME! fix FontLoader so all text is in same font at defined sizes
        FontLoader.setTypeface(context, new TextView[] { mTime, mScore }, Font.ANGRYBIRDS);
		mHandler = new Handler();
		//set button (and image button) onClick listeners
		mTryAgainButton.setOnClickListener(this);
		mNextLevelButton.setOnClickListener(this);
        mChangeThemeButton.setOnClickListener(this);
        mChangeGameButton.setOnClickListener(this);
        gotoPostSurveyBtn.setOnClickListener(this);
	}

    @Override
    public void onClick (View view) {
        switch (view.getId()) {
            case R.id.popup_won_view_button_try_again:
                Shared.eventBus.notify(new MatchBackGameEvent());
                break;
            case R.id.popup_won_view_button_next_level:
                Shared.eventBus.notify(new MatchNextGameEvent());
                break;
            case R.id.popup_won_view_button_change_theme:
                Shared.eventBus.notify(new MatchStartEvent());
                break;
            case R.id.popup_won_view_button_change_game:
                continueToSelectGameFragment();
                break;
            case R.id.popup_won_goto_post_survey_button:
                continueToPostSurvey();
                break;
        }
    }

    public void continueToPostSurvey () {
        Log.d (TAG, "method continueToPostSurvey");
        PopupManager.closePopup();
        ScreenController.getInstance().openScreen(ScreenController.Screen.POST_SURVEY);

    }

    public void continueToSelectGameFragment() {
        Log.d (TAG, "method continueToSelectGameFragment");
        PopupManager.closePopup();
        ScreenController.getInstance().openScreen(ScreenController.Screen.SELECT_GAME);
    }

	public void setGameState(final GameState gameState) {
		Log.d (TAG, "method setGameState");
        Log.d (TAG, " ... gameState: " + gameState + " | gameState.remainingTimeInSeconds: " + gameState.remainingTimeInSeconds);
		int min = gameState.remainingTimeInSeconds / 60;
		int sec = gameState.remainingTimeInSeconds - min * 60;
		mTime.setText(" " + String.format(Locale.ENGLISH, "%02d", min) + ":" +
                String.format(Locale.ENGLISH, "%02d", sec));
		mScore.setText("" + 0);
		
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				Log.d (TAG, "method setGameState: overriding run()");
				animateScoreAndTime(gameState.remainingTimeInSeconds, gameState.achievedScore);
				animateStars(gameState.achievedStars);
			}
		}, 500);                    //TODO change 500 to value in xml
	}

	private void animateStars(int start) {
		Log.d (TAG, "method animateStars");
		switch (start) {
		case 0:
			mStar1.setVisibility(View.GONE);
			mStar2.setVisibility(View.GONE);
			mStar3.setVisibility(View.GONE);
			break;
		case 1:
			mStar2.setVisibility(View.GONE);
			mStar3.setVisibility(View.GONE);
			mStar1.setAlpha(0f);
			animateStar(mStar1, 0);
			break;
		case 2:
			mStar3.setVisibility(View.GONE);
			mStar1.setVisibility(View.VISIBLE);
			mStar1.setAlpha(0f);
			animateStar(mStar1, 0);
			mStar2.setVisibility(View.VISIBLE);
			mStar2.setAlpha(0f);
			animateStar(mStar2, 600);
			break;
		case 3:
			mStar1.setVisibility(View.VISIBLE);
			mStar1.setAlpha(0f);
			animateStar(mStar1, 0);
			mStar2.setVisibility(View.VISIBLE);
			mStar2.setAlpha(0f);
			animateStar(mStar2, 600);
			mStar3.setVisibility(View.VISIBLE);
			mStar3.setAlpha(0f);
			animateStar(mStar3, 1200);
			break;
		default:
			break;
		}
	}
	
	private void animateStar(final View view, int delay) {
		Log.d (TAG, "method animateStar");
		ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 0, 1f);
		alpha.setDuration(100);
		ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0, 1f);
		ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0, 1f);
		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.playTogether(alpha, scaleX, scaleY);
		animatorSet.setInterpolator(new BounceInterpolator());
		animatorSet.setStartDelay(delay);
		animatorSet.setDuration(600);
		view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
		animatorSet.start();
		
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				Log.d (TAG, "method animateStar: overriding run()");
				Audio.showStar();
			}
		}, delay);
	}

	private void animateScoreAndTime(final int remainingTimeInSeconds, final int achievedScore) {
		Log.d (TAG, "method animateScoreAndTime: remainingTimeInSeconds: " + remainingTimeInSeconds + " | achievedScore: " + achievedScore);
		final int totalAnimation = 1000;        //TODO change this to a variable? -
        final int timeTaken = (int) (Shared.currentMatchGame.gameClock.getPassedTime() / 1000);
        Log.d (TAG, "animateScoreAndTime: timeTaken: " + timeTaken);
        Log.d (TAG, "method animateScoreAndTime: calling Clock.getInstance().startTimer");
		Clock.getInstance().startClock(totalAnimation, 50, new TimerCountdown() {     //run through the clock at 35ms per second (just over 30fps so not video?)

			@Override
			public void onTick(long millisUntilFinished) {
                //Log.d (TAG, "method animateScoreAndTime: overriding onTick: millisUntilFinished: " + millisUntilFinished);
				float factor = millisUntilFinished / (totalAnimation * 1f); // 0.1
				int scoreToShow = achievedScore - (int) (achievedScore * factor);
				int timeToShow = (int) (remainingTimeInSeconds * factor);
				int min = timeToShow / 60;
				int sec = timeToShow - min * 60;
                Log.d (TAG, "animateScoreAndTime: factor: " + factor + " | min: " + min +
                        " | sec: " + sec);
                mTime.setText(" " + String.format(Locale.ENGLISH, "%02d", min) + ":" +
                        String.format(Locale.ENGLISH, "%02d", sec));
				mScore.setText("" + scoreToShow);
			}

			@Override
			public void onFinish() {
                Log.d (TAG, "method animateScoreAndTime: overriding onFinish");
                int timeTakenMin = timeTaken / 60;
                int timeTakenSec = timeTaken - timeTakenMin * 60;
                Log.d (TAG, "animateScoreAndTime: final time | min: " + timeTakenMin +
                        " | sec: " + timeTakenSec);
                mTime.setText(" " + String.format(Locale.ENGLISH, "%02d", timeTakenMin) + ":" +
                        String.format(Locale.ENGLISH, "%02d", timeTakenSec));
                mScore.setText("" + achievedScore);
			}
		});
	}
}