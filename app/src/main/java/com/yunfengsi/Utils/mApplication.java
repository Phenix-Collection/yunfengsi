package com.yunfengsi.Utils;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Process;
import android.os.StrictMode;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.alibaba.sdk.android.push.register.HuaWeiRegister;
import com.alibaba.sdk.android.push.register.MiPushRegister;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;
import com.spreada.utils.chinese.ZHConverter;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareConfig;
import com.umeng.socialize.shareboard.ShareBoardConfig;
import com.yunfengsi.BuildConfig;
import com.yunfengsi.Deamon.OnePixelActivity;
import com.yunfengsi.Managers.Base.BasePayParams;
import com.yunfengsi.R;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;

/**
 * Created by Administrator on 2016/9/12.
 */
public class mApplication extends Application {
    private static final String TAG = "mApplication";
    private static mApplication application;
    private static mApplication mainApplication;
    //当前交易的订单id
    public static String  sut_id  = "";
    public static String  id      = "";
    public static String  title   = "";
    public static String  type    = "";
    //    public   ShareBoardConfig config;//分享面板配置
    public static boolean Debug   = BuildConfig.DEBUG;
    public static boolean isChina = true;

    //    public static String gg_url="",gg_image="";//首页广告弹窗 背景图  ggimage   跳转链接  ggurl
    public static boolean changeIcon = false;//是否切换图标
    public static ComponentName componentName;//入口名称

    public static final String                                  alias1          = "com.yunfengsi.Setting.Splash";
    public static final String                                  alias2          = "com.yunfengsi.Splash1";
    public              HashMap<Class, WeakReference<Activity>> activityHashMap = new HashMap<>();


    public OnePixelActivity onePixelActivity;//守护activity,存在于主进程中


