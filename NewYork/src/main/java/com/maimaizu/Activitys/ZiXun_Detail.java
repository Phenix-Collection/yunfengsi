package com.maimaizu.Activitys;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.zxing.Result;
import com.lzy.okgo.OkGo;
import com.maimaizu.Adapter.PL_List_Adapter;
import com.maimaizu.Mine.Login;
import com.maimaizu.Mine.Mine_gerenziliao;
import com.maimaizu.R;
import com.maimaizu.Utils.AnalyticalJSON;
import com.maimaizu.Utils.Constants;
import com.maimaizu.Utils.DimenUtils;
import com.maimaizu.Utils.LogUtil;
import com.maimaizu.Utils.LoginUtil;
import com.maimaizu.Utils.Network;
import com.maimaizu.Utils.ProgressUtil;
import com.maimaizu.Utils.QrUtils;
import com.maimaizu.Utils.ShareManager;
import com.maimaizu.Utils.StatusBarCompat;
import com.maimaizu.Utils.TimeUtils;
import com.maimaizu.Utils.mApplication;
import com.maimaizu.View.mPLlistview;
import com.maimaizu.View.myWebView;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

/**
 * Created by Administrator on 2016/6/17.
 */
public class ZiXun_Detail extends AppCompatActivity implements OnClickListener, PL_List_Adapter.onHuifuListener {
    private static final String TAG = "Newsd";
    private ImageView back, dianzanImg;
    private TextView title, time, user, fasong, plNum;
    private TextView dianzan;
    private myWebView content;
    private mPLlistview PlListVIew;
    private EditText PLText;
    private int screenWidth;
    private String id;
    private String page = "1";
    private String endPage = "";
    private ArrayList<HashMap<String, String>> Pllist;
    private String var;
    private PL_List_Adapter adapter;
    private ImageView shoucang, fenxiang2;

    private SharedPreferences sp;
    //第一次加载的评论数量
    private int firstNum = 0;
    //第一次加载的评论map
    private HashMap<String, String> FirstMap;
    //无评论时的header
    private TextView tv;
    private InputMethodManager imm;
//    private SHARE_MEDIA[] share_list;
//    private ShareAction action;

    JCVideoPlayerStandard player;
    private boolean needTochange = false;
    private LinearLayout dianzanLayout;
    //    private LinearLayout img_layout;
    private ArrayList<String> arrayList;
    private FrameLayout options;

    private LinearLayout currentLayout;
    private int currentPosition;
    private String currentId;
    private boolean isPLing = false;

    private LinearLayout pinglun, fenxiangb;
    private FrameLayout overlay;
    private UMWeb umWeb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zixun_detail);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        initView();
        LoadData();
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

    private void initView() {

        overlay = (FrameLayout) findViewById(R.id.frame);
        overlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                PLText.setHint(mApplication.ST("写入您的评论（100字以内）"));
                isPLing = false;
                imm.hideSoftInputFromWindow(PLText.getWindowToken(), 0);
                v.setVisibility(View.GONE);
            }
        });
        pinglun = (LinearLayout) findViewById(R.id.pinglun);
        ((TextView) findViewById(R.id.pingluntext)).setText(mApplication.ST("评论"));
        pinglun.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                overlay.setVisibility(View.VISIBLE);
                PLText.requestFocus();
                isPLing = false;
                PLText.setHint(mApplication.ST("写入您的评论（100字以内）"));
//                        imm. showSoftInput(PLText,2);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

            }
        });
        fenxiangb = (LinearLayout) findViewById(R.id.fenxiangb);
        ((TextView) findViewById(R.id.fenxiangtext)).setText(mApplication.ST("分享"));
        fenxiangb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                umWeb = new UMWeb(Constants.FX_host_Ip + TAG + "/id/" + id);
                umWeb.setTitle(mApplication.ST(FirstMap.get("title")));
                umWeb.setDescription(mApplication.ST(FirstMap.get("abstract")));
//                UMImageMark umImageMark = new UMImageMark();
//                umImageMark.setGravity(Gravity.BOTTOM | Gravity.RIGHT);
//                umImageMark.setMarkBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.umeng_socialize_twitter));
//                umImageMark.setAlpha(0.5f);//设置透明度
//                umImageMark.setMargins(3,3,3,3);//设置边距
                umWeb.setThumb(new UMImage(ZiXun_Detail.this, FirstMap.get("image")));
                if (umWeb != null) {
                    new ShareManager().shareWeb(umWeb, ZiXun_Detail.this);
                }
