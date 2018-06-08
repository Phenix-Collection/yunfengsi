package com.yunfengsi.Setting;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunfengsi.R;
import com.yunfengsi.Utils.ProgressUtil;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.mApplication;


/**
 * Created by Administrator on 2016/10/11.
 */
public class AD extends AppCompatActivity {
    private WebView webView;
    private ImageView back;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, ContextCompat.getColor(this, R.color.main_color));

        setContentView(R.layout.enter_wrap);
        webView= (WebView) findViewById(R.id.enter_wrap_web);
        if(getIntent().getBooleanExtra("bangzhu",false)){
            ((TextView) findViewById(R.id.ad_name)).setText(mApplication.ST("帮助"));
            webView.loadUrl("https://indrah.cn/yfs.php/Index/help");
        }else{
            ((TextView) findViewById(R.id.ad_name)).setText(mApplication.ST(getResources().getString(R.string.app_name)));
            webView.loadUrl(getIntent().getStringExtra("url"));
        }

        WebSettings webSettings=webView.getSettings();
        //设置WebView属性，能够执行Javascript脚本
        webSettings.setJavaScriptEnabled(true);
        //设置可以访问文件
        webSettings.setAllowFileAccess(true);
        if (Build.VERSION.SDK_INT >= 21) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.setDownloadListener(new MyWebViewDownLoadListener());
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                view.loadUrl(url);
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                ProgressUtil.show(AD.this,"", mApplication.ST("正在加载...."));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView=null;
    }

    private class MyWebViewDownLoadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent,
                                    String contentDisposition, String mimetype, long contentLength) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }
}
