package com.trafalgartmc.guinep;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.trafalgartmc.guinep.Adapters.ChatAdapter;
import com.trafalgartmc.guinep.Classes.Forecast;
import com.trafalgartmc.guinep.Classes.NetWorkHelper;
import com.trafalgartmc.guinep.Classes.WeatherDataParser;
import com.trafalgartmc.guinep.Settings.SettingsActivity;
import com.trafalgartmc.guinep.Utility.AlertBox;
import com.trafalgartmc.guinep.Utility.Common;
import com.trafalgartmc.guinep.Welcome.Intro;

import java.lang.ref.WeakReference;
import java.util.List;

import core.ChatObject;

public class ChatActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private WeakReference<Drawable> drawableWeakReference = null;
    private DrawerLayout mDrawer;
    private AlertDialog alert;
    private Button action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_min_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, ChatFragment.newInstance())
                    .commit();
        }

        NetWorkHelper.agentId = getIntent().getStringExtra(NetWorkHelper.AGENT_ID);

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
        TextView appUser = nav.findViewById(R.id.app_user);
        TextView degree  = nav.findViewById(R.id.today_degree);
        ImageView weatherIcon   = nav.findViewById(R.id.today_weather_icon);
        final ImageView profile = nav.findViewById(R.id.profile);

        appUser.setText(Common.getSession(mBaseContext).getString(getString(R.string.SESSION_NAME),
                getString(R.string.pref_default_display_name)));

        Common.setProfilePhoto(getApplicationContext(), profile);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Show(ChatActivity.this, getString(R.string.exit_chat), AlertBox.Type.TWO_BUTTON);
                action.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        NetWorkHelper.disconnect();
                        alert.dismiss();
                        if(Common.getSession(mBaseContext).getInt(getString(R.string.SESSION_ID),0)==0){
                            Intent intent = new Intent(mBaseContext, LoginActivity.class);
                            startActivity(intent);
                        }else {
                            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(ChatActivity.this, profile, "profile_photo");
                            Intent intent = new Intent(ChatActivity.this, UpdateProfileActivity.class);
                            startActivity(intent, optionsCompat.toBundle());
                        }
                    }
                });
            }
        });

        if(weatherData!=null) {
            RelativeLayout nav_weather = nav.findViewById(R.id.nav_weather);
            //set degree
            degree.setText(Common.formatTemperature(mBaseContext, Float.parseFloat(weatherData.get(0).getTemp()), Common.isMetric(mBaseContext)));
            //set icon
            weatherIcon.setImageDrawable(getDrawableIcon(weatherData.get(0).getIcon()));

            nav_weather.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDrawer.closeDrawer(GravityCompat.START);
                    Intent intent = new Intent(mBaseContext, ForecastActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    private Drawable getDrawableIcon(String icon) {
        if(drawableWeakReference==null){
            drawableWeakReference = new WeakReference<>(ContextCompat.getDrawable(ChatActivity.this, Common.getWeatherIcon(icon)));
        }
        return drawableWeakReference.get();
    }

    @Override
    public void onBackPressed(){
        AlertBox.Show(this, getString(R.string.exit_chat), AlertBox.Type.TWO_BUTTON);
        AlertBox.action.setText("Ok");
        AlertBox.action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NetWorkHelper.disconnect();
                AlertBox.close();
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        final int id = item.getItemId();

        if (id == R.id.action_chat) {
            return false;
        }

        final boolean[] exit = {false};
        Show(this, getString(R.string.exit_chat), AlertBox.Type.TWO_BUTTON);
        action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NetWorkHelper.disconnect();
                alert.dismiss();
                exit[0] = true;

                if (id == R.id.action_home) {
                    Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                if (id == R.id.action_special) {
                    Intent intent = new Intent(ChatActivity.this, SpecialsActivity.class);
                    startActivity(intent);
                }
                if (id == R.id.action_photo_stream) {
                    Intent intent = new Intent(ChatActivity.this, PhotoStreamActivity.class);
                    startActivity(intent);
                }
                if (id == R.id.action_settings) {
                    Intent intent = new Intent(ChatActivity.this, SettingsActivity.class);
                    startActivity(intent);
                }
                if (id == R.id.action_about) {
                    Common.about(ChatActivity.this);
                }
                finish();
            }
        });

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        /*Bundle bundle = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
        }*/
        // Handle navigation view item clicks here.
        final int id = item.getItemId();

        if (id == R.id.action_about) {
            Common.about(this);
            return true;
        }

        final boolean[] exit = {false};
        Show(this, getString(R.string.exit_chat), AlertBox.Type.TWO_BUTTON);
        action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NetWorkHelper.disconnect();
                alert.dismiss();
                exit[0] = true;

                if (id == R.id.action_news) {
                    Intent intent = new Intent(ChatActivity.this, NewsActivity.class);
                    startActivity(intent);
                }
                if (id == R.id.action_itinerary) {
                    Intent intent = new Intent(ChatActivity.this, ItineraryActivity.class);
                    startActivity(intent);
                }
                if (id == R.id.action_invoice) {
                    Intent intent = new Intent(ChatActivity.this, InvoiceActivity.class);
                    startActivity(intent);
                }
                if (id == R.id.action_register) {
                    Intent intent = new Intent(ChatActivity.this, RegisterActivity.class);
                    startActivity(intent);
                }
                if (id == R.id.action_login) {
                    Intent intent = new Intent(ChatActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                if (id == R.id.action_settings) {
                    Intent intent = new Intent(ChatActivity.this, SettingsActivity.class);
                    startActivity(intent);
                }
                if (id == R.id.action_quick_info) {
                    SharedPreferences sharedPref = getSharedPreferences(getString(R.string.application_id), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("WELCOME_PAGE_SEEN",true).apply();
                    Intent intent = new Intent(ChatActivity.this, Intro.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                finish();
            }
        });

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // TO DO - Refactor
    private void Show(Context c,String message,AlertBox.Type type){
        View view = View.inflate(c, R.layout.alert_box_layout, null);

        ImageView icon = view.findViewById(R.id.about);
        TextView mes = view.findViewById(R.id.message);
        action = view.findViewById(R.id.action);

        int img = (type == AlertBox.Type.NORMAL) ? R.drawable.icon_success : R.drawable.icon_error;

        icon.setImageDrawable(ContextCompat.getDrawable(c,img));
        mes.setText(message);

        AlertDialog.Builder dialog = new AlertDialog.Builder(c);
        alert = dialog.create();
        alert.setView(view);
        alert.setCancelable(false);
        alert.show();

        if(type == AlertBox.Type.TWO_BUTTON){
            action.setText("Ok");
            action = view.findViewById(R.id.action);
            action.setVisibility(View.VISIBLE);
        }

        final Button cancel = view.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
            }
        });
    }

    /**
     * Fragment
     **/
    public static class ChatFragment extends Fragment {
        private Activity mActivity;
        private Context mContext;
        private ChatAdapter cAdapter;
        private NetWorkHelper netWorkHelper;
        private RecyclerView recyclerView;

        public ChatFragment() {}

        public static ChatFragment newInstance() {
            return new ChatFragment();
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mActivity = getActivity();
            mContext = mActivity.getApplicationContext();
            cAdapter = new ChatAdapter(mActivity);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View viewRoot = inflater.inflate(R.layout.chat_layout, container, false);

            final TextView message = viewRoot.findViewById(R.id.message);
            recyclerView = viewRoot.findViewById(R.id.msg_area);

            recyclerView.setAdapter(cAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

            message.setHorizontallyScrolling(false);
            message.setMaxLines(1000);
            message.setMaxLines(3);
            message.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                    boolean handled = false;
                    if(actionId == EditorInfo.IME_ACTION_SEND && message.getText().toString().trim().length() > 0 ) {
                        NetWorkHelper.sendMsgToSever(
                                new ChatObject( message.getText().toString(),
                                        NetWorkHelper.myId,
                                        NetWorkHelper.agentId,
                                        NetWorkHelper.username,
                                        ChatObject.MESSAGE));

                    }else{
                        Toast.makeText(mContext, R.string.error_empty_msg,Toast.LENGTH_SHORT).show();
                    }
                    handled = true;
                    message.setText(null);
                    return handled;
                }
            });

            Log.e(Common.LOG_TAG, "NET STATE IS RUNNING>> " + NetWorkHelper.isClientRunning());
            if(!NetWorkHelper.isClientRunning()) {
                netWorkHelper = new NetWorkHelper(mActivity, cAdapter, recyclerView);
                netWorkHelper.start();
            }
            return viewRoot;
        }
    }
}
