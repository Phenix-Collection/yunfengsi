package com.yunfengsi.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.yunfengsi.Adapter.ziXun_List_Adapter;
import com.yunfengsi.BaseSTFragement;
import com.yunfengsi.R;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.Network;
import com.yunfengsi.Utils.mApplication;
import com.yunfengsi.View.LoadMoreListView;
import com.yunfengsi.View.mAudioManager;
import com.yunfengsi.ZiXun_Detail;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;


/**
 * Created by Administrator on 2016/6/1.
 */
public class ZiXun extends BaseSTFragement implements OnClickListener, OnRefreshListener, LoadMoreListView.OnLoadMore {
    private static final String TAG = "ZiXun";
    private LoadMoreListView ziXunListView;
    public List<HashMap<String, String>> dataList;
    private String page = "1";
    private String endPage = "";
    private ziXun_List_Adapter adapter;
    private static final String ZX_list = "zixun_list";
    private SwipeRefreshLayout refreshLayout;
    private boolean isRefresh;
    private boolean isFirstIn = true;
    private View view;

    private ImageView tip;
//    private ValueAnimator valueAnimator;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.zixun_list, container, false);

        if (isFirstIn) {
            initView(view);
            refreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    refreshLayout.setRefreshing(true);
                    loadData();

                }
            });
            isFirstIn = false;
        }

        return view;
    }



