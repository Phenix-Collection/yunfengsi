package com.maimaizu.Mine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.maimaizu.R;
import com.maimaizu.Utils.ImageUtil;
import com.maimaizu.Utils.StatusBarCompat;
import com.maimaizu.Utils.mApplication;


/**
 * Created by Administrator on 2016/10/25.
 */
public class BangZhu extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.mine_bangzhu);
        ((ImageView) findViewById(R.id.logo)).setImageBitmap(ImageUtil.readBitMap(this,R.drawable.indra));
        ((TextView) findViewById(R.id.content)).setText(mApplication.ST("    【纽约房买卖租】是一款满足华人用户在纽约买/卖/租房需求的找房平台！\n" +
                "在纽约拥有上万华人用户的同时，拥有着纽约最大的房产资源信息库，其中包含已上市和每天最新上市以及还未上市的海量房产资讯供您挑选。"));
        findViewById(R.id.bangzhu_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
