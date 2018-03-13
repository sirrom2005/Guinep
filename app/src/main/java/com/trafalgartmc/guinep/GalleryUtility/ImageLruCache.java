package com.trafalgartmc.guinep.GalleryUtility;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.util.LruCache;

/**
 * Created by: iceman
 * Date: 8/14/2015
 */
public class ImageLruCache {
    private  static final String LOG_TAG = ImageLruCache.class.getSimpleName();
    private LruCache<String, Bitmap> mMemoryCache;

    public ImageLruCache(){
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory()/1024);
        final int cacheSize = maxMemory/10;
        mMemoryCache =  new LruCache<String, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount()/1024;
            }
        };
        Log.d(LOG_TAG, "MAX MEMORY " + maxMemory/1000 + " mb -- " + cacheSize/1000 + " mb");
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            Log.d(LOG_TAG, "ADD ImageLruCache");
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    public void clearCache() {
        mMemoryCache.evictAll();
    }

    public static ImageLruCache getInstance() {
        // Search for, or create an instance of the non-UI RetainFragment
        //final RetainFragment mRetainFragment = findOrCreateRetainFragment(fragmentManager);
        // See if we already have an ImageCache stored in RetainFragment
        //ImageLruCache cache = (ImageLruCache) mRetainFragment.getObject();

       /* if (cache == null) {
            cache = new ImageLruCache();
            mRetainFragment.setObject(cache);
        }*/
        return new ImageLruCache();
    }

    /**
     * Locate an existing instance of this Fragment or if not found, create and
     * add it using FragmentManager.
     *
     * @param fm The FragmentManager manager to use.
     * @return The existing instance of the Fragment or the new instance if just
     *         created.
     */
    private static RetainFragment findOrCreateRetainFragment(FragmentManager fm) {
        //BEGIN_INCLUDE(find_create_retain_fragment)
        // Check to see if we have retained the worker fragment.
        RetainFragment mRetainFragment = (RetainFragment) fm.findFragmentByTag(LOG_TAG);

        // If not retained (or first time running), we need to create and add it.
        if (mRetainFragment == null) {
            mRetainFragment = new RetainFragment();
            fm.beginTransaction().add(mRetainFragment, LOG_TAG).commitAllowingStateLoss();
        }

        return mRetainFragment;
        //END_INCLUDE(find_create_retain_fragment)
    }

    /**
     * A simple non-UI Fragment that stores a single Object and is retained over configuration
     * changes. It will be used to retain the ImageCache object.
     */
    public static class RetainFragment extends Fragment {
        private Object mObject;

        /**
         * Empty constructor as per the Fragment documentation
         */
        public RetainFragment() {}

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Make sure this Fragment is retained over a configuration change
            setRetainInstance(true);
        }

        /**
         * Store a single object in this Fragment.
         *
         * @param object The object to store
         */
        public void setObject(Object object) {
            mObject = object;
        }

        /**
         * Get the stored object.
         *
         * @return The stored object
         */
        public Object getObject() {
            return mObject;
        }
    }
}
