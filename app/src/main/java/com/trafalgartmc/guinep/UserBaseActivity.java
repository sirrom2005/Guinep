package com.trafalgartmc.guinep;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.util.Locale;

/**
 * Created by rohan on 4/8/2017.
 */

class UserBaseActivity extends AppCompatActivity {
    public Activity mBaseActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBaseActivity = this;
    }

    /*@Override
    public void onBackPressed() {
        int backStackEntryCount = this.getSupportFragmentManager().getBackStackEntryCount();
        if (backStackEntryCount == 0) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            super.onBackPressed();
        }
    }*/
}
