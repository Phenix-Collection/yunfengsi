package com.yunfengsi.NianFo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.yunfengsi.Adapter.Nianfo_home_Adaper;
import com.yunfengsi.Adapter.mArrayAdapter;
import com.yunfengsi.R;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.NumUtils;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.ProgressUtil;
import com.yunfengsi.Utils.ShareManager;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.TimeUtils;
import com.yunfengsi.Utils.mApplication;
import com.yunfengsi.View.LoadMoreListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/8/1.
 */
public class nianfo_home_tab2 extends AppCompatActivity implements View.OnClickListener,SwipeRefreshLayout.OnRefreshListener,LoadMoreListView.OnLoadMore {
    private static final String TAG = "nianfo_home_tab2";
    private LoadMoreListView listView;
    private Nianfo_home_Adaper adapter;
    private List<HashMap<String, String>> list;
    private boolean isLoaded = false;
    public String totalNum = "";
    private ArrayList<HashMap<String, String>> typelist;
    private SharedPreferences sp;
    private TextView username;
    private EditText type;
    private TextView commit, num;
    private Handler handler = new Handler();
    private nianfo_home_tab2 context;
    private SwipeRefreshLayout swip;
    private boolean isRefresh=false;
    private String page="1";
    private String endPage="";
    private InputMethodManager imm;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.fragment_nianfo_home_tab1);
        listView = (LoadMoreListView) findViewById(R.id.nianfo_home_tab1_listview);
        listView.setLoadMoreListen(this);
        swip= (SwipeRefreshLayout) findViewById(R.id.nianfo_1_swip);
        swip.setOnRefreshListener(this);
        swip.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        adapter = new Nianfo_home_Adaper(this, "诵经");
