package com.yunfengsi.Model_zhongchou;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.zxing.Result;
import com.lzy.okgo.OkGo;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;
import com.yunfengsi.Adapter.PL_List_Adapter;
import com.yunfengsi.Adapter.PingLunActivity;
import com.yunfengsi.Login;
import com.yunfengsi.Audio_BD.WakeUp.Recognizelmpl.IBDRcognizeImpl;
import com.yunfengsi.R;
import com.yunfengsi.Setting.Mine_gerenziliao;
import com.yunfengsi.Setting.ViewPagerActivity;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.CheckNumUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.ImageUtil;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.LoginUtil;
import com.yunfengsi.Utils.MD5Utls;
import com.yunfengsi.Utils.Network;
import com.yunfengsi.Utils.ProgressUtil;
import com.yunfengsi.Utils.QrUtils;
import com.yunfengsi.Utils.ScaleImageUtil;
import com.yunfengsi.Utils.ShareManager;
import com.yunfengsi.Utils.TimeUtils;
import com.yunfengsi.Utils.ToastUtil;
import com.yunfengsi.Utils.mApplication;
import com.yunfengsi.View.mItemDecoration;
import com.yunfengsi.View.myWebView;
import com.yunfengsi.ZhiFuShare;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.carbs.android.avatarimageview.library.AvatarImageView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * A simple {@link Fragment} subclass.
 * <p/>
 * 众筹项目详情 Fragment
 */
public class FundingDetailFragment extends Fragment implements View.OnClickListener,
        HttpHelper.HttpUtilHelperCallback, PL_List_Adapter.onHuifuListener {
    private String page = "1";
    private String endPage = "";
    //    private PL_List_Adapter adapter;
    private SharedPreferences sp;
    private InputMethodManager imm;
    private TextView tv;//评论的头部
    private View view;
    private TextView tv_fund_detail_title;//众筹详情标题页
    private TextView tv_support_count;//众筹点赞数
    private TextView tv_money_goal;//目标金额
    private TextView tv_money_get;//已筹金额

    private CircleProgressView circle_people_num;//支持人数
    private CircleProgressView circle_funding_percent;//目标金额达成率
    private CircleProgressView circle_time_rest;//剩余时间


    private ShareAction action;
    private TextView btn_item_detail;//项目详情
    private TextView btn_item_comments;//众筹状态

    //项目详情
    private myWebView content;

    private RecyclerView PlListVIew;
//    private mPLlistview PlListVIew;

    private static final String TAG = "Crowdfundingd";

    //标题栏详情
    private TextView tv_title_name;

    private HttpHelper httpHelper;//网络请求
    private static final int GET_FUND_DETAIL = 0;//请求众筹详情的标识
    private static final int GET_FUND_COMMENTS = 1;//请求众筹评论的标识
    private Intent intent;//接受众筹Id

    private ArrayList<String> imageUrlList = new ArrayList<>();
    private String currentFee;
    private String fundId;


    //评论收藏底部
    private EditText PLText;
    private TextView fasong;

    private boolean needTochange;
    private ImageView fenxiang;
    private int screeWidth, dp10, dp200, dp7;
    /**图片轮播*/
    /**
     * 图片轮番播放
     */
    private Banner banner;
//    private ViewPager viewPager;
//    private LinearLayout PointLayou;


    private LinearLayout currentLayout;
    private int currentPosition;
    private String currentId;
    private boolean isPLing = false;
    /*
      活动按钮
       */
    private TextView tv_activity;

    private LinearLayout pinglun, fenxiangb;
    private FrameLayout overlay;
    private ImageView toggle;
    private TextView audio;
    private IBDRcognizeImpl ibdRcognize;
    private UMWeb umWeb;
    private ArrayList<String> arrayList;
    JSONObject object;

    private LinearLayout dianzan;
    private ImageView dianzanImg;
    private TextView dianzanText;


    private ShuChengAdapter ad;
    private int firstNum = 0;
    private TextView plNum;
    private View head;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_funding_deatil, null);

        initViews();
        initEvents();
        initDatas();

        return view;
    }


    private void initViews() {
        ((ImageView) view.findViewById(R.id.tip)).setImageBitmap(ImageUtil.readBitMap(getActivity(),R.drawable.load_neterror));
        view.findViewById(R.id.tip).setOnClickListener(this);
        ad = new ShuChengAdapter(getActivity(), new ArrayList<HashMap<String, String>>());
        ad.setOnHuifuListener(this);
        TextView imageView = new TextView(getActivity());
        imageView.setTextColor(Color.BLACK);
        imageView.setText("暂无评论");
        imageView.setGravity(Gravity.CENTER);
        ViewGroup.LayoutParams vl = new ViewGroup.LayoutParams(getResources().getDisplayMetrics().widthPixels, DimenUtils.dip2px(getActivity(), 200));
        imageView.setLayoutParams(vl);
        imageView.setPadding(0, DimenUtils.dip2px(getActivity(), 20), 0, DimenUtils.dip2px(getActivity(), 20));
        ad.setEmptyView(true, imageView);
        ad.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        head = LayoutInflater.from(getActivity()).inflate(R.layout.fund_header, null);
        plNum = (TextView) head.findViewById(R.id.textView);

        PLText = (EditText) view.findViewById(R.id.fund_detail_apply_edt);
        PLText.setHint(mApplication.ST("写入你的评论(300字以内)"));
        toggle = (ImageView) view.findViewById(R.id.toggle_audio_word);
        audio = (TextView) view.findViewById(R.id.audio_button);
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

                        if(ibdRcognize==null){
                            ibdRcognize=new IBDRcognizeImpl(getActivity());
                            ibdRcognize.attachView(PLText,audio,toggle);
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
        overlay = (FrameLayout) view.findViewById(R.id.frame);
        overlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PLText.setHint(mApplication.ST("写入您的评论（300字以内）"));
                isPLing = false;
                imm.hideSoftInputFromWindow(PLText.getWindowToken(), 0);
                v.setVisibility(View.GONE);
            }
        });
        pinglun = (LinearLayout) view.findViewById(R.id.pinglun);
        pinglun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlay.setVisibility(View.VISIBLE);
                PLText.requestFocus();
                isPLing = false;
                PLText.setHint(mApplication.ST("写入您的评论（300字以内）"));
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);


            }
        });
        fenxiangb = (LinearLayout) view.findViewById(R.id.fenxiangb);
        fenxiangb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (umWeb != null) {
                    new ShareManager().shareWeb(umWeb, getActivity());
                }
            }
        });
        screeWidth = getResources().getDisplayMetrics().widthPixels;
        dp10 = DimenUtils.dip2px(getActivity(), 10);
        dp200 = DimenUtils.dip2px(getActivity(), 200);
        dp7 = DimenUtils.dip2px(getActivity(), 7);
        banner = (Banner) head.findViewById(R.id.banner);
        banner.setDelayTime(3000);
        banner.setImageLoader(new ImageLoader() {
            @Override
            public void displayImage(Context context, Object path, ImageView imageView) {
                Glide.with(FundingDetailFragment.this)
                        .load(path).override(getResources().getDisplayMetrics().widthPixels, DimenUtils.dip2px(getActivity(), 200))
                        .into(imageView);
            }
        });
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                if (imageUrlList != null) {
                    ScaleImageUtil.openBigIagmeMode(getActivity(), imageUrlList, position);
                }
            }
        });
        sp = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);

        httpHelper = new HttpHelper(getActivity(), this);
        head.findViewById(R.id.btn_item_jindu).setOnClickListener(this);
        ((TextView) head.findViewById(R.id.btn_item_jindu)).setText(mApplication.ST("项目进度"));
        ((TextView) head.findViewById(R.id.btn_item_dongtai)).setText(mApplication.ST("爱心动态"));
        ((TextView) head.findViewById(R.id.textView)).setText(mApplication.ST("最新评论"));
        head.findViewById(R.id.btn_item_dongtai).setOnClickListener(this);
