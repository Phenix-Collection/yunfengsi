package com.yunfengsi.Audio_BD.WakeUp;


import android.content.Context;
import android.text.TextUtils;

import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.yunfengsi.Audio_BD.WakeUp.TTS.InitConfig;
import com.yunfengsi.Audio_BD.WakeUp.TTS.NonBlockSyntherizer;
import com.yunfengsi.Audio_BD.WakeUp.TTS.OfflineResource;
import com.yunfengsi.Utils.LogUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fujiayi on 2017/6/21.
 */

public class SimpleWakeupListener implements IWakeupListener {
    private InitConfig initConfig;//语音合成参数
    // TtsMode.MIX; 离在线融合，在线优先； TtsMode.ONLINE 纯在线； 没有纯离线
    protected TtsMode ttsMode = TtsMode.MIX;

    // 离线发音选择，VOICE_FEMALE即为离线女声发音。
    // assets目录下bd_etts_common_speech_m15_mand_eng_high_am-mix_v3.0.0_20170505.dat为离线男声模型；
    // assets目录下bd_etts_common_speech_f7_mand_eng_high_am-mix_v3.0.0_20170512.dat为离线女声模型
    protected String offlineVoice = OfflineResource.VOICE_DUXY;
    private Context context;
    private NonBlockSyntherizer syntherizer;

    private String text="这里是中央广播电台,欢迎收看新闻联播";
    public SimpleWakeupListener(Context context) {
        super();
        this.context=context;
        initConfig=new InitConfig("10558348","tBBGe3A1UVuX5zZI96LFjwU0"
        ,"cFOyGpcCy3TMvcfinvDIOclAGXNakQx3"
        ,ttsMode,getParams(),speechSynthesizer);
        syntherizer=new NonBlockSyntherizer(context,initConfig);
    }

