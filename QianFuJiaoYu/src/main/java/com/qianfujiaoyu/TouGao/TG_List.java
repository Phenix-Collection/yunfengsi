package com.qianfujiaoyu.TouGao;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
import com.qianfujiaoyu.Activitys.Home_Class;
import com.qianfujiaoyu.R;
import com.qianfujiaoyu.Utils.AnalyticalJSON;
import com.qianfujiaoyu.Utils.ApisSeUtil;
import com.qianfujiaoyu.Utils.Constants;
import com.qianfujiaoyu.Utils.DimenUtils;
import com.qianfujiaoyu.Utils.Network;
import com.qianfujiaoyu.Utils.PreferenceUtil;
import com.qianfujiaoyu.Utils.ProgressUtil;
import com.qianfujiaoyu.Utils.StatusBarCompat;
import com.qianfujiaoyu.Utils.ToastUtil;
import com.qianfujiaoyu.Utils.mApplication;
import com.qianfujiaoyu.View.mItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/11/2.
 */
public class TG_List extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private ImageView back;
    private TextView title;
    private RecyclerView listView;
    private int page = 1;
    private int endPage = -1;
    private boolean isLoadMore = false;
    private boolean isRefresh;
    private mAdapter adapter;
    private static final String TAG = "TG_List";
    private SwipeRefreshLayout swip;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.tougao_list);
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
                        js.put("class_id", getIntent().getStringExtra("id"));
                        js.put("page", page);
                        js.put("m_id", Constants.M_id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m = ApisSeUtil.i(js);
                    String data = OkGo.post(Constants.Tougao_List_IP)
                            .params("key", m.K())
                            .params("msg", m.M()).execute().body().string();
                    if (!TextUtils.isEmpty(data)) {
                        final List<HashMap<String, String>> list1 = AnalyticalJSON.getList_zj(data);
                        Log.w(TAG, "run: list------>" + list1);
                        if (list1 != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (isRefresh) {
                                        adapter.setNewData(list1);
                                        isRefresh = false;
                                        swip.setRefreshing(false);
                                    } else if (isLoadMore) {
                                        isLoadMore = false;
                                        if (list1.size() < 10) {
                                            ToastUtil.showToastShort("已经没有更多数据啦", Gravity.CENTER);
                                            endPage = page;
                                            adapter.notifyDataChangedAfterLoadMore(list1, false);
                                        } else {
                                            adapter.notifyDataChangedAfterLoadMore(list1, true);
                                        }
                                    }
                                    swip.setRefreshing(false);

                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                   swip.setRefreshing(false);
                                }
                            });
                        }
                    }
                } catch (final IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            swip.setRefreshing(false);
                        }
                    });
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void initView() {
        swip= (SwipeRefreshLayout) findViewById(R.id.swip);
        swip.setOnRefreshListener(this);
        swip.setColorSchemeResources(R.color.main_color);
        back = (ImageView) findViewById(R.id.back);
        back.setImageResource(R.drawable.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        title = (TextView) findViewById(R.id.title);
        title.setText("动态列表");

        listView = (RecyclerView) findViewById(R.id.fund_people_list);
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.addItemDecoration(new mItemDecoration(this));
        adapter = new mAdapter(this, new ArrayList<HashMap<String, String>>());
        adapter.openLoadMore(10, true);
        adapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
        adapter.setEmptyView(mApplication.getLoadNothing(R.drawable.load_nothing, "暂无班级动态",200));

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
        listView.setAdapter(adapter);


    }
    @Override
    public void onRefresh() {
        page = 1;
        isRefresh = true;
        endPage = -1;
        adapter.openLoadMore(10, true);
        getData();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }



    public static class mAdapter extends BaseQuickAdapter<HashMap<String,String>> {

        public List<HashMap<String, String>> list;
        private Context context;

        public mAdapter(Activity context, List<HashMap<String, String>> data) {
            super(R.layout.tougao_list_item, data);
            this.context=context;
            this.list=data;
        }


        public void addList(List<HashMap<String, String>> list) {
            this.list = list;
        }


        @Override
        protected void convert(final BaseViewHolder holder, final HashMap<String, String> map) {
            Glide.with(context).load(map.get("image1")).override(DimenUtils.dip2px(mApplication.getInstance(), 80)
                    , DimenUtils.dip2px(mApplication.getInstance(), 80))
                    .centerCrop()
                    .placeholder(R.drawable.indra).into((ImageView) holder.getView(R.id.tougao_list_item_head));
            holder.setText(R.id.tougao_list_item_name,"发布人:" + map.get("pet_name"));
            holder.setTag(R.id.tougao_list_item_name,map.get("id"));
            holder.setText(R.id.tougao_list_item_title,"标题:" + map.get("title"));
            holder.setText(R.id.tougao_list_item_time,
                    "投稿时间:" + map.get("time"));

            holder.setOnClickListener(R.id.content,new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, TG_Detail.class);
                    intent.putExtra("id", map.get("id"));
                    context.startActivity(intent);
                }
            });
            if(map.get("user_id").equals(PreferenceUtil.getUserId(context))){
                holder.setVisible(R.id.delete,true);
            }else{
                holder.setVisible(R.id.delete,false);
            }
            holder.setOnClickListener(R.id.delete,new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if(map.get("user_id").equals(PreferenceUtil.getUserId(context))){
                        AlertDialog.Builder b = new AlertDialog.Builder(context);
                        b.setTitle("确认要删除这条动态吗？")
                                .setNegativeButton("确  定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        v.setEnabled(false);
                                        ProgressUtil.show(context, "", "正在删除");
                                        JSONObject js = new JSONObject();
                                        try {
                                            js.put("m_id", Constants.M_id);
                                            js.put("id", map.get("id"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        ApisSeUtil.M m = ApisSeUtil.i(js);
                                        OkGo.post(Constants.Archmage_delete_zixun_IP)
                                                .params("key", m.K())
                                                .params("msg", m.M())
                                                .execute(new AbsCallback<Object>() {
                                                    @Override
                                                    public Object convertSuccess(Response response) throws Exception {
                                                        return null;
                                                    }

                                                    @Override
                                                    public void onSuccess(Object o, Call call, Response response) {
                                                        if (response != null) {
                                                            try {
                                                                String data = response.body().string();
                                                                HashMap<String, String> m = AnalyticalJSON.getHashMap(data);
                                                                if (m != null && "000".equals(m.get("code"))) {
                                                                    v.setEnabled(true);
                                                                    Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
                                                                    ((SwipeMenuLayout) holder.convertView).smoothClose();
                                                                    int i=getData().indexOf(map);
                                                                    notifyItemRemoved(i);
                                                                    getData().remove(map);
                                                                    EventBus.getDefault().post(new Home_Class.ClassInfo());
                                                                } else {
                                                                    v.setEnabled(true);
                                                                    Toast.makeText(context, "删除失败，请稍后重试", Toast.LENGTH_SHORT).show();
                                                                }
                                                            } catch (IOException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onAfter(Object o, Exception e) {
                                                        super.onAfter(o, e);
                                                        ProgressUtil.dismiss();
                                                    }
                                                });
                                    }


                                }).setPositiveButton("取  消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
                    }else{
                        ToastUtil.showToastShort("只能修改自己发布的动态呦");
                        ((SwipeMenuLayout) holder.convertView).smoothClose();
                    }

                }
            });
        }






    }
}
