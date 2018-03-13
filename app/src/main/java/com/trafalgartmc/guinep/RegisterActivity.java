package com.trafalgartmc.guinep;

import android.content.Intent;
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

public class RegisterActivity extends UserBaseActivity {
    private SnackbarMsg mSnackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Button register     = (Button) findViewById(R.id.btn);
        Button haveAccount  = (Button) findViewById(R.id.have_account);
        final TextView name = (TextView) findViewById(R.id.full_name);
        final TextView email= (TextView) findViewById(R.id.email);

        haveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String n = name.getText().toString().trim();
                String e = email.getText().toString().trim();;

                if(n.trim().length()==0) {
                    mSnackbar.show(findViewById(R.id.container), getBaseContext(), getString(R.string.enter_full_name), SnackbarMsg.Type.NORMAL);
                    return;
                }

                if(!android.util.Patterns.EMAIL_ADDRESS.matcher(e).matches()) {
                    mSnackbar.show(findViewById(R.id.container), getBaseContext(), getString(R.string.enter_email), SnackbarMsg.Type.NORMAL);
                    return;
                }
                registrationProcess(e,n);
            }
        });

        mSnackbar = new SnackbarMsg();
    }

    private void registrationProcess(final String email, final String name) {
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            OkHttpClient client = new OkHttpClient();
            Request request;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Common.showLoading(RegisterActivity.this, getString(R.string.loading));
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestBody requestBody = new FormBody.Builder()
                        .add("email", email)
                        .add("name", name)
                        .build();

                request = new Request.Builder()
                        .url(Common.API_SERVER + "mobile_register.php")
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
                Common.closeDialog(RegisterActivity.this);
                switch (body){
                    case "_ACCOUNT_EXIST_" :
                        AlertBox.Show(mBaseActivity,getString(R.string.account_exist, email),AlertBox.Type.TWO_BUTTON);
                        AlertBox.action.setVisibility(View.VISIBLE);
                        AlertBox.action.setText( getResources().getString(R.string.forget_pass));
                        AlertBox.action.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertBox.close();
                                Intent intent = new Intent(getApplicationContext(), ForgetActivity.class);
                                startActivity(intent);
                            }
                        });
                    break;
                    case "_NOT_CREATED_" :
                        AlertBox.Show(mBaseActivity,getString(R.string.account_not_created),AlertBox.Type.ERROR);
                    break;
                    case "_CREATED_" :
                        //AlertBox.ShowMsg(mBaseActivity,getString(R.string.account_created,email));
                        Common.showMsgBox(RegisterActivity.this, getString(R.string.account_created,email));
                    break;
                    case "_EMAIL_ERROR_" :
                        AlertBox.Show(mBaseActivity,getString(R.string.account_created_email_error,email),AlertBox.Type.NORMAL);
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
