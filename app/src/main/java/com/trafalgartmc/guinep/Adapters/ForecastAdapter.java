package com.trafalgartmc.guinep.Adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.trafalgartmc.guinep.Classes.Forecast;
import com.trafalgartmc.guinep.Classes.WeatherDataParser;
import com.trafalgartmc.guinep.R;
import com.trafalgartmc.guinep.Utility.Common;

import java.util.List;
import java.util.Locale;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ViewHolder> {
    private Context mContext;
    private LayoutInflater inflater;
    private List<Forecast> data;

    public ForecastAdapter(Context context) {
        mContext = context;
        if(inflater == null){inflater = LayoutInflater.from(mContext);}
    }

    public void loadData(Context context) {
        data = WeatherDataParser.getWeatherData(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        ViewHolder viewHolder;

        if(viewType==0) {
            view = inflater.inflate(R.layout.todays_forecast, parent, false);
            viewHolder = new ViewHolder(view,viewType);
        }else{
            view = inflater.inflate(R.layout.list_item_forecast, parent, false);
            viewHolder = new ViewHolder(view,viewType);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder vHolder, int position) {
        if(position==0){
            vHolder.tHight.setText(Common.formatTemperature(mContext, Float.parseFloat(data.get(position).getTemp_max()), Common.isMetric(mContext)));
            vHolder.tLow.setText(Common.formatTemperature(mContext, Float.parseFloat(data.get(position).getTemp_min()), Common.isMetric(mContext)));
            vHolder.tforecast.setText(data.get(position).getDescription());
            vHolder.tDate.setText(Common.getFriendlyDayString(mContext, data.get(position).getDate()));
            vHolder.tImage.setImageDrawable(ContextCompat.getDrawable(mContext, Common.getWeatherIcon(data.get(position).getIcon())));
            Locale loc = new Locale("",data.get(position).getCountry());
            vHolder.country.setText(loc.getDisplayCountry());
        }else {
            vHolder.vHight.setText(Common.formatTemperature(mContext, Float.parseFloat(data.get(position).getTemp_max()), Common.isMetric(mContext)));
            vHolder.vLow.setText(Common.formatTemperature(mContext, Float.parseFloat(data.get(position).getTemp_min()), Common.isMetric(mContext)));
            vHolder.vforecast.setText(data.get(position).getDescription());
            vHolder.vDate.setText(Common.getFriendlyDayString(mContext, data.get(position).getDate()));
            vHolder.vImage.setImageDrawable(ContextCompat.getDrawable(mContext, Common.getWeatherIcon(data.get(position).getIcon())));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position==0)? 0 : 1; //0 Today | 1 List
    }

    @Override
    public int getItemCount() {
        if(data==null)
            return 0;
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView vHight, vLow, vforecast, vDate,tHight, tLow, tforecast, tDate, country;
        ImageView vImage,tImage;

        private ViewHolder(View v, int viewType){
            super(v);
            if(viewType==0){
                tHight      = v.findViewById(R.id.today_high);
                tLow        = v.findViewById(R.id.today_low);
                tDate       = v.findViewById(R.id.today_date);
                tforecast   = v.findViewById(R.id.today_forecast);
                tImage      = v.findViewById(R.id.today_icon);
                country     = v.findViewById(R.id.country);
            }
            else {
                vHight      = v.findViewById(R.id.list_item_high_textview);
                vLow        = v.findViewById(R.id.list_item_low_textview);
                vDate       = v.findViewById(R.id.list_item_date_textview);
                vforecast   = v.findViewById(R.id.list_item_forecast_textview);
                vImage      = v.findViewById(R.id.list_item_icon);
            }
        }
    }
}
