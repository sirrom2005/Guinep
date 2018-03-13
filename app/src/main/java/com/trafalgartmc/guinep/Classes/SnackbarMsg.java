package com.trafalgartmc.guinep.Classes;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.trafalgartmc.guinep.R;
import com.trafalgartmc.guinep.Utility.Common;

import static com.trafalgartmc.guinep.Classes.SnackbarMsg.Type.NORMAL;

/**
 * Created by rohan on 5/27/2017.
 */

public class SnackbarMsg {
    private Snackbar mSnackbar;
    private Snackbar.SnackbarLayout mLayout;
    private LayoutInflater mInflater;
    private View mSnackView;
    private LinearLayout mSnackLayout;
    private TextView mSnackBarText;
    public enum Type {ERROR,NORMAL};

    public void show(View v, Context c, String text, Type mType) {
        // Create the SnackbarMsg
        if(mSnackbar==null)
            mSnackbar = Snackbar.make(v, "", android.support.design.widget.Snackbar.LENGTH_LONG);
        // Inflate our custom view
        if(mInflater==null) {
            mInflater = LayoutInflater.from(c);
            mSnackView = mInflater.inflate(R.layout.snack_bar_layout, null);
        }
        // Get the SnackbarMsg's layout view
        if(mLayout==null) {
            mLayout = (android.support.design.widget.Snackbar.SnackbarLayout) mSnackbar.getView();
            // Add the view to the SnackbarMsg's layout
            mLayout.setPadding(0,0,0,0);
            mLayout.addView(mSnackView,0);
            // Hide the text
            mLayout.findViewById(android.support.design.R.id.snackbar_text).setVisibility(View.INVISIBLE);
            mSnackLayout    = (LinearLayout) mSnackView.findViewById(R.id.snack_layout);
            mSnackBarText   = (TextView) mSnackView.findViewById(R.id.snack_bar_text);
            mSnackBarText.setTextColor(Color.WHITE);
        }

        mSnackBarText.setText(text);

        switch(mType){
            case NORMAL :
                mSnackLayout.setBackgroundColor(Color.DKGRAY);
            break;
            case ERROR :
                mSnackLayout.setBackgroundColor(Color.RED);
            break;
        }

        mSnackbar.show();
    }
}
