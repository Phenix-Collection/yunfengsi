package com.yunfengsi.Setting;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.yunfengsi.Adapter.Mine_SC_adapter;
import com.yunfengsi.R;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.mApplication;
import com.yunfengsi.View.LoadMoreListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/10/18.
 */
public class Activity_ShouCang_Result extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener ,LoadMoreListView.OnLoadMore{
    public ImageView back, tip;
    private TextView           title;
    private SwipeRefreshLayout swipeRefreshLayout;
    public LoadMoreListView   listView;
    private SharedPreferences  sp;
    private Mine_SC_adapter    SCadapter;
    private String page = "1";
    private String endPage = "";
    private static final int PAGE_SIZE=10;
    private static final String TAG = "Activity_ShouCang";
    List<HashMap<String, String>> list;
    private boolean isRefresh=true;
    private boolean isFirstIn = true;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shoucang_activity_result);
        initView();
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                onRefresh();
            }
        });


    }

    private void getShouCang(){
        JSONObject js=new JSONObject();
        try {
            js.put("m_id",Constants.M_id);
            js.put("user_id", PreferenceUtil.getUserId(this));
            js.put("content",getIntent().getStringExtra("text"));
            js.put("page",page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m=ApisSeUtil.i(js);
        LogUtil.e("收藏搜索列表：："+js);
        OkGo.post(Constants.Collect_Search_List).tag(this).params("key",m.K())
                .params("msg",m.M())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if(s!=null){
                            HashMap<String,String > map=AnalyticalJSON.getHashMap(s);
                            if(map!=null){
                               ArrayList<HashMap<String,String >> list=AnalyticalJSON.getList_zj(map.get("msg"));
                                if(list!=null){
                                    final TextView    t = (TextView) (listView.footer.findViewById(R.id.load_more_text));
                                    final ProgressBar p = (ProgressBar) (listView.footer.findViewById(R.id.load_more_bar));
                                    if(list.size()<PAGE_SIZE){
                                        endPage=page;
                                    }
                                    if(isRefresh){
                                        isRefresh=false;

                                        SCadapter.list=list;
                                        if(isFirstIn){
                                            isFirstIn=false;
                                            listView.setAdapter(SCadapter);
                                        }else{
                                            SCadapter.notifyDataSetChanged();
                                        }

                                        if(!endPage.equals(page)){
                                            if (t.getText().toString().equals("没有更多数据了")) {
                                                p.setVisibility(View.VISIBLE);
                                                t.setText("正在加载....");
                                            }
                                            listView.onLoadComplete();
                                        }else{
                                            if(list.size()!=0){
                                                listView.footer.setVisibility(View.VISIBLE);
                                                p.setVisibility(View.GONE);
                                                t.setText("没有更多数据了");
                                                t.setVisibility(View.VISIBLE);
                                            }else{
                                                listView.footer.setVisibility(View.GONE);
                                            }

                                        }


                                    }else{
                                        SCadapter.list.addAll(list);
                                        SCadapter.notifyDataSetChanged();
                                        if (endPage.equals(page)) {
                                            p.setVisibility(View.GONE);
                                            t.setText("没有更多数据了");
                                            return;
                                        } else {
                                            p.setVisibility(View.VISIBLE);
                                            t.setText("正在加载....");
                                            listView.onLoadComplete();
                                        }
                                    }
                                    if(swipeRefreshLayout.isRefreshing()){
                                        swipeRefreshLayout.setRefreshing(false);
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onAfter(String s, Exception e) {
                        super.onAfter(s, e);
                        swipeRefreshLayout.setRefreshing(false);
                        if(SCadapter.list==null||SCadapter.list.size()==0){
                            listView.footer.setVisibility(View.GONE);
                            tip.setVisibility(View.VISIBLE);
                        }else{
                            tip.setVisibility(View.GONE);
                        }
                    }
                });

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(this);
    }


    private void initView() {

        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        back = (ImageView) findViewById(R.id.title_back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        back.setImageResource(R.drawable.back);
        title = (TextView) findViewById(R.id.title_title);
        title.setText(mApplication.ST("搜索结果"));
        tip = (ImageView) findViewById(R.id.shoucang_tip);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swip);
        swipeRefreshLayout.setColorSchemeResources(R.color.main_color);
        swipeRefreshLayout.setOnRefreshListener(this);
        list = new ArrayList<>();
        listView = (LoadMoreListView) findViewById(R.id.shoucang_listview);
        listView.setLoadMoreListen(this);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                TextView view1 = (TextView) view.findViewById(R.id.mine_shoucang_item_type);
//                String id1 = view1.getTag().toString();
//                Intent intent = new Intent();
//
//                if (view1.getText().toString().equals(mApplication.ST("图文"))) {
//                    intent.setClass(mApplication.getInstance(), ZiXun_Detail.class);
//                } else if (view1.getText().toString().equals(mApplication.ST("活动"))) {
//                    intent.setClass(mApplication.getInstance(), ActivityDetail.class);
//                } else if (view1.getText().toString().equals(mApplication.ST("助学"))) {
//                    intent.setClass(mApplication.getInstance(), FundingDetailActivity.class);
//                }
//                intent.putExtra("id", id1);
//                startActivityForResult(intent, 0);
//            }
//        });
        SCadapter=new Mine_SC_adapter(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;

        }
    }

    @Override
    public void onRefresh() {
        isRefresh = true;
        page = "1";
        endPage = "";
        getShouCang();
    }
    @Override
    public void loadMore() {
        if (!endPage.equals(page)) {
            page = String.valueOf(Integer.parseInt(page) + 1);
        } else {
            final ProgressBar p = (ProgressBar) (listView.footer.findViewById(R.id.load_more_bar));
            final TextView t = (TextView) (listView.footer.findViewById(R.id.load_more_text));
            p.setVisibility(View.GONE);
            t.setText("没有更多数据了");
            return;
        }
        getShouCang();
    }
}
