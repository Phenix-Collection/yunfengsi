package com.yunfengsi.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.yunfengsi.Adapter.Mine_SC_adapter;
import com.yunfengsi.Model_activity.activity_Detail;
import com.yunfengsi.Model_zhongchou.FundingDetailActivity;
import com.yunfengsi.R;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.TimeUtils;
import com.yunfengsi.Utils.ToastUtil;
import com.yunfengsi.Utils.mApplication;
import com.yunfengsi.View.mPLlistview2;
import com.yunfengsi.ZiXun_Detail;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/7/21.
 */
public class Mine_SC extends Fragment implements View.OnClickListener {
    private mPLlistview2 listView;
    private TextView tip;
    private SharedPreferences sp;
    private static final String TAG = "Mine_SC";
    private Mine_SC_adapter SCadapter;
    List<HashMap<String, String>> list;
    private Handler handler = new Handler();
    private boolean needToChange = false;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            SCadapter.list.clear();
            getData();
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mine_shoucang_fragment, container, false);

        listView = (mPLlistview2) view.findViewById(R.id.mine_shoucang_listview);
        listView.removeFooterView(listView.footer);
        tip = (TextView) view.findViewById(R.id.mine_shoucang_tip);
        Drawable d= ContextCompat.getDrawable(getActivity(),R.drawable.indra);
        d.setBounds(0,0,200,200);
        tip.setCompoundDrawables(null,d,null,null);
        listView.setEmptyView(tip);
        SCadapter = new Mine_SC_adapter(getActivity());
        list = new ArrayList<HashMap<String, String>>();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView view1 = (TextView) view.findViewById(R.id.mine_shoucang_item_type);
                String id1 = view1.getTag().toString();
                Intent intent = new Intent();

                if (view1.getText().toString().equals("图文")) {
                    intent.setClass(mApplication.getInstance(), ZiXun_Detail.class);
                }else if(view1.getText().toString().equals("活动")){
                    long end_time= TimeUtils.dataOne(SCadapter.list.get(position).get("end_time"));
                    LogUtil.e("结束时间："+end_time+"  当前时间："+System.currentTimeMillis());
                    if(end_time>=System.currentTimeMillis()){
                        ToastUtil.showToastShort("活动已结束");
                        return;
                    }
                    intent.setClass(mApplication.getInstance(),activity_Detail.class);
                }else if(view1.getText().toString().equals("慈善")){
                    intent.setClass(mApplication.getInstance(), FundingDetailActivity.class);
                }
                intent.putExtra("id", id1);
                startActivityForResult(intent, 0);
            }
        });
        listView.setAdapter(SCadapter);
        getData();
        IntentFilter intentFilter = new IntentFilter("Mine_SC");
        getActivity().registerReceiver(receiver, intentFilter);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        OkGo.getInstance().cancelTag(TAG);
        getActivity().unregisterReceiver(receiver);
    }

    private void getData() {
        if (sp == null) {
            sp = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        }
        if (sp.getString("user_id", "").equals("") || sp.getString("uid", "").equals("")) {
            tip.setText("暂无收藏信息,请点击头像登录");
            tip.setVisibility(View.VISIBLE);
            SCadapter.list.clear();
            SCadapter.notifyDataSetChanged();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject js=new JSONObject();
                    try {
                        js.put("user_id", sp.getString("user_id", ""));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String data = OkGo.post(Constants.news_sc_list_Ip).tag(TAG)
                            .params("msg", ApisSeUtil.getMsg(js))
                            .params("key", ApisSeUtil.getKey())
                            .execute().body().string();
                    if (!data.equals("")) {
                        Log.w(TAG, "run: news_+-=-getData-=-=-=" + data);
                        List<HashMap<String, String>> mlist = AnalyticalJSON.getList(data, "news");
                        if (mlist == null) {

                        } else {
                            if (list != null) list.addAll(mlist);
                        }
                    }
                    String data1 = OkGo.post(Constants.Activity_Shoucang_list_IP).tag(TAG)
                            .params("msg", ApisSeUtil.getMsg(js))
                            .params("key", ApisSeUtil.getKey())
                            .execute().body().string();
                    if (!data1.equals("")) {
                        List<HashMap<String, String>> mlist = AnalyticalJSON.getList(data1, "activity");
                        if (mlist == null) {

                        } else {
                            if (list != null) list.addAll(mlist);
                        }
                    }
                    String data2 = OkGo.post(Constants.FUNDING_DETAIL_Shoucang_List).tag(TAG)
                            .params("msg", ApisSeUtil.getMsg(js))
                            .params("key", ApisSeUtil.getKey())
                            .execute().body().string();
                    if (!data2.equals("")) {
                        Log.w(TAG, "run: Fund+-=-getData-=-=-=" + data2);
                        List<HashMap<String, String>> mlist = AnalyticalJSON.getList(data2, "crowdfunding");
                        if (mlist == null) {

                        } else {
                            if (list != null) list.addAll(mlist);
                        }
                    }
                    if (list != null && list.size() == 0) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (SCadapter != null && tip != null) {
                                    SCadapter.notifyDataSetChanged();
                                    tip.setText("暂时还没有收藏");
                                    tip.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (list!=null&&list.size() == 0) {
                                if (tip != null && tip.getVisibility() != View.VISIBLE) {
                                    tip.setText("暂时还没有收藏");
                                    if (tip.getVisibility() == View.GONE)
                                        tip.setVisibility(View.VISIBLE);
                                    SCadapter.notifyDataSetChanged();
                                }
                            } else {
                                if (SCadapter != null && listView != null && tip != null) {
                                    Log.e(TAG, "run: list-=-=-=-=>"+list );
                                    List<HashMap<String ,String >>list1=list;
                                    SCadapter.addList(list1);
                                    listView.setAdapter(SCadapter);
                                    if (tip.getVisibility() == View.VISIBLE) {
                                        tip.setVisibility(View.GONE);
                                    }
                                }
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //点击事件
    @Override
    public void onClick(View v) {

    }
}
