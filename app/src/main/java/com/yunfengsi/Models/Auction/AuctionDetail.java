package com.yunfengsi.Models.Auction;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
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
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.zxing.Result;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.ruffian.library.RTextView;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;
import com.yunfengsi.Audio_BD.WakeUp.Recognizelmpl.IBDRcognizeImpl;
import com.yunfengsi.R;
import com.yunfengsi.Setting.AD;
import com.yunfengsi.Setting.Mine_gerenziliao;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.ImageUtil;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.LoginUtil;
import com.yunfengsi.Utils.NumUtils;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.ProgressUtil;
import com.yunfengsi.Utils.QrUtils;
import com.yunfengsi.Utils.ScaleImageUtil;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.TimeUtils;
import com.yunfengsi.Utils.ToastUtil;
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
public class AuctionDetail extends AppCompatActivity implements View.OnClickListener, BaseQuickAdapter.UpFetchListener {
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


    private int     pageSize   = 20;
    private int     page       = 1;
    private int     endPage    = -1;
    private boolean isLoadMore = false;
    private MessageAdapter adapter;
    private Context        context;


    private EditText     PLText;
    private LinearLayout pinglun, fenxiangb;
    private FrameLayout        overlay;
    private TextView           audio;
    private IBDRcognizeImpl    ibdRcognize;
    private ImageView          toggle;
    private InputMethodManager imm;
    private TextView           fasong;


