package com.yunfengsi.Setting;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.yunfengsi.R;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.ProgressUtil;
import com.yunfengsi.Utils.ShareManager;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.mApplication;

import java.util.Calendar;

/**
 * Created by Administrator on 2017/5/15.
 */

public class Month_Detail extends AppCompatActivity implements View.OnClickListener {
    public String y = "";
    private WebView webView;
    Calendar c;
    public String m = "";
    private static final String URL = Constants.host_Ip+ "/" + Constants.NAME_LOW + ".php/Index/mybill";
    private String url = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.month_detail);
        mApplication.getInstance().addActivity(this);
        ((TextView) findViewById(R.id.reload)).setText(mApplication.ST("刷新"));
        ((TextView) findViewById(R.id.title)).setText(mApplication.ST("感谢信"));
        ((TextView) findViewById(R.id.share)).setText(mApplication.ST("分享"));
        c = Calendar.getInstance();
        webView = findViewById(R.id.enter_wrap_web);
//        webView.setBackgroundColor(ContextCompat.getColor(this,R.color.main_color));
        WebSettings webSettings = webView.getSettings();
        //设置WebView属性，能够执行Javascript脚本
        webSettings.setJavaScriptEnabled(true);
        //设置可以访问文件
        webSettings.setAllowFileAccess(true);
        webSettings.setDomStorageEnabled(true);
        y = String.valueOf(c.get(Calendar.YEAR));
        m = String.valueOf(c.get(Calendar.MONTH)+1);
        if (m.length() < 2) {
            m = "0" + m;
        }
        ((TextView) findViewById(R.id.select)).setText(mApplication.ST(y + "年" + m + "月"));
        Drawable d= ContextCompat.getDrawable(this,R.drawable.spinner_icon);
        d.setBounds(0,0, DimenUtils.dip2px(this,10),DimenUtils.dip2px(this,10));

        ((TextView) findViewById(R.id.select)).setCompoundDrawables(null,null,d,null);
        url = URL + "/id/" + PreferenceUtil.getUserIncetance(this).getString("user_id", "") + "/time/" + y + m
                + "/type/1/st/" + (mApplication.isChina ? "s" : "t");
        LogUtil.e("！！~！~！" + url);
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                ProgressUtil.show(Month_Detail.this, "", mApplication.ST("请稍等...."));
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                ProgressUtil.dismiss();
            }

        });
    }

    @Override
    protected void onDestroy() {
        if( webView!=null) {

            // 如果先调用destroy()方法，则会命中if (isDestroyed()) return;这一行代码，需要先onDetachedFromWindow()，再
            // destory()
            ViewParent parent = webView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(webView);
            }

            webView.stopLoading();
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            webView.getSettings().setJavaScriptEnabled(false);
            webView.clearHistory();
            webView.clearView();
            webView.removeAllViews();
            webView.destroy();

        }
        super.onDestroy();
        mApplication.getInstance().romoveActivity(this);
    }

    @Override
    public void onClick(final View v) {

        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.select:
                showDateDialog();
                break;
            case R.id.share:
                UMWeb umWeb = new UMWeb(url);
                umWeb.setTitle("致"+PreferenceUtil.getUserIncetance(this).getString("pet_name","")+"的感谢信");
                umWeb.setDescription(y+"年"+m+"月的一封感谢信");
                umWeb.setThumb(new UMImage(Month_Detail.this,R.drawable.yunfengcishan_jpg));
                new ShareManager().shareWeb(umWeb,Month_Detail.this);
                break;
            case R.id.reload:
                webView.clearCache(true);
                url = URL + "/id/" + PreferenceUtil.getUserIncetance(this).getString("user_id", "") + "/time/" + y + m
                        + "/type/1/st/" + (mApplication.isChina ? "s" : "t");
                webView.loadUrl(url);
                LogUtil.e("!@!@!@!@!"+url);
                break;
        }
    }

    public void showDateDialog() {
        View mView = View.inflate(this, R.layout.dialog_date_picker, null);
        final NumberPicker np1 = mView.findViewById(R.id.year);
        final NumberPicker np2 = mView.findViewById(R.id.month);

        //获取当前日期

        final int year = c.get(Calendar.YEAR);
        final int month = c.get(Calendar.MONTH) + 1;//月份是从0开始算的
        LogUtil.e(year + "年" + month + "月");

        //设置年份

        np1.setMinValue(2016);
        np1.setMaxValue(year);
        np1.setValue(year); //中间参数 设置默认值


        //设置月份
        np2.setMinValue(1);
        np2.setMaxValue(month);
        np2.setValue(month);

//        //设置天数
//        np3.setMaxValue(31);
//        np3.setValue(day);
//        np3.setMinValue(1);

        //年份滑动监听
        np1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Log.e("NumberPicker","oldVal-----"+oldVal+"-----newVal-----"+newVal);
                if(newVal!=year){
                    np2.setMinValue(1);
                    np2.setMaxValue(12);
                    np2.setValue(month);
                }else{
                    np2.setMinValue(1);
                    np2.setMaxValue(month);
                    np2.setValue(month);
                }

            }
        });

        //月份滑动监听
//        np2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
//            @Override
//            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
//                Log.i("NumberPicker","oldVal-----"+oldVal+"-----newVal-----"+newVal);
//                //月份判断
//                switch (newVal){
////                    default:m=newVal;
//                }
//
//            }
//        });

        new AlertDialog.Builder(Month_Detail.this).setTitle("请选择时间")
                .setView(mView)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        y = String.valueOf(np1.getValue());
                        m = String.valueOf(np2.getValue());
                        if (m.length() < 2) {
                            m = "0" + m;
                        }
//                        int days = np3.getValue();
                        ((TextView) findViewById(R.id.select)).setText(y + "年" + m + "月");
                        dialog.dismiss();
                        findViewById(R.id.reload).performClick();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    //设置回退
    //覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(webView!=null){
            if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
                webView.goBack(); //goBack()表示返回WebView的上一页面
                return true;
            }
            webView.removeAllViews();
            webView.destroy();
            webView = null;
        }

        finish();//结束退出程序
        return false;
    }

    @Override
    protected void onPause() {
        if (webView != null)
            webView.onPause();
        ProgressUtil.dismiss();
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (webView != null)
            webView.onResume();
        super.onResume();
    }
}
