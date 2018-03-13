package com.trafalgartmc.guinep.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.trafalgartmc.guinep.Classes.ItineraryData;
import com.trafalgartmc.guinep.ItineraryDetailActivity;
import com.trafalgartmc.guinep.R;
import com.trafalgartmc.guinep.TravelMapActivity;

import java.util.List;

public class ItineraryDetailAdapter extends RecyclerView.Adapter<ItineraryDetailAdapter.ViewHolder>{
    public static final String MAP_COORDS = "map_coord";
    public static final String MAP_INFO   = "map_info";
    private LayoutInflater inflater;
    private List<ItineraryData> dataList;
    private Activity mActivity;
    private Context mContext;

    public ItineraryDetailAdapter(Activity activity) {
        mActivity = activity;
        mContext = activity.getApplicationContext();
        if(inflater == null){inflater = LayoutInflater.from(mContext);}
    }

    public void loadData(List<ItineraryData> mObj) {
        dataList = mObj;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.itinerary_detail_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        dataList = ItineraryDetailActivity.ItineraryDetailFragment.mObj;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder vHolder, int position) {
        final ItineraryData obj = dataList.get(position);
        vHolder.vDeparture.setText(obj.getDepartureCityName());
        vHolder.vDepartAirport.setText(obj.getDepartureAirport());
        vHolder.vDepartureDate.setText(obj.getDepartureDate());
        vHolder.vDepartureTime.setText(obj.getDepartureTime());
        vHolder.vArrival.setText(obj.getArrivalCityName());
        vHolder.vArrivalAirport.setText(obj.getArrivalAirport());
        vHolder.vArrivalDate.setText(obj.getArrivalDate());
        vHolder.vArrivalTime.setText(obj.getArrivalTime());
        vHolder.vFlight.setText(obj.getAirline() + ", Flight (" + obj.getFlightNo() + ")");
        vHolder.vClassType.setText(obj.getClassOfSvc());

        vHolder.vMapIicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, TravelMapActivity.class);
                String[] info  = {  obj.getDepartureCityName(),
                                    obj.getDepartureAirport(),
                                    obj.getArrivalCityName(),
                                    obj.getArrivalAirport(),
                                };

                double[] cords = {  Double.parseDouble(obj.getDestinationLatitude()),
                                    Double.parseDouble(obj.getDestinationLongitude()),
                                    Double.parseDouble(obj.getArrivalLatitude()),
                                    Double.parseDouble(obj.getArrivalLongitude()),
                                 };
                intent.putExtra(MAP_COORDS, cords);
                intent.putExtra(MAP_INFO, info);
                mActivity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (dataList==null)? 0 : dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView    vDeparture,
                    vDepartAirport,
                    vDepartureDate,
                    vDepartureTime,
                    vArrival,
                    vArrivalAirport,
                    vArrivalDate,
                    vArrivalTime,
                    vFlight,
                    vClassType;
        ImageView vMapIicon;

        public ViewHolder(View v){
            super(v);
            vDeparture      = v.findViewById(R.id.departure);
            vDepartAirport  = v.findViewById(R.id.d_airport);
            vDepartureDate  = v.findViewById(R.id.departure_date);
            vDepartureTime  = v.findViewById(R.id.departure_time);
            vArrival        = v.findViewById(R.id.arrival);
            vArrivalAirport = v.findViewById(R.id.a_airport);
            vArrivalDate    = v.findViewById(R.id.arrival_date);
            vArrivalTime    = v.findViewById(R.id.arrival_time);
            vFlight         = v.findViewById(R.id.flight);
            vClassType      = v.findViewById(R.id.class_type);
            vMapIicon       = v.findViewById(R.id.map_icon);
        }
    }
}
