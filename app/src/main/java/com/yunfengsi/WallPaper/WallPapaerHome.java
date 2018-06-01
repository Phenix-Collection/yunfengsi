package com.yunfengsi.WallPaper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.yunfengsi.R;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.LoginUtil;
import com.yunfengsi.Utils.Network;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.mApplication;
import com.yunfengsi.View.UsefulImageView;

import java.util.ArrayList;

/**
 * 作者：因陀罗网 on 2018/5/24 14:43
 * 公司：成都因陀罗网络科技有限公司
 */
public class WallPapaerHome extends AppCompatActivity implements View.OnClickListener {
    private TabLayout            tabLayout;
    private ImageView            back;
    private ViewPager            viewPager;
    private WallPaperPageAdapter adapter;
    private ArrayList<Fragment>  list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        mApplication.getInstance().addActivity(this);


        setContentView(R.layout.wall_page_home);
        tabLayout = findViewById(R.id.tab);
        viewPager = findViewById(R.id.viewpager);
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        list = new ArrayList<>();
        list.add(new RecommendFragment());
        list.add(new WallPagerClassification());

        adapter = new WallPaperPageAdapter(getSupportFragmentManager(), list);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager, true);

        if (!PreferenceUtil.getUserId(this).equals("")) {
            Glide.with(this).load(PreferenceUtil.getUserIncetance(this).getString("head_url", ""))
                    .asBitmap()
                    .override(DimenUtils.dip2px(this, 40), DimenUtils.dip2px(this, 40))
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            RoundedBitmapDrawable rbd = RoundedBitmapDrawableFactory.create(getResources(), resource);
                            rbd.setCircular(true);
                            ((ImageView) findViewById(R.id.userImage)).setImageDrawable(rbd);
                        }
                    });
        }

        findViewById(R.id.userImage).setOnClickListener(this);
        ((UsefulImageView) findViewById(R.id.userImage)).isCircle(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.userImage:
                if (new LoginUtil().checkLogin(this)) {
                    if(Network.HttpTest(WallPapaerHome.this)){
                        startActivity(new Intent(this, WallPaperUserHome.class));
                    }

                }
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mApplication.getInstance().romoveActivity(this);
    }

    private class WallPaperPageAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> list;

        public WallPaperPageAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
            super(fm);
            this.list = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return "推荐";
            } else if (position == 1) {
                return "分类";
            }
            return super.getPageTitle(position);
        }
    }


}
