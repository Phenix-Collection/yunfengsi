package com.qianfujiaoyu.Setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.qianfujiaoyu.R;
import com.qianfujiaoyu.Utils.AnalyticalJSON;
import com.qianfujiaoyu.Utils.ApisSeUtil;
import com.qianfujiaoyu.Utils.Constants;
import com.qianfujiaoyu.Utils.ImageUtil;
import com.qianfujiaoyu.Utils.ProgressUtil;
import com.qianfujiaoyu.Utils.StatusBarCompat;
import com.qianfujiaoyu.Utils.mApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class GanyuActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "GanyuActivity";
     private TextView wangzhan,qq,email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ganyu);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        wangzhan= (TextView) findViewById(R.id.guanfangwangzhan);
        qq= (TextView) findViewById(R.id.QQqun);
        email= (TextView) findViewById(R.id.user_email);

        ((TextView) findViewById(R.id.title)).setText(mApplication.ST("关于我们"));
        ((TextView) findViewById(R.id.name)).setText(mApplication.ST(getResources().getString(R.string.app_name)));
        ((TextView) findViewById(R.id.guanwang)).setText(mApplication.ST("官方网站"));
        ((TextView) findViewById(R.id.dianhua)).setText(mApplication.ST("热线电话"));
        ((TextView) findViewById(R.id.email)).setText(mApplication.ST("客服邮箱"));
        ((TextView) findViewById(R.id.banquan)).setText(mApplication.ST("Copyright 2016-2017 成都因陀罗网络科技有限公司 版权所有"));
        ((ImageView) findViewById(R.id.logo)).setImageBitmap(ImageUtil.readBitMap(this,R.drawable.indra));
        getData();
        findViewById(R.id.yonghuxieyi).setOnClickListener(this);
        findViewById(R.id.bangzhu).setOnClickListener(this);
    }

    /**
     * 获取数据
     */
    private void getData() {

        ProgressUtil.show(this,"","正在加载....");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject js=new JSONObject();
                    try {
                        js.put("m_id",Constants.M_id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m=ApisSeUtil.i(js);
                    String data= OkGo.post(Constants.AboutUs_Ip).tag(TAG)
                            .params("key",m.K())
                            .params("msg",m.M())
                            .execute().body().string();
                    if(!data.equals("")&&!data.equals("null")){
                        final HashMap<String ,String >map= AnalyticalJSON.getHashMap(data);
                        Log.w(TAG, "run: "+data );
                        if(map!=null){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    wangzhan.setText(map.get("url").equals("")? mApplication.ST("正在建设中"):map.get("url"));
                                    qq.setText(map.get("tel").equals("")?mApplication.ST("即将开放"):map.get("tel"));
                                    email.setText(map.get("email").equals("")?mApplication.ST("即将开放"):map.get("email"));
                                  ProgressUtil.dismiss();
                                }
                            });
                        }

                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GanyuActivity.this, mApplication.ST("加载失败"), Toast.LENGTH_SHORT).show();
                                ProgressUtil.dismiss();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(GanyuActivity.this, mApplication.ST("加载失败"), Toast.LENGTH_SHORT).show();
                         ProgressUtil.dismiss();
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
        switch (id){
            case R.id.guanyu_back:
                finish();
                break;
            case R.id.yonghuxieyi:
                Intent intent=new Intent(this,BangZhu.class);
                intent.putExtra("type",BangZhu.XIEYI);
                startActivity(intent);
                break;
            case R.id.bangzhu:
                Intent intent2=new Intent(this,BangZhu.class);
                intent2.putExtra("type",BangZhu.BANGZHU);
                startActivity(intent2);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        OkGo.getInstance().cancelTag(TAG);
        super.onDestroy();
    }
}