//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        valueAnimator.cancel();
//        valueAnimator=null;
//    }



    // TODO: 2017/4/19 重置简繁
    @Override
    protected void resetData() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }


    public void initView(View view) {
        tip = (ImageView) view.findViewById(R.id.tip);
        tip.setOnClickListener(this);
        adapter = new ziXun_List_Adapter(getActivity());
        dataList = new ArrayList<>();
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.zixun_refresh);
        refreshLayout.setOnRefreshListener(this);
        ziXunListView = (LoadMoreListView) view.findViewById(R.id.zixun_list);
        ziXunListView.setLoadMoreListen(this);
        refreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        ziXunListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != parent.getCount() - 1) {
                    Intent intent = new Intent(mApplication.getInstance(), ZiXun_Detail.class);
                    intent.putExtra("id", String.valueOf(parent.getItemIdAtPosition(position)));
                    if (null != view.findViewById(R.id.zixun_video_title)) {
                        String videourl = view.findViewById(R.id.zixun_video_title).getTag().toString();
                        String title = ((TextView) view.findViewById(R.id.zixun_video_title)).getText().toString();
                        String image = view.findViewById(R.id.zixun_video_Num).getTag().toString();
                        if (!videourl.equals("")) {
                            intent.putExtra("video_url", videourl);
                            intent.putExtra("title", title);
                            intent.putExtra("image", image);
                            Log.w(TAG, "onItemClick: title-=-=" + title + " url=-=" + videourl + " image-=-=" + image);
                        }
                    }
                    if (null != view.findViewById(R.id.zixun_video_user)) {
                        String active = view.findViewById(R.id.zixun_video_user).getTag().toString();
                        if (!active.equals("")) {
                            intent.putExtra("active_url", active);
                        }
                    }
//                    if (null != view.findViewById(R.id.zixun_item_sourse)) {
//                        String active = view.findViewById(R.id.zixun_item_sourse).getTag().toString();
//                        if (!active.equals("")) {
//                            intent.putExtra("active_url", active);
//                        }
//                    }

                    startActivity(intent);
                }
            }
        });
    }

    public void loadData() {//加载数据
        if (!Network.HttpTest(getActivity())) {
            Toast.makeText(mApplication.getInstance(), "无网络连接", Toast.LENGTH_SHORT).show();
        }
        getData4ZXandCache();
    }

    @Override
    public void onRefresh() {
        isRefresh = true;
        page = "1";
        endPage = "";
        getData4ZXandCache();

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (this != null && !isVisibleToUser) {
            JCVideoPlayer.releaseAllVideos();
            mAudioManager.release();
        }

    }

    @Override
    public void loadMore() {
        if (!endPage.equals(page)) {
            page = String.valueOf(Integer.parseInt(page) + 1);
        } else {
            final ProgressBar p = (ProgressBar) (ziXunListView.footer.findViewById(R.id.load_more_bar));
            final TextView t = (TextView) (ziXunListView.footer.findViewById(R.id.load_more_text));
            p.setVisibility(View.GONE);
            t.setText("没有更多数据了");
            return;
        }
        getData4ZXandCache();
    }

    private void getData4ZXandCache() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String data1 = null;
                try {
                    JSONObject js = new JSONObject();
                    try {
                        js.put("m_id", Constants.M_id);
                        js.put("page", page);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m = ApisSeUtil.i(js);
                    LogUtil.e("首页资讯：：" + js);
                    ////上拉加载是 Integer.valueof(page)++;
                    data1 = OkGo.post(Constants.ZiXun_total_Ip).tag(TAG)
                            .params("key", m.K())
                            .params("msg", m.M()).execute().body().string();
                    if (!data1.equals("")) {
                        if (AnalyticalJSON.getList(data1, "news") == null) {
                            //无直播信息
                            mZiXunhandler.sendEmptyMessage(Constants.LoadFail);
                        } else {
                            if (AnalyticalJSON.getList(data1, "news").size() != 10) {
                                endPage = page;
                            }
                            final TextView t = (TextView) (ziXunListView.footer.findViewById(R.id.load_more_text));
                            final ProgressBar p = (ProgressBar) (ziXunListView.footer.findViewById(R.id.load_more_bar));
                            dataList = AnalyticalJSON.getList(data1, "news");
                            if (0 == adapter.mlist.size()) {
                                if (getActivity() != null) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            adapter.addList(dataList);
                                            ziXunListView.setAdapter(adapter);
                                            if (refreshLayout.isRefreshing())
                                                refreshLayout.setRefreshing(false);
                                            tip.setVisibility(View.GONE);
                                        }
                                    });
                                }
                            } else {
                                if (isRefresh) {
                                    if (getActivity() != null) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                isRefresh = false;
                                                endPage = "";
                                                adapter.addList(dataList);
                                                if (refreshLayout.isRefreshing())
                                                    refreshLayout.setRefreshing(false);
                                                if (t.getText().toString().equals("没有更多数据了")) {
                                                    p.setVisibility(View.VISIBLE);
                                                    t.setText("正在加载....");
                                                }
                                                adapter.notifyDataSetChanged();
                                                ziXunListView.onLoadComplete();
                                                tip.setVisibility(View.GONE);
                                            }
                                        });
                                    }
                                } else {
                                    if (getActivity() != null) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                adapter.mlist.addAll(dataList);
                                                adapter.notifyDataSetChanged();
                                                if (endPage.equals(page)) {
                                                    p.setVisibility(View.GONE);
                                                    t.setText("没有更多数据了");
                                                    return;
                                                } else {
                                                    p.setVisibility(View.VISIBLE);
                                                    t.setText("正在加载....");
                                                    ziXunListView.onLoadComplete();
                                                }
                                                tip.setVisibility(View.GONE);
                                            }
                                        });
                                    }

                                }


                            }

                        }
                    } else {
                        mZiXunhandler.sendEmptyMessage(Constants.LoadFail);
                    }
                } catch (Exception e) {
                    mZiXunhandler.sendEmptyMessage(Constants.LoadFail);
                } finally {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (refreshLayout != null && refreshLayout.isRefreshing())
                                    refreshLayout.setRefreshing(false);
                            }
                        });
                    }

                }


            }
        }).start();
    }


    Handler mZiXunhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {//通知listview更新界面
            super.handleMessage(msg);

            switch (msg.what) {

                case Constants.LoadFail:
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (Network.HttpTest(getActivity())) {
                                    Glide.with(getActivity()).load(R.drawable.load_nothing).override(DimenUtils.dip2px(getActivity(), 150), DimenUtils.dip2px(getActivity(), 150))
                                            .fitCenter().into(tip);
                                } else {
                                    Glide.with(getActivity()).load(R.drawable.load_neterror).override(DimenUtils.dip2px(getActivity(), 150), DimenUtils.dip2px(getActivity(), 150))
                                            .fitCenter().into(tip);
                                }
                                tip.setVisibility(View.VISIBLE);
                                if (refreshLayout.isShown()) refreshLayout.setRefreshing(false);
                                if (ziXunListView.footer.isShown()) ziXunListView.onLoadComplete();
                            }
                        });
                    }

                    break;

            }
        }
    };

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.tip:
                v.setVisibility(View.GONE);
                refreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(true);
                        getData4ZXandCache();
                    }
                });
                break;


        }
    }


}
