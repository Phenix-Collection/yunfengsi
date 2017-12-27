package com.qianfujiaoyu.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.app.ActivityCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cookie.store.MemoryCookieStore;
import com.qianfujiaoyu.Model_Order.Dingdan_commit;
import com.qianfujiaoyu.R;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareConfig;
import com.umeng.socialize.shareboard.ShareBoardConfig;

import java.util.ArrayList;
import java.util.logging.Level;

/**
 * Created by Administrator on 2016/9/12.
 */
public class mApplication extends Application {
    private int caheM;
    private static final String TAG = "mApplication";
    private static  mApplication application;
    //当前交易的订单id
    public static  String sut_id="";
    public static  String type="";
    public ShareBoardConfig config;//分享面板配置
    private boolean Debug=true;
    public static  boolean isChina=true;

    public static  int  city=1;
    public static  String citys="";
    public  static Dingdan_commit dingdan_commit;
    public  static ArrayList<String >  FangWu=new ArrayList<>();
    public  static ArrayList<String >  ZiXun=new ArrayList<>();
//    public  Login login;
    @Override
    public void onCreate() {
        super.onCreate();

        application=this;
        //Glide
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        caheM = maxMemory / 4;
        GlideBuilder gb = new GlideBuilder(this);
        gb.setDecodeFormat(DecodeFormat.PREFER_RGB_565);
        gb.setMemoryCache(new LruResourceCache(caheM));
        gb.setBitmapPool(new LruBitmapPool(caheM));
        //Glide


        //必须调用初始化
        OkGo.init(this);
        //以下都不是必须的，根据需要自行选择
        OkGo.getInstance()//
                .debug("OkGo", Level.SEVERE, Debug)                                     //是否打开调试
                .setConnectTimeout(10000)               //全局的连接超时时间
                .setReadTimeOut(10000)                  //全局的读取超时时间
                .setWriteTimeOut(10000)                 //全局的写入超时时间
                .setCookieStore(new MemoryCookieStore()) ;                          //cookie使用内存缓存（app退出后，cookie消失）
//                .setCookieStore(new PersistentCookieStore())  ;                  //cookie持久化存储，如果cookie不过期，则一直有效
//                .addCommonHeaders(headers)                                         //设置全局公共头
//                .addCommonParams(params);

        UMShareAPI.get(this);//初始化友盟
        PlatformConfig.setWeixin("wx4cad8315141b7b59","b54de31ae18a2a2fb518e24166af8f10");
        PlatformConfig.setQQZone("1106057837", "RKgtIFooMvgC1oC5");
//        PlatformConfig.setSinaWeibo("2018815414", "9bc9e490e67fe21e177b69eed248cb4f","https://api.weibo.com/oauth2/default.html");//
//        PlatformConfig.setTwitter("EDADcB0VXwKvZIgo4eFBlsv0y","mFi70hgH3gQ9fw61ArxKgPzB2omSBJie8HozXlvvQUECTnlrbq");

//        config = new ShareBoardConfig();
//        config.setShareboardPostion(ShareBoardConfig.SHAREBOARD_POSITION_BOTTOM);
//        config.setMenuItemBackgroundShape(ShareBoardConfig.BG_SHAPE_CIRCULAR);
//        config.setCancelButtonVisibility(true);
        Config.DEBUG = Debug;
        Config.isJumptoAppStore = true;
        com.umeng.socialize.utils.Log.LOG=Debug;
        UMShareConfig config = new UMShareConfig();
        config.isOpenShareEditActivity(true);

        UMShareAPI.get(this).setShareConfig(config);
//
//        //初始化阿里云推送
//        initCloudChannel(this);

//       if(LeakCanary.isInAnalyzerProcess(this)&&!Debug){
//           return;
//       }
//       LeakCanary.install(this);
    }

    public static View getLoadNothing(int resId,String msg,int topDp){
        TextView loadNothing = new TextView(mApplication.getInstance());
        loadNothing.setText(mApplication.ST(msg));
        loadNothing.setTextColor(Color.BLACK);
        loadNothing.setTextSize(16);
        Drawable drawable = ActivityCompat.getDrawable(mApplication.getInstance(), resId);
        drawable.setBounds(0, 0, DimenUtils.dip2px(mApplication.getInstance(), 120), DimenUtils.dip2px(mApplication.getInstance(), 120));
        loadNothing.setCompoundDrawables(null, drawable, null, null);
        loadNothing.setGravity(Gravity.CENTER_HORIZONTAL);
        loadNothing.setPadding(0, DimenUtils.dip2px(mApplication.getInstance(), topDp), 0, 0);
        ViewGroup.LayoutParams vl = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadNothing.setLayoutParams(vl);
        return loadNothing;
    }

