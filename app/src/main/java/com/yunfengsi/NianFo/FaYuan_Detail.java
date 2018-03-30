package com.yunfengsi.NianFo;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.yunfengsi.R;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.NumUtils;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.TimeUtils;
import com.yunfengsi.Utils.ToastUtil;
import com.yunfengsi.Utils.mApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class FaYuan_Detail extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private TextView mtvtitle;
    private TextView mtvdidian;
    private TextView mtvtime;
    private TextView mtvleijione;
    private TextView mtvpintaitwo;
    private TextView mtvleijithree;
    private TextView mtvhead;
    private RecyclerView mlistview;
    private ImageView mimageback;


    private SharedPreferences sp;
    private HashMap<String, String> listhashMap;
    private ArrayList<Integer> arrayList;
    private fdAdapter adapter;
    private static final String TAG = "GYMX";
    private String type, type_String;
    private String url;
    private List<String> keyList;
    private List<String> valueList;
    private RelativeLayout bg_layout;
    private String digit;//单位后缀
    private String targetTime;
    //Hodler hodler;
    private String sb2;

    public void init() {
//        ((TextView) findViewById(R.id.leiji)).setText(mApplication.ST("累计"));
//        ((TextView) findViewById(R.id.pingtai)).setText(mApplication.ST("平台"));
        keyList = new ArrayList<>();
        valueList = new ArrayList<>();
//        mtvhead = (TextView) findViewById(R.id.activity_lf_tvhead);
        mtvtitle = (TextView) findViewById(R.id.activity_lf_title);
        swip = (SwipeRefreshLayout) findViewById(R.id.swip);
        swip.setOnRefreshListener(this);
        swip.setColorSchemeResources(R.color.main_color);
//        mtvdidian = (TextView) findViewById(R.id.activity_lf_didian);
        mtvtime = (TextView) findViewById(R.id.activity_lf_time);
        mtvleijione = (TextView) findViewById(R.id.activity_lf_tvleijione);
        mtvpintaitwo = (TextView) findViewById(R.id.activity_lf_tvpintaitwo);
//        mtvleijithree = (TextView) findViewById(R.id.activity_lf_tvleijithree);
        mlistview = (RecyclerView) findViewById(R.id.activity_lf_list);
        mlistview.setLayoutManager(new LinearLayoutManager(this));
//        mlistview.addItemDecoration(new mItemDecoration(this));
        mimageback = (ImageView) findViewById(R.id.activity_lf_imageback);
        bg_layout = (RelativeLayout) findViewById(R.id.activity_lf_layoutzx);


    }

    private String id;
    private static final int PAGESIZE = 10;
    private int page = 1;
    private int endPage = 0;
    private boolean isLoadMore = false;
    private boolean isRefresh = false;
    private SwipeRefreshLayout swip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.fayuan_detail);
        init();
        type = getIntent().getStringExtra("type");

        id = getIntent().getStringExtra("id");
        sp = getSharedPreferences("user", MODE_PRIVATE);

        switch (getIntent().getStringExtra("type_id")) {
            case "1":
//                url = Constants.Mine_GK_NF;
//                Log.w(TAG, "getweb: url" + url);
                type_String = "buddha";
                sb2 = "念";
                digit = "声";
                break;
            case "2":
//                url = Constants.Mine_GK_SJ;
//                Log.w(TAG, "getweb: url" + url);
                type_String = "reading";
                sb2 = "诵";
                digit = "部";
                break;
            case "3":
//                url = Constants.Mine_GK_CZ;
//                Log.w(TAG, "getweb: url" + url);
                type_String = "japa";
                sb2 = "持";
                digit = "遍";
                break;
        }
        swip.post(new Runnable() {
            @Override
            public void run() {
                swip.setRefreshing(true);
                onRefresh();
            }
        });
        adapter = new fdAdapter(new ArrayList<HashMap<String, String>>(), digit, sb2);
        adapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_RIGHT);


        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {

                if (endPage != page) {
                    isLoadMore = true;
                    page++;
                    getweb();
                }
            }
        },mlistview);
        TextView textView = new TextView(this);
        Drawable d = ContextCompat.getDrawable(this, R.drawable.load_nothing);
        d.setBounds(0, 0, DimenUtils.dip2px(this, 150), DimenUtils.dip2px(this, 150) * d.getIntrinsicHeight() / d.getIntrinsicWidth());
        textView.setCompoundDrawables(null, d, null, null);
        textView.setCompoundDrawablePadding(DimenUtils.dip2px(this, 10));
        textView.setText("暂无数据\n\n下拉刷新");
        textView.setGravity(Gravity.CENTER);
        ViewGroup.MarginLayoutParams vl = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        vl.topMargin = DimenUtils.dip2px(this, 180);
        textView.setLayoutParams(vl);
        adapter.setEmptyView(textView);
