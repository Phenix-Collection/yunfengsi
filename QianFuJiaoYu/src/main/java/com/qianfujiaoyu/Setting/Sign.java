package com.qianfujiaoyu.Setting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.qianfujiaoyu.R;
import com.qianfujiaoyu.Utils.AnalyticalJSON;
import com.qianfujiaoyu.Utils.ApisSeUtil;
import com.qianfujiaoyu.Utils.Constants;
import com.qianfujiaoyu.Utils.Network;
import com.qianfujiaoyu.Utils.PreferenceUtil;
import com.qianfujiaoyu.Utils.StatusBarCompat;
import com.qianfujiaoyu.Utils.mApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Administrator on 2016/12/28.
 */
public class Sign extends AppCompatActivity implements View.OnClickListener {
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_info:
                if(!Network.HttpTest(this)){
                    Toast.makeText(Sign.this, mApplication.ST("请检查网络"), Toast.LENGTH_SHORT).show();
                    return;
                }
                new  Thread(new Runnable() {
                    @Override
                    public void run() {
                        String data1 = null;
                        try {
                            JSONObject js=new JSONObject();
                            try {
                                js.put("signature", sign.getText().toString().trim());
                                js.put("m_id",Constants.M_id);
                                js.put("user_id", PreferenceUtil.getUserIncetance(mApplication.getInstance()).getString("user_id",""));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ApisSeUtil.M m=ApisSeUtil.i(js);
                            data1 = OkGo.post(Constants.SignChange)
                                    .params("key",m.K())
                                    .params("msg",m.M()).execute().body().string();
                            if (!data1.equals("")) {
                                HashMap<String, String> map = AnalyticalJSON.getHashMap(data1);
                                if (map != null && "000".equals(map.get("code"))) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            SharedPreferences sp = PreferenceUtil.getUserIncetance(getApplicationContext());
                                            SharedPreferences.Editor ed = sp.edit();
                                            ed.putString("sign", sign.getText().toString());
                                            ed.apply();
                                            Intent intent1=new Intent();
                                            intent1.putExtra("sign",sign.getText().toString());
                                            setResult(4,intent1);
                                            finish();
                                            Toast.makeText(Sign.this, mApplication.ST("修改签名成功"), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
                break;
            case R.id.title_back:
                finish();
                break;
        }


    }
    private ImageView back;
    private TextView title;
    private TextView save;
    private EditText sign;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_activity);
        StatusBarCompat.compat(this, ContextCompat.getColor(this, R.color.main_color));
        back = (ImageView) findViewById(R.id.title_back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        back.setImageResource(R.drawable.back);
        title = (TextView) findViewById(R.id.title_title);
        title.setText(mApplication.ST("修改个性签名"));

        save= (TextView) findViewById(R.id.title_info);
        save.setVisibility(View.VISIBLE);
        save.setText(mApplication.ST("保存"));
        save.setOnClickListener(this);

        sign= (EditText) findViewById(R.id.sign_ed);
        sign.setHint(mApplication.ST("请输入您的签名"));
        sign.setText(PreferenceUtil.getUserIncetance(this).getString("signature",""));

    }
}
