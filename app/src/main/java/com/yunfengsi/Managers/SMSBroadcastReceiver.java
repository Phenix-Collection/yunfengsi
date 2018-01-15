package com.yunfengsi.Managers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.EditText;

import com.yunfengsi.Utils.LogUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 作者：因陀罗网 on 2018/1/12 13:11
 * 公司：成都因陀罗网络科技有限公司
 */

public class SMSBroadcastReceiver extends BroadcastReceiver {
    private onMessgeReceiveListener listener;
    private String TAG="SMSBroadcastReceiver";
    private EditText input;

    public SMSBroadcastReceiver(EditText editText) {
        this.input=editText;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Object[] pdus = (Object[]) intent.getExtras().get("pdus");
        for (Object pdu : pdus) {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
            String sender = smsMessage.getDisplayOriginatingAddress();
            String content = smsMessage.getMessageBody();
            long date = smsMessage.getTimestampMillis();
            Date timeDate = new Date(date);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = simpleDateFormat.format(timeDate);

            Log.e(TAG, "onReceive: 短信来自:" + sender);
            Log.e(TAG, "onReceive: 短信内容:" + content);
            Log.e(TAG, "onReceive: 短信时间:" + time);
            LogUtil.e("listener：："+listener);
            //如果短信号码来自自己的短信网关号码
            if (listener != null) {
                Log.e(TAG, "onReceive: 回调");
                listener.onReceive(content);
            }
            if(input!=null){
                input.setText(getTrueCode(content));
            }
        }
    }

    public interface  onMessgeReceiveListener{
        void  onReceive(String msg);
    }


    public void setListener(onMessgeReceiveListener listener) {
        this.listener = listener;
    }

    public String getTrueCode(String message){
        String code="";
        code=message.substring(message.indexOf("验证码为"),message.indexOf("验证码为")+6);
        LogUtil.e("验证码：："+code);
        return code;
    }
}
