package com.trafalgartmc.guinep;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.trafalgartmc.guinep.Adapters.FeaturedLocationAdapter;
import com.trafalgartmc.guinep.Classes.AdsDataParser;
import com.trafalgartmc.guinep.Classes.LocationDataParser;
import com.trafalgartmc.guinep.Settings.SettingsActivity;
import com.trafalgartmc.guinep.Utility.AlertBox;
import com.trafalgartmc.guinep.Utility.Common;
import com.trafalgartmc.guinep.Utility.Utility;
import com.trafalgartmc.guinep.Welcome.Intro;

public class MainActivity extends MainBaseActivity {
    private HomeFragment homeFragment;
    private SwipeRefreshLayout mSwipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.application_id), Context.MODE_PRIVATE);
        if(!sharedPref.getBoolean(Common.WELCOME_PAGE_SEEN,false)){
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(mBaseActivity, Intro.class);
            startActivity(intent);
        }


        if (Utility.hasLolliPop()) {
            getWindow().setSharedElementExitTransition(TransitionInflater.from(mBaseContext).inflateTransition(R.transition.share_ele_transition));
        }

        homeFragment = HomeFragment.newInstance();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, homeFragment)
                    .commit();
        }

        mSwipe = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mSwipe.setProgressViewOffset(false, 0, getResources().getDimensionPixelSize(R.dimen.refresher_offset_end));
        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                homeFragment.getFeaturedInfo(mSwipe);
            }
        });
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
        if (id == R.id.action_home) {
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
            Intent intent = new Intent(mBaseActivity, PhotoStreamActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_refresh) {
            homeFragment.getFeaturedInfo(mSwipe);
            return true;
        }
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_about) {
            Common.about(mBaseActivity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Fragment
     * */
    public static class HomeFragment extends Fragment {
        private FeaturedLocationAdapter mLocationAdapter;
        private Activity mActivity;
        private Context mContext;


        public HomeFragment() {}

        public static HomeFragment newInstance() {
            return new HomeFragment();
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mActivity = getActivity();
            mContext = mActivity.getApplicationContext();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View viewRoot = inflater.inflate(R.layout.recycler_view, container, false);
            RecyclerView recyclerView = viewRoot.findViewById(R.id.recycler_view);

            mLocationAdapter = new FeaturedLocationAdapter(mActivity);

            recyclerView.setAdapter(mLocationAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            return viewRoot;
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            getFeaturedInfo(null);
        }

        @Override
        public void onResume() {
            super.onResume();
        }

        @Override
        public void onPause() {
            super.onPause();
        }

        public void getFeaturedInfo(final SwipeRefreshLayout swipe) {
            if(swipe==null){
                Common.showLoading(mActivity, getResources().getString(R.string.loading));
            }else {
                swipe.setRefreshing(true);
            }
            AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    LocationDataParser.clearLocationData();
                    AdsDataParser.clearAdsData();
                    if(Utility.isConnected(mContext)){
                        Common.saveDownloadedDataFile(mContext,
                                Common.DESTINATION_API,
                                Common.DESTINATION_FILE, true, swipe != null);
                        Common.saveDownloadedDataFile(mContext,
                                Common.ADS_API,
                                Common.ADS_FILE, true, swipe != null);
                    }else{
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, R.string.internet_unavailable, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void o) {
                    super.onPostExecute(o);
                    mLocationAdapter.loadData(mContext);
                    mLocationAdapter.notifyDataSetChanged();
                    if(swipe==null){
                        Common.closeDialog(mActivity);
                    }else {
                        swipe.setRefreshing(false);
                    }
                }
            };

            task.execute();
        }
    }
}
