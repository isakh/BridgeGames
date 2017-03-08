package ws.isak.memgamev.ui;

import java.util.Locale;
import android.util.Log;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ws.isak.memgamev.R;
import ws.isak.memgamev.common.Shared;

/*
 *
 * @author isak
 */

public class UserSetupView extends LinearLayout {

    public static final String TAG = "Class: UserSetupView";
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