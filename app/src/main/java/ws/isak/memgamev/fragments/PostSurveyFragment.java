package ws.isak.memgamev.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.util.Log;
import android.widget.Toast;

import ws.isak.memgamev.R;
import ws.isak.memgamev.common.Shared;
import ws.isak.memgamev.engine.ScreenController;

/*
 *
 *
 * @author isak
 */

public class PostSurveyFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    public static final String TAG = "Class: PostSurveyFrag";

    //return values
    public static boolean spectrogramFamiliar;
    public static int hearIsSeeLikert;
    public static int hearIsPredictLikert;

    //widget variables
    private RadioGroup spectrogramFamiliarBtnGrp;
    private RadioGroup hearIsSeeBtnGrp;
    private RadioGroup hearIsPredictBtnGrp;

    private Button submitPostSurveyBtn;

    /*
     * Override method onCreateView creates the view from the inflated layout and sets up the listeners
     * for the RadioGroup buttons and the submit Button
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "overriding onCreateView");
        View view = LayoutInflater.from(Shared.context).inflate(R.layout.post_survey_fragment, container, false);

        //create the radio groups
        spectrogramFamiliarBtnGrp = (RadioGroup) view.findViewById(R.id.post_survey_prior_spectrogram_familiarity_button_group);
        //Log.d (TAG, "           : address of spectrogramFamiliarBtnGrp: " + spectrogramFamiliarBtnGrp);
        hearIsSeeBtnGrp = (RadioGroup) view.findViewById(R.id.post_survey_hearing_equals_seeing_button_group);
        hearIsPredictBtnGrp = (RadioGroup) view.findViewById(R.id.post_survey_hearing_equals_predicting_button_group);
        //and set them so nothing is checked at the start
        spectrogramFamiliarBtnGrp.clearCheck();
        hearIsSeeBtnGrp.clearCheck();
        hearIsPredictBtnGrp.clearCheck();
        //attach OnCheckChangedListener to the radio button groups
        spectrogramFamiliarBtnGrp.setOnCheckedChangeListener(this);
        hearIsSeeBtnGrp.setOnCheckedChangeListener(this);
        hearIsPredictBtnGrp.setOnCheckedChangeListener(this);

        //get submit button and set its click listener
        submitPostSurveyBtn = (Button) view.findViewById(R.id.post_survey_submit_button);
        submitPostSurveyBtn.setOnClickListener(this);

        //
        return view;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        Log.d(TAG, "overriding method onCheckedChanged");
        RadioButton rb = (RadioButton) group.findViewById(checkedId);
        if (null != rb && checkedId > -1) {
            Toast.makeText(Shared.context, rb.getText(), Toast.LENGTH_SHORT).show();
        }
    }

    /*
     *  Overriding method onClick handles the behaviour necessary for a click on the
     *  submit survey button TODO as well as on the radio buttons?
     */

    @Override
    public void onClick (View view) {
        Log.d(TAG, "overriding onClick method: implementing View.OnClickListener");
        switch (view.getId()) {
            case R.id.post_survey_submit_button:
                //Log.d (TAG, "       : surveySubmitButton");
                submitPostSurvey();
                //TODO case R.id. any other buttons
                break;
        }
    }

    public void submitPostSurvey () {
        Log.d (TAG, "method submitPostSurvey: preview data in debugger before storing to UserData");
        //[1] Were you previously familiar with spectrograms
        RadioButton rb1 = (RadioButton) spectrogramFamiliarBtnGrp.findViewById(spectrogramFamiliarBtnGrp.getCheckedRadioButtonId());
        Log.d (TAG, "                       : rb1.getText(): " + rb1.getText());
        if (rb1.getText().equals("NO") || rb1.getText() == null) {
            spectrogramFamiliar = false;
        }
        else {
            spectrogramFamiliar = true;
        }
        Log.d (TAG, "                       : spectrogramFamiliar: " + spectrogramFamiliar);
        // [2] Whilst playing the game, could you hear a song by observing its shape?
        RadioButton rb2 = (RadioButton) hearIsSeeBtnGrp.findViewById(hearIsSeeBtnGrp.getCheckedRadioButtonId());
        Log.d (TAG, "                       : rb2.getText(): " + rb2.getText());
        if (rb2.getText() == null) {hearIsSeeLikert = 0;}
        else if (rb2.getText().equals("NEVER")) {hearIsSeeLikert = 1;}
        else if (rb2.getText().equals("RARELY")) {hearIsSeeLikert = 2;}
        else if (rb2.getText().equals("SOMETIMES")) {hearIsSeeLikert = 3;}
        else if (rb2.getText().equals("FREQUENTLY")) {hearIsSeeLikert = 4;}
        else if (rb2.getText().equals("ALWAYS")) {hearIsSeeLikert = 5;}
        else {
            Log.d(TAG, "method submitPostSurvey: rb2.getText() not resolving");
        }
        Log.d(TAG, "method submitPostSurvey: hearIsSeeLikert: " + hearIsSeeLikert);
        //[3] After playing the game, could you predict the shape of the song when listening to it?
        RadioButton rb3 = (RadioButton) hearIsPredictBtnGrp.findViewById(hearIsPredictBtnGrp.getCheckedRadioButtonId());
        Log.d (TAG, "                       : rb3.getText(): " + rb3.getText());
        if (rb3.getText() == null) {hearIsPredictLikert = 0;}
        else if (rb3.getText().equals("NEVER")) {hearIsPredictLikert = 1;}
        else if (rb3.getText().equals("RARELY")) {hearIsPredictLikert = 2;}
        else if (rb3.getText().equals("SOMETIMES")) {hearIsPredictLikert = 3;}
        else if (rb3.getText().equals("FREQUENTLY")) {hearIsPredictLikert = 4;}
        else if (rb3.getText().equals("ALWAYS")) {hearIsPredictLikert = 5;}
        else {
            Log.d(TAG, "method submitPostSurvey: rb3.getText() not resolving");
        }
        Log.d (TAG, "method submitPostSurvey: hearIsPredictLikert: " + hearIsPredictLikert);

        //Load all responses to USER_DATA
        Log.d (TAG, "******* Shared.userData @: " + Shared.userData);
        Shared.userData.setSpectrogramFamiliar(spectrogramFamiliar);
        Shared.userData.setHearIsSeeLikert(hearIsSeeLikert);
        Shared.userData.setHearIsPredictLikert(hearIsPredictLikert);

        //when done continue to next finished screen where option are for a new user or close app
        ScreenController.getInstance().openScreen(ScreenController.Screen.FINISHED);
    }
}