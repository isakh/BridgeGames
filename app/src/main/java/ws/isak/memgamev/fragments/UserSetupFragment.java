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

public class UserSetupFragment extends Fragment{

    public static final String TAG = "Class: UserSetupFrag";
    public static String userName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d (TAG, "overriding onCreateView");
        View view = LayoutInflater.from(Shared.context).inflate(R.layout.user_setup_fragment, container, false);
        Button registerNewUser = (Button) view.findViewById(R.id.user_name_register_button);
        Button loginExistingUser = (Button) view.findViewById(R.id.user_preexisting_login_button);

        registerNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO deal with checking for name uniqueness and set up new UserData in memory
                userName = getUserName(view);
                if (CheckUserUnique (userName)) {
                    Log.d (TAG, "method onCreateView: overriding onClick: unique userName: instantiating new UserData");
                    UserData instantiateUser = new UserData();
                }
                else {
                    //TODO error - get user to re-enter name
                }
            }
        });

        loginExistingUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                //TODO deal with checking user exists and load current UserData
            }
        });
        return view;
    }

    private String getUserName (View view) {
        final EditText nameField = (EditText) view.findViewById(R.id.user_name_input);
        String name = nameField.getText().toString();
        Log.d (TAG, "method getUserName: name: " + name);
        return name;
    }

    private boolean CheckUserUnique (String userName) {
        Log.d (TAG, "method CheckUserUnique");
        //TODO
    }

    private
}
