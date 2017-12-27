package com.qianfujiaoyu.Model_Order;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.zxing.Result;
import com.lzy.okgo.OkGo;
import com.qianfujiaoyu.Activitys.ViewPagerActivity;
import com.qianfujiaoyu.Base.ScaleImageUtil;
import com.qianfujiaoyu.R;
import com.qianfujiaoyu.Utils.AnalyticalJSON;
import com.qianfujiaoyu.Utils.ApisSeUtil;
import com.qianfujiaoyu.Utils.Constants;
import com.qianfujiaoyu.Utils.DimenUtils;
import com.qianfujiaoyu.Utils.ImageUtil;
import com.qianfujiaoyu.Utils.LogUtil;
import com.qianfujiaoyu.Utils.LoginUtil;
import com.qianfujiaoyu.Utils.Network;
import com.qianfujiaoyu.Utils.PreferenceUtil;
import com.qianfujiaoyu.Utils.ProgressUtil;
import com.qianfujiaoyu.Utils.QrUtils;
import com.qianfujiaoyu.Utils.ShareManager;
import com.qianfujiaoyu.Utils.StatusBarCompat;
import com.qianfujiaoyu.Utils.mApplication;
import com.qianfujiaoyu.View.myWebView;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/11/30.
 */
public class Order_detail extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Dishesd";
    private Banner banner;
    private LinearLayout car, root;
    private ImageView fenxiang;
    private ImageView back, shoucang, car_img, dianzan, dianzan_cai;
    private TextView title_title, title, type, sales, money, cost, car_text, msg, percent, dianzan_text, hate_text;
    private ProgressBar progress, progressBar_h;
    private String id;
    private ArrayList<String> list;
    private boolean like = false;
    private boolean hate = false;
    private myWebView webView;
    private ArrayList<String  > arrayList;
    private HashMap<String,String > map;
    private UMWeb umWeb;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.order_detail);
        root = (LinearLayout) findViewById(R.id.order_detail_root);
        root.setVisibility(View.INVISIBLE);
        id = getIntent().getStringExtra("id");
        initView();
        getData();

    }

    @Override
    protected void onPause() {
        super.onPause();
        ProgressUtil.dismiss();
    }

    /**
     * 获取详情
     */
    private void getData() {
        if (!Network.HttpTest(this)) {
            return;
        }
        ProgressUtil.show(this, "", "正在加载数据");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject js=new JSONObject();
                    try {
                        js.put("order_id", id);
                        js.put("m_id",Constants.M_id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m=ApisSeUtil.i(js);
                    String data = OkGo.post(Constants.Order_detail).tag(TAG)
                            .params("key",m.K())
                            .params("msg",m.M()).execute().body().string();
                    if (!TextUtils.isEmpty(data)) {
                         map = AnalyticalJSON.getHashMap(data);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (map != null) {
                                    setImages(map);
                                    umWeb = new UMWeb(Constants.FX_host_Ip + TAG + "/id/"  + getIntent().getStringExtra("id"));
                                    umWeb.setTitle(mApplication.ST(map.get("title")));
                                    umWeb.setThumb(new UMImage(Order_detail.this, map.get("image1")));
                                    if (!map.get("abstract").equals("")) {
                                        umWeb.setDescription(mApplication.ST(map.get("abstract")));
                                    } else {
                                        umWeb.setDescription(mApplication.ST("销量:"+map.get("sales")+"\n"+"价格:"+map.get("money")));
                                    }
                                    title.setText(map.get("title"));
                                    type.setText(map.get("name"));
                                    sales.setText("总销量" + map.get("sales"));
                                    msg.setText(map.get("abstract"));
                                    money.setText("¥ " + map.get("money"));
                                    cost.setText("¥ " + map.get("cost"));
                                    cost.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                                    webView.loadDataWithBaseURL("", map.get("contents")
                                            , "text/html", "UTF-8", null);
                                    int d1 = 0, d2 = 0;
                                    if (null != map.get("likes")) {
                                        dianzan_text.setText(map.get("likes"));
                                        d1 = Integer.valueOf(map.get("likes"));
                                    } else {
                                        dianzan_text.setText("0");
                                    }
                                    if (null != map.get("tread")) {
                                        hate_text.setText(map.get("tread"));
                                        d2 = Integer.valueOf(map.get("tread"));
                                    } else {
                                        hate_text.setText("0");
                                    }
                                    if ((d1 + d2) != 0) {
                                        progressBar_h.setMax(100);
                                        percent.setText(d1 * 100 / (d1 + d2) + "%");
                                        final int p=d1 * 100 / (d1 + d2);
                                        ValueAnimator value = ValueAnimator.ofInt(0,p);
                                        value.setDuration(1500);
                                        value.setInterpolator(new AnticipateOvershootInterpolator());
                                        ValueAnimator.AnimatorUpdateListener an = new ValueAnimator.AnimatorUpdateListener() {
                                            @Override
                                            public void onAnimationUpdate(ValueAnimator animation) {
                                                progressBar_h.setProgress((Integer) animation.getAnimatedValue());

                                            }
                                        };
                                        value.addUpdateListener(an);
                                        value.start();
                                    }
                                    ProgressUtil.dismiss();
                                    root.setVisibility(View.VISIBLE);
                                } else {
                                    ProgressUtil.dismiss();
                                    Toast.makeText(Order_detail.this, "系统繁忙，请稍后重试", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ProgressUtil.dismiss();
                            Toast.makeText(Order_detail.this, "系统繁忙，请稍后重试", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void setImages(HashMap<String, String> map) {
        if (!map.get("image1").equals("")) {

            list.add(map.get("image1"));
        }
        if (!map.get("image2").equals("")) {

            list.add(map.get("image2"));
        }
        if (!map.get("image3").equals("")) {

            list.add(map.get("image3"));
        }
        banner.setImages(list);
        banner.start();
    }

    private void initView() {
        list = new ArrayList<>();
        back = (ImageView) findViewById(R.id.title_back);
        back.setVisibility(View.VISIBLE);
        back.setImageBitmap(ImageUtil.readBitMap(this, R.drawable.back));

        title_title = (TextView) findViewById(R.id.title_title);
        title_title.setText("商品详情");

        shoucang = (ImageView) findViewById(R.id.title_image2);
        shoucang.setVisibility(View.VISIBLE);
        shoucang.setOnClickListener(this);

        fenxiang = (ImageView) findViewById(R.id.title_image3);
        fenxiang.setOnClickListener(this);
        banner = (Banner) findViewById(R.id.banner);
        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                ScaleImageUtil.openBigIagmeMode(Order_detail.this,list,position);
            }
        });
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        banner.setDelayTime(3000);
        banner.setImageLoader(new ImageLoader() {
            @Override
            public void displayImage(Context context, Object path, ImageView imageView) {
                Glide.with(context).load(path).override(getResources().getDisplayMetrics().widthPixels, DimenUtils.dip2px(context,240))
                        .centerCrop().into(imageView);
            }
        });
        banner.setImages(list);
        car = (LinearLayout) findViewById(R.id.order_detail_car);
        car_img = (ImageView) findViewById(R.id.order_detail_car_img);
        car_text = (TextView) findViewById(R.id.order_detail_car_text);
        progress = (ProgressBar) findViewById(R.id.order_detail_progress);
        car.setOnClickListener(this);

        title = (TextView) findViewById(R.id.order_detail_title);
        type = (TextView) findViewById(R.id.order_detail_type);
        sales = (TextView) findViewById(R.id.order_detail_sales);
        money = (TextView) findViewById(R.id.order_detail_true_money);
        cost = (TextView) findViewById(R.id.order_detail_false_money);

        msg = (TextView) findViewById(R.id.order_detail_msg);
        percent = (TextView) findViewById(R.id.order_detail_percent);
        dianzan = (ImageView) findViewById(R.id.order_detail_like_img);
        dianzan.setOnClickListener(this);
        dianzan_text = (TextView) findViewById(R.id.order_detail_like_text);
        dianzan_cai = (ImageView) findViewById(R.id.order_detail_hate_img);
        dianzan_cai.setRotation(180);
        dianzan_cai.setOnClickListener(this);
        hate_text = (TextView) findViewById(R.id.order_detail_hate_text);
        tintDrawable(dianzan_cai, R.drawable.dianzan, R.color.huise);
        tintDrawable(dianzan, R.drawable.dianzan, R.color.huise);
        progressBar_h = (ProgressBar) findViewById(R.id.order_detail_progress_H);
        if(getIntent().getBooleanExtra("flag",false)){
            car_img.setVisibility(View.GONE);
            car_text.setText("已放入购物车");
            car.setEnabled(false);
        }
        webView = (myWebView) findViewById(R.id.order_web);

        WebSettings webSettings = webView.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setAppCacheEnabled(true);
        if (Build.VERSION.SDK_INT >= 19) {
            webSettings.setLoadsImagesAutomatically(true);
        } else {
            webSettings.setLoadsImagesAutomatically(false);
        }
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (!view.getSettings().getLoadsImagesAutomatically()) {
                    view.getSettings().setLoadsImagesAutomatically(true);
                }
                imgReset();
                addImageClickListner();


            }

        });
        JavascriptInterface js = new JavascriptInterface(this);
        webView.addJavascriptInterface(js, "addUrl");
        webView.addJavascriptInterface(js, "imagelistener");
        webView.setOnLongClickListener(new myWebView.onLongClickListener() {
            @Override
            public void onLongClcik(String imgUrl) {
                Glide.with(Order_detail.this).load(imgUrl).asBitmap().skipMemoryCache(true).override(400, 400).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        Result result = QrUtils.handleQRCodeFormBitmap(resource);
                        if (result == null) {
                            LogUtil.w("onResourceReady: 不是二维码   " + result);
                        } else {
                            LogUtil.w("onResourceReady: 是二维码   " + result);
                            if (result.getText().toString().startsWith("http")) {
                                Uri uri = Uri.parse(result.getText().toString());
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(uri);
                                startActivity(intent);
                            } else {
                                Toast.makeText(Order_detail.this, "无法识别,请确认当前页面是否有二维码图片", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
            }
        });

    }

    private void tintDrawable(ImageView img, int imgRes, int resId) {
        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), imgRes);
        Drawable.ConstantState state = drawable.getConstantState();
        Drawable drawable1 = DrawableCompat.wrap(state == null ? drawable : state.newDrawable().mutate());
        drawable1.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        DrawableCompat.setTint(drawable, ContextCompat.getColor(getApplicationContext(), resId));
        img.setImageDrawable(drawable);
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.title_image3:
                if(umWeb!=null){
                    new ShareManager().shareWeb(umWeb,this);
                }
                break;
            case R.id.title_image2:
                if (!new LoginUtil().checkLogin(this)) {
                    return;
                }
                if (!Network.HttpTest(this)) {
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject js=new JSONObject();
                            try {
                                js.put("order_id", id);
                                js.put("m_id",Constants.M_id);
                                js.put("user_id", PreferenceUtil.getUserIncetance(mApplication.getInstance()).getString("user_id",""));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ApisSeUtil.M m=ApisSeUtil.i(js);
                            String data = OkGo.post(Constants.Order_shoucang)
                                    .params("key",m.K())
                                    .params("msg",m.M()).execute().body().string();
                            if (!data.equals("")) {
                                if (AnalyticalJSON.getHashMap(data) != null && "000".equals(AnalyticalJSON.getHashMap(data).get("code"))) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(Order_detail.this, "添加收藏成功", Toast.LENGTH_SHORT).show();
                                            v.setSelected(true);


                                        }
                                    });
                                } else if (AnalyticalJSON.getHashMap(data) != null && "002".equals(AnalyticalJSON.getHashMap(data).get("code"))) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(Order_detail.this, "已取消收藏", Toast.LENGTH_SHORT).show();
                                            v.setSelected(false);


                                        }
                                    });
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(Order_detail.this, "服务器异常", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            case R.id.order_detail_car://加入购物车

                ArrayList<HashMap<String,String>> l=new ArrayList<HashMap<String, String>>();
                l.add(map);
                Intent intent=new Intent(this,Dingdan_commit.class);
                intent.putExtra("list",l);
                startActivity(intent);
//                if (Network.HttpTest(this)) {
//                    if (new LoginUtil().checkLogin(this)) {
//                        progress.setVisibility(View.VISIBLE);
//                        v.setEnabled(false);
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//                                    String data = OkGo.post(Constants.Order_add_car).tag("main").params("key", Constants.safeKey)
//                                            .params("user_id", PreferenceUtil.getUserIncetance(Order_detail.this).getString("user_id", "")).params("order_id", id)
//                                            .params("type", "1")
//                                            .params("limited", "0")
//                                            .params("m_id", Constants.M_id).execute().body().string();
//                                    if (!TextUtils.isEmpty(data)) {
//                                        final HashMap<String, String> map = AnalyticalJSON.getHashMap(data);
//
//                                        runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                if (map != null) {
//                                                    if ("000".equals(map.get("code")) || "002".equals(map.get("code"))) {
//                                                        progress.setVisibility(View.GONE);
//                                                        car_img.setVisibility(View.GONE);
//                                                        car_text.setText("已放入购物车");
//                                                        Intent intent=new Intent("car");
//                                                        sendBroadcast(intent);
//                                                    }
//                                                } else {
//                                                    progress.setVisibility(View.GONE);
//                                                    v.setEnabled(true);
//                                                    Toast.makeText(Order_detail.this, "系统繁忙，请稍后重试", Toast.LENGTH_SHORT).show();
//                                                }
//                                            }
//                                        });
//
//                                    } else {
//
//                                        runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                progress.setVisibility(View.GONE);
//                                                v.setEnabled(true);
//                                                Toast.makeText(Order_detail.this, "系统繁忙，请稍后重试", Toast.LENGTH_SHORT).show();
//                                            }
//                                        });
//
//                                    }
//                                } catch (Exception e) {
//
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            progress.setVisibility(View.GONE);
//                                            v.setEnabled(true);
//                                        }
//                                    });
//
//                                }
//                            }
//                        }).start();
//                    }
//                }
                break;
            case R.id.order_detail_like_img:
                if (!like && !hate) {
                    dianzan.setEnabled(false);
                    if (!new LoginUtil().checkLogin(Order_detail.this)) {
                        return;
                    } else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject js=new JSONObject();
                                    try {
                                        js.put("type", "1");
                                        js.put("order_id", id);
                                        js.put("m_id",Constants.M_id);
                                        js.put("user_id", PreferenceUtil.getUserIncetance(mApplication.getInstance()).getString("user_id",""));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    ApisSeUtil.M m=ApisSeUtil.i(js);
                                    String data = OkGo.post(Constants.Order_like)
                                            .params("key",m.K())
                                            .params("msg",m.M()).execute().body().string();
                                    if (!data.equals("")) {
                                        HashMap<String, String> map = AnalyticalJSON.getHashMap(data);
                                        if ((map != null &&("000").equals(map.get("code")))
                                               ) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    tintDrawable(dianzan, R.drawable.dianzan, R.color.main_color);
                                                    dianzan_text.setText((Integer.valueOf(dianzan_text.getText().toString())+1)+"");
                                                    dianzan_text.setTextColor(getResources().getColor(R.color.main_color));
                                                    int d1=Integer.valueOf(dianzan_text.getText().toString());
                                                    int d2=Integer.valueOf(hate_text.getText().toString());
                                                    percent.setText(((d1*100)/(d1+d2))+"%");
                                                    like=true;
                                                }
                                            });

                                        }else if((map!=null&&"002".equals(map.get("code")))){
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(Order_detail.this, "您已对该商品评价过了", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }else{
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                dianzan.setEnabled(true);
                                                Toast.makeText(Order_detail.this, "系统繁忙，请稍后重试", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                } catch (Exception e) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            dianzan.setEnabled(true);
                                            Toast.makeText(Order_detail.this, "系统繁忙，请稍后重试", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    e.printStackTrace();
                                }

                            }
                        }).start();
                    }
                }
                break;
            case R.id.order_detail_hate_img:
                if (!like && !hate) {
                    dianzan_cai.setEnabled(false);
                    if (!new LoginUtil().checkLogin(Order_detail.this)) {
                        return;
                    } else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject js=new JSONObject();
                                    try {
                                        js.put("type", "2");
                                        js.put("order_id", id);
                                        js.put("m_id",Constants.M_id);
                                        js.put("user_id", PreferenceUtil.getUserIncetance(mApplication.getInstance()).getString("user_id",""));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    ApisSeUtil.M m=ApisSeUtil.i(js);
                                    String data = OkGo.post(Constants.Order_like)
                                            .params("key",m.K())
                                            .params("msg",m.M()).execute().body().string();
                                    if (!data.equals("")) {
                                        HashMap<String, String> map = AnalyticalJSON.getHashMap(data);
                                        if ((map != null &&("000").equals(map.get("code")))
                                                ) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    tintDrawable(dianzan_cai, R.drawable.dianzan, R.color.main_color);
                                                    hate_text.setText((Integer.valueOf(hate_text.getText().toString())+1)+"");
                                                    hate_text.setTextColor(getResources().getColor(R.color.main_color));
                                                    int d1=Integer.valueOf(dianzan_text.getText().toString());
                                                    int d2=Integer.valueOf(hate_text.getText().toString());
                                                    percent.setText(((d1*100)/(d1+d2))+"%");
                                                    hate=true;
                                                }
                                            });

                                        }else if((map!=null&&"002".equals(map.get("code")))){
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(Order_detail.this, "您已对该商品评价过了", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                        }
                                    }else{
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                dianzan_cai.setEnabled(true);
                                                Toast.makeText(Order_detail.this, "系统繁忙，请稍后重试", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                } catch (Exception e) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            dianzan_cai.setEnabled(true);
                                            Toast.makeText(Order_detail.this, "系统繁忙，请稍后重试", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    e.printStackTrace();
                                }

                            }
                        }).start();
                    }
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(TAG);
    }

    public class JavascriptInterface {
        private Context context;

        public JavascriptInterface(Context context) {
            this.context = context;
        }

        @android.webkit.JavascriptInterface
        public void openImage(String img) {
            if (arrayList != null) {
                Intent intent = new Intent();
                intent.putExtra("array", arrayList);
                intent.putExtra("position", arrayList.indexOf(img));
                intent.setClass(context, ViewPagerActivity.class);
                context.startActivity(intent);
                LogUtil.w("openImage: 网页图片地址" + img + "页码：" + arrayList.indexOf(img));
            }

        }

        @android.webkit.JavascriptInterface
        public void addUrlToList(String img) {
            if (arrayList == null) {
                arrayList = new ArrayList<String>();
            }
            arrayList.add(img);
        }
    }


    /**
     * 添加图片监听
     */
    private void addImageClickListner() {
        //
        webView.loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName('img'); " +
                "for(var i=0;i<objs.length;i++)  " +
                "{window.addUrl.addUrlToList(objs[i].src);"
                + "    objs[i].onclick=function()  " +
                "    {  "
                + "        window.imagelistener.openImage(this.src);  " +
                "    }  " +
                "}" +
                "})()");
    }
    private void imgReset() {
        webView.loadUrl("javascript:(function(){" +
                "var table=document.getElementsByTagName('table');" +
                "for(var i=0;i<table.length;i++){" +
                "var t=table[i];" +
                "t.style.width='100%';" +
                "t.style.margin='auto';" +
                "t.style.display='block';" +
                "}" +
                "var objs = document.getElementsByTagName('img'); " +
                "for(var i=0;i<objs.length;i++)  " +
                "{"
                + "var img = objs[i];   " +
                "img.style.maxWidth = '100%'; " +
                "var w=img.style.width;" +
                "if(w > '50%') {" +
                "img.style.width='100%';}" +
                "img.style.height = 'auto'; " +
                "img.style.marginBottom=10;" +
                "img.style.marginTop=10;" +
                "img.style.marginLeft='auto';" +
                "img.style.marginRight='auto';" +
                "img.style.display='block';" +
                "}" +
                "var obj1=document.getElementsByTagName('section');" +
                "for(var i=0;i<obj1.length;i++)  " +
                "{"
                + "var sec = obj1[i];  " +
                "sec.style.maxWidth = '100%'; " +
                "var w1=sec.style.width;" +
                "if(w1>'50%'){" +
                "w1='100%';" +
                "}" +
                "sec.style.height = 'auto';" +
                "}" +
                "})()"
        );
    }
}
