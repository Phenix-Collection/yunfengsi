package com.yunfengsi.Models.Model_activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.yunfengsi.R;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.mApplication;

import java.util.HashMap;
import java.util.List;



/**
 * Created by Administrator on 2016/10/7.
 */
public class activity_adapter extends BaseAdapter {
    public List<HashMap<String, String>> list;
    private Context context;
    private LayoutInflater inflater;
    private int screeWidth;
    private int dp10;
    private int dp180;
    private static final String TAG = "activity_adapter";
    public activity_adapter(Context context, List<HashMap<String, String>> list) {
        super();
        this.list = list;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        screeWidth = context.getResources().getDisplayMetrics().widthPixels;
        dp10 = DimenUtils.dip2px(context, 10);
        dp180 = DimenUtils.dip2px(context, 180);
    }
    public void setList(List<HashMap<String, String>> list){
        this.list=list;
    }
    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        } else if (position == 1) {
            return 1;
        } else {
            return 2;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Holder holder = null;
        Holder2 holder2 = null;
        HashMap<String, String> map = list.get(position);
        if (view == null) {
            if (getItemViewType(position) == 0) {
                holder = new Holder();
                view = inflater.inflate(R.layout.activity_header, parent, false);
                holder.title = view.findViewById(R.id.activity_title);
                holder.imageView = view.findViewById(R.id.activity_image);
                ((TextView) view.findViewById(R.id.title1)).setText(mApplication.ST("热门活动"));
                view.setTag(holder);
            } else {
                holder2 = new Holder2();
                if (getItemViewType(position) == 1) {
                    view = inflater.inflate(R.layout.activity_header1, parent, false);
                    ((TextView) view.findViewById(R.id.title2)).setText(mApplication.ST("最新活动"));
                } else {
                    view = inflater.inflate(R.layout.activity_header2, parent, false);
                }
                holder2.title = view.findViewById(R.id.activity_item_title);
                holder2.content = view.findViewById(R.id.activity_item_content);
                holder2.imageView = view.findViewById(R.id.activity_item_img);
                holder2.peopleNum = view.findViewById(R.id.activity_item_peopleNum);
                holder2.time = view.findViewById(R.id.activity_item_time);
                view.setTag(holder2);
            }
        } else {
            if (getItemViewType(position) == 0) {
                holder = (Holder) view.getTag();
            } else {
                holder2 = (Holder2) view.getTag();
            }
        }
        if (getItemViewType(position) == 0) {//最热活动
            Glide.with(context).load(map.get("image1")).asBitmap().centerCrop().override(screeWidth - (dp10*2), dp180).into(new BitmapImageViewTarget(holder.imageView) {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    RoundedBitmapDrawable rbd = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                    rbd.setCornerRadius(10);
                    setDrawable(rbd);
                }
            });
            holder.title.setText(mApplication.ST(map.get("title")));
            holder.title.setTag(map.get("id"));
        } else {
            Glide.with(context).load(map.get("image1")).asBitmap().centerCrop().override(screeWidth * 9 / 25, screeWidth*27 /100).into(new BitmapImageViewTarget(holder2.imageView) {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    RoundedBitmapDrawable rbd = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                    rbd.setCornerRadius(7);
                    setDrawable(rbd);
                }
            });
            holder2.title.setText(mApplication.ST(map.get("title")));
            holder2.title.setTag(map.get("id"));
            holder2.content.setText(mApplication.ST(map.get("abstract")));
            holder2.peopleNum.setText(mApplication.ST("报名人数:" + map.get("enrollment")));
            holder2.time.setText(mApplication.ST("报名时间:" + map.get("start_time") + " 至 " + map.get("end_time")));
        }

        return view;
    }

    static class Holder {
        TextView title;
        ImageView imageView;
    }

    static class Holder2 {
        TextView title;
        TextView content;
        TextView time;
        TextView peopleNum;
        ImageView imageView;
    }

}
