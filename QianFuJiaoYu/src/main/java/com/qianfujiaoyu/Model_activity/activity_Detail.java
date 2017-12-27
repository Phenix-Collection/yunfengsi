package com.qianfujiaoyu.Model_activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.request.BaseRequest;
import com.qianfujiaoyu.Activitys.Login;
import com.qianfujiaoyu.Adapter.PL_List_Adapter;
import com.qianfujiaoyu.Base.ScaleImageUtil;
import com.qianfujiaoyu.R;
import com.qianfujiaoyu.Setting.Mine_gerenziliao;
import com.qianfujiaoyu.Utils.AnalyticalJSON;
import com.qianfujiaoyu.Utils.ApisSeUtil;
import com.qianfujiaoyu.Utils.Constants;
import com.qianfujiaoyu.Utils.DimenUtils;
import com.qianfujiaoyu.Utils.LogUtil;
import com.qianfujiaoyu.Utils.LoginUtil;
import com.qianfujiaoyu.Utils.Network;
import com.qianfujiaoyu.Utils.PreferenceUtil;
import com.qianfujiaoyu.Utils.ProgressUtil;
import com.qianfujiaoyu.Utils.ShareManager;
import com.qianfujiaoyu.Utils.StatusBarCompat;
import com.qianfujiaoyu.Utils.TimeUtils;
import com.qianfujiaoyu.Utils.mApplication;
import com.qianfujiaoyu.View.mPLlistview;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;


/**
 * Created by Administrator on 2016/10/7.
 */
public class activity_Detail extends AppCompatActivity implements View.OnClickListener, PL_List_Adapter.onHuifuListener {
    private ImageView back, rightImg, shoucang, dianzanImg;
    private TextView dianzanText, title, fabuTime, faqidanwei, huodongdidian, huodongTime, peopleNum, Tobaoming, content, FaSong;
    private EditText PLText;
    private LinearLayout dianzan;
    //    private LinearLayout PointLayou;//轮播图圆点layout
    private mPLlistview PlListVIew;
    private String page = "1";
    private String endPage = "";
    private Thread thread;//线程
    private static final String TAG = "Activityd";
    private String Id;//活动id
    private ArrayList<String> imageList;
    //    private ViewPager viewPager;//轮播
    private int screeWidth, dp10, dp180, dp7;
    private PL_List_Adapter adapter;
    private SharedPreferences sp;
    private InputMethodManager imm;
    private TextView tv;//评论的头部
    private boolean needTochange;//判断是否需要通知其他页面改变

    private ShareAction action;
    private boolean isPLing = false;
    private LinearLayout currentLayout;
    private int currentPosition;
    private String currentId;
    /*
       活动按钮
        */
    private TextView tv_activity;

    private LinearLayout pinglun, fenxiangb;
    private FrameLayout overlay;
    private UMWeb umWeb;

    private Banner banner;
    private boolean isKECheng=false;
    private String act_prol="";//活动协议Html

