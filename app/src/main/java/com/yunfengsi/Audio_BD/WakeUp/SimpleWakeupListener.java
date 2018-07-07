package com.yunfengsi.Audio_BD.WakeUp;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.baidu.speech.EventListener;
import com.baidu.speech.asr.SpeechConstant;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.yunfengsi.Audio_BD.WakeUp.Recognizelmpl.IBDRcognizeImpl;
import com.yunfengsi.MainActivity;
import com.yunfengsi.Managers.MessageCenter;
import com.yunfengsi.Models.BlessTree.BlessTree;
import com.yunfengsi.Models.E_Book.BookList;
import com.yunfengsi.Models.Model_activity.Mine_activity_list;
import com.yunfengsi.Models.More.Fortune;
import com.yunfengsi.Models.More.Meditation;
import com.yunfengsi.Models.NianFo.NianFo;
import com.yunfengsi.Models.NianFo.nianfo_home_tab1;
import com.yunfengsi.Models.NianFo.nianfo_home_tab2;
import com.yunfengsi.Models.NianFo.nianfo_home_tab3;
import com.yunfengsi.Models.NianFo.nianfo_home_tab4;
import com.yunfengsi.Models.NianFo.nianfo_home_tab5;
import com.yunfengsi.Models.NianFo.nianfo_home_tab6;
import com.yunfengsi.Models.TouGao.TouGao;
import com.yunfengsi.Models.WallPaper.WallPapaerHome;
import com.yunfengsi.Models.WallPaper.WallPaperUpload;
import com.yunfengsi.Models.WallPaper.WallPaperUserHome;
import com.yunfengsi.Models.YunDou.DuiHuan;
import com.yunfengsi.Models.YunDou.MyQuan;
import com.yunfengsi.Models.YunDou.YunDouHome;
import com.yunfengsi.Models.YunDou.yundou_paihang;
import com.yunfengsi.R;
import com.yunfengsi.Setting.Activity_ShouCang;
import com.yunfengsi.Setting.GanyuActivity;
import com.yunfengsi.Setting.Mine_HuiYuan;
import com.yunfengsi.Setting.Month_Detail;
import com.yunfengsi.Setting.Search;
import com.yunfengsi.Setting.Setting;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.JsUtil;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.LoginUtil;
import com.yunfengsi.Utils.ToastUtil;
import com.yunfengsi.Utils.WakeLockUtil;
import com.yunfengsi.Utils.mApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by fujiayi on 2017/6/21.
 */

public class SimpleWakeupListener implements IWakeupListener, EventListener {
//    private InitConfig initConfig;//语音合成参数
    // TtsMode.MIX; 离在线融合，在线优先； TtsMode.ONLINE 纯在线； 没有纯离线
//    protected TtsMode ttsMode = TtsMode.MIX;

    // 离线发音选择，VOICE_FEMALE即为离线女声发音。
    // assets目录下bd_etts_common_speech_m15_mand_eng_high_am-mix_v3.0.0_20170505.dat为离线男声模型；
    // assets目录下bd_etts_common_speech_f7_mand_eng_high_am-mix_v3.0.0_20170512.dat为离线女声模型
//    protected String offlineVoice = OfflineResource.VOICE_DUXY;
    private Activity        context;
    //    public NonBlockSyntherizer syntherizer;
    private IBDRcognizeImpl ibdRcognize;
    private static final String  DefaultAnswer = "你好";
    private static final String  OK            = "好的";
    private static final String  OhNo          = "不好意思，师父没听懂你的意思";
    private              String  text          = DefaultAnswer;
    private              boolean isRecognize   = false;

    private SoundPool                soundPool;
    private MediaPlayer              mediaPlayer;
    private HashMap<String, Integer> soundMaps;
    private android.os.Handler handler = new android.os.Handler();

    public SimpleWakeupListener(Activity context) {
        super();
        this.context = context;
//        initConfig=new InitConfig("10558348","tBBGe3A1UVuX5zZI96LFjwU0"
//        ,"cFOyGpcCy3TMvcfinvDIOclAGXNakQx3"
//        ,ttsMode,getParams(),speechSynthesizer);
//        syntherizer=new NonBlockSyntherizer(context,initConfig);
        initPermission();
        ibdRcognize = new IBDRcognizeImpl(context);
        ibdRcognize.setEventListener(this);
        ibdRcognize.setTimeOut(3);
//        soundPool=new SoundPool(3, AudioManager.STREAM_MUSIC,0);
//        soundMaps=new HashMap<>();
//        soundMaps.put(DefaultAnswer,soundPool.load(context, R.raw.amituofo,1));
//        soundMaps.put(OhNo,soundPool.load(context, R.raw.ohno,1));
//        soundMaps.put(OK,soundPool.load(context, R.raw.ok,1));


    }

