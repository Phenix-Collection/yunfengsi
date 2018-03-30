package com.yunfengsi.YaoYue;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.yunfengsi.R;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.Network;
import com.yunfengsi.Utils.PhoneSMSManager;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.mApplication;
import com.yunfengsi.View.mItemDecoration;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 作者：因陀罗网 on 2017/11/21 13:50
 * 公司：成都因陀罗网络科技有限公司
 */

public class CarerManage extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {


    private RecyclerView recyclerView;
    private SwipeRefreshLayout swip;
    private MessageAdapter adapter;
    private int pageSize = 10;
    private int page = 1;
    private int endPage = -1;
    private boolean isLoadMore = false;
    private boolean isRefresh = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.message_center);


        ((ImageView) findViewById(R.id.title_back)).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.title_title)).setText("申请列表");
        ((ImageView) findViewById(R.id.title_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        swip = (SwipeRefreshLayout) findViewById(R.id.swip);
        swip.setOnRefreshListener(this);
        swip.setColorSchemeResources(R.color.main_color);

        recyclerView = (RecyclerView) findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new mItemDecoration(this));

        adapter = new MessageAdapter(this, new ArrayList<HashMap<String, String>>());


        adapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);

//        adapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
//            @Override
//            public void onItemClick(View view, int i) {
//                Intent intent=new Intent(Meditation_History.this,Fortune_Detail.class);
//                intent.putExtra("map", ((HashMap) view.getTag()));
//                startActivity(intent);
//            }
//        });
        recyclerView.setAdapter(adapter);

        TextView textView = new TextView(this);
        Drawable d = ContextCompat.getDrawable(this, R.drawable.load_nothing);
        d.setBounds(0, 0, DimenUtils.dip2px(this, 150), DimenUtils.dip2px(this, 150) * d.getIntrinsicHeight() / d.getIntrinsicWidth());
        textView.setCompoundDrawables(null, d, null, null);
        textView.setCompoundDrawablePadding(DimenUtils.dip2px(this, 10));
        textView.setText(mApplication.ST("暂无申请"));


        textView.setGravity(Gravity.CENTER);
        ViewGroup.MarginLayoutParams vl = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        vl.topMargin = DimenUtils.dip2px(this, 180);
        textView.setLayoutParams(vl);
        adapter.setEmptyView(textView);
        swip.post(new Runnable() {
            @Override
            public void run() {
                swip.setRefreshing(true);
                onRefresh();
            }
        });
    }

    private  class MessageAdapter extends BaseQuickAdapter<HashMap<String, String>,BaseViewHolder> {

        public MessageAdapter(Context context, List<HashMap<String, String>> data) {
            super(R.layout.item_yaoyue, data);
        }

        @Override
        protected void convert(BaseViewHolder holder, final HashMap<String, String> map) {
            holder.getView(R.id.delete).setVisibility(View.GONE);
            holder.setVisible(R.id.button, true)
                    .setVisible(R.id.timeLayout, false)
                    .setVisible(R.id.titleLayout, false);
            holder.setText(R.id.user,"乘客:")
                    .setText(R.id.userName,map.get("pet_name"))
                    .setText(R.id.phone,map.get("phone"))
                    .setText(R.id.seat,"乘坐人数:")
                    .setText(R.id.num,map.get("passenger"))
                    .setText(R.id.address,map.get("address"));
            holder.setText(R.id.button,"拨打电话");
            holder.getView(R.id.button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PhoneSMSManager.callPhone1(CarerManage.this,map.get("phone"));
                }
            });

        }
    }

    private void getHistory() {
        if (Network.HttpTest(this)) {
            JSONObject js = new JSONObject();
            try {
                js.put("m_id", Constants.M_id);
                js.put("act_id", getIntent().getStringExtra("id"));
                js.put("num", getIntent().getStringExtra("num"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ApisSeUtil.M m = ApisSeUtil.i(js);
            LogUtil.e("获取约车管理列表：：" + js);
            OkGo.post(Constants.YuecarManageList)
                    .tag(this)
                    .params("key", m.K())
                    .params("msg", m.M())
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {

                            final ArrayList<HashMap<String, String>> list = AnalyticalJSON.getList_zj(AnalyticalJSON.getHashMap(s).get("msg"));
                            if (list != null) {
                                if (isRefresh) {
                                    adapter.setNewData(list);
                                    isRefresh = false;
                                    swip.setRefreshing(false);
                                }
                            }

                        }

                        @Override
                        public void onAfter(String s, Exception e) {
                            super.onAfter(s, e);
                            swip.setRefreshing(false);
                        }

                    });
        }
    }

    @Override
    public void onRefresh() {
//        ArrayList<HashMap<String, String>> list = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            HashMap<String, String> map = new HashMap<>();
//            list.add(map);
//        }
//        adapter.setNewData(list);
        isRefresh = true;
        getHistory();
    }
}