//        list = new ArrayList<>();
        typelist = new ArrayList<>();
        imm= (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        type = (EditText) findViewById(R.id.nianfo_home_tab1_type);
        type.setFocusable(false);
        findViewById(R.id.nianfo_home_tab1_chaxunchengji).setOnClickListener(this);
        sp = getSharedPreferences("user", Context.MODE_PRIVATE);
        username = (TextView)findViewById(R.id.nianfo_home_tab1_mName);
        commit = (TextView)findViewById(R.id.nianfo_home_tab1_commit);
        num = (TextView) findViewById(R.id.nianfo_home_tab1_num);
        username.setText(sp.getString("pet_name", ""));
        swip.post(new Runnable() {
            @Override
            public void run() {
                swip.setRefreshing(true);
                loadData();
            }
        });

        type.setOnClickListener(this);
        commit.setOnClickListener(this);
        findViewById(R.id.nianfo_home_back).setOnClickListener(this);
        ((TextView) findViewById(R.id.sb)).setText(mApplication.ST("部"));
        ((TextView) findViewById(R.id.sb2)).setText(mApplication.ST("诵"));
        type.setHint(mApplication.ST("请选择经书"));
        num.setHint(mApplication.ST("请输入数目"));
        commit.setText(mApplication.ST("提交"));
        Glide.with(this).load(PreferenceUtil.getUserIncetance(this).getString("head_url",""))
                .override(DimenUtils.dip2px(this,40),DimenUtils.dip2px(this,40)).into((ImageView) findViewById(R.id.head));
        ((TextView) findViewById(R.id.nianfo_home_tab1_chaxunchengji)).setText(mApplication.ST("查看累计成绩"));
    }


    private void loadData() {
        if (!isLoaded) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final TextView t = (TextView) (listView.footer.findViewById(R.id.load_more_text));
                        final ProgressBar p = (ProgressBar) (listView.footer.findViewById(R.id.load_more_bar));
                        JSONObject js=new JSONObject();
                        try {
                            js.put("page", page);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String data1 = OkGo.post(Constants.nianfo_home_songjing_Get_Ip)
                                .params("key", ApisSeUtil.getKey())
                                .params("msg",ApisSeUtil.getMsg(js)).tag(TAG).execute().body().string();
                        if (!data1.equals("")) {
                            list = AnalyticalJSON.getList(data1, "readingd");
                            typelist = AnalyticalJSON.getList(data1, "reading");
                            if (null != AnalyticalJSON.getHashMap(data1)) {
                                totalNum = AnalyticalJSON.getHashMap(data1).get("num") == null ? "0" : AnalyticalJSON.getHashMap(data1).get("num");
                            }
                            if (list != null
                                    && handler != null) {
                                if (list.size() != 10) endPage = page;
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        ProgressUtil.dismiss();
                                        ((TextView) findViewById(R.id.nianfo_title)).setText(mApplication.ST("公共累计诵经"+ NumUtils.getNumStr(totalNum)+"部"));
                                        if (adapter.mlist.size() == 0) {
                                            adapter.addList(list);
                                            listView.setAdapter(adapter);
                                            if(endPage.equals(page)){
                                                t.setText(mApplication.ST("没有更多数据了"));
                                                t.setVisibility(View.VISIBLE);
                                            }
                                        } else {
                                            if (isRefresh) {
                                                isRefresh = false;
                                                adapter.mlist.clear();
                                                adapter.addList(list);
                                                adapter.notifyDataSetChanged();
                                                endPage = "";
                                                if (t.getText().toString().equals(mApplication.ST("没有更多数据了"))) {
                                                    listView.onLoadComplete();
                                                    t.setText(mApplication.ST("正在加载...."));
                                                }
                                            } else {
                                                adapter.mlist.addAll(list);
                                                adapter.notifyDataSetChanged();


                                                if (endPage.equals(page)) {
                                                    p.setVisibility(View.GONE);
                                                    t.setText(mApplication.ST("没有更多数据了"));
                                                } else {
                                                    t.setText(mApplication.ST("正在加载...."));
                                                    listView.onLoadComplete();
                                                }



                                            }
                                        }
                                        if (swip.isRefreshing()) swip.setRefreshing(false);
                                    }
                                });


                            }else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listView.onLoadComplete();
                                        endPage = page;
                                        p.setVisibility(View.GONE);
                                        t.setText(mApplication.ST("没有更多数据了"));
                                        if (swip.isRefreshing()) swip.setRefreshing(false);

                                    }
                                });

                            }

                        }
                    } catch (Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(nianfo_home_tab2.this, mApplication.ST("加载失败，请检查网络连接"), Toast.LENGTH_SHORT).show();
                                if (swip.isRefreshing()) swip.setRefreshing(false);
                                listView.onLoadComplete();

                            }
                        });
                        e.printStackTrace();
                    }
                }
            }).start();

        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        ProgressUtil.dismiss();
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.Share:
                UMWeb umWeb=new UMWeb("http://a.app.qq.com/o/simple.jsp?pkgname=com.ytl.qianyishenghao");
                umWeb.setTitle("千亿圣号App");
                umWeb.setDescription("快来千亿圣号共修吧");
                umWeb.setThumb(new UMImage(this,R.drawable.indra));
                new ShareManager().shareWeb(umWeb,this);
                break;
            case R.id.nianfo_home_back:
                finish();
                break;
            case R.id.nianfo_home_tab1_type:
                View view = LayoutInflater.from(this).inflate(R.layout.nianfo_type_dialog, null);
                ListView list = (ListView) view.findViewById(R.id.nianfo_type_listview);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(view);
                final AlertDialog dialog = builder.create();
                if (typelist != null) {
                    ArrayAdapter adapter = new mArrayAdapter(this, R.layout.nianfo_home_dialog_item, R.id.nianfo_home_dialog_item_text, typelist, "诵经");
                    list.setAdapter(adapter);
                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            type.setText(((TextView) view).getText().toString());
                            type.setTag((view).getTag());
                            dialog.dismiss();
                        }
                    });
                }
                Window window = dialog.getWindow();
                window.getDecorView().setPadding(0, 0, 0, 0);
                window.setGravity(Gravity.CENTER);
                WindowManager.LayoutParams wl = window.getAttributes();

                wl.height = WindowManager.LayoutParams.WRAP_CONTENT;
                wl.width = getResources().getDisplayMetrics().widthPixels * 75 / 100;
                window.setAttributes(wl);

                dialog.show();


                if (swip.isRefreshing()) swip.setRefreshing(false);
                ProgressUtil.dismiss();
                dialog.show();
                break;
            case R.id.nianfo_home_tab1_commit:
                v.setEnabled(false);
                if (!type.getText().toString().trim().equals("") && !num.getText().toString().trim().equals("")
                        &&
                        !num.getText().toString().trim().equals("0")) {
                    ProgressUtil.show(nianfo_home_tab2.this,"",mApplication.ST("正在提交"));
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String data1 = null;
                            try {
                                JSONObject js=new JSONObject();
                                try {
                                    js.put("user_id", sp.getString("user_id", ""));
                                    js.put("gongke_id", type.getTag().toString());
                                    js.put("num", num.getText().toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                data1 = OkGo.post(Constants.nianfo_home_songjing_Commit_Ip).tag(TAG)
                                        .params("key", ApisSeUtil.getKey())
                                        .params("msg", ApisSeUtil.getMsg(js)).execute().body().string();
                                if (!data1.equals("")) {
                                    final HashMap<String ,String >m=AnalyticalJSON.getHashMap(data1);
                                    if (m!=null&&"000".equals(m.get("code"))) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                ProgressUtil.dismiss();
                                                HashMap<String, String> map = new HashMap<>();
                                                map.put("ls_time", TimeUtils.getStrTime(System.currentTimeMillis() + ""));
                                                map.put("rg_name", type.getText().toString());
                                                map.put("ls_nfnum", num.getText().toString());
                                                map.put("user_image", sp.getString("head_url", ""));
                                                map.put("pet_name", username.getText().toString());
                                                map.put("nf_id",m.get("nf_id"));
                                                map.put("id",sp.getString("user_id",""));
                                                adapter.mlist.add(0,map);
                                                adapter.notifyDataSetChanged();
                                                listView.setSelection(0);
                                                type.setText("");
                                                num.setText("");
                                                v.setEnabled(true);
                                                imm.hideSoftInputFromWindow(num.getWindowToken(),0);
                                            }
                                        });

                                    }
                                }
                            } catch (Exception e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ProgressUtil.dismiss();
                                    }
                                });
                                e.printStackTrace();
                            }

                        }
                    }).start();


                } else {
                    Toast.makeText(mApplication.getInstance(), mApplication.ST("请仔细填写功课内容"), Toast.LENGTH_SHORT).show();
                    v.setEnabled(true);
                }


                break;
            case R.id.nianfo_home_tab1_chaxunchengji:
                Intent intet = new Intent(mApplication.getInstance(), NianFo_Detail.class);
                intet.putExtra("type","诵经");
                startActivity(intet);
                break;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(TAG);
    }
    @Override
    public void onRefresh() {
        page = "1";
        isRefresh = true;
        endPage = "";
        loadData();
    }

    @Override
    public void loadMore() {

        if (!endPage.equals(page)) {
            page = String.valueOf(Integer.parseInt(page) + 1);
        }else{
            final ProgressBar p = (ProgressBar) (listView.footer.findViewById(R.id.load_more_bar));
            final TextView t = (TextView) (listView.footer.findViewById(R.id.load_more_text));
            p.setVisibility(View.GONE);
            t.setText(mApplication.ST("没有更多数据了"));
            return;
        }
        loadData();
    }
}
