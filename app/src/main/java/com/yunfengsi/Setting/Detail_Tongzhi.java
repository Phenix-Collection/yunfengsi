package com.yunfengsi.Setting;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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
import com.yunfengsi.R;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.ImageUtil;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.Network;
import com.yunfengsi.Utils.ProgressUtil;
import com.yunfengsi.Utils.QrUtils;
import com.yunfengsi.Utils.ScaleImageUtil;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.TimeUtils;
import com.yunfengsi.Utils.mApplication;
import com.yunfengsi.View.myWebView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 作者：因陀罗网 on 2017/6/30 16:03
 * 公司：成都因陀罗网络科技有限公司
 */

public class Detail_Tongzhi extends AppCompatActivity implements View.OnClickListener {
    private String Id,User_id;
    private ImageView head;
    private TextView type,title,pet_name,time;
    private myWebView content;
    private ArrayList<String> arrayList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, ContextCompat.getColor(this, R.color.main_color));
        setContentView(R.layout.detail_tongzhi);
        ImageView back = (ImageView) findViewById(R.id.title_back);
        back.setOnClickListener(this);
        back.setVisibility(View.VISIBLE);
        TextView t = (TextView) findViewById(R.id.title_title);
        t.setText(mApplication.ST("通知详情"));
        Id=getIntent().getStringExtra("id");
        User_id=getIntent().getStringExtra("user_id");
        type= (TextView) findViewById(R.id.type);
        title= (TextView) findViewById(R.id.title);
        pet_name= (TextView) findViewById(R.id.pet_name);
        time= (TextView) findViewById(R.id.time);
        content= (myWebView) findViewById(R.id.web);
        head= (ImageView) findViewById(R.id.head);
        initWEB();
        getData();
    }

    private void initWEB() {
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
                content.setVisibility(View.VISIBLE);
                ProgressUtil.dismiss();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, ImageUtil.readBitMap(Detail_Tongzhi.this, R.drawable.indra));
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
                Glide.with(Detail_Tongzhi.this).load(imgUrl).asBitmap().skipMemoryCache(true).override(400, 400).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        Result result = QrUtils.handleQRCodeFormBitmap(resource);
                        if (result == null) {
                            LogUtil.w("onResourceReady: 不是二维码   " + result);
                        } else {
                            LogUtil.w("onResourceReady: 是二维码   " + result);
                            if (result.getText().startsWith("http")) {
                                Uri uri = Uri.parse(result.getText());
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(uri);
                                startActivity(intent);
                            } else {
                                Toast.makeText(Detail_Tongzhi.this, mApplication.ST("无法识别,请确认当前页面是否有二维码图片"), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
            }
        });
    }
    @Override
    protected void onPause() {
        super.onPause();
        content.onPause();
        ProgressUtil.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        content.onResume();
    }

    private void getData() {
        if(Network.HttpTest(this)){
            JSONObject js=new JSONObject();
            try {
                js.put("m_id",Constants.M_id);
                js.put("id",Id);
                js.put("user_id",User_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ApisSeUtil.M m=ApisSeUtil.i(js);
            OkGo.post(Constants.tongzhi_Detail).tag(this)
                    .params("key",m.K())
                    .params("msg",m.M())
                    .execute(new AbsCallback<HashMap<String,String>>() {
                        @Override
                        public void onSuccess(HashMap<String, String> map, Call call, Response response) {
                            title.setText(mApplication.ST(map.get("title")));
                            pet_name.setText(mApplication.ST(map.get("pet_name")));
                            Glide.with(Detail_Tongzhi.this)
                                    .load(map.get("user_image"))
                                    .override(DimenUtils.dip2px(Detail_Tongzhi.this,60)
                                    ,DimenUtils.dip2px(Detail_Tongzhi.this,60))
                                    .into(head);
                            type.setText(mApplication.ST("0".equals(User_id)?"公告":"通知"));
                            time.setText(TimeUtils.getTrueTimeStr(map.get("time")));
                            content.loadDataWithBaseURL("",mApplication.ST(map.get("contents"))
                                    , "text/html", "UTF-8", null);
                        }

                        @Override
                        public HashMap<String, String> convertSuccess(Response response) throws Exception {
                            return AnalyticalJSON.getHashMap(response.body().string());
                        }

                        @Override
                        public void onBefore(BaseRequest request) {
                            super.onBefore(request);
                            ProgressUtil.show(Detail_Tongzhi.this,"","正在加载");
                        }

                        @Override
                        public void onAfter(HashMap<String, String> stringStringHashMap, Exception e) {
                            super.onAfter(stringStringHashMap, e);
                            ProgressUtil.dismiss();
                        }
                    });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.title_back:
                finish();
                break;
        }
    }




    public class JavascriptInterface {
        private Context context;

        public JavascriptInterface(Context context) {
            this.context = context;
        }

        @android.webkit.JavascriptInterface
        public void openImage(String img) {
            if (arrayList != null) {
                ScaleImageUtil.openBigIagmeMode(Detail_Tongzhi.this,arrayList,arrayList.indexOf(img),true);
                LogUtil.e("openImage: 网页图片地址" + img + "页码：" + arrayList.indexOf(img));
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
