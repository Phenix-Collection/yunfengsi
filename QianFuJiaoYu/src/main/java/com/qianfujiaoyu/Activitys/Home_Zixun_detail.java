package com.qianfujiaoyu.Activitys;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.qianfujiaoyu.Adapter.PL_List_Adapter;
import com.qianfujiaoyu.Base.ScaleImageUtil;
import com.qianfujiaoyu.R;
import com.qianfujiaoyu.Setting.JuBaoActivity;
import com.qianfujiaoyu.Setting.Mine_gerenziliao;
import com.qianfujiaoyu.Utils.AnalyticalJSON;
import com.qianfujiaoyu.Utils.ApisSeUtil;
import com.qianfujiaoyu.Utils.Constants;
import com.qianfujiaoyu.Utils.DimenUtils;
import com.qianfujiaoyu.Utils.LoginUtil;
import com.qianfujiaoyu.Utils.Network;
import com.qianfujiaoyu.Utils.PreferenceUtil;
import com.qianfujiaoyu.Utils.ProgressUtil;
import com.qianfujiaoyu.Utils.StatusBarCompat;
import com.qianfujiaoyu.Utils.TimeUtils;
import com.qianfujiaoyu.Utils.ToastUtil;
import com.qianfujiaoyu.Utils.mApplication;
import com.qianfujiaoyu.View.mAudioManager;
import com.qianfujiaoyu.View.mAudioView;
import com.qianfujiaoyu.View.mPLlistview;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

/**
 * Created by Administrator on 2016/11/7.
 */
public class Home_Zixun_detail extends AppCompatActivity implements View.OnClickListener, PL_List_Adapter.onHuifuListener {
    private ImageView back, dianzanImg;
    private static final String TAG = "Home_Zixun_detail";
    private TextView title, time, user, fasong, plNum;
    private TextView dianzan;
    private TextView content;
    private FrameLayout options;
    private mPLlistview PlListVIew;
    private EditText PLText;
    private String id;
    private String page = "1";
    private String endPage = "";
    private ArrayList<HashMap<String, String>> Pllist;
    //    private AvatarImageView head;
    private PL_List_Adapter adapter;
    private ImageView shoucang;
    //    private ProgressDialog progressDialog;
    private SharedPreferences sp;
    //第一次加载的评论数量
    private int firstNum = 0;
    //第一次加载的评论map
    private HashMap<String, String> FirstMap;
    //无评论时的header
    private TextView tv;
    private InputMethodManager imm;
    private ListView listView;
    private ValueAnimator va;
    JCVideoPlayerStandard player;
    private boolean needTochange = false;
    private LinearLayout dianzanLayout;
    private int matchWidth;
    private imgAdapter adapter1;
    private LinearLayout bootomLayout, FirstLayout;
    private LinearLayout pinglun_bottom;
    private TextView dianzan_bottom;
    private ImageView zhifu;
    private int screenHeight, screenWidth;
    private LinearLayout currentLayout;
    private int currentPosition;
    private String currentId;
    private boolean isPLing = false;


