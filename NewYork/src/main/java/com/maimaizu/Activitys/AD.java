package com.maimaizu.Activitys;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.maimaizu.R;
import com.maimaizu.Utils.ProgressUtil;
import com.maimaizu.Utils.StatusBarCompat;
import com.maimaizu.Utils.mApplication;


/**
 * Created by Administrator on 2016/10/11.
 */
public class AD extends AppCompatActivity {
    private WebView webView;
    private ImageView back;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, (Color.parseColor("#90000000")));

        setContentView(R.layout.enter_wrap);
        ((TextView) findViewById(R.id.title)).setText(mApplication.ST(getResources().getString(R.string.app_name)));
        webView= (WebView) findViewById(R.id.enter_wrap_web);
        webView.loadUrl(getIntent().getStringExtra("url"));
        WebSettings webSettings=webView.getSettings();
        //设置WebView属性，能够执行Javascript脚本
        webSettings.setJavaScriptEnabled(true);
        //设置可以访问文件
        webSettings.setAllowFileAccess(true);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onLoadResource(WebView view, String url) {

                ProgressUtil.show(AD.this,null, mApplication.ST("正在加载...."));
                super.onLoadResource(view, url);
            }

              @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
               ProgressUtil.dismiss();
            }
        });
        findViewById(R.id.enter_wrap_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView=null;
                finish();
            }
        });
    }
    @Override
    //设置回退
    //覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack(); //goBack()表示返回WebView的上一页面
            return true;
        }
        webView=null;
        finish();//结束退出程序
        return false;
    }

    @Override
    protected void onPause() {
        if(webView!=null)
        webView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        if(webView!=null)
        webView.onResume();
        super.onResume();
    }
}
