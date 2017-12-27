package com.maimaizu.Adapter;

import android.content.Context;
import android.view.View;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.maimaizu.R;
import com.maimaizu.Utils.AnalyticalJSON;
import com.maimaizu.Utils.Constants;
import com.maimaizu.Utils.PreferenceUtil;

import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/5/8.
 */

public class mFangYuanAdapter extends BaseItemDraggableAdapter {
    private Context context;
    public mFangYuanAdapter(Context context,int layoutResId, List data) {
        super(layoutResId, data);
        this.context=context;
    }

    @Override
    protected void convert(BaseViewHolder holder, Object o) {
        final HashMap<String ,String > map= (HashMap<String, String>) o;
        holder.setText(R.id.name,"称       呼:  "+map.get("call"))
                .setText(R.id.phone,"联系方式:  "+map.get("phone"))
                .setText(R.id.money,map.get("type").equals("2")?("期望租金:  "+map.get("money")+"美元/月"):("期望售价:  "+map.get("money")+"美元"))
        .setText(R.id.village,"小区名称:  "+map.get("village"))
        .setText(R.id.address,"详细地址:  "+map.get("address"))
        .setText(R.id.type,map.get("type").equals("2")?"租房":"卖房")
        .setOnClickListener(R.id.delete, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OkGo.post(Constants.FangYuanDelete).params("key", Constants.safeKey)
                        .params("m_id",Constants.M_id)
                        .params("user_id", PreferenceUtil.getUserIncetance(context).getString("user_id",""))
                        .params("id",map.get("id"))
                        .execute(new AbsCallback<HashMap<String,String>>() {
                            @Override
                            public void onSuccess(HashMap<String, String> m, Call call, Response response) {
                                if(m!=null){
                                    if("000".equals(m.get("code"))){
                                        int i=getData().indexOf(map);
                                        getData().remove(map);
                                        notifyItemRemoved(i);
                                    }
                                }

                            }

                            @Override
                            public HashMap<String, String> convertSuccess(Response response) throws Exception {
                                return AnalyticalJSON.getHashMap(response.body().string());
                            }
                        });
            }
        });
        switch (map.get("status")){
            case "0":
                holder.setText(R.id.status,"待审核");
                break;
            case "1":
                holder.setText(R.id.status,"已拒绝");
                break;
            case "2":
                holder.setText(R.id.status,"已通过");
                break;
        }

    }




}
