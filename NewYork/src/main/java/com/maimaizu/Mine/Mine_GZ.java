package com.maimaizu.Mine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.maimaizu.Activitys.Home2_Detail;
import com.maimaizu.Activitys.NewHouseActivity;
import com.maimaizu.Activitys.ZuFangActivity;
import com.maimaizu.Adapter.mBaseAdapter;
import com.maimaizu.R;
import com.maimaizu.Utils.Constants;
import com.maimaizu.Utils.DimenUtils;
import com.maimaizu.Utils.LogUtil;
import com.maimaizu.Utils.StatusBarCompat;
import com.maimaizu.Utils.ToastUtil;
import com.maimaizu.Utils.mApplication;
import com.maimaizu.View.mItemDecoration;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/7/21.
 */
public class Mine_GZ extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private TextView textView;
    private RecyclerView recyclerView;
    private List<mBaseAdapter.OneMulitem> list;
    private LayoutInflater layoutInflater;
    private mBaseAdapter adapter;
    private SharedPreferences sp;
    private ImageView back;
    private TextView title;
    private static final String TAG = "Mine_GZ";
    private SwipeRefreshLayout swip;
    private int page = 1;
    private int endPage = -1;
    private boolean isLoadMore = false;
    private boolean isRefresh = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_guanzhu_fragment);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        back = (ImageView) findViewById(R.id.title_back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        back.setImageResource(R.drawable.back);
        title = (TextView) findViewById(R.id.title_title);
        title.setText(mApplication.ST("关注"));
        list = new ArrayList<>();
        adapter = new mBaseAdapter(this, list);
        swip = (SwipeRefreshLayout) findViewById(R.id.swip);
        swip.setOnRefreshListener(this);
        swip.setColorSchemeResources(R.color.main_color);
        sp = getSharedPreferences("user", Context.MODE_PRIVATE);
        layoutInflater = LayoutInflater.from(mApplication.getInstance());
        recyclerView = (RecyclerView) findViewById(R.id.mine_guanzhu_listview);
        textView = new TextView(this);
        textView.setText(mApplication.ST("暂无关注\n下拉刷新"));
        Drawable drawable = ActivityCompat.getDrawable(this, R.drawable.load_nothing);
        drawable.setBounds(0, 0, DimenUtils.dip2px(this, 120), DimenUtils.dip2px(this, 120));
        textView.setCompoundDrawables(null, drawable, null, null);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setPadding(0, DimenUtils.dip2px(this, 20), 0, 0);
        ViewGroup.LayoutParams vl = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(vl);


        recyclerView.addItemDecoration(new mItemDecoration(this));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.openLoadMore(10, true);
        adapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        adapter.setEmptyView(textView);
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (endPage != page) {
                    isLoadMore = true;
                    page++;
                    loadData();
                }
            }
        });
        adapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                int type = Integer.valueOf(((mBaseAdapter.OneMulitem) adapter.getItem(i)).getItemType());
                switch (type) {
                    case 1:
                        Intent intent1 = new Intent(Mine_GZ.this, NewHouseActivity.class);
                        intent1.putExtra("id", ((mBaseAdapter.OneMulitem) adapter.getItem(i)).getId());
                        startActivity(intent1);
                        break;
                    case 2:
                        Intent intent = new Intent(Mine_GZ.this, Home2_Detail.class);
                        intent.putExtra("id", ((mBaseAdapter.OneMulitem) adapter.getItem(i)).getId());
                        startActivity(intent);
                        break;
                    case 3:
                        Intent intent2 = new Intent(Mine_GZ.this, ZuFangActivity.class);
                        intent2.putExtra("id", ((mBaseAdapter.OneMulitem) adapter.getItem(i)).getId());
                        startActivity(intent2);
                        break;
                }

            }
        });
        recyclerView.setAdapter(adapter);

        swip.post(new Runnable() {
            @Override
            public void run() {
                swip.setRefreshing(true);
                onRefresh();
            }
        });


    }


    Handler handler = new Handler();

    private void loadData() {

        OkGo.post(Constants.getHouseKeeps).tag(TAG).params("key", Constants.safeKey).params("user_id", sp.getString("user_id", ""))
                .params("m_id", Constants.M_id)
                .execute(new AbsCallback<ArrayList<mBaseAdapter.OneMulitem>>() {
                    @Override
                    public ArrayList<mBaseAdapter.OneMulitem> convertSuccess(Response response) throws Exception {
                        if (response != null) {
                            String data = response.body().string();
                            JSONArray jsonArray = new JSONArray(data);
                            LogUtil.e("!@!@!@!@!##" + jsonArray);
                            ArrayList<mBaseAdapter.OneMulitem> oneList = new ArrayList<mBaseAdapter.OneMulitem>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                LogUtil.e("i::_________" + i);
                                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                                mBaseAdapter.OneMulitem o = adapter.getOneMulitem();
                                o.setArea(jsonObject.getString("area"));
                                o.setHousetype(jsonObject.getString("housetype"));
                                o.setId(jsonObject.getString("id"));
                                o.setImage(jsonObject.getString("image"));
                                o.setMoney(jsonObject.getString("money"));
                                o.setPoint(jsonObject.getString("point"));
//                                o.setTags(new JSONArray(jsonObject.getString("bq")));
                                o.setVillage(jsonObject.getString("village"));
                                o.setTitle(jsonObject.getString("title"));
                                o.setItemType(Integer.valueOf(jsonObject.getString("type")));
                                oneList.add(o);
                            }
                            return oneList;
                        }
                        return null;
                    }

                    @Override
                    public void onSuccess(ArrayList<mBaseAdapter.OneMulitem> l, Call call, Response response) {

                        if (l != null) {
                            LogUtil.e(l + "");
                            if (isRefresh) {
                                adapter.setNewData(l);
                                isRefresh = false;
                                swip.setRefreshing(false);
                            } else if (isLoadMore) {
                                isLoadMore = false;
                                if (l.size() < 10) {
                                    ToastUtil.showToastShort("已经没有更多数据啦", Gravity.CENTER);
                                    endPage = page;
                                    adapter.notifyDataChangedAfterLoadMore(l, false);
                                } else {
                                    adapter.notifyDataChangedAfterLoadMore(l, true);
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        swip.setRefreshing(false);
                    }
                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(TAG);
    }

    @Override
    public void onRefresh() {
        page = 1;
        endPage = -1;
        isRefresh = true;
        loadData();
    }


}
