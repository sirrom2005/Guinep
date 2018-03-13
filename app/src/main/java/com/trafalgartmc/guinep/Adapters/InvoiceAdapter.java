package com.trafalgartmc.guinep.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trafalgartmc.guinep.Classes.FileDownloader;
import com.trafalgartmc.guinep.Classes.InvoiceData;
import com.trafalgartmc.guinep.R;
import com.trafalgartmc.guinep.Utility.Common;

import java.util.List;

public class InvoiceAdapter extends RecyclerView.Adapter<InvoiceAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private Activity mActivity;
    private Context mContext;
    private List<InvoiceData.InvoiceListData> dataList;

    public InvoiceAdapter(Activity activity) {
        mActivity = activity;
        mContext = activity.getBaseContext();
        if(inflater == null){inflater = LayoutInflater.from(mContext);}
    }

    public void loadData(List<InvoiceData.InvoiceListData> mObj) {
        dataList = mObj;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.invoice_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder vHolder, int position) {
        final InvoiceData.InvoiceListData obj = dataList.get(position);
        vHolder.vInvoiceNum.setText(String.valueOf(obj.getInvoiceNo()));
        vHolder.vTotal.setText(obj.getCurrency() + "$ " + Common.currencyFormat(obj.getTotal()));
        vHolder.vDetail.setText(obj.getRoute());

        vHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileDownloader.Download(
                        mActivity,
                        "invoice_" + obj.getInvoiceNo() + ".pdf",
                        Common.API_SERVER + "download_invoice.php?key=" + obj.getInvoiceNo());
            }
        });
    }

    @Override
    public int getItemCount(){ return (dataList==null)? 0 : dataList.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView vInvoiceNum,vDetail,vTotal;

        public ViewHolder(View v){
            super(v);
            vInvoiceNum = v.findViewById(R.id.invoice_code);
            vDetail     = v.findViewById(R.id.invoice_desc);
            vTotal      = v.findViewById(R.id.total);
        }
    }
}
