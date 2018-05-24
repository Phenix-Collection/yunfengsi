package com.yunfengsi.WallPager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.yunfengsi.R;

/**
 * 作者：因陀罗网 on 2018/5/24 14:43
 * 公司：成都因陀罗网络科技有限公司
 */
public class WallPapaerHome extends AppCompatActivity {
    private TabLayout tabLayout;
    private ImageView back;
    private ViewPager viewPager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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



    }




}