    private LinearLayout pinglun, fenxiangb;
    private FrameLayout overlay;
    private Dialog dialog;
    private long downtime;
    float _x, _y;
    private String User_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.home_zixun_detail);
        initView();
        LoadData();
    }

    private void initView() {
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
        ((TextView) pinglun.findViewById(R.id.pltv)).setText("评论");
        pinglun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                overlay.setVisibility(View.VISIBLE);
                PLText.requestFocus();
                isPLing = false;
                PLText.setHint(mApplication.ST("写入您的评论（300字以内）"));
//                        imm. showSoftInput(PLText,2);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

            }
        });
        fenxiangb = (LinearLayout) findViewById(R.id.fenxiangb);
        ((TextView) fenxiangb.findViewById(R.id.fxtv)).setText("分享");
        fenxiangb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (umWeb != null) {
//                    new ShareManager().shareWeb(umWeb, ZiXun_Detail.this);
//                }

            }
        });
        screenHeight = getResources().getDisplayMetrics().heightPixels;
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        matchWidth = getResources().getDisplayMetrics().widthPixels - DimenUtils.dip2px(this, 10);
        listView = (ListView) findViewById(R.id.zixun_item_iamges);
        options = (FrameLayout) findViewById(R.id.zixun_item_option);
        shoucang = (ImageView) findViewById(R.id.zixun_item_shoucang);
        shoucang.setOnClickListener(this);
        Pllist = new ArrayList<>();
        adapter = new PL_List_Adapter(this);
        adapter.setOnHuifuListener(this);
        PLText = (EditText) findViewById(R.id.zixun_detail_apply_edt);
        sp = PreferenceUtil.getUserIncetance(this);
        fasong = (TextView) findViewById(R.id.zixun_detail_fasong);
        fasong.setOnClickListener(this);
        fasong.setText("发送");
        findViewById(R.id.zixun_detail_fenxiang2).setOnClickListener(this);

        back = (ImageView) this.findViewById(R.id.zixun_item_back);
        title = (TextView) findViewById(R.id.zixun_item_title);
        time = (TextView) findViewById(R.id.zixun_item_time);
        user = (TextView) findViewById(R.id.zixun_item_name);
        content = (TextView) findViewById(R.id.zixun_item_content);

        PlListVIew = (mPLlistview) findViewById(R.id.zixun_item_plListview);
        PlListVIew.setFooterDividersEnabled(false);
        PlListVIew.footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlListVIew.footer.setText("正在加载");
                if (!endPage.equals(page)) page = String.valueOf(Integer.valueOf(page) + 1);
                getPLandSet(FirstMap);
            }
        });
        plNum = (TextView) findViewById(R.id.zixun_item_pinlunNum);
        imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        back.setOnClickListener(this);
        shoucang.setOnClickListener(this);
        dianzanLayout = (LinearLayout) findViewById(R.id.zixun_item_dianzan);
        dianzanImg = (ImageView) findViewById(R.id.zixun_item_dianzan_img);
        dianzan = (TextView) findViewById(R.id.zixun_item_dianzan_text);
        dianzan_bottom = (TextView) findViewById(R.id.jubao);
        dianzan_bottom.setOnClickListener(this);
        findViewById(R.id.zixun_detail_fenxiang2).setOnClickListener(this);
        if ("1".equals(getIntent().getStringExtra("type"))) {
            shoucang.setSelected(true);
        }
        if (getIntent().getStringExtra("name") != null) {
            user.setText(getIntent().getStringExtra("name"));
        }

    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.zixun_item_shoucang://收藏
                if (!new LoginUtil().checkLogin(this)) {
                    return;
                }
                if (!Network.HttpTest(this)) {
                    Toast.makeText(Home_Zixun_detail.this, "请检查网络连接", Toast.LENGTH_SHORT).show();
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject js=new JSONObject();
                            try {
                                js.put("draft_id", id);
                                js.put("m_id",Constants.M_id);
                                js.put("user_id", PreferenceUtil.getUserIncetance(mApplication.getInstance()).getString("user_id",""));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ApisSeUtil.M m=ApisSeUtil.i(js);
                            String data = OkGo.post(Constants.Zixun_shoucang_cancle_IP)
                                    .params("key",m.K())
                                    .params("msg",m.M()).execute().body().string();
                            if (!data.equals("")) {
                                HashMap<String, String> map = AnalyticalJSON.getHashMap(data);
                                if (map != null && "000".equals(AnalyticalJSON.getHashMap(data).get("code"))) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(Home_Zixun_detail.this, "添加收藏成功", Toast.LENGTH_SHORT).show();
                                            v.setSelected(true);
                                            needTochange = true;

                                        }
                                    });
                                } else if (map != null && "002".equals(AnalyticalJSON.getHashMap(data).get("code"))) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(Home_Zixun_detail.this, "已取消收藏", Toast.LENGTH_SHORT).show();
                                            v.setSelected(false);
                                            needTochange = true;

                                        }
                                    });
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(Home_Zixun_detail.this, "服务器异常", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            case R.id.zixun_detail_fenxiang2://底部分享
                imm.hideSoftInputFromWindow(PLText.getWindowToken(), 0);
                fenxiangb.performClick();
                break;
            case R.id.jubao:
                Intent intent = new Intent(this, JuBaoActivity.class);
                startActivity(intent);
                break;
//            case R.id.pinglun:
//                bootomLayout.setVisibility(View.VISIBLE);
//                PLText.setHint("请输入您的评论");
//                PLText.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        imm.showSoftInput(PLText,InputMethodManager.SHOW_FORCED);
//                    }
//                },200);
//                FirstLayout.setVisibility(View.GONE);
//                break;


            case R.id.zixun_item_back://返回
                finish();
                break;

            case R.id.zixun_detail_fasong://发送提交评论
                if(!new LoginUtil().checkLogin(Home_Zixun_detail.this)){
                    return;
                }
                if(sp.getString("pet_name","").equals("")){
                    Intent intent1=new Intent(this, Mine_gerenziliao.class);
                    startActivity(intent1);
                    ToastUtil.showToastShort("请设置基本信息");
                    return;
                }
                if (PLText.getText().toString().trim().equals("")) {
                    Toast.makeText(this, "请输入评论", Toast.LENGTH_SHORT).show();
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
                                JSONObject js=new JSONObject();
                                try {
                                    js.put("draft_id", id);
                                    js.put("ct_contents", content);
                                    js.put("type", "1");
                                    js.put("m_id",Constants.M_id);
                                    js.put("user_id", PreferenceUtil.getUserIncetance(mApplication.getInstance()).getString("user_id",""));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                ApisSeUtil.M m=ApisSeUtil.i(js);
                                final String data = OkGo.post(Constants.Zixun_commitPL_IP)
                                        .params("key",m.K())
                                        .params("msg",m.M()).execute().body().string();
                                if (!data.equals("")) {
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
                                                map.put("user_id", sp.getString("user_id", ""));
                                                map.put("ct_time", time);
                                                map.put("id", hashMap.get("id"));
                                                map.put("reply", new JSONArray().toString());
//                                                map.put("realname",sp.getString("ident","1"));
                                                PlListVIew.setFocusable(true);
                                                if (adapter.mlist.size() == 0) {
                                                    adapter.mlist.add(0, map);
                                                    adapter.flagList.add(0, false);
                                                    PlListVIew.removeHeaderView(tv);
                                                    PlListVIew.setAdapter(adapter);
                                                    PlListVIew.footer.setText("没有更多数据了");
                                                    PlListVIew.setEnabled(false);
                                                } else {
                                                    adapter.mlist.add(0, map);
                                                    adapter.flagList.add(0, false);
                                                    adapter.notifyDataSetChanged();
                                                    PlListVIew.setEnabled(true);
                                                }
                                                PlListVIew.setSelection(0);
                                                v.setEnabled(true);
                                                firstNum += 1;
                                                overlay.setVisibility(View.GONE);
                                                plNum.setText("评论 " + firstNum);
                                                PLText.setText("");
                                                imm.hideSoftInputFromWindow(PLText.getWindowToken(), 0);
                                                Toast.makeText(Home_Zixun_detail.this, "添加评论成功", Toast.LENGTH_SHORT).show();
                                                ProgressUtil.dismiss();
                                            }
                                        });
                                    }
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            v.setEnabled(true);
                                            Toast.makeText(Home_Zixun_detail.this, "提交评论失败，请重新尝试", Toast.LENGTH_SHORT).show();
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
                                JSONObject js=new JSONObject();
                                try {
                                    js.put("draft_id", currentId);
                                    js.put("ct_contents", content);
                                    js.put("type", "2");
                                    js.put("m_id",Constants.M_id);
                                    js.put("user_id", PreferenceUtil.getUserIncetance(mApplication.getInstance()).getString("user_id",""));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                ApisSeUtil.M m=ApisSeUtil.i(js);
                                final String data = OkGo.post(Constants.Zixun_commitPL_IP)
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
                                                TextView textView = new TextView(Home_Zixun_detail.this);
                                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                                layoutParams.setMargins(0, DimenUtils.dip2px(Home_Zixun_detail.this, 5), 0, DimenUtils.dip2px(Home_Zixun_detail.this, 5));
                                                textView.setLayoutParams(layoutParams);
                                                String pet_name = sp.getString("pet_name", "");
                                                SpannableStringBuilder ssb = new SpannableStringBuilder(pet_name + ":" + content);
                                                ssb.setSpan(new ClickableSpan() {
                                                    @Override
                                                    public void onClick(View widget) {
                                                        Intent intent = new Intent(Home_Zixun_detail.this, User_Detail.class);
                                                        startActivity(intent);
                                                    }
                                                }, 0, pet_name.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                                                textView.setText(ssb);
                                                currentLayout.addView(textView);
                                                PLText.setText("");
                                                overlay.setVisibility(View.GONE);
                                                imm.hideSoftInputFromWindow(PLText.getWindowToken(), 0);
                                                listView.setSelection(currentPosition);
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
                                            Toast.makeText(Home_Zixun_detail.this, "回复提交失败，请重新尝试", Toast.LENGTH_SHORT).show();
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

            case R.id.zixun_item_dianzan:
                dianzanLayout.setEnabled(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject js=new JSONObject();
                            try {
                                js.put("draft_id", id);
                                js.put("m_id",Constants.M_id);
                                js.put("user_id", PreferenceUtil.getUserIncetance(mApplication.getInstance()).getString("user_id",""));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ApisSeUtil.M m=ApisSeUtil.i(js);
                            String data = OkGo.post(Constants.Zixun_dianzan_IP)
                                    .params("key",m.K())
                                    .params("msg",m.M()).execute().body().string();
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
                                            Toast.makeText(Home_Zixun_detail.this, "已点过赞啦", Toast.LENGTH_SHORT).show();
                                            dianzanLayout.setSelected(true);

                                        }
                                    });

                                }
                            }
                        } catch (Exception e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dianzanLayout.setEnabled(true);
                                }
                            });
                            e.printStackTrace();
                        }

                    }
                }).start();
                break;
        }
    }

    /**
     * 检查网络状态
     */
    private void checkNetwork() {
        if (!Network.HttpTest(this)) {
            Toast.makeText(Home_Zixun_detail.this, "网络连接失败，请检查网络", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public void LoadData() {//加载详情数据
        checkNetwork();
        id = getIntent().getStringExtra("id");

        ProgressUtil.show(this, "", "正在加载");
        if (!id.equals("") && id != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String data1 = null;
                    try {
                        JSONObject js=new JSONObject();
                        try {
                            js.put("id", id);
                            js.put("m_id",Constants.M_id);
                            js.put("user_id", PreferenceUtil.getUserIncetance(mApplication.getInstance()).getString("user_id",""));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ApisSeUtil.M m=ApisSeUtil.i(js);
                        data1 = OkGo.post(Constants.Zixun_Detail_IP).tag(TAG)
                                .params("key",m.K())
                                .params("msg",m.M()).execute().body().string();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.e(TAG, "run: data1-------->" + data1);
                    if (data1 != null && !data1.equals("")) {
                        FirstMap = AnalyticalJSON.getHashMap(data1);
                        if (FirstMap != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    User_id = FirstMap.get("user_id");
                                    title.setText(FirstMap.get("title"));
                                    time.setText(TimeUtils.getTrueTimeStr(FirstMap.get("time")));
                                    user.setText(FirstMap.get("pet_name"));
                                    content.setText(FirstMap.get("contents"));
                                    dianzanLayout.setVisibility(View.VISIBLE);
                                    dianzan.setText(FirstMap.get("likes"));
                                    plNum.setVisibility(View.VISIBLE);
                                    plNum.setText("评论 " + FirstMap.get("ctr"));
                                    firstNum = Integer.valueOf(FirstMap.get("ctr"));
                                    if (FirstMap.get("options").endsWith(".mp4")) {
                                        //视频
                                        Log.w(TAG, "onBindViewHolder: 显示视频");
                                        JCVideoPlayerStandard j = new JCVideoPlayerStandard(Home_Zixun_detail.this);
                                        LinearLayout.LayoutParams l = new LinearLayout.LayoutParams(matchWidth, DimenUtils.dip2px(Home_Zixun_detail.this, 180));
                                        options.addView(j);
                                        j.setUp(FirstMap.get("options"), JCVideoPlayer.SCREEN_LAYOUT_NORMAL, "  ");
                                        Glide.with(Home_Zixun_detail.this).load(FirstMap.get("image1")).override(matchWidth, DimenUtils.dip2px(Home_Zixun_detail.this, 180)).centerCrop().placeholder(R.drawable.load_nothing).into(j.thumbImageView);
                                    } else if (FirstMap.get("options").endsWith(".mp3")) {
                                        //音频
                                        Log.w(TAG, "onBindViewHolder: 显示音频");
                                        mAudioManager.release();
                                        final mAudioView mAudioView = new mAudioView(Home_Zixun_detail.this);
                                        mAudioView.setOnImageClickListener(new mAudioView.onImageClickListener() {
                                            @Override
                                            public void onImageClick(final mAudioView v) {
                                                if (mAudioManager.getAudioView() != null && mAudioManager.getAudioView().isPlaying()) {
                                                    mAudioManager.release();
                                                    mAudioManager.getAudioView().setPlaying(false);
                                                    mAudioManager.getAudioView().resetAnim();
                                                    if (v == mAudioManager.getAudioView()) {
                                                        return;
                                                    }
                                                }
                                                if (!mAudioView.isPlaying()) {
                                                    Log.w(TAG, "onImageClick: 开始播放");
                                                    mAudioManager.playSound(v, FirstMap.get("options"), new MediaPlayer.OnCompletionListener() {
                                                        @Override
                                                        public void onCompletion(MediaPlayer mp) {
                                                            mAudioView.resetAnim();
                                                        }
                                                    }, new MediaPlayer.OnPreparedListener() {
                                                        @Override
                                                        public void onPrepared(MediaPlayer mp) {
                                                            mAudioView.setTime(mAudioManager.mMediaplayer.getDuration() / 1000);
                                                        }
                                                    });

                                                } else {
                                                    Log.w(TAG, "onImageClick: 停止播放");
                                                    mAudioManager.release();
                                                }

                                            }
                                        });
                                        options.addView(mAudioView);

                                    }
                                    ArrayList<String> l = new ArrayList<>();
                                    try {
                                        JSONArray js = new JSONArray(FirstMap.get("image"));
                                        for (int i = 0; i < js.length(); i++) {
                                            l.add(((JSONObject) js.get(i)).getString("url"));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    if (l.size() != 0) {
                                        if (adapter1 == null) {
                                            adapter1 = new imgAdapter(Home_Zixun_detail.this, l);
                                        }
                                        listView.setAdapter(adapter1);
                                    } else {
                                        listView.setVisibility(View.GONE);
                                    }

                                    ProgressUtil.dismiss();


                                }
                            });
                            getPLandSet(FirstMap);
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ProgressUtil.dismiss();
                                    Toast.makeText(Home_Zixun_detail.this, "服务器异常，请稍后重试", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Home_Zixun_detail.this, "服务器异常，请稍后重试", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }).start();
        }
    }

    private void getPLandSet(final HashMap<String, String> map) {//加载评论并设置
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject js=new JSONObject();
                    try {
                        js.put("draft_id", id.equals("") ? getIntent().getStringExtra("id") : id);
                        js.put("page",page);
                        js.put("m_id",Constants.M_id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m=ApisSeUtil.i(js);
                    String data = OkGo.post(Constants.Zixun_PL_IP).tag(TAG)
                            .params("key",m.K())
                            .params("msg",m.M()).execute().body().string();
                    if (!data.equals("")) {
                        Pllist = AnalyticalJSON.getList_zj(AnalyticalJSON.getHashMap(data).get("comment"));
                        if ((Pllist != null)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    PlListVIew.setFocusable(false);
                                    if (adapter.mlist.size() == 0 && Pllist.size() == 0) {//没有评论的时候
                                        tv = new TextView(Home_Zixun_detail.this);
                                        tv.setText("还没有评论嘞");
                                        PlListVIew.addHeaderView(tv);
                                        PlListVIew.footer.setVisibility(View.GONE);
                                        PlListVIew.setAdapter(adapter);

                                        return;
                                    }
                                    if (adapter.mlist.size() == 0) {//添加评论的的时候
                                        adapter.addList(Pllist);
                                        PlListVIew.setAdapter(adapter);
                                        if (Pllist.size() < 10) {
                                            endPage = page;
                                            PlListVIew.footer.setText("没有更多数据了");
                                            PlListVIew.footer.setEnabled(false);
                                        } else {
                                            PlListVIew.footer.setText("点击加载更多");
                                        }
                                    } else {
                                        adapter.mlist.addAll(Pllist);
                                        boolean flag = false;

                                        for (int i = 0; i < Pllist.size(); i++) {
                                            adapter.flagList.add(flag);
                                        }
                                        adapter.notifyDataSetChanged();
                                        if (Pllist.size() < 10) {
                                            endPage = page;
                                            PlListVIew.footer.setText("没有更多评论了");
                                            PlListVIew.footer.setEnabled(false);
                                        } else {
                                            PlListVIew.footer.setText("点击加载更多");
                                        }
                                    }
                                    plNum.setText("评论 " + adapter.mlist.size());
                                    firstNum = adapter.mlist.size();
                                }
                            });


                        } else {
                            PlListVIew.footer.setEnabled(false);
                            PlListVIew.footer.setText("没有更多评论了");
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
        super.onDestroy();
        OkGo.getInstance().cancelTag(TAG);
        JCVideoPlayer.releaseAllVideos();
        mAudioManager.release();
    }

    @Override
    public void onHuifuClicked(String id, int p, View v, String pet_name) {
        // TODO: 2016/12/27 评论回复接口
        overlay.setVisibility(View.VISIBLE);
        isPLing = true;
        currentLayout = (LinearLayout) v;
        currentPosition = p;
        currentId = id;
        SpannableString ss = new SpannableString(mApplication.ST("回复 ") + pet_name + " :");
        ss.setSpan(new ForegroundColorSpan(Color.BLACK), 3, pet_name.length() + 3, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        PLText.setHint(ss);
        PLText.requestFocus();


        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private static class imgAdapter extends BaseAdapter {
        private ArrayList<String> list;
        private int imageWidth;
        private Activity a;
        private WeakReference w;

        public imgAdapter(Activity a1, ArrayList<String> list) {
            super();
            this.list = list;
            w = new WeakReference<Activity>(a1);
            this.a = (Activity) w.get();
            imageWidth = this.a.getResources().getDisplayMetrics().widthPixels - DimenUtils.dip2px(a, 10);
        }

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            ImageView imageView = new ImageView(a);
            imageView.setLayoutParams(new AbsListView.LayoutParams(imageWidth, imageWidth * 3 / 4));
            Glide.with(a).load(getItem(position)).override(imageWidth, imageWidth * 3 / 4).centerCrop().placeholder(R.drawable.load_nothing).into(imageView);
            view = imageView;
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ScaleImageUtil.openBigIagmeMode(a,list,position);
                }
            });
            return view;
        }
    }


}
