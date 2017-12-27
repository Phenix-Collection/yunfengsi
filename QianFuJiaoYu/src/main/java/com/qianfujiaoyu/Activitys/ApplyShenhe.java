package com.qianfujiaoyu.Activitys;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.qianfujiaoyu.Base.BaseActivity;
import com.qianfujiaoyu.R;
import com.qianfujiaoyu.Utils.AnalyticalJSON;
import com.qianfujiaoyu.Utils.ApisSeUtil;
import com.qianfujiaoyu.Utils.Constants;
import com.qianfujiaoyu.Utils.DimenUtils;
import com.qianfujiaoyu.Utils.ImageUtil;
import com.qianfujiaoyu.Utils.LogUtil;
import com.qianfujiaoyu.Utils.Network;
import com.qianfujiaoyu.Utils.PreferenceUtil;
import com.qianfujiaoyu.Utils.TimeUtils;
import com.qianfujiaoyu.Utils.ToastUtil;
import com.qianfujiaoyu.Utils.mApplication;
import com.qianfujiaoyu.View.mItemDecoration;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 作者：因陀罗网 on 2017/5/19 14:31
 * 公司：成都因陀罗网络科技有限公司
 */

public class ApplyShenhe extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private SwipeRefreshLayout swip;
    private RecyclerView recyclerView;
    private int page = 1;
    private int endPage = -1;
    private boolean isLoadMore = false;
    private boolean isRefresh;
    private classAdapter adapter;

    @Override
    public int getLayoutId() {
        return R.layout.list_classes;
    }

    @Override
    public void initView() {
        swip = (SwipeRefreshLayout) findViewById(R.id.swip);
        swip.setColorSchemeResources(R.color.main_color);
        swip.setOnRefreshListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new mItemDecoration(this));
        adapter = new classAdapter(this, new ArrayList<HashMap<String, String>>());
        adapter.openLoadMore(10, true);
        adapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
        adapter.setEmptyView(mApplication.getLoadNothing(R.drawable.load_nothing, "暂无班级申请\n\n下拉刷新",200));
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (endPage != page) {
                    isLoadMore = true;
                    page++;
                    getData();
                }
            }
        });
        recyclerView.setAdapter(adapter);
        findViewById(R.id.back).setVisibility(View.VISIBLE);

        findViewById(R.id.right_text).setVisibility(View.VISIBLE);

        ((ImageView) findViewById(R.id.right1)).setImageBitmap(ImageUtil.readBitMap(this, R.drawable.search_gray));
        ((ImageView) findViewById(R.id.back)).setImageBitmap(ImageUtil.readBitMap(this, R.drawable.back));
        ((TextView) findViewById(R.id.title)).setText("通知");
    }

    @Override
    public boolean setEventBus() {
        return false;
    }

    @Override
    public boolean isMainColor() {
        return true;
    }
    // TODO: 2017/5/18  获取数据
    private void getData() {
        if(!Network.HttpTest(mApplication.getInstance())){
            swip.setRefreshing(false);
            return;
        }
        String url ="";
        if(!getIntent().getBooleanExtra("Mine",false)){
            url=Constants.ShengQingList;
        }else{
            url=Constants.MyApply;
        }
        JSONObject js=new JSONObject();
        try {
            js.put("page",page);
            js.put("m_id",Constants.M_id);
            js.put("user_id", PreferenceUtil.getUserIncetance(this).getString("user_id",""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m=ApisSeUtil.i(js);
        OkGo.post(url).tag(this)
                .params("key",m.K())
                .params("msg",m.M())
                .execute(new AbsCallback<ArrayList<HashMap<String,String>>>() {
                    @Override
                    public void onSuccess(ArrayList<HashMap<String, String>> l, Call call, Response response) {
                        if (l != null) {
                            LogUtil.e(l + "");
                            if (isRefresh) {
                                adapter.setNewData(l);
                                isRefresh = false;
                                swip.setRefreshing(false);
                            } else if (isLoadMore) {
                                isLoadMore = false;
                                if (l.size() < 10) {
                                    ToastUtil.showToastShort("已经没有更多数据啦", Gravity.CENTER);
                                    endPage = page;
                                    adapter.notifyDataChangedAfterLoadMore(l, false);
                                } else {
                                    adapter.notifyDataChangedAfterLoadMore(l, true);
                                }
                            }
                        }
                    }

                    @Override
                    public ArrayList<HashMap<String, String>> convertSuccess(Response response) throws Exception {
                        return AnalyticalJSON.getList_zj(response.body().string());
                    }

                    @Override
                    public void onAfter(ArrayList<HashMap<String, String>> hashMaps, Exception e) {
                        super.onAfter(hashMaps, e);
                        swip.setRefreshing(false);
                    }
                });
    }
    @Override
    public void doThings() {
        swip.post(new Runnable() {
            @Override
            public void run() {
                swip.setRefreshing(true);
                onRefresh();
            }
        });
    }

    @Override
    public void onRefresh() {
        page = 1;
        isRefresh = true;
        endPage = -1;
        adapter.openLoadMore(10, true);
        getData();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    private  class classAdapter extends BaseQuickAdapter<HashMap<String, String>> {
        private Context context;
        private Drawable d0, d1, d2;
        public classAdapter(Context context, ArrayList<HashMap<String, String>> data) {
            super(R.layout.tongzhi_item, data);
            this.context = context;
            d0 = ContextCompat.getDrawable(context, R.drawable.guanzhu);
            d0.setBounds(0, 0, DimenUtils.dip2px(context, 25), DimenUtils.dip2px(context, 25));
            d1 = ContextCompat.getDrawable(context, R.drawable.guanzhu_gray);
            d1.setBounds(0, 0, DimenUtils.dip2px(context, 25), DimenUtils.dip2px(context, 25));
            d2 = ContextCompat.getDrawable(context, R.drawable.delete);
            d2.setBounds(0, 0, DimenUtils.dip2px(context, 25), DimenUtils.dip2px(context, 25));
        }

        @Override
        protected void convert(final BaseViewHolder holder, final HashMap<String, String> map) {

            TextView agree=holder.getView(R.id.tongzhi_tongyi);
            TextView diny=holder.getView(R.id.tongzhi_diny);
            TextView delete=holder.getView(R.id.delete);

            ((TextView) holder.getView(R.id.tongzhi_item_name)).setText(map.get("pet_name"));
            SpannableString ss=new SpannableString("申请加入:"+map.get("title"));
            ss.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context,R.color.main_color)),4,ss.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            ((TextView) holder.getView(R.id.tongzhi_item_msg)).setText(ss);
            ((TextView) holder.getView(R.id.tongzhi_item_time)).setText(TimeUtils.getTrueTimeStr(map.get("time")));
            Glide.with(context).load(map.get("user_image")).override(DimenUtils.dip2px(context, 70), DimenUtils.dip2px(context, 70)).into((ImageView) holder.getView(R.id.tongzhi_item_head));
            if(!getIntent().getBooleanExtra("Mine",false)){//管理员


//                ((TextView) holder.getView(R.id.tongzhi_item_name)).setText(map.get("pet_name"));
//                SpannableString ss=new SpannableString("申请加入:"+map.get("title"));
//                ss.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context,R.color.main_color)),4,ss.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
//                ((TextView) holder.getView(R.id.tongzhi_item_msg)).setText(ss);
//                ((TextView) holder.getView(R.id.tongzhi_item_time)).setText(TimeUtils.getTrueTimeStr(map.get("time")));
                agree.setVisibility(View.VISIBLE);
                agree.setVisibility(View.VISIBLE);

                // TODO: 2016/12/23 收到通知

                if (map.get("status").equals("2")) {
                    agree.setText("对方已同意");
                    diny.setVisibility(View.GONE);
                    agree.setCompoundDrawables(null, d0, null, null);
                } else if (map.get("status").equals("1")) {
                    diny.setText("对方已拒绝");
                    diny.setCompoundDrawables(null, d2, null, null);
                    agree.setVisibility(View.GONE);


                } else {
                    agree.setCompoundDrawables(null, d0, null, null);
                    diny.setCompoundDrawables(null,d2,null,null);
                    agree.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            JSONObject js=new JSONObject();
                            try {
                                js.put("status", "2");
                                js.put("id",map.get("id"));
                                js.put("class_id", map.get("class_id"));
                                js.put("page",page);
                                js.put("m_id",Constants.M_id);
                                js.put("user_id",map.get("user_id"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ApisSeUtil.M m=ApisSeUtil.i(js);
                            OkGo.post(Constants.CommitApplyResult)
                                    .params("key",m.K())
                                    .params("msg",m.M())
                                    .execute(new AbsCallback<HashMap<String,String>>() {
                                        @Override
                                        public HashMap<String, String> convertSuccess(Response response) throws Exception {
                                            return AnalyticalJSON.getHashMap(response.body().string());
                                        }

                                        @Override
                                        public void onSuccess(HashMap<String, String> m, Call call, Response response) {
                                            if(m!=null){
                                                if("000".equals(m.get("code"))){
                                                    map.put("status","2");
                                                    notifyItemChanged(holder.getAdapterPosition());
                                                }else if("001".equals(m.get("code"))){
                                                    LogUtil.e("连续点击两次同意");
                                                }
                                            }
                                        }

                                    });
                        }
                    });
                    diny.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            JSONObject js=new JSONObject();
                            try {
                                js.put("id",map.get("id"));
                                js.put("class_id", map.get("class_id"));
                                js.put("status", "1");
                                js.put("page",page);
                                js.put("m_id",Constants.M_id);
                                js.put("user_id",map.get("user_id"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ApisSeUtil.M m=ApisSeUtil.i(js);
                            OkGo.post(Constants.CommitApplyResult)
                                    .params("key",m.K())
                                    .params("msg",m.M())
                                    .execute(new AbsCallback<HashMap<String,String>>() {
                                        @Override
                                        public HashMap<String, String> convertSuccess(Response response) throws Exception {
                                            return AnalyticalJSON.getHashMap(response.body().string());
                                        }

                                        @Override
                                        public void onSuccess(HashMap<String, String> m, Call call, Response response) {
                                            if(m!=null){
                                                if("000".equals(m.get("code"))){
                                                    map.put("status","1");
                                                    notifyItemChanged(holder.getAdapterPosition());
                                                }
                                            }
                                        }

                                    });
                        }
                    });
                }
            }else{
                diny.setVisibility(View.GONE);
                agree.setVisibility(View.VISIBLE);
                if(map.get("status").equals("0")){
                    agree.setBackgroundResource(R.drawable.button1_shape_pressed);
                    agree.setTextColor(Color.WHITE);
                    agree.setText("未查看");
                }else if(map.get("status").equals("1")){
                    agree.setBackgroundResource(R.drawable.button1_shape_enabled);
                    agree.setTextColor(Color.parseColor("#cccccc"));
                    agree.setText("对方已拒绝");
                }else if(map.get("status").equals("2")){
                    agree.setBackgroundResource(R.drawable.button1_shape);
                    agree.setTextColor(Color.WHITE);
                    agree.setText("对方已同意");
                }
            }

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.w(TAG, "onClick: 通知的id：" + v);
                    JSONObject js=new JSONObject();
                    try {
                        js.put("id", map.get("id"));
                        js.put("m_id",Constants.M_id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m=ApisSeUtil.i(js);
                    OkGo.post(Constants.DeleteApplyResult)
                            .params("key",m.K())
                            .params("msg",m.M()).
                            execute(new AbsCallback<HashMap<String,String>>() {
                                @Override
                                public HashMap<String, String> convertSuccess(Response response) throws Exception {
                                    return AnalyticalJSON.getHashMap(response.body().string());
                                }

                                @Override
                                public void onSuccess(HashMap<String, String> map, Call call, Response response) {
                                    if(map!=null){
                                        if("000".equals(map.get("code"))){
                                            getData().remove(holder.getAdapterPosition());
                                            notifyItemRemoved(holder.getAdapterPosition());
                                        }
                                    }
                                }




                    });
                }
            });

        }
    }

}
