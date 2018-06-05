package com.yunfengsi.Managers;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.sdk.android.push.AndroidPopupActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.yunfengsi.Adapter.PingLunActivity;
import com.yunfengsi.Fragment.Mine_GYQD;
import com.yunfengsi.Model_activity.Mine_activity_list;
import com.yunfengsi.Model_activity.activity_Detail;
import com.yunfengsi.NianFo.NianFo;
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
import com.yunfengsi.Utils.mApplication;
import com.yunfengsi.WallPaper.WallPaperUserHome;
import com.yunfengsi.XuanzheActivity;
import com.yunfengsi.ZiXun_Detail;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 作者：因陀罗网 on 2017/11/21 13:50
 * 公司：成都因陀罗网络科技有限公司
 */

public class MessageCenter extends AndroidPopupActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final int ZiXun             = 1;
    private static final int HuoDong           = 2;
    private static final int GongYang          = 3;
    private static final int ZhuXue            = 4;
    private static final int PINGLUN           = 5;
    private static final int MineHuodong       = 6;
    private static final int GongYangPay       = 7;
    private static final int ZhuXuePay         = 8;
    private static final int WallPaperVerified = 9;
    private static final int QianDaoToMineActivity = 10;
    private RecyclerView       recyclerView;
    private SwipeRefreshLayout swip;
    private MessageAdapter     adapter;
    private int     pageSize   = 10;
    private int     page       = 1;
    private int     endPage    = -1;
    private boolean isLoadMore = false;
    private boolean isRefresh  = false;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mApplication.getInstance().romoveActivity(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.message_center);
        mApplication.getInstance().addActivity(this);

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
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(BaseQuickAdapter a, View view, int position) {
                Intent                  intent = new Intent();
                HashMap<String, String> map    = adapter.getData().get(position);
                if (map.get("type") != null) {
                    switch (Integer.valueOf(map.get("type"))) {
                        case ZiXun:
                            intent.setClass(MessageCenter.this, ZiXun_Detail.class);
                            break;
                        case HuoDong:
                            intent.setClass(MessageCenter.this, activity_Detail.class);
                            break;
                        case GongYang:
                            intent.setClass(MessageCenter.this, XuanzheActivity.class);
                            break;
                        case ZhuXue:
                            intent.setClass(MessageCenter.this, NianFo.class);
                            break;
                        case MineHuodong:
                            intent.setClass(MessageCenter.this, Mine_activity_list.class);
                            break;
                        case PINGLUN:
                            intent.setClass(MessageCenter.this, PingLunActivity.class);
                            break;
                        case ZhuXuePay:
                        case GongYangPay:
                            intent.setClass(MessageCenter.this, Mine_GYQD.class);
                            break;
                        case WallPaperVerified:
                            intent.setClass(MessageCenter.this, WallPaperUserHome.class);
                            intent.putExtra("mine", true);

                            break;
                        case QianDaoToMineActivity:
                            intent.setClass(MessageCenter.this, Mine_activity_list.class);
                            break;
//                        case mReceiver.QiYuan:
//                            intent.setClass(MessageCenter.this, BlessTree.class);
//                            break;
//                        case mReceiver.Fojin:
//                            intent.setClass(MessageCenter.this, BookList.class);
//                            break;
//                        case mReceiver.Bushi:
//                            intent.setClass(MessageCenter.this, Fortune.class);
//                            break;
//                        case mReceiver.ZuoChan:
//                            intent.setClass(MessageCenter.this, Meditation.class);
//                            break;


                    }

                    intent.putExtra("id", map.get("type_id"));
                    startActivity(intent);
                }
            }
        });
        recyclerView.setAdapter(adapter);

        View     empty    = LayoutInflater.from(this).inflate(R.layout.empty_layout, null);
        TextView textView = empty.findViewById(R.id.empty);
        Drawable d        = ContextCompat.getDrawable(this, R.drawable.load_nothing);
        d.setBounds(0, 0, DimenUtils.dip2px(this, 150), DimenUtils.dip2px(this, 150) * d.getIntrinsicHeight() / d.getIntrinsicWidth());
        textView.setCompoundDrawables(null, d, null, null);
        textView.setText(mApplication.ST("暂无通知"));
        adapter.setEmptyView(empty);

        swip.post(new Runnable() {
            @Override
            public void run() {
                swip.setRefreshing(true);
                onRefresh();
            }
        });
    }

    @Override
    protected void onSysNoticeOpened(String s, String s1, Map<String, String> map) {
        LogUtil.e("通知中心 辅助弹窗打开");
    }

    private static class MessageAdapter extends BaseQuickAdapter<HashMap<String, String>, BaseViewHolder> {
        Drawable next;
        int      dp30;

        public MessageAdapter(Context context, List<HashMap<String, String>> data) {
            super(R.layout.item_message_center, data);
            dp30 = DimenUtils.dip2px(context, 20);
            next = ContextCompat.getDrawable(context, R.drawable.item_tip);
            next.setBounds(0, 0, dp30, dp30);
        }

        @Override
        protected void convert(BaseViewHolder holder, HashMap<String, String> map) {
            if (map.get("type") != null) {
                switch (Integer.valueOf(map.get("type"))) {
                    case ZiXun:
                        holder.setText(R.id.title, "图文");
                        break;
                    case HuoDong:
                        holder.setText(R.id.title, "活动");
                        break;
                    case GongYang:
                        holder.setText(R.id.title, "供养");
                        break;
                    case ZhuXue:
                        holder.setText(R.id.title, "助学");
                        break;

                    case GongYangPay:
                        holder.setText(R.id.title, "供养支付");
                        break;
                    case ZhuXuePay:
                        holder.setText(R.id.title, "助学支付");
                        break;
                    case WallPaperVerified:
                        holder.setText(R.id.title, "壁纸审核结果");
                        break;
//                    case mReceiver.GONGXIU:
//                        holder.setText(R.id.title, "共修");
//                        break;
//                    case mReceiver.BaoMing:
//                        holder.setText(R.id.title, "报名结果");
//                        break;
//                    case mReceiver.TongZhi:
//                        holder.setText(R.id.title, "通知");
//                        break;
                    case PINGLUN:
                        holder.setText(R.id.title, "评论");
                        break;
                    case QianDaoToMineActivity:
                        holder.setText(R.id.title, "签到");
                        break;
//                    case mReceiver.QiYuan:
//                        holder.setText(R.id.title, "祈愿树");
//                        break;
//                    case mReceiver.Fojin:
//                        holder.setText(R.id.title, "佛经");
//                        break;
//                    case mReceiver.Bushi:
//                        holder.setText(R.id.title, "卜事");
//                        break;
//                    case mReceiver.ZuoChan:
//                        holder.setText(R.id.title, "坐禅");
//                        break;
                }
            }
            holder.setText(R.id.content, mApplication.ST(map.get("contents")))
                    .setText(R.id.time, mApplication.ST(TimeUtils.getTrueTimeStr(map.get("time"))))
                    .setText(R.id.time2, mApplication.ST(TimeUtils.getTrueTimeStr(map.get("time"))));
            ((TextView) holder.getView(R.id.detail)).setCompoundDrawables(null, null, next, null);


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
        getNotice();
    }
}
