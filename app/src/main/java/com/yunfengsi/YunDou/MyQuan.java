package com.yunfengsi.YunDou;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.yunfengsi.Model_activity.activity_Detail;
import com.yunfengsi.R;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.ProgressUtil;
import com.yunfengsi.Utils.ShareManager;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.TimeUtils;
import com.yunfengsi.Utils.ToastUtil;
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
 * 作者：因陀罗网 on 2018/4/23 17:29
 * 公司：成都因陀罗网络科技有限公司
 */
public class MyQuan extends AppCompatActivity implements DuiHuanContract.MyQuanView {
    private RecyclerView       recyclerView;
    private SwipeRefreshLayout swip;
    private MessageAdapter     adapter;

    private MyQuanPresenterImpl quanPresenter;
    private UMWeb               umWeb;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mApplication.getInstance().romoveActivity(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.message_center);
        mApplication.getInstance().addActivity(this);

        ((ImageView) findViewById(R.id.title_back)).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.title_title)).setText(mApplication.ST("我的福利"));
        ((ImageView) findViewById(R.id.title_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        quanPresenter = new MyQuanPresenterImpl(this);
        swip = (SwipeRefreshLayout) findViewById(R.id.swip);
        swip.setOnRefreshListener(quanPresenter);
        swip.setColorSchemeResources(R.color.main_color);

        recyclerView = (RecyclerView) findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new mItemDecoration(this));

        adapter = new MessageAdapter(this, new ArrayList<HashMap<String, String>>());
//        adapter.openLoadMore(pageSize, true);

        adapter.setOnLoadMoreListener(quanPresenter, recyclerView);
        adapter.disableLoadMoreIfNotFullPage();

        adapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final BaseQuickAdapter adapter, View view, final int position) {
                if (System.currentTimeMillis() >= TimeUtils.dataOne(((HashMap<String, String>) adapter.getItem(position)).get("end_time"))) {
                    ToastUtil.showToastShort(mApplication.ST("该券已过期"));
                    return;
                }

                String       status = ((HashMap<String, String>) adapter.getItem(position)).get("start");
                final String type   = ((HashMap<String, String>) adapter.getItem(position)).get("type");
                switch (status) {
                    case "1":

                        AlertDialog.Builder builder = new AlertDialog.Builder(MyQuan.this);
                        View v = LayoutInflater.from(MyQuan.this).inflate(R.layout.quan_user_dialog, null);
                        builder.setView(v);
                        final AlertDialog dialog = builder.create();
                        v.findViewById(R.id.use).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent();
                                if (type.equals("1")) {//活动券
                                    intent.setClass(MyQuan.this, activity_Detail.class);
                                    intent.putExtra("wel_id", ((HashMap<String, String>) adapter.getItem(position)).get("id"));
                                    intent.putExtra("id", ((HashMap<String, String>) adapter.getItem(position)).get("act_id"));
                                    startActivityForResult(intent, 666);
                                } else if (type.equals("2")||type.equals("3")) {//排位券   祈福券
                                    showInputDialog(type, ((HashMap<String, String>) adapter.getItem(position)).get("id"));
                                }
                                dialog.dismiss();
                            }
                        });
                        v.findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                umWeb = new UMWeb(Constants.FX_host_Ip + "Welfare/id/" + ((HashMap<String, String>) adapter.getItem(position)).get("id"));
                                umWeb.setDescription(PreferenceUtil.getUserIncetance(MyQuan.this).getString("pet_name", "") +
                                        "向您送出一张" + ((HashMap<String, String>) adapter.getItem(position)).get("title") + "券");
                                umWeb.setTitle(((HashMap<String, String>) adapter.getItem(position)).get("title") + "券");
                                umWeb.setThumb(new UMImage(MyQuan.this, R.drawable.indra_share));
                                new ShareManager().shareWeb(umWeb, MyQuan.this);
                                dialog.dismiss();
                            }
                        });
                        v.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                        Window window = dialog.getWindow();
                        WindowManager.LayoutParams wl = window.getAttributes();
                        wl.width = getResources().getDisplayMetrics().widthPixels * 7 / 10;
                        wl.height = WindowManager.LayoutParams.WRAP_CONTENT;
                        window.setAttributes(wl);


                        break;
                    case "2":
                        if(type.equals("1")){
                            ToastUtil.showToastShort("该券已被使用");
                        }else if (type.equals("2")||type.equals("3")) {//排位券   祈福券
                            // TODO: 2018/5/22 进入排位页面
                            goToDetail(type,((HashMap<String, String>) adapter.getItem(position)).get("wishuser")
                            ,((HashMap<String, String>) adapter.getItem(position)).get("wishcontent")
                            ,((HashMap<String, String>) adapter.getItem(position)).get("end_time"));
                        }


                        break;
                    case "3":
                        ToastUtil.showToastShort("该券已被转赠");
                        break;
                }


            }
        });

        recyclerView.setAdapter(adapter);

        TextView textView = new TextView(this);
        Drawable d        = ContextCompat.getDrawable(this, R.drawable.load_nothing);
        d.setBounds(0, 0, DimenUtils.dip2px(this, 150), DimenUtils.dip2px(this, 150) * d.getIntrinsicHeight() / d.getIntrinsicWidth());
        textView.setCompoundDrawables(null, d, null, null);
        textView.setCompoundDrawablePadding(DimenUtils.dip2px(this, 10));
        textView.setText(mApplication.ST("您暂未获得兑换券"));


        textView.setGravity(Gravity.CENTER);
        ViewGroup.MarginLayoutParams vl = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        vl.topMargin = DimenUtils.dip2px(this, 180);
        textView.setLayoutParams(vl);
        adapter.setEmptyView(textView);

        swip.post(new Runnable() {
            @Override
            public void run() {
                swip.setRefreshing(true);
                quanPresenter.onRefresh();
            }
        });
    }

    private void goToDetail(String type, String wishuser, String wishcontent,String timeOrginal) {
        Intent intent=new Intent(this,QiFu_PaiWei_Detail.class);
        intent.putExtra("address",wishuser);
        intent.putExtra("person",wishcontent);
        intent.putExtra("time",timeOrginal);
        intent.putExtra("type",type);
        startActivity(intent);
    }

    private void showInputDialog(final String type, final String id) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MyQuan.this);
        View                view1    = LayoutInflater.from(MyQuan.this).inflate(R.layout.dialog_title_message, null);
        builder1.setView(view1);
        final AlertDialog dialog1 = builder1.create();
        TextView          commit  = view1.findViewById(R.id.commit);
        final EditText    title   =view1.findViewById(R.id.user);
        final EditText    content =view1.findViewById(R.id.content);
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(title.getText().toString().trim().equals("")||
                        content.getText().toString().trim().equals("")){
                    ToastUtil.showToastShort("请填写完整信息");
                    return;
                }
                JSONObject js=new JSONObject();
                try {
                    js.put("m_id",Constants.M_id);
                    js.put("user_id",PreferenceUtil.getUserId(MyQuan.this));
                    js.put("id",id);
                    js.put("wishuser",title.getText().toString().trim());
                    js.put("wishcontent",content.getText().toString().trim());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ApisSeUtil.M m= ApisSeUtil.i(js);
                LogUtil.e("祈福，排位券使用：：："+js+"    类型：："+(type.equals("3")?"祈福券":"排位券"));
                OkGo.post(Constants.Quan_Use).params("key",m.K())
                        .params("msg",m.M())
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(String s, Call call, Response response) {
                                quanPresenter.onRefresh();//刷新当前页面

                                dialog1.dismiss();
                               HashMap<String,String > map= AnalyticalJSON.getHashMap(s);
                               if(map!=null){
                                   if("000".equals(map.get("code"))){
                                       // TODO: 2018/5/22 进入祈福、排位详情页面
                                       goToDetail(type,title.getText().toString().trim(),content.getText().toString().trim(),map.get("end_time"));
                                   }else{
                                       ToastUtil.showToastShort("该兑换券已转赠");
                                   }
                               }

                            }

                            @Override
                            public void onBefore(BaseRequest request) {
                                super.onBefore(request);
                                ProgressUtil.show(MyQuan.this,"","正在提交...");
                            }

                            @Override
                            public void onAfter(String s, Exception e) {
                                super.onAfter(s, e);
                                ProgressUtil.dismiss();

                            }
                        });
            }
        });
        Window window = dialog1.getWindow();
        window.getDecorView().setPadding(0,0,0,0);
        window.setWindowAnimations(R.style.dialogWindowAnim);
        window.setBackgroundDrawableResource(R.color.transparent);
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams wl =window.getAttributes();
        wl.width=getResources().getDisplayMetrics().widthPixels*6/10;
        wl.height=WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(wl);
        dialog1.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 666 && resultCode == 666) {
            quanPresenter.onRefresh();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ProgressUtil.dismiss();
    }

    @Override
    public void hideSwip() {
        swip.setRefreshing(false);
    }

    @Override
    public MessageAdapter getAdapter() {
        return adapter;
    }

    public static class MessageAdapter extends BaseQuickAdapter<HashMap<String, String>, BaseViewHolder> {
        private Context context;

        public MessageAdapter(Context context, List<HashMap<String, String>> data) {
            super(R.layout.item_duihuan_center, data);
            this.context = context;
        }

        @Override
        protected void convert(BaseViewHolder holder, HashMap<String, String> map) {
            Glide.with(context).load(map.get("image")).animate(R.anim.left_in)
                    .into((ImageView) holder.getView(R.id.image));
            if (System.currentTimeMillis() >= TimeUtils.dataOne(map.get("end_time"))) {
                holder.setText(R.id.date, mApplication.ST("有效期至：" + TimeUtils.getTrueTimeStr(map.get("end_time"))) + mApplication.ST("    状态:已过期"));
                holder.setTextColor(R.id.date, ContextCompat.getColor(context, R.color.wordblack));
            } else {
                String status = map.get("start");//标记是否使用 或转赠
                String type   = map.get("type");//标识 活动券  祈福券，排位券  123
                if (status.equals("1")) {
                    holder.setTextColor(R.id.date, ContextCompat.getColor(context, R.color.wordblack));
                    if (type.equals("1")) {
                        holder.setText(R.id.date, mApplication.ST("有效期至：" + TimeUtils.getTrueTimeStr(map.get("end_time"))) + mApplication.ST("    状态:未使用"));
                    } else {
                        holder.setText(R.id.date, mApplication.ST("    状态:未使用"));
                    }

                } else if (status.equals("2")) {

                    holder.setTextColor(R.id.date, ContextCompat.getColor(context, R.color.wordblack));
                    holder.setText(R.id.date, mApplication.ST("有效期至：" + TimeUtils.getTrueTimeStr(map.get("end_time"))) + mApplication.ST("    状态:已使用"));
                } else if (status.equals("3")) {
                    holder.setTextColor(R.id.date, ContextCompat.getColor(context, R.color.wordblack));
                    if (type.equals("1")) {
                        holder.setText(R.id.date, mApplication.ST("有效期至：" + TimeUtils.getTrueTimeStr(map.get("end_time"))) + mApplication.ST("    状态:已转赠"));
                    } else {//2/3
                        holder.setText(R.id.date, mApplication.ST("    状态:已转赠"));
                    }
                }

            }
        }
    }
}
