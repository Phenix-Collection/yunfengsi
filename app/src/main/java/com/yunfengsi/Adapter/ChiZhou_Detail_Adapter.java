package com.yunfengsi.Adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yunfengsi.R;
import com.yunfengsi.Utils.NumUtils;
import com.yunfengsi.Utils.mApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/6/1.
 */
public class ChiZhou_Detail_Adapter extends BaseAdapter {
    public List<HashMap<String, String>> mlist;
    private Context context;

    public ChiZhou_Detail_Adapter(Context context) {
        this.context = context;
        mlist = new ArrayList<>();
    }

    public void addList(List<HashMap<String, String>> mlist) {
        this.mlist = mlist;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public Object getItem(int position) {
        return mlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder = null;
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.niaofo_detail_list_item, parent, false);

            holder.title = view.findViewById(R.id.nianfodetail_list_item_names);
            holder.num = view.findViewById(R.id.nianfodetail_list_item_num);


            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        HashMap<String, String> bean = mlist.get(position);

        holder.title.setText(mApplication.ST(bean.get("ja_name")));

        SpannableString ss=new SpannableString(mApplication.ST(NumUtils.getNumStr(bean.get("ja_num"))+"遍，"+bean.get("ja_actor")+"人参与"));
        ss.setSpan(new AbsoluteSizeSpan(20,true),0,bean.get("ja_num").length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new AbsoluteSizeSpan(20,true),ss.length()-bean.get("ja_actor").length()-3,ss.length()-3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.num.setText(ss);

        return view;
    }

    static class ViewHolder {

        TextView title;
        TextView num;

    }
}
