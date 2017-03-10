package ws.isak.memgamev.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import ws.isak.memgamev.R;
import ws.isak.memgamev.common.Shared;
import ws.isak.memgamev.common.UserData;

/*
 *
 *
 * @author isak
 */

public class UserSetupFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "Class: UserSetupFrag";
    public static String newUserName;
    public static String preexistingUserName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d (TAG, "overriding onCreateView");
        View view = LayoutInflater.from(Shared.context).inflate(R.layout.user_setup_fragment, container, false);
        //Log.d (TAG, "             address of: view: " + view);
        Button registerNewUser = (Button) view.findViewById(R.id.user_setup_register_button);
        //Log.d (TAG, "             address of: registerNewUser: " + registerNewUser);
        Button loginExistingUser = (Button) view.findViewById(R.id.user_setup_login_button);
        //Log.d (TAG, "             address of: loginExistingUser: " + loginExistingUser);
        registerNewUser.setOnClickListener(this);
        loginExistingUser.setOnClickListener(this);
        return view;
    }

    //this switch implements the onClick behavior for each of the buttons in the fragment
    @Override
    public void onClick (View v) {
        Log.d (TAG, "overriding onClick");
        switch (v.getId()) {
            case R.id.user_setup_register_button:
                //Log.d (TAG, "       :register button");
                createNewUser(v);
            case R.id.user_setup_login_button:
                //Log.d (TAG, "       : login button");
                loginExistingUser(v);
        }
    }

    public void loginExistingUser (View v) {
        Log.d (TAG, "method loginExistingUser");
        //TODO deal with checking for name uniqueness and set up new UserData in memory
        newUserName = getNewUserName();
        if (CheckUserUnique(newUserName)) {
            Log.d(TAG, "method onCreateView: overriding onClick: unique userName: instantiating new UserData");
            //TODO UserData instantiateUser = new UserData();
            //TODO load screen for next step
        } else {
            //TODO error - get user to re-enter name
        }
    }

    public void createNewUser (View v) {
        Log.d (TAG, "method createNewUser");
        preexistingUserName = getLoginName();
        //TODO deal with checking user exists and load current UserData
    }


    private String getNewUserName () {
        final EditText nameField = (EditText) getActivity().findViewById(R.id.user_setup_register_input);
        Log.d (TAG, "method getNewUserName: address of nameField: " + nameField);
        String name = nameField.getText().toString();
        Log.d (TAG, "method getNewUserName: name: " + name);
        return name;
    }

    private String getLoginName () {
        final EditText nameField = (EditText) getActivity().findViewById(R.id.user_setup_login_input);
        String name = nameField.getText().toString();
        Log.d (TAG, "method getLoginName: name: " + name);
        return name;
    }

    private boolean CheckUserUnique (String userName) {
        Log.d (TAG, "method CheckUserUnique");
        boolean userUnique = true;
        //TODO check against all names in database, set false if matches any
        return userUnique;
    }

    //TODO ...
}
