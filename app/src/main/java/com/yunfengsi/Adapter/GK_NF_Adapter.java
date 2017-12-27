package com.yunfengsi.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yunfengsi.R;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.TimeUtils;
import com.yunfengsi.Utils.mApplication;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/8/5.
 */
public class GK_NF_Adapter extends BaseAdapter{

    private Context context;
    private List<String> keyList;
    private String type;
    private Hoder hoder;
    private List<String> valueList;
    public GK_NF_Adapter(Context context, List<String> list,String type){
        this.context=context;
        this.keyList =list;
        this.type=type;
        valueList=new ArrayList<>();
    }
    public  void setValueList(List<String> List){
        this.valueList=List;
    }
    @Override
    public int getCount() {
        return keyList.size();
    }
    @Override
    public Object getItem(int i) {
        return keyList.get(i);
    }
    @Override
    public long getItemId(int i) {
        return i;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view==null){
            hoder=new Hoder();
            view= LayoutInflater.from(context).inflate(R.layout.mine_gk_lf_itme,null);
            hoder.mtvtime=(TextView) view.findViewById(R.id.mine_gk_lf_itme_tvtime);
            hoder.mlinearLayout=((LinearLayout) view.findViewById(R.id.layout));
            view.setTag(hoder);
        }else {
            hoder=(Hoder) view.getTag();
        }
        hoder.mlinearLayout.removeAllViews();
        String data=valueList.get(i);
            for (HashMap<String, String> map : AnalyticalJSON.getList_zj(data)) {
                View view1 = LayoutInflater.from(context).inflate(R.layout.gk_itme_itme, null);
                TextView tvnj = (TextView) view1.findViewById(R.id.itme_tvnianjingname);
                TextView tvs = (TextView) view1.findViewById(R.id.itme_tvnianjingnum);
                TextView tvtime = (TextView) view1.findViewById(R.id.itme_tvnianjingtime);
                switch (type) {
                    case "念佛":
                        tvnj.setText(mApplication.ST(map.get("ba_name")));
                        tvs.setText(mApplication.ST(map.get("ls_nfnum")+"声"));
                        break;
                    case "诵经":
                        tvnj.setText(mApplication.ST(map.get("rg_name")));
                        tvs.setText(mApplication.ST(map.get("ls_nfnum")+"部"));
                        break;
                    case "持咒":
                        tvnj.setText(mApplication.ST(map.get("ja_name")));
                        tvs.setText(mApplication.ST(map.get("ls_nfnum")+"遍"));
                        break;
                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-HH-dd HH:mm:ss");
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                tvtime.setText(mApplication.ST(TimeUtils.getStrTime_spe(sdf, map.get("ls_time"), simpleDateFormat)));

                hoder.mlinearLayout.addView(view1);
            }




        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat formatter2=new SimpleDateFormat("yyyy年MM月dd日");
        hoder.mtvtime.setText(mApplication.ST(TimeUtils.getStrTime_spe(sdf,keyList.get(i),formatter2)));
        return view;
    }
   static class Hoder{
        TextView mtvtime;
        LinearLayout mlinearLayout;
    }
}
