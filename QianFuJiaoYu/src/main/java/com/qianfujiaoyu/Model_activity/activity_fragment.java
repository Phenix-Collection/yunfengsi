package com.qianfujiaoyu.Model_activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.qianfujiaoyu.R;
import com.qianfujiaoyu.Utils.AnalyticalJSON;
import com.qianfujiaoyu.Utils.ApisSeUtil;
import com.qianfujiaoyu.Utils.Constants;
import com.qianfujiaoyu.Utils.DimenUtils;
import com.qianfujiaoyu.Utils.ImageUtil;
import com.qianfujiaoyu.Utils.Network;
import com.qianfujiaoyu.Utils.StatusBarCompat;
import com.qianfujiaoyu.Utils.mApplication;
import com.qianfujiaoyu.View.LoadMoreListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;


/**
 * Created by Administrator on 2016/10/5.
 */
public class activity_fragment extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, LoadMoreListView.OnLoadMore {
    private View view;
    private SwipeRefreshLayout swip;//下拉刷新控件
    private LoadMoreListView listView;
    private ImageView tip;//加载失败的提示
    private boolean isFirstIn = true;
    private String page = "1";
    private String endPage = "";
    private activity_adapter adpter;
    private boolean isRefresh = false;//是否属于下拉刷新操作
    private TextView t;//加载数据的底部提示
    private ProgressBar p;//加载数据的底部进度
    private static final String TAG = "activity_fragment";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, ContextCompat.getColor(this, R.color.main_color));
        setContentView(R.layout.activity_fragment);
        initView();
    }





    /**
     * 初始化控件
     *
     *
     */
    private void initView() {
        ((TextView) findViewById(R.id.title)).setText("首页");
        ((ImageView) findViewById(R.id.back)).setVisibility(View.VISIBLE);
        ((ImageView) findViewById(R.id.back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((ImageView) findViewById(R.id.back)).setImageBitmap(ImageUtil.readBitMap(this,R.drawable.back));


        swip = (SwipeRefreshLayout) findViewById(R.id.activity_swip);
        listView = (LoadMoreListView)findViewById(R.id.activity_listview);
        swip.setOnRefreshListener(this);
        listView.setLoadMoreListen(this);
        listView.setOnItemClickListener(onItemClickListener);
        t = (TextView) (listView.footer.findViewById(R.id.load_more_text));
        p = (ProgressBar) (listView.footer.findViewById(R.id.load_more_bar));
        tip = (ImageView)findViewById(R.id.activity_tip);
        tip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                swip.post(new Runnable() {
                    @Override
                    public void run() {
                        swip.setRefreshing(true);
                        getData();
                    }
                });
            }
        });
        swip.setColorSchemeResources(R.color.main_color);
        swip.post(new Runnable() {
            @Override
            public void run() {
                if (isFirstIn) {
                    swip.setRefreshing(true);
                    getData();
                }
                isFirstIn = false;
            }
        });
    }

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(view.findViewById(R.id.activity_title)==null&&view.findViewById(R.id.activity_item_title)==null){
                return;
            }
            Intent intent = new Intent(mApplication.getInstance(), activity_Detail.class);
            String Id = "";
            if (position == 0) {
                Id = ((TextView) view.findViewById(R.id.activity_title)).getTag().toString();
            } else {
                Id = ((TextView) view.findViewById(R.id.activity_item_title)).getTag().toString();
            }
            if (!TextUtils.isEmpty(Id)) {
                intent.putExtra("id", Id);
                startActivity(intent);
            }
        }
    };



    /**
     * 退出页面时的设置
     */
    @Override
    public void onDestroy() {
        OkGo.getInstance().cancelTag(TAG);
        super.onDestroy();
    }

    /**
     * 从网络获取数据
     */
    private void getData() {
        if (!Network.HttpTest(mApplication.getInstance())) {
            Toast.makeText(mApplication.getInstance(), "网络连接失败，请下拉刷新", Toast.LENGTH_SHORT).show();
            if (null != swip && swip.isShown()) swip.setRefreshing(false);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject js=new JSONObject();
                    try {
                        js.put("page",page);
                        js.put("m_id",Constants.M_id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m=ApisSeUtil.i(js);
                    String data = OkGo.post(Constants.Activity_list_IP).tag(TAG)
                            .params("key",m.K())
                            .params("msg",m.M()).execute().body().string();
                    if (!TextUtils.isEmpty(data)) {
                        final List<HashMap<String, String>> list = AnalyticalJSON.getList(data, "activity");
                        listView.post(new Runnable() {
                            @Override
                            public void run() {
                                if (list != null) {//数据解析成功
                                    if (list.size() != 10) {
                                        endPage = page;
                                    }
                                    if (adpter == null) {//第一次加载
                                        adpter = new activity_adapter(mApplication.getInstance(), list);
                                        listView.setAdapter(adpter);
                                        if (swip.isRefreshing()) swip.setRefreshing(false);
                                        if (listView.isLoading) listView.onLoadComplete();
                                        tip.setVisibility(View.GONE);
                                    } else {
                                        if (isRefresh) {
                                            isRefresh = false;
                                            adpter.setList(list);
                                            adpter.notifyDataSetChanged();
                                            endPage = "";
                                            if (swip.isRefreshing()) swip.setRefreshing(false);
                                            if (t.getText().toString().equals("没有更多数据了")) {
                                                listView.onLoadComplete();
                                                t.setText("正在加载....");
                                                p.setVisibility(View.VISIBLE);
                                            }
                                        } else {
                                            adpter.list.addAll(list);
                                            if (endPage.equals(page)) {
                                                t.setText("没有更多数据了");
                                                p.setVisibility(View.GONE);
                                                return;
                                            } else {
                                                t.setText("正在加载....");
                                            }
                                        }
                                        listView.onLoadComplete();
                                        tip.setVisibility(View.GONE);
                                    }
                                } else {//数据解析失败
                                    if(Network.HttpTest(activity_fragment.this)){
                                        Glide.with(activity_fragment.this).load(R.drawable.load_nothing).override(DimenUtils.dip2px(activity_fragment.this,150),
                                                DimenUtils.dip2px(activity_fragment.this,150)).fitCenter().into(tip);
                                    }else{
                                        Glide.with(activity_fragment.this).load(R.drawable.load_nothing).override(DimenUtils.dip2px(activity_fragment.this,150),
                                                DimenUtils.dip2px(activity_fragment.this,150)).fitCenter().into(tip);
                                    }
                                    tip.setVisibility(View.VISIBLE);
                                }
                            }
                        });

                    }
                } catch (Exception e) {
                  runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tip.setVisibility(View.VISIBLE);
                            if(Network.HttpTest(activity_fragment.this)){
                                Glide.with(activity_fragment.this).load(R.drawable.load_nothing).override(DimenUtils.dip2px(activity_fragment.this,150),
                                        DimenUtils.dip2px(activity_fragment.this,150)).fitCenter().into(tip);
                            }else{
                                Glide.with(activity_fragment.this).load(R.drawable.load_nothing).override(DimenUtils.dip2px(activity_fragment.this,150),
                                        DimenUtils.dip2px(activity_fragment.this,150)).fitCenter().into(tip);
                            }
                        }
                    });
                    e.printStackTrace();
                } finally {
                    if (listView != null) {
                        listView.post(new Runnable() {
                            @Override
                            public void run() {
                                if (swip != null && swip.isRefreshing()) swip.setRefreshing(false);
                            }
                        });
                    }
                }
            }
        }).start();
    }

    /**
     * 上拉加载
     */
    @Override
    public void loadMore() {
        if (!endPage.equals(page)) {
            page = String.valueOf(Integer.parseInt(page) + 1);
        } else {
            p.setVisibility(View.GONE);
            t.setText("没有更多数据了");
            return;
        }
        getData();
    }

    /**
     * 下拉刷新
     */
    @Override
    public void onRefresh() {
        page = "1";
        isRefresh = true;
        endPage = "";
        getData();
    }
}
