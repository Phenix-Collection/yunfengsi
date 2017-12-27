package com.qianfujiaoyu.Activitys;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.request.BaseRequest;
import com.qianfujiaoyu.R;
import com.qianfujiaoyu.Utils.AnalyticalJSON;
import com.qianfujiaoyu.Utils.ApisSeUtil;
import com.qianfujiaoyu.Utils.Constants;
import com.qianfujiaoyu.Utils.DimenUtils;
import com.qianfujiaoyu.Utils.LogUtil;
import com.qianfujiaoyu.Utils.ProgressUtil;
import com.qianfujiaoyu.Utils.StatusBarCompat;
import com.qianfujiaoyu.Utils.ToastUtil;
import com.qianfujiaoyu.Utils.mApplication;
import com.qianfujiaoyu.View.ShiPuDecoration;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 作者：因陀罗网 on 2017/7/19 15:58
 * 公司：成都因陀罗网络科技有限公司
 */

public class ShiPu extends AppCompatActivity implements View.OnClickListener,SwipeRefreshLayout.OnRefreshListener {
    private ImageView back;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swip;
    private ShiPuAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, ContextCompat.getColor(this, R.color.main_color));
        setContentView(R.layout.activity_shipu);
        swip= (SwipeRefreshLayout) findViewById(R.id.swip);
        swip.setColorSchemeResources(R.color.main_color);
        swip.setOnRefreshListener(this);

        recyclerView= (RecyclerView) findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new GridLayoutManager(this,4));
        recyclerView.addItemDecoration(new ShiPuDecoration(this,1,R.color.main_color));

        adapter=new ShiPuAdapter(this,new ArrayList<HashMap<String, String>>());
        adapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        View head= LayoutInflater.from(this).inflate(R.layout.headview_shipu,null);
        adapter.addHeaderView(head);
        TextView textView = new TextView(this);
        Drawable d = ContextCompat.getDrawable(this, R.drawable.load_nothing);
        d.setBounds(0, 0, DimenUtils.dip2px(this, 150), DimenUtils.dip2px(this, 150) * d.getIntrinsicHeight() / d.getIntrinsicWidth());
        textView.setCompoundDrawables(null, d, null, null);
        textView.setCompoundDrawablePadding(DimenUtils.dip2px(this, 10));
        textView.setText("暂未设置食谱");
        textView.setGravity(Gravity.CENTER);
        ViewGroup.MarginLayoutParams vl = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        vl.topMargin = DimenUtils.dip2px(this, 180);
        textView.setLayoutParams(vl);
        adapter.setEmptyView(textView);
        recyclerView.setAdapter(adapter);
        adapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {

                if(!adapter.getData().get(i).get("id").equals("")&&!adapter.getData().get(i).get("text").equals("")){

                    JSONObject js=new JSONObject();
                    try {
                        js.put("id",adapter.getData().get(i).get("id"));
                        js.put("m_id",Constants.M_id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m=ApisSeUtil.i(js);
                    final String tag=adapter.getData().get(i).get("tag");
                    OkGo.post(Constants.Shipu_Detail)
                            .tag(this)
                            .params("key",m.K())
                            .params("msg",m.M())
                            .execute(new AbsCallback<String>() {
                                @Override
                                public void onSuccess(String stringStringHashMap, Call call, Response response) {
                                    if(!TextUtils.isEmpty(stringStringHashMap)){
                                        View view = LayoutInflater.from(ShiPu.this).inflate(R.layout.activity_confirm_dialog, null);
                                        final WebView web = (WebView) view.findViewById(R.id.web);
                                        TextView cancle = (TextView) view.findViewById(R.id.cancle);
                                        web.loadDataWithBaseURL("", stringStringHashMap
                                                , "text/html", "UTF-8", null);
                                        AlertDialog.Builder builder = new AlertDialog.Builder(ShiPu.this);
                                        builder.setView(view);
                                        final AlertDialog dialog = builder.create();
                                        cancle.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                web.removeAllViews();
                                                web.destroy();
                                                dialog.dismiss();
                                            }
                                        });
                                        web.setWebViewClient(new WebViewClient(){
                                            @Override
                                            public void onPageFinished(WebView view, String url) {
                                                super.onPageFinished(view, url);
                                                dialog.show();
                                                ProgressUtil.dismiss();
                                            }
                                        });

                                    }else{
                                        ToastUtil.showToastShort("暂无详情数据");
                                        ProgressUtil.dismiss();
                                    }
                                }

                                @Override
                                public String convertSuccess(Response response) throws Exception {
                                    HashMap<String,String> map=AnalyticalJSON.getHashMap(response.body().string());
                                    if(map!=null){
                                        switch (tag){
                                            case  "am":
                                                return map.get("amd");
                                            case "noon":
                                                return map.get("noond");
                                            case "pm":
                                                return map.get("pmd");
                                        }
                                    }

                                    return "";
                                }

                                @Override
                                public void onBefore(BaseRequest request) {
                                    super.onBefore(request);
                                    ProgressUtil.show(ShiPu.this,"","正在获取详情");
                                }

                                @Override
                                public void onAfter(String stringStringHashMap, Exception e) {
                                    super.onAfter(stringStringHashMap, e);

                                }
                            });
                }

            }
        });
        swip.post(new Runnable() {
            @Override
            public void run() {
                swip.setRefreshing(false);
                onRefresh();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(this);
    }

    private void getData() {
        JSONObject js=new JSONObject();
        try {
            js.put("m_id",Constants.M_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m=ApisSeUtil.i(js);
        OkGo.post(Constants.Shipu).tag(this).params("key",m.K())
                .params("msg",m.M())
                .execute(new AbsCallback<ArrayList<HashMap<String,String>>>() {
                    @Override
                    public ArrayList<HashMap<String,String>> convertSuccess(Response response) throws Exception {
                        LogUtil.e("正在解析数据");
                        ArrayList<HashMap<String,String>> list= AnalyticalJSON.getList_zj(response.body().string());
                        if(list==null||list.size()==0){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.showToastShort("暂未设置食谱");
                                    ShiPu.this.finish();
                                }
                            });
                            return null;
                        }

                        ArrayList<HashMap<String,String>> l=new ArrayList<HashMap<String, String>>();
                        for(HashMap<String,String> m:list){
                            HashMap<String,String> map=new HashMap<String, String>();
                            map.put("text",m.get("time"));
                            map.put("id","");
                            l.add(map);
                            HashMap<String,String> map1=new HashMap<String, String>();
                            map1.put("text",m.get("am"));
                            map1.put("id",m.get("id"));
                            map1.put("tag","am");
                            l.add(map1);
                            HashMap<String,String> map2=new HashMap<String, String>();
                            map2.put("text",m.get("noon"));
                            map2.put("id",m.get("id"));
                            map2.put("tag","noon");
                            l.add(map2);
                            HashMap<String,String> map3=new HashMap<String, String>();
                            map3.put("text",m.get("pm"));
                            map3.put("id",m.get("id"));
                            map3.put("tag","pm");
                            l.add(map3);
                        }

                        return l;
                    }

                    @Override
                    public void onSuccess(ArrayList<HashMap<String,String>> list, Call call, Response response) {

                            adapter.setNewData(list);
                            swip.setRefreshing(false);

                    }


                });
    }

    @Override
    public void onRefresh() {

        getData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.refresh:
                swip.post(new Runnable() {
                    @Override
                    public void run() {
                        swip.setRefreshing(true);
                        onRefresh();
                    }
                });
                break;
        }
    }

    private static  class ShiPuAdapter extends BaseQuickAdapter<HashMap<String,String>> {
        private Activity ac;
        ViewGroup.LayoutParams vp;
        public ShiPuAdapter(Activity a, List<HashMap<String, String>> data) {
            super(R.layout.item_shipu,data);
            WeakReference<Activity> w=new WeakReference<Activity>(a);
            ac=w.get();
           vp =new ViewGroup.LayoutParams((ac.getResources().getDisplayMetrics()
                    .widthPixels-DimenUtils.dip2px(ac,20))/4,(ac.getResources().getDisplayMetrics()
                    .heightPixels-DimenUtils.dip2px(ac,120))/7);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, HashMap<String, String> stringStringHashMap) {
            TextView text=baseViewHolder.getView(R.id.shipu_text);
            if(baseViewHolder.getView(R.id.shipu_text).getLayoutParams()!=vp){
                baseViewHolder.getView(R.id.shipu_text).setLayoutParams(vp);
            }
            baseViewHolder.setText(R.id.shipu_text,stringStringHashMap.get("text"));

            if(!stringStringHashMap.get("id").equals("")&&!stringStringHashMap.get("text").equals("")){
                text.setBackgroundResource(R.color.colorPrimaryDark);
                text.setTextColor(ActivityCompat.getColor(ac,R.color.black));
                text.setTextSize(14);
            }else{
                text.setBackgroundResource(R.color.white);
                text.setTextColor(ActivityCompat.getColor(ac,R.color.main_color));
                text.setTextSize(18);
            }
        }
    }
}
