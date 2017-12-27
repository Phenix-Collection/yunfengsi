package com.yunfengsi.NianFo;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.yunfengsi.Adapter.ChiZhou_Detail_Adapter;
import com.yunfengsi.R;
import com.yunfengsi.Utils.ACache;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.Network;
import com.yunfengsi.Utils.ProgressUtil;
import com.yunfengsi.Utils.mApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.view.View.GONE;

//import com.letv.recorder.util.NetworkUtils;

/**
 * Created by Administrator on 2016/8/4.
 */
public class Chizhou_Detail_Fragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "ChiZhou";
    private ListView listView;
    public List<HashMap<String, String>> dataList;
    private String page = "1";
    private ChiZhou_Detail_Adapter adapter;
    private ACache aCache;
    private static final String ChiZhoulist = "ChiZhou_list";
    private ImageView loading;//////
    Handler handler = new Handler();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.niaofo_detail_list, container, false);
        aCache = ACache.get(getActivity());
        initView(view);
        loadData();
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {

        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    public void initView(View view) {
        /////
        adapter = new ChiZhou_Detail_Adapter(getActivity());
        dataList = new ArrayList<>();
        listView = (ListView) view.findViewById(R.id.niaofo_detail_list);
    }

    public void loadData() {//加载数据
        if (!Network.HttpTest(getActivity())) {

            String data = aCache.getAsString(ChiZhoulist);
            setData(data);
            Toast.makeText(mApplication.getInstance(), "无网络连接", Toast.LENGTH_SHORT).show();
            if (loading.isShown()) {
                loading.clearAnimation();
                loading.setVisibility(GONE);
            }
        }

        getData4ZXandCache();
    }

    private void getData4ZXandCache() {
        ProgressUtil.show(getActivity(),"","请稍等");
        new Thread(new Runnable() {
            @Override
            public void run() {
                String data1 = null;
                try {
                    JSONObject js=new JSONObject();
                    try {
                        js.put("page",page);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    data1 = OkGo.post(Constants.ChiZhou_detail_Ip).tag(TAG)
                            .params("key", ApisSeUtil.getKey())
                            .params("msg", ApisSeUtil.getMsg(js)).execute().body().string();
                    Log.e(TAG, "run: data1----?>" + data1);
                    setData(data1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void setData(String data1) {
        dataList= AnalyticalJSON.getList_zj(data1);
        adapter.addList(dataList);

        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ProgressUtil.dismiss();
                        listView.setAdapter(adapter);
                    }
                });
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        OkGo.getInstance().cancelTag(TAG);
    }
}
