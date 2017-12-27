package com.qianfujiaoyu.Base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.lzy.okgo.OkGo;
import com.qianfujiaoyu.R;
import com.qianfujiaoyu.Utils.ProgressUtil;
import com.qianfujiaoyu.Utils.StatusBarCompat;

import org.greenrobot.eventbus.EventBus;

/**
 * 作者：因陀罗网 on 2017/5/17 14:45
 * 公司：成都因陀罗网络科技有限公司
 */

public abstract class BaseActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if(isMainColor()){
            StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        }
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initView();

        if(setEventBus()){
            EventBus.getDefault().register(this);
        }
        doThings();
    }
    public abstract int getLayoutId();
    public abstract void initView();
    public abstract boolean setEventBus() ;
    public abstract boolean isMainColor() ;
    public abstract void doThings();


    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(this);
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
//        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }
}
