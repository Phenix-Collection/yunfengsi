package com.yunfengsi.Models.RedPacket;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.yunfengsi.R;
import com.yunfengsi.View.ErWeiMa.QRCodeUtil;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.DESUtil;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.ImageUtil;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.Network;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.ProgressUtil;
import com.yunfengsi.Utils.TimeUtils;
import com.yunfengsi.Utils.ToastUtil;
import com.yunfengsi.View.RedRainView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 作者：因陀罗网 on 2017/11/15 16:11
 * 公司：成都因陀罗网络科技有限公司
 */

public class RedPacket extends AppCompatActivity {

    private RedRainView redRainView;
    private HashMap<String, String> MMap;//拆福包后数据
    private HashMap<String, String> info;//背景图片，赠语等信息
    private String status = "0";
    private MediaPlayer mMediaplayer = new MediaPlayer();
    private ValueAnimator valueAnimator;//音乐动画
    private View musicImg;
    private int emptyCount = 0;

    private String unionId, openId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.red_packet);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ((ImageView) findViewById(R.id.title_back)).setImageBitmap(ImageUtil.readBitMap(this, R.drawable.cancle_white));
        ((ImageView) findViewById(R.id.title_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(redRainView!=null){
                    redRainView.stopRainNow();
                }
                finish();
            }
        });
        getInfo();
        redRainView = (RedRainView) findViewById(R.id.redrain);
        Glide.with(this).load(R.drawable.hongbao_bg).asBitmap()
                .into(new SimpleTarget<Bitmap>(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels - DimenUtils.dip2px(this, 40)) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        BitmapDrawable bt = new BitmapDrawable(resource);
                        if (Build.VERSION.SDK_INT >= 16) {
                            redRainView.setBackground(bt);
                        } else {
                            redRainView.setBackgroundDrawable(bt);
                        }
                    }
                });

        redRainView.setOnRedPacketClickListener(new RedRainView.onRedPacketClickListener() {
            @Override
            public void onRePacketClick(RedRainView.RedPacket redPacket) {
                AlertDialog.Builder b = new AlertDialog.Builder(RedPacket.this);
                final View view = LayoutInflater.from(RedPacket.this).inflate(R.layout.red_packet_dialog, null);
                final ImageView hongbaoBg = ((ImageView) view.findViewById(R.id.backgroud));
                hongbaoBg.setImageBitmap(ImageUtil.readBitMap(RedPacket.this, R.drawable.hongbao_close_bg));
                if (info != null) {
                    Glide.with(RedPacket.this).load(info.get("redimg")).placeholder(R.drawable.default_redimg)
                            .into(((ImageView) view.findViewById(R.id.redimg)));
//
                } else {
                    ((ImageView) view.findViewById(R.id.redimg)).setImageBitmap(ImageUtil.readBitMap(RedPacket.this, R.drawable.default_redimg));
                }
                ImageView redimg = (ImageView) view.findViewById(R.id.redimg);
                final TextView chai = (TextView) view.findViewById(R.id.chai);
                int px500 = getResources().getDisplayMetrics().widthPixels;
                int px1 = px500 / 400;
                ViewGroup.MarginLayoutParams vm = (ViewGroup.MarginLayoutParams) chai.getLayoutParams();
                vm.topMargin = px1 * 144;
                chai.setLayoutParams(vm);
                b.setView(view);
                ViewGroup.MarginLayoutParams vm1 = (ViewGroup.MarginLayoutParams) redimg.getLayoutParams();
                vm1.bottomMargin = px1 * 120;
                redimg.setLayoutParams(vm1);
                chai.setLayoutParams(vm);
                final AlertDialog dialog = b.create();

                chai.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        int random = 0;
                        if (emptyCount <= 3) {
                            random = new Random().nextInt(10);
                        }
                        final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 360f);
                        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                chai.setRotationY((Float) valueAnimator.getAnimatedValue());

                            }
                        });
                        if (random < 5) {//抽中红包
                            emptyCount = 0;
                            valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
                        } else {//没抽中红包
                            emptyCount++;
                            valueAnimator.setRepeatCount(1);
                            valueAnimator.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animator) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animator) {

                                }

                                @Override
                                public void onAnimationCancel(Animator animator) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animator) {
                                    ToastUtil.showToastShort("很遗憾，这个福包是空的，请再次尝试");
                                    dialog.dismiss();
                                }
                            });
                            valueAnimator.setInterpolator(new LinearInterpolator());
                            valueAnimator.setDuration(800);
                            valueAnimator.start();
                            return;
                        }
                        valueAnimator.setInterpolator(new LinearInterpolator());
                        valueAnimator.setDuration(800);
                        valueAnimator.start();
                        JSONObject js = new JSONObject();
                        try {
                            js.put("m_id", Constants.M_id);
                            js.put("user_id", PreferenceUtil.getUserId(RedPacket.this));
                            js.put("red_id", info.get("red_id"));
                            js.put("type", info.get("id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ;
                        ApisSeUtil.M m = ApisSeUtil.i(js);

                        OkGo.post(Constants.RedMoney).params("key", m.K())
                                .params("msg", m.M())
                                .execute(new StringCallback() {
                                    @Override
                                    public void onSuccess(String s, Call call, Response response) {
                                        HashMap<String, String> map = AnalyticalJSON.getHashMap(s);
                                        if (map != null) {
                                            RedPacket.this.MMap = map;
                                            redRainView.stopRainNow();
                                            valueAnimator.cancel();
                                            dialog.dismiss();
                                            if ("002".equals(map.get("code"))) {
                                                ToastUtil.showToastShort("您来太晚了，福包已被领完了");
                                            } else {
                                                showGetDialog(map);
                                            }

                                        } else {
                                            valueAnimator.cancel();
                                            ToastUtil.showToastShort("福包数据请求失败，请稍后重试");
                                        }
                                    }

                                    @Override
                                    public void onError(Call call, Response response, Exception e) {
                                        super.onError(call, response, e);
                                        valueAnimator.cancel();
                                        ToastUtil.showToastShort("福包数据请求失败，请稍后重试");
                                    }
                                });
                    }
                });


                Window window = dialog.getWindow();
                WindowManager.LayoutParams wl = window.getAttributes();
                window.getDecorView().setPadding(0, 0, 0, 0);
                wl.gravity = Gravity.CENTER;
                wl.width = getResources().getDisplayMetrics().widthPixels * 8 / 10;
