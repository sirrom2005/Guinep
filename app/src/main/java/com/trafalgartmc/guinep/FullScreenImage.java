package com.trafalgartmc.guinep;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.trafalgartmc.guinep.Classes.DataObject;
import com.trafalgartmc.guinep.Classes.GalleryDataParser;
import com.trafalgartmc.guinep.GalleryUtility.FullScreenImageWorker;
import com.trafalgartmc.guinep.GalleryUtility.ImageWorker;
import com.trafalgartmc.guinep.Utility.Common;
import com.trafalgartmc.guinep.Utility.Utility;

import java.util.List;

public class FullScreenImage extends NoDrawerBaseActivity {
    private List<DataObject> mImageList;
    private int mImageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.full_screen_image_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ViewPager pager = (ViewPager) findViewById(R.id.pager);

        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.createShareIntent(mBaseActivity,
                        getString(R.string.share_look_at_photo),
                        mImageList.get(mImageId).getImage().replaceAll(getString(R.string.reg_large_img),""));
            }
        });

        mImageList = GalleryDataParser.getGalleryData(mBaseContext);
        Intent intent = getIntent();
        mImageId = intent.getIntExtra("IMAGE_LIST_KEY", 0);

        MyPagerAdapter mMyPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());

        pager.setAdapter(mMyPagerAdapter);
        pager.setOffscreenPageLimit(4);
        pager.setCurrentItem(mImageId);

        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(pager.getCurrentItem() + 1 + " of " + mImageList.size());
        }

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels){}

            @Override
            public void onPageSelected(int position){
                if (actionBar != null) {
                    actionBar.setTitle(position + 1 + " of " + mImageList.size());
                }
                mImageId = position;
            }

            @Override
            public void onPageScrollStateChanged(int state){}
        });
    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter {
        private MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            String[] images = { Common.GALLERY_LOCATION + mImageList.get(position).getImage(),
                                Common.GALLERY_LOCATION + mImageList.get(position).getImage().replaceAll(getString(R.string.reg_large_img),"")};

            return FullScreenImageFragment.newInstance(images);
        }

        @Override
        public int getCount() {
            return mImageList.size();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utility.delTmpFiles();
    }

    public static class FullScreenImageFragment extends Fragment {
        private static final String IMG_ARRAY = "com.trafalgartmc.guinep.gallery.IMG_URL";
        private FullScreenImageWorker mImageWorker;
        private String[] myImage;

        public static Fragment newInstance(String[] img) {
            FullScreenImageFragment f = new FullScreenImageFragment();
            Bundle args = new Bundle();
            args.putStringArray(IMG_ARRAY,img);
            f.setArguments(args);
            return f;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Bundle bundle = getArguments();
            myImage = bundle.getStringArray(IMG_ARRAY);
            String thumb = Utility.cleanImageName(myImage[0]);
            if(getContext().getFileStreamPath(thumb).exists()) {
                mImageWorker = new FullScreenImageWorker(getContext(), Utility.getImage(getContext(), thumb));
            }else{
                mImageWorker = new FullScreenImageWorker(getContext(), R.drawable.empty_photo);
            }
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.full_screen_image_fragment, container, false);
            ImageView imageView = (ImageView) rootView.findViewById(R.id.imageView);

            final SwipeRefreshLayout swipe = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh);
            swipe.setProgressViewOffset(false, 0,250);
            swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swipe.setRefreshing(false);
                }
            });

            mImageWorker.loadImage(myImage[1], imageView);
            return rootView;
        }
    }
}
