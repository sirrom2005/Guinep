package com.trafalgartmc.guinep;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
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

import com.trafalgartmc.guinep.Adapters.ItineraryDetailAdapter;
import com.trafalgartmc.guinep.Classes.FileDownloader;
import com.trafalgartmc.guinep.Classes.ItineraryData;
import com.trafalgartmc.guinep.Settings.SettingsActivity;
import com.trafalgartmc.guinep.Utility.Common;
import com.trafalgartmc.guinep.Utility.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import static com.trafalgartmc.guinep.Adapters.ItineraryAdapter.INVOICE_CODE;

public class ItineraryDetailActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_STORAGE_ONLY = 2002;
    private static int key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Write task
        super.onCreate(savedInstanceState);
        setContentView(R.layout.itinerary_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        key = getIntent().getIntExtra(INVOICE_CODE,0);

        //Show toolbar text only when minimized
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setExpandedTitleColor(Color.argb(0,0,0,0)); // transperent color = #00000000
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE); //Color of your title

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, ItineraryDetailFragment.newInstance())
                    .commit();
        }

        final SwipeRefreshLayout swipe = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipe.setProgressViewOffset(false, 0,getResources().getDimensionPixelSize(R.dimen.refresher_offset_end));
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe.setRefreshing(false);
            }
        });

        FloatingActionButton downloadFab = (FloatingActionButton) findViewById(R.id.download_fab);
        downloadFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadFile();
            }
        });
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home){
            // Press Back Icon
            onBackPressed();
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

    private void downloadFile() {
        if(ContextCompat.checkSelfPermission(getBaseContext(),android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        {
            downloadFileAction();
        }else{
            ActivityCompat.requestPermissions(ItineraryDetailActivity.this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_STORAGE_ONLY);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_STORAGE_ONLY:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    downloadFile();
                }
            break;
        }
    }

    private void downloadFileAction() {
        if(Utility.isConnected(getApplicationContext())) {
            FileDownloader.Download(ItineraryDetailActivity.this,
                                    "itinerary_" + key + ".pdf",
                                    Common.API_SERVER + "download_itinerary.php?key=" + key);
        }
    }

    /**
     * Fragment
     * */
    public static class ItineraryDetailFragment extends Fragment {
        private Activity mActivity;
        private Context mContext;
        private ItineraryDetailAdapter mAdapter;
        public static List<ItineraryData> mObj;

        public ItineraryDetailFragment(){}

        public static ItineraryDetailFragment newInstance()
        {
            return new ItineraryDetailFragment();
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mActivity = getActivity();
            mContext = mActivity.getApplicationContext();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.recycler_view, container, false);
            RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);

            mAdapter = new ItineraryDetailAdapter(mActivity);
            recyclerView.setAdapter(mAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(mActivity.getApplicationContext()));

            loadData();
            return rootView;
        }

        private void loadData() {
            AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
                OkHttpClient client = new OkHttpClient();
                Request request;

                @Override
                protected String doInBackground(Void... params) {
                    RequestBody requestBody = new FormBody.Builder()
                            .add("key", String.valueOf(key))
                            .build();

                    request = new Request.Builder()
                            .url(Common.API_SERVER + "mobile_itinerary_detail.php")
                            .post(requestBody)
                            .build();
                    try {
                        return client.newCall(request).execute().body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(String data) {
                    super.onPostExecute(data);
                    mObj = new ArrayList<>();
                    if(data!=null) {
                        try {
                            JSONArray list = new JSONObject(data).getJSONArray("list");
                            for(int i=0; i<list.length(); i++){
                                mObj.add(new ItineraryData(
                                        list.getJSONObject(i).getInt("invoiceno"),
                                        list.getJSONObject(i).getString("recordloca"),
                                        list.getJSONObject(i).getString("classofsvc"),
                                        list.getJSONObject(i).getString("dap_airport"),
                                        list.getJSONObject(i).getString("d_latitude"),
                                        list.getJSONObject(i).getString("d_longitude"),
                                        list.getJSONObject(i).getString("a_airport"),
                                        list.getJSONObject(i).getString("a_latitude"),
                                        list.getJSONObject(i).getString("a_longitude"),
                                        list.getJSONObject(i).getString("destination"),
                                        list.getJSONObject(i).getString("flightno"),
                                        list.getJSONObject(i).getString("departcityname"),
                                        list.getJSONObject(i).getString("departdate"),
                                        list.getJSONObject(i).getString("departtime"),
                                        list.getJSONObject(i).getString("cityname"),
                                        list.getJSONObject(i).getString("arrivedate"),
                                        list.getJSONObject(i).getString("arrivetime"),
                                        list.getJSONObject(i).getString("airlinename")
                                ));
                            }
                        } catch (JSONException e) {
                            Log.e(Common.LOG_TAG,e.getMessage());
                        }
                    }
                    //
                    mAdapter.loadData(mObj);
                    mAdapter.notifyDataSetChanged();
                }
            };
            if(Utility.isConnected(mContext)) {
                Common.showLoading(mActivity, getResources().getString(R.string.loading));
                Log.e(Common.LOG_TAG,getResources().getString(R.string.loading));
                task.execute();
                Common.closeDialog(mActivity);
            }
        }
    }
}
