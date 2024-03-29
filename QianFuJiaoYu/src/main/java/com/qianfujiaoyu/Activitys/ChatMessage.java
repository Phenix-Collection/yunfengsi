package com.qianfujiaoyu.Activitys;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
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

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.qianfujiaoyu.R;
import com.qianfujiaoyu.Utils.AnalyticalJSON;
import com.qianfujiaoyu.Utils.ApisSeUtil;
import com.qianfujiaoyu.Utils.Constants;
import com.qianfujiaoyu.Utils.DimenUtils;
import com.qianfujiaoyu.Utils.ImageUtil;
import com.qianfujiaoyu.Utils.LogUtil;
import com.qianfujiaoyu.Utils.Network;
import com.qianfujiaoyu.Utils.PreferenceUtil;
import com.qianfujiaoyu.Utils.StatusBarCompat;
import com.qianfujiaoyu.Utils.TimeUtils;
import com.qianfujiaoyu.Utils.ToastUtil;
import com.qianfujiaoyu.View.mItemDecoration;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

import static com.qianfujiaoyu.R.id.abs;

/**
 * 作者：因陀罗网 on 2017/8/9 11:44
 * 公司：成都因陀罗网络科技有限公司
 */

public class ChatMessage extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout swip;
    private RecyclerView recyclerView;
    private static final int PAGESIZE = 10;
    private int page = 1;
    private int endPage = 0;
    private boolean isLoadMore = false;
    private boolean isRefresh = false;
    private chatMessageAdapter adapter;
    BroadcastReceiver reicever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onRefresh();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(this);
        unregisterReceiver(reicever);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter inte = new IntentFilter("chat");
        registerReceiver(reicever, inte);
        StatusBarCompat.compat(this, ContextCompat.getColor(this,R.color.main_color));

        setContentView(R.layout.chat_message);

        findViewById(R.id.back).setVisibility(View.VISIBLE);
        ((ImageView) findViewById(R.id.back)).setImageBitmap(ImageUtil.readBitMap(this,R.drawable.back));
        findViewById(R.id.back).setOnClickListener(this);
        ((TextView) findViewById(R.id.title)).setText("私信");
        swip = (SwipeRefreshLayout) findViewById(R.id.swip);
        swip.setOnRefreshListener(this);
        swip.setColorSchemeResources(R.color.main_color);

        recyclerView = (RecyclerView) findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new mItemDecoration(this));
        adapter = new chatMessageAdapter(new ArrayList<HashMap<String, String>>());
        adapter.openLoadMore(PAGESIZE, true);
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {

                if (endPage != page) {
                    isLoadMore = true;
                    page++;
                    getChatMessage();
                }
            }
        });
        adapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                Intent intent = new Intent(ChatMessage.this, Ask_Detail.class);
                ArrayList<HashMap<String, String>> list = (ArrayList<HashMap<String, String>>) adapter.getData();
                HashMap<String, String> map = list.get(i);
                if (map.get("user_id").equals(PreferenceUtil.getUserId(ChatMessage.this))) {
                    intent.putExtra("id", map.get("user_friend"));
                } else {
                    intent.putExtra("id", map.get("user_id"));
                }

//                intent.putExtra("id", ((MyItem) adapter.getData().get(i)).getId());
                startActivity(intent);
            }
        });
        TextView textView = new TextView(this);
        Drawable d = ContextCompat.getDrawable(this, R.drawable.load_nothing);
        d.setBounds(0, 0, DimenUtils.dip2px(this, 150), DimenUtils.dip2px(this, 150) * d.getIntrinsicHeight() / d.getIntrinsicWidth());
        textView.setCompoundDrawables(null, d, null, null);
        textView.setCompoundDrawablePadding(DimenUtils.dip2px(this, 10));
        textView.setText("暂无私信");
        textView.setTextColor(Color.BLACK);
        textView.setGravity(Gravity.CENTER);
        ViewGroup.MarginLayoutParams vl = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        vl.topMargin = DimenUtils.dip2px(this, 180);
        textView.setLayoutParams(vl);
        adapter.setEmptyView(textView);
        recyclerView.setAdapter(adapter);
        swip.post(new Runnable() {
            @Override
            public void run() {
                swip.setRefreshing(true);
                onRefresh();
            }
        });

    }

    private void getChatMessage() {
        if (Network.HttpTest(this)) {
            JSONObject js = new JSONObject();
            try {
                js.put("m_id", Constants.M_id);
                js.put("user_id", PreferenceUtil.getUserId(this));
                js.put("page", page);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            LogUtil.e("获取聊天窗口:" + js);
            ApisSeUtil.M m = ApisSeUtil.i(js);
            OkGo.post(Constants.Chat_Message).params("key", m.K())
                    .params("msg", m.M())
                    .tag(this)//
                    .execute(new AbsCallback<ArrayList<HashMap<String, String>>>() {
                        @Override
                        public ArrayList<HashMap<String, String>> convertSuccess(Response response) throws Exception {
                            return AnalyticalJSON.getList_zj(response.body().string());
                        }

                        @Override
                        public void onSuccess(ArrayList<HashMap<String, String>> list, Call call, Response response) {
                            if (isRefresh) {
                                adapter.setNewData(list);
                                isRefresh = false;
                                swip.setRefreshing(false);
                            } else if (isLoadMore) {
                                isLoadMore = false;
                                if (list.size() < PAGESIZE) {
                                    ToastUtil.showToastShort("消息列表加载完毕", Gravity.CENTER);
//                                endPage = page;
                                    adapter.notifyDataChangedAfterLoadMore(list, false);
                                } else {
                                    adapter.notifyDataChangedAfterLoadMore(list, true);
                                }
                            }

                            swip.setRefreshing(false);
                        }

                        @Override
                        public void onAfter(ArrayList<HashMap<String, String>> list, Exception e) {
                            super.onAfter(list, e);
                            swip.setRefreshing(false);
                        }
                    });

        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back) {
            finish();
        }
    }

    @Override
    public void onRefresh() {
        page = 1;
        endPage = -1;
        isRefresh = true;
        adapter.openLoadMore(PAGESIZE, true);
        getChatMessage();
    }

    private class chatMessageAdapter extends BaseQuickAdapter<HashMap<String, String>> {
        public chatMessageAdapter(List<HashMap<String, String>> data) {
            super(R.layout.item_chat_message, data);
        }

        @Override
        protected void convert(BaseViewHolder holder, HashMap<String, String> map) {
            Glide.with(ChatMessage.this).load(map.get("user_image"))
                    .override(DimenUtils.dip2px(ChatMessage.this, 60), DimenUtils.dip2px(ChatMessage.this, 60))
                    .fitCenter()
                    .into((ImageView) holder.getView(R.id.head));
            holder.setText(R.id.userName, map.get("pet_name"))
                    .setText(R.id.time, TimeUtils.getTrueTimeStr(map.get("time")));
            if (!"".equals(map.get("contents"))) {
                holder.setText(R.id.message, map.get("contents"));
            } else if (!"".equals(map.get("image"))) {
                holder.setText(R.id.message, "[图片]");
            } else if (!"".equals(map.get("video")) && map.get("video").endsWith("mp3")) {
                holder.setText(R.id.message, "[语音]");
            } else if (!"".equals(map.get("video")) && map.get("video").endsWith("mp4")) {
                holder.setText(R.id.message, "[视频]");
            }


        }
    }
}
