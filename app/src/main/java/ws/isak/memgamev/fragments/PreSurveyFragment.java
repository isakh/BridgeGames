package ws.isak.memgamev.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import ws.isak.memgamev.R;
import ws.isak.memgamev.common.Shared;
import ws.isak.memgamev.engine.ScreenController;
import ws.isak.memgamev.engine.ScreenController.Screen;

/*
 *
 * @author isak
 */

public class PreSurveyFragment extends Fragment implements  View.OnClickListener, AdapterView.OnItemSelectedListener, RadioGroup.OnCheckedChangeListener {

    public static final String TAG="Class: PreSurveyFrag";

    public static String ageRange;
    public static String yearsTwitchingRange;
    public static String speciesKnownRange;
    public static String audibleRecognizedRange;
    public static String interfaceExperienceRange;

    public static boolean hearingEqualsSeeing;
    public static boolean hasUsedSmartPhone;

    private Spinner ageQuerySpinner;
    private Spinner yearsTwitchingSpinner;
    private Spinner numSpeciesKnownSpinner;
    private Spinner audibleRecognizedSpinner;
    private Spinner interfaceExperienceSpinner;

    private RadioGroup listeningEquivalentBtns;
    private RadioGroup touchScreenExperienceBtns;

    private Button submitPreSurveyBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "overriding onCreateView");
        View view = LayoutInflater.from(Shared.context).inflate(R.layout.pre_survey_fragment, container, false);

        //create and populate the spinners and set their item selection listeners
        ageQuerySpinner = (Spinner) view.findViewById(R.id.pre_survey_age_query_spinner);
        yearsTwitchingSpinner = (Spinner) view.findViewById(R.id.pre_survey_years_twitching_query_spinner);
        numSpeciesKnownSpinner = (Spinner) view.findViewById(R.id.pre_survey_species_known_query_spinner);
        audibleRecognizedSpinner = (Spinner) view.findViewById(R.id.pre_survey_audible_identification_query_spinner);
        interfaceExperienceSpinner = (Spinner) view.findViewById(R.id.pre_survey_experience_duration_query_spinner);
        //Log.d (TAG, "ageQuerySpinner: " + ageQuerySpinner);
        //ageQuerySpinner = (Spinner) getActivity().findViewById(R.id.pre_survey_age_query_spinner);
        //create an array adapter using a string array and a default spinner layout
        ArrayAdapter<CharSequence> ageQueryAdapter =  ArrayAdapter.createFromResource(Shared.context, R.array.pre_survey_age_query_list, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> yearsTwitchingQueryAdapter =  ArrayAdapter.createFromResource(Shared.context, R.array.pre_survey_years_twitching_query_list, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> speciesKnownQueryAdapter =  ArrayAdapter.createFromResource(Shared.context, R.array.pre_survey_species_known_query_list, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> audibleIdentificationQueryAdapter = ArrayAdapter.createFromResource(Shared.context, R.array.pre_survey_audible_identification_query_list, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> interfaceExperienceQueryAdapter = ArrayAdapter.createFromResource(Shared.context, R.array.pre_survey_experience_duration_query_list, android.R.layout.simple_spinner_item);
        //Log.d (TAG, "ageQueryAdapter: " + ageQueryAdapter);
        //specify the layout to use when the list of choices appears
        ageQueryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearsTwitchingQueryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        speciesKnownQueryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        audibleIdentificationQueryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        interfaceExperienceQueryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //apply the adapter to the spinner
        ageQuerySpinner.setAdapter(ageQueryAdapter);
        yearsTwitchingSpinner.setAdapter(yearsTwitchingQueryAdapter);
        numSpeciesKnownSpinner.setAdapter(speciesKnownQueryAdapter);
        audibleRecognizedSpinner.setAdapter(audibleIdentificationQueryAdapter);
        interfaceExperienceSpinner.setAdapter(interfaceExperienceQueryAdapter);

        //create the radio groups
        listeningEquivalentBtns = (RadioGroup) view.findViewById(R.id.pre_survey_listening_equivalent_button_group);
        touchScreenExperienceBtns = (RadioGroup) view.findViewById(R.id.pre_survey_interface_experience_button_group);
        //and set them so nothing is checked at start
        listeningEquivalentBtns.clearCheck();
        touchScreenExperienceBtns.clearCheck();
        //attach OnCheckedChange listener to radio group buttons
        listeningEquivalentBtns.setOnCheckedChangeListener(this);
        touchScreenExperienceBtns.setOnCheckedChangeListener(this);

        //get submit button and set its click listener
        submitPreSurveyBtn = (Button) view.findViewById(R.id.pre_survey_submit_button);
        submitPreSurveyBtn.setOnClickListener(this);
        return view;
    }

    /*
     * Overriding method OnItemSelected uses a switch to handle the selection of various
     * options from each of the spinners
     */
    @Override
    public void onItemSelected (AdapterView <?> parent, View v, int pos, long id) {
        Log.d(TAG, "overriding method onItemSelected: for each spinner");
        switch (parent.getId()) {
            case R.id.pre_survey_age_query_spinner:
                //TODO Toast.makeText(parent.getContext(), "OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString(), Toast.LENGTH_SHORT).show();
            case R.id.pre_survey_years_twitching_query_spinner:
                //TODO
            case R.id.pre_survey_species_known_query_spinner:
                //TODO
            case R.id.pre_survey_audible_identification_query_spinner:
                //TODO
            case R.id.pre_survey_experience_duration_query_spinner:
                //TODO
        }
    }

    @Override
    public void onNothingSelected (AdapterView <?> parent) {
        //TODO anything???
        //FIXME unless autogenerated method stub is sufficient, if so why?
    }

    @Override
    public void onCheckedChanged (RadioGroup group, int checkedId) {
        Log.d (TAG, "overriding method onCheckedChanged");
        RadioButton rb = (RadioButton) group.findViewById(checkedId);
        if(null!=rb && checkedId > -1){
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
            case R.id.pre_survey_submit_button:
                //Log.d (TAG, "       : surveySubmitButton");
                submitPreSurvey();
                //TODO case R.id. any other buttons
        }
    }

    public void submitPreSurvey () {
        Log.d (TAG, "method submitPreSurvey: preview data in debugger before storing to UserData");
        //[1] Age
        //TODO do we need this? Spinner ageQuerySpinner = (Spinner) getActivity().findViewById(R.id.pre_survey_age_query_spinner);
        ageRange = ageQuerySpinner.getSelectedItem().toString();
        Log.d (TAG, "                       : ageRange: " + ageRange);
        //[2] Years Twitching
        Spinner yearsTwitchingSpinner = (Spinner) getActivity().findViewById(R.id.pre_survey_years_twitching_query_spinner);
        yearsTwitchingRange = yearsTwitchingSpinner.getSelectedItem().toString();
        Log.d (TAG, "                       : yearsTwitchingRange: " + yearsTwitchingRange);
        //[3] Species Known
        Spinner speciesKnownSpinner = (Spinner) getActivity().findViewById(R.id.pre_survey_species_known_query_spinner);
        speciesKnownRange = speciesKnownSpinner.getSelectedItem().toString();
        Log.d (TAG, "                       : speciesKnownRange: " + speciesKnownRange);
        //[4] Audible Identification
        Spinner audibleIDSpinner = (Spinner) getActivity().findViewById(R.id.pre_survey_audible_identification_query_spinner);
        audibleRecognizedRange = audibleIDSpinner.getSelectedItem().toString();
        Log.d (TAG, "                       : audibleRecognizedRange: " + audibleRecognizedRange);
        //[5] Are listening and seeing equivalent?
        RadioButton rb1 = (RadioButton) listeningEquivalentBtns.findViewById(listeningEquivalentBtns.getCheckedRadioButtonId());
        if (rb1.getText() == "NO") {
            hearingEqualsSeeing = false;
        }
        else {
            hearingEqualsSeeing = true;
        }
        Log.d (TAG, "                       : hearingEqualsSeeing: " + hearingEqualsSeeing);
        //[6] Have you used a touch screen device?
        RadioButton rb2 = (RadioButton) touchScreenExperienceBtns.findViewById(touchScreenExperienceBtns.getCheckedRadioButtonId());
        if (rb2.getText() == "NO") {
            hasUsedSmartPhone = false;
        }
        else {
            hasUsedSmartPhone = true;
        }
        Log.d (TAG, "                       : hasUsedSmartPhone: " + hasUsedSmartPhone);
        //[6a] Touchscreen Interface Experience  //TODO generate strings/layout programmatically based on response to [6]?
        Spinner interfaceExperienceSpinner = (Spinner) getActivity().findViewById(R.id.pre_survey_experience_duration_query_spinner);
        interfaceExperienceRange = interfaceExperienceSpinner.getSelectedItem().toString();
        Log.d (TAG, "                       : interfaceExperienceRange: " + interfaceExperienceRange);


        //TODO load all responses to USER_DATA

        //when done continue to next screen //TODO should become game selection screen
        ScreenController.getInstance().openScreen(Screen.MENU_MEM);
    }
}