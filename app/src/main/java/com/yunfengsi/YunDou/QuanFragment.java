package com.yunfengsi.YunDou;

import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yunfengsi.R;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.TimeUtils;
import com.yunfengsi.Utils.mApplication;
import com.yunfengsi.View.mItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 作者：因陀罗网 on 2018/4/23 15:45
 * 公司：成都因陀罗网络科技有限公司
 */
public class QuanFragment extends Fragment implements DuiHuanContract.IFView {
    private View view;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swip;
    private MessageAdapter adapter;

    QuanFramgentPresenterImpl quanPresenter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_quan_duihuan,container,false);
        swip = (SwipeRefreshLayout) view.findViewById(R.id.swip);
        quanPresenter=new QuanFramgentPresenterImpl(this);
        EventBus.getDefault().register(this);
        swip.setOnRefreshListener(quanPresenter);
        swip.setColorSchemeResources(R.color.main_color);


        recyclerView = (RecyclerView) view.findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new mItemDecoration(getActivity()));

        adapter = new MessageAdapter(getActivity(), new ArrayList<HashMap<String, String>>());
        adapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        adapter.setOnLoadMoreListener(quanPresenter, recyclerView);
        recyclerView.setAdapter(adapter);

        TextView textView = new TextView(getActivity());
        Drawable d = ContextCompat.getDrawable(getActivity(), R.drawable.load_nothing);
        d.setBounds(0, 0, DimenUtils.dip2px(getActivity(), 150), DimenUtils.dip2px(getActivity(), 150) * d.getIntrinsicHeight() / d.getIntrinsicWidth());
        textView.setCompoundDrawables(null, d, null, null);
        textView.setCompoundDrawablePadding(DimenUtils.dip2px(getActivity(), 10));
        if(getSortId().equals("0")){
            textView.setText(mApplication.ST("暂无福利券"));
        }else{
            textView.setText(mApplication.ST("暂无"+getArguments().getString("name")));
        }

        adapter.setFooterViewAsFlow(true);


        textView.setGravity(Gravity.CENTER);
        ViewGroup.MarginLayoutParams vl = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        vl.topMargin = DimenUtils.dip2px(getActivity(), 180);
        textView.setLayoutParams(vl);
        adapter.setEmptyView(textView);

        swip.post(new Runnable() {
            @Override
            public void run() {
                swip.setRefreshing(true);
                quanPresenter.onRefresh();
            }
        });
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                HashMap<String, String> map= (HashMap<String, String>) adapter.getItem(position);

                switch (map.get("type")){

                }
                Intent intent=new Intent(getActivity(),QuanDetail.class);
                intent.putExtra("map", ((HashMap) adapter.getItem(position)));
                startActivity(intent);
            }
        });
        return view;

    }
    @Subscribe
    public void onYunDouChanged(YunDouHome.YunDouEvent yunDouEvent) {
        LogUtil.e("福利券数量改变");
        quanPresenter.onRefresh();
    }
    @Override
    public String  getSortId() {
        return getArguments().getString("sort")==null?"0":getArguments().getString("sort");
    }

    @Override
    public void hideSwip() {
        swip.setRefreshing(false);
    }

    @Override
    public MessageAdapter getAdapter() {
        return adapter;
    }

    public static class MessageAdapter extends BaseQuickAdapter<HashMap<String, String>, BaseViewHolder> {
        private Context context;

        public MessageAdapter(Context context, List<HashMap<String, String>> data) {
            super(R.layout.item_duihuan_center, data);
            this.context = context;
        }

        @Override
        protected void convert(BaseViewHolder holder, HashMap<String, String> map) {
            Glide.with(context).load(map.get("image")).animate(R.anim.left_in)
                    .into((ImageView) holder.getView(R.id.image));
            if(System.currentTimeMillis()>=TimeUtils.dataOne(map.get("end_time"))){
                holder.setText(R.id.date, mApplication.ST("已过期"));
                holder.setTextColor(R.id.date, ContextCompat.getColor(context,R.color.gray));
            }else{
                holder.setTextColor(R.id.date,ContextCompat.getColor(context,R.color.wordblack));
                if(map.get("type").equals("1")){
                    holder.setText(R.id.date, mApplication.ST("消耗"+map.get("cost")+"云豆    有效期至："+ TimeUtils.getTrueTimeStr(map.get("end_time"))));

                }else{
                    holder.setText(R.id.date, mApplication.ST("消耗"+map.get("cost")+"云豆"));

                }
            }


        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
