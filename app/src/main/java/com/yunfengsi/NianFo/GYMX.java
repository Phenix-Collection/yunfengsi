package com.yunfengsi.NianFo;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.yunfengsi.Adapter.GK_NF_Adapter;
import com.yunfengsi.R;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.NumUtils;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.mApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class GYMX extends AppCompatActivity implements View.OnClickListener {
    private TextView mtvtitle;
    private TextView mtvdidian;
    private TextView mtvtime;
    private TextView mtvleijione;
    private TextView mtvpintaitwo;
    private TextView mtvleijithree;
    private TextView mtvhead;
    private ListView mlistview;
    private ImageView mimageback;


    private SharedPreferences sp;
    private HashMap<String, String> listhashMap;
    private ArrayList<Integer> arrayList;
    private GK_NF_Adapter adapter;
    private static final String TAG = "GYMX";
    private String type,type_String ;
    private String url ;
    private List<String > keyList;
    private List<String >valueList;
    private RelativeLayout bg_layout;
    private String digit;//单位后缀
    //Hodler hodler;

    public void init() {
//        ((TextView) findViewById(R.id.leiji)).setText(mApplication.ST("累计"));
//        ((TextView) findViewById(R.id.pingtai)).setText(mApplication.ST("平台"));
        keyList=new ArrayList<>();
        valueList=new ArrayList<>();
//        mtvhead = (TextView) findViewById(R.id.activity_lf_tvhead);
        mtvtitle = (TextView) findViewById(R.id.activity_lf_title);

//        mtvdidian = (TextView) findViewById(R.id.activity_lf_didian);
        mtvtime = (TextView) findViewById(R.id.activity_lf_time);
        mtvleijione = (TextView) findViewById(R.id.activity_lf_tvleijione);
        mtvpintaitwo = (TextView) findViewById(R.id.activity_lf_tvpintaitwo);
//        mtvleijithree = (TextView) findViewById(R.id.activity_lf_tvleijithree);
        mlistview = (ListView) findViewById(R.id.activity_lf_list);
        mimageback = (ImageView) findViewById(R.id.activity_lf_imageback);
        bg_layout= (RelativeLayout) findViewById(R.id.activity_lf_layoutzx);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.gongyangmingxi_acticity);
        init();
        type = getIntent().getStringExtra("type");

        sp = getSharedPreferences("user", MODE_PRIVATE);
        getweb();  //从服务器拿到念佛的数据
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String time = simpleDateFormat.format(Calendar.getInstance().getTime());
        mtvtime.setText(time); //今日时间

//        mtvhead.setText(type);
        mtvtitle.setText(mApplication.ST(type));
        mlistview.setAdapter(adapter);
        mimageback.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        finish();
    }

    public void getweb() {

        switch (type) {
            case "念佛":
                url = Constants.Mine_GK_NF;
                Log.w(TAG, "getweb: url"+url );
                type_String="buddha";

                digit="声";
                break;
            case "诵经":
                url = Constants.Mine_GK_SJ;
                Log.w(TAG, "getweb: url"+url );
                type_String="reading";

                digit="部";
                break;
            case "持咒":
                url = Constants.Mine_GK_CZ;
                Log.w(TAG, "getweb: url"+url );
                type_String="japa";

                digit="遍";
                break;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject js=new JSONObject();
                    try {
                        js.put("user_id", sp.getString("user_id", ""));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    final String data = OkGo.post(url).tag(TAG).params("key", ApisSeUtil.getKey())
                            .params("msg", ApisSeUtil.getMsg(js))
                            .execute().body().string();
                    if (!data.equals("")) {
                        listhashMap = AnalyticalJSON.getHashMap(data);
                        if (listhashMap != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    long pingtai,leiji;
                                    if(listhashMap.get("num")==null||listhashMap.get("num").equals("null")){
                                        pingtai=0;
                                    }else{
                                        pingtai=Long.valueOf(listhashMap.get("num"));
                                    }
                                    if(listhashMap.get("usernum")==null||listhashMap.get("usernum").equals("null")){
                                        leiji=0;
                                    }else{
                                        leiji=Long.valueOf(listhashMap.get("usernum"));
                                    }
                                    SpannableString ssp=new SpannableString(mApplication.ST("平台汇总\n"+ NumUtils.getNumStr(String .valueOf(pingtai))+digit));
                                    ssp.setSpan(new AbsoluteSizeSpan(28,true), ssp.length()-NumUtils.getNumStr(String .valueOf(pingtai)).length()-1, ssp.length()-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    ssp.setSpan(new ForegroundColorSpan(Color.parseColor("#777777")), ssp.length()-NumUtils.getNumStr(String .valueOf(pingtai)).length()-1, ssp.length()-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    mtvpintaitwo.setText(ssp);
                                    SpannableString ssj=new SpannableString(mApplication.ST("个人累计\n"+NumUtils.getNumStr(String .valueOf(leiji))+digit));
                                    ssj.setSpan(new AbsoluteSizeSpan(28,true), ssj.length()-String.valueOf(leiji).length()-1, ssj.length()-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    ssj.setSpan(new ForegroundColorSpan(Color.parseColor("#777777")), ssj.length()-String.valueOf(leiji).length()-1, ssj.length()-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    mtvleijione.setText(ssj);

                                    getList4JsonObject4(data,type_String);
                                    adapter = new GK_NF_Adapter(GYMX.this, keyList, type);
                                    adapter.setValueList(valueList);
                                    mlistview.setAdapter(adapter);
                                }
                            });
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }catch ( IllegalStateException e){

                }
            }

        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(TAG);
    }

    //解析{{[{"":""},{"":""}}]}
    public  void getList4JsonObject4(String json, String type) {

        JSONObject js = null;
        try {
            js = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (js == null) {
            return ;
        }
        JSONObject j = null;

        try {
            if(!js.get(type).equals("0")){
                j = (JSONObject) js.get(type);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (j == null) {
            return ;
        }
        Iterator<String> keysIterator = j.keys();
        while (keysIterator.hasNext()) {
            String key = keysIterator.next();
            keyList.add(key);
            JSONArray jsonArray = null;
            try {
                jsonArray = (JSONArray) j.get(key);
                valueList.add(jsonArray.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }
}