//                new ShareManager().shareMsg(ZiXun_Detail.this, "", mApplication.ST("分享资讯到"), mApplication.ST(FirstMap.get("title")) + "\n" + Constants.FX_host_Ip + "newsd/id/" + id + "/type/" + (mApplication.isChina ? "s" : "t"), "");

            }
        });

        shoucang = (ImageView) findViewById(R.id.zixun_detail_shoucang);
        shoucang.setOnClickListener(this);
        fenxiang2 = (ImageView) findViewById(R.id.zixun_detail_fenxiang2);
        Pllist = new ArrayList<>();
        adapter = new PL_List_Adapter(this);
        adapter.setOnHuifuListener(this);
        PLText = (EditText) findViewById(R.id.zixun_detail_apply_edt);
        PLText.setHint(mApplication.ST("写入您的评论（100字以内）"));
        sp = getSharedPreferences("user", MODE_PRIVATE);
        plNum = (TextView) findViewById(R.id.zixun_Detail_appendPLNum);
        fasong = (TextView) findViewById(R.id.zixun_detail_fasong);
        fasong.setText(mApplication.ST("发送"));
        //点赞
        dianzanLayout = (LinearLayout) findViewById(R.id.zixun_detail_dianzan);
        dianzanLayout.setOnClickListener(this);
        dianzanImg = (ImageView) findViewById(R.id.zixun_detail_dianzan_img);
        dianzan = (TextView) findViewById(R.id.zixun_detail_dianzan_text);
        dianzanLayout.setVisibility(View.GONE);

        //点赞
        back = (ImageView) this.findViewById(R.id.zixun_detail_leftImg);
        title = (TextView) findViewById(R.id.zixun_detail_title);
        time = (TextView) findViewById(R.id.zixun_detail_time);
        user = (TextView) findViewById(R.id.zixun_detail_user);
        content = (myWebView) findViewById(R.id.zixun_detail_content);
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
                imgReset();
                addImageClickListner();
                ProgressUtil.dismiss();
                Log.w(TAG, "onPageFinished: 重置图片");
            }

        });
        JavascriptInterface js = new JavascriptInterface(this);
        content.addJavascriptInterface(js, "addUrl");
        content.addJavascriptInterface(js, "imagelistener");
        content.setOnLongClickListener(new myWebView.onLongClickListener() {
            @Override
            public void onLongClcik(String imgUrl) {
                Glide.with(ZiXun_Detail.this).load(imgUrl).asBitmap().skipMemoryCache(true).override(400, 400).into(new SimpleTarget<Bitmap>() {
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
                                Toast.makeText(ZiXun_Detail.this, mApplication.ST("无法识别,请确认当前页面是否有二维码图片"), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
            }
        });
        PlListVIew = (mPLlistview) findViewById(R.id.zixun_Detail_PL_listview);
        PlListVIew.setFooterDividersEnabled(false);
        PlListVIew.footer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                PlListVIew.footer.setText(mApplication.ST("正在加载"));
                if (!endPage.equals(page)) page = String.valueOf(Integer.valueOf(page) + 1);
                getPLandSet(FirstMap);
            }
        });
        imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        options = (FrameLayout) findViewById(R.id.zixun_item_option);

        back.setOnClickListener(this);
        plNum.setOnClickListener(this);
        fasong.setOnClickListener(this);
        shoucang.setOnClickListener(this);

        fenxiang2.setOnClickListener(this);
        //分享