    private static final String TAG = "SimpleWakeupListener";
    @Override
    public void onSuccess(String word, WakeUpResult result) {
        LogUtil.e("唤醒成功，唤醒词：" + word+"    语音回复：："+text);
        if(syntherizer!=null){
            syntherizer.speak(text);
        }
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


    //语音合成回调
    private SpeechSynthesizerListener speechSynthesizer=new SpeechSynthesizerListener() {
        @Override
        public void onSynthesizeStart(String s) {
            LogUtil.e("语音开始合成");
        }

        @Override
        public void onSynthesizeDataArrived(String s, byte[] bytes, int i) {
            LogUtil.e("语音合成数据返回");
        }

        @Override
        public void onSynthesizeFinish(String s) {
            LogUtil.e("语音合成完成");
        }

        @Override
        public void onSpeechStart(String s) {
            LogUtil.e("开始播放语音");
        }

        @Override
        public void onSpeechProgressChanged(String s, int i) {

        }

        @Override
        public void onSpeechFinish(String s) {
            LogUtil.e("语音播放完成");
        }

        @Override
        public void onError(String s, SpeechError speechError) {
            LogUtil.e("错误"+speechError.description);
        }
    };





    /**
     * 合成的参数，可以初始化时填写，也可以在合成前设置。
     *
     * @return
     */
    protected Map<String, String> getParams() {
        Map<String, String> params = new HashMap<String, String>();
        // 以下参数均为选填
        // 设置在线发声音人： 0 普通女声（默认） 1 普通男声 2 特别男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
        params.put(SpeechSynthesizer.PARAM_SPEAKER, "0");
        // 设置合成的音量，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_VOLUME, "5");
        // 设置合成的语速，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_SPEED, "5");
        // 设置合成的语调，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_PITCH, "5");

        params.put(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_HIGH_SPEED_SYNTHESIZE);
        // 该参数设置为TtsMode.MIX生效。即纯在线模式不生效。
        // MIX_MODE_DEFAULT 默认 ，wifi状态下使用在线，非wifi离线。在线状态下，请求超时6s自动转离线
        // MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI wifi状态下使用在线，非wifi离线。在线状态下， 请求超时1.2s自动转离线
        // MIX_MODE_HIGH_SPEED_NETWORK ， 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
        // MIX_MODE_HIGH_SPEED_SYNTHESIZE, 2G 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线

        // 离线资源文件
        OfflineResource offlineResource = createOfflineResource(offlineVoice);
        // 声学模型文件路径 (离线引擎使用), 请确认下面两个文件存在
        params.put(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, offlineResource.getTextFilename());
        params.put(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE,
                offlineResource.getModelFilename());
        return params;
    }


    protected OfflineResource createOfflineResource(String voiceType) {
        OfflineResource offlineResource = null;
        try {
            offlineResource = new OfflineResource(context, voiceType);
        } catch (IOException e) {
            // IO 错误自行处理
            e.printStackTrace();
            LogUtil.e("【error】:copy files from assets failed." + e.getMessage());
        }
        return offlineResource;
    }

    /**
     * speak 实际上是调用 synthesize后，获取音频流，然后播放。
     * 获取音频流的方式见SaveFileActivity及FileSaveListener
     * 需要合成的文本text的长度不能超过1024个GBK字节。
     */
    private void speak() {


        // 需要合成的文本text的长度不能超过1024个GBK字节。
        if (TextUtils.isEmpty(text)) {
            text = "我没听懂你的意思";
        }
        // 合成前可以修改参数：
        // Map<String, String> params = getParams();
        // synthesizer.setParams(params);
        int result = syntherizer.speak(text);
        checkResult(result, "speak");
    }


    /**
     * 合成但是不播放，
     * 音频流保存为文件的方法可以参见SaveFileActivity及FileSaveListener
     */
    private void synthesize() {

        // 需要合成的文本text的长度不能超过1024个GBK字节。
        if (TextUtils.isEmpty(text)) {
            text = "我没听懂你的意思";
        }
        int result = syntherizer.synthesize(text);
        checkResult(result, "synthesize");
    }

//    /**
//     * 批量播放
//     */
//    private void batchSpeak() {
//        mShowText.setText("");
//        List<Pair<String, String>> texts = new ArrayList<Pair<String, String>>();
//        texts.add(new Pair<String, String>("开始批量播放，", "a0"));
//        texts.add(new Pair<String, String>("123456，", "a1"));
//        texts.add(new Pair<String, String>("欢迎使用百度语音，，，", "a2"));
//        texts.add(new Pair<String, String>("重(chong2)量这个是多音字示例", "a3"));
//        int result = synthesizer.batchSpeak(texts);
//        checkResult(result, "batchSpeak");
//    }


    /**
     * 切换离线发音。注意需要添加额外的判断：引擎在合成时该方法不能调用
     */
    private void loadModel(String mode) {
        offlineVoice = mode;
        OfflineResource offlineResource = createOfflineResource(offlineVoice);
        LogUtil.e("切换离线语音：" + offlineResource.getModelFilename());
        int result = syntherizer.loadModel(offlineResource.getModelFilename(), offlineResource.getTextFilename());
        checkResult(result, "loadModel");
    }

    private void checkResult(int result, String method) {
        if (result != 0) {
            LogUtil.e("error code :" + result + " method:" + method + ", 错误码文档:http://yuyin.baidu.com/docs/tts/122 ");
        }
    }


    /**
     * 暂停播放。仅调用speak后生效
     */
    private void pause() {
        int result = syntherizer.pause();
        checkResult(result, "pause");
    }

    /**
     * 继续播放。仅调用speak后生效，调用pause生效
     */
    private void resume() {
        int result = syntherizer.resume();
        checkResult(result, "resume");
    }

    /*
     * 停止合成引擎。即停止播放，合成，清空内部合成队列。
     */
    private void stop() {
        int result = syntherizer.stop();
        checkResult(result, "stop");
    }


}
