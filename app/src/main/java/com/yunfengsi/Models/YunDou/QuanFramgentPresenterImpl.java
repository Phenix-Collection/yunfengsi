package com.yunfengsi.Models.YunDou;

import android.support.v4.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
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
public class QuanFramgentPresenterImpl implements DuiHuanContract.IFPresenter, SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {
    private int pageSize = 20;
    private int page = 1;
    private int endPage = -1;
    private boolean isLoadMore = false;
    private boolean isRefresh = false;


    DuiHuanContract.IFView iView;

    public QuanFramgentPresenterImpl(DuiHuanContract.IFView iView) {
        WeakReference<DuiHuanContract.IFView> weakReference = new WeakReference<DuiHuanContract.IFView>(iView);
        this.iView = weakReference.get();
    }

    @Override
    public void getDuiHuanList() {
        if(!Network.HttpTest(mApplication.getInstance())){
            return;
        }
        JSONObject js = new JSONObject();
        try {
            js.put("m_id", Constants.M_id);
            js.put("page", page);
            js.put("sort",iView.getSortId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m = ApisSeUtil.i(js);
        LogUtil.e("获取兑换中心列表"+js);
        OkGo.post(Constants.ExchangeList).params("key", m.K())
                .params("msg", m.M())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        final ArrayList<HashMap<String, String>> list = AnalyticalJSON.getList_zj(AnalyticalJSON.getHashMap(s).get("msg"));
                        if (list != null) {
                            if (isRefresh) {
                                iView.getAdapter().setNewData(list);
                                isRefresh = false;
                                iView.hideSwip();
                            } else if (isLoadMore) {
                                isLoadMore = false;
                                if (list.size() < pageSize) {
                                    endPage = page;
                                    iView.getAdapter().addData(list);
                                    iView.getAdapter().loadMoreEnd(true);
                                } else {
                                    iView.getAdapter().loadMoreComplete();
                                    iView.getAdapter().addData(list);
                                }
                            }
                        }
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);

                    }

                    @Override
                    public void onAfter(String s, Exception e) {
                        super.onAfter(s, e);

                        iView.hideSwip();
                    }
                });
    }




    @Override
    public void onRefresh() {
        page = 1;
        isRefresh = true;
        iView.getAdapter().setEnableLoadMore(true);
        getDuiHuanList();
    }

    @Override
    public void onLoadMoreRequested() {
        LogUtil.e("加载更多兑换券");
        if (endPage != page) {
            isLoadMore = true;
            page++;
            getDuiHuanList();
        }
    }
}
