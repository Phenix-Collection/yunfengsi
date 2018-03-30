package com.yunfengsi.Utils;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.alibaba.sdk.android.push.register.HuaWeiRegister;
import com.alibaba.sdk.android.push.register.MiPushRegister;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;
import com.spreada.utils.chinese.ZHConverter;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareConfig;
import com.yunfengsi.Login;
import com.yunfengsi.R;

import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;

/**
 * Created by Administrator on 2016/9/12.
 */
public class mApplication extends Application {
    private int caheM;
    private static final String TAG = "mApplication";
    private static mApplication application;
    //当前交易的订单id
    public static String sut_id = "";
    public static String id = "";
    public static String title = "";
    public static String type = "";
    //    public   ShareBoardConfig config;//分享面板配置
    private boolean Debug = true;
    public static boolean isChina = true;
    public Login login;

    //    public static String gg_url="",gg_image="";//首页广告弹窗 背景图  ggimage   跳转链接  ggurl
    public static boolean changeIcon = false;//是否切换图标
    public static ComponentName componentName;//入口名称

    public static final String alias1 = "com.yunfengsi.Splash";
    public static final String alias2 = "com.yunfengsi.Splash1";
    public static HashMap<Class, Activity> activityHashMap = new HashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();

        application = this;
        //Glide
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        caheM = maxMemory / 8;
        GlideBuilder gb = new GlideBuilder(this);
        gb.setDecodeFormat(DecodeFormat.PREFER_RGB_565);
        gb.setMemoryCache(new LruResourceCache(caheM));

//        gb.setBitmapPool(new LruBitmapPool(caheM));
        //Glide


        HttpHeaders headers = new HttpHeaders();
        //所有的 header 都 不支持 中文

        HttpParams params = new HttpParams();
        //所有的 params 都 支持 中文


        //必须调用初始化
        OkGo.init(this);
        //以下都不是必须的，根据需要自行选择
        OkGo.getInstance()//

                .debug("OkGo", Level.SEVERE, Debug)
                //是否打开调试
                .setConnectTimeout(OkGo.DEFAULT_MILLISECONDS)               //全局的连接超时时间
                .setReadTimeOut(OkGo.DEFAULT_MILLISECONDS)                  //全局的读取超时时间
                .setWriteTimeOut(OkGo.DEFAULT_MILLISECONDS)                 //全局的写入超时时间
                //.setCookieStore(new MemoryCookieStore())                           //cookie使用内存缓存（app退出后，cookie消失）
//                .setCookieStore(new PersistentCookieStore())                    //cookie持久化存储，如果cookie不过期，则一直有效
                .addCommonHeaders(headers)                                         //设置全局公共头
                .addCommonParams(params);

        UMShareAPI.get(this);//初始化友盟
        PlatformConfig.setWeixin("wxd33fe2dd9a8d2b6b", "5c43f64262abc1e2f0b18434afff7919");
//        PlatformConfig.setWeixin("wx7f8b711548c749fb","6159914840766b002a4542c899c9fba3");//公众号数据  测试
        PlatformConfig.setQQZone("1105643311", "QPle8NDkjehWHPx8");
        PlatformConfig.setSinaWeibo("2018815414", "9bc9e490e67fe21e177b69eed248cb4f", "https://api.weibo.com/oauth2/default.html");//
        PlatformConfig.setTwitter("Z9vhrsa91vvyahIKTDffPxuY7", "w0OsXsQ9N4DUIdfL6uAlFbI5ZdQ1m4MUAHsjMUSVH7mTXm2pl2");//

//        config = new ShareBoardConfig();
//        config.setShareboardPostion(ShareBoardConfig.SHAREBOARD_POSITION_BOTTOM);
//        config.setMenuItemBackgroundShape(ShareBoardConfig.BG_SHAPE_CIRCULAR);
//        config.setCancelButtonVisibility(true);
        Config.DEBUG = Debug;
        com.umeng.socialize.utils.Log.LOG = Debug;
        UMShareConfig config = new UMShareConfig();
        config.isOpenShareEditActivity(true);

        UMShareAPI.get(this).setShareConfig(config);
        isChina = PreferenceUtil.getSettingIncetance(this).getBoolean("isChina", true);

//        Intent intent = new Intent(this, GohnsonService.class);
//        startService(intent);


        //初始化阿里云推送
        initCloudChannel(this);

