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
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
import com.yunfengsi.Managers.CollectManager;
import com.yunfengsi.Model_activity.activity_Detail;
import com.yunfengsi.Model_zhongchou.FundingDetailActivity;
import com.yunfengsi.R;
import com.yunfengsi.Setting.Activity_ShouCang;
import com.yunfengsi.Setting.Activity_ShouCang_Result;
import com.yunfengsi.Setting.onDeleteEvent;
import com.yunfengsi.Utils.LoginUtil;
import com.yunfengsi.Utils.Network;
import com.yunfengsi.Utils.TimeUtils;
import com.yunfengsi.Utils.mApplication;
import com.yunfengsi.ZiXun_Detail;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/7/21.
 */
public class Mine_SC_adapter extends BaseAdapter {
    public  List<HashMap<String, String>> list;
    public  Activity                      context;
    public  LayoutInflater                inflater;
    private int                           screenWidth;

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
        final ViewHolder        holder;
        View                    view = convertView;
        HashMap<String, String> map  = list.get(position);
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
        holder.user.setText(mApplication.ST(map.get("title")));
        holder.title.setText(mApplication.ST(map.get("abstract")));
        holder.time.setText(TimeUtils.getTrueTimeStr(map.get("time")));
        holder.type.setTag(map.get("collect_id"));
        Glide.with(context).load(map.get("image")).override(screenWidth * 3 / 10, screenWidth * 6 / 25).centerCrop().into(holder.image);
        holder.delete.setTag(map.get("type"));
        holder.delete.setTag(R.id.sut_type, map.get("collect_id"));
        holder.delete.setTag(R.id.position, position);
        if (map.get("type").equals("1")) {
            holder.type.setText(mApplication.ST("图文"));
        } else if (map.get("type").equals("2")) {
            holder.type.setText(mApplication.ST("活动"));
        } else if (map.get("type").equals("4")) {
            holder.type.setText(mApplication.ST("助学"));
        }
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView view1  = (TextView) v.findViewById(R.id.mine_shoucang_item_type);
                String   id1    = view1.getTag().toString();
                Intent   intent = new Intent();

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
                final String id       = (String) v.getTag(R.id.sut_type);
                final int    position = (int) v.getTag(R.id.position);
                final String type     = (String) v.getTag();

                if (!new LoginUtil().checkLogin(context)) {
                    return;
                }
                if (!Network.HttpTest(context)) {
                    Toast.makeText(context, mApplication.ST("请检查网络连接"), Toast.LENGTH_SHORT).show();
                    return;
                }
                CollectManager.doCollectOnDelete(context, id, type, new CollectManager.OnDeleteListener() {
                    @Override
                    public void onDelete() {
                        ((SwipeMenuLayout) holder.delete.getParent()).quickClose();
                        list.remove(position);
                        notifyDataSetChanged();
                        if (list.size() == 0) {
                            if (context instanceof Activity_ShouCang) {
                                ((Activity_ShouCang) context).tip.setVisibility(View.VISIBLE);
                                ((Activity_ShouCang) context).listView.footer.setVisibility(View.GONE);
                            } else if (context instanceof Activity_ShouCang_Result) {
                                ((Activity_ShouCang_Result) context).tip.setVisibility(View.VISIBLE);
                                ((Activity_ShouCang_Result) context).listView.footer.setVisibility(View.GONE);

                                EventBus.getDefault().post(new onDeleteEvent());
                            }

                        } else {
                            if (context instanceof Activity_ShouCang) {
                                ((Activity_ShouCang) context).tip.setVisibility(View.GONE);
                            } else if (context instanceof Activity_ShouCang_Result) {
                                EventBus.getDefault().post(new onDeleteEvent());
                                ((Activity_ShouCang_Result) context).tip.setVisibility(View.GONE);
                            }

                        }

                    }
                });

            }
        });
        return view;
    }

    static class ViewHolder {
        ImageView      image;
        RelativeLayout relativeLayout;
        TextView       title, user, time, type, delete;
    }
}
