package com.trafalgartmc.guinep;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.trafalgartmc.guinep.Adapters.TravelSpecialsAdapter;
import com.trafalgartmc.guinep.Classes.DataObject;
import com.trafalgartmc.guinep.GalleryUtility.ImageWorker;
import com.trafalgartmc.guinep.Utility.Common;
import com.trafalgartmc.guinep.Utility.Utility;

/**
 * @author Rohan Morris
 * date 3/19/2017.
 */

public class AdsActivity extends NoDrawerBaseActivity {
    private String ADS_DATA = "com.trafalgartmc.guinep.ads.data";
    private static DataObject obj;
    private ImageWorker mImageWorker;
    private static boolean mIsSpecialScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Utility.hasLolliPop()){
            getWindow().setSharedElementEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.share_ele_transition));
        }

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, AdsFragment.newInstance())
                    .commit();
        }

        if(getSupportActionBar()!=null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageView banner = (ImageView) findViewById(R.id.banner);

        obj = (DataObject) getIntent().getSerializableExtra(ADS_DATA);

        if(obj==null){
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        mBaseToolbar.setTitle(obj.getTitle());

        if(mImageWorker == null){
            mImageWorker = new ImageWorker(this, R.drawable.empty_photo);
        }
        String img = obj.getImage().replace(".", "_640.");
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
    public static class AdsFragment extends Fragment {
        public AdsFragment() {}

        public static AdsFragment newInstance() {
            return new AdsFragment();
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View viewRoot = inflater.inflate(R.layout.ads_item, container, false);

            TextView title = (TextView) viewRoot.findViewById(R.id.title);
            WebView webView = (WebView) viewRoot.findViewById(R.id.html);

            title.setText(obj.getTitle());
            webView.loadData(obj.getBody(), "text/html; charset=utf-8", "UTF-8");

            return viewRoot;
        }
    }
}
