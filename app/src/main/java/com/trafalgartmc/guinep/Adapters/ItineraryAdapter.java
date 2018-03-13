package com.trafalgartmc.guinep.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.trafalgartmc.guinep.Classes.ItineraryData;
import com.trafalgartmc.guinep.ItineraryDetailActivity;
import com.trafalgartmc.guinep.R;

import java.util.List;

public class ItineraryAdapter extends RecyclerView.Adapter<ItineraryAdapter.ViewHolder> {
    public static final String INVOICE_CODE = "itinerary_code";
    private LayoutInflater inflater;
    private Activity mActivity;
    private List<ItineraryData.ItineraryListData> dataList;

    public ItineraryAdapter(Activity activity) {
        mActivity = activity;
        Context mContext = activity.getBaseContext();
        if(inflater == null){inflater = LayoutInflater.from(mContext);}
    }

    public void loadData(List<ItineraryData.ItineraryListData> mObj) {
        dataList = mObj;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.itinerary_list_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder vHolder, final int position) {
        final ItineraryData.ItineraryListData obj = dataList.get(position);
        vHolder.vLocation.setText(mActivity.getString(R.string.itinerary_for, obj.getDestination()));
        vHolder.vDate.setText(obj.getDepartureDate());
        vHolder.vRef.setText(mActivity.getString(R.string.booking_ref, obj.getItineraryCode()));

        vHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, vHolder.vImage, "featured_photo");
                Intent intent = new Intent(mActivity, ItineraryDetailActivity.class);
                intent.putExtra(INVOICE_CODE, obj.getInvoiceNo());
                mActivity.startActivity(intent,optionsCompat.toBundle());
            }
        });
    }

    @Override
    public int getItemCount() {
        return (dataList==null)? 0 : dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView vLocation,vDate,vRef;
        ImageView vImage;

        public ViewHolder(View v){
            super(v);
            vLocation = v.findViewById(R.id.location);
            vDate     = v.findViewById(R.id.date_label);
            vRef      = v.findViewById(R.id.ref);
            vImage    = v.findViewById(R.id.image);
        }
    }
}
