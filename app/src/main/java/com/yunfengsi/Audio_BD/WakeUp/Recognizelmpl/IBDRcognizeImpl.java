package com.yunfengsi.Audio_BD.WakeUp.Recognizelmpl;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/1/3.
 */

public class IBDRcognizeImpl implements EventListener {
    private Activity context;
    private EventManager asr;
    private EditText displayView;//需要显示语音识别结果的控件
    private ImageView keyView;//需要切换语音文字状态的开关控件
    private TextView audioButton;//长按录音的控件
    private int defaultTimeOut=0;
    public IBDRcognizeImpl(Activity context) {
        this.context = context;
        initPermission();
        asr = EventManagerFactory.create(context, "asr");
        asr.registerListener(this); //  EventListener 中 onEvent方法
    }
    public void setEventListener(EventListener eventListener){
        asr.unregisterListener(this);
        asr.registerListener(eventListener);
    }
    public void setTimeOut(int second){
        defaultTimeOut=second;
    }
    public void attachView(EditText displayView, TextView audioButton, ImageView keyView) {
        this.displayView = displayView;
        this.keyView = keyView;
        this.audioButton = audioButton;
    }

    @Override
    public void onEvent(String name, String params, byte[] data, int offset, int length) {
        switch (name) {
            case SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL://临时结果
//                if (params.contains("nlu_result")) {
//                    if (length > 0 && data.length > 0) {
//                        if (displayView != null) {
//                            displayView.append(new String(data, offset, length));
//                            LogUtil.e("语意解析成功：" + new String(data, offset, length));
//                        }
//                        return;
//                    }
//                }
                if(params.contains("final_result")){
                    try {
                        JSONObject js = new JSONObject(params);
                        if (displayView != null) {
                            displayView.append(js.getJSONArray("results_recognition").getString(0));
                        }

                        LogUtil.e(":识别结果：：：" + js.getJSONArray("results_recognition").getString(0));
                    } catch (JSONException e) {
                        LogUtil.e("json解析错误");
                        e.printStackTrace();
                    }
                }

                break;
            case SpeechConstant.CALLBACK_EVENT_ASR_FINISH://本次识别结束
                LogUtil.e("语音识别finish");
                if (displayView != null) {
                    displayView.setVisibility(View.VISIBLE);
                }
                if (audioButton != null) {
                    audioButton.setVisibility(View.GONE);
                }

                if (keyView != null) {
                    keyView.setSelected(!keyView.isSelected());
                }


                break;
            case SpeechConstant.CALLBACK_EVENT_ASR_READY://识别引擎就绪
                LogUtil.e("语音识别Ready");
                ToastUtil.showToastShort("请说话");
                break;
            case SpeechConstant.ASR_CANCEL://识别取消
                LogUtil.e("语音识别Cancel");
                break;

        }
    }

    /**
     * 测试参数填在这里
     */
    public void start() {
        cancel();

        Map<String, Object> params = new LinkedHashMap<String, Object>();
        String event = null;
        event = SpeechConstant.ASR_START; // 替换成测试的event

        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
        params.put(SpeechConstant.PID, 15361);
        params.put(SpeechConstant.NLU, "enable");
        params.put(SpeechConstant.VAD_ENDPOINT_TIMEOUT, defaultTimeOut);//0 为长语音
        // params.put(SpeechConstant.VAD, SpeechConstant.VAD_DNN);
        //  params.put(SpeechConstant.PROP ,20000);
        // 请先使用如‘在线识别’界面测试和生成识别参数。 params同ActivityRecog类中myRecognizer.start(params);
        String json = null; //可以替换成自己的json
        json = new JSONObject(params).toString(); // 这里可以替换成你需要测试的json
        asr.send(event, json, null, 0, 0);
        LogUtil.e("输入参数：" + json);
    }

    public void stop() {
        asr.send(SpeechConstant.ASR_STOP, null, null, 0, 0); //
    }

    public void cancel() {
        asr.send(SpeechConstant.ASR_CANCEL, null, null, 0, 0); //
    }

    public void release() {
        cancel();
        asr = null;
    }

    /**
     * android 6.0 以上需要动态申请权限
     */
    private void initPermission() {
        String permissions[] = {Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        ArrayList<String> toApplyList = new ArrayList<String>();

        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(context, perm)) {
                toApplyList.add(perm);
                //进入到这里代表没有权限.

            }
        }
        String tmpList[] = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(context, toApplyList.toArray(tmpList), 123);
        }

    }
}
