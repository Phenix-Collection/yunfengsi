package com.yunfengsi.Models.Auction;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
import com.yunfengsi.Utils.NumUtils;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.TimeUtils;
import com.yunfengsi.Utils.ToastUtil;
import com.yunfengsi.Utils.mApplication;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 作者：luZheng on 2018/06/09 09:44
 */
public class AuctionList extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView       recyclerView;
    private SwipeRefreshLayout swip;
    private MessageAdapter     adapter;
    private int     pageSize   = 8;
    private int     page       = 1;
    private int     endPage    = -1;
    private boolean isLoadMore = false;
    private boolean isRefresh  = false;
    public static class RefreshEvent{

    }
    @Subscribe
    public void doRefresh(RefreshEvent event){
        onRefresh();
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_center);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        EventBus.getDefault().register(this);
        ((ImageView) findViewById(R.id.title_back)).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.title_title)).setText("义卖");
        ((TextView) findViewById(R.id.handle_right)).setText("竞拍记录");
        ((TextView) findViewById(R.id.handle_right)).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.handle_right)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AuctionList.this,Bid_History_Mine.class));
            }
        });
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
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new MessageAdapter(this, new ArrayList<HashMap<String, String>>());

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
        }, recyclerView);
        adapter.disableLoadMoreIfNotFullPage();

        recyclerView.setAdapter(adapter);


        adapter.setEmptyView(mApplication.getEmptyView(this, 150, "暂无义卖商品"));

        swip.post(new Runnable() {
            @Override
            public void run() {
                onRefresh();
            }
        });
    }

    private class MessageAdapter extends BaseQuickAdapter<HashMap<String, String>, BaseViewHolder> {
        int dp30;

        public MessageAdapter(Context context, List<HashMap<String, String>> data) {
            super(R.layout.item_auciton_list, data);
            dp30 = getResources().getDisplayMetrics().widthPixels - DimenUtils.dip2px(context, 20);

        }

        @Override
        protected void convert(BaseViewHolder holder, final HashMap<String, String> map) {
            Glide.with(AuctionList.this)
                    .load(map.get("image"))
                    .override(dp30, dp30)
                    .placeholder(R.color.darkWhite)
                    .error(R.color.darkWhite)
                    .centerCrop()
                    .into((ImageView) holder.getView(R.id.image));
            SpannableString ss = new SpannableString("当前 ￥" + NumUtils.getNumStr(map.get("now_price")));
            ss.setSpan(new ForegroundColorSpan(Color.RED), 3, ss.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.setText(R.id.title, mApplication.ST(map.get("title")))
                    .setText(R.id.priceNow, ss)
                    .setText(R.id.peopleNum, mApplication.ST(map.get("partake") + " 人出价"));
            long Lstart = TimeUtils.dataOne(map.get("start_time"));
            long Lend   = TimeUtils.dataOne(map.get("end_time"));

            if (Lstart > System.currentTimeMillis()) {

                holder.setText(R.id.endTime, mApplication.ST("开始时间 " + map.get("start_time")));
            } else if (Lstart<=System.currentTimeMillis()&&Lend> System.currentTimeMillis()) {
                holder.setText(R.id.endTime, mApplication.ST(("结束时间 " + map.get("end_time"))));

            } else {
                holder.setText(R.id.endTime, mApplication.ST("竞拍已结束"));

            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AuctionList.this, AuctionDetail.class);
                    intent.putExtra("id", map.get("id"));
                    startActivity(intent);
                }
            });
        }
    }

    private void getNotice() {
        if (Network.HttpTest(this)) {
            JSONObject js = new JSONObject();
            try {
                js.put("page", page);
                js.put("m_id", Constants.M_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ApisSeUtil.M m = ApisSeUtil.i(js);
            LogUtil.e("获取拍卖商品信息：：" + js);
            OkGo.post(Constants.AuctionList)
                    .tag(this)
                    .params("key", m.K())
                    .params("msg", m.M())
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            HashMap<String, String> map = AnalyticalJSON.getHashMap(s);
                            if (map != null) {
                                final ArrayList<HashMap<String, String>> list = AnalyticalJSON.getList_zj(map.get("msg"));
                                if (list != null) {
                                    if (isRefresh) {
                                        adapter.setNewData(list);
                                        isRefresh = false;
                                        swip.setRefreshing(false);
                                    } else if (isLoadMore) {
                                        isLoadMore = false;
                                        if (list.size() < pageSize) {
                                            endPage = page;
                                            adapter.addData(list);
                                            adapter.loadMoreEnd(false);
                                        } else {
                                            adapter.addData(list);
                                            adapter.loadMoreComplete();
                                        }
                                    }
                                }
                            }


                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            super.onError(call, response, e);

                        }

                        @Override
                        public void onAfter(String s, Exception e) {
                            super.onAfter(s, e);
                            if(e instanceof SocketTimeoutException){
                                LogUtil.e("onAfter：：：："+e);
                                ToastUtil.showToastShort("网络连接超时，请检查网络后重试");
                            }
                            swip.setRefreshing(false);
                        }

                    });
        }
    }

    @Override
    public void onRefresh() {
        swip.setRefreshing(true);
        page = 1;
        isRefresh = true;
        adapter.setEnableLoadMore(true);
        getNotice();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