    @Override
    public void onCreate() {
        super.onCreate();
        String curProcess = SystemUtil.getProcessName(this, Process.myPid());
        LogUtil.e("当前进程：：：" + curProcess + "    包名：：：" + getPackageName());


        if (getPackageName().equals(curProcess)) {//如果当前进程是主进程，才初始化图片和网络，分享框架
            //Glide
            int maxMemory = (int) Runtime.getRuntime().maxMemory();
            int caheM     = maxMemory / 8;
            GlideBuilder gb = new GlideBuilder(this);
            gb.setDecodeFormat(DecodeFormat.PREFER_RGB_565);
            gb.setMemoryCache(new LruResourceCache(caheM));

            gb.setBitmapPool(new LruBitmapPool(caheM));
            //Glide


            HttpHeaders headers = new HttpHeaders();
            //所有的 header 都 不支持 中文

            HttpParams params = new HttpParams();
            //所有的 params 都 支持 中文


            //必须调用初始化
            OkGo.init(this);
            //以下都不是必须的，根据需要自行选择
            OkGo.getInstance()//

                    .debug("OkGo", Debug ? Level.SEVERE : Level.OFF, Debug)
                    //是否打开调试
                    .setConnectTimeout(8000)               //全局的连接超时时间
                    .setReadTimeOut(8000)                  //全局的读取超时时间
                    .setWriteTimeOut(8000)                 //全局的写入超时时间
                    //.setCookieStore(new MemoryCookieStore())                           //cookie使用内存缓存（app退出后，cookie消失）
//                .setCookieStore(new PersistentCookieStore())                    //cookie持久化存储，如果cookie不过期，则一直有效
                    .addCommonHeaders(headers)                                         //设置全局公共头
                    .addCommonParams(params);

            UMConfigure.init(this, "58b7f0b57f2c74437b00047c"
                    , "umeng", UMConfigure.DEVICE_TYPE_PHONE, "");

            PlatformConfig.setWeixin("wxd33fe2dd9a8d2b6b", "5c43f64262abc1e2f0b18434afff7919");
//        PlatformConfig.setWeixin("wx7f8b711548c749fb","6159914840766b002a4542c899c9fba3");//公众号数据  测试
            PlatformConfig.setQQZone("1105643311", "QPle8NDkjehWHPx8");
            PlatformConfig.setSinaWeibo("2018815414", "9bc9e490e67fe21e177b69eed248cb4f", "https://api.weibo.com/oauth2/default.html");//

            ShareBoardConfig config = new ShareBoardConfig();
            config.setShareboardPostion(ShareBoardConfig.SHAREBOARD_POSITION_BOTTOM);
            config.setMenuItemBackgroundShape(ShareBoardConfig.BG_SHAPE_CIRCULAR);
            config.setCancelButtonVisibility(true);
            UMShareConfig config1 = new UMShareConfig();
            config1.isOpenShareEditActivity(true);

            UMShareAPI.get(this).setShareConfig(config1);


            isChina = PreferenceUtil.getSettingIncetance(this).getBoolean("isChina", true);

            mainApplication = this;
        }

        /**
         *
         * 以下为守护进程重新实例化时需要重新初始化的代码
         *
         * 推送
         */
        application = this;


        //初始化阿里云推送
        initCloudChannel(this);

        // 注册方法会自动判断是否支持小米系统推送，如不支持会跳过注册。
        MiPushRegister.register(this, "2882303761517517038", "5341751749038");
        // 注册方法会自动判断是否支持华为系统推送，如不支持会跳过注册。
        HuaWeiRegister.register(this);


        if (Build.VERSION.SDK_INT > 23) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            builder.detectFileUriExposure();
        }


//        if(LeakCanary.isInAnalyzerProcess(this)&&!Debug){
//            return;
//        }
//        LeakCanary.install(this);
    }

    /**
     * 获取当前进程对象
     *
     * @return
     */
    public static mApplication getInstance() {
        return application;
    }

    /**
     * 获取主进程对象
     *
     * @return
     */
    public static mApplication getMainInstance() {
        return mainApplication;
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

    public void closeAllActivities() {
        Iterator<WeakReference<Activity>> iterActivity = activityHashMap.values().iterator();
        while (iterActivity.hasNext()) {
            iterActivity.next().get().finish();
        }
        activityHashMap.clear();
    }

    public void addActivity(Activity activity) {
        LogUtil.e("添加页面：；" + activity.getClass().getName());
        activityHashMap.put(activity.getClass(), new WeakReference<Activity>(activity));
    }

    public void romoveActivity(Activity activity) {
        if (activityHashMap.containsKey(activity.getClass())) {
            LogUtil.e("移除页面：；" + activity.getClass().getName());
            activityHashMap.remove(activity.getClass());
            if (activity != null) {
                LogUtil.e("销毁页面：；" + activity.getClass().getName());
                activity.finish();
            }

        }
    }

    public static View getEmptyView(Context context, int marginTopDp, String tip) {
        TextView textView = new TextView(context);
        Drawable d        = ContextCompat.getDrawable(context, R.drawable.load_nothing);
        d.setBounds(0, 0, DimenUtils.dip2px(context, 150), DimenUtils.dip2px(context, 150) * d.getIntrinsicHeight() / d.getIntrinsicWidth());
        textView.setCompoundDrawables(null, d, null, null);
        textView.setCompoundDrawablePadding(DimenUtils.dip2px(context, 5));
        textView.setText(mApplication.ST(tip));


        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        ViewGroup.LayoutParams vl = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        textView.setLayoutParams(vl);
        textView.setPadding(0, DimenUtils.dip2px(context, marginTopDp), 0, 0);
        return textView;
    }

    /**
     * //     * @param context
     * //     * @param allmoney  总支付费用
     * //     * @param attachId  支付商品的id  多个用，链接
     * //     * @param title  支付标题   多个用，链接
     * //     * @param num    购买数量  默认1
     * //     * @param type   支付type  1视频    2直播 3 寺庙 4供养 5助学 6 书城 7点餐  8预约 9业务购买 11 拼团12 约参与费用 13义卖支付  14 活动快速通道
     * //     * @param extra  供养使用  extra   如果有地址信息  则为地址id
     */
    public static void openPayLayout(final Activity context, final BasePayParams payParams) {// TODO: 2016/12/20 打开支付窗口


        AlertDialog.Builder b           = new AlertDialog.Builder(context);
        final AlertDialog   alertDialog = b.create();
        View                view        = LayoutInflater.from(context).inflate(R.layout.pay_bottom_layout, null);
        Drawable            drawable    = context.getResources().getDrawable(R.drawable.pay_wc);
        Drawable            drawable1   = context.getResources().getDrawable(R.drawable.pay_qq);
        Drawable            drawable2   = context.getResources().getDrawable(R.drawable.pay_up);
        Drawable            drawable3   = context.getResources().getDrawable(R.drawable.pay_ali);
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
                WXPayUtils.openWXPay(context, payParams);
                alertDialog.dismiss();
            }
        });
//        view.findViewById(R.id.tv_pay_qq).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                QpayUtil.openQQPay(context, attachId, title, allmoney, num, type, extra);
//                alertDialog.dismiss();
//            }
//        });
        view.findViewById(R.id.tv_pay_ali).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AliPayUtil.openAliPay(context, payParams);
                alertDialog.dismiss();
            }
        });
//        view.findViewById(R.id.tv_pay_up).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (Double.valueOf(allmoney) > 5000) {
//                    Toast.makeText(context, "银联支付最高限额5000元人民币", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                ((UpPayUtil) context).allmoney = allmoney;
//                ((UpPayUtil) context).type = type;
//                ((UpPayUtil) context).num = num;
//                ((UpPayUtil) context).shop_id = attachId;
//                ((UpPayUtil) context).extra = extra;
//                ((UpPayUtil) context).title = title;
//
//                Runnable r = ((UpPayUtil) context).payRunnable;
//                new Thread(r).start();
//                alertDialog.dismiss();
//            }
//        });
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