    private void openRecognize() {
        ibdRcognize.start();
        isRecognize = true;
    }


    private static final String TAG = "SimpleWakeupListener";

    @Override
    public void onSuccess(String word, WakeUpResult result) {
        boolean isWakeUp = true;
        text = DefaultAnswer;
        LogUtil.e("唤醒成功，唤醒词：" + word + "    语音回复：：" + text);
        ibdRcognize.cancel();
//        soundPool.play(soundMaps.get(text),1.0f,1.0f,10,0,1.0f);
//        if(!isRecognize){
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    openRecognize();
//                }
//            },1000);
//        }
        if (mediaPlayer != null) {

            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();

        }
        mediaPlayer = MediaPlayer.create(context, R.raw.amituofo);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                openRecognize();
            }
        });


    }

    @Override
    public void onStop() {
        LogUtil.e("唤醒词识别结束：");
    }

    @Override
    public void onError(int errorCode, String errorMessge, WakeUpResult result) {
        LogUtil.e("唤醒错误：" + errorCode + ";错误消息：" + errorMessge + "; 原始返回" + result.getOrigalJson());
    }

    @Override
    public void onASrAudio(byte[] data, int offset, int length) {
        LogUtil.e("audio data： " + data.length);
    }


    //语音合成回调
//    private SpeechSynthesizerListener speechSynthesizer=new SpeechSynthesizerListener() {
//        @Override
//        public void onSynthesizeStart(String s) {
//            LogUtil.e("语音开始合成,合成：："+s);
//        }
//
//        @Override
//        public void onSynthesizeDataArrived(String s, byte[] bytes, int i) {
//        }
//
//        @Override
//        public void onSynthesizeFinish(String s) {
//            LogUtil.e("语音合成完成");
//        }
//
//        @Override
//        public void onSpeechStart(String s) {
//            LogUtil.e("开始播放语音");
//        }
//
//        @Override
//        public void onSpeechProgressChanged(String s, int i) {
//
//        }
//
//        @Override
//        public void onSpeechFinish(String s) {
//            LogUtil.e("语音播放完成");
//            if(!text.equals(OK)){
//                LogUtil.e("onSpeechFinish    开启语音识别");
//                openRecognize();
//            }
//
//        }
//
//        @Override
//        public void onError(String s, SpeechError speechError) {
//            LogUtil.e("错误"+speechError.description);
//        }
//    };


    /**
     * 合成的参数，可以初始化时填写，也可以在合成前设置。
     *
     * @return
     */
//    protected Map<String, String> getParams() {
//        Map<String, String> params = new HashMap<String, String>();
//        // 以下参数均为选填
//        // 设置在线发声音人： 0 普通女声（默认） 1 普通男声 2 特别男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
//        params.put(SpeechSynthesizer.PARAM_SPEAKER, "3");
//        // 设置合成的音量，0-9 ，默认 5
//        params.put(SpeechSynthesizer.PARAM_VOLUME, "5");
//        // 设置合成的语速，0-9 ，默认 5
//        params.put(SpeechSynthesizer.PARAM_SPEED, "5");
//        // 设置合成的语调，0-9 ，默认 5
//        params.put(SpeechSynthesizer.PARAM_PITCH, "5");
//
//        params.put(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_HIGH_SPEED_SYNTHESIZE);
//        // 该参数设置为TtsMode.MIX生效。即纯在线模式不生效。
//        // MIX_MODE_DEFAULT 默认 ，wifi状态下使用在线，非wifi离线。在线状态下，请求超时6s自动转离线
//        // MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI wifi状态下使用在线，非wifi离线。在线状态下， 请求超时1.2s自动转离线
//        // MIX_MODE_HIGH_SPEED_NETWORK ， 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
//        // MIX_MODE_HIGH_SPEED_SYNTHESIZE, 2G 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
//
//        // 离线资源文件
//        OfflineResource offlineResource = createOfflineResource(offlineVoice);
//        // 声学模型文件路径 (离线引擎使用), 请确认下面两个文件存在
//        params.put(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, offlineResource.getTextFilename());
//        params.put(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE,
//                offlineResource.getModelFilename());
//        return params;
//    }


