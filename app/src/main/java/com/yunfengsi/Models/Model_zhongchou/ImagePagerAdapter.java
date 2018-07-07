package com.yunfengsi.Models.Model_zhongchou;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.yunfengsi.R;
import com.yunfengsi.Utils.DimenUtils;

import java.util.List;

/**
 * Created by Administrator on 2016/9/21 0021.
 * 图片轮播适配器
 */
public class ImagePagerAdapter extends BaseAdapter {

    private Context context;
    private List<String> imageIdList;
    private int size;
    //是否无限循环
    private boolean isInfiniteLoop;
    private int screenWidth;

    public ImagePagerAdapter(Context context, List<String> imageIdList) {
        this.context = context;
        this.imageIdList = imageIdList;
        if(imageIdList!=null){
            this.size=imageIdList.size();
        }
        isInfiniteLoop=false;
        screenWidth=context.getResources().getDisplayMetrics().widthPixels;
    }

    private int getPosition(int position){
        return isInfiniteLoop?position%size:position;
    }

    @Override
    public int getCount() {
        return isInfiniteLoop?Integer.MAX_VALUE:imageIdList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView==null){
            holder=new ViewHolder();
            convertView= LayoutInflater.from(context).inflate(R.layout.item_splash,null);
            holder.imageView= convertView.findViewById(R.id.img);
            convertView.setTag(holder);
        }else {
            holder=(ViewHolder)convertView.getTag();
        }

        Glide.with(context).load(this.imageIdList.get(getPosition(position)))
                .override(screenWidth, DimenUtils.dip2px(context,200))
                .centerCrop().into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        return convertView;
    }

    private static class ViewHolder{
        ImageView imageView;
    }

    /**
     * @return the isInfiniteLoop
     */
    public boolean isInfiniteLoop() {
        return isInfiniteLoop;
    }

    public ImagePagerAdapter setInfiniteLoop(boolean isInfiniteLoop) {
        this.isInfiniteLoop = isInfiniteLoop;
        return this;
    }

    public ImagePagerAdapter isInfiniteLoop(boolean isInfiniteLoop){
        this.isInfiniteLoop=isInfiniteLoop;
        return this;
    }
}
