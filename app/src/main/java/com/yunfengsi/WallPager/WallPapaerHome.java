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
import android.widget.ImageView;

import com.yunfengsi.R;
import com.yunfengsi.Utils.StatusBarCompat;

import java.util.ArrayList;

/**
 * 作者：因陀罗网 on 2018/5/24 14:43
 * 公司：成都因陀罗网络科技有限公司
 */
public class WallPapaerHome extends AppCompatActivity implements View.OnClickListener {
    private TabLayout tabLayout;
    private ImageView back;
    private ViewPager viewPager;
    private WallPaperPageAdapter adapter;
    private ArrayList<Fragment> list;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.wall_page_home);
        tabLayout=findViewById(R.id.tab);
        viewPager=findViewById(R.id.viewpager);
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        list=new ArrayList<>();
        list.add(new RecommendFragment());
        list.add(new WallPagerClassification());

        adapter=new WallPaperPageAdapter(getSupportFragmentManager(),list);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager,true);

        findViewById(R.id.userImage).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

    }


    private class WallPaperPageAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> list;
        public WallPaperPageAdapter(FragmentManager fm,ArrayList<Fragment> fragments) {
            super(fm);
            this.list=fragments;
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
            if(position==0){
                return "推荐";
            }else if(position==1){
                return "分类";
            }
            return super.getPageTitle(position);
        }
    }


}
