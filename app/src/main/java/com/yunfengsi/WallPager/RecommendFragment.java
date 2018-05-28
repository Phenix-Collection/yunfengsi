package com.yunfengsi.WallPager;

import android.app.ActivityOptions;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.yunfengsi.R;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 作者：因陀罗网 on 2018/5/26 11:15
 * 公司：成都因陀罗网络科技有限公司
 */
public class RecommendFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private View                  view;
    private SwipeRefreshLayout    swipeRefreshLayout;
    private RecyclerView          recyclerView;
    private RecommendPagerAdapter adapter;
    private int pageSize = 9;
    private int page = 1;
    private int endPage = -1;
    private boolean isLoadMore = false;
    private boolean isRefresh = false;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_recommend_wallpager, container, false);

        swipeRefreshLayout = view.findViewById(R.id.swip);
        swipeRefreshLayout.setColorSchemeResources(R.color.main_color);
        swipeRefreshLayout.setOnRefreshListener(this);

        recyclerView = view.findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));


        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        adapter = new RecommendPagerAdapter(list);
        adapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (endPage != page) {
                    isLoadMore = true;
                    page++;
                    getWallPapers();
                }
            }
        }, recyclerView);
        adapter.disableLoadMoreIfNotFullPage();
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                onRefresh();
                swipeRefreshLayout.setRefreshing(true);
            }
        });
        return view;

    }


    private class RecommendPagerAdapter extends BaseQuickAdapter<HashMap<String, String>, BaseViewHolder> {
        public RecommendPagerAdapter(@Nullable List<HashMap<String, String>> data) {
            super(R.layout.item_wallpager_recommend, data);
        }

        @Override
        protected void convert(final BaseViewHolder helper, final HashMap<String, String> item) {
            Glide.with(getActivity()).load(item.get("image"))
                    .into((ImageView) helper.getView(R.id.image));
            helper.getView(R.id.image).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Build.VERSION.SDK_INT>=21){
                        ActivityOptions options=ActivityOptions.makeSceneTransitionAnimation(getActivity(),
                                Pair.create(v, getString(R.string.wallPaper_ShareName))
                        ,Pair.create((((WallPapaerHome) getActivity()).findViewById(R.id.userImage)),getString(R.string.wallPaper_User_Head_ShareName)));
                        IWallPaperManager.goToWallPaperDetailWithAnim(getActivity(),options.toBundle(),item.get("image"));
                    }else{
                        IWallPaperManager.goToWallPaperDetailNormal(getActivity(),item.get("image"));
                    }
                }
            });
        }
    }

    private void getWallPapers() {
        JSONObject js=new JSONObject();
        try {
            js.put("m_id", Constants.M_id);
            js.put("page",page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e("获取壁纸列表"+js);
        ApisSeUtil.M m=ApisSeUtil.i(js);
        OkGo.post(Constants.WallPaperList).tag(this)
                .params("key",m.K())
                .params("msg",m.M())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        HashMap<String,String > map=AnalyticalJSON.getHashMap(s);
                        if(map!=null){
                            final ArrayList<HashMap<String, String>> list = AnalyticalJSON.getList_zj(map.get("msg"));
                            if (list != null) {
                                if (isRefresh) {
                                    adapter.setNewData(list);
                                    isRefresh = false;
                                } else if (isLoadMore) {
                                    isLoadMore = false;
                                    if (list.size() < pageSize) {
                                        endPage = page;
                                        adapter.addData(list);
                                        adapter.loadMoreEnd(true);
                                    } else {
                                        adapter.addData(list);
                                        adapter.loadMoreComplete();
                                    }
                                }
                            }
                        }

                    }

                    @Override
                    public void onAfter(String s, Exception e) {
                        super.onAfter(s, e);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    @Override
    public void onRefresh() {
        page = 1;
        isRefresh = true;
        adapter.setEnableLoadMore(true);
        getWallPapers();
    }
}
