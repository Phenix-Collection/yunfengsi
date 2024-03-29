package com.pay.sample;

import com.tencent.mobileqq.openpay.api.IOpenApi;
import com.tencent.mobileqq.openpay.api.IOpenApiListener;
import com.tencent.mobileqq.openpay.api.OpenApiFactory;
import com.tencent.mobileqq.openpay.data.base.BaseResponse;
import com.tencent.mobileqq.openpay.data.pay.PayResponse;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;

public class CallbackActivity extends Activity implements IOpenApiListener {

    String appId = "1000002";
    IOpenApi openApi;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callback);

        openApi = OpenApiFactory.getInstance(this, appId);
        openApi.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        openApi.handleIntent(intent, this);
    }
    
    
    @Override
    public void onOpenResponse(BaseResponse response) {
        String title = "Callback from mqq";
        String message;
        
        if (response == null) {
            message = "response is null.";
            return;
        } else {
            if (response instanceof PayResponse) {
                PayResponse payResponse = (PayResponse) response;

                message = " apiName:" + payResponse.apiName
                        + " serialnumber:" + payResponse.serialNumber
                        + " isSucess:" + payResponse.isSuccess()
                        + " retCode:" + payResponse.retCode
                        + " retMsg:" + payResponse.retMsg;
                if (payResponse.isSuccess()) {
                    if (!payResponse.isPayByWeChat()) {
                        message += " transactionId:" + payResponse.transactionId
                                + " payTime:" + payResponse.payTime
                                + " callbackUrl:" + payResponse.callbackUrl
                                + " totalFee:" + payResponse.totalFee
                                + " spData:" + payResponse.spData;
                    }
                    
                }
            } else {
                message = "response is not PayResponse.";
            }
        }
        
        Dialog alertDialog = new AlertDialog.Builder(this). 
                setTitle(title). 
                setMessage(message). 
                create(); 
        alertDialog.show();
    }

}