    public static byte[] PK(){
        return Base64.decode("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDMzj1J20jfuAU3CQDPElbOuASC" +
                "1Qase0eyA1j+bvp64foNnrJ7O5ggM2zJDP3jmEMPrm9BywTIKou30jA0fZh62dRl" +
                "3DslBLJKLlq9xnpecLaawMe0xT3AM54fYMYZdVzKXK8s9OKSYt61V+yDIo+AMgw/" +
                "P60irfotxeRNZNNhHQIDAQAB");
    }
    public static mApplication getInstance() {
        return application;
    }
    /**
     * 简繁互换
     *
     * @return
     */

    public static String ST(String s){
//        try {
//            return isChina ? JChineseConvertor.getInstance().t2s(s):JChineseConvertor.getInstance().s2t(s);
//        } catch (Exception e) {
//            e.printStackTrace();
//            LogUtil.e("简繁转换错误");
//        }
        return  s;
    }
//    public  ShareBoardConfig getShareBoardConfig(){
//        return config;
//    }
//    /**
//     * 初始化云推送通道
//     * @param applicationContext
//     */
//    private void initCloudChannel(Context applicationContext) {
//        PushServiceFactory.init(applicationContext);
//        final CloudPushService pushService = PushServiceFactory.getCloudPushService();
//        pushService.register(applicationContext, new CommonCallback() {
//            @Override
//            public void onSuccess(String response) {
//                Log.e(TAG, "初始化阿里云推送成功,设备Id:"+pushService.getDeviceId());
//            }
//            @Override
//            public void onFailed(String errorCode, String errorMessage) {
//                Log.e(TAG, "初始化阿里云推送失败 -- errorcode:" + errorCode + " -- errorMessage:" + errorMessage);
//            }
//        });
//    }
//
//
    public static void openPayLayout(final Activity context, final String allmoney, final String attachId, final String title, final String num,final String address, final String type, final String extra) {// TODO: 2016/12/20 打开支付窗口
        AlertDialog.Builder b=new AlertDialog.Builder(context);
        final AlertDialog alertDialog=b.create();
        View view= LayoutInflater.from(context).inflate(R.layout.pay_bottom_layout,null);
        Drawable drawable=context.getResources().getDrawable(R.drawable.pay_wc);
//        Drawable drawable1=context.getResources().getDrawable(R.drawable.pay_qq);
//        Drawable drawable2=context.getResources().getDrawable(R.drawable.pay_up);
        Drawable drawable3 = context.getResources().getDrawable(R.drawable.pay_ali);
        drawable.setBounds(0,0,DimenUtils.dip2px(context,35),DimenUtils.dip2px(context,35));
//        drawable1.setBounds(0,0,DimenUtils.dip2px(context,35),DimenUtils.dip2px(context,35));
//        drawable2.setBounds(0,0,DimenUtils.dip2px(context,35),DimenUtils.dip2px(context,35));
        drawable3.setBounds(0,0,DimenUtils.dip2px(context,35),DimenUtils.dip2px(context,35));
        ((TextView) view.findViewById(R.id.tv_pay_wx)).setCompoundDrawables(drawable,null,null,null);
//        ((TextView) view.findViewById(R.id.tv_pay_qq)).setCompoundDrawables(drawable1,null,null,null);
//        ((TextView) view.findViewById(R.id.tv_pay_up)).setCompoundDrawables(drawable2,null,null,null);
        ((TextView) view.findViewById(R.id.tv_pay_ali)).setCompoundDrawables(drawable3, null, null, null);
        view.findViewById(R.id.tv_pay_wx).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WXPayUtils.openWXPay(context,allmoney,attachId,title,num,type,extra);
                alertDialog.dismiss();
            }
        });
//        view.findViewById(R.id.tv_pay_qq).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                QpayUtil.openQQPay(context,attachId,title,allmoney,num,type,extra);
//                alertDialog.dismiss();
//            }
//        });
        view.findViewById(R.id.tv_pay_ali).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AliPayUtil.openAliPay(context,allmoney, attachId, title, num,type, extra);
                alertDialog.dismiss();
            }
        });
//        view.findViewById(R.id.tv_pay_up).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(Double.valueOf(allmoney)>5000){
//                    Toast.makeText(context, "银联支付最高限额5000元人民币", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                ((UpPayUtil) context).allmoney=allmoney;
//                ((UpPayUtil) context).type=type;
//                ((UpPayUtil) context).num=num;
//                ((UpPayUtil) context).shop_id=attachId;
//                ((UpPayUtil) context).extra=extra;
//                ((UpPayUtil) context).title=title;
//
//                Runnable r=((UpPayUtil) context).payRunnable;
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
        WindowManager.LayoutParams wl=alertDialog.getWindow().getAttributes();
        wl.width=context.getResources().getDisplayMetrics().widthPixels;
        wl.height=WindowManager.LayoutParams.WRAP_CONTENT;
        alertDialog.getWindow().setAttributes(wl);
//        alertDialog.getWindow().getAttributes().width=context.getResources().getDisplayMetrics().widthPixels*94/100;
//        alertDialog.getWindow().getAttributes().height=context.getResources().getDisplayMetrics().heightPixels*6/10;
    }
}