//        share_list = new SHARE_MEDIA[]{
//                SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE,
//        };
//        img_layout = (LinearLayout) findViewById(R.id.img_layout);
        final String url = getIntent().getStringExtra("video_url");
        String image = getIntent().getStringExtra("image");
        if (url != null) {
            if (url.endsWith(".mp4") || url.endsWith(".m3u8")) {
                //视频
//                JCVideoPlayerStandard webVieo = new JCVideoPlayerStandard(ZiXun_Detail.this);
//                webVieo.setUp(url,
//                        JCVideoPlayer.SCREEN_LAYOUT_LIST, getIntent().getStringExtra("title"));
//                Glide.with(ZiXun_Detail.this)
//                        .load(getIntent().getStringExtra("image"))
//                        .override(screenWidth, DimenUtils.dip2px(this, 180))
//                        .centerCrop()
//                        .into(webVieo.thumbImageView);
////                ViewGroup.LayoutParams ll = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DimenUtils.dip2px(this, 200));
////                AbsoluteLayout.LayoutParams layoutParams = new AbsoluteLayout.LayoutParams(ll);
//                options.addView(webVieo);
//                webVieo.titleTextView.setVisibility(View.GONE);
                ImageView imageView = new ImageView(this);
                FrameLayout.LayoutParams l = new FrameLayout.LayoutParams(screenWidth, DimenUtils.dip2px(this, 200));
                imageView.setBackgroundColor(Color.BLACK);
                imageView.setLayoutParams(l);
                Glide.with(ZiXun_Detail.this)
                        .load(getIntent().getStringExtra("image"))
                        .override(screenWidth - DimenUtils.dip2px(this, 20), DimenUtils.dip2px(this, 180))
                        .centerCrop()
                        .into(imageView);
                final ImageView start = new ImageView(this);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(DimenUtils.dip2px(this, 50), DimenUtils.dip2px(this, 50));
                layoutParams.gravity = Gravity.CENTER;
                start.setLayoutParams(layoutParams);
                start.setBackgroundResource(R.drawable.jc_play_normal);
                imageView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ZiXun_Detail.this, Video_Detail.class);
                        intent.putExtra("url", url);
                        intent.putExtra("title", getIntent().getStringExtra("title"));
                        startActivity(intent);
                    }
                });
                options.addView(imageView);
                options.addView(start);
            }
        }
        id = getIntent().getStringExtra("id");
        if (mApplication.ZiXun.contains(id)) {
            shoucang.setSelected(true);
        }
    }

    @Override
    public void onHuifuClicked(String id, int p, View v, String name) {
        // TODO: 2016/12/27 评论回复接口
        overlay.setVisibility(View.VISIBLE);
        isPLing = true;
        currentLayout = (LinearLayout) v;
        currentPosition = p;
        currentId = id;
        SpannableString ss = new SpannableString("回复 " + name + " :");
        ss.setSpan(new ForegroundColorSpan(Color.BLACK), 3, name.length() + 3, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        PLText.setHint(ss);
        PLText.requestFocus();


        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

//
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
    protected void onPause() {
        super.onPause();

        content.onPause();
        JCVideoPlayer.releaseAllVideos();
        ProgressUtil.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        content.onResume();
    }

    @Override
    public void onBackPressed() {
        if (JCVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.zixun_detail_shoucang://收藏
                if (!new LoginUtil().checkLogin(this)) {
                    return;
                }
                if (!Network.HttpTest(this)) {
                    Toast.makeText(ZiXun_Detail.this, mApplication.ST("请检查网络连接"), Toast.LENGTH_SHORT).show();
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String data = OkGo.post(Constants.News_SC_Ip).params("key", Constants.safeKey).params("newsid", id)
                                    .params("user_id", sp.getString("user_id", "")).execute().body().string();
                            if (!data.equals("")) {
                                if (AnalyticalJSON.getHashMap(data).get("code").equals("000")) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ZiXun_Detail.this, mApplication.ST("添加收藏成功"), Toast.LENGTH_SHORT).show();
                                            v.setSelected(true);
                                            needTochange = true;
                                            if (!mApplication.ZiXun.contains(id)) {
                                                mApplication.ZiXun.add(id);
                                            }

                                        }
                                    });
                                } else if (AnalyticalJSON.getHashMap(data).get("code").equals("002")) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ZiXun_Detail.this, mApplication.ST("已取消收藏"), Toast.LENGTH_SHORT).show();
                                            v.setSelected(false);
                                            needTochange = true;
                                            if (mApplication.ZiXun.contains(id)) {
                                                mApplication.ZiXun.remove(id);
                                            }

                                        }
                                    });
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ZiXun_Detail.this, mApplication.ST("服务器异常"), Toast.LENGTH_SHORT).show();
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

            case R.id.zixun_detail_fenxiang2://底部分享
                imm.hideSoftInputFromWindow(PLText.getWindowToken(), 0);
                fenxiangb.performClick();
                break;
            case R.id.zixun_detail_leftImg://返回
                finish();
                break;
            case R.id.zixun_detail_dianzan://资讯点赞
                dianzanLayout.setEnabled(false);
                if (sp.getString("user_id", "").equals("") && sp.getString("uid", "").equals("")) {
                    Intent intent = new Intent(this, Login.class);
                    startActivity(intent);
                    Toast.makeText(ZiXun_Detail.this, mApplication.ST("请先登录"), Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String data = OkGo.post(Constants.ZX_DZ_IP).params("news_id", id).params("user_id", sp.getString("user_id", ""))
                                        .params("key", Constants.safeKey).execute().body().string();
                                if (!data.equals("")) {
                                    HashMap<String, String> map = AnalyticalJSON.getHashMap(data);
                                    if (map != null && map.get("code").equals("000")) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                dianzan.setText((Integer.valueOf(dianzan.getText().toString()) + 1) + "");
                                                dianzanImg.setImageResource(R.drawable.dianzan1);
                                                dianzan.setTextColor(getResources().getColor(R.color.main_color));
                                                dianzanLayout.setSelected(true);
                                            }
                                        });

                                    } else {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                dianzanImg.setImageResource(R.drawable.dianzan1);
                                                dianzan.setTextColor(getResources().getColor(R.color.main_color));
                                                dianzanLayout.setSelected(true);
                                                Toast.makeText(ZiXun_Detail.this, mApplication.ST("已点过赞啦"), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }).start();
                }


                break;
            case R.id.zixun_detail_fasong://发送提交评论
                if (!new LoginUtil().checkLogin(ZiXun_Detail.this)) {
                    return;
                }
                if (PLText.getText().toString().trim().equals("")) {
                    Toast.makeText(this, "请输入评论", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (sp.getString("pet_name", "").trim().equals("")) {
                    Toast.makeText(this, "请完善信息", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, Mine_gerenziliao.class);
                    startActivity(intent);
                    return;
                }
                v.setEnabled(false);
                ProgressUtil.show(this, "", "正在提交");
                if (!isPLing) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final String content = PLText.getText().toString();
                                final String data = OkGo.post(Constants.News_PL_add_IP).params("user_id", sp.getString("user_id", "")).params("news_id", id)
                                        .params("ct_contents", content).params("key", Constants.safeKey).params("m_id", Constants.M_id).execute().body().string();
                                if (data != null & !data.equals("")) {
                                    Log.i(TAG, "run:      data------>" + data);
                                    final HashMap<String, String> hashMap = AnalyticalJSON.getHashMap(data);
                                    if (hashMap != null && "000".equals(hashMap.get("code"))) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                final HashMap<String, String> map = new HashMap<>();
                                                String headurl = sp.getString("head_path", "").equals("") ? sp.getString("head_url", "") : sp.getString("head_path", "");
                                                final String time = TimeUtils.getStrTime(System.currentTimeMillis() + "");
                                                String petname = sp.getString("pet_name", "");
                                                String diazannum = "0";
                                                map.put("user_image", headurl);
                                                map.put("ct_contents", content);
                                                map.put("pet_name", petname);
                                                map.put("ct_ctr", diazannum);
                                                map.put("ct_time", time);
                                                map.put("id", hashMap.get("id"));
                                                map.put("reply", new JSONArray().toString());
                                                PlListVIew.setFocusable(true);
                                                if (adapter.mlist.size() == 0) {
                                                    adapter.mlist.add(0, map);
                                                    adapter.flagList.add(0, false);
                                                    PlListVIew.removeHeaderView(tv);
                                                    PlListVIew.setAdapter(adapter);

                                                } else {
                                                    adapter.mlist.add(0, map);
                                                    adapter.flagList.add(0, false);
                                                    adapter.notifyDataSetChanged();

                                                }
                                                PlListVIew.setSelection(0);
                                                v.setEnabled(true);
                                                firstNum += 1;
                                                plNum.setText("评论 " + firstNum);
                                                PLText.setText("");
                                                imm.hideSoftInputFromWindow(PLText.getWindowToken(), 0);
                                                overlay.setVisibility(View.GONE);
                                                Toast.makeText(ZiXun_Detail.this, "添加评论成功", Toast.LENGTH_SHORT).show();
                                                ProgressUtil.dismiss();
                                            }
                                        });
                                    }
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            v.setEnabled(true);
                                            Toast.makeText(ZiXun_Detail.this, "上传评论失败，请重新尝试", Toast.LENGTH_SHORT).show();
                                            ProgressUtil.dismiss();
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final String content = PLText.getText().toString();
                                final String data = OkGo.post(Constants.little_zixun_pl_add_IP).params("user_id", sp.getString("user_id", "")).params("ct_id", currentId)
                                        .params("ct_contents", content).params("key", Constants.safeKey).params("m_id", Constants.M_id).execute().body().string();
                                if (!data.equals("")) {
                                    final HashMap<String, String> hashMap = AnalyticalJSON.getHashMap(data);
                                    if (hashMap != null && "000".equals(hashMap.get("code"))) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (currentLayout.getVisibility() == View.GONE) {
                                                    currentLayout.setVisibility(View.VISIBLE);
                                                }
                                                TextView textView = new TextView(ZiXun_Detail.this);
                                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                                layoutParams.setMargins(0, DimenUtils.dip2px(ZiXun_Detail.this, 5), 0, DimenUtils.dip2px(ZiXun_Detail.this, 5));
                                                textView.setLayoutParams(layoutParams);
                                                String pet_name = sp.getString("pet_name", "");
                                                SpannableStringBuilder ssb = new SpannableStringBuilder(pet_name + ":" + content);
                                                ssb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(ZiXun_Detail.this, R.color.main_color)), 0, pet_name.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                                                textView.setText(ssb);
                                                currentLayout.addView(textView);
                                                PLText.setText("");
                                                PLText.setHint("写入您的评论（100字以内）");
                                                overlay.setVisibility(View.GONE);
                                                imm.hideSoftInputFromWindow(PLText.getWindowToken(), 0);
                                                PlListVIew.setSelection(currentPosition);
                                                fasong.setEnabled(true);
                                                isPLing = false;
                                                try {
                                                    JSONArray jsonArray = new JSONArray(adapter.mlist.get(currentPosition).get("reply"));
                                                    JSONObject jsonObject = new JSONObject();
                                                    jsonObject.put("id", hashMap.get("id"));
                                                    jsonObject.put("pet_name", pet_name);
                                                    jsonObject.put("ct_contents", content);
                                                    jsonObject.put("user_id", sp.getString("user_id", ""));
                                                    jsonArray.put(jsonObject);
                                                    adapter.mlist.get(currentPosition).put("reply", jsonArray.toString());
                                                    adapter.notifyDataSetChanged();
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                ProgressUtil.dismiss();
                                            }
                                        });
                                    }
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            v.setEnabled(true);
                                            Toast.makeText(ZiXun_Detail.this, "回复提交失败，请重新尝试", Toast.LENGTH_SHORT).show();
                                            ProgressUtil.dismiss();
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
                break;
        }
    }


    public void LoadData() {//加载详情数据

        if (!id.equals("") && id != null) {
            ProgressUtil.show(this, "", "正在加载");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String data1 = null;
                    try {
                        data1 = OkGo.get(Constants.ZiXun_detail_Ip).tag(TAG).params("news_id", id).execute().body().string();
                    } catch (Exception e) {
                        e.printStackTrace();
                        ProgressUtil.dismiss();
                    }
                    Log.e(TAG, "run: data1-------->" + data1);
                    if (data1 != null && !data1.equals("")) {
                        FirstMap = AnalyticalJSON.getHashMap(data1);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (FirstMap == null) {
                                    ProgressUtil.dismiss();
                                    Toast.makeText(ZiXun_Detail.this, mApplication.ST("数据加载失败，请重新打开页面"), Toast.LENGTH_SHORT).show();
                                    finish();
                                    return;
                                }
                                final String url = getIntent().getStringExtra("video_url");
//                                    if(null!=url){
//                                        if(url.endsWith("mp4")){
//                                            stub.setLayoutResource(R.layout.video_stub);
//                                            View view=  stub.inflate();
//                                            player= (JCVideoPlayerStandard) view.findViewById(R.id.zixun_detail_player);
//                                            Log.w(TAG, "initView: player-=-"+player+"  getIntent().getStringExtra(\"video_url\")" +getIntent().getStringExtra("video_url")
//                                                    +"   "+getIntent().getStringExtra("title"));
//                                            player.setUp(url,getIntent().getStringExtra("title"));
//                                            player.titleTextView.setVisibility(View.GONE);
//                                            Glide.with(ZiXun_Detail.this).load(getIntent().getStringExtra("image"))
//                                                    .override(screenWidth- DimenUtils.dip2px(ZiXun_Detail.this,10),(screenWidth- DimenUtils.dip2px(ZiXun_Detail.this,10))/2)
//                                                    .centerCrop()
//                                                    .into(player.thumbImageView);
//                                        }else if(url.endsWith("mp3")){
//                                            stub.setLayoutResource(R.layout.audio_stub);
//                                            View view=  stub.inflate();
//                                            final mAudioView mAudioView= (mAudioView) view.findViewById(R.id.audioView);
//                                            mAudioView.mTime.setGravity(Gravity.CENTER);
//                                            mAudioView.setOnImageClickListener(new mAudioView.onImageClickListener() {
//                                                @Override
//                                                public void onImageClick() {
//                                                    if(!mAudioView.isPlaying()){
//                                                        mAudioManager.playSound(url, new MediaPlayer.OnCompletionListener() {
//                                                            @Override
//                                                            public void onCompletion(MediaPlayer mp) {
//
//                                                            }
//                                                        }, new MediaPlayer.OnPreparedListener() {
//                                                            @Override
//                                                            public void onPrepared(MediaPlayer mp) {
//                                                                mAudioView.setTime(mp.getDuration()/1000);
//                                                            }
//                                                        });
//                                                    }else{
//                                                        mAudioManager.release();
//                                                    }
//
//                                                }
//                                            });
//                                        }
//                                    }
                                String images = FirstMap.get("image1");
//                                if (images != null && !images.equals("")) {
//                                    List<HashMap<String, String>> l = AnalyticalJSON.getList_zj(images);
//                                    if (l != null) {
//                                        for (HashMap map : l) {
//                                            Log.w(TAG, "run: 开始循环图片");
//                                            ImageView i = new ImageView(getApplicationContext());
//                                            i.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//                                            i.setPadding(0, DimenUtils.dip2px(ZiXun_Detail.this, 10), 0, DimenUtils.dip2px(ZiXun_Detail.this, 10));
//                                            Glide.with(ZiXun_Detail.this).load(map.get("url")).override(screenWidth - DimenUtils.dip2px(ZiXun_Detail.this, 10), (screenWidth - DimenUtils.dip2px(ZiXun_Detail.this, 10)) / 2).centerCrop().placeholder(R.drawable.place_holder2).into(i);
//                                            img_layout.addView(i);
//                                        }
//                                    }
//                                }
                                title.setText(mApplication.ST(FirstMap.get("title")));
                                time.setText(TimeUtils.getTrueTimeStr(FirstMap.get("time")));
                                user.setText(mApplication.ST("发布人:" + FirstMap.get("issuer")));
                                try {
                                    content.loadDataWithBaseURL(Constants.IMGDIR + TAG + "/" + TimeUtils.getStrTime(System.currentTimeMillis() / 1000 + "") + ".jpg", mApplication.ST(FirstMap.get("contents"))
                                            , "text/html", "UTF-8", null);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                ProgressUtil.dismiss();
                            }
                        });
//
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                    content.setText(var1);
                                dianzanLayout.setVisibility(View.VISIBLE);
                                Log.w(TAG, "run: like s-=-=-=-=-=" + FirstMap.get("likes"));
                                dianzan.setText(FirstMap.get("likes"));
                                plNum.setVisibility(View.VISIBLE);
                                if (FirstMap.get("news_comment") != null) {
                                    plNum.setText(mApplication.ST("评论 " + FirstMap.get("news_comment")));
                                    firstNum = Integer.valueOf(FirstMap.get("news_comment"));
                                } else {
                                    firstNum = 0;
                                    plNum.setText(mApplication.ST("评论"));
                                }

//                                    if (loading.isShown()) {
//                                        loading.clearAnimation();
//                                        loading.setVisibility(GONE);
////                                    }
//                                    //使用的是测试分享地址
//                                    ShareContent shareContent = new ShareContent();
//                                    shareContent.mFollow = getIntent().getStringExtra("id");
//                                    shareContent.mTitle = FirstMap.get("title");
//                                    shareContent.mText =FirstMap.get("abstract");
//                                    shareContent.mTargetUrl = Constants.FX_host_Ip + TAG + "/id/" + shareContent.mFollow;
//                                    shareContent.mMedia=new UMImage(ZiXun_Detail.this,FirstMap.get("image"));
//                                    action = new ShareAction(ZiXun_Detail.this);
//
//
//                                    action.setDisplayList(share_list)
//                                            .setShareContent(shareContent)
//                                            .setListenerList(umShareListener);
//                                    //使用的是测试分享地址
                            }
                        });

                        getPLandSet(FirstMap);
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ProgressUtil.dismiss();

                            }
                        });
                    }
                }
            }).start();
        }
    }

