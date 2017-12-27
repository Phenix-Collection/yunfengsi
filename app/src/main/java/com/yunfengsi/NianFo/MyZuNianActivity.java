package com.yunfengsi.NianFo;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.yunfengsi.R;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.ProgressUtil;
import com.yunfengsi.Utils.ShareManager;
import com.yunfengsi.Utils.StatusBarCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyZuNianActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Recitingd";
    private RecyclerView mrecyclerview;
    private ListView mlistview;
    private TextView mfohao;
    private TextView mcontents; //助念的内容
    private TextView mpepole; //为他助念的人数
    private TextView mfohaonum;//助念的佛号声
    private TextView mtitle; //标题
    private MyZuNian_ListView_Adapter listviewadapter;
    private MyZuNian_RecyclerView_Adapter mrecycleradpter;
    private int screenwidth;
    private List<Integer> headid = new ArrayList<>();
    private HashMap<String, String> hashMap1 = new HashMap<>();
    private ArrayList<HashMap<String, String>> headlist = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> recitlist = new ArrayList<HashMap<String, String>>();
    private UMWeb umWeb;

    public void init() {
        mtitle = (TextView) findViewById(R.id.activity_my_zunian_title_tv);
        mcontents = (TextView) findViewById(R.id.activity_my_zunian_tv_zhuniancontents);
        mpepole = (TextView) findViewById(R.id.activity_my_zunian_tv_pepole);
        mfohaonum = (TextView) findViewById(R.id.activity_my_zunian_tv_fohaonum);
        mrecyclerview = (RecyclerView) findViewById(R.id.activity_my_zunian_recyclerview);
        mlistview = (ListView) findViewById(R.id.activity_my_zunian_listview);
        mlistview.setHeaderDividersEnabled(false);
        mfohao = (TextView) findViewById(R.id.activity_my_zunian_tv_fohao);
        listviewadapter = new MyZuNian_ListView_Adapter(this, recitlist);
        mrecycleradpter = new MyZuNian_RecyclerView_Adapter(this, headlist);
        screenwidth = getBaseContext().getResources().getDisplayMetrics().widthPixels;


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.activity_my_zu_nian);
        init();
        mlistview.setAdapter(listviewadapter);
        BigDecimal bd = new BigDecimal((screenwidth - (11 * DimenUtils.dip2px(this, 5))) / DimenUtils.dip2px(this, 40));
        bd.setScale(1, BigDecimal.ROUND_HALF_UP);
        int i = 0;
        if (bd.toString().charAt(bd.toString().length() - 1) >= 5) {
            i = bd.intValue() + 1;
        } else {
            i = bd.intValue();
        }
        mrecyclerview.setLayoutManager(new GridLayoutManager(MyZuNianActivity.this, i));
        mrecyclerview.setAdapter(mrecycleradpter);
        mrecyclerview.addItemDecoration(new SpaceItemDecoration());

        getdata();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ProgressUtil.dismiss();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.activity_my_zunian_back:
                finish();
                break;
            case R.id.activity_my_zunian_share_bnt:
                if(umWeb!=null){
                    new  ShareManager().shareWeb(umWeb,this);
                }

                break;

        }
    }

    class SpaceItemDecoration extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            //每个Itme之间的距离
            outRect.bottom = 7;
            outRect.top = 2;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(TAG);
        UMShareAPI.get(this).release();
    }

    public void getdata() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject js=new JSONObject();
                    try {
                        js.put("reciting_id", getIntent().getStringExtra("id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String data = OkGo.post(Constants.MyZuNian_IP).tag(TAG).params("key", ApisSeUtil.getKey())
                            .params("msg", ApisSeUtil.getMsg(js)).execute().body().string();
                    hashMap1 = AnalyticalJSON.getHashMap(data);
                    if (hashMap1 != null && (!hashMap1.get("reciting").equals("null"))) {
                        final String fohaonum = hashMap1.get("num");
                        if (hashMap1.get("reciting") != null) {
                            final String zhuniannum = AnalyticalJSON.getHashMap(hashMap1.get("reciting")).get("rtg_likes");
                            final String zhuniancontents = AnalyticalJSON.getHashMap(hashMap1.get("reciting")).get("rtg_contents");
                            final String zhuniantitle = AnalyticalJSON.getHashMap(hashMap1.get("reciting")).get("pet_name");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mtitle.setText(zhuniantitle + "的助念明细");
                                    mcontents.setText("        "+zhuniancontents);
                                    mpepole.append(":"+zhuniannum + "人");
                                    if (fohaonum.equals("null")) {
                                        mfohaonum.setText("助念佛号:0遍");
                                    } else {
                                        mfohaonum.setText("助念佛号:"+fohaonum + "遍");
                                    }
                                    umWeb=new UMWeb(Constants.FX_host_Ip + TAG + "/reciting_id/" + getIntent().getStringExtra("id"));
                                    umWeb.setThumb(new UMImage(MyZuNianActivity.this,R.drawable.indra));
                                    umWeb.setTitle(mtitle.getText().toString());
                                    umWeb.setDescription(zhuniancontents);

                                }
                            });
                        }
                    }
                    if (hashMap1 != null && hashMap1.get("head") != null) {
                        final ArrayList<HashMap<String, String>> head = AnalyticalJSON.getList_zj(hashMap1.get("head"));
                        Log.w(TAG, "run: " + hashMap1.get("head"));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                headlist.addAll(head);
                                mrecycleradpter.notifyDataSetChanged();
                            }
                        });

                        if (hashMap1.get("recitlist") != null && hashMap1.get("recitlist") != null) {
                            final ArrayList<HashMap<String, String>> recit = AnalyticalJSON.getList_zj(hashMap1.get("recitlist"));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (recit.size() == 0) {
                                        TextView textView = new TextView(getApplicationContext());
                                        textView.setText("暂无人助念，快为他助念吧");
                                        textView.setTextSize(15);
                                        textView.setTextColor(Color.BLACK);
                                        mlistview.addHeaderView(textView);
                                        listviewadapter.notifyDataSetChanged();
                                    } else {
                                        recitlist.addAll(recit);
                                        listviewadapter.notifyDataSetChanged();
                                    }

                                }
                            });
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }catch (IllegalStateException e){

                }
            }
        }).start();
    }
}
