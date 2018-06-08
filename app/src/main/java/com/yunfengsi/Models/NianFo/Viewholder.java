package com.yunfengsi.Models.NianFo;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.yunfengsi.R;

import cn.carbs.android.avatarimageview.library.AvatarImageView;

/**
 * Created by Administrator on 2016/8/22.
 */
class Viewholder extends RecyclerView.ViewHolder {
    public AvatarImageView mroundedimageview;
    public Viewholder(View itemView) {
        super(itemView);
        mroundedimageview = (AvatarImageView) itemView.findViewById(R.id.griditme_roundedimageview);
        Log.d("我就打哈日志：","222222");
    }
}
