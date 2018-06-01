package com.yunfengsi.WallPaper;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.yunfengsi.R;
import com.yunfengsi.Utils.StatusBarCompat;

import java.util.HashMap;

/**
 * 作者：因陀罗网 on 2018/5/30 18:07
 * 公司：成都因陀罗网络科技有限公司
 */
public class WallPaperClassfiedList extends AppCompatActivity {
    private Fragment fragment;
    private String   id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.wall_paper_classified_detail_list);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        HashMap<String, String> map = (HashMap<String, String>) getIntent().getSerializableExtra("map");
        id = map.get("id");
        ((TextView) findViewById(R.id.title)).setText(map.get("name"));


        RecommendFragment fragment = new RecommendFragment();
        Bundle            bundle   = new Bundle();
        bundle.putString("classfy",map.get("id"));
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment, fragment).commit();


    }
}