//    UMShareListener umShareListener = new UMShareListener() {
//        @Override
//        public void onResult(SHARE_MEDIA share_media) {
//
//        }
//
//        @Override
//        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
//
//        }
//
//        @Override
//        public void onCancel(SHARE_MEDIA share_media) {
//
//        }
//    };

    private void getPLandSet(final HashMap<String, String> map) {//加载评论并设置
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String data = OkGo.post(Constants.ZiXun_detail_PL_Ip).tag(TAG).params("page", page)
                            .params("news_id", id.equals("") ? getIntent().getStringExtra("id") : id).execute().body().string();
                    if (!data.equals("")) {
                        Log.w(TAG, "run:      PLdata------>" + data);
                        Pllist = AnalyticalJSON.getList(data, "comment");
                        if ((Pllist != null)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    PlListVIew.setFocusable(false);
                                    if (adapter.mlist.size() == 0 && Pllist.size() == 0) {//没有评论的时候
                                        tv = new TextView(ZiXun_Detail.this);
                                        tv.setText(mApplication.ST("还没有评论嘞"));
                                        PlListVIew.addHeaderView(tv);
                                        PlListVIew.footer.setVisibility(View.GONE);
                                        PlListVIew.setAdapter(adapter);

                                        return;
                                    }
                                    if (adapter.mlist.size() == 0) {//添加评论的的时候
                                        adapter.addList(Pllist);
                                        PlListVIew.setAdapter(adapter);
                                        plNum.setText(mApplication.ST("评论 " + map.get("news_comment")));
                                        if (Pllist.size() < 10) {
                                            endPage = page;
                                            PlListVIew.footer.setText(mApplication.ST("没有更多数据了"));
                                            PlListVIew.footer.setEnabled(false);
                                        } else {
                                            PlListVIew.footer.setText(mApplication.ST("点击加载更多"));
                                        }
                                    } else {
                                        adapter.mlist.addAll(Pllist);
                                        boolean flag = false;
                                        for (int i = 0; i < Pllist.size(); i++) {
                                            adapter.flagList.add(flag);
                                        }
                                        adapter.notifyDataSetChanged();
                                        plNum.setText("评论 " + map.get("news_comment"));
                                        if (Pllist.size() < 10) {
                                            endPage = page;
                                            PlListVIew.footer.setText(mApplication.ST("没有更多数据了"));
                                            PlListVIew.footer.setEnabled(false);
                                        } else {
                                            PlListVIew.footer.setText(mApplication.ST("点击加载更多"));
                                            PlListVIew.footer.setEnabled(true);
                                        }
                                    }
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    PlListVIew.footer.setText(mApplication.ST("没有更多数据了"));
                                    PlListVIew.footer.setEnabled(false);
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


    @Override
    protected void onDestroy() {

        OkGo.getInstance().cancelTag(TAG);
        JCVideoPlayer.releaseAllVideos();

        UMShareAPI.get(this).release();

        if (content != null) {
            // 如果先调用destroy()方法，则会命中if (isDestroyed()) return;这一行代码，需要先onDetachedFromWindow()，再
            // destory()
            ViewParent parent = content.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(content);
            }

            content.stopLoading();
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            content.getSettings().setJavaScriptEnabled(false);
            content.clearHistory();
            content.clearView();
            content.removeAllViews();

            try {
                content.destroy();
            } catch (Throwable ex) {

            }
        }
        super.onDestroy();
    }
}
