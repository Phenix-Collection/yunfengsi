package com.yunfengsi.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
import com.yunfengsi.Models.NianFo.HuiXiang;
import com.yunfengsi.R;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.Network;
import com.yunfengsi.Utils.NumUtils;
import com.yunfengsi.Utils.TimeUtils;
import com.yunfengsi.Utils.ToastUtil;
import com.yunfengsi.Utils.mApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Carry_lew on 2016/7/29.
 */
public class Nianfo_home_Adaper extends BaseAdapter {

    private Context context;
    public List<HashMap<String, String>> mlist = new ArrayList<>();
    private LayoutInflater inflater;
    private int screenWidth;
    private String type;
    private SharedPreferences sp;

    public Nianfo_home_Adaper(Context context, String type) {
        this.context = context;
        this.mlist = new ArrayList<>();
        inflater = LayoutInflater.from(this.context);
        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        this.type = type;
        sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
    }

    public void addList(List<HashMap<String, String>> list) {
        this.mlist.addAll(list);
    }

    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public Object getItem(int position) {
        return this.mlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (!mlist.get(position).get("id").equals(sp.getString("user_id", ""))) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Holder holder;
        View view = convertView;
        final HashMap<String,String > map=mlist.get(position);
        if (view == null) {
            holder = new Holder();
            view = inflater.inflate(R.layout.nianfo_item, null);
            holder.imageView = (ImageView) view.findViewById(R.id.nianfo_item_ima);
            holder.txt_date = (TextView) view.findViewById(R.id.nianfo_item_date);
            holder.txt_desc = (TextView) view.findViewById(R.id.nianfo_item_desc);
            holder.txt_name = (TextView) view.findViewById(R.id.nianfo_item_name);
            holder.txt_share= (TextView) view.findViewById(R.id.nianfo_item_share);
            holder.content= (RelativeLayout) view.findViewById(R.id.content);
            holder.txt_delete= (TextView) view.findViewById(R.id.delete);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        if(getItemViewType(position)==1){
            view.setBackgroundColor(Color.parseColor("#eeeeee"));
            holder.txt_share.setVisibility(View.VISIBLE);
            if(!((SwipeMenuLayout) view).isSwipeEnable()){{
                ((SwipeMenuLayout) view).setSwipeEnable(true);
            }}
            ((SwipeMenuLayout) view).quickClose();

        }else{
            view.setBackgroundColor(Color.WHITE);
            holder.txt_share.setVisibility(View.GONE);
            if(((SwipeMenuLayout) view).isSwipeEnable()){
                ((SwipeMenuLayout) view).quickClose();
                ((SwipeMenuLayout) view).setSwipeEnable(false);
            }

        }
        Glide.with(context).load(mlist.get(position).get("user_image")).override(screenWidth * 3 / 20, screenWidth * 3 / 20)
                .centerCrop().into(holder.imageView);
        if (type.equals("念佛")) {

            holder.txt_name.setText(mlist.get(position).get("pet_name").equals("") ? mApplication.ST("佚名") : mlist.get(position).get("pet_name"));
            SpannableString ss=new SpannableString(mApplication.ST(type + "--念 " + mlist.get(position).get("ba_name") + " " + NumUtils.getNumStr( mlist.get(position).get("ls_nfnum") )+ " 声"));
            ss.setSpan(new AbsoluteSizeSpan(17,true),ss.length()-map.get("ls_nfnum").length()-2,ss.length()-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.txt_desc.setText(ss);
            holder.txt_date.setText(TimeUtils.getTrueTimeStr(mlist.get(position).get("ls_time")));

        } else if (type.equals("诵经")) {

            holder.txt_name.setText(mlist.get(position).get("pet_name").equals("") ? mApplication.ST("佚名") : mlist.get(position).get("pet_name"));
            SpannableString ss=new SpannableString(mApplication.ST(type + "-- " + mlist.get(position).get("rg_name") + " " + NumUtils.getNumStr( mlist.get(position).get("ls_nfnum") )+ " 部"));
            ss.setSpan(new AbsoluteSizeSpan(17,true),ss.length()-map.get("ls_nfnum").length()-2,ss.length()-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.txt_desc.setText(ss);
            holder.txt_date.setText(TimeUtils.getTrueTimeStr(mlist.get(position).get("ls_time")));
        } else if (type.equals("持咒")) {

            holder.txt_name.setText(mlist.get(position).get("pet_name").equals("") ? mApplication.ST("佚名") : mlist.get(position).get("pet_name"));
            SpannableString ss=new SpannableString(mApplication.ST(type + "-- " + mlist.get(position).get("ja_name") + " " + NumUtils.getNumStr( mlist.get(position).get("ls_nfnum") ) + " 遍"));
            ss.setSpan(new AbsoluteSizeSpan(17,true),ss.length()-map.get("ls_nfnum").length()-2,ss.length()-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.txt_desc.setText(ss);
            holder.txt_date.setText(TimeUtils.getTrueTimeStr(mlist.get(position).get("ls_time")));
        }
        holder.txt_name.setTag(mlist.get(position).get("id"));

        if (holder.txt_share != null) {
            holder.txt_share.setTag(mlist.get(position).get("nf_id"));
            holder.txt_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, HuiXiang.class);
                    switch (type) {
                        case "念佛":
                            intent.putExtra("status", 1);
                            intent.putExtra("name",map.get("ba_name"));
                            intent.putExtra("num",map.get("ls_nfnum"));
                            break;
                        case "诵经":
                            intent.putExtra("status", 2);
                            intent.putExtra("name",map.get("rg_name"));
                            intent.putExtra("num",map.get("ls_nfnum"));
                            break;
                        case "持咒":
                            intent.putExtra("status", 3);
                            intent.putExtra("name",map.get("ja_name"));
                            intent.putExtra("num",map.get("ls_nfnum"));
                            break;
                    }
                    intent.putExtra("nf_id", v.getTag().toString());
                    context.startActivity(intent);
                }
            });
            
            
        }

        final View finalView = view;
        holder.txt_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog .Builder builder=new AlertDialog.Builder(context);

                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //删除功课
                        if(!Network.HttpTest(context)){
                            return;
                        }
                        JSONObject js=new JSONObject();
                        try {
                            js.put("m_id", Constants.M_id);
                            js.put("user_id",map.get("id"));
                            js.put("id",map.get("nf_id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ApisSeUtil.M m=ApisSeUtil.i(js);
                        LogUtil.e("功课删除：：："+js);
                        OkGo.post(Constants.Buddha_Delete).params("key",m.K())
                                .params("msg",m.M())
                                .execute(new StringCallback() {
                                    @Override
                                    public void onSuccess(String s, Call call, Response response) {
                                        HashMap<String,String > m= AnalyticalJSON.getHashMap(s);
                                        if(m!=null){
                                            if("000".equals(m.get("code"))){
                                                ToastUtil.showToastShort("删除成功");
                                                ((SwipeMenuLayout) finalView).quickClose();
                                                mlist.remove(map);
                                                notifyDataSetChanged();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onError(Call call, Response response, Exception e) {
                                        super.onError(call, response, e);
                                        ToastUtil.showToastShort("删除失败，请稍后重试");
                                    }
                                });
                    }
                });
                AlertDialog alertDialog=builder.create();
                alertDialog.setMessage("确认删除该条信息吗？");
                alertDialog.show();

            }
        });
        return view;
    }



    static class Holder {
        ImageView imageView;
        TextView txt_name;
        TextView txt_desc;
        TextView txt_date;
        TextView txt_share;
        TextView txt_delete;
        RelativeLayout content;
    }
}
