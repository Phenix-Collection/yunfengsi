package com.maimaizu.Fragments;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.request.BaseRequest;
import com.maimaizu.Activitys.Home2_Detail;
import com.maimaizu.Activitys.NewHouseActivity;
import com.maimaizu.Activitys.ZuFangActivity;
import com.maimaizu.Adapter.mBaseAdapter;
import com.maimaizu.Base.BaseFragment;
import com.maimaizu.R;
import com.maimaizu.Utils.Constants;
import com.maimaizu.Utils.DimenUtils;
import com.maimaizu.Utils.Network;
import com.maimaizu.Utils.ToastUtil;
import com.maimaizu.Utils.mApplication;
import com.maimaizu.View.mItemDecoration;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/4/25.
 */

public class TuiJianFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final int PAGESIZE = 10;
    private SwipeRefreshLayout swip;
    private RecyclerView recyclerView;
    private mBaseAdapter adapter;
    private List<mBaseAdapter.OneMulitem> list;

    private View root;
    private int page = 1;
    private int endPage = -1;
    private boolean isLoadMore = false;
    private boolean isRefresh = false;
    private boolean isLoad = false;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_tuijian;
    }

    @Override
    public void initView(View view) {
        root = view;
        swip = (SwipeRefreshLayout) view.findViewById(R.id.swip);
        swip.setColorSchemeResources(R.color.main_color);
        swip.setOnRefreshListener(this);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycle);
        adapter = new mBaseAdapter(getActivity(), list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new mItemDecoration(getActivity()));
        recyclerView.setAdapter(adapter);

        TextView textView = new TextView(getActivity());
        textView.setText(mApplication.ST("暂无数据\n下拉刷新"));
        Drawable drawable = ActivityCompat.getDrawable(getActivity(), R.drawable.load_nothing);
        drawable.setBounds(0, 0, DimenUtils.dip2px(getActivity(), 120), DimenUtils.dip2px(getActivity(), 120));
        textView.setCompoundDrawables(null, drawable, null, null);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setPadding(0, DimenUtils.dip2px(getActivity(), 20), 0, 0);
        ViewGroup.LayoutParams vl = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(vl);
        adapter.setEmptyView(textView);
        adapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        adapter.openLoadMore(PAGESIZE, true);
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
                int type = Integer.valueOf(((mBaseAdapter.OneMulitem) adapter.getItem(i)).getItemType());
                switch (type) {
                    case 1:
                        Intent intent1 = new Intent(getActivity(), NewHouseActivity.class);
                        intent1.putExtra("id", ((mBaseAdapter.OneMulitem) adapter.getItem(i)).getId());
                        startActivity(intent1);
                        break;
                    case 2:
                        Intent intent = new Intent(getActivity(), Home2_Detail.class);
                        intent.putExtra("id", ((mBaseAdapter.OneMulitem) adapter.getItem(i)).getId());
                        startActivity(intent);
                        break;
                    case 3:
                        Intent intent2 = new Intent(getActivity(), ZuFangActivity.class);
                        intent2.putExtra("id", ((mBaseAdapter.OneMulitem) adapter.getItem(i)).getId());
                        startActivity(intent2);
                        break;
                }
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (root != null && !isLoad && isVisibleToUser) {
            swip.post(new Runnable() {
                @Override
                public void run() {
                    swip.setRefreshing(true);
                    onRefresh();
                }
            });

        }
    }

    // TODO: 2017/5/1 数据获取
    private void getData() {


        OkGo.post(Constants.getHomeMore).tag(this).params("key", Constants.safeKey)
                .params("m_id", Constants.M_id)
                .params("page", page)
                .params("city", mApplication.city)
                .execute(new AbsCallback<ArrayList<mBaseAdapter.OneMulitem>>() {
                    @Override
                    public ArrayList<mBaseAdapter.OneMulitem> convertSuccess(Response response) throws Exception {
                        if (response != null) {
                            String data = response.body().string();
                            JSONArray jsonArray = new JSONArray(data);
                            ArrayList<mBaseAdapter.OneMulitem> oneList = new ArrayList<mBaseAdapter.OneMulitem>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                                mBaseAdapter.OneMulitem o = adapter.getOneMulitem();
                                o.setArea(jsonObject.getString("area"));
                                o.setHousetype(jsonObject.getString("housetype"));
                                o.setId(jsonObject.getString("id"));
                                o.setImage(jsonObject.getString("image"));
                                o.setMoney(jsonObject.getString("money"));
                                o.setPoint(jsonObject.getString("point"));
                                o.setTags(new JSONArray(jsonObject.getString("bq")));
                                o.setVillage(jsonObject.getString("village"));
                                o.setTitle(jsonObject.getString("title"));
                                o.setItemType(Integer.valueOf(jsonObject.getString("type")));
                                oneList.add(o);
                            }
                            return oneList;
                        }
                        return null;
                    }

                    @Override
                    public void onSuccess(ArrayList<mBaseAdapter.OneMulitem> list, Call call, Response response) {
                        if (isRefresh) {
                            adapter.setNewData(list);
                            isRefresh = false;
                            swip.setRefreshing(false);
                            isLoad = true;
                        } else if (isLoadMore) {
                            isLoadMore = false;
                            if (list.size() < PAGESIZE) {
                                ToastUtil.showToastShort("已经没有更多数据啦", Gravity.CENTER);
//                                endPage = page;
                                adapter.notifyDataChangedAfterLoadMore(list, false);
                            } else {
                                adapter.notifyDataChangedAfterLoadMore(list, true);
                            }
                        }
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        if (!Network.HttpTest(mApplication.getInstance())) {
                            ToastUtil.showToastShort("网络无法连接，请稍后重试", Gravity.CENTER);
                            return;
                        }
                        ;
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        swip.setRefreshing(false);
                        adapter.notifyDataChangedAfterLoadMore(true);
                    }
                });

    }


    @Override
    public void setOnClick() {

    }

    @Override
    public boolean setEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetCitys(String id) {//id
        onRefresh();
    }

    @Override
    public void doThings() {

    }

    @Override
    public void onRefresh() {
        page = 1;
        endPage = -1;
        isRefresh = true;
        getData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(this);
    }
}
