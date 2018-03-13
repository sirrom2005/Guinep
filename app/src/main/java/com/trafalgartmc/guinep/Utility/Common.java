package com.trafalgartmc.guinep.Utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.trafalgartmc.guinep.GalleryUtility.DownLoadImage;
import com.trafalgartmc.guinep.MainActivity;
import com.trafalgartmc.guinep.R;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;


/**
 * Author: Rohan Morris
 * Date: 03/18/2017
 * Description: Global functions
 */
public class Common {
    public static final String  WELCOME_PAGE_SEEN       = "WELCOME_PAGE_SEEN",
                                FORECAST_FOR_TODAY      = "http://api.openweathermap.org/data/2.5/weather?units=metric&appid=",
                                FORECAST                = "http://api.openweathermap.org/data/2.5/forecast?units=metric&appid=",
                                API_KEY                 = "api_key=df89sdfjsdfsd0fsdfsdfdfkio43534540fgdfgdfgdfg878loliolkadsdbvbb676765",
                                API_SERVER              = "http://api.trafalgartmc.com/",
                                NEWS_API                = API_SERVER + "news.json?" + API_KEY,
                                DESTINATION_API         = API_SERVER + "destination.json?" + API_KEY,
                                SPECIALS_API            = API_SERVER + "specials.json?" + API_KEY,
                                GALLERY_API             = API_SERVER + "gallery.json?" + API_KEY,
                                ADS_API                 = API_SERVER + "ads.json?" + API_KEY,
                                GALLERY_LOCATION        = API_SERVER + "gallery/",
                                FORECAST_FOR_TODAY_FILE = "weather",
                                FORECAST_FILE           = "forecast",
                                NEWS_FILE               = "news",
                                DESTINATION_FILE        = "destination",
                                SPECIALS_FILE           = "specials",
                                GALLERY_FILE            = "photo_gallery",
                                ADS_FILE                = "ads",
                                DATE_FORMAT             = "yyyyMMdd",
                                PREFERENCE_LOGIN        = "login_session",
                                /*DATE_FORMAT           = "MMMM dd. yyyy",*/
                                URL                     = "web_url",
                                LOG_TAG                 = "MY_APP";
    public static final int BANNER_MIN_HEIGHT           = 145;
    private static AlertDialog alert        = null;
    private static RelativeLayout loader, confirm;

