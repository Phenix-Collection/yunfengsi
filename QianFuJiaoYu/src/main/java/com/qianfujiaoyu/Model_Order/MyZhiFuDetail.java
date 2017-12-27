package com.qianfujiaoyu.Model_Order;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.qianfujiaoyu.R;
import com.qianfujiaoyu.Utils.AnalyticalJSON;
import com.qianfujiaoyu.Utils.ApisSeUtil;
import com.qianfujiaoyu.Utils.Constants;
import com.qianfujiaoyu.Utils.ImageUtil;
import com.qianfujiaoyu.Utils.LogUtil;
import com.qianfujiaoyu.Utils.PreferenceUtil;
import com.qianfujiaoyu.Utils.StatusBarCompat;
import com.qianfujiaoyu.View.mItemListview;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/1/19.
 */
public class MyZhiFuDetail extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private String snr;
    private ImageView Back;
    private mItemListview listview;
    private TextView Title, User, Address, Phone, Time, Status, Snr, YouHui, Money_Total, Pay;
    public TextView Total_Cost;
    private itemAdapter adapter;
    private SwipeRefreshLayout swip;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, ContextCompat.getColor(this, R.color.main_color));
        setContentView(R.layout.my_zhifu_detail);
        Back = (ImageView) findViewById(R.id.back);
        Back.setImageBitmap(ImageUtil.readBitMap(this, R.drawable.back));
        Back.setVisibility(View.VISIBLE);
        Back.setOnClickListener(this);
        Title = (TextView) findViewById(R.id.title);
        Title.setText("我的订单详情");
        initView();
        snr = getIntent().getStringExtra("snr");
        getData();


    }

    /**
     * 初始化数据
     */
    private void initView() {
        swip = (SwipeRefreshLayout) findViewById(R.id.swip);
        swip.setColorSchemeResources(R.color.main_color);
        swip.setOnRefreshListener(this);
        User = (TextView) findViewById(R.id.username);
        Address = (TextView) findViewById(R.id.address);
        Phone = (TextView) findViewById(R.id.phone);
        Time = (TextView) findViewById(R.id.time);
        Status = (TextView) findViewById(R.id.status);
        Snr = (TextView) findViewById(R.id.snr);
        Total_Cost = (TextView) findViewById(R.id.total);
        YouHui = (TextView) findViewById(R.id.youhui);
        Money_Total = (TextView) findViewById(R.id.money_total);
        Pay = (TextView) findViewById(R.id.pay);
        listview = (mItemListview) findViewById(R.id.listview);
        adapter = new itemAdapter(this, new ArrayList<HashMap<String, String>>());
        listview.setAdapter(adapter);
    }

    /**
     * 获取订单详情
     */
    private void getData() {
        JSONObject js = new JSONObject();
        try {
            js.put("snr", snr);
            js.put("m_id", Constants.M_id);
            js.put("user_id", PreferenceUtil.getUserIncetance(this).getString("user_id", ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e("js::::" + js);
        ApisSeUtil.M m = ApisSeUtil.i(js);
        OkGo.post(Constants.MyOrders_detail).tag(this).params("key", m.K())
                .params("msg", m.M())
                .execute(new AbsCallback<HashMap<String, String>>() {
                    @Override
                    public HashMap<String, String> convertSuccess(Response response) throws Exception {
                        return AnalyticalJSON.getHashMap(response.body().string());
                    }

                    @Override
                    public void onSuccess(final HashMap<String, String> map, Call call, Response response) {
                        if (map != null) {
                            HashMap<String, String> address = null;
                            if (map.get("address") != null && !map.get("address").equals("null")) {
                                address = AnalyticalJSON.getHashMap(map.get("address"));
                            }
                            if (address != null) {
                                User.setText("收  货  人: " + address.get("receiver") + "    " + address.get("phone"));
                                if (address.get("province").contains("北京")
                                        || address.get("province").contains("上海")
                                        || address.get("province").contains("天津")
                                        || address.get("province").contains("重庆")
                                        || address.get("province").contains("香港")
                                        || address.get("province").contains("澳门")
                                        || address.get("province").contains("钓鱼岛")) {
                                    if (address.get("province").contains("澳门") || address.get("province").contains("香港")) {
                                        Address.setText("地        址: " + address.get("province") + "特别行政区" + address.get("address"));
                                    } else {
                                        Address.setText("地        址: " + address.get("province") + (address.get("province").endsWith("岛") ? "" : "市") + address.get("address"));
                                    }

                                } else {
                                    if (address.get("city").endsWith("区") || address.get("city").endsWith("州")) {
                                        Address.setText("地        址: " + address.get("province") + "省" + address.get("city") + address.get("address"));
                                    } else if (address.get("city").contains("其他")) {
                                        Address.setText("地        址: " + address.get("province") + "省" + address.get("address"));
                                    } else {
                                        Address.setText("地        址: " + address.get("province") + "省" + address.get("city") + "市" + address.get("address"));
                                    }

                                }
                            }
                            ArrayList<HashMap<String, String>> list = null;

                            list = AnalyticalJSON.getList_zj(map.get("order"));

                            Snr.setText(map.get("snr"));
                            Money_Total.setText("￥" + map.get("money"));
                            Pay.setText("￥" + map.get("money"));
                            Status.setText("已支付");
                            Time.setText(map.get("end_time"));
                            adapter.Update(list);
                            listview.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Total_Cost.setText("￥" + String.format("%.2f", adapter.total_cost));
                                    if(adapter.total_cost - Double.valueOf(map.get("money"))>0){
                                        YouHui.setText("￥" + String.format("%.2f", adapter.total_cost - Double.valueOf(map.get("money"))));
                                    }else{
                                        YouHui.setText("￥" +"0.00");
                                    }

                                }
                            }, 300);

                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toast.makeText(MyZhiFuDetail.this, "加载失败，请稍后重试", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onAfter(HashMap<String, String> stringStringHashMap, Exception e) {
                        super.onAfter(stringStringHashMap, e);
                        if (swip.isRefreshing()) swip.setRefreshing(false);
                    }


                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(this);

    }

    @Override
    public void onRefresh() {
        swip.post(new Runnable() {
            @Override
            public void run() {
                swip.setRefreshing(true);
                adapter.total_cost = 0d;
                getData();
            }
        });
    }


    private static class itemAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<HashMap<String, String>> list;
        public double total_cost = 0d;

        public itemAdapter(Context context, ArrayList<HashMap<String, String>> list) {
            super();
            this.context = context;
            this.list = list;
        }

        public void Update(ArrayList<HashMap<String, String>> list) {

            this.list = list;
            notifyDataSetChanged();

        }

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();

        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            HashMap<String, String> map = list.get(position);
            LogUtil.e("getView！！@！@！@！");

            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.myzhifudetail_item, parent, false);
            }
            if (((mItemListview) parent).isOnMeasure) {
                return view;
            }
            TextView name = (TextView) view.findViewById(R.id.name);
            TextView cost = (TextView) view.findViewById(R.id.cost);
            TextView money = (TextView) view.findViewById(R.id.money);
            TextView num = (TextView) view.findViewById(R.id.num);
            name.setText(map.get("title"));
            cost.setText("￥" + map.get("cost"));
            cost.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            money.setText("￥" + map.get("money"));
            num.setText("×" + map.get("num"));
            total_cost += (Integer.valueOf(map.get("num")) * Double.valueOf(map.get("cost")));

            return view;
        }
    }
}
