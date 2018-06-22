package com.yunfengsi.Setting;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.speech.EventListener;
import com.baidu.speech.asr.SpeechConstant;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.lzy.okgo.OkGo;
import com.yunfengsi.Audio_BD.WakeUp.Recognizelmpl.IBDRcognizeImpl;
import com.yunfengsi.Models.GongYangDetail;
import com.yunfengsi.Models.Model_activity.ActivityDetail;
import com.yunfengsi.Models.Model_zhongchou.FundingDetailActivity;
import com.yunfengsi.Models.ZiXun_Detail;
import com.yunfengsi.R;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.Network;
import com.yunfengsi.Utils.NumUtils;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.TimeUtils;
import com.yunfengsi.Utils.ToastUtil;
import com.yunfengsi.Utils.mApplication;
import com.yunfengsi.View.DiffuseView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/10/10.
 */
public class Search extends AppCompatActivity implements View.OnClickListener {
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_back://返回
                finish();
                break;
            case R.id.search_sousuo://搜索
                getData();
                break;
            case R.id.search_zixun://图文分类
                resetStatus();
                zixun.setSelected(true);
                zixun.setTextColor(ContextCompat.getColor(this, R.color.main_color));
                type = "news";
                if (!"".equals(input.getText().toString())) {
                    getData();
                }
                break;
            case R.id.search_huodong://活动分类
                resetStatus();
                huodong.setSelected(true);
                huodong.setTextColor(ContextCompat.getColor(this, R.color.main_color));
                type = "activity";
                if (!"".equals(input.getText().toString())) {
                    getData();
                }
                break;
            case R.id.search_gongyang://供养分类
                resetStatus();
                gongyang.setSelected(true);
                gongyang.setTextColor(ContextCompat.getColor(this, R.color.main_color));
                type = "shop";
                if (!"".equals(input.getText().toString())) {
                    getData();
                }
                break;
            case R.id.search_cishan://助学分类
                resetStatus();
                zhongchou.setSelected(true);
                zhongchou.setTextColor(ContextCompat.getColor(this, R.color.main_color));
                type = "cfg";
                if (!"".equals(input.getText().toString())) {
                    getData();
                }
                break;
        }


    }

    /**
     * 搜索获取数据并保存搜索记录
     */
    private void getData() {
        if (!Network.HttpTest(Search.this)) {
            return;
        }
        if (input.getText().toString().equals("")) {
            Toast.makeText(Search.this, "请输入关键字", Toast.LENGTH_SHORT).show();
            return;
        }
        if (type.equals("news")) {
            url = Constants.News_Search_Ip;
        } else if (type.equals("activity")) {
            url = Constants.Activity_Search_Ip;
        } else if (type.equals("shop")) {
            url = Constants.GY_Search_Ip;
        } else if (type.equals("cfg")) {
            url = Constants.CFG_Search_Ip;
        }
        if (url.equals("")) {
            Toast.makeText(Search.this, "请选择分类标签", Toast.LENGTH_SHORT).show();
            return;
        }
        p.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject js = new JSONObject();
                    try {
                        js.put("m_id", Constants.M_id);
                        js.put("msg", input.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m = ApisSeUtil.i(js);
                    final String data = OkGo.post(url).params("key", m.K()).params("msg", m.M())
                            .execute().body().string();
//                    gridList.add(input.getText().toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!data.equals("")) {
                                listList = AnalyticalJSON.getList(data, type);
                                if (listList != null) {
                                    if (listList.size() == 0) {
                                        Toast.makeText(Search.this, "未搜索到相关信息", Toast.LENGTH_SHORT).show();
                                    }
                                    if (adapter.getList().size() == 0) {
                                        adapter.addList(listList);
                                        listView.setAdapter(adapter);
                                        p.setVisibility(View.GONE);
                                    } else {
                                        adapter.addList(listList);
                                        adapter.notifyDataSetChanged();
                                        p.setVisibility(View.GONE);
                                        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(input.getWindowToken(), 0);
                                    }
                                } else {
                                    Toast.makeText(Search.this, "未搜索到相关信息", Toast.LENGTH_SHORT).show();
                                    if (p != null) p.setVisibility(View.GONE);
                                }
                            } else {
                                Toast.makeText(Search.this, "未搜索到相关信息", Toast.LENGTH_SHORT).show();
                                if (p != null) p.setVisibility(View.GONE);
                            }
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private listAdapter adapter;
    private static final String TAG = "Search";
    private              String url = Constants.News_Search_Ip;//搜索的接口地址
    private ImageView back;
    private EditText  input;
    private TextView  sousuo, zixun, huodong, gongyang, zhongchou, removeAll;
    private ListView                      listView;
    private GridView                      grid;
    private List<String>                  gridList;
    private List<HashMap<String, String>> listList;
    private String                        type;//搜索类别标示
    private ProgressBar                   p;
    private DiffuseView                   diffuseView;
    private IBDRcognizeImpl               ibdRcognize;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.search);
        mApplication.getInstance().addActivity(this);
        initView();
//        loadHistroy();
    }

    /**
     * 重置类别
     */
    private void resetStatus() {
        zixun.setTextColor(Color.BLACK);
        huodong.setTextColor(Color.BLACK);
        zhongchou.setTextColor(Color.BLACK);
        gongyang.setTextColor(Color.BLACK);
        zixun.setSelected(false);
        huodong.setSelected(false);
        gongyang.setSelected(false);
        zhongchou.setSelected(false);
    }

    /**
     * 初始化数据
     */
    private void initView() {
        listList = new ArrayList<>();
        adapter = new listAdapter(this, listList);
        back = (ImageView) findViewById(R.id.search_back);
        back.setOnClickListener(this);
        sousuo = (TextView) findViewById(R.id.search_sousuo);//搜索按钮
        sousuo.setText(mApplication.ST("搜索"));
        p = (ProgressBar) findViewById(R.id.search_loading);
        input = (EditText) findViewById(R.id.search_edit);//搜索输入框
        input.setHint(mApplication.ST("请输入关键字"));
        String message = getIntent().getStringExtra("text");
        if (message != null) {
            input.setText(message);
        }
        diffuseView = (DiffuseView) findViewById(R.id.audio);
        ibdRcognize = new IBDRcognizeImpl(this);
        ibdRcognize.setEventListener(new EventListener() {
            @Override
            public void onEvent(String name, String params, byte[] data, int offset, int length) {
                switch (name) {
                    case SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL://临时结果
//                        if (params.contains("nlu_result")) {
//                            if (length > 0 && data.length > 0) {
//                                if (input != null) {
//                                    input.setText(new String(data, offset, length));
//                                    LogUtil.e("语意解析成功：" + new String(data, offset, length));
//                                }
//                                return;
//                            }
//                        }
                        if (params.contains("final_result")) {
                            try {
                                JSONObject js = new JSONObject(params);
                                if (input != null) {
                                    input.setText(js.getJSONArray("results_recognition").getString(0));
                                }

                                LogUtil.e(":识别结果：：：" + js.getJSONArray("results_recognition").getString(0));
                            } catch (JSONException e) {
                                LogUtil.e("json解析错误");
                                e.printStackTrace();
                            }
                        }

                        break;
                    case SpeechConstant.CALLBACK_EVENT_ASR_FINISH://本次识别结束
                        LogUtil.e("语音识别finish");
                        if (input != null) {
                            input.setVisibility(View.VISIBLE);
                        }

                        if (sousuo != null) {
                            sousuo.performClick();
                        }


                        break;
                    case SpeechConstant.CALLBACK_EVENT_ASR_READY://识别引擎就绪
                        LogUtil.e("语音识别Ready");
                        ToastUtil.showToastShort("请说话");
                        break;
                    case SpeechConstant.ASR_CANCEL://识别取消
                        LogUtil.e("语音识别Cancel");
                        break;

                }
            }
        });
        diffuseView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (Network.HttpTest(Search.this)) {
                            diffuseView.start();
                            ibdRcognize.start();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        diffuseView.stop();
                        ibdRcognize.stop();
                        break;

                }
                return true;
            }
        });

        zixun = (TextView) findViewById(R.id.search_zixun);
        zixun.setText(mApplication.ST("图文"));
        huodong = (TextView) findViewById(R.id.search_huodong);
        huodong.setText(mApplication.ST("活动"));
        gongyang = (TextView) findViewById(R.id.search_gongyang);
        gongyang.setText(mApplication.ST("供养"));
        zhongchou = (TextView) findViewById(R.id.search_cishan);
        zhongchou.setText(mApplication.ST("助学"));
