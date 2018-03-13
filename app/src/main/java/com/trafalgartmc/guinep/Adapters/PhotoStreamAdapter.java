package com.trafalgartmc.guinep.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.trafalgartmc.guinep.Classes.DataObject;
import com.trafalgartmc.guinep.Classes.GalleryDataParser;
import com.trafalgartmc.guinep.FullScreenImage;
import com.trafalgartmc.guinep.GalleryUtility.ImageWorker;
import com.trafalgartmc.guinep.R;
import com.trafalgartmc.guinep.Utility.Common;

import java.util.List;

public class PhotoStreamAdapter extends RecyclerView.Adapter<PhotoStreamAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private Activity mActivity;
    private Context mContext;
    private ImageWorker mImageWorker;
    private List<DataObject> dataList;

    public PhotoStreamAdapter(Activity context) {
        mActivity = context;
        mContext = context.getApplicationContext();

        if(inflater == null){inflater = LayoutInflater.from(mContext);}

        if(mImageWorker == null){
            mImageWorker = new ImageWorker(mContext, R.drawable.empty_photo);
        }
    }

    public void loadData(Context context) {
        dataList = GalleryDataParser.getGalleryData(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.image_icon, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder vHolder, int position) {
        final int pos = position;

        mImageWorker.loadImage(Common.GALLERY_LOCATION + dataList.get(pos).getImage(), vHolder.vImage);

        vHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, FullScreenImage.class);
                intent.putExtra("IMAGE_LIST_KEY",pos);
                mActivity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(dataList==null)
            return 0;
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView vImage;

        private ViewHolder(View v){
            super(v);
            vImage = v.findViewById(R.id.image_thumb);
        }
    }
}