package com.yunfengsi.Models.WallPaper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.lzy.okgo.OkGo;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.yunfengsi.Adapter.PL_List_Adapter;
import com.yunfengsi.Audio_BD.WakeUp.Recognizelmpl.IBDRcognizeImpl;
import com.yunfengsi.Models.YunDou.YunDouAwardDialog;
import com.yunfengsi.R;
import com.yunfengsi.Setting.Mine_gerenziliao;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.LoginUtil;
import com.yunfengsi.Utils.Network;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.ProgressUtil;
import com.yunfengsi.Utils.ShareManager;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.TimeUtils;
import com.yunfengsi.Utils.ToastUtil;
import com.yunfengsi.Utils.mApplication;
import com.yunfengsi.View.LoadMoreListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * 作者：因陀罗网 on 2018/6/5 14:06
 * 公司：成都因陀罗网络科技有限公司
 */
public class WallPaperComments extends AppCompatActivity implements PL_List_Adapter.onHuifuListener, LoadMoreListView.OnLoadMore, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private String id;
    private String page    = "1";
    private String endPage = "";

    private TextView         fasong;
    private LoadMoreListView PlListVIew;
    private EditText         PLText;
    private ImageView        dianzanImg;
    private TextView         dianzanText;
    private int              screenWidth;

    private PL_List_Adapter adapter;

    private UMWeb             umWeb;
    private SharedPreferences sp;

    private InputMethodManager imm;
    private LinearLayout       currentLayout;
    private int                currentPosition;
    private String             currentId;
    private boolean isPLing = false;

    private LinearLayout pinglun, fenxiangb;
    private FrameLayout     overlay;
    private ImageView       toggle;
    private TextView        audio;
    private IBDRcognizeImpl ibdRcognize;

    private ArrayList<HashMap<String, String>> Pllist;

    //无评论时的header
    private TextView tv;

    private SwipeRefreshLayout swip;

    private boolean isRefresh;


    private TextView    t;//加载数据的底部提示
    private ProgressBar p;//加载数据的底部进度

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.wallpaper_comment);
        id = getIntent().getStringExtra("id");


        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        swip = findViewById(R.id.swip);
        swip.setOnRefreshListener(this);
        swip.setColorSchemeResources(R.color.main_color);

        PLText = (EditText) findViewById(R.id.zixun_detail_apply_edt);
        PLText.setHint(mApplication.ST("写入你的评论(300字以内)"));
        Glide.with(this).load(R.drawable.pinglun).skipMemoryCache(true).override(DimenUtils.dip2px(this, 25), DimenUtils.dip2px(this, 25))
                .into((ImageView) findViewById(R.id.pinglun_image));
        Glide.with(this).load(R.drawable.fenxiangb).skipMemoryCache(true).override(DimenUtils.dip2px(this, 25), DimenUtils.dip2px(this, 25))
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
                            ibdRcognize = new IBDRcognizeImpl(WallPaperComments.this);
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
//                        imm. showSoftInput(PLText,2);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

            }
        });
        fenxiangb = (LinearLayout) findViewById(R.id.fenxiangb);
        fenxiangb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Network.HttpTest(WallPaperComments.this)) {
                    Glide.with(WallPaperComments.this)
                            .load(getIntent().getStringExtra("url"))
                            .asBitmap().into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            UMWeb umWeb = new UMWeb("http://a.app.qq.com/o/simple.jsp?pkgname=com.yunfengsi");
                            umWeb.setThumb(new UMImage(WallPaperComments.this, resource));
                            umWeb.setTitle("云峰寺全新壁纸功能上线啦");
                            umWeb.setDescription("我在云峰寺壁纸功能里发现一张超赞的佛系美图，快点我下载吧~");
                            new ShareManager().shareWeb(umWeb, WallPaperComments.this);
                        }
                    });
                }
            }
        });

        ((TextView) findViewById(R.id.pltv)).setText(mApplication.ST("评论"));
        ((TextView) findViewById(R.id.fxtv)).setText(mApplication.ST("分享"));

        fasong = (TextView) findViewById(R.id.zixun_detail_fasong);
        fasong.setOnClickListener(this);
        fasong.setText(mApplication.ST("发送"));

        sp = PreferenceUtil.getUserIncetance(this);
        Pllist = new ArrayList<>();


        adapter = new PL_List_Adapter(this);
        adapter.setOnHuifuListener(this);

        PlListVIew = (LoadMoreListView) findViewById(R.id.listview);
        PlListVIew.setLoadMoreListen(this);

        imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);

        t = (TextView) (PlListVIew.footer.findViewById(R.id.load_more_text));
        p = (ProgressBar) (PlListVIew.footer.findViewById(R.id.load_more_bar));

        swip.post(new Runnable() {
            @Override
            public void run() {
                swip.setRefreshing(true);
                onRefresh();
            }
        });

    }

    @Override
    public void onHuifuClicked(String id, int p, View v, String name) {
        // TODO: 2016/12/27 评论回复接口
        overlay.setVisibility(View.VISIBLE);
        isPLing = true;
        currentLayout = (LinearLayout) v;
        currentPosition = p;
        currentId = id;
        SpannableString ss = new SpannableString(mApplication.ST("回复 ") + name + " :");
        ss.setSpan(new ForegroundColorSpan(Color.BLACK), 3, name.length() + 3, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        PLText.setHint(ss);
        PLText.requestFocus();


        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void loadMore() {
        if (!endPage.equals(page)) {
            page = String.valueOf(Integer.parseInt(page) + 1);
        } else {
            p.setVisibility(View.GONE);
            t.setText("没有更多数据了");
            return;
        }
        getPLandSet();
    }

    private void getPLandSet() {//加载评论并设置
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject js = new JSONObject();
                    try {
                        js.put("m_id", Constants.M_id);
                        js.put("page", page);
                        js.put("wallpaper_id", id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m = ApisSeUtil.i(js);
                    LogUtil.e("加载壁纸评论"+js);
                    String data = OkGo.post(Constants.WallPaperCommentList)
                            .tag(this)
                            .params("key", m.K())
                            .params("msg", m.M()).execute().body().string();
                    if(data==null||data.equals("")||data.equals("null")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(isRefresh&&PlListVIew.getHeaderViewsCount()==0){
                                    tv = new TextView(WallPaperComments.this);
                                    AbsListView.LayoutParams vl =new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
                                    tv.setLayoutParams(vl);
                                    tv.setPadding(0,DimenUtils.dip2px(WallPaperComments.this,10),0,DimenUtils.dip2px(WallPaperComments.this,10));
                                    tv.setText(mApplication.ST("还没有评论,快来评论吧"));
                                    PlListVIew.addHeaderView(tv);
                                    PlListVIew.footer.setVisibility(View.GONE);
                                    isRefresh=false;
                                    if(adapter!=null){
                                        adapter.mlist.clear();
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        });
                        return;
                    }
                    if (!data.equals("")) {
                        Pllist = AnalyticalJSON.getList(data, "comment");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if ((Pllist != null)) {
                                    if (adapter.mlist.size() == 0 && Pllist.size() == 0) {//没有评论的时候
                                        tv = new TextView(WallPaperComments.this);
                                        tv.setText(mApplication.ST("还没有评论,快来评论吧"));
                                        AbsListView.LayoutParams vl=new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
                                        tv.setLayoutParams(vl);
                                        tv.setPadding(0,DimenUtils.dip2px(WallPaperComments.this,10),0,DimenUtils.dip2px(WallPaperComments.this,10));
                                        if(PlListVIew.getHeaderViewsCount()==0){
                                            PlListVIew.addHeaderView(tv);
                                        }
                                        PlListVIew.footer.setVisibility(View.GONE);
                                        PlListVIew.setAdapter(adapter);

                                        return;
                                    }
                                    PlListVIew.removeHeaderView(tv);
                                    if (isRefresh) {
                                        isRefresh=false;
                                        adapter.addList(Pllist);
                                        PlListVIew.setAdapter(adapter);
                                        if (swip.isRefreshing()) swip.setRefreshing(false);
                                        if (t.getText().toString().equals("没有更多数据了")) {
                                            PlListVIew.onLoadComplete();
                                            t.setText("正在加载....");
                                            p.setVisibility(View.VISIBLE);
                                        }
                                    } else {
                                        adapter.mlist.addAll(Pllist);
                                        boolean flag = false;
                                        for (int i = 0; i < Pllist.size(); i++) {
                                            adapter.flagList.add(flag);
                                        }
                                        adapter.notifyDataSetChanged();
                                        if (endPage.equals(page)) {
                                            t.setText("没有更多数据了");
                                            p.setVisibility(View.GONE);
                                            return;
                                        } else {
                                            t.setText("正在加载....");
                                        }
                                    }
                                    PlListVIew.onLoadComplete();
                                    if (Pllist.size() < 10) {
                                        endPage = page;
                                    }
                                }else{
                                    if(isRefresh){
                                        tv = new TextView(WallPaperComments.this);
                                        tv.setText(mApplication.ST("还没有评论,快来评论吧"));
                                        PlListVIew.addHeaderView(tv);
                                        PlListVIew.footer.setVisibility(View.GONE);
                                        isRefresh=false;
                                    }
                                }


                            }
                        });

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (WallPaperComments.this != null) {
                        runOnUiThread(new Runnable() {
                                          @Override
                                          public void run() {
                                              swip.setRefreshing(false);
                                          }
                                      }
                        );

                    }

                }
            }
        }).start();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.zixun_detail_fenxiang2://底部分享
                imm.hideSoftInputFromWindow(PLText.getWindowToken(), 0);
                fenxiangb.performClick();
                break;
            case R.id.zixun_detail_fasong://发送提交评论
                if (!new LoginUtil().checkLogin(this)) {
                    return;
                }
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
                                JSONObject   js      = new JSONObject();
                                try {
                                    js.put("user_id", sp.getString("user_id", ""));
                                    js.put("ct_contents", content);
                                    js.put("wallpaper_id", id);
                                    js.put("m_id", Constants.M_id);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                ApisSeUtil.M m = ApisSeUtil.i(js);
                                final String data = OkGo.post(Constants.WallPaperCommentUpload)
                                        .tag(this)
                                        .params("key", m.K())
                                        .params("msg", m.M()).execute().body().string();
                                if (data != null & !data.equals("")) {

                                    final HashMap<String, String> hashMap = AnalyticalJSON.getHashMap(data);
                                    if (hashMap != null && "000".equals(hashMap.get("code"))) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (!"0".equals(hashMap.get("yundousum"))) {
                                                    YunDouAwardDialog.show(WallPaperComments.this, "每日评论", hashMap.get("yundousum"));
                                                }
                                                ToastUtil.showToastShort(mApplication.ST(getString(R.string.commitCommentSuccess)));
                                                final HashMap<String, String> map       = new HashMap<>();
                                                String                        headurl   = sp.getString("head_path", "").equals("") ? sp.getString("head_url", "") : sp.getString("head_path", "");
                                                final String                  time      = TimeUtils.getStrTime(System.currentTimeMillis() + "");
                                                String                        petname   = sp.getString("pet_name", "");
                                                String                        diazannum = "0";
                                                map.put("user_image", headurl);
                                                map.put("ct_contents", content);
                                                map.put("pet_name", petname);
                                                map.put("ct_ctr", diazannum);
                                                map.put("level", sp.getString("level", "0"));
                                                map.put("ct_time", time);
                                                if (sp.getString("role", "").equals("3")) {
                                                    map.put("role", "3");
                                                } else {
                                                    map.put("role", "0");
                                                }

                                                map.put("id", hashMap.get("id"));
                                                map.put("reply", new JSONArray().toString());
                                                map.put("level", sp.getString("level", "0"));
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
//                                                firstNum += 1;
//                                                plNum.setText(mApplication.ST("评论 " + firstNum));
                                                PLText.setText("");
                                                imm.hideSoftInputFromWindow(PLText.getWindowToken(), 0);
                                                overlay.setVisibility(View.GONE);

                                                ProgressUtil.dismiss();
                                            }
                                        });
                                    }
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            v.setEnabled(true);
                                            Toast.makeText(WallPaperComments.this, mApplication.ST("上传评论失败，请重新尝试"), Toast.LENGTH_SHORT).show();
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
                                JSONObject   js      = new JSONObject();
                                try {
                                    js.put("user_id", sp.getString("user_id", ""));
                                    js.put("ct_contents", content);
                                    js.put("ct_id", currentId);
                                    js.put("m_id", Constants.M_id);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                final String data = OkGo.post(Constants.little_zixun_pl_add_IP)
                                        .tag(this)
                                        .params("key", ApisSeUtil.getKey())
                                        .params("msg", ApisSeUtil.getMsg(js)).execute().body().string();
                                if (!data.equals("")) {
                                    final HashMap<String, String> hashMap = AnalyticalJSON.getHashMap(data);
                                    if (hashMap != null && "000".equals(hashMap.get("code"))) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (!"0".equals(hashMap.get("yundousum"))) {
                                                    YunDouAwardDialog.show(WallPaperComments.this, "每日评论", hashMap.get("yundousum"));
                                                }
                                                if (currentLayout.getVisibility() == View.GONE) {
                                                    currentLayout.setVisibility(View.VISIBLE);
                                                }
                                                ToastUtil.showToastShort(mApplication.ST(getString(R.string.commitCommentSuccess)));
                                                TextView                  textView     = new TextView(WallPaperComments.this);
                                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                                layoutParams.setMargins(0, DimenUtils.dip2px(WallPaperComments.this, 5), 0, DimenUtils.dip2px(WallPaperComments.this, 5));
                                                textView.setLayoutParams(layoutParams);
                                                String                 pet_name = sp.getString("pet_name", "");
                                                SpannableStringBuilder ssb      = new SpannableStringBuilder(pet_name + ":" + content);
                                                ssb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(WallPaperComments.this, R.color.main_color)), 0, pet_name.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                                                textView.setText(ssb);
                                                currentLayout.addView(textView);
                                                PLText.setText("");
                                                PLText.setHint(mApplication.ST("写入您的评论（300字以内）"));
                                                overlay.setVisibility(View.GONE);
                                                imm.hideSoftInputFromWindow(PLText.getWindowToken(), 0);
                                                PlListVIew.setSelection(currentPosition);
                                                fasong.setEnabled(true);
                                                isPLing = false;
                                                try {
                                                    JSONArray  jsonArray  = new JSONArray(adapter.mlist.get(currentPosition).get("reply"));
                                                    JSONObject jsonObject = new JSONObject();
                                                    jsonObject.put("id", hashMap.get("id"));
                                                    jsonObject.put("pet_name", mApplication.ST(pet_name));
                                                    if (sp.getString("role", "").equals("3")) {
                                                        jsonObject.put("role", "3");
                                                    } else {
                                                        jsonObject.put("role", "0");
                                                    }
                                                    jsonObject.put("ct_contents", mApplication.ST(content));
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
                                            Toast.makeText(WallPaperComments.this, mApplication.ST("回复提交失败，请重新尝试"), Toast.LENGTH_SHORT).show();
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
    protected void onDestroy() {
        super.onDestroy();
        if(ibdRcognize!=null){
            ibdRcognize.release();
        }
        OkGo.getInstance().cancelTag(this);
        UMShareAPI.get(this).release();
    }

    @Override
    public void onRefresh() {
        page = "1";
        isRefresh = true;
        endPage = "";
        getPLandSet();
    }
}
