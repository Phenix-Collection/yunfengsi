package com.yunfengsi.NianFo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.yunfengsi.Adapter.mArrayAdapter;
import com.yunfengsi.R;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.Network;
import com.yunfengsi.Utils.NumUtils;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.ProgressUtil;
import com.yunfengsi.Utils.ShareManager;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.TimeUtils;
import com.yunfengsi.Utils.ToastUtil;
import com.yunfengsi.Utils.mApplication;
import com.yunfengsi.View.LoadMoreListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/8/1.
 */
public class nianfo_home_tab6 extends Activity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, LoadMoreListView.OnLoadMore {
    private static final String TAG = "nianfo_home_tab6";
    private LoadMoreListView listView;
    private FayuanAdapter adapter;
    private String page = "1";
    private String endPage = "";
    private SwipeRefreshLayout swip;
    private boolean isRefresh = false;
    private ArrayList<HashMap<String, String>> typelist;
    private int screeWidth;
    private SharedPreferences sp;
    private TextView username;
    private EditText type;
    private TextView commit, num, sb2, sb;
    private TextView chengji;
    private Handler handler = new Handler();
    private nianfo_home_tab6 c;
    private InputMethodManager imm;

    private String choosedType = "1";
    private String targetTime="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.fragment_nianfo_home_tab6);
        listView = (LoadMoreListView) findViewById(R.id.nianfo_home_tab1_listview);
        listView.setLoadMoreListen(this);
        listView.setFooterDividersEnabled(false);
        swip = (SwipeRefreshLayout) findViewById(R.id.nianfo_1_swip);
        swip.setOnRefreshListener(this);
        swip.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        adapter = new FayuanAdapter();
        typelist = new ArrayList<>();
        type = (EditText) findViewById(R.id.nianfo_home_tab1_type);
        type.setFocusable(false);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        screeWidth = getResources().getDisplayMetrics().widthPixels;
        sp = getSharedPreferences("user", Context.MODE_PRIVATE);
        sb2 = (TextView) findViewById(R.id.sb2);
        sb = (TextView) findViewById(R.id.sb);
        username = (TextView) findViewById(R.id.nianfo_home_tab1_mName);
        commit = (TextView) findViewById(R.id.nianfo_home_tab1_commit);
        num = (EditText) findViewById(R.id.nianfo_home_tab1_num);
        chengji = (TextView) findViewById(R.id.nianfo_home_tab1_chaxunchengji);
        username.setText(sp.getString("pet_name", ""));
        swip.post(new Runnable() {
            @Override
            public void run() {
                swip.setRefreshing(true);
                loadData();
            }
        });


        type.setOnClickListener(this);
        commit.setOnClickListener(this);
        chengji.setOnClickListener(this);
        findViewById(R.id.nianfo_home_back).setOnClickListener(this);
        Drawable drawable= ContextCompat.getDrawable(this,R.drawable.down);
        int dp20= DimenUtils.dip2px(this,15);
        drawable.setBounds(0,0,dp20,dp20);
        chengji.setCompoundDrawables(drawable,null,null,null);
        sb2.setText(mApplication.ST(" 念 "));
        sb.setText(mApplication.ST(" 声"));
        type.setHint(mApplication.ST("请选择佛号"));
        num.setHint(mApplication.ST("请输入数目"));


