package com.yunfengsi.Managers.AboutPay;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
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
import com.yunfengsi.Utils.mApplication;
import com.yunfengsi.View.ErWeiMa.QRActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 作者：因陀罗网 on 2017/8/18 18:45
 * 公司：成都因陀罗网络科技有限公司
 */

public class Fahuo extends AppCompatActivity implements View.OnClickListener {
    private EditText  code;
    private TextView  chooseCompany;
    private String name, CompanyCode, money;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, ContextCompat.getColor(this, R.color.main_color));
        setContentView(R.layout.activity_fahuo_info);
        findViewById(R.id.title_back).setOnClickListener(this);
        chooseCompany = (TextView) findViewById(R.id.chooseCompany);
        TextView  commit = (TextView) findViewById(R.id.commit);
        ImageView toScan = (ImageView) findViewById(R.id.toscan);
        toScan.setOnClickListener(this);
        commit.setOnClickListener(this);
        code = (EditText) findViewById(R.id.code);
        chooseCompany.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.toscan:
                if (Build.VERSION.SDK_INT >= 23) {
                    int ca = ContextCompat.checkSelfPermission(mApplication.getInstance(), Manifest.permission.CAMERA);
                    if (ca != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, 000);
                        return;
                    }
                }
                Intent intent = new Intent(this, QRActivity.class);
                startActivityForResult(intent, 66);
                break;
            case R.id.chooseCompany:
                Intent i = new Intent(this, WuLiuCompanys.class);
                startActivityForResult(i, 666);
                break;
            case R.id.commit:
                if (code.getText().toString().trim().equals("")) {
                    ToastUtil.showToastShort("请填写运单号");
                    return;
                }
                if (name.equals("")) {
                    ToastUtil.showToastShort("请选择物流公司");
                    return;
                }
                AlertDialog.Builder b = new AlertDialog.Builder(this);
                b.setMessage("商品:  " + name + "\n"
                        + "实付:  " + getIntent().getStringExtra("money") + "元\n"
                        + "运单号:  " + code.getText().toString() + "\n"
                        + "承运公司:  " + name).setTitle("发货信息确认").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        fahuo();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();

                break;
        }
    }

    private void fahuo() {
        if (Network.HttpTest(this)) {
            JSONObject js = new JSONObject();

            try {
                js.put("m_id", Constants.M_id);
                js.put("exp_code", code.getText().toString());
                js.put("express", CompanyCode);
                js.put("exp_name", name);
                js.put("id",getIntent().getStringExtra("id"));
                js.put("admin_id",PreferenceUtil.getUserId(this));
                js.put("user_id", getIntent().getStringExtra("user_id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ApisSeUtil.M m = ApisSeUtil.i(js);
            LogUtil.e("绑定订单：：" + js);
            OkGo.post(Constants.Deliveryexpress).tag(this)
                    .params("key", m.K())
                    .params("msg", m.M())
                    .execute(new AbsCallback<HashMap<String, String>>() {
                        @Override
                        public HashMap<String, String> convertSuccess(Response response) throws Exception {
                            return AnalyticalJSON.getHashMap(response.body().string());
                        }



                        @Override
                        public void onSuccess(HashMap<String, String> map, Call call, Response response) {
                            if (map != null) {
                                if ("000".equals(map.get("code"))) {
                                    JSONObject js = new JSONObject();
                                    try {
                                        js.put("id", getIntent().getStringExtra("id"));
                                        js.put("snr", getIntent().getStringExtra("snr"));
                                        js.put("user_id", PreferenceUtil.getUserId(Fahuo.this));
                                        js.put("admin_id", PreferenceUtil.getUserId(Fahuo.this));
                                        js.put("m_id", Constants.M_id);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    ApisSeUtil.M m = ApisSeUtil.i(js);
                                    OkGo.post(Constants.Deliveryexpress)
                                            .params("key", m.K())
                                            .params("msg", m.M())
                                            .execute(new StringCallback() {


                                                @Override
                                                public void onBefore(BaseRequest request) {
                                                    super.onBefore(request);
//                                                            ProgressUtil.show(Fahuo.this, "", "正在发货中");
                                                }

                                                @Override
                                                public void onSuccess(String s, Call call, Response response) {
                                                    HashMap<String, String> m = AnalyticalJSON.getHashMap(s);
                                                    if (m != null) {
                                                        if ("000".equals(m.get("code"))) {
                                                            ToastUtil.showToastShort("该订单已发货");

                                                            setResult(999);
                                                            finish();
                                                        } else {
                                                            ToastUtil.showToastShort("订单发货失败，请稍后重试");
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onAfter(@Nullable String s, @Nullable Exception e) {
                                                    super.onAfter(s, e);
                                                    ProgressUtil.dismiss();
                                                }


                                            });
                                } else {
                                    ToastUtil.showToastShort("快递信息绑定失败，请稍后重试");
                                }
                            }

                        }

                        @Override
                        public void onBefore(BaseRequest request) {
                            super.onBefore(request);
                            ProgressUtil.show(Fahuo.this, "", "正在绑定快递信息");
                        }

                        @Override
                        public void onAfter(@Nullable HashMap<String, String> map, @Nullable Exception e) {
                            super.onAfter(map, e);
                            ProgressUtil.dismiss();
                        }
                    });

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == 66) {
                code.setText(data.getStringExtra("code"));
            } else if (requestCode == 666) {
                name = data.getStringExtra("name");
                CompanyCode = data.getStringExtra("code");
                chooseCompany.setText(name);
                LogUtil.e("选中的物流：；" + name + "   Code::" + CompanyCode);

            }

        }
    }
}
