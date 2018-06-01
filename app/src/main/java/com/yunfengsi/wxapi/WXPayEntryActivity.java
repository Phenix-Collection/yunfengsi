package com.yunfengsi.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yunfengsi.Model_zhongchou.Fund_Share;
import com.yunfengsi.R;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.ImageUtil;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.mApplication;
import com.yunfengsi.ZhiFuShare;

import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2016/7/15.
 */
public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler, View.OnClickListener {
    private static final String TAG = "WXPayEntryActivity";
    private IWXAPI                  api;
    private TextView                msg;
    private WeakReference<Activity> context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wx_result);
        Log.w(TAG, "-=-=进入WXPayEntryActivity:-==-= ");
        context = new WeakReference<Activity>(this);
        api = WXAPIFactory.createWXAPI(mApplication.getInstance(), Constants.WXPay_APPID);
        api.registerApp(Constants.WXPay_APPID);
        api.handleIntent(getIntent(), this);

    }

    @Override
    public void onReq(BaseReq baseReq) {
        LogUtil.e("微信支付请求");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onResp(BaseResp resp) {
        if (msg == null) {
            msg = (TextView) findViewById(R.id.wx_result);
            ((ImageView) findViewById(R.id.wx_imageview)).setImageBitmap(ImageUtil.readBitMap(this, R.drawable.pay_wc));
        }
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            if (resp.errCode == 0) {
                msg.setText("支付成功");
                if ("4".equals(mApplication.type)) {
                    Intent intent1 = new Intent(this, ZhiFuShare.class);
                    intent1.putExtra("stu_id", mApplication.sut_id);
                    startActivity(intent1);

                    finish();
                } else if ("5".equals(mApplication.type)) {//慈善
                    Intent intent = new Intent("Mine");
                    intent.putExtra("level", true);
                    sendBroadcast(intent);
                    finish();
                    // TODO: 2017/5/11 进入分享页面
                    ///
                    intent.setClass(this, Fund_Share.class);
                    intent.putExtra("sut_id", mApplication.sut_id);
                    intent.putExtra("id", mApplication.id);
                    intent.putExtra("title", mApplication.title);
                    startActivity(intent);
                }
            } else if (resp.errCode == -2) {
                msg.setText("您已取消本次支付");
            } else {
                msg.setText("订单获取失败");
                Toast.makeText(WXPayEntryActivity.this, "请检查网络稍后重试", Toast.LENGTH_SHORT).show();
            }
        }

    }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wx_commit:
                finish();
                break;
        }
    }
}
