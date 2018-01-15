package com.yunfengsi.Managers;

import android.content.Context;
import android.content.Intent;
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
import com.yunfengsi.Model_activity.activity_Detail;
import com.yunfengsi.Model_zhongchou.FundingDetailActivity;
import com.yunfengsi.R;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.Network;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.TimeUtils;
import com.yunfengsi.Utils.ToastUtil;
import com.yunfengsi.Utils.mApplication;
import com.yunfengsi.XuanzheActivity;
import com.yunfengsi.ZiXun_Detail;

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

public class MessageCenter extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final int ZiXun = 1;
    private static final int HuoDong = 2;
    private static final int GongYang = 3;
    private static final int ZhuXue = 4;
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
        ((TextView) findViewById(R.id.title_title)).setText("通知中心");
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

        adapter = new MessageAdapter(this,new ArrayList<HashMap<String, String>>());
        adapter.openLoadMore(pageSize, true);
        adapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (endPage != page) {
                    isLoadMore = true;
                    page++;
                    getNotice();
                }
            }
        });
        adapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                Intent intent=new Intent();
                HashMap<String,String > map=adapter.getData().get(i);
                if(map.get("type")!=null){
                    switch (Integer.valueOf(map.get("type"))){
                        case 1:
                            intent.setClass(MessageCenter.this,ZiXun_Detail.class);
                            break;
                        case 2:
                            intent.setClass(MessageCenter.this,activity_Detail.class);
                            break;
                        case 3:
                            intent.setClass(MessageCenter.this,XuanzheActivity.class);
                            break;
                        case 4:
                            intent.setClass(MessageCenter.this,FundingDetailActivity.class);
                            break;
                    }
                    intent.putExtra("id",map.get("type_id"));
                    startActivity(intent);
                }
            }
        });
        recyclerView.setAdapter(adapter);

        TextView textView = new TextView(this);
        Drawable d = ContextCompat.getDrawable(this, R.drawable.load_nothing);
        d.setBounds(0, 0, DimenUtils.dip2px(this, 150), DimenUtils.dip2px(this, 150) * d.getIntrinsicHeight() / d.getIntrinsicWidth());
        textView.setCompoundDrawables(null, d, null, null);
        textView.setCompoundDrawablePadding(DimenUtils.dip2px(this, 10));
        textView.setText(mApplication.ST("暂无通知"));


        textView.setGravity(Gravity.CENTER);
        ViewGroup.MarginLayoutParams vl = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        vl.topMargin = DimenUtils.dip2px(this, 180);
        textView.setLayoutParams(vl);
        adapter.setEmptyView(textView);
        swip.post(new Runnable() {
            @Override
            public void run() {
                swip.setRefreshing(false);
                onRefresh();
            }
        });
    }

    private static class MessageAdapter extends BaseQuickAdapter<HashMap<String, String>> {
        Drawable next;
        int dp30;
        public MessageAdapter(Context context,List<HashMap<String, String>> data) {
            super(R.layout.item_message_center, data);
            dp30=DimenUtils.dip2px(context,20);
            next=ContextCompat.getDrawable(context,R.drawable.item_tip);
            next.setBounds(0,0,dp30,dp30);
        }

        @Override
        protected void convert(BaseViewHolder holder, HashMap<String, String> map) {
            if(map.get("type")!=null){
                switch (Integer.valueOf(map.get("type"))){
                    case 1:
                        holder.setText(R.id.title,"资讯");
                        break;
                    case 2:
                        holder.setText(R.id.title,"活动");
                        break;
                    case 3:
                        holder.setText(R.id.title,"供养");
                        break;
                    case 4:
                        holder.setText(R.id.title,"助学");
                        break;
                }
            }
            holder.setText(R.id.content,mApplication.ST(map.get("contents")))
                    .setText(R.id.time,mApplication.ST(TimeUtils.getTrueTimeStr(map.get("time"))))
                    .setText(R.id.time2,mApplication.ST(TimeUtils.getTrueTimeStr(map.get("time"))));
            ((TextView) holder.getView(R.id.detail)).setCompoundDrawables(null,null,next,null);


        }
    }

    private void getNotice() {
        if (Network.HttpTest(this)) {
            JSONObject js = new JSONObject();
            try {
                js.put("page", page);
                js.put("m_id", Constants.M_id);
                js.put("user_id", PreferenceUtil.getUserId(this));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ApisSeUtil.M m = ApisSeUtil.i(js);
            LogUtil.e("获取通知信息：：" + js);
            OkGo.post(Constants.Notice)
                    .tag(this)
                    .params("key", m.K())
                    .params("msg", m.M())
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            final ArrayList<HashMap<String, String>> list = AnalyticalJSON.getList_zj(s);
                            if (list != null) {
                                if (isRefresh) {
                                    adapter.setNewData(list);
                                    isRefresh = false;
                                    swip.setRefreshing(false);
                                } else if (isLoadMore) {
                                    isLoadMore = false;
                                    if (list.size() < 10) {
                                        ToastUtil.showToastShort(mApplication.ST("通知加载完毕"), Gravity.BOTTOM);
                                        endPage = page;
                                        adapter.notifyDataChangedAfterLoadMore(list, false);
                                    } else {
                                        adapter.notifyDataChangedAfterLoadMore(list, true);
                                    }
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
        page = 1;
        isRefresh = true;
        adapter.openLoadMore(10, true);
        getNotice();
        swip.setRefreshing(false);
    }
}
