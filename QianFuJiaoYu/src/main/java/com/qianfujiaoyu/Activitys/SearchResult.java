package com.qianfujiaoyu.Activitys;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.qianfujiaoyu.Base.BaseActivity;
import com.qianfujiaoyu.R;
import com.qianfujiaoyu.Utils.AnalyticalJSON;
import com.qianfujiaoyu.Utils.ApisSeUtil;
import com.qianfujiaoyu.Utils.Constants;
import com.qianfujiaoyu.Utils.DimenUtils;
import com.qianfujiaoyu.Utils.ImageUtil;
import com.qianfujiaoyu.Utils.LogUtil;
import com.qianfujiaoyu.Utils.Network;
import com.qianfujiaoyu.Utils.PreferenceUtil;
import com.qianfujiaoyu.Utils.TimeUtils;
import com.qianfujiaoyu.Utils.ToastUtil;
import com.qianfujiaoyu.Utils.mApplication;
import com.qianfujiaoyu.View.mItemDeraction;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 作者：因陀罗网 on 2017/5/19 15:16
 * 公司：成都因陀罗网络科技有限公司
 */

public class SearchResult extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener,View.OnClickListener
{
    private SwipeRefreshLayout swip;
    private RecyclerView recyclerView;
    private int page=1;
    private int endPage=-1;
    private boolean isLoadMore=false;
    private boolean isRefresh;
    private classAdapter adapter;
    @Override
    public int getLayoutId() {
        return R.layout.list_classes;
    }

    @Override
    public void initView() {
        swip= (SwipeRefreshLayout) findViewById(R.id.swip);
        swip.setColorSchemeResources(R.color.main_color);
        swip.setOnRefreshListener(this);
        recyclerView= (RecyclerView) findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new mItemDeraction(2, Color.parseColor("#b6b6b6")));
        adapter=new classAdapter(this,new ArrayList<HashMap<String, String>>());
        adapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
        adapter.setEmptyView(mApplication.getLoadNothing(R.drawable.load_nothing,"暂无班级\n\n下拉刷新",200));
        recyclerView.setAdapter(adapter);
        findViewById(R.id.back).setVisibility(View.VISIBLE);
        ((ImageView) findViewById(R.id.back)).setImageBitmap(ImageUtil.readBitMap(this,R.drawable.back));
        ((TextView) findViewById(R.id.title)).setText("搜索结果");
    }

    // TODO: 2017/5/18  获取数据
    private void getData() {
        if(!Network.HttpTest(mApplication.getInstance())){
            swip.setRefreshing(false);
            return;
        }
        JSONObject js=new JSONObject();
        try {
            js.put("title", getIntent().getStringExtra("content"));
            js.put("m_id",Constants.M_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m=ApisSeUtil.i(js);
        OkGo.post(Constants.SearchClasses).tag(this)
                .params("key",m.K())
                .params("msg",m.M())
                .execute(new AbsCallback<ArrayList<HashMap<String,String>>>() {
                    @Override
                    public void onSuccess(ArrayList<HashMap<String, String>> l, Call call, Response response) {
                        if (l != null) {
                            LogUtil.e(l + "");
                            if (isRefresh) {
                                adapter.setNewData(l);
                                isRefresh = false;
                                swip.setRefreshing(false);
                            }
                        }
                    }

                    @Override
                    public ArrayList<HashMap<String, String>> convertSuccess(Response response) throws Exception {
                        return AnalyticalJSON.getList_zj(response.body().string());
                    }
                });
    }

    private static  class classAdapter extends BaseQuickAdapter<HashMap<String,String>> {
        private Context context;
        public classAdapter(Context context,ArrayList<HashMap<String, String>> data) {
            super(R.layout.item_search_class, data);
            this.context=context;
        }

        @Override
        protected void convert(BaseViewHolder holder, final HashMap<String, String> map) {
            Glide.with(context).load(map.get("image"))
                    .override(DimenUtils.dip2px(context,90)
                            ,DimenUtils.dip2px(context,90))
                    .placeholder(R.drawable.indra)
                    .fitCenter()
                    .into((ImageView) holder.getView(R.id.image));
            holder.setText(R.id.name,map.get("title"))
                    .setText(R.id.people,"班级人数:"+map.get("num")+"人")
                    .setText(R.id.time,"创建时间:"+ TimeUtils.getTrueTimeStr(map.get("time")));
            holder.getView(R.id.jiaru).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JSONObject js=new JSONObject();
                    try {
                        js.put("class_id",map.get("id"));
                        js.put("m_id",Constants.M_id);
                        js.put("user_id", PreferenceUtil.getUserIncetance(mApplication.getInstance()).getString("user_id",""));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m=ApisSeUtil.i(js);
                    OkGo.post(Constants.ApplyIn).params("key",Constants.safeKey)
                            .params("key",m.K())
                            .params("msg",m.M())
                            .execute(new AbsCallback<HashMap<String,String>>() {
                                @Override
                                public void onSuccess(HashMap<String,String> map, Call call, Response response) {
                                    if(map!=null){
                                        switch (map.get("code")){
                                            case "000":
                                                ToastUtil.showToastShort("提交成功");
                                                break;
                                            case "003":
                                                ToastUtil.showToastShort("您已经是该班级成员了");
                                                break;
                                            case "002":
                                                ToastUtil.showToastShort("加入申请已提交，请勿频繁提交");
                                                break;
                                        }
                                    }
                                }

                                @Override
                                public HashMap<String,String> convertSuccess(Response response) throws Exception {
                                    return AnalyticalJSON.getHashMap(response.body().string());
                                }
                            });
                }
            });

        }
    }

    @Override
    public boolean setEventBus() {
        return false;
    }

    @Override
    public boolean isMainColor() {
        return true;
    }

    @Override
    public void doThings() {
        swip.post(new Runnable() {
            @Override
            public void run() {
                swip.setRefreshing(true);
                onRefresh();
            }
        });
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
            case R.id.right1://管理员进入班级申请界面
                Intent intent=new Intent(this,ApplyShenhe.class);
                startActivity(intent);
                break;
        }
    }
}
