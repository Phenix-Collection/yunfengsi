package com.yunfengsi.Models.NianFo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.sdk.android.push.AndroidPopupActivity;
import com.bumptech.glide.Glide;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.yunfengsi.R;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.LoginUtil;
import com.yunfengsi.Utils.ProgressUtil;
import com.yunfengsi.Utils.ShareManager;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.mApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NianFo extends AndroidPopupActivity implements View.OnClickListener {
    private PopupWindow pp;//加号弹出窗口
    private ShareAction action;

    private static final String TAG = "NianFo";





    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.nianfo);
        mApplication.getInstance().addActivity(this);
        findViewById(R.id.title_back).setVisibility(View.VISIBLE);
        findViewById(R.id.title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.title_image2).setVisibility(View.VISIBLE);
        findViewById(R.id.title_image2).setOnClickListener(this);
        ((ImageView) findViewById(R.id.title_image2)).setImageResource(R.drawable.fenxiang2);

        ((TextView) findViewById(R.id.title_title)).setText("共修");

//
        //图片地址数组
        SharedPreferences sp         = getSharedPreferences("user", Context.MODE_PRIVATE);
        ArrayList<String> imageList  = new ArrayList<>();
        ListView          mgridview  = findViewById(R.id.nianfo_home_gridview);
        int               dp10       = DimenUtils.dip2px(this, 10);
        int               dp180      = DimenUtils.dip2px(this, 150);
        int               dp7        = DimenUtils.dip2px(this, 7);
        int               screeWidth = getResources().getDisplayMetrics().widthPixels;


        ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();
        //为每个格子添加内容

        HashMap<String, Object> map = new HashMap<String, Object>();// 建立hashmap对象

        map.put("ItemImage", R.drawable.nianfo);
        map.put("title", "  念佛");

        HashMap<String, Object> map1 = new HashMap<String, Object>();// 建立hashmap对象
        map1.put("ItemImage", R.drawable.songjin);
        map1.put("title", "  诵经");
        HashMap<String, Object> map2 = new HashMap<String, Object>();// 建立hashmap对象
        map2.put("ItemImage", R.drawable.chizhou);
        map2.put("title", "  持咒");
        HashMap<String, Object> map5 = new HashMap<String, Object>();// 建立hashmap对象
        map5.put("ItemImage", R.drawable.fayuan);
        map5.put("title", "请发愿");
        HashMap<String, Object> map3 = new HashMap<String, Object>();// 建立hashmap对象
        map3.put("ItemImage", R.drawable.zhunian);
        map3.put("title", "  助念");
        HashMap<String, Object> map4 = new HashMap<String, Object>();// 建立hashmap对象
        map4.put("ItemImage", R.drawable.chanhui);
        map4.put("title", "  忏悔");

        lstImageItem.add(map5);
        lstImageItem.add(map);
        lstImageItem.add(map1);
        lstImageItem.add(map2);

        lstImageItem.add(map3);
        lstImageItem.add(map4);

        /**
         * 为GridView建立SimpleAdapter适配器
         */
        // SimpleAdapter()中的五个参数分别是：第一个context，第二个数据资源，第三个每一个子项的布局文件，第四个每一个子项中的Key数组
        // 第五个每一个子项中的Value数组
        mAdapter ad = new mAdapter(lstImageItem,this);
        mgridview.setAdapter(ad);// 添加适配器
        mgridview.setOnItemClickListener(new ItemClickListener());// 为每一个子项设置监听



    }



    @Override
    public void onDestroy() {
        super.onDestroy();

        mApplication.getInstance().romoveActivity(this);
    }

    @Override
    protected void onSysNoticeOpened(String s, String s1, Map<String, String> map) {

    }

    static class mAdapter extends BaseAdapter {
        private ArrayList<HashMap<String, Object>> l;
        private Context context;
        private int screenWidthl;

        public mAdapter(ArrayList<HashMap<String, Object>> item, Context context) {
            super();
            l = item;
            this.context = context;
            screenWidthl = context.getResources().getDisplayMetrics().widthPixels;
        }

        @Override
        public int getCount() {
            return l == null ? 0 : l.size();
        }

        @Override
        public Object getItem(int position) {
            return l.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int hei = parent.getHeight() / 6;
            View view = LayoutInflater.from(context).inflate(R.layout.grid_itme, parent, false);
            ViewGroup.LayoutParams vl = view.getLayoutParams();
            vl.height = hei;
            view.setLayoutParams(vl);
            ImageView i = view.findViewById(R.id.imageview);
            Glide.with(context).load((Integer) l.get(position).get("ItemImage")).override(
                    hei - DimenUtils.dip2px(context, 20), hei - DimenUtils.dip2px(context, 20))
                    .into(i);
            ((TextView) view.findViewById(R.id.title)).setText(String.valueOf(l.get(position).get("title")));

            return view;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ProgressUtil.dismiss();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.title_image2:
                UMWeb umWeb=new UMWeb("http://a.app.qq.com/o/simple.jsp?pkgname=com.yunfengsi");
                umWeb.setTitle("云峰寺App");
                umWeb.setDescription("快来云峰寺共修吧");
                umWeb.setThumb(new UMImage(this,R.drawable.indra_share));
                new ShareManager().shareWeb(umWeb,this);
                break;



        }
    }

    class ItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if (new LoginUtil().checkLogin(NianFo.this)) {
                Log.d("选中的Itme:", i + "");
                Intent intent = new Intent();
                if (i == 1) {
                    intent.setClass(mApplication.getInstance(), nianfo_home_tab1.class);
                } else if (i == 2) {
                    intent.setClass(mApplication.getInstance(), nianfo_home_tab2.class);
                } else if (i == 3) {
                    intent.setClass(mApplication.getInstance(), nianfo_home_tab3.class);
                } else if (i == 0) {
                    intent.setClass(mApplication.getInstance(), nianfo_home_tab6.class);
                } else if (i == 4) {
                    intent.setClass(mApplication.getInstance(), nianfo_home_tab4.class);
                } else if (i == 5) {
                    intent.setClass(mApplication.getInstance(), nianfo_home_tab5.class);
                }
                startActivity(intent);
            }

        }
    }




}
