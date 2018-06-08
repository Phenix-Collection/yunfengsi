package com.yunfengsi.Models.Model_activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.push.AndroidPopupActivity;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.CoordinateConverter;
import com.amap.api.location.DPoint;
import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.yunfengsi.R;
import com.yunfengsi.Setting.GanyuActivity;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.Network;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.ProgressUtil;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.TimeUtils;
import com.yunfengsi.Utils.ToastUtil;
import com.yunfengsi.Utils.mApplication;
import com.yunfengsi.View.LoadMoreListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/10/11.
 */
public class Mine_activity_list extends AndroidPopupActivity implements LoadMoreListView.OnLoadMore, View.OnClickListener, AMapLocationListener, SwipeRefreshLayout.OnRefreshListener {
    private ImageView back;
    private TextView title;
    private LoadMoreListView listView;
    private String page = "1";
    private String endPage = "";
    private TextView t;//加载数据的底部提示
    private ProgressBar p;//加载数据的底部进度
    private static final String TAG = "Fund_surpport_list";
    private mAdapter adapter;


    //声明mlocationClient对象
    public AMapLocationClient mlocationClient;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    private double lat, longt;//定位获取的经纬度
    private SwipeRefreshLayout swip;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.fund_people_list);
        initView();
        swip = (SwipeRefreshLayout) findViewById(R.id.swip);
        swip.setColorSchemeResources(R.color.main_color);
        swip.setOnRefreshListener(this);
        swip.post(new Runnable() {
            @Override
            public void run() {
                swip.setRefreshing(true);
                getData();
            }
        });


        getLocation();

    }

    private void getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    666);//自定义的code
            return;
        }
        mlocationClient = new AMapLocationClient(this);
//初始化定位参数
        mLocationOption = new AMapLocationClientOption();
//设置定位监听
        mlocationClient.setLocationListener(this);
//设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
//设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
// 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
// 在定位结束后，在合适的生命周期调用onDestroy()方法
// 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
//启动定位
        mlocationClient.startLocation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mlocationClient.unRegisterLocationListener(this);
        mlocationClient.stopLocation();

    }

    /**
     * 上拉加载
     */
    @Override
    public void loadMore() {
//        if (!endPage.equals(page)) {
//            page = String.valueOf(Integer.parseInt(page) + 1);
//        } else {
        p.setVisibility(View.GONE);
        t.setText(mApplication.ST("没有更多数据了"));
//            return;
//        }
//        getData();
    }

    /**
     * 加载数据
     */
    private void getData() {
        if (!Network.HttpTest(this)) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject js = new JSONObject();
                    js.put("user_id", PreferenceUtil.getUserIncetance(Mine_activity_list.this).getString("user_id", ""));
                    js.put("m_id", Constants.M_id);
                    ApisSeUtil.M m = ApisSeUtil.i(js);
                    LogUtil.e("我的活动列表：：；" + js);
                    String data = OkGo.post(Constants.mine_activity)
                            .params("key", m.K())
                            .params("msg", m.M())
                            .execute().body().string();
                    if (!TextUtils.isEmpty(data)) {
                        final List<HashMap<String, String>> list1 = AnalyticalJSON.getList_zj(data);

                        if (list1 != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    if (adapter == null) {
                                        adapter = new mAdapter(Mine_activity_list.this, list1);
                                        listView.setAdapter(adapter);
                                    } else {
                                        adapter.addList(list1);
                                        adapter.notifyDataSetChanged();
                                    }

                                    listView.onLoadComplete();
                                    swip.setRefreshing(false);

                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    swip.setRefreshing(false);
                                    Toast.makeText(Mine_activity_list.this, mApplication.ST("数据加载失败，请稍后尝试"), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void initView() {
        back = (ImageView) findViewById(R.id.title_back);
        back.setImageResource(R.drawable.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        title = (TextView) findViewById(R.id.title_title);
        title.setText(mApplication.ST("我的活动"));
        ((TextView) findViewById(R.id.handle_right)).setText(mApplication.ST("修行经历"));
        ((TextView) findViewById(R.id.handle_right)).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.handle_right)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Mine_activity_list.this,ActivityHistory.class));
            }
        });



        listView = (LoadMoreListView) findViewById(R.id.fund_people_list);
        listView.setLoadMoreListen(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView title = (TextView) view.findViewById(R.id.fund_list_item_name);
                if (title != null && title.getTag() != null) {
                    Intent intent = new Intent(Mine_activity_list.this, activity_Detail.class);
                    intent.putExtra("id", ((String) title.getTag()));
                    startActivity(intent);
                }
            }
        });

        t = (TextView) (listView.footer.findViewById(R.id.load_more_text));
        p = (ProgressBar) (listView.footer.findViewById(R.id.load_more_bar));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
