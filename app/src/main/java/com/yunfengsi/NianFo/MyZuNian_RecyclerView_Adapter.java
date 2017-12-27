package com.yunfengsi.NianFo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.yunfengsi.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/8/22.
 */
public class MyZuNian_RecyclerView_Adapter  extends RecyclerView.Adapter<Viewholder> {
    private static final String TAG = "RecyclerView_Adapter";
    private Context context;
    private ArrayList<HashMap<String,String>> list;
    public MyZuNian_RecyclerView_Adapter(Context context,ArrayList<HashMap<String,String>> list){
        this.context=context;
        this.list=list;
    }
    @Override
    public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.gridview_itme, parent, false);
        return new Viewholder(view);
    }
    @Override
    public void onBindViewHolder(Viewholder holder, final int i) {
       Glide.with(context).load(list.get(i).get("user_image")).into(holder.mroundedimageview);

    }

    @Override
    public long getItemId(int position) {
        return Integer.valueOf(list.get(position).get("id"));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
