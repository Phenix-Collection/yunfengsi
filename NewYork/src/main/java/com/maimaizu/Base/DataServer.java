package com.maimaizu.Base;

import android.view.Gravity;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.request.BaseRequest;
import com.maimaizu.Adapter.mBaseAdapter;
import com.maimaizu.Utils.Constants;
import com.maimaizu.Utils.Network;
import com.maimaizu.Utils.ToastUtil;
import com.maimaizu.Utils.mApplication;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/5/2.
 */

public class DataServer {
    public   interface onDataReceivedlistener {
        void onDataReceived(ArrayList<mBaseAdapter.OneMulitem> list);
        void onDataError();
    };

    public static void getMainData(Object tag,int page, final mBaseAdapter adapter, final onDataReceivedlistener listener) {
        OkGo.post(Constants.getHomeMore).tag(tag).params("key", Constants.safeKey)
                .params("m_id", Constants.M_id)
                .params("page", page)
                .params("city", mApplication.city)
                .execute(new AbsCallback<ArrayList<mBaseAdapter.OneMulitem>>() {
                    @Override
                    public ArrayList<mBaseAdapter.OneMulitem> convertSuccess(Response response) throws Exception {
                        if (response != null) {
                            String data = response.body().string();
                            JSONArray jsonArray = new JSONArray(data);
                            ArrayList<mBaseAdapter.OneMulitem> oneList = new ArrayList<mBaseAdapter.OneMulitem>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                                mBaseAdapter.OneMulitem o = adapter.getOneMulitem();
                                o.setArea(jsonObject.getString("area"));
                                o.setHousetype(jsonObject.getString("housetype"));
                                o.setId(jsonObject.getString("id"));
                                o.setImage(jsonObject.getString("image"));
                                o.setMoney(jsonObject.getString("money"));
                                o.setPoint(jsonObject.getString("point"));
                                o.setTags(new JSONArray(jsonObject.getString("bq")));
                                o.setVillage(jsonObject.getString("village"));
                                o.setTitle(jsonObject.getString("title"));
                                o.setItemType(Integer.valueOf(jsonObject.getString("type")));
                                oneList.add(o);
                            }
                            return oneList;
                        }
                        return null;
                    }

                    @Override
                    public void onSuccess(ArrayList<mBaseAdapter.OneMulitem> list, Call call, Response response) {
                        listener.onDataReceived(list);
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        if(!Network.HttpTest(mApplication.getInstance())){
                            ToastUtil.showToastShort("网络无法连接，请稍后重试", Gravity.CENTER);
                            try {
                                throw  new Exception("网络无法连接，请稍后重试");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        };
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        listener.onDataError();
                    }
                });
    }
}
