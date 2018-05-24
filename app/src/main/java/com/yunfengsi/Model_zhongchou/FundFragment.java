package com.yunfengsi.Model_zhongchou;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
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
import com.yunfengsi.View.mHeadLineView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 * 众筹项目入口
 */
public class FundFragment extends BaseSTFragement implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, LoadMoreListView.OnLoadMore {

    private View view;
    private SwipeRefreshLayout swip;
    private LoadMoreListView listView;
    private FundingItemAdapter adapter;//众筹adapter
    private boolean isFirstIn = true;
    private String page = "1";
    private String endPage = "";
    private boolean isRefresh = false;//是否属于下拉刷新操作
    private TextView t;//加载数据的底部提示
    private ProgressBar p;//加载数据的底部进度

    private ImageView tip;
    private mHeadLineView headLineView;
    Timer time = new Timer();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_three, null);
        headLineView = (mHeadLineView) view.findViewById(R.id.headline);
        tip = (ImageView) view.findViewById(R.id.tip);
        tip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swip.post(new Runnable() {
                    @Override
                    public void run() {
                        tip.setVisibility(View.GONE);
                        swip.setRefreshing(true);
                        getData();
                    }
                });
            }
        });
        view.findViewById(R.id.change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject js=new JSONObject();
                try {
                    js.put("m_id",Constants.M_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ApisSeUtil.M m=ApisSeUtil.i(js);
                LogUtil.e("众筹换一换：："+js);
                OkGo.post(Constants.Fund_Change).params("key",m.K())
                        .params("msg",m.M())
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(String s, Call call, Response response) {
                                list = AnalyticalJSON.getList(s, "crowdfunding");
                                if(list!=null){
                                    listView.smoothScrollToPosition(0);
                                    adapter.setList(list);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        });

            }
        });
        return view;
    }

    // TODO: 2017/4/19 重置简繁
    @Override
    protected void resetData() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 退出页面时的设置
     */
    @Override
    public void onDestroy() {
        OkGo.getInstance().cancelTag(TAG);
        time.cancel();
        time=null;
        super.onDestroy();
    }

    /**
     * 初始化控件
     *
     * @param view
     */
    private void initView(final View view) {
        swip = (SwipeRefreshLayout) view.findViewById(R.id.fund_swip);
        listView = (LoadMoreListView) view.findViewById(R.id.fund_listview);
        swip.setOnRefreshListener(this);
        listView.setLoadMoreListen(this);
        listView.footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.findViewById(R.id.change).performClick();
            }
        });
        t = (TextView) (listView.footer.findViewById(R.id.load_more_text));
        p = (ProgressBar) (listView.footer.findViewById(R.id.load_more_bar));
        swip.setColorSchemeResources(R.color.main_color);
        swip.post(new Runnable() {
            @Override
            public void run() {
                if (isFirstIn) {
                    swip.setRefreshing(true);
                    getData();
                    getHeadLine();
                    if (time != null) {
                        time.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                swip.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        getHeadLine();
                                    }
                                });
                            }
                        },0, 30000);
                    }
                }
                isFirstIn = false;
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (isFirstIn)
                initView(view);
        }
    }

    ArrayList<HashMap<String, String>> list;


    /**
     * 获取数据   Constants.FUND_LIST
     */
    private void getData() {
//        if(!Network.HttpTest(mApplication.getInstance())){
//            Toast.makeText(mApplication.getInstance(), "网络连接失败，请下拉刷新", Toast.LENGTH_SHORT).show();
//            if (swip != null && swip.isShown()) swip.setRefreshing(false);
//            return;
//        }




        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject js = new JSONObject();
                    try {
                        js.put("m_id", Constants.M_id);
                        js.put("page", page);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String data = OkGo.post(Constants.FUND_LIST).tag(TAG)
                            .params("key", ApisSeUtil.getKey())
                            .params("msg", ApisSeUtil.getMsg(js))
                            .execute().body().string();
                    if (!TextUtils.isEmpty(data)) {
                        list = AnalyticalJSON.getList(data, "crowdfunding");
                        if (list != null) {
                            if (list.size() != 10) {
                                endPage = page;
                            }


                            listView.post(new Runnable() {
                                @Override
                                public void run() {
//                                    ArrayList<HashMap<String,String>> rs=new ArrayList<>();
//                                    for(int i=0;i<list.size();i++){
//                                        double get=list.get(i).get("sen_money")==null?0:Double.valueOf(list.get(i).get("sen_money"));
//                                        double goal=list.get(i).get("tar_money")==null?0:Double.valueOf(list.get(i).get("tar_money"));
//                                        if(get>=goal){
//                                            rs.add(list.get(i));
//                                            LogUtil.e(get+" ：：删除：： "+goal);
//                                        }
//                                    }
//                                    list.removeAll(rs);
                                    if (adapter == null) {
                                        adapter = new FundingItemAdapter(list, getActivity());
                                        listView.setAdapter(adapter);
                                        if (swip.isRefreshing()) swip.setRefreshing(false);
                                        tip.setVisibility(View.GONE);
                                    } else {
                                        if (isRefresh) {
                                            isRefresh = false;
                                            adapter.setList(list);
                                            adapter.notifyDataSetChanged();
                                            endPage = "";
                                            if (swip.isRefreshing()) swip.setRefreshing(false);
                                            if (t.getText().toString().equals(mApplication.ST("没有更多数据了"))) {
                                                listView.onLoadComplete();
                                                t.setText(mApplication.ST("正在加载...."));
                                                p.setVisibility(View.VISIBLE);
                                            }
                                        } else {
                                            adapter.img_list.addAll(list);
                                            if (endPage.equals(page)) {
                                                t.setText(mApplication.ST("没有更多数据了"));
                                                p.setVisibility(View.GONE);
                                                return;
                                            } else {
                                                p.setVisibility(View.VISIBLE);
                                                t.setText(mApplication.ST("正在加载...."));
                                            }
                                        }
                                        tip.setVisibility(View.GONE);
                                        listView.onLoadComplete();
                                        LogUtil.e(list + "");
                                    }

                                }
                            });
                        } else {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (Network.HttpTest(getActivity())) {
                                        Glide.with(getActivity()).load(R.drawable.load_nothing).override(DimenUtils.dip2px(getActivity(), 150),
                                                DimenUtils.dip2px(getActivity(), 150)).fitCenter().into(tip);
                                    } else {
                                        Glide.with(getActivity()).load(R.drawable.load_neterror).override(DimenUtils.dip2px(getActivity(), 150),
                                                DimenUtils.dip2px(getActivity(), 150)).fitCenter().into(tip);
                                    }
                                    tip.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (Network.HttpTest(getActivity())) {
                                Glide.with(getActivity()).load(R.drawable.load_nothing).override(DimenUtils.dip2px(getActivity(), 150),
                                        DimenUtils.dip2px(getActivity(), 150)).fitCenter().into(tip);
                            } else {
                                Glide.with(getActivity()).load(R.drawable.load_neterror).override(DimenUtils.dip2px(getActivity(), 150),
                                        DimenUtils.dip2px(getActivity(), 150)).fitCenter().into(tip);
                            }
                            tip.setVisibility(View.VISIBLE);
                        }
                    });

                    e.printStackTrace();
                } finally {
                    if (listView != null) {
                        listView.post(new Runnable() {
                            @Override
                            public void run() {
                                if (swip != null && swip.isRefreshing()) swip.setRefreshing(false);
                            }
                        });
                    }
                }
            }
        }).start();
    }

    /*
        获取最新消息
     */
    private void getHeadLine() {
        if (Network.HttpTest(getActivity())) {
            JSONObject js = new JSONObject();
            try {
                js.put("m_id", Constants.M_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ApisSeUtil.M m = ApisSeUtil.i(js);
            OkGo.post(Constants.Fund_HeadLine)
                    .tag(TAG)
                    .params("key", m.K())
                    .params("msg", m.M())
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            ArrayList<HashMap<String, String>> list = AnalyticalJSON.getList_zj(s);
                            if (list != null) {
                                headLineView.onDataArrival(list,mHeadLineView.FUND);
                            }
                        }
                    });
        }
    }

    private static final String TAG = "FundFragment";


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }


    /**
     * 上拉加载
     */
    @Override
    public void loadMore() {
        p.setVisibility(View.GONE);
        t.setText(mApplication.ST("换一换"));
        return;
//        if (!endPage.equals(page)) {
//            page = String.valueOf(Integer.parseInt(page) + 1);
//        } else {
//            p.setVisibility(View.GONE);
//            t.setText(mApplication.ST("没有更多数据了"));
//            return;
//        }
//        getData();
    }

    /**
     * 下拉刷新
     */
    @Override
    public void onRefresh() {
        page = "1";
        isRefresh = true;
        endPage = "";
        getData();
        getHeadLine();
    }
}
