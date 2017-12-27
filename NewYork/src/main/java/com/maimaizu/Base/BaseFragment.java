package com.maimaizu.Base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Administrator on 2017/4/24.
 */

public abstract class BaseFragment extends Fragment {
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(getLayoutId(),container,false);
        initView(view);
        setOnClick();
        doThings();
        if(setEventBus()){
            EventBus.getDefault().register(this);
        }
        return view;

    }

    public abstract int getLayoutId();
    public abstract void initView(View view);
    public abstract void setOnClick();
    public abstract boolean setEventBus() ;
    public abstract void doThings();





    @Override
    public void onDestroy() {
        super.onDestroy();
        if(setEventBus()){
            EventBus.getDefault().unregister(this);
        }
    }
}
