package com.qianfujiaoyu.Setting;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.zxing.Result;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.request.BaseRequest;
import com.qianfujiaoyu.Activitys.ViewPagerActivity;
import com.qianfujiaoyu.R;
import com.qianfujiaoyu.Utils.AnalyticalJSON;
import com.qianfujiaoyu.Utils.ApisSeUtil;
import com.qianfujiaoyu.Utils.Constants;
import com.qianfujiaoyu.Utils.ImageUtil;
import com.qianfujiaoyu.Utils.LogUtil;
import com.qianfujiaoyu.Utils.ProgressUtil;
import com.qianfujiaoyu.Utils.QrUtils;
import com.qianfujiaoyu.Utils.StatusBarCompat;
import com.qianfujiaoyu.Utils.TimeUtils;
import com.qianfujiaoyu.View.myWebView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;


/**
 * Created by Administrator on 2016/10/25.
 */
public class BangZhu extends AppCompatActivity {
    public  static  final int BANGZHU=1;
    public  static  final int XIEYI=2;

    private myWebView content;
    private static final String TAG = "BangZhu";
    private ArrayList<String> arrayList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.mine_bangzhu);
        findViewById(R.id.back).setVisibility(View.VISIBLE);
        ((ImageView) findViewById(R.id.back)).setImageBitmap(ImageUtil.readBitMap(this,R.drawable.back));
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if(getIntent().getIntExtra("type",1)==BANGZHU){
            ((TextView) findViewById(R.id.title)).setText("帮助中心");
        }else if(getIntent().getIntExtra("type",1)==XIEYI){
            ((TextView) findViewById(R.id.title)).setText("用户协议");
        }

        content= (myWebView) findViewById(R.id.webView);
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
                super.onPageStarted(view, url, ImageUtil.readBitMap(BangZhu.this, R.drawable.indra));
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
                Glide.with(BangZhu.this).load(imgUrl).asBitmap().skipMemoryCache(true).override(400, 400).into(new SimpleTarget<Bitmap>() {
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
                                Toast.makeText(BangZhu.this, "无法识别,请确认当前页面是否有二维码图片", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
            }
        });
        getBangzhu();
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
                Log.w(TAG, "openImage: 网页图片地址" + img + "页码：" + arrayList.indexOf(img));
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
    private void getBangzhu() {
        JSONObject js = new JSONObject();
        try {
            js.put("m_id", Constants.M_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m=ApisSeUtil.i(js);
        String url="";
        if(getIntent().getIntExtra("type",1)==BANGZHU){
            url=Constants.BangZhu;
        }else if(getIntent().getIntExtra("type",1)==XIEYI){
            url=Constants.Store_Prol;
        }
        OkGo.post(url)
                .params("key",m.K())
                .params("msg",m.M())
                .execute(new AbsCallback<Object>() {
                    @Override
                    public Object convertSuccess(Response response) throws Exception {
                        return null;
                    }



                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        ProgressUtil.show(BangZhu.this,"","正在加载");
                    }

                    @Override
                    public void onAfter(@Nullable Object o, @Nullable Exception e) {
                        super.onAfter(o, e);
                        ProgressUtil.dismiss();
                    }

                    @Override
                    public void onSuccess(Object o, Call call, Response response) {
                        if(response!=null){
                            try {
                                String data=response.body().string();
                                if(!TextUtils.isEmpty(data)){
                                    final HashMap<String ,String >map= AnalyticalJSON.getHashMap(data);
                                    if(map!=null){
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                content.loadDataWithBaseURL(Constants.IMGDIR + TAG + "/" + TimeUtils.getStrTime(System.currentTimeMillis() / 1000 + "") + ".jpg",
                                                        getIntent().getIntExtra("type",1)==BANGZHU?map.get("guide"):map.get("agreements")
                                                        , "text/html", "UTF-8", null);
                                            }
                                        });

                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
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
