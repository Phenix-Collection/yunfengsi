package com.yunfengsi.Models.NianFo;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yunfengsi.R;
import com.yunfengsi.Utils.TimeUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/8/20.
 */
public class MyZuNian_ListView_Adapter extends BaseAdapter {
    private Context context;
    private String type;
    private Hoder hoder;
    ArrayList<HashMap<String,String>> list;
    //private String[] text={"1","2","3","1","2","3"};
    public MyZuNian_ListView_Adapter(Context context,ArrayList<HashMap<String,String>> list){
        this.list=list;
        this.context=context;
    }
    @Override
    public int getCount() {
        return list.size();
    }
    @Override
    public Object getItem(int i) {
        return list.get(i);
    }
    @Override
    public long getItemId(int i) {
        return i;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view==null){
            hoder=new Hoder();
            view= LayoutInflater.from(context).inflate(R.layout.list,null);
            hoder.mlinearLayout=((LinearLayout) view.findViewById(R.id.myzunian_layout));
            view.setTag(hoder);
        }else {
            hoder=(Hoder) view.getTag();
        }
        hoder.mlinearLayout.removeAllViews();
                View view1 = LayoutInflater.from(context).inflate(R.layout.list_itme, null);
               TextView tvname = (TextView) view1.findViewById(R.id.tv_myzunian_listitme_name);
               TextView tvtype = (TextView) view1.findViewById(R.id.tv_myzunian_listitme_type);
               TextView tvnum = (TextView) view1.findViewById(R.id.tv_myzunian_listitme_num);
               TextView tvtime=(TextView)view1.findViewById(R.id.tv_myzunian_listitme_time);
                hoder.mlinearLayout.addView(view1);
        tvname.setText(list.get(i).get("pet_name"));
        SpannableString ss=new SpannableString("助念 "+list.get(i).get("ba_name"));
        ss.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context,R.color.main_color)),2,ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvtype.setText(ss);
        tvnum.setText(list.get(i).get("ls_nfnum"));
        tvtime.setText(TimeUtils.getTrueTimeStr(list.get(i).get("ls_time")));

        return view;
    }
    static class Hoder{

        LinearLayout mlinearLayout;
    }

}
