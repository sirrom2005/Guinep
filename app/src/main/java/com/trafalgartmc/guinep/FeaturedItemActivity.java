package com.trafalgartmc.guinep;

import android.app.Activity;
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

import com.trafalgartmc.guinep.Adapters.FeaturedLocationAdapter;
import com.trafalgartmc.guinep.Adapters.TravelSpecialsAdapter;
import com.trafalgartmc.guinep.Classes.DataObject;
import com.trafalgartmc.guinep.GalleryUtility.ImageWorker;
import com.trafalgartmc.guinep.Utility.Common;
import com.trafalgartmc.guinep.Utility.Utility;

/**
 * @author Rohan Morris
 * date 3/19/2017.
 */

public class FeaturedItemActivity extends NoDrawerBaseActivity {
    private String DESTINATION_DATA = "com.trafalgartmc.guinep.Location";
    private String IS_SPECIALS_SCREEN = "com.trafalgartmc.guinep.DataKey";
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
                    .add(R.id.container, FeaturedCountryFragment.newInstance())
                    .commit();
        }

        if(getSupportActionBar()!=null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageView banner = (ImageView) findViewById(R.id.banner);

        mIsSpecialScreen = getIntent().getBooleanExtra(IS_SPECIALS_SCREEN, false);

        String key = mIsSpecialScreen ? TravelSpecialsAdapter.SPECIALS_DATA : DESTINATION_DATA;

        if(mIsSpecialScreen){ mBaseToolbar.setTitle("Specials"); }
        obj = (DataObject) getIntent().getSerializableExtra(key);

        if(obj==null){
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        if(mImageWorker == null){
            mImageWorker = new ImageWorker(this, R.drawable.empty_photo);
        }
        String img = obj.getImage().replace(".","_640.");
        mImageWorker.loadImage(Common.API_SERVER + "images/test/" + img, banner);

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
    public static class FeaturedCountryFragment extends Fragment {
        private Activity mActivity;
        private Context mContext;
        public FeaturedCountryFragment() {}

        public static FeaturedCountryFragment newInstance() {
            return new FeaturedCountryFragment();
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mActivity = getActivity();
            mContext = mActivity.getApplicationContext();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View viewRoot = inflater.inflate(R.layout.featured_item, container, false);

            TextView title      = viewRoot.findViewById(R.id.title);
            TextView subTitle   = viewRoot.findViewById(R.id.sub_title);
            WebView webView     = viewRoot.findViewById(R.id.html);
            Button book         = viewRoot.findViewById(R.id.book);
            Button call         = viewRoot.findViewById(R.id.call);
            Button share        = viewRoot.findViewById(R.id.share);

            title.setText(obj.getTitle());
            subTitle.setText(obj.getSubTitle());
            webView.loadData(obj.getBody(), "text/html; charset=utf-8", "UTF-8");

            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String phone = (!obj.getPhone().isEmpty())? obj.getPhone() : mContext.getResources().getString(R.string.company_phone_number);
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
                    mActivity.startActivity(intent);
                }
            });
            book.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mActivity, BrowserActivity.class);
                    intent.putExtra(Common.URL,"https://www.trafalgaronline.com/");
                    mActivity.startActivity(intent);
                }
            });
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utility.createShareIntent(mActivity,
                            (mIsSpecialScreen)? mContext.getString(R.string.share_special, obj.getTitle()) : mContext.getString(R.string.share_feat_loc, obj.getTitle()),
                            obj.getImage());
                }
            });

            return viewRoot;
        }
    }
}