    private TextView KC_Tip;//课程价格和人数限制
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initView();
        LoadData();
    }

    /**
     * 初始化数据
     */
    private void initView() {
        KC_Tip= (TextView) findViewById(R.id.kecheng_tip);
        isKECheng=getIntent().getBooleanExtra("kecheng",false);
        LogUtil.e("是否是课程：：："+isKECheng);
        if(isKECheng){
            ((TextView) findViewById(R.id.title_title)).setText("课程详情");
            ((TextView) findViewById(R.id.activity_detail_baoming)).setText("立即预约");
        }
        overlay = (FrameLayout) findViewById(R.id.frame);
        overlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PLText.setHint(mApplication.ST("写入您的评论（300字以内）"));
                isPLing = false;
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
                isPLing = false;
                PLText.setHint(mApplication.ST("写入您的评论（300字以内）"));
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                PLText.requestFocus();

            }
        });
        fenxiangb = (LinearLayout) findViewById(R.id.fenxiangb);
        fenxiangb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (umWeb != null) {
                    new ShareManager().shareWeb(umWeb, activity_Detail.this);
                }
            }
        });

        ((TextView) findViewById(R.id.p1)).setText(mApplication.ST("最新评论"));
        ((TextView) findViewById(R.id.pltv)).setText(mApplication.ST("评论"));
        ((TextView) findViewById(R.id.fxtv)).setText(mApplication.ST("分享"));
        dianzanImg = (ImageView) findViewById(R.id.activity_detail_dianzan_img);
        dianzanText = (TextView) findViewById(R.id.activity_detail_dianzan_text);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        sp = getSharedPreferences("user", MODE_PRIVATE);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        screeWidth = getResources().getDisplayMetrics().widthPixels;
        dp10 = DimenUtils.dip2px(this, 10);
        adapter = new PL_List_Adapter(this);
        adapter.setOnHuifuListener(this);
        dp180 = DimenUtils.dip2px(this, 180);
        dp7 = DimenUtils.dip2px(this, 7);
        banner = (Banner) findViewById(R.id.banner);
        imageList = new ArrayList<>();

        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                if(imageList!=null){
                    ScaleImageUtil.openBigIagmeMode(activity_Detail.this,imageList,position);
                }
            }
        });
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        banner.setImageLoader(new ImageLoader() {
            @Override
            public void displayImage(Context context, Object path, ImageView imageView) {
                Glide.with(context).load(path).override(getResources().getDisplayMetrics().widthPixels-DimenUtils.dip2px(context,20),DimenUtils.dip2px(context,180))
                        .centerCrop().into(imageView);
            }
        });
        banner.setDelayTime(3000);
        //图片地址数组

        //轮播图圆点layout
//        PointLayou = (LinearLayout) findViewById(R.id.activity_detail_circlePoint_layout);
        //活动Id
        Id = getIntent().getStringExtra("id");
        //返回按钮
       findViewById(R.id.back).setVisibility(View.VISIBLE);


//        //页面标题
//        ((TextView) findViewById(R.id.title_title)).setText(mApplication.ST("活动详情"));

        //分享按钮
        rightImg = (ImageView) findViewById(R.id.title_image2);
//        rightImg.setOnClickListener(this);
        //标题，发布时间
        title = (TextView) findViewById(R.id.activity_detail_title);
        fabuTime = (TextView) findViewById(R.id.activity_detail_time);
        //轮播
//        viewPager = (ViewPager) findViewById(R.id.activity_detail_viewPager);
        //发起单位，活动地点，活动时间，已报名人数
        faqidanwei = (TextView) findViewById(R.id.activity_detail_faxidanwei);
        huodongdidian = (TextView) findViewById(R.id.activity_detail_huodongdidian);
        huodongTime = (TextView) findViewById(R.id.activity_detail_huodongshijian);
        peopleNum = (TextView) findViewById(R.id.activity_detail_peopleNum);
        //报名入口
        Tobaoming = (TextView) findViewById(R.id.activity_detail_baoming);
//        Tobaoming.setOnClickListener(this);
        //点赞
        dianzan = (LinearLayout) findViewById(R.id.activity_detail_dianzan);
//        dianzan.setOnClickListener(this);
        //活动详情
        content = (TextView) findViewById(R.id.activity_detail_info);
        //评论列表
        PlListVIew = (mPLlistview) findViewById(R.id.activity_detail_listview);
        PlListVIew.footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlListVIew.footer.setText(mApplication.ST("正在加载"));
                if (!endPage.equals(page)) page = String.valueOf(Integer.valueOf(page) + 1);
                getPLData();
            }
        });
        //评论框底部
        PLText = (EditText) findViewById(R.id.activity_detail_apply_edt);
        PLText.setHint(mApplication.ST("写入你的评论(300字以内)"));
        FaSong = (TextView) findViewById(R.id.activity_detail_fasong);
        FaSong.setText(mApplication.ST("发送"));
        shoucang = (ImageView) findViewById(R.id.activity_detail_shoucang);