        chengji.setText(mApplication.ST("念佛"));
        ((TextView) findViewById(R.id.nianfo_title)).setText(mApplication.ST("发愿"));
        Glide.with(this).load(PreferenceUtil.getUserIncetance(this).getString("head_url", ""))
                .override(DimenUtils.dip2px(this, 40), DimenUtils.dip2px(this, 40)).into((ImageView) findViewById(R.id.head));
        setTargetTime();//获取发愿截止时间
    }
    private void setTargetTime(){
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("m_id", Constants.M_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m=ApisSeUtil.i(jsonObject);
        OkGo.post(Constants.Fayuan_TargetTime_Get_Ip).tag(this).params("key",m.K())
                .params("msg",m.M())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        HashMap<String,String> map= AnalyticalJSON.getHashMap(s);
                        if(map!=null){
                            if("000".equals(map.get("code"))){
                                targetTime=map.get("time");
                            }
                        }

                    }
                });

    }


    private void loadData() {
//        ProgressUtil.show(this,"","正在加载");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final ProgressBar p = (ProgressBar) (listView.footer.findViewById(R.id.load_more_bar));
                    final TextView t = (TextView) (listView.footer.findViewById(R.id.load_more_text));
                    JSONObject js = new JSONObject();
                    try {
                        js.put("page", page);
                        js.put("m_id", Constants.M_id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m = ApisSeUtil.i(js);
                    String data1 = OkGo.post(Constants.Fayuan_List_Ip)
                            .params("key", m.K())
                            .params("msg", m.M())
                            .tag(TAG).execute().body().string();
                    if (!data1.equals("")) {

                        final ArrayList<HashMap<String,String>> list1 = AnalyticalJSON.getList_zj(data1);

                        if (list1 != null && handler != null) {
                            if (list1.size() != 10) endPage = page;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
//                                    ProgressUtil.dismiss();
                                    TextView t = (TextView) (listView.footer.findViewById(R.id.load_more_text));
                                    if (adapter.mlist.size() == 0) {
                                        adapter.addList(list1);
                                        LogUtil.e(list1 + "");
                                        listView.setAdapter(adapter);

                                    } else {
                                        if (isRefresh) {
                                            isRefresh = false;
                                            adapter.mlist.clear();
                                            adapter.addList(list1);
                                            adapter.notifyDataSetChanged();
                                            endPage = "";
                                            if (t.getText().toString().equals(mApplication.ST("没有更多数据了"))) {
                                                listView.onLoadComplete();
                                                t.setText(mApplication.ST("正在加载...."));
                                            }
                                        } else {
                                            adapter.mlist.addAll(list1);
                                            adapter.notifyDataSetChanged();

                                            ProgressBar p = (ProgressBar) (listView.footer.findViewById(R.id.load_more_bar));
                                            if (endPage.equals(page)) {
                                                p.setVisibility(View.GONE);
                                                t.setText(mApplication.ST("没有更多数据了"));
                                            } else {
                                                t.setText(mApplication.ST("正在加载...."));
                                                listView.onLoadComplete();
                                            }


                                        }
                                    }
                                    if (swip.isRefreshing()) swip.setRefreshing(false);
                                }

                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                    ProgressUtil.dismiss();
                                    endPage = page;
                                    p.setVisibility(View.GONE);
                                    t.setText(mApplication.ST("没有更多数据了"));
                                    listView.onLoadComplete();
                                    if (swip.isRefreshing()) swip.setRefreshing(false);

                                }
                            });

                        }

                    }
                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(nianfo_home_tab6.this, mApplication.ST("加载失败，请检查网络连接"), Toast.LENGTH_SHORT).show();
                            if (swip.isRefreshing()) swip.setRefreshing(false);
                        }
                    });
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void loadData2() {
//        ProgressUtil.show(this,"","正在加载");
        if (!Network.HttpTest(this)) {
            return;
        }
        ProgressUtil.show(this, "", "正在加载");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    JSONObject js = new JSONObject();
                    try {
                        js.put("page", "1");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m = ApisSeUtil.i(js);
                    String url = Constants.nianfo_home_nianfo_Get_Ip;
                    String ts = "buddha";
                    switch (choosedType) {
                        case "1":
                            url = Constants.nianfo_home_nianfo_Get_Ip;
                            ts = "buddha";
                            break;
                        case "2":
                            url = Constants.nianfo_home_songjing_Get_Ip;
                            ts = "reading";
                            break;
                        case "3":
                            url = Constants.nianfo_home_chizhou_Get_Ip;
                            ts = "japa";
                            break;
                    }
                    String data1 = OkGo.post(url)
                            .params("key", m.K())
                            .params("msg", m.M())
                            .tag(TAG).execute().body().string();
                    if (!data1.equals("")) {

                        final HashMap<String, String> map = AnalyticalJSON.getHashMap(data1);
                        if (map != null) {
                            typelist = AnalyticalJSON.getList(data1, ts);
                        }
                        if (handler != null) {

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    View view = LayoutInflater.from(nianfo_home_tab6.this).inflate(R.layout.nianfo_type_dialog, null);
                                    ListView list = (ListView) view.findViewById(R.id.nianfo_type_listview);
                                    AlertDialog.Builder builder = new AlertDialog.Builder(nianfo_home_tab6.this);
                                    builder.setView(view);

                                    final AlertDialog dialog = builder.create();
                                    if (typelist != null) {
                                        String s = "念佛";
                                        if (choosedType.equals("1")) {
                                            s = "念佛";
                                        } else if (choosedType.equals("2")) {
                                            s = "诵经";
                                        } else if (choosedType.equals("3")) {
                                            s = "持咒";
                                        }
                                        ArrayAdapter adapter = new mArrayAdapter(nianfo_home_tab6.this, R.layout.nianfo_home_dialog_item, R.id.nianfo_home_dialog_item_text, typelist, s);
                                        list.setAdapter(adapter);
                                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                                type.setText(((TextView) view).getText().toString());
                                                type.setTag((view).getTag());
                                                type.setTag(R.id.type_text, ((TextView) view).getText().toString());
                                                dialog.dismiss();
                                            }
                                        });
                                    }
                                    Window window = dialog.getWindow();
                                    window.getDecorView().setPadding(0, 0, 0, 0);
                                    window.setGravity(Gravity.CENTER);
                                    WindowManager.LayoutParams wl = window.getAttributes();

                                    wl.height = WindowManager.LayoutParams.WRAP_CONTENT;
                                    wl.width = getResources().getDisplayMetrics().widthPixels * 75 / 100;
                                    window.setAttributes(wl);

                                    dialog.show();


                                    if (swip.isRefreshing()) swip.setRefreshing(false);
                                    ProgressUtil.dismiss();
                                }

                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ProgressUtil.dismiss();
                                    ToastUtil.showToastShort("加载失败，请稍后尝试");

                                }
                            });

                        }

                    }
                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(nianfo_home_tab6.this, mApplication.ST("加载失败，请检查网络连接"), Toast.LENGTH_SHORT).show();
                            if (swip.isRefreshing()) swip.setRefreshing(false);
                            ProgressUtil.dismiss();
                        }
                    });
                    e.printStackTrace();
                }
            }
        }).start();

    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.Share:
                UMWeb umWeb = new UMWeb("http://a.app.qq.com/o/simple.jsp?pkgname=com.ytl.qianyishenghao");
                umWeb.setTitle("千亿圣号App");
                umWeb.setDescription("快来千亿圣号共修吧");
                umWeb.setThumb(new UMImage(this, R.drawable.indra));
                new ShareManager().shareWeb(umWeb, this);
                break;
            case R.id.nianfo_home_back:
                finish();
                break;
            case R.id.nianfo_home_tab1_type:
                loadData2();
                break;
            case R.id.nianfo_home_tab1_commit:
                if (!type.getText().toString().trim().equals("") && !num.getText().toString().trim().equals("") &&
                        !num.getText().toString().trim().equals("0")) {
                    v.setEnabled(false);
                    String content = "您发愿" + sb2.getText().toString().trim() + type.getTag(R.id.type_text).toString()
                            + num.getText().toString() + sb.getText().toString() + "\n\n截止日期:" + targetTime + "(农历7月15日)";
                    AlertDialog.Builder builder = new AlertDialog.Builder(nianfo_home_tab6.this);
                    View view = LayoutInflater.from(nianfo_home_tab6.this).inflate(R.layout.fayuan_dialog, null);
                    TextView c = (TextView) view.findViewById(R.id.content);
                    ((TextView) view.findViewById(R.id.title)).setText("请确认您的发愿信息");
                    c.setText(content);
                    builder.setView(view);
                    final AlertDialog d = builder.create();

                    ((TextView) view.findViewById(R.id.cancle)).setText("取消");
                    view.findViewById(R.id.cancle).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            d.dismiss();
                        }
                    });
                    ((TextView) view.findViewById(R.id.commit)).setText("确认发愿");
                    view.findViewById(R.id.commit).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            uploadData(v, d);
                        }
                    });
                    d.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            v.setEnabled(true);
                        }
                    });
                    d.show();
                }else{
                    Toast.makeText(mApplication.getInstance(), mApplication.ST("请仔细填写发愿信息"), Toast.LENGTH_SHORT).show();
                    v.setEnabled(true);
                }

                break;
            case R.id.nianfo_home_tab1_chaxunchengji:
                AlertDialog.Builder b = new AlertDialog.Builder(this);
                final String[] s = new String[]{"念佛", "诵经", "持咒"};
                b.setItems(s, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        chengji.setText(s[i]);
                        if (i == 0) {
                            sb2.setText(mApplication.ST(" 念 "));
                            sb.setText(mApplication.ST(" 声"));
                            type.setHint(mApplication.ST("请选择佛号"));
                            type.setText("");
                            choosedType = "1";
                        } else if (i == 1) {
                            sb2.setText(mApplication.ST(" 诵 "));
                            sb.setText(mApplication.ST(" 部"));
                            type.setHint(mApplication.ST("请选择经书"));
                            type.setText("");
                            choosedType = "2";
                        } else if (i == 2) {
                            sb2.setText(mApplication.ST(" 持 "));
                            sb.setText(mApplication.ST(" 遍"));
                            type.setHint(mApplication.ST("请选择咒语"));
                            type.setText("");
                            choosedType = "3";
                        }
                        dialogInterface.dismiss();

                    }
                });
                Dialog dialog = b.create();
                Window window = dialog.getWindow();
                window.getDecorView().setPadding(15, 15, 15, 15);
                WindowManager.LayoutParams wl = window.getAttributes();
                wl.width = getResources().getDisplayMetrics().widthPixels * 6 / 10;
                window.setAttributes(wl);
                dialog.show();
                break;
        }
    }

    private void uploadData(final View v, final DialogInterface dialogInterface) {
        if (!type.getText().toString().trim().equals("") && !num.getText().toString().trim().equals("") &&
                !num.getText().toString().trim().equals("0")) {
            ProgressUtil.show(nianfo_home_tab6.this, "", mApplication.ST("正在提交"));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String data1 = null;
                    try {
                        JSONObject js = new JSONObject();
                        try {
                            js.put("user_id", sp.getString("user_id", ""));
                            js.put("ls_id", type.getTag().toString());
                            js.put("num", num.getText().toString());
                            js.put("type", choosedType);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ApisSeUtil.M m = ApisSeUtil.i(js);
                        LogUtil.e("发愿提交：：" + js);
                        data1 = OkGo.post(Constants.Fayuan_Commit_Ip).tag(TAG)
                                .params("key", m.K())
                                .params("msg", m.M())
                                .execute().body().string();
                        if (!data1.equals("")) {
                            final HashMap<String, String> m1 = AnalyticalJSON.getHashMap(data1);
                            if (m1 != null) {
                                if ("000".equals(m1.get("code"))) {
                                    if (handler != null) {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                ProgressUtil.dismiss();
                                                HashMap<String, String> map = new HashMap<>();
                                                map.put("user_id", PreferenceUtil.getUserId(nianfo_home_tab6.this));
                                                map.put("type", choosedType);
                                                map.put("name",type.getText().toString());
                                                map.put("ls_id", type.getTag().toString());
                                                map.put("user_image", sp.getString("head_url", ""));
                                                map.put("pet_name", username.getText().toString());
                                                map.put("target_num", num.getText().toString());
                                                map.put("num","0");
                                                map.put("end_time",targetTime);
                                                map.put("time", TimeUtils.getStrTime(System.currentTimeMillis() + ""));
//                                                if (choosedType.equals("1")) {
//                                                    map.put("name", "念佛");
//                                                } else if (choosedType.equals("2")) {
//                                                    map.put("name", "诵经");
//                                                } else if (choosedType.equals("3")) {
//                                                    map.put("name", "持咒");
//                                                }
                                                map.put("id", m1.get("id"));
                                                adapter.mlist.add(0, map);
                                                adapter.notifyDataSetChanged();
                                                listView.setSelection(0);
                                                type.setText("");

                                                num.setText("");
                                                v.setEnabled(true);
                                                dialogInterface.dismiss();
                                                imm.hideSoftInputFromWindow(num.getWindowToken(), 0);
                                            }
                                        });
                                    }

                                } else if ("003".equals(m1.get("code"))) {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            ToastUtil.showToastShort("您还有" + type.getTag(R.id.type_text).toString() + "未完成，请勿重复提交");
                                            dialogInterface.dismiss();
                                            ProgressUtil.dismiss();
                                        }
                                    });

                                }
                            }
                        }
                    } catch (Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ProgressUtil.dismiss();
                            }
                        });
                        e.printStackTrace();
                    }

                }
            }).start();
        } else {
            Toast.makeText(mApplication.getInstance(), mApplication.ST("请仔细填写功课内容"), Toast.LENGTH_SHORT).show();
            v.setEnabled(true);
        }
    }

    public class FayuanAdapter extends BaseAdapter {
        public List<HashMap<String, String>> mlist = new ArrayList<>();
        private int screenWidth;

        @Override
        public int getCount() {
            return mlist == null ? 0 : mlist.size();
        }

        @Override
        public Object getItem(int i) {
            return mlist.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public int getItemViewType(int position) {
            if (!mlist.get(position).get("user_id").equals(sp.getString("user_id", ""))) {
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        public FayuanAdapter() {
            super();
            screenWidth = getResources().getDisplayMetrics().widthPixels;
        }

        public void addList(List<HashMap<String, String>> list) {
            this.mlist.addAll(list);
        }

        @Override
        public View getView(int position, View v, ViewGroup viewGroup) {
            final Holder holder;
            View view = v;
            final HashMap<String, String> map = mlist.get(position);
            if (view == null) {
                holder = new Holder();
                view = LayoutInflater.from(nianfo_home_tab6.this).inflate(R.layout.nianfo_item_fayuan, null);
                holder.imageView = (ImageView) view.findViewById(R.id.nianfo_item_ima);
                holder.txt_date = (TextView) view.findViewById(R.id.nianfo_item_date);
                holder.txt_desc = (TextView) view.findViewById(R.id.nianfo_item_desc);
                holder.txt_name = (TextView) view.findViewById(R.id.nianfo_item_name);
                holder.txt_share = (TextView) view.findViewById(R.id.nianfo_item_share);
                holder.text_status = (TextView) view.findViewById(R.id.nianfo_item_status);
                holder.swipeview = (SwipeMenuLayout) view.findViewById(R.id.swipview);
                holder.text_delete = (TextView) view.findViewById(R.id.delete);
                view.setTag(holder);
            } else {
                holder = (Holder) view.getTag();
            }
            if (getItemViewType(position) == 1) {
                view.setBackgroundColor(Color.parseColor("#eeeeee"));
                holder.txt_share.setVisibility(View.VISIBLE);
                holder.txt_share.setText(mApplication.ST("发愿分享"));
            } else {
                view.setBackgroundColor(Color.WHITE);
                holder.txt_share.setVisibility(View.GONE);

            }
            if(targetTime.equals("")){
                targetTime=map.get("end_time");
            }
            Glide.with(nianfo_home_tab6.this).load(mlist.get(position).get("user_image")).placeholder(R.drawable.def).override(screenWidth * 3 / 20, screenWidth * 3 / 20)
                    .centerCrop().into(holder.imageView);
            String name = (map.get("pet_name").equals("") ? mApplication.ST("佚名") : map.get("pet_name"));
            double target = Double.valueOf(map.get("target_num"));
            double now = Double.valueOf(map.get("num"));
            Double offset = target == 0 ? 0 : (now * 100d / target);
            final long targetTime = TimeUtils.dataOne(map.get("end_time"));

            if((holder.swipeview.isSwipeEnable())){{
                holder.swipeview.quickClose();
            }}
            holder.swipeview.setSwipeEnable(false);
            if (now >= target) {//发愿已达成
                holder.text_status.setText("已达成 ");
                holder.text_status.setBackgroundResource(R.drawable.status_completed_shape_fayuan);
            } else {//发愿未完成
                if (targetTime <= System.currentTimeMillis()) { //发愿已过期
                    holder.text_status.setText("失败 " + offset.intValue() + "%");
                    holder.text_status.setBackgroundResource(R.drawable.status_failed_shape_fayuan);

                } else if (targetTime > System.currentTimeMillis()) {//发愿未过期
                    holder.text_status.setText("进行中 " + offset.intValue() + "%");
                    holder.text_status.setBackgroundResource(R.drawable.status_playing_shape_fayuan);
                    if(getItemViewType(position)==1){
                        if(!holder.swipeview.isSwipeEnable()){{
                            holder.swipeview.setSwipeEnable(true);
                        }}
                        holder.swipeview.quickClose();
                    }
                }
            }


            holder.txt_name.setText(name);
            if (map.get("name") != null) {
                String type = map.get("type");
                String sb = "", sb2 = "";
                if ("1".equals(type)) {
                    sb = "念";
                    sb2 = " 声";
                } else if ("2".equals(type)) {
                    sb = "诵";
                    sb2 = " 部";
                } else if ("3".equals(type)) {
                    sb = "持";
                    sb2 = " 遍";
                }

                SpannableString ss = new SpannableString(mApplication.ST("发愿" + "--" + sb + "  " + map.get("name") + " " + NumUtils.getNumStr(mlist.get(position).get("target_num")) + sb2));
                ss.setSpan(new AbsoluteSizeSpan(17, true), ss.length() - NumUtils.getNumStr(mlist.get(position).get("target_num")).length() - 2, ss.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.txt_desc.setText(ss);


                final String finalSb = sb;
                final String finalSb1 = sb2;
                holder.txt_share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(nianfo_home_tab6.this,HuiXiang.class);
                        intent.putExtra("status", 6);
                        intent.putExtra("name",map.get("name"));
                        intent.putExtra("num",map.get("target_num"));
                        intent.putExtra("sb", finalSb);
                        intent.putExtra("digit", finalSb1);
                        intent.putExtra("nf_id",map.get("id"));
                        intent.putExtra("time",map.get("time"));
                        intent.putExtra("target_time",map.get("end_time"));
                        startActivity(intent);
                    }
                });
            }
            final View finalView = view;
            holder.text_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(nianfo_home_tab6.this);

                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //删除发愿
                            if(!Network.HttpTest(nianfo_home_tab6.this)){
                                return;
                            }
                            JSONObject js=new JSONObject();
                            try {
                                js.put("m_id", Constants.M_id);
                                js.put("user_id",map.get("user_id"));
                                js.put("id",map.get("id"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ApisSeUtil.M m=ApisSeUtil.i(js);
                            LogUtil.e("发愿删除：：："+js);
                            OkGo.post(Constants.Fayuan_Delete).params("key",m.K())
                                    .params("msg",m.M())
                                    .execute(new StringCallback() {
                                        @Override
                                        public void onSuccess(String s, Call call, Response response) {
                                            HashMap<String,String > m= AnalyticalJSON.getHashMap(s);
                                            if(m!=null){
                                                if("000".equals(m.get("code"))){
                                                    ToastUtil.showToastShort("删除成功");
                                                    ((SwipeMenuLayout) finalView.findViewById(R.id.swipview)).quickClose();
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
                    android.app.AlertDialog alertDialog=builder.create();
                    alertDialog.setMessage("确认删除该条发愿吗？");
                    alertDialog.show();

                }
            });

            holder.txt_date.setText(TimeUtils.getTrueTimeStr(mlist.get(position).get("time")));
//            view.setAnimation(AnimationUtils.loadAnimation(nianfo_home_tab6.this,R.anim.umeng_socialize_fade_in));


            return view;
        }

        class Holder {
            ImageView imageView;
            TextView txt_name;
            TextView txt_desc;
            TextView txt_date;
            TextView txt_share, text_status,text_delete;
            SwipeMenuLayout swipeview;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(TAG);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ProgressUtil.dismiss();
    }

    @Override
    public void onRefresh() {
        page = "1";
        isRefresh = true;
        endPage = "";
        loadData();
    }

    @Override
    public void loadMore() {
        if (!endPage.equals(page)) {
            page = String.valueOf(Integer.parseInt(page) + 1);
        } else {
            final ProgressBar p = (ProgressBar) (listView.footer.findViewById(R.id.load_more_bar));
            final TextView t = (TextView) (listView.footer.findViewById(R.id.load_more_text));
            p.setVisibility(View.GONE);
            t.setText(mApplication.ST("没有更多数据了"));
            return;
        }

        loadData();
    }
}
