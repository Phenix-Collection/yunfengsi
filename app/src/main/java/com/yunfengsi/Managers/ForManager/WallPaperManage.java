package com.yunfengsi.Managers.ForManager;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.ProgressUtil;
import com.yunfengsi.Utils.ScaleImageUtil;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.TimeUtils;
import com.yunfengsi.Utils.ToastUtil;
import com.yunfengsi.Utils.mApplication;
import com.yunfengsi.View.ListDialog;
import com.yunfengsi.View.mItemDecoration;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 作者：luZheng on 2018/06/07 10:10
 */
public class WallPaperManage extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private RecyclerView       recyclerView;
    private SwipeRefreshLayout swip;
    private MessageAdapter     adapter;
    private int     pageSize   = 10;
    private int     page       = 1;
    private int     endPage    = -1;
    private boolean isLoadMore = false;
    private boolean isRefresh  = false;

    private LinearLayout display, delete;

    private ArrayList<HashMap<String, String>> classificationList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.comment_manage);

        ((ImageView) findViewById(R.id.title_back)).setVisibility(View.VISIBLE);
//        findViewById(R.id.handle_right).setVisibility(View.VISIBLE);
//        ((TextView) findViewById(R.id.handle_right)).setText(mApplication.ST("禁言列表"));
//        findViewById(R.id.handle_right).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(WallPaperManage.this,UserBannedCommentList.class));
//            }
//        });
        ((TextView) findViewById(R.id.title_title)).setText("壁纸管理");
        ((ImageView) findViewById(R.id.title_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        display = findViewById(R.id.display);
        delete = findViewById(R.id.delete);

        display.setOnClickListener(this);
        delete.setOnClickListener(this);
        findViewById(R.id.bottomLayout).setVisibility(View.GONE);
        findViewById(R.id.line2).setVisibility(View.GONE);

        swip = (SwipeRefreshLayout) findViewById(R.id.swip);
        swip.setOnRefreshListener(this);
        swip.setColorSchemeResources(R.color.main_color);

        recyclerView = (RecyclerView) findViewById(R.id.recycle);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new mItemDecoration(this));

        adapter = new MessageAdapter(this, new ArrayList<HashMap<String, String>>());

//        adapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (endPage != page) {
                    isLoadMore = true;
                    page++;
                    getComments();
                }
            }
        }, recyclerView);
        adapter.disableLoadMoreIfNotFullPage();

        recyclerView.setAdapter(adapter);


        adapter.setEmptyView(mApplication.getEmptyView(this, 150, "暂无最新壁纸"));

        swip.post(new Runnable() {
            @Override
            public void run() {
                swip.setRefreshing(true);
                onRefresh();
            }
        });

    }

    /**
     * 获取分类列表
     */
    private void getTitles() {
        JSONObject js = new JSONObject();
        try {
            js.put("m_id", Constants.M_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m = ApisSeUtil.i(js);
        OkGo.post(Constants.WallPaperTypeList)
                .params("key", m.K())
                .params("msg", m.M())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        HashMap<String, String> map = AnalyticalJSON.getHashMap(s);
                        if (map != null) {
                            ArrayList<HashMap<String, String>> list = AnalyticalJSON.getList_zj(map.get("msg"));
                            if (list != null) {
                                classificationList = list;
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
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    private void postDisplay(String id, final int pos, final ArrayList<HashMap<String, String>> deleteMaps) {
        JSONObject js = new JSONObject();
        try {
            js.put("m_id", Constants.M_id);
            js.put("admin_id", PreferenceUtil.getUserId(this));
            js.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e("显示待审核评论：：" + js);
        ApisSeUtil.M m = ApisSeUtil.i(js);
        OkGo.post(Constants.PendingCommentDisplay)
                .tag(this)
                .params("key", m.K())
                .params("msg", m.M())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        HashMap<String, String> map = AnalyticalJSON.getHashMap(s);
                        if (map != null) {
                            if ("000".equals(map.get("code"))) {
                                ToastUtil.showToastShort("显示评论成功");

                                if (pos >= 0) {
                                    adapter.getData().remove(pos);
                                    adapter.notifyItemRemoved(pos);
                                } else {
                                    adapter.getData().removeAll(deleteMaps);
                                    adapter.notifyDataSetChanged();
                                }

                            } else if ("005".equals(map.get("code"))) {
                                ToastUtil.showToastShort(getString(R.string.haveNoPermission));
                            }
                        }

                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        ProgressUtil.show(WallPaperManage.this, "", "正在删除...");
                    }

                    @Override
                    public void onAfter(String s, Exception e) {
                        super.onAfter(s, e);
                        ProgressUtil.dismiss();
                        onRefresh();
                    }
                });
    }

    private class MessageAdapter extends BaseQuickAdapter<HashMap<String, String>, BaseViewHolder> {
        int dp30;


        public MessageAdapter(Context context, List<HashMap<String, String>> data) {
            super(R.layout.item_wallpaper_manage, data);
            dp30 = DimenUtils.dip2px(context, 45);

        }

        @Override
        protected void convert(final BaseViewHolder holder, final HashMap<String, String> map) {
            holder.setText(R.id.name, map.get("pet_name"))
                    .setText(R.id.classification, map.get("name"))
                    .setTag(R.id.classification, map.get("type"))
                    .setText(R.id.time, "上传于 : " + TimeUtils.getTrueTimeStr(map.get("time")));
            Glide.with(WallPaperManage.this)
                    .load(map.get("image"))
                    .override(dp30 << 1, (dp30 << 1) * 16 / 9)
                    .into((ImageView) holder.getView(R.id.wallpaper));

            Glide.with(WallPaperManage.this)
                    .load(map.get("user_image"))
                    .override(dp30, dp30)
                    .into((ImageView) holder.getView(R.id.head));
            holder.getView(R.id.wallpaper).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ScaleImageUtil.openBigIagmeMode(WallPaperManage.this, map.get("image"), true);
                }
            });

            /**
             * 选择分类
             */
            holder.getView(R.id.classification).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (classificationList == null) {
                        ToastUtil.showToastShort("分类列表获取失败，请检查网络连接后重新尝试");
                        getTitles();
                    } else {
                        ListDialog.create(WallPaperManage.this)
                                .setView(R.layout.dialog_list)
                                .mode(ListDialog.WITH_LIST)
                                .setList(classificationList, "name")
                                .setCancelViewId(R.id.cancel)
                                .setListViewId(R.id.recycle)
                                .setText(R.id.title, "壁纸分类")
                                .setGravity(Gravity.BOTTOM)
                                .setItemDefaultTextsize(16)
                                .setAnimResId(R.style.dialogWindowAnim)
                                .setCallBack(new ListDialog.HandleCallBack<HashMap<String, String>>() {
                                    @Override
                                    public void onItemClick(HashMap<String, String> item, int pos,AlertDialog dialog) {
                                        dialog.dismiss();
                                        ((TextView) v).setText(item.get("name"));
                                        v.setTag(item.get("id"));
                                    }

                                }).show();

                    }

                }
            });

            /**
             * 壁纸删除  返回005代表没有权限操作
             */
            holder.getView(R.id.delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(WallPaperManage.this);
                    builder.setMessage("确定要删除该壁纸吗？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    deleteWallPaper(map.get("id"), holder.getAdapterPosition(), (SwipeMenuLayout) holder.itemView);
                                }
                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();

                }
            });

            /**
             * 壁纸通过  返回005代表没有权限操作
             */
            holder.getView(R.id.commit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(WallPaperManage.this);
                    builder.setMessage("确定要显示该壁纸吗？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    commitWallPaper(map.get("id"), (String) holder.getView(R.id.classification).getTag(), holder.getAdapterPosition(), (SwipeMenuLayout) holder.itemView);
                                }
                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();

                }
            });
        }
    }

    /**
     * @param id      审核列表的id
     * @param type_id 分类id
     */
    private void commitWallPaper(String id, String type_id, final int pos, final SwipeMenuLayout itemview) {
        JSONObject js = new JSONObject();
        try {
            js.put("m_id", Constants.M_id);
            js.put("admin_id", PreferenceUtil.getUserId(this));
            js.put("id", id);
            js.put("type_id", type_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e("显示待审核壁纸：：" + js);
        ApisSeUtil.M m = ApisSeUtil.i(js);
        OkGo.post(Constants.WallPaperManagePass)
                .tag(this)
                .params("key", m.K())
                .params("msg", m.M())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        HashMap<String, String> map = AnalyticalJSON.getHashMap(s);
                        if (map != null) {
                            if ("000".equals(map.get("code"))) {
                                ToastUtil.showToastShort("壁纸显示成功");
                                itemview.smoothClose();
                                adapter.remove(pos);

                            } else if ("005".equals(map.get("code"))) {
                                ToastUtil.showToastShort(getString(R.string.haveNoPermission));
                            }
                        }

                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        ProgressUtil.show(WallPaperManage.this, "", "正在提交...");
                    }

                    @Override
                    public void onAfter(String s, Exception e) {
                        super.onAfter(s, e);
                        ProgressUtil.dismiss();
                        onRefresh();
                    }
                });
    }

    /**
     * @param id  审核列表的id
     * @param pos 删除的位置
     */
    private void deleteWallPaper(String id, final int pos, final SwipeMenuLayout itemview) {
        JSONObject js = new JSONObject();
        try {
            js.put("m_id", Constants.M_id);
            js.put("admin_id", PreferenceUtil.getUserId(this));
            js.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e("删除待审核壁纸：：" + js);
        ApisSeUtil.M m = ApisSeUtil.i(js);
        OkGo.post(Constants.WallPaperManageDelete)
                .tag(this)
                .params("key", m.K())
                .params("msg", m.M())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        HashMap<String, String> map = AnalyticalJSON.getHashMap(s);
                        if (map != null) {
                            if ("000".equals(map.get("code"))) {
                                ToastUtil.showToastShort("删除成功");
                                itemview.smoothClose();
                                adapter.remove(pos);

                            } else if ("005".equals(map.get("code"))) {
                                ToastUtil.showToastShort(getString(R.string.haveNoPermission));
                            }
                        }

                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        ProgressUtil.show(WallPaperManage.this, "", "正在删除...");
                    }

                    @Override
                    public void onAfter(String s, Exception e) {
                        super.onAfter(s, e);
                        ProgressUtil.dismiss();
                        onRefresh();
                    }
                });
    }

    private void getComments() {
        if (Network.HttpTest(this)) {
            JSONObject js = new JSONObject();
            try {
                js.put("page", page);
                js.put("m_id", Constants.M_id);
                js.put("admin_id", PreferenceUtil.getUserId(this));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ApisSeUtil.M m = ApisSeUtil.i(js);
            LogUtil.e("获取待审核壁纸：：" + js);
            OkGo.post(Constants.WallPaperManageList)
                    .tag(this)
                    .params("key", m.K())
                    .params("msg", m.M())
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            HashMap<String, String> map = AnalyticalJSON.getHashMap(s);
                            if (map != null) {
                                if ("000".equals(map.get("code"))) {
                                    final ArrayList<HashMap<String, String>> list = AnalyticalJSON.getList_zj(map.get("msg"));
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
                                } else if ("005".equals(map.get("code"))) {
                                    ToastUtil.showToastShort(getString(R.string.haveNoPermission));
                                    finish();
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
        swip.setRefreshing(true);
        page = 1;
        isRefresh = true;
        adapter.setEnableLoadMore(true);
        getComments();
        getTitles();
    }
}
