package ws.isak.memgamev.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ws.isak.memgamev.R;
import ws.isak.memgamev.common.Shared;
import ws.isak.memgamev.common.UserData;
import ws.isak.memgamev.engine.ScreenController;
import ws.isak.memgamev.engine.ScreenController.Screen;

/*
 * This class contains the fragment that sets up the game.  It comprises two edit text fields with
 * corresponding submission buttons for the two cases where the user is registering for the first
 * time and where the user is logging back in having previously registered.  In either case, as this
 * is the opening fragment for play, a UserData object mUserData is instantiated here
 * TODO how does this work with the UserData object in Shared??
 *
 * @author isak
 */

public class UserSetupFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "Class: UserSetupFrag";
    public static String newUserName;
    public static String loginName;

    private Button registerNewUser;
    private Button loginExistingUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "overriding onCreateView");
        View view = LayoutInflater.from(Shared.context).inflate(R.layout.user_setup_fragment, container, false);

        //create buttons for registration and login
        Log.d (TAG, "                       : create buttons for registration and login");
        registerNewUser = (Button) view.findViewById(R.id.user_setup_register_button);
        loginExistingUser = (Button) view.findViewById(R.id.user_setup_login_button);

        //prepare listeners on buttons
        Log.d (TAG, "                       : prepare listeners on buttons");
        registerNewUser.setOnClickListener(this);
        loginExistingUser.setOnClickListener(this);
        return view;
    }

    //this switch implements the onClick behavior for each of the buttons in the fragment
    @Override
    public void onClick(View v) {
        Log.d(TAG, "overriding onClick");
        switch (v.getId()) {
            case R.id.user_setup_register_button:
                Log.d (TAG, "       :register button selected");
                registerNewUser(v);
                break;
            case R.id.user_setup_login_button:
                Log.d (TAG, "       : login button selected");
                loginExistingUser(v);
                break;
        }
    }

    /*
     * Method createNewUser takes the current View, uses that information to call a method which
     * extracts the name field typed by the user, checks whether said name is unique and new, and if
     * so, creates a new UserData entity and starts to fill in the data fields
     * FIXME - should this return the UserData??
     */
    public void registerNewUser(View v) {
        Log.d(TAG, "method registerNewUser");
        newUserName = getNewUserName();
        if (CheckUserUnique(newUserName)) {
            Log.d(TAG, "                    : unique userName: instantiating new UserData and appending to UserData list");
            //TODO ********************* DOES THIS WORK?
            Shared.userData.getInstance();
            Shared.userData.setUserName(newUserName);
            //TODO Fix ^this^ because there will inevitably be access conflicts on nonStatic Shared.userData??
            Log.d (TAG, "                   : next screen is PRE_SURVEY to populate new curUserData");
            ScreenController.getInstance().openScreen(Screen.PRE_SURVEY);
        } else {
            Log.d (TAG, "                   : userName not unique: ***** ");
            Toast.makeText(Shared.context, "Please choose a name that is not already registered", Toast.LENGTH_LONG).show();
        }
    }

    /*
     * Method loginExistingUser takes the current View, uses that information to call a method which
     * extracts the name field typed by the user, checks whether said name is already in the list of
     * registered users, and if so, sets that UserData object to 'live' and triggers loading of the
     * next screen.
     */
    public void loginExistingUser(View v) {
        Log.d(TAG, "method loginExistingUser");
        //TODO deal with checking for name uniqueness and set up new UserData in memory
        loginName = getLoginName();
        if (CheckUserExists(loginName)) {
            Log.d (TAG, "                   : preexistingUserName is true, setting current UserData to user's UserData");
            //TODO ******************** DOES THIS WORK
            Shared.userData.getInstance(loginName);
            //load screen for next step
            Log.d (TAG, "                   : existing user: next screen is SELECT_GAME");
            ScreenController.getInstance().openScreen(Screen.SELECT_GAME);
        } else {
            //TODO error - get user to re-enter name
            Toast.makeText(Shared.context, "The User Name you have entered is not registered", Toast.LENGTH_LONG).show();
        }
    }

    private String getNewUserName() {
        final EditText nameField = (EditText) getActivity().findViewById(R.id.user_setup_register_input);
        //Log.d(TAG, "method getNewUserName: address of nameField: " + nameField);
        String name = nameField.getText().toString();
        Log.d(TAG, "method getNewUserName: name: " + name);
        return name;
    }

    private String getLoginName() {
        final EditText nameField = (EditText) getActivity().findViewById(R.id.user_setup_login_input);
        String name = nameField.getText().toString();
        Log.d(TAG, "method getLoginName: name: " + name);
        return name;
    }

    private boolean CheckUserExists(String userName) {
        Log.d(TAG, "method CheckUserExists: called from loginExistingUser");
        boolean userExists = true;      //FIXME
        //TODO check to find match for name in database
        return userExists;
    }

    private boolean CheckUserUnique(String userName) {
        Log.d(TAG, "method CheckUserUnique");
        boolean userUnique = true;
        //TODO check against all names in database, set false if matches any
        return userUnique;
    }
}