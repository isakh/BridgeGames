package ws.isak.bridge.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ws.isak.bridge.R;
import ws.isak.bridge.common.Shared;
import ws.isak.bridge.engine.ScreenController;

/**
 * Created by isakherman on 6/12/17.
 */

public class FinishedComposeFragment extends BaseFragment implements View.OnClickListener {

        public static final String TAG = "FinishedComposeFragment";

        private Button gameSelectScreenBtn;
        private Button nextPlayerBtn;
        private Button closeAppBtn;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "overriding onCreateView");
        View view = LayoutInflater.from(Shared.context).inflate(R.layout.finished_compose_game_fragment, container, false);

        //create the buttons
        gameSelectScreenBtn = (Button) view.findViewById(R.id.finished_compose_game_select_button);
        nextPlayerBtn = (Button) view.findViewById(R.id.finished_compose_next_player_button);
        closeAppBtn = (Button) view.findViewById(R.id.finished_compose_close_app_button);

        //set on click listeners
        gameSelectScreenBtn.setOnClickListener(this);
        nextPlayerBtn.setOnClickListener(this);
        closeAppBtn.setOnClickListener(this);

        return view;
    }



    @Override
    public void onClick (View view) {
        Log.d(TAG, "overriding onClick method: implementing View.OnClickListener");
        switch (view.getId()) {
            case R.id.finished_compose_game_select_button:
                goToGameSelectScreen();
                break;
            case R.id.finished_next_player_button:
                nextPlayerButton();
                break;
            case R.id.finished_close_app_button:
                closeAppButton();
                break;
        }
    }

    public void goToGameSelectScreen () {
        Log.d(TAG, "method goToGameSelectScreen");
        ScreenController.getInstance().openScreen(ScreenController.Screen.SELECT_GAME);
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

}
