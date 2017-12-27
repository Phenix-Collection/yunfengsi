package com.yunfengsi.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lzy.okgo.OkGo;
import com.yunfengsi.NianFo.HuiXiang;
import com.yunfengsi.R;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.ProgressUtil;
import com.yunfengsi.Utils.TimeUtils;
import com.yunfengsi.Utils.ToastUtil;
import com.yunfengsi.Utils.mApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/8/2.
 */
public class nianfo_home_chanhui_adapter extends BaseAdapter {
    public List<HashMap<String, String>> list;
    private Context context;
    private SharedPreferences sp;
    private int screenWidth;
    public List<HashMap<String, String>> list1;
    private int currentIndex;
    viewHolder holder;
    public ArrayList<Boolean> sba;
    private static final String TAG = "chanhui_adapter";

    public nianfo_home_chanhui_adapter(Context context) {
        super();
        this.context = context;
        list = new ArrayList<>();
        sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        sp = PreferenceUtil.getUserIncetance(context);
    }

    public void addList(List<HashMap<String, String>> list) {
        this.list = list;
        sba = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            sba.add(i, true);
        }

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).get("user_id").equals(sp.getString("user_id", ""))||!sba.get(position)) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public View getView(int position, final View convertView, final ViewGroup parent) {

        View view = convertView;
        currentIndex = position;
        Log.w(TAG, "getView:  positiong-=-=-=-=-=-=>" + position);
         HashMap<String, String> map = list.get(position);
        if (view == null) {
            holder = new viewHolder();

            view = LayoutInflater.from(context).inflate(R.layout.nianfo_home_chanhui_item, parent, false);
            holder.time = (TextView) view.findViewById(R.id.nianfo_home_chanhui_item_time);
            holder.username = (TextView) view.findViewById(R.id.nianfo_home_chanhui_item_username);
            holder.head = (ImageView) view.findViewById(R.id.nianfo_home_chanhui_item_head);
            holder.content = (TextView) view.findViewById(R.id.nianfo_home_chanhui_item_content);
            holder.tip = (TextView) view.findViewById(R.id.nianfo_home_chanhui_item_tip);
            holder.dianzan = (TextView) view.findViewById(R.id.nianfo_home_chanhui_item_dianzan);
            LogUtil.e(map.get("user_id") + "  !!@!@!@!@!@!@!   " + sp.getString("user_id", ""));
            LogUtil.e(getItemViewType(position) + "");



            holder.dianzan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    v.setEnabled(false);
                    final int p = (int) v.getTag();
                    if (getItemViewType(p) == 1 || mApplication.ST("邀请同修来随喜").equals(((TextView) v).getText().toString())) {
                        Intent intent = new Intent(context, HuiXiang.class);
                        intent.putExtra("status", 5);
                        intent.putExtra("content",list.get(p).get("cfs_contents"));
                        intent.putExtra("pet_name",list.get(p).get("pet_name"));
                        intent.putExtra("nf_id", list.get(p).get("id"));
                        context.startActivity(intent);
                        v.setEnabled(true);
                        return;
                    }
                    final View view1 = parent.getChildAt(p - ((ListView) parent).getFirstVisiblePosition());
                    final TextView tip = (TextView) view1.findViewById(R.id.nianfo_home_chanhui_item_tip);
                    ProgressUtil.show(context,"","请稍等");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject js = new JSONObject();
                                js.put("chanhui_id", tip.getTag().toString());
                                js.put("user_id", sp.getString("user_id", ""));
                                String data1 = OkGo.post(Constants.nianfo_home_chanhui_Dianzan_Ip)
                                        .params("key", ApisSeUtil.getKey())
                                        .params("msg", ApisSeUtil.getMsg(js)).execute().body().string();
                                if (!data1.equals("")) {
                                    HashMap<String, String> map1 = AnalyticalJSON.getHashMap(data1);
                                    if (map1 == null) {
                                        ((Activity) context).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                v.setEnabled(true);
                                                Toast.makeText(context, mApplication.ST("网络异常，请重新操作"), Toast.LENGTH_SHORT).show();
                                                ProgressUtil.dismiss();
                                            }
                                        });
                                    } else {
                                        if (("000").equals(map1.get("code") == null ? "" : map1.get("code"))) {
                                            ((Activity) context).runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
ProgressUtil.dismiss();
                                                    String newNUM = String.valueOf(Integer.valueOf(list.get(p).get("cfs_likes")) + 1);

                                                    list.get(p).put("cfs_likes", newNUM);
                                                    ((TextView) view1.findViewById(R.id.nianfo_home_chanhui_item_tip)).setText(mApplication.ST("随喜人数: " + newNUM + " 人"));
                                                    v.setEnabled(true);
                                                    sba.set(p,false);
                                                    try {
                                                        JSONArray jsonArray=new JSONArray(list.get(p).get("head"));
                                                        JSONObject js=new JSONObject();
                                                        js.put("id",PreferenceUtil.getUserIncetance(context).getString("user_id",""));
                                                        jsonArray.put(js);
                                                        list.get(p).put("head",jsonArray.toString());
                                                        LogUtil.e("忏悔随喜后本地修改数据：："+list.get(p).get("head"));
                                                        ((TextView) v).setText(mApplication.ST("邀请同修来随喜"));
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }

                                                }

                                            });
                                        } else if ("003".equals(map1.get("code"))) {
                                            ((Activity) context).runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ProgressUtil.dismiss();
                                                    ToastUtil.showToastShort(mApplication.ST("已随喜过了"), Gravity.CENTER);
                                                }
                                            });
                                        }
                                    }
                                } else {
                                    ((Activity) context).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            v.setEnabled(true);
                                            ProgressUtil.dismiss();
                                        }
                                    });
                                }


                            } catch (Exception e) {
                                e.printStackTrace();
                                v.setEnabled(true);
                            }
                        }
                    }).start();

                }
            });
            view.setTag(holder);
        } else {
            holder = (viewHolder) view.getTag();
        }
        if (map.get("head") != null) {
            list1 = AnalyticalJSON.getList_zj(list.get(position).get("head"));
            if (list1 != null && list1.size() != 0) {
                for (int i = 0; i < list1.size(); i++) {
                    if (list1.get(i).get("id").equals(sp.getString("user_id", ""))) {
                        sba.set(position, false);
                        break;
                    }
                }
            }
        }
        if (getItemViewType(position) == 1) {
            holder.dianzan.setText(mApplication.ST("邀请同修来随喜"));
            if(map.get("user_id").equals(PreferenceUtil.getUserIncetance(context).getString("user_id",""))){
                view.setBackgroundColor(Color.parseColor("#eeeeee"));
            }else{
                view.setBackgroundColor(Color.parseColor("#ffffff"));
            }
        } else {
            holder.dianzan.setText(mApplication.ST("为他随喜"));
            view.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        Glide.with(context).load(map.get("user_image")).centerCrop().override(DimenUtils.dip2px(context,50), DimenUtils.dip2px(context,50)).diskCacheStrategy(DiskCacheStrategy.SOURCE).centerCrop().into(holder.head);
        holder.username.setText(map.get("pet_name").equals("") ? mApplication.ST("佚名") : map.get("pet_name"));
        holder.time.setText(TimeUtils.getTrueTimeStr(map.get("cfs_time")));
        holder.content.setText(mApplication.ST(map.get("cfs_contents")));
        if (map.get("cfs_likes") == null || map.get("cfs_likes").equals("0")) {
            holder.tip.setText(mApplication.ST("暂无人随喜"));
        } else {
            SpannableString ss=new SpannableString(mApplication.ST("随喜人数: " + map.get("cfs_likes") + " 人"));
            ss.setSpan(new AbsoluteSizeSpan(17,true),ss.length()-map.get("cfs_likes").length()-2,ss.length()-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.tip.setText(ss);
        }
        holder.tip.setTag(map.get("id"));
        holder.dianzan.setTag(position);
        holder.dianzan.setEnabled(true);

        return view;
    }

    static class viewHolder {
        TextView username, time, content, tip, dianzan;
        ImageView head;
    }
}