    private HashMap<String, String> detailMap;
    double finalPrice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.auction_detail);
        context = this;
        id = getIntent().getStringExtra("id");
        initViews();
        initWebView();
        comments = findViewById(R.id.commentsView);
        comments.setLayoutManager(new ScrollSpeedLinearLayoutManger(this));

        commentsList = new ArrayList<>();

        adapter = new MessageAdapter(this, commentsList);
        adapter.setUpFetchEnable(true);
        adapter.setUpFetchListener(this);

        adapter.setEmptyView(mApplication.getEmptyView(this, 100, "暂无评论"));

        comments.setFocusable(false);
        comments.setNestedScrollingEnabled(false);
        getPl();
        getDetail();
    }

    private void getPl() {
        JSONObject js = new JSONObject();
        try {
            js.put("m_id", Constants.M_id);
            js.put("auction_id", id);
            js.put("page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m = ApisSeUtil.i(js);
        LogUtil.e("获取义卖留言列表：：" + js);
        OkGo.post(Constants.AuctionCommentList)
                .tag(this)
                .params("key", m.K())
                .params("msg", m.M())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        HashMap<String, String> map = AnalyticalJSON.getHashMap(s);
                        if (map != null) {
                            final ArrayList<HashMap<String, String>> list = AnalyticalJSON.getList_zj(map.get("msg"));
                            if (list != null) {
                                if (isLoadMore) {
                                    isLoadMore = false;
                                    if (list.size() < pageSize) {
                                        endPage = page;
                                        adapter.addData(0, list);
                                    } else {
                                        adapter.addData(0, list);
                                    }
                                } else {
                                    if (comments.getAdapter() == null) {
                                        comments.setAdapter(adapter);
                                    }
                                    if (list.size() < pageSize) {
                                        endPage = page;
                                    }
                                    adapter.setNewData(list);
                                }
                            }
                            comments.scrollToPosition(adapter.getData().size()-1);
                        }
                    }

                });
    }

    private void initViews() {
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.share).setOnClickListener(this);
        findViewById(R.id.collect).setOnClickListener(this);
        slide = findViewById(R.id.slide);
        /**
         * slide状态判断
         */
        slide.setOnSlideDetailsListener(new SlideDetailsLayout.OnSlideDetailsListener() {
            @Override
            public void onStatucChanged(SlideDetailsLayout.Status status) {
                if (status == SlideDetailsLayout.Status.CLOSE) {
                    findViewById(R.id.topLayout).animate().translationY(0)
                            .setDuration(400)
                            .start();
                    findViewById(R.id.line).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.topLayout).animate().translationY(findViewById(R.id.topLayout).getHeight())
                            .setDuration(400)
                            .start();
                    findViewById(R.id.line).setVisibility(View.GONE);
                }
            }

            @Override
            public void onScrollChanged() {

            }
        });
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
                        .override(screenWidth, screenWidth / 2)
                        .centerCrop()
                        .into(imageView);
            }
        });
        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                ScaleImageUtil.openBigIagmeMode(AuctionDetail.this,imageUrls,position,true);
            }
        });
        behind = ((myWebView) findViewById(R.id.bebind));


        imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        PLText = (EditText) findViewById(R.id.zixun_detail_apply_edt);
        PLText.setHint(mApplication.ST("写入你的评论(300字以内)"));
        Glide.with(this).load(R.drawable.pinglun).skipMemoryCache(true).override(DimenUtils.dip2px(this, 25), DimenUtils.dip2px(this, 25))
                .into((ImageView) findViewById(R.id.pinglun_image));
        Glide.with(this).load(R.drawable.auction_bid).skipMemoryCache(true).override(DimenUtils.dip2px(this, 25), DimenUtils.dip2px(this, 25))
                .into((ImageView) findViewById(R.id.fenxiang_image));
        toggle = (ImageView) findViewById(R.id.toggle_audio_word);
        audio = (TextView) findViewById(R.id.audio_button);
        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle.setSelected(!view.isSelected());
                if (toggle.isSelected()) {
                    audio.setVisibility(View.VISIBLE);
                    PLText.setVisibility(View.GONE);
                } else {
                    audio.setVisibility(View.GONE);
                    PLText.setVisibility(View.VISIBLE);
                }
            }
        });
        audio.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        if (ibdRcognize == null) {
                            ibdRcognize = new IBDRcognizeImpl(AuctionDetail.this);
                            ibdRcognize.attachView(PLText, audio, toggle);
                        }
                        view.setSelected(true);
                        audio.setText("松开完成识别");
                        ibdRcognize.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        view.setSelected(false);
                        audio.setText("按住 说话");
                        ibdRcognize.stop();
                        break;
                }
                return true;
            }
        });
        fasong = findViewById(R.id.fasong);
        fasong.setText("发送");
        fasong.setOnClickListener(this);
        overlay = (FrameLayout) findViewById(R.id.frame);
        overlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PLText.setHint(mApplication.ST("写入您的评论（300字以内）"));
                imm.hideSoftInputFromWindow(PLText.getWindowToken(), 0);
                v.setVisibility(View.GONE);
            }
        });
        pinglun = (LinearLayout) findViewById(R.id.pinglun);
        pinglun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlay.setVisibility(View.VISIBLE);
                PLText.requestFocus();
                PLText.setHint(mApplication.ST("写入您的评论（300字以内）"));
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

            }
        });
        ((TextView) findViewById(R.id.pltv)).setText(mApplication.ST("评论"));
        ((TextView) findViewById(R.id.fxtv)).setText(mApplication.ST("出价"));
        fenxiangb = (LinearLayout) findViewById(R.id.fenxiangb);
        fenxiangb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long Lstart = TimeUtils.dataOne(detailMap.get("start_time"));
                long Lend   = TimeUtils.dataOne(detailMap.get("end_time"));
                if(Lstart <= System.currentTimeMillis()&&System.currentTimeMillis()<Lend){
                    postBid();
                }else if(Lstart > System.currentTimeMillis()){
                    ToastUtil.showToastShort("竞拍尚未开始");
                }else{
                    ToastUtil.showToastShort("竞拍已结束");
                }


            }
        });
    }

    /**
     * 出价
     */
    private void postBid() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("确定参与竞价吗?").setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                JSONObject js = new JSONObject();
                try {
                    js.put("m_id", Constants.M_id);
                    js.put("auction_id", id);

                    js.put("user_id", PreferenceUtil.getUserId(context));

                    if (Double.valueOf(NumUtils.getNumStr(detailMap.get("now_price"))) <= 0) {
                        finalPrice = Double.valueOf(detailMap.get("bottom_price"));
                    } else {
                        finalPrice = Double.valueOf(detailMap.get("now_price")) + Double.valueOf(detailMap.get("sum_price"));
                    }
                    js.put("money", String.format("%.2f", finalPrice));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ApisSeUtil.M m = ApisSeUtil.i(js);
                LogUtil.e("出价：：：" + js);
                OkGo.post(Constants.AuctionOffer)
                        .params("key", m.K())
                        .params("msg", m.M())
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(String s, Call call, Response response) {
                                HashMap<String, String> map = AnalyticalJSON.getHashMap(s);
                                if (map != null && "000".equals(map.get("code"))) {
                                    priceNow.setText(String.format("%.2f", finalPrice));
                                    overPrice.setText("领先          " + String.format("%.2f", finalPrice));
                                    detailMap.put("now_price", String.format("%.2f", finalPrice));
                                    getDetail();
                                } else {
                                    ToastUtil.showToastShort("出价失败，请重新尝试");
                                }
                            }

                            @Override
                            public void onError(Call call, Response response, Exception e) {
                                super.onError(call, response, e);
                                ToastUtil.showToastShort("出价失败，请重新尝试");
                            }
                        });
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();

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
                        if(slide.getVisibility()==View.GONE){
                            slide.setAlpha(0);
                            slide.setVisibility(View.VISIBLE);
                            slide.animate().alpha(1)
                                    .setDuration(400)
                                    .start();
                        }
                        HashMap<String, String> m = AnalyticalJSON.getHashMap(s);

                        if (m != null) {
                            if ("000".equals(m.get("code"))) {
                                HashMap<String, String> map = AnalyticalJSON.getHashMap(m.get("msg"));
                                detailMap = map;
                                if(map.get("title")==null||"".equals(map.get("title"))){
                                    ToastUtil.showToastShort("此商品已不存在");
                                    finish();
                                    return;
                                }
                                if (imageUrls == null) {
                                    startBanner(map);
                                    checkTime(map.get("start_time"), map.get("end_time"));
                                    webConent = map.get("contents");
                                    behind.loadDataWithBaseURL("", webConent, "text/html", "utf-8", null);
                                    priceMarket.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                                    priceMarket.setText("市场价 " + NumUtils.getNumStr(map.get("market_price")));
                                    title.setText(map.get("title"));
                                }


                                priceNow.setText(map.get("now_price"));

                                circusee.setText("围观 " + map.get("ctr") + "人");
                                bid.setText("出价 " + map.get("partake") + "人");
                                collectNum.setText("收藏 " + map.get("keeps") + "人");
                                priceStart.setText("起拍价：￥" + NumUtils.getNumStr(map.get("bottom_price")));
                                pricePerAdd.setText("加价幅度：￥" + NumUtils.getNumStr(map.get("sum_price")));
                                priceHistoryNum.setText(map.get("num") + "条");
                                petName.setText(PreferenceUtil.getUserIncetance(AuctionDetail.this).getString("pet_name", ""));

                                ArrayList<HashMap<String, String>> users = AnalyticalJSON.getList_zj(map.get("auction_user"));
                                if (users == null || users.size() == 0) {
                                    petName.setText("暂无人出价");
                                } else {
                                    Glide.with(context).load(users.get(0).get("user_image"))
                                            .asBitmap()
                                            .override(DimenUtils.dip2px(context, 30), DimenUtils.dip2px(context, 30))
                                            .into(new SimpleTarget<Bitmap>() {
                                                @Override
                                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                                    RoundedBitmapDrawable rbd = RoundedBitmapDrawableFactory.create(getResources(), resource);
                                                    rbd.setCircular(true);
                                                    ((RTextView) petName).setIconNormal(rbd);
                                                }
                                            });
                                    petName.setText(users.get(0).get("pet_name"));
                                    overPrice.setText("领先          " + users.get(0).get("money"));
                                }
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
            auctionStatus = "竞价中";
            time_tip.setText(auctionStatus + "\n" + start + " 至 " + end);
        } else if (Lend <= System.currentTimeMillis()) {
            auctionStatus = "竞价结束";
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
                if (lend > System.currentTimeMillis()) {
                    final StringBuilder ss = new StringBuilder();
                    ss.append(auctionStatus);
                    ss.append("      ");
                    String timeOffset = TimeUtils.formatTimeShort(lend - System.currentTimeMillis(), false);
                    ss.append("预计 ");
                    ss.append(timeOffset);
                    ss.append(" 后开始");
                    time_tip.post(new Runnable() {
                        @Override
                        public void run() {
                            time_tip.setText(ss.toString());
                        }
                    });
                } else {
                    scheduledExecutorService.shutdownNow();
                    time_tip.post(new Runnable() {
                        @Override
                        public void run() {
                            time_tip.setText("竞拍结束");
                        }
                    });

                }

            }
        }, 0, 1, TimeUnit.SECONDS);
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
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.fasong:
                if (!new LoginUtil().checkLogin(context)) {
                    return;
                }
                if (PLText.getText().toString().trim().equals("")) {
                    Toast.makeText(this, mApplication.ST("请输入评论"), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (PreferenceUtil.getUserIncetance(context).getString("pet_name", "").trim().equals("")) {
                    Toast.makeText(this, mApplication.ST("请完善信息"), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, Mine_gerenziliao.class);
                    startActivity(intent);
                    return;
                }
                v.setEnabled(false);
                ProgressUtil.show(this, "", mApplication.ST("正在提交"));

                final String content = PLText.getText().toString();
                JSONObject js = new JSONObject();
                try {
                    js.put("user_id", PreferenceUtil.getUserIncetance(context).getString("user_id", ""));
                    js.put("ct_contents", content);
                    js.put("auction_id", id);
                    js.put("m_id", Constants.M_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                LogUtil.e("义卖评论：：：" + js);
                ApisSeUtil.M m = ApisSeUtil.i(js);
                OkGo.post(Constants.AuctionCommentPost)
                        .params("key", m.K())
                        .params("msg", m.M()).execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        final HashMap<String, String> hashMap = AnalyticalJSON.getHashMap(s);
                        if (hashMap != null && "000".equals(hashMap.get("code"))) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                    if (!"0".equals(hashMap.get("yundousum"))) {
//                                        YunDouAwardDialog.show(AuctionDetail.this, "每日评论", hashMap.get("yundousum"));
//                                    }
                                    ToastUtil.showToastShort(mApplication.ST(getString(R.string.commitCommentSuccess)));

//                                    final HashMap<String, String> map     = new HashMap<>();
//                                    String                        petname = PreferenceUtil.getUserIncetance(context).getString("pet_name", "");
//
//                                    map.put("ct_contents", content);
//                                    map.put("pet_name", petname);
//                                    if (PreferenceUtil.getUserIncetance(context).getString("role", "").equals("3")) {
//                                        map.put("role", "3");
//                                    } else {
//                                        map.put("role", "0");
//                                    }
//                                    if (adapter.getData().size() == 0) {
//                                        adapter.addData(map);
//                                    } else {
//                                        adapter.addData(map);
//                                    }
//                                    comments.scrollToPosition(adapter.getData().size() - 1);
                                    v.setEnabled(true);
                                    PLText.setText("");
                                    imm.hideSoftInputFromWindow(PLText.getWindowToken(), 0);
                                    overlay.setVisibility(View.GONE);
                                    ProgressUtil.dismiss();
                                }
                            });

                        } else {
                            v.setEnabled(true);
                            Toast.makeText(AuctionDetail.this, mApplication.ST("上传评论失败，请重新尝试"), Toast.LENGTH_SHORT).show();
                            ProgressUtil.dismiss();
                        }

                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        v.setEnabled(true);
                        Toast.makeText(AuctionDetail.this, mApplication.ST("上传评论失败，请重新尝试"), Toast.LENGTH_SHORT).show();
                    }
                });


                break;
            case R.id.moreHistory:
                Intent intent = new Intent();
                intent.setClass(this, Bid_History.class);
                intent.putExtra("id", id);
                startActivity(intent);
                break;
            case R.id.share:
                break;
            case R.id.collect:
                break;
            case R.id.back:
                finish();
                break;
        }
    }

    @Override
    public void onUpFetch() {
        LogUtil.e("上拉加载");
        if (endPage != page) {
            page++;
            isLoadMore = true;
            getPl();
        } else {
            LogUtil.e("加载完毕");
        }

    }


    private class MessageAdapter extends BaseQuickAdapter<HashMap<String, String>, BaseViewHolder> {


        public MessageAdapter(Context context, List<HashMap<String, String>> data) {
            super(R.layout.item_auction_detail, data);


        }

        @Override
        protected void convert(BaseViewHolder holder, HashMap<String, String> map) {
            holder.setText(R.id.item_user, map.get("pet_name") + " : ")
                    .setText(R.id.item_msg, map.get("ct_contents"));

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdownNow();
            scheduledExecutorService = null;
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
