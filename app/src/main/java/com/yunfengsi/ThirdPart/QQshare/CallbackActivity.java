//package com.yunfengsi.ThirdPart.QQshare;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.tencent.mobileqq.openpay.api.IOpenApi;
//import com.tencent.mobileqq.openpay.api.IOpenApiListener;
//import com.tencent.mobileqq.openpay.api.OpenApiFactory;
//import com.tencent.mobileqq.openpay.data.base.BaseResponse;
//import com.tencent.mobileqq.openpay.data.pay.PayResponse;
//import com.yunfengsi.Models.Model_zhongchou.Fund_Share;
//import com.yunfengsi.R;
//import com.yunfengsi.Utils.ImageUtil;
//import com.yunfengsi.Utils.LogUtil;
//import com.yunfengsi.Utils.QpayUtil;
//import com.yunfengsi.Utils.mApplication;
//import com.yunfengsi.WebShare.WebInteraction;
//
//
//public class CallbackActivity extends Activity implements IOpenApiListener, View.OnClickListener {
//
//
//    IOpenApi openApi;
//    private TextView msg;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.wx_result);
//
//        openApi = OpenApiFactory.getInstance(this, QpayUtil.APP_ID);
//        openApi.handleIntent(getIntent(), this);
//    }
//
//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        setIntent(intent);
//        openApi.handleIntent(intent, this);
//    }
//
//
//    @Override
//    public void onOpenResponse(BaseResponse response) {
//        if (msg == null) {
//            msg = (TextView) findViewById(R.id.wx_result);
//            ((ImageView) findViewById(R.id.wx_imageview)).setImageBitmap(ImageUtil.readBitMap(this, R.drawable.pay_qq));
//        }
//        if (response == null) {
//            msg.setText("支付失败");
//            return;
//        } else {
//            if (response instanceof PayResponse) {
//                PayResponse payResponse = (PayResponse) response;
//                LogUtil.w(payResponse.retCode+"");
//                if (payResponse.isSuccess()) {
//                    if (!payResponse.isPayByWeChat()) {
//                        msg.setText("支付成功");
//                        if("4".equals(mApplication.type)){
//                            Intent intent1 = new Intent(this, WebInteraction.class);
//                            intent1.putExtra("stu_id", mApplication.sut_id);
//                            startActivity(intent1);
//                            finish();
//                        }
//                        else if("5".equals(mApplication.type)){//慈善
//                            Intent intent=new Intent("Mine");
//                            intent.putExtra("level",true);
//                            sendBroadcast(intent);
//                            finish();
//                            // TODO: 2017/5/11 进入分享页面
//                            ///
//                            intent.setClass(this, Fund_Share.class);
//                            intent.putExtra("sut_id",mApplication.sut_id);
//                            intent.putExtra("id",mApplication.id);
//                            intent.putExtra("title",mApplication.title);
//                            startActivity(intent);
//                        }
//                    }
//                } else if (payResponse.retCode == -1) {
//                    msg.setText("您取消了本次支付");
//                } else if (payResponse.retCode == -3) {
//                    msg.setText("请勿重复提交订单");
//                } else if (payResponse.retCode == -4) {
//                    msg.setText("快速注册用户手机号不一致");
//                } else if (payResponse.retCode == -5) {
//                    msg.setText("账户被冻结");
//                } else if (payResponse.retCode == -6) {
//                    msg.setText("支付密码输入错误次数超过上限");
//                } else if (payResponse.retCode == -100) {
//                    msg.setText("网络异常错误");
//                    Toast.makeText(CallbackActivity.this, "请检查网络稍后重试", Toast.LENGTH_SHORT).show();
//                } else {
//                    msg.setText("支付失败");
//                    Toast.makeText(CallbackActivity.this, "请检查网络稍后重试", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }
//
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.wx_commit:
//                finish();
//                break;
//        }
//    }
//}
