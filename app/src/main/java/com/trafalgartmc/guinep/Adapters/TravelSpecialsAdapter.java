package com.trafalgartmc.guinep.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.trafalgartmc.guinep.BrowserActivity;
import com.trafalgartmc.guinep.Classes.DataObject;
import com.trafalgartmc.guinep.Classes.SpecialsDataParser;
import com.trafalgartmc.guinep.FeaturedItemActivity;
import com.trafalgartmc.guinep.GalleryUtility.ImageWorker;
import com.trafalgartmc.guinep.R;
import com.trafalgartmc.guinep.Utility.Common;
import com.trafalgartmc.guinep.Utility.Utility;

import java.util.List;

/**
 * @author Rohan Morris
 * date 3/19/2017.
 */

public class TravelSpecialsAdapter extends RecyclerView.Adapter<TravelSpecialsAdapter.ViewHolder> {
    public static final String SPECIALS_DATA = "com.trafalgartmc.guinep.specials";
    private final String IS_SPECIALS_SCREEN = "com.trafalgartmc.guinep.DataKey";
    private LayoutInflater inflater;
    private Context mContext;
    private Activity mActivity;
    private List<DataObject> dataList;
    private ImageWorker mImageWorker;

    public TravelSpecialsAdapter(Activity activity) {
        mActivity = activity;
        mContext = activity.getBaseContext();

        if(inflater == null){inflater = LayoutInflater.from(mContext);}

        if(mImageWorker == null){
            mImageWorker = new ImageWorker(mContext, R.drawable.empty_photo);
        }
    }

    public void loadData(Context context) {
        dataList = SpecialsDataParser.getSpecialsList(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        ViewHolder viewHolder;

        view = inflater.inflate(R.layout.featured_list_layout, parent, false);
        viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder vHolder, @SuppressLint("RecyclerView") final int loc)
    {
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
                intent.putExtra(IS_SPECIALS_SCREEN,true);
                intent.putExtra(SPECIALS_DATA,dataList.get(loc));
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
                intent.putExtra(IS_SPECIALS_SCREEN,true);
                intent.putExtra(SPECIALS_DATA,dataList.get(loc));
                mActivity.startActivity(intent, optionsCompat.toBundle());
            }
        });
        vHolder.vBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, BrowserActivity.class);
                intent.putExtra(Common.URL,"https://www.trafalgaronline.com/");
                mActivity.startActivity(intent);
            }
        });
        vHolder.vShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.createShareIntent(mActivity,
                        mContext.getString(R.string.share_special, dataList.get(loc).getTitle()),
                        dataList.get(loc).getImage());
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
        TextView vTitle, vSubTitle;
        ImageView vImage;
        Button vMore, vBook, vCall, vShare;

        private ViewHolder(View v){
            super(v);
            vTitle      = (TextView) v.findViewById(R.id.title);
            vSubTitle   = (TextView) v.findViewById(R.id.sub_title);
            vImage      = (ImageView) v.findViewById(R.id.photo);
            vMore       = (Button) v.findViewById(R.id.more);
            vBook       = (Button) v.findViewById(R.id.book);
            vCall       = (Button) v.findViewById(R.id.call);
            vShare      = (Button) v.findViewById(R.id.share);
        }
    }
}