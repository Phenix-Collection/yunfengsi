package com.yunfengsi.Models.Model_zhongchou;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.yunfengsi.Managers.Base.BaseSTActivity;
import com.yunfengsi.R;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.MD5Utls;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.ProgressUtil;
import com.yunfengsi.Utils.ShareManager;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.ToastUtil;
import com.yunfengsi.Utils.mApplication;
import com.yunfengsi.Models.YunDou.YunDouAwardDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/5/11.
 */

public class Fund_Share extends BaseSTActivity implements View.OnClickListener{
    private TextInputEditText editText;
    private TextInputLayout editLayout;
    private String id,sut_id;

    @Override
    protected void resetData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.share:
                share();
                break;
            case R.id.commit:
                if(editText.getText().toString().equals("")){
                    ToastUtil.showToastShort("请输入祝福内容");
                    return;
                }
                getData();
                break;

        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.fund_share);
        id=getIntent().getStringExtra("id");
        sut_id=getIntent().getStringExtra("sut_id");
        initView();
        postYundouZX();
        getData();
    }
    public void postYundouZX() {
        JSONObject js = new JSONObject();
        try {
            js.put("m_id", Constants.M_id);
            js.put("user_id", PreferenceUtil.getUserId(this));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e("每日助学   回调确认：：" + js);
        ApisSeUtil.M m = ApisSeUtil.i(js);
        OkGo.post(Constants.ZX_YUNDOU).params("key", m.K())
                .params("msg", m.M())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        try {
                            JSONObject js = new JSONObject(s);
                            if (js != null) {
                                if (js.getString("yundousum") != null && !js.getString("yundousum").equals("0")) {
                                    YunDouAwardDialog.show(Fund_Share.this,"每日助学",js.getString("yundousum"));
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });
    }
    private void getData() {
        JSONObject js=new JSONObject();
        try {
            js.put("mark",editText.getText().toString());
            js.put("m_id", Constants.M_id);
            js.put("sut_id",sut_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkGo.post(Constants.CfgCommit).tag(this)
                .params("key", ApisSeUtil.getKey())
                .params("msg",ApisSeUtil.getMsg(js))
                .execute(new AbsCallback<HashMap<String,String>>() {
                    @Override
                    public HashMap<String, String> convertSuccess(Response response) throws Exception {
                        return AnalyticalJSON.getHashMap(response.body().string());
                    }

                    @Override
                    public void onSuccess(HashMap<String, String> map, Call call, Response response) {
                        if(map!=null){
                            LogUtil.e(map+"");
                            if("000".equals(map.get("code"))){
                                share();
                            }
                        }
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        ProgressUtil.show(Fund_Share.this,"","请稍等");
                    }

                    @Override
                    public void onAfter(HashMap<String, String> map, Exception e) {
                        super.onAfter(map, e);
                        ProgressUtil.dismiss();
                    }




                });
    }

    private void share() {
        String md5 = MD5Utls.stringToMD5(Constants.safeKey);
        String m1 = md5.substring(0, 16);
        String m2 = md5.substring(16, md5.length());
        UMWeb  umWeb = new UMWeb(Constants.FX_host_Ip + "Cfghxd" + "/id/" + m1 + id + m2 + "/st/" + (mApplication.isChina ? "s" : "t"));
        umWeb.setTitle(mApplication.ST(PreferenceUtil.getUserIncetance(Fund_Share.this).getString("pet_name","")+"邀您一起捐"));
        umWeb.setDescription(mApplication.ST(PreferenceUtil.getUserIncetance(Fund_Share.this).getString("pet_name","")+"发起 【"+
        getIntent().getStringExtra("title")+"】 公益助学,祈愿天下孩子都有书读"));
        umWeb.setThumb(new UMImage(Fund_Share.this, R.drawable.yunfengcishan_jpg));
        new ShareManager().shareWeb(umWeb,Fund_Share.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ProgressUtil.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mApplication.id="";
        mApplication.sut_id="";
        mApplication.type="";
        mApplication.title="";
        OkGo.getInstance().cancelTag(this);
    }

    private void initView() {
        TextView title = findViewById(R.id.title);
        title.setText(mApplication.ST("助学回向"));
        TextView text = findViewById(R.id.text);
        text.setText(mApplication.ST("写下祝福"));
        TextView commit = findViewById(R.id.commit);
        commit.setText(mApplication.ST("提交"));
        commit.setOnClickListener(this);
        ImageView image = findViewById(R.id.image);
        Glide.with(this).load(R.drawable.fund_share).fitCenter().into(image);
        editText= findViewById(R.id.edit);
        editText.setHint(mApplication.ST("祝福内容(300字)"));
    }
}
