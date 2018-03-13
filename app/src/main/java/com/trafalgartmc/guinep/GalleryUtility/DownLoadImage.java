package com.trafalgartmc.guinep.GalleryUtility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.trafalgartmc.guinep.Utility.Common;
import com.trafalgartmc.guinep.Utility.Utility;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;

/**
 * Created by Rohan
 * Date 7/5/2017.
 */
public class DownLoadImage extends AsyncTask<String,Void,Bitmap> {
    private final Context c;

    public DownLoadImage(Context c){
        this.c = c;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        String fileUrl = params[0];
        String fileName = Utility.cleanImageName(fileUrl);

        if(!c.getFileStreamPath(fileName).exists()) {
            try {
                URL req = new URL(fileUrl);
                InputStream input = req.openConnection().getInputStream();
                Bitmap image = BitmapFactory.decodeStream(input);

                FileOutputStream fOut = c.openFileOutput(fileName, Context.MODE_PRIVATE);

                image.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.flush();
                fOut.close();
            } catch (IOException e) {
                Log.e(Common.LOG_TAG, e.getMessage());
                return null;
            }
        }
        return Utility.getImage(c, fileName);
    }

    @Override
    protected void onPostExecute(Bitmap aVoid) {
        super.onPostExecute(aVoid);
    }
}