package com.qianfujiaoyu.Model_Order;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.request.BaseRequest;
import com.qianfujiaoyu.Model_activity.activity_Detail;
import com.qianfujiaoyu.R;
import com.qianfujiaoyu.Utils.AnalyticalJSON;
import com.qianfujiaoyu.Utils.ApisSeUtil;
import com.qianfujiaoyu.Utils.Constants;
import com.qianfujiaoyu.Utils.DimenUtils;
import com.qianfujiaoyu.Utils.ImageUtil;
import com.qianfujiaoyu.Utils.LogUtil;
import com.qianfujiaoyu.Utils.LoginUtil;
import com.qianfujiaoyu.Utils.PreferenceUtil;
import com.qianfujiaoyu.Utils.ProgressUtil;
import com.qianfujiaoyu.Utils.TimeUtils;
import com.qianfujiaoyu.Utils.ToastUtil;
import com.qianfujiaoyu.Utils.mApplication;
import com.qianfujiaoyu.View.mItemDeraction;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;


public class Order_item_fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private View view;
    private static final String TAG = "Order_item_fragment";
    private boolean isLoaded = false;
    private Bundle b;
    private static final String TYPE = "type";//获取数据的标识
    private int page = 1;
    private int endPage = -1;
    private String Id, url, data;

    private SwipeRefreshLayout swip;
    private RecyclerView listView;
    private boolean isRefresh = false;
    private OrderAdapter adapter;
    private boolean isLoadMore = false;
    List<HashMap<String, String>> list = null;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        OkGo.getInstance().cancelTag(TAG);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.order_list, container, false);

        b = getArguments();

        if (!isLoaded && b == null) {
            initView(view);
            swip.post(new Runnable() {
                @Override
                public void run() {
                    swip.setRefreshing(true);
                    onRefresh();
                }
            });

            isLoaded = true;
        }
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !isLoaded && b != null) {
            initView(view);
            swip.post(new Runnable() {
                @Override
                public void run() {
                    swip.setRefreshing(true);
                    onRefresh();
                }
            });
            isLoaded = true;
        }
    }

    /**
     * 获取数据
     */
    private void getData() {
        if (b == null) {
            url = Constants.Order_total;
        } else if ("课程".equals(b.getString(TYPE))) {
            url = Constants.Activity_list_IP;
        } else {
            url = Constants.Order_special;
            Id = b.getString(TYPE);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject js = new JSONObject();
                try {
                    js.put("page", page);
                    js.put("m_id", Constants.M_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    if (TextUtils.isEmpty(Id)) {
                        if (url.equals(Constants.Order_total)) {
                        } else {
                            js.put("type", 2);

                        }
                    } else {
                        js.put("type", Id);
                    }
                    ApisSeUtil.M m = ApisSeUtil.i(js);
                    data = OkGo.post(url).tag(this)
                            .params("key", m.K())
                            .params("msg", m.M()).execute().body().string();
                    LogUtil.e("js:::::" + js);
                    if (!TextUtils.isEmpty(data)) {

                        if (url.equals(Constants.Activity_list_IP)) {
                            list = AnalyticalJSON.getList(data, "activity");
                        } else {
                            list = AnalyticalJSON.getList_zj(data);
                        }

                        if (list != null) {
                            if (listView != null) {
                                listView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (isRefresh) {
                                            adapter.setNewData(list);
                                            isRefresh = false;
                                            swip.setRefreshing(false);

                                        } else if (isLoadMore) {
                                            isLoadMore = false;
                                            if (list.size() < 10) {
                                                ToastUtil.showToastShort("已经没有更多数据啦", Gravity.CENTER);
                                                endPage = page;
                                                adapter.notifyDataChangedAfterLoadMore(list, false);
                                            } else {
                                                adapter.notifyDataChangedAfterLoadMore(list, true);
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private static class OrderAdapter extends BaseQuickAdapter<HashMap<String, String>> {

        private WeakReference<Activity> w;
        private Activity context;
        private int dp120;
        private boolean isActivity = false;

        OrderAdapter(Activity a, ArrayList<HashMap<String, String>> list1, boolean isActivity) {
            super(R.layout.order_item, list1);

            w = new WeakReference<Activity>(a);
            this.context = w.get();
            dp120 = DimenUtils.dip2px(context, 120);
            this.isActivity = isActivity;
        }


        @Override
        protected void convert(BaseViewHolder holder, final HashMap<String, String> map) {
            if (isActivity) {
                holder.setImageBitmap(R.id.order_item_car_img, ImageUtil.readBitMap(context, R.drawable.yuyue));
                Glide.with(context).load(map.get("image1")).asBitmap().override(dp120, dp120).centerCrop()
                        .error(R.drawable.load_nothing)
                        .into(new BitmapImageViewTarget((ImageView) holder.getView(R.id.order_item_image)) {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                RoundedBitmapDrawable rb = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                                rb.setCornerRadius(10);
                                setDrawable(rb);
                            }
                        });
                holder.setVisible(R.id.order_item_type,false);
                holder.setText(R.id.order_item_type, map.get("name"))
                        .setText(R.id.order_item_name, map.get("title"))
                        .setText(R.id.order_item_abstract, map.get("abstract"))
                        .setText(R.id.order_item_sales, "截止:" + map.get("end_time"))
                        .setVisible(R.id.order_item_likes, false)
                        .setText(R.id.order_item_true_money, (map.get("money").equals("")||Double.valueOf(map.get("money")).intValue()<=0)?"免费":("¥ " + map.get("money")))
                        .setText(R.id.order_item_false_money, (map.get("quota").equals("")||Double.valueOf(map.get("quota")).intValue()<=0)?"人数不限":("人数:" + map.get("enrollment") + "/" + map.get("quota")))
                        .setTag(R.id.order_item_car, map.get("id"))
                ;
                if(TimeUtils.dataOne(map.get("end_time"))<System.currentTimeMillis()){
                    holder.setText(R.id.order_item_car_text,"已结束");
                    holder.setVisible(R.id.order_item_car_img,false);
                    holder.getView(R.id.order_item_car_text).setEnabled(false);
                }else{
                    holder.setVisible(R.id.order_item_car_img,true);
                    holder.setText(R.id.order_item_car_text,"立即预约");
                    holder.getView(R.id.order_item_car_text).setEnabled(true);
                }
                holder.convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String id = map.get("id");
                        Intent intent = new Intent(context, activity_Detail.class);
                        intent.putExtra("id", id);
                        intent.putExtra("kecheng",true);
                        context.startActivity(intent);
                    }
                });

                //// TODO: 2016/11/30 立即预约
                holder.setOnClickListener(R.id.order_item_car, new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        if (!new LoginUtil().checkLogin(context)) {
                            return;
                        }
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                        builder1.setCancelable(true);
                        builder1.setPositiveButton(mApplication.ST("取消"), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setTitle(mApplication.ST("确定报名参加" + map.get("title") + "吗？")).setNegativeButton(mApplication.ST("确定"), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                JSONObject js = new JSONObject();
                                try {
                                    js.put("act_id", map.get("id"));
                                    js.put("m_id", Constants.M_id);
                                    js.put("user_id", PreferenceUtil.getUserIncetance(mApplication.getInstance()).getString("user_id", ""));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                ApisSeUtil.M m = ApisSeUtil.i(js);
                                OkGo.post(Constants.Activity_BaoMing).tag(TAG)
                                        .params("key", m.K())
                                        .params("msg", m.M()).execute(new AbsCallback<HashMap<String, String>>() {
                                    @Override
                                    public HashMap<String, String> convertSuccess(okhttp3.Response response) throws Exception {
                                        return AnalyticalJSON.getHashMap(response.body().string());
                                    }


                                    @Override
                                    public void onBefore(BaseRequest request) {
                                        super.onBefore(request);
                                        ProgressUtil.show(context, "", mApplication.ST("正在报名，请稍等"));
                                    }

                                    @Override
                                    public void onSuccess(HashMap<String, String> map, Call call, okhttp3.Response response) {
                                        View view = LayoutInflater.from(context).inflate(R.layout.baoming_alert, null);
                                        AlertDialog.Builder b = new AlertDialog.Builder(context).
                                                setView(view);
                                        final AlertDialog d = b.create();
                                        d.getWindow().setDimAmount(0.2f);
                                        view.findViewById(R.id.commit).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                d.dismiss();
                                            }
                                        });
                                        if ("000".equals(map.get("code"))) {
                                            ((TextView) view.findViewById(R.id.result_msg)).setText(mApplication.ST("您已成功预约"));
                                            view.findViewById(R.id.commit).setBackgroundColor(Color.parseColor("#40d976"));
                                        } else {
                                            ((TextView) view.findViewById(R.id.result_msg)).setText(mApplication.ST("您已经预约过了哟~"));
                                            view.findViewById(R.id.commit).setBackgroundColor(Color.parseColor("#e75e5e"));
                                        }
//                                        ((TextView) view.findViewById(R.id.phone)).setText(mApplication.ST("审核结果请及时关注App我的活动：\n[我的]->[活动]"));
                                        ((TextView) view.findViewById(R.id.phone)).setText(mApplication.ST(""));

                                        d.show();
                                    }

                                    @Override
                                    public void onAfter(HashMap<String, String> map, Exception e) {
                                        super.onAfter(map, e);
                                        ProgressUtil.dismiss();
                                    }


                                });

                            }
                        }).create().show();
                    }
                });
            } else {//购买
                holder.setImageBitmap(R.id.order_item_car_img, ImageUtil.readBitMap(context, R.drawable.shopcar));
                Glide.with(context).load(map.get("image1")).asBitmap().override(dp120, dp120).centerCrop()
                        .error(R.drawable.load_nothing)
                        .into(new BitmapImageViewTarget((ImageView) holder.getView(R.id.order_item_image)) {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                RoundedBitmapDrawable rb = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                                rb.setCornerRadius(10);
                                setDrawable(rb);
                            }
                        });
                ((TextView) holder.getView(R.id.order_item_false_money)).getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                holder.setText(R.id.order_item_type, map.get("name"))
                        .setText(R.id.order_item_name, map.get("title"))
                        .setText(R.id.order_item_abstract, map.get("abstract"))
                        .setText(R.id.order_item_score, "每" + map.get("convert") + "积分可抵用1元")
                        .setText(R.id.order_item_sales, "总销量" + map.get("sales"))
                        .setText(R.id.order_item_true_money, "¥ " + map.get("money"))
                        .setText(R.id.order_item_likes, "赞" + map.get("likes"))
                        .setText(R.id.order_item_false_money, "¥ " + map.get("cost"))
                        .setTag(R.id.order_item_car, map.get("id"));


                //// TODO: 2016/11/30 立即购买
                holder.setOnClickListener(R.id.order_item_car, new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        ArrayList<HashMap<String, String>> l = new ArrayList<HashMap<String, String>>();
                        l.add(map);
                        Intent intent = new Intent(context, Dingdan_commit.class);
                        intent.putExtra("list", l);
                        context.startActivity(intent);

                    }
                });
                holder.convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String id = map.get("id");
                        Intent intent = new Intent(context, Order_detail.class);
                        intent.putExtra("id", id);
                        context.startActivity(intent);
                    }
                });


            }

        }


    }

    /**
     * 初始化控件
     *
     * @param view
     */
    private void initView(View view) {
        swip = (SwipeRefreshLayout) view.findViewById(R.id.zixun_refresh);
        swip.setOnRefreshListener(this);
        swip.setColorSchemeResources(R.color.main_color);
        listView = (RecyclerView) view.findViewById(R.id.zixun_list);
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (b != null && "课程".equals(b.getString(TYPE))) {
            adapter = new OrderAdapter(getActivity(), new ArrayList<HashMap<String, String>>(), true);
        } else {
            adapter = new OrderAdapter(getActivity(), new ArrayList<HashMap<String, String>>(), false);
        }

        adapter.openLoadMore(10, true);
        adapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (endPage != page) {
                    isLoadMore = true;
                    page++;
                    getData();
                }
            }
        });

        adapter.setEmptyView(mApplication.getLoadNothing(R.drawable.load_nothing, "暂无数据\n\n下拉刷新",200));
        listView.addItemDecoration(new mItemDeraction(2, Color.parseColor("#b6b6b6")));
        listView.setAdapter(adapter);
    }


    @Override
    public void onRefresh() {
        isRefresh = true;
        isLoadMore = false;
        page = 1;
        endPage = -1;
        adapter.openLoadMore(10, true);

        getData();

    }

    @Override
    public void onClick(View v) {

    }
}
