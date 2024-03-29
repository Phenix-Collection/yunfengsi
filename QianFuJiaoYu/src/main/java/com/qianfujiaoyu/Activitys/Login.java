package com.qianfujiaoyu.Activitys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.qianfujiaoyu.R;
import com.qianfujiaoyu.Setting.FindPassword;
import com.qianfujiaoyu.Utils.ACache;
import com.qianfujiaoyu.Utils.AnalyticalJSON;
import com.qianfujiaoyu.Utils.ApisSeUtil;
import com.qianfujiaoyu.Utils.Constants;
import com.qianfujiaoyu.Utils.ImageUtil;
import com.qianfujiaoyu.Utils.ProgressUtil;
import com.qianfujiaoyu.Utils.StatusBarCompat;
import com.qianfujiaoyu.Utils.Verification;
import com.qianfujiaoyu.Utils.mApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.Response;


/**
 * Created by Administrator on 2016/6/3.
 */
public class Login extends AppCompatActivity implements OnClickListener {
    private static final String TAG = "Login";
//    private UMShareAPI mshareApi;
//    private LinearLayout weChatLogin;
//    private LinearLayout QQLogin;
//    private SHARE_MEDIA platform = null;
    private TextView zhuce;
    private EditText username, password;
    private Button Login;
//    private ImageView back;
    private TextView wangjimima;
    private String headurl;
    private int screenWidth;
    private ACache aCache;
    private SharedPreferences sp;

   private InputMethodManager imm;
    public String type;//1   phone  2 email
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login1);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        initView();

    }

    private void initView() {

        imm= (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        aCache = ACache.get(this);
//        mshareApi = UMShareAPI.get(this);
        sp = getSharedPreferences("user", MODE_PRIVATE);
        screenWidth = getResources().getDisplayMetrics().widthPixels;
//        weChatLogin = (LinearLayout) findViewById(R.id.weChat);
//        weChatLogin.setOnClickListener(this);
//        QQLogin = (LinearLayout) findViewById(R.id.QQ);
//        QQLogin.setOnClickListener(this);
        zhuce = (TextView) findViewById(R.id.Login_to_zhuce);
        zhuce.setText(mApplication.ST("注册账号"));
        zhuce.setOnClickListener(this);
        username = (EditText) findViewById(R.id.userName_edt);
        username.setHint(mApplication.ST("手机号/邮箱地址"));
        password = (EditText) findViewById(R.id.passWord_edt);
        password.setHint(mApplication.ST("密码/Password"));
        Login = (Button) findViewById(R.id.Login_login);
        Login.setText(mApplication.ST("登录"));
        Login.setOnClickListener(this);
//        back = (ImageView) findViewById(R.id.login_back);
//        back.setOnClickListener(this);
        wangjimima = (TextView) findViewById(R.id.Login_wangjimima);
        wangjimima.setText(mApplication.ST("忘记密码?"));
        wangjimima.setOnClickListener(this);
        // TODO: 2017/5/18 设置页面背景
//        findViewById(R.id.login_bg).setBackgroundDrawable(new GlideBitmapDrawable(getResources(), ImageUtil.readBitMap(this,R.drawable.backgd)));
        ((ImageView) findViewById(R.id.pe)).setImageBitmap(ImageUtil.readBitMap(this,R.drawable.userpic));
        ((ImageView) findViewById(R.id.password)).setImageBitmap(ImageUtil.readBitMap(this,R.drawable.password));
        ((ImageView) findViewById(R.id.image)).setImageBitmap(ImageUtil.readBitMap(this,R.drawable.indra));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

//            case R.id.weChat:
//                platform = SHARE_MEDIA.WEIXIN;
//                mshareApi.doOauthVerify(this, platform, umAuthListener);
//                if (progressDialog == null) {
//                    progressDialog = new ProgressDialog(Login.this);
//                    progressDialog.isIndeterminate();
//                    progressDialog.setCanceledOnTouchOutside(false);
//                    progressDialog.setMessage("正在登录，请稍等");
//                    progressDialog.show();
//                }
//                break;
//            case R.id.QQ:
//                platform = SHARE_MEDIA.QQ;
//                mshareApi.doOauthVerify(this, platform, umAuthListener);
//                if (progressDialog == null) {
//                    progressDialog = new ProgressDialog(Login.this);
//                    progressDialog.isIndeterminate();
//                    progressDialog.setCanceledOnTouchOutside(false);
//                    progressDialog.setMessage("正在登录，请稍等");
//                    progressDialog.show();
//                }
//                break;
            case R.id.Login_wangjimima:
                Intent intent4 = new Intent(this, FindPassword.class);
                startActivity(intent4);
                break;

            case R.id.Login_to_zhuce:
                Intent intent = new Intent(this, ZhuCe.class);
                startActivity(intent);

                break;
            case R.id.Login_login://登录
                imm.hideSoftInputFromWindow(username.getWindowToken(),0);
                imm.hideSoftInputFromWindow(password.getWindowToken(),0);
                if ("".equals(username.getText().toString().trim()) || "".equals(password.getText().toString().trim())) {
                    Toast.makeText(Login.this, mApplication.ST("请输入完整信息"), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!username.getText().toString().trim().contains("@")) {
                    if(!Verification.isMobileNO(username.getText().toString().trim())){
                        Toast.makeText(Login.this, mApplication.ST("请输入正确的手机号码"), Toast.LENGTH_SHORT).show();
                        return;
                    }else{
                        type="1";
                    }
                }
                if (username.getText().toString().trim().contains("@")) {
                    if(!Verification.isEmail(username.getText().toString().trim())){
                        Toast.makeText(Login.this, mApplication.ST("请输入正确的邮箱地址"), Toast.LENGTH_SHORT).show();
                        return;
                    }else{
                        type="2";
                    }
                }
                ProgressUtil.show(this,"",mApplication.ST("正在登录，请稍等"));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Response response = null;
                        try {
                            JSONObject js=new JSONObject();
                            try {
                                js.put("phone", username.getText().toString());
                                js.put("password", password.getText().toString());
                                js.put("type",type);
                                js.put("m_id",Constants.M_id);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ApisSeUtil.M m=ApisSeUtil.i(js);
                            response = OkGo.post(Constants.Login_Ip).tag(TAG)
                                    .params("key",m.K())
                                    .params("msg",m.M()).execute();
                            String data1 = response.body().string();
                            if (!data1.equals("")) {
                                final HashMap<String, String> map = AnalyticalJSON.getHashMap(data1);//解析登录返回信息
                                if (map != null && "000".equals(map.get("code"))) {
                                    SharedPreferences usersp = getSharedPreferences("user", MODE_PRIVATE);
                                    SharedPreferences.Editor ed = usersp.edit();
                                    ed.putString("user_id", map.get("user_id"));//用户名
                                    ed.putString("uid", username.getText().toString());//用户uid
                                    if(type.equals("1")){
                                        ed.putString("phone", username.getText().toString());//电话号码
                                    }else{
                                        ed.putString("email", username.getText().toString());//邮箱地址
                                    }
                                    // TODO: 2017/5/18 登录成功  绑定推送别名
//                                    final CloudPushService pushService = PushServiceFactory.getCloudPushService();
//                                    pushService.addAlias(map.get("user_id"), new CommonCallback() {
//                                        @Override
//                                        public void onSuccess(String s) {
//                                            LogUtil.e("别名绑定成功，哈哈哈哈哈哈哈哈");
//                                        }
//
//                                        @Override
//                                        public void onFailed(String s, String s1) {
//                                            LogUtil.e("别名绑定失败，呜呜呜呜呜呜");
//                                        }
//                                    });
                                    ed.putString("status", map.get("status"));
                                    ed.apply();
                                    mLoginHandler.sendEmptyMessage(-1);

                                } else if ("003".equals(map.get("code"))) {
                                    mLoginHandler.sendEmptyMessage(-3);
                                } else {
                                    mLoginHandler.sendEmptyMessage(-2);
                                }
                            } else {
                                mLoginHandler.sendEmptyMessage(-2);
                            }

                        }catch (Exception e){
                            mLoginHandler.sendEmptyMessage(-2);
                        }


                    }
                }).start();


                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        Intent intent=new Intent(this,MainActivity.class);
//        startActivity(intent);
//        intent.putExtra("exit",true);
//        startActivity(intent);
//
//        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(TAG);
        ( findViewById(R.id.login_bg)).setBackgroundDrawable(null);
    }

   private  Handler mLoginHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case -1:

                    Toast.makeText(Login.this, mApplication.ST("登录成功"), Toast.LENGTH_SHORT).show();
                   ProgressUtil.dismiss();
                    Intent intent4=new Intent(Login.this,MainActivity.class);
                    startActivity(intent4);
                    Intent intent = new Intent("Mine");
                    sendBroadcast(intent);

                    finish();
                    break;
                case -2:
                    ProgressUtil.dismiss();
                    Toast.makeText(Login.this,mApplication.ST("连接超时,请重新尝试") , Toast.LENGTH_SHORT).show();
                    break;
                case -3:
                    ProgressUtil.dismiss();
                    Toast.makeText(Login.this, mApplication.ST("用户名或密码不匹配，请重新输入"), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        mshareApi.onActivityResult(requestCode, resultCode, data);
    }

