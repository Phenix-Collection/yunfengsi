package com.yunfengsi.Audio_BD.WakeUp;


import com.yunfengsi.Utils.LogUtil;

/**
 * Created by fujiayi on 2017/6/21.
 */

public class SimpleWakeupListener implements IWakeupListener {

    private static final String TAG = "SimpleWakeupListener";
    @Override
    public void onSuccess(String word, WakeUpResult result) {
        LogUtil.e("唤醒成功，唤醒词：" + word);
    }

    @Override
    public void onStop() {
        LogUtil.e( "唤醒词识别结束：");
    }

    @Override
    public void onError(int errorCode, String errorMessge, WakeUpResult result) {
        LogUtil.e("唤醒错误："+ errorCode +";错误消息："+errorMessge +"; 原始返回"+ result.getOrigalJson());
    }

    @Override
    public void onASrAudio(byte[] data, int offset, int length) {
        LogUtil.e("audio data： "+data.length);
    }

}