//        mViewFlow = (ViewFlow) view.findViewById(R.id.viewflow);
//        mFlowIndicator = (CircleFlowIndicator) view.findViewById(R.id.viewflowindic);

        tv_title_name = (TextView) getActivity().findViewById(R.id.tv_title_name);
        tv_title_name.setText(mApplication.ST("助学详情"));

        tv_fund_detail_title = (TextView) head.findViewById(R.id.tv_fund_detail_title);
        tv_fund_detail_title.setText(mApplication.ST("正在加载数据，请稍等"));
        tv_support_count = (TextView) head.findViewById(R.id.tv_support_count);
//        ((TextView) view.findViewById(R.id.persontv)).setText(mApplication.ST("支持人次"));
//        ((TextView) view.findViewById(R.id.timetv)).setText(mApplication.ST("剩余时间"));
//        ((TextView) view.findViewById(R.id.goaltv)).setText(mApplication.ST("目标金额"));
//        ((TextView) view.findViewById(R.id.gettv)).setText(mApplication.ST("达成金额"));
        ((TextView) view.findViewById(R.id.pltv)).setText(mApplication.ST("评论"));
        ((TextView) view.findViewById(R.id.fxtv)).setText(mApplication.ST("分享"));
        tv_money_goal = (TextView) head.findViewById(R.id.tv_money_goal);
        tv_money_get = (TextView) head.findViewById(R.id.tv_money_get);


        btn_item_detail = (TextView) head.findViewById(R.id.btn_item_detail);
        btn_item_detail.setText(mApplication.ST("项目详情"));
        btn_item_detail.setSelected(true);
        btn_item_comments = (TextView) view.findViewById(R.id.fund_status);
        btn_item_comments.setText(mApplication.ST("助学"));
        btn_item_detail.setOnClickListener(this);

        dianzan = (LinearLayout) head.findViewById(R.id.zixun_detail_dianzan);
        dianzan.setOnClickListener(this);
        dianzanImg = (ImageView) head.findViewById(R.id.zixun_detail_dianzan_img);
        dianzanText = (TextView) head.findViewById(R.id.zixun_detail_dianzan_text);
        circle_people_num = (CircleProgressView) head.findViewById(R.id.circle_people_num);
        circle_funding_percent = (CircleProgressView) head.findViewById(R.id.circle_funding_percent);
        circle_time_rest = (CircleProgressView) head.findViewById(R.id.circle_time_rest);


        content = (myWebView) head.findViewById(R.id.tv_funding_detail_content);
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
                                Toast.makeText(getActivity(), "无法识别,请确认当前页面是否有二维码图片", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
            }
        });
        PlListVIew = (RecyclerView) view.findViewById(R.id.pull_to_comments);
        PlListVIew.setLayoutManager(new LinearLayoutManager(getActivity()));
        PlListVIew.addItemDecoration(new mItemDecoration(getActivity()));
        ad.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        ad.openLoadMore(10, true);
        ad.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (!endPage.equals(page)) {
                    ad.openLoadMore(10, true);
                    page = String.valueOf(Integer.valueOf(page) + 1);
                    getFundingComments(fundId);
                }
            }
        });
        ad.addHeaderView(head, 0);
        ad.setIsHuifu(false);
        PlListVIew.setAdapter(ad);


