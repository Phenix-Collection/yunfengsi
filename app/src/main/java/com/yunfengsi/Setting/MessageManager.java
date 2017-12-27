package com.yunfengsi.Setting;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunfengsi.Adapter.MessagePagerAdapter;
import com.yunfengsi.Fragment.TongzhiFragment;
import com.yunfengsi.Fragment.pinglun_fragment;
import com.yunfengsi.R;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.mApplication;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/4/17.
 */

public class MessageManager extends AppCompatActivity implements View.OnClickListener{
    private ImageView back;
    private TabLayout tab;
    private ViewPager viewpager;
    private MessagePagerAdapter adapter;
    private ArrayList<Fragment> list;
    private TextView tongzhi,pinglun;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.my_pinglun_activity);

        initView();
    }

    private void initView() {

        back = (ImageView) findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        tab= (TabLayout) findViewById(R.id.tab);
        viewpager= (ViewPager) findViewById(R.id.viewpager);
        list=new ArrayList<>();
        pinglun_fragment f2=new pinglun_fragment();
        TongzhiFragment f1=new TongzhiFragment();
        list.add(f1);
        list.add(f2);
        adapter=new MessagePagerAdapter(getSupportFragmentManager(),list);

        viewpager.setAdapter(adapter);
        tab.setupWithViewPager(viewpager);
        tongzhi= (TextView) findViewById(R.id.tongzhi);
        pinglun= (TextView) findViewById(R.id.pinglun);
        tongzhi.setOnClickListener(this);
        pinglun.setOnClickListener(this);
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        tongzhi.performClick();
                        break;
                    case 1:
                        pinglun.performClick();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tongzhi.setText(mApplication.ST("通知中心"));
        pinglun.setText(mApplication.ST("评论回复"));
        tongzhi.performClick();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.tongzhi:
                tongzhi.setSelected(true);
                pinglun.setSelected(false);
                tongzhi.setTextColor(ContextCompat.getColor(this,R.color.main_color));
                pinglun.setTextColor(Color.WHITE);
                viewpager.setCurrentItem(0);
                break;
            case R.id.pinglun:
                tongzhi.setSelected(false);
                pinglun.setSelected(true);
                tongzhi.setTextColor(Color.WHITE);
                pinglun.setTextColor(ContextCompat.getColor(this,R.color.main_color));
                viewpager.setCurrentItem(1);
                break;
        }
    }
}
