package com.yunfengsi.Models.More;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.yunfengsi.R;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.ImageUtil;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.OneDrawable;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.ProgressUtil;
import com.yunfengsi.Utils.ShareManager;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.ToastUtil;
import com.yunfengsi.Utils.mApplication;
import com.yunfengsi.Models.YunDou.YunDouAwardDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 作者：因陀罗网 on 2018/1/29 15:40
 * 公司：成都因陀罗网络科技有限公司
 * 在线运势  每日一签
 */

public class Fortune extends AppCompatActivity implements SensorEventListener, View.OnClickListener {
    private ImageView back;
    private ImageView qiantong, qian_small, qian;
    private TextView auto, history;
    private TextView tip;
    private int width, height;
    private SensorManager sensorManager;
    private boolean isWaved = false;//是否摇过签
    private SoundPool soundPool;//音效播放器
    private int soundId;
    private int waveCount = 0;
    private int cancelCount = 0;
    private boolean isDisplay = false;//是否正在显示签
    private boolean isRegist = false;//是否注册加速度传感器
    private boolean isAutoIng=false;//是否正在自动摇签
    private int streamid;//音效流
    private int limit = 14;

    @Override
    protected void onDestroy() {
        qian.destroyDrawingCache();
        qian_small.destroyDrawingCache();
        qiantong.destroyDrawingCache();
        findViewById(R.id.root).destroyDrawingCache();
        super.onDestroy();
        mApplication.getInstance().romoveActivity(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.fortune);
        mApplication.getInstance().addActivity(this);
        back = (ImageView) findViewById(R.id.back);
        ((TextView) findViewById(R.id.title)).setText(mApplication.ST("卜事"));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        qian_small = (ImageView) findViewById(R.id.qian_small);
        Drawable drawable = OneDrawable.createBgDrawable(this, R.drawable.qian1_low);
        auto = (TextView) findViewById(R.id.autoFortune);
        auto.setText(mApplication.ST("自动摇签"));
        auto.setOnClickListener(this);
        history = (TextView) findViewById(R.id.fortuneHistory);
        history.setText(mApplication.ST("摇签记录"));
        history.setOnClickListener(this);
        qian_small.setImageDrawable(drawable);
        qian = (ImageView) findViewById(R.id.qian);
        tip = (TextView) findViewById(R.id.tip);

        width = getResources().getDisplayMetrics().widthPixels - DimenUtils.dip2px(this, 100);
        height = getResources().getDisplayMetrics().heightPixels - DimenUtils.dip2px(this, 145);
        qiantong = (ImageView) findViewById(R.id.qiantong);
        //加载首屏图片
        Glide.with(this).load(R.drawable.qian1_low)
                .override(width, height)
                .into(qiantong);
        //将摇动图片加入内存
        Glide.with(this).load(R.drawable.qian2_low)
                .asBitmap().into(width, height);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        soundId = soundPool.load(this, R.raw.qian, 1);
        qian_small.setOnClickListener(this);
        qian.setOnClickListener(this);
        tip.setOnClickListener(this);
        tip.setText(mApplication.ST("1.抽签前双手合十，默念[南无观世音菩萨]三遍。\n\n2.默念自己姓名、出生时辰、年龄、地址。\n\n3.助学供养可以更好的体现您的诚意。\n\n4.晃动手机开始抽签，且每日一次。"));
        findViewById(R.id.share).setOnClickListener(this);

        ((TextView) findViewById(R.id.tip1)).setText(mApplication.ST("Tip : 摇一摇手机开始抽签"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        ProgressUtil.dismiss();
        if(isRegist){
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        sensorManager.unregisterListener(this);
//    }

    @Override
    protected void onStop() {
        super.onStop();
        ProgressUtil.dismiss();
        sensorManager.unregisterListener(this);

    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        soundPool.release();
    }

    // TODO: 2018/1/29 传感器回调
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (isDisplay||isAutoIng) {
            LogUtil.e("正在显示签或者正在自动摇签阶段");
            return;
        }
        //获取传感器类型
        int sensorType = event.sensor.getType();
        //values[0]:X轴，values[1]：Y轴，values[2]：Z轴
        float[] values = event.values;
        //如果传感器类型为加速度传感器，则判断是否为摇一摇
        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            LogUtil.i("摇一摇");
            if (Build.VERSION.SDK_INT >= 21) {
                limit = 11;
            } else {
                limit = 10;
            }
            if ((Math.abs(values[0]) > limit || Math.abs(values[1]) > limit || Math
                    .abs(values[2]) > limit)) {
                LogUtil.e("sensor x ============ values[0] = " + values[0]);
                LogUtil.e("sensor y =========== values[1] = " + values[1]);
                LogUtil.e("sensor z ============ values[2] = " + values[2]);
                if (waveCount == 0) {
                    waveCount = 1;
                    isWaved = true;

                    streamid = soundPool.play(soundId, 0.8f, 0.8f, 10, Integer.MAX_VALUE, 1.0f);
                }
                waveCount++;
                cancelCount = 0;
                if (waveCount % 2 == 0) {
                    Glide.with(this).load(R.drawable.qian1_low)
                            .override(width, height)
                            .into(qiantong);
                } else {
                    Glide.with(this).load(R.drawable.qian2_low)
                            .override(width, height)
                            .into(qiantong);
                }
                LogUtil.e("开始soundPool:::::" + soundPool + "      ;::   " + soundId);

            } else {
                cancelCount++;
                LogUtil.e("cancelCount::;" + cancelCount + "    是否正在摇签：：    " + (isWaved ? "是" : "否"));
                if (cancelCount >= 3 && isWaved) {
                    LogUtil.e("停止声音");
                    cancelCount = 0;

                    LogUtil.e("结束soundPool:::::" + soundPool + "      ;::   " + soundId);
                    soundPool.stop(streamid);
                    Glide.with(this).load(R.drawable.qian1_low)
                            .override(width, height)
                            .into(qiantong);
                    isWaved = false;
                    doFortune();
                }


            }
        }
    }

    private void doFortune() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("m_id", Constants.M_id);
            jsonObject.put("user_id", PreferenceUtil.getUserId(this));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m = ApisSeUtil.i(jsonObject);
        LogUtil.e("抽签：：：" + jsonObject);
        OkGo.post(Constants.Fortune).params("key", m.K())
                .params("msg", m.M())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        HashMap<String, String> map = AnalyticalJSON.getHashMap(s);
                        if (map != null) {
                            auto.setEnabled(true);
                            if ("000".equals(map.get("code"))) {
                                if(!"0".equals(map.get("yundousum"))){

                                    YunDouAwardDialog.show(Fortune.this,"每日一签",map.get("yundousum"));
                                }
                            } else if ("002".equals(map.get("code"))) {
                                ToastUtil.showToastShort("您今天已经抽过签了");
                            }
                            if (!isDisplay) {
                                findViewById(R.id.qiantongContainer).setVisibility(View.GONE);
                                qian.setVisibility(View.VISIBLE);
                                HashMap<String, String> msg = AnalyticalJSON.getHashMap(map.get("msg"));
                                qian.setImageBitmap(ImageUtil.drawTextToBitmap(Fortune.this, R.drawable.qian3, msg.get("num")
                                        , DimenUtils.dip2px(Fortune.this, 100),
                                        DimenUtils.dip2px(Fortune.this, 100) * 1016 / 244, 18, true));
                                AnimatorSet animatorSet = new AnimatorSet();
                                ObjectAnimator scaleX = ObjectAnimator.ofFloat(qian, "scaleX", 0.2f, 1f);
                                ObjectAnimator scaleY = ObjectAnimator.ofFloat(qian, "scaleY", 0.2f, 1f);
                                ObjectAnimator rotation = ObjectAnimator.ofFloat(qian, "rotation", 0, 360);
                                scaleX.setDuration(2000);
                                scaleY.setDuration(2000);
                                rotation.setDuration(2000);
                                animatorSet.playTogether(scaleX, scaleY, rotation);
                                animatorSet.start();
                                qian.setTag(msg);
                                isDisplay = true;
                                qian_small.setVisibility(View.VISIBLE);
                            }



                        }
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        ProgressUtil.show(Fortune.this, "", "请稍等");
                    }

                    @Override
                    public void onAfter(String s, Exception e) {
                        super.onAfter(s, e);
                        ProgressUtil.dismiss();
                        if(isAutoIng){
                            isAutoIng=false;
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!isDisplay) {
                            waveCount = 0;
                            ToastUtil.showToastShort("请求超时，请重新摇签");
                            findViewById(R.id.qiantongContainer).setVisibility(View.GONE);
                        }

                    }
                });
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.share:
                UMWeb umWeb = new UMWeb("http://a.app.qq.com/o/simple.jsp?pkgname=com.yunfengsi");
                umWeb.setTitle(mApplication.ST("人生卜事"));
                umWeb.setDescription(mApplication.ST("想知道今天有什么好运将要发生吗？我来告诉你答案~"));
                umWeb.setThumb(new UMImage(this, R.drawable.indra_share));
                new ShareManager().shareWeb(umWeb, this);
                break;
            case R.id.qian:
                Intent intent = new Intent(this, Fortune_Detail.class);
                intent.putExtra("map", ((HashMap<String, String>) qian.getTag()));
                startActivity(intent);
                break;
            case R.id.qian_small:
                isDisplay = false;
                isWaved = false;
                waveCount = 0;
                if (!isRegist) {
                    isRegist = true;
                    sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
                }
                findViewById(R.id.qiantongContainer).setVisibility(View.VISIBLE);
                findViewById(R.id.bg).setVisibility(View.VISIBLE);
                qian.setVisibility(View.GONE);
                qian_small.setVisibility(View.GONE);
                break;

            case R.id.autoFortune:
                isAutoIng=true;
                view.setEnabled(false);
                streamid = soundPool.play(soundId, 0.8f, 0.8f, 10, 3, 1.0f);
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doFortune();
                    }
                }, 1000);
                break;
            case R.id.fortuneHistory:
                startActivity(new Intent(this, Fortune_History.class));
                break;
            case R.id.tip:
                tip.setVisibility(View.GONE);
                findViewById(R.id.bg).setVisibility(View.GONE);
                qian_small.setEnabled(true);
                break;
        }
    }
}
