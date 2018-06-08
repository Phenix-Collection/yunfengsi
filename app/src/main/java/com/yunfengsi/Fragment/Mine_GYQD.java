package com.yunfengsi.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.yunfengsi.Models.Model_zhongchou.FundingDetailActivity;
import com.yunfengsi.R;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.ImageUtil;
import com.yunfengsi.Utils.Network;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.TimeUtils;
import com.yunfengsi.Utils.mApplication;
import com.yunfengsi.View.mPLlistview2;
import com.yunfengsi.WebShare.ZhiFuShare;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/9/14.
 */
public class Mine_GYQD extends AppCompatActivity implements View.OnClickListener{
    private mPLlistview2 listView;
    private TextView tip;
    private List<HashMap<String, String>> list;
    private SharedPreferences sp;
    private static final String TAG = "Mine_GYQD";
    private ZFAdapter adapter;
    private Handler handler = new Handler();
    private String page = "1";
    private String endPage = "";
    private BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            page="1";
            endPage="";
            getData();
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.mine_shoucang_fragment);
        listView = (mPLlistview2)findViewById(R.id.mine_shoucang_listview);
        ImageView back= (ImageView) findViewById(R.id.title_back);
        back.setVisibility(View.VISIBLE);
        back.setImageBitmap(ImageUtil.readBitMap(this, R.drawable.back));
        back.setOnClickListener(this);
        ((TextView) findViewById(R.id.title_title)).setText(mApplication.ST("功德"));
        tip = (TextView) findViewById(R.id.mine_shoucang_tip);
        Drawable d= ContextCompat.getDrawable(this, R.drawable.indra);
        d.setBounds(0,0,200,200);
        tip.setCompoundDrawables(null,d,null,null);
        listView.setEmptyView(tip);
        tip.setText(mApplication.ST("暂时没有支付记录"));
        list = new ArrayList<HashMap<String, String>>();
        sp = getSharedPreferences("user", Context.MODE_PRIVATE);
        adapter = new ZFAdapter();
        listView.footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.footer.setText(mApplication.ST("正在加载"));
                if (!endPage.equals(page)) page = String.valueOf(Integer.valueOf(page) + 1);
                Log.w(TAG, "onClick:  page-=-=-=" + page + "   endPaeg-=-" + endPage);
                getData();
            }
        });
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView title= (TextView) view.findViewById(R.id.title);
                if(title.getTag()!=null){
                    if("4".equals(title.getTag(R.id.sut_type))){
                        Intent intent=new Intent(Mine_GYQD.this, ZhiFuShare.class);
                        intent.putExtra("stu_id",view.findViewById(R.id.title).getTag().toString());
                        startActivity(intent);
                    }else if("5".equals(title.getTag(R.id.sut_type))){
                        Intent intent=new Intent(Mine_GYQD.this, FundingDetailActivity.class);
                        intent.putExtra("id",list.get(position).get("shop_id"));
                        startActivity(intent);
                    }
                }
            }
        });
        getData();
        IntentFilter intentFilter=new IntentFilter("Mine_GY");
        registerReceiver(broadcastReceiver,intentFilter);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);

    }

    private void getData() {
        if (!Network.HttpTest(getApplicationContext())) {
            Toast.makeText(mApplication.getInstance(), mApplication.ST("请检查网络连接"), Toast.LENGTH_SHORT).show();
            return;
        }
        if (sp.getString("user_id", "").equals("")) {
            tip.setVisibility(View.VISIBLE);
            tip.setText(mApplication.ST("请登录"));
            list.clear();
            adapter.notifyDataSetChanged();
            listView.footer.setVisibility(View.GONE);
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject js=new JSONObject();
                    try {
                        js.put("m_id", Constants.M_id);
                        js.put("page",page);
                        js.put("user_id", sp.getString("user_id", ""));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String data = OkGo.post(Constants.ZhiFu_Detail_Ip).tag(TAG)
                            .params("key", ApisSeUtil.getKey())
                            .params("msg", ApisSeUtil.getMsg(js))
                           .execute().body().string();
                    if (!TextUtils.isEmpty(data)) {
                        Log.w(TAG, "run: " + data);
                        final ArrayList<HashMap<String ,String >>list1 = AnalyticalJSON.getList(data, "gyqd");
                        if (list1 != null) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (list1.size() == 0) {//没有评论的时候
                                        tip.setVisibility(View.VISIBLE);
                                        tip.setText(mApplication.ST("暂时没有支付记录"));
                                        listView.footer.setVisibility(View.GONE);
                                        adapter.notifyDataSetChanged();
                                        return;
                                    } else {
                                        listView.footer.setVisibility(View.VISIBLE);
                                        list.addAll(list1);
                                        adapter.notifyDataSetChanged();
                                        tip.setVisibility(View.GONE);
                                        if (list1.size() < 10) {
                                            endPage = page;
                                            listView.footer.setText(mApplication.ST("没有更多数据了"));
                                            listView.footer.setEnabled(false);
                                        } else {
                                            endPage="";
                                            listView.footer.setEnabled(true);
                                            listView.footer.setText(mApplication.ST("点击加载更多"));
                                        }
                                    }
                                }
                            });
                        }else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if(tip!=null&&listView!=null){
                                        tip.setVisibility(View.VISIBLE);
                                        tip.setText(mApplication.ST("暂时没有支付记录"));
                                        listView.footer.setVisibility(View.GONE);
                                        list=new ArrayList<HashMap<String, String>>();
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            });
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        if(v.getId()== R.id.title_back){
            finish();
        }
    }


    private class ZFAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return null==list?0:list.size();
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
            Holder holder=null;
            if(view==null){
                holder=new Holder();
                view=LayoutInflater.from(Mine_GYQD.this).inflate(R.layout.mine_gyqd,parent,false);
                holder.image= (TextView) view.findViewById(R.id.image);
                holder.title= (TextView) view.findViewById(R.id.title);
               holder.danjia= (TextView) view.findViewById(R.id.danjia);
               holder.num= (TextView) view.findViewById(R.id.num);
               holder.time= (TextView) view.findViewById(R.id.time);
                holder.total= (TextView) view.findViewById(R.id.total);
                view.setTag(holder);
            }else{
                holder= (Holder) view.getTag();
            }
            double d=Double.valueOf(list.get(position).get("money"))/(Integer.valueOf(list.get(position).get("num")));

            holder.title.setText(mApplication.ST(list.get(position).get("sut_title")));
            holder.title.setTag(list.get(position).get("id"));
            holder.title.setTag(R.id.sut_type,list.get(position).get("sut_type"));
            holder.danjia.setText("￥"+String.format("%.2f",d));
            holder.num.setText(mApplication.ST("数量:"+list.get(position).get("num")));
            holder.total.setText(mApplication.ST("总计:"+list.get(position).get("money")+"元"));
            holder.time.setText(mApplication.ST(TimeUtils.getTrueTimeStr(list.get(position).get("end_time"))));
//            Glide.with(Mine_GYQD.this).load(list.get(position).get("image")).override(DimenUtils.dip2px(Mine_GYQD.this,60),DimenUtils.dip2px(Mine_GYQD.this,60))
//            .centerCrop().into(holder.image);
            if("4".equals(list.get(position).get("sut_type"))){
                holder.image.setText(mApplication.ST("供养"));
                holder.image.setTextColor(ContextCompat.getColor(Mine_GYQD.this, R.color.main_color));
                holder.image.setSelected(false);
            }else{
                holder.image.setText(mApplication.ST("助学"));
                holder.image.setTextColor(Color.parseColor("#FF6F61"));
                holder.image.setSelected(true);
            }
            return view;
        }
        class  Holder {
            TextView title;
            TextView danjia;
            TextView num;
            TextView time;
            TextView image;
            TextView total;
        }
    }
}
