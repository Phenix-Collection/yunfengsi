package com.yunfengsi.Setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yunfengsi.R;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.mApplication;

import java.io.IOException;

import taobe.tec.jcc.JChineseConvertor;

/**
 * Created by Administrator on 2017/4/3.
 */

public class ST extends AppCompatActivity implements View.OnClickListener{
    private ImageView img1,img2;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.st);
        img1= (ImageView) findViewById(R.id.img1);
        img2= (ImageView) findViewById(R.id.img2);
        RelativeLayout layout1 = (RelativeLayout) findViewById(R.id.layout1);
        RelativeLayout layout2 = (RelativeLayout) findViewById(R.id.layout2);
        if(mApplication.isChina){
            img1.setVisibility(View.VISIBLE);
        }else{
            img2.setVisibility(View.VISIBLE);
        }
        ((TextView) findViewById(R.id.title)).setText(mApplication.ST("多语言"));
        layout1.setOnClickListener(this);
        layout2.setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.layout1:
                img1.setVisibility(View.VISIBLE);
                img2.setVisibility(View.GONE);
                PreferenceUtil.setChina(this,true);
                try {
                    ((TextView) findViewById(R.id.title)).setText(JChineseConvertor.getInstance().t2s("多语言"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mApplication.isChina=true;
                Intent intent=new Intent("st");
                sendBroadcast(intent);
                break;
            case R.id.layout2:
                img2.setVisibility(View.VISIBLE);
                img1.setVisibility(View.GONE);
                PreferenceUtil.setChina(this,false);
                mApplication.isChina=false;
                try {
                    ((TextView) findViewById(R.id.title)).setText(JChineseConvertor.getInstance().s2t("多语言"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Intent intent1=new Intent("st");
                sendBroadcast(intent1);
                break;
        }
    }
}
