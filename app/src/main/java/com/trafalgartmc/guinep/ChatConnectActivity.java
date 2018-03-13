package com.trafalgartmc.guinep;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.trafalgartmc.guinep.Adapters.SpinnerAdapter;
import com.trafalgartmc.guinep.Classes.NetWorkHelper;
import com.trafalgartmc.guinep.Classes.SelectableItem;
import com.trafalgartmc.guinep.Utility.Common;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class ChatConnectActivity extends UserBaseActivity {
    private List<SelectableItem> itemList = new ArrayList<>();
    private TextView agentName;
    private String agentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_connect);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        agentId = Common.getSession(getApplicationContext()).getString(getString(R.string.SESSION_AGENT_ID),null);

        agentName                   = (TextView) findViewById(R.id.agent_name);
        final Button btnChange      = (Button) findViewById(R.id.btn_change);
        final ImageView btnAccept   = (ImageView) findViewById(R.id.btn_accept);

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatConnectActivity.this, ChatActivity.class);
                intent.putExtra(NetWorkHelper.AGENT_ID, agentId);
                startActivity(intent);
                finish();
            }
        });

        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Common.showLoadingV1(mBaseActivity,getString(R.string.loading));
            }

            @Override
            protected String doInBackground(Void... params) { return NetWorkHelper.getAgentList(); }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(s!=null) {
                    s = "{\"list\":" + s + "}";
                    Log.e(Common.LOG_TAG, s);
                    try {
                        HashMap<String, String> agents = new HashMap<>();
                        JSONArray val = new JSONObject(s).getJSONArray("list");
                        for (int i = 0; i < val.length(); i++) {
                            agents.put( val.getJSONObject(i).getString("id"),
                                        val.getJSONObject(i).getString("name"));

                            itemList.add(new SelectableItem(val.getJSONObject(i).getInt("id"),
                                                            val.getJSONObject(i).getString("name")));
                        }

                        if(agents.containsKey(agentId)){
                            agentName.setText(getString(R.string.your_agent, agents.get(agentId)));
                        }else {
                            Random rand = new Random();
                            int n = agents.size()>1 ? rand.nextInt(agents.size()-1) : 0;
                            agentName.setText(getString(R.string.your_agent, agents.values().toArray()[n]));
                            agentId = agents.keySet().toArray()[n].toString();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    btnAccept.setVisibility(View.VISIBLE);
                    btnChange.setText(R.string.change_agent);
                    btnChange.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            agentList(mBaseActivity,itemList);
                        }
                    });
                }else{
                    agentName.setText(R.string.no_agents_online);
                    btnChange.setText(R.string.leave_email);
                    btnChange.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_SENDTO);
                            intent.setType("text/plain");
                            intent.setData(Uri.parse("mailto:social@thetrafalgartravel.com"));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    });
                }
                Common.closeDialogV1();
            }
        }.execute();
    }


    public void agentList(Context c, List<SelectableItem> items) {
        View view = View.inflate(c, R.layout.select_agent_layout, null);

        final Spinner agentList = view.findViewById(R.id.agent_list);
        Button accept = view.findViewById(R.id.accept);

        final SpinnerAdapter adapter = new SpinnerAdapter(c);
        adapter.loadData(items);
        agentList.setAdapter(adapter);

        AlertDialog.Builder dialog = new AlertDialog.Builder(c);
        final AlertDialog alert  = dialog.create();
        alert.setView(view);
        alert.setCancelable(false);
        alert.show();

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agentId = String.valueOf(itemList.get((int) agentList.getSelectedItemId()).getKey());
                agentName.setText(getString(R.string.your_agent, itemList.get((int) agentList.getSelectedItemId()).getValue()));
                alert.dismiss();
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
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
