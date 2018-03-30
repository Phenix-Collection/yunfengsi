package com.yunfengsi.NianFo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.yunfengsi.Adapter.nianfo_home_zhunian_adapter;
import com.yunfengsi.Login;
import com.yunfengsi.Audio_BD.WakeUp.Recognizelmpl.IBDRcognizeImpl;
import com.yunfengsi.R;
import com.yunfengsi.Setting.Mine_gerenziliao;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.LoginUtil;
import com.yunfengsi.Utils.Network;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.ProgressUtil;
import com.yunfengsi.Utils.ShareManager;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.TimeUtils;
import com.yunfengsi.Utils.mApplication;
import com.yunfengsi.View.DiffuseView;
import com.yunfengsi.View.LoadMoreListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/8/1.
 */
public class nianfo_home_tab4 extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, LoadMoreListView.OnLoadMore {
    private static final String TAG = "nianfo_home_tab4";
    private LoadMoreListView listView;
    private EditText editText;
    private TextView textView;
    private nianfo_home_zhunian_adapter adapter;
    private Handler handler = new Handler();
    private List<HashMap<String, String>> list;
    private SharedPreferences sp;
    private SwipeRefreshLayout swip;
    private boolean isRefresh = false;
    private String page = "1";
    private String endPage = "";
    private InputMethodManager imm;
    private DiffuseView diffuseView;
    private IBDRcognizeImpl ibdRcognize;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.nianfo_home_chanhui);
        mApplication.addActivity(this);
        editText = (EditText) findViewById(R.id.nianfo_home_chanhui_content);
        editText.setHint(mApplication.ST("请输入申请助念内容（200字以内）"));
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(200)});
        diffuseView= (DiffuseView) findViewById(R.id.audio);
        ibdRcognize=new IBDRcognizeImpl(this);
        ibdRcognize.attachView(editText,null,null);
        diffuseView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        diffuseView.start();
                        ibdRcognize.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        diffuseView.stop();
                        ibdRcognize.stop();
                        break;

                }
                return true;
            }
        });
        listView = (LoadMoreListView) findViewById(R.id.nianfo_home_chanhui_listview);
        listView.setLoadMoreListen(this);
        swip = (SwipeRefreshLayout) findViewById(R.id.chanhui_swip);
        swip.setOnRefreshListener(this);
        swip.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light,
                android.R.color.holo_green_light);

        textView = (TextView) findViewById(R.id.nianfo_home_chanhui_commit);
        textView.setText(mApplication.ST("申请助念"));
        sp = getSharedPreferences("user", Context.MODE_PRIVATE);
        list = new ArrayList<>();
        ((TextView) findViewById(R.id.nianfo_title)).setText(mApplication.ST("公共助念目录"));
        adapter = new nianfo_home_zhunian_adapter(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LogUtil.e("点击助念" );
                if (position != parent.getCount() - 1) {
                    String Id = (String) view.findViewById(R.id.nianfo_home_chanhui_item_tip).getTag();
                    Intent intent = new Intent(nianfo_home_tab4.this,
                            MyZuNianActivity.class);
                    intent.putExtra("id", Id);
                    startActivity(intent);
                }
            }
        });
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        swip.post(new Runnable() {
            @Override
            public void run() {
                swip.setRefreshing(true);
                initData();
            }
        });
        textView.setOnClickListener(this);
        findViewById(R.id.nianfo_chanhui_back).setOnClickListener(this);
    }

    private void initData() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final ProgressBar p = (ProgressBar) (listView.footer.findViewById(R.id.load_more_bar));
                    final TextView t = (TextView) (listView.footer.findViewById(R.id.load_more_text));
                    JSONObject js=new JSONObject();
                    try {
                        js.put("page", page);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String data1 = OkGo.post(Constants.nianfo_home_zhunian_Get_Ip)
                            .params("key", ApisSeUtil.getKey())
                            .params("msg",ApisSeUtil.getMsg(js)).tag(TAG).execute().body().string();
                    if (!data1.equals("")) {
                        list = AnalyticalJSON.getList_zj(data1);
                        if (list != null) {
                            if (list.size() < 10) {
                                endPage = page;
                            }
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
//
                                    if (adapter.list.size() == 0) {
                                        adapter.addList(list);
                                        listView.setAdapter(adapter);
                                    } else {
                                        if (isRefresh) {
                                            isRefresh = false;
                                            adapter.list.clear();
                                            adapter.addList(list);
                                            adapter.notifyDataSetChanged();

                                            if (t.getText().toString().equals(mApplication.ST("没有更多数据了"))) {
                                                listView.onLoadComplete();
                                                t.setText(mApplication.ST("正在加载...."));
                                            }
                                        } else {
                                            adapter.list.addAll(list);
                                            for(HashMap map:list){
                                                adapter.sba.add(true);
                                            }
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

                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    endPage = page;
                                    p.setVisibility(View.GONE);
                                    t.setText(mApplication.ST("没有更多数据了"));
                                    listView.onLoadComplete();
                                    if (swip.isRefreshing()) swip.setRefreshing(false);

                                }
                            });

                        }

                    }
                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (swip.isRefreshing()) swip.setRefreshing(false);

                            ProgressUtil.dismiss();
                        }
                    });
                    e.printStackTrace();
                }
            }
        }).start();

    }
    @Override
    protected void onPause() {
        super.onPause();
        ProgressUtil.dismiss();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {

            case R.id.Share:
                UMWeb umWeb=new UMWeb("http://a.app.qq.com/o/simple.jsp?pkgname=com.ytl.qianyishenghao");
                umWeb.setTitle("千亿圣号App");
                umWeb.setDescription("快来千亿圣号共修吧");
                umWeb.setThumb(new UMImage(this,R.drawable.indra));
                new ShareManager().shareWeb(umWeb,this);
                break;
            case R.id.nianfo_chanhui_back:
                finish();
                break;
            case R.id.nianfo_home_chanhui_commit:
                if (!new LoginUtil().checkLogin(this)) {
                    intent = new Intent(this, Login.class);
                    startActivity(intent);
                    Toast.makeText(mApplication.getInstance(), mApplication.ST("请先登录"), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Network.HttpTest(getApplicationContext())) {
                    Toast.makeText(mApplication.getInstance(), mApplication.ST("请检查网络连接"), Toast.LENGTH_SHORT).show();
                    return;
                }
                if(sp.getString("pet_name","").trim().equals("")){
                    Toast.makeText(this, mApplication.ST("请完善信息"), Toast.LENGTH_SHORT).show();
                    Intent intent1=new Intent(this,Mine_gerenziliao.class);
                    startActivity(intent1);
                    return;
                }
                if (editText.getText().toString().trim().equals("")) {
                    Toast.makeText(mApplication.getInstance(),mApplication.ST("请将信息填写完整") , Toast.LENGTH_SHORT).show();
                    return;
                }
                textView.setEnabled(false);
                ProgressUtil.show(nianfo_home_tab4.this,"",mApplication.ST("正在提交"));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject js=new JSONObject();
                            try {
                                js.put("user_id", sp.getString("user_id", ""));
                                js.put("contents", editText.getText().toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            String data1 = OkGo.post(Constants.nianfo_home_zhunian_Commit_Ip).tag(TAG)
                                    .params("key",ApisSeUtil.getKey())
                                    .params("msg", ApisSeUtil.getMsg(js))
                                   .execute().body().string();
                            if (!data1.equals("")) {
                                Log.w(TAG, "run: data1-=-=" + data1);
                                final HashMap<String, String> map1 = AnalyticalJSON.getHashMap(data1);
                                if (map1 != null && map1.get("code").equals("000")) {
                                    HashMap<String, String> map = new HashMap<>();
                                    map.put("user_image", sp.getString("head_url", ""));
                                    map.put("pet_name", sp.getString("pet_name", ""));
                                    map.put("rtg_time", TimeUtils.getStrTime(System.currentTimeMillis() + ""));
                                    map.put("rtg_contents", editText.getText().toString());
                                    map.put("id", map1.get("rtg_id"));
                                    map.put("rtg_likes", "0");
                                    map.put("user_id", PreferenceUtil.getUserIncetance(nianfo_home_tab4.this).getString("user_id",""));
                                    map.put("rtg_userid", PreferenceUtil.getUserIncetance(nianfo_home_tab4.this).getString("user_id",""));
                                    adapter.list.add(0, map);
                                    adapter.addList(adapter.list);
                                    adapter.sba.add(0,true);
                                    if (handler != null) {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                ProgressUtil.dismiss();
                                                listView.setAdapter(adapter);
                                                editText.setText("");
                                                listView.setSelection(0);
                                                textView.setEnabled(true);
                                                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                                            }
                                        });
                                    }
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(mApplication.getInstance(), mApplication.ST("信息提交失败，请重新尝试"), Toast.LENGTH_SHORT).show();
                                            ProgressUtil.dismiss();
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

                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(TAG);
        mApplication.romoveActivity(this);
    }

    @Override
    public void onRefresh() {
        page = "1";
        isRefresh = true;
        endPage = "";
        initData();
    }

    @Override
    public void loadMore() {

        if (!endPage.equals(page)) {
            page = String.valueOf(Integer.parseInt(page) + 1);
        } else {
            final ProgressBar p = (ProgressBar) (listView.footer.findViewById(R.id.load_more_bar));
            final TextView t = (TextView) (listView.footer.findViewById(R.id.load_more_text));
            p.setVisibility(View.GONE);
            t.setText(mApplication.ST("没有更多数据了"));
            return;
        }
        initData();
    }
}
