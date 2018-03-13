package com.trafalgartmc.guinep.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.trafalgartmc.guinep.BrowserActivity;
import com.trafalgartmc.guinep.Classes.DataObject;
import com.trafalgartmc.guinep.Classes.GalleryImageAnimation;
import com.trafalgartmc.guinep.Classes.LocationDataParser;
import com.trafalgartmc.guinep.FeaturedItemActivity;
import com.trafalgartmc.guinep.GalleryUtility.ImageWorker;
import com.trafalgartmc.guinep.R;
import com.trafalgartmc.guinep.Utility.Common;
import com.trafalgartmc.guinep.Utility.Utility;

import java.util.List;

public class FeaturedLocationAdapter extends RecyclerView.Adapter<FeaturedLocationAdapter.ViewHolder> {
    private final String DESTINATION_DATA = "com.trafalgartmc.guinep.Location";
    private LayoutInflater inflater;
    private Context mContext;
    private Activity mActivity;
    private List<DataObject> dataList;
    private ImageWorker mImageWorker;

    public FeaturedLocationAdapter(Activity activity) {
        mActivity = activity;
        mContext = activity.getBaseContext();

        if(inflater == null){inflater = LayoutInflater.from(mContext);}

        if(mImageWorker == null){
            mImageWorker = new ImageWorker(this.mContext, R.drawable.empty_photo);
        }
    }

    public void loadData(Context context) {
        dataList = LocationDataParser.getLocationList(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        ViewHolder viewHolder;
        view = inflater.inflate((viewType==0)? R.layout.image_slide_show : R.layout.featured_list_layout, parent, false);
        viewHolder = new ViewHolder(view, viewType);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder vHolder, final int position) {
        if(position==0) {
            vHolder.vButton.setText(R.string.find_flights);
            vHolder.vButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mActivity, BrowserActivity.class);
                    intent.putExtra(Common.URL,"https://www.trafalgaronline.com/");
                    mActivity.startActivity(intent);
                }
            });

            GalleryImageAnimation.init(mActivity, vHolder.vSlides, vHolder.vCardView);
        }else{
            final int loc = position - 1;
            vHolder.vTitle.setText(dataList.get(loc).getTitle());
            if(!dataList.get(loc).getSubTitle().isEmpty()) {
                vHolder.vSubTitle.setText(dataList.get(loc).getSubTitle());
            }else{
                vHolder.vSubTitle.setVisibility(View.GONE);
            }

            String img = dataList.get(loc).getImage().replace(".","_640.");
            mImageWorker.loadImage(Common.API_SERVER + "images/" + img, vHolder.vImage);
            vHolder.vImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, vHolder.vImage, "featured_photo");
                    Intent intent = new Intent(mActivity, FeaturedItemActivity.class);
                    intent.putExtra(DESTINATION_DATA,dataList.get(loc));
                    mActivity.startActivity(intent, optionsCompat.toBundle());
                }
            });
            vHolder.vCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String phone = (!dataList.get(loc).getPhone().isEmpty())? dataList.get(loc).getPhone() : mContext.getResources().getString(R.string.company_phone_number);
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
                    mActivity.startActivity(intent);
                }
            });
            vHolder.vMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, vHolder.vImage, "featured_photo");
                    Intent intent = new Intent(mActivity, FeaturedItemActivity.class);
                    intent.putExtra(DESTINATION_DATA,dataList.get(loc));
                    mActivity.startActivity(intent, optionsCompat.toBundle());
                }
            });
            vHolder.vBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Intent intent = new Intent(mActivity, BookActivity.class);
                    //ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, vHolder.vImage, "featured_photo");
                    Intent intent = new Intent(mActivity, BrowserActivity.class);
                    intent.putExtra(Common.URL,"https://www.trafalgaronline.com/");
                    mActivity.startActivity(intent);
                }
            });
            vHolder.vShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utility.createShareIntent(mActivity, mContext.getString(R.string.share_feat_loc, dataList.get(loc).getTitle()), dataList.get(loc).getImage());
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position==0)? 0 : 1; //0 is button | 1 is info list
    }

    @Override
    public int getItemCount() {
        if(dataList==null)
            return 1; // show the slide show even with a empty list
        return dataList.size() + 1;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CardView  vCardView;
        TextView  vTitle, vSubTitle;
        ImageView vImage, vSlides;
        Button    vButton, vMore, vBook, vCall, vShare;

        private ViewHolder(View v, int viewType){
            super(v);
            if(viewType==0) {
                vCardView   = v.findViewById(R.id.card_view);
                vSlides     = v.findViewById(R.id.slide_show);
                vButton     = v.findViewById(R.id.button);
            }else {
                vTitle      = v.findViewById(R.id.title);
                vSubTitle   = v.findViewById(R.id.sub_title);
                vImage      = v.findViewById(R.id.photo);
                vMore       = v.findViewById(R.id.more);
                vBook       = v.findViewById(R.id.book);
                vCall       = v.findViewById(R.id.call);
                vShare      = v.findViewById(R.id.share);
            }
        }
    }
}