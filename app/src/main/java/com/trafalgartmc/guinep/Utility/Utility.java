package com.trafalgartmc.guinep.Utility;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.trafalgartmc.guinep.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Utility {
    private static OkHttpClient client;
    private static List<File> tmpFile;

    /**
     * Test if device is connect to a network
     * @param c
     * @return
     */
    public static boolean isConnected(Context c){
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    /**
     * Download json file
     * @param URL
     * @return
     */
    public static String getJsonFile(String URL) {
        String jsonStr = null;
        if(client==null){
            client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(2, TimeUnit.SECONDS)
                    .readTimeout(3, TimeUnit.SECONDS)
                    .build();
        }

        Request request = new Request.Builder().url(URL).build();

        try {
            Response response = client.newCall(request).execute();
            jsonStr = response.body().string();
            Log.e(Common.LOG_TAG, "URL " + URL + "\nBody " + jsonStr);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e(Common.LOG_TAG, "HERE ");
        return jsonStr;
    }

    /**
     * Write text file
     * @param c
     * @param data
     * @param fileName
     */
    public static void writeTextFile(final Context c, final String data, final String fileName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FileOutputStream file = c.openFileOutput(fileName, c.MODE_PRIVATE);
                    file.write(data.getBytes());
                    file.close();
                } catch (IOException e) {
                    Log.e(Common.LOG_TAG,e.getMessage());
                }
                Log.e(Common.LOG_TAG , "DATA WRITTEN " + fileName + " >> " + data);
            }
        }).start();
    }

    /**
     * Read file store on internal system
     * @return String*/
    public static String readTextFile(Context c, String fileName)
    {
        File theFile = new File(c.getFilesDir(), fileName);
        if (!theFile.exists()) {return null;}

        String data;
        StringBuffer buffer = new StringBuffer();
        FileInputStream file = null;
        try {
            file = c.openFileInput(fileName);
            BufferedReader input = new BufferedReader(new InputStreamReader(file));
            try {
                while ((data = input.readLine()) != null) {
                    buffer.append(data);
                }
            } catch (IOException e) {
                Log.e(Common.LOG_TAG, e.getMessage());
            }
        } catch (FileNotFoundException e) {
            Log.e(Common.LOG_TAG, e.getMessage());
        } finally {
            try {
                if (file != null) {
                    file.close();
                }
            } catch (IOException e) {
                Log.e(Common.LOG_TAG, e.getMessage());
            }
        }
        return buffer.toString();
    }

    public static Bitmap getImage(Context c, String file){
        FileInputStream f;
        Bitmap bitmap = null;
        try {
            f = c.openFileInput(file);
            bitmap = BitmapFactory.decodeStream(f);
        }catch(FileNotFoundException e) {
            Log.e(Common.LOG_TAG, "FileNotFoundException [" + file + "] " + e.getMessage());
        }catch (OutOfMemoryError e){
            Log.e(Common.LOG_TAG, "OutOfMemoryError [" + file + "]" + e.getMessage() + file);
            e.printStackTrace();
        }
        return bitmap;
    }

    public static void delTmpFiles() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(tmpFile!=null) {
                    for (File c : tmpFile) {
                        c.delete();
                    }
                }
            }
        }).start();
    }

    public static String cleanImageName(String url) {
        if(!url.isEmpty()){
            String[] array = url.split("/");
            return array[array.length-1];
        }
        return null;
    }

    public static boolean hasLolliPop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static void createShareIntent(final Context c, final String bodyText, final String img) {
        final List<Intent> targetedShareIntents = new ArrayList<>();
        final Intent[] shareIntent = {new Intent(Intent.ACTION_SEND)};
        shareIntent[0].setType("text/plain");
        final String MY_FILE = Common.timeStamp() + ".jpg";
        final File[] imgFile = new File[1];

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                /*File dir  = new File(Environment.getExternalStorageDirectory()+ "/temp");
                imgFile[0] = new File(dir,MY_FILE);
                if(img.equals("")){ return null; }
                FileOutputStream out = null;
                if(!dir.exists()){dir.mkdir();}
                if(dir.exists()){
                    try {
                        out = new FileOutputStream(imgFile[0],true);
                        Bitmap image = getImage(c, cleanImageName(img));
                        image.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    } catch (FileNotFoundException e) {
                        Log.e(Common.LOG_TAG, e.getMessage());
                    }finally {
                        try {
                            out.flush();
                            out.close();
                            addTmpFile(imgFile[0]);
                        } catch (IOException e) {
                            Log.e(Common.LOG_TAG, e.getMessage());
                        }
                    }
                }*/
                return null;
            }

            @Override
            protected void onPostExecute(Void o) {
                super.onPostExecute(o);
                //!imgFile[0].exists()
                if(true) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Find flights, get travel specials and more, download Guinep from the app store https://goo.gl/FUJdjO");
                    sendIntent.setType("text/plain");
                    c.startActivity(Intent.createChooser(sendIntent, "Share with:"));
                    return;
                }else{
                    //Log.e(Common.LOG_TAG, img + " === " + imgFile[0].toString());
                }

                String subject = c.getString(R.string.app_name);
                List<ResolveInfo> activityList = c.getPackageManager().queryIntentActivities(shareIntent[0], PackageManager.GET_META_DATA);
                int activitySize = activityList.size();

                for(int i=0; i<activitySize; i++){
                    String pName = activityList.get(i).activityInfo.packageName;
                    if(pName.contains("com.facebook.katana")){
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                        intent.putExtra(Intent.EXTRA_TITLE, subject);
                        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                        intent.putExtra(Intent.EXTRA_TEXT, c.getResources().getString(R.string.website));
                        intent.setPackage(pName);
                        targetedShareIntents.add(intent);
                    }
                    else{
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        if(imgFile[0].exists()) {
                            Uri uri = Uri.fromFile(imgFile[0]);
                            intent.setType("image/jpeg");
                            intent.putExtra(Intent.EXTRA_STREAM, uri);
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        }
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                        intent.putExtra(Intent.EXTRA_SUBJECT,subject);
                        intent.putExtra(Intent.EXTRA_TEXT,bodyText);
                        intent.setPackage(pName);
                        targetedShareIntents.add(intent);
                    }
                }

                if(targetedShareIntents.isEmpty()) {
                    Toast.makeText(c,"Share Media Not Found",Toast.LENGTH_SHORT).show();
                }else{
                    shareIntent[0] = Intent.createChooser(targetedShareIntents.remove(0), "Share with...");
                    shareIntent[0].putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[]{}));
                    c.startActivity(shareIntent[0]);
                }
            }
        };
        task.execute();
    }
}