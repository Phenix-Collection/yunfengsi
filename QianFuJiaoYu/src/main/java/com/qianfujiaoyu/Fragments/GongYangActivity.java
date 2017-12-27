package com.qianfujiaoyu.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.qianfujiaoyu.Activitys.Detail_Teachers;
import com.qianfujiaoyu.R;
import com.qianfujiaoyu.Utils.AnalyticalJSON;
import com.qianfujiaoyu.Utils.ApisSeUtil;
import com.qianfujiaoyu.Utils.Constants;
import com.qianfujiaoyu.Utils.DimenUtils;
import com.qianfujiaoyu.Utils.LogUtil;
import com.qianfujiaoyu.Utils.Network;
import com.qianfujiaoyu.Utils.TimeUtils;
import com.qianfujiaoyu.Utils.ToastUtil;
import com.qianfujiaoyu.Utils.mApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class GongYangActivity extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView mlistview;
    private SwipeRefreshLayout swip;
    private boolean isRefresh;
    private int page = 1;
    private int endPage = -1;
    private boolean isFirstIn = true;
    private View view;
    private static final String TAG = "shopd";
    private GridAdapter adapter;
    private ImageView tip;


    private boolean isLoadMore = false;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (this!=null&&isVisibleToUser) {
            if (isFirstIn) {
                swip = (SwipeRefreshLayout) view.findViewById(R.id.swip);
                swip.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light,
                        android.R.color.holo_green_light);
                swip.setOnRefreshListener(this);
                swip.post(new Runnable() {
                    @Override
                    public void run() {
                        swip.setRefreshing(true);
                        onRefresh();
                    }
                });
                isFirstIn = false;
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.activity_gong_yang, container, false);

        mlistview = (RecyclerView) view.findViewById(R.id.listview);
        mlistview.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        adapter = new GridAdapter(getActivity(), new ArrayList<HashMap<String, String>>());
        adapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        adapter.openLoadMore(10, true);
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
        adapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                Intent intent=new Intent(getActivity(), Detail_Teachers.class);
                intent.putExtra("id",adapter.getData().get(i).get("id"));
                startActivity(intent);
            }
        });
        adapter.setEmptyView(mApplication.getLoadNothing(R.drawable.load_nothing, "暂无数据\n\n下拉刷新",200));
        mlistview.setAdapter(adapter);
        return view;
    }



    private void getData() {
        if(!Network.HttpTest(mApplication.getInstance())){
            swip.setRefreshing(false);
            return;
        }
        JSONObject js=new JSONObject();
        try {
            js.put("page",page);
            js.put("m_id",Constants.M_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m=ApisSeUtil.i(js);
        OkGo.post(Constants.getTeachers).tag(this)
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
                });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(this);
    }

    @Override
    public void onRefresh() {
        page = 1;
        isRefresh = true;
        endPage = -1;
        adapter.openLoadMore(10, true);
        getData();
    }

    private static class GridAdapter extends BaseQuickAdapter<HashMap<String, String>> {
        private Context context;
        public GridAdapter(Context context, List<HashMap<String, String>> data) {
            super(R.layout.list_itme2, data);
            this.context=context;
        }

        @Override
        protected void convert(BaseViewHolder holder, HashMap<String, String> map) {

            Glide.with(context).load(map.get("user_image"))
                    .override(DimenUtils.dip2px(context,60)
                    ,DimenUtils.dip2px(context,60))
                    .into((ImageView) holder.getView(R.id.list_itme_imageview));
            long time = TimeUtils.dataOne(map.get("years"));
            long now = System.currentTimeMillis();
            int i = (int) ((now - time) / 1000 / 60 / 60 / 24 / 365);
            holder.setText(R.id.list_itme_name,map.get("pet_name"))
                    .setText(R.id.list_itme_time,i>=1?"工作年限: "+String.valueOf(i) + "年":"工作年限: <1年")
                    .setText(R.id.listitem_type,map.get("vocation"));
        }
    }


}
