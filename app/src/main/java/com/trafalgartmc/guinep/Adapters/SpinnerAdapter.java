package com.trafalgartmc.guinep.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.trafalgartmc.guinep.Classes.SelectableItem;
import com.trafalgartmc.guinep.R;

import java.util.List;

public class SpinnerAdapter extends ArrayAdapter<SelectableItem> {
    private List<SelectableItem> obj;
    private LayoutInflater inflater;

    public SpinnerAdapter(Context context) {
        super(context, R.layout.spinner_layout);
        if(inflater == null){inflater = LayoutInflater.from(context);}
    }

    public void loadData(List<SelectableItem> data) {
        obj = data;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rootView   = inflater.inflate(R.layout.spinner_layout, parent, false);
        TextView value  = rootView.findViewById(R.id.value);

        value.setText(obj.get(position).getValue());
        return rootView;
    }

    @Nullable
    @Override
    public SelectableItem getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getPosition(SelectableItem item) {
        return super.getPosition(item);
    }

    @Override
    public int getCount() {
        return obj.size();
    }
}