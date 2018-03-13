package com.trafalgartmc.guinep.Classes;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.trafalgartmc.guinep.R;
import com.trafalgartmc.guinep.Utility.Common;
import com.trafalgartmc.guinep.Utility.Utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by rohan
 * Date on 6/21/2017.
 */

public class FileDownloader {

    public static void Download(final Activity activity, final String fileName, final String url)
    {
        Common.showLoading(activity, activity.getBaseContext().getResources().getString(R.string.downloading));
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                try {
                    File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    File file = new File(dir,fileName);

                    if(file.exists()){ return true; }
                    Response response = client.newCall(request).execute();
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(response.body().bytes());
                    fos.close();
                    return true;
                } catch (IOException e) {
                    Log.e(Common.LOG_TAG, e.getMessage());
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean b) {
                super.onPostExecute(b);
                Common.closeDialog(activity);
                if(b){
                    openFile(activity, fileName);
                }
            }
        };
        if(Utility.isConnected(activity)) {
            task.execute();
        }
    }

    public static void openFile(Activity activity, String filename) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename);
        Uri uri = Uri.fromFile(file).normalizeScheme();
        String ext = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
        String mime = (ext != null)?MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext) : null;
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, mime);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        //File Hack
        if(Build.VERSION.SDK_INT>=24){
            try{
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        activity.startActivity(Intent.createChooser(intent, activity.getString(R.string.open_with)));
    }
}
