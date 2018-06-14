package com.yunfengsi.Models.Auction;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
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
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.zxing.Result;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.youth.banner.Banner;
import com.youth.banner.loader.ImageLoader;
import com.yunfengsi.R;
import com.yunfengsi.Setting.AD;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.ImageUtil;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.NumUtils;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.ProgressUtil;
import com.yunfengsi.Utils.QrUtils;
import com.yunfengsi.Utils.ScaleImageUtil;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.TimeUtils;
import com.yunfengsi.Utils.mApplication;
import com.yunfengsi.View.ScrollSpeedLinearLayoutManger;
import com.yunfengsi.View.SlideDetailsLayout;
import com.yunfengsi.View.myWebView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 作者：luZheng on 2018/06/08 17:11
 */
public class AuctionDetail extends AppCompatActivity implements View.OnClickListener {
    private SlideDetailsLayout slide;
    private myWebView          behind;
    private RecyclerView       comments;
    private String             id;

    private ScheduledExecutorService           scheduledExecutorService;
    private ArrayList<HashMap<String, String>> commentsList;
    private ArrayList<String>                  imageUrls;
    private ArrayList<String>                  scaledImageList;
    private Banner                             banner;

    private boolean isResetedHeight = false;
    private String  webConent       = "";
    private int     screenWidth     = 0;

