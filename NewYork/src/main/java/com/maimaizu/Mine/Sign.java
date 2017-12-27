package com.maimaizu.Mine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.maimaizu.R;
import com.maimaizu.Utils.AnalyticalJSON;
import com.maimaizu.Utils.Constants;
import com.maimaizu.Utils.Network;
import com.maimaizu.Utils.PreferenceUtil;
import com.maimaizu.Utils.mApplication;

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
                            data1 = OkGo.post(Constants.SignChange)
                                    .params("key", Constants.safeKey)
                                    .params("user_id", PreferenceUtil.getUserIncetance(getApplicationContext()).getString("user_id", ""))
                                    .params("signature", sign.getText().toString().trim()).execute().body().string();
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
        sign.setText(PreferenceUtil.getUserIncetance(this).getString("signture",""));

    }
}
