package com.qianfujiaoyu.Activitys;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.qianfujiaoyu.Adapter.expandAdapter;
import com.qianfujiaoyu.R;
import com.qianfujiaoyu.Utils.AnalyticalJSON;
import com.qianfujiaoyu.Utils.ApisSeUtil;
import com.qianfujiaoyu.Utils.Constants;
import com.qianfujiaoyu.Utils.LogUtil;
import com.qianfujiaoyu.Utils.Network;
import com.qianfujiaoyu.Utils.StatusBarCompat;
import com.qianfujiaoyu.View.LoadMoreListViewExpand;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/11/2.
 */
public class Member_List extends AppCompatActivity implements LoadMoreListViewExpand.OnLoadMore, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private ImageView back;
    private TextView title;
    private LoadMoreListViewExpand listView;
    private String page = "1";
    private String endPage = "";
    private TextView t;//加载数据的底部提示
    private ProgressBar p;//加载数据的底部进度
    private expandAdapter adapter;
    private static final String TAG = "Fans_List";
    //    private int currentPos=-1;
    private ImageView tip;
    private SwipeRefreshLayout swip;
    private boolean isRefresh = false;
    public String  isTeacher="1";
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            swip.post(new Runnable() {
                @Override
                public void run() {
                    swip.setRefreshing(true);
                    onRefresh();
                }
            });
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.fans_focus_list);
        initView();
        swip.post(new Runnable() {
            @Override
            public void run() {
                swip.setRefreshing(true);
                onRefresh();
            }
        });
    }


    /**
     * 上拉加载
     */
    @Override
    public void loadMore() {
        isRefresh=false;

        if (!endPage.equals(page)) {
            page = String.valueOf(Integer.parseInt(page) + 1);
        } else {
            p.setVisibility(View.GONE);
            t.setText("没有更多成员了");
            return;
        }
        getData();
    }

    @Override
    public void onRefresh() {
        page = "1";
        endPage = "";
        isRefresh = true;
        getData();
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
                    try {
                        js.put("id", getIntent().getStringExtra("id"));
                        js.put("page", page);
                        js.put("m_id", Constants.M_id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m = ApisSeUtil.i(js);
                    String data = OkGo.post(Constants.getClassUserList).tag(TAG)
                            .params("key",m.K())
                            .params("msg",m.M()).execute().body().string();
                    if (!TextUtils.isEmpty(data)) {
                        final ArrayList<HashMap<String, String>> list1 = AnalyticalJSON.getList_zj(data);
                        Log.w(TAG, "run: list------>" + list1);
                        if (list1 != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (list1.size() == 0) {
                                        p.setVisibility(View.GONE);
                                        t.setText("没有更多班级成员了");
                                        Toast.makeText(Member_List.this, "暂无班级成员", Toast.LENGTH_SHORT).show();
                                        tip.setVisibility(View.VISIBLE);
                                        if (swip != null && swip.isRefreshing())
                                            swip.setRefreshing(false);
                                        return;
                                    }
                                    tip.setVisibility(View.GONE);
                                    if (list1.size() < 10) {
                                        endPage = page;
                                        p.setVisibility(View.GONE);
                                        t.setText("没有更多班级成员了");
                                    }
                                    if (adapter == null) {
                                        adapter = new expandAdapter(Member_List.this, list1, expandAdapter.Fans);
                                        listView.setAdapter(adapter);
                                    } else {
                                        if (isRefresh) {
                                            isRefresh = false;
                                            adapter.addList(list1);
                                            adapter.notifyDataSetChanged();
                                        } else {
                                            adapter.list.addAll(list1);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                    if (swip != null && swip.isRefreshing())
                                        swip.setRefreshing(false);
                                    listView.onLoadComplete();

                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (swip != null && swip.isRefreshing())
                                        swip.setRefreshing(false);
                                    tip.setVisibility(View.VISIBLE);
                                    Toast.makeText(Member_List.this, "暂无班级成员", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tip.setVisibility(View.VISIBLE);
                            if (swip != null && swip.isRefreshing()) swip.setRefreshing(false);
                            Toast.makeText(Member_List.this, "加载异常，请稍后重试", Toast.LENGTH_SHORT).show();
                        }
                    });
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        OkGo.getInstance().cancelTag(TAG);
    }


    private void initView() {
        IntentFilter intentFilter = new IntentFilter("bz");
        registerReceiver(receiver, intentFilter);
        isTeacher=getIntent().getStringExtra("role");
        LogUtil.e("member中权限：：："+isTeacher);
        swip = (SwipeRefreshLayout) findViewById(R.id.ask_swip);
        swip.setOnRefreshListener(this);
        swip.setColorSchemeResources(R.color.main_color);
        tip = (ImageView) findViewById(R.id.fans_tip);
        tip.setOnClickListener(this);
        back = (ImageView) findViewById(R.id.back);
        back.setImageResource(R.drawable.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        title = (TextView) findViewById(R.id.title);
        title.setText("班级成员");

        listView = (LoadMoreListViewExpand) findViewById(R.id.fund_people_list);
        listView.setLoadMoreListen(this);
        listView.setGroupIndicator(null);
        listView.setChildIndicator(null);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                String i = view.findViewById(R.id.user_list_item_name).getTag().toString();
//                Intent intent=new Intent(getApplicationContext(),User_Detail.class);
//                intent.putExtra("id",i);
//                intent.putExtra("type",1);
//                Log.w(TAG, "onItemClick: 位置： "+position );
//                startActivityForResult(intent,0);
//            }
//        });
        t = (TextView) (listView.footer.findViewById(R.id.load_more_text));
        p = (ProgressBar) (listView.footer.findViewById(R.id.load_more_bar));

    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(resultCode==0x00){
//            boolean b=false;
//
//            if(data!=null){
//                b=data.getBooleanExtra("isDeleted",false);
//            }
//            if(adapter!=null&&b){
//                adapter.list.remove(p);
//                adapter.notifyDataSetChanged();
//            }
//        }
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.fans_tip:
                onRefresh();
                break;
        }
    }


//    public static class mAdapter extends BaseAdapter {
//
//        public List<HashMap<String, String>> list;
//        private Activity context;
//        private Drawable d2;
//
//        public mAdapter(Activity context, List<HashMap<String, String>> list) {
//            super();
//            this.context = context;
//            this.list = list;
//            d2 = ContextCompat.getDrawable(context, R.drawable.delete);
//            d2.setBounds(0, 0, DimenUtils.dip2px(context, 25), DimenUtils.dip2px(context, 25));
//        }
//
//        public void addList(List<HashMap<String, String>> list) {
//            this.list = list;
//        }
//
//        @Override
//        public int getCount() {
//            return list == null ? 0 : list.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return list.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(final int position, View view, ViewGroup parent) {
//            viewHolder holder = null;
//            final HashMap<String, String> map = list.get(position);
//            if (view == null) {
//                holder = new viewHolder();
//                view = LayoutInflater.from(context).inflate(R.layout.user_list_item, parent, false);
//                holder.head = (AvatarImageView) view.findViewById(R.id.user_list_item_head);
//                holder.name = (TextView) view.findViewById(R.id.user_list_item_name);
//                holder.sign = (TextView) view.findViewById(R.id.user_list_item_sign);
//                holder.job = (TextView) view.findViewById(R.id.user_list_item_job);
//                holder.guanzhu = (TextView) view.findViewById(R.id.user_list_item_guanzhu);
//                holder.tip = (ImageView) view.findViewById(R.id.user_list_item_tip);
//                holder.content = (LinearLayout) view.findViewById(R.id.content);
//                holder.delete = (TextView) view.findViewById(R.id.delete);
//                view.setTag(holder);
//            } else {
//                holder = (viewHolder) view.getTag();
//            }
//            holder.content.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    String i = v.findViewById(R.id.user_list_item_name).getTag().toString();
//                    Intent intent = new Intent(context, User_Detail.class);
//                    intent.putExtra("id", i);
//                    intent.putExtra("type", 1);
//                    Log.w(TAG, "onItemClick: 位置： " + position);
//                    context.startActivityForResult(intent, 0);
//                }
//            });
//            Glide.with(context).load(list.get(position).get("user_image")).override(DimenUtils.dip2px(mApplication.getInstance(), 60)
//                    , DimenUtils.dip2px(mApplication.getInstance(), 60)).into(holder.head);
//            holder.name.setText(list.get(position).get("pet_name"));
//            holder.name.setTag(list.get(position).get("id"));
//            holder.sign.setText(list.get(position).get("signature").equals("") ? "这个人很懒，还没有签名噢" : list.get(position).get("signature"));
//            if ("1".equals(map.get("realname"))) {
//                holder.job.setBackgroundResource(R.drawable.button1_shape_enabled);
//                holder.job.setText("未认证");
//                holder.job.setTextColor(Color.parseColor("#aaaaaa"));
//            } else {
//                holder.job.setText(map.get("job"));
//            }
//            holder.guanzhu.setCompoundDrawables(null, d2, null, null);
//            holder.guanzhu.setText("移除粉丝");
//            holder.guanzhu.setTextColor(Color.RED);
//            holder.guanzhu.setVisibility(View.GONE);
//            holder.delete.setText("移除粉丝");
//            holder.delete.setTag(position);
//            holder.delete.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(final View v) {
//
//
//                }
//            });
//            return view;
//        }
//
//        static class viewHolder {
//            AvatarImageView head;
//            TextView name, job, guanzhu;
//            ImageView tip;
//            TextView sign;
//            LinearLayout content;
//            TextView delete;
//        }
//    }
}
