package com.trafalgartmc.guinep;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.trafalgartmc.guinep.Adapters.InvoiceAdapter;
import com.trafalgartmc.guinep.Classes.InvoiceData;
import com.trafalgartmc.guinep.Utility.Common;
import com.trafalgartmc.guinep.Utility.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class InvoiceActivity extends NoDrawerBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Common.getSession(mBaseContext).getInt(getString(R.string.SESSION_ID),0)==0){
            Intent intent = new Intent(this, AccessDenied.class);
            startActivity(intent);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //ImageView banner = (ImageView) findViewById(R.id.banner);
        //banner.getLayoutParams().height = Common.getBannerHeight(mBaseContext);

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, InvoiceFragment.newInstance())
                    .commit();
        }

        final SwipeRefreshLayout swipe = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipe.setProgressViewOffset(false, 0,getResources().getDimensionPixelSize(R.dimen.refresher_offset_end));
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe.setRefreshing(false);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home){
            // Press Back Icon
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Fragment
     * */
    public static class InvoiceFragment extends Fragment {
        private InvoiceAdapter mAdapter;
        private Activity mActivity;
        private Context mContext;
        private List<InvoiceData.InvoiceListData> mObj;

        public InvoiceFragment(){}

        public static final InvoiceFragment newInstance()
        {
            return new InvoiceFragment();
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mActivity = getActivity();
            mContext = mActivity.getApplicationContext();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.recycler_view, container, false);
            RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);

            mAdapter = new InvoiceAdapter(mActivity);
            recyclerView.setAdapter(mAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

            loadData(container);
            return rootView;
        }

        private void loadData(final ViewGroup container) {
            AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
                OkHttpClient client = new OkHttpClient();
                Request request;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    Common.showLoading(mActivity, getResources().getString(R.string.loading));
                }

                @Override
                protected String doInBackground(Void... params) {
                    int id = Common.getSession(mContext).getInt(getString(R.string.SESSION_ID),0);
                    RequestBody requestBody = new FormBody.Builder()
                            .add("user_id", String.valueOf(id))
                            .build();

                    request = new Request.Builder()
                            .url(Common.API_SERVER + "mobile_invoice.php")
                            .post(requestBody)
                            .build();
                    try {
                        return client.newCall(request).execute().body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(String data) {
                    super.onPostExecute(data);
                    mObj = new ArrayList<>();
                    if(data!=null) {
                        try {
                            JSONArray list = new JSONObject(data).getJSONArray("list");
                            for(int i=0; i<list.length(); i++){
                                mObj.add(new InvoiceData.InvoiceListData(
                                        list.getJSONObject(i).getInt("invoicenum"),
                                        list.getJSONObject(i).getString("currency_key"),
                                        list.getJSONObject(i).getDouble("total"),
                                        list.getJSONObject(i).getString("route")
                                ));
                                mAdapter.loadData(mObj);
                                mAdapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            container.addView(View.inflate(mContext,R.layout.empty_list,null));
                        }
                    }
                    Common.closeDialog(mActivity);
                }
            };

            if(Utility.isConnected(mContext)) {
                task.execute();
            }
        }
    }
}
