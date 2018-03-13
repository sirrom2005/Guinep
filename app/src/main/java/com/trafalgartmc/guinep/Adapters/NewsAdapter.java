package com.trafalgartmc.guinep.Adapters;

import android.annotation.SuppressLint;
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

import com.trafalgartmc.guinep.Classes.DataObject;
import com.trafalgartmc.guinep.Classes.NewsDataParser;
import com.trafalgartmc.guinep.GalleryUtility.ImageWorker;
import com.trafalgartmc.guinep.NewsDetailActivity;
import com.trafalgartmc.guinep.R;
import com.trafalgartmc.guinep.Utility.Common;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
    public static final String  NEWS_DATA = "com.trafalgartmc.guinep.NewsData";
    private LayoutInflater inflater;
    private Activity mActivity;
    private Context mContext;
    private List<DataObject> dataList;
    private ImageWorker mImageWorker;

    public NewsAdapter(Activity activity) {
        mActivity = activity;
        mContext = mActivity.getBaseContext();
        if(inflater == null){inflater = LayoutInflater.from(mContext);}

        if(mImageWorker == null){
            mImageWorker = new ImageWorker(mContext, R.drawable.empty_photo);
        }
    }

    public void loadData(Context context) {
        dataList = NewsDataParser.getNewsList(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.news_list_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder vHolder, @SuppressLint("RecyclerView") final int position) {
        final String title  = dataList.get(position).getTitle();
        final String date   = dataList.get(position).getSubTitle();

        String img = dataList.get(position).getImage().replace(".","_640.");
        mImageWorker.loadImage(Common.API_SERVER + "images/" + img, vHolder.vImage);

        vHolder.vTitle.setText(title);
        vHolder.vDate.setText(date);
        vHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, vHolder.vImage, "featured_photo");
                Intent intent = new Intent(mContext, NewsDetailActivity.class);
                intent.putExtra(NEWS_DATA, dataList.get(position));
                mActivity.startActivity(intent, optionsCompat.toBundle());
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
        TextView vTitle,vDate;
        ImageView vImage;

        private ViewHolder(View v){
            super(v);
            vTitle  = v.findViewById(R.id.news_title);
            vDate   = v.findViewById(R.id.news_date);
            vImage  = v.findViewById(R.id.image);
        }
    }
}
