package com.yunfengsi.Models.E_Book;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.yunfengsi.Models.E_Book.TreeBean.Book;
import com.yunfengsi.Models.E_Book.TreeBean.DaZang1;
import com.yunfengsi.Models.E_Book.TreeBean.DaZang2;
import com.yunfengsi.Models.E_Book.TreeBean.DaZang3;
import com.yunfengsi.Models.E_Book.TreeBean.DaZang4;
import com.yunfengsi.Models.E_Book.TreeBean.DaZang5;
import com.yunfengsi.Models.E_Book.TreeBean.DaZang6;
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
import com.yunfengsi.Utils.ShareManager;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.ToastUtil;
import com.yunfengsi.Utils.mApplication;
import com.yunfengsi.View.BookRecyclerView;
import com.yunfengsi.View.mItemDecoration;

import org.json.JSONArray;
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

public class BookList extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private static final String TAG = "BookList";
    public static final  String DZ  = "dazang";
    private BookRecyclerView                   recyclerView;
    private BookAdapter                        adapter;
    private ArrayList<HashMap<String, Object>> list;
    private SwipeRefreshLayout                 swip;
    private int     pageSize   = 15;
    private int     page       = 1;
    private int     endPage    = -1;
    private boolean isLoadMore = false;
    //    private MyGridView gridView;
