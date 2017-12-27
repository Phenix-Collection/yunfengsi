package com.maimaizu.Mine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.maimaizu.Activitys.HomeActivity;
import com.maimaizu.R;
import com.maimaizu.Utils.AnalyticalJSON;
import com.maimaizu.Utils.Constants;
import com.maimaizu.Utils.ImageUtil;
import com.maimaizu.Utils.LogUtil;
import com.maimaizu.Utils.ProgressUtil;
import com.maimaizu.Utils.StatusBarCompat;
import com.maimaizu.Utils.ToastUtil;
import com.maimaizu.Utils.Verification;
import com.maimaizu.Utils.mApplication;

import java.io.IOException;
import java.util.HashMap;


/**
 * Created by Administrator on 2016/6/15.
 */
public class ZhuCe extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ZhuCe";
    private EditText phonenum, password, Mid, password2;
    private TextView getMid;
    private Button submit;
    private String YZM;
//    public  ZhuCe instance;
    private ImageView headImage;
    private SharedPreferences sp;
    private ImageView back;
    /*
    手机号码 邮箱
     */
    private TextView tvPhone, tvEmail;
    private boolean isPhone = true;
    private LinearLayout yanzhenLayout;
    private InputMethodManager imm;
    private int time=60000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zhuce1);
        StatusBarCompat.compat(this, getResources().getColor(R.color.umeng_socialize_divider));
