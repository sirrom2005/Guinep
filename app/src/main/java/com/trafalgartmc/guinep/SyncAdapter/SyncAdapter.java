package com.trafalgartmc.guinep.SyncAdapter;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.trafalgartmc.guinep.R;
import com.trafalgartmc.guinep.Utility.Common;
import com.trafalgartmc.guinep.Utility.Utility;

/**
 * Created by Rohan
 * Date on 5/01/2017.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    // Interval at which to sync with the server, in milliseconds.
    private static final int SYNC_INTERVAL = 3600000*4;
    private static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        /* Put the data transfer code here. */
        Log.e(Common.LOG_TAG, "onPerformSync - Sync Adapter" );
        Context c = getContext();
        float lat = Common.getlatitude(c);
        float lon = Common.getlongitude(c);
        String data;

        /* Get weather for today data
        if((data = Utility.getJsonFile(Common.FORECAST_FOR_TODAY + c.getString(R.string.openweathermap_api_key) + "&lat=" + lat + "&lon=" + lon)) != null)
        { Utility.writeTextFile(c,data,Common.FORECAST_FOR_TODAY_FILE); }
        /* Get weather for the week data
        if((data = Utility.getJsonFile(Common.FORECAST + c.getString(R.string.openweathermap_api_key) + "&lat=" + lat + "&lon=" + lon)) != null)
        { Utility.writeTextFile(c,data,Common.FORECAST_FILE); }
        /* Get news data */
        if((data = Utility.getJsonFile(Common.NEWS_API)) != null)
        { Utility.writeTextFile(c,Common.decodeString(data),Common.NEWS_FILE); }
        /* Get destination data */
        if((data = Utility.getJsonFile(Common.DESTINATION_API)) != null)
        { Utility.writeTextFile(c,Common.decodeString(data),Common.DESTINATION_FILE); }
        /* Get special data */
        if((data = Utility.getJsonFile(Common.SPECIALS_API)) != null)
        { Utility.writeTextFile(c,Common.decodeString(data),Common.SPECIALS_FILE); }
        /* Get Photo Gallery Data */
        if((data = Utility.getJsonFile(Common.GALLERY_API)) != null)
        { Utility.writeTextFile(c,data,Common.GALLERY_FILE); }
        /* Get special Ads */
        if((data = Utility.getJsonFile(Common.ADS_API)) != null)
        { Utility.writeTextFile(c,Common.decodeString(data),Common.ADS_FILE); }
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    private static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder()
                    .syncPeriodic(syncInterval, flexTime)
                    .setSyncAdapter(account, authority)
                    .setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account, authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    private static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet. If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    private static Account getSyncAccount(final Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                        (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        // Create the account type and default account
        Account newAccount = new Account(   context.getString(R.string.app_name),
                                            context.getString(R.string.sync_account_type));
        // If the password doesn't exist, the account doesn't exist
        if(null == accountManager.getPassword(newAccount)){
            /*
            * Add the account and account type, no password or user data
            * If successful, return the Account object, otherwise report an error.
            */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
            * If you don't set android:syncable="true" in
            * in your <provider> element in the manifest,
            * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
            * here.
            */
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
        * Since we've created an account
        */
        SyncAdapter.configurePeriodicSync(context,SYNC_INTERVAL,SYNC_FLEXTIME);
        /*
        * Without calling setSyncAutomatically, our periodic sync will not be enabled.
        */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);
        /*
        * Finally, let's do a sync to get things started
        */
        //syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
