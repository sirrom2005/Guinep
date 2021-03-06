package com.trafalgartmc.guinep.Classes;

import android.content.Context;
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

public class AdsDataParser {
    private static List<DataObject> mObj;

    public static List<DataObject> getAdsList(Context c) {
        if(mObj==null) {
            String jsonStr = Utility.readTextFile(c,Common.ADS_FILE);
            if(jsonStr==null){return null;}
            mObj = new ArrayList<>();
            try {
                JSONArray list = new JSONObject(jsonStr).getJSONArray("list");
                for(int i=0; i<list.length(); i++){
                    mObj.add(new DataObject(list.getJSONObject(i).getInt("id"),
                                            list.getJSONObject(i).getString("title"),
                                            null,
                                            list.getJSONObject(i).getString("details"),
                                            list.getJSONObject(i).getString("banner"),
                                            null));
                }
            } catch (JSONException e) {
                Log.e(Common.LOG_TAG,e.getMessage());
            }
        }
        return mObj;
    }
    public static void clearAdsData() {
        mObj = null;
    }
}
