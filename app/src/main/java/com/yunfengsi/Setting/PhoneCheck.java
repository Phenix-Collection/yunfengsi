package com.yunfengsi.Setting;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.yunfengsi.R;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.Network;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.ToastUtil;
import com.yunfengsi.Utils.Verification;
import com.yunfengsi.Utils.mApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/9/13.
 */
public class PhoneCheck extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "PhoneCheck";
    private TextView getMid;
    private EditText phone, MID, newPhone;
    private String            YZM;
    private static final int intervalTime = 60000;
//    private boolean checked = false;//是否验证成功

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.phone_check);
        initView();
    }

    private void initView() {
        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        ((TextView) findViewById(R.id.title_title)).setText(mApplication.ST("更换手机号"));
        ImageView back = findViewById(R.id.title_back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        getMid = findViewById(R.id.Zhuce_getMid);
        TextView commit = findViewById(R.id.zhuce_commit);
        phone = findViewById(R.id.oldPhone);
        newPhone = findViewById(R.id.NewPhone);
        MID = findViewById(R.id.Zhuce_Mid);
        getMid.setOnClickListener(this);
        commit.setOnClickListener(this);
    }

    CountDownTimer timer = new CountDownTimer(intervalTime, 1000) {//验证码倒计时
        @Override
        public void onTick(long millisUntilFinished) {
            getMid.setText(mApplication.ST(millisUntilFinished / 1000 + "秒后可重新获取"));
            getMid.setTextColor(Color.parseColor("#9b9b9b"));
            getMid.setEnabled(false);
        }

        @Override
        public void onFinish() {
            if (getMid != null) {
                getMid.setText(mApplication.ST("请重新发送"));
                getMid.setTextColor(Color.parseColor("#000000"));
                getMid.setEnabled(true);
                phone.setFocusable(true);
            }
        }
    };

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.zhuce_commit:
                if ( "".equals(MID.getText().toString())) {
                    Toast.makeText(mApplication.getInstance(), mApplication.ST("请输入验证码"), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Verification.isMobileNO(phone.getText().toString())) {
                    Toast.makeText(mApplication.getInstance(), mApplication.ST("请输入正确的手机号码"), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (newPhone.getText().toString().equals("")) {
                    Toast.makeText(this, mApplication.ST("请输入新手机号码"), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (YZM == null || !YZM.equals(MID.getText().toString())) {
                    Toast.makeText(mApplication.getInstance(), mApplication.ST("验证码不匹配"), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Verification.isMobileNO(newPhone.getText().toString())) {
                    Toast.makeText(this, mApplication.ST("请输入正确的新手机号码"), Toast.LENGTH_SHORT).show();
                    return;
                }

                JSONObject jsonObject=new JSONObject();
                try {
                    jsonObject.put("m_id",Constants.M_id);
                    jsonObject.put("oldphone",phone.getText());
                    jsonObject.put("newphone",newPhone.getText());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                LogUtil.e("更换手机号码：："+jsonObject);
                ApisSeUtil.M m=ApisSeUtil.i(jsonObject);
                OkGo.post(Constants.ResetPhone)
                        .params("key",m.K())
                        .params("msg",m.M())
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(String s, Call call, Response response) {
                                HashMap<String,String> map=AnalyticalJSON.getHashMap(s);
                                if(map!=null){
                                    switch (map.get("code")){
                                        case "000":
                                            ToastUtil.showToastShort("手机号码绑定成功");
                                            finish();
                                            break;
                                        case "002":
                                            ToastUtil.showToastShort("绑定失败，请稍后重试");
                                            break;
                                        case "003":
                                            ToastUtil.showToastShort("新手机号已被注册");
                                            break;
                                    }
                                }
                            }
                        });

                break;
            case R.id.Zhuce_getMid:
                if (!Network.HttpTest(this)) {
                    return;
                }
                if (phone.getText().toString().equals("")) {
                    Toast.makeText(this, mApplication.ST("请输入原手机号码"), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!Verification.isMobileNO(phone.getText().toString())) {
                    Toast.makeText(this, mApplication.ST("请输入正确的原手机号码"), Toast.LENGTH_SHORT).show();
                    return;
                }


                //获取验证码
                v.setEnabled(false);
                getMid.setText(mApplication.ST("正在请求验证码"));

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("m_id", Constants.M_id);
                            jsonObject.put("phone", phone.getText().toString());
                            jsonObject.put("region", "86");
                            LogUtil.e("发送验证码：：" + jsonObject);
                            ApisSeUtil.M m = ApisSeUtil.i(jsonObject);
                            String data = OkGo.post(Constants.Mid_IP)
                                    .params("key", m.K())
                                    .params("msg", m.M())
                                    .execute().body().string();

                            if (!data.equals("")) {
                                HashMap<String, String> map = AnalyticalJSON.getHashMap(data);
                                if (map != null) {
                                    switch (map.get("code")) {
                                        case "000":
                                            timer.start();
                                            YZM = map.get("yzm");
                                            phone.setFocusable(false);
                                            getMid.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    YZM = "";
                                                    phone.setFocusable(true);
                                                    v.setEnabled(true);
                                                }
                                            }, intervalTime);
                                            break;
                                        case "222":
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ToastUtil.showToastShort(mApplication.ST("验证码请求过于频繁"), Gravity.CENTER);
                                                    v.setEnabled(true);
                                                    getMid.setText(mApplication.ST("获取验证码"));
                                                    phone.setFocusable(true);
                                                }
                                            });

                                            break;
                                        case "333":
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    v.setEnabled(true);
                                                    getMid.setText(mApplication.ST("获取验证码"));
                                                    getMid.setTextColor(Color.BLACK);
                                                    phone.setFocusable(true);
                                                    ToastUtil.showToastShort("验证码请求超过上限", Gravity.CENTER);
                                                }
                                            });

                                            break;
                                    }
                                }

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
        }
    }
}
