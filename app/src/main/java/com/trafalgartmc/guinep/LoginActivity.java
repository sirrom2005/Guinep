package com.trafalgartmc.guinep;

import android.content.Intent;
import android.content.SharedPreferences;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class LoginActivity extends UserBaseActivity {
    private SnackbarMsg mSnackbar;
    private TextView mPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Common.getSession(getApplicationContext()).getInt(getString(R.string.SESSION_ID),0)!=0){
            Common.endSession(getApplicationContext());
        }

        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        final Button login  = (Button) findViewById(R.id.btn);
        Button forget       = (Button) findViewById(R.id.forget);
        final TextView email= (TextView) findViewById(R.id.email);
        mPass = (TextView) findViewById(R.id.password);


        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgetActivity.class);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String e = email.getText().toString().trim();
                String p = mPass.getText().toString().trim();

                if(!android.util.Patterns.EMAIL_ADDRESS.matcher(e).matches()) {
                    mSnackbar.show(findViewById(R.id.container), getApplicationContext(), getString(R.string.enter_email), SnackbarMsg.Type.NORMAL);
                    return;
                }

                if(p.length()==0) {
                    mSnackbar.show(findViewById(R.id.container), getApplicationContext(), getString(R.string.enter_password), SnackbarMsg.Type.NORMAL);
                    return;
                }

                Common.showLoading(mBaseActivity, getString(R.string.loading));
                loginProcess(e,p);
            }
        });

        mSnackbar = new SnackbarMsg();
    }

    private void loginProcess(final String email, final String pass) {
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            OkHttpClient client = new OkHttpClient();
            Request request;

            @Override
            protected String doInBackground(Void... params) {
                RequestBody requestBody = new FormBody.Builder()
                        .add("user", email)
                        .add("pass", pass)
                        .build();

                request = new Request.Builder()
                        .url(Common.API_SERVER + "mobile_login.php")
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

                switch (body){
                    case "_NOT_FOUND_" :
                        Common.closeDialog(mBaseActivity);
                        AlertBox.Show(mBaseActivity,getString(R.string.invalid_login),AlertBox.Type.ERROR);
                        mPass.setText("");
                    break;
                    case "_SYS_ERROR_" :
                        Common.closeDialog(mBaseActivity);
                        AlertBox.Show(mBaseActivity,getString(R.string.network_error),AlertBox.Type.ERROR);
                    break;
                    default:
                        try {
                            JSONObject obj  = new JSONObject(body);
                            int id          = obj.getInt("id");
                            String fullname = obj.getString("fullname");
                            String agent_id = obj.getString("agent_id");
                            String email    = obj.getString("email");

                            SharedPreferences sharedPref = getSharedPreferences(Common.PREFERENCE_LOGIN,MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();

                            editor.putInt(getString(R.string.SESSION_ID), id);
                            editor.putString(getString(R.string.SESSION_NAME), fullname);
                            editor.putString(getString(R.string.SESSION_AGENT_ID), agent_id);
                            editor.putString(getString(R.string.SESSION_EMAIL), email);
                            editor.apply();

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } catch (JSONException e) {
                            AlertBox.Show(mBaseActivity,getString(R.string.network_error),AlertBox.Type.ERROR);
                        }
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
