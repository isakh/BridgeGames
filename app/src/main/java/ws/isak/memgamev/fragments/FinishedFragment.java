package ws.isak.memgamev.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;

import ws.isak.memgamev.R;
import ws.isak.memgamev.common.Shared;
import ws.isak.memgamev.engine.ScreenController;


/*
 *
 * @author isak
 */

public class FinishedFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "Class: FinishedFragment";

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
    }
}
