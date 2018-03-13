package com.trafalgartmc.guinep.FireBase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.trafalgartmc.guinep.Classes.GalleryDataParser;
import com.trafalgartmc.guinep.MainActivity;
import com.trafalgartmc.guinep.R;
import com.trafalgartmc.guinep.Utility.Common;
import com.trafalgartmc.guinep.Utility.Utility;

/**
 * Created by: rohan
 * Date: 5/5/2017
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        String data;
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(Common.LOG_TAG, "Data type: " + remoteMessage.getData().get("data"));
            switch(remoteMessage.getData().get("data")){
                case "news":
                    if((data = Utility.getJsonFile(Common.NEWS_API)) != null)
                    { Utility.writeTextFile(getApplicationContext(), Common.decodeString(data), Common.NEWS_FILE); }
                break;
                case "location":
                    if((data = Utility.getJsonFile(Common.DESTINATION_API)) != null)
                    { Utility.writeTextFile(getApplicationContext(), Common.decodeString(data), Common.DESTINATION_FILE); }
                break;
                case "specials":
                    if((data = Utility.getJsonFile(Common.SPECIALS_API)) != null)
                    { Utility.writeTextFile(getApplicationContext(), Common.decodeString(data), Common.SPECIALS_FILE); }
                break;
                case "gallery":
                    Log.d(TAG, "get gallery");
                    if((data = Utility.getJsonFile(Common.GALLERY_API)) != null)
                    { Utility.writeTextFile(getApplicationContext(), data, Common.GALLERY_FILE); }
                    GalleryDataParser.clearGalleryData();
                break;
                case "ads":
                    if((data = Utility.getJsonFile(Common.ADS_API)) != null)
                    { Utility.writeTextFile(getApplicationContext(),Common.decodeString(data),Common.ADS_FILE); }
                break;
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            sendNotification(remoteMessage);
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param remoteMessage FCM message body received.
     */
    private void sendNotification(RemoteMessage remoteMessage) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}