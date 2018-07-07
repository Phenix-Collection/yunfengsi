package com.yunfengsi.Models.YaoYue;

import android.app.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
import com.yunfengsi.R;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.Network;
import com.yunfengsi.Utils.PhoneSMSManager;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.ProgressUtil;
import com.yunfengsi.Utils.ToastUtil;
import com.yunfengsi.View.mItemDeraction;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;


/**
 * 作者：因陀罗网 on 2017/6/5 15:40
 * 公司：成都因陀罗网络科技有限公司
 */

public class YaoYue extends Fragment implements View.OnClickListener, OnRefreshListener {
    private static final int PAGESIZE = 10;
    private int page = 1;
    private int endPage = 0;
    private boolean isLoadMore = false;
    private boolean isRefresh = false;
    private SwipeRefreshLayout swip;
    private RecyclerView recyclerView;
    private ImageView iv_tougao;
    private TGAdapter adapter;
    private boolean isLoad = false;
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onRefresh();
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_zixun_tougao, container, false);
        getActivity().registerReceiver(receiver, new IntentFilter("yaoyue"));
        swip = view.findViewById(R.id.swip);
        swip.setOnRefreshListener(this);
        swip.setColorSchemeResources(R.color.main_color);
        recyclerView = view.findViewById(R.id.recycle);


        adapter = new TGAdapter(getActivity(), new ArrayList<HashMap<String, String>>());

        adapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);

        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {

                if (endPage != page) {
                    isLoadMore = true;
                    page++;
                    getData();
                }
            }
        },recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new mItemDeraction(1, R.color.black));
        recyclerView.setAdapter(adapter);
        TextView textView = new TextView(getActivity());
        Drawable d = ContextCompat.getDrawable(getActivity(), R.drawable.load_nothing);
        d.setBounds(0, 0, DimenUtils.dip2px(getActivity(), 150), DimenUtils.dip2px(getActivity(), 150) * d.getIntrinsicHeight() / d.getIntrinsicWidth());
        textView.setCompoundDrawables(null, d, null, null);
        textView.setCompoundDrawablePadding(DimenUtils.dip2px(getActivity(), 10));
        textView.setText("该活动暂无邀约信息\n下拉刷新");
        textView.setGravity(Gravity.CENTER);
        ViewGroup.MarginLayoutParams vl = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        vl.topMargin = DimenUtils.dip2px(getActivity(), 180);
        textView.setLayoutParams(vl);
        adapter.setEmptyView(textView);


        swip.post(new Runnable() {
            @Override
            public void run() {
                swip.setRefreshing(true);
                onRefresh();
            }
        });

