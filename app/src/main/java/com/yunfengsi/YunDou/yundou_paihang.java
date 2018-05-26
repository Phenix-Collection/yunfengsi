package com.yunfengsi.YunDou;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.ruffian.library.RTextView;
import com.yunfengsi.R;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.mApplication;
import com.yunfengsi.View.mItemDecoration;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 作者：因陀罗网 on 2018/5/21 17:43
 * 公司：成都因陀罗网络科技有限公司
 */
public class yundou_paihang extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView       recyclerView;
    private SwipeRefreshLayout swip;

    private MessageAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.yundou_paihang_list);
        ((ImageView) findViewById(R.id.title_back)).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.title_title)).setText("云豆排行榜");
        ((ImageView) findViewById(R.id.title_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        swip = (SwipeRefreshLayout) findViewById(R.id.swip);
        swip.setOnRefreshListener(this);
        swip.setColorSchemeResources(R.color.main_color);

        recyclerView = (RecyclerView) findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new mItemDecoration(this));

        adapter = new MessageAdapter(this, new ArrayList<HashMap<String, String>>());
        recyclerView.setAdapter(adapter);

        swip.post(new Runnable() {
            @Override
            public void run() {
                swip.setRefreshing(true);
                onRefresh();
            }
        });
    }

    public void getPH() {
        JSONObject js = new JSONObject();
        try {
            js.put("m_id", Constants.M_id);
            js.put("user_id", PreferenceUtil.getUserId(this));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m = ApisSeUtil.i(js);
        LogUtil.e("云豆排行榜：：" + js);
        OkGo.post(Constants.Yundou_PaiHang_List).tag(this).params("key", m.K())
                .params("msg", m.M())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        HashMap<String, String> map = AnalyticalJSON.getHashMap(s);
                        if (map != null) {
                            ArrayList<HashMap<String, String>> list = AnalyticalJSON.getList_zj(map.get("msg"));
                            adapter.setNewData(list);


                            ((TextView) findViewById(R.id.paiming)).setText(mApplication.ST("我的排名 : " + map.get("user_ph")));
                        }


                    }

                    @Override
                    public void onAfter(String s, Exception e) {
                        super.onAfter(s, e);
                        swip.setRefreshing(false);
                    }
                });
    }

    private static class MessageAdapter extends BaseQuickAdapter<HashMap<String, String>, BaseViewHolder> {
        private Context context;
        private Drawable place;
        public MessageAdapter(Context context, List<HashMap<String, String>> data) {
            super(R.layout.item_yundou_paiming, data);
            this.context = context;
            place=ContextCompat.getDrawable(context,R.drawable.placeholder_c6c6c6);
            place.setBounds(0,0,DimenUtils.dip2px(context,50),DimenUtils.dip2px(context,50));
        }

        @Override
        protected void convert(final BaseViewHolder holder, final HashMap<String, String> map) {
            final RTextView user = ((RTextView) holder.getView(R.id.user));
            Glide.with(context).load(map.get("user_image"))
                    .asBitmap()
                    .override(DimenUtils.dip2px(context, 50), DimenUtils.dip2px(context, 50))
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            RoundedBitmapDrawable rbd = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                            rbd.setCircular(true);
                            user.setIconNormal(rbd);
                        }

                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            super.onLoadFailed(e, errorDrawable);
                            user.setIconNormal(place);
                        }

                    });

            holder.setText(R.id.user, map.get("pet_name"))
                    .setText(R.id.num, map.get("yundou"));
            int       i  = getData().indexOf(map);
            RTextView rt = holder.getView(R.id.level);
            if (i < 3) {
                if (i == 0) {
                    rt.setBackgroundColorNormal(Color.RED);
                } else if (i == 1) {
                    rt.setBackgroundColorNormal(ContextCompat.getColor(context, R.color.main_color));
                } else if (i == 2) {
                    rt.setBackgroundColorNormal(Color.parseColor("#bbFE963B"));
                }

            } else {
                rt.setBackgroundColorNormal(Color.parseColor("#b6b6b6"));
            }
            rt.setText(String.valueOf(i + 1));
//            holder.setText(R.id.content, mApplication.ST(map.get("contents")))
//                    .setText(R.id.time, mApplication.ST(TimeUtils.getTrueTimeStr(map.get("time"))))
//                    .setText(R.id.time2, mApplication.ST(TimeUtils.getTrueTimeStr(map.get("time"))));


        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(this);
    }

    @Override
    public void onRefresh() {
        getPH();
    }
}
