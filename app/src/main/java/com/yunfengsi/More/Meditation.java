package com.yunfengsi.More;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.yunfengsi.R;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.mApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 作者：因陀罗网 on 2018/1/31 16:30
 * 公司：成都因陀罗网络科技有限公司
 */

public class Meditation extends AppCompatActivity implements View.OnClickListener {
    private TextView key;
    private ImageView back;
    private TextView time;
    private CountDownTimer countDownTimer;
    private static final int UP = 0;
    private static final int OVER = 1;
    private static final int FINAL = 2;
    private int status = UP;//
    private MediaPlayer mediaPlayer;
    private long destTime = 0;
    private long allTime=45*60000;
    private TextView history;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.meditation);
        mApplication.addActivity(this);
        ((TextView) findViewById(R.id.title)).setText(mApplication.ST("坐禅"));
        ((TextView) findViewById(R.id.meditationHistory)).setText(mApplication.ST("记录"));
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (destTime > 0 && status == OVER) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Meditation.this);
                    builder.setMessage(mApplication.ST("坐禅时间还未结束，确定要结束坐禅吗？"))
                            .setPositiveButton(mApplication.ST("确定结束"), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    doOver();
                                }
                            }).setNegativeButton(mApplication.ST("取消"), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).create().show();

                }else{
                    finish();
                }
            }
        });
        key = (TextView) findViewById(R.id.meditation_key);
        key.setText(mApplication.ST("开始坐禅"));
        key.setOnClickListener(this);
        time = (TextView) findViewById(R.id.time);
        countDownTimer = new CountDownTimer(allTime, 1000) {
            @Override
            public void onTick(long l) {
                destTime = l;
                long minute = l / 1000 / 60;
                long second = l / 1000 - minute * 60;
                LogUtil.e("当前时间：：；" + minute + " : " + second);
                time.setText(mApplication.ST(minute + "分  :  " + (second < 10 ? "0" + second : second) + "秒"));

            }

            @Override
            public void onFinish() {
                key.performClick();
                time.setText("");
            }
        };
        mediaPlayer = MediaPlayer.create(this, R.raw.up_seat);
        history= (TextView) findViewById(R.id.meditationHistory);
        history.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        back.performClick();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.release();//释放资源
        countDownTimer.cancel();
        mApplication.romoveActivity(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.meditationHistory:
                startActivity(new Intent(this,Meditation_History.class));
                break;
            case R.id.meditation_key:
                if (status == UP) {
                    status = OVER;
                    time.setVisibility(View.VISIBLE);
                    //播放上座音，开始倒计时，变化按钮文字
                    key.setText(mApplication.ST("结束坐禅"));
                    countDownTimer.start();
                    mediaPlayer.start();
                    mediaPlayer.setVolume(1.0f,1.0f);
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(final MediaPlayer media) {
                            mediaPlayer.reset();
                            mediaPlayer = MediaPlayer.create(Meditation.this, R.raw.huxi);
                            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer media) {
                                    mediaPlayer.start();
                                    mediaPlayer.setVolume(1.0f,1.0f);
                                }
                            });
                            mediaPlayer.start();
                            mediaPlayer.setVolume(1.0f,1.0f);
                        }
                    });

                } else if (status == OVER) {
                    //播放下座音，按钮失效，坐禅结束
                    if (destTime > 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Meditation.this);
                        builder.setMessage(mApplication.ST("本次坐禅时间还未结束，确定要结束坐禅吗？"))
                                .setPositiveButton(mApplication.ST("确定结束"), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        doOver();
                                    }
                                }).setNegativeButton(mApplication.ST("取消"), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).create().show();
                    } else {
                        doOver();
                    }

                }


                break;
        }
    }

    private void doOver() {
        postMuse();
        key.setEnabled(false);
        countDownTimer.cancel();
        status = FINAL;
        time.setVisibility(View.GONE);
        key.setText(mApplication.ST("坐禅结束"));
        mediaPlayer.reset();
        mediaPlayer = MediaPlayer.create(Meditation.this, R.raw.bells);
        mediaPlayer.start();
        mediaPlayer.setVolume(1.0f,1.0f);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer1) {
                mediaPlayer.reset();
                mediaPlayer = MediaPlayer.create(Meditation.this, R.raw.down_seat);
                mediaPlayer.start();
                mediaPlayer.setVolume(1.0f,1.0f);
            }
        });
    }

    private void postMuse() {
        JSONObject js=new JSONObject();
        try {
            js.put("user_id", PreferenceUtil.getUserId(this));
            js.put("time", (allTime-destTime)/1000);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m=ApisSeUtil.i(js);
        OkGo.post(Constants.Muse).params("key",m.K())
                .params("msg",m.M())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        HashMap<String,String> map= AnalyticalJSON.getHashMap(s);
                        if(map!=null){
                            if("000".equals(map.get("code"))){
                                LogUtil.e("坐禅提交成功");
                            }else{
                                LogUtil.e("code校验错误");
                            }
                        }else{
                            onError(call,response,new Exception("map为null"));
                        }

                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        LogUtil.e("坐禅提交失败：；"+e.getMessage());
                        postMuse();
                    }
                });
    }
}
