package com.maimaizu.Activitys;

import android.graphics.drawable.Drawable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.maimaizu.Adapter.mFangYuanAdapter;
import com.maimaizu.Base.BaseActivity;
import com.maimaizu.R;
import com.maimaizu.Utils.AnalyticalJSON;
import com.maimaizu.Utils.Constants;
import com.maimaizu.Utils.DimenUtils;
import com.maimaizu.Utils.LogUtil;
import com.maimaizu.Utils.PreferenceUtil;
import com.maimaizu.Utils.StatusBarCompat;
import com.maimaizu.Utils.mApplication;
import com.maimaizu.View.mItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/5/8.
 */

public class Mine_FangYuan extends BaseActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout swip;
    private RecyclerView recyclerView;
    private mFangYuanAdapter adapter;
    private TextView loadNothing;

    @Override
    public int getLayoutId() {
        return R.layout.mine_fangyuan;
    }

    @Override
    public void initView() {
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        loadNothing = new TextView(this);
        loadNothing.setText(mApplication.ST("暂无房源\n下拉刷新"));
        Drawable drawable = ActivityCompat.getDrawable(this, R.drawable.load_nothing);
        drawable.setBounds(0, 0, DimenUtils.dip2px(this, 120), DimenUtils.dip2px(this, 120));
        loadNothing.setCompoundDrawables(null, drawable, null, null);
        loadNothing.setGravity(Gravity.CENTER_HORIZONTAL);
        loadNothing.setPadding(0, DimenUtils.dip2px(this, 20), 0, 0);
        ViewGroup.LayoutParams vl = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadNothing.setLayoutParams(vl);

        swip = (SwipeRefreshLayout) findViewById(R.id.swip);
        swip.setColorSchemeResources(R.color.main_color);
        swip.setOnRefreshListener(this);


        recyclerView = (RecyclerView) findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new mItemDecoration(this));
        adapter = new mFangYuanAdapter(this,R.layout.item_mine_fangyuan, new ArrayList());
        adapter.setEmptyView(loadNothing);
        adapter.openLoadAnimation();

//        ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(adapter);
//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
//        itemTouchHelper.attachToRecyclerView(recyclerView);

// 开启拖拽
//        adapter.enableDragItem(itemTouchHelper, R.id.root, true);
//        adapter.setOnItemDragListener(new OnItemDragListener() {
//            @Override
//            public void onItemDragStart(RecyclerView.ViewHolder viewHolder, int i) {
//                LogUtil.e("onItemDragStart~~!~!~!~!~!~!~");
//            }
//
//            @Override
//            public void onItemDragMoving(RecyclerView.ViewHolder viewHolder, int i, RecyclerView.ViewHolder viewHolder1, int i1) {
//                LogUtil.e("onItemDragMoving~~!~!~!~!~!~!~");
//            }
//
//            @Override
//            public void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int i) {
//                LogUtil.e("onItemDragEnd~~!~!~!~!~!~!~");
//            }
//        });


//        adapter.enableSwipeItem();
//        adapter.setOnItemSwipeListener(new OnItemSwipeListener() {
//            @Override
//            public void onItemSwipeStart(RecyclerView.ViewHolder viewHolder, int i) {
//                LogUtil.e("onItemSwipeStart~~!~!~!~!~!~!~");
//            }
//
//            @Override
//            public void clearView(RecyclerView.ViewHolder viewHolder, int i) {
//                LogUtil.e("clearView~~!~!~!~!~!~!~");
//            }
//
//            @Override
//            public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int i) {
//                LogUtil.e("onItemSwiped~~!~!~!~!~!~!~");
//
//            }
//
//            @Override
//            public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float v, float v1, boolean b) {
////                LogUtil.e("onItemSwipeMoving~~!~!~!~!~!~!~");
//            }
//        });
        recyclerView.setAdapter(adapter);
        swip.post(new Runnable() {
            @Override
            public void run() {
                swip.setRefreshing(true);
                getData();
            }
        });
    }



    @Override
    public void setOnClick() {

    }

    @Override
    public boolean setEventBus() {
        return false;
    }

    @Override
    public void doThings() {
        getData();
    }

    // TODO: 2017/5/8 获取数据
    private void getData() {
        OkGo.post(Constants.FangYuanList).tag(this).params("key", Constants.safeKey)
                .params("m_id", Constants.M_id)
                .params("user_id", PreferenceUtil.getUserIncetance(this).getString("user_id", ""))
                .execute(new AbsCallback<ArrayList<HashMap<String, String>>>() {
                    @Override
                    public void onSuccess(ArrayList<HashMap<String, String>> list, Call call, Response response) {
                        LogUtil.e(list + "");
                        adapter.setNewData(list);
                    }

                    @Override
                    public ArrayList<HashMap<String, String>> convertSuccess(Response response) throws Exception {
                        return AnalyticalJSON.getList_zj(response.body().string());
                    }

                    @Override
                    public void onAfter(ArrayList<HashMap<String, String>> hashMaps, Exception e) {
                        super.onAfter(hashMaps, e);
                        swip.setRefreshing(false);
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    @Override
    public void onRefresh() {
        getData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(this);
    }
}
