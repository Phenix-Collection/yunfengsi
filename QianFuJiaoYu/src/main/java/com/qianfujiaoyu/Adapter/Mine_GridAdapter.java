package com.qianfujiaoyu.Adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qianfujiaoyu.Base.HomeManager;
import com.qianfujiaoyu.R;
import com.qianfujiaoyu.Utils.DimenUtils;
import com.qianfujiaoyu.Utils.mApplication;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/5/10.
 */

public class Mine_GridAdapter extends BaseItemDraggableAdapter<HashMap<String,Object>> {
    public Mine_GridAdapter(List<HashMap<String, Object>> data) {
        super(R.layout.item_mine, data);
    }

    @Override
    protected void convert(BaseViewHolder holder, HashMap<String, Object> map) {

        holder.setText(R.id.text, mApplication.ST(map.get(HomeManager.text).toString()));
        Glide.with(mApplication.getInstance()).load(map.get(HomeManager.img))
                .override(DimenUtils.dip2px(mApplication.getInstance(),40),DimenUtils.dip2px(mApplication.getInstance(),40))
                .fitCenter().into((ImageView) holder.getView(R.id.image));
    }


}
