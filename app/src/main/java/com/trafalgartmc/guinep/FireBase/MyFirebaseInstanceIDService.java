package com.trafalgartmc.guinep.FireBase;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;

import com.google.firebase.messaging.FirebaseMessaging;
import com.trafalgartmc.guinep.Utility.Common;

/**
 * Created by: Rohan Morris
 * Date: 5/12/2017
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService  {

    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.e(Common.LOG_TAG, "New token " + token);


        FirebaseMessaging.getInstance().subscribeToTopic("gcm_main_data");
        Log.e(Common.LOG_TAG,">>>> gcm_main_data");

        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        // avoid creating several instances, should be singleon
        /*OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormEncodingBuilder()
                .add("device_id", token)
                .build();

        Request request = new Request.Builder()
                .url(Common.API_SERVER + "add_device_id.php")
                .post(requestBody)
                .build();

        Response response;
        try {
            response = client.newCall(request).execute();
            Log.e(Common.LOG_TAG, "Body " + response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
}
