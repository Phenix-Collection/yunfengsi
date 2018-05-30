package com.yunfengsi.WallPager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.yunfengsi.R;
import com.yunfengsi.Utils.StatusBarCompat;

import java.util.ArrayList;

/**
 * 作者：因陀罗网 on 2018/5/29 16:44
 * 公司：成都因陀罗网络科技有限公司
 */
public class WallPaperUserHome extends AppCompatActivity implements View.OnClickListener {
    private TabLayout            tabLayout;
    private ViewPager            viewPager;
    private WallPaperPageAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.wall_paper_mine);
        tabLayout = findViewById(R.id.tab);
        viewPager = findViewById(R.id.viewpager);
        ArrayList<Fragment> fragments = new ArrayList<>();
        UserHomeFragment    mine      = new UserHomeFragment();
        Bundle              bundle    = new Bundle();
        bundle.putString("type", "1");
        mine.setArguments(bundle);
        UserHomeFragment verify  = new UserHomeFragment();
        Bundle           bundle1 = new Bundle();
        bundle.putString("type", "2");
        mine.setArguments(bundle1);
        UserHomeFragment collection = new UserHomeFragment();
        Bundle           bundle2    = new Bundle();
        bundle.putString("type", "3");
        mine.setArguments(bundle2);

        fragments.add(mine);
        fragments.add(verify);
        fragments.add(collection);
        adapter = new WallPaperPageAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager, true);


    }

    @Override
    protected void onStart() {
        super.onStart();
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.upload).setOnClickListener(this);

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.upload:
                // TODO: 2018/5/29 上传页面开启
                break;
        }
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
                return "我的壁纸";
            } else if (position == 1) {
                return "壁纸审核";
            } else if (position == 2) {
                return "我的收藏";
            }
            return super.getPageTitle(position);
        }
    }
}
