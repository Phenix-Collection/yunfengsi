package com.yunfengsi.Deamon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.yunfengsi.Utils.LogUtil;

/**
 * 作者：因陀罗网 on 2018/5/22 15:34
 * 公司：成都因陀罗网络科技有限公司
 */
public class KeepLiveWakeLockReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action=intent.getAction();
        LogUtil.e(" KeepLiveWakeLockReceiver————>onReceive  ,action--->"+action);
        if(action.equals(Intent.ACTION_SCREEN_OFF)){//锁屏
            LogUtil.e("锁屏");
            KeepLiveManager.getInstance().startKeepAliveActivity();
        }else if(action.equals(Intent.ACTION_SCREEN_ON)){//屏幕亮起
//            LogUtil.e("屏幕亮起");
//            KeepLiveManager.getInstance().finishKeepAliveActivity();
        }
    }
}
