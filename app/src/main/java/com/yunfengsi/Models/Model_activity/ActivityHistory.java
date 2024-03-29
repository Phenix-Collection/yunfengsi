package com.yunfengsi.Models.Model_activity;

import android.content.Context;
import android.graphics.Typeface;
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
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.yunfengsi.R;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.MD5Utls;
import com.yunfengsi.Utils.Network;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.ProgressUtil;
import com.yunfengsi.Utils.ShareManager;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.TimeUtils;
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

public class ActivityHistory extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {


    private SwipeRefreshLayout swip;
    private MessageAdapter     adapter;
    private int     pageSize   = 20;
    private int     page       = 1;
    private int     endPage    = -1;
    private boolean isLoadMore = false;
    private boolean isRefresh  = false;

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
        ((TextView) findViewById(R.id.title_title)).setText(mApplication.ST("修行经历"));
        findViewById(R.id.title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ((TextView) findViewById(R.id.handle_right)).setText(mApplication.ST("分享"));


        findViewById(R.id.handle_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String md5   = MD5Utls.stringToMD5(Constants.safeKey);
                String m1    = md5.substring(0, 16);
                String m2    = md5.substring(16, md5.length());
                UMWeb  umWeb = new UMWeb(Constants.FX_host_Ip + "practice" + "/id/" + m1 + PreferenceUtil.getUserId(ActivityHistory.this) + m2 + "/st/" + (mApplication.isChina ? "s" : "t"));
                umWeb.setTitle(mApplication.ST(PreferenceUtil.getUserIncetance(ActivityHistory.this).getString("pet_name", "") + "  正在这里修行"));
                umWeb.setThumb(new UMImage(ActivityHistory.this, R.drawable.indra_share));
                umWeb.setDescription("这是我的修行经历，快来看看吧");
                new ShareManager().shareWeb(umWeb, ActivityHistory.this);
            }
        });
        swip = findViewById(R.id.swip);
        swip.setOnRefreshListener(this);
        swip.setColorSchemeResources(R.color.main_color);

        RecyclerView recyclerView = findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new mItemDecoration(this));

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
        textView.setText(mApplication.ST("暂无修行经历\n\n快去报名参加活动吧"));
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

    private static class MessageAdapter extends BaseQuickAdapter<HashMap<String, String>, BaseViewHolder> {
        private Context context;

        public MessageAdapter(Context context, List<HashMap<String, String>> data) {
            super(R.layout.item_fortune_history, data);
            this.context = context;
        }

        @Override
        protected void convert(BaseViewHolder holder, HashMap<String, String> map) {
            ((TextView) holder.getView(R.id.title)).setTextSize(18);
            holder.setText(R.id.title, mApplication.ST(map.get("title")))
                    .setText(R.id.time, mApplication.ST(TimeUtils.getTrueTimeStr(map.get("time"))));
            holder.setGone(R.id.msg, false);
            ((TextView) holder.getView(R.id.title)).setTypeface(Typeface.MONOSPACE);
//            LogUtil.e("当前下标：："+holder.getAdapterPosition());
//            switch (holder.getAdapterPosition()%5){
//                case 0:
//                    ((TextView) holder.getView(R.id.title)).setTypeface(Typeface.MONOSPACE);
//                    ((TextView) holder.getView(R.id.title)).append("南 无 阿 弥 陀 佛   等宽字体");
//                    break;
//                case 1:
//                    ((TextView) holder.getView(R.id.title)).setTypeface(Typeface.DEFAULT);
//                    ((TextView) holder.getView(R.id.title)).append("南 无 阿 弥 陀 佛   默认字体");
//                    break;
//                case 2:
//                    ((TextView) holder.getView(R.id.title)).setTypeface(Typeface.DEFAULT_BOLD);
//                    ((TextView) holder.getView(R.id.title)).append("南无阿弥陀佛   默认粗体");
//                    break;
//                case 3:
//                    ((TextView) holder.getView(R.id.title)).setTypeface(Typeface.SANS_SERIF);
//                    ((TextView) holder.getView(R.id.title)).append("南无阿弥陀佛   SANS_SERIF体");
//                    break;
//                case 4:
//                    ((TextView) holder.getView(R.id.title)).setTypeface(Typeface.SERIF);
//                    ((TextView) holder.getView(R.id.title)).append("南无阿弥陀佛   SERIF体");
//                    break;
//            }


        }
    }

    private void getHistory() {
        if (Network.HttpTest(this)) {
            JSONObject js = new JSONObject();
            try {
                js.put("page", page);
                js.put("m_id", Constants.M_id);
                //有user_id则调相应的用户活动经历  没有则调自己的
                js.put("user_id", getIntent().getStringExtra("user_id") == null ? PreferenceUtil.getUserId(this) : getIntent().getStringExtra("user_id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ApisSeUtil.M m = ApisSeUtil.i(js);
            LogUtil.e("获取修行经历：：" + js);
            OkGo.post(Constants.Practice)
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
                            if (adapter.getData().size() == 0) {
                                findViewById(R.id.handle_right).setVisibility(View.GONE);
                            } else {
                                if (getIntent().getStringExtra("user_id") == null) {
                                    findViewById(R.id.handle_right).setVisibility(View.VISIBLE);
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
