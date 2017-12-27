package com.qianfujiaoyu.Activitys;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.qianfujiaoyu.View.mItemDeraction;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 作者：因陀罗网 on 2017/5/18 15:38
 * 公司：成都因陀罗网络科技有限公司
 */

public class List_Classes extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
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
        recyclerView.addItemDecoration(new mItemDeraction(2, Color.parseColor("#b6b6b6")));
        adapter = new classAdapter(this, new ArrayList<HashMap<String, String>>());
        adapter.openLoadMore(10, true);
        adapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
        adapter.setEmptyView(mApplication.getLoadNothing(R.drawable.load_nothing, "暂未加入班级\n\n右上角搜索班级",200));

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

        Glide.with(this).load(R.drawable.search_white).centerCrop()
                .override(DimenUtils.dip2px(this, 45), DimenUtils.dip2px(this, 45)).into(((ImageView) findViewById(R.id.right_text)));
        ((ImageView) findViewById(R.id.back)).setImageBitmap(ImageUtil.readBitMap(this, R.drawable.back));
        ((TextView) findViewById(R.id.title)).setText("我的班级");
    }

    // TODO: 2017/5/18  获取数据
    private void getData() {
        if (!Network.HttpTest(mApplication.getInstance())) {
            swip.setRefreshing(false);
            return;
        }
        JSONObject js=new JSONObject();
        try {
            js.put("page",page);
            js.put("m_id",Constants.M_id);
            js.put("user_id", PreferenceUtil.getUserIncetance(this).getString("user_id",""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e("获取班级列表：："+js);
        ApisSeUtil.M m=ApisSeUtil.i(js);
        OkGo.post(Constants.getMyClassList).tag(this)
                .params("key",m.K())
                .params("msg",m.M())
                .execute(new AbsCallback<ArrayList<HashMap<String, String>>>() {
                    @Override
                    public void onSuccess(ArrayList<HashMap<String, String>> l, Call call, Response response) {
//
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

    private class classAdapter extends BaseQuickAdapter<HashMap<String, String>> {
        private Context context;

        public classAdapter(Context context, ArrayList<HashMap<String, String>> data) {
            super(R.layout.item_classes, data);
            this.context = context;
        }

        @Override
        protected void convert(final BaseViewHolder holder, final HashMap<String, String> map) {
            if (map.get("user_id").equals(PreferenceUtil.getUserIncetance(context).getString("user_id",""))) {
               holder.setVisible(R.id.delete,false);
                holder.setVisible(R.id.check,true);
            }else{
                holder.setVisible(R.id.check,true);
                holder.setVisible(R.id.delete,true);
            }
            Glide.with(context).load(map.get("image"))
                    .override(DimenUtils.dip2px(context, 90)
                            , DimenUtils.dip2px(context, 90))
                    .placeholder(R.drawable.indra)
                    .centerCrop()
                    .into((ImageView) holder.getView(R.id.image));
            holder.setText(R.id.name, map.get("title"))
                    .setText(R.id.people, "班级人数:" + map.get("num") + "人")
                    .setText(R.id.time, "创建时间:" + TimeUtils.getTrueTimeStr(map.get("time")));

            holder.getView(R.id.content).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(List_Classes.this, Home_Class.class);
                    intent.putExtra("id", map.get("id"));
                    intent.putExtra("url",map.get("image"));
                    intent.putExtra("title",map.get("title"));
                    intent.putExtra("role",map.get("user_id").equals(PreferenceUtil.getUserIncetance(context).getString("user_id",""))?Home_Class.Admin:Home_Class.MEMBER);
                    intent.putExtra("num", map.get("num"));
                    startActivity(intent);
                }
            });
            holder.getView(R.id.check).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context,Member_List.class);
                    intent.putExtra("id",map.get("id"));
                    intent.putExtra("role",map.get("user_id").equals(PreferenceUtil.getUserIncetance(context).getString("user_id",""))?Home_Class.Admin:Home_Class.MEMBER);
                    startActivity( intent);
                }
            });
            holder.getView(R.id.delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JSONObject js=new JSONObject();
                    try {
                        js.put("class_id",map.get("id"));
                        js.put("m_id",Constants.M_id);
                        js.put("user_id", PreferenceUtil.getUserIncetance(mApplication.getInstance()).getString("user_id",""));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m=ApisSeUtil.i(js);
                    OkGo.post(Constants.QuitClass).params("key",Constants.safeKey)
                            .params("key",m.K())
                            .params("msg",m.M())
                            .execute(new AbsCallback<HashMap<String,String>>() {
                                @Override
                                public void onSuccess(HashMap<String, String> map, Call call, Response response) {
                                    if(map!=null){
                                        if("000".equals(map.get("code"))){
                                            getData().remove(holder.getAdapterPosition());
                                            notifyItemRemoved(holder.getAdapterPosition());
                                        }
                                    }
                                }

                                @Override
                                public HashMap<String, String> convertSuccess(Response response) throws Exception {
                                    return AnalyticalJSON.getHashMap(response.body().string());
                                }
                            });
                }
            });
        }
    }

    @Override
    public boolean setEventBus() {
        return false;
    }

    @Override
    public boolean isMainColor() {
        return true;
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
            case R.id.right1://管理员进入班级申请界面
                Intent intent = new Intent(this, ApplyShenhe.class);
                startActivity(intent);
                break;
            case R.id.right_text:
                Intent intent1 = new Intent(this, Search.class);
                startActivity(intent1);
                break;
        }
    }
}
