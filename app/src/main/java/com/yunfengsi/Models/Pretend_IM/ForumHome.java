package com.yunfengsi.Models.Pretend_IM;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.mApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 作者：luZheng on 2018/07/13 16:25
 */
public class ForumHome extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swip;
    private MessageAdapter     adapter;
    private int     pageSize   = 10;
    private int     page       = 1;
    private int     endPage    = -1;
    private boolean isLoadMore = false;
    private boolean isRefresh  = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forum_home);
        getWindow().setBackgroundDrawableResource(R.color.window_light_gray_bg);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));

        findViewById(R.id.title_back).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.title_title)).setText("论坛");
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

        adapter.setEmptyView(mApplication.getEmptyView(this, 150, "暂无论坛内容"));

        swip.post(new Runnable() {
            @Override
            public void run() {
                swip.setRefreshing(true);
                onRefresh();
            }
        });

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
            LogUtil.e("获取论坛信息：：" + js);
            OkGo.post(Constants.Tribunelist)
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
        getNotice();
    }
}
