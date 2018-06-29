package com.yunfengsi.Fragment;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.yunfengsi.MainActivity;
import com.yunfengsi.Managers.Base.BaseSTFragement;
import com.yunfengsi.Managers.ForManager.ForManagers;
import com.yunfengsi.Managers.MessageCenter;
import com.yunfengsi.Managers.MineManager;
import com.yunfengsi.Models.Auction.AuctionList;
import com.yunfengsi.Models.BlessTree.BlessTree;
import com.yunfengsi.Models.E_Book.BookList;
import com.yunfengsi.Models.Model_activity.Mine_activity_list;
import com.yunfengsi.Models.More.Fortune;
import com.yunfengsi.Models.More.Meditation;
import com.yunfengsi.Models.NianFo.GYMX;
import com.yunfengsi.Models.NianFo.GYMX_FaYuan;
import com.yunfengsi.Models.NianFo.NianFo;
import com.yunfengsi.Models.NianFo.nianfo_home_tab4;
import com.yunfengsi.Models.NianFo.nianfo_home_tab5;
import com.yunfengsi.Models.TouGao.TouGao;
import com.yunfengsi.Models.WallPaper.WallPapaerHome;
import com.yunfengsi.Models.YunDou.MyQuan;
import com.yunfengsi.Models.YunDou.YunDouAwardDialog;
import com.yunfengsi.Models.YunDou.YunDouHome;
import com.yunfengsi.R;
import com.yunfengsi.Setting.Activity_ShouCang;
import com.yunfengsi.Setting.Login;
import com.yunfengsi.Setting.Mine_HuiYuan;
import com.yunfengsi.Setting.Mine_gerenziliao;
import com.yunfengsi.Setting.Month_Detail;
import com.yunfengsi.Setting.NiCTemple_Activity;
import com.yunfengsi.Setting.Sign;
import com.yunfengsi.Setting.gerenshezhi;
import com.yunfengsi.Utils.ACache;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.ImageUtil;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.LoginUtil;
import com.yunfengsi.Utils.MD5Utls;
import com.yunfengsi.Utils.Network;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.ProgressUtil;
import com.yunfengsi.Utils.SystemUtil;
import com.yunfengsi.Utils.TimeUtils;
import com.yunfengsi.Utils.mApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;
import okhttp3.Call;
import okhttp3.Response;

//import com.hyphenate.EMCallBack;
//import com.hyphenate.chat.EMClient;


/**
 * Created by Administrator on 2016/5/31.
 */
public class Mine extends BaseSTFragement implements View.OnClickListener {
    private static final String TAG          = "Mine";
    private static final int    CHOOSEPICTUE = 2;//相册
    private static final int    TAKEPICTURE  = 1;//相机

    private Uri pictureUri = null;
    private AlertDialog       dialog;
    public  ImageView         head;
    public  SharedPreferences sp;
    public  ACache            aCache;
    private int               screenWidth;
    public  String            path;
    private File              Headfile;
    //    private TextView tab2, tab3;
//    private ViewPager viewpager;
//    private FragmentManager fm;
//    private List<Fragment> list;
    private TextView          qiehuanzhanghao;
    private TextView          petname, sign;
    private LinearLayout geren, shoucang, tougao, zhifu, bangzhu, huodong;


    private LinearLayout lyone;
    private LinearLayout lytwo;
    private LinearLayout lythree;
    private LinearLayout lyfour;
    private LinearLayout lyfive;
    private ImageView    Msg;


    //我的云豆
    private FrameLayout header;



    private MineManager  mineManager;
    private Intent intent = new Intent();
    private ImageView level;
    private boolean           isAgreeed    = false;//是否同意隐私政策
    private BroadcastReceiver userReseiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!intent.getBooleanExtra("level", false)) {
                updateInfo();
                Log.w(TAG, "onReceive: 状态改变");
            } else {
                getLevelInfo();
            }

        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View         v            = inflater.inflate(R.layout.mine, container, false);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(TAG);
        getActivity().registerReceiver(userReseiver, intentFilter);
        sp = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        LogUtil.e("当前账号Id:" + sp.getString("user_id", ""));
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        aCache = ACache.get(mApplication.getInstance());
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recycle);
        mineManager = new MineManager(getActivity(), recyclerView);
        isAgreeed = sp.getString("agree_status", "").equals("0") || sp.getString("agree_status", "").equals("") ? false : true;

        /**
         * 点击打开个人二维码  二维码规则：对user_id进行加密
         */
        v.findViewById(R.id.qr).setOnClickListener(this);
        if (!new LoginUtil().checkLogin(getActivity())) {
            v.findViewById(R.id.qr).setVisibility(View.GONE);
        }

