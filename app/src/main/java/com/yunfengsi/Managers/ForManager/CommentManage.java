package com.yunfengsi.Managers.ForManager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
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
import com.yunfengsi.Utils.ImageUtil;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.Network;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.ProgressUtil;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.TimeUtils;
import com.yunfengsi.Utils.ToastUtil;
import com.yunfengsi.Utils.mApplication;
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
public class CommentManage extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
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
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.comment_manage);

        ((ImageView) findViewById(R.id.title_back)).setVisibility(View.VISIBLE);
        findViewById(R.id.handle_right).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.handle_right)).setText(mApplication.ST("禁言列表"));
        findViewById(R.id.handle_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CommentManage.this,UserBannedCommentList.class));
            }
        });
        ((TextView) findViewById(R.id.title_title)).setText("评论管理");
        ((ImageView) findViewById(R.id.title_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        LinearLayout display = findViewById(R.id.display);
        LinearLayout delete  = findViewById(R.id.delete);
        display.setOnClickListener(this);
        delete.setOnClickListener(this);


        swip = (SwipeRefreshLayout) findViewById(R.id.swip);
        swip.setOnRefreshListener(this);
        swip.setColorSchemeResources(R.color.main_color);

        RecyclerView        recyclerView        = (RecyclerView) findViewById(R.id.recycle);
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


        adapter.setEmptyView(mApplication.getEmptyView(this, 150, "暂无最新评论"));

        swip.post(new Runnable() {
            @Override
            public void run() {
                swip.setRefreshing(true);
                onRefresh();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.display:
                if (adapter.choosedIds.size() == 0) {
                    ToastUtil.showToastShort("请选择要显示的评论");
                    return;
                }
                AlertDialog.Builder b = new AlertDialog.Builder(this);
                b.setMessage("确定要显示选中的评论吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                SparseArray<String> sparseArray = adapter.choosedIds;
                                StringBuilder       id          = new StringBuilder();
                                int                 len         = sparseArray.size();
                                int                 pos         = -1;
                                for (int i = 0; i < len; i++) {
                                    int key = sparseArray.keyAt(i);
                                    id.append(sparseArray.get(key));
                                    if (i != sparseArray.size() - 1) {
                                        id.append(",");
                                    }
                                    if (len == 1) {
                                        pos = key;
                                    }
                                }
                                postDisplay(id.toString(),pos,adapter.choosedMaps);
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();

                break;
            case R.id.delete:
                if (adapter.choosedIds.size() == 0) {
                    ToastUtil.showToastShort("请选择要删除的评论");
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("确定要删除选中的评论吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                SparseArray<String> sparseArray = adapter.choosedIds;
                                StringBuilder       id          = new StringBuilder();
                                int                 pos         = -1;
                                int                 len         = sparseArray.size();
                                for (int i = 0; i < len; i++) {
                                    int key = sparseArray.keyAt(i);
                                    id.append(sparseArray.get(key));
                                    if (i != sparseArray.size() - 1) {
                                        id.append(",");
                                    }
                                    if (len == 1) {
                                        pos = key;
                                    }
                                }

                                postDelete(id.toString(), pos, adapter.choosedMaps);
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();

                break;
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
                                adapter.choosedIds.clear();
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
                        ProgressUtil.show(CommentManage.this, "", "正在删除...");
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
        public SparseArray<String>                choosedIds;
        public ArrayList<HashMap<String, String>> choosedMaps;

        public MessageAdapter(Context context, List<HashMap<String, String>> data) {
            super(R.layout.item_comment_manage, data);
            dp30 = DimenUtils.dip2px(context, 45);
            choosedIds = new SparseArray<>();
            choosedMaps = new ArrayList<>();
        }

        @Override
        protected void convert(final BaseViewHolder holder, final HashMap<String, String> map) {
            holder.setText(R.id.petName, map.get("pet_name"))
                    .setText(R.id.title, new StringBuilder().append("对 [ ").append(map.get("title")).append(" ] 评论 :"))
                    .setText(R.id.comment, map.get("ct_contents"))
                    .setText(R.id.time, TimeUtils.getTrueTimeStr(map.get("ct_time")));
            Glide.with(CommentManage.this)
                    .load(map.get("user_image"))
                    .override(dp30, dp30)
                    .into((ImageView) holder.getView(R.id.head));


            final String    id     = map.get("id");
            final int       pos    = holder.getAdapterPosition();
            final ImageView choose = holder.getView(R.id.choose);

            if (choosedIds.get(pos) != null) {
                choose.setImageBitmap(ImageUtil.readBitMap(CommentManage.this, R.drawable.selected_btn));
            } else {
                choose.setImageBitmap(ImageUtil.readBitMap(CommentManage.this, R.drawable.unselected_btn));
            }
            holder.getView(R.id.content).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (choosedIds.get(pos) != null) {
                        choosedIds.remove(pos);
                        choosedMaps.remove(map);
                        choose.setImageBitmap(ImageUtil.readBitMap(CommentManage.this, R.drawable.unselected_btn));
                    } else {
                        choosedIds.put(pos, id);
                        choosedMaps.add(map);
                        choose.setImageBitmap(ImageUtil.readBitMap(CommentManage.this, R.drawable.selected_btn));
                    }
                }
            });
            /**
             * 用户禁言   返回005代表没有权限操作
             */
            holder.getView(R.id.ban).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CommentManage.this);
                    builder.setMessage("确定要对该用户禁言吗？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    banCommitComment(map.get("user_id"), (SwipeMenuLayout) holder.itemView);
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

    private void banCommitComment(String id, final SwipeMenuLayout itemview) {
        JSONObject js = new JSONObject();
        try {
            js.put("m_id", Constants.M_id);
            js.put("admin_id", PreferenceUtil.getUserId(this));
            js.put("user_id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e("用户禁言：：" + js);
        ApisSeUtil.M m = ApisSeUtil.i(js);
        OkGo.post(Constants.UserBanComment)
                .tag(this)
                .params("key", m.K())
                .params("msg", m.M())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        HashMap<String, String> map = AnalyticalJSON.getHashMap(s);
                        if (map != null) {
                            if ("000".equals(map.get("code"))) {
                                ToastUtil.showToastShort("禁言成功");
                                itemview.smoothClose();

                            } else if ("002".equals(map.get("code"))) {
                                ToastUtil.showToastShort("该用户已经被禁言了");
                            }else if ("005".equals(map.get("code"))) {
                                ToastUtil.showToastShort(getString(R.string.haveNoPermission));
                            }
                        }

                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        ProgressUtil.show(CommentManage.this, "", "正在删除...");
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
     * @param id  如果选中多条item，并且点击外层delete  那么   多个id间用英文逗号隔开 合并为id传入  EX：123,234,345
     * @param pos 条目在adapter中的位置   多条删除传-1
     */
    private void postDelete(String id, final int pos, final ArrayList<HashMap<String, String>> deleteMaps) {
        JSONObject js = new JSONObject();
        try {
            js.put("m_id", Constants.M_id);
            js.put("admin_id", PreferenceUtil.getUserId(this));
            js.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e("删除待审核评论：：" + js);
        ApisSeUtil.M m = ApisSeUtil.i(js);
        OkGo.post(Constants.PendingCommentDelete)
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
                                adapter.choosedIds.clear();
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
                        ProgressUtil.show(CommentManage.this, "", "正在删除...");
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
            LogUtil.e("获取待审核评论：：" + js);
            OkGo.post(Constants.PendingCommentList)
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
    }
}
