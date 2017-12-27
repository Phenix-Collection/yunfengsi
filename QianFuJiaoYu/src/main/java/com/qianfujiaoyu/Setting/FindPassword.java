package com.qianfujiaoyu.Setting;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.qianfujiaoyu.R;
import com.qianfujiaoyu.Utils.AnalyticalJSON;
import com.qianfujiaoyu.Utils.ApisSeUtil;
import com.qianfujiaoyu.Utils.Constants;
import com.qianfujiaoyu.Utils.ProgressUtil;
import com.qianfujiaoyu.Utils.StatusBarCompat;
import com.qianfujiaoyu.Utils.ToastUtil;
import com.qianfujiaoyu.Utils.Verification;
import com.qianfujiaoyu.Utils.mApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/6/21.
 */
public class FindPassword extends AppCompatActivity implements View.OnClickListener {
    private EditText phoneNum, newWord, word2, Mid;
    private TextView getMid;
    private Button button;
    private String YZM;
    /*
    手机号码 邮箱
     */
    private TextView tvPhone, tvEmail;
    private boolean isPhone = true;
    private LinearLayout yanzhenLayout,wordlayout1,wordlayout2;
    private InputMethodManager imm;

    private int time=60000;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {//找回密码
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_wangjimima);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        initView();
    }

    private void initView() {
        ((TextView) findViewById(R.id.login_wangjimima_title)).setText(mApplication.ST("找回密码"));
        phoneNum = (EditText) findViewById(R.id.login_wangjimima_phonenum);
        phoneNum.setHint(mApplication.ST("手机号码"));
        newWord = (EditText) findViewById(R.id.login_wangjimima_password);
        newWord.setHint(mApplication.ST("新密码"));
        word2 = (EditText) findViewById(R.id.login_wangjimima_password2);
        word2.setHint(mApplication.ST("再次输入密码"));
        Mid = (EditText) findViewById(R.id.login_wangjimima_Mid);
        Mid.setHint(mApplication.ST("请输入验证码"));
        getMid = (TextView) findViewById(R.id.login_wangjimima_getMid);
        getMid.setText(mApplication.ST("获取验证码"));
        button = (Button) findViewById(R.id.login_wangjimima_submit);
        button.setText(mApplication.ST("提交信息"));
        imm= (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        findViewById(R.id.findPassWord_back).setOnClickListener(this);
        button.setOnClickListener(this);
        getMid.setOnClickListener(this);
        tvPhone = (TextView) findViewById(R.id.zhuce_phone);
        tvPhone.setText(mApplication.ST("手机号码"));
        tvEmail = (TextView) findViewById(R.id.zhuce_email);
        tvEmail.setText(mApplication.ST("邮箱地址"));
        tvEmail.setOnClickListener(this);
        tvPhone.setOnClickListener(this);
        yanzhenLayout = (LinearLayout) findViewById(R.id.yanzhen_layout);
        wordlayout1 = (LinearLayout) findViewById(R.id.wordLayout);
        wordlayout2 = (LinearLayout) findViewById(R.id.wordLayout2);
        tvPhone.performClick();
    }

    CountDownTimer timer = new CountDownTimer(time, 1000) {//验证码倒计时
        @Override
        public void onTick(long millisUntilFinished) {
            getMid.setText(mApplication.ST((millisUntilFinished/1000) + "秒后可重新获取"));
            getMid.setTextColor(Color.parseColor("#aaaaaa"));
            getMid.setEnabled(false);
        }

        @Override
        public void onFinish() {
            if (getMid != null) {
                getMid.setText(mApplication.ST("请重新发送"));
                getMid.setTextColor(Color.parseColor("#000000"));
                getMid.setEnabled(true);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.zhuce_phone://手机号注册
                v.setSelected(true);
                ((TextView) v).setTextColor(Color.WHITE);
                tvEmail.setTextColor(Color.GRAY);
                tvEmail.setSelected(false);
                yanzhenLayout.setVisibility(View.VISIBLE);
                wordlayout2.setVisibility(View.VISIBLE);
                wordlayout1.setVisibility(View.VISIBLE);
                findViewById(R.id.line).setVisibility(View.VISIBLE);
//                findViewById(R.id.country_code).setVisibility(View.VISIBLE);
                phoneNum.setText("");
                phoneNum.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
                imm.hideSoftInputFromWindow(phoneNum.getWindowToken(),0);
                phoneNum.setInputType(InputType.TYPE_CLASS_NUMBER);
                isPhone = true;
                phoneNum.setHint(mApplication.ST("手机号码"));

                break;
            case R.id.zhuce_email://邮箱注册
                v.setSelected(true);
                ((TextView) v).setTextColor(Color.WHITE);
                tvPhone.setTextColor(Color.GRAY);
                tvPhone.setSelected(false);
                phoneNum.setText("");
                phoneNum.setFilters(new InputFilter[]{new InputFilter.LengthFilter(25)});
                imm.hideSoftInputFromWindow(phoneNum.getWindowToken(),0);
                phoneNum.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                findViewById(R.id.line).setVisibility(View.GONE);
//                findViewById(R.id.country_code).setVisibility(View.GONE);
                yanzhenLayout.setVisibility(View.GONE);
                wordlayout2.setVisibility(View.GONE);
                wordlayout1.setVisibility(View.GONE);
                isPhone = false;
                phoneNum.setHint(mApplication.ST("邮箱地址"));
                break;
            case R.id.login_wangjimima_getMid:

//                if (!Verification.isMobileNO(phoneNum.getText().toString())) {
//                    Toast.makeText(FindPassword.this, mApplication.ST("请输入正确的手机号码"), Toast.LENGTH_SHORT).show();
//                    return;
//                }
                if (phoneNum.getText().toString().equals("")) {
                    Toast.makeText(FindPassword.this, mApplication.ST("请将信息填写完整"), Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!newWord.getText().toString().trim().equals(word2.getText().toString().trim())){
                    Toast.makeText(this, mApplication.ST("两次密码输入不一致，请重新填写"), Toast.LENGTH_SHORT).show();
                    newWord.setText("");
                    word2.setText("");
                    return;
                }
                //获取验证码
                v.setEnabled(false);
                getMid.setText(mApplication.ST("正在请求验证码"));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject js=new JSONObject();
                            try {
                                js.put("phone", phoneNum.getText().toString());
                                js.put("m_id",Constants.M_id);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ApisSeUtil.M m=ApisSeUtil.i(js);
                            String data = OkGo.post(Constants.Mid_IP)
                                    .params("key",m.K())
                                    .params("msg",m.M())
                                   .execute().body().string();
                            if (!data.equals("")) {
                                HashMap<String ,String> map= AnalyticalJSON.getHashMap(data);
                                if(map!=null){
                                    switch (map.get("code")){
                                        case "000":
                                            timer.start();
                                            YZM = map.get("yzm");
                                            getMid.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    YZM="";
                                                    v.setEnabled(true);
                                                }
                                            },time);
                                            break;
                                        case "222":
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ToastUtil.showToastShort(mApplication.ST("验证码请求过于频繁"),Gravity.CENTER);
                                                    v.setEnabled(true);
                                                    getMid.setText(mApplication.ST("获取验证码"));
                                                }
                                            });

                                            break;
                                        case "333":
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    v.setEnabled(true);
                                                    getMid.setText(mApplication.ST("获取验证码"));
                                                    getMid.setTextColor(Color.WHITE);
                                                    ToastUtil.showToastShort("验证码请求超过上限", Gravity.CENTER);
                                                }
                                            });

                                            break;
                                    }
                                }

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            case R.id.login_wangjimima_submit:
                if ("".equals(phoneNum.getText().toString().trim())) {
                    if (isPhone) {
                        Toast.makeText(FindPassword.this, mApplication.ST("请输入手机号码"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(FindPassword.this, mApplication.ST("请输入邮箱地址"), Toast.LENGTH_SHORT).show();
                    }
                    return;
                }
                if (isPhone) {
                    if ("".equals(newWord.getText().toString().trim())) {
                        Toast.makeText(FindPassword.this, mApplication.ST("请输入密码"), Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        if (newWord.getText().length() < 6) {
                            Toast.makeText(FindPassword.this, mApplication.ST("请输入6-16位的密码"), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    if ("".equals(word2.getText().toString().trim())) {
                        Toast.makeText(FindPassword.this, mApplication.ST("请再次输入密码"), Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        if (word2.getText().length() < 6) {
                            Toast.makeText(FindPassword.this, mApplication.ST("请输入6-16位的密码"), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    if (newWord.getText() != null && word2.getText() != null) {
                        if (!newWord.getText().toString().equals(word2.getText().toString())) {
                            Toast.makeText(FindPassword.this, mApplication.ST("两次密码输入不一致，请重新输入"), Toast.LENGTH_SHORT).show();
                            newWord.setText("");
                            word2.setText("");
                            return;
                        }
                    }
                    if (!Verification.isMobileNO(phoneNum.getText().toString())) {
                        Toast.makeText(FindPassword.this, mApplication.ST("请输入正确的手机号码"), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if("".equals(Mid.getText().toString().trim())){
                        Toast.makeText(FindPassword.this, mApplication.ST("请输入验证码"), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (YZM == null || !YZM.equals(Mid.getText().toString().trim())) {
                        Toast.makeText(FindPassword.this, mApplication.ST("验证码不匹配"), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    submitPhone();
                } else {
                    if (!Verification.isEmail(phoneNum.getText().toString())) {
                        Toast.makeText(FindPassword.this, mApplication.ST("请输入正确的邮箱地址"), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    submitEmail();
                }
                ProgressUtil.show(this, "", mApplication.ST("正在提交用户信息"));


                break;
            case R.id.findPassWord_back:
                finish();
                break;
        }
    }
    // TODO: 2017/2/21 提交邮箱地址找回密码
    private void submitEmail() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject js=new JSONObject();
                    try {
                        js.put("email", phoneNum.getText().toString());
                        js.put("m_id",Constants.M_id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m=ApisSeUtil.i(js);
                    String data = OkGo.post(Constants.email_findpsd__IP)
                            .params("key",m.K())
                            .params("msg",m.M()).execute().body().string();
                    if (!data.equals("")) {
                        if (AnalyticalJSON.getHashMap(data) != null && "000".equals(AnalyticalJSON.getHashMap(data).get("code"))) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(FindPassword.this, mApplication.ST("邮件已发送，请去邮箱查看并填写新密码，有效期30分钟"), Toast.LENGTH_LONG).show();
                                    ProgressUtil.dismiss();
                                    finish();

                                }
                            });
                        } else if (AnalyticalJSON.getHashMap(data) != null && "002".equals(AnalyticalJSON.getHashMap(data).get("code"))) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(FindPassword.this, mApplication.ST("该邮箱尚未注册"), Toast.LENGTH_SHORT).show();
                                    ProgressUtil.dismiss();
                                }
                            });

                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(FindPassword.this, mApplication.ST("输入信息有误，请重新尝试"), Toast.LENGTH_SHORT).show();
                                    ProgressUtil.dismiss();
                                }
                            });
                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ProgressUtil.dismiss();
                                Toast.makeText(FindPassword.this, mApplication.ST("密码更改请求失败，请重新尝试"), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // TODO: 2017/2/21 提交手机号码找回密码
    private void submitPhone() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject js=new JSONObject();
                    try {
                        js.put("phone", phoneNum.getText().toString());
                        js.put("password", newWord.getText().toString());
                        js.put("m_id",Constants.M_id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m=ApisSeUtil.i(js);
                    String data = OkGo.post(Constants.WJMM_IP)
                            .params("key",m.K())
                            .params("msg",m.M()).execute().body().string();
                    if (!data.equals("")) {
                        if (AnalyticalJSON.getHashMap(data) != null && "000".equals(AnalyticalJSON.getHashMap(data).get("code"))) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(FindPassword.this, mApplication.ST("密码更改成功"), Toast.LENGTH_SHORT).show();
                                    ProgressUtil.dismiss();
                                    finish();

                                }
                            });
                        } else if (AnalyticalJSON.getHashMap(data) != null && "002".equals(AnalyticalJSON.getHashMap(data).get("code"))) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(FindPassword.this, mApplication.ST("该手机尚未注册"), Toast.LENGTH_SHORT).show();
                                    ProgressUtil.dismiss();
                                }
                            });

                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(FindPassword.this, mApplication.ST( "输入信息有误，请重新尝试"), Toast.LENGTH_SHORT).show();
                                    ProgressUtil.dismiss();
                                }
                            });
                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ProgressUtil.dismiss();
                                Toast.makeText(FindPassword.this, mApplication.ST("密码更改失败，请重新尝试"), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
