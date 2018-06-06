package com.yunfengsi.E_Book;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.yunfengsi.E_Book.TreeBean.Book;
import com.yunfengsi.R;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.FileUtils;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.Network;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.ProgressUtil;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.ToastUtil;
import com.yunfengsi.Utils.mApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 作者：因陀罗网 on 2017/11/21 13:50
 * 公司：成都因陀罗网络科技有限公司
 */

public class DZJHistory extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {


    private RecyclerView       recyclerView;
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
        setContentView(R.layout.message_center);

        ((ImageView) findViewById(R.id.title_back)).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.title_title)).setText("大藏经足迹");
        ((ImageView) findViewById(R.id.title_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        swip = (SwipeRefreshLayout) findViewById(R.id.swip);
        swip.setOnRefreshListener(this);
        swip.setColorSchemeResources(R.color.main_color);

        recyclerView = (RecyclerView) findViewById(R.id.recycle);
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

        adapter.setEmptyView(mApplication.getEmptyView(this, 150, "您还没有阅读过大藏经哦"));

        swip.post(new Runnable() {
            @Override
            public void run() {
                swip.setRefreshing(true);
                onRefresh();
            }
        });
    }

    public void getNext(final HashMap<String, String> map) {
        final JSONObject js = new JSONObject();
        try {
            js.put("id", map.get("dzj_id"));
            js.put("m_id", Constants.M_id);
            js.put("level", map.get("level"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m = ApisSeUtil.i(js);
        LogUtil.e("获取次级目录：：；" + js);

        OkGo.post(Constants.DaZang_Chapter_Detail).tag(this)
                .params("key", m.K())
                .params("msg", m.M())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        HashMap<String, String> m = AnalyticalJSON.getHashMap(s);
                        if (m != null) {
                            if ("002".equals(m.get("code")) && m.get("content") != null) {
                                if (!m.get("content").equals("")) {
                                    writeTxtOrOpenBook(map.get("id"), m.get("content"));
                                } else {
                                    ToastUtil.showToastShort("该卷经书内容正在积极添加中，敬请期待");
                                }

                            } else {
                                onError(call, response, new Exception("eeeee"));
                            }

                        } else {
                            onError(call, response, new Exception("呵呵呵呵"));
                        }
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        ProgressUtil.show(DZJHistory.this, "", "请稍等");
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        LogUtil.e("加载次级目录失败：；" + e);
                    }

                    @Override
                    public void onAfter(String s, Exception e) {
                        super.onAfter(s, e);
                        ProgressUtil.dismiss();
                    }
                });
    }

    private class MessageAdapter extends BaseQuickAdapter<HashMap<String, String>, BaseViewHolder> {
        private Context context;

        public MessageAdapter(Context context, @Nullable List<HashMap<String, String>> data) {
            super(R.layout.item_jinshu_dazang_delete, data);
            this.context = context;
        }

        @Override
        protected void convert(final BaseViewHolder helper, final HashMap<String, String> map) {

            try {
                helper.setText(R.id.text, URLDecoder.decode(map.get("title"), "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                LogUtil.e("转码失败");
            }

            Glide.with(context).load(R.mipmap.book_normal)
                    .override(DimenUtils.dip2px(context, 30), DimenUtils.dip2px(context, 30))
                    .into((ImageView) helper.getView(R.id.icon));

            helper.getView(R.id.content).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: 2018/3/15  获取content直接进入书籍
                    getNext(map);
                }
            });
            helper.getView(R.id.delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JSONObject js=new JSONObject();
                    try {
                        js.put("m_id",Constants.M_id);
                        js.put("user_id",PreferenceUtil.getUserId(DZJHistory.this));
                        js.put("id",map.get("id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m=ApisSeUtil.i(js);
                    LogUtil.e("删除足迹：："+js);
                    OkGo.post(Constants.DaZangHistory_Delete)
                            .params("key",m.K())
                            .params("msg",m.M())
                            .execute(new StringCallback() {
                                @Override
                                public void onSuccess(String s, Call call, Response response) {
                                    HashMap<String,String > map=AnalyticalJSON.getHashMap(s);
                                    if(map!=null){
                                        if(map.get("code").equals("000")){
                                            int index=helper.getAdapterPosition();
                                            getData().remove(index);
                                            notifyItemRemoved(index);
                                        }
                                    }
                                }

                                @Override
                                public void onAfter(String s, Exception e) {
                                    super.onAfter(s, e);
                                    ProgressUtil.dismiss();
                                }

                                @Override
                                public void onBefore(BaseRequest request) {
                                    super.onBefore(request);
                                    ProgressUtil.show(DZJHistory.this,"","正在删除...");
                                }
                            });
                }
            });

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
            LogUtil.e("获取大藏经足迹：：" + js);
            OkGo.post(Constants.DaZangHistory)
                    .tag(this)
                    .params("key", m.K())
                    .params("msg", m.M())
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            HashMap<String, String> msg = AnalyticalJSON.getHashMap(s);

                            if (msg != null&&"000".equals(msg.get("code"))) {
                                ArrayList<HashMap<String, String>> list = AnalyticalJSON.getList_zj(msg.get("msg"));
                                if (list != null) {
                                    if (isRefresh) {
                                        adapter.setNewData(list);
                                        isRefresh = false;
                                        swip.setRefreshing(false);
                                    } else if (isLoadMore) {
                                        isLoadMore = false;
                                        if (list.size() < 10) {
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


                        }

                        @Override
                        public void onAfter(String s, Exception e) {
                            super.onAfter(s, e);
                            swip.setRefreshing(false);
                        }

                    });
        }
    }

    public void writeTxtOrOpenBook(String Id, String content) {
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "DangZang/", "DaZangJing" + Id + ".txt");

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
                try {
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
                    BufferedWriter     bufferedWriter     = new BufferedWriter(outputStreamWriter);
                    bufferedWriter.write(content);
                    bufferedWriter.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Book book = new Book(file.getName(), file.getPath());
        book.setId(Id);
        book.setEncoding(FileUtils.getFileEncode2(file.getPath()));
        book.setAccessTime(System.currentTimeMillis());
        Intent intent = new Intent(DZJHistory.this, IRead.class);
        intent.putExtra("book", book);
        intent.putExtra("type", 2);
        startActivity(intent);


    }

    @Override
    public void onRefresh() {

        page = 1;
        isRefresh = true;
        adapter.setEnableLoadMore(true);
        getNotice();
    }
}
