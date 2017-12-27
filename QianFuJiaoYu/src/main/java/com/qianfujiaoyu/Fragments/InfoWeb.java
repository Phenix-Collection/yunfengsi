package com.qianfujiaoyu.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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
import com.qianfujiaoyu.Utils.ToastUtil;
import com.qianfujiaoyu.Utils.mApplication;
import com.qianfujiaoyu.View.myWebView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 作者：因陀罗网 on 2017/5/18 10:40
 * 公司：成都因陀罗网络科技有限公司
 */

public class InfoWeb extends Fragment {
    private myWebView content;
    private ArrayList<String> arrayList;
    private View view;
    private boolean isFirstIn = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.info_web, container, false);
        content = (myWebView) view.findViewById(R.id.web);

        WebSettings webSettings = content.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        webSettings.setAppCacheEnabled(true);

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
                super.onPageStarted(view, url, ImageUtil.readBitMap(getActivity(), R.drawable.indra));
            }
            //            @Override
//            public void onScaleChanged(WebView view, float oldScale, float newScale) {
//                super.onScaleChanged(view, oldScale, newScale);
//                view.requestFocus();
//                view.requestFocusFromTouch();
//            }
        });
        JavascriptInterface js = new JavascriptInterface(getActivity());
        content.addJavascriptInterface(js, "addUrl");
        content.addJavascriptInterface(js, "imagelistener");
        content.setOnLongClickListener(new myWebView.onLongClickListener() {
            @Override
            public void onLongClcik(String imgUrl) {
                Glide.with(getActivity()).load(imgUrl).asBitmap().skipMemoryCache(true).override(400, 400).into(new SimpleTarget<Bitmap>() {
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
                                Toast.makeText(getActivity(), mApplication.ST("无法识别,请确认当前页面是否有二维码图片"), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
            }
        });



        return view;

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (this != null && isVisibleToUser) {
            if (isFirstIn) {
                getData();
            }
        }
    }

    // TODO: 2017/5/18 获取数据
    private void getData() {
        JSONObject js = new JSONObject();
        try {
            js.put("m_id", Constants.M_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m = ApisSeUtil.i(js);
        OkGo.post(Constants.getQianFuInfo).tag(this)
                .params("key", m.K())
                .params("msg", m.M())
                .execute(new AbsCallback<HashMap<String, String>>() {
                    @Override
                    public void onSuccess(HashMap<String, String> map, Call call, Response response) {
                        if (map != null) {
                            isFirstIn = false;
                            content.loadDataWithBaseURL("", mApplication.ST(map.get("contents"))
                                    , "text/html", "UTF-8", null);
                        }else{
                            ToastUtil.showToastShort("暂无园介绍");
                            ProgressUtil.dismiss();
                        }
                    }

                    @Override
                    public HashMap<String, String> convertSuccess(Response response) throws Exception {
                        return AnalyticalJSON.getHashMap(response.body().string());
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        ProgressUtil.show(getActivity(), "", "请稍等");
                    }

                    @Override
                    public void onAfter(HashMap<String, String> map, Exception e) {
                        super.onAfter(map, e);
                        ProgressUtil.dismiss();
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

    @Override
    public void onPause() {
        super.onPause();
        content.onPause();
        ProgressUtil.dismiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(this);
        content.removeAllViews();
        content.destroy();
        content = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        content.onResume();
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
                Log.w("大图浏览", "openImage: 网页图片地址" + img + "页码：" + arrayList.indexOf(img));
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
}