//                wl.width = getResources().getDisplayMetrics().widthPixels;
                wl.height = WindowManager.LayoutParams.WRAP_CONTENT;
                window.setDimAmount(0.7f);
                window.setWindowAnimations(R.style.RedPacketAnimation);
                window.setBackgroundDrawableResource(R.color.transparent);
                window.setAttributes(wl);
                dialog.show();
            }
        });
    }

    private void showGetDialog(HashMap<String, String> map) {
        ((ImageView) findViewById(R.id.title_back)).setVisibility(View.GONE);
        AlertDialog.Builder b = new AlertDialog.Builder(RedPacket.this);
        final View view1 = LayoutInflater.from(RedPacket.this).inflate(R.layout.hongbao_dialog_open, null);
        ImageView hongbaoBg = ((ImageView) view1.findViewById(R.id.openbg));
        hongbaoBg.setImageBitmap(ImageUtil.readBitMap(RedPacket.this, R.drawable.hongbao_bg2));
        TextView money = (TextView) view1.findViewById(R.id.money);
        SpannableString ss = new SpannableString("恭喜您获得" + map.get("money") + "元");
        ss.setSpan(new ForegroundColorSpan(Color.parseColor("#ffefd5")), 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new AbsoluteSizeSpan(DimenUtils.dip2px(RedPacket.this, 30)), 6, ss.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        money.setText(ss);
        b.setView(view1);
//        TextView motto = (TextView) view1.findViewById(R.id.motto);
//        SpannableString ss1 = new SpannableString(info.get("issuer") + "赠语:\n" + info.get("contents"));
//        ss1.setSpan(new AbsoluteSizeSpan(DimenUtils.dip2px(RedPacket.this, 24)), 0, info.get("issuer").length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        motto.setText(ss1);
        final AlertDialog dialog = b.create();
        if (status.equals("0") || status.equals("1")) {
            ((TextView) view1.findViewById(R.id.get_money)).setText("领取福包");
            ((TextView) view1.findViewById(R.id.get_money)).setBackgroundResource(R.drawable.get_money_sel);
        } else if (status.equals("2")) {
            view1.postDelayed(new Runnable() {
                @Override
                public void run() {
                    obtainQrImg(view1);
                }
            }, 500);

//            ((TextView) view1.findViewById(R.id.get_money)).setText("点击二维码去微信领福包");
            ((TextView) view1.findViewById(R.id.get_money)).setVisibility(View.GONE);

        }

        musicImg = view1.findViewById(R.id.audio);
        startAudio();
        valueAnimator = ValueAnimator.ofFloat(0, 360);
        mMediaplayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                valueAnimator.setDuration(2000);
                valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
                valueAnimator.setInterpolator(new LinearInterpolator());
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        view1.findViewById(R.id.audio).setRotation((Float) valueAnimator.getAnimatedValue());
                    }
                });
                valueAnimator.start();
            }

        });
        mMediaplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mMediaplayer.reset();
                valueAnimator.cancel();
                musicImg.setRotation(0);
            }
        });
        //开关音乐
        view1.findViewById(R.id.audio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMediaplayer.isPlaying()) {
                    mMediaplayer.stop();
                    mMediaplayer.reset();
                    valueAnimator.cancel();
                    view1.findViewById(R.id.audio).setRotation(0);
                } else {
                    startAudio();
                }


            }
        });
        view1.findViewById(R.id.cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        //领取福包
        view1.findViewById(R.id.get_money).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder b = new AlertDialog.Builder(RedPacket.this);
                final View view2 = LayoutInflater.from(RedPacket.this).inflate(R.layout.dialog_bottom_good_manager, null);
                b.setView(view2);
                b.setCancelable(true);
                final Dialog dialog = b.create();
                view2.findViewById(R.id.cancle).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                view2.findViewById(R.id.weixin).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getWexinAuther(view1);
                        dialog.dismiss();
                    }
                });
                view2.findViewById(R.id.alipay).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                Window window = dialog.getWindow();
                window.setGravity(Gravity.BOTTOM);
                window.getDecorView().setPadding(0, 0, 0, 0);
                window.setWindowAnimations(R.style.dialogWindowAnim);
                window.setBackgroundDrawableResource(R.color.vifrification);
                WindowManager.LayoutParams wl = window.getAttributes();
                wl.width = getResources().getDisplayMetrics().widthPixels;
                wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                window.setAttributes(wl);
                dialog.show();
            }
        });

        //保存二维码
        view1.findViewById(R.id.qr_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                obtainQrImg(view1);
                getWexinAuther(view1);


            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                finish();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wl = window.getAttributes();
        window.getDecorView().setPadding(0, 0, 0, 0);
        wl.gravity = Gravity.CENTER;
        wl.width = getResources().getDisplayMetrics().widthPixels * 8 / 10;
        wl.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setDimAmount(0.5f);
        window.setWindowAnimations(R.style.RedPacketAnimation);
        window.setBackgroundDrawableResource(R.color.transparent);
        window.setAttributes(wl);
        dialog.show();
    }

    private void goToWeChat() {
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");

            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cmp);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