        // 注册方法会自动判断是否支持小米系统推送，如不支持会跳过注册。
        MiPushRegister.register(this, "2882303761517517038", "5341751749038");
// 注册方法会自动判断是否支持华为系统推送，如不支持会跳过注册。
        HuaWeiRegister.register(this);

//       if(LeakCanary.isInAnalyzerProcess(this)){
//           return;
//       }
//       LeakCanary.install(this);
    }

    public static mApplication getInstance() {
        return application;
    }

    /**
     * 简繁互换
     *
     * @return
     */
    public static String ST(String s) {
        if (isChina) {
            s = ZHConverter.getInstance(ZHConverter.SIMPLIFIED).convert(s);
        } else {
            StringBuilder stringBuilder = new StringBuilder(s);
            if (s.contains("叶")) {
                stringBuilder.replace(s.indexOf("叶"), s.indexOf("叶") + 1, "......");
            }
            stringBuilder.replace(0, stringBuilder.length(), ZHConverter.getInstance(ZHConverter.TRADITIONAL).convert(stringBuilder.toString()));
            if (stringBuilder.toString().contains("......")) {
                stringBuilder.replace(stringBuilder.indexOf("......"), stringBuilder.indexOf("......") + 6, "葉");
            }
            s = stringBuilder.toString();
        }
//        s= ZHConverter.getInstance(isChina?ZHConverter.SIMPLIFIED:ZHConverter.TRADITIONAL).convert(s);
//        s=StUtil.convert(s,isChina?0:1);
//        try {
//            return isChina ? JChineseConvertor.getInstance().t2s(s):JChineseConvertor.getInstance().s2t(s);
//        } catch (Exception e) {
//            e.printStackTrace();
//            LogUtil.e("简繁转换错误");
//        }
        return s;
    }

    public static byte[] PK() {
        return Base64.decode("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDMzj1J20jfuAU3CQDPElbOuASC" +
                "1Qase0eyA1j+bvp64foNnrJ7O5ggM2zJDP3jmEMPrm9BywTIKou30jA0fZh62dRl" +
                "3DslBLJKLlq9xnpecLaawMe0xT3AM54fYMYZdVzKXK8s9OKSYt61V+yDIo+AMgw/" +
                "P60irfotxeRNZNNhHQIDAQAB");
    }

    /**
     * 初始化云推送通道
     *
     * @param applicationContext
     */
    private void initCloudChannel(Context applicationContext) {
        PushServiceFactory.init(applicationContext);
        final CloudPushService pushService = PushServiceFactory.getCloudPushService();
        pushService.register(applicationContext, new CommonCallback() {
            @Override
            public void onSuccess(String response) {
                Log.e(TAG, "初始化阿里云推送成功,设备Id:" + pushService.getDeviceId());
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                Log.e(TAG, "初始化阿里云推送失败 -- errorcode:" + errorCode + " -- errorMessage:" + errorMessage);
            }
        });
    }

    public static void closeAllActivities() {
        Iterator<Activity> iterActivity = activityHashMap.values().iterator();
        while (iterActivity.hasNext()) {
            iterActivity.next().finish();
        }
        activityHashMap.clear();
    }

    public static void addActivity(Activity activity) {
        activityHashMap.put(activity.getClass(), activity);
    }

    public static void romoveActivity(Activity activity) {
        if (activityHashMap.containsValue(activity)) {
            activityHashMap.remove(activity.getClass());
            if (activity != null) {
                activity.finish();
            }

        }
    }


    public static void openPayLayout(final Activity context, final String allmoney, final String attachId, final String title, final String num, final String type, final String extra) {// TODO: 2016/12/20 打开支付窗口


        AlertDialog.Builder b = new AlertDialog.Builder(context);
        final AlertDialog alertDialog = b.create();
        View view = LayoutInflater.from(context).inflate(R.layout.pay_bottom_layout, null);
        Drawable drawable = context.getResources().getDrawable(R.drawable.pay_wc);
        Drawable drawable1 = context.getResources().getDrawable(R.drawable.pay_qq);
        Drawable drawable2 = context.getResources().getDrawable(R.drawable.pay_up);
        Drawable drawable3 = context.getResources().getDrawable(R.drawable.pay_ali);
        drawable.setBounds(0, 0, DimenUtils.dip2px(context, 35), DimenUtils.dip2px(context, 35));
        drawable1.setBounds(0, 0, DimenUtils.dip2px(context, 35), DimenUtils.dip2px(context, 35));
        drawable2.setBounds(0, 0, DimenUtils.dip2px(context, 35), DimenUtils.dip2px(context, 35));
        drawable3.setBounds(0, 0, DimenUtils.dip2px(context, 35), DimenUtils.dip2px(context, 35));
        ((TextView) view.findViewById(R.id.tv_pay_wx)).setCompoundDrawables(drawable, null, null, null);
        ((TextView) view.findViewById(R.id.tv_pay_qq)).setCompoundDrawables(drawable1, null, null, null);
        ((TextView) view.findViewById(R.id.tv_pay_up)).setCompoundDrawables(drawable2, null, null, null);
        ((TextView) view.findViewById(R.id.tv_pay_ali)).setCompoundDrawables(drawable3, null, null, null);
        view.findViewById(R.id.tv_pay_wx).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WXPayUtils.openWXPay(context, allmoney, attachId, title, num, type, extra);
                alertDialog.dismiss();
            }
        });
        view.findViewById(R.id.tv_pay_qq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QpayUtil.openQQPay(context, attachId, title, allmoney, num, type, extra);
                alertDialog.dismiss();
            }
        });
        view.findViewById(R.id.tv_pay_ali).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AliPayUtil.openAliPay(context, allmoney, attachId, title, num, extra, type);
                alertDialog.dismiss();
            }
        });
        view.findViewById(R.id.tv_pay_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Double.valueOf(allmoney) > 5000) {
                    Toast.makeText(context, "银联支付最高限额5000元人民币", Toast.LENGTH_SHORT).show();
                    return;
                }
                ((UpPayUtil) context).allmoney = allmoney;
                ((UpPayUtil) context).type = type;
                ((UpPayUtil) context).num = num;
                ((UpPayUtil) context).shop_id = attachId;
                ((UpPayUtil) context).extra = extra;
                ((UpPayUtil) context).title = title;

                Runnable r = ((UpPayUtil) context).payRunnable;
                new Thread(r).start();
                alertDialog.dismiss();
            }
        });
        alertDialog.setView(view);
        alertDialog.show();
        alertDialog.getWindow().setDimAmount(0.1f);
        alertDialog.getWindow().setWindowAnimations(R.style.dialogWindowAnim);
        alertDialog.getWindow().setGravity(Gravity.BOTTOM);
        alertDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
//        alertDialog.getWindow().getAttributes().width=context.getResources().getDisplayMetrics().widthPixels*94/100;
//        alertDialog.getWindow().getAttributes().height=context.getResources().getDisplayMetrics().heightPixels*6/10;
    }
}
