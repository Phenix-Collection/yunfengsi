package com.yunfengsi.Models.E_Book;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.yunfengsi.Models.E_Book.TreeBean.Book;
import com.yunfengsi.R;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.FileUtils;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.ProgressUtil;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.ToastUtil;

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

public class DaZangSearch extends AppCompatActivity {

    private int pageSize = 10;
    private int page = 1;
    private int endPage = -1;
    private boolean isLoadMore = false;
    private MessageAdapter adapter;
   private  TextView footter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_da_zang_search);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        ((ImageView) findViewById(R.id.title_back)).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.title_title)).setText("搜索结果");
        ((ImageView) findViewById(R.id.title_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        final String content = getIntent().getStringExtra("content");
        RecyclerView recyclerView = findViewById(R.id.results);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageAdapter(this, new ArrayList<HashMap<String, String>>());
        adapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
//        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
//            @Override
//            public void onLoadMoreRequested() {
//                if (endPage != page) {
//                    isLoadMore = true;
//                    page++;
//                    getResults(content);
//                }
//            }
//        }, recyclerView);
         footter=new TextView(this);
        ViewGroup.LayoutParams vl=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        footter.setLayoutParams(vl);
        footter.setPadding(0,DimenUtils.dip2px(this,8),0,DimenUtils.dip2px(this,8));
        footter.setText("点击加载更多");
        footter.setTextSize(16);
        footter.setTextColor(Color.BLACK);
        footter.setGravity(Gravity.CENTER);
        footter.setVisibility(View.GONE);
        adapter.setFooterView(footter);
        footter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLoadMore = true;
                page++;
                getResults(content);
            }
        });
        recyclerView.setAdapter(adapter);


        getResults(content);
    }

    private class MessageAdapter extends BaseQuickAdapter<HashMap<String, String>, BaseViewHolder> {
        private Context context;

        public MessageAdapter(Context context, @Nullable List<HashMap<String, String>> data) {
            super(R.layout.item_jinshu_dazang, data);
            this.context = context;
        }

        @Override
        protected void convert(BaseViewHolder helper, final HashMap<String, String> map) {

            try {
                helper.setText(R.id.text, URLDecoder.decode(map.get("title"), "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                LogUtil.e("转码失败");
            }

            Glide.with(context).load(R.mipmap.book_normal)
                    .override(DimenUtils.dip2px(context, 30), DimenUtils.dip2px(context, 30))
                    .into((ImageView) helper.getView(R.id.icon));

            helper.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: 2018/3/15  获取content直接进入书籍
                    getNext(map);
                }
            });

        }
    }

    public void getNext(final HashMap<String, String> map) {
        final JSONObject js = new JSONObject();
        try {
            js.put("id", map.get("id"));
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
                                    ToastUtil.showToastShort("该经书内容正在积极添加中，敬请期待");
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
                        ProgressUtil.show(DaZangSearch.this, "", "请稍等");
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
                    BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
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
        Intent intent = new Intent(DaZangSearch.this, IRead.class);
        intent.putExtra("book", book);
        intent.putExtra("type", 2);
        startActivity(intent);


    }

    private void getResults(String content) {
        JSONObject js = new JSONObject();
        try {
            js.put("type", "2");
            js.put("content", content);
            js.put("m_id", Constants.M_id);
            js.put("page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e("大藏经搜素结果：：" + js);
        ApisSeUtil.M m = ApisSeUtil.i(js);
        OkGo.post(Constants.DaZang_Search).params("key", m.K())
                .params("msg", m.M()).execute(new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                HashMap<String, String> map = AnalyticalJSON.getHashMap(s);
                if (map != null) {
                    if (map.get("code") != null) {
                        if ("003".equals(map.get("code"))) {
                            ToastUtil.showToastShort("未搜索到相关内容");
                            adapter.loadMoreEnd(false);
                        } else if ("000".equals(map.get("code"))) {
                            ArrayList<HashMap<String, String>> list = AnalyticalJSON.getList_zj(map.get("msg"));
                            LogUtil.e("搜索结果：：" + list);
                            if (list != null) {
                                if (!isLoadMore) {
                                    adapter.setNewData(list);
                                    footter.setVisibility(View.VISIBLE);
                                    if (list.size() < 10) {
                                        endPage = page;
                                        footter.setText("没有更多数据了");
                                        footter.setEnabled(false);
                                        footter.setTextColor(Color.parseColor("#d6d6d6"));
                                    }
                                } else {
                                    isLoadMore = false;
                                    if (list.size() < 10) {
                                        endPage = page;
                                        footter.setText("没有更多数据了");
                                        footter.setEnabled(false);
                                        footter.setTextColor(Color.parseColor("#d6d6d6"));
                                        adapter.addData(list);
                                    } else {
                                        adapter.addData(list);

                                    }
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onBefore(BaseRequest request) {
                super.onBefore(request);
                ProgressUtil.show(DaZangSearch.this, "", "请稍等");
                ProgressUtil.canCancelAble(false);
            }

            @Override
            public void onAfter(String s, Exception e) {
                super.onAfter(s, e);
                ProgressUtil.dismiss();
            }
        });
    }
}