//        getweb();  //从服务器拿到念佛的数据

        setTargetTime();

        mtvtitle.setText(mApplication.ST(type));
        mlistview.setAdapter(adapter);
        mimageback.setOnClickListener(this);
    }

    private void setTargetTime() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("m_id", Constants.M_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m = ApisSeUtil.i(jsonObject);
        OkGo.post(Constants.Fayuan_TargetTime_Get_Ip).tag(this).params("key", m.K())
                .params("msg", m.M())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        HashMap<String, String> map = AnalyticalJSON.getHashMap(s);
                        if (map != null) {
                            if ("000".equals(map.get("code"))) {
                                targetTime = map.get("time");
                                mtvtime.setText("截止时间:" + targetTime + "(农历7月15日)");
                            }
                        }

                    }
                });

    }

    @Override
    public void onClick(View view) {
        finish();
    }

    public void getweb() {


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject js = new JSONObject();
                    try {
                        js.put("user_id", sp.getString("user_id", ""));
                        js.put("page", page);
                        js.put("id", id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m = ApisSeUtil.i(js);
                    LogUtil.e("发愿详情：" + js);
                    final String data = OkGo.post(Constants.Fayuan_Info_Detail_Ip).tag(TAG).params("key", m.K())
                            .params("msg", m.M())
                            .execute().body().string();
                    if (!data.equals("")) {
                        listhashMap = AnalyticalJSON.getHashMap(data);
                        if (listhashMap != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String t1 = "发愿目标\n" + NumUtils.getNumStr(listhashMap.get("target_num")) + digit;
                                    String num=(NumUtils.getNumStr(Long.valueOf(listhashMap.get("num"))>=Long.valueOf(listhashMap.get("target_num"))?listhashMap.get("target_num"):listhashMap.get("num")));
                                    String t2 = "已达成\n" + num + digit;
                                    SpannableString s1 = new SpannableString(t1);
                                    s1.setSpan(new AbsoluteSizeSpan(28, true), s1.length() - NumUtils.getNumStr(listhashMap.get("target_num")).length() - 1, s1.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    s1.setSpan(new ForegroundColorSpan(Color.parseColor("#777777")), s1.length() - NumUtils.getNumStr(listhashMap.get("target_num")).length() - 1, s1.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    mtvleijione.setText(s1);
                                    SpannableString s2 = new SpannableString(t2);
                                    s2.setSpan(new AbsoluteSizeSpan(28, true), s2.length() - num.length() - 1, s2.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    s2.setSpan(new ForegroundColorSpan(Color.parseColor("#777777")), s2.length() - num.length() - 1, s2.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    mtvpintaitwo.setText(s2);

                                    ArrayList<HashMap<String, String>> list = AnalyticalJSON.getList_zj(listhashMap.get("mingxi"));
                                    if (list != null) {
                                        if (isRefresh) {
                                            adapter.setNewData(list);
                                            isRefresh = false;
                                            swip.setRefreshing(false);
                                        } else if (isLoadMore) {
                                            isLoadMore = false;
                                            if (list.size() < PAGESIZE) {
                                                ToastUtil.showToastShort("加载完毕", Gravity.CENTER);
//                                endPage = page;
                                                adapter.addData(list);
                                                adapter.loadMoreEnd(false);
                                            } else {
                                                adapter.addData(list);
                                                adapter.loadMoreComplete();
                                            }
                                        }

                                        swip.setRefreshing(false);
                                    }
                                }
                            });
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {

                }
            }

        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(TAG);
    }

    public class fdAdapter extends BaseQuickAdapter<HashMap<String, String>,BaseViewHolder> {
        private String digit, sb2;

        public fdAdapter(List<HashMap<String, String>> data, String digit, String sb2) {
            super(R.layout.gk_itme_itme, data);
            this.digit = digit;
            this.sb2 = sb2;
        }

        @Override
        protected void convert(BaseViewHolder holder, HashMap<String, String> map) {
            holder.setText(R.id.itme_tvnianjingnum, NumUtils.getNumStr(map.get("ls_nfnum"))  + digit)
            .setText(R.id.itme_tvnianjingname,type);
            holder.setText(R.id.itme_tvnianjingtime, TimeUtils.getTrueTimeStr(map.get("ls_time")));
        }
    }


    @Override
    public void onRefresh() {
        page = 1;
        endPage = -1;
        isRefresh = true;
        adapter.setEnableLoadMore(true);
        getweb();
    }
}
