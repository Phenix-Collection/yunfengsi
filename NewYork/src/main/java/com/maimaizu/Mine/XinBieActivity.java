package com.maimaizu.Mine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.maimaizu.R;
import com.maimaizu.Utils.AnalyticalJSON;
import com.maimaizu.Utils.Constants;
import com.maimaizu.Utils.StatusBarCompat;
import com.maimaizu.Utils.mApplication;

import java.io.IOException;
import java.util.HashMap;

public class XinBieActivity extends AppCompatActivity implements View.OnClickListener{
private RadioGroup mrgroup;
 private String xinbie;
    private Intent intent;
    private SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, ContextCompat.getColor(this, R.color.main_color));

        setContentView(R.layout.activity_xin_bie);
        sp=getSharedPreferences("user",MODE_PRIVATE);
        intent=getIntent();
        mrgroup=(RadioGroup) findViewById(R.id.radioGroup);
        mrgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                //获取变更后的选中项的ID
                int radioButtonId = radioGroup.getCheckedRadioButtonId();
                //根据ID获取RadioButton的实例
                RadioButton rb = (RadioButton)XinBieActivity.this.findViewById(radioButtonId);
                //更新文本内容，以符合选中项
               xinbie=rb.getText().toString();
            }
        });
        ((TextView) findViewById(R.id.title)).setText(mApplication.ST("性別"));
        ((TextView) findViewById(R.id.xinbie_baochun)).setText(mApplication.ST("保存"));
        ((RadioButton) findViewById(R.id.radioFemale)).setText(mApplication.ST("女"));
        ((RadioButton) findViewById(R.id.radioMale)).setText(mApplication.ST("男"));
        if(sp.getString("sex","1").equals("1")){
            ((RadioButton) findViewById(R.id.radioMale)).setChecked(true);
        }else{
            ((RadioButton) findViewById(R.id.radioFemale)).setChecked(true);
        }
    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
        switch (id){
            case R.id.xinbie_back:
                finish();
                break;
            case R.id.xinbie_baochun:      //保存
                if(xinbie==null){
                    Toast.makeText(XinBieActivity.this, mApplication.ST("请选择性别"), Toast.LENGTH_SHORT).show();
                }else {
                    modification();  //数据交互修改性别的方法
              }
                break;
        }
    }
    public void modification(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String data = OkGo.post(Constants.User_info_xiugaixb)

                            .params("user_id", sp.getString("user_id",""))
                            .params("sex",  xinbie.equals(mApplication.ST("男")) ? "1" : "2")
                            .params("key", Constants.safeKey)
                            .execute().body().string();
                    HashMap<String,String> retur= AnalyticalJSON.getHashMap(data);
                    if (retur.get("code").equals("000")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Toast.makeText(XinBieActivity.this, mApplication.ST("性别修改成功"),Toast.LENGTH_SHORT).show();
                                intent.putExtra("xinbie", xinbie);
                                XinBieActivity.this.setResult(1, intent);
                                 finish();
                            }
                        });
                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Toast.makeText(XinBieActivity.this, mApplication.ST("性别修改失败"),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
