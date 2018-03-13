package com.trafalgartmc.guinep.Welcome;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.trafalgartmc.guinep.MainActivity;
import com.trafalgartmc.guinep.R;
import com.trafalgartmc.guinep.RegisterActivity;
import com.trafalgartmc.guinep.SyncAdapter.SyncAdapter;
import com.trafalgartmc.guinep.Utility.AlertBox;
import com.trafalgartmc.guinep.Utility.Common;
import com.trafalgartmc.guinep.Utility.Utility;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Intro extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_DEVICE_LOCATION = 2020;
    private LinearLayout mTabLayout;
    private ViewPager mViewPager;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        mTabLayout = (LinearLayout) findViewById(R.id.viewPagerTabs);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        drawPageSelectionIndicators(0);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels){}

            @Override
            public void onPageSelected(int position) { drawPageSelectionIndicators(position); }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.application_id), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(Common.WELCOME_PAGE_SEEN,true).apply();

        SyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    public void onBackPressed(){}

    private void drawPageSelectionIndicators(int mPosition){
        if(mTabLayout!=null) {
            mTabLayout.removeAllViews();
        }

        int length = mSectionsPagerAdapter.getCount();
        ImageButton[] dots = new ImageButton[length];
        for (int i = 0; i < length; i++) {
            dots[i] = new ImageButton(getApplicationContext());
            if(i==mPosition)
                dots[i].setImageDrawable(ContextCompat.getDrawable(this,R.drawable.item_selected));
            else
                dots[i].setImageDrawable(ContextCompat.getDrawable(this,R.drawable.item_unselected));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(0, 0, 0, 0);
            dots[i].setBackgroundColor(Color.argb(0,0,0,0));
            dots[i].setPadding(10, 10, 10, 10);
            mTabLayout.addView(dots[i],params);

            final int finalI = i;
            dots[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mViewPager.setCurrentItem(finalI);
                }
            });
        }
    }

    private void updateSlide(int next) {
        mViewPager.setCurrentItem(next,true);
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        private Intro mContext;

        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {}

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment;
            fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mContext = (Intro)getActivity();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_intro, container, false);

            TextView introTitle = rootView.findViewById(R.id.intro_header);
            TextView introBody  = rootView.findViewById(R.id.intro_text);
            Button btn          = rootView.findViewById(R.id.btn);
            Button register     = rootView.findViewById(R.id.register);

            final int position  = getArguments().getInt(ARG_SECTION_NUMBER);
            String[] title      = getResources().getStringArray(R.array.intro_title);
            String[] btn_text   = getResources().getStringArray(R.array.btn_text);
            final String[] body = getResources().getStringArray(R.array.intro_body);

            introTitle.setText(title[position]);
            introBody.setText(body[position]);
            btn.setText(btn_text[position]);

            if(position==3){register.setVisibility(View.VISIBLE);}
            if(position == 2) {
                if (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_DEVICE_LOCATION);
                } else {
                    getLocation();
                }
            }

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int next = position+1;
                    if (next < 4) {
                        mContext.updateSlide(next);
                    } else {
                        Intent intent = new Intent(mContext, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        mContext.startActivity(intent);
                    }
                }
            });

            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, RegisterActivity.class);
                    startActivity(intent);
                }
            });

            return rootView;
        }


        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            if (requestCode == MY_PERMISSIONS_REQUEST_DEVICE_LOCATION) {
                if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                }
                mContext.updateSlide(2);
            }
        }

        public void getLocation() {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
                if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (lastKnownLocation != null) {
                        SharedPreferences sharedPref = mContext.getSharedPreferences(getString(R.string.application_id), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        final float lat = (float) lastKnownLocation.getLatitude();
                        final float lon = (float) lastKnownLocation.getLongitude();
                        editor.putFloat("Latitude",  lat);
                        editor.putFloat("Longitude", lon);
                        editor.apply();
                        Log.e(Common.LOG_TAG, lastKnownLocation.getLatitude() + " -- " + lastKnownLocation.getLongitude());

                        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... params) {
                                Common.saveDownloadedDataFile(mContext, Common.ADS_API, Common.ADS_FILE, true, true);

                                Common.saveDownloadedDataFile(mContext,
                                        Common.FORECAST_FOR_TODAY + getString(R.string.openweathermap_api_key) + "&lat=" + lat + "&lon=" + lon,
                                        Common.FORECAST_FOR_TODAY_FILE, false, true);

                                Common.saveDownloadedDataFile(mContext,
                                        Common.FORECAST + getString(R.string.openweathermap_api_key) + "&lat=" + lat + "&lon=" + lon,
                                        Common.FORECAST_FILE, false, true);

                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void o) {
                                super.onPostExecute(o);

                            }
                        };
                        if(Utility.isConnected(mContext)) {
                            task.execute();
                        }
                    }
                }
            }
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return 4;
        }
    }
}