    /**
     * Display About Dialog
     * @param c
     */
    public static void about(Context c) {
        View view = View.inflate(c, R.layout.about_layout, null);

        AlertDialog.Builder dialog = new AlertDialog.Builder(c);
        alert = dialog.create();
        alert.setView(view);
        alert.setCancelable(false);
        alert.show();

        final Button cancel = view.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
            }
        });
    }

    public static void showMsgBox(final Activity c, String txt) {
        RelativeLayout v = c.findViewById(R.id.loader);
        TextView message = v.findViewById(R.id.message);
        v.findViewById(R.id.loading).setVisibility(View.GONE);
        v.findViewById(R.id.confirmation_box).setVisibility(View.VISIBLE);
        Button btn = v.findViewById(R.id.btn);
        message.setText(txt);
        v.setVisibility(View.VISIBLE);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(c, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                c.startActivity(intent);
            }
        });
    }

    public static void showLoading(Activity c, String txt) {
        RelativeLayout v = c.findViewById(R.id.loader);
        TextView text = v.findViewById(R.id.text);
        text.setText((txt==null)? "Please wait..." : txt);
        v.setVisibility(View.VISIBLE);
    }

    public static void closeDialog(Activity c) {
        c.findViewById(R.id.loader).setVisibility(View.GONE);
    }

    public static void showLoadingV1(Context c, String txt) {
        View view = View.inflate(c, R.layout.loadingv1, null);
        TextView text = view.findViewById(R.id.text);
        loader  = view.findViewById(R.id.loader);
        confirm = view.findViewById(R.id.confirm);

        text.setText((txt==null)? "Please wait..." : txt);
        AlertDialog.Builder dialog = new AlertDialog.Builder(c);
        alert = dialog.create();
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alert.setView(view);
        alert.setCancelable(false);
        alert.show();
    }

    public static void closeDialogV1() {
        try{
            alert.dismiss();
        }catch(IllegalArgumentException ex){
            Log.e(LOG_TAG, ex.getMessage());
        }
    }

    public static void playConfirmation(final Activity c) {
        RelativeLayout v = c.findViewById(R.id.loader);
        RelativeLayout loader = v.findViewById(R.id.loading);
        RelativeLayout confirm = v.findViewById(R.id.confirm);
        TextView text = v.findViewById(R.id.msg);

        text.setText(c.getString(R.string.update_profile));

        Animation animOpen = AnimationUtils.loadAnimation(c, R.anim.fab_open);
        Animation animClose = AnimationUtils.loadAnimation(c, R.anim.fab_close);
        loader.startAnimation(animClose);
        confirm.startAnimation(animOpen);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Log.e(LOG_TAG,e.getMessage());
                }finally {
                    c.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(c, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            c.startActivity(intent);
                        }
                    });
                }
            }
        }).start();
    }

    public static float getlatitude(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.application_id), context.MODE_PRIVATE);
        return prefs.getFloat("Latitude", Float.parseFloat(context.getString(R.string.default_latitude)));
    }

    public static float getlongitude(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.application_id), context.MODE_PRIVATE);
        return prefs.getFloat("Longitude", Float.parseFloat(context.getString(R.string.default_longitude)));
    }

    public static boolean isMetric(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_unit_key),
                context.getString(R.string.pref_unit_metric))
                .equals(context.getString(R.string.pref_unit_metric));
    }

    public static String timeStamp(){
        return new SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(new Date());
    }

    public static String currencyFormat(Double amount){
        DecimalFormat f = new DecimalFormat("####,###,###.00");
        return f.format(amount);
    }

    public static String getCurrentDate() {
        SimpleDateFormat monthDayFormat = new SimpleDateFormat(DATE_FORMAT);
        return monthDayFormat.format(new Date());
    }

    public static String getTodaysDate(){
        Date now = new Date();
        SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd");
        return ft.format(now);
    }

    public static String formatTemperature(Context context, double temperature, boolean isMetric) {
        double temp;
        if ( !isMetric ) {
            temp = 9*temperature/5+32;
        } else {
            temp = temperature;
        }
        return context.getString(R.string.format_temperature, temp);
    }

    public static int getWeatherIcon(String i) {
        Log.e(Common.LOG_TAG, i);
        int img = R.drawable.clear_sky;
        if(i.equals("01d")){
            img = R.drawable.clear_sky;
        }
        if(i.equals("01n")){
            img = R.drawable.clear_sky_n;
        }
        if(i.equals("02d")){
            img = R.drawable.few_clouds;
        }
        if(i.equals("02n")){
            img = R.drawable.few_clouds_n;
        }
        if(i.equals("03d") || i.equals("03n") || i.equals("04d") || i.equals("04n")){
            img = R.drawable.clouds;
        }
        if(i.equals("50d") || i.equals("50n")){
            img = R.drawable.mist;
        }
        if(i.equals("10d") || i.equals("10n")){
            img = R.drawable.rain;
        }
        if(i.equals("09d") || i.equals("09n")){
            img = R.drawable.shower_rain;
        }
        if(i.equals("13d") || i.equals("13d")){
            img = R.drawable.snow;
        }
        if(i.equals("11d") || i.equals("11n")){
            img = R.drawable.thunderstorm;
        }
        return img;
    }

    /**
     * Helper method to convert the database representation of the date into something to display
     * to users.  As classy and polished a user experience as "20140102" is, we can do better.
     *
     * @param context Context to use for resource localization
     * @param dateInMillis The date in milliseconds
     * @return a user-friendly representation of the date.
     */
    public static String getFriendlyDayString(Context context, long dateInMillis) {
        // The day string for forecast uses the following logic:
        // For today: "Today, June 8"
        // For tomorrow:  "Tomorrow"
        // For the next 5 days: "Wednesday" (just the day name)
        // For all days after that: "Mon Jun 8"

        Time time = new Time();
        time.setToNow();
        long currentTime        = System.currentTimeMillis();
        int julianDay           = Time.getJulianDay(dateInMillis, time.gmtoff);
        int currentJulianDay    = Time.getJulianDay(currentTime, time.gmtoff);

        // If the date we're building the String for is today's date, the format
        // is "Today, June 24"
        if (julianDay == currentJulianDay) {
            String today = context.getString(R.string.today);
            int formatId = R.string.format_full_friendly_date;
            return String.format(context.getString(formatId), today, getFormattedMonthDay(context, dateInMillis));
        } else if ( julianDay < currentJulianDay + 7 ) {
            // If the input date is less than a week in the future, just return the day name.
            return getDayName(context, dateInMillis);
        } else {
            // Otherwise, use the form "Mon Jun 3"
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
            return shortenedDateFormat.format(dateInMillis);
        }
    }

    /**
     * Converts db date format to the format "Month day", e.g "June 24".
     * @param context Context to use for resource localization
     * @param dateInMillis The db formatted date string, expected to be of the form specified
     *                in Utility.DATE_FORMAT
     * @return The day in the form of a string formatted "December 6"
     */
    public static String getFormattedMonthDay(Context context, long dateInMillis ) {
        Time time = new Time();
        time.setToNow();
        SimpleDateFormat dbDateFormat = new SimpleDateFormat(DATE_FORMAT);
        SimpleDateFormat monthDayFormat = new SimpleDateFormat("MMMM dd");
        String monthDayString = monthDayFormat.format(dateInMillis);
        return monthDayString;
    }

    /**
     * Given a day, returns just the name to use for that day.
     * E.g "today", "tomorrow", "wednesday".
     *
     * @param context Context to use for resource localization
     * @param dateInMillis The date in milliseconds
     * @return
     */
    public static String getDayName(Context context, long dateInMillis) {
        // If the date is today, return the localized version of "Today" instead of the actual
        // day name.
        Time t = new Time();
        t.setToNow();
        int julianDay = Time.getJulianDay(dateInMillis, t.gmtoff);
        int currentJulianDay = Time.getJulianDay(System.currentTimeMillis(), t.gmtoff);
        if (julianDay == currentJulianDay) {
            return context.getString(R.string.today);
        } else if ( julianDay == currentJulianDay +1 ) {
            return context.getString(R.string.tomorrow);
        } else {
            Time time = new Time();
            time.setToNow();
            // Otherwise, the format is just the day of the week (e.g "Wednesday".
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
            return dayFormat.format(dateInMillis);
        }
    }

    /**
     * Encode base64 string
     * @param data
     * @return
     */
    public static String decodeString(String data) {
        String str = null;
        try {
            str = new String(Base64.decode(data, Base64.DEFAULT), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return str;
    }

    public static SharedPreferences getSession(Context c) {
        return c.getSharedPreferences(Common.PREFERENCE_LOGIN,Context.MODE_PRIVATE);
    }

    public static void endSession(Context c) {
        File myDir = c.getFilesDir(); //get your internal directory
        File myFile = new File(myDir, getSession(c).getInt(c.getString(R.string.SESSION_ID),0) + ".jpg");
        if(myFile.exists()){
            Log.e(Common.LOG_TAG, ">>>>" + myFile.toString());
            myFile.delete();
        }

        SharedPreferences sharedPref = c.getSharedPreferences(Common.PREFERENCE_LOGIN,Context.MODE_PRIVATE);
        sharedPref.edit().clear().apply();
        //clearAppFiles(c);
    }

    public static void clearAppFiles(Context c) {
        File myDir = c.getFilesDir(); //get your internal directory
        for(String file : myDir.list())
        {
            File myFile = new File(myDir, file);
            myFile.delete();
            Log.e(LOG_TAG, "Files >> " + myFile);
        }
    }

    public static int getBannerHeight(Context c) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Common.BANNER_MIN_HEIGHT, c.getResources().getDisplayMetrics());
    }

    public static void setProfilePhoto(Context c, ImageView profile) {
        int userId = getSession(c).getInt(c.getString(R.string.SESSION_ID),0);
        if(userId != 0){
            String fileName = userId + ".jpg";
            DownLoadImage dwn = new DownLoadImage(c);
            try {
                Bitmap bitmap = dwn.execute(Common.API_SERVER + "profile_photo/" + fileName).get();
                if(bitmap!=null)
                    profile.setImageBitmap(bitmap);
            } catch (InterruptedException | ExecutionException e) {
                Log.e(LOG_TAG, e.getMessage());
            }
        }
    }

    /**
     * Download and save Json file
     * @param c
     * @param url
     * @param fileName
     * @param decode
     */
    public static void saveDownloadedDataFile(Context c, String url, String fileName, boolean decode, boolean overWrite){
        File myDir = c.getFilesDir(); //get your internal directory
        File myFile = new File(myDir, fileName);

        if (overWrite) myFile.delete();

        if (!myFile.exists()) {
            Log.e(LOG_TAG, "Attempting to write >> " + fileName);
            String data;
            if ((data = Utility.getJsonFile(url)) != null) {
                Utility.writeTextFile(c, (decode) ? Common.decodeString(data) : data, fileName);
            }
        }
    }
}