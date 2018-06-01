package com.yunfengsi.Deamon;

import android.os.Bundle;
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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mApplication.getInstance().onePixelActivity=this;
        LogUtil.e("一像素打开"+mApplication.getMainInstance()+"    "+mApplication.getInstance());
        Window window=getWindow();
        window.setGravity(Gravity.LEFT|Gravity.TOP);
        window.setBackgroundDrawableResource(R.color.transparent);
        WindowManager.LayoutParams wl=window.getAttributes();
        wl.x=0;
        wl.y=0;
        wl.width=1;
        wl.height=1;
        window.setAttributes(wl);



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.e("一像素销毁");
    }
}
