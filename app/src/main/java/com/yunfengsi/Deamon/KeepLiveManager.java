package com.yunfengsi.Deamon;

import android.content.Intent;

import com.yunfengsi.Utils.mApplication;

/**
 * 作者：因陀罗网 on 2018/5/22 15:19
 * 公司：成都因陀罗网络科技有限公司
 */
public class KeepLiveManager {

    private static KeepLiveManager instance;


    public static KeepLiveManager getInstance() {
        if(instance==null){
            synchronized (KeepLiveManager.class){
                if(instance==null){
                    instance=new KeepLiveManager();
                }
            }
        }
        return instance;
    }


    public void    startKeepAliveActivity(){
        Intent intent=new Intent(mApplication.getInstance(),OnePixelActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mApplication.getInstance().startActivity(intent);
    }

}