//        //ListView的监听事件
//        pull_to_comments.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if(event.getAction() == MotionEvent.ACTION_UP){
//                    scrollView.requestDisallowInterceptTouchEvent(false);
//                }else{
//                    scrollView.requestDisallowInterceptTouchEvent(true);//屏蔽父控件的拦截事件
//                }
//                return false;
//            }
//        });
        //底部
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        fasong = (TextView) view.findViewById(R.id.fund_detail_fasong);
        fasong.setText(mApplication.ST("发送"));

        fenxiang = (ImageView) view.findViewById(R.id.fund_detail_fenxiang);

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
                Log.w(TAG, "openImage: 网页图片地址" + img + "页码：" + arrayList.indexOf(img));
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

    private void initDatas() {


        intent = getActivity().getIntent();
        fundId = intent.getStringExtra("id");

        if (!TextUtils.isEmpty(fundId)) {
            getFundingDetail(fundId);
        }

        getFundingComments(fundId);

    }

    private void initEvents() {

        /**圆环进度条的设置*/
        circle_people_num.setProgress(100);//全满

        circle_people_num.setTrendsProgress(false);//不动态加载
        circle_people_num.setmTxCenterEnd("人");

        circle_time_rest.setProgress(100);
        circle_time_rest.setTrendsProgress(false);
        circle_time_rest.setmTxCenterEnd("天");

        circle_funding_percent.setmTxCenterEnd("%");
        circle_funding_percent.setTrendsProgress(true);//动态加载
        circle_funding_percent.setmTxHint2(mApplication.ST("助学进度"));
    }

    /**
     * 获取众筹详情
     *
     * @param fundId
     */
    private void getFundingDetail(final String fundId) {
        //传参数
        List<String> key_list = new ArrayList<>();
        key_list.add("m_id");
        key_list.add("zhongchou_id");

        List<String> value_list = new ArrayList<>();
        //value_list.add(Constants.USER_ID_TEST);
        value_list.add("1");
        value_list.add(fundId);
        if (!Network.HttpTest(mApplication.getInstance())) {
            view.findViewById(R.id.tip).setVisibility(VISIBLE);
            Toast.makeText(mApplication.getInstance(), mApplication.ST("网络连接失败"), Toast.LENGTH_SHORT).show();
            return;
        }
        ProgressUtil.show(getActivity(), "", "正在加载");
        httpHelper.postData(Constants.FUND_DETAIL, key_list, value_list, GET_FUND_DETAIL);
    }

    /**
     * 获取众筹评论
     *
     * @param fundId
     */
    private void getFundingComments(String fundId) {
        //传参数
        List<String> key_list = new ArrayList<>();
        key_list.add("page");
        key_list.add("cfg_id");

        List<String> value_list = new ArrayList<>();
        // value_list.add(Constants.SAFE_KEY);
        //value_list.add(Constants.USER_ID_TEST);
        value_list.add(page);
        value_list.add(fundId);

        httpHelper.postData(Constants.FUNDING_DETAIL_COMMENTS, key_list, value_list, GET_FUND_COMMENTS);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(mApplication.getInstance()).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.tip:
                getFundingDetail(fundId);
                getFundingComments(fundId);
                break;
            case R.id.zixun_detail_dianzan:
                if (sp.getString("user_id", "").equals("") && sp.getString("uid", "").equals("")) {
                    Intent intent = new Intent(getActivity(), Login.class);
                    startActivity(intent);
                    Toast.makeText(getActivity(), mApplication.ST("请先登录"), Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject js = new JSONObject();
                                try {
                                    js.put("user_id", sp.getString("user_id", ""));
                                    js.put("cfg_id", fundId);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                String data = OkGo.post(Constants.FUNDDeatail_like).params("key", ApisSeUtil.getKey())
                                        .params("msg", ApisSeUtil.getMsg(js))
                                        .execute().body().string();
                                if (!data.equals("")) {
                                    final HashMap<String, String> map = AnalyticalJSON.getHashMap(data);
                                    if (map != null && map.get("code").equals("000")) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                dianzanText.setText((Integer.valueOf(dianzanText.getText().toString()) + 1) + "");
                                                tv_support_count.setText(mApplication.ST(Integer.valueOf(dianzanText.getText().toString()) + "人赞过"));
                                                dianzanImg.setImageResource(R.drawable.dianzan1);
                                                dianzanText.setTextColor(getResources().getColor(R.color.main_color));
                                                dianzan.setEnabled(false);
                                            }
                                        });

                                    } else {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                dianzanImg.setImageResource(R.drawable.dianzan1);
                                                dianzanText.setTextColor(getResources().getColor(R.color.main_color));
                                                dianzan.setEnabled(false);
                                                Toast.makeText(getActivity(), mApplication.ST("已点过赞啦"), Toast.LENGTH_SHORT).show();
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
            case R.id.btn_item_dongtai://项目动态
                Intent intent1 = new Intent(mApplication.getInstance(), Fund_surpport_list.class);
                intent1.putExtra("id", fundId);
                startActivity(intent1);
                break;
            case R.id.btn_item_jindu://项目进度
                Intent i = new Intent(getActivity(), ZhiFuShare.class);
                i.putExtra("id", fundId);
                i.putExtra(ZhiFuShare.Progress, true);
                startActivity(i);
                break;
            case R.id.fund_detail_fenxiang:
                imm.hideSoftInputFromWindow(PLText.getWindowToken(), 0);
                fenxiangb.performClick();
                break;
            case R.id.fund_status:
                if (new LoginUtil().checkLogin(getActivity()))
                    showPayDialog(getActivity());
                break;
            case R.id.fund_detail_fasong:
                if (PLText.getText().toString().trim().equals("")) {
                    Toast.makeText(getActivity(), mApplication.ST("请输入评论"), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (sp.getString("pet_name", "").trim().equals("")) {
                    Toast.makeText(getActivity(), mApplication.ST("请完善信息"), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), Mine_gerenziliao.class);
                    startActivity(intent);
                    return;
                }
                v.setEnabled(false);
                ProgressUtil.show(getActivity(), "", mApplication.ST("正在提交"));
                if (!isPLing) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final String content = PLText.getText().toString();

                                JSONObject js = new JSONObject();
                                try {
                                    js.put("user_id", sp.getString("user_id", ""));
                                    js.put("ct_contents", content);
                                    js.put("cfg_id", fundId);
                                    js.put("m_id", Constants.M_id);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                ApisSeUtil.M m = ApisSeUtil.i(js);
                                LogUtil.e("众筹评论:::" + js);
                                final String data = OkGo.post(Constants.FUNDING_DETAIL_ADD_COMMENTS)
                                        .params("key", m.K())
                                        .params("msg", m.M()).execute().body().string();
                                if (!data.equals("")) {
                                    Log.i(TAG, "run:      data------>" + data);
                                    final HashMap<String, String> hashMap = AnalyticalJSON.getHashMap(data);
                                    if (hashMap != null && "000".equals(hashMap.get("code"))) {
                                        getActivity().runOnUiThread(new Runnable() {
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
                                                map.put("level", sp.getString("level", "0"));
                                                map.put("ct_ctr", diazannum);
                                                map.put("ct_time", time);
                                                if (sp.getString("role", "").equals("3")) {
                                                    map.put("role", "3");
                                                } else {
                                                    map.put("role", "0");
                                                }
                                                map.put("id", hashMap.get("id"));
                                                map.put("reply", new JSONArray().toString());
                                                map.put("level", sp.getString("level", "0"));
                                                ad.getData().add(0, map);
                                                ad.notifyDataSetChanged();
                                                ad.flagList.add(0, false);


                                                v.setEnabled(true);
                                                firstNum += 1;
                                                plNum.setText("评论 " + firstNum);
                                                PLText.setText("");
                                                imm.hideSoftInputFromWindow(PLText.getWindowToken(), 0);
                                                overlay.setVisibility(View.GONE);

                                                Toast.makeText(mApplication.getInstance(), mApplication.ST("添加评论成功"), Toast.LENGTH_SHORT).show();
                                                ProgressUtil.dismiss();
                                            }
                                        });
                                    } else {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(mApplication.getInstance(), mApplication.ST("上传评论失败，请重新尝试"), Toast.LENGTH_SHORT).show();
                                                ProgressUtil.dismiss();
                                                v.setEnabled(true);
                                            }
                                        });
                                    }
                                } else {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(mApplication.getInstance(), mApplication.ST("上传评论失败，请重新尝试"), Toast.LENGTH_SHORT).show();
                                            ProgressUtil.dismiss();
                                            v.setEnabled(true);
                                        }
                                    });
                                }
                            } catch (IOException e) {
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
                                JSONObject js = new JSONObject();
                                try {
                                    js.put("user_id", sp.getString("user_id", ""));
                                    js.put("ct_contents", content);
                                    js.put("ct_id", currentId);
                                    js.put("m_id", Constants.M_id);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                final String data = OkGo.post(Constants.little_zixun_pl_add_IP)
                                        .params("key", ApisSeUtil.getKey())
                                        .params("msg", ApisSeUtil.getMsg(js)).execute().body().string();
                                if (!data.equals("")) {
                                    final HashMap<String, String> hashMap = AnalyticalJSON.getHashMap(data);
                                    if (hashMap != null && "000".equals(hashMap.get("code"))) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (currentLayout.getVisibility() == View.GONE) {
                                                    currentLayout.setVisibility(View.VISIBLE);
                                                }
                                                TextView textView = new TextView(getActivity());
                                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                                layoutParams.setMargins(0, DimenUtils.dip2px(getActivity(), 5), 0, DimenUtils.dip2px(getActivity(), 5));
                                                textView.setLayoutParams(layoutParams);
                                                String pet_name = sp.getString("pet_name", "");
                                                SpannableStringBuilder ssb = new SpannableStringBuilder(pet_name + ":" + content);
                                                ssb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getActivity(), R.color.main_color)), 0, pet_name.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                                                textView.setText(ssb);
                                                currentLayout.addView(textView);
                                                PLText.setText("");
                                                PLText.setHint(mApplication.ST("写入您的评论（300字以内）"));
                                                overlay.setVisibility(View.GONE);
                                                imm.hideSoftInputFromWindow(PLText.getWindowToken(), 0);
//                                                PlListVIew.scrollToPosition(currentPosition);
                                                fasong.setEnabled(true);
                                                isPLing = false;
                                                try {
                                                    JSONArray jsonArray = new JSONArray(ad.getData().get(currentPosition).get("reply"));
                                                    JSONObject jsonObject = new JSONObject();
                                                    jsonObject.put("id", hashMap.get("id"));
                                                    jsonObject.put("pet_name", pet_name);
                                                    if (sp.getString("role", "").equals("3")) {
                                                        jsonObject.put("role", "3");
                                                    } else {
                                                        jsonObject.put("role", "0");
                                                    }
                                                    jsonObject.put("ct_contents", content);
                                                    jsonObject.put("user_id", sp.getString("user_id", ""));
                                                    jsonArray.put(jsonObject);
                                                    ad.getData().get(currentPosition).put("reply", jsonArray.toString());
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                ProgressUtil.dismiss();
                                            }
                                        });
                                    }
                                } else {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            v.setEnabled(true);
                                            Toast.makeText(getActivity(), mApplication.ST("回复提交失败，请重新尝试"), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        imm.hideSoftInputFromWindow(PLText.getWindowToken(), 0);
        OkGo.getInstance().cancelTag(getActivity());
    }
    //    @Override
//    public void onRefresh() {
//        //刷新数据代码
//        if (list_fragment_swipe.isRefreshing()){
//            list_fragment_swipe.setRefreshing(false);
//        }
//    }


    @Override
    public void successCallback(int tab, String result) {

        switch (tab) {
            case GET_FUND_DETAIL://众筹详情
                if (TextUtils.isEmpty(result) || "null".equals(result)) {
                    view.findViewById(R.id.tip).setVisibility(VISIBLE);
                    Toast.makeText(getActivity(), mApplication.ST("该助学项目已下架"), Toast.LENGTH_SHORT).show();
                    ProgressUtil.dismiss();
                    getActivity().finish();
                    return;

                }
                view.findViewById(R.id.tip).setVisibility(GONE);
                PlListVIew.setVisibility(VISIBLE);
                banner.setFocusable(true);
                banner.setFocusableInTouchMode(true);
                banner.requestFocus();
                try {
                    object = new JSONObject(result);
                    final JSONObject obj_result = object.getJSONObject("crowdfunding");
                    setUrlToImage(obj_result);

                    tv_support_count.setText(mApplication.ST(obj_result.get("likes") + "人赞过"));
                    tv_fund_detail_title.setText(obj_result.getString("title"));
                    content.loadDataWithBaseURL(Constants.IMGDIR + TAG + "/" + TimeUtils.getStrTime(System.currentTimeMillis() / 1000 + "") + ".jpg", obj_result.getString("abstract")
                            , "text/html", "UTF-8", null);
                    // cfg_comment//
                    firstNum = Integer.valueOf(obj_result.getString("cfg_comment"));
                    ((TextView) head.findViewById(R.id.textView)).setText("评论 " + firstNum);
                    circle_people_num.setIsINT(true);
                    circle_funding_percent.setIsINT(false);
                    circle_people_num.setDrawProgress(Integer.parseInt(obj_result.getString("cy_people")));//设置中间绘制数字
                    //计算剩余天数
                    dianzanText.setText(obj_result.getString("likes"));
                    Double t = (((TimeUtils.dataOne(obj_result.getString("end_time")) - System.currentTimeMillis())) / 1000 / 60 / 60 / 24d);
                    int t1 = ((Double) Math.ceil(t)).intValue();
                    circle_time_rest.setIsINT(true);
                    if (t <= 0) {
                        btn_item_comments.setText(mApplication.ST("已结束"));
                        btn_item_comments.setEnabled(false);
                        btn_item_comments.setBackgroundResource(R.drawable.dark_circle);
                        circle_time_rest.setDrawProgress(0);
                    } else {
                        circle_time_rest.setDrawProgress(t1);
                    }

                    //将科学计数法转换成普通数字
                    final BigDecimal db = new BigDecimal(obj_result.getString("tar_money"));

                    tv_money_goal.setText("￥" + db.toPlainString() + "\n目标金额");

                    //已筹金额
                    double get_money = Double.valueOf(obj_result.getString("sen_money"));
                    //目标金额

                    int goal_money = db.intValue();
                    Log.e(TAG, "successCallback: " + "   1-=-=-=->" + get_money + "   2=-=-=>" + goal_money + "    percent-=-=>" +
                            (get_money * 100 / goal_money));
                    int p = ((get_money * 100 / goal_money) + "").lastIndexOf(".");
                    int progress = Integer.valueOf(((get_money * 100 / goal_money) + "").substring(0, p));
                    circle_funding_percent.setProgress(progress);

                    tv_money_get.setText("￥" + obj_result.getString("sen_money") + "\n达成金额");

                    String md5 = MD5Utls.stringToMD5(Constants.safeKey);
                    String m1 = md5.substring(0, 16);
                    String m2 = md5.substring(16, md5.length());
                    umWeb = new UMWeb(Constants.FX_host_Ip + TAG + "/id/" + m1 + fundId + m2 + "/st/" + (mApplication.isChina ? "s" : "t"));
                    umWeb.setTitle(mApplication.ST(obj_result.getString("title")));
                    umWeb.setDescription(mApplication.ST(String.valueOf(Html.fromHtml(obj_result.getString("abstract"))) + "\n已筹金额:" + obj_result.getString("sen_money") + "\n目标金额：" + db.toPlainString()));
                    umWeb.setThumb(new UMImage(mApplication.getInstance(), obj_result.getString("image")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                btn_item_comments.setOnClickListener(this);

                fenxiang.setOnClickListener(this);
                fasong.setOnClickListener(this);

                break;
            case GET_FUND_COMMENTS://获取众筹评论
                ArrayList<HashMap<String, String>> Pllist = AnalyticalJSON.getList(result, "comment");
                PlListVIew.setFocusable(false);
                if (Pllist != null) {
                    if (Pllist.size() != 10) {
                        endPage = page;
                        ToastUtil.showToastShort("评论加载完毕");
                        ad.notifyDataChangedAfterLoadMore(Pllist, false);
                    } else {
                        ad.notifyDataChangedAfterLoadMore(Pllist, true);

                    }
                    boolean flag = false;
                    for (int i = 0; i < Pllist.size(); i++) {
                        ad.flagList.add(flag);
                    }
                }
                ProgressUtil.dismiss();
                break;
        }

    }

    @Override
    public void errorCallback(int tab) {
        switch (tab) {
            case GET_FUND_DETAIL:
                view.findViewById(R.id.tip).setVisibility(VISIBLE);

                break;
        }

    }

    /**
     * 加载轮播图片并加载小圆点
     */
    private void setUrlToImage(final JSONObject js) {
        String image = null;
        String image1 = null;
        String image2 = null;
        try {
            image = js.getString("image");
            if (null != js.getString("image2"))
                image2 = js.getString("image2");
            if (null != js.getString("image1"))
                image1 = js.getString("image1");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (image != null && !TextUtils.isEmpty(image)) {
            imageUrlList.add(image);
        }
        if (image1 != null && !TextUtils.isEmpty(image1)) {
            imageUrlList.add(image1);
        }
        if (image2 != null && !TextUtils.isEmpty(image2)) {
            imageUrlList.add(image2);
        }
        banner.setImages(imageUrlList);
        banner.start();
    }


    //调起支付  需要在上下文设置currentFee变量，并为每个item设置tag识别
    public void showPayDialog(final Activity context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final View view = LayoutInflater.from(context).inflate(R.layout.pay_choose_num_dialog, null);
        final TextView one = (TextView) view.findViewById(R.id.one);
        final TextView five = (TextView) view.findViewById(R.id.five);
        final TextView ten = (TextView) view.findViewById(R.id.ten);
        final TextView twelve = (TextView) view.findViewById(R.id.twelve);
        final TextView fifty = (TextView) view.findViewById(R.id.fifty);
        final TextView one_han = (TextView) view.findViewById(R.id.han);
        final TextView two_han = (TextView) view.findViewById(R.id.two_han);
        final TextView five_han = (TextView) view.findViewById(R.id.five_han);
        final TextView others = (TextView) view.findViewById(R.id.others);
        final EditText otherNum = (EditText) view.findViewById(R.id.otherNum);
        final Button agreeTOPay = (Button) view.findViewById(R.id.agreeToPay);
        final RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.otherlayout);
        int dimens = DimenUtils.dip2px(context, 10);
        builder.setView(view, 0, dimens, 0, dimens);
        final AlertDialog dialog = builder.create();
        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(0x00000000));
        window.setWindowAnimations(R.style.dialogWindowAnim);
        WindowManager.LayoutParams wl = window.getAttributes();
        window.setAttributes(wl);
        dialog.show();
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                switch (v.getId()) {
                    case R.id.one:
                        view.findViewWithTag(currentFee).setEnabled(true);
                        currentFee = (String) v.getTag();
                        v.setEnabled(false);
                        break;
                    case R.id.five:
                        view.findViewWithTag(currentFee).setEnabled(true);
                        currentFee = (String) v.getTag();
                        v.setEnabled(false);
                        break;
                    case R.id.ten:
                        view.findViewWithTag(currentFee).setEnabled(true);
                        currentFee = (String) v.getTag();
                        v.setEnabled(false);
                        break;
                    case R.id.twelve:
                        view.findViewWithTag(currentFee).setEnabled(true);
                        currentFee = (String) v.getTag();
                        v.setEnabled(false);
                        break;
                    case R.id.fifty:
                        view.findViewWithTag(currentFee).setEnabled(true);
                        currentFee = (String) v.getTag();
                        v.setEnabled(false);
                        break;
                    case R.id.han:
                        view.findViewWithTag(currentFee).setEnabled(true);
                        currentFee = (String) v.getTag();
                        v.setEnabled(false);
                        break;
                    case R.id.two_han:
                        view.findViewWithTag(currentFee).setEnabled(true);
                        currentFee = (String) v.getTag();
                        v.setEnabled(false);
                        break;
                    case R.id.five_han:
                        view.findViewWithTag(currentFee).setEnabled(true);
                        currentFee = (String) v.getTag();
                        v.setEnabled(false);
                        break;
                    case R.id.others:
                        view.findViewWithTag(currentFee).setEnabled(true);
                        currentFee = (String) v.getTag();
                        v.setEnabled(false);
                        break;
                    case R.id.canclePay:
                        dialog.dismiss();
                        break;
                    case R.id.agreeToPay:
                        if (!Network.HttpTest(getActivity())) {
                            Toast.makeText(mApplication.getInstance(), mApplication.ST("网络连接不稳定，请稍后重试"), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (sp.getString("pet_name", "").trim().equals("")) {
                            Toast.makeText(context, mApplication.ST("请完善信息"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context, Mine_gerenziliao.class);
                            startActivity(intent);
                            return;
                        }
                        dialog.dismiss();
                        mApplication.openPayLayout(context, currentFee.equals(others.getTag().toString()) ? String.format("%.2f", (Double.valueOf(otherNum.getText().toString()))) : String.valueOf(Integer.valueOf(currentFee)), fundId, getActivity().getIntent().getStringExtra("title") == null ? tv_fund_detail_title.getText().toString() : getActivity().getIntent().getStringExtra("title"), "1", "5", "");
                        break;
                }
                if (v.getId() != R.id.canclePay && v.getId() != R.id.agreeToPay) {
                    if (!currentFee.equals(others.getTag().toString())) {
                        if (layout.getVisibility() == VISIBLE) {
                            layout.setVisibility(GONE);
                        }
                    } else {
                        if (layout.getVisibility() == GONE) {
                            layout.setVisibility(VISIBLE);
                        }

                    }
                    if (v instanceof TextView && v.getId() != R.id.agreeToPay && v.getId() != R.id.canclePay) {
                        ((TextView) v).setTextColor(getResources().getColor(R.color.main_color));
                    }
                    if (view.findViewWithTag(currentFee) instanceof TextView) {
                        ((TextView) view.findViewWithTag(currentFee)).setTextColor(Color.GRAY);
                    }
                }
                if (v.getId() == R.id.others) {
                    if (otherNum.getText().toString().equals("")) {
                        agreeTOPay.setEnabled(false);
                        agreeTOPay.setTextColor(getResources().getColor(R.color.umeng_socialize_text_friends_list));
                    }
                } else {
                    if (!agreeTOPay.isEnabled()) {
                        agreeTOPay.setEnabled(true);
                        agreeTOPay.setTextColor(getResources().getColor(R.color.white));
                    }
                }

            }
        };
        currentFee = (String) one.getTag();
        one.setEnabled(false);
        one.setTextColor(Color.GRAY);
        ten.setOnClickListener(onClickListener);
        twelve.setOnClickListener(onClickListener);
        one.setOnClickListener(onClickListener);
        fifty.setOnClickListener(onClickListener);
        five.setOnClickListener(onClickListener);
        two_han.setOnClickListener(onClickListener);
        five_han.setOnClickListener(onClickListener);
        one_han.setOnClickListener(onClickListener);
        others.setOnClickListener(onClickListener);
        view.findViewById(R.id.canclePay).setOnClickListener(onClickListener);
        agreeTOPay.setOnClickListener(onClickListener);
        otherNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    agreeTOPay.setEnabled(false);
                    agreeTOPay.setTextColor(getResources().getColor(R.color.umeng_socialize_text_friends_list));
                    return;
                }
                if (CheckNumUtil.checkNum(s.toString())) {
                    agreeTOPay.setEnabled(true);
                    agreeTOPay.setTextColor(Color.WHITE);
                } else {
                    agreeTOPay.setEnabled(false);
                    agreeTOPay.setTextColor(context.getResources().getColor(R.color.umeng_socialize_text_friends_list));
                }
//                if (Integer.valueOf(s.toString()) > 0.01) {
//                    if (!agreeTOPay.isEnabled()) {
//                        agreeTOPay.setEnabled(true);
//                        agreeTOPay.setTextColor(getResources().getColor(R.color.white));
//                    }
//                } else {
//                    if (agreeTOPay.isEnabled()) {
//                        agreeTOPay.setEnabled(false);
//                        agreeTOPay.setTextColor(getResources().getColor(R.color.umeng_socialize_text_friends_list));
//                    }
//                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    @Override
    public void onHuifuClicked(String id, int p, View v, String name) {
// TODO: 2016/12/27 评论回复接口
        overlay.setVisibility(View.VISIBLE);
        PLText.requestFocus();
        isPLing = true;
        currentLayout = (LinearLayout) v;
        currentPosition = p;
        currentId = id;
        SpannableString ss = new SpannableString(mApplication.ST("回复 ") + name + " :");
        ss.setSpan(new ForegroundColorSpan(Color.BLACK), 3, name.length() + 3, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        PLText.setHint(ss);

        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

    }

    @Override
    public void onPause() {
        super.onPause();
        ProgressUtil.dismiss();
        if (content != null) {
            content.onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (content != null) {
            content.onResume();
        }
    }

    public static class ShuChengAdapter extends BaseQuickAdapter<HashMap<String, String>> {

        public List<HashMap<String, String>> mlist;
        private int screenwidth;
        private Context context;
        private static final String TAG = "PL_List_Adapter";
        public ArrayList<Boolean> flagList;
        private SharedPreferences sp;
        private Drawable dianzan, dianzan1;
        private int mainColor;
        private PL_List_Adapter.onHuifuListener onHuifu;
        private boolean isHuifu = false;
        private boolean toDetail = false;

        public void setOnHuifuListener(PL_List_Adapter.onHuifuListener onhuifu) {
            this.onHuifu = onhuifu;
        }


        public void setIsHuifu(boolean flag) {
            this.isHuifu = flag;
        }

        public void setToDetail(boolean flag) {
            this.toDetail = flag;
        }

        public void addList(List<HashMap<String, String>> list) {
            this.mlist = list;
            Boolean flag = false;
            for (int i = 0; i < mlist.size(); i++) {
                flagList.add(flag);
            }
        }

        public ShuChengAdapter(Context context, List<HashMap<String, String>> data) {
            super(R.layout.video_pinglun_item, data);
            this.mlist = new ArrayList<>();
            this.context = context;
            screenwidth = context.getResources().getDisplayMetrics().widthPixels;
            sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
            flagList = new ArrayList<>();
            dianzan = ContextCompat.getDrawable(context, R.drawable.dianzan);
            dianzan1 = ContextCompat.getDrawable(context, R.drawable.dianzan1);

            dianzan1.setBounds(0, 0, DimenUtils.dip2px(context, 15), DimenUtils.dip2px(context, 15));
            dianzan.setBounds(0, 0, DimenUtils.dip2px(context, 15), DimenUtils.dip2px(context, 15));
            mainColor = ContextCompat.getColor(context, R.color.main_color);
        }

        @Override
        protected void convert(final BaseViewHolder holder, final HashMap<String, String> bean) {
            TextView Dznum = holder.getView(R.id.Pl_item_DianZan_num);
            TextView huifu = holder.getView(R.id.PL_item_huifu);
            AvatarImageView head = holder.getView(R.id.PL_item_Head);
            TextView userName = holder.getView(R.id.PL_item_Name);
            TextView content = holder.getView(R.id.Pl_item_Content);
            TextView time = holder.getView(R.id.PL_item_time);
            LinearLayout huifuLayout = holder.getView(R.id.pl_huifu_layout);
            if (isHuifu) {
                Dznum.setVisibility(View.GONE);
                huifu.setVisibility(View.GONE);
            } else {
                huifu.setText(mApplication.ST("回复"));
//                holder.convertView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent = new Intent(context, PingLunActivity.class);
//                        intent.putExtra("id", bean.get("id"));
//                        intent.putExtra("content", bean.get("ct_contents"));
//                        intent.putExtra("pet_name", bean.get("pet_name"));
//                        intent.putExtra("user_image", bean.get("user_image"));
//                        intent.putExtra("num", bean.get("ct_ctr"));
//                        intent.putExtra("time", bean.get("ct_time"));
//                        intent.putExtra("realname", bean.get("realname"));
//                        intent.putExtra("isLike", flagList.get(getData().indexOf(bean)));
//                        context.startActivity(intent);
//                    }
//                });


            }

            Glide.with(context).load(bean.get("user_image"))
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .override(screenwidth / 10, screenwidth / 10)
                    .into(head);

            if (bean.get("id") == null) {
                Dznum.setTag("");
            } else {
                Dznum.setTag(bean.get("id"));
            }
            if ("3".equals(bean.get("role"))) {
                userName.setTextColor(Color.parseColor("#E12202"));
            } else {
                userName.setTextColor(Color.BLACK);
            }
            userName.setText(bean.get("pet_name"));
            content.setText(mApplication.ST(bean.get("ct_contents")));
            Dznum.setText(bean.get("ct_ctr"));
            if (flagList.get(getData().indexOf(bean))) {
                Dznum.setCompoundDrawables(null, null, dianzan1, null);
                Dznum.setTextColor(mainColor);
            } else {
                Dznum.setCompoundDrawables(null, null, dianzan, null);
                Dznum.setTextColor(Color.GRAY);
            }
            time.setText(mApplication.ST(TimeUtils.getTrueTimeStr(bean.get("ct_time"))));
            Dznum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (!new LoginUtil().checkLogin(context)) {
                        return;
                    }
                    ;
                    if (v.getTag() == null || v.getTag().toString().equals("")) {
                        Toast.makeText(context, mApplication.ST("快去给其他人点赞吧"), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (flagList.get(getData().indexOf(bean))) {
                        return;
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject js = new JSONObject();
                                try {
                                    js.put("user_id", sp.getString("user_id", ""));
                                    js.put("comment_id", v.getTag().toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                ApisSeUtil.M m = ApisSeUtil.i(js);
                                String data1 = OkGo.post(Constants.PL_DZ_IP)
                                        .params("key", m.K())
                                        .params("msg", m.M())
                                        .execute().body().string();
                                if (!data1.equals("")) {
                                    final View childat = holder.convertView;
                                    final TextView dznum = (TextView) childat.findViewById(R.id.Pl_item_DianZan_num);
                                    HashMap<String, String> map = AnalyticalJSON.getHashMap(data1);
                                    if (map != null && map.get("code").equals("000")) {
                                        ((Activity) context).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                ((TextView) v).setCompoundDrawables(null, null, dianzan1, null);
                                                if (dznum != null) {
                                                    ((Activity) context).runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            String d = (Integer.valueOf(dznum.getText().toString()) + 1) + "";
                                                            dznum.setText(d);
                                                            dznum.setTextColor(mainColor);
                                                            bean.put("ct_ctr", d);
                                                            flagList.set(getData().indexOf(bean), true);
                                                        }
                                                    });

                                                }
                                            }
                                        });
                                    } else {
                                        ((Activity) context).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                ((TextView) v).setCompoundDrawables(null, null, dianzan1, null);
                                                dznum.setTextColor(mainColor);
                                                Toast.makeText(context, mApplication.ST("你已经对该评论点过赞了"), Toast.LENGTH_SHORT).show();
                                                flagList.set(getData().indexOf(bean), true);
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
            });
            huifuLayout.setTag(bean.get("id"));
            huifu.setTag(huifuLayout);
            huifu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LinearLayout layout = (LinearLayout) v.getTag();
                    onHuifu.onHuifuClicked(layout.getTag().toString(), getData().indexOf(bean), layout, bean.get("pet_name"));
                }
            });
            if (bean.get("reply") != null && !"".equals(bean.get("reply"))) {
                final ArrayList<HashMap<String, String>> replay = AnalyticalJSON.getList_zj(bean.get("reply"));
                if (replay != null) {
                    huifuLayout.removeAllViews();
                    for (final HashMap<String, String> map : replay) {
                        TextView textView = new TextView(context);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams.setMargins(0, DimenUtils.dip2px(context, 5), 0, DimenUtils.dip2px(context, 5));
                        textView.setLayoutParams(layoutParams);
                        String pet_name = map.get("pet_name");
                        final String c = mApplication.ST(map.get("ct_contents"));
                        SpannableStringBuilder ssb = new SpannableStringBuilder(pet_name + ":" + c);
                        if ("3".equals(map.get("role"))) {
                            textView.setTextColor(ContextCompat.getColor(context, R.color.pinglun_name));
                            ssb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.black)), pet_name.length(), pet_name.length() + 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                        } else {
                            ssb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.main_color)), 0, pet_name.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                        }
                        textView.setText(ssb);
                        textView.setMovementMethod(LinkMovementMethod.getInstance());
                        huifuLayout.addView(textView);
                        if (huifuLayout.getVisibility() == View.GONE) {
                            huifuLayout.setVisibility(View.VISIBLE);
                        }
                    }
                    if (replay.size() >= 3) {
                        TextView textView = new TextView(context);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams.setMargins(0, DimenUtils.dip2px(context, 5), 0, DimenUtils.dip2px(context, 5));
                        textView.setLayoutParams(layoutParams);
                        textView.setText(mApplication.ST("查看更多回复"));
                        textView.setTextColor(ContextCompat.getColor(context, R.color.black));
                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, PingLunActivity.class);
                                intent.putExtra("id", bean.get("id"));
                                intent.putExtra("content", bean.get("ct_contents"));
                                intent.putExtra("pet_name", bean.get("pet_name"));
                                intent.putExtra("user_image", bean.get("user_image"));
                                intent.putExtra("num", bean.get("ct_ctr"));
                                intent.putExtra("time", bean.get("ct_time"));
                                intent.putExtra("realname", bean.get("realname"));
                                intent.putExtra("isLike", flagList.get(getData().indexOf(bean)));
                                context.startActivity(intent);
                            }
                        });
                        huifuLayout.addView(textView);
                    } else {
                        if (huifuLayout.getChildCount() >= 1) {
                            if (((TextView) huifuLayout.getChildAt(huifuLayout.getChildCount() - 1)).getText().toString().equals(mApplication.ST("查看更多回复"))) {
                                huifuLayout.removeView(huifuLayout.getChildAt(huifuLayout.getChildCount() - 1));
                            }
                        } else {
                            huifuLayout.setVisibility(View.GONE);
                        }

                    }
                } else {
                    huifuLayout.removeAllViews();
                    huifuLayout.setVisibility(View.GONE);
                }
            } else {
                huifuLayout.removeAllViews();
                huifuLayout.setVisibility(View.GONE);
            }
        }
    }
}
