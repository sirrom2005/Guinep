package com.trafalgartmc.guinep;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.trafalgartmc.guinep.Classes.Forecast;
import com.trafalgartmc.guinep.Classes.WeatherDataParser;
import com.trafalgartmc.guinep.Settings.SettingsActivity;
import com.trafalgartmc.guinep.Utility.AlertBox;
import com.trafalgartmc.guinep.Utility.Common;
import com.trafalgartmc.guinep.Welcome.Intro;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;

/**
 * MainBaseActivity
 * Created by rohan on 4/6/2017.
 */

public class MainBaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout mDrawer = null;
    private WeakReference<Drawable> drawableWeakReference = null;
    public Activity mBaseActivity;
    public Context mBaseContext;
    public Toolbar mBaseToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_activity_main);
        mBaseToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mBaseToolbar);
        mBaseActivity = this;
        mBaseContext = getApplicationContext();

        //Show toolbar text only when minimized
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setExpandedTitleColor(Color.argb(0,0,0,0)); // transparent color = #00000000
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE); //Color of your title

        RelativeLayout bannerFrame = (RelativeLayout) findViewById(R.id.banner_frame);
        bannerFrame.getLayoutParams().height = Common.getBannerHeight(mBaseContext);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mBaseToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        List<Forecast> weatherData = WeatherDataParser.getWeatherData(mBaseContext);

        View nav = navigationView.getHeaderView(0); // 0-index header
        TextView appUser= nav.findViewById(R.id.app_user);
        RelativeLayout nav_weather = nav.findViewById(R.id.nav_weather);
        TextView degree = nav.findViewById(R.id.today_degree);
        TextView country = nav.findViewById(R.id.country);

        ImageView weatherIcon   = nav.findViewById(R.id.today_weather_icon);
        final ImageView profile = nav.findViewById(R.id.profile);

        appUser.setText(Common.getSession(mBaseContext).getString(getString(R.string.SESSION_NAME),
                        getString(R.string.pref_default_display_name)));

        Common.setProfilePhoto(mBaseContext, profile);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Common.getSession(mBaseContext).getInt(getString(R.string.SESSION_ID),0)==0){
                    Intent intent = new Intent(mBaseContext, LoginActivity.class);
                    startActivity(intent);
                }else{
                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(mBaseActivity, profile, "profile_photo");
                    Intent intent = new Intent(mBaseContext, UpdateProfileActivity.class);
                    startActivity(intent, optionsCompat.toBundle());
                }
            }
        });

        if(weatherData!=null) {
            //set degree
            degree.setText(Common.formatTemperature(mBaseContext, Float.parseFloat(weatherData.get(0).getTemp()), Common.isMetric(mBaseContext)));
            //set icon
            weatherIcon.setImageDrawable(getDrawableIcon(weatherData.get(0).getIcon()));
            //Set location text
            Locale loc = new Locale("",weatherData.get(0).getCountry());
            country.setText(weatherData.get(0).getCity() + ", " + loc.getDisplayCountry());
        }else{
            country.setText("Location Server Disabled");
        }

        nav_weather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.closeDrawer(GravityCompat.START);
                Intent intent = new Intent(mBaseContext, ForecastActivity.class);
                startActivity(intent);
            }
        });

        Menu menuNav = navigationView.getMenu();
        MenuItem register   = menuNav.findItem(R.id.action_register);
        MenuItem login      = menuNav.findItem(R.id.action_login);
        MenuItem changePass = menuNav.findItem(R.id.action_change_pass);
        MenuItem logout     = menuNav.findItem(R.id.action_logout);


        if(Common.getSession(mBaseContext).getInt(getString(R.string.SESSION_ID),0)==0){
            register.setVisible(true);
            login.setVisible(true);
            changePass.setVisible(false);
            logout.setVisible(false);
        }else{
            register.setVisible(false);
            login.setVisible(false);
            changePass.setVisible(true);
            logout.setVisible(true);
        }
    }

    private Drawable getDrawableIcon(String icon) {
        if(drawableWeakReference==null){
            drawableWeakReference = new WeakReference<>(ContextCompat.getDrawable(mBaseContext, Common.getWeatherIcon(icon)));
        }
        return drawableWeakReference.get();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.global, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        /*Bundle bundle = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
        }*/
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.action_news) {
            Intent intent = new Intent(mBaseActivity, NewsActivity.class);
            startActivity(intent);
        }
        if (id == R.id.action_itinerary) {
            if(Common.getSession(mBaseContext).getInt(getString(R.string.SESSION_ID),0)==0){
                AlertBox.Show(mBaseActivity,getString(R.string.access_denied),AlertBox.Type.ERROR);
            }else {
                Intent intent = new Intent(mBaseActivity, ItineraryActivity.class);
                startActivity(intent);
            }
        }
        if (id == R.id.action_invoice) {
            if(Common.getSession(mBaseContext).getInt(getString(R.string.SESSION_ID),0)==0){
                AlertBox.Show(mBaseActivity,getString(R.string.access_denied),AlertBox.Type.ERROR);
            }else {
                Intent intent = new Intent(mBaseActivity, InvoiceActivity.class);
                startActivity(intent);
            }
        }
        if (id == R.id.action_register) {
            Intent intent = new Intent(mBaseActivity, RegisterActivity.class);
            startActivity(intent);
        }
        if (id == R.id.action_change_pass) {
            Intent intent = new Intent(mBaseActivity, ChangePassActivity.class);
            startActivity(intent);
        }
        if (id == R.id.action_login) {
            Intent intent = new Intent(mBaseActivity, LoginActivity.class);
            startActivity(intent);
        }
        if (id == R.id.action_logout) {
            Common.showLoading(mBaseActivity, getResources().getString(R.string.ending_session));
            Common.endSession(mBaseContext);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }finally {
                        Intent intent = new Intent(mBaseActivity, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }
            }).start();
        }
        if (id == R.id.action_settings) {
            Intent intent = new Intent(mBaseActivity, SettingsActivity.class);
            startActivity(intent);
        }
        if (id == R.id.action_quick_info) {
            SharedPreferences sharedPref = getSharedPreferences(getString(R.string.application_id), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("WELCOME_PAGE_SEEN",true).apply();
            Intent intent = new Intent(mBaseActivity, Intro.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        if (id == R.id.action_about) {
            Common.about(mBaseActivity);
        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
