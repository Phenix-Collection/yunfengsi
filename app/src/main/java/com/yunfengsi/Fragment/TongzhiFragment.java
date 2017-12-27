package com.yunfengsi.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import com.yunfengsi.R;
import com.yunfengsi.Setting.Detail_Tongzhi;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.Network;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.ProgressUtil;
import com.yunfengsi.Utils.TimeUtils;
import com.yunfengsi.Utils.ToastUtil;
import com.yunfengsi.Utils.mApplication;
import com.yunfengsi.View.mItemDecoration;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 作者：因陀罗网 on 2017/6/11 11:44
 * 公司：成都因陀罗网络科技有限公司
 */

public class TongzhiFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String pingtuan = "1";
    private static final String guanzhu = "2";
    private SwipeRefreshLayout swip;
    private RecyclerView recyclerView;
    private DingdanAdapter adapter;
    private int page = 1;
    private int endPage = 0;
    private boolean isLoadMore = false;
    private boolean isRefresh = false;
    private static final String TAG = "PinTuanFragment";
    Bundle b;
    private String url;

    //    BroadcastReceiver receive=new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if(intent.getBooleanExtra("tuikuan",false)){
//                onRefresh();
//            }
//        }
//    };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

//        IntentFilter intentFilter=new IntentFilter("tuikuan");
//        getActivity().registerReceiver(receive,intentFilter);
        View view = inflater.inflate(R.layout.fragment_tongzhi, container, false);
        swip = (SwipeRefreshLayout) view.findViewById(R.id.swip);
        swip.setOnRefreshListener(this);
        swip.setColorSchemeResources(R.color.main_color);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new mItemDecoration(getActivity()));
        TextView textView = new TextView(getActivity());
        Drawable d = ContextCompat.getDrawable(getActivity(), R.drawable.load_nothing);
        d.setBounds(0, 0, DimenUtils.dip2px(getActivity(), 150), DimenUtils.dip2px(getActivity(), 150) * d.getIntrinsicHeight() / d.getIntrinsicWidth());
        textView.setCompoundDrawables(null, d, null, null);
        textView.setCompoundDrawablePadding(DimenUtils.dip2px(getActivity(), 10));
        textView.setText(mApplication.ST("暂无通知"));


        textView.setGravity(Gravity.CENTER);
        ViewGroup.MarginLayoutParams vl = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        vl.topMargin = DimenUtils.dip2px(getActivity(), 180);
        textView.setLayoutParams(vl);
        adapter = new DingdanAdapter(getActivity(), new ArrayList<HashMap<String, String>>());
        adapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
        adapter.openLoadMore(10, true);
        adapter.setEmptyView(textView);
        adapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
                    @Override
                    public void onItemClick(View view, int i) {
//                        Intent intent = new Intent(getActivity(), Pintuan_Detail.class);
//                        intent.putExtra("id", adapter.getData().get(i).get("id"));
//                        startActivity(intent);
                    }
                });
        recyclerView.setAdapter(adapter);
        swip.post(new Runnable() {
            @Override
            public void run() {
                swip.setRefreshing(true);
                onRefresh();
            }
        });

        return view;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        getActivity().unregisterReceiver(receive);
    }

    private void getData() {
        if (!Network.HttpTest(getActivity())) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject js = new JSONObject();
                    js.put("user_id", PreferenceUtil.getUserIncetance(getActivity()).getString("user_id", ""));
                    js.put("m_id", Constants.M_id);
                    js.put("page", page);
                    ApisSeUtil.M m = ApisSeUtil.i(js);
                    String data = OkGo.post(Constants.tongzhi_center).tag(TAG).params("key", m.K())
                            .params("msg", m.M()).execute().body().string();
                    if (!TextUtils.isEmpty(data)) {
                        final ArrayList<HashMap<String, String>> list = AnalyticalJSON.getList_zj(data);
                        if (list != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.setNewData(list);
                                    if (isRefresh) {
                                        adapter.setNewData(list);
                                        isRefresh = false;
                                        swip.setRefreshing(false);
                                    } else if (isLoadMore) {
                                        isLoadMore = false;
                                        if (list.size() < 10) {
                                            ToastUtil.showToastShort(mApplication.ST("已经没有更多通知啦"), Gravity.CENTER);
//                                endPage = page;
                                            adapter.notifyDataChangedAfterLoadMore(list, false);
                                        } else {
                                            adapter.notifyDataChangedAfterLoadMore(list, true);
                                        }
                                    }

                                    swip.setRefreshing(false);
                                }
                            });
                        } else {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    swip.setRefreshing(false);
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onRefresh() {
        page = 1;
        endPage = -1;
        isRefresh = true;
        adapter.openLoadMore(10, true);
        getData();
    }

    public class DingdanAdapter extends BaseQuickAdapter<HashMap<String, String>> {
        private Activity a;

        public DingdanAdapter(Activity activity, ArrayList<HashMap<String, String>> data) {
            super(R.layout.item_tongzhi, data);
            WeakReference<Activity> w = new WeakReference<Activity>(activity);
            a = w.get();
        }

        @Override
        protected void convert(BaseViewHolder holder, final HashMap<String, String> map) {
            holder.setText(R.id.title,mApplication.ST(map.get("title")))
                    .setText(R.id.pet_name,mApplication.ST(map.get("pet_name")))
                    .setText(R.id.time,TimeUtils.getTrueTimeStr(map.get("time")));
            Glide.with(getActivity()).load(map.get("user_image"))
                    .override(DimenUtils.dip2px(getActivity(),60),DimenUtils.dip2px(getActivity(),60))
                    .into((ImageView) holder.getView(R.id.head));
            if(map.get("status").equals("2")){
                holder.getView(R.id.content).setSelected(true);
                ((TextView) holder.getView(R.id.pet_name)).setTextColor(Color.parseColor("#999999"));
                ((TextView) holder.getView(R.id.title)).setTextColor(Color.parseColor("#999999"));
            }else{
                holder.getView(R.id.content).setSelected(false);
                ((TextView) holder.getView(R.id.pet_name)).setTextColor(Color.parseColor("#000000"));
                ((TextView) holder.getView(R.id.title)).setTextColor(Color.parseColor("#666666"));
            }
            if(map.get("user_id").equals("0")){
                holder.setText(R.id.type, mApplication.ST("公告"));
                holder.setVisible(R.id.cancle,false);
            }else{
                holder.setText(R.id.type,mApplication.ST("通知"));
                holder.setVisible(R.id.cancle,true);
            }
            holder.setOnClickListener(R.id.content, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(getActivity(), Detail_Tongzhi.class);
                    intent.putExtra("id",map.get("id"));
                    intent.putExtra("user_id",map.get("user_id"));
                    startActivity(intent);
                }
            });
            holder.setOnClickListener(R.id.cancle, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(Network.HttpTest(getActivity())){
                        JSONObject js=new JSONObject();
                        try {
                            js.put("m_id",Constants.M_id);
                            js.put("id",map.get("id"));
                            js.put("user_id",map.get("user_id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ApisSeUtil.M m=ApisSeUtil.i(js);
                        OkGo.post(Constants.tongzhi_Delete)
                                .params("key",m.K())
                                .params("msg",m.M())
                                .execute(new AbsCallback<HashMap<String,String>>() {
                                    @Override
                                    public void onSuccess(HashMap<String, String> map1, Call call, Response response) {
                                        if("000".equals(map1.get("code"))){
                                            ToastUtil.showToastShort("删除成功");
                                            notifyItemRemoved(getData().indexOf(map));
                                            getData().remove(map);
                                        }
                                    }

                                    @Override
                                    public HashMap<String, String> convertSuccess(Response response) throws Exception {
                                        return AnalyticalJSON.getHashMap(response.body().string());
                                    }

                                    @Override
                                    public void onBefore(BaseRequest request) {
                                        super.onBefore(request);
                                        ProgressUtil.show(getActivity(),"","正在删除");
                                    }

                                    @Override
                                    public void onAfter(HashMap<String, String> stringStringHashMap, Exception e) {
                                        super.onAfter(stringStringHashMap, e);
                                        ProgressUtil.dismiss();
                                    }
                                });
                    }
                }
            });
        }
    }
}