//    private gridAdapter adapter;
    private boolean isRefresh  = true;

    //    private final BookCollectionShadow myCollection = new BookCollectionShadow();

    private TextView                   dazang;
    private RecyclerView               treeRecycle;
    private ArrayList<MultiItemEntity> mainList;
    private TreeAdapter                treeAdapter;
    private EditText                   edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);
        mApplication.getInstance().addActivity(this);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        edit = findViewById(R.id.search_edit);
        TextView search = findViewById(R.id.search);
        search.setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        TextView fojing = findViewById(R.id.fojing);
        fojing.setOnClickListener(this);
        fojing.setEnabled(false);
        dazang = findViewById(R.id.dazang);
        dazang.setOnClickListener(this);
        findViewById(R.id.share).setOnClickListener(this);
        findViewById(R.id.zuji).setOnClickListener(this);

        treeRecycle = findViewById(R.id.dazang_mulu);
        treeRecycle.setLayoutManager(new LinearLayoutManager(this));
        treeRecycle.addItemDecoration(new mItemDecoration(this));

        swip = findViewById(R.id.swip);
        swip.setColorSchemeResources(R.color.main_color);
        swip.setOnRefreshListener(this);

        recyclerView = findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        mainList = new ArrayList<>();

        treeAdapter = new TreeAdapter(mainList);
        treeRecycle.setAdapter(treeAdapter);

        adapter = new BookAdapter(this, list, recyclerView);
        recyclerView.setAdapter(adapter);

        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (endPage != page) {
                    isLoadMore = true;
                    page++;
                    getList();
                }
            }
        }, recyclerView);
        adapter.disableLoadMoreIfNotFullPage();
        swip.post(new Runnable() {
            @Override
            public void run() {
                swip.setRefreshing(true);
                onRefresh();
            }
        });
        verifyStoragePermissions(this);

        getMainList();
        if (getIntent().getIntExtra("type", 0) == 2) {
            dazang.performClick();
        }



    }



    @Override
    protected void onResume() {
        super.onResume();


    }

    private void getMainList() {
        if (!Network.HttpTest(this)) {
            ToastUtil.showToastShort("请检查网络连接");
            return;
        }
        JSONObject js = new JSONObject();
        try {
            js.put("m_id", Constants.M_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m = ApisSeUtil.i(js);
        LogUtil.e("获取顶级目录：：" + js);
        OkGo.post(Constants.DaZang_Chapter_Top).tag(this).params("key", m.K())
                .params("msg", m.M())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        HashMap<String, String> map = AnalyticalJSON.getHashMap(s);
                        if (map != null) {
                            if (map.get("code") != null && "000".equals(map.get("code"))) {
                                try {
                                    JSONArray jsonArray = new JSONArray(map.get("msg"));
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject js = (JSONObject) jsonArray.get(i);
                                        DaZang1    d1 = new DaZang1();
                                        d1.setId(js.getString("id"));
                                        d1.setTitle(js.getString("title"));
                                        d1.setLoaded(false);
                                        mainList.add(d1);

                                    }
                                    treeAdapter.setNewData(mainList);
                                    LogUtil.e("顶级目录：：；" + mainList + "    " + treeRecycle.getAdapter());


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    onError(call, response, e);
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
                        ProgressUtil.show(BookList.this, "", "正在加载目录..");
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        LogUtil.e("获取顶级目录失败：：" + e);
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
        Intent intent = new Intent(BookList.this, IRead.class);
        intent.putExtra("book", book);
        intent.putExtra("type", 2);
        startActivity(intent);


    }

    private class TreeAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {
        /**
         * Same as QuickAdapter#QuickAdapter(Context,int) but with
         * some initialization data.
         *
         * @param data A new list is created out of this one to avoid mutable list
         */
        public ArrayList<String> hadLoadedItemsIds = new ArrayList<>();

        public TreeAdapter(List<MultiItemEntity> data) {
            super(data);
            addItemType(0, R.layout.item_jinshu_dazang);
            addItemType(1, R.layout.item_jinshu_dazang2);
            addItemType(2, R.layout.item_jinshu_dazang3);
            addItemType(3, R.layout.item_jinshu_dazang4);
            addItemType(4, R.layout.item_jinshu_dazang5);
            addItemType(5, R.layout.item_jinshu_dazang6);

        }


        @Override
        protected void convert(BaseViewHolder helper, final MultiItemEntity item) {
            switch (helper.getItemViewType()) {
                case 0:
                    final DaZang1 daZang1 = (DaZang1) item;
                    final int pos = helper.getAdapterPosition();
                    try {
                        helper.setText(R.id.text, URLDecoder.decode(daZang1.getTitle(), "utf-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        LogUtil.e("转码失败");
                    }
                    if (((DaZang1) item).getSubItems() != null) {
                        Glide.with(BookList.this).load(R.mipmap.book_open)
                                .override(DimenUtils.dip2px(BookList.this, 30), DimenUtils.dip2px(BookList.this, 30))
                                .into((ImageView) helper.getView(R.id.icon));
                    } else {
                        Glide.with(BookList.this).load(R.mipmap.book_normal)
                                .override(DimenUtils.dip2px(BookList.this, 30), DimenUtils.dip2px(BookList.this, 30))
                                .into((ImageView) helper.getView(R.id.icon));
                    }
                    helper.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (daZang1.getContent() != null && !daZang1.getContent().equals("")) {
                                // TODO: 2018/3/15  直接进入书籍
                                writeTxtOrOpenBook(daZang1.getId(), daZang1.getContent());

                            } else {
                                if (daZang1.getSubItems() == null) {
                                    getNext(daZang1, pos);
                                } else {
//                                if (!hadLoadedItemsIds.contains(daZang1.getId())) {
//                                    hadLoadedItemsIds.add(daZang1.getId());
//                                }
                                    if (((DaZang1) item).isExpanded()) {
                                        collapse(pos);
                                    } else {
                                        expand(pos);
                                    }
                                    notifyDataSetChanged();
                                }
                            }


                        }
                    });
                    break;
                case 1:
                    final DaZang2 daZang2 = (DaZang2) item;
                    final int pos2 = helper.getAdapterPosition();
                    try {
                        helper.setText(R.id.text, URLDecoder.decode(daZang2.getTitle(), "utf-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        LogUtil.e("转码失败");
                    }
                    if (((DaZang2) item).getSubItems() != null) {
                        Glide.with(BookList.this).load(R.mipmap.book_open)
                                .override(DimenUtils.dip2px(BookList.this, 30), DimenUtils.dip2px(BookList.this, 30))
                                .into((ImageView) helper.getView(R.id.icon));
                    } else {
                        Glide.with(BookList.this).load(R.mipmap.book_normal)
                                .override(DimenUtils.dip2px(BookList.this, 30), DimenUtils.dip2px(BookList.this, 30))
                                .into((ImageView) helper.getView(R.id.icon));
                    }
                    helper.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (daZang2.getContent() != null && !daZang2.getContent().equals("")) {
                                // TODO: 2018/3/15  直接进入书籍
                                writeTxtOrOpenBook(daZang2.getId(), daZang2.getContent());
                            } else {
                                if (daZang2.getSubItems() == null) {
                                    getNext(daZang2, pos2);
                                } else {
//                                if (!hadLoadedItemsIds.contains(daZang1.getId())) {
//                                    hadLoadedItemsIds.add(daZang1.getId());
//                                }
                                    if (((DaZang2) item).isExpanded()) {
                                        collapse(pos2);
                                    } else {
                                        expand(pos2);
                                    }
                                    notifyDataSetChanged();
                                }
                            }


                        }
                    });
                    break;
                case 2:
                    final DaZang3 daZang3 = (DaZang3) item;
                    final int pos3 = helper.getAdapterPosition();
                    try {
                        helper.setText(R.id.text, URLDecoder.decode(daZang3.getTitle(), "utf-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        LogUtil.e("转码失败");
                    }
                    if (((DaZang3) item).getSubItems() != null) {
                        Glide.with(BookList.this).load(R.mipmap.book_open)
                                .override(DimenUtils.dip2px(BookList.this, 30), DimenUtils.dip2px(BookList.this, 30))
                                .into((ImageView) helper.getView(R.id.icon));
                    } else {
                        Glide.with(BookList.this).load(R.mipmap.book_normal)
                                .override(DimenUtils.dip2px(BookList.this, 30), DimenUtils.dip2px(BookList.this, 30))
                                .into((ImageView) helper.getView(R.id.icon));
                    }
                    helper.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (daZang3.getContent() != null && !daZang3.getContent().equals("")) {
                                // TODO: 2018/3/15  直接进入书籍
                                writeTxtOrOpenBook(daZang3.getId(), daZang3.getContent());
                            } else {
                                if (daZang3.getSubItems() == null) {
                                    getNext(daZang3, pos3);
                                } else {
//                                if (!hadLoadedItemsIds.contains(daZang1.getId())) {
//                                    hadLoadedItemsIds.add(daZang1.getId());
//                                }
                                    if (((DaZang3) item).isExpanded()) {
                                        collapse(pos3);
                                    } else {
                                        expand(pos3);
                                    }
                                    notifyDataSetChanged();
                                }
                            }


                        }
                    });
                    break;
                case 3:
                    final DaZang4 daZang4 = (DaZang4) item;
                    final int pos4 = helper.getAdapterPosition();
                    try {
                        helper.setText(R.id.text, URLDecoder.decode(daZang4.getTitle(), "utf-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        LogUtil.e("转码失败");
                    }
                    if (((DaZang4) item).getSubItems() != null) {
                        Glide.with(BookList.this).load(R.mipmap.book_open)
                                .override(DimenUtils.dip2px(BookList.this, 30), DimenUtils.dip2px(BookList.this, 30))
                                .into((ImageView) helper.getView(R.id.icon));
                    } else {
                        Glide.with(BookList.this).load(R.mipmap.book_normal)
                                .override(DimenUtils.dip2px(BookList.this, 30), DimenUtils.dip2px(BookList.this, 30))
                                .into((ImageView) helper.getView(R.id.icon));
                    }
                    helper.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (daZang4.getContent() != null && !daZang4.getContent().equals("")) {
                                // TODO: 2018/3/15  直接进入书籍
                                writeTxtOrOpenBook(daZang4.getId(), daZang4.getContent());
                            } else {
                                if (daZang4.getSubItems() == null) {
                                    getNext(daZang4, pos4);
                                } else {
//                                if (!hadLoadedItemsIds.contains(daZang1.getId())) {
//                                    hadLoadedItemsIds.add(daZang1.getId());
//                                }
                                    if (((DaZang4) item).isExpanded()) {
                                        collapse(pos4);
                                    } else {
                                        expand(pos4);
                                    }
                                    notifyDataSetChanged();
                                }
                            }


                        }
                    });
                    break;
                case 4:
                    final DaZang5 daZang5 = (DaZang5) item;
                    final int pos5 = helper.getAdapterPosition();
                    try {
                        helper.setText(R.id.text, URLDecoder.decode(daZang5.getTitle(), "utf-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        LogUtil.e("转码失败");
                    }

                    if (((DaZang5) item).getSubItems() != null) {
                        Glide.with(BookList.this).load(R.mipmap.book_open)
                                .override(DimenUtils.dip2px(BookList.this, 30), DimenUtils.dip2px(BookList.this, 30))
                                .into((ImageView) helper.getView(R.id.icon));
                    } else {
                        Glide.with(BookList.this).load(R.mipmap.book_normal)
                                .override(DimenUtils.dip2px(BookList.this, 30), DimenUtils.dip2px(BookList.this, 30))
                                .into((ImageView) helper.getView(R.id.icon));
                    }
                    helper.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (daZang5.getContent() != null && !daZang5.getContent().equals("")) {
                                // TODO: 2018/3/15  直接进入书籍
                                writeTxtOrOpenBook(daZang5.getId(), daZang5.getContent());
                            } else {
                                if (daZang5.getSubItems() == null) {
                                    getNext(daZang5, pos5);
                                } else {
//                                if (!hadLoadedItemsIds.contains(daZang1.getId())) {
//                                    hadLoadedItemsIds.add(daZang1.getId());
//                                }
                                    if (((DaZang5) item).isExpanded()) {
                                        collapse(pos5);
                                    } else {
                                        expand(pos5);
                                    }
                                    notifyDataSetChanged();
                                }
                            }


                        }
                    });
                    break;
                case 5:
                    final DaZang6 daZang6 = (DaZang6) item;
                    final int pos6 = helper.getAdapterPosition();
                    try {
                        helper.setText(R.id.text, URLDecoder.decode(daZang6.getTitle(), "utf-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        LogUtil.e("转码失败");
                    }
                    if (((DaZang6) item).getSubItems() != null) {
                        Glide.with(BookList.this).load(R.mipmap.book_open)
                                .override(DimenUtils.dip2px(BookList.this, 30), DimenUtils.dip2px(BookList.this, 30))
                                .into((ImageView) helper.getView(R.id.icon));
                    } else {
                        Glide.with(BookList.this).load(R.mipmap.book_normal)
                                .override(DimenUtils.dip2px(BookList.this, 30), DimenUtils.dip2px(BookList.this, 30))
                                .into((ImageView) helper.getView(R.id.icon));
                    }
                    helper.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (daZang6.getContent() != null && !daZang6.getContent().equals("")) {
                                // TODO: 2018/3/15  直接进入书籍
                                writeTxtOrOpenBook(daZang6.getId(), daZang6.getContent());
                            } else {
                                if (daZang6.getSubItems() == null) {
                                    getNext(daZang6, pos6);
                                } else {
//                                if (!hadLoadedItemsIds.contains(daZang1.getId())) {
//                                    hadLoadedItemsIds.add(daZang1.getId());
//                                }
                                    if (((DaZang6) item).isExpanded()) {
                                        collapse(pos6);
                                    } else {
                                        expand(pos6);
                                    }
                                    notifyDataSetChanged();
                                }
                            }


                        }
                    });
                    break;
            }
        }
    }

    private void getNext(final MultiItemEntity multiItemEntity, final int pos) {
        final JSONObject js = new JSONObject();
        try {
            switch (multiItemEntity.getItemType()) {
                case 0:
                    js.put("id", ((DaZang1) multiItemEntity).getId());
                    break;

                case 1:
                    js.put("id", ((DaZang2) multiItemEntity).getId());
                    break;
                case 2:
                    js.put("id", ((DaZang3) multiItemEntity).getId());
                    break;
                case 3:
                    js.put("id", ((DaZang4) multiItemEntity).getId());
                    break;
                case 4:
                    js.put("id", ((DaZang5) multiItemEntity).getId());
                    break;
                case 5:
                    js.put("id", ((DaZang6) multiItemEntity).getId());
                    break;

            }
            js.put("m_id", Constants.M_id);
            js.put("user_id", PreferenceUtil.getUserId(this));
            js.put("level", multiItemEntity.getItemType() + 1);


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
                        HashMap<String, String> map = AnalyticalJSON.getHashMap(s);
                        if (map != null) {
                            if (map.get("code") != null && "000".equals(map.get("code"))) {
                                try {
                                    JSONArray jsonArray = new JSONArray(map.get("msg"));
                                    ArrayList list      = new ArrayList();
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject js = (JSONObject) jsonArray.get(i);
                                        switch (multiItemEntity.getItemType()) {
                                            case 0:
                                                DaZang2 daZang2 = new DaZang2();
                                                daZang2.setId(js.getString("id"));
                                                daZang2.setTitle(js.getString("title"));
                                                list.add(daZang2);
                                                ((DaZang1) multiItemEntity).setSubItems(list);
                                                break;

                                            case 1:
                                                DaZang3 daZang3 = new DaZang3();
                                                daZang3.setId(js.getString("id"));
                                                daZang3.setTitle(js.getString("title"));
                                                list.add(daZang3);
                                                ((DaZang2) multiItemEntity).setSubItems(list);
                                                break;
                                            case 2:
                                                DaZang4 daZang4 = new DaZang4();
                                                daZang4.setId(js.getString("id"));
                                                daZang4.setTitle(js.getString("title"));
                                                list.add(daZang4);
                                                ((DaZang3) multiItemEntity).setSubItems(list);
                                                break;
                                            case 3:
                                                DaZang5 daZang5 = new DaZang5();
                                                daZang5.setId(js.getString("id"));
                                                daZang5.setTitle(js.getString("title"));
                                                list.add(daZang5);
                                                ((DaZang4) multiItemEntity).setSubItems(list);
                                                break;
                                            case 4:
                                                DaZang6 daZang6 = new DaZang6();
                                                daZang6.setId(js.getString("id"));
                                                daZang6.setTitle(js.getString("title"));
                                                list.add(daZang6);
                                                ((DaZang5) multiItemEntity).setSubItems(list);
                                                break;

                                        }
                                    }
                                    if (((AbstractExpandableItem) multiItemEntity).isExpanded()) {
                                        treeAdapter.collapse(pos);
                                    } else {
                                        treeAdapter.expand(pos);
                                    }
//                                    treeAdapter.notifyItemChanged(pos);
                                    treeAdapter.notifyDataSetChanged();


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    onError(call, response, e);
                                }
                            } else if (map.get("content") != null) {
                                switch (multiItemEntity.getItemType()) {
                                    case 0:

                                        ((DaZang1) multiItemEntity).setContent(map.get("content"));
                                        writeTxtOrOpenBook(((DaZang1) multiItemEntity).getId(), map.get("content"));
                                        break;

                                    case 1:
                                        ((DaZang2) multiItemEntity).setContent(map.get("content"));
                                        writeTxtOrOpenBook(((DaZang2) multiItemEntity).getId(), map.get("content"));
                                        break;
                                    case 2:
                                        ((DaZang3) multiItemEntity).setContent(map.get("content"));
                                        writeTxtOrOpenBook(((DaZang3) multiItemEntity).getId(), map.get("content"));
                                        break;
                                    case 3:
                                        ((DaZang4) multiItemEntity).setContent(map.get("content"));
                                        writeTxtOrOpenBook(((DaZang4) multiItemEntity).getId(), map.get("content"));
                                        break;
                                    case 4:
                                        ((DaZang5) multiItemEntity).setContent(map.get("content"));
                                        writeTxtOrOpenBook(((DaZang5) multiItemEntity).getId(), map.get("content"));
                                        break;
                                    case 5:
                                        ((DaZang6) multiItemEntity).setContent(map.get("content"));
                                        writeTxtOrOpenBook(((DaZang6) multiItemEntity).getId(), map.get("content"));
                                        break;
                                }
                                if (!map.get("content").equals("")) {

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
                        ProgressUtil.show(BookList.this, "", "请稍等");
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

    private void getList() {
        JSONObject js = new JSONObject();
        try {
            js.put("m_id", Constants.M_id);
            js.put("page", page);
            js.put("user_id", PreferenceUtil.getUserId(this));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e("加载经书列表++" + js);
        ApisSeUtil.M m = ApisSeUtil.i(js);
        OkGo.post(Constants.JingShu).params("key", m.K())
                .params("msg", m.M())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        HashMap<String, String> map = AnalyticalJSON.getHashMap(s);
                        if (map != null) {
                            if (map.get("code") != null && map.get("code").equals("000")) {
                                ArrayList list = AnalyticalJSON.getList_zj(map.get("msg"));

                                if (list != null) {
                                    if (isRefresh) {
                                        HashMap<String, String> dz = new HashMap<>();
                                        dz.put("id", DZ);
                                        dz.put("m_id", "1");
                                        dz.put("title", "大藏经");
                                        dz.put("contents", "go to DZ");
                                        dz.put("time", "");
                                        list.add(0, dz);//添加大藏经类目
                                        adapter.setNewData(list);
                                        isRefresh = false;
                                        swip.setRefreshing(false);
                                    } else if (isLoadMore) {
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
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (list == null || list.size() == 0) {
                            list = FileUtils.getStorageMapEntities(BookList.this, TAG);
                            adapter.setNewData(list);
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
        page = 1;
        endPage = -1;
        isRefresh = true;
        isLoadMore = false;
        adapter.setEnableLoadMore(true);
        getList();
    }

    int a = 0;

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.share:
                UMWeb umWeb = new UMWeb("http://a.app.qq.com/o/simple.jsp?pkgname=com.yunfengsi");
                umWeb.setDescription("阅读佛经,明心见性,学习佛家的大智慧和大爱。这里有大量的经书,一起来阅读吧!");
                umWeb.setThumb(new UMImage(this, R.drawable.indra_share));
                umWeb.setTitle("须弥芥子，万千佛法");
                new ShareManager().shareWeb(umWeb, this);
                break;
            case R.id.search:
                if (edit.getText().toString().trim().equals("")) {
                    ToastUtil.showToastShort("请输入搜索内容");
                    return;
                }
                Intent intent = new Intent(this, DaZangSearch.class);
                intent.putExtra("content", edit.getText().toString().trim());
                startActivity(intent);
                break;
            case R.id.fojing:
                view.setEnabled(false);
                findViewById(R.id.dazang).setEnabled(true);
                recyclerView.setVisibility(View.VISIBLE);
                findViewById(R.id.dazang_mulu2).setVisibility(View.GONE);
                findViewById(R.id.zuji).setVisibility(View.GONE);
                break;
            case R.id.dazang:
                view.setEnabled(false);
                findViewById(R.id.fojing).setEnabled(true);
                recyclerView.setVisibility(View.GONE);
                findViewById(R.id.dazang_mulu2).setVisibility(View.VISIBLE);
                findViewById(R.id.zuji).setVisibility(View.VISIBLE);
                break;
            case R.id.zuji:
                startActivity(new Intent(BookList.this,DZJHistory.class));
                break;
        }
    }


    private class BookAdapter extends BaseQuickAdapter<HashMap<String, Object>, BaseViewHolder> {
        private Context          context;
        private BookRecyclerView recyclerView;

        public BookAdapter(Context context, ArrayList<HashMap<String, Object>> data, BookRecyclerView recyclerView) {
            super(R.layout.item_book_shelf, data);
            this.context = context;
            this.recyclerView = recyclerView;
        }

        @Override
        public int getItemCount() {
            int count   = getData().size() % 3 == 0 ? getData().size() / 3 : getData().size() / 3 + 1;
            int height  = DimenUtils.dip2px(context, 180);
            int coulumn = recyclerView.getHeight() / height + 1;

            return count < coulumn ? coulumn : count;
        }

        @Override
        protected void convert(final BaseViewHolder holder, final HashMap<String, Object> map) {
            TextView item1 = holder.getView(R.id.item1);
            TextView item2 = holder.getView(R.id.item2);
            TextView item3 = holder.getView(R.id.item3);

            int column = getData().indexOf(map) + 1;//当前行数

            for (int i = getData().indexOf(map) * 3; i < 3 * column; i++) {

                if (i < getData().size()) {
                    HashMap<String, Object> m = getData().get(i);
                    switch (i % 3) {
                        case 0:
                            if (!m.get("id").equals(DZ)) {
                                item1.setVisibility(View.VISIBLE);
                                item1.setBackgroundResource(R.drawable.jinshu);
                                item1.setTag(R.id.BookPath, m.get("contents"));
                                item1.setTag(R.id.BookId, m.get("id"));
                                item1.setText(m.get("title").toString());
                            }else{
                                item1.setBackgroundResource(R.drawable.dazang_img);
                            }
                            item1.setVisibility(View.VISIBLE);

                            break;
                        case 1:
                            item2.setTag(R.id.BookPath, m.get("contents"));
                            item2.setVisibility(View.VISIBLE);
                            item2.setTag(R.id.BookId, m.get("id"));
                            item2.setText(m.get("title").toString());
                            break;
                        case 2:
                            item3.setTag(R.id.BookPath, m.get("contents"));
                            item3.setVisibility(View.VISIBLE);
                            item3.setTag(R.id.BookId, m.get("id"));
                            item3.setText(m.get("title").toString());
                            break;
                    }
                } else {
                    switch (i % 3) {
                        case 0:
                            item1.setVisibility(View.GONE);
                            item1.setTag(null);
                            break;
                        case 1:
                            item2.setVisibility(View.GONE);
                            item2.setTag(null);
                            break;
                        case 2:
                            item3.setVisibility(View.GONE);
                            item3.setTag(null);
                            break;
                    }
                }
            }


            holder.getView(R.id.item1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LogUtil.e("点击第一列");
                    if(map.get("id").equals(DZ)){
                        dazang.performClick();
                    }else{
                        clickBook(view.getTag(R.id.BookId).toString(), ((TextView) view).getText().toString(), view.getTag(R.id.BookPath).toString());
                    }
                }
            });
            holder.getView(R.id.item2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LogUtil.e("点击第2列");
                    clickBook(view.getTag(R.id.BookId).toString(), ((TextView) view).getText().toString(), view.getTag(R.id.BookPath).toString());
                }
            });
            holder.getView(R.id.item3).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LogUtil.e("点击第3列");
                    clickBook(view.getTag(R.id.BookId).toString(), ((TextView) view).getText().toString(), view.getTag(R.id.BookPath).toString());
                }
            });

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ProgressUtil.dismiss();
    }

    private void clickBook(final String id, final String title, String url) {
        File file = new File(getExternalFilesDir("book"), title + ".txt");
//                    File file=new File(Environment.getExternalStorageDirectory(),"金刚经_314.txt");
        LogUtil.e("file是否存在：：：：" + file.exists() + " 编码格式：：：" + FileUtils.getFileEncode2(file.getPath()));
        if (file.exists()) {
            Book book = new Book();
            book.setAccessTime(System.currentTimeMillis());
            book.setBookName(title);
            book.setId(id);
            book.setEncoding(FileUtils.getFileEncode(file.getPath()));
            book.setPath(file.getPath());
            Intent intent = new Intent(BookList.this, IRead.class);
            intent.putExtra("book", book);
            intent.putExtra("type", 1);
            intent.putExtra("id", id);
            startActivity(intent);

        } else {
            //下载书籍并打开
            OkGo.get(url).execute(new FileCallback(getExternalFilesDir("book").getAbsolutePath(), title + ".txt") {
                @Override
                public void onSuccess(File file, Call call, Response response) {
                    LogUtil.e("file:::" + file.length() + "   filele:::" + file.getName() + " 编码格式：：：" + FileUtils.getFileEncode(file.getPath()));
                    Book book = new Book();
                    book.setAccessTime(System.currentTimeMillis());
                    book.setBookName(title);
                    book.setId(id);
                    book.setEncoding(FileUtils.getFileEncode(file.getPath()));
                    book.setPath(file.getPath());
                    Intent intent = new Intent(BookList.this, IRead.class);
                    intent.putExtra("book", book);
                    intent.putExtra("type", 1);
                    intent.putExtra("id", id);
//            Intent intent = new Intent(BookList.this, Read.class);
//            intent.putExtra("title", title + ".txt");
//            intent.putExtra("path", file.getPath());
                    startActivity(intent);
//                    Intent intent = new Intent(BookList.this, Read.class);
//                    intent.putExtra("title", title+ ".txt");
//                    intent.putExtra("path", file.getPath());
//                    startActivity(intent);
//                                HwTxtPlayActivity.loadTxtFile(BookList.this,file.getPath());
                }

                @Override
                public void onError(Call call, Response response, Exception e) {
                    super.onError(call, response, e);
                    ToastUtil.showToastShort("经书加载失败，请检查网络连接");
                }

                @Override
                public void onBefore(BaseRequest request) {
                    super.onBefore(request);
                    ProgressUtil.show(BookList.this, "", "正在加载经书..");
                }

                @Override
                public void onAfter(File file, Exception e) {
                    super.onAfter(file, e);
                    ProgressUtil.dismiss();
                }
            });
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter.getData() != null) {
            FileUtils.saveStorage2SDCard(this, ((ArrayList<HashMap<String, Object>>) adapter.getData()), TAG);
        }
        OkGo.getInstance().cancelTag(this);
        mApplication.getInstance().romoveActivity(this);
    }

    /**
     * 输入一段字符流，判断其什么编码格式
     *
     * @param head
     * @return
     */
    private String codetype(byte[] head) {
        String code = "";
        if (head[0] == -29 && head[1] == -128 && head[2] == -128)
            code = "UTF-8";
        else {
            code = "GBK";
        }
        return code;
    }

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    66);
        }
    }


}
