package com.qianfujiaoyu.Model_Order;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.qianfujiaoyu.Activitys.Search;
import com.qianfujiaoyu.Adapter.myShuchengPagerAdapter;
import com.qianfujiaoyu.R;
import com.qianfujiaoyu.Utils.AnalyticalJSON;
import com.qianfujiaoyu.Utils.ApisSeUtil;
import com.qianfujiaoyu.Utils.Constants;
import com.qianfujiaoyu.Utils.ImageUtil;
import com.qianfujiaoyu.Utils.ProgressUtil;
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
public class Order_Tab extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ZiXun_Tab";
    private ArrayList<String> titles;
    private List<Fragment> list;
    private myShuchengPagerAdapter adpter;
    private TabLayout tab;
    private ViewPager pager;
    private ImageView tip;
    private FragmentManager fm;

    private int screenHeight, screenWidth;
    private ImageView search;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, ContextCompat.getColor(this, R.color.main_color));
        setContentView(R.layout.zixun_tab);
        search= (ImageView) findViewById(R.id.right1);
        search.setVisibility(View.VISIBLE);
        search.setOnClickListener(this);
        search.setImageBitmap(ImageUtil.readBitMap(this,R.drawable.search_good));
        getData();

    }



    /**
     * 获取数据
     */
    private void getData() {
        ProgressUtil.show(this, "", "正在加载");
        if (tab == null) {
            initView();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject js=new JSONObject();
                    try {
                        js.put("m_id",Constants.M_id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m=ApisSeUtil.i(js);
                    final String data = OkGo.post(Constants.Order_type)
                            .cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)
                            .params("key",m.K())
                            .params("msg",m.M()).execute().body().string();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (!TextUtils.isEmpty(data)) {
                                    Log.e(TAG, "run: data_______>" + data);
                                    List<HashMap<String, String>> list1 = AnalyticalJSON.getList_zj(data);
                                    if (list1 != null&&titles.size()==0) {
                                        ProgressUtil.dismiss();
                                        titles.add("所有");
                                        list.add(new Order_item_fragment());
                                        titles.add("课程");
                                        Order_item_fragment f = new Order_item_fragment();
                                        Bundle b1= new Bundle();
                                        b1.putString("type", "课程");
                                        f.setArguments(b1);
                                        list.add(f);
                                        for (HashMap<String, String> map : list1) {
                                            titles.add(map.get("name"));
                                            Bundle b = new Bundle();
                                            b.putString("type", map.get("id"));
                                            Order_item_fragment fragment = new Order_item_fragment();
                                            fragment.setArguments(b);
                                            list.add(fragment);///添加fragment
                                        }
                                        if (list.size() > 4) {
                                            tab.setTabMode(TabLayout.MODE_SCROLLABLE);
                                        } else {
                                            tab.setTabMode(TabLayout.MODE_FIXED);
                                        }
                                        tip.setVisibility(View.GONE);
                                        adpter = new myShuchengPagerAdapter(mApplication.getInstance(), fm, list, titles);
                                        pager.setAdapter(adpter);
                                        pager.setOffscreenPageLimit(list.size());
                                        tab.setupWithViewPager(pager);
                                    } else {
                                        ProgressUtil.dismiss();
                                        tip.setVisibility(View.VISIBLE);
                                        findViewById(R.id.line).setVisibility(View.GONE);
                                    }

                                } else {
                                    ProgressUtil.dismiss();
                                    tip.setVisibility(View.VISIBLE);
                                    findViewById(R.id.line).setVisibility(View.GONE);
                                }
                            }
                        });

                } catch (Exception e) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ProgressUtil.dismiss();
                                tip.setVisibility(View.VISIBLE);
                                findViewById(R.id.line).setVisibility(View.GONE);
                            }
                        });

                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 初始化控件
     *
     *
     */
    private void initView() {

        screenHeight = getResources().getDisplayMetrics().heightPixels;
        screenWidth = getResources().getDisplayMetrics().widthPixels;

        fm = getSupportFragmentManager();
        tip = (ImageView)findViewById(R.id.reload);
        tip.setImageBitmap(ImageUtil.readBitMap(this,R.drawable.load_nothing));
        tip.setOnClickListener(this);
        tab = (TabLayout) findViewById(R.id.zixun_tab_tablayout);
        pager = (ViewPager)findViewById(R.id.zixun_tab_viewPager);
        titles = new ArrayList<>();
        list = new ArrayList<>();
        findViewById(R.id.line).setVisibility(View.VISIBLE);
        findViewById(R.id.back).setVisibility(View.VISIBLE);
        ((ImageView) findViewById(R.id.back)).setImageBitmap(ImageUtil.readBitMap(this,R.drawable.back));
        ((TextView) findViewById(R.id.title)).setText("商城");


    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reload:
                getData();
                tip.setVisibility(View.GONE);
                break;
            case R.id.back:
                finish();
                break;
            case R.id.right1:
                Intent intent=new Intent(this, Search_Good.class);
                startActivity(intent);
                break;



        }
    }


}
