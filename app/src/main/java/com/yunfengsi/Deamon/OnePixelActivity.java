package com.yunfengsi.Deamon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.yunfengsi.R;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.mApplication;

/**
 * 作者：因陀罗网 on 2018/5/22 15:00
 * 公司：成都因陀罗网络科技有限公司
 */
public class OnePixelActivity extends AppCompatActivity {

    KeepLiveWakeReceiver receiver1;//开锁屏监听

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mApplication.getInstance().onePixelActivity = this;
        LogUtil.e("一像素打开" + mApplication.getMainInstance() + "    " + mApplication.getInstance());
        Window window = getWindow();
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        window.setBackgroundDrawableResource(R.color.transparent);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.dimAmount = 0;
        wl.x = 0;
        wl.y = 0;
        wl.width = 1;
        wl.height = 1;
        window.setAttributes(wl);
        //注册屏幕亮、灭监听
        receiver1 = new KeepLiveWakeReceiver();
        IntentFilter intentFilter1 = new IntentFilter();
        intentFilter1.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(receiver1, intentFilter1);
        checkScreen();
    }

    public class KeepLiveWakeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogUtil.e(" KeepLiveWakeLockReceiver————>onReceive  ,action--->" + action);
            if (action.equals(Intent.ACTION_SCREEN_ON)) {//屏幕亮起
                LogUtil.e("屏幕亮起");
                finish();
                System.exit(0);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver1);
        LogUtil.e("一像素销毁");
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkScreen();
    }

    /**
     * 检查屏幕状态  isScreenOn为true  屏幕“亮”结束该Activity
     */
    private void checkScreen() {

        PowerManager pm         = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        boolean      isScreenOn = pm.isScreenOn();
        if (isScreenOn) {
            finish();
        }
    }

}
