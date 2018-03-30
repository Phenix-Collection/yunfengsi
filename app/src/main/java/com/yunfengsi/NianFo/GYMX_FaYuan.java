package com.yunfengsi.NianFo;

import android.content.Intent;
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
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.TimeUtils;
import com.yunfengsi.Utils.ToastUtil;
import com.yunfengsi.Utils.mApplication;
import com.yunfengsi.View.mItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class GYMX_FaYuan extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private TextView mtvtitle;
    private TextView mtvtime;

    private RecyclerView mlistview;
    private ImageView mimageback;


    private SharedPreferences sp;
    private HashMap<String, String> map;
    private ArrayList<Integer> arrayList;
    private FayuanAdapter adapter;
    private static final String TAG = "GYMX";

    private List<String> keyList;
    private List<String> valueList;
    private String digit;//单位后缀
    //Hodler hodler;
    private TextView pingtai1, pingtai2, pingtai3, geren1, geren2, geren3;
    private String targetTime;
    private static final int PAGESIZE = 10;
    private int page = 1;
    private int endPage = 0;
    private boolean isLoadMore = false;
    private boolean isRefresh = false;
    private SwipeRefreshLayout swip;

    public void init() {
        keyList = new ArrayList<>();
        valueList = new ArrayList<>();
        mtvtitle = (TextView) findViewById(R.id.activity_lf_title);
        adapter = new FayuanAdapter(new ArrayList<HashMap<String, String>>());
        swip = (SwipeRefreshLayout) findViewById(R.id.swip);
        swip.setOnRefreshListener(this);
        swip.setColorSchemeResources(R.color.main_color);
        mtvtime = (TextView) findViewById(R.id.activity_lf_time);
        pingtai1 = (TextView) findViewById(R.id.pintai1);
        pingtai2 = (TextView) findViewById(R.id.pintai2);
        pingtai3 = (TextView) findViewById(R.id.pintai3);
        geren1 = (TextView) findViewById(R.id.geren1);
        geren2 = (TextView) findViewById(R.id.geren2);
        geren3 = (TextView) findViewById(R.id.geren3);
        mlistview = (RecyclerView) findViewById(R.id.activity_lf_list);
        mimageback = (ImageView) findViewById(R.id.activity_lf_imageback);
        adapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_RIGHT);

        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {

                if (endPage != page) {
                    isLoadMore = true;
                    page++;
                    getList();
                }
            }
        },mlistview);
        TextView textView = new TextView(this);
        Drawable d = ContextCompat.getDrawable(this, R.drawable.load_nothing);
        d.setBounds(0, 0, DimenUtils.dip2px(this, 150), DimenUtils.dip2px(this, 150) * d.getIntrinsicHeight() / d.getIntrinsicWidth());
        textView.setCompoundDrawables(null, d, null, null);
        textView.setCompoundDrawablePadding(DimenUtils.dip2px(this, 10));
        textView.setText("暂无发愿记录\n\n下拉刷新");
        textView.setGravity(Gravity.CENTER);
        ViewGroup.MarginLayoutParams vl = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        vl.topMargin = DimenUtils.dip2px(this, 150);
        textView.setLayoutParams(vl);
        adapter.setEmptyView(textView);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter a, View view, int i) {
                Intent intent=new Intent(GYMX_FaYuan.this,FaYuan_Detail.class);
                intent.putExtra("id",adapter.getData().get(i).get("id"));
                intent.putExtra("type",adapter.getData().get(i).get("name"));
                intent.putExtra("type_id",adapter.getData().get(i).get("type"));
                startActivity(intent);
            }
        });

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.gongyangmingxi_acticity_fayuan);
        init();

        sp = getSharedPreferences("user", MODE_PRIVATE);
        getweb();  //从服务器拿到念佛的数据
        setTargetTime();
        swip.post(new Runnable() {
            @Override
            public void run() {
                swip.setRefreshing(true);
                onRefresh();
            }
        });

        mtvtitle.setText(mApplication.ST("发愿"));

        mlistview.setLayoutManager(new LinearLayoutManager(this));
        mlistview.addItemDecoration(new mItemDecoration(this));
        mlistview.setAdapter(adapter);
        mimageback.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        finish();
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

    public void getweb() {


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject js = new JSONObject();
                    try {
                        js.put("user_id", sp.getString("user_id", ""));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m = ApisSeUtil.i(js);
                    LogUtil.e("发愿信息统计：：" + js);
                    final String data = OkGo.post(Constants.Fayuan_Info_Ip).tag(this).params("key", m.K())
                            .params("msg", m.M())
                            .execute().body().string();
                    if (!data.equals("")) {
                        map = AnalyticalJSON.getHashMap(data);
                        if (map != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    SpannableString p1 = new SpannableString("平台累计 " + NumUtils.getNumStr(map.get("zs_pt")));
//                                    p1.setSpan(new AbsoluteSizeSpan(DimenUtils.dip2px(GYMX_FaYuan.this, 28)),4, p1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    pingtai1.setText(p1);
                                    SpannableString p2 = new SpannableString("已完成 " + NumUtils.getNumStr(map.get("wc_pt")));
//                                    p2.setSpan(new AbsoluteSizeSpan(DimenUtils.dip2px(GYMX_FaYuan.this, 28)),3, p2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    pingtai2.setText(p2);
                                    SpannableString p3 = new SpannableString("进行中 " + NumUtils.getNumStr(map.get("jx_pt")));
//                                    p3.setSpan(new AbsoluteSizeSpan(DimenUtils.dip2px(GYMX_FaYuan.this, 28)),3, p3.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    pingtai3.setText(p3);
                                    SpannableString g1 = new SpannableString("个人累计 " + NumUtils.getNumStr(map.get("zs_gr")));
//                                    g1.setSpan(new AbsoluteSizeSpan(DimenUtils.dip2px(GYMX_FaYuan.this, 28)),4, g1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    geren1.setText(g1);
                                    SpannableString g2 = new SpannableString("已完成 " + NumUtils.getNumStr(map.get("wc_gr")));
//                                    g2.setSpan(new AbsoluteSizeSpan(DimenUtils.dip2px(GYMX_FaYuan.this, 28)),3, g2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    geren2.setText(g2);
                                    SpannableString g3 = new SpannableString("进行中 " + NumUtils.getNumStr(map.get("jx_gr")));
//                                    g3.setSpan(new AbsoluteSizeSpan(DimenUtils.dip2px(GYMX_FaYuan.this, 28)),3, g3.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    geren3.setText(g3);
//                                    adapter = new GK_NF_Adapter(GYMX_FaYuan.this, keyList, type);
//                                    adapter.setValueList(valueList);
//                                    mlistview.setAdapter(adapter);


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

    public void getList() {
        JSONObject js = new JSONObject();
        try {
            js.put("page", page);
            js.put("user_id", PreferenceUtil.getUserId(this));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m = ApisSeUtil.i(js);
        LogUtil.e("发愿信息列表：" + js);
        OkGo.post(Constants.Fayuan_Info_List_Ip).tag(this).params("key", m.K())
                .params("msg", m.M())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (s != null) {
                            ArrayList<HashMap<String, String>> list = AnalyticalJSON.getList_zj(s);
                            if (list != null) {
                                if (isRefresh) {
                                    adapter.setNewData(list);
                                    isRefresh = false;
                                    swip.setRefreshing(false);
                                } else if (isLoadMore) {
                                    isLoadMore = false;
                                    if (list.size() < PAGESIZE) {
                                        ToastUtil.showToastShort("发愿记录加载完毕", Gravity.CENTER);
//                                endPage = page;
                                        adapter.addData(list);
                                        adapter.loadMoreEnd(false);
                                    } else {
                                        adapter.addData(list);
                                        adapter.loadMoreComplete();
                                    }
                                }
                            }
                        }
                        swip.setRefreshing(false);
                    }
                });
    }

    @Override
    public void onRefresh() {
        page = 1;
        endPage = -1;
        isRefresh = true;
        adapter.setEnableLoadMore(true);
        getList();
    }

    public class FayuanAdapter extends BaseQuickAdapter<HashMap<String, String>,BaseViewHolder> {
        public FayuanAdapter(List<HashMap<String, String>> data) {
            super(R.layout.fayuan_detail_item, data);
        }

        @Override
        protected void convert(BaseViewHolder holder, HashMap<String, String> map) {
            String t1 = TimeUtils.getTrueTimeStr(map.get("time")) + "发愿:\n";
            String t2 = "";
            String t3 = "";
            long target = 0;
            long num = 0;
            if (map.get("target_num") == null || map.get("target_num").equals("")) {
                target = 0;
            } else {
                target = Long.valueOf(map.get("target_num"));
            }
            if (map.get("num") == null || map.get("num").equals("")) {
                num = 0;
            } else {
                if(num>=target){
                    num=Long.valueOf(map.get("target_num"));
                }else{
                    num = Long.valueOf(map.get("num"));
                }

            }
            if ("1".equals(map.get("type"))) {
                t2 = "念" + map.get("name") + target + "声\n";
                t3 = "已达成 " + num + " 声   " +
                        (target-num<=0?("完成于"+TimeUtils.getTrueTimeStr(map.get("wc_time"))):("剩余 " + (target - num) + " 声"));
            } else if ("2".equals(map.get("type"))) {
                t2 = "诵" + map.get("name") + target + "部\n";
                t3 = "已达成 " + num + " 部   " +
                        (target-num<=0?("完成于"+TimeUtils.getTrueTimeStr(map.get("wc_time"))):("剩余 " + (target - num) + " 部"));
            } else if ("3".equals(map.get("type"))) {
                t2 = "持" + map.get("name") + target + "遍\n";
                t3 = "已达成 " + num + " 遍   " +
                        (target-num<=0?("完成于"+TimeUtils.getTrueTimeStr(map.get("wc_time"))):("剩余 " + (target - num) + " 遍"));
            }
            SpannableStringBuilder ssb=new SpannableStringBuilder(t1);
            SpannableString s2=new SpannableString(t2);
            s2.setSpan(new ForegroundColorSpan(Color.BLACK),0,s2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.append(s2);
            SpannableString s3=new SpannableString(t3);
            s3.setSpan(new ForegroundColorSpan(ContextCompat.getColor(GYMX_FaYuan.this,R.color.wordhuise)),0,s3.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.append(s3);
            holder.setText(R.id.content,ssb);
            long targetTime=0;
            long now=0;
            if(target>num){//进行中
                if(map.get("end_time")==null||map.get("end_time").equals("")){
                    now=System.currentTimeMillis();
                    targetTime=0;
                }else{
                    targetTime=TimeUtils.dataOne(map.get("end_time"));
                    now=System.currentTimeMillis();
                }
                if(targetTime-now>0){//进行中
                    Long offset=(targetTime-now)/1000/60/60/24;
                    int days=offset.intValue();
                    holder.setText(R.id.status,"还剩\n"+days+"天");
                    holder.getView(R.id.status).setBackground(null);
                }else{
//                    holder.setText(R.id.status,mApplication.ST("失败"));
                    holder.getView(R.id.status).setBackgroundResource(R.drawable.fail);

                }
            }else{//已完成
//                holder.setText(R.id.status,mApplication.ST("已完成"));
                holder.getView(R.id.status).setBackgroundResource(R.drawable.complete);
           }


        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(this);
    }

    //解析{{[{"":""},{"":""}}]}
    public void getList4JsonObject4(String json, String type) {

        JSONObject js = null;
        try {
            js = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (js == null) {
            return;
        }
        JSONObject j = null;

        try {
            if (!js.get(type).equals("0")) {
                j = (JSONObject) js.get(type);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (j == null) {
            return;
        }
        Iterator<String> keysIterator = j.keys();
        while (keysIterator.hasNext()) {
            String key = keysIterator.next();
            keyList.add(key);
            JSONArray jsonArray = null;
            try {
                jsonArray = (JSONArray) j.get(key);
                valueList.add(jsonArray.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }
}