//        //自由滑动
//        new PointMoveHelper(getActivity(), iv_tougao)
//                .setViewUnMoveable(((MainActivity) getActivity()).pager)
//                .setHorizontalMargin(10);
        return view;

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
    }

    /**
     * 加载数据
     */
    private void getData() {
        if (!Network.HttpTest(getActivity())) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    LogUtil.e("run: 页码：" + page + "最后一页：" + endPage);
                    JSONObject js = new JSONObject();
                    try {
                        js.put("m_id", Constants.M_id);
                        js.put("page", page);
                        js.put("act_id", getActivity().getIntent().getStringExtra("id"));
                        js.put("user_id", PreferenceUtil.getUserId(getActivity()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m = ApisSeUtil.i(js);
                    LogUtil.e("约车列表" + js);
                    String data = OkGo.post(Constants.CarList)
                            .params("key", m.K())
                            .params("msg", m.M()).execute().body().string();
                    HashMap<String,String> map=AnalyticalJSON.getHashMap(data);
                    if (map!=null&&map.get("code").equals("000")) {
                        final ArrayList<HashMap<String, String>> list = AnalyticalJSON.getList_zj(map.get("msg"));
                        LogUtil.e("run: list------>" + list);
                        if (list != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (isRefresh) {
                                        adapter.setNewData(list);
                                        if (adapter.currentPositon != -1) {
                                            recyclerView.scrollToPosition(adapter.currentPositon);
                                            adapter.currentPositon = -1;
                                        }
                                        isRefresh = false;
                                        swip.setRefreshing(false);
                                    } else if (isLoadMore) {
                                        isLoadMore = false;
                                        if (list.size() < PAGESIZE) {
                                            ToastUtil.showToastShort("已经没有更多数据啦", Gravity.CENTER);
//                                endPage = page;
                                          adapter.addData(list);
                                          adapter.loadMoreEnd(false);
                                        } else {
                                            adapter.loadMoreComplete();
                                            adapter.addData(list);
                                        }
                                    }

                                    swip.setRefreshing(false);
                                }
                            });
                        } else {
                            if (getActivity() != null)
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        swip.setRefreshing(false);
                                    }
                                });
                        }
                    }
                } catch (Exception e) {

                    e.printStackTrace();
                }
            }

        }).start();
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onRefresh() {
        page = 1;
        endPage = -1;
        isRefresh = true;
        adapter.setEnableLoadMore(true);
        getData();
    }

    public  class TGAdapter extends BaseQuickAdapter<HashMap<String, String>,BaseViewHolder> {
        private Activity context;

        public int currentPositon = -1;

        public TGAdapter(Activity context, List<HashMap<String, String>> data) {
            super(R.layout.item_yaoyue, data);
            WeakReference<Activity> w = new WeakReference<Activity>(context);
            this.context = w.get();

        }

        @Override
        protected void convert(final BaseViewHolder hoder, final HashMap<String, String> bean) {
            if (bean.get("status").equals("2")) {//有车
                hoder.setText(R.id.user, "车主:")
                        .setText(R.id.seat, "空余座位:")
                        .setText(R.id.phone, "***********");
                if (bean.get("user_start").equals("2")) {//已参与
                    hoder.getView(R.id.button).setEnabled(false);
                    hoder.setText(R.id.button, "已申请");
                } else {//未参与
                    hoder.getView(R.id.button).setEnabled(true);
                    hoder.setText(R.id.button, "申请");
                }

            } else {//没车
                hoder.getView(R.id.button).setEnabled(true);
                hoder.setText(R.id.user, "乘客:")
                        .setText(R.id.seat, "乘坐人数:")
                        .setText(R.id.button, "拨打电话")
                        .setText(R.id.phone, bean.get("phone"));
            }
            if(bean.get("user_id").equals(PreferenceUtil.getUserId(context))){//自己发的消息  隐藏按钮,显示删除按钮
                hoder.setVisible(R.id.button,false);
                hoder.setVisible(R.id.delete,true);
                ((SwipeMenuLayout) hoder.itemView).setSwipeEnable(true);
            }else{//别人发的消息  显示按钮,隐藏删除按钮
                hoder.setVisible(R.id.button,true);
                hoder.setVisible(R.id.delete,false);
                ((SwipeMenuLayout) hoder.itemView).setSwipeEnable(false);
            }
            hoder.setText(R.id.num, bean.get("passenger"))
                    .setText(R.id.title, bean.get("title"))
                    .setText(R.id.time, bean.get("start_time"))
                    .setText(R.id.address, bean.get("address"))
                    .setText(R.id.userName,bean.get("pet_name"));

            hoder.getView(R.id.button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String text= ((TextView) view).getText().toString().trim();
                    switch (text){
                        case "申请":
                            AlertDialog.Builder builder=new AlertDialog.Builder(context);
                            View v=LayoutInflater.from(context).inflate(R.layout.dialog_yue,null);
                            final EditText phone= v.findViewById(R.id.phone);
                            final EditText people= v.findViewById(R.id.peopleNum);
                            final EditText address= v.findViewById(R.id.address);
                            builder.setView(v);
                            final AlertDialog dialog=builder.create();
                            v.findViewById(R.id.cancle).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                }
                            });
                            v.findViewById(R.id.commit).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(phone.getText().toString().trim().equals("")){
                                        ToastUtil.showToastShort("请输入联系方式");
                                        return;
                                    }
                                    if(people.getText().toString().trim().equals("")){
                                        ToastUtil.showToastShort("请输入乘车人数");
                                        return;
                                    }
                                    if(address.getText().toString().trim().equals("")){
                                        ToastUtil.showToastShort("请输入出发地点");
                                        return;
                                    }
                                    JSONObject js=new JSONObject();
                                    try {
                                        js.put("m_id",Constants.M_id);
                                        js.put("user_id",PreferenceUtil.getUserId(context));
                                        js.put("act_id",getActivity().getIntent().getStringExtra("id"));
                                        js.put("num",bean.get("num"));
                                        js.put("phone",phone.getText().toString().trim());
                                        js.put("address",address.getText().toString().trim());
                                        js.put("passenger",people.getText().toString().trim());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    ApisSeUtil.M m=ApisSeUtil.i(js);
                                    LogUtil.e("申请约车：：；"+js);
                                    OkGo.post(Constants.SubmitYueCar).params("key",m.K())
                                            .params("msg",m.M())
                                            .execute(new StringCallback() {
                                                @Override
                                                public void onSuccess(String s, Call call, Response response) {
                                                    HashMap<String,String > map=AnalyticalJSON.getHashMap(s);
                                                    if(map!=null){
                                                        if("000".equals(map.get("code"))){
                                                            ToastUtil.showToastShort("提交申请成功，请等待车主联系");
                                                            hoder.getView(R.id.button).setEnabled(false);
                                                            bean.put("user_start","2");
                                                            hoder.setText(R.id.button,"已申请");
                                                            context.sendBroadcast(new Intent("yaoyue"));
                                                        }else if("003".equals(map.get("code"))){
                                                            ToastUtil.showToastShort("您已经申请过该条信息了");
                                                            hoder.getView(R.id.button).setEnabled(false);
                                                            hoder.setText(R.id.button,"已申请");
                                                            bean.put("user_start","2");
                                                            context.sendBroadcast(new Intent("yaoyue"));
                                                        }

                                                    }
                                                }

                                                @Override
                                                public void onBefore(BaseRequest request) {
                                                    super.onBefore(request);
                                                    ProgressUtil.show(context,"","正在提交申请，请稍等");
                                                }

                                                @Override
                                                public void onAfter(String s, Exception e) {
                                                    super.onAfter(s, e);
                                                    ProgressUtil.dismiss();
                                                    dialog.dismiss();
                                                }
                                            });
                                }
                            });
                            Window window = dialog.getWindow();
                            WindowManager.LayoutParams wl = window.getAttributes();
                            window.getDecorView().setPadding(0, 0, 0, 0);
                            wl.gravity = Gravity.CENTER;
                            wl.width = getResources().getDisplayMetrics().widthPixels * 6 / 10;
                            wl.height = WindowManager.LayoutParams.WRAP_CONTENT;
                            window.setDimAmount(0.7f);
                            window.setWindowAnimations(R.style.dialogWindowAnim);
                            window.setBackgroundDrawableResource(R.color.transparent);
                            window.setAttributes(wl);
                            dialog.show();

                            break;
                        case "拨打电话":
                            PhoneSMSManager.callPhone1(context,bean.get("phone"));
                            break;
                    }
                }
            });
            hoder.getView(R.id.content).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(bean.get("user_id").equals(PreferenceUtil.getUserId(context))
                            &&bean.get("status").equals("2")){
                        Intent intent=new Intent(context,CarerManage.class);
                        intent.putExtra("id",getActivity().getIntent().getStringExtra("id"));
                        intent.putExtra("num",bean.get("num"));
                        startActivity(intent);
                    }

                }
            });

            hoder.getView(R.id.delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                    builder.setMessage("确认删除该条信息吗？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    doDelete();
                                }
                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).create().show();
                }

                private void doDelete() {
                    JSONObject js=new JSONObject();
                    try {
                        js.put("m_id", Constants.M_id);
                        js.put("act_id",getActivity().getIntent().getStringExtra("id"));
                        js.put("num",bean.get("num"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m=ApisSeUtil.i(js);
                    LogUtil.e("删除约车信息：："+js);
                    OkGo.post(Constants.DeleteYueCar).params("key",m.K())
                            .params("msg",m.M())
                            .execute(new StringCallback() {
                                @Override
                                public void onSuccess(String s, Call call, Response response) {
                                    HashMap<String,String > map= AnalyticalJSON.getHashMap(s);
                                    if(map!=null){
                                        if("000".equals(map.get("code"))){
                                            ToastUtil.showToastShort("删除成功");
                                            getData().remove(bean);
                                            notifyDataSetChanged();
                                            context.sendBroadcast(new Intent("yaoyue"));
                                        }
                                    }
                                }

                                @Override
                                public void onBefore(BaseRequest request) {
                                    super.onBefore(request);
                                    ProgressUtil.show(context,"","正在删除");
                                }

                                @Override
                                public void onAfter(String s, Exception e) {
                                    super.onAfter(s, e);
                                    ProgressUtil.dismiss();
                                }
                            });
                }
            });

        }

    }


}
