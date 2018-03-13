package com.trafalgartmc.guinep.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trafalgartmc.guinep.Classes.NetWorkHelper;
import com.trafalgartmc.guinep.R;
import com.trafalgartmc.guinep.Utility.Common;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private Context mContext;

    public ChatAdapter(Activity activity){
        mContext  = activity.getApplicationContext();
        if(inflater == null){inflater = LayoutInflater.from(mContext);}
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        ViewHolder viewHolder;

        view = inflater.inflate((viewType==0)? R.layout.chat_message : R.layout.chat_message_reply, parent, false);
        viewHolder = new ViewHolder(view, viewType);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder vHolder, final int position) {
        if(NetWorkHelper.msgObj.get(position).getSenderUserName().equals(NetWorkHelper.username)){
            vHolder.msg.setText(NetWorkHelper.msgObj.get(position).getMessage());
            vHolder.time.setText(NetWorkHelper.msgObj.get(position).getTime());
        }else {
            vHolder.reply_msg.setText(NetWorkHelper.msgObj.get(position).getMessage());
            vHolder.reply_time.setText(NetWorkHelper.msgObj.get(position).getTime());
        }
        Log.e(Common.LOG_TAG, ">>>> " + position);
    }

    @Override
    public int getItemViewType(int pos) {
        return NetWorkHelper.msgObj.get(pos).getSenderUserName().equals(NetWorkHelper.username) ? 0 : 1;
    }

    @Override
    public int getItemCount() {
        return (NetWorkHelper.msgObj == null) ? 0 : NetWorkHelper.msgObj.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView    msg,
                    time,
                    reply_msg,
                    reply_time;

        public ViewHolder(View v, int viewType){
            super(v);
            if(viewType == 0){
                msg         = v.findViewById(R.id.text_message);
                time        = v.findViewById(R.id.time);
            }else{
                reply_msg   = v.findViewById(R.id.reply_text_message);
                reply_time  = v.findViewById(R.id.reply_time);
            }
        }
    }
}