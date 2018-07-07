package com.yunfengsi.Models.YunDou;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.ruffian.library.RTextView;
import com.yunfengsi.R;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.Network;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.ToastUtil;
import com.yunfengsi.Utils.mApplication;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

public class YunDouHome extends AppCompatActivity {
    private RTextView num;
    private WebView   webView;
    private boolean isChanged=false;

    public static class YunDouEvent {

    }


    @Subscribe
    public void onYunDouChanged(YunDouEvent yunDouEvent) {
        LogUtil.e("云豆数量改变");
        isChanged=true;
        getNumAndInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mApplication.getInstance().romoveActivity(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mApplication.getInstance().addActivity(this);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.activity_yun_dou_home);

        findViewById(R.id.title_back).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.title_title)).setText(mApplication.ST("我的云豆"));
        findViewById(R.id.title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ((TextView) findViewById(R.id.handle_right)).setText(mApplication.ST("兑换中心"));
        findViewById(R.id.handle_right).setVisibility(View.VISIBLE);
        findViewById(R.id.handle_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(YunDouHome.this, DuiHuan.class));
            }
        });
        findViewById(R.id.paihangbang).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(YunDouHome.this,yundou_paihang.class));
            }
        });

        num = findViewById(R.id.yundouNum);

        webView = findViewById(R.id.info);

        ((TextView) findViewById(R.id.yundouHistory)).setText(mApplication.ST("云豆记录"));
        findViewById(R.id.yundouHistory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(YunDouHome.this, YunDou_History.class));
            }
        });

        getNumAndInfo();
    }

    private void getNumAndInfo() {
        if(!Network.HttpTest(this)){
            return;
        }
        JSONObject js = new JSONObject();
        try {
            js.put("m_id", Constants.M_id);
            js.put("user_id", PreferenceUtil.getUserId(this));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m = ApisSeUtil.i(js);
        LogUtil.e("获取云豆::" + js);
        OkGo.post(Constants.YunDouHome).params("key", m.K())
                .params("msg", m.M())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        HashMap<String, String> map = AnalyticalJSON.getHashMap(s);
                        if (map != null) {
                            if ("000".equals(map.get("code"))) {
                                if(isChanged){
                                    num.setText(map.get("yundou"));
                                    isChanged=false;
                                }else{
                                    num.setText(map.get("yundou"));
                                    // TODO: 2018/4/23 加载网页数据
                                    webView.loadDataWithBaseURL("",mApplication.ST(map.get("yd_set")),"text/html","UTF-8",null);
                                }

                            }
                        } else {
                            ToastUtil.showToastShort(mApplication.ST("获取云豆失败"));
                        }
                    }
                });

    }
}
