package com.yunfengsi.YunDou;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.Network;
import com.yunfengsi.Utils.mApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 作者：因陀罗网 on 2018/4/20 14:41
 * 公司：成都因陀罗网络科技有限公司
 */
public class DuiHuanPresenterImpl implements DuiHuanContract.IPresenter{



    DuiHuanContract.IView iView;
    private ArrayList<Fragment> fragments;
    private tabPagerAdapter adapter;
    public DuiHuanPresenterImpl(DuiHuanContract.IView iView) {
        WeakReference<DuiHuanContract.IView> weakReference = new WeakReference<DuiHuanContract.IView>(iView);
        this.iView = weakReference.get();
        fragments=new ArrayList<>();
    }


    @Override
    public void getTitles() {
        if(!Network.HttpTest((Context) iView)){
            return;
        }
        JSONObject js=new JSONObject();
        try {
            js.put("m_id",Constants.M_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m=ApisSeUtil.i(js);
        LogUtil.e("获取兑换中心标题");
        OkGo.post(Constants.ExchangeListTitles).params("key",m.K())
                .params("msg",m.M())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {

                        final ArrayList<HashMap<String, String>> list = AnalyticalJSON.getList_zj(AnalyticalJSON.getHashMap(s).get("msg"));
                        if(list!=null){
                            ArrayList<String > titles=new ArrayList<>();
                            QuanFragment quanFragment=new QuanFragment();
                            Bundle b=new Bundle();
                            b.putString("sort","0");
                            quanFragment.setArguments(b);
                            fragments.add(quanFragment);
                            titles.add(mApplication.ST("全部"));
                            for (HashMap<String, String> map : list) {
                                QuanFragment q=new QuanFragment();
                                Bundle bb=new Bundle();
                                bb.putString("sort",map.get("id"));
                                bb.putString("name",mApplication.ST(map.get("name")));
                                q.setArguments(bb);
                                fragments.add(q);
                                titles.add(map.get("name"));
                            }
                            adapter=new tabPagerAdapter(((DuiHuan) iView),iView.getIFragmentManager(),fragments);
                            adapter.setTitles(titles);
                            iView.showTabs(adapter);

                        }
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        iView.onNetWorkBefore();
                    }

                    @Override
                    public void onAfter(String s, Exception e) {
                        super.onAfter(s, e);
                        iView.onNetWorkAfter();
                    }
                });

    }


}
