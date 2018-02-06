package com.yunfengsi;

import android.app.Activity;
import android.content.ClipData;
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
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.ImageUtil;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.LoginUtil;
import com.yunfengsi.Utils.MD5Utls;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.ProgressUtil;
import com.yunfengsi.Utils.ShareManager;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.mApplication;
import com.yunfengsi.Utils.photoUtil;

import java.io.File;


/**
 * Created by Administrator on 2016/10/11.
 */
//其中 mLinkKey即后台mLink服务对应的mLink Key。
//@MLinkRouter(keys={"zixun_detail"})
public class ZhiFuShare extends AppCompatActivity {
    private WebView webView;
    private ImageView back;
    private static final String URL = "http://indrah.cn" + "/" + Constants.NAME_LOW + ".php/Index/shareg";
    private static final String URL2 = "http://indrah.cn" + "/" + Constants.NAME_LOW + ".php/Index/sharegx";
    private String stu_id = "";
    private TextView name;
    public static final String ISFORM = "form";
    public static final String Progress = "progress";

    private static final String URL_FORM = Constants.host_Ip + "/" + Constants.NAME_LOW + ".php/Index/form";
    private static final String URL_Red = Constants.host_Ip + "/" + Constants.NAME_LOW + ".php/Index/red";
    private ValueCallback<Uri[]> mUploadCallbackAboveL;
    private ValueCallback<Uri> mUploadImage;
    private Uri imageUri;
    private String url = "";
    //type  1,资讯，2活动，3，供养，4，助学，5红包
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, ContextCompat.getColor(this, R.color.main_color));

        setContentView(R.layout.enter_wrap);
        webView = (WebView) findViewById(R.id.enter_wrap_web);
        name = (TextView) findViewById(R.id.ad_name);

        if (getIntent().getBooleanExtra(Progress, false)) {//项目进度
//           https://indrah.cn/yfs.php/Index/cfgload/id/xx
            url = Constants.FX_host_Ip + "/cfgload/id/" + getIntent().getStringExtra("id") + "/st/" + (mApplication.isChina ? "s" : "t");
            name.setText(mApplication.ST("项目进度"));
        } else {
            if (!getIntent().getBooleanExtra(ISFORM, false)) {
                //红包
                if ("5".equals(getIntent().getStringExtra("type"))) {//红包
                    LogUtil.e("红包页面：：~~！~！~！~"+getIntent().getStringExtra("type"));
                    url = URL_Red+ "/status/1";//status  1  为本地app打开  该页面

                } else {

                    stu_id = getIntent().getStringExtra("stu_id");
                    if (stu_id != null) {
                        url = URL + "/type/1/sut_id/" + stu_id + "/st/" + (mApplication.isChina ? "s" : "t");
                        name.setText(mApplication.ST("供养回向"));
                    } else {
                        url = URL2 + "/type/1/nf_id/" + getIntent().getStringExtra("nf_id") + "/status/" + getIntent().getIntExtra("status", 0) + "/st/" + (mApplication.isChina ? "s" : "t");
                        switch (getIntent().getIntExtra("status", 0)) {
                            case 1:
                                name.setText(mApplication.ST("念佛回向"));
                                break;
                            case 2:
                                name.setText(mApplication.ST("诵经回向"));
                                break;
                            case 3:
                                name.setText(mApplication.ST("持咒回向"));
                                break;
                            case 4:
                                name.setText(mApplication.ST("助念回向"));
                                break;
                            case 5:
                                name.setText(mApplication.ST("忏悔回向"));
                                break;
                        }
                    }
                }
            } else {//加载资料完善页   红包页面
                LogUtil.e("资料完善！@！@！@！@！");
                name.setText(mApplication.ST("资料完善"));
                String md5 = MD5Utls.stringToMD5(Constants.safeKey);
                String m1 = md5.substring(0, 16);
                String m2 = md5.substring(16, md5.length());
                url = URL_FORM + "/id/" + m1 + PreferenceUtil.getUserIncetance(this).getString("user_id", "") + m2 + "/st/" + (mApplication.isChina ? "s" : "t");
            }
        }


        ShareScript js = new ShareScript();
        webView.addJavascriptInterface(js, "share");
        LogUtil.e("链接：：" + url+"    意图：：："+"     "+getIntent().getExtras());
        WebSettings webSettings = webView.getSettings();
        //设置WebView属性，能够执行Javascript脚本
        webSettings.setJavaScriptEnabled(true);
        //设置可以访问文件
        webSettings.setAllowFileAccess(true);
        webSettings.setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT >= 21) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                LogUtil.e("跳转地址：：："+url);
//                if(url.equals(ZhiFuShare.this.url)){
                    view.loadUrl(url);
