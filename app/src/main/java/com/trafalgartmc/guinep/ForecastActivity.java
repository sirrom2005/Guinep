package com.trafalgartmc.guinep;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.trafalgartmc.guinep.Adapters.ForecastAdapter;
import com.trafalgartmc.guinep.Classes.WeatherDataParser;
import com.trafalgartmc.guinep.Settings.SettingsActivity;
import com.trafalgartmc.guinep.Utility.Common;
import com.trafalgartmc.guinep.Utility.Utility;

public class ForecastActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_DEVICE_LOCATION  = 2020;
    private SwipeRefreshLayout swipe;
    private ForecastFragment forecastFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forecast_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        forecastFragment = ForecastFragment.newInstance();
        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, forecastFragment)
                    .commit();
        }

        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipe.setProgressViewOffset(false, 0, getResources().getDimensionPixelSize(R.dimen.refresher_offset_end));
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                forecastFragment.refreshWeather(swipe);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mini_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home){
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_refresh) {
            forecastFragment.refreshWeather(swipe);
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

    /**
     * Fragment
     * */
    public static class ForecastFragment extends Fragment {
        private Activity mActivity;
        private Context mContext;
        private ForecastAdapter forecastAdapter;
        private float lat, lon;

        public ForecastFragment(){}

        public static ForecastFragment newInstance(){ return new ForecastFragment();}

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mActivity = getActivity();
            mContext = mActivity.getBaseContext();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.recycler_view, container, false);
            RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);

            forecastAdapter = new ForecastAdapter(mContext);
            recyclerView.setAdapter(forecastAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

            return rootView;
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            downloadForcast();
        }

        public void refreshWeather(final SwipeRefreshLayout swipe){
            if(swipe==null){
                Common.showLoading(mActivity, getResources().getString(R.string.loading));
            }else{
                swipe.setRefreshing(true);
            }
            AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    if(Utility.isConnected(mContext)) {
                        WeatherDataParser.clearWeatherData();
                        if(lat==0){
                            lat = Common.getlatitude(mContext);
                            lon = Common.getlongitude(mContext);
                        }

                        Common.saveDownloadedDataFile(mContext,
                                Common.FORECAST_FOR_TODAY + getString(R.string.openweathermap_api_key) + "&lat=" + lat + "&lon=" + lon,
                                Common.FORECAST_FOR_TODAY_FILE, false, true);

                        Common.saveDownloadedDataFile(mContext,
                                Common.FORECAST + getString(R.string.openweathermap_api_key) + "&lat=" + lat + "&lon=" + lon,
                                Common.FORECAST_FILE, false, true);

                        return null;
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    forecastAdapter.loadData(mContext);
                    forecastAdapter.notifyDataSetChanged();
                    if(swipe==null){
                        Common.closeDialog(mActivity);
                    }else{
                        swipe.setRefreshing(false);
                    }
                }
            };
            if(Utility.isConnected(mContext)) {
                task.execute();
            }
        }

        private void downloadForcast(){
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Check Permissions Now
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSIONS_REQUEST_DEVICE_LOCATION);
            } else {
                // permission has been granted, continue as usual
                // Provides a simple way of getting a device's location and is well suited for
                // applications that do not require a fine-grained location and that do not need location
                // updates. Gets the best and most recent location currently available, which may be null
                // in rare cases when a location is not available.
            /*Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                Log.e(Common.LOG_TAG, mLastLocation.getLatitude() + " -- " + mLastLocation.getLongitude());
                Toast.makeText(getBaseContgext(),mLastLocation.getLatitude() + " -- " + mLastLocation.getLongitude(),Toast.LENGTH_LONG).show();
            } else {
                Log.e(Common.LOG_TAG,"mLastLocation = null");
            }*/

                LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            /*locationManager.requestLocationUpdates(locationManager.getBestProvider(new Criteria(), true), 0, 0, new LocationListener() {
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    // called when the location provider status changes. Possible status: OUT_OF_SERVICE, TEMPORARILY_UNAVAILABLE or AVAILABLE.
                    Log.e(Common.LOG_TAG, provider + " >> " + status + " onStatusChanged ");
                }
                public void onProviderEnabled(String provider) {
                    // called when the location provider is enabled by the user
                    Log.e(Common.LOG_TAG, provider + " onProviderEnabled ");
                }
                public void onProviderDisabled(String provider) {
                    // called when the location provider is disabled by the user. If it is already disabled, it's called immediately after requestLocationUpdates
                    Log.e(Common.LOG_TAG, provider + " onProviderDisabled ");
                }

                public void onLocationChanged(Location location) {
                    Log.e(Common.LOG_TAG, location + " onLocationChanged ");
                    double latitute = location.getLatitude();
                    double longitude = location.getLongitude();
                    // do whatever you want with the coordinates
                    Log.e(Common.LOG_TAG, latitute + " -- " + longitude);
                    Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
                    try {
                        List<Address> addresses = gcd.getFromLocation(latitute, longitude, 5);

                        Log.e(Common.LOG_TAG, addresses.size() + " Size");
                        if (addresses.size() > 0) {
                            for(int i=0;i<addresses.size(); i++){
                                Log.e(Common.LOG_TAG, addresses.get(i).getLocale() + " - " + addresses.get(i).getLocality() + " - " + addresses.get(i).getSubLocality());
                            }
                            Log.e(Common.LOG_TAG, addresses.get(0).getLocale() + " - " + addresses.get(0).getLocality() + " - " + addresses.get(0).getSubLocality());
                            //String cityName = addresses.get(0).getLocality();
                        }
                    } catch (IOException e) {
                        Log.e(Common.LOG_TAG, ">>> " + e.getMessage());
                    }
                }
            });*/

                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (lastKnownLocation != null) {
                    SharedPreferences sharedPref = mContext.getSharedPreferences(getString(R.string.application_id), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    lat = (float) lastKnownLocation.getLatitude();
                    lon = (float) lastKnownLocation.getLongitude();
                    editor.putFloat("Latitude",  lat);
                    editor.putFloat("Longitude", lon);
                    editor.apply();
                    Log.e(Common.LOG_TAG, lastKnownLocation.getLatitude() + " -- " + lastKnownLocation.getLongitude());
                }
            }
            refreshWeather(null);
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (requestCode == MY_PERMISSIONS_REQUEST_DEVICE_LOCATION) {
                if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    downloadForcast();
                }
            }
        }
    }
}
