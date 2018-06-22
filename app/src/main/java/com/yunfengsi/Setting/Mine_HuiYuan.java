package com.yunfengsi.Setting;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.zxing.Result;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.request.BaseRequest;
import com.yunfengsi.Managers.Base.BaseSTActivity;
import com.yunfengsi.Models.ZiXun_Detail;
import com.yunfengsi.R;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.ImageUtil;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.ProgressUtil;
import com.yunfengsi.Utils.QrUtils;
import com.yunfengsi.Utils.ScaleImageUtil;
import com.yunfengsi.Utils.mApplication;
import com.yunfengsi.View.myWebView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/5/10.
 */

public class Mine_HuiYuan extends BaseSTActivity implements View.OnClickListener {
    private TextView title, t1, chengzhang;
    private TextView score, score_time, score_now, score_next, score_small;
    private ImageView gif_now, gif_next, head;
    private ProgressBar progressBar;
    private myWebView content;
    private int next = 0;
    private int level = 0;
    private ArrayList<String> arrayList;

    @Override
    protected void resetData() {
        title.setText(mApplication.ST("会员中心"));
        t1.setText(mApplication.ST("您当前爱心值为"));
        chengzhang.setText(mApplication.ST("成长记录"));
        Drawable d = ContextCompat.getDrawable(this, R.drawable.chengzhang);
        d.setBounds(0, 0, DimenUtils.dip2px(this, 20), DimenUtils.dip2px(this, 20));
        chengzhang.setCompoundDrawables(d, null, null, null);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.heart_circle);
        initView();
        resetData();
        getData();
    }

    private void getData() {
        JSONObject js = new JSONObject();
        try {
            js.put("user_id", PreferenceUtil.getUserIncetance(this).getString("user_id", ""));
            js.put("m_id", Constants.M_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m=ApisSeUtil.i(js);
        OkGo.post(Constants.getLevelProgress)
                .tag(this)
                .params("key", m.K())
                .params("msg", m.M())
                .execute(new AbsCallback<Object>() {
                    @Override
                    public Object convertSuccess(Response response) throws Exception {
                        return null;
                    }

                    @Override
                    public void onSuccess(Object o, Call call, Response response) {
                        try {
                            String data = response.body().string();
                            if (!TextUtils.isEmpty(data)) {
                                HashMap<String, String> map = AnalyticalJSON.getHashMap(data);
                                if (map != null) {
                                    LogUtil.e(map + "");
                                    resetData();
                                    String s = "";
                                    String s1 = "";
                                    score_time.setText(mApplication.ST("会员有效期:" + map.get("start_time") + " 到 " + map.get("end_time")));
                                    if (map.get("love") != null && !map.get("love").equals("null")) {
                                        s =String.valueOf(Double.valueOf(map.get("love")).intValue()) ;
                                        score.setText(s);
                                    } else {
                                        score.setText("0");
                                    }
                                    if (map.get("money") != null && !map.get("money").equals("null")) {
                                        s1 = String.valueOf( Double.valueOf(map.get("money")).intValue());
                                    }
                                    score_now.setText(mApplication.ST("当前积分:" + s1));
                                    int sc = Integer.valueOf(s1);
                                    String le=PreferenceUtil.getUserIncetance(Mine_HuiYuan.this)
                                            .getString("level","0");
                                    if (le.equals("0")) {
                                        next = 120;
                                        level = 0;
                                        score_time.setVisibility(View.GONE);
                                        Glide.with(Mine_HuiYuan.this).load(R.drawable.gif0).into(head);
                                        progressBar.setMax(next);
                                        progressBar.setProgress(sc);
                                        score_small.setText(sc + mApplication.ST("分"));
                                        score_next.setText(mApplication.ST("到下一级需要积分" + (next - sc)));
                                        Glide.with(Mine_HuiYuan.this).load(R.drawable.gif0)
                                                .into(gif_now);
                                        Glide.with(Mine_HuiYuan.this).load(R.drawable.gif1).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                                .into(gif_next);
                                    } else if (le.equals("1")) {
                                        level = 1;
                                        next = 1200;
                                        Glide.with(Mine_HuiYuan.this).load(R.drawable.gif1).asGif()
                                                .diskCacheStrategy(DiskCacheStrategy.SOURCE).into(head);
                                        progressBar.setMax(next);
                                        progressBar.setProgress(sc);
                                        score_small.setText(sc + mApplication.ST("分"));
                                        score_next.setText(mApplication.ST("到下一级需要积分" + (next - sc)));
                                        Glide.with(Mine_HuiYuan.this).load(R.drawable.gif1).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                                .into(gif_now);
                                        Glide.with(Mine_HuiYuan.this).load(R.drawable.gif2).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                                .into(gif_next);
                                    } else if (le.equals("2")) {
                                        level = 2;
                                        next = 30000;
                                        Glide.with(Mine_HuiYuan.this).load(R.drawable.gif2).asGif()
                                                .diskCacheStrategy(DiskCacheStrategy.SOURCE).into(head);
                                        progressBar.setMax(next);
                                        progressBar.setProgress(sc);
                                        score_small.setText(sc + mApplication.ST("分"));
                                        score_next.setText(mApplication.ST("到下一级需要积分" + (next - sc)));
                                        Glide.with(Mine_HuiYuan.this).load(R.drawable.gif2).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                                .into(gif_now);
                                        Glide.with(Mine_HuiYuan.this).load(R.drawable.gif3).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                                .into(gif_next);
                                    } else if (le.equals("3")&&sc>=30000&&sc<100000) {
                                        level = 3;
                                        next = 100000;
                                        Glide.with(Mine_HuiYuan.this).load(R.drawable.gif3).asGif()
                                                .diskCacheStrategy(DiskCacheStrategy.SOURCE).into(head);
                                        progressBar.setMax(next);
                                        progressBar.setProgress(sc);
                                        score_small.setText(sc + mApplication.ST("分"));
                                        score_next.setText(mApplication.ST("到下一级需要积分" + (next - sc)));
                                        Glide.with(Mine_HuiYuan.this).load(R.drawable.gif3).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                                .into(gif_now);
                                        Glide.with(Mine_HuiYuan.this).load(R.drawable.gif3).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                                .into(gif_next);
                                    } else if (le.equals("3")&&sc >= 100000) {
                                        level = 3;
                                        next = -1;
                                        Glide.with(Mine_HuiYuan.this).load(R.drawable.gif3).asGif()
                                                .diskCacheStrategy(DiskCacheStrategy.SOURCE).into(head);
                                        score_next.setText("");
                                        progressBar.setMax(100);
                                        progressBar.setProgress(100);
                                        score_small.setText(sc + mApplication.ST("分"));
                                        gif_next.setVisibility(View.GONE);
                                        Glide.with(Mine_HuiYuan.this).load(R.drawable.gif3).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                                .into(gif_now);
                                    }


                                    content.loadDataWithBaseURL("", mApplication.ST(map.get("explain"))
                                            , "text/html", "UTF-8", null);

                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        ProgressUtil.show(Mine_HuiYuan.this, "", mApplication.ST("请稍等"), 0.5f);
                    }

                    @Override
                    public void onAfter(Object o, Exception e) {
                        super.onAfter(o, e);
                        ProgressUtil.dismiss();
                    }


                });
    }

    private void initView() {
        findViewById(R.id.back).setOnClickListener(this);
        title = (TextView) findViewById(R.id.title);
        t1 = (TextView) findViewById(R.id.t1);
        score = (TextView) findViewById(R.id.score_big);
        score_now = (TextView) findViewById(R.id.score_now);
        score_next = (TextView) findViewById(R.id.score_need);
        score_time = (TextView) findViewById(R.id.time);
        chengzhang = (TextView) findViewById(R.id.chengzhangjilu);
        chengzhang.setOnClickListener(this);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        gif_next = (ImageView) findViewById(R.id.gif_next);
        gif_now = (ImageView) findViewById(R.id.gif_now);
        score_small = (TextView) findViewById(R.id.score_small);
        head = (ImageView) findViewById(R.id.head);
        content = (myWebView) findViewById(R.id.web);
        WebSettings webSettings = content.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
//        webSettings.setUseWideViewPort(true);//关键点
//        webSettings.setLoadWithOverviewMode(true);
//        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

//        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setAppCacheEnabled(true);
//        //提高网页加载速度，暂时阻塞图片加载，然后网页加载好了，在进行加载图片
//        webSettings.setBlockNetworkImage(true);
        if (Build.VERSION.SDK_INT >= 19) {
            webSettings.setLoadsImagesAutomatically(true);
        } else {
            webSettings.setLoadsImagesAutomatically(false);
        }
        content.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (!view.getSettings().getLoadsImagesAutomatically()) {
                    view.getSettings().setLoadsImagesAutomatically(true);
                }
//                view.getSettings().setBlockNetworkImage(false);
                imgReset();
                addImageClickListner();
                ProgressUtil.dismiss();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, ImageUtil.readBitMap(Mine_HuiYuan.this, R.drawable.indra));
            }
            //            @Override
//            public void onScaleChanged(WebView view, float oldScale, float newScale) {
//                super.onScaleChanged(view, oldScale, newScale);
//                view.requestFocus();
//                view.requestFocusFromTouch();
//            }
        });
        JavascriptInterface js = new JavascriptInterface(this);
        content.addJavascriptInterface(js, "addUrl");
        content.addJavascriptInterface(js, "imagelistener");
        content.setOnLongClickListener(new myWebView.onLongClickListener() {
            @Override
            public void onLongClcik(String imgUrl) {
                Glide.with(Mine_HuiYuan.this).load(imgUrl).asBitmap().skipMemoryCache(true).override(400, 400).into(new SimpleTarget<Bitmap>() {
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
                                Toast.makeText(Mine_HuiYuan.this, mApplication.ST("无法识别,请确认当前页面是否有二维码图片"), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
            }
        });
    }

    public class JavascriptInterface {
        private Context context;

        public JavascriptInterface(Context context) {
            this.context = context;
        }

        @android.webkit.JavascriptInterface
        public void openImage(String img) {
            if (arrayList != null) {
                ScaleImageUtil.openBigIagmeMode(Mine_HuiYuan.this, arrayList, arrayList.indexOf(img),true);
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
        content.loadUrl("javascript:(function(){" +
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.chengzhangjilu:
                Intent intent = new Intent(this, ChengZhangJiLu.class);
                startActivity(intent);
                break;
        }
    }


    private void imgReset() {
        content.loadUrl("javascript:(function(){" +
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