//                }else{
//                    finish();
//                }
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                ProgressUtil.show(ZhiFuShare.this, "", mApplication.ST("请稍等...."));
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                ProgressUtil.dismiss();
            }

        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override      // Android > 5.0调用这个方法
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                LogUtil.e("图片文件回调");
                mUploadCallbackAboveL = filePathCallback;
                photoUtil.choosePic(ZhiFuShare.this, 0);
                photoUtil.setmUploadCallbackAboveL(mUploadCallbackAboveL);
                return true;
            }

            @Deprecated            // Android > 4.1.1 调用这个方法
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                LogUtil.e("图片文件回调");
                mUploadImage = uploadMsg;
//
                if (Build.VERSION.SDK_INT == 19) {
                    photoUtil.DoPicture(ZhiFuShare.this, 0, null);//直接选择照片
                } else {
                    photoUtil.choosePic(ZhiFuShare.this, 0);//选择相机或照片
                }
                photoUtil.setmUploadImage(mUploadImage);
            }


            @Deprecated            // 3.0 + 调用这个方法
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                LogUtil.e("图片文件回调");
                mUploadImage = uploadMsg;
                photoUtil.choosePic(ZhiFuShare.this, 0);
                photoUtil.setmUploadImage(mUploadImage);
//                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
//                i.addCategory(Intent.CATEGORY_OPENABLE);
//                i.setType("*/*");
//                startActivityForResult(
//                        Intent.createChooser(i, "File Browser"),
//                        0);

            }
        });
        findViewById(R.id.reload).setVisibility(View.VISIBLE);
        if("5".equals(getIntent().getStringExtra("type"))||(getIntent().getStringExtra("url")!=null&&getIntent().getStringExtra("url").contains(URL_Red))){
            ((TextView) findViewById(R.id.reload)).setText(mApplication.ST("分享"));
            name.setText("智灯师父发福包了！");
        }else{
            ((TextView) findViewById(R.id.reload)).setText(mApplication.ST("刷新"));
        }

        //直接传入url

        if(getIntent().getStringExtra("url")!=null){
            url=getIntent().getStringExtra("url");
            LogUtil.e("GGurl       "+ url);
        }
        findViewById(R.id.reload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("5".equals(getIntent().getStringExtra("type"))||(getIntent().getStringExtra("url")!=null&&getIntent().getStringExtra("url").contains(URL_Red))){
                    UMWeb umWeb=new UMWeb(URL_Red);
                    umWeb.setThumb(new UMImage(ZhiFuShare.this,R.drawable.hongbao_3));
                    umWeb.setTitle("智灯师父发福包啦！");
                    // TODO: 2017/11/29  
                    umWeb.setDescription("智灯师父新年送祝福，福包抢不停");
                    ShareManager shareManager=new ShareManager();
                    shareManager.shareWeb(umWeb,ZhiFuShare.this);
                }else{
                    webView.loadUrl(url);
                }

            }
        });

        findViewById(R.id.enter_wrap_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView = null;
                setResult(666);
                finish();
            }
        });
        webView.loadUrl(url);
    }

    private class ShareScript {


        private ShareScript() {
            super();
        }

        @JavascriptInterface
        public void formCommitEnd(String code) {
            LogUtil.e("返回的参数：" + code);
            if ("001".equals(code)) {
                Toast.makeText(ZhiFuShare.this, mApplication.ST("完善资料成功，可到我的>设置>个人信息中查看资料"), Toast.LENGTH_SHORT).show();
                PreferenceUtil.getUserIncetance(getApplicationContext()).edit().putString("perfect", "2").commit();
                Intent intent = new Intent();
                intent.putExtra("baoming", true);
                setResult(66, intent);
                finish();
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ZhiFuShare.this, mApplication.ST("上传资料失败，请重新填写"), Toast.LENGTH_SHORT).show();
                        String md5 = MD5Utls.stringToMD5(Constants.safeKey);
                        String m1 = md5.substring(0, 16);
                        String m2 = md5.substring(16, md5.length());
                        webView.loadUrl(URL_FORM + "/id/" + m1 + PreferenceUtil.getUserIncetance(getApplicationContext()).getString("user_id", "") + m2);
                    }
                });
            }
        }

        @JavascriptInterface
        public void openShare(String imgUrl, String petName, String goodName, String num, String info, String units, String money) {
            if("5".equals(getIntent().getStringExtra("type"))||(getIntent().getStringExtra("url")!=null&&getIntent().getStringExtra("url").contains(URL_Red))){
                LogUtil.e("跳转红包页面");
                if(new LoginUtil().checkLogin(ZhiFuShare.this)){
                    Intent intent=new Intent(ZhiFuShare.this,RedPacket.class);
                    startActivityForResult(intent,2);
                }

                return;
            }

            LogUtil.e("String ::::" + imgUrl + "\n:::" + petName + "\n:::" + goodName + "\n:::" + num + "\n:::" + info + "\n:::" + units + "\n:::" + money);
            /*
            支付回向
             */
            if (stu_id != null) {
                final UMWeb umWeb = new UMWeb(URL + "/type/0/sut_id/" + stu_id);
                umWeb.setDescription(mApplication.ST("大家都在这供养！快来看看吧!"));

                if (units.contains("元")) {
                    umWeb.setTitle(petName + mApplication.ST("为您") + goodName.trim() + money.trim() + mApplication.ST(units));
                } else {
                    umWeb.setTitle(petName + mApplication.ST("为您") + goodName.trim() + num.trim() + mApplication.ST(units));
                }
                String image = "";
                if (imgUrl.startsWith("/amf")) {
                    image = Constants.host_Ip + imgUrl.replace("/amf", "");
                } else {
                    if (imgUrl.startsWith("http")) {
                        image = imgUrl;
                    } else {
                        image = Constants.host_Ip + imgUrl;
                    }
                }
                LogUtil.e(image);
                umWeb.setThumb(new UMImage(ZhiFuShare.this, image));

                //使用的是测试分享地址
                webView.post(new Runnable() {
                    @Override
                    public void run() {
                        new ShareManager().shareWeb(umWeb, ZhiFuShare.this);
                    }
                });
            }
              /*
            共修回向
             */
            else {
                final UMWeb umWeb = new UMWeb(URL2 + "/type/0/nf_id/" + getIntent().getStringExtra("nf_id") + "/status/" + getIntent().getIntExtra("status", 0));
                switch (imgUrl) {
                    case "1":
                    case "2":
                    case "3":
                        umWeb.setTitle(petName + mApplication.ST(info + goodName + num));
                        umWeb.setDescription(mApplication.ST("大家都在这共修！快来看看吧!"));
                        break;
                    case "4":
                    case "5":
                        umWeb.setTitle(petName + mApplication.ST(info));
                        umWeb.setDescription(mApplication.ST(units));
                        break;
                }
                umWeb.setThumb(new UMImage(ZhiFuShare.this, R.drawable.indra));
                //使用的是测试分享地址
//                webView.post(new Runnable() {
//                    @Override
//                    public void run() {
                new ShareManager().shareWeb(umWeb, ZhiFuShare.this);
//                    }
//                });

            }


        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UMShareAPI.get(this).release();
        webView = null;
        mApplication.id = "";
        mApplication.sut_id = "";
        mApplication.type = "";
        mApplication.title = "";
        OkGo.getInstance().cancelTag(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (mUploadImage == null && null == mUploadCallbackAboveL) return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (mUploadCallbackAboveL != null) {
                Uri[] results = null;
                if (resultCode == Activity.RESULT_OK) {
                    if (data == null) {//自定义路径后的拍照
                        results = new Uri[]{Uri.fromFile(new File(ImageUtil.getImageAbsolutePath(this, photoUtil.uri)))};
                        LogUtil.e("回调：：：自定义路径：：；" + ImageUtil.getImageAbsolutePath(this, photoUtil.uri) + "    文件大小：：" + new File(ImageUtil.getImageAbsolutePath(this, photoUtil.uri)).length());

                    } else {
                        String dataString = data.getDataString();

                        ClipData clipData = data.getClipData();
                        if (clipData != null) {
                            results = new Uri[clipData.getItemCount()];
                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                ClipData.Item item = clipData.getItemAt(i);
                                results[i] = item.getUri();
                                LogUtil.e("回调地址：：：返回不为空：：；" + ImageUtil.getImageAbsolutePath(this, item.getUri()));
                            }
                        }

                        if (dataString != null) {
                            results = new Uri[]{Uri.fromFile(new File(ImageUtil.getImageAbsolutePath(this, Uri.parse(dataString))))};
                            LogUtil.e("回调地址：：：data.getDataString()：：；" + ImageUtil.getImageAbsolutePath(this, Uri.parse(dataString)) + "     文件大小：:::" + new File(ImageUtil.getImageAbsolutePath(this, Uri.parse(dataString))).length());
                        }

                    }
                }
                mUploadCallbackAboveL.onReceiveValue(results);
                mUploadCallbackAboveL = null;
            } else if (mUploadImage != null) {
                mUploadImage.onReceiveValue(result);
                mUploadImage = null;
            }
        }else if(requestCode==2&&resultCode==666){//红包活动已结束
            webView.loadUrl(url);
        }
    }

    @Override
    //设置回退
    //覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
//            webView.goBack(); //goBack()表示返回WebView的上一页面
//            return true;
//        }
        if(keyCode==KeyEvent.KEYCODE_BACK){
            webView.removeJavascriptInterface("share");
            webView.removeJavascriptInterface(ISFORM);
            webView = null;
            setResult(666);
            finish();//结束退出程序
            return true;
        }
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
