package com.yunfengsi.Managers.ForManager;

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
import com.ruffian.library.RTextView;
import com.yunfengsi.Managers.AboutPay.Fahuo;
import com.yunfengsi.Managers.AboutPay.MyZhiFuDetail;
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
import com.yunfengsi.Utils.ToastUtil;
import com.yunfengsi.Utils.mApplication;

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

public class AuctionManage extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {


    private SwipeRefreshLayout swip;
    private MessageAdapter     adapter;
    private int     pageSize   = 5;
    private int     page       = 1;
    private int     endPage    = -1;
    private boolean isLoadMore = false;
    private boolean isRefresh  = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.message_center);


        ((ImageView) findViewById(R.id.title_back)).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.title_title)).setText(mApplication.ST("发货管理"));
        ((ImageView) findViewById(R.id.title_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        swip = (SwipeRefreshLayout) findViewById(R.id.swip);
        swip.setOnRefreshListener(this);
        swip.setColorSchemeResources(R.color.main_color);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycle);
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
        textView.setText(mApplication.ST("暂无需发货商品"));
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


        public MessageAdapter(Context context, List<HashMap<String, String>> data) {
            super(R.layout.item_auction_manage, data);
            this.context = context;

        }

        @Override
        protected void convert(final BaseViewHolder holder, final HashMap<String, String> map) {

            TextView        time   = holder.getView(R.id.time_tip);
            final RTextView handle = holder.getView(R.id.handleBid);
            time.setText(mApplication.ST("支付时间:"+map.get("end_time")));
            handle.setText(mApplication.ST("立即发货"));
            holder.setText(R.id.title, map.get("sut_title"))
                    .setText(R.id.price, "当前价  ￥" + map.get("money"));


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent =new Intent(AuctionManage.this, MyZhiFuDetail.class);
                    intent.putExtra("id",map.get("id"));
                    intent.putExtra("snr",map.get("snr"));

                    startActivity(intent);
                }
            });

            handle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(AuctionManage.this, Fahuo.class);
                    intent.putExtra("id",map.get("id"));
                    intent.putExtra("money",map.get("money"));
                    intent.putExtra("user_id",map.get("user_id"));
                    startActivityForResult(intent,666);

                }
            });

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==999){
            onRefresh();
        }
    }

    private void getHistory() {
        if (Network.HttpTest(this)) {
            JSONObject js = new JSONObject();
            try {
                js.put("page", page);
                js.put("m_id", Constants.M_id);
                js.put("admin_id", PreferenceUtil.getUserId(this));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ApisSeUtil.M m = ApisSeUtil.i(js);
            LogUtil.e("获取发货管理列表：：" + js);
            OkGo.post(Constants.DeliveryMangeList)
                    .tag(this)
                    .params("key", m.K())
                    .params("msg", m.M())
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            HashMap<String,String > map=AnalyticalJSON.getHashMap(s);
                            if(map!=null){
                                if("005".equals(map.get("code"))){
                                    ToastUtil.showToastShort(getString(R.string.haveNoPermission));
                                    finish();
                                    return;
                                }
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
                                            adapter.loadMoreComplete();
                                            adapter.addData(list);
                                        }
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

        page = 1;
        isRefresh = true;
        adapter.setEnableLoadMore(true);
        getHistory();
    }
}
