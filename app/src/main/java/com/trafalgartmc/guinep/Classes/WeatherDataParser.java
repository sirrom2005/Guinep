package com.trafalgartmc.guinep.Classes;

import android.content.Context;
import android.text.format.Time;
import android.util.Log;

import com.trafalgartmc.guinep.Utility.Common;
import com.trafalgartmc.guinep.Utility.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rohan
 * Date: 5/6/2017.
 */

public class WeatherDataParser {
    private static List<Forecast> mForecast;
    private static Time dayTime = new Time();
    private static int julianStartDay;

    public static List<Forecast> getWeatherData(Context c){
        return getForecast(c);
    }

    private static List<Forecast> getForecast(Context c) {
        if(mForecast == null) {
            Log.e(Common.LOG_TAG,"GET WEATHER");
            String jsonStr = Utility.readTextFile(c,Common.FORECAST_FOR_TODAY_FILE);
            if(jsonStr == null){ return null; }
            mForecast = new ArrayList<>();

            long dateTime;
            dayTime.setToNow();
            // we start at the day returned by local time. Otherwise this is a mess.
            julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);
            // now we work exclusively in UTC
            dayTime = new Time();

            try {
                dateTime = dayTime.setJulianDay(julianStartDay);
                String mainStr      = new JSONObject(jsonStr).getString("main");
                String temp         = new JSONObject(mainStr).getString("temp");
                String temp_min     = new JSONObject(mainStr).getString("temp_min");
                String temp_max     = new JSONObject(mainStr).getString("temp_max");
                JSONArray weather   = new JSONObject(jsonStr).getJSONArray("weather");
                String icon         = weather.getJSONObject(0).getString("icon");
                String description  = weather.getJSONObject(0).getString("description");
                String city         = new JSONObject(jsonStr).getString("name");
                String sys          = new JSONObject(jsonStr).getString("sys");
                String country      = new JSONObject(sys).getString("country");
                mForecast.add(new Forecast(temp, temp_min, temp_max, icon, null, description, dateTime, country, city));
            } catch (JSONException e) {
                Log.e(Common.LOG_TAG,e.getMessage());
            }

            getForecastForTheWeek(c);
        }

        return mForecast;
    }

    private static void getForecastForTheWeek(Context c) {
        String jsonStr = Utility.readTextFile(c,Common.FORECAST_FILE);
        int dayPos = 1;
        long dateTime;

        if(jsonStr!=null) {
            try {
                JSONArray list = new JSONObject(jsonStr).getJSONArray("list");
                for (int i = 0; i < list.length(); i++) {
                    String date = list.getJSONObject(i).getString("dt_txt");
                    if (date.contains("12:00:00")) {
                        // Cheating to convert this to UTC time, which is what we want anyhow
                        dateTime = dayTime.setJulianDay(julianStartDay + dayPos);
                        JSONArray weatherStr = list.getJSONObject(i).getJSONArray("weather");
                        String temp     = list.getJSONObject(i).getJSONObject("main").getString("temp");
                        String temp_max = list.getJSONObject(i).getJSONObject("main").getString("temp_max");
                        String temp_min = list.getJSONObject(i).getJSONObject("main").getString("temp_min");
                        String icon = weatherStr.getJSONObject(0).getString("icon");
                        String label = weatherStr.getJSONObject(0).getString("main");
                        String description = weatherStr.getJSONObject(0).getString("description");
                        mForecast.add(new Forecast(temp, temp_max, temp_min, icon, label, description, dateTime, null, null));
                        dayPos++;
                    }
                }
            } catch (JSONException e) {
                Log.e(Common.LOG_TAG, e.getMessage());
            }
        }
    }

    public static void clearWeatherData() {
        mForecast   = null;
    }
}
