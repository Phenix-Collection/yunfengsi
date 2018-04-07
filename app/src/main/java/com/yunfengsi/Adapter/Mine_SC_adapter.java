package com.yunfengsi.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
import com.yunfengsi.Model_activity.activity_Detail;
import com.yunfengsi.Model_zhongchou.FundingDetailActivity;
import com.yunfengsi.R;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.LoginUtil;
import com.yunfengsi.Utils.Network;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.TimeUtils;
import com.yunfengsi.Utils.mApplication;
import com.yunfengsi.ZiXun_Detail;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/7/21.
 */
public class Mine_SC_adapter extends BaseAdapter {
    public List<HashMap<String, String>> list;
    public Activity context;
    public LayoutInflater inflater;
    private int screenWidth;

    public Mine_SC_adapter(Activity context) {
        super();
        this.context = context;
        inflater = LayoutInflater.from(context);
        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        list = new ArrayList<>();
    }

    public void addList(List<HashMap<String, String>> list) {
        this.list = list;

    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        View view = convertView;
        HashMap<String, String> map = list.get(position);
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.mine_shoucang_item, parent, false);
            holder.image = (ImageView) view.findViewById(R.id.mine_shoucang_item_image);
            holder.title = (TextView) view.findViewById(R.id.mine_shoucang_item_title);
            holder.time = (TextView) view.findViewById(R.id.mine_shoucang_item_time);
            holder.user = (TextView) view.findViewById(R.id.mine_shoucang_item_user);
            holder.type = (TextView) view.findViewById(R.id.mine_shoucang_item_type);
            holder.delete = view.findViewById(R.id.delete);
            holder.relativeLayout = view.findViewById(R.id.content);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        if (map.get("end_time") == null && map.get("time") != null) {
            holder.time.setText(TimeUtils.getTrueTimeStr(map.get("issue_time")));
            holder.title.setText(mApplication.ST(map.get("title")));
            holder.user.setText(mApplication.ST(map.get("issuer")));
            holder.type.setText(mApplication.ST("图文"));
            holder.delete.setTag(1);
            holder.delete.setTag(R.id.sut_type, map.get("id"));
            holder.delete.setTag(R.id.position, position);
            holder.type.setTag(map.get("id"));
            Glide.with(context).load(map.get("image")).override(screenWidth * 3 / 10, screenWidth * 6 / 25).centerCrop().into(holder.image);
        } else if (map.get("cy_people") == null && map.get("end_time") != null) {
            holder.type.setText(mApplication.ST("活动"));
            holder.type.setTag(map.get("id"));
            holder.user.setText(mApplication.ST(map.get("author")));
            holder.title.setText(mApplication.ST(map.get("title")));
            Glide.with(context).load(map.get("image1")).override(screenWidth * 3 / 10, screenWidth * 6 / 25).centerCrop().into(holder.image);
            long end_time = TimeUtils.dataOne(map.get("end_time"));
            LogUtil.e("结束时间：" + end_time + "  当前时间：" + System.currentTimeMillis());
            if (end_time >= System.currentTimeMillis()) {
                holder.time.setText(mApplication.ST("活动已结束"));
            } else {
                holder.time.setText(mApplication.ST("结束时间：" + TimeUtils.getTrueTimeStr(map.get("end_time"))));
            }
            holder.delete.setTag(2);
            holder.delete.setTag(R.id.sut_type, map.get("id"));
            holder.delete.setTag(R.id.position, position);
        } else if (map.get("cy_people") != null) {
            holder.type.setText(mApplication.ST("助学"));
            holder.type.setTag(map.get("id"));
            holder.user.setText(mApplication.ST("参与人数：" + map.get("cy_people")));
            holder.title.setText(mApplication.ST(map.get("title")));
            Glide.with(context).load(map.get("image")).override(screenWidth * 3 / 10, screenWidth * 6 / 25).centerCrop().into(holder.image);
            holder.time.setText(mApplication.ST("结束时间：" + TimeUtils.getTrueTimeStr(map.get("end_time"))));
            holder.delete.setTag(3);
            holder.delete.setTag(R.id.sut_type, map.get("id"));
            holder.delete.setTag(R.id.position, position);
        }
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView view1 = (TextView) v.findViewById(R.id.mine_shoucang_item_type);
                String id1 = view1.getTag().toString();
                Intent intent = new Intent();

