package com.yunfengsi.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
import com.yunfengsi.NianFo.HuiXiang;
import com.yunfengsi.NianFo.MyZuNianActivity;
import com.yunfengsi.R;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.Network;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.ProgressUtil;
import com.yunfengsi.Utils.TimeUtils;
import com.yunfengsi.Utils.ToastUtil;
import com.yunfengsi.Utils.mApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/8/2.
 */
public class nianfo_home_zhunian_adapter extends BaseAdapter {
    public List<HashMap<String, String>> list;
    private Context context;
    private SharedPreferences sp;
    private int screenWidth;
    public List<HashMap<String, String>> list1;
    private static final String TAG = "chanhui_adapter";
    private ArrayList<HashMap<String, String>> typelist;
    public ArrayList<Boolean> sba;
    public InputMethodManager imm;

    public nianfo_home_zhunian_adapter(Context context) {
        super();
        this.context = context;
        list = new ArrayList<>();
        list1 = new ArrayList<>();
        sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
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
        // TODO: 2017/8/30  助念点击后修改
        if (list.get(position).get("rtg_userid").equals(sp.getString("user_id", "")) || !sba.get(position)) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        viewHolder holder;
//        View view = convertView;
        final HashMap<String, String> map = list.get(position);
//        if (view == null) {
        holder = new viewHolder();

        View view = LayoutInflater.from(context).inflate(R.layout.nianfo_home_zhunian_item, parent, false);
        holder.container= (RelativeLayout) view.findViewById(R.id.content);
        holder.time = (TextView) view.findViewById(R.id.nianfo_home_chanhui_item_time);
        holder.username = (TextView) view.findViewById(R.id.nianfo_home_chanhui_item_username);
        holder.head = (ImageView) view.findViewById(R.id.nianfo_home_chanhui_item_head);
        holder.content = (TextView) view.findViewById(R.id.nianfo_home_chanhui_item_content);
        holder.tip = (TextView) view.findViewById(R.id.nianfo_home_chanhui_item_tip);
        holder.dianzan = (TextView) view.findViewById(R.id.nianfo_home_chanhui_item_dianzan);
        holder.swipeview = (SwipeMenuLayout) view.findViewById(R.id.swipview);
        holder.text_delete = (TextView) view.findViewById(R.id.delete);
//            view.setTag(holder);
//        } else {
//            holder = (viewHolder) view.getTag();
//        }
        Glide.with(context).load(map.get("user_image"))
                .placeholder(R.color.huise)
                .override(DimenUtils.dip2px(context, 50), DimenUtils.dip2px(context, 50)).diskCacheStrategy(DiskCacheStrategy.RESULT)
                .centerCrop().into(holder.head);
        holder.username.setText(map.get("pet_name").equals("") ? mApplication.ST("佚名") : map.get("pet_name"));
        holder.time.setText(TimeUtils.getTrueTimeStr(map.get("rtg_time")));
        holder.content.setText(mApplication.ST(map.get("rtg_contents")));
        if (map.get("rtg_likes") == null || map.get("rtg_likes").equals("0")) {
            holder.tip.setText(mApplication.ST("暂无助念"));
        } else {
            SpannableString ss = new SpannableString(mApplication.ST("助念人数: " + map.get("rtg_likes") + " 人"));
            ss.setSpan(new AbsoluteSizeSpan(17, true), ss.length() - map.get("rtg_likes").length() - 2, ss.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.tip.setText(ss);

        }
        // TODO: 2017/8/30  助念点击后修改
        if (map.get("head") != null && !map.get("head").equals("")) {
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

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,
                        MyZuNianActivity.class);
                intent.putExtra("id", map.get("id"));
                context.startActivity(intent);
            }
        });
        if ((holder.swipeview.isSwipeEnable())) {
            holder.swipeview.quickClose();
            holder.text_delete.setVisibility(View.GONE);
            holder.swipeview.setSwipeEnable(false);
        }
        if (getItemViewType(position) == 0) {
            holder.dianzan.setText(mApplication.ST("为他助念"));
            view.setBackgroundColor(Color.parseColor("#ffffff"));
        } else {
            holder.dianzan.setText(mApplication.ST("邀请同修来助念"));
            if (map.get("rtg_userid").equals(PreferenceUtil.getUserIncetance(context).getString("user_id", ""))) {
                view.setBackgroundColor(Color.parseColor("#eeeeee"));
                if (!((SwipeMenuLayout) view).isSwipeEnable()) {
                    {
                        ((SwipeMenuLayout) view).setSwipeEnable(true);
                    }
                }
                ((SwipeMenuLayout) view).quickClose();
                holder.text_delete.setVisibility(View.VISIBLE);
            } else {
                view.setBackgroundColor(Color.parseColor("#ffffff"));
            }
        }
        // TODO: 2017/8/30  助念点击后修改
        holder.dianzan.setTag(position);
        holder.tip.setTag(map.get("id"));
        holder.dianzan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final int p = (int) v.getTag();
                if (getItemViewType(p) == 1) {
                    Intent intent = new Intent(context, HuiXiang.class);
                    intent.putExtra("status", 4);
                    intent.putExtra("content", list.get(p).get("rtg_contents"));
                    intent.putExtra("pet_name", list.get(p).get("pet_name"));
                    intent.putExtra("nf_id", list.get(p).get("id"));
                    context.startActivity(intent);
                    return;
                }
                final View view1 = parent.getChildAt(p - ((ListView) parent).getFirstVisiblePosition());
                final TextView tip = (TextView) view1.findViewById(R.id.nianfo_home_chanhui_item_tip);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                final View view = LayoutInflater.from(context).inflate(R.layout.alert_view_nianfo_zhunian, null);
                final EditText name = (EditText) view.findViewById(R.id.zhunian_alert_name);
                name.setText(list.get(p).get("pet_name").equals("") ? mApplication.ST("佚名") : list.get(p).get("pet_name"));
                final EditText fohao = (EditText) view.findViewById(R.id.zhunian_alert_fohao);
                fohao.setFocusable(false);
                final EditText num = (EditText) view.findViewById(R.id.zhunian_alert_num);
                TextView commit = (TextView) view.findViewById(R.id.zhunian_alert_commit);
                builder.setView(view);
                final AlertDialog dialog = builder.create();
                dialog.show();
                WindowManager.LayoutParams wl = dialog.getWindow().getAttributes();
                wl.width = context.getResources().getDisplayMetrics().widthPixels;

                dialog.getWindow().setAttributes(wl);
                fohao.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View view = LayoutInflater.from(context).inflate(R.layout.nianfo_type_dialog, null);
                        final ListView list = (ListView) view.findViewById(R.id.nianfo_type_listview);
                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
                        builder.setView(view);
                        final android.support.v7.app.AlertDialog dialog1 = builder.create();
                        Window window = dialog1.getWindow();
                        window.getDecorView().setPadding(0, 0, 0, 0);
                        window.setGravity(Gravity.CENTER);
                        WindowManager.LayoutParams wl = window.getAttributes();

                        wl.height = WindowManager.LayoutParams.WRAP_CONTENT;
                        wl.width = context.getResources().getDisplayMetrics().widthPixels * 75 / 100;
                        window.setAttributes(wl);
                        dialog1.show();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String data = OkGo.post(Constants.nianfo_home_zhunian_fohao_Ip).execute().body().string();
                                    if (!data.equals("")) {
                                        Log.w(TAG, "run: " + data);
                                        typelist = AnalyticalJSON.getList(mApplication.ST(data), "buddha");
                                        if (typelist != null) {
                                            ((Activity) context).runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ArrayAdapter adapter = new mArrayAdapter(context, R.layout.nianfo_home_dialog_item, R.id.nianfo_home_dialog_item_text, typelist, "念佛");
                                                    list.setAdapter(adapter);
                                                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                        @Override
                                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                            fohao.setText(((TextView) view).getText().toString());
                                                            fohao.setTag((view).getTag());
                                                            dialog1.dismiss();
                                                        }
                                                    });
                                                }
                                            });

                                        }
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                });
                commit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        if (name.getText().toString().trim().equals("") || fohao.getText().toString().trim().equals("") || num.getText().toString().trim().equals("")) {
                            Toast.makeText(context, mApplication.ST("请填写完整信息"), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        view.setEnabled(false);
                        ProgressUtil.show(context, "", "请稍等");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject js = new JSONObject();
                                    js.put("user_id", sp.getString("user_id", ""));
                                    js.put("gongke_id", (String) fohao.getTag());
                                    js.put("num", num.getText().toString());
                                    js.put("recit_id", tip.getTag().toString());
                                    String data = OkGo.post(Constants.nianfo_home_zhunian_ZN_Ip)
                                            .params("key", ApisSeUtil.getKey())
                                            .params("msg", ApisSeUtil.getMsg(js)).execute().body().string();
                                    if (!data.equals("")) {
                                        final HashMap<String, String> map = AnalyticalJSON.getHashMap(data);
                                        if (map != null && "000".equals(map.get("code"))) {
                                            ((Activity) context).runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (sba.get(p)) {
                                                        sba.set(p, false);
                                                        String newNum = String.valueOf(Integer.valueOf(list.get(p).get("rtg_likes")) + 1);
                                                        list.get(p).put("rtg_likes", newNum);
                                                        tip.setText(mApplication.ST("助念人数: " + newNum + " 人"));
                                                        try {
                                                            JSONArray jsonArray = new JSONArray(list.get(p).get("head"));
                                                            JSONObject js = new JSONObject();
                                                            js.put("id", PreferenceUtil.getUserIncetance(context).getString("user_id", ""));
                                                            jsonArray.put(js);
                                                            list.get(p).put("head", jsonArray.toString());
                                                            LogUtil.e("助念后本地修改数据：：" + list.get(p).get("head"));
                                                            ((TextView) v).setText(mApplication.ST("邀请同修来助念"));
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                    imm.hideSoftInputFromWindow(num.getWindowToken(), 0);
                                                    dialog.dismiss();
                                                    Toast.makeText(context, mApplication.ST("信息提交成功"), Toast.LENGTH_SHORT).show();
                                                    view.setEnabled(true);

                                                }
                                            });
                                        }
                                    }

                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } finally {
                                    ((Activity) context).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ProgressUtil.dismiss();
                                        }
                                    });
                                }
                            }
                        }).start();
                    }
                });
            }
        });
        final View finalView = view;
        holder.text_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //删除功课
                        if (!Network.HttpTest(context)) {
                            return;
                        }
                        JSONObject js = new JSONObject();
                        try {
                            js.put("m_id", Constants.M_id);
                            js.put("user_id", map.get("rtg_userid"));
                            js.put("id", map.get("id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ApisSeUtil.M m = ApisSeUtil.i(js);
                        LogUtil.e("助念删除：：：" + js);
                        OkGo.post(Constants.Reciting_Delete).params("key", m.K())
                                .params("msg", m.M())
                                .execute(new StringCallback() {
                                    @Override
                                    public void onSuccess(String s, Call call, Response response) {
                                        HashMap<String, String> m = AnalyticalJSON.getHashMap(s);
                                        if (m != null) {
                                            if ("000".equals(m.get("code"))) {
                                                ToastUtil.showToastShort("删除成功");
                                                ((SwipeMenuLayout) finalView.findViewById(R.id.swipview)).quickClose();
                                                list.remove(map);
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
                AlertDialog alertDialog = builder.create();
                alertDialog.setMessage("确认删除该条忏悔吗？");
                alertDialog.show();

            }
        });
        return view;
    }

    static class viewHolder {
        TextView username, time, content, tip, dianzan, text_delete;
        ImageView head;
        SwipeMenuLayout swipeview;
        RelativeLayout container;
    }
}
