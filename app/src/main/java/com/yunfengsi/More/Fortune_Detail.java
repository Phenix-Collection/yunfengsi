package com.yunfengsi.More;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunfengsi.R;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.mApplication;

import java.util.HashMap;

/**
 * 作者：因陀罗网 on 2018/1/30 18:12
 * 公司：成都因陀罗网络科技有限公司
 */

public class Fortune_Detail extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.activity_fortune_detail);

        ((ImageView) findViewById(R.id.title_back)).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.title_title)).setText(mApplication.ST("灵签详解"));
        ((ImageView) findViewById(R.id.title_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        HashMap<String,String > map= (HashMap<String, String>) getIntent().getSerializableExtra("map");
        LogUtil.e("map:::"+map);
        ((TextView) findViewById(R.id.num)).setText(mApplication.ST(map.get("num")));
        ((TextView) findViewById(R.id.title)).setText(mApplication.ST(map.get("title")));
        ((TextView) findViewById(R.id.poetry)).setText(mApplication.ST(map.get("poetry")));
        ((TextView) findViewById(R.id.translate)).setText(mApplication.ST(map.get("translate")));
        ((TextView) findViewById(R.id.draw)).setText(mApplication.ST(map.get("draw")));
        ((TextView) findViewById(R.id.content)).setText(mApplication.ST(map.get("content")));
        ((TextView) findViewById(R.id.story)).setText(mApplication.ST(map.get("story")));


    }
}