                if (view1.getText().toString().equals(mApplication.ST("图文"))) {
                    intent.setClass(mApplication.getInstance(), ZiXun_Detail.class);
                } else if (view1.getText().toString().equals(mApplication.ST("活动"))) {
                    intent.setClass(mApplication.getInstance(), activity_Detail.class);
                } else if (view1.getText().toString().equals(mApplication.ST("助学"))) {
                    intent.setClass(mApplication.getInstance(), FundingDetailActivity.class);
                }
                intent.putExtra("id", id1);
                context.startActivityForResult(intent, 0);
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String id = (String) v.getTag(R.id.sut_type);
                final int position = (int) v.getTag(R.id.position);
                switch (((int) v.getTag())) {
                    case 1:
                        if (!new LoginUtil().checkLogin(context)) {
                            return;
                        }
                        if (!Network.HttpTest(context)) {
                            Toast.makeText(context, mApplication.ST("请检查网络连接"), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject js = new JSONObject();
                                    try {
                                        js.put("user_id", PreferenceUtil.getUserId(context));
                                        js.put("newsid", id);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    ApisSeUtil.M m = ApisSeUtil.i(js);
                                    String data = OkGo.post(Constants.News_SC_Ip)
                                            .params("key", m.K())
                                            .params("msg", m.M())
                                            .execute().body().string();
                                    if (!data.equals("")) {
                                        if (AnalyticalJSON.getHashMap(data).get("code").equals("002")) {
                                            context.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ((SwipeMenuLayout) holder.delete.getParent()).quickClose();
                                                    list.remove(position);
                                                    notifyDataSetChanged();
                                                }
                                            });
                                        } else {
                                            context.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(context, mApplication.ST("服务器异常"), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                        break;
                    case 2:
                        if (!new LoginUtil().checkLogin(context)) {
                            return;
                        }
                        if (!Network.HttpTest(context)) {
                            Toast.makeText(context, mApplication.ST("请检查网络连接"), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject js = new JSONObject();
                                    try {
                                        js.put("act_id", id);
                                        js.put("user_id", PreferenceUtil.getUserId(context));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    String data = OkGo.post(Constants.Activity_Shoucang_IP)
                                            .params("key", ApisSeUtil.getKey())
                                            .params("msg", ApisSeUtil.getMsg(js))
                                            .execute().body().string();
                                    if (!data.equals("")) {
                                        if (AnalyticalJSON.getHashMap(data) != null && "002".equals(AnalyticalJSON.getHashMap(data).get("code"))) {
                                            context.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ((SwipeMenuLayout) holder.delete.getParent()).quickClose();
                                                    list.remove(position);
                                                    notifyDataSetChanged();
                                                }
                                            });
                                        } else {
                                            context.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(context, mApplication.ST("服务器异常"), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                        break;
                    case 3:
                        if (!Network.HttpTest(mApplication.getInstance())) {
                            Toast.makeText(mApplication.getInstance(), "请检查网络连接", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject js = new JSONObject();
                                    try {
                                        js.put("user_id", PreferenceUtil.getUserId(context));
                                        js.put("cfg_id", id);
                                        js.put("m_id", Constants.M_id);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    String data = OkGo.post(Constants.FUNDING_DETAIL_Shoucang).params("key", Constants.safeKey)
                                            .params("key", ApisSeUtil.getKey())
                                            .params("msg", ApisSeUtil.getMsg(js)).execute().body().string();
                                    if (!data.equals("")) {
                                        if (AnalyticalJSON.getHashMap(data) != null && "002".equals(AnalyticalJSON.getHashMap(data).get("code"))) {
                                            context.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ((SwipeMenuLayout) holder.delete.getParent()).quickClose();
                                                    list.remove(position);
                                                    notifyDataSetChanged();


                                                }
                                            });
                                        } else {
                                            context.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(mApplication.getInstance(), "服务器异常", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                        break;
                }
            }
        });
        return view;
    }

    static class ViewHolder {
        ImageView image;
        RelativeLayout relativeLayout;
        TextView title, user, time, type, delete;
    }
}