// TODO: handle exception
            Toast.makeText(RedPacket.this, "检查到您手机没有安装微信，请安装后使用该功能", Toast.LENGTH_LONG).show();
        }
    }

    //微信授权信息获取
    private void getWexinAuther(final View view1) {
        final UMShareAPI umShareAPI = UMShareAPI.get(this);
        umShareAPI.doOauthVerify(this, SHARE_MEDIA.WEIXIN, new UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {
                ProgressUtil.show(RedPacket.this, "", "请稍等");
            }

            @Override
            public void onComplete(SHARE_MEDIA share_media, int action, Map<String, String> map) {
                if (action == 0) {
                    umShareAPI.getPlatformInfo(RedPacket.this, SHARE_MEDIA.WEIXIN, this);
                    ProgressUtil.dismiss();
                    LogUtil.e("微信 action==0：：：；" + map);
                } else if (action == 2) {
                    ProgressUtil.dismiss();
                    LogUtil.e("微信 action==2：：：；" + map);
                    openId = map.get("openid");
                    unionId = map.get("unionid");
                    JSONObject js = new JSONObject();
                    try {
                        js.put("m_id", Constants.M_id);
                        js.put("openid", openId);
                        js.put("unionid", unionId);
                        js.put("user_id", PreferenceUtil.getUserId(RedPacket.this));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m = ApisSeUtil.i(js);
                    OkGo.post(Constants.WeChatRed).params("key", m.K())
                            .params("msg", m.M())
                            .execute(new StringCallback() {
                                @Override
                                public void onSuccess(String s, Call call, Response response) {
                                    LogUtil.e("微信红包：：：：" + s);
                                    HashMap<String, String> m = AnalyticalJSON.getHashMap(s);
                                    if (m != null) {
                                        if ("000".equals(m.get("code"))) {
                                            ProgressUtil.dismiss();
                                        } else if ("003".equals(m.get("code"))) {

                                        }
                                        obtainQrImg(view1);
                                        AlertDialog.Builder b = new AlertDialog.Builder(RedPacket.this);
                                        b.setPositiveButton("去微信", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                goToWeChat();
                                                dialogInterface.dismiss();
                                            }
                                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        }).setMessage("现在去微信领取福包吗？打开微信扫一扫>点击右上角弹窗选择“从相册选择二维码”>选择福包二维码扫描").create().show();
                                    }
                                }

                                @Override
                                public void onAfter(String s, Exception e) {
                                    super.onAfter(s, e);
                                    ProgressUtil.dismiss();
                                }
                            });
                }
            }

            @Override
            public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                ToastUtil.showToastShort(throwable.getMessage());
                ProgressUtil.dismiss();
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media, int i) {
                ProgressUtil.dismiss();
            }
        });
    }

    private void startAudio() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mMediaplayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                        @Override
                        public boolean onError(MediaPlayer mp, int what, int extra) {
                            mMediaplayer.reset();
                            return false;
                        }
                    });
                    mMediaplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mMediaplayer.setDataSource(info.get("audiourl"));
                    mMediaplayer.prepare();
                    mMediaplayer.start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }).start();
    }

    private void obtainQrImg(final View view1) {
        String user_id = PreferenceUtil.getUserId(RedPacket.this);
        JSONObject js = new JSONObject();
        String msg = "";
        try {
            js.put("user_id", user_id);
            if (status.equals("0")) {
                LogUtil.e("拆包"+MMap.get("num"));
                js.put("num", MMap.get("num"));
                js.put("money", MMap.get("money"));
                msg = com.yunfengsi.Utils.Base64.encode(DESUtil.encrypt(js.toString().getBytes(), MMap.get("key")));
            } else if (status.equals("2")) {

                js.put("num", info.get("num"));
                js.put("money", info.get("money"));
                msg = com.yunfengsi.Utils.Base64.encode(DESUtil.encrypt(js.toString().getBytes(), info.get("key")));
                LogUtil.e("已拆包"+info.get("num")+"     "+msg);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String content = null;

        content = Constants.host_Ip + "/" + Constants.NAME_LOW + ".php/Index/Yfshblq/msg/" + msg;

        LogUtil.e("content:::" + content);
        final String filename = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + System.currentTimeMillis()+ ".jpg";
        if (QRCodeUtil.createQRImage(content, 400, 400, ImageUtil.readBitMap(RedPacket.this, R.drawable.indra), filename)) {

            Glide.with(RedPacket.this).load(filename).into(new SimpleTarget<GlideDrawable>() {
                @Override
                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                    try {
                        ((ImageView) view1.findViewById(R.id.qr_img)).setImageDrawable(resource);
                        view1.findViewById(R.id.qr_img).setVisibility(View.VISIBLE);
                        //显示二维码提示
                        ((TextView) view1.findViewById(R.id.tip)).setText("点击二维码跳转到微信");
                        view1.findViewById(R.id.tip).setVisibility(View.VISIBLE);
                        view1.findViewById(R.id.cancle).setVisibility(View.GONE);
                        view1.setDrawingCacheEnabled(true);
                        Bitmap v = view1.getDrawingCache();
                        v.compress(Bitmap.CompressFormat.JPEG, 60, new FileOutputStream(new File(filename)));
                        view1.setDrawingCacheEnabled(false);
                        view1.findViewById(R.id.cancle).setVisibility(View.VISIBLE);
                        //把文件插入到系统图库
                        MediaStore.Images.Media.insertImage(getContentResolver(), filename, PreferenceUtil.getUserId(RedPacket.this) + ".jpg", null);
                        //保存图片后发送广播通知更新数据库
                        Uri uri = Uri.fromFile(new File(filename));
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                        ToastUtil.showToastShort("福包二维码已保存，请到微信中打开扫描");
                    } catch (FileNotFoundException e) {
                        ToastUtil.showToastShort("未找到二维码图片");
                        e.printStackTrace();
                    }
                }
            });


        } else {
            ToastUtil.showToastShort("二维码保存失败");
        }
        ;
    }

    private void getInfo() {
        if (Network.HttpTest(this)) {
            JSONObject js = new JSONObject();
            try {
                js.put("m_id", Constants.M_id);
                js.put("user_id", PreferenceUtil.getUserId(this));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ApisSeUtil.M m = ApisSeUtil.i(js);

            OkGo.post(Constants.RedBack).params("key", m.K())
                    .params("msg", m.M())
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            HashMap<String, String> map = AnalyticalJSON.getHashMap(s);
                            if (map != null) {
                                info = map;
                                if (map.get("start_time") != null && System.currentTimeMillis() < TimeUtils.dataOne(map.get("start_time"))) {
                                    ToastUtil.showToastShort("活动尚未开始");
                                    return;
                                } else if (map.get("end_time") != null && System.currentTimeMillis() > TimeUtils.dataOne(map.get("end_time"))) {
                                    ToastUtil.showToastShort("活动已结束");
                                    setResult(666);
                                    return;
                                }
                                if ("000".equals(map.get("code"))) {
                                    status = "0";
                                    redRainView.startRain();

                                    Glide.with(RedPacket.this).load(map.get("backimg")).asBitmap()
                                            .into(new SimpleTarget<Bitmap>(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels) {
                                                @Override
                                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                                    BitmapDrawable bt = new BitmapDrawable(resource);
                                                    if (Build.VERSION.SDK_INT >= 16) {
                                                        redRainView.setBackground(bt);
                                                    } else {
                                                        redRainView.setBackgroundDrawable(bt);
                                                    }
                                                }
                                            });
                                } else if ("002".equals(map.get("code"))) {//已拆开，未领取
                                    if (map.get("status") != null) {
                                        status = "2";
                                        showGetDialog(info);
                                    }
                                } else if ("003".equals(map.get("code"))) {//已领取

                                    finish();
                                    Intent intent = new Intent(RedPacket.this, RedPacket_Final.class);
                                    startActivity(intent);

                                }

                            }
                        }

                        @Override
                        public void onAfter(String s, Exception e) {
                            super.onAfter(s, e);
                            ProgressUtil.dismiss();
                        }

                        @Override
                        public void onBefore(BaseRequest request) {
                            super.onBefore(request);
                            ProgressUtil.show(RedPacket.this, "", "请稍等");
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            super.onError(call, response, e);
                            getInfo();
                        }
                    });
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        ProgressUtil.dismiss();
        if (mMediaplayer != null && mMediaplayer.isPlaying()) {
            mMediaplayer.stop();
            mMediaplayer.reset();
        }
        if (valueAnimator != null && musicImg != null) {
            valueAnimator.cancel();
            musicImg.setRotation(0);
        }


    }
}