//        removeAll= (TextView) findViewById(R.id.search_removeHistory);//清空记录
        listView = (ListView) findViewById(R.id.search_listview);//搜索结果展示listview
//        grid= (GridView) findViewById(R.id.search_grid);//搜索历史gridView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LogUtil.e("当前Url:::"+url);
                Intent intent = new Intent();

                if (url.equals(Constants.News_Search_Ip)) {
                    intent.setClass(mApplication.getInstance(), ZiXun_Detail.class);
                } else if (url.equals(Constants.Activity_Search_Ip)) {
                    intent.setClass(mApplication.getInstance(), ActivityDetail.class);
                } else if (url.equals(Constants.CFG_Search_Ip)) {
                    intent.setClass(mApplication.getInstance(), FundingDetailActivity.class);
                } else if (url.equals(Constants.GY_Search_Ip)) {
                    intent.setClass(mApplication.getInstance(), GongYangDetail.class);
                }
                intent.putExtra("id", adapter.getList().get(position).get("id"));
                startActivity(intent);
            }
        });

        sousuo.setOnClickListener(this);
        zixun.setOnClickListener(this);
        huodong.setOnClickListener(this);
        gongyang.setOnClickListener(this);
        zhongchou.setOnClickListener(this);
//        removeAll.setOnClickListener(this);
        zixun.performClick();
    }

    /**
     * 搜索历史适配器
     */
    static class gridAdapter extends BaseAdapter {
        private List<String> list1;
        private Context      context;

        public gridAdapter(Context context, List<String> list) {
            super();
            this.context = context;
            this.list1 = list;

        }

        @Override
        public int getCount() {
            return list1 == null ? 0 : list1.size();
        }

        @Override
        public Object getItem(int position) {
            return list1.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = new TextView(context);
            textView.setTextSize(14);
            textView.setMaxLines(1);
            textView.setTextColor(Color.BLACK);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setText(list1.get(position));
            return textView;
        }
    }

    /**
     * 搜索结果适配器
     */
    private class listAdapter extends BaseAdapter {
        public  List<HashMap<String, String>> list;
        private Context                       context;

        private LayoutInflater inflater;
        private int            screenWidth;
        private Drawable       ctr, like, comment;

        public listAdapter(Context context, List<HashMap<String, String>> list1) {
            super();
            this.context = context;
            this.list = list1;
            inflater = LayoutInflater.from(context);
            screenWidth = context.getResources().getDisplayMetrics().widthPixels;
            ctr = ContextCompat.getDrawable(context, R.drawable.ctr_small);
            like = ContextCompat.getDrawable(context, R.drawable.like_small);
            comment = ContextCompat.getDrawable(context, R.drawable.comment_small);
            ctr.setBounds(0, 0, DimenUtils.dip2px(context, 16), DimenUtils.dip2px(context, 16));
            like.setBounds(0, 0, DimenUtils.dip2px(context, 15), DimenUtils.dip2px(context, 15));
            comment.setBounds(0, 0, DimenUtils.dip2px(context, 14), DimenUtils.dip2px(context, 14));
        }

        public List<HashMap<String, String>> getList() {
            return list;
        }

        public void addList(List<HashMap<String, String>> list1) {
            this.list = list1;
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
        public int getViewTypeCount() {
            return 4;
        }

        @Override
        public int getItemViewType(int position) {
            if (url.equals(Constants.News_Search_Ip)) {

                return 0;

            } else if (url.equals(Constants.GY_Search_Ip)) {
                return 2;
            } else if (url.equals(Constants.Activity_Search_Ip)) {
                return 1;
            } else if (url.equals(Constants.CFG_Search_Ip)) {
                return 3;
            }
            return 0;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {

            final HashMap<String, String> map = list.get(position);
            if (getItemViewType(position) == 0) {
                ViewHolder0 holder;
                if (view == null) {
                    holder = new ViewHolder0();
                    view = LayoutInflater.from(context).inflate(R.layout.hot_two_item, parent, false);
                    holder.img = (ImageView) view.findViewById(R.id.hot_two_item_image);
                    holder.title = (TextView) view.findViewById(R.id.hot_two_item_title);
                    holder.time = (TextView) view.findViewById(R.id.hot_two_item_time);
                    holder.user = (TextView) view.findViewById(R.id.hot_two_item_Plnum);
                    holder.type = (TextView) view.findViewById(R.id.hot_two_item_type);
                    holder.ctr = (TextView) view.findViewById(R.id.hot_two_item_ctr);
                    holder.abs = (TextView) view.findViewById(R.id.hot_two_item_abs);
                    holder.comments = (TextView) view.findViewById(R.id.hot_two_item_comments);
                    holder.likes = (TextView) view.findViewById(R.id.hot_two_item_likes);
                    view.setTag(holder);
                } else {
                    holder = (ViewHolder0) view.getTag();
                }
                final ImageView imageView = holder.img;
                Glide.with(context).load(map.get("image"))
                        .asBitmap()
                        .override(DimenUtils.dip2px(context, 136), DimenUtils.dip2px(context, 102))
                        .centerCrop()
                        .into(new BitmapImageViewTarget(holder.img) {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                RoundedBitmapDrawable rbd = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                                rbd.setCornerRadius(DimenUtils.dip2px(context, 2));
                                imageView.setImageDrawable(rbd);

                            }
                        });

                if (map.get("title").contains(input.getText().toString().trim())) {
                    int             dex   = map.get("title").indexOf(input.getText().toString().trim());
                    SpannableString title = new SpannableString(mApplication.ST(map.get("title")));
                    title.setSpan(new ForegroundColorSpan(Color.RED), dex, dex + input.getText().toString().trim().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    holder.title.setText(title);
                } else {
                    holder.title.setText(mApplication.ST(map.get("title")));
                }


                String time = map.get("time");
                holder.time.setText(mApplication.ST(TimeUtils.getTrueTimeStr(time)));
                holder.user.setText(mApplication.ST(map.get("issuer")));
                holder.type.setText(mApplication.ST("图文"));
                holder.ctr.setText(mApplication.ST(NumUtils.getNumStr(map.get("ctr"))));
                holder.ctr.setCompoundDrawables(ctr, null, null, null);
                holder.likes.setText(NumUtils.getNumStr(map.get("likes")));
                holder.likes.setCompoundDrawables(like, null, null, null);
                holder.comments.setText(NumUtils.getNumStr(map.get("news_comment")));
                holder.comments.setCompoundDrawables(comment, null, null, null);
                holder.user.setTag(map.get("active"));
                holder.abs.setText(mApplication.ST(map.get("abstract")));


            } else if (getItemViewType(position) == 1) {
                Holder2 holder2;
                if (view == null) {
                    holder2 = new Holder2();
                    view = inflater.inflate(R.layout.activity_header2, parent, false);
                    holder2.title = (TextView) view.findViewById(R.id.activity_item_title);
                    holder2.content = (TextView) view.findViewById(R.id.activity_item_content);
                    holder2.imageView = (ImageView) view.findViewById(R.id.activity_item_img);
                    holder2.peopleNum = (TextView) view.findViewById(R.id.activity_item_peopleNum);
                    holder2.time = (TextView) view.findViewById(R.id.activity_item_time);
                    view.setTag(holder2);

                } else {
                    holder2 = (Holder2) view.getTag();
                }
                Glide.with(context).load(map.get("image1")).asBitmap().centerCrop().override(screenWidth * 7 / 20, screenWidth / 4).into(new BitmapImageViewTarget(holder2.imageView) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        super.onResourceReady(resource, glideAnimation);
                        RoundedBitmapDrawable rbd = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        rbd.setCornerRadius(7);
                        setDrawable(rbd);
                    }
                });
                if (map.get("title").contains(input.getText().toString().trim())) {
                    int             dex   = map.get("title").indexOf(input.getText().toString().trim());
                    SpannableString title = new SpannableString(mApplication.ST(map.get("title")));
                    title.setSpan(new ForegroundColorSpan(Color.RED), dex, dex + input.getText().toString().trim().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    holder2.title.setText(title);
                } else {
                    holder2.title.setText(mApplication.ST(map.get("title")));
                }
                holder2.title.setTag(map.get("id"));
                holder2.content.setText(mApplication.ST(map.get("abstract")));
                holder2.peopleNum.setText(mApplication.ST("报名人数:" + map.get("enrollment")));
                holder2.time.setText(mApplication.ST("报名时间:" + map.get("start_time") + " 至 " + map.get("end_time")));
            } else if (getItemViewType(position) == 2) {
                ViewHolder holder;
                if (view == null) {
                    holder = new ViewHolder();
                    view = inflater.inflate(R.layout.mine_shoucang_item, parent, false);
                    holder.image = (ImageView) view.findViewById(R.id.mine_shoucang_item_image);
                    holder.title = (TextView) view.findViewById(R.id.mine_shoucang_item_title);
                    holder.time = (TextView) view.findViewById(R.id.mine_shoucang_item_time);
                    holder.user = (TextView) view.findViewById(R.id.mine_shoucang_item_user);
                    holder.type = (TextView) view.findViewById(R.id.mine_shoucang_item_type);
                    holder.delete = (TextView) view.findViewById(R.id.delete);
                    holder.content = (RelativeLayout) view.findViewById(R.id.content);
                    view.setTag(holder);
                } else {
                    holder = (ViewHolder) view.getTag();
                }
                holder.content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setClass(mApplication.getInstance(),GongYangDetail.class);
                        intent.putExtra("id", map.get("id"));
                        context.startActivity(intent);
                    }
                });
                holder.delete.setVisibility(View.GONE);
                holder.type.setText(mApplication.ST("供养"));
                holder.type.setTag(map.get("id"));
                Glide.with(context).load(map.get("image")).override(screenWidth / 5, screenWidth / 5).centerCrop().into(holder.image);
                holder.user.setText(mApplication.ST(map.get("product")));

                if (map.get("type1").contains(input.getText().toString().trim())) {
                    int             dex   = map.get("type1").indexOf(input.getText().toString().trim());
                    SpannableString title = new SpannableString(mApplication.ST(map.get("type1")));
                    title.setSpan(new ForegroundColorSpan(Color.RED), dex, dex + input.getText().toString().trim().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    holder.title.setText(title);
                } else {
                    holder.title.setText(mApplication.ST(map.get("type1")));
                }
//                holder.title.setText(mApplication.ST(map.get("type1")));
                holder.time.setText(mApplication.ST("￥" + map.get("money1")));
            } else if (getItemViewType(position) == 3) {
                ViewHolder holder;
                if (view == null) {
                    holder = new ViewHolder();
                    view = inflater.inflate(R.layout.mine_shoucang_item, parent, false);
                    holder.image = (ImageView) view.findViewById(R.id.mine_shoucang_item_image);
                    holder.title = (TextView) view.findViewById(R.id.mine_shoucang_item_title);
                    holder.time = (TextView) view.findViewById(R.id.mine_shoucang_item_time);
                    holder.user = (TextView) view.findViewById(R.id.mine_shoucang_item_user);
                    holder.type = (TextView) view.findViewById(R.id.mine_shoucang_item_type);
                    holder.delete = (TextView) view.findViewById(R.id.delete);
                    holder.content = (RelativeLayout) view.findViewById(R.id.content);
                    view.setTag(holder);
                } else {
                    holder = (ViewHolder) view.getTag();
                }
                holder.content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setClass(mApplication.getInstance(),FundingDetailActivity.class);
                        intent.putExtra("id", map.get("id"));
                        context.startActivity(intent);
                    }
                });
                holder.delete.setVisibility(View.GONE);
                holder.type.setText(mApplication.ST("助学"));
                holder.type.setTag(map.get("id"));
                holder.user.setText(mApplication.ST("参与人数：" + map.get("cy_people")));
                if (map.get("title").contains(input.getText().toString().trim())) {
                    int             dex   = map.get("title").indexOf(input.getText().toString().trim());
                    SpannableString title = new SpannableString(mApplication.ST(map.get("title")));
                    title.setSpan(new ForegroundColorSpan(Color.RED), dex, dex + input.getText().toString().trim().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    holder.title.setText(title);
                } else {
                    holder.title.setText(mApplication.ST(map.get("title")));
                }
                final ImageView img = holder.image;
                Glide.with(context).load(map.get("image"))
                        .asBitmap().override(DimenUtils.dip2px(Search.this, 120), DimenUtils.dip2px(Search.this, 90)).centerCrop()
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                RoundedBitmapDrawable rbd = RoundedBitmapDrawableFactory.create(getResources(), resource);
                                rbd.setCornerRadius(5);
                                img.setImageDrawable(rbd);
                            }
                        });
                holder.time.setText(mApplication.ST("结束时间：" + map.get("end_time")));
            }


            return view;
        }

        class ViewHolder {
            ImageView image;
            TextView  title, user, time, type;
            TextView delete;
            RelativeLayout content;
        }

        class ViewHolder0 {
            ImageView   img;
            TextView    title;
            TextView    likes;
            TextView    comments;
            TextView    time;
            TextView    user;
            TextView    ctr;
            TextView    type;
            TextView    abs;
            //        JCVideoPlayerStandard player;
            FrameLayout stub;
        }

        class Holder2 {
            TextView  title;
            TextView  content;
            TextView  time;
            TextView  peopleNum;
            ImageView imageView;

        }
    }

    @Override
    protected void onDestroy() {
//        removeDuplicate(gridList);
//        IOUtil.setData(this,TAG,"history",gridList);
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(input.getWindowToken(), 0);
        super.onDestroy();
        mApplication.getInstance().romoveActivity(this);

    }


}
