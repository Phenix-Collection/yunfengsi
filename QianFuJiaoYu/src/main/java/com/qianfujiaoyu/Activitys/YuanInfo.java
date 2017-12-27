package com.qianfujiaoyu.Activitys;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qianfujiaoyu.Adapter.myShuchengPagerAdapter;
import com.qianfujiaoyu.Base.BaseActivity;
import com.qianfujiaoyu.Fragments.GongYangActivity;
import com.qianfujiaoyu.Fragments.InfoWeb;
import com.qianfujiaoyu.Fragments.ZiXun;
import com.qianfujiaoyu.R;
import com.qianfujiaoyu.Utils.ImageUtil;
import com.qianfujiaoyu.Utils.mApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：因陀罗网 on 2017/5/18 10:36
 * 公司：成都因陀罗网络科技有限公司
 */

public class YuanInfo extends BaseActivity implements View.OnClickListener{

    private ArrayList<String> titles;
    private List<Fragment> list;
    private myShuchengPagerAdapter adpter;
    private TabLayout tab;
    private ViewPager pager;
    private FragmentManager fm;
    @Override
    public int getLayoutId() {
        return R.layout.yuan_info;
    }

    @Override
    public void initView() {
        fm = getSupportFragmentManager();
        tab = (TabLayout) findViewById(R.id.zixun_tab_tablayout);
        pager = (ViewPager)findViewById(R.id.zixun_tab_viewPager);
        titles = new ArrayList<>();
        list = new ArrayList<>();
        findViewById(R.id.line).setVisibility(View.VISIBLE);
        findViewById(R.id.back).setVisibility(View.VISIBLE);
        ((ImageView) findViewById(R.id.back)).setImageBitmap(ImageUtil.readBitMap(this,R.drawable.back));
        ((TextView) findViewById(R.id.title)).setText("园公告");
        list.add(new ZiXun());
        list.add(new InfoWeb());
        list.add(new GongYangActivity());
        titles.add("园公告");
        titles.add("园介绍");
        titles.add("教师团队");
        adpter = new myShuchengPagerAdapter(mApplication.getInstance(), fm, list, titles);
        pager.setAdapter(adpter);
        pager.setOffscreenPageLimit(list.size());
        tab.setupWithViewPager(pager);
    }

    @Override
    public boolean setEventBus() {
        return false;
    }

    @Override
    public boolean isMainColor() {
        return true;
    }

    @Override
    public void doThings() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;



        }
    }
}
