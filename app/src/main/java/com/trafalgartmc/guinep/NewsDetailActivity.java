package com.trafalgartmc.guinep;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.trafalgartmc.guinep.Adapters.NewsAdapter;
import com.trafalgartmc.guinep.Classes.DataObject;
import com.trafalgartmc.guinep.GalleryUtility.ImageWorker;
import com.trafalgartmc.guinep.Utility.Common;
import com.trafalgartmc.guinep.Utility.Utility;

public class NewsDetailActivity extends NoDrawerBaseActivity {
    private static DataObject obj;
    private ImageWorker mImageWorker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Utility.hasLolliPop()){
            getWindow().setSharedElementEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.share_ele_transition));
        }

        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, NewsDetailFragment.newInstance())
                    .commit();
        }

        obj = (DataObject) getIntent().getSerializableExtra(NewsAdapter.NEWS_DATA);

        if(mImageWorker == null){
            mImageWorker = new ImageWorker(this, R.drawable.empty_photo);
        }
        ImageView banner = (ImageView) findViewById(R.id.banner);
        String img = obj.getImage().replace(".","_640.");
        mImageWorker.loadImage(Common.API_SERVER + "images/" + img, banner);

        final SwipeRefreshLayout swipe = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipe.setProgressViewOffset(false, 0,getResources().getDimensionPixelSize(R.dimen.refresher_offset_end));
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe.setRefreshing(false);
            }
        });
    }

    /**
     * Fragment
     * */
    public static class NewsDetailFragment extends Fragment {
        public NewsDetailFragment(){}

        public static NewsDetailFragment newInstance()
        {
            return new NewsDetailFragment();
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.featured_item, container, false);
            LinearLayout option_bar = (LinearLayout) rootView.findViewById(R.id.option_bar);

            TextView title  = (TextView) rootView.findViewById(R.id.title);
            TextView date   = (TextView) rootView.findViewById(R.id.sub_title);
            WebView webView = (WebView) rootView.findViewById(R.id.html);

            title.setText(obj.getTitle());
            date.setText((obj.getSubTitle()));
            webView.loadData(obj.getBody(), "text/html; charset=utf-8", "UTF-8");

            option_bar.setVisibility(View.GONE);
            return rootView;
        }
    }
}
