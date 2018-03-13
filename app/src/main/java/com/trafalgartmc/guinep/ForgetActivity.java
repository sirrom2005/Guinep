package com.trafalgartmc.guinep;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.trafalgartmc.guinep.Classes.SnackbarMsg;
import com.trafalgartmc.guinep.Utility.AlertBox;
import com.trafalgartmc.guinep.Utility.Common;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;


public class ForgetActivity extends UserBaseActivity {
    private SnackbarMsg mSnackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Button btn = (Button) findViewById(R.id.btn);
        final TextView email= (TextView) findViewById(R.id.email);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String e = email.getText().toString();
                if(!android.util.Patterns.EMAIL_ADDRESS.matcher(e).matches()) {
                    mSnackbar.show(findViewById(R.id.container), getBaseContext(), getString(R.string.enter_email), SnackbarMsg.Type.NORMAL);
                    return;
                }
                processRequest(e);
            }
        });

        mSnackbar = new SnackbarMsg();
    }

    private void processRequest(final String email) {
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            OkHttpClient client = new OkHttpClient();
            Request request;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Common.showLoading(ForgetActivity.this, getString(R.string.loading));
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestBody requestBody = new FormBody.Builder()
                        .add("email", email)
                        .build();

                request = new Request.Builder()
                        .url(Common.API_SERVER + "mobile_request_login.php")
                        .post(requestBody)
                        .build();

                try {
                    return client.newCall(request).execute().body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return "";
            }

            @Override
            protected void onPostExecute(String body) {
                super.onPostExecute(body);
                Common.closeDialog(ForgetActivity.this);
                switch (body){
                    case "_NOT_FOUND_" :
                        AlertBox.Show(mBaseActivity,getString(R.string.email_not_found,email),AlertBox.Type.ERROR);
                    break;
                    case "_SYS_ERROR_" :
                        AlertBox.Show(mBaseActivity,getString(R.string.network_error),AlertBox.Type.ERROR);
                    break;
                    case "_NOT_CREATED_" :
                        AlertBox.Show(mBaseActivity,getString(R.string.account_no_reset),AlertBox.Type.ERROR);
                    break;
                    case "_CREATED_" :
                        Common.showMsgBox(ForgetActivity.this, getString(R.string.account_reset,email));
                    break;
                    case "_EMAIL_ERROR_" :
                        AlertBox.Show(mBaseActivity,getString(R.string.email_error,email),AlertBox.Type.NORMAL);
                    break;
                    default:
                        AlertBox.Show(mBaseActivity,getString(R.string.network_error),AlertBox.Type.ERROR);
                    break;
                }
            }
        };
        task.execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home){
            // Press Back Icon
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
