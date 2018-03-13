package com.trafalgartmc.guinep.GalleryUtility;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.trafalgartmc.guinep.Utility.Common;
import com.trafalgartmc.guinep.Utility.Utility;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by: iceman
 * Date: 8/11/2015
 */
public class ImageWorker {
    private static final String LOG_TAG = Common.LOG_TAG;
    private static Context mContext;
    private static ImageLruCache mCache;
    private static Bitmap mLoadingBitmap;
    private final Resources mResources;

    public ImageWorker(Context c, int imgPlaceHolder) {
        ImageWorker.mContext = c;
        mResources = mContext.getResources();
        if(mCache==null) {
            mCache = ImageLruCache.getInstance();
        }
        mLoadingBitmap = BitmapFactory.decodeResource(mResources, imgPlaceHolder);
    }

    public void loadImage(String url, ImageView imageView) {
        String name = Utility.cleanImageName(url);
        Bitmap bitmap = mCache.getBitmapFromMemCache(name);

        if(bitmap!=null){
            imageView.setImageBitmap(bitmap);
        }else{
            if(DownLoadImage.cancelPotentialWork(name, imageView)) {
                final DownLoadImage mDownLoadImage = new DownLoadImage(imageView, name, url);
                final DownLoadImage.AsyncDrawable asyncDrawable =
                        new DownLoadImage.AsyncDrawable(mResources, mLoadingBitmap, mDownLoadImage);
                imageView.setImageDrawable(asyncDrawable);
                mDownLoadImage.executeOnExecutor(DownLoadImage.DUAL_THREAD_EXECUTOR);
            }
        }
    }

    private static class DownLoadImage extends AsyncTask<Void,Void,Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        String file;
        String fileUrl;

        private DownLoadImage(ImageView view, String s, String url){
            //Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<>(view);
            file = s;
            fileUrl = url;
        }

        private static final ThreadFactory sThreadFactory = new ThreadFactory() {
            private final AtomicInteger mCount = new AtomicInteger(1);

            public Thread newThread(@NonNull Runnable r) {
                return new Thread(r, "AsyncTask #" + mCount.getAndIncrement());
            }
        };

        private static final Executor DUAL_THREAD_EXECUTOR = Executors.newFixedThreadPool(10, sThreadFactory);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            String fileName = file;
            URL req;
            Bitmap image = null;
            FileOutputStream fOut = null;

            if(mContext.getFileStreamPath(fileName).exists()) {
                return Utility.getImage(mContext, fileName);
            }
            else{
                try {
                    if(Utility.isConnected(mContext)) {
                        Log.e(LOG_TAG, "DOWNLOADING....."+fileUrl);
                        req = new URL(fileUrl);
                        InputStream input = new BufferedInputStream(req.openConnection().getInputStream());
                        input.mark(input.available());

                        //First decode with inJustDecodeBounds=true to check dimensions
                        final BitmapFactory.Options options = new BitmapFactory.Options();
                        /*options.inJustDecodeBounds = true;
                        BitmapFactory.decodeStream(input, null, options);
                        input.reset();*/
                        //Calculate inSampleSize
                        options.inSampleSize = 3;//calculateInSampleSize(options, 150, 200);
                        //Decode bitmap with inSampleSize set
                        options.inJustDecodeBounds = false;
                        image = BitmapFactory.decodeStream(input, null, options);

                        if (image != null) {
                            Log.e(LOG_TAG, "WRITE TTO DISK " + image);
                            mCache.addBitmapToMemoryCache(fileName, image);
                            fOut = mContext.openFileOutput(fileName, mContext.MODE_PRIVATE);
                            image.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                        } else {
                            Log.e(LOG_TAG, "NULL");
                            image = null;
                        }
                    }
                }catch (OutOfMemoryError e) {
                    Log.e(LOG_TAG, "OutOfMemoryError " + fileName + " " + fileUrl);
                    e.printStackTrace();
                }
                catch (FileNotFoundException e) {
                    Log.e(LOG_TAG, "FileNotFoundException " + fileName + " " + fileUrl);
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    try{
                        if(fOut!=null){
                            fOut.flush();
                            fOut.close();
                        }
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "here " + fileName);
                        e.printStackTrace();
                    }
                }
                return image;
            }
        }

        private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
            // Raw height and width of image
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;
            if (height > reqHeight || width > reqWidth) {
                final int halfHeight = height / 2;
                final int halfWidth = width / 2;
                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width larger than the requested height and width.
                int imgSizeMax = Math.max(height,width);
                int deviseMin = Math.min(halfHeight, halfWidth);
                while((imgSizeMax / inSampleSize) > deviseMin) {
                    inSampleSize *= 2;
                }
            }
            Log.e(LOG_TAG, width + " - " + height + " - " + inSampleSize+"");
            return inSampleSize;
        }

        @Override
        protected void onPostExecute(Bitmap img) {
            if (isCancelled()){ img = null; }

            if(img!=null){
                final ImageView imageView = imageViewReference.get();
                final DownLoadImage bitmapWorkerTask = getDownLoadImageTask(imageView);

                if (imageView != null) {
                    try {
                        //set image to view
                        imageView.setImageBitmap(bitmapWorkerTask.get());
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                    imageView.setVisibility(View.VISIBLE);
                }
            }
        }

        private static class AsyncDrawable extends BitmapDrawable {
            private final WeakReference<DownLoadImage> bitmapWorkerTaskReference;

            private AsyncDrawable(Resources res, Bitmap bitmap, DownLoadImage downLoadImage) {
                super(res, bitmap);
                bitmapWorkerTaskReference = new WeakReference<>(downLoadImage);
            }

            private DownLoadImage getDownLoadImageTask() {
                return bitmapWorkerTaskReference.get();
            }
        }

        private static DownLoadImage getDownLoadImageTask(ImageView imageView) {
            if (imageView != null) {
                final Drawable drawable = imageView.getDrawable();
                if (drawable instanceof AsyncDrawable) {
                    final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                    return asyncDrawable.getDownLoadImageTask();
                }
            }
            return null;
        }

        private static boolean cancelPotentialWork(String data, ImageView imageView) {
            final DownLoadImage bitmapWorkerTask = getDownLoadImageTask(imageView);

            if (bitmapWorkerTask != null) {
                final String bitmapData = bitmapWorkerTask.file;
                // If bitmapData is not yet set or it differs from the new data
                if (bitmapData.equals("") || bitmapData != data) {
                    // Cancel previous task
                    bitmapWorkerTask.cancel(true);
                } else {
                    // The same work is already in progress
                    return false;
                }
            }
            // No task associated with the ImageView, or an existing task was cancelled
            return true;
        }
    }
}
