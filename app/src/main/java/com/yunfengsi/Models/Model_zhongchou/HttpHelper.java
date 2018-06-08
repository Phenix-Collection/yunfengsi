package com.yunfengsi.Models.Model_zhongchou;

import android.content.Context;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.yunfengsi.Utils.ApisSeUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/9/22 0022.
 */
public class HttpHelper {
    private Context mContext;
    private HttpUtilHelperCallback mCallbck;

    public HttpHelper(Context mContext, HttpUtilHelperCallback mCallbck) {
        this.mContext = mContext;
        this.mCallbck = mCallbck;
    }

    /**
     * 无需传参，直接获取数据
     * @param url 参数url地址
     * @param tab 标识
     */
    public void getData(String url,final int tab){
        OkGo.get(url).tag(mContext).execute(new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                try {
                    mCallbck.successCallback(tab,response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                mCallbck.errorCallback(tab);
            }

        });
    }

    /**
     * 传String类型的数据获取参数
     * @param
     * @param url
     * @param tab
     */
    public void postData(String url, List<String> key_list, List<String> value_list, final int tab){
        HttpParams httpParams=new HttpParams();
        httpParams.put(new HashMap<String, String>());

        JSONObject js=new JSONObject();
        for (int i=0;i<key_list.size();i++){
            try {
                js.put(key_list.get(i),value_list.get(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        ApisSeUtil.M m=ApisSeUtil.i(js);
        httpParams.put("key", m.K());

        httpParams.put("msg",m.M());
        //params.put();

        OkGo.post(url).params(httpParams).tag(mContext).execute(new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                mCallbck.successCallback(tab,s);
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                mCallbck.errorCallback(tab);
            }


        });
    }

    public interface HttpUtilHelperCallback
    {
        /**
         * 数据请求成功回调函数
         *
         * @param tab
         *            标记
         * @param result
         *            数据请求返回数据
         */
        public void successCallback(int tab, String result);

        /**
         * 数据请求失败回调函数
         *
         * @param tab
         *            标记
         */
        public void errorCallback(int tab);
    }
}
