package com.yunfengsi.Utils;

import android.app.Activity;
import android.text.TextUtils;
import android.webkit.WebView;

import com.lzy.okgo.OkGo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Administrator on 2017/4/16.
 */

public class Api {
    /*
    获取活动报名须知
     */
    public static void getUserNeedKnow(final Activity context, final WebView webView){

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    JSONObject js=new JSONObject();
                    js.put("m_id",Constants.M_id);
                    final String  data = OkGo.post(Constants.getUserNeedKnow)
                            .params("key",ApisSeUtil.getKey())
                            .params("msg",ApisSeUtil.getMsg(js))
                            .execute().body().string();
                    if(!TextUtils.isEmpty(data)){
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject jsonObject=new JSONObject(data);
                                    if(jsonObject!=null){
                                       String  html=jsonObject.get("act_prol").toString();
                                        webView.loadDataWithBaseURL("", html
                                                , "text/html", "UTF-8", null);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }).start();


    }
}
