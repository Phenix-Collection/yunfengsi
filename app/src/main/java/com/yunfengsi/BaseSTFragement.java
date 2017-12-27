package com.yunfengsi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2017/4/3.
 */

public abstract class BaseSTFragement extends Fragment {

    BroadcastReceiver BaseSTFragementreceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            resetData();
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        IntentFilter intentFilter = new IntentFilter("st");
        getActivity().registerReceiver(BaseSTFragementreceiver, intentFilter);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected abstract void resetData();

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(BaseSTFragementreceiver);

    }


}
