package ws.isak.bridge.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;

import ws.isak.bridge.R;
import ws.isak.bridge.common.Shared;
import ws.isak.bridge.engine.ScreenController;


/*
 * Class Finished Fragment creates the view and defines the button clicks for the fragment that
 * comes after the post-survey and allows either a new user to start, or the app to close
 *
 * TODO solve fully closing the app
 *
 * @author isak
 */

public class FinishedFragment extends BaseFragment implements View.OnClickListener {

    public static final String TAG = "FinishedFragment";

    private Button nextPlayerBtn;
    private Button closeAppBtn;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "overriding onCreateView");
        View view = LayoutInflater.from(Shared.context).inflate(R.layout.finished_fragment, container, false);

        //create the buttons
        nextPlayerBtn = (Button) view.findViewById(R.id.finished_next_player_button);
        closeAppBtn = (Button) view.findViewById(R.id.finished_close_app_button);

        //set on click listeners
        nextPlayerBtn.setOnClickListener(this);
        closeAppBtn.setOnClickListener(this);

        return view;
    }



    @Override
    public void onClick (View view) {
        Log.d(TAG, "overriding onClick method: implementing View.OnClickListener");
        switch (view.getId()) {
            case R.id.finished_next_player_button:
                nextPlayerButton();
                break;
            case R.id.finished_close_app_button:
                closeAppButton();
                break;
        }
    }

    public void nextPlayerButton () {
        Log.d (TAG, "method nextPlayerButton");
        //TODO check that the UserData has been returned to storage before launching next user
        ScreenController.getInstance().openScreen(ScreenController.Screen.USER_SETUP);
    }

    public void closeAppButton () {
        //FIXME how to fully close the app
        //FIXME how to fully close the app - does this work??
        //((Activity) Shared.context).finish();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
