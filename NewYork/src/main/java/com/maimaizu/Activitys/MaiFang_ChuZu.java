package com.maimaizu.Activitys;

import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.request.BaseRequest;
import com.maimaizu.Base.BaseActivity;
import com.maimaizu.R;
import com.maimaizu.Utils.AnalyticalJSON;
import com.maimaizu.Utils.Constants;
import com.maimaizu.Utils.PreferenceUtil;
import com.maimaizu.Utils.ProgressUtil;
import com.maimaizu.Utils.StatusBarCompat;
import com.maimaizu.Utils.ToastUtil;

import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/5/8.
 */

public class MaiFang_ChuZu extends BaseActivity implements View.OnClickListener{
    private TextView title;
    private EditText vallige,address,money,name,phone;
    private TextView commit;

    private TextView money_tv;
    @Override
    public int getLayoutId() {
        return R.layout.activity_maifang;
    }

    @Override
    public void initView() {
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        title = (TextView) findViewById(R.id.title);
        vallige = (EditText) findViewById(R.id.vallige);
        address = (EditText) findViewById(R.id.address);
        money_tv= (TextView) findViewById(R.id.money_tv);
        money= (EditText) findViewById(R.id.money);
        name= (EditText) findViewById(R.id.user_name);
        phone= (EditText) findViewById(R.id.phone);
        if(getIntent().getIntExtra("type",1)==2){
            title.setText("发布租赁房源");
            money_tv.setText("期望租金");
            money.setHint("您期望的月租金(美元)");
        }else{
            title.setText("免费发布房源");
            money_tv.setText("期望售价");
            money.setHint("您期望的售价(美元)");
        }
    }

    @Override
    public void setOnClick() {

    }

    @Override
    public boolean setEventBus() {
        return false;
    }

    @Override
    public void doThings() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.info:
                break;
            case R.id.commit:
                if(vallige.getText().toString().equals("")){
                    ToastUtil.showToastShort("请输入小区名称", Gravity.CENTER);
                    return;
                }
                if(address.getText().toString().equals("")){
                    ToastUtil.showToastShort("请输入详细地址", Gravity.CENTER);
                    return;
                }
                if(money.getText().toString().equals("")){
                    ToastUtil.showToastShort("请输入"+money.getHint().toString(), Gravity.CENTER);
                    return;
                }
                if(name.getText().toString().equals("")){
                    ToastUtil.showToastShort("请输入您的称呼", Gravity.CENTER);
                    return;
                }
                if(phone.getText().toString().equals("")){
                    ToastUtil.showToastShort("请输入手机号码", Gravity.CENTER);
                    return;
                }
                OkGo.post(Constants.FangYuanCommit).tag(this)
                        .params("key",Constants.safeKey)
                        .params("m_id",Constants.M_id)
                        .params("type",getIntent().getIntExtra("type",1))
                        .params("user_id", PreferenceUtil.getUserIncetance(this).getString("user_id",""))
                        .params("call",name.getText().toString())
                        .params("phone",phone.getText().toString())
                        .params("village",vallige.getText().toString())
                        .params("address",address.getText().toString())
                        .params("money",money.getText().toString())
                        .execute(new AbsCallback<HashMap<String ,String>>() {
                            @Override
                            public void onSuccess(HashMap<String, String> map, Call call, Response response) {
                                if(map!=null){
                                    if("000".equals(map.get("code"))){
                                        ToastUtil.showToastLong("房源信息已提交，请等待工作人员与您联系",Gravity.CENTER);
                                        finish();
                                        return;
                                    }
                                }
                                ToastUtil.showToastShort("房源信息提交失败，请检查网络连接");
                            }

                            @Override
                            public HashMap<String, String> convertSuccess(Response response) throws Exception {

                                return AnalyticalJSON.getHashMap(response.body().string());
                            }

                            @Override
                            public void onAfter(HashMap<String, String> map, Exception e) {
                                super.onAfter(map, e);
                                ProgressUtil.dismiss();
                            }

                            @Override
                            public void onBefore(BaseRequest request) {
                                super.onBefore(request);
                                ProgressUtil.show(MaiFang_ChuZu.this,"","正在提交房源信息\n请稍等");
                            }
                        });
                break;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(this);
    }
}
