package com.qianfujiaoyu.Model_Order;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.qianfujiaoyu.R;
import com.qianfujiaoyu.Utils.AnalyticalJSON;
import com.qianfujiaoyu.Utils.ApisSeUtil;
import com.qianfujiaoyu.Utils.Constants;
import com.qianfujiaoyu.Utils.DimenUtils;
import com.qianfujiaoyu.Utils.Network;
import com.qianfujiaoyu.Utils.PreferenceUtil;
import com.qianfujiaoyu.Utils.ProgressUtil;
import com.qianfujiaoyu.Utils.TimeUtils;
import com.qianfujiaoyu.Utils.ToastUtil;
import com.qianfujiaoyu.View.mItemDecoration;
import com.qianfujiaoyu.View.mRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

import static com.qianfujiaoyu.R.id.num;


/**
 * 作者：因陀罗网 on 2017/6/11 11:44
 * 公司：成都因陀罗网络科技有限公司
 */

public class DingDanFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
//    private static final String pingtuan = "1";
//    private static final String fahuo = "2";
//    private static final String tuikuan = "3";
    private SwipeRefreshLayout swip;
    private RecyclerView recyclerView;
    private DingdanAdapter adapter;
    private int page = 1;
    private int endPage = 0;
    private boolean isLoadMore = false;
    private boolean isRefresh = false;
    private static final String TAG = "DingDanFragment";
    Bundle b;

        BroadcastReceiver receive=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

                onRefresh();

        }
    };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        b = getArguments();
