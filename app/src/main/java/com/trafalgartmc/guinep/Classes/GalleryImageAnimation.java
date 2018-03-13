package com.trafalgartmc.guinep.Classes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.trafalgartmc.guinep.AdsActivity;
import com.trafalgartmc.guinep.FeaturedItemActivity;
import com.trafalgartmc.guinep.Utility.Common;
import com.trafalgartmc.guinep.Utility.Utility;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Rohan
 * Date 5/6/2017.
 */

public final class GalleryImageAnimation {
    private static Thread mSlidShowThread = null;
    private static boolean mSlidShowLoop;
    private static WeakReference<ImageView> imageViewWeakReference = null;
    private static WeakReference<Activity> activityWeakReference = null;
    private static WeakReference<CardView> cardViewWeakReference = null;
    private static int  current = 0,
                        next = 1;

    public GalleryImageAnimation() {}

    public static void init(Activity activity, ImageView vSlides, CardView vCardView) {
        imageViewWeakReference = new WeakReference<>(vSlides);
        cardViewWeakReference = new WeakReference<>(vCardView);
        activityWeakReference = new WeakReference<>(activity);
        start();
    }

    public static void start(){
        final Activity myActivity = activityWeakReference.get();
        final ImageView imageView = imageViewWeakReference.get();
        final CardView cardView = cardViewWeakReference.get();
        Log.e(Common.LOG_TAG, "myActivity " + myActivity + " imageViewWeakReference " + imageViewWeakReference );

        mSlidShowThread = new Thread(new Runnable() {
            @Override
            public void run() {
                final List<DataObject> imgList = AdsDataParser.getAdsList(myActivity.getApplicationContext());
                int size = (imgList==null)? 0 : imgList.size();
                if(size==0){
                    myActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cardView.setVisibility(View.GONE);
                        }
                    });
                }else{
                    final Bitmap[] bitmaps = new Bitmap[size];
                    //Download Image
                    for(int i=0; i< size; i++) {
                        Log.e(Common.LOG_TAG, Common.API_SERVER  + "images/" + imgList.get(i).getImage().replace(".","_480."));
                        bitmaps[i] = DownLoadImage(myActivity.getApplicationContext(), Common.API_SERVER  + "images/" + imgList.get(i).getImage().replace(".","_480."));
                        i++;
                    }

                    if(size==1){
                        Log.e(Common.LOG_TAG, size + " size " + bitmaps[0]);
                        myActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.e(Common.LOG_TAG, "The image " + bitmaps[0]);
                                imageView.setImageBitmap(bitmaps[0]);
                                imageView.refreshDrawableState();
                                imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(myActivity, imageView, "featured_photo");
                                        Intent intent = new Intent(myActivity, AdsActivity.class);
                                        intent.putExtra("com.trafalgartmc.guinep.ads.data",imgList.get(0));
                                        myActivity.startActivity(intent, optionsCompat.toBundle());
                                    }
                                });
                            }
                        });
                    }else{
                        final Drawable[] layers = new Drawable[2];
                        //Loop animation
                        try {
                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Log.e(Common.LOG_TAG, ">>>> " + current);
                                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(myActivity, imageView, "featured_photo");
                                    Intent intent = new Intent(myActivity, AdsActivity.class);
                                    intent.putExtra("com.trafalgartmc.guinep.ads.data",imgList.get(current));
                                    myActivity.startActivity(intent, optionsCompat.toBundle());
                                }
                            });

                            mSlidShowLoop = true;
                            while(mSlidShowLoop) {
                                synchronized(this) {
                                    layers[0] = new BitmapDrawable(myActivity.getResources(), bitmaps[current]);
                                    layers[1] = new BitmapDrawable(myActivity.getResources(), bitmaps[next]);
                                    myActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            TransitionDrawable transitionDrawable = new TransitionDrawable(layers);
                                            imageView.setImageDrawable(transitionDrawable);
                                            transitionDrawable.startTransition(1000);
                                        }
                                    });

                                    current++;
                                    next++;

                                    if(next == size) {
                                        next = 0;
                                    } else if(current == size) {
                                        current = 0;
                                    }

                                    mSlidShowThread.sleep(5000);
                                    Log.e(Common.LOG_TAG, current + " = " + next + " Threads " + Thread.activeCount() + " Name " + mSlidShowThread.getName());
                                }
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        mSlidShowThread.start();
    }

    public static void stop(){
        if(mSlidShowThread!=null) {
            boolean retry = true;
            while (retry){
                Log.e(Common.LOG_TAG, "alive" );
                if(mSlidShowThread.isAlive()){
                    retry = false;
                    mSlidShowThread.interrupt();
                    mSlidShowLoop = false;
                    mSlidShowThread = null;
                    imageViewWeakReference =  null;
                    activityWeakReference = null;
                }
            }
        }
    }

    private static Bitmap DownLoadImage(Context c, String url) {
        String fileName = Utility.cleanImageName(url);

        URL req = null;
        Bitmap image = null;
        if (!c.getFileStreamPath(fileName).exists()) {
            try {
                req = new URL(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {
                InputStream input = req.openConnection().getInputStream();
                image = BitmapFactory.decodeStream(input);
            } catch (IOException e) {
                e.printStackTrace();
            }

            FileOutputStream fOut = null;
            try {
                fOut = c.openFileOutput(fileName, c.MODE_PRIVATE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            image.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            try {
                fOut.flush();
                fOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return Utility.getImage(c, fileName);
    }
}
