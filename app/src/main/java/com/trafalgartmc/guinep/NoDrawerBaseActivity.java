package com.trafalgartmc.guinep;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.trafalgartmc.guinep.Settings.SettingsActivity;
import com.trafalgartmc.guinep.Utility.Common;

/**
 * Created by Rohan
 * Date 4/8/2017.
 */

class NoDrawerBaseActivity extends AppCompatActivity {
    public Context mBaseContext,mBaseActivity;
    public Toolbar mBaseToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);
        mBaseToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mBaseToolbar);

        mBaseActivity = this;
        mBaseContext = getApplicationContext();

        ImageView banner = (ImageView) findViewById(R.id.banner);
        banner.setImageDrawable(ContextCompat.getDrawable(mBaseContext, R.drawable.banner2));

        //Show toolbar text only when minimized
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setExpandedTitleColor(Color.argb(0,0,0,0)); // transperent color = #00000000
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE); //Color of your title
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mini_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            onBackPressed();
            return true;
        }

        if (id == R.id.action_settings) {
            Intent intent = new Intent(mBaseActivity, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_about) {
            Common.about(mBaseActivity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
