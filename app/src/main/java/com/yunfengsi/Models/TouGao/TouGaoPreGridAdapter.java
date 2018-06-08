package com.yunfengsi.Models.TouGao;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.yunfengsi.R;
import com.yunfengsi.Setting.ViewPagerActivity;
import com.yunfengsi.Models.TouGao.Photo.PhotoPicker;
import com.yunfengsi.Utils.DimenUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/10/20.
 */
public class TouGaoPreGridAdapter extends BaseAdapter {
    //获取从选择器中选择的图片地址
    private List<HashMap<String ,String >> mImgs=new ArrayList<>();
    private WeakReference<Activity> weakReference;
    private Activity context;
    private LayoutInflater inflater;

    private int  screenWidth;
    private int dp100;

    public TouGaoPreGridAdapter(Activity context, List<HashMap<String ,String >> mImgs) {
        super();
        weakReference=new WeakReference<Activity>(context);
        this.context=weakReference.get();
        this.mImgs=mImgs;
        inflater=LayoutInflater.from(context);
        dp100= DimenUtils.dip2px(context,90);
    }
    public void setSingleWidth(int w){
        dp100=DimenUtils.dip2px(context,w);
    }

    public void setmImgs(List<HashMap<String ,String >>mImgs ){
        this.mImgs=mImgs;
    }
    @Override
    public int getCount() {
        return mImgs==null?0:mImgs.size();
    }

    @Override
    public Object getItem(int position) {
        return mImgs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, final ViewGroup parent) {
        Viewholder viewholder=null;
        if(view==null){
            viewholder=new Viewholder();
            view= inflater.inflate(R.layout.tougao_yulan_grid_item,parent,false);
            viewholder.imageView= (ImageView) view.findViewById(R.id.tougao_grid_item_img);
            viewholder.cancle= (ImageView) view.findViewById(R.id.tougao_grid_item_cancle);
            view.setTag(viewholder);
        }else{
            viewholder= (Viewholder) view.getTag();
        }

        if(mImgs.get(position).get("url").equals("add")){
            Glide.with(context).load(R.drawable.addimg_with_text).override(dp100,dp100).centerCrop().into(viewholder.imageView);
            viewholder.cancle.setVisibility(View.GONE);
        }else{
            Glide.with(context).load(mImgs.get(position).get("url")).override(dp100,dp100).centerCrop().into(viewholder.imageView);
            viewholder.cancle.setVisibility(View.GONE);
//            viewholder.cancle.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if(oncCancleListener!=null) {
//                        oncCancleListener.onCancle(position);
//                    }
//                }
//            });
        }

        viewholder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mImgs.get(position).get("url").equals("add")){
                    int i=mImgs.size()-1;//已经选择的图片数量
                    Intent intent=new Intent(context, PhotoPicker.class);
                    intent.putExtra("num",i);
                    context.startActivityForResult(intent,000);
                }else{
                    Intent intent=new Intent(context, ViewPagerActivity.class);
                    String image=(String) parent.getTag();
                    intent.putExtra("url",image );
                    Log.w(TAG, "onClick:图片地址数组： "+image);
                    context.startActivity(intent);
                }
            }
        });
        return view;
    }

    private static final String TAG = "TouGaoPreGridAdapter";
    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }


    static  class  Viewholder{
        ImageView imageView,cancle;
    }
}