//        instance = this;
        sp = getSharedPreferences("user", MODE_PRIVATE);
        initView();
    }

    private void initView() {
        tvPhone = (TextView) findViewById(R.id.zhuce_phone);
        tvPhone.setText(mApplication.ST("手机注册"));
        tvEmail = (TextView) findViewById(R.id.zhuce_email);
        tvEmail.setText(mApplication.ST("邮箱注册"));
        tvEmail.setOnClickListener(this);
        tvPhone.setOnClickListener(this);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        yanzhenLayout = (LinearLayout) findViewById(R.id.yanzhen_layout);
        password2 = (EditText) findViewById(R.id.Zhuce_password2);
        password2.setHint(mApplication.ST("确认密码/Password"));
        back = (ImageView) findViewById(R.id.zhuce_back);
        back.setOnClickListener(this);
        headImage = (ImageView) findViewById(R.id.activity_register_imageview);
        phonenum = (EditText) findViewById(R.id.Zhuce_phonenum);
        phonenum.setHint(mApplication.ST("手机号码/PhoneNumber"));
        password = (EditText) findViewById(R.id.Zhuce_password);
        password.setHint(mApplication.ST("密码/Password"));
        Mid = (EditText) findViewById(R.id.Zhuce_Mid);
        Mid.setHint(mApplication.ST("验证码/Code"));
        getMid = (TextView) findViewById(R.id.Zhuce_getMid);
        getMid.setHint(mApplication.ST("获取验证码"));
        getMid.setOnClickListener(this);
        submit = (Button) findViewById(R.id.Zhuce_submit);
        submit.setText(mApplication.ST("注册"));
        submit.setOnClickListener(this);
        headImage.setImageBitmap(ImageUtil.readBitMap(this, R.drawable.indra));
        tvPhone.performClick();
//        (findViewById(R.id.zhuce_main)).setBackgroundDrawable(new GlideBitmapDrawable(getResources(), ImageUtil.readBitMap(this, R.drawable.backgd)));
    }

    CountDownTimer timer = new CountDownTimer(60000, 1000) {//验证码倒计时
        @Override
        public void onTick(long millisUntilFinished) {
            getMid.setText(mApplication.ST(millisUntilFinished / 1000 + "秒后可重新获取"));
            getMid.setTextColor(Color.parseColor("#bbbbbb"));
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
        OkGo.getInstance().cancelTag(TAG);
//        (findViewById(R.id.zhuce_main)).setBackgroundDrawable(null);
    }

    Handler mZhuceHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                if (isPhone) {
                    Toast.makeText(ZhuCe.this, mApplication.ST("注册成功"), Toast.LENGTH_SHORT).show();
                    ProgressUtil.show(ZhuCe.this, "",mApplication.ST( "正在登录"));
                    ////////////////////////////////////首次登陆

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String data1 = null;
                            try {
                                data1 = OkGo.post(Constants.Login_Ip).params("phone", phonenum.getText().toString()).params("password", password.getText().toString())
                                        .params("m_id", Constants.M_id).params("key", Constants.safeKey)
                                        .params("type", "1").execute().body().string();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (data1 != null) {
                                HashMap<String, String> map = AnalyticalJSON.getHashMap(data1);
                                if (map != null && "000".equals(map.get("code"))) {
                                    SharedPreferences usersp = getSharedPreferences("user", MODE_PRIVATE);
                                    SharedPreferences.Editor ed = usersp.edit();
                                    ed.putString("user_id", map.get("user_id"));
                                    ed.putString("phone", phonenum.getText().toString());
                                    ed.putString("uid", phonenum.getText().toString());
                                    ed.apply();
                                    mZhuceHandler.sendEmptyMessage(-1);

                                } else {
                                    mZhuceHandler.sendEmptyMessage(-2);
                                }
                            } else {
                                mZhuceHandler.sendEmptyMessage(-2);
                            }
                        }
                    }).start();
                } else {
                    ProgressUtil.dismiss();
                    Toast.makeText(ZhuCe.this, mApplication.ST("邮箱验证已发送至您的邮箱，请前往邮箱进行验证，有效期30分钟"), Toast.LENGTH_LONG).show();
                    finish();
                }

            } else if (msg.what == 1) {
                Toast.makeText(ZhuCe.this, mApplication.ST("用户信息提交失败"), Toast.LENGTH_SHORT).show();
                ProgressUtil.dismiss();
            } else if (msg.what == -1) {
                ProgressUtil.dismiss();
                Intent intent = new Intent(ZhuCe.this, HomeActivity.class);
                startActivity(intent);
//                if (mApplication.getInstance().login != null)mApplication.getInstance().login.finish();
                Intent intent1 = new Intent("Mine");
                sendBroadcast(intent1);
                finish();

            } else if (msg.what == -2) {
                ProgressUtil.dismiss();
                Toast.makeText(ZhuCe.this, mApplication.ST("登陆失败，请输入已注册的账户密码"), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ZhuCe.this, Login.class);
                startActivity(intent);
                finish();
            }
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        Intent intent=new Intent(this,MainActivity.class);
//        startActivity(intent);
//        intent.putExtra("exit",true);
//        startActivity(intent);


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
                phonenum.setHint(mApplication.ST("手机号码/PhoneNumber"));
                phonenum.setText("");
                phonenum.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
                imm.hideSoftInputFromWindow(phonenum.getWindowToken(), 0);
                phonenum.setInputType(InputType.TYPE_CLASS_NUMBER);
                isPhone = true;

                break;
            case R.id.zhuce_email://邮箱注册
                v.setSelected(true);
                ((TextView) v).setTextColor(Color.WHITE);
                tvPhone.setTextColor(Color.GRAY);
                tvPhone.setSelected(false);
                phonenum.setHint(mApplication.ST("邮箱地址/Email"));
                phonenum.setText("");
                phonenum.setFilters(new InputFilter[]{new InputFilter.LengthFilter(25)});
                imm.hideSoftInputFromWindow(phonenum.getWindowToken(), 0);
                phonenum.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                yanzhenLayout.setVisibility(View.GONE);
                isPhone = false;
                break;


            case R.id.zhuce_back:
                onBackPressed();

                break;
            case R.id.Zhuce_getMid://获取验证码
                if (phonenum.getText().toString().equals("")) {
                    Toast.makeText(ZhuCe.this, mApplication.ST("请输入手机号码") , Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Verification.isMobileNO(phonenum.getText().toString())) {
                    Toast.makeText(ZhuCe.this, mApplication.ST("请输入正确的手机号码"), Toast.LENGTH_SHORT).show();
                    return;
                }
                //获取验证码
                v.setEnabled(false);
                getMid.setText(mApplication.ST("正在请求验证码"));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String data = OkGo.post(Constants.Mid_IP).params("phone", phonenum.getText().toString())
                                    .params("key", Constants.safeKey).params("m_id", Constants.M_id).execute().body().string();
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
                                                    ToastUtil.showToastShort(mApplication.ST("验证码请求过于频繁"), Gravity.CENTER);
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
            case R.id.Zhuce_submit://///////提交注册
                if ("".equals(phonenum.getText().toString().trim())) {
                    if (isPhone) {
                        Toast.makeText(ZhuCe.this, mApplication.ST("请输入手机号码"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ZhuCe.this, mApplication.ST("请输入邮箱地址"), Toast.LENGTH_SHORT).show();
                    }
                    return;
                }
                if ("".equals(password.getText().toString().trim())) {
                    Toast.makeText(ZhuCe.this, mApplication.ST("请输入密码"), Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if (password.getText().length() < 6) {
                        Toast.makeText(ZhuCe.this, mApplication.ST("请输入6-16位的密码"), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if ("".equals(password2.getText().toString().trim())) {
                    Toast.makeText(ZhuCe.this, mApplication.ST("请再次输入密码"), Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if (password2.getText().length() < 6) {
                        Toast.makeText(ZhuCe.this, mApplication.ST("请输入6-16位的密码"), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if (password.getText() != null && password2.getText() != null) {
                    if (!password.getText().toString().equals(password2.getText().toString())) {
                        Toast.makeText(ZhuCe.this, mApplication.ST("两次密码输入不一致，请重新输入"), Toast.LENGTH_SHORT).show();
                        password.setText("");
                        password2.setText("");
                        return;
                    }
                }
                if (isPhone) {
                    if (!Verification.isMobileNO(phonenum.getText().toString())) {
                        Toast.makeText(ZhuCe.this, mApplication.ST("请输入正确的手机号码"), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if ("".equals(Mid.getText().toString().trim())) {
                        Toast.makeText(ZhuCe.this, mApplication.ST("请输入验证码"), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (YZM == null || !YZM.equals(Mid.getText().toString().trim())) {
                        Toast.makeText(ZhuCe.this, mApplication.ST("验证码不匹配"), Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    if (!Verification.isEmail(phonenum.getText().toString())) {
                        Toast.makeText(ZhuCe.this, mApplication.ST("请输入正确的邮箱地址"), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                ProgressUtil.show(this, "", mApplication.ST("正在提交用户信息"));
                if (isPhone) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String data1 = null;
                            try {
                                data1 = OkGo.post(Constants.Regist_Ip).tag(TAG).params("phone", phonenum.getText().toString())
                                        .params("password", password.getText().toString()).params("key", Constants.safeKey).params("m_id", Constants.M_id).execute().body().string();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            LogUtil.e("手机注册data1-------->" + data1);
                            if (data1 != null) {
                                HashMap<String, String> map = AnalyticalJSON.getHashMap(data1);
                                if (map != null) {
                                    if ("000".equals(map.get("code"))) {
                                        mZhuceHandler.sendEmptyMessage(0);
                                    } else if ("003".equals(map.get("code"))) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(ZhuCe.this, mApplication.ST("用户名已注册"), Toast.LENGTH_SHORT).show();
                                                ProgressUtil.dismiss();
                                            }
                                        });
                                    } else {
                                        mZhuceHandler.sendEmptyMessage(1);
                                    }
                                }
                            } else {
                                mZhuceHandler.sendEmptyMessage(1);
                            }
                        }
                    }).start();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String data1 = null;
                            try {
                                data1 = OkGo.post(Constants.email_zhuce__IP)
                                        .tag(TAG)
                                        .params("email", phonenum.getText().toString().trim())
                                        .params("password", password.getText().toString().trim())
                                        .params("key", Constants.safeKey)
                                        .params("m_id", Constants.M_id).execute().body().string();

                            } catch (IOException e) {
                                LogUtil.e("dsfjsldjflksdjflksdjflksdj");
                            }
                            LogUtil.e("邮箱注册data1-------->" + data1);
                            if (data1 != null) {
                                HashMap<String, String> map = AnalyticalJSON.getHashMap(data1);
                                if (map != null) {
                                    if ("000".equals(map.get("code"))) {
                                        mZhuceHandler.sendEmptyMessage(0);
                                    } else if ("003".equals(map.get("code"))) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(ZhuCe.this, mApplication.ST("用户名已注册"), Toast.LENGTH_SHORT).show();
                                                ProgressUtil.dismiss();
                                            }
                                        });
                                    } else if ("005".equals(map.get("code"))) {
                                        Toast.makeText(ZhuCe.this, mApplication.ST("邮件发送失败，请稍后重试"), Toast.LENGTH_SHORT).show();
                                        ProgressUtil.dismiss();
                                    } else {
                                        mZhuceHandler.sendEmptyMessage(1);
                                    }
                                }
                            } else {
                                mZhuceHandler.sendEmptyMessage(1);
                            }
                        }
                    }).start();
                }


                break;
        }
    }

}