//        FaSong.setOnClickListener(this);
//        shoucang.setOnClickListener(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 退出页面的设置
     */
    @Override
    protected void onDestroy() {
        if (thread.isAlive()) thread.interrupt();
        OkGo.getInstance().cancelTag(TAG);
        if (needTochange) {
            Intent intent = new Intent("Mine_SC");
            sendBroadcast(intent);
        }
        super.onDestroy();
        UMShareAPI.get(this).release();
//        ShareManager.release();
    }

    /**
     * 获取评论数据
     */
    private void getPLData() {
        new Thread(new Runnable() {
            ArrayList<HashMap<String, String>> Pllist;

            @Override
            public void run() {
                try {
                    JSONObject js=new JSONObject();
                    try {
                        js.put("act_id", Id);
                        js.put("page",page);
                        js.put("m_id",Constants.M_id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m=ApisSeUtil.i(js);
                    String data = OkGo.post(Constants.Activity_pinglun_IP)
                            .params("key",m.K())
                            .params("msg",m.M())
                            .execute().body().string();
                    if (!TextUtils.isEmpty(data)) {
                        Log.w(TAG, "run:      PLdata------>" + data);
                        Pllist = AnalyticalJSON.getList(data, "comment");

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                if ((Pllist != null)) {
                                    PlListVIew.setFocusable(false);
                                    if (adapter.mlist.size() == 0 && Pllist.size() == 0) {//没有评论的时候
                                        tv = new TextView(activity_Detail.this);
                                        tv.setText(mApplication.ST("还没有评论嘞"));
                                        PlListVIew.addHeaderView(tv);
                                        PlListVIew.footer.setVisibility(View.GONE);
                                        PlListVIew.setAdapter(adapter);
                                        ((TextView) findViewById(R.id.p1)).setText("暂无评论");
                                        LogUtil.e("暂无评论");
                                        return;
                                    }
                                    if (adapter.mlist.size() == 0) {//添加评论的的时候
                                        adapter.addList(Pllist);
                                        PlListVIew.setAdapter(adapter);
                                        if (Pllist.size() < 20) {
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
                                        if (Pllist.size() < 20) {
                                            endPage = page;
                                            PlListVIew.footer.setText(mApplication.ST("没有更多数据了"));
                                            PlListVIew.footer.setEnabled(false);
                                        } else {
                                            PlListVIew.footer.setText(mApplication.ST("点击加载更多"));
                                        }
                                    }
                                } else {
                                    PlListVIew.footer.setText(mApplication.ST("没有更多数据了"));
                                    PlListVIew.footer.setEnabled(false);
                                }
                            }
                        });

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 加载详情页数据（不包括评论）
     */
    private void LoadData() {
//        if (progressDialog == null) {
//            progressDialog = ProgressDialog.show(this, null, );
//            progressDialog.setCanceledOnTouchOutside(true);
//        }
        ProgressUtil.show(this, "", mApplication.ST("正在加载...."));
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject js=new JSONObject();
                    try {
                        js.put("act_id", Id);
                        js.put("m_id",Constants.M_id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m=ApisSeUtil.i(js);
                    String data = OkGo.post(Constants.Activity_detail_IP).tag(TAG)
                            .params("key",m.K())
                            .params("msg",m.M()).execute().body().string();
                    if (!TextUtils.isEmpty(data) && !"null".equals(data)) {
                        Log.e(TAG, "run: " + data + "   id-=-=>" + Id);
                        HashMap<String, String> totalMap = AnalyticalJSON.getHashMap(data);
                        if (totalMap != null) {
                            final HashMap<String, String> map = AnalyticalJSON.getHashMap(totalMap.get("activity"));
                            if (map != null) {//加载数据成功
                                totalMap = null;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(!isKECheng){
                                            if (map.get("prol") != null &&!map.get("prol").equals("")) {
                                                act_prol = map.get("prol");
                                                LogUtil.e("活动协议：：" + act_prol);
                                            } else {
                                                LogUtil.e("没有活动协议");
                                            }
                                            KC_Tip.setVisibility(View.GONE);
                                        }else{
                                            KC_Tip.setVisibility(View.VISIBLE);
                                            String m="人数:" + map.get("enrollment") + "/" + map.get("quota");
                                            String s;
                                            if(map.get("quota").equals("")||Integer.valueOf(map.get("quota"))<=0){
                                                LogUtil.e("<=");
                                                s=((map.get("money").equals("")||Double.valueOf(map.get("money")).intValue()<=0)?"免费":("¥ " + map.get("money")))+"   "+
                                                        "人数不限";
                                            }else{
                                                LogUtil.e(">");
                                                s=((map.get("money").equals("")||Double.valueOf(map.get("money")).intValue()<=0)?"免费":("¥ " + map.get("money")))+"   "+
                                                        m;
                                            }

                                            LogUtil.e(s);
                                            KC_Tip.setText(s);
                                            LogUtil.e("课程不需要协议");
                                        }

                                        dianzan.setOnClickListener(activity_Detail.this);
                                        Tobaoming.setOnClickListener(activity_Detail.this);
                                        rightImg.setOnClickListener(activity_Detail.this);
                                        FaSong.setOnClickListener(activity_Detail.this);
                                        shoucang.setOnClickListener(activity_Detail.this);
                                        title.setText(mApplication.ST(map.get("title")));//标题
                                        fabuTime.setText(mApplication.ST((isKECheng?"发布时间:":"发布时间: " )+ TimeUtils.getTrueTimeStr(map.get("time"))));//发布时间
                                        setUrlToImage(map);//保存图片地址并加载,显示小圆点
                                        faqidanwei.append(mApplication.ST((isKECheng?"主办方:":"发起单位:") + map.get("author")));//发布单位
                                        huodongdidian.append(mApplication.ST((isKECheng?"课程地点:":"活动地点:") + map.get("address")));//活动地点
//                                        String acttime = map.get("act_time");
                                        String endTime = map.get("end_time");
                                        String startTime = map.get("start_time");
//                                        if ((TimeUtils.dataOne(acttime) - System.currentTimeMillis()) <= 0) {
//                                            huodongTime.append(mApplication.ST("活动时间:活动已开始"));
//                                        } else {
                                        huodongTime.append(mApplication.ST((isKECheng?"预约开始时间:":"报名开始时间:") + startTime) + "\n"+(isKECheng?"预约结束时间:":"报名结束时间:") + endTime
                                        );//活动时间
//                                        }
                                        if ((TimeUtils.dataOne(endTime) - System.currentTimeMillis()) <= 0) {
                                            Tobaoming.setText(mApplication.ST((isKECheng?"课程预约已结束":"报名已结束")));
                                            Tobaoming.setEnabled(false);
                                            Tobaoming.setBackgroundColor(Color.DKGRAY);
                                        }
                                        peopleNum.append(mApplication.ST((isKECheng?"已预约人数:":"已报名人数:" )+ map.get("enrollment")));//已报名人数
                                        dianzanText.setText(map.get("likes"));
                                        content.setText(mApplication.ST("    " + map.get("abstract")));//简介
//                                        String md5 = MD5Utls.stringToMD5(Constants.safeKey);
//                                        String m1 = md5.substring(0, 16);
//                                        String m2 = md5.substring(16, md5.length());
                                        umWeb = new UMWeb(Constants.FX_host_Ip + TAG + "/id/"  + Id );
                                        LogUtil.e("链接：：：：" + Constants.FX_host_Ip + TAG + "/id/"  + Id );
                                        umWeb.setTitle(mApplication.ST(map.get("title")));
                                        umWeb.setDescription(mApplication.ST(map.get("abstract")));
                                        umWeb.setThumb(new UMImage(activity_Detail.this, map.get("image1")));
                                        getPLData();
                                        ProgressUtil.dismiss();
                                    }
                                });
                            }
                        } else {//加载失败
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ProgressUtil.dismiss();
                                    Toast.makeText(activity_Detail.this, mApplication.ST("请检查网络连接"), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(activity_Detail.this, mApplication.ST((isKECheng?"该课程预约已结束":"该活动已结束")), Toast.LENGTH_SHORT).show();
                                ProgressUtil.dismiss();
                                finish();
                            }
                        });
                    }
                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ProgressUtil.dismiss();
                                    Toast.makeText(activity_Detail.this, mApplication.ST("请检查网络连接"), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            }
        });
        thread.start();
    }


    /**
     * 添加评论
     *
     * @param v 发送按钮
     */
    private void addPL(final View v) {
        if (PLText.getText().toString().trim().equals("")) {
            Toast.makeText(this, mApplication.ST("请输入评论"), Toast.LENGTH_SHORT).show();
            return;
        }
        if (sp.getString("pet_name", "").trim().equals("")) {
            Toast.makeText(this, mApplication.ST("请完善信息"), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, Mine_gerenziliao.class);
            startActivity(intent);
            return;
        }
        v.setEnabled(false);
        ProgressUtil.show(this, "", mApplication.ST("正在提交"));
        if (!isPLing) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final String content = PLText.getText().toString();
                        JSONObject js=new JSONObject();
                        try {
                            js.put("act_id", Id);
                            js.put("ct_contents", content);
                            js.put("m_id",Constants.M_id);
                            js.put("user_id", PreferenceUtil.getUserIncetance(mApplication.getInstance()).getString("user_id",""));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ApisSeUtil.M m=ApisSeUtil.i(js);
                        final String data = OkGo.post(Constants.Activity_pinglun_add_IP)
                                .params("key",m.K())
                                .params("msg",m.M()).execute().body().string();
                        if (!data.equals("")) {
                            Log.i(TAG, "run:      data------>" + data);
                            final HashMap<String, String> hashMap = AnalyticalJSON.getHashMap(data);
                            if (hashMap != null && "000".equals(hashMap.get("code"))) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ((TextView) findViewById(R.id.p1)).setText("最新评论");
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
                                        map.put("id", hashMap.get("id"));
                                        map.put("reply", new JSONArray().toString());
                                        PlListVIew.setFocusable(true);
                                        if (adapter.mlist.size() == 0) {
                                            adapter.mlist.add(0, map);
                                            adapter.flagList.add(0, false);
                                            if (tv != null) PlListVIew.removeHeaderView(tv);
                                            PlListVIew.setAdapter(adapter);

                                        } else {
                                            adapter.mlist.add(0, map);
                                            adapter.flagList.add(0, false);
                                            adapter.notifyDataSetChanged();

                                        }
                                        PlListVIew.setSelection(0);
                                        v.setEnabled(true);
                                        PLText.setText("");
                                        imm.hideSoftInputFromWindow(PLText.getWindowToken(), 0);
                                        overlay.setVisibility(View.GONE);
                                        Toast.makeText(activity_Detail.this, mApplication.ST("添加评论成功"), Toast.LENGTH_SHORT).show();
                                        ProgressUtil.dismiss();
                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(activity_Detail.this, mApplication.ST("上传评论失败，请重新尝试"), Toast.LENGTH_SHORT).show();
                                        ProgressUtil.dismiss();
                                        v.setEnabled(true);
                                    }
                                });
                            }
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(activity_Detail.this, mApplication.ST("上传评论失败，请重新尝试"), Toast.LENGTH_SHORT).show();
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
                        JSONObject js=new JSONObject();
                        try {
                            js.put("ct_id", currentId);
                            js.put("ct_contents", content);
                            js.put("m_id",Constants.M_id);
                            js.put("user_id", PreferenceUtil.getUserIncetance(mApplication.getInstance()).getString("user_id",""));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ApisSeUtil.M m=ApisSeUtil.i(js);
                        final String data = OkGo.post(Constants.little_zixun_pl_add_IP)
                                .params("key",m.K())
                                .params("msg",m.M()).execute().body().string();
                        if (!data.equals("")) {
                            final HashMap<String, String> hashMap = AnalyticalJSON.getHashMap(data);
                            if (hashMap != null && "000".equals(hashMap.get("code"))) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (currentLayout.getVisibility() == View.GONE) {
                                            currentLayout.setVisibility(View.VISIBLE);
                                        }
                                        TextView textView = new TextView(activity_Detail.this);
                                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                        layoutParams.setMargins(0, DimenUtils.dip2px(activity_Detail.this, 5), 0, DimenUtils.dip2px(activity_Detail.this, 5));
                                        textView.setLayoutParams(layoutParams);
                                        String pet_name = sp.getString("pet_name", "");
                                        SpannableStringBuilder ssb = new SpannableStringBuilder(pet_name + ":" + mApplication.ST(content));
                                        ssb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(activity_Detail.this, R.color.main_color)), 0, pet_name.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                                        textView.setText(ssb);
                                        currentLayout.addView(textView);
                                        PLText.setText("");
                                        PLText.setHint(mApplication.ST("写入您的评论（300字以内）"));
                                        imm.hideSoftInputFromWindow(PLText.getWindowToken(), 0);
                                        overlay.setVisibility(View.GONE);
                                        PlListVIew.setSelection(currentPosition);
                                        FaSong.setEnabled(true);
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
                                    Toast.makeText(activity_Detail.this, mApplication.ST("回复提交失败，请重新尝试"), Toast.LENGTH_SHORT).show();
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
    }

    /**
     * 加载轮播图片并加载小圆点
     *
     * @param map
     */
    private void setUrlToImage(final HashMap<String, String> map) {
        String image1 = map.get("image1");
        String image2 = map.get("image2");
        String image3 = map.get("image3");
        if (!TextUtils.isEmpty(image1)) {
            imageList.add(image1);
        }
        if (!TextUtils.isEmpty(image2)) {
            imageList.add(image2);
        }
        if (!TextUtils.isEmpty(image3)) {
            imageList.add(image3);
        }
        banner.setImages(imageList);
        banner.start();
    }


    /**
     * 点击事件
     *
     * @param v
     */
    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.activity_detail_fasong:
                addPL(v);//添加评论
                break;
            case R.id.activity_detail_dianzan://点赞
                if (sp.getString("user_id", "").equals("") && sp.getString("uid", "").equals("")) {
                    Intent intent = new Intent(this, Login.class);
                    startActivity(intent);
                    Toast.makeText(activity_Detail.this, mApplication.ST("请先登录"), Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject js=new JSONObject();
                                try {
                                    js.put("act_id", Id);
                                    js.put("m_id",Constants.M_id);
                                    js.put("user_id", PreferenceUtil.getUserIncetance(mApplication.getInstance()).getString("user_id",""));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                ApisSeUtil.M m=ApisSeUtil.i(js);
                                String data = OkGo.post(Constants.Activity_DZ_IP)
                                        .params("key",m.K())
                                        .params("msg",m.M()).execute().body().string();
                                if (!data.equals("")) {
                                    HashMap<String, String> map = AnalyticalJSON.getHashMap(data);
                                    if (map != null && map.get("code").equals("000")) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                dianzanText.setText((Integer.valueOf(dianzanText.getText().toString()) + 1) + "");
                                                dianzanText.setTextColor(getResources().getColor(R.color.main_color));
                                                dianzanImg.setImageResource(R.drawable.dianzan1);
                                                dianzan.setEnabled(false);
                                            }
                                        });

                                    } else {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                dianzanText.setTextColor(getResources().getColor(R.color.main_color));
                                                dianzanImg.setImageResource(R.drawable.dianzan1);
                                                dianzan.setEnabled(false);
                                                Toast.makeText(activity_Detail.this, mApplication.ST("已点过赞啦"), Toast.LENGTH_SHORT).show();
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
            case R.id.activity_detail_baoming://报名页面

//                if (new LoginUtil().checkLogin(activity_Detail.this)) {
//                    AlertDialog.Builder builder1 = new AlertDialog.Builder(activity_Detail.this);
//                    builder1.setCancelable(true);
//                    builder1.setPositiveButton(mApplication.ST("取消"), new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    }).setTitle(mApplication.ST("确定报名参加" + title.getText().toString() + "吗？")).setNegativeButton(mApplication.ST("确定"), new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//
//                            JSONObject js=new JSONObject();
//                            try {
//                                js.put("act_id", Id);
//                                js.put("m_id",Constants.M_id);
//                                js.put("user_id", PreferenceUtil.getUserIncetance(mApplication.getInstance()).getString("user_id",""));
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                            ApisSeUtil.M m=ApisSeUtil.i(js);
//                            OkGo.post(Constants.Activity_BaoMing).tag(TAG)
//                                    .params("key",m.K())
//                                    .params("msg",m.M()).execute(new AbsCallback<HashMap<String, String>>() {
//                                @Override
//                                public HashMap<String, String> convertSuccess(okhttp3.Response response) throws Exception {
//                                    return AnalyticalJSON.getHashMap(response.body().string());
//                                }
//
//
//                                @Override
//                                public void onBefore(BaseRequest request) {
//                                    super.onBefore(request);
//                                    ProgressUtil.show(activity_Detail.this, "", mApplication.ST(isKECheng?"正在预约，请稍等":"正在报名，请稍等"));
//                                }
//
//                                @Override
//                                public void onSuccess(HashMap<String, String> map, Call call, okhttp3.Response response) {
//                                    View view = LayoutInflater.from(activity_Detail.this).inflate(R.layout.baoming_alert, null);
//                                    AlertDialog.Builder b = new AlertDialog.Builder(activity_Detail.this).
//                                            setView(view);
//                                    final AlertDialog d = b.create();
//                                    d.getWindow().setDimAmount(0.2f);
//                                    view.findViewById(R.id.commit).setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            d.dismiss();
//                                        }
//                                    });
//                                    if ("000".equals(map.get("code"))) {
//                                        ((TextView) view.findViewById(R.id.result_msg)).setText(mApplication.ST(isKECheng?"您已成功预约":"您已成功报名"));
//                                        view.findViewById(R.id.commit).setBackgroundColor(Color.parseColor("#40d976"));
//                                    } else {
//                                        ((TextView) view.findViewById(R.id.result_msg)).setText(mApplication.ST(isKECheng?"您已经预约过了哟~":"您已经报名过了哟~"));
//                                        view.findViewById(R.id.commit).setBackgroundColor(Color.parseColor("#e75e5e"));
//                                    }
////                                    ((TextView) view.findViewById(R.id.phone)).setText(mApplication.ST("审核结果请及时关注App我的活动：\n[我的]->[活动]"));
//
//                                    d.show();
//                                }
//
//                                @Override
//                                public void onAfter(HashMap<String, String> map, Exception e) {
//                                    super.onAfter(map, e);
//                                    ProgressUtil.dismiss();
//                                }
//
//
//                            });
//
//                        }
//                    }).create().show();


//                }
                if (new LoginUtil().checkLogin(activity_Detail.this)) {
//                    if (sp.getString("phone", "").equals("")) {
//                        Intent intent = new Intent(this, PhoneCheck.class);
//                        startActivity(intent);
//                        Toast.makeText(this, "请完善手机号码方便我们的工作人员与您联系", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
                    if (act_prol != null && !act_prol.equals("")) {
                        View view = LayoutInflater.from(this).inflate(R.layout.activity_confirm_dialog2, null);
                        final WebView web = (WebView) view.findViewById(R.id.web);
                        TextView cancle = (TextView) view.findViewById(R.id.cancle);
                        cancle.setText(mApplication.ST("点错了"));
                        final TextView baoming = (TextView) view.findViewById(R.id.baoming);
                        baoming.setEnabled(false);
                        final CountDownTimer cdt = new CountDownTimer(10000, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                baoming.setText(mApplication.ST("请阅读报名须知(" + millisUntilFinished / 1000 + "秒)"));
                            }

                            @Override
                            public void onFinish() {
                                baoming.setText(mApplication.ST("同意"));
                                baoming.setEnabled(true);
                            }
                        };
                        web.loadDataWithBaseURL("", act_prol
                                , "text/html", "UTF-8", null);
//                    Api.getUserNeedKnow(this,web);
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setView(view);
                        final AlertDialog dialog = builder.create();
                        cancle.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                web.destroy();
                                cdt.cancel();
                                dialog.dismiss();
                            }
                        });
                        baoming.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                showBaoMingDialog();

                            }
                        });
                        cdt.start();
                        dialog.show();

                    } else {
                        showBaoMingDialog();
                    }
                }

                break;
            case R.id.back://返回
                finish();
                break;
            case R.id.title_image2://分享
                imm.hideSoftInputFromWindow(PLText.getWindowToken(), 0);
                if (umWeb != null) {
                    new ShareManager().shareWeb(umWeb, activity_Detail.this);
                }
                break;
            case R.id.activity_detail_shoucang:
                if (!new LoginUtil().checkLogin(this)) {
                    return;
                }
                if (!Network.HttpTest(this)) {
                    Toast.makeText(activity_Detail.this, mApplication.ST("请检查网络连接"), Toast.LENGTH_SHORT).show();
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject js=new JSONObject();
                            try {
                                js.put("act_id", Id);
                                js.put("m_id",Constants.M_id);
                                js.put("user_id", PreferenceUtil.getUserIncetance(mApplication.getInstance()).getString("user_id",""));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ApisSeUtil.M m=ApisSeUtil.i(js);
                            String data = OkGo.post(Constants.Activity_Shoucang_IP)
                                    .params("key",m.K())
                                    .params("msg",m.M()).execute().body().string();
                            if (!data.equals("")) {
                                if (AnalyticalJSON.getHashMap(data) != null && "000".equals(AnalyticalJSON.getHashMap(data).get("code"))) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(activity_Detail.this, mApplication.ST("添加收藏成功"), Toast.LENGTH_SHORT).show();
                                            v.setSelected(true);
                                            needTochange = true;

                                        }
                                    });
                                } else if (AnalyticalJSON.getHashMap(data) != null && "002".equals(AnalyticalJSON.getHashMap(data).get("code"))) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(activity_Detail.this, mApplication.ST("已取消收藏"), Toast.LENGTH_SHORT).show();
                                            v.setSelected(false);
                                            needTochange = true;

                                        }
                                    });
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(activity_Detail.this, mApplication.ST("服务器异常"), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ProgressUtil.dismiss();
    }
    private void showBaoMingDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(activity_Detail.this);
        builder1.setCancelable(true);
        builder1.setPositiveButton(mApplication.ST("取消"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setTitle(mApplication.ST("确定报名参加" + title.getText().toString() + "吗？")).setNegativeButton(mApplication.ST("确定"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                BaoMing();
            }


        }).create().show();
    }
    private void BaoMing() {

//        if (sp.getString("perfect", "1").equals("1")&&!act_prol.equals("")) {
//            Intent intent = new Intent(activity_Detail.this, ZhiFuShare.class);
//            intent.putExtra(ZhiFuShare.ISFORM, true);
//            startActivity(intent);
//            Toast.makeText(activity_Detail.this, mApplication.ST("您还未完善资料，快去完善资料吧"), Toast.LENGTH_SHORT).show();
//        } else {
            JSONObject js = new JSONObject();
            try {
                js.put("act_id", Id);
                js.put("user_id", sp.getString("user_id", ""));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ApisSeUtil.M m = ApisSeUtil.i(js);
            OkGo.post(Constants.Activity_BaoMing).tag(TAG)
                    .params("key", m.K())
                    .params("msg", m.M())
                    .execute(new AbsCallback<HashMap<String, String>>() {
                        @Override
                        public HashMap<String, String> convertSuccess(Response response) throws Exception {
                            return AnalyticalJSON.getHashMap(response.body().string());
                        }


                        @Override
                        public void onBefore(BaseRequest request) {
                            super.onBefore(request);
                            ProgressUtil.show(activity_Detail.this, "", mApplication.ST("正在报名，请稍等"));
                        }

                        @Override
                        public void onSuccess(final HashMap<String, String> map, Call call, Response response) {
                            if (map != null) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        View view = LayoutInflater.from(activity_Detail.this).inflate(R.layout.baoming_alert, null);
                                        AlertDialog.Builder b = new AlertDialog.Builder(activity_Detail.this).
                                                setView(view);
                                        final AlertDialog d = b.create();
                                        d.getWindow().setDimAmount(0.2f);
                                        view.findViewById(R.id.commit).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                d.dismiss();
                                            }
                                        });
                                        if ("000".equals(map.get("code"))) {
                                            ((TextView) view.findViewById(R.id.result_msg)).setText(mApplication.ST("您已成功报名"));
                                            view.findViewById(R.id.commit).setBackgroundColor(Color.parseColor("#40d976"));
                                        } else if ("003".equals(map.get("code"))) {
                                            ((TextView) view.findViewById(R.id.result_msg)).setText(mApplication.ST("您已经报名过了哟~"));
                                            view.findViewById(R.id.commit).setBackgroundColor(Color.parseColor("#e75e5e"));
                                        }
                                        ((TextView) view.findViewById(R.id.phone)).setText(mApplication.ST("审核结果请及时关注App我的活动"));

                                        d.show();
                                    }
                                });

                            }
                        }

                        @Override
                        public void onAfter(HashMap<String, String> map, Exception e) {
                            super.onAfter(map, e);
                            ProgressUtil.dismiss();
                        }


                    });
        }
//    }
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
        PLText.requestFocus();
    }
}