//        header=v.findViewById(R.id.header);
//        header.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (new LoginUtil().checkLogin(getActivity())) {
//                    startActivity(new Intent(getActivity(), MemberCenter.class));
//                }
//            }
//        });

        mineManager.setOnitemClickListener(onItemClickListener);
        ((ImageView) v.findViewById(R.id.mine_gerenbeijing)).setImageBitmap(ImageUtil.readBitMap(getActivity(), R.drawable.mine_banner));
        ((ImageView) v.findViewById(R.id.mine_gerenbeijing)).setColorFilter(Color.parseColor("#30000000"));
        head = (ImageView) v.findViewById(R.id.mine_head);
        petname = (TextView) v.findViewById(R.id.mine_petName);
        sign = (TextView) v.findViewById(R.id.mine_sign);
        level = (ImageView) v.findViewById(R.id.level);
        switch (sp.getString("level", "0")) {
            case "0":
                level.setVisibility(View.GONE);
                break;
            case "1":
                level.setVisibility(View.VISIBLE);
                Glide.with(getActivity())
                        .load(R.drawable.gif1)
                        .asGif()
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE).into(level);
                break;
            case "2":
                level.setVisibility(View.VISIBLE);
                Glide.with(getActivity())
                        .load(R.drawable.gif2)
                        .asGif()
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE).into(level);
                break;
            case "3":
            case "4":
                level.setVisibility(View.VISIBLE);
                Glide.with(getActivity())
                        .load(R.drawable.gif3)
                        .asGif()
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE).into(level);
                break;
        }
        SetHead();
        if (!sp.getString("pet_name", "").equals("")) {
            petname.setText(sp.getString("pet_name", ""));
        }
        if (!sp.getString("sign", "").equals("")) {
            sign.setText(sp.getString("sign", ""));
        } else {
            sign.setText("暂无个性签名");
        }

        head.setOnClickListener(this);
        sign.setOnClickListener(this);
        petname.setOnClickListener(this);
        updateInfo();
        getLevelInfo();
        return v;
    }

    BaseQuickAdapter.OnItemClickListener onItemClickListener=new BaseQuickAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            HashMap<String, Object> map = mineManager.getMaps().get(position);

            switch (map.get(MineManager.text).toString()) {
                case "义卖":
                    startActivity(new Intent(getActivity(), AuctionList.class));
                    break;
                case "管理员":
                    startActivity(new Intent(getActivity(), ForManagers.class));
                    break;
                case "壁纸":
                    startActivity(new Intent(getActivity(), WallPapaerHome.class));
                    break;
                case "我的云豆":
                    if (new LoginUtil().checkLogin(getActivity())) {
                        startActivity(new Intent(getActivity(), YunDouHome.class));
                    }
                    break;
                case "我的福利":
                    if (new LoginUtil().checkLogin(getActivity())) {
                        startActivity(new Intent(getActivity(), MyQuan.class));
                    }
                    break;
                case "祈愿树":
                    if (new LoginUtil().checkLogin(getActivity())) {
                        startActivity(new Intent(getActivity(), BlessTree.class));
                    }
                    break;
                case "佛经":
//                        if (new LoginUtil().checkLogin(getActivity())) {
                    startActivity(new Intent(getActivity(), BookList.class));
//                        }
                    break;
                case "卜事":
                    if (new LoginUtil().checkLogin(getActivity())) {
                        startActivity(new Intent(getActivity(), Fortune.class));
                    }
                    break;
                case "坐禅":
                    if (new LoginUtil().checkLogin(getActivity())) {
                        startActivity(new Intent(getActivity(), Meditation.class));
                    }
                    break;
                case "通知":
                    if (new LoginUtil().checkLogin(getActivity())) {
                        intent.setClass(getActivity(), MessageCenter.class);
                        startActivity(intent);
                    }
                    break;
                case "投稿":
                    if (new LoginUtil().checkLogin(getActivity())) {
                        intent.setClass(getActivity(), TouGao.class);
                        startActivity(intent);
                    }
                    break;
                case "切换账号":
                    CloudPushService pushService = PushServiceFactory.getCloudPushService();
                    pushService.removeAlias(sp.getString("user_id", ""), new CommonCallback() {
                        @Override
                        public void onSuccess(String s) {
                            LogUtil.e("解绑成功");
                        }

                        @Override
                        public void onFailed(String s, String s1) {
                            LogUtil.e("解绑失败");
                        }
                    });
                    SharedPreferences.Editor ed = sp.edit();
                    ed.putString("uid", "");
                    ed.putString("user_id", "");
                    ed.putString("head_path", "");
                    ed.putString("head_url", "");
                    ed.putString("phone", "");
                    ed.putString("perfect", "1");
                    ed.putString("signature", "");
                    ed.putString("sex", "");
                    ed.putLong("time", 0);
                    ed.putString("pet_name", "");
                    ed.putString("level", "0");
                    ed.putString("tougao_title", "");
                    ed.putString("tougao_content", "");
                    ed.putString("role", "1");
                    aCache.remove("head_" + sp.getString("user_id", ""));
                    ed.apply();
                    Glide.with(getActivity()).load(R.drawable.indra).into(head);
                    petname.setText("昵称");
                    intent.setClass(mApplication.getInstance(), Login.class);


                    startActivity(intent);
                    getActivity().finish();
                    break;
                case "感谢信":
                    if (!new LoginUtil().checkLogin(getActivity())) {
                        return;
                    }
                    intent.setClass(getActivity(), Month_Detail.class);
                    startActivity(intent);
                    break;
                case "功德":
                    if (!new LoginUtil().checkLogin(getActivity())) {
                        return;
                    }
                    intent.setClass(getActivity(), Mine_GYQD.class);
                    startActivity(intent);
                    break;
                case "活动":
                    if (new LoginUtil().checkLogin(getActivity())) {
                        intent.setClass(getActivity(), Mine_activity_list.class);
                        startActivity(intent);
                    }

                    break;
                case "共修":
                    intent.setClass(getActivity(), NianFo.class);

                    startActivity(intent);
                    break;
                case "收藏":
                    if (!new LoginUtil().checkLogin(getActivity())) {
                        return;
                    }
                    intent.setClass(getActivity(), Activity_ShouCang.class);
                    startActivity(intent);
                    break;
                case "会员中心":
                    if (!new LoginUtil().checkLogin(getActivity())) {
                        return;
                    }
                    if (view.findViewById(R.id.badge).getVisibility() == View.VISIBLE) {
                        sp.edit().putLong("time", System.currentTimeMillis()).apply();
                        view.findViewById(R.id.badge).setVisibility(View.GONE);
                        ((MainActivity) getActivity()).tabLayout.getTabAt(4)
                                .getCustomView().findViewById(R.id.badge)
                                .setVisibility(View.GONE);
                    }
                    intent.setClass(getActivity(), Mine_HuiYuan.class);
                    startActivity(intent);
                    break;
                case "功课":
                    openGongke();
                    break;
                case "设置":
                    intent.setClass(getActivity(), gerenshezhi.class);
                    startActivity(intent);
                    break;
            }
        }
    };
    public void openGongke() {
        View vi = LayoutInflater.from(getActivity()).inflate(R.layout.mine_gongke_fragment, null);
        TextView tvone = (TextView) vi.findViewById(R.id.mine_gongke_fragment_oneitme);

        TextView tvtwo   = (TextView) vi.findViewById(R.id.mine_gongke_fragment_twoitme);
        TextView tvthree = (TextView) vi.findViewById(R.id.mine_gongke_fragment_threeitme);
        TextView tvfour  = (TextView) vi.findViewById(R.id.mine_gongke_fragment_fouritme);
        TextView tvfive  = (TextView) vi.findViewById(R.id.mine_gongke_fragment_fiveitme);
        TextView tv6     = (TextView) vi.findViewById(R.id.mine_gongke_fragment_sixitme);
        TextView cancle = (TextView) vi.findViewById(R.id.dismiss);
        cancle.setText(mApplication.ST("取消"));
        tvone.setText(mApplication.ST("念佛"));
        tvtwo.setText(mApplication.ST("诵经"));
        tvthree.setText(mApplication.ST("持咒"));
        tv6.setText(mApplication.ST("发愿"));
        tvfour.setText(mApplication.ST("助念"));
        tvfive.setText(mApplication.ST("忏悔"));
        tvone.setOnClickListener(Mine.this);
        tvtwo.setOnClickListener(Mine.this);
        tvthree.setOnClickListener(Mine.this);
        tvfour.setOnClickListener(Mine.this);
        tv6.setOnClickListener(Mine.this);
        tvfive.setOnClickListener(Mine.this);

        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        final AlertDialog   d = b.create();
        d.setView(vi);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });

        Window window = d.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.dialogWindowAnim);
        d.show();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (this != null && isVisibleToUser) {
            getLevelInfo();
        }
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        getLevelInfo();
//    }

    // TODO: 2017/4/19 重置简繁
    @Override
    protected void resetData() {
        mineManager.notifyDataChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(userReseiver);
        OkGo.getInstance().cancelTag(TAG);
        mineManager.saveMySetting();
    }

    public void getLevelInfo() {
        JSONObject js = new JSONObject();
        try {
            js.put("m_id", Constants.M_id);
            js.put("user_id", PreferenceUtil.getUserIncetance(getActivity()).getString("user_id", ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m
                = ApisSeUtil.i(js);
        OkGo.post(Constants.getLevelInfo).tag(TAG)
                .params("key", m.K())
                .params("msg", m.M())
//                .params("key", Constants.safeKey)
//                .params("m_id", Constants.M_id)
//                .params("user_id", PreferenceUtil.getUserIncetance(getActivity()).getString("user_id", ""))
                .execute(new AbsCallback<Object>() {
                    @Override
                    public Object convertSuccess(Response response) throws Exception {
                        return null;
                    }

                    @Override
                    public void onSuccess(Object o, Call call, Response response) {
                        try {
                            String data = response.body().string();
                            if (data != null && !TextUtils.isEmpty(data)) {
                                HashMap<String, String> map = AnalyticalJSON.getHashMap(data);
                                if (map != null) {
                                    if ("000".equals(map.get("code"))) {
                                        SharedPreferences.Editor editor = sp.edit();
                                        switch (map.get("level")) {
                                            case "0":
                                                level.setVisibility(View.GONE);
                                                break;
                                            case "1":
                                                level.setVisibility(View.VISIBLE);
                                                Glide.with(getActivity())
                                                        .load(R.drawable.gif1)
                                                        .asGif()
                                                        .diskCacheStrategy(DiskCacheStrategy.SOURCE).into(level);
                                                break;
                                            case "2":
                                                level.setVisibility(View.VISIBLE);
                                                Glide.with(getActivity())
                                                        .load(R.drawable.gif2)
                                                        .asGif()
                                                        .diskCacheStrategy(DiskCacheStrategy.SOURCE).into(level);
                                                break;
                                            case "3":
                                            case "4":
                                                level.setVisibility(View.VISIBLE);
                                                Glide.with(getActivity())
                                                        .load(R.drawable.gif3)
                                                        .asGif()
                                                        .diskCacheStrategy(DiskCacheStrategy.SOURCE).into(level);
                                                break;



                                        }
                                        editor.putString("level", map.get("level"));
                                        editor.commit();
                                    } else {
                                        LogUtil.e("获取等级信息失败");
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }


                });

    }

    Handler handler = new Handler();

    private void updateInfo() {
        final Intent intent = new Intent();
        if (TextUtils.isEmpty(sp.getString("user_id", ""))) {
            return;
        }
        final JSONObject js = new JSONObject();
        try {
            js.put("m_id", Constants.M_id);
            js.put("type", "1");
            js.put("phonename", SystemUtil.getSystemModel());
            js.put("user_id", PreferenceUtil.getUserIncetance(getActivity()).getString("user_id", ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e("我的：：：；个人信息：：" + js);
        final ApisSeUtil.M m = ApisSeUtil.i(js);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String data = OkGo.post(Constants.User_Info_Ip).tag(TAG)
                            .params("key", m.K()).params("msg", m.M())

                            .execute().body().string();

                    if (!TextUtils.isEmpty(data)) {
                        final HashMap<String, String>  map = AnalyticalJSON.getHashMap(data);
                        final SharedPreferences.Editor ed  = sp.edit();
                        if (handler != null) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (map != null) {
                                        //notice   消息中心    1有新消息  2 没有新消息
                                        if ("1".equals(map.get("notice"))) {
                                            ((MainActivity) getActivity()).notice.setSelected(true);
                                        } else if ("2".equals(map.get("notice"))) {
                                            ((MainActivity) getActivity()).notice.setSelected(false);
                                        }
                                        if (!"".equals(map.get("user_image"))) {
                                            ed.putString("head_url", map.get("user_image"));
                                            Glide.with(Mine.this).load(map.get("user_image")).asBitmap().override(90, 90).into(new BitmapImageViewTarget(head) {
                                                @Override
                                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                                    aCache.put("head_" + sp.getString("user_id", ""), resource);
                                                    head.setImageBitmap(resource);
                                                }
                                            });
                                        }
                                        if (!"".equals(map.get("sex"))) {
                                            ed.putString("sex", map.get("sex"));
                                        }
                                        if (!"0".equals(map.get("yundousum"))) {
                                            YunDouAwardDialog.show(getActivity(), "每日登录", map.get("yundousum"));
                                        }
                                        if (!"".equals(map.get("signature"))) {
                                            ed.putString("signature", map.get("signature"));
                                            sign.setText(map.get("signature"));
                                        }
                                        if (!"".equals(map.get("pet_name"))) {
                                            ed.putString("pet_name", map.get("pet_name"));
                                            petname.setText(map.get("pet_name"));
                                        }
                                        if (!"".equals(map.get("role"))) {//管理员权限缓存
                                            ed.putString("role", map.get("role"));
                                        }
                                        if ("".equals(map.get("pet_name")) || "".equals(map.get("sex"))) {
                                            intent.setClass(getActivity(), Mine_gerenziliao.class);
                                            startActivity(intent);
                                        }
                                        //1 未完善资料  2 已完善资料
                                        ed.putString("perfect", map.get("perfect"));
                                        // TODO: 2017/5/12 判断会员是否过期并提示
                                        LogUtil.e("上次点击事件：：：" + sp.getLong("time", 0) +
                                                "   是否同一天：：" + TimeUtils.isSameDate(sp.getLong("time", 0), System.currentTimeMillis()));
                                        if (!TimeUtils.isSameDate(sp.getLong("time", 0), System.currentTimeMillis())) {
                                            if (map.get("time") != null && !map.get("time").equals("")) {

                                                long endTime = TimeUtils.dataOne(map.get("time"));
                                                long t       = TimeUtils.getAddMonthDate(1);
                                                if (endTime - t <= 0) {
                                                    ((MainActivity) getActivity()).tabLayout.getTabAt(4)
                                                            .getCustomView().findViewById(R.id.badge)
                                                            .setVisibility(View.VISIBLE);
                                                    mineManager.chageRedPoint();
                                                } else if (endTime - System.currentTimeMillis() <= 0) {
                                                    LogUtil.e("会员已过期");
                                                } else if (endTime - t > 0) {
                                                    ed.putLong("time", 0);
                                                }
                                                LogUtil.e("当前事件:::" + System.currentTimeMillis());
                                                LogUtil.e("结束事件:::" + endTime);
                                                LogUtil.e("当前事件+一个月::" + t);
                                                LogUtil.e("!~!~!~:::" + TimeUtils.dataOne("2017-06-12 16:00:00"));
                                            }
                                        }
                                        if ("0".equals(map.get("agree_status"))) {
                                            isAgreeed = false;
                                            ed.putString("agree_status", "0");//是否同意隐私政策  0未同意  1为同意
                                            ed.commit();
                                        } else {
                                            isAgreeed = true;
                                            ed.putString("agree_status", map.get("agree_status"));//是否同意隐私政策  0未同意  1为同意
                                            ed.commit();
                                        }
                                        if (!isAgreeed) {
                                            getProl();
                                        }

                                    }


                                    if(mineManager!=null){
                                        mineManager.initMine();
                                        mineManager.setOnitemClickListener(onItemClickListener);
                                    }

                                }
                            });


                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void getProl() {
        JSONObject js = new JSONObject();
        try {
            js.put("m_id", Constants.M_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m = ApisSeUtil.i(js);
        OkGo.post(Constants.little_yhxy__IP).tag(TAG).params("key", m.K()).params("msg", m.M())
                .execute(new AbsCallback<Object>() {
                    @Override
                    public Object convertSuccess(Response response) throws Exception {
                        return null;
                    }

                    @Override
                    public void onSuccess(Object o, Call call, Response response) {
                        try {
                            String data = response.body().string();
                            if (!data.equals("")) {
                                final HashMap<String, String> map = AnalyticalJSON.getHashMap(data);
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (map != null) {
                                            View          view   = LayoutInflater.from(getActivity()).inflate(R.layout.activity_confirm_dialog, null);
                                            final WebView web    = (WebView) view.findViewById(R.id.web);
                                            TextView      cancle = (TextView) view.findViewById(R.id.cancle);
                                            cancle.setText(mApplication.ST("不同意"));
                                            final TextView baoming = (TextView) view.findViewById(R.id.baoming);
                                            baoming.setEnabled(false);
                                            final CountDownTimer cdt = new CountDownTimer(10000, 1000) {
                                                @Override
                                                public void onTick(long millisUntilFinished) {
                                                    baoming.setText(mApplication.ST("请阅读隐私政策(" + millisUntilFinished / 1000 + "秒)"));
                                                }

                                                @Override
                                                public void onFinish() {
                                                    baoming.setText(mApplication.ST("同意"));
                                                    baoming.setEnabled(true);
                                                }
                                            };
                                            web.loadDataWithBaseURL("", map.get("privacy")
                                                    , "text/html", "UTF-8", null);
//
                                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                            builder.setView(view);
                                            final AlertDialog dialog = builder.create();
                                            cancle.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    web.destroy();
                                                    cdt.cancel();
                                                    dialog.dismiss();
                                                    if (MainActivity.activity != null) {
                                                        MainActivity.activity.finish();
                                                        MainActivity.activity = null;
                                                    }
                                                    System.exit(0);
                                                }
                                            });
                                            baoming.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    dialog.dismiss();
                                                    agreeSecret();
                                                }
                                            });
                                            cdt.start();
                                            builder.setCancelable(false);
                                            web.setWebViewClient(new WebViewClient() {
                                                @Override
                                                public void onPageFinished(WebView view, String url) {
                                                    super.onPageFinished(view, url);
                                                    dialog.show();
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }


                });
    }

    //同意隐私政策
    private void agreeSecret() {
        if (Network.HttpTest(getActivity())) {
            if (new LoginUtil().checkLogin(getActivity())) {
                JSONObject js = new JSONObject();
                try {
                    js.put("user_id", PreferenceUtil.getUserId(getActivity()));
                    js.put("m_id", Constants.M_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ApisSeUtil.M m = ApisSeUtil.i(js);
                OkGo.post(Constants.yhxy_agree).tag(TAG)
                        .params("key", m.K())
                        .params("msg", m.M())
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(String s, Call call, Response response) {
                                SharedPreferences.Editor ed = sp.edit();
                                ed.putString("agree_status", "0");//是否同意隐私政策  0未同意  1为同意
                                ed.commit();

                            }

                            @Override
                            public void onBefore(BaseRequest request) {
                                super.onBefore(request);
                                ProgressUtil.show(getActivity(), "", "请稍等");
                            }

                            @Override
                            public void onAfter(String s, Exception e) {
                                super.onAfter(s, e);
                                ProgressUtil.dismiss();
                            }
                        });
            }

        }
    }

    public void SetHead() {
        Bitmap bm = aCache.getAsBitmap("head_" + sp.getString("user_id", ""));
        if (bm != null) {
            Log.w(TAG, "SetHead:  Cache");
            head.setImageBitmap(bm);
            return;
        }
        if (path != null && !("").equals(path)) {
            File file = new File(sp.getString("head_path", ""));
            if (file.exists() && file.isFile()) {
                Log.w(TAG, "SetHead:  file");
                Glide.with(this).load(path).asBitmap().into(new BitmapImageViewTarget(head) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        aCache.put("head_" + sp.getString("user_id", ""), resource);
                        head.setImageBitmap(resource);
                    }
                });
                return;
            }
        }
        if (!sp.getString("head_url", "").equals("")) {
            Glide.with(mApplication.getInstance()).load(sp.getString("head_url", "")).asBitmap()
                    .override(screenWidth / 4, screenWidth / 4).into(new BitmapImageViewTarget(head) {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    if (resource != null) {
                        aCache.put("head_" + sp.getString("user_id", ""), resource);
                        head.setImageBitmap(resource);
                    }
                }
            });
        }
    }


    public void uploadHead(final File file) {
        final String uid = sp.getString("user_id", "");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject js = new JSONObject();
                    js.put("user_id", uid);
                    js.put("m_id", Constants.M_id);
                    Response response = OkGo.post(Constants.uploadHead_IP).tag(TAG)
                            .params("head", file).params("key", ApisSeUtil.getKey())
                            .params("msg", ApisSeUtil.getMsg(js)).execute();
                    String data1 = response.body().string();

                    if (!data1.equals("")) {
                        HashMap<String, String> map = AnalyticalJSON.getHashMap(data1);
                        if (map != null && null != map.get("code")) {
                            if ("000".equals(map.get("code"))) {
                                SharedPreferences.Editor ed  = sp.edit();
                                String                   url = map.get("head");
                                if (url != null) {
                                    ed.putString("head_url", url);
                                    ed.apply();
                                }
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(mApplication.getInstance(), mApplication.ST("头像更改成功"), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(mApplication.getInstance(), mApplication.ST("头像更改失败"), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        } else {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mApplication.getInstance(), mApplication.ST("服务器异常"), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mApplication.getInstance(), mApplication.ST("服务器异常"), Toast.LENGTH_SHORT).show();
                        }
                    });
                    e.printStackTrace();
                }

            }
        }).start();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            switch (requestCode) {
                case CHOOSEPICTUE:
                    Bitmap bm = null;
                    if (dialog != null)
                        dialog.dismiss();
                    pictureUri = data.getData();// 选择照片的Uri 可能为null
                    if (pictureUri != null) {
                        //上传头像
                        path = ImageUtil.getImageAbsolutePath(mApplication.getInstance(), pictureUri);
                        if (path.endsWith("webp") || path.endsWith("WEBP")) {
                            Toast.makeText(getActivity(), mApplication.ST("暂不支持该图片格式，请重新选择"), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        bm = ImageUtil.getImageThumbnail(path, screenWidth / 4, screenWidth / 4);
                        head.setImageBitmap(bm);
                        FileOutputStream faos = null;
                        try {
                            faos = new FileOutputStream(path);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        bm.compress(Bitmap.CompressFormat.JPEG, 60, faos);
                        Log.w(TAG, "onActivityResult: size-=-=-=" + bm.getByteCount());
                        try {
                            if (faos != null) {
                                faos.flush();
                                Headfile = new File(path);
                                faos.close();
                            } else {
                                Toast.makeText(mApplication.getInstance(), mApplication.ST("上传失败,请重新尝试"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Log.w(TAG, "onActivityResult: ___________." + new File(path).length());

                        SharedPreferences.Editor ed = sp.edit();
                        aCache.put("head_" + sp.getString("user_id", ""), bm);
                        ed.putString("head_path", path);
                        ed.apply();
                        uploadHead(Headfile);
                    } else {
                        Toast.makeText(mApplication.getInstance(), mApplication.ST("上传失败,请重新尝试"), Toast.LENGTH_SHORT).show();
                    }


                    break;
                case TAKEPICTURE:
                    if (dialog != null)
                        dialog.dismiss();
                    if (pictureUri != null) {
                        //上传头像
                        path = ImageUtil.getRealPathFromURI(getActivity(), pictureUri);
                        bm = ImageUtil.getImageThumbnail(path, screenWidth / 4, screenWidth / 4);
                        head.setImageBitmap(bm);
                        FileOutputStream faos = null;
                        try {
                            faos = new FileOutputStream(path);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        bm.compress(Bitmap.CompressFormat.JPEG, 60, faos);

                        try {
                            if (faos != null) {
                                faos.flush();
                                Headfile = new File(path);
                                faos.close();
                            } else {
                                Toast.makeText(mApplication.getInstance(), mApplication.ST("上传失败,请重新尝试"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.w(TAG, "onActivityResult: ___________." + new File(path).length());
                        SharedPreferences.Editor ed = sp.edit();
                        aCache.put("head_" + sp.getString("user_id", ""), bm);
                        ed.putString("head_path", path);
                        ed.apply();
                        uploadHead(Headfile);
                    } else {
                        Toast.makeText(mApplication.getInstance(), mApplication.ST("上传失败,请重新尝试"), Toast.LENGTH_SHORT).show();
                    }


                    break;
            }
        } else if (resultCode == 4) {
            sign.setText(data.getStringExtra("sign"));
        }

    }


    @Override
    public void onClick(final View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.qr:

                String md5 = MD5Utls.stringToMD5(Constants.safeKey);
                String m1 = md5.substring(0, 16);
                String m2 = md5.substring(16, md5.length());
                LogUtil.e("m1:::" + m1 + "\n" + "m2:::" + m2);
                final StringBuilder builder = new StringBuilder(m1);
                builder.append(PreferenceUtil.getUserId(getActivity()));
                builder.append(m2);
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        final Bitmap bitmap = QRCodeEncoder.syncEncodeQRCode(builder.toString(), DimenUtils.dip2px(getActivity(),240),Color.BLACK,aCache.getAsBitmap("head_"+PreferenceUtil.getUserId(getActivity())));
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LogUtil.e("bitmap：："+bitmap.getByteCount());
                                AlertDialog.Builder builder1  = new AlertDialog.Builder(getActivity());
                                ImageView           imageView = new ImageView(getActivity());
                                imageView.setImageBitmap(bitmap);
                                AlertDialog dialog = builder1.setView(imageView).create();
                                dialog.getWindow().getDecorView().setPadding(0, 0, 0, 0);
                                dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
                                WindowManager.LayoutParams wl = dialog.getWindow().getAttributes();
                                wl.width = DimenUtils.dip2px(getActivity(), 240);
                                wl.height = DimenUtils.dip2px(getActivity(), 240);
                                dialog.getWindow().setAttributes(wl);
                                dialog.show();
                            }
                        });
                    }
                }.start();

                break;
//            case R.id.mine_pinglun:
//                intent.setClass(getActivity(), MessageManager.class);
//                startActivity(intent);
//                break;
            case R.id.mine_petName:
                intent.setClass(getActivity(), NiCTemple_Activity.class);
                intent.putExtra("title", "昵称");
                startActivity(intent);
                break;
     /*
            签名点击修改签名
             */
            case R.id.mine_sign:
                if (!new LoginUtil().checkLogin(getActivity())) {
                    return;
                }
                intent = new Intent(getActivity(), Sign.class);
                startActivityForResult(intent, 4);
                break;
            case R.id.mine_head:
                if (sp.getString("uid", "").equals("") || sp.getString("user_id", "").equals("")) {
                    intent = new Intent(getActivity(), Login.class);
                    startActivity(intent);
                } else {
                    choosePic();
                }
                break;

            case R.id.mine_gongke_fragment_oneitme: //功课--->nianfo
                intent.setClass(getActivity(), GYMX.class);
                intent.putExtra("type", "念佛");
                startActivity(intent);

                break;
            case R.id.mine_gongke_fragment_twoitme://功课--->诵经
                intent.setClass(getActivity(), GYMX.class);
                intent.putExtra("type", "诵经");
                startActivity(intent);
                break;
            case R.id.mine_gongke_fragment_threeitme://功课--->持咒
                intent.setClass(getActivity(), GYMX.class);
                intent.putExtra("type", "持咒");
                startActivity(intent);
                break;
            case R.id.mine_gongke_fragment_fouritme://功课--->助念
                intent.setClass(getActivity(), nianfo_home_tab4.class);
                startActivity(intent);
                break;
            case R.id.mine_gongke_fragment_fiveitme:  //功课--->忏悔
                intent.setClass(getActivity(), nianfo_home_tab5.class);
                startActivity(intent);
                break;
            case R.id.mine_gongke_fragment_sixitme:
                intent.setClass(mApplication.getInstance(), GYMX_FaYuan.class);
                intent.putExtra("type", "发愿");
                startActivity(intent);
                break;
        }
    }

    //选择照片或照相
    private void choosePic() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View                view    = LayoutInflater.from(mApplication.getInstance()).inflate(R.layout.dialog_album_camera, null);
        view.findViewById(R.id.photograph).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int permission1 = ContextCompat.checkSelfPermission(mApplication.getInstance(), Manifest.permission.CAMERA);
                    if (permission1 != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, TAKEPICTURE);
                    } else {
                        chooseCamera();
                        dialog.dismiss();
                    }
                } else {
                    chooseCamera();
                    dialog.dismiss();
                }
            }
        });
        view.findViewById(R.id.album).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int permissionCheck  = ContextCompat.checkSelfPermission(mApplication.getInstance(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    int permissionCheck1 = ContextCompat.checkSelfPermission(mApplication.getInstance(), Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS);
                    int permissionCheck2 = ContextCompat.checkSelfPermission(mApplication.getInstance(), Manifest.permission.READ_EXTERNAL_STORAGE);
                    if (permissionCheck
                            != PackageManager.PERMISSION_GRANTED && permissionCheck1 != PackageManager.PERMISSION_GRANTED
                            && permissionCheck2 != PackageManager.PERMISSION_GRANTED) {
//
                        requestPermissions(
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS
                                        , Manifest.permission.READ_EXTERNAL_STORAGE},
                                CHOOSEPICTUE);
                    } else {
                        choosePhotoAlbum();
                        dialog.dismiss();
                    }
                } else {
                    choosePhotoAlbum();
                    dialog.dismiss();
                }

            }
        });
        builder.setView(view);
        dialog = builder.create();
        view.findViewById(R.id.cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Window window = dialog.getWindow();

        window.setGravity(Gravity.BOTTOM);
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setWindowAnimations(R.style.dialogWindowAnim);
        window.setBackgroundDrawableResource(R.color.vifrification);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.width = getActivity().getResources().getDisplayMetrics().widthPixels;
        wl.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(wl);
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CHOOSEPICTUE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    choosePhotoAlbum();
                } else {
                    Toast.makeText(mApplication.getInstance(), mApplication.ST("无相册权限将无法使用该功能"), Toast.LENGTH_SHORT).show();
                }
                break;
            case TAKEPICTURE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(mApplication.getInstance(), mApplication.ST("无相机权限将无法使用该功能"), Toast.LENGTH_SHORT).show();
                }

        }
    }

    /**
     * 调用相机
     */
    private void chooseCamera() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss",
                Locale.CHINA);
        // Standard Intent action that can be sent to have the camera
        // application capture an image and return it.
        Intent        takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ContentValues attrs             = new ContentValues();
        attrs.put(MediaStore.Images.Media.DISPLAY_NAME,
                dateFormat.format(new Date(System.currentTimeMillis())));// 添加照片名字
        attrs.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");// 图片类型
        attrs.put(MediaStore.Images.Media.DESCRIPTION, "");// 图片描述
        // //插入图片 成功则返回图片所对应的URI 当然我们可以自己指定图片的位置
        pictureUri = getActivity().getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, attrs);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);// 指定照片的位置
        startActivityForResult(takePictureIntent, TAKEPICTURE);

    }

    /**
     * 相册
     */
    private void choosePhotoAlbum() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setType("image/*");
//        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, CHOOSEPICTUE);

    }


}