//    protected OfflineResource createOfflineResource(String voiceType) {
//        OfflineResource offlineResource = null;
//        try {
//            offlineResource = new OfflineResource(context, voiceType);
//        } catch (IOException e) {
//            // IO 错误自行处理
//            e.printStackTrace();
//            LogUtil.e("【error】:copy files from assets failed." + e.getMessage());
//        }
//        return offlineResource;
//    }

    /**
     * speak 实际上是调用 synthesize后，获取音频流，然后播放。
     * 获取音频流的方式见SaveFileActivity及FileSaveListener
     * 需要合成的文本text的长度不能超过1024个GBK字节。
     */
//    private void speak() {
//
//
//        // 需要合成的文本text的长度不能超过1024个GBK字节。
//        if (TextUtils.isEmpty(text)) {
//            text = "我没听懂你的意思";
//        }
//        int result = syntherizer.speak(text);
//        checkResult(result, "speak");
//    }


    /**
     * 合成但是不播放，
     * 音频流保存为文件的方法可以参见SaveFileActivity及FileSaveListener
     */
//    private void synthesize() {

//        // 需要合成的文本text的长度不能超过1024个GBK字节。
//        if (TextUtils.isEmpty(text)) {
//            text = "我没听懂你的意思";
//        }
//        int result = syntherizer.synthesize(text);
//        checkResult(result, "synthesize");
////    }

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
//    private void loadModel(String mode) {
//        offlineVoice = mode;
//        OfflineResource offlineResource = createOfflineResource(offlineVoice);
//        LogUtil.e("切换离线语音：" + offlineResource.getModelFilename());
//        int result = syntherizer.loadModel(offlineResource.getModelFilename(), offlineResource.getTextFilename());
//        checkResult(result, "loadModel");
//    }
    private void checkResult(int result, String method) {
        if (result != 0) {
            LogUtil.e("error code :" + result + " method:" + method + ", 错误码文档:http://yuyin.baidu.com/docs/tts/122 ");
        }
    }


    /**
     * 暂停播放。仅调用speak后生效
     */
//    private void pause() {
//        int result = syntherizer.pause();
//        checkResult(result, "pause");
//    }

    /**
     * 继续播放。仅调用speak后生效，调用pause生效
     */
//    private void resume() {
//        int result = syntherizer.resume();
//        checkResult(result, "resume");
//    }

    /*
     * 停止合成引擎。即停止播放，合成，清空内部合成队列。
     */
//    private void stop() {
//        int result = syntherizer.stop();
//        checkResult(result, "stop");
//    }

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


    // TODO: 2018/1/24 语音识别
    @Override
    public void onEvent(String name, String params, byte[] data, int offset, int length) {
        String result;
        switch (name) {
            case SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL://临时结果

                if (params.contains("final_result")) {
                    try {
                        JSONObject js = new JSONObject(params);
                        result = js.getJSONArray("results_recognition").getString(0);
                        LogUtil.e(":唤醒后识别结果：：：" + result);

                        checkKeys(result);
                    } catch (JSONException e) {
                        LogUtil.e("json解析错误");
                        e.printStackTrace();
                    }
                }

                break;
            case SpeechConstant.CALLBACK_EVENT_ASR_FINISH://本次识别结束
                isRecognize = false;
                LogUtil.e("唤醒后语音识别finish    " + isRecognize);

                break;
            case SpeechConstant.CALLBACK_EVENT_ASR_READY://识别引擎就绪
                LogUtil.e("唤醒后语音识别Ready");
                break;
            case SpeechConstant.ASR_CANCEL://识别取消
                LogUtil.e("唤醒后语音识别Cancel");

                break;

        }
    }

    private void checkKeys(String result) {
        if (result != null) {
            if (result.contains("师父师父") || result.contains("师傅师傅") || result.contains("师傅师父") ||
                    result.contains("师父师傅") || result.contains("师父师夫")) {
                if (!isRecognize) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            openRecognize();
                        }
                    }, 1000);
                }
                return;
            }
            if (result.contains("共修")) {
                text = OK;
                context.startActivity(new Intent(context, NianFo.class));

            } else if (result.contains("云峰寺简介")) {
                text = OK;
                showAboutApp();

            } else if (result.contains("搜索")) {
                text = OK;
                Intent intent = new Intent(context, Search.class);
                intent.putExtra("text", result.substring(result.indexOf("搜索") + 2));
                context.startActivity(intent);
            } else if (result.contains("设置")) {
                text = OK;
                context.startActivity(new Intent(context, Setting.class));
            } else if (result.contains("排行榜")) {
                text = OK;
                context.startActivity(new Intent(context, yundou_paihang.class));
            } else if (result.contains("祈福券") || result.contains("牌位券")) {
                text = OK;
                context.startActivity(new Intent(context, DuiHuan.class));
            } else if (result.contains("上传壁纸")) {
                text = OK;
                if (new LoginUtil().checkLogin(context)) {
                    context.startActivity(new Intent(context, WallPaperUpload.class));
                }

            } else if (result.contains("我的壁纸") || result.contains("管理壁纸")) {
                text = OK;
                if (new LoginUtil().checkLogin(context)) {
                    context.startActivity(new Intent(context, WallPaperUserHome.class));
                }

            } else if (result.contains("壁纸")) {
                text = OK;
                context.startActivity(new Intent(context, WallPapaerHome.class));
            } else if (result.contains("客服")) {
                text = OK;
                Intent i2 = new Intent(context, GanyuActivity.class);
                context.startActivity(i2);
            }