//                aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                lat = aMapLocation.getLatitude();//获取纬度
                longt = aMapLocation.getLongitude();//获取经度
//                aMapLocation.getAccuracy();//获取精度信息
//                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                Date date = new Date(aMapLocation.getTime());
//                df.format(date);//定位时间
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
        }
    }

    @Override
    public void onRefresh() {
        getData();
    }

    @Override
    protected void onSysNoticeOpened(String s, String s1, Map<String, String> map) {

    }

    public class mAdapter extends BaseAdapter {

        public List<HashMap<String, String>> list;
        private Context context;

        public mAdapter(Context context, List<HashMap<String, String>> list) {
            super();
            this.context = context;
            this.list = list;
        }

        public void addList(List<HashMap<String, String>> list) {
            this.list = list;
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
        public View getView(final int position, View view, ViewGroup parent) {
            viewHolder holder = null;
            final HashMap<String, String> map = list.get(position);
            if (view == null) {
                holder = new viewHolder();
                view = LayoutInflater.from(context).inflate(R.layout.mine_activity_item, parent, false);
                holder.head = (ImageView) view.findViewById(R.id.fund_list_item_head);
                holder.name = (TextView) view.findViewById(R.id.fund_list_item_name);
                holder.money = (TextView) view.findViewById(R.id.fund_list_item_money);
                holder.time = (TextView) view.findViewById(R.id.fund_list_item_time);
                holder.sign = (TextView) view.findViewById(R.id.signact);
                view.setTag(holder);
            } else {
                holder = (viewHolder) view.getTag();
            }

            Glide.with(context).load(list.get(position).get("image1")).centerCrop().override(DimenUtils.dip2px(mApplication.getInstance(), 120)
                    , DimenUtils.dip2px(mApplication.getInstance(), 90)).into(holder.head);
//            if("".equals(map.get("pet_name"))){
//                holder.name.setText(map.get("pet_name")+"❤❤");
//            }else{
//                holder.name.setText(list.get(position).get("pet_name").substring(0, 1)+"❤❤");
//            }
            if ("0".equals(list.get(position).get("status"))) {
                holder.money.setText(mApplication.ST("待审核"));
                holder.sign.setVisibility(View.GONE);
            } else if ("1".equals(list.get(position).get("status"))) {
                holder.money.setText(mApplication.ST("已拒绝"));
                holder.sign.setVisibility(View.GONE);
            } else if ("2".equals(list.get(position).get("status"))) {
                holder.money.setText(mApplication.ST("已通过"));
                if (!map.get("longitude").equals("") && !map.get("latitude").equals("")) {
                    if (map.get("act_time") != null && !map.get("act_time").equals("")) {
                        long startTime = TimeUtils.dataOne(map.get("act_time"));
                        if (System.currentTimeMillis() < startTime) {
                            holder.sign.setVisibility(View.VISIBLE);
                        } else {
                            holder.sign.setVisibility(View.GONE);
                        }
                    } else {
                        holder.sign.setVisibility(View.GONE);
                    }
                } else {
                    holder.sign.setVisibility(View.GONE);
                }


            } else {
                holder.money.setText("");
            }
            if (list.get(position).get("start").equals("1")) {
                holder.sign.setText("签到");
                holder.sign.setEnabled(true);
            } else {
                holder.sign.setText("查看详情");
                holder.sign.setEnabled(true);
            }
            holder.time.setText(mApplication.ST(TimeUtils.getTrueTimeStr(list.get(position).get("time"))));
            holder.name.setText(mApplication.ST(list.get(position).get("title")));
            holder.name.setTag(list.get(position).get("act_id"));

            holder.sign.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    TextView textView= (TextView) view;
                    if(textView.getText()!=null){
                        if("签到".equals(textView.getText().toString())){
                            if (!map.get("longitude").equals("") && !map.get("latitude").equals("")) {
                                DPoint defaultPoint = new DPoint(Double.valueOf(map.get("latitude")), Double.valueOf(map.get("longitude")));
                                DPoint mPoint = new DPoint(lat, longt);
                                int distance = ((int) CoordinateConverter.calculateLineDistance(defaultPoint, mPoint));
                                LogUtil.e("默认金纬度：：；" + map.get("longitude") + "     " + map.get("latitude"));
                                LogUtil.e("测量金纬度：：；" + longt + "     " + lat);
                                LogUtil.e("定位距离：：：" + distance + "     " + CoordinateConverter.calculateLineDistance(defaultPoint, mPoint));
                                if (distance > 500) {
                                    ToastUtil.showToastShort("当前位置已超出云峰寺签到范围");
                                    return;
                                }
                                postSign(view,map);
                            }
                        }else if("查看详情".equals(textView.getText().toString())){
                            showDetailDialog(Mine_activity_list.this,map.get("bednum"),map.get("roomnum"));
                        }
                    }


                }

                private void postSign(final View view, HashMap<String, String> map) {
                    JSONObject js = new JSONObject();
                    try {
                        js.put("m_id", Constants.M_id);
                        js.put("id", list.get(position).get("id"));
                        js.put("user_id", PreferenceUtil.getUserId(context));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m = ApisSeUtil.i(js);
                    LogUtil.e("活动签到：：；" + js);
                    OkGo.post(Constants.Signact).params("key", m.K())
                            .params("msg", m.M())
                            .execute(new StringCallback() {
                                @Override
                                public void onSuccess(String s, Call call, Response response) {
                                    HashMap<String, String> map = AnalyticalJSON.getHashMap(s);
                                    if (map != null) {
                                        if ("000".equals(map.get("code"))) {
                                            ToastUtil.showToastShort("签到成功");
                                            ((TextView) view).setText("查看详情");
                                            showDetailDialog(Mine_activity_list.this,map.get("bednum"),map.get("roomnum"));
                                            view.setEnabled(true);
                                        } else if ("002".equals(map.get("code"))) {
                                            ((TextView) view).setText("查看详情");
                                            ToastUtil.showToastShort("您已经签过到了");
                                            view.setEnabled(true);
                                        }
                                    }
                                }

                                @Override
                                public void onBefore(BaseRequest request) {
                                    super.onBefore(request);
                                    ProgressUtil.show(context, "", "正在签到，请稍等");
                                }

                                @Override
                                public void onAfter(String s, Exception e) {
                                    super.onAfter(s, e);
                                    ProgressUtil.dismiss();
                                }
                            });
                }
            });
            return view;
        }

        private void showDetailDialog(final Activity activity,String bednum,String roomnum) {

            AlertDialog.Builder builder=new AlertDialog.Builder(activity);
            SpannableStringBuilder ss;
            if(bednum.equals("")||roomnum.equals("")){
                ss=new SpannableStringBuilder("暂未分配住宿，请稍后查看");
            }else{
                ss=new SpannableStringBuilder("您已被分配到 "+roomnum+bednum+" 住宿\n\n");
                String deafualt= "如对住宿有特殊要求需要修改请联系客服更改床位";
                ss.append(deafualt);
                ss.setSpan(new ForegroundColorSpan(Color.RED),ss.length()-deafualt.length(),ss.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                ss.setSpan(new ForegroundColorSpan(ContextCompat.getColor(activity,R.color.main_color)),7,7+roomnum.length()+bednum.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                ss.setSpan(new AbsoluteSizeSpan(DimenUtils.dip2px(activity,14)),ss.length()-deafualt.length()-1,ss.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            }

            AlertDialog dialog=builder.setMessage(ss)
            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).setNegativeButton("联系客服", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    startActivity(new Intent(activity, GanyuActivity.class));
                }
            }).create();
            dialog.show();
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.BLACK);
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.RED);
            ((TextView) dialog.getDelegate().findViewById(android.R.id.message)).setTextSize(18);


        }

        class viewHolder {
            ImageView head;
            TextView name, sign;
            TextView money;
            TextView time;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 666 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            LogUtil.e("获取权限成功，开始定位");
            getLocation();
        } else {
            ToastUtil.showToastShort("无定位权限将无法进行活动签到");
        }
    }
}
