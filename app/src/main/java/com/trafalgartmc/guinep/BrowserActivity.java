package com.trafalgartmc.guinep;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
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

public class BrowserActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private WeakReference<Drawable> drawableWeakReference = null;
    private Context mBaseContext,mBaseActivity;
    private DrawerLayout mDrawer;
    private static String mUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_min_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mBaseActivity = this;
        mBaseContext = getApplicationContext();

        Intent intent = getIntent();
        mUrl = intent.getStringExtra(Common.URL);

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, BrowserFragment.newInstance())
                    .commit();
        }

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final Context mBaseContext = getApplicationContext();

        List<Forecast> weatherData = WeatherDataParser.getWeatherData(mBaseContext);

        View nav = navigationView.getHeaderView(0); // 0-index header
        TextView appUser= nav.findViewById(R.id.app_user);
        TextView degree = nav.findViewById(R.id.today_degree);
        RelativeLayout nav_weather = nav.findViewById(R.id.nav_weather);
        TextView country = nav.findViewById(R.id.country);

        ImageView weatherIcon   = nav.findViewById(R.id.today_weather_icon);
        final ImageView profile = nav.findViewById(R.id.profile);

        appUser.setText(Common.getSession(mBaseContext).getString(getString(R.string.SESSION_NAME),
                getString(R.string.pref_default_display_name)));

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Common.getSession(mBaseContext).getInt(getString(R.string.SESSION_ID),0)==0){
                    Intent intent = new Intent(mBaseContext, LoginActivity.class);
                    startActivity(intent);
                }else {
                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(BrowserActivity.this, profile, "profile_photo");
                    Intent intent = new Intent(BrowserActivity.this, UpdateProfileActivity.class);
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
        }

        nav_weather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.closeDrawer(GravityCompat.START);
                Intent intent = new Intent(mBaseContext, ForecastActivity.class);
                startActivity(intent);
            }
        });
    }

    private Drawable getDrawableIcon(String icon) {
        if(drawableWeakReference==null){
            drawableWeakReference = new WeakReference<>(ContextCompat.getDrawable(BrowserActivity.this, Common.getWeatherIcon(icon)));
        }
        return drawableWeakReference.get();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home){
            finish();
        }
        if (id == R.id.action_home) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_special) {
            if(Common.getSession(mBaseContext).getInt(getString(R.string.SESSION_ID),0)==0){
                AlertBox.Show(mBaseActivity,getString(R.string.access_denied),AlertBox.Type.ERROR);
            }else {
                Intent intent = new Intent(this, SpecialsActivity.class);
                startActivity(intent);
                return true;
            }
        }
        if (id == R.id.action_chat) {
            if(Common.getSession(mBaseContext).getInt(getString(R.string.SESSION_ID),0)==0){
                AlertBox.Show(mBaseActivity,getString(R.string.access_denied),AlertBox.Type.ERROR);
            }else {
                Intent intent = new Intent(this, ChatConnectActivity.class);
                startActivity(intent);
                return true;
            }
        }
        if (id == R.id.action_photo_stream) {
            Intent intent = new Intent(this, PhotoStreamActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_refresh) {
            return true;
        }
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_about) {
            Common.about(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
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
            Intent intent = new Intent(this, NewsActivity.class);
            startActivity(intent);
        }
        if (id == R.id.action_itinerary) {
            if(Common.getSession(mBaseContext).getInt(getString(R.string.SESSION_ID),0)==0){
                AlertBox.Show(mBaseActivity,getString(R.string.access_denied),AlertBox.Type.ERROR);
            }else {
                Intent intent = new Intent(this, ItineraryActivity.class);
                startActivity(intent);
            }
        }
        if (id == R.id.action_invoice) {
            if(Common.getSession(mBaseContext).getInt(getString(R.string.SESSION_ID),0)==0){
                AlertBox.Show(mBaseActivity,getString(R.string.access_denied),AlertBox.Type.ERROR);
            }else {
                Intent intent = new Intent(this, InvoiceActivity.class);
                startActivity(intent);
            }
        }
        if (id == R.id.action_register) {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        }
        if (id == R.id.action_login) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        if (id == R.id.action_quick_info) {
            SharedPreferences sharedPref = getSharedPreferences(getString(R.string.application_id), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("WELCOME_PAGE_SEEN",true).apply();
            Intent intent = new Intent(this, Intro.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        if (id == R.id.action_about) {
            Common.about(this);
        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Fragment
     **/
    public static class BrowserFragment extends Fragment {
        private Activity mActivity;
        private Context mContext;
        private WebView webView;

        public BrowserFragment() {}

        public static BrowserFragment newInstance() {
            return new BrowserFragment();
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mActivity = getActivity();
            mContext = mActivity.getBaseContext();
        }

        @SuppressLint("SetJavaScriptEnabled")
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View viewRoot = inflater.inflate(R.layout.browser_layout, container, false);

            webView = viewRoot.findViewById(R.id.web_view);
            webView.loadUrl(mUrl);
            webView.getSettings().setJavaScriptEnabled(true);

            webView.setWebViewClient(new WebViewClient(){
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    super.shouldOverrideUrlLoading(view, request);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        webView.loadUrl(request.getUrl().toString());
                    }
                    return true;
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String urlNewString) {
                    super.shouldOverrideUrlLoading(view, urlNewString);
                    webView.loadUrl(urlNewString);
                    return true;
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    Common.showLoadingV1(mActivity, getResources().getString(R.string.loading));
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    Common.closeDialogV1();
                }
            });

            return viewRoot;
        }
    }
}
