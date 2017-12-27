package com.yunfengsi.Model_activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.yunfengsi.R;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.Network;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.TimeUtils;
import com.yunfengsi.Utils.mApplication;
import com.yunfengsi.View.LoadMoreListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/10/11.
 */
public class Mine_activity_list extends AppCompatActivity implements LoadMoreListView.OnLoadMore, View.OnClickListener {
    private ImageView back;
    private TextView title;
    private LoadMoreListView listView;
    private String page = "1";
    private String endPage = "";
    private TextView t;//加载数据的底部提示
    private ProgressBar p;//加载数据的底部进度
    private static final String TAG = "Fund_surpport_list";
    private mAdapter adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.fund_people_list);
        initView();
        getData();

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
                    JSONObject js=new JSONObject();
                    js.put("user_id", PreferenceUtil.getUserIncetance(Mine_activity_list.this).getString("user_id",""));
                    js.put("m_id", Constants.M_id);
                    String data = OkGo.post(Constants.mine_activity)
                            .params("key", ApisSeUtil.getKey())
                            .params("msg",ApisSeUtil.getMsg(js))
                            .execute().body().string();
                    if (!TextUtils.isEmpty(data)) {
                        final List<HashMap<String, String>> list1 = AnalyticalJSON.getList_zj(data);

                        if (list1 != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                    if (list1.size() < 10) {
//                                        endPage = page;
//                                    }
                                    if (adapter==null) {
                                        adapter=new mAdapter(Mine_activity_list.this,list1);
                                        listView.setAdapter(adapter);
                                    }
//                                    else {
//                                        adapter.list.addAll(list1);
//                                        adapter.notifyDataSetChanged();
//
//                                    }
                                    listView.onLoadComplete();

                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
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

        listView = (LoadMoreListView) findViewById(R.id.fund_people_list);
        listView.setLoadMoreListen(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView title= (TextView) view.findViewById(R.id.fund_list_item_name);
                if(title!=null&&title.getTag()!=null){
                    Intent intent=new Intent(Mine_activity_list.this, activity_Detail.class);
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
    public static class mAdapter extends BaseAdapter{

        public List<HashMap<String ,String >>list;
        private Context context;
        public mAdapter(Context context,List<HashMap<String ,String >>list) {
            super();
            this.context=context;
            this.list=list;
        }

        public void addList(List<HashMap<String ,String >>list){
            this.list=list;
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
            viewHolder holder = null;
            HashMap<String ,String>map=list.get(position);
            if (view == null) {
                holder = new viewHolder();
                view = LayoutInflater.from(context).inflate(R.layout.mine_activity_item, parent, false);
                holder.head = (ImageView) view.findViewById(R.id.fund_list_item_head);
                holder.name = (TextView) view.findViewById(R.id.fund_list_item_name);
                holder.money = (TextView) view.findViewById(R.id.fund_list_item_money);
                holder.time = (TextView) view.findViewById(R.id.fund_list_item_time);
                view.setTag(holder);
            } else {
                holder = (viewHolder) view.getTag();
            }

            Glide.with(context).load(list.get(position).get("image1")).override(DimenUtils.dip2px(mApplication.getInstance(), 120)
                    , DimenUtils.dip2px(mApplication.getInstance(), 90)).into(holder.head);
//            if("".equals(map.get("pet_name"))){
//                holder.name.setText(map.get("pet_name")+"❤❤");
//            }else{
//                holder.name.setText(list.get(position).get("pet_name").substring(0, 1)+"❤❤");
//            }
            if("0".equals(list.get(position).get("status"))){
                holder.money.setText(mApplication.ST("待审核"));
            }else if("1".equals(list.get(position).get("status"))){
                holder.money.setText(mApplication.ST("已拒绝"));
            }else if("2".equals(list.get(position).get("status"))){
                holder.money.setText(mApplication.ST("已通过"));
            }else {
                holder.money.setText("");
            }
            holder.time.setText(mApplication.ST(TimeUtils.getTrueTimeStr(list.get(position).get("time"))));
            holder.name.setText(mApplication.ST(list.get(position).get("title")));
            holder.name.setTag(list.get(position).get("act_id"));
            return view;
        }

        static  class viewHolder {
            ImageView head;
            TextView name;
            TextView money;
            TextView time;
        }
    }

}