    private String auctionStatus = "即将开始";
    private TextView title, time_tip, priceNow, priceMarket, circusee, bid, collectNum, priceStart, pricePerAdd,
            priceHistoryNum, moreHistory, petName, overPrice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.auction_detail);
        id = getIntent().getStringExtra("id");
        initViews();
        initWebView();
        comments = findViewById(R.id.commentsView);
        comments.setLayoutManager(new ScrollSpeedLinearLayoutManger(this));

        commentsList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            HashMap<String, String> map = new HashMap<>();
            commentsList.add(map);
        }
        final MessageAdapter adapter = new MessageAdapter(this, commentsList);
        adapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_RIGHT);
        comments.setAdapter(adapter);
        comments.setFocusable(false);
        comments.setHasFixedSize(true);
        comments.setNestedScrollingEnabled(false);


        getDetail();
    }

    private void initViews() {
        slide = findViewById(R.id.slide);
        title = findViewById(R.id.title);
        banner = findViewById(R.id.banner);
        time_tip = findViewById(R.id.time_tip);
        priceNow = findViewById(R.id.price_now);
        priceMarket = findViewById(R.id.cost);
        circusee = findViewById(R.id.circusee);
        bid = findViewById(R.id.bid);
        collectNum = findViewById(R.id.collectNum);
        priceStart = findViewById(R.id.price_start);
        pricePerAdd = findViewById(R.id.perPriceAdd);
        priceHistoryNum = findViewById(R.id.priceHistoryNum);
        moreHistory = findViewById(R.id.moreHistory);
        moreHistory.setOnClickListener(this);
        petName = findViewById(R.id.petName);
        overPrice = findViewById(R.id.overPrice);
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        banner.setImageLoader(new ImageLoader() {
            @Override
            public void displayImage(Context context, Object path, ImageView imageView) {
                Glide.with(AuctionDetail.this).load(path)
                        .override(screenWidth, screenWidth / 4)
                        .into(imageView);
            }
        });
        behind = ((myWebView) findViewById(R.id.bebind));
    }

    private void initWebView() {
        WebSettings webSettings = behind.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setAppCacheEnabled(true);
//        //提高网页加载速度，暂时阻塞图片加载，然后网页加载好了，在进行加载图片
        webSettings.setBlockNetworkImage(true);
        if (Build.VERSION.SDK_INT >= 19) {
            webSettings.setLoadsImagesAutomatically(true);
        } else {
            webSettings.setLoadsImagesAutomatically(false);
        }
        behind.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                imgReset();
                addImageClickListner();
                ProgressUtil.dismiss();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Intent intent = new Intent(AuctionDetail.this, AD.class);
                intent.putExtra("url", url);
                startActivity(intent);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, ImageUtil.readBitMap(AuctionDetail.this, R.drawable.indra));
            }


        });

        JavascriptInterface js = new JavascriptInterface(this);
        behind.addJavascriptInterface(js, "addUrl");
        behind.addJavascriptInterface(js, "imagelistener");
        behind.setOnLongClickListener(new myWebView.onLongClickListener() {
            @Override
            public void onLongClcik(String imgUrl) {
                Glide.with(AuctionDetail.this).load(imgUrl).asBitmap().skipMemoryCache(true).override(400, 400).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        Result result = QrUtils.handleQRCodeFormBitmap(resource);
                        if (result == null) {
                            LogUtil.w("onResourceReady: 不是二维码   " + result);
                        } else {
                            LogUtil.w("onResourceReady: 是二维码   " + result);
                            if (result.getText().toString().startsWith("http")) {
                                Uri    uri    = Uri.parse(result.getText().toString());
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(uri);
                                startActivity(intent);
                            } else {
                                Toast.makeText(AuctionDetail.this, mApplication.ST("无法识别,请确认当前页面是否有二维码图片"), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
            }
        });
    }

    private void getDetail() {
        JSONObject js = new JSONObject();
        try {
            js.put("m_id", Constants.M_id);
            js.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e("获取义卖详情" + js);
        ApisSeUtil.M m = ApisSeUtil.i(js);
        OkGo.post(Constants.AuctionDetail)
                .tag(this)
                .params("key", m.K())
                .params("msg", m.M())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        slide.setVisibility(View.VISIBLE);
                        HashMap<String, String> m = AnalyticalJSON.getHashMap(s);

                        if (m != null) {
                            if ("000".equals(m.get("code"))) {
                                HashMap<String, String> map = AnalyticalJSON.getHashMap(m.get("msg"));

                                startBanner(map);

                                checkTime(map.get("start_time"), map.get("end_time"));


                                title.setText(map.get("title"));
                                priceNow.setText(NumUtils.getNumStr(map.get("now_price")));
                                priceMarket.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG );
                                priceMarket.setText("市场价 " + NumUtils.getNumStr(map.get("market_price")));

                                circusee.setText("围观 " + map.get("ctr") + "人");
                                bid.setText("出价 " + map.get("partake") + "人");
                                collectNum.setText("收藏 " + map.get("keeps") + "人");
                                priceStart.setText("起拍价：￥" + NumUtils.getNumStr(map.get("bottom_price")));
                                pricePerAdd.setText("加价幅度：￥" + NumUtils.getNumStr(map.get("sum_price")));
                                priceHistoryNum.setText(map.get("num") + "条");
                                petName.setText(PreferenceUtil.getUserIncetance(AuctionDetail.this).getString("pet_name", ""));
                                overPrice.setVisibility(View.GONE);
                                webConent = map.get("contents");
                                behind.loadDataWithBaseURL("", webConent, "text/html", "utf-8", null);

                            }
                        }
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        ProgressUtil.show(AuctionDetail.this, "", "正在加载..");
                    }

                    @Override
                    public void onAfter(String s, Exception e) {
                        super.onAfter(s, e);
                        ProgressUtil.dismiss();
                    }
                });
    }

    private void checkTime(String start, String end) {
        long       Lstart = TimeUtils.dataOne(start);
        final long Lend   = TimeUtils.dataOne(end);

        if (Lstart > System.currentTimeMillis()) {
            auctionStatus = "即将开始";
            timeCountDown(Lend);
        } else if (Lstart < System.currentTimeMillis() && Lend > System.currentTimeMillis()) {
            auctionStatus = "正在竞拍";
            time_tip.setText(auctionStatus);
        } else if (Lend <= System.currentTimeMillis()) {
            auctionStatus = "竞拍结束";
            time_tip.setText(auctionStatus);
        }
    }

    /**
     * 倒计时
     */
    private void timeCountDown(final long lend) {
        if (scheduledExecutorService == null) {
            scheduledExecutorService = Executors.newScheduledThreadPool(1);
        }
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                LogUtil.e("定时任务");
                if(lend>System.currentTimeMillis()){
                    final StringBuilder ss = new StringBuilder();
                    ss.append(auctionStatus);
                    ss.append("      ");
                    String timeOffset = TimeUtils.formatTimeShort(lend - System.currentTimeMillis(), false);
                    ss.append("预计 ");
                    ss.append(timeOffset);
                    ss.append(" 后开始");
                    LogUtil.e("倒计时：：；"+ss.toString());
                    time_tip.post(new Runnable() {
                        @Override
                        public void run() {
                            time_tip.setText(ss.toString());
                        }
                    });
                }else{
                    scheduledExecutorService.shutdownNow();
                    time_tip.post(new Runnable() {
                        @Override
                        public void run() {
                            time_tip.setText("竞拍结束");
                        }
                    });

                }

            }
        }, 0, 1,TimeUnit.SECONDS);
    }

    private void startBanner(HashMap<String, String> map) {
        ArrayList<HashMap<String, String>> list = AnalyticalJSON.getList_zj(map.get("pic"));
        imageUrls = new ArrayList<>();
        imageUrls.add(map.get("image"));
        for (HashMap<String, String> map1 : list) {
            imageUrls.add(map1.get("url"));
        }
        banner.setImages(imageUrls);
        banner.start();
        list.clear();
    }

    @Override
    public void onClick(View v) {

    }


    private class MessageAdapter extends BaseQuickAdapter<HashMap<String, String>, BaseViewHolder> {
        Drawable next;
        int      dp30;

        public MessageAdapter(Context context, List<HashMap<String, String>> data) {
            super(R.layout.item_user_ban, data);
            dp30 = DimenUtils.dip2px(context, 20);
            next = ContextCompat.getDrawable(context, R.drawable.item_tip);
            next.setBounds(0, 0, dp30, dp30);
        }

        @Override
        protected void convert(BaseViewHolder holder, HashMap<String, String> map) {
            Glide.with(AuctionDetail.this).load(R.drawable.def).into((ImageView) holder.getView(R.id.head));
            holder.setText(R.id.name, "用户" + holder.getAdapterPosition());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(scheduledExecutorService!=null){
            scheduledExecutorService.shutdownNow();
            scheduledExecutorService=null;
        }
        OkGo.getInstance().cancelTag(this);
    }

    private void imgReset() {
        behind.loadUrl("javascript:(function(){" +
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


    /**
     * 添加图片监听
     */
    private void addImageClickListner() {
        //
        behind.loadUrl("javascript:(function(){" +
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

    public class JavascriptInterface {
        private Context context;

        public JavascriptInterface(Context context) {
            this.context = context;
        }

        @android.webkit.JavascriptInterface
        public void openImage(String img) {
            if (scaledImageList != null) {
                ScaleImageUtil.openBigIagmeMode(AuctionDetail.this, scaledImageList, scaledImageList.indexOf(img), true);
                LogUtil.e("openImage: 网页图片地址" + img + "页码：" + scaledImageList.indexOf(img));
            }

        }

        @android.webkit.JavascriptInterface
        public void addUrlToList(String img) {
            if (scaledImageList == null) {
                scaledImageList = new ArrayList<String>();
            }
            scaledImageList.add(img);
        }
    }

}
