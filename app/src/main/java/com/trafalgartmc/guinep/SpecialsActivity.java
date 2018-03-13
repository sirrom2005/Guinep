package com.trafalgartmc.guinep;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import com.trafalgartmc.guinep.Adapters.TravelSpecialsAdapter;
import com.trafalgartmc.guinep.Classes.SpecialsDataParser;
import com.trafalgartmc.guinep.Settings.SettingsActivity;
import com.trafalgartmc.guinep.Utility.Common;
import com.trafalgartmc.guinep.Utility.Utility;

public class SpecialsActivity extends MainBaseActivity{
    private SpecialsFragment specialsFragment;
    private SwipeRefreshLayout swipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Common.getSession(mBaseContext).getInt(getString(R.string.SESSION_ID),0)==0){
            Intent intent = new Intent(this, AccessDenied.class);
            startActivity(intent);
        }

        if(Utility.hasLolliPop()){
            getWindow().setSharedElementExitTransition(TransitionInflater.from(this).inflateTransition(R.transition.share_ele_transition));
        }

        specialsFragment = SpecialsFragment.newInstance();
        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, specialsFragment)
                    .commit();
        }

        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipe.setProgressViewOffset(false, 0,getResources().getDimensionPixelSize(R.dimen.refresher_offset_end));
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                specialsFragment.refreshSpecials(swipe);
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
            onBackPressed();
        }

        if (id == R.id.action_home) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_special) {
            return true;
        }
        if (id == R.id.action_chat) {
            Intent intent = new Intent(this, ChatConnectActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_photo_stream) {
            Intent intent = new Intent(this, PhotoStreamActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_refresh) {
            specialsFragment.refreshSpecials(swipe);
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

    /**
     * Fragment
     * */
    public static class SpecialsFragment extends Fragment {
        private TravelSpecialsAdapter travelSpecialsAdapter;
        private Activity mActivity;
        private Context mContext;
        public SpecialsFragment() {}

        public static SpecialsFragment newInstance() {
            return new SpecialsFragment();
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

            travelSpecialsAdapter = new TravelSpecialsAdapter(mActivity);
            recyclerView.setAdapter(travelSpecialsAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            return viewRoot;
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            refreshSpecials(null);
        }

        @Override
        public void onPause() {
            super.onPause();
        }

        public void refreshSpecials(final SwipeRefreshLayout swipe) {
            if(swipe==null){
                Common.showLoading(mActivity, getResources().getString(R.string.loading));
            }else {
                swipe.setRefreshing(true);
            }
            AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                 @Override
                protected Void doInBackground(Void... params) {
                     SpecialsDataParser.clearSpecialsData();
                     Common.saveDownloadedDataFile(  mContext,
                                                    Common.SPECIALS_API,
                                                    Common.SPECIALS_FILE, true, swipe!=null);
                    return null;
                }

                @Override
                protected void onPostExecute(Void o) {
                    super.onPostExecute(o);
                    travelSpecialsAdapter.loadData(mContext);
                    travelSpecialsAdapter.notifyDataSetChanged();
                    if(swipe==null){
                        Common.closeDialog(mActivity);
                    }else {
                        swipe.setRefreshing(false);
                    }
                }
            };
            if(Utility.isConnected(mContext)) {
                task.execute();
            }
        }
    }
}
