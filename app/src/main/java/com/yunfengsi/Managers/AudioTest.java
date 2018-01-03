package com.yunfengsi.Managers;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.yunfengsi.R;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.ToastUtil;
import com.yunfengsi.View.DiffuseView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 作者：因陀罗网 on 2018/1/3 10:50
 * 公司：成都因陀罗网络科技有限公司
 */

public class AudioTest extends AppCompatActivity implements EventListener{
    private DiffuseView diffuseView;
    private TextView logText;
    private EventManager asr;
    private TextView result;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_test);
        logText= (TextView) findViewById(R.id.log);
        result= (TextView) findViewById(R.id.result);
        initButton();
        initPermission();

        asr = EventManagerFactory.create(this, "asr");
        asr.registerListener(this); //  EventListener 中 onEvent方法
    }

    private void initButton() {
        diffuseView = (DiffuseView) findViewById(R.id.audio_button);
        Glide.with(this).load(R.drawable.auido_white)
                .asBitmap().override(DimenUtils.dip2px(this, 40), DimenUtils.dip2px(this, 40))
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        diffuseView.setCoreImage(resource);
                        diffuseView.setCoreRadius(DimenUtils.dip2px(AudioTest.this, 40));
                    }
                });
        diffuseView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ToastUtil.showToastShort("按下");
                        diffuseView.start();
                        start();
                        break;
                    case MotionEvent.ACTION_UP:
                        ToastUtil.showToastShort("抬起");
                        diffuseView.stop();
                        stop();
                        break;

                }
                return true;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        asr.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0);

    }
    /**
     * 测试参数填在这里
     */
    private void start() {
        cancel();
        logText.setText("");
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        String event = null;
        event = SpeechConstant.ASR_START; // 替换成测试的event

        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
          params.put(SpeechConstant.NLU, "enable");
         params.put(SpeechConstant.VAD_ENDPOINT_TIMEOUT, 0);
        // params.put(SpeechConstant.VAD, SpeechConstant.VAD_DNN);
        //  params.put(SpeechConstant.PROP ,20000);
        // 请先使用如‘在线识别’界面测试和生成识别参数。 params同ActivityRecog类中myRecognizer.start(params);
        String json = null; //可以替换成自己的json
        json = new JSONObject(params).toString(); // 这里可以替换成你需要测试的json
        asr.send(event, json, null, 0, 0);
        printLog("输入参数：" + json);
    }
    private void printLog(String text) {

            text += "  ;time=" + System.currentTimeMillis();

        text += "\n";
        Log.i(getClass().getName(), text);
        logText.append(text + "\n");
    }

    private void stop() {
        asr.send(SpeechConstant.ASR_STOP, null, null, 0, 0); //
    }
    private void cancel() {
        asr.send(SpeechConstant.ASR_CANCEL, null, null, 0, 0); //
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

        for (String perm :permissions){
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
                //进入到这里代表没有权限.

            }
        }
        String tmpList[] = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()){
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // 此处为android 6.0以上动态授权的回调，用户自行实现。
    }

    @Override
    public void onEvent(String name, String params, byte[] data, int offset, int length) {
        String logTxt = "name: " + name;
        switch (name){
            case SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL://临时结果
                break;
            case SpeechConstant.CALLBACK_EVENT_ASR_FINISH://本次识别结束
                break;
            case SpeechConstant.CALLBACK_EVENT_ASR_READY://识别引擎就绪
                break;
            case SpeechConstant.ASR_CANCEL://识别取消
                break;

        }

        if (params != null && !params.isEmpty()) {
            logTxt += " ;params :" + params;
        }
        if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL)) {
            if (params.contains("\"nlu_result\"")) {
                if (length > 0 && data.length > 0) {
                    logTxt += ", 语义解析结果：" + new String(data, offset, length);
                }
            }
            try {
                JSONObject js=new JSONObject(params);
                result.setText(js.getJSONArray("results_recognition").getString(0));
                LogUtil.e(":识别结果：：："+js.getJSONArray("results_recognition").getString(0));
            } catch (JSONException e) {
                LogUtil.e("json解析错误" );
                e.printStackTrace();
            }

        } else if (data != null) {
            logTxt += " ;data length=" + data.length;
        }

        printLog(logTxt);
    }
}
