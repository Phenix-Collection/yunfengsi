package com.yunfengsi.Setting;

import android.content.Intent;
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
import com.yunfengsi.R;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.Network;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.ProgressUtil;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.ToastUtil;
import com.yunfengsi.Utils.Verification;
import com.yunfengsi.Utils.mApplication;

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
    private String YZM;
    private String code="86";
    /*
    手机号码 邮箱
     */
    private TextView tvPhone, tvEmail;
    private boolean isPhone = true;
    private LinearLayout yanzhenLayout,wordlayout1,wordlayout2;
    private InputMethodManager imm;

    private int time=60000;
//    private BroadcastReceiver smsReceiver =new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
//            for (Object pdu : pdus) {
//                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
//                String sender = smsMessage.getDisplayOriginatingAddress();
//                String content = smsMessage.getMessageBody();
//                long date = smsMessage.getTimestampMillis();
//                Date timeDate = new Date(date);
//                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                String time = simpleDateFormat.format(timeDate);
//
//               LogUtil.e("onReceive: 短信来自:" + sender);
//               LogUtil.e( "onReceive: 短信内容:" + content);
//               LogUtil.e( "onReceive: 短信时间:" + time);
//
//                //如果短信号码来自自己的短信网关号码
//                String code="";
//                code=content.substring(content.indexOf("验证码为"),content.indexOf("验证码为")+6);
//                LogUtil.e("验证码：："+code);
//                if(Mid!=null){
//                    Mid.setText(code);
//                }
//            }
//        }
//    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {//找回密码
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_wangjimima);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        initView();
    }

    private void initView() {
        findViewById(R.id.bangzhu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(FindPassword.this,GanyuActivity.class);
                startActivity(intent);
            }
        });
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
        Button button = (Button) findViewById(R.id.login_wangjimima_submit);
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
        TextView t= (TextView) findViewById(R.id.country_code);
        if(PreferenceUtil.getSettingIncetance(this).getString("country","").equals("")){
            t.setText(mApplication.ST("国家/地区: 中国大陆  +86"));
        }else{
            code=PreferenceUtil.getSettingIncetance(this).getString("code","");
            t.setText(mApplication.ST("国家/地区: "+PreferenceUtil.getSettingIncetance(this).getString("country","")+"   +"+code));
        }
//        registerReceiver(smsReceiver,new IntentFilter(Constants.SMS_RECEIVED_ACTION));
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
//        unregisterReceiver(smsReceiver);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==111){
            code=data.getStringExtra("code");
            code=data.getStringExtra("code");
            String country=data.getStringExtra("country");
            ((TextView) findViewById(R.id.country_code)).setText(country+"   +"+code);
            PreferenceUtil.getSettingIncetance(this).edit().putString("country",country)
                    .putString("code",code).apply();
        }
    }
    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.country_code:
                Intent intent =new Intent(FindPassword.this, CountryCode.class);
                startActivityForResult(intent,000);
                break;
            case R.id.zhuce_phone://手机号注册

                v.setSelected(true);
                ((TextView) v).setTextColor(Color.WHITE);
                tvEmail.setTextColor(Color.GRAY);
                tvEmail.setSelected(false);
                yanzhenLayout.setVisibility(View.VISIBLE);
                wordlayout2.setVisibility(View.VISIBLE);
                wordlayout1.setVisibility(View.VISIBLE);
                findViewById(R.id.line).setVisibility(View.VISIBLE);
                findViewById(R.id.country_code).setVisibility(View.VISIBLE);
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
                findViewById(R.id.country_code).setVisibility(View.GONE);
                yanzhenLayout.setVisibility(View.GONE);
                wordlayout2.setVisibility(View.GONE);
                wordlayout1.setVisibility(View.GONE);
                isPhone = false;
                phoneNum.setHint(mApplication.ST("邮箱地址"));
                break;
            case R.id.login_wangjimima_getMid:
                if(!Network.HttpTest(FindPassword.this)){
                    return;
                }
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
                                js.put("m_id", Constants.M_id);
                                js.put("region",code);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            LogUtil.e("发送验证码：："+js);
                            ApisSeUtil.M m=ApisSeUtil.i(js);
                            String data = OkGo.post(Constants.Mid_IP)
                                    .params("key", m.K())
                                    .params("msg", m.M())

                                    .execute().body().string();
                            if (!data.equals("")) {
                                HashMap<String ,String> map=AnalyticalJSON.getHashMap(data);
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
                if(!Network.HttpTest(FindPassword.this)){
                    return;
                }
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
//                    if (!Verification.isMobileNO(phoneNum.getText().toString())) {
//                        Toast.makeText(FindPassword.this, mApplication.ST("请输入正确的手机号码"), Toast.LENGTH_SHORT).show();
//                        return;
//                    }
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
                        js.put("m_id", Constants.M_id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String data = OkGo.post(Constants.email_findpsd__IP)
                           .params("key", ApisSeUtil.getKey())
                            .params("msg",ApisSeUtil.getMsg(js)).execute().body().string();
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
                        js.put("m_id", Constants.M_id);
                        js.put("password", newWord.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String data = OkGo.post(Constants.WJMM_IP)
                            .params("key",ApisSeUtil.getKey())
                            .params("msg",ApisSeUtil.getMsg(js)).execute().body().string();
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
