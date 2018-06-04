package com.yunfengsi.WallPaper;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.yunfengsi.R;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.ProgressUtil;
import com.yunfengsi.Utils.ScaleImageUtil;
import com.yunfengsi.Utils.ToastUtil;
import com.yunfengsi.Utils.mApplication;

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
 * <p>
 * <p>
 * 壁纸用户中心统一fragment
 */
public class UserHomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private View                  view;
    private SwipeRefreshLayout    swipeRefreshLayout;
    private RecyclerView          recyclerView;
    private RecommendPagerAdapter adapter;
    private int     pageSize   = 9;
    private int     page       = 1;
    private int     endPage    = -1;
    private boolean isLoadMore = false;
    private boolean isRefresh  = false;
    private String  type       = "1";//1  mine   2  verify  3   collect   4   Others

    BroadcastReceiver receiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(type.equals("1")){//删除自己的壁纸时用到
                LogUtil.e("我的壁纸  刷新");
                onRefresh();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_recommend_wallpager, container, false);

        swipeRefreshLayout = view.findViewById(R.id.swip);
        swipeRefreshLayout.setColorSchemeResources(R.color.main_color);
        swipeRefreshLayout.setOnRefreshListener(this);

        recyclerView = view.findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
//        StaggeredGridLayoutManager layoutManager=new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
//        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
//        recyclerView.setLayoutManager(layoutManager);

        type = getArguments().getString("type");

        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        adapter = new RecommendPagerAdapter(list);
