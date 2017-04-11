package ws.isak.bridge.ui;

import android.util.Log;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import ws.isak.bridge.R;

/*
 * Class UserSetupView creates the view for the user setup fragment
 *
 * @author isak
 */

public class UserSetupView extends LinearLayout {

    public static final String TAG = "UserSetupView";
    private TextView mTitle;

    public UserSetupView (Context context) {
        this (context, null);
        Log.d (TAG, "constructor");
    }

    public UserSetupView (Context context, AttributeSet attrs) {
        super (context, attrs);
        Log.d(TAG, "overloaded constructor with AttributeSet");
        LayoutInflater.from(context).inflate(R.layout.user_setup_fragment, this, true);
        setOrientation(LinearLayout.VERTICAL);
        mTitle = (TextView) findViewById(R.id.user_setup_title);
    }
}