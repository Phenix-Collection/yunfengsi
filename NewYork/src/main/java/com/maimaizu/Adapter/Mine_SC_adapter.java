package com.maimaizu.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.maimaizu.R;
import com.maimaizu.Utils.TimeUtils;
import com.maimaizu.Utils.mApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/7/21.
 */
public class Mine_SC_adapter extends BaseAdapter {
    public List<HashMap<String, String>> list;
    public Context context;
    public LayoutInflater inflater;
    private int screenWidth;

    public Mine_SC_adapter(Context context) {
        super();
        this.context = context;
        inflater = LayoutInflater.from(context);
        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        list=new ArrayList<>();
    }

    public void addList(List<HashMap<String, String>> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list==null?0:list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View view = convertView;
        HashMap<String, String> map = list.get(position);
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.mine_shoucang_item, parent, false);
            holder.image = (ImageView) view.findViewById(R.id.mine_shoucang_item_image);
            holder.title = (TextView) view.findViewById(R.id.mine_shoucang_item_title);
            holder.time = (TextView) view.findViewById(R.id.mine_shoucang_item_time);
            holder.user = (TextView) view.findViewById(R.id.mine_shoucang_item_user);
            holder.type= (TextView) view.findViewById(R.id.mine_shoucang_item_type);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        if(map.get("money")==null&&map.get("end_time")==null&&map.get("time")!=null){
            holder.time.setText(TimeUtils.getTrueTimeStr(map.get("issue_time")));
            holder.title.setText(mApplication.ST(map.get("title")));
            holder.user.setText(mApplication.ST(map.get("issuer")));
            holder.type.setText(mApplication.ST("资讯"));
            holder.type.setTag(map.get("id"));
            Glide.with(context).load(map.get("image")).override(screenWidth * 3 / 10, screenWidth*6/ 25).error(R.drawable.mdivider).centerCrop().into(holder.image);
        }else if(map.get("cy_people")==null&&map.get("end_time")!=null){
            holder.type.setText(mApplication.ST("活动"));
            holder.type.setTag(map.get("id"));
            holder.user.setText(mApplication.ST(map.get("author")));
            holder.title.setText(mApplication.ST(map.get("title")));
            Glide.with(context).load(map.get("image1")).override(screenWidth * 3 / 10, screenWidth*6/ 25).error(R.drawable.mdivider).centerCrop().into(holder.image);
            holder.time.setText(mApplication.ST("结束时间："+map.get("end_time")));
        }else if(map.get("money")!=null){
            holder.type.setText(mApplication.ST("商品"));
            holder.type.setTag(map.get("id"));
            holder.user.setText(mApplication.ST(map.get("title")));
            holder.title.setText("￥"+map.get("money"));
            Glide.with(context).load(map.get("image1")).override(screenWidth * 3 / 10, screenWidth*6/ 25).error(R.drawable.mdivider).centerCrop().into(holder.image);
            holder.time.setText(TimeUtils.getTrueTimeStr(map.get("time")));
        }else if(map.get("address")!=null){
            holder.type.setText(mApplication.ST("店铺"));
            holder.type.setTag(map.get("id"));
            holder.title.setText(mApplication.ST(map.get("address")));
            holder.user.setText(mApplication.ST(map.get("title")));
//            holder.time.setText("销量 "+map.get("sales")+"    收藏 "+map.get("likes"));
            Glide.with(context).load(map.get("image1")).override(screenWidth * 3 / 10, screenWidth*6/ 25).error(R.drawable.mdivider).centerCrop().into(holder.image);
        }

        return view;
    }

    static class ViewHolder {
        ImageView image;
        TextView title, user, time,type;
    }
}
