package com.maimaizu.Mine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.maimaizu.Activitys.ZiXun_Detail;
import com.maimaizu.Adapter.Mine_SC_adapter;
import com.maimaizu.R;
import com.maimaizu.Utils.AnalyticalJSON;
import com.maimaizu.Utils.Constants;
import com.maimaizu.Utils.PreferenceUtil;
import com.maimaizu.Utils.StatusBarCompat;
import com.maimaizu.Utils.mApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/10/18.
 */
public class Activity_ShouCang extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private ImageView back, tip;
    private TextView title;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private SharedPreferences sp;
    private Mine_SC_adapter SCadapter;
    private static final String TAG = "Activity_ShouCang";
    private List<HashMap<String, String>> list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shoucang_activity);

        initView();
        getData();

    }

    /**
     * 获取数据
     */
    private void getData() {
        if (sp == null) {
            sp = PreferenceUtil.getUserIncetance(this);
        }
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
        final List<HashMap<String ,String >>list1=new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                    String data = OkGo.post(Constants.getHouseKeeps).tag(TAG).params("user_id", sp.getString("user_id", "")).params("key", Constants.safeKey)
//                            .execute().body().string();
//                    if (!data.equals("")) {
//                        Log.w(TAG, "run: news_+-=-getData-=-=-=" + data);
//                        List<HashMap<String, String>> mlist = AnalyticalJSON.getList_zj(data);///商品
//                        if (mlist != null) {
//                            list1.addAll(mlist);
//                        }
//                    }
//                    String data1 = OkHttpUtils.post(Constants.Store_shoucang_list).tag(TAG).params("user_id", sp.getString("user_id", "")).params("key", Constants.safeKey)
//                            .execute().body().string();
//                    if (!data1.equals("")) {
//                        Log.w(TAG, "run: store_+-=-getData-=-=-=" + data1);
//                        List<HashMap<String, String>> mlist = AnalyticalJSON.getList(data1,"storelist");///商品
//                        if (mlist != null) {
//                            list1.addAll(mlist);
//                        }
//                    }


//                    String data1 = OkHttpUtils.post(Constants.Activity_Shoucang_list_IP).tag(TAG).params("user_id", sp.getString("user_id", "")).params("key", Constants.safeKey)
//                            .execute().body().string();
//                    if (!data1.equals("")) {
//                        List<HashMap<String, String>> mlist = AnalyticalJSON.getList(data1, "activity");//活动
//                        if (mlist == null) {
//
//                        } else {
//                            if (list != null) list.addAll(mlist);
//                        }
//                    }
                    String data2 = OkGo.post(Constants.news_sc_list_Ip).tag(TAG).params("user_id", sp.getString("user_id", "")).params("key", Constants.safeKey)
                            .execute().body().string();
                    if (!data2.equals("")) {
                        Log.w(TAG, "run: news+-=-getData-=-=-=" + data2);
                        List<HashMap<String, String>> mlist = AnalyticalJSON.getList(data2, "news");//资讯
                        if (mlist != null) {
                            list1.addAll(mlist);
                        }
                    }
//                    String data3 = OkHttpUtils.post(Constants.Activity_Shoucang_list_IP).tag(TAG).params("user_id", sp.getString("user_id", "")).params("key", Constants.safeKey)
//                            .execute().body().string();
//                    if (!data3.equals("")) {
//                        Log.w(TAG, "run: 活动+-=-getData-=-=-=" + data3);
//                        List<HashMap<String, String>> mlist = AnalyticalJSON.getList(data3, "activity");//活动
//                        if (mlist != null) {
//                            list1.addAll(mlist);
//                        }
//                    }

                    tip.post(new Runnable() {
                        @Override
                        public void run() {
                            if (SCadapter == null) {
                                SCadapter = new Mine_SC_adapter(Activity_ShouCang.this);
                            }
                                if (list1.size() == 0) {
                                    if (tip != null && tip.getVisibility() != View.VISIBLE) {
                                        if(swipeRefreshLayout!=null&&swipeRefreshLayout.isRefreshing())swipeRefreshLayout.setRefreshing(false);
                                        Toast.makeText(Activity_ShouCang.this, mApplication.ST("暂无收藏信息"), Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    if (SCadapter != null && listView != null && tip != null) {
                                        list.addAll(list1);
                                        SCadapter.addList(list);
                                        listView.setAdapter(SCadapter);
                                        if(swipeRefreshLayout!=null&&swipeRefreshLayout.isRefreshing())swipeRefreshLayout.setRefreshing(false);
                                    }
                                }
                        }
                    });
                } catch (Exception e) {
                    listView.post(new Runnable() {
                        @Override
                        public void run() {
                            if (tip != null && tip.getVisibility() != View.VISIBLE) {
                                if(swipeRefreshLayout!=null&&swipeRefreshLayout.isRefreshing())swipeRefreshLayout.setRefreshing(false);
                                Toast.makeText(Activity_ShouCang.this, mApplication.ST("暂无收藏信息"), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void initView() {
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        back = (ImageView) findViewById(R.id.title_back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        back.setImageResource(R.drawable.back);
        title = (TextView) findViewById(R.id.title_title);
        title.setText(mApplication.ST("收藏"));
        tip = (ImageView) findViewById(R.id.shoucang_tip);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swip);
        swipeRefreshLayout.setColorSchemeResources(R.color.main_color);
        swipeRefreshLayout.setOnRefreshListener(this);
        list = new ArrayList<>();
        listView = (ListView) findViewById(R.id.shoucang_listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView view1 = (TextView) view.findViewById(R.id.mine_shoucang_item_type);
                String id1 = view1.getTag().toString();
                Intent intent = new Intent();

                if (view1.getText().toString().equals(mApplication.ST("资讯"))) {
                    intent.setClass(mApplication.getInstance(), ZiXun_Detail.class);
                }
//                else if(view1.getText().toString().equals(mApplication.ST("商品"))){
//                    intent.setClass(mApplication.getInstance(), Order_detail.class);
//                }
                intent.putExtra("id", id1);
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
        }
    }

    @Override
    public void onRefresh() {
//        getData();
        tip.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        },3000);
    }
}