//        adapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
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
        String tip = "";
        switch (type) {
            case "1":
                pageSize = 9;
                tip = "您还没有通过审核的壁纸哟,快去右上角上传吧";
                IntentFilter intentFilter=new IntentFilter("wall_mine");
                LocalBroadcastManager.getInstance(mApplication.getInstance()).registerReceiver(receiver,intentFilter);
                break;
            case "2":
                pageSize = 16;
                tip = "您还没有上传过壁纸哟，快去右上角上传吧";
                break;
            case "3":
                pageSize = 12;
                tip = "您还没有收藏过壁纸哟，快去收藏一下吧";
                break;
            case "4":
                pageSize = 9;
                tip = "Ta还没有已发布的壁纸哟";
                break;
        }
        adapter.setEmptyView(mApplication.getEmptyView(getActivity(), 100, tip));
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
        int singleWidth;

        public RecommendPagerAdapter(@Nullable List<HashMap<String, String>> data) {
            super(R.layout.item_wallpager_userhome, data);
            singleWidth = (getResources().getDisplayMetrics().widthPixels - DimenUtils.dip2px(getActivity(), 24)) / 3;
        }

        @Override
        protected void convert(final BaseViewHolder helper, final HashMap<String, String> item) {
//            ((ImageView) helper.getView(R.id.image))
            Glide.with(getActivity()).load(item.get("image"))
//                    .skipMemoryCache(true)
                    .override(singleWidth, (singleWidth << 4) / 9)
                    .centerCrop()
                    .into( ((ImageView) helper.getView(R.id.image)));
            helper.getView(R.id.image).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (type) {
                        case "1":
//                            v.setTag(R.id.WallPaperId,item.get("id"));
                            IWallPaperManager.goToWallPaperDetailCompat(getActivity(), getData().indexOf(item), (ArrayList<HashMap<String, String>>) getData(), v,true,false);
                            break;
                        case "2":
                            ScaleImageUtil.openBigIagmeMode(getActivity(), item.get("image"), false);
                            break;
                        case "3":
                            // TODO: 2018/5/30
//                            v.setTag(R.id.WallPaperId,item.get("id"));
                            IWallPaperManager.goToWallPaperDetailCompat(getActivity(),getData().indexOf(item), (ArrayList<HashMap<String, String>>) getData(), v,false,true);
                            break;
                        case "4":
//                            v.setTag(R.id.WallPaperId,item.get("id"));
                            IWallPaperManager.goToWallPaperDetailCompat(getActivity(),getData().indexOf(item), (ArrayList<HashMap<String, String>>) getData(), v,false,false);
                            break;
                    }

                }
            });
            switch (type) {
                case "1":
                    break;
                case "2":
                    helper.getView(R.id.status).setVisibility(View.VISIBLE);
                    helper.getView(R.id.delete).setVisibility(View.VISIBLE);
                    helper.getView(R.id.delete).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage("该壁纸正在审核中，确认删除该壁纸吗?")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            JSONObject jsonObject = new JSONObject();
                                            try {
                                                jsonObject.put("m_id", Constants.M_id);
                                                jsonObject.put("user_id", PreferenceUtil.getUserId(getActivity()));
                                                jsonObject.put("wallpaper_id", item.get("id"));
                                                jsonObject.put("type","2");//1 审核过的   2  正在审核的
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            LogUtil.e("删除待审核壁纸：；" + jsonObject);
                                            ApisSeUtil.M m = ApisSeUtil.i(jsonObject);
                                            OkGo.post(Constants.WallPaperTMineDelete).params("key", m.K())
                                                    .params("msg", m.M())
                                                    .execute(new StringCallback() {
                                                        @Override
                                                        public void onSuccess(String s, Call call, Response response) {
                                                            HashMap<String,String > map=AnalyticalJSON.getHashMap(s);
                                                            if(map!=null){
                                                                if("000".equals(map.get("code"))){
                                                                    ToastUtil.showToastShort("删除成功");

                                                                    getData().remove(helper.getAdapterPosition());
                                                                    notifyItemRemoved(helper.getAdapterPosition());
                                                                }else{
                                                                    ToastUtil.showToastShort("删除失败，请稍后重试");
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onBefore(BaseRequest request) {
                                                            super.onBefore(request);
                                                            ProgressUtil.show(getActivity(),"","正在删除...");
                                                        }

                                                        @Override
                                                        public void onAfter(String s, Exception e) {
                                                            super.onAfter(s, e);
                                                            ProgressUtil.dismiss();
                                                        }
                                                    });
                                        }
                                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();

                                }
                            }).create().show();

                        }
                    });
                    break;
                case "3":
                    helper.getView(R.id.collectImg).setVisibility(View.VISIBLE);
                    break;
                case "4":

                    break;
            }
        }
    }

    private void getWallPapers() {
        JSONObject js  = new JSONObject();
        String     url = Constants.WallPaperMine;
        try {
            js.put("m_id", Constants.M_id);
            js.put("page", page);

            switch (type) {
                case "1":
                    LogUtil.e("获取我的壁纸列表" + js);
                    url = Constants.WallPaperMine;
                    js.put("user_id", PreferenceUtil.getUserId(mApplication.getInstance()));
                    break;
                case "2":
                    url = Constants.WallPaperMineWaitingForVerified;
                    LogUtil.e("获取我的待审核壁纸列表" + js);
                    js.put("user_id", PreferenceUtil.getUserId(mApplication.getInstance()));
                    break;
                case "3":
                    url = Constants.WallPaperMyCollection;
                    LogUtil.e("获取我的收藏壁纸列表" + js);
                    js.put("user_id", PreferenceUtil.getUserId(mApplication.getInstance()));
                    break;
                case "4":
                    url = Constants.WallPaperMine;
                    LogUtil.e("获取某人壁纸列表" + js);
                    js.put("user_id", getActivity().getIntent().getStringExtra("id"));
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApisSeUtil.M m = ApisSeUtil.i(js);

        OkGo.post(url).tag(this)
                .params("key", m.K())
                .params("msg", m.M())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        HashMap<String, String> map = AnalyticalJSON.getHashMap(s);
                        if (map != null) {
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
        swipeRefreshLayout.setRefreshing(true);
        getWallPapers();
    }

    @Override
    public void onPause() {
        super.onPause();
        Glide.get(getActivity()).clearMemory();
    }


}