//            else if (result.contains("义卖") || result.contains("拍卖") || result.contains("竞拍")) {
//                text = OK;
//                Intent i2 = new Intent(context, AuctionList.class);
//                context.startActivity(i2);
//            }
            else if (result.contains("祈愿树") || result.contains("许愿") || result.contains("祈愿")) {
                text = OK;
                Intent i2 = new Intent(context, BlessTree.class);
                context.startActivity(i2);
            } else if (result.contains("电子书") || result.contains("佛经") || result.contains("经书")) {
                text = OK;
                Intent i2 = new Intent(context, BookList.class);
                context.startActivity(i2);
            } else if (result.contains("大藏经")) {
                text = OK;
                Intent i2 = new Intent(context, BookList.class);
                i2.putExtra("type", 2);
                context.startActivity(i2);
            } else if (result.contains("活动") || result.contains("报名") || result.contains("短期出家")) {
                text = OK;
                ((MainActivity) context).pager.setCurrentItem(1);
                Intent i2 = new Intent(context, MainActivity.class);
                i2.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);//设置不要刷新将要跳到的界面
                i2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//它可以关掉所要到的界面中间的activity
                context.startActivity(i2);
                mApplication.getInstance().closeAllActivities();
            } else if (result.contains("供养")) {
                text = OK;
                ((MainActivity) context).pager.setCurrentItem(2);
                Intent i2 = new Intent(context, MainActivity.class);
                i2.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);//设置不要刷新将要跳到的界面
                i2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//它可以关掉所要到的界面中间的activity
                context.startActivity(i2);
                mApplication.getInstance().closeAllActivities();
            } else if (result.contains("助学")) {
                text = OK;
                ((MainActivity) context).pager.setCurrentItem(3);
                Intent i2 = new Intent(context, MainActivity.class);
                i2.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);//设置不要刷新将要跳到的界面
                i2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//它可以关掉所要到的界面中间的activity
                context.startActivity(i2);
                mApplication.getInstance().closeAllActivities();
            } else if (result.contains("收藏")) {
                if (new LoginUtil().checkLogin(context)) {
                    text = OK;
                    context.startActivity(new Intent(context, Activity_ShouCang.class));
                }
            } else if (result.contains("云豆") || result.contains("我的云豆")) {
                if (new LoginUtil().checkLogin(context)) {
                    text = OK;
                    context.startActivity(new Intent(context, YunDouHome.class));
                }
            } else if (result.contains("福利") || result.contains("我的福利") || result.contains("福利券")) {
                if (new LoginUtil().checkLogin(context)) {
                    text = OK;
                    context.startActivity(new Intent(context, MyQuan.class));
                }
            } else if (result.contains("念佛")) {
                text = OK;
                context.startActivity(new Intent(context, nianfo_home_tab1.class));
            } else if (result.contains("诵经")) {
                text = OK;
                context.startActivity(new Intent(context, nianfo_home_tab2.class));
            } else if (result.contains("持咒")) {
                text = OK;
                context.startActivity(new Intent(context, nianfo_home_tab3.class));
            } else if (result.contains("助念")) {
                text = OK;
                context.startActivity(new Intent(context, nianfo_home_tab4.class));
            } else if (result.contains("忏悔")) {
                text = OK;
                context.startActivity(new Intent(context, nianfo_home_tab5.class));
            } else if (result.contains("发愿")) {
                text = OK;
                context.startActivity(new Intent(context, nianfo_home_tab6.class));
            } else if (result.contains("坐禅") || result.contains("禅修") || result.contains("打坐") || result.contains("静坐")) {
                text = OK;
                context.startActivity(new Intent(context, Meditation.class));
            } else if (result.contains("通知")) {
                if (new LoginUtil().checkLogin(context)) {
                    text = OK;
                    context.startActivity(new Intent(context, MessageCenter.class));
                }
            } else if (result.contains("会员")) {
                if (new LoginUtil().checkLogin(context)) {
                    text = OK;
                    context.startActivity(new Intent(context, Mine_HuiYuan.class));
                }
            } else if (result.contains("投稿")) {
                if (new LoginUtil().checkLogin(context)) {
                    text = OK;
                    context.startActivity(new Intent(context, TouGao.class));
                }
            } else if (result.contains("签到") || result.contains("我的活动") || result.contains("活动审核结果")) {
                if (new LoginUtil().checkLogin(context)) {
                    text = OK;
                    context.startActivity(new Intent(context, Mine_activity_list.class));
                }
            } else if (result.contains("感谢信")) {
                if (new LoginUtil().checkLogin(context)) {
                    text = OK;
                    context.startActivity(new Intent(context, Month_Detail.class));
                }
            } else if (result.contains("抽签") || result.contains("运势") || result.contains("每日一签") || result.contains("卜事")
                    || result.contains("算命") || result.contains("卜卦") || result.contains("解梦") || result.contains("算卦")) {
                if (new LoginUtil().checkLogin(context)) {
                    text = OK;
                    context.startActivity(new Intent(context, Fortune.class));
                }
            } else {
                text = OhNo;
            }
            ibdRcognize.stop();
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();

            if (text.equals(OhNo)) {
                mediaPlayer = MediaPlayer.create(context, R.raw.ohno);
                mediaPlayer.start();
            } else {
                //如果语音命中    界面跳转 唤醒屏幕   解锁键盘  语音回复
                WakeLockUtil.wakeUpAndUnlock();
                mediaPlayer = MediaPlayer.create(context, R.raw.ok);
                mediaPlayer.start();
            }

        }
    }

    private void showAboutApp() {
        JSONObject js = new JSONObject();
        try {
            js.put("m_id", Constants.M_id);
        } catch (JSONException e) {
            e.printStackTrace();

        }
        LogUtil.e("获取关于云峰寺：：" + js);
        ApisSeUtil.M m = ApisSeUtil.i(js);
        OkGo.post(Constants.AboutApp)
                .params("key", m.K())
                .params("msg", m.M())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        HashMap<String, String> map = AnalyticalJSON.getHashMap(s);
                        if (map != null && !map.get("aboutapp").equals("")) {
                            View          view   = LayoutInflater.from(context).inflate(R.layout.activity_confirm_dialog, null);
                            final WebView web    = view.findViewById(R.id.web);
                            TextView      cancle = view.findViewById(R.id.cancle);
                            cancle.setText(mApplication.ST("确定"));
                            final TextView baoming = view.findViewById(R.id.baoming);
                            baoming.setEnabled(false);
                            baoming.setVisibility(View.GONE);

                            web.loadDataWithBaseURL("", map.get("aboutapp")
                                    , "text/html", "UTF-8", null);

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setView(view);

                            final AlertDialog dialog = builder.create();
                            cancle.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    web.destroy();
                                    dialog.dismiss();
                                }
                            });
                            builder.setCancelable(false);
                            web.setWebViewClient(new WebViewClient() {
                                @Override
                                public void onPageFinished(WebView view, String url) {
                                    super.onPageFinished(view, url);
                                    dialog.show();
                                    JsUtil.imgReset(web);
                                }
                            });


                        } else {
                            ToastUtil.showToastShort("暂无云峰寺简介");
                        }
//
                    }


                });
    }
}
