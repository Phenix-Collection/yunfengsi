package com.yunfengsi.Models.Model_zhongchou;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lzy.okgo.OkGo;
import com.yunfengsi.R;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.Network;
import com.yunfengsi.Utils.ProgressUtil;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.TimeUtils;
import com.yunfengsi.Utils.mApplication;
import com.yunfengsi.View.LoadMoreListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import cn.carbs.android.avatarimageview.library.AvatarImageView;

/**
 * Created by Administrator on 2016/10/11.
 */
public class Fund_surpport_list extends AppCompatActivity implements LoadMoreListView.OnLoadMore, View.OnClickListener {
    private LoadMoreListView listView;
    private String page = "1";
    private String endPage = "";
    private TextView t;//加载数据的底部提示
    private ProgressBar p;//加载数据的底部进度
    private static final String TAG = "Fund_surpport_list";
    private mAdapter adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.fund_people_list);
        initView();
        getData();

    }

    /**
     * 上拉加载
     */
    @Override
    public void loadMore() {
        if (!endPage.equals(page)) {
            page = String.valueOf(Integer.parseInt(page) + 1);
        } else {
            p.setVisibility(View.GONE);
            t.setText(mApplication.ST("没有更多数据了"));
            return;
        }
        getData();
    }

    /**
     * 加载数据
     */
    private void getData() {
        if (!Network.HttpTest(this)) {
            return;
        }
        ProgressUtil.show(this,"","正在加载");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject js=new JSONObject();
                    try {
                        js.put("cfg_id", getIntent().getStringExtra("id"));
                        js.put("m_id", Constants.M_id);
                        js.put("page", page);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil .M m=ApisSeUtil.i(js);
                    String data = OkGo.post(Constants.CFG_List_Ip)
                            .params("key", m.K())
                            .params("msg", m.M())
                            .execute().body().string();
                    if (!TextUtils.isEmpty(data)) {
                        final List<HashMap<String, String>> list1 = AnalyticalJSON.getList(data, "cfg");
                        Log.w(TAG, "run: list------>"+list1 );
                        if (list1 != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (list1.size() < 10) {
                                        endPage = page;
                                    }
                                    if (adapter==null) {
                                        adapter=new mAdapter(Fund_surpport_list.this,list1);
                                        listView.setAdapter(adapter);
                                    } else {
                                        adapter.list.addAll(list1);
                                        adapter.notifyDataSetChanged();

                                    }
                                    listView.onLoadComplete();
                                    ProgressUtil.dismiss();
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ProgressUtil.dismiss();
                                    Toast.makeText(Fund_surpport_list.this, mApplication.ST("数据加载失败，请稍后尝试"), Toast.LENGTH_SHORT).show();
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

    private void initView() {
        ImageView back = (ImageView) findViewById(R.id.title_back);
        back.setImageResource(R.drawable.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        TextView title = (TextView) findViewById(R.id.title_title);
        title.setText(mApplication.ST("爱心动态"));

        listView = (LoadMoreListView) findViewById(R.id.fund_people_list);
        listView.setLoadMoreListen(this);
        t = (TextView) (listView.footer.findViewById(R.id.load_more_text));
        p = (ProgressBar) (listView.footer.findViewById(R.id.load_more_bar));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
        }
    }
    public static class mAdapter extends BaseAdapter{

        public List<HashMap<String ,String >>list;
        private Context context;
        public mAdapter(Context context,List<HashMap<String ,String >>list) {
            super();
            this.context=context;
            this.list=list;
        }

        public void addList(List<HashMap<String ,String >>list){
            this.list=list;
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
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            viewHolder holder = null;
            HashMap<String ,String>map=list.get(position);
            if (view == null) {
                holder = new viewHolder();
                view = LayoutInflater.from(context).inflate(R.layout.fund_list_item, parent, false);
                holder.head = (AvatarImageView) view.findViewById(R.id.fund_list_item_head);
                holder.name = (TextView) view.findViewById(R.id.fund_list_item_name);
                holder.money = (TextView) view.findViewById(R.id.fund_list_item_money);
                holder.time = (TextView) view.findViewById(R.id.fund_list_item_time);
                holder.level= (ImageView) view.findViewById(R.id.level);
                view.setTag(holder);
            } else {
                holder = (viewHolder) view.getTag();
            }

            Glide.with(context).load(list.get(position).get("user_image")).override(DimenUtils.dip2px(mApplication.getInstance(), 50)

                    , DimenUtils.dip2px(mApplication.getInstance(), 50))
                    .placeholder(R.drawable.def).into(holder.head);

            if("".equals(map.get("pet_name"))){
                SpannableString ss=new SpannableString("佚名"+("".equals(map.get("mark").trim())?"":("\n\n"+mApplication.ST(map.get("mark").trim()))));
                ss.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context,R.color.wordhuise)),2,ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ss.setSpan(new AbsoluteSizeSpan(DimenUtils.dip2px(context,13)),2,ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.name.setText(ss);
            }else{
                SpannableString ss=new SpannableString(mApplication.ST(map.get("pet_name")+("".equals(map.get("mark").trim())?"":("\n\n"+mApplication.ST(map.get("mark").trim())))));
                ss.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context,R.color.wordhuise)),map.get("pet_name").length(),ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ss.setSpan(new AbsoluteSizeSpan(DimenUtils.dip2px(context,13)),map.get("pet_name").length(),ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.name.setText(ss);
            }
            holder.money.setText(mApplication.ST(list.get(position).get("money")+"元"));
            holder.time.setText(mApplication.ST(TimeUtils.getTrueTimeStr(list.get(position).get("end_time"))));
            if (map.get("level") != null) {
                switch (map.get("level")) {
                    case "0":
                        holder.level.setVisibility(View.GONE);
                        break;
                    case "1":
                        holder.level.setVisibility(View.VISIBLE);
                        Glide.with(context).load(R.drawable.gif1).asGif()
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .override(DimenUtils.dip2px(context, 25), DimenUtils.dip2px(context, 25))
                                .fitCenter().into(holder.level);
                        break;
                    case "2":
                        holder.level.setVisibility(View.VISIBLE);
                        Glide.with(context).load(R.drawable.gif2).asGif()
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .override(DimenUtils.dip2px(context, 25), DimenUtils.dip2px(context, 25))
                                .fitCenter().into(holder.level);
                        break;
                    case "3":
                    case "4":
                        holder.level.setVisibility(View.VISIBLE);
                        Glide.with(context).load(R.drawable.gif3).asGif()
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .override(DimenUtils.dip2px(context, 25), DimenUtils.dip2px(context, 25))
                                .fitCenter().into(holder.level);
                        break;
                }
            }

            return view;
        }

        static  class viewHolder {
            AvatarImageView head;
            ImageView level;
            TextView name;
            TextView money;
            TextView time;
        }
    }

}