//        if (b == null) {//个人
//
//        } else {
//            switch (b.getString("tag")) {
//                case pingtuan:
//                    break;
//                case fahuo:
//                    break;
//                case tuikuan:
//                    break;
//            }
//            ;
//        }
        IntentFilter intentFilter=new IntentFilter("tuikuan");
        getActivity().registerReceiver(receive,intentFilter);
        View view = inflater.inflate(R.layout.fragment_dingdan, container, false);
        swip = (SwipeRefreshLayout) view.findViewById(R.id.swip);
        swip.setOnRefreshListener(this);
        swip.setColorSchemeResources(R.color.main_color);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        TextView textView = new TextView(getActivity());
        Drawable d = ContextCompat.getDrawable(getActivity(), R.drawable.load_nothing);
        d.setBounds(0, 0, DimenUtils.dip2px(getActivity(), 150), DimenUtils.dip2px(getActivity(), 150) * d.getIntrinsicHeight() / d.getIntrinsicWidth());
        textView.setCompoundDrawables(null, d, null, null);
        textView.setCompoundDrawablePadding(DimenUtils.dip2px(getActivity(), 10));
        textView.setText("暂无订单记录");
        textView.setGravity(Gravity.CENTER);
        ViewGroup.MarginLayoutParams vl = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        vl.topMargin = DimenUtils.dip2px(getActivity(), 180);
        textView.setLayoutParams(vl);
        adapter = new DingdanAdapter(getActivity(), new ArrayList<HashMap<String, String>>());
        adapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
        adapter.openLoadMore(10, true);
        adapter.setEmptyView(textView);
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
        getActivity().unregisterReceiver(receive);
    }

    private void getData() {
        if (!Network.HttpTest(getActivity())) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = Constants.MyOrders;
                    JSONObject js = new JSONObject();
//                    if (!PreferenceUtil.getUserIncetance(getActivity()).getString("role", "").equals("3")) {
//                        js.put("user_id", PreferenceUtil.getUserIncetance(getActivity()).getString("user_id", ""));
//                        url = Constants.GoodsDingdan;
//                    } else {
//                        url = Constants.GoodsAdminGroup;
//                        js.put("type", b.getString("tag"));
//                    }
                    js.put("user_id",PreferenceUtil.getUserIncetance(getActivity()).getString("user_id",""));
                    js.put("page", page);
                    js.put("m_id", Constants.M_id);
                    ApisSeUtil.M m = ApisSeUtil.i(js);
                    String data = OkGo.post(url).tag(TAG).params("key", m.K())
                            .params("msg", m.M()).execute().body().string();
                    if (!TextUtils.isEmpty(data)) {
                        Log.w(TAG, "run: " + data);
                        final ArrayList<HashMap<String, String>> list = AnalyticalJSON.getList_zj(data);
                        if (list != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (isRefresh) {
                                        adapter.setNewData(list);
                                        isRefresh = false;
                                        swip.setRefreshing(false);
                                    } else if (isLoadMore) {
                                        isLoadMore = false;
                                        if (list.size() < 10) {
                                            ToastUtil.showToastShort("已经没有更多数据啦", Gravity.CENTER);
//                                endPage = page;
                                            adapter.notifyDataChangedAfterLoadMore(list, false);
                                        } else {
                                            adapter.notifyDataChangedAfterLoadMore(list, true);
                                        }
                                    }

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

    public static class DingdanAdapter extends BaseQuickAdapter<HashMap<String, String>> {
        private Activity a;

        // TODO: 2017/6/22 item适配器
        public static class itemAdapter extends BaseQuickAdapter<HashMap<String, String>> {
            //type 1:黑色  2.  红色   3  邀约
            private int type;

            public itemAdapter(int type, List<HashMap<String, String>> data) {
                super(R.layout.item_dingdan_item_text_text, data);
                this.type = type;
            }

            @Override
            protected void convert(BaseViewHolder holder, HashMap<String, String> map) {
                TextView title = holder.getView(R.id.title);
                if (type == 1) {
                    title.setText(map.get("title"));
                    title.setTextColor(Color.BLACK);
                } else if (type == 2) {
                    title.setText(map.get("sut_title"));
                    title.setTextColor(Color.parseColor("#F37885"));
                } else if (type == 3) {
                    title.setText(map.get("sut_title"));
                    title.setTextColor(Color.parseColor("#FF7F00"));
                }
                ((TextView) holder.getView(num)).setText("×" + map.get("num"));
            }
        }

        public DingdanAdapter(Activity activity, ArrayList<HashMap<String, String>> data) {
            super(R.layout.my_orders_item, data);
            WeakReference<Activity> w = new WeakReference<Activity>(activity);
            a = w.get();
        }

        @Override
        protected void convert(final BaseViewHolder holder, final HashMap<String, String> map) {
            holder.setText(R.id.time, TimeUtils.getTrueTimeStr(map.get("end_time")));

            if (map.get("sut_type").equals("7")) {
                PuTong(holder, map);
            } else {
                holder.setText(R.id.status, "已支付");
                if (map.get("sut_type").equals("12")) {//应邀金
                    YingYaoJin(holder, map);
                } else {
                    pintuan(holder, map);
                }
            }
        }

        private void pintuan(final BaseViewHolder holder, final HashMap<String, String> map) {
            holder.getView(R.id.tuikuan).setVisibility(View.INVISIBLE);
            holder.getView(R.id.pintuan).setVisibility(View.VISIBLE);
            HashMap<String, String> m1 = new HashMap<>();
            m1.put("sut_title", map.get("sut_title"));
            m1.put("num", map.get("num"));
            m1.put("snr", map.get("snr"));
            ArrayList<HashMap<String, String>> l = new ArrayList<>();
            l.add(m1);
            ((mRecyclerView) holder.getView(R.id.listview)).setLayoutManager(new LinearLayoutManager(a));
            ((mRecyclerView) holder.getView(R.id.listview)).addItemDecoration(new mItemDecoration(a));
            ((mRecyclerView) holder.getView(R.id.listview)).setAdapter
                    (new itemAdapter(2, l));
            int num = 0;
            for (HashMap<String, String> m : l) {
                num += Integer.valueOf(m.get("num"));
            }
            SpannableString ss = new SpannableString("共" + num + "件商品,实付￥" + map.get("money"));
            ss.setSpan(new ForegroundColorSpan(Color.BLACK), (num + "").length() + 7, ss.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            holder.setText(R.id.numMoney, ss);
            ((itemAdapter) ((mRecyclerView) holder.getView(R.id.listview)).getAdapter()).setOnRecyclerViewItemClickListener(new OnRecyclerViewItemClickListener() {
                @Override
                public void onItemClick(View view, int i) {
                    String snr = map.get("snr");
                    Intent intent = new Intent(a, MyZhiFuDetail.class);
                    if (PreferenceUtil.getUserIncetance(a).getString("role", "").equals("3")) {
                        intent.putExtra("id", map.get("id"));
                    }
                    intent.putExtra("status", ((TextView) holder.getView(R.id.pintuan)).getText().toString());
                    intent.putExtra("snr", snr);
//                    intent.putExtra("KT", map.get("number").equals("") ? false : true);?
                    a.startActivity(intent);
                }
            });
            if (PreferenceUtil.getUserIncetance(a).getString("role", "").equals("3")) {

                if ("1".equals(map.get("status"))) {//等待拼团
                    long endtime = TimeUtils.dataOne(map.get("end_time"));
                    if (System.currentTimeMillis() - endtime >= (24 * 60 * 60 * 2 * 1000)) {
                        holder.setText(R.id.status, "拼团失败");
                        ((TextView) holder.getView(R.id.pintuan)).setText("退款");
                        ((TextView) holder.getView(R.id.pintuan)).setEnabled(true);
                    } else {
                        ((TextView) holder.getView(R.id.pintuan)).setText("完成拼团");
                        ((TextView) holder.getView(R.id.pintuan)).setEnabled(true);
                    }

                } else if ("2".equals(map.get("status"))) {//已完成拼团
                    if (map.get("delivery").equals("1")) {
                        ((TextView) holder.getView(R.id.pintuan)).setText("发货");
                        ((TextView) holder.getView(R.id.pintuan)).setEnabled(true);
                    } else if (map.get("delivery").equals("2")) {
                        ((TextView) holder.getView(R.id.pintuan)).setText("已发货");
                        ((TextView) holder.getView(R.id.pintuan)).setEnabled(false);
                    }
                } else if ("3".equals(map.get("status"))) {//已申请退款
                    ((TextView) holder.getView(R.id.pintuan)).setText("已退款");
                    ((TextView) holder.getView(R.id.pintuan)).setEnabled(false);
                }

            } else {
                if ("1".equals(map.get("status"))) {
                    long endtime = TimeUtils.dataOne(map.get("end_time"));
                    if (System.currentTimeMillis() - endtime >= (24 * 60 * 60 * 2 * 1000)) {
                        holder.setText(R.id.status, "拼团失败");
                        ((TextView) holder.getView(R.id.pintuan)).setText("退款");
                        ((TextView) holder.getView(R.id.pintuan)).setEnabled(true);
                    } else {
                        ((TextView) holder.getView(R.id.pintuan)).setText("等待拼团");
                        ((TextView) holder.getView(R.id.pintuan)).setEnabled(false);
                    }
                } else if ("2".equals(map.get("status"))) {
                    if (map.get("delivery").equals("1")) {
                        ((TextView) holder.getView(R.id.pintuan)).setText("等待发货");
                        ((TextView) holder.getView(R.id.pintuan)).setEnabled(false);
                    } else if (map.get("delivery").equals("2")) {
                        ((TextView) holder.getView(R.id.pintuan)).setText("已发货");
                        ((TextView) holder.getView(R.id.pintuan)).setEnabled(false);
                    }

                } else if ("3".equals(map.get("status"))) {
                    ((TextView) holder.getView(R.id.pintuan)).setText("已退款");
                    ((TextView) holder.getView(R.id.pintuan)).setEnabled(false);
                }

            }


            holder.setOnClickListener(R.id.pintuan, new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    if (!Network.HttpTest(a)) {
                        return;
                    }
                    if ("1".equals(map.get("status"))) {//等待拼团

                        if (((TextView) view).getText().toString().equals("退款")) {//拼团时间已过
                            AlertDialog.Builder builder = new AlertDialog.Builder(a);
                            builder.setPositiveButton("退款", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    TuiKuan(view);
                                }
                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).setMessage("需要立即退款吗？退款成功后会在0-3个工作日内原路返回").create().show();

                        } else {//管理员凑团
                            JSONObject js = new JSONObject();
                            try {
                                js.put("m_id", Constants.M_id);
                                js.put("number", map.get("number"));
                                js.put("snr", map.get("snr"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ApisSeUtil.M m = ApisSeUtil.i(js);
//                            OkGo.post(Constants.GoodsAdminSave).params("key", m.K())
//                                    .params("msg", m.M()).execute(new StringCallback() {
//
//                                @Override
//                                public void onBefore(BaseRequest request) {
//                                    super.onBefore(request);
//                                    ProgressUtil.show(a, "", "正在凑单，请稍后");
//                                }
//
//                                @Override
//                                public void onSuccess(String s, Call call, Response response) {
//                                    HashMap<String, String> map = AnalyticalJSON.getHashMap(s);
//                                    if (map != null) {
//                                        if ("000".equals(map.get("code"))) {
//                                            ToastUtil.showToastShort("凑单成功");
//                                            ((TextView) view).setText("发货");
//                                            map.put("status", "2");
//                                            Intent intent=new Intent("tuikuan");
//                                            a.sendBroadcast(intent);
//                                        } else {
//                                            ToastUtil.showToastShort("凑单失败，请稍后重试");
//                                        }
//                                    } else {
//                                        ToastUtil.showToastShort("凑单失败，请稍后重试");
//                                    }
//                                }
//
//                                @Override
//                                public void onAfter(@Nullable String s, @Nullable Exception e) {
//                                    super.onAfter(s, e);
//                                    ProgressUtil.dismiss();
//                                }
//
//
//                            });
                        }

                    } else if ("2".equals(map.get("status")) && "1".equals(map.get("delivery"))) {//发货
                        JSONObject js = new JSONObject();
                        try {
                            js.put("id", map.get("id"));
                            js.put("snr", map.get("snr"));
                            js.put("type",2);
                            js.put("m_id", Constants.M_id);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ApisSeUtil.M m = ApisSeUtil.i(js);
//                        OkHttpUtils.post(Constants.GoodsAdminfahuo)
//                                .params("key", m.K())
//                                .params("msg", m.M())
//                                .execute(new StringCallback() {
//
//
//                                    @Override
//                                    public void onBefore(BaseRequest request) {
//                                        super.onBefore(request);
//                                        ProgressUtil.show(a, "", "请稍等");
//                                    }
//
//                                    @Override
//                                    public void onSuccess(String s, Call call, Response response) {
//                                        HashMap<String, String> m = AnalyticalJSON.getHashMap(s);
//                                        if (m != null) {
//                                            if ("000".equals(m.get("code"))) {
//                                                ToastUtil.showToastShort("该订单已发货");
//                                                map.put("delivery", "2");
//                                                ((TextView) view).setText("已发货");
//                                                view.setEnabled(false);
//                                                Intent intent=new Intent("tuikuan");
//                                                a.sendBroadcast(intent);
//                                            }
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onAfter(@Nullable String s, @Nullable Exception e) {
//                                        super.onAfter(s, e);
//                                        ProgressUtil.dismiss();
//                                    }
//
//
//                                });
                    }
                }

                private void TuiKuan(final View view) {
                    JSONObject js = new JSONObject();
                    try {
                        js.put("m_id", Constants.M_id);
                        js.put("user_id", PreferenceUtil.getUserIncetance(a).getString("role", "").equals("3") ?
                                map.get("user_id") : PreferenceUtil.getUserIncetance(a).getString("user_id", ""));
                        js.put("shop_id", map.get("shop_id"));
                        js.put("number", map.get("number"));
                        js.put("id", map.get("id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m = ApisSeUtil.i(js);
//                    OkHttpUtils.post(Constants.PTtuikuan)
//                            .params("key", m.K())
//                            .params("msg", m.M())
//                            .execute(new StringCallback() {
//
//                                @Override
//                                public void onAfter(@Nullable String s, @Nullable Exception e) {
//                                    super.onAfter(s, e);
//                                    ProgressUtil.dismiss();
//                                }
//
//
//                                @Override
//                                public void onBefore(BaseRequest request) {
//                                    super.onBefore(request);
//                                    ProgressUtil.show(a, "", "正在提交退款申请，请稍等");
//                                }
//
//                                @Override
//                                public void onSuccess(String s, Call call, Response response) {
//                                    HashMap<String, String> map1 = AnalyticalJSON.getHashMap(s);
//                                    if (map1 != null) {
//                                        if ("000".equals(map1.get("code"))) {
//                                            ToastUtil.showToastShort("退款申请已提交，请耐心等待");
//                                            map.put("status", "3");
//                                            ((TextView) view).setText("已退款");
//                                            view.setEnabled(false);
//                                            Intent intent=new Intent("tuikuan");
//                                            a.sendBroadcast(intent);
//                                        } else if ("444".equals(map1.get("code"))) {
//                                            ToastUtil.showToastShort("退款申请失败，请稍后重试或联系客服");
//                                        }
//                                    }
//                                }
//                            });
                }
            });
        }

        private void YingYaoJin(BaseViewHolder holder, HashMap<String, String> map) {
            holder.getView(R.id.tuikuan).setVisibility(View.INVISIBLE);
            holder.getView(R.id.pintuan).setVisibility(View.INVISIBLE);
            HashMap<String, String> m1 = new HashMap<>();
            m1.put("sut_title", map.get("sut_title"));
            m1.put("num", map.get("num"));
            m1.put("snr", map.get("snr"));
            ArrayList<HashMap<String, String>> l = new ArrayList<>();
            l.add(m1);
            ((mRecyclerView) holder.getView(R.id.listview)).setLayoutManager(new LinearLayoutManager(a));
            ((mRecyclerView) holder.getView(R.id.listview)).addItemDecoration(new mItemDecoration(a));
            ((mRecyclerView) holder.getView(R.id.listview)).setAdapter
                    (new itemAdapter(3, l));
            int num = 0;
            for (HashMap<String, String> m : l) {
                num += Integer.valueOf(m.get("num"));
            }
            SpannableString ss = new SpannableString("共" + num + "件商品,实付￥" + map.get("money"));
            ss.setSpan(new ForegroundColorSpan(Color.BLACK), (num + "").length() + 7, ss.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            holder.setText(R.id.numMoney, ss);
            ((itemAdapter) ((mRecyclerView) holder.getView(R.id.listview)).getAdapter()).setOnRecyclerViewItemClickListener(null);
        }

        private void PuTong(BaseViewHolder holder, final HashMap<String, String> map) {
            holder.setText(R.id.status, "已支付");
            ArrayList<HashMap<String, String>> l = AnalyticalJSON.getList_zj(map.get("order"));

            if (l != null) {
                if(l.size()==0){
                    HashMap<String,String> map1=new HashMap<>();
                    map1.put("title",map.get("sut_title"));
                    map1.put("num",map.get("num"));
                    l.add(map1);
                }
                ((mRecyclerView) holder.getView(R.id.listview)).setLayoutManager(new LinearLayoutManager(a));
                ((mRecyclerView) holder.getView(R.id.listview)).addItemDecoration(new mItemDecoration(a));
                ((mRecyclerView) holder.getView(R.id.listview)).setAdapter
                        (new itemAdapter(1, l));
                int num = 0;
                for (HashMap<String, String> m : l) {
                    num += Integer.valueOf(m.get("num"));
                }
                SpannableString ss = new SpannableString("共" + num + "件商品,实付￥" + map.get("money"));
                ss.setSpan(new ForegroundColorSpan(Color.BLACK), (num + "").length() + 7, ss.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                holder.setText(R.id.numMoney, ss);
                ((itemAdapter) ((mRecyclerView) holder.getView(R.id.listview)).getAdapter()).setOnRecyclerViewItemClickListener(new OnRecyclerViewItemClickListener() {
                    @Override
                    public void onItemClick(View view, int i) {
                        String snr = map.get("snr");
                        Intent intent = new Intent(a, MyZhiFuDetail.class);
                        if (PreferenceUtil.getUserIncetance(a).getString("role", "").equals("3")) {
                            intent.putExtra("id", map.get("id"));
                        }
                        intent.putExtra("status", "");
                        intent.putExtra("snr", snr);
                        intent.putExtra("KT", "".equals(map.get("number")) ? false : true);
                        a.startActivity(intent);
                    }
                });

            }
            if (PreferenceUtil.getUserIncetance(a).getString("role", "").equals("3")) {
                if (map.get("status").equals("3")) {
                    holder.getView(R.id.tuikuan).setVisibility(View.INVISIBLE);
                    holder.getView(R.id.pintuan).setEnabled(false);
                    holder.setText(R.id.pintuan, "已退款");
                    holder.getView(R.id.pintuan).setVisibility(View.VISIBLE);
                } else {
                    if ("1".equals(map.get("delivery"))) {
                        holder.setText(R.id.pintuan, "发货");
                        holder.setText(R.id.tuikuan, "退款");
                        holder.getView(R.id.tuikuan).setVisibility(View.VISIBLE);
                        holder.getView(R.id.tuikuan).setEnabled(true);
                        holder.getView(R.id.pintuan).setVisibility(View.VISIBLE);
                        holder.getView(R.id.pintuan).setEnabled(true);
                        holder.setOnClickListener(R.id.tuikuan, new View.OnClickListener() {
                            @Override
                            public void onClick(final View view) {
                                JSONObject js = new JSONObject();
                                try {
                                    js.put("id", map.get("id"));
                                    js.put("user_id", map.get("user_id"));
                                    js.put("shop_id", map.get("shop_id"));
                                    js.put("m_id", Constants.M_id);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                ApisSeUtil.M m = ApisSeUtil.i(js);
//                                OkHttpUtils.post(Constants.TuiKuan_Normal).params("key", m.K())
//                                        .params("msg", m.M()).execute(new StringCallback() {
//                                    @Override
//                                    public void onSuccess(String s, Call call, Response response) {
//                                        HashMap<String, String> map1 = AnalyticalJSON.getHashMap(s);
//                                        if (map1 != null) {
//                                            if ("000".equals(map1.get("code"))) {
//                                                ToastUtil.showToastShort("退款申请已提交，请耐心等待");
//                                                map.put("status", "3");
//                                                ((TextView) view).setText("已退款");
//                                                view.setEnabled(false);
//                                                Intent intent=new Intent("tuikuan");
//                                                a.sendBroadcast(intent);
//                                            } else if ("444".equals(map1.get("code"))) {
//                                                ToastUtil.showToastShort("退款申请失败，请稍后重试或联系客服");
//                                            }
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onBefore(BaseRequest request) {
//                                        super.onBefore(request);
//                                        ProgressUtil.show(a, "", "请稍等");
//                                    }
//
//                                    @Override
//                                    public void onAfter(@Nullable String s, @Nullable Exception e) {
//                                        super.onAfter(s, e);
//                                        ProgressUtil.dismiss();
//                                    }
//                                });
//                            }
//                        });
//                        holder.setOnClickListener(R.id.pintuan, new View.OnClickListener() {
//                            @Override
//                            public void onClick(final View view) {
//                                JSONObject js = new JSONObject();
//                                try {
//                                    js.put("id", map.get("id"));
//                                    js.put("snr", map.get("snr"));
//                                    js.put("m_id", Constants.M_id);
//                                    js.put("type", "1");
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                                ApisSeUtil.M m = ApisSeUtil.i(js);
//                                OkHttpUtils.post(Constants.GoodsAdminfahuo)
//                                        .params("key", m.K())
//                                        .params("msg", m.M())
//                                        .execute(new StringCallback() {
//
//
//                                            @Override
//                                            public void onBefore(BaseRequest request) {
//                                                super.onBefore(request);
//                                                ProgressUtil.show(a, "", "请稍等");
//                                            }
//
//                                            @Override
//                                            public void onSuccess(String s, Call call, Response response) {
//                                                HashMap<String, String> m = AnalyticalJSON.getHashMap(s);
//                                                if (m != null) {
//                                                    if ("000".equals(m.get("code"))) {
//                                                        ToastUtil.showToastShort("该订单已发货");
//                                                        map.put("delivery", "2");
//                                                        ((TextView) view).setText("已发货");
//                                                        view.setEnabled(false);
//                                                        Intent intent=new Intent("tuikuan");
//                                                        a.sendBroadcast(intent);
//                                                    }
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onAfter(@Nullable String s, @Nullable Exception e) {
//                                                super.onAfter(s, e);
//                                                ProgressUtil.dismiss();
//                                            }
//
//
//                                        });
                            }
                        });
                    }else{
                        holder.getView(R.id.pintuan).setVisibility(View.VISIBLE);
                        holder.setText(R.id.pintuan,"已发货");
                        holder.getView(R.id.pintuan).setEnabled(false);
                        holder.getView(R.id.tuikuan).setVisibility(View.INVISIBLE);
                    }
                }

            } else {
                if ("3".equals(map.get("status"))) {
                    holder.getView(R.id.tuikuan).setVisibility(View.INVISIBLE);
                    holder.getView(R.id.pintuan).setEnabled(false);
                    holder.setText(R.id.pintuan, "已退款");
                    holder.getView(R.id.pintuan).setVisibility(View.VISIBLE);
                }else if("2".equals(map.get("delivery"))){
                    holder.getView(R.id.tuikuan).setVisibility(View.INVISIBLE);
                    holder.getView(R.id.pintuan).setEnabled(false);
                    holder.setText(R.id.pintuan, "已发货");
                    holder.getView(R.id.pintuan).setVisibility(View.VISIBLE);
                } else if("1".equals(map.get("delivery"))){
                    holder.getView(R.id.tuikuan).setVisibility(View.INVISIBLE);
                    holder.getView(R.id.pintuan).setEnabled(false);
                    holder.setText(R.id.pintuan, "等待发货");
                    holder.getView(R.id.pintuan).setVisibility(View.VISIBLE);
                }
                holder.getView(R.id.pintuan).setVisibility(View.VISIBLE);
                holder.getView(R.id.tuikuan).setVisibility(View.INVISIBLE);
            }
        }
    }
}
