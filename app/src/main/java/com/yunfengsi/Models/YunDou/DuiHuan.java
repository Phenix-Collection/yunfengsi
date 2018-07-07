package com.yunfengsi.Models.YunDou;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunfengsi.R;
import com.yunfengsi.Utils.ProgressUtil;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.mApplication;

public class DuiHuan extends AppCompatActivity implements DuiHuanContract.IView {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.activity_dui_huan);
        findViewById(R.id.title_back).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.title_title)).setText(mApplication.ST("兑换中心"));
        findViewById(R.id.title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ((TextView) findViewById(R.id.handle_right)).setText(mApplication.ST("我的福利"));
        findViewById(R.id.handle_right).setVisibility(View.VISIBLE);
        findViewById(R.id.handle_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DuiHuan.this,MyQuan.class));
            }
        });
        DuiHuanPresenterImpl duiHuanPresenter = new DuiHuanPresenterImpl(this);
        tabLayout=findViewById(R.id.tab);
        viewPager=findViewById(R.id.viewpager);

        duiHuanPresenter.getTitles();
    }




    @Override
    public FragmentManager getIFragmentManager() {
        return getSupportFragmentManager();
    }

    @Override
    public void showTabs(tabPagerAdapter adapter) {
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(adapter.getCount());
        if(adapter.getCount()>4){
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        }else{
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        }
        for(int i=0;i<adapter.getCount();i++){
            TabLayout .Tab tab=tabLayout.getTabAt(i);
            if(tab!=null){
                tab.setCustomView(adapter.getCustomView(i));
            }
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getCustomView().setEnabled(false);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getCustomView().setEnabled(true);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.setCurrentItem(0);
        tabLayout.getTabAt(0).getCustomView().setEnabled(false);
    }

    @Override
    public void onNetWorkBefore() {
        ProgressUtil.show(this,"","请稍等");
    }

    @Override
    public void onNetWorkAfter() {
        ProgressUtil.dismiss();
    }


}
