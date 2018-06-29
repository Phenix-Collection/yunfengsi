package com.yunfengsi.Setting;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.request.BaseRequest;
import com.yunfengsi.Managers.Base.BaseSTActivity;
import com.yunfengsi.Models.Model_zhongchou.FundingDetailActivity;
import com.yunfengsi.R;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.ProgressUtil;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.ToastUtil;
import com.yunfengsi.Utils.mApplication;
import com.yunfengsi.View.mItemDeraction;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/5/11.
 */

public class ChengZhangJiLu extends BaseSTActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private SwipeRefreshLayout swip;
    private mJiLuAdapter adapter;
    private int page = 1;
    private int endPage = -1;
    private boolean isLoadMore = false;
    private boolean isRefresh = false;

    @Override
    protected void resetData() {


    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.activity_chengzhang);

        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(this);
    }

    private void initView() {
        TextView title = (TextView) findViewById(R.id.title);
        swip = (SwipeRefreshLayout) findViewById(R.id.swip);
        swip.setColorSchemeResources(R.color.main_color);
        swip.setOnRefreshListener(this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new mItemDeraction(1, Color.parseColor("#b6b6b6")));
        adapter = new mJiLuAdapter(this, new ArrayList<HashMap<String, String>>());
        adapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter a, View view, int position) {
                Intent intent = new Intent(ChengZhangJiLu.this, FundingDetailActivity.class);
                intent.putExtra("id", adapter.getData().get(position).get("shop_id"));
                startActivity(intent);
            }
        });

        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {

                if (endPage != page) {
                    isRefresh = false;
                    isLoadMore = true;
                    page++;
                    getData();
                }
            }
        }, recyclerView);
        TextView textView = new TextView(this);
        textView.setText(mApplication.ST("暂无数据\n下拉刷新"));
        Drawable drawable = ActivityCompat.getDrawable(this, R.drawable.load_nothing);
        drawable.setBounds(0, 0, DimenUtils.dip2px(this, 120), DimenUtils.dip2px(this, 120));
        textView.setCompoundDrawables(null, drawable, null, null);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setPadding(0, DimenUtils.dip2px(this, 20), 0, 0);
        ViewGroup.LayoutParams vl = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(vl);
        adapter.setEmptyView(textView);
        recyclerView.setAdapter(adapter);
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
        endPage = -1;
        isLoadMore = false;
        isRefresh = true;
        getData();
    }

    private void getData() {
        JSONObject js=new JSONObject();
        try {
            js.put("user_id",PreferenceUtil.getUserIncetance(this).getString("user_id",""));
            js.put("m_id", Constants.M_id);
            js.put("page", String.valueOf(page));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkGo.post(Constants.getLevelList).tag(this)
                .params("key", ApisSeUtil.getKey())
                .params("msg", ApisSeUtil.getMsg(js))
                .execute(new AbsCallback<Object>() {
            @Override
            public Object convertSuccess(Response response) throws Exception {
                return null;
            }

            @Override
            public void onSuccess(Object o, Call call, Response response) {
                try {
                    String data = response.body().string();
                    if (!TextUtils.isEmpty(data)) {
                        ArrayList<HashMap<String, String>> l = AnalyticalJSON.getList_zj(data);
                        if (l != null) {
                            LogUtil.e("~!~!~   " + l);
                            if (isRefresh) {
                                adapter.setNewData(l);
                                isRefresh = false;
                                swip.setRefreshing(false);

                            } else if (isLoadMore) {
                                isLoadMore = false;
                                if (l.size() < 5) {
                                    ToastUtil.showToastShort(mApplication.ST("已经没有更多数据啦"), Gravity.CENTER);
                                    endPage = page;
                                    adapter.addData(l);
                                    adapter.loadMoreEnd(false);
                                } else {
                                    adapter.addData(l);
                                    adapter.loadMoreComplete();
                                }
                            }
                        } else if (data.equals("null")) {
                            endPage = page--;
                            swip.setRefreshing(false);
                            adapter.setEnableLoadMore(false);
                        } else {
                            ToastUtil.showToastShort(mApplication.ST("数据加载失败，请检查网络连接"), Gravity.CENTER);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onBefore(BaseRequest request) {
                super.onBefore(request);
                ProgressUtil.show(ChengZhangJiLu.this, "", "请稍等");
            }

            @Override
            public void onAfter(Object o, Exception e) {
                super.onAfter(o, e);
                ProgressUtil.dismiss();
            }



        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }


    public static class mJiLuAdapter extends BaseQuickAdapter<HashMap<String, String>,BaseViewHolder> {
        private Context context;

        public mJiLuAdapter(Context context, List<HashMap<String, String>> data) {
            super(R.layout.item_jilu, data);
            this.context = context;
        }

        //shop_id  shop_num  money   title   image  abstract cy_people
        @Override
        protected void convert(BaseViewHolder holder, HashMap<String, String> map) {
            String h = String.valueOf(Html.fromHtml(map.get("abstract")));
            holder.setText(R.id.title, mApplication.ST(map.get("title")))
                    .setText(R.id.info, mApplication.ST(h))
                    .setText(R.id.money_num, mApplication.ST("我支持了" + String.format("%.2f",Double.valueOf(map.get("money")))) + "元  " + "我参与了" + map.get("shop_num") + "次")
                    .setText(R.id.people, mApplication.ST("总支持人数:" + map.get("cy_people") + "人"));
            Glide.with(context).load(map.get("image"))
                    .override(DimenUtils.dip2px(context, 120), DimenUtils.dip2px(context, 90))
                    .centerCrop()
                    .into((ImageView) holder.getView(R.id.image));


        }
    }
}
