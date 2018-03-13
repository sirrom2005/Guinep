package com.trafalgartmc.guinep;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

/**
 * Created by rohan on 6/5/2017.
 */

public class ChangePassActivity extends UserBaseActivity {
    private SnackbarMsg mSnackbar;
    private TextView mPasswordOld, mPassword, mPasswordConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button btn = (Button) findViewById(R.id.btn);
        mPasswordOld = (TextView) findViewById(R.id.password_old);
        mPassword = (TextView) findViewById(R.id.password);
        mPasswordConfirm = (TextView) findViewById(R.id.password_confirm);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String p1 = mPasswordOld.getText().toString().trim();
                String p2 = mPassword.getText().toString().trim();
                String p3 = mPasswordConfirm.getText().toString().trim();

                if(p1.trim().length()==0) {
                    mSnackbar.show(findViewById(R.id.container), getApplicationContext(), getString(R.string.enter_old_password), SnackbarMsg.Type.NORMAL);
                    return;
                }

                if(p2.length()==0) {
                    mSnackbar.show(findViewById(R.id.container), getApplicationContext(), getString(R.string.enter_new_password), SnackbarMsg.Type.NORMAL);
                    return;
                }

                if(p3.length()==0) {
                    mSnackbar.show(findViewById(R.id.container), getApplicationContext(), getString(R.string.enter_confirm_password), SnackbarMsg.Type.NORMAL);
                    return;
                }

                if(p2.length()<5) {
                    mSnackbar.show(findViewById(R.id.container), getApplicationContext(), getString(R.string.password_short), SnackbarMsg.Type.NORMAL);
                    return;
                }

                if(!p2.equals(p3)) {
                    mSnackbar.show(findViewById(R.id.container), getApplicationContext(), getString(R.string.password_no_match), SnackbarMsg.Type.NORMAL);
                    mPasswordConfirm.setText("");
                    return;
                }

                int id = Common.getSession(mBaseActivity.getApplicationContext()).getInt(getString(R.string.SESSION_ID),0);
                if(id==0){

                }else{
                    changeProcess(p1, p2, id);
                }
            }
        });

        mSnackbar = new SnackbarMsg();
    }

    private void changeProcess(final String p1, final String p2, final int id) {
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            OkHttpClient client = new OkHttpClient();
            Request request;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Common.showLoading(ChangePassActivity.this, getString(R.string.loading));
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestBody requestBody = new FormBody.Builder()
                        .add("old_pass", p1)
                        .add("new_pass", p2)
                        .add("user_id", String.valueOf(id))
                        .build();

                request = new Request.Builder()
                        .url(Common.API_SERVER + "mobile_change_pass.php")
                        .post(requestBody)
                        .build();
                try {
                    return client.newCall(request).execute().body().string();
                } catch (IOException e) {
                    Log.e(Common.LOG_TAG, e.getMessage());
                }
                return "";
            }

            @Override
            protected void onPostExecute(String body) {
                super.onPostExecute(body);
                Common.closeDialog(ChangePassActivity.this);
                switch (body){
                    case "_CHANGED_" :
                        //AlertBox.ShowMsg(mBaseActivity,getString(R.string.password_change));
                        Common.showMsgBox(ChangePassActivity.this, getString(R.string.password_change));
                    break;
                    case "_NOT_CHANGED_" :
                        AlertBox.Show(mBaseActivity,getString(R.string.invalid_login),AlertBox.Type.ERROR);
                    break;
                    case "_SYS_ERROR_" :
                        AlertBox.Show(mBaseActivity,getString(R.string.sys_error),AlertBox.Type.ERROR);
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
