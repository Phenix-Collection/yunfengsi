package com.yunfengsi.Models.Auction;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.ruffian.library.RTextView;
import com.yunfengsi.Managers.AboutPay.Dingdan_commit;
import com.yunfengsi.R;
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
import com.yunfengsi.Utils.mApplication;

import org.greenrobot.eventbus.Subscribe;
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

public class Bid_History_Mine extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {


    private SwipeRefreshLayout swip;
    private MessageAdapter     adapter;
    private int     pageSize   = 5;
    private int     page       = 1;
    private int     endPage    = -1;
    private boolean isLoadMore = false;
    private boolean isRefresh  = false;

    public static class DoRefreshEvent{

    }
    @Subscribe
    public void doRefresh(DoRefreshEvent event){
        onRefresh();
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.message_center);

//        findViewById(R.id.title_image2).setVisibility(View.GONE);
//        ((ImageView) findViewById(R.id.title_image2)).setImageBitmap(ImageUtil.readBitMap(this, R.drawable.fenxiang2));
//        findViewById(R.id.title_image2).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                UMWeb  umWeb = new UMWeb(Constants.FX_host_Ip + "wish" + "/id/"  + map.get("id") + "/st/" + (mApplication.isChina ? "s" : "t"));
//                umWeb.setTitle(mApplication.ST("祈愿功德回向"));
//                umWeb.setDescription(mApplication.ST("一叶一菩提，一花一世界；爱出者爱返，福往者福来"));
//                umWeb.setThumb(new UMImage(Bless_History.this, R.drawable.indra_share));
//                new ShareManager().shareWeb(umWeb,Bless_History.this);
//            }
//        });
        findViewById(R.id.title_back).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.title_title)).setText(mApplication.ST("竞拍记录"));
        findViewById(R.id.title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        swip = findViewById(R.id.swip);
        swip.setOnRefreshListener(this);
        swip.setColorSchemeResources(R.color.main_color);

        RecyclerView recyclerView = findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.addItemDecoration(new mItemDecoration(this));

        adapter = new MessageAdapter(this, new ArrayList<HashMap<String, String>>());

        adapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                LogUtil.e("加载更多");
                if (endPage != page) {
                    isLoadMore = true;
                    page++;
                    getHistory();
                }
            }
        }, recyclerView);

        recyclerView.setAdapter(adapter);

        TextView textView = new TextView(this);
        Drawable d        = ContextCompat.getDrawable(this, R.drawable.load_nothing);
        d.setBounds(0, 0, DimenUtils.dip2px(this, 150), DimenUtils.dip2px(this, 150) * d.getIntrinsicHeight() / d.getIntrinsicWidth());
        textView.setCompoundDrawables(null, d, null, null);
        textView.setCompoundDrawablePadding(DimenUtils.dip2px(this, 10));
        textView.setText(mApplication.ST("暂无竞拍记录"));
        adapter.setFooterViewAsFlow(true);


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

    @Override
    protected void onPause() {
        super.onPause();
        ProgressUtil.dismiss();
    }

    private class MessageAdapter extends BaseQuickAdapter<HashMap<String, String>, BaseViewHolder> {
        private Context context;
        private int     gray, main, green;

        public MessageAdapter(Context context, List<HashMap<String, String>> data) {
            super(R.layout.item_bid_history_mine, data);
            this.context = context;
            gray = ContextCompat.getColor(Bid_History_Mine.this, R.color.lightslategray);
            main = ContextCompat.getColor(Bid_History_Mine.this, R.color.main_color);
            green = ContextCompat.getColor(Bid_History_Mine.this, R.color.green);
        }

        @Override
        protected void convert(final BaseViewHolder holder, final HashMap<String, String> map) {
            Glide.with(Bid_History_Mine.this)
                    .load(map.get("image"))
                    .asBitmap()
                    .override(DimenUtils.dip2px(Bid_History_Mine.this, 90)
                            , DimenUtils.dip2px(Bid_History_Mine.this, 90))
                    .centerCrop().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    RoundedBitmapDrawable rbd = RoundedBitmapDrawableFactory.create(getResources(), resource);
                    rbd.setCornerRadius(DimenUtils.dip2px(Bid_History_Mine.this, 5));
                    ((ImageView) holder.getView(R.id.image)).setImageDrawable(rbd);
                }
            });

            TextView        time   = holder.getView(R.id.time_tip);
            final RTextView handle = holder.getView(R.id.handleBid);
            holder.setText(R.id.title, map.get("title"))
                    .setText(R.id.price, "当前价  ￥" + map.get("money"));
            int       status   = Integer.valueOf(map.get("status"));
            final int start    = Integer.valueOf(map.get("start"));
            int       delivery = Integer.valueOf(map.get("delivery"));
            if (status == 0) {
                // 活动还未结束
                time.setText(String.format("%s  结束", map.get("end_time")));
                handle.setText(mApplication.ST("继续竞拍"));
                handle.setEnabled(true);
            } else if (status == 1) {
                // 竞拍失败,活动时间结束
                time.setText(mApplication.ST("竞拍已结束"));
                handle.setText(mApplication.ST("竞拍失败"));
                handle.setBackgroundColorUnable(gray);
                handle.setEnabled(false);
            } else if (status == 2) {
                // 竞拍成功
                if (start == 1) {
                    // 待支付
                    if (System.currentTimeMillis() >= TimeUtils.dataOne(map.get("valid_time"))) {
                        // 未支付,且支付有效期已到
                        time.setText(mApplication.ST("此商品由于您未及时支付，已流拍"));
                        handle.setText(mApplication.ST("物品流拍"));
                        handle.setBackgroundColorUnable(gray);
                        handle.setEnabled(false);
                    } else {
                        time.setText(mApplication.ST("有效期至:" + map.get("valid_time")));
                        handle.setText(mApplication.ST("立即付款"));
                        handle.setEnabled(true);
                    }
                } else if (start == 2) {
                    // 已支付
                    if (delivery == 1) {
                        // 待发货
                        handle.setText(mApplication.ST("等待发货"));
                        handle.setBackgroundColorUnable(main);
                        handle.setEnabled(false);
                    } else {
                        // 已发货
                        handle.setText(mApplication.ST("已发货"));
                        handle.setBackgroundColorUnable(green);
                        handle.setEnabled(false);
                    }
                }


            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent itent = new Intent(Bid_History_Mine.this, AuctionDetail.class);
                    itent.putExtra("id", map.get("auct_id"));
                    startActivity(itent);
                }
            });

            handle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(handle.getText().toString().equals(mApplication.ST("继续竞拍"))){
                        Intent itent = new Intent(Bid_History_Mine.this, AuctionDetail.class);
                        itent.putExtra("id", map.get("auct_id"));
                        startActivity(itent);
                    }else if(handle.getText().toString().equals(mApplication.ST("立即付款"))){
                        Intent itent = new Intent(Bid_History_Mine.this, Dingdan_commit.class);
                        ArrayList<HashMap<String, String>> list = new ArrayList<>();
                        list.add(map);
                        itent.putExtra("list",list);
                        startActivity(itent);
                    }

                }
            });

        }
    }

    private void getHistory() {
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
            LogUtil.e("获取我的出价记录：：" + js);
            OkGo.post(Constants.AuctionMyBid)
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
                                } else if (isLoadMore) {
                                    isLoadMore = false;
                                    if (list.size() < pageSize) {
                                        endPage = page;
                                        adapter.addData(list);
                                        adapter.loadMoreEnd(false);
                                    } else {
                                        adapter.loadMoreComplete();
                                        adapter.addData(list);
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
        adapter.setEnableLoadMore(true);
        getHistory();
    }
}
