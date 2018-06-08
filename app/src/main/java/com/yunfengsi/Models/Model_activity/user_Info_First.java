package com.yunfengsi.Models.Model_activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.request.BaseRequest;
import com.yunfengsi.R;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.ImageUtil;
import com.yunfengsi.Utils.ProgressUtil;
import com.yunfengsi.Utils.TimeUtils;
import com.yunfengsi.View.myWebView;
import com.yunfengsi.WebShare.ZhiFuShare;

import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/3/21.
 */

public class user_Info_First extends AppCompatActivity  implements View.OnClickListener{
    private ImageView imgBack;
    private myWebView webContent;
    private TextView  tvCommit;
    private static final String TAG = "user_Info_First";
    private CountDownTimer cdt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_ziliaowanshan);

        imgBack= (ImageView) findViewById(R.id.back);
        imgBack.setOnClickListener(this);
        webContent= (myWebView) findViewById(R.id.user_info);
        WebSettings webSettings = webContent.getSettings();
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
        webContent.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (!view.getSettings().getLoadsImagesAutomatically()) {
                    view.getSettings().setLoadsImagesAutomatically(true);
                }

                imgReset();
                ProgressUtil.dismiss();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, ImageUtil.readBitMap(user_Info_First.this, R.drawable.indra));
            }
            
        });
        tvCommit= (TextView) findViewById(R.id.commit);
        cdt=new CountDownTimer(10000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvCommit.setText("同意 "+millisUntilFinished/1000+"秒");
            }

            @Override
            public void onFinish() {
                tvCommit.setText("同意");
                tvCommit.setBackgroundResource(R.drawable.button1_sel);
                tvCommit.setTextColor(Color.WHITE);
                tvCommit.setOnClickListener(user_Info_First.this);
                tvCommit.setEnabled(true);
            }
        };
        cdt.start();
        getData();
    }

    private void getData() {
        OkGo.post(Constants.getUserNeedKnow).params("key",Constants.safeKey)
                .params("m_id",Constants.M_id)
                .execute(new AbsCallback<Object>() {
                    @Override
                    public Object convertSuccess(Response response) throws Exception {
                        return null;
                    }

                    @Override
                    public void onSuccess(Object o, Call call, Response response) {
                        if(response!=null){
                            try {
                                String data=response.body().string();
                                if(!TextUtils.isEmpty(data)){
                                    HashMap<String ,String >map= AnalyticalJSON.getHashMap(data);
                                    if(map!=null){
                                        String html=map.get("act_prol");
                                        webContent.loadDataWithBaseURL(Constants.IMGDIR + TAG + "/" + TimeUtils.getStrTime(System.currentTimeMillis() / 1000 + "") + ".jpg",html
                                                , "text/html", "UTF-8", null);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        ProgressUtil.show(user_Info_First.this,"","正在加载，请稍等");
                    }


                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toast.makeText(user_Info_First.this, "数据加载失败，请重新打开页面", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onAfter(Object o, Exception e) {
                        super.onAfter(o, e);
                        ProgressUtil.dismiss();
                    }


                });

    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.commit){
            Intent intent =new Intent(this,ZhiFuShare.class);
            intent.putExtra(ZhiFuShare.ISFORM,true);
            startActivity(intent);
            finish();
        }else if(v.getId()==R.id.back){
            finish();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        webContent.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
        webContent.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webContent=null;
        cdt.cancel();
        cdt=null;
    }

    private void imgReset() {
        webContent.loadUrl("javascript:(function(){" +
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
