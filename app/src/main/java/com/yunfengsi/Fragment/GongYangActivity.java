package com.yunfengsi.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
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
import com.yunfengsi.Utils.ImageUtil;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.Network;
import com.yunfengsi.Utils.mApplication;
import com.yunfengsi.View.mHeadLineView;
import com.yunfengsi.XuanzheActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import okhttp3.Call;
import okhttp3.Response;

public class GongYangActivity extends BaseSTFragement implements SwipeRefreshLayout.OnRefreshListener {
    private GridView mlistview;
    private List<HashMap<String, String>> list;
    private Hoder hoder;
    private SwipeRefreshLayout swip;
    private boolean isRefresh;
    private String page = "1";
    private String endPage = "";
    private boolean isFirstIn = true;
    private View view;
    private static final String TAG = "shopd";

    private ImageView tip;
    private mHeadLineView headLineView;
    Timer time = new Timer();
    private BaseAdapter myadapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                hoder = new Hoder();
                view = LayoutInflater.from(mApplication.getInstance()).inflate(R.layout.list_itme2, null);
                hoder.imageview = (AvatarImageView) view.findViewById(R.id.list_itme_imageview);
                hoder.tvtitle = (TextView) view.findViewById(R.id.list_itme_name);
                hoder.tvmoney = (TextView) view.findViewById(R.id.list_itme_qian);
                view.setTag(hoder);
            } else {
                hoder = (Hoder) view.getTag();
            }
            Glide.with(getActivity()).load(list.get(i).get("image")).override(DimenUtils.dip2px(getActivity(), 100), DimenUtils.dip2px(getActivity(), 100))
                    .centerCrop().placeholder(R.drawable.placeholder_grey).into(hoder.imageview);
            hoder.tvtitle.setText(mApplication.ST(list.get(i).get("product")));
            hoder.tvmoney.setText("￥" + list.get(i).get("money1"));
            hoder.tvtitle.setTag(list.get(i).get("id"));
            hoder.tvmoney.setTag(list.get(i).get("image"));
            if (i >= list.size() - 1 && (i + 1) % 15 == 0) {
                getData();
                page = String.valueOf(Integer.valueOf(page) + 1);
            }
            return view;
        }
    };
    private AdapterView.OnItemClickListener onitemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if (i != list.size()) {
                Intent intent = new Intent(getActivity(), XuanzheActivity.class);
                TextView title = (TextView) view.findViewById(R.id.list_itme_name);
                TextView money = (TextView) view.findViewById(R.id.list_itme_qian);
                ImageView image = (ImageView) view.findViewById(R.id.list_itme_imageview);
                intent.putExtra("id", title.getTag().toString());
                intent.putExtra("title", title.getText().toString());
                intent.putExtra("money", money.getText().toString());

                image.setDrawingCacheEnabled(true);
                Bitmap b = image.getDrawingCache();
                if (b != null) {
                    intent.putExtra("head", ImageUtil.Bitmap2StrByBase64(b));
                } else {
                    intent.putExtra("head_url", money.getTag().toString());
                }
                image.setDrawingCacheEnabled(false);
                startActivity(intent);
            }
        }
    };

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (isFirstIn) {
                swip = (SwipeRefreshLayout) view.findViewById(R.id.swip);
                swip.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light,
                        android.R.color.holo_green_light);
                swip.setOnRefreshListener(this);
                swip.post(new Runnable() {
                    @Override
                    public void run() {
                        list = new ArrayList<>();
                        swip.setRefreshing(true);
                        mlistview = (GridView) view.findViewById(R.id.listview);
                        mlistview.setAdapter(myadapter);
                        mlistview.setOnItemClickListener(onitemClickListener);
                        getData();
                        getHeadLine();
                        if (time != null) {

                            time.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    getHeadLine();
                                }
                            }, 0,30000);
                        }
                    }
                });
                isFirstIn = false;
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.activity_gong_yang, container, false);
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
        return view;
    }

    // TODO: 2017/4/19 重置简繁
    @Override
    protected void resetData() {
        if (myadapter != null) {
            myadapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        time.cancel();
        time=null;
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
            OkGo.post(Constants.GongYang_HeadLine)
                    .tag(TAG)
                    .params("key", m.K())
                    .params("msg", m.M())
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            ArrayList<HashMap<String, String>> list = AnalyticalJSON.getList_zj(s);
                            LogUtil.e("供养最新消息::::"+list);
                            if (list != null) {
                                headLineView.onDataArrival(list, mHeadLineView.GONGYANG);
                            }
                        }
                    });
        }
    }
    private void getData() {
//        if(!Network.HttpTest(mApplication.getInstance())){
//            Toast.makeText(mApplication.getInstance(), "网络连接失败，请下拉刷新", Toast.LENGTH_SHORT).show();
//            if (null!=swip&&swip.isShown()) swip.setRefreshing(false);
//            return;
//        }


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject js=new JSONObject();
                    try {
                        js.put("m_id", Constants.M_id);
                        js.put("page", page);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String data = OkGo.post(Constants.ShangPin_list_Ip)
                            .params("key", ApisSeUtil.getKey())
                            .params("msg", ApisSeUtil.getMsg(js)).tag(TAG).execute().body().string();
                    if (!TextUtils.isEmpty(data)) {
                        final List<HashMap<String, String>> list1 = AnalyticalJSON.getList(data, "shop");
                        if (list1 != null) {
                            if (list1.size() != 15) endPage = page;
                            mlistview.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (list.size() == 0) {
                                        list = list1;
                                        mlistview.setAdapter(myadapter);
                                        if (swip.isRefreshing()) swip.setRefreshing(false);
                                    } else {
                                        if (isRefresh) {
                                            isRefresh = false;
                                            list.clear();
                                            list = list1;
                                            myadapter.notifyDataSetChanged();
                                            if (swip.isShown()) swip.setRefreshing(false);
                                        } else {
                                            list.addAll(list1);
                                            myadapter.notifyDataSetChanged();
                                        }
                                    }
                                    tip.setVisibility(View.GONE);
                                }
                            });
                        } else {
                            if (getActivity() != null) {
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
                                        if (null != swip && swip.isRefreshing())
                                            swip.setRefreshing(false);
                                    }
                                });
                            }

                        }
                    } else {
                        if (getActivity() != null) {
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
                                    if (null != swip && swip.isRefreshing())
                                        swip.setRefreshing(false);
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (null != swip && swip.isRefreshing()) swip.setRefreshing(false);
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
                } finally {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (null != swip && swip.isRefreshing()) swip.setRefreshing(false);
                            }
                        });
                    }
                }
            }
        }).start();
    }


    @Override
    public void onRefresh() {
        page = "1";
        isRefresh = true;
        endPage = "";
        getData();
        getHeadLine();
    }


    static class Hoder {
        AvatarImageView imageview;
        TextView tvtitle;
        TextView tvmoney;

    }
}
