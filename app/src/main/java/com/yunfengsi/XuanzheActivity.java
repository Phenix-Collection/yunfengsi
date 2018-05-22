package com.yunfengsi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Selection;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.push.AndroidPopupActivity;
import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.yunfengsi.Adapter.PL_List_Adapter;
import com.yunfengsi.Audio_BD.WakeUp.Recognizelmpl.IBDRcognizeImpl;
import com.yunfengsi.Setting.Mine_gerenziliao;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.ImageUtil;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.LoginUtil;
import com.yunfengsi.Utils.MD5Utls;
import com.yunfengsi.Utils.Network;
import com.yunfengsi.Utils.ProgressUtil;
import com.yunfengsi.Utils.ShareManager;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.TimeUtils;
import com.yunfengsi.Utils.ToastUtil;
import com.yunfengsi.Utils.mApplication;
import com.yunfengsi.View.mPLlistview;
import com.yunfengsi.YunDou.YunDouAwardDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class XuanzheActivity extends AndroidPopupActivity implements View.OnClickListener, PL_List_Adapter.onHuifuListener {
    private TextView tvqian;
    private TextView tvmoney1;
    private TextView tvmoney2;
    private TextView tvmoney3;
    private Button bntdelect;
    private Button bntadd;
    private TextView bntbuy;
    private EditText edmoneycustom;
    private EditText tvnum;
    private String money;
    private TextView title;
    private boolean moneycustom;
    private ImageView image;
    int num = 1;
    private SharedPreferences sp;
    private String m1, m2, m3;
    private ImageView fenxiang;
    private SHARE_MEDIA[] share_list;
    private ShareAction action;
    private static final String TAG = "Shopd";
    private boolean isTrue = false;
    private EditText beizhu;

    private mPLlistview PlListVIew;
    private EditText PLText;
    private TextView fasong;
    private PL_List_Adapter adapter;
    private String page = "1";
    private String endPage = "";
    private ArrayList<HashMap<String, String>> Pllist;
    private InputMethodManager imm;
    private String id;
    private HashMap<String, String> map;
    //无评论时的header
    private TextView tv;
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

    private TextView abs;
    private ImageView tip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.activity_xuanzhe);
        id = getIntent().getStringExtra("id");
        abs = (TextView) findViewById(R.id.xuanzhe_abs);


        tip = (ImageView) findViewById(R.id.tip);
        tip.setOnClickListener(this);

        PLText = (EditText) findViewById(R.id.zixun_detail_apply_edt);
        PLText.setHint(mApplication.ST("您的留言(300字以内)"));
        Glide.with(this).load(R.drawable.pinglun).skipMemoryCache(true).override(DimenUtils.dip2px(this,25),DimenUtils.dip2px(this,25))
                .into((ImageView) findViewById(R.id.pinglun_image));
        Glide.with(this).load(R.drawable.fenxiangb).skipMemoryCache(true).override(DimenUtils.dip2px(this,25),DimenUtils.dip2px(this,25))
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

                        if(ibdRcognize==null){
                            ibdRcognize=new IBDRcognizeImpl(XuanzheActivity.this);
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


            }
        });
        fenxiangb = (LinearLayout) findViewById(R.id.fenxiangb);
        fenxiangb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (umWeb != null) {
                    new ShareManager().shareWeb(umWeb, XuanzheActivity.this);
                }
            }
        });
        ((TextView) findViewById(R.id.titletv)).setText(mApplication.ST("功德无量"));
        ((TextView) findViewById(R.id.numtv)).setText(mApplication.ST("数量"));
        ((TextView) findViewById(R.id.pltv)).setText(mApplication.ST("评论"));
        ((TextView) findViewById(R.id.fxtv)).setText(mApplication.ST("分享"));

        fenxiang = (ImageView) findViewById(R.id.zixun_detail_fenxiang2);
        Pllist = new ArrayList<>();
        adapter = new PL_List_Adapter(this);
        adapter.setOnHuifuListener(this);

        fasong = (TextView) findViewById(R.id.zixun_detail_fasong);
        fasong.setText(mApplication.ST("发送"));
        fasong.setOnClickListener(this);
        PlListVIew = (mPLlistview) findViewById(R.id.zixun_Detail_PL_listview);
        PlListVIew.footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlListVIew.footer.setText(mApplication.ST("正在加载"));
                if (!endPage.equals(page)) page = String.valueOf(Integer.valueOf(page) + 1);
                getPLandSet(map);
            }
        });
        imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);


        beizhu = (EditText) findViewById(R.id.beizhu);
        beizhu.setHint(mApplication.ST("请输入申请人和祈福内容（限50字,选填）"));
        sp = getSharedPreferences("user", MODE_PRIVATE);
        title = (TextView) findViewById(R.id.xuanzhe_name);
        title.setText(getIntent().getStringExtra("title"));
        edmoneycustom = (EditText) findViewById(R.id.money_custom);
        edmoneycustom.setHint(mApplication.ST("随喜捐赠(元)"));
        edmoneycustom.setOnClickListener(this);
        image = (ImageView) findViewById(R.id.xuanzhe_imageview);
        if (getIntent().getStringExtra("head") == null) {
            Glide.with(this).load(getIntent().getStringExtra("head_url")).centerCrop().into(image);
        } else {
            LogUtil.w("onCreate: 数组");
            byte[] b = getIntent().getByteArrayExtra("head");
            image.setImageBitmap(BitmapFactory.decodeByteArray(b, 0, b.length));
        }
        tvnum = (EditText) findViewById(R.id.tv_fact_count);
        tvnum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if ("".equals(s.toString()) || "0".equals(s.toString())) {
                    tvnum.setText("1");
                    num = 1;
                    Toast.makeText(XuanzheActivity.this, mApplication.ST("数量不能为零"), Toast.LENGTH_SHORT).show();
                    Selection.setSelection(tvnum.getText(), 1);
                    return;
                }
                num = Integer.valueOf(s.toString());
                LogUtil.e("商品数量：" + num + "     ~~~~::" + s.toString());
            }
        });
        tvmoney1 = (TextView) findViewById(R.id.money1);
        tvmoney2 = (TextView) findViewById(R.id.money2);
        tvmoney3 = (TextView) findViewById(R.id.money3);
        tvqian = (TextView) findViewById(R.id.xuanzhe_qian);
        tvqian.setText(getIntent().getStringExtra("money"));
        bntadd = (Button) findViewById(R.id.btn_count_plus);
        bntdelect = (Button) findViewById(R.id.btn_count_minus);
        bntbuy = (TextView) findViewById(R.id.xuanzhe_buy);
        bntbuy.setText(mApplication.ST("供养"));
        edmoneycustom.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    tvmoney3.setBackgroundResource(R.drawable.moneyshape);
                    tvmoney2.setBackgroundResource(R.drawable.moneyshape);
                    tvmoney1.setBackgroundResource(R.drawable.moneyshape);
                    edmoneycustom.setBackgroundResource(R.drawable.moneyxuanzhe);
                    moneycustom = true;
                } else {
                    Log.d("失去焦点", "222");
                }
            }
        });
        //分享
        share_list = new SHARE_MEDIA[]{
                SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE,
        };
        getData();


    }

    private void getPLandSet(final HashMap<String, String> map) {//加载评论并设置
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject js = new JSONObject();
                    try {
                        js.put("m_id", Constants.M_id);
                        js.put("shop_id", id);
                        js.put("page", page);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String data = OkGo.post(Constants.ShangPin_Detail_PL_Ip).tag(TAG)
                            .params("key", ApisSeUtil.getKey())
                            .params("msg", ApisSeUtil.getMsg(js)).execute().body().string();
                    if (!data.equals("")) {
                        LogUtil.w("run:      PLdata------>" + data);
                        Pllist = AnalyticalJSON.getList(data, "comment");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if ((Pllist != null)) {
                                    PlListVIew.setFocusable(false);
                                    if (adapter.mlist.size() == 0 && Pllist.size() == 0) {//没有评论的时候
                                        tv = new TextView(XuanzheActivity.this);
                                        tv.setText(mApplication.ST("还没有留言，快来留言吧"));
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
                                        if (Pllist.size() < 10) {
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
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {

                }
            }
        }).start();

    }

    private boolean checkNum(String t) {// TODO: 2016/12/17 检测文字正确性

        if (t.startsWith("0")) {
            if (t.lastIndexOf(".") != -1) {
                if (!t.endsWith(".")) {
                    isTrue = true;
                }
            }
        } else {
            if (t.lastIndexOf(".") != -1) {
                if (!t.startsWith(".") && !t.endsWith(".")) {
                    isTrue = true;
                }
            } else {
                isTrue = true;
            }
        }
        return isTrue;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    private void getData() {
        if (!Network.HttpTest(this)) {
            tip.setImageBitmap(ImageUtil.readBitMap(this,R.drawable.load_neterror));
            tip.setVisibility(View.VISIBLE);
            return;
        }
        if(id==null||id.equals("")){
            return;
        }
        ProgressUtil.show(this,"","正在加载");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject js = new JSONObject();
                    try {
                        js.put("m_id", Constants.M_id);
                        js.put("id", getIntent().getStringExtra("id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String data = OkGo.post(Constants.ShangPin_Detail_Ip)
                            .params("key", ApisSeUtil.getKey())
                            .params("msg", ApisSeUtil.getMsg(js)).execute().body().string();
                    LogUtil.e(data);
                    if (!TextUtils.isEmpty(data) && !"null".equals(data)) {
                        map = AnalyticalJSON.getHashMap(data);
                        if (map != null && map.get("code") == null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tip.setVisibility(View.GONE);
                                    findViewById(R.id.scroll).setVisibility(View.VISIBLE);
                                    findViewById(R.id.headLayout).setFocusable(true);
                                    findViewById(R.id.headLayout).setFocusableInTouchMode(true);
                                    findViewById(R.id.headLayout).requestFocus();
                                    fenxiang.setOnClickListener(XuanzheActivity.this);
                                    bntbuy.setOnClickListener(XuanzheActivity.this);
                                    abs.setText(mApplication.ST("".equals(map.get("abstract"))?"暂无简介":map.get("abstract")));
                                    ((TextView) findViewById(R.id.pinglun1)).setText(mApplication.ST("评论 "+map.get("shop_comment")));
                                    String md5 = MD5Utls.stringToMD5(Constants.safeKey);
                                    String m11 = md5.substring(0, 16);
                                    String m21 = md5.substring(16, md5.length());
                                    umWeb = new UMWeb(Constants.FX_host_Ip + TAG + "/id/" + m11 + getIntent().getStringExtra("id") + m21 + "/st/" + (mApplication.isChina ? "s" : "t"));
                                    umWeb.setTitle(mApplication.ST(map.get("product")));
                                    umWeb.setDescription(mApplication.ST(map.get("type1") + ":" + map.get("money1") + "元"));
                                    umWeb.setThumb(new UMImage(XuanzheActivity.this, map.get("image")));

                                    Glide.with(XuanzheActivity.this).load(map.get("image")).into(image);
                                    title.setText(mApplication.ST(map.get("product")));
                                    tvqian.setText(mApplication.ST("￥" + map.get("money1")));
                                    if (!"".equals(map.get("type1")) && "".equals(map.get("type3"))) {
                                        tvmoney1.setText(mApplication.ST(map.get("type1") + " " + map.get("money1") + "元"));
                                        m1 = map.get("money1");
                                        tvmoney1.setVisibility(View.VISIBLE);
                                    }
                                    if (!"".equals(map.get("type2")) && "".equals(map.get("type3"))) {
                                        tvmoney1.setText(mApplication.ST(map.get("type1") + " " + map.get("money1") + "元"));
                                        tvmoney2.setText(mApplication.ST(map.get("type2") + " " + map.get("money2") + "元"));
                                        m1 = map.get("money1");
                                        m2 = map.get("money2");
                                        tvmoney1.setVisibility(View.VISIBLE);
                                        tvmoney2.setVisibility(View.VISIBLE);
                                    }
                                    if (!"".equals(map.get("type3"))) {
                                        tvmoney1.setText(mApplication.ST(map.get("type1") + " " + map.get("money1") + "元"));
                                        tvmoney2.setText(mApplication.ST(map.get("type2") + " " + map.get("money2") + "元"));
                                        tvmoney3.setText(mApplication.ST(map.get("type3") + " " + map.get("money3") + "元"));
                                        m1 = map.get("money1");
                                        m2 = map.get("money2");
                                        m3 = map.get("money3");
                                        tvmoney1.setVisibility(View.VISIBLE);
                                        tvmoney2.setVisibility(View.VISIBLE);
                                        tvmoney3.setVisibility(View.VISIBLE);
                                    }
                                    getPLandSet(map);
                                }

                            });
                        }else{
                            tip.setImageBitmap(ImageUtil.readBitMap(mApplication.getInstance(), R.drawable.load_nothing));
                            tip.setVisibility(View.VISIBLE);
                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tip.setImageBitmap(ImageUtil.readBitMap(mApplication.getInstance(), R.drawable.load_nothing));
                                tip.setVisibility(View.VISIBLE);
                                ProgressUtil.dismiss();
                                finish();
                            }
                        });
                    }
                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tip.setImageBitmap(ImageUtil.readBitMap(mApplication.getInstance(), R.drawable.load_nothing));
                            tip.setVisibility(View.VISIBLE);
                            ProgressUtil.dismiss();
                            Toast.makeText(XuanzheActivity.this, mApplication.ST("网络连接超时"), Toast.LENGTH_SHORT).show();
                        }
                    });
                }finally {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UMShareAPI.get(this).release();
//        ShareManager.release();
    }

    @Override
    public void onClick(final View view) {

        switch (view.getId()) {

            case R.id.tip:
                getData();
                break;
            case R.id.zixun_detail_fasong:
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
                view.setEnabled(false);
                ProgressUtil.show(this, "", mApplication.ST("正在提交"));
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
                                    js.put("shop_id", id);
                                    js.put("m_id", Constants.M_id);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                final String data = OkGo.post(Constants.ShangPin_Detail_PL_COmmit_Ip).params("key", ApisSeUtil.getKey())
                                        .params("msg", ApisSeUtil.getMsg(js))
                                        .execute().body().string();
                                if (data != null & !data.equals("")) {
                                    Log.i(TAG, "run:      data------>" + data);
                                    final HashMap<String, String> hashMap = AnalyticalJSON.getHashMap(data);
                                    if (hashMap != null && "000".equals(hashMap.get("code"))) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if(!"0".equals(hashMap.get("yundousum"))){
                                                    YunDouAwardDialog.show(XuanzheActivity.this,"每日评论",hashMap.get("yundousum"));
                                                }else{
                                                    ToastUtil.showToastShort(mApplication.ST("添加评论成功"));
                                                }
                                                final HashMap<String, String> map = new HashMap<>();
                                                String headurl = sp.getString("head_path", "").equals("") ? sp.getString("head_url", "") : sp.getString("head_path", "");
                                                final String time = TimeUtils.getStrTime(System.currentTimeMillis() + "");
                                                String petname = sp.getString("pet_name", "");
                                                String diazannum = "0";
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
                                                view.setEnabled(true);
                                                PLText.setText("");
                                                imm.hideSoftInputFromWindow(PLText.getWindowToken(), 0);
                                                int firstnum=Integer.valueOf(XuanzheActivity.this.map.get("shop_comment"));
                                                ((TextView) findViewById(R.id.pinglun1)).setText(mApplication.ST("评论 "+(firstnum+1)));
                                                overlay.setVisibility(View.GONE);
                                                ProgressUtil.dismiss();
                                            }
                                        });
                                    }
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            view.setEnabled(true);
                                            Toast.makeText(XuanzheActivity.this, mApplication.ST("上传评论失败，请重新尝试"), Toast.LENGTH_SHORT).show();
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
                                        .params("key", ApisSeUtil.getKey()).params("msg", ApisSeUtil.getMsg(js)).execute().body().string();
                                if (!data.equals("")) {
                                    final HashMap<String, String> hashMap = AnalyticalJSON.getHashMap(data);
                                    if (hashMap != null && "000".equals(hashMap.get("code"))) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (currentLayout.getVisibility() == View.GONE) {
                                                    currentLayout.setVisibility(View.VISIBLE);
                                                }
                                                if(!"0".equals(hashMap.get("yundousum"))){
                                                    YunDouAwardDialog.show(XuanzheActivity.this,"每日评论",hashMap.get("yundousum"));
                                                }
                                                TextView textView = new TextView(XuanzheActivity.this);
                                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                                layoutParams.setMargins(0, DimenUtils.dip2px(XuanzheActivity.this, 5), 0, DimenUtils.dip2px(XuanzheActivity.this, 5));
                                                textView.setLayoutParams(layoutParams);
                                                String pet_name = sp.getString("pet_name", "");
                                                SpannableStringBuilder ssb = new SpannableStringBuilder(mApplication.ST(pet_name + ":" + content));
                                                ssb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(XuanzheActivity.this, R.color.main_color)), 0, pet_name.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                                                textView.setText(ssb);
                                                currentLayout.addView(textView);
                                                PLText.setText("");
                                                PLText.setHint(mApplication.ST("您的留言（300字以内）"));
                                                imm.hideSoftInputFromWindow(PLText.getWindowToken(), 0);
                                                overlay.setVisibility(View.GONE);
                                                PlListVIew.setSelection(currentPosition);
                                                fasong.setEnabled(true);
                                                isPLing = false;
                                                try {
                                                    JSONArray jsonArray = new JSONArray(adapter.mlist.get(currentPosition).get("reply"));
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
                                            view.setEnabled(true);
                                            Toast.makeText(XuanzheActivity.this, mApplication.ST("回复提交失败，请重新尝试"), Toast.LENGTH_SHORT).show();
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
            case R.id.zixun_detail_fenxiang2:
                imm.hideSoftInputFromWindow(PLText.getWindowToken(), 0);
                fenxiangb.performClick();
                break;
            case R.id.btn_count_minus:   //减
                if (num == 1) {
                    bntdelect.setEnabled(false);
                } else {
                    num = num - 1;

                    tvnum.setText(String.valueOf(num));
                }
                break;
            case R.id.btn_count_plus:
                //加
                num = num + 1;
                tvnum.setText(String.valueOf(num));
                if (num > 1) {
                    bntdelect.setEnabled(true);
                }
                break;
            case R.id.money_custom:
                edmoneycustom.setFocusable(true);//设置输入框可聚集
                edmoneycustom.setFocusableInTouchMode(true);//设置触摸聚焦
                edmoneycustom.requestFocus();//请求焦点
                edmoneycustom.findFocus();//获取焦点

                tvmoney3.setBackgroundResource(R.drawable.moneyshape);
                tvmoney2.setBackgroundResource(R.drawable.moneyshape);
                tvmoney1.setBackgroundResource(R.drawable.moneyshape);
                edmoneycustom.setBackgroundResource(R.drawable.moneyxuanzhe);
                moneycustom = true;
                break;
            case R.id.money1:
                edmoneycustom.setFocusable(false);
                moneycustom = false;
                edmoneycustom.setText("");
                edmoneycustom.setHint("随喜捐赠(元)");
                edmoneycustom.setBackgroundResource(R.drawable.moneyshape);
                tvmoney2.setBackgroundResource(R.drawable.moneyshape);
                tvmoney3.setBackgroundResource(R.drawable.moneyshape);
                tvmoney1.setBackgroundResource(R.drawable.moneyxuanzhe);
                money = m1;
                tvqian.setText("￥" + money);
                break;
            case R.id.money2:
                edmoneycustom.setFocusable(false);
                moneycustom = false;
                edmoneycustom.setText("");
                edmoneycustom.setHint(mApplication.ST("随喜捐赠(元)"));
                edmoneycustom.setBackgroundResource(R.drawable.moneyshape);
                tvmoney1.setBackgroundResource(R.drawable.moneyshape);
                tvmoney3.setBackgroundResource(R.drawable.moneyshape);
                tvmoney2.setBackgroundResource(R.drawable.moneyxuanzhe);
                money = m2;
                tvqian.setText("￥" + money);
                break;
            case R.id.money3:
                edmoneycustom.setFocusable(false);
                moneycustom = false;
                edmoneycustom.setText("");
                edmoneycustom.setHint(mApplication.ST("随喜捐赠(元)"));
                tvmoney2.setBackgroundResource(R.drawable.moneyshape);
                tvmoney1.setBackgroundResource(R.drawable.moneyshape);
                tvmoney3.setBackgroundResource(R.drawable.moneyxuanzhe);
                edmoneycustom.setBackgroundResource(R.drawable.moneyshape);
                money = m3;
                tvqian.setText("￥" + money);
                break;
            case R.id.xuanzhe_buy:
//                if(true){
//                    startUpPay();
//                    return;
//                }
                if (!new LoginUtil().checkLogin(this)) {
                    return;
                }
//                if (sp.getString("pet_name", "").trim().equals("")) {
//                    Toast.makeText(this, mApplication.ST("请完善信息"), Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(this, Mine_gerenziliao.class);
//                    startActivity(intent);
//                    return;
//                }
                if (moneycustom) {
                    money = edmoneycustom.getText().toString().trim();
                    if (!checkNum(money)) {
                        Toast.makeText(XuanzheActivity.this, mApplication.ST("请输入正确的金额"), Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        isTrue = false;
                    }
                }
                if (TextUtils.isEmpty(money)) {
                    Toast.makeText(XuanzheActivity.this, mApplication.ST("请选中金额"), Toast.LENGTH_SHORT).show();
                    return;
                }
                String allmoney = "";
                if (moneycustom) {
                    allmoney = String.format("%.2f", Double.valueOf(money));
                    num = 1;
                } else {
                    allmoney = String.format("%.2f", Double.valueOf(money) * Integer.parseInt(tvnum.getText().toString()));
                }
                Log.w(TAG, "onClick: 总金额：" + allmoney);
                mApplication.openPayLayout(this, allmoney, getIntent().getStringExtra("id"), map.get("product"), String.valueOf(num), "4", beizhu.getText().toString().trim());

                break;
            case R.id.xuanzhe_back:
                finish();
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ProgressUtil.dismiss();
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
    protected void onSysNoticeOpened(String s, String s1, Map<String, String> map) {
        id=AnalyticalJSON.getHashMap(map.get("msg")).get("id");
        getData();
    }
}
