package com.yunfengsi.Setting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.yunfengsi.R;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.SystemUtil;
import com.yunfengsi.Utils.mApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

public class AnquanActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView mtvnc;
    private TextView mxinbie;
    private TextView msimiao;
    private SharedPreferences sp;

    private HashMap<String, String> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anquan);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        sp = getSharedPreferences("user", MODE_PRIVATE);
        mtvnc = findViewById(R.id.anquan_nichen_tv);
        mxinbie = findViewById(R.id.anquan_xinbie_tv);
        TextView sign = findViewById(R.id.sign1);
//        msimiao = (TextView) findViewById(R.id.anquan_simiao_tv);
        TextView loginState = findViewById(R.id.zhanghaoyuanquan_loginState);
        getdatafromserve();
        if(!TextUtils.isEmpty(sp.getString("user_id",""))){
            loginState.setText(mApplication.ST("账号已登录，无安全风险"));
        }else{
            loginState.setText(mApplication.ST("账号未登录"));
        }
        ((TextView) findViewById(R.id.title)).setText(mApplication.ST("账号安全"));
        ((TextView) findViewById(R.id.petname)).setText(mApplication.ST("昵称"));
        ((TextView) findViewById(R.id.sex)).setText(mApplication.ST("性別"));
        ((TextView) findViewById(R.id.mima)).setText(mApplication.ST("密码"));
        ((TextView) findViewById(R.id.now)).setText(mApplication.ST("当前登录状态"));
        ((TextView) findViewById(R.id.sign1)).setText(mApplication.ST("个性签名"));

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.sign:
               Intent intent=new Intent(this,Sign.class);
                startActivity(intent);
                break;
            case R.id.anquan_back:
                finish();
                break;
            case R.id.anquan_nichen_back:
                Intent intentnc = new Intent(AnquanActivity.this, NiCTemple_Activity.class);
                intentnc.putExtra("title", "昵称");
                startActivityForResult(intentnc, 1);
                break;
            case R.id.anquan_xinbie_back:
                Intent intentxb = new Intent(AnquanActivity.this, XinBieActivity.class);
                startActivityForResult(intentxb, 2);
                break;
//            case R.id.anquan_simiao_back:
//                Intent intentsimiao = new Intent(AnquanActivity.this, NiCTemple_Activity.class);
//                intentsimiao.putExtra("title", "所属寺庙");
//                startActivityForResult(intentsimiao, 3);
//                break;
            case R.id.anquan_mima_back:
                Intent inten =new Intent(this,FindPassword.class);
                startActivity(inten);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == 1) {
                String nc = data.getStringExtra("edit");
                if (!nc.equals("")) {
                    mtvnc.setText(nc);
                    SharedPreferences.Editor ed=sp.edit();
                    ed.putString("pet_name", nc);
                    ed.apply();
                }
            } else if (requestCode == 2) {
                String xinbie = data.getStringExtra("xinbie");
                if (!xinbie.equals("")) {
                    mxinbie.setText(xinbie);
                }
            } else if (requestCode == 3) {
                String simiao = data.getStringExtra("edit");
                if (!simiao.equals("")) {
                    msimiao.setText(simiao);
                }
            }
        }
    }

    private static final String TAG = "AnquanActivity";
    public void getdatafromserve() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                String data1 = null;
                try {
                    JSONObject js=new JSONObject();
                    try {
                        js.put("m_id", Constants.M_id);
                        js.put("user_id", sp.getString("user_id", ""));
                        js.put("type","1");
                        js.put("phonename", SystemUtil.getSystemModel());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    LogUtil.e("关于我们：：个人信息：："+js);
                    ApisSeUtil.M m=ApisSeUtil.i(js);
                    data1 = OkGo.post(Constants.User_Info_Ip)
                            .params("key", m.K())
                            .params("msg",m.M()).execute().body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (data1 != null && !data1.equals("")) {
                    Log.d("个人信息数据为：", data1);
                    map = AnalyticalJSON.getHashMap(data1);
                    if (map != null && map.get("code") == null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mtvnc.setText(map.get("pet_name"));
                                if (map.get("sex").equals("1")) {
                                    mxinbie.setText(mApplication.ST("男"));
                                } else if (map.get("sex").equals("2")) {
                                    mxinbie.setText(mApplication.ST("女"));
                                }
                            }
                        });
                    }
                }else if(map!=null&&map.get("code")!=null){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AnquanActivity.this, mApplication.ST("用户信息获取失败"), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }
}
