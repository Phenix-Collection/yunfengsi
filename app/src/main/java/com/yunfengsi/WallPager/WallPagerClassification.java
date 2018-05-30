package com.yunfengsi.WallPager;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
import com.yunfengsi.View.mItemDecoration;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 作者：因陀罗网 on 2017/9/9 05:15
 * 公司：成都因陀罗网络科技有限公司
 */

public class WallPagerClassification extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private View               view;
    private RecyclerView       recyclerView;
    private SwipeRefreshLayout swip;
    private fenleiAdapter      adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_wallpager_classification, container, false);
        initView(view);
        return view;

    }

    private void initView(View view) {

        swip = (SwipeRefreshLayout) view.findViewById(R.id.swip);
        swip.setOnRefreshListener(this);
        swip.setColorSchemeResources(R.color.main_color);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new mItemDecoration(getActivity()));
        adapter = new fenleiAdapter(new ArrayList<HashMap<String, String>>());
        recyclerView.setAdapter(adapter);

        swip.post(new Runnable() {
            @Override
            public void run() {
                swip.setRefreshing(true);
                getTitles();
            }
        });

        FrameLayout all    = (FrameLayout) LayoutInflater.from(getActivity()).inflate(R.layout.footer_fenlei, null);
        TextView    tv_all = (TextView) all.findViewById(R.id.all);
        tv_all.setOnClickListener(this);
        adapter.addFooterView(all);
        tv_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), Search_Result.class);
//                startActivity(intent);
            }
        });

        TextView textView = new TextView(getActivity());
        Drawable d1       = ContextCompat.getDrawable(getActivity(), R.drawable.load_nothing);
        d1.setBounds(0, 0, DimenUtils.dip2px(getActivity(), 150), DimenUtils.dip2px(getActivity(), 150) * d1.getIntrinsicHeight() / d1.getIntrinsicWidth());
        textView.setCompoundDrawables(null, d1, null, null);
        textView.setCompoundDrawablePadding(DimenUtils.dip2px(getActivity(), 10));
        textView.setText("暂无壁纸分类");
        textView.setGravity(Gravity.CENTER);
        ViewGroup.MarginLayoutParams vl = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        vl.topMargin = DimenUtils.dip2px(getActivity(), 140);
        textView.setLayoutParams(vl);
        adapter.setEmptyView(textView);
        adapter.setHeaderFooterEmpty(true,false);

//        adapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
//            @Override
//            public void onItemClick(View view, int i) {
//                Intent intent = new Intent(getActivity(), Search_Result.class);
//                intent.putExtra("id", adapter.getData().get(i).get("id"));
//                intent.putExtra("name", adapter.getData().get(i).get("name"));
//                startActivity(intent);
//            }
//        });

    }

    /**
     * 获取数据
     */
    private void getTitles() {
        JSONObject js=new JSONObject();
        try {
            js.put("m_id", Constants.M_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m=ApisSeUtil.i(js);
        OkGo.post(Constants.WallPaperTypeList)
                .params("key",m.K())
                .params("msg",m.M())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        HashMap<String,String > map= AnalyticalJSON.getHashMap(s);
                        if(map!=null){
                            ArrayList<HashMap<String,String >> list=AnalyticalJSON.getList_zj(map.get("msg"));
                            if(list!=null){
                                adapter.setNewData(list);

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

    @Override
    public void onRefresh() {


        getTitles();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.all://所有商品
                break;
        }
    }

    public class fenleiAdapter extends BaseQuickAdapter<HashMap<String, String>, BaseViewHolder> {
        public fenleiAdapter(List<HashMap<String, String>> data) {
            super(R.layout.item_fenlei, data);
        }

        @Override
        protected void convert(BaseViewHolder holder, HashMap<String, String> map) {
            holder.setText(R.id.type, map.get("name"))
                    .setText(R.id.abs, "查看更多");
            Glide.with(getActivity()).load(map.get("image"))
                    .override(DimenUtils.dip2px(getActivity(), 80), DimenUtils.dip2px(getActivity(), 80))
                    .fitCenter().into((ImageView) holder.getView(R.id.image));
        }
    }
}