//    private UMAuthListener umAuthListener = new UMAuthListener() {
//        String uid;
//        String accessToken;
//
//        @Override
//        public void onComplete(final SHARE_MEDIA platform, int action, final Map<String, String> data) {
//            if (progressDialog == null) {
//                progressDialog = new ProgressDialog(Login.this);
//                progressDialog.isIndeterminate();
//                progressDialog.setCanceledOnTouchOutside(false);
//                progressDialog.setMessage("正在登录，请稍等");
//            }
//            if (!progressDialog.isShowing()) progressDialog.show();
//            if (action == 0) {
//                mshareApi.getPlatformInfo(Login.this, platform, umAuthListener);
//                uid = data.get("openid");
//                accessToken = data.get("access_token");
//            } else if (action == 2) {
//                Log.w(TAG, "onComplete: data=-=-=-=-=-=-=-=-=23434324" + data);
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            String data1 = OkHttpUtils.post(Constants.Login_3_Ip).tag(TAG).params("uid", uid).params("password", accessToken)
//                                    .params("pet_name", platform.equals(SHARE_MEDIA.QQ) ? data.get("screen_name") : data.get("nickname"))
//                                    .params("sex", platform.equals(SHARE_MEDIA.QQ) ? (data.get("gender").equals("男") ? "1" : "2") : data.get("sex"))
//                                    .params("key", Constants.safeKey)
//                                    .params("m_id", Constants.M_id)
//                                    .params("type", platform.equals(SHARE_MEDIA.QQ) ? "2" : "3")
//                                    .params("head", platform.equals(SHARE_MEDIA.QQ) ? data.get("profile_image_url") : data.get("headimgurl")).execute().body().string();
//                            if (!data1.equals("")) {
//                                Log.w(TAG, "run: data1_+_+_+_+_+_" + data1);
//                                final HashMap<String, String> map = AnalyticalJSON.getHashMap(data1);
//                                if (null != map && map.get("code").equals("000")) {
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            SharedPreferences.Editor ed = sp.edit();
//                                            ed.putString("user_id", map.get("user_id"));
//                                            ed.putString("uid", uid);
//                                            ed.putString("phone", map.get("phone"));
//                                            ed.putString("pet_name", (platform.equals(SHARE_MEDIA.QQ) ? data.get("screen_name") : data.get("nickname")).equals("") ? map.get("pet_name") :
//                                                    platform.equals(SHARE_MEDIA.QQ) ? data.get("screen_name") : data.get("nickname"));
//                                            ed.putString("sex", platform.equals(SHARE_MEDIA.QQ) ? data.get("gender") : data.get("sex"));
//                                            if (!(platform.equals(SHARE_MEDIA.QQ) ? data.get("profile_image_url") : data.get("headimgurl")).equals("")) {
//                                                ed.putString("head_url", platform.equals(SHARE_MEDIA.QQ) ? data.get("profile_image_url") : data.get("headimgurl"));
//                                            }
//                                            ed.apply();
//                                            if (progressDialog.isShowing() && progressDialog != null) {
//                                                progressDialog.dismiss();
//                                            }
//                                            if (TextUtils.isEmpty(map.get("phone"))) {
//                                                runOnUiThread(new Runnable() {
//                                                    @Override
//                                                    public void run() {
//                                                        Intent intent = new Intent(Login.this, PhoneCheck.class);
//                                                        startActivity(intent);
//
//                                                    }
//                                                });
//                                            }
//                                            mLoginHandler.sendEmptyMessage(-1);
//
//
//
//
//                                        }
//                                    });
//                                } else {
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                           if(progressDialog!=null&&progressDialog.isShowing()) progressDialog.dismiss();
//                                            Toast.makeText(Login.this, "登录异常，请稍后尝试", Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
//                                }
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    if(progressDialog!=null&&progressDialog.isShowing()) progressDialog.dismiss();
//                                    Toast.makeText(Login.this, "登录异常，请稍后尝试", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                        }
//                    }
//                }).start();
//            }
//
//        }
//
//        @Override
//        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
//            Toast.makeText(getApplicationContext(), "未知错误" + t, Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//
//        public void onCancel(SHARE_MEDIA platform, int action) {
//        }
//    };

}
