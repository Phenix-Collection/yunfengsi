package com.qianfujiaoyu.Setting;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.qianfujiaoyu.Activitys.Home_Zixun_detail;
import com.qianfujiaoyu.Activitys.ZiXun_Detail;
import com.qianfujiaoyu.Adapter.Mine_SC_adapter;
import com.qianfujiaoyu.Model_activity.activity_Detail;
import com.qianfujiaoyu.R;
import com.qianfujiaoyu.Utils.AnalyticalJSON;
import com.qianfujiaoyu.Utils.ApisSeUtil;
import com.qianfujiaoyu.Utils.Constants;
import com.qianfujiaoyu.Utils.DimenUtils;
import com.qianfujiaoyu.Utils.PreferenceUtil;
import com.qianfujiaoyu.Utils.StatusBarCompat;
import com.qianfujiaoyu.Utils.mApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/10/18.
 */
public class Activity_ShouCang extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private ImageView back;
    private TextView title;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private SharedPreferences sp;
    private Mine_SC_adapter SCadapter;
    private static final String TAG = "Activity_ShouCang";
    List<HashMap<String, String>> list;
    private TextView tip;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shoucang_activity);

        initView();
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                getData();
            }
        });


    }

    /**
     * 获取数据
     */
    private void getData() {
        if (sp == null) {
            sp = getSharedPreferences("user", Context.MODE_PRIVATE);
        }
//        if (sp.getString("user_id", "").equals("") || sp.getString("uid", "").equals("")) {
//            tip.setText("暂无收藏信息,请点击头像登录");
//            tip.setVisibility(View.VISIBLE);
//            SCadapter.list.clear();
//            SCadapter.notifyDataSetChanged();
//            return;
//        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject js=new JSONObject();
                    try {
                        js.put("m_id",Constants.M_id);
                        js.put("user_id", PreferenceUtil.getUserIncetance(mApplication.getInstance()).getString("user_id",""));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m=ApisSeUtil.i(js);
                    String data = OkGo.post(Constants.news_sc_list_Ip).tag(TAG)
                             .params("key",m.K())
                            .params("msg",m.M())
                            .execute().body().string();
                    if (!data.equals("")) {
                        Log.w(TAG, "run: news_+-=-getData-=-=-=" + data);
                        List<HashMap<String, String>> mlist = AnalyticalJSON.getList(data, "news");///图文
                        if (mlist == null) {

                        } else {
                            if (list != null) list.addAll(mlist);
                        }
                    }
                    String data2= OkGo.post(Constants.Zixun_shoucang_list_IP).tag(TAG)
                            .params("key",m.K())
                            .params("msg",m.M())
                            .execute().body().string();
                    if (!data2.equals("")) {
                        Log.w(TAG, "run: news_+-=-getData-=-=-=" + data2);
                        List<HashMap<String, String>> mlist = AnalyticalJSON.getList(data2, "draft");///图文
                        if (mlist == null) {

                        } else {
                            if (list != null) list.addAll(mlist);
                        }
                    }
                    String data1 = OkGo.post(Constants.Activity_Shoucang_list_IP).tag(TAG)
                            .params("key",m.K())
                            .params("msg",m.M())
                            .execute().body().string();
                    if (!data1.equals("")) {
                        List<HashMap<String, String>> mlist = AnalyticalJSON.getList(data1, "activity");//活动
                        if (mlist == null) {

                        } else {
                            if (list != null) list.addAll(mlist);
                        }
                    }

//                    String data3 = OkHttpUtils.post(Constants.Shucheng_shoucang_list_Ip).tag(TAG).params("user_id", sp.getString("user_id", "")).params("key", Constants.safeKey)
//                            .execute().body().string();
//                    if (!data3.equals("")) {
//                        Log.w(TAG, "run: 书城+-=-getData-=-=-=" + data3);
//                        List<HashMap<String, String>> mlist = AnalyticalJSON.getList(data3, "books");//图书
//                        if (mlist == null) {
//
//                        } else {
//                            if (list != null) list.addAll(mlist);
//                        }
//                    }

                    listView.post(new Runnable() {
                        @Override
                        public void run() {
                            if (SCadapter == null) {
                                SCadapter = new Mine_SC_adapter(Activity_ShouCang.this);
                            }
                            if (list != null) {
                                if (list.size() == 0) {
                                    tip.setVisibility(View.VISIBLE);
                                } else {
                                    if (SCadapter != null && listView != null) {
                                        List<HashMap<String, String>> list1 = list;
                                        SCadapter.addList(list1);
                                        listView.setAdapter(SCadapter);
                                        tip.setVisibility(View.GONE);
                                    }
                                }
                            }

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(list!=null&&list.size()!=0){
                                tip.setVisibility(View.GONE);
                            }else{
                                tip.setVisibility(View.VISIBLE);
                            }
                            if(swipeRefreshLayout.isRefreshing())swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }
            }
        }).start();
    }

    private void initView() {
        tip= (TextView) findViewById(R.id.tip);
        Drawable d= ContextCompat.getDrawable(this,R.drawable.load_nothing);
        int dp120=DimenUtils.dip2px(this,120);
        d.setBounds(0,0,dp120,dp120);
        tip.setCompoundDrawables(null,d,null,null);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        back = (ImageView) findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        back.setImageResource(R.drawable.back);
        title = (TextView) findViewById(R.id.title);
        title.setText(mApplication.ST("收藏"));
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

                if (view1.getText().toString().equals(mApplication.ST("图文"))) {
                    intent.setClass(mApplication.getInstance(), ZiXun_Detail.class);
                } 
                else if (view1.getText().toString().equals(mApplication.ST("活动"))) {
                    intent.setClass(mApplication.getInstance(), activity_Detail.class);
                }
                else if (view1.getText().toString().equals(mApplication.ST("动态"))) {
                    intent.setClass(mApplication.getInstance(), Home_Zixun_detail.class);
                }
                intent.putExtra("id", id1);
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
//        getData();
    }
}
