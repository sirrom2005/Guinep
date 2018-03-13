package com.trafalgartmc.guinep;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.trafalgartmc.guinep.Adapters.NewsAdapter;
import com.trafalgartmc.guinep.Classes.NewsDataParser;
import com.trafalgartmc.guinep.Utility.Common;
import com.trafalgartmc.guinep.Utility.Utility;

public class NewsActivity extends NoDrawerBaseActivity {
    private NewsFragment newsFragment;
    private SwipeRefreshLayout mSwipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        newsFragment = NewsFragment.newInstance();
        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, newsFragment)
                    .commit();
        }

        //ImageView banner = (ImageView) findViewById(R.id.banner);
        //banner.getLayoutParams().height = Common.getBannerHeight(mBaseContext);

        mSwipe = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mSwipe.setProgressViewOffset(false, 0,getResources().getDimensionPixelSize(R.dimen.refresher_offset_end));
        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                newsFragment.listNewsArticles(mSwipe);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            newsFragment.listNewsArticles(mSwipe);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Fragment
     * */
    public static class NewsFragment extends Fragment {
        private Activity mActivity;
        private Context mContext;
        private NewsAdapter mNewsAdapter;

        public NewsFragment(){}

        public static NewsFragment newInstance(){
            return new NewsFragment();
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

            mNewsAdapter = new NewsAdapter(mActivity);
            recyclerView.setAdapter(mNewsAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            return rootView;
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            listNewsArticles(null);
        }

        public void listNewsArticles(final SwipeRefreshLayout swipe) {
            if(swipe==null){
                Common.showLoading(mActivity, getResources().getString(R.string.loading));
            }else {
                swipe.setRefreshing(true);
            }
            AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                        NewsDataParser.clearNewsData();
                        Common.saveDownloadedDataFile(mContext,
                                Common.NEWS_API,
                                Common.NEWS_FILE, true, swipe!=null);
                    return null;
                }

                @Override
                protected void onPostExecute(Void o) {
                    super.onPostExecute(o);
                    mNewsAdapter.loadData(mContext);
                    mNewsAdapter.notifyDataSetChanged();
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
