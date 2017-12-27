package com.maimaizu.View;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fangdai.activity.FangDai;
import com.maimaizu.Activitys.MaiFang_ChuZu;
import com.maimaizu.Activitys.SearchResult;
import com.maimaizu.R;
import com.maimaizu.Utils.DimenUtils;
import com.maimaizu.Utils.LoginUtil;
import com.maimaizu.Utils.mApplication;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/4/25.
 */

public class mHeadView extends FrameLayout {
    private static final String img = "img";
    private static final String text = "text";
    private Banner banner;
    private RecyclerView recyclerView;
    private GridAdapter adapter;
    private String titles[] = new String[]{"买新房", "买二手房", "找租房", "我要卖房", "我要出租", "房贷计算"};
    private int images[] = new int[]{R.drawable.xinfang, R.drawable.ershoufang, R.drawable.zufang, R.drawable.maifang, R.drawable.woyaochuzu, R.drawable.fangdaijisuanqi};
    private Context context;

    public mHeadView(Context context) {
        super(context);
        initView(context);
        this.context = context;
    }

    public mHeadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        this.context = context;
    }


    // TODO: 2017/4/25 初始化
    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.mheadview, this, true);
        banner = (Banner) view.findViewById(R.id.banner);
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycle);
        RecyclerView.LayoutManager rl = new GridLayoutManager(context, 4);
        recyclerView.setLayoutManager(rl);
        adapter = new GridAdapter(R.layout.home_grid_item, initData());
        recyclerView.setAdapter(adapter);

    }

    // TODO: 2017/4/25 初始化item数据源
    private List<HashMap<String, String>> initData() {
        List<HashMap<String, String>> l = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            HashMap<String, String> map = new HashMap<>();
            map.put(img, images[i] + "");
            map.put(text, titles[i]);
            l.add(map);
        }
        return l;
    }

//    // TODO: 2017/4/25 初始化item
//    private View initItemView(Context context) {
//
//        return l;
//    }

    // TODO: 2017/4/25 设置banner数据源
    public void setData(List<String> data) {
        if (data != null) {
            banner.setImages(data);
            banner.setImageLoader(new GlideImageLoader());
            banner.setOffscreenPageLimit(data.size());
            banner.start();
        }


    }

    // TODO: 2017/4/25 设置banner点击事件
    public void setBannerListener(OnBannerListener listen) {
        banner.setOnBannerListener(listen);
    }


    // TODO: 2017/4/25 适配器
    private class GridAdapter extends BaseQuickAdapter<HashMap<String, String>> {

        private GridAdapter(int res, List<HashMap<String, String>> data) {
            super(res, data);

        }

        @Override
        protected void convert(BaseViewHolder holder, final HashMap<String, String> map) {
            holder.setText(R.id.text, mApplication.ST(map.get(text)));
            Glide.with(context).load(Integer.valueOf(map.get(img))).override(DimenUtils.dip2px(context, 50), DimenUtils.dip2px(context, 50))
                    .thumbnail(0.6f)
                    .fitCenter().into((ImageView) holder.getView(R.id.img));

            holder.convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    switch (map.get(text)) {
                        case "买新房":
                            intent.setClass(context, SearchResult.class);
                            intent.putExtra("flag", true);
                            intent.putExtra("type", 1);
                            break;
                        case "买二手房":
                            intent.setClass(context, SearchResult.class);
                            intent.putExtra("flag", true);
                            intent.putExtra("type", 2);
                            break;
                        case "找租房":
                            intent.setClass(context, SearchResult.class);
                            intent.putExtra("flag", true);
                            intent.putExtra("type", 3);
                            break;
                        case "我要卖房":
                            if (new LoginUtil().checkLogin(context)) {
                                intent.setClass(context, MaiFang_ChuZu.class);
                                intent.putExtra("type", 1);
                            }else{
                                return;
                            }

                            break;
                        case "我要出租":
                            if (new LoginUtil().checkLogin(context)) {
                                intent.setClass(context, MaiFang_ChuZu.class);
                                intent.putExtra("type", 2);
                            }else{
                                return;
                            }
                            break;
                        case "房贷计算":
                            intent.setClass(context, FangDai.class);
                            break;
                    }
                    context
                            .startActivity(intent);
                }
            });
        }
    }


    private  class GlideImageLoader extends ImageLoader {

        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            //Glide 加载图片简单用法
            int width = context.getResources().getDisplayMetrics().widthPixels;
            Glide.with(context).load(path).thumbnail(0.5f).override(width, width * 3 / 10).into(imageView);
        }
    }

}
