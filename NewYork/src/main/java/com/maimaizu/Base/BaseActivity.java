package com.maimaizu.Base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.maimaizu.Utils.ProgressUtil;
import com.umeng.socialize.UMShareAPI;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Administrator on 2017/4/24.
 */

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initView();
        setOnClick();
        if(setEventBus()){
            EventBus.getDefault().register(this);
        }
        doThings();
    }
    public abstract int getLayoutId();
    public abstract void initView();
    public abstract void setOnClick();
    public abstract boolean setEventBus() ;
    public abstract void doThings();


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(setEventBus()){
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        ProgressUtil.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }
}
