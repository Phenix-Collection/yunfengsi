package com.qianfujiaoyu.Activitys;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.callback.FileCallback;
import com.qianfujiaoyu.Base.BaseActivity;
import com.qianfujiaoyu.Base.HomeManager;
import com.qianfujiaoyu.Base.ScaleImageUtil;
import com.qianfujiaoyu.Model_Order.Mine_GYQD;
import com.qianfujiaoyu.Model_Order.Order_Tab;
import com.qianfujiaoyu.Model_Order.Order_detail;
import com.qianfujiaoyu.Model_activity.activity_Detail;
import com.qianfujiaoyu.Model_activity.activity_fragment;
import com.qianfujiaoyu.R;
import com.qianfujiaoyu.Setting.Activity_ShouCang;
import com.qianfujiaoyu.Setting.Mine_gerenziliao;
import com.qianfujiaoyu.Setting.gerenshezhi;
import com.qianfujiaoyu.SideListview.Contact;
import com.qianfujiaoyu.Utils.AnalyticalJSON;
import com.qianfujiaoyu.Utils.ApisSeUtil;
import com.qianfujiaoyu.Utils.Constants;
import com.qianfujiaoyu.Utils.DimenUtils;
import com.qianfujiaoyu.Utils.FileUtils;
import com.qianfujiaoyu.Utils.ImageUtil;
import com.qianfujiaoyu.Utils.LogUtil;
import com.qianfujiaoyu.Utils.LoginUtil;
import com.qianfujiaoyu.Utils.PermissionUtil;
import com.qianfujiaoyu.Utils.PreferenceUtil;
import com.qianfujiaoyu.Utils.ShareManager;
import com.qianfujiaoyu.Utils.Verification;
import com.qianfujiaoyu.Utils.mApplication;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import okhttp3.Call;
import okhttp3.Response;

import static com.qianfujiaoyu.Utils.mApplication.ST;

public class MainActivity extends BaseActivity {
    private Banner banner;
    private RecyclerView recyclerView;
    private int screenWidth;
    private ArrayList<String> images;
    private ArrayList<HashMap<String, String>> imgInfos;//图片参数

    private HomeManager homeManager;
    private ImageView head;
    private PopupWindow pp;
    private String appUrl;
    private String jishuSupprot = "";
    private String share = "";

    // TODO: 2017/5/2 banner 点击检测
    private static final int AD = 1;
    private static final int Good = 3;
    private static final int HuoDong = 4;
    private static final int KeCheng = 5;
    private static final int ZIXUN = 2;
    private boolean needExit;
    private String SMS = "";
    private ImageView more;

    /*
    检查更新
    */
    private void checkUpdate() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject js = new JSONObject();
                    try {
                        js.put("m_id", Constants.M_id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m = ApisSeUtil.i(js);
                    String data = OkGo.post(Constants.Update_Ip)
                            .params("key", m.K()).params("msg", m.M())
                            .execute().body().string();
                    if (!TextUtils.isEmpty(data)) {
                        final HashMap<String, String> map = AnalyticalJSON.getHashMap(data);
                        if (map != null) {
                            String code = map.get("app_code");
                            final String appname = map.get("app_name");
                            appUrl = map.get("app_url");
                            share = map.get("share");
                            jishuSupprot = map.get("support");
                            if (null != code && Verification.getVersionCode(mApplication.getInstance()) < Integer.valueOf(code)) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showDialog(appname, appUrl, map.get("app_update"));//更新通知


                                    }
                                });
                            }

                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //更新进度框
    private void showDialog(final String appname, final String appUrl, String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        final View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.update_alet_layout, null);
        ((TextView) view.findViewById(R.id.version_update_title)).setText(ST("检测到新版本：" + appname.substring(Constants.NAME_CHAR_NUM, appname.length() - 4)));
        final TextView textView = (TextView) view.findViewById(R.id.version_update_content);
        textView.setText(ST(content.equals("") ? "是否需要更新？" : content));
        TextView update = (TextView) view.findViewById(R.id.version_update_update);
        update.setText(ST("更新"));
        TextView cancle = (TextView) view.findViewById(R.id.version_update_cancel);
        cancle.setText(ST("取消"));
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((TextView) v).getText().equals(ST("后台更新"))) {
                    dialog.dismiss();
                    return;
                }
                PermissionUtil.checkPermission(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS});

                ((TextView) v).setText(ST("后台更新"));
                final ProgressBar updateBar = (ProgressBar) view.findViewById(R.id.version_update_progress);
                updateBar.setVisibility(View.VISIBLE);
                final TextView percent = (TextView) view.findViewById(R.id.version_update_percent);
                percent.setVisibility(View.VISIBLE);
                textView.setVisibility(View.GONE);
                OkGo.get(appUrl).tag("download").execute(new FileCallback(Environment.getExternalStorageDirectory().getAbsolutePath(), appname) {

                    @Override
                    public void onSuccess(File file, Call call, Response response) {

                    }

                    @Override
                    public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        super.downloadProgress(currentSize, totalSize, progress, networkSpeed);
                        percent.setText((int) (progress * 100) + "%");
                        updateBar.setProgress((int) (progress * 100));
                    }

                    @Override
                    public void onAfter(File file, Exception e) {
                        super.onAfter(file, e);
                        Verification.installApk(getApplicationContext(), appname);
                    }


                });
            }
        });
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                OkGo.getInstance().cancelTag("download");
                FileUtils.deleteFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath(), appname));
            }
        });
        dialog.setView(view);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    /**
     * 获取短信邀请文本
     */
    private void getSmsInviteCode() {
        JSONObject js = new JSONObject();
        try {
            js.put("m_id", Constants.M_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m = ApisSeUtil.i(js);
        OkGo.post(Constants.little_sms_get__IP).tag(this).params("key", m.K()).params("msg", m.M())
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
                                SMS = map.get("sms");
                                PreferenceUtil.getSettingIncetance(MainActivity.this).edit().putString("sms", SMS).apply();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        initHeader();
        more = (ImageView) findViewById(R.id.right1);
        more.setImageBitmap(ImageUtil.readBitMap(this, R.drawable.home_add_more));
        more.setVisibility(View.VISIBLE);

        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(mApplication.getInstance()).inflate(R.layout.home_add_layout, null);
                if (pp == null) {
                    pp = new PopupWindow(view);
                    pp.setFocusable(true);
                    pp.setOutsideTouchable(true);
                    pp.setTouchable(true);
                    ColorDrawable c = new ColorDrawable(ContextCompat.getColor(MainActivity.this, R.color.main_color));
//                    Drawable drawable= ContextCompat.getDrawable(this,R.drawable.titlebar_bg);
                    pp.setBackgroundDrawable(c);
                    pp.setWidth(DimenUtils.dip2px(mApplication.getInstance(), 150));
                    pp.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                    pp.showAsDropDown(more, -50, 0);
                } else {
                    if (pp.isShowing()) pp.dismiss();
                    pp.showAsDropDown(more, -50, 0);
                }
                initPopupWindow(view);
            }
        });
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        banner = (Banner) findViewById(R.id.banner);
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        banner.setImageLoader(new ImageLoader() {
            @Override
            public void displayImage(Context context, Object path, ImageView imageView) {
                Glide.with(MainActivity.this).load(path).override(screenWidth, screenWidth * 2 / 5)
                        .crossFade(400)
                        .centerCrop()
                        .placeholder(R.color.style_divider_color)
                        .into(imageView);
            }
        });
        banner.setDelayTime(3000);
        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                HashMap<String, String> map = imgInfos.get(position);
                LogUtil.e(map + "    " + position);
                if (map != null) {
                    Intent intent = new Intent();
                    switch (Integer.valueOf(map.get("type"))) {
                        case AD:
                            if(map.get("url").equals("")){
                                ScaleImageUtil.openBigIagmeMode(MainActivity.this,images.get(position));
                            }else{
                                intent.setClass(MainActivity.this, AD.class);
                                intent.putExtra("url", map.get("url"));
                                startActivity(intent);
                            }

                            break;
                        case Good:
                            intent.setClass(MainActivity.this, Order_detail.class);
                            intent.putExtra("id", map.get("aid"));
                            startActivity(intent);
                            break;
                        case HuoDong:
                            intent.setClass(MainActivity.this, activity_Detail.class);
                            intent.putExtra("id", map.get("aid"));
                            startActivity(intent);
                            break;
                        case KeCheng:
                            intent.setClass(MainActivity.this, activity_Detail.class);
                            intent.putExtra("kecheng",true);
                            intent.putExtra("id", map.get("aid"));
                            startActivity(intent);
                            break;
                        case ZIXUN:
                            intent.setClass(MainActivity.this, ZiXun_Detail.class);
                            intent.putExtra("id", map.get("aid"));
                            startActivity(intent);
                            break;

                    }
                }
            }
        });
        RecyclerView.LayoutManager rl = new GridLayoutManager(this, 4);

        recyclerView = (RecyclerView) findViewById(R.id.recycle);
        recyclerView.setLayoutManager(rl);

        homeManager = new HomeManager(this, recyclerView);
        homeManager.initMine();
        homeManager.setOnitemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                final Intent intent = new Intent();
                HashMap<String, Object> map = homeManager.getMaps().get(i);
                switch (map.get(HomeManager.text).toString()) {
                    case "园介绍":
                        intent.setClass(MainActivity.this, YuanInfo.class);
                        startActivity(intent);

                        break;
                    case "活动":
                        intent.setClass(MainActivity.this, activity_fragment.class);
                        startActivity(intent);
                        break;
                    case "食谱":
                        intent.setClass(MainActivity.this, ShiPu.class);
                        startActivity(intent);
                        break;
                    case "商城":
                        intent.setClass(MainActivity.this, Order_Tab.class);
                        startActivity(intent);
                        break;
                    case "订单":
                        intent.setClass(MainActivity.this, Mine_GYQD.class);
                        startActivity(intent);
                        break;
                    case "我的班级":
                        if (new LoginUtil().checkLogin(MainActivity.this)) {
                            intent.setClass(mApplication.getInstance(), List_Classes.class);
                            startActivity(intent);
                        }
                        break;
                    case "收藏":
                        if (new LoginUtil().checkLogin(MainActivity.this)) {
                            intent.setClass(MainActivity.this, Activity_ShouCang.class);
                            startActivity(intent);
                        }
                        break;
                    case "邀请":
                        if (Build.VERSION.SDK_INT >= 23) {
                            //判断有没有拨打电话权限
                            if (PermissionChecker.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                                //请求拨打电话权限
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 222);
                                return;
                            }
                        }

                        intent.setClass(MainActivity.this, Contact.class);
                        intent.putExtra("sms", ST("".equals(SMS) ? PreferenceUtil.getSettingIncetance(MainActivity.this).getString("sms", "") : SMS));
                        startActivity(intent);
                        break;
                    case "通知":
                        if (new LoginUtil().checkLogin(MainActivity.this)) {
                            intent.setClass(MainActivity.this, ApplyShenhe.class);
                            intent.putExtra("Mine", true);//判断是否是自己的审核
                            startActivity(intent);
                        }
                        break;
                    case "设置":
                        intent.setClass(MainActivity.this, gerenshezhi.class);
                        startActivity(intent);
                        break;
                    case "私信":
                        if (new LoginUtil().checkLogin(MainActivity.this)) {
                            intent.setClass(MainActivity.this, ChatMessage.class);
                            startActivity(intent);
                        }
                        break;
                    case "切换账号":
                        AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
                        b.setTitle("确认要切换账号吗？")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        SharedPreferences.Editor ed = PreferenceUtil.getUserIncetance(MainActivity.this).edit();
                                        SharedPreferences.Editor address = getSharedPreferences("address", Context.MODE_PRIVATE).edit();
                                        address.clear().apply();
                                        ed.putString("uid", "");
                                        ed.putString("user_id", "");
                                        ed.putString("head_path", "");
                                        ed.putString("head_url", "");
                                        ed.putString("sign", "");
                                        ed.putString("phone", "");
                                        ed.putString("sex", "");
                                        ed.putString("pet_name", "");
                                        ed.apply();
                                        intent.setClass(mApplication.getInstance(), Login.class);
                                        startActivity(intent);
                                        finish();
                                        dialog.dismiss();
                                    }
                                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();

                        break;

                }

            }
        });


    }

    private void initPopupWindow(View v) {
        int dp30=DimenUtils.dip2px(this,25);
        ((TextView) v.findViewById(R.id.item_fabu)).setText("技术支持");
        Drawable d=ContextCompat.getDrawable(this,R.drawable.jishuzhichi);
        d.setBounds(0,0,dp30,dp30);
        ((TextView) v.findViewById(R.id.item_fabu)).setCompoundDrawables(d,null,null,null);
        v.findViewById(R.id.item_fabu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri content_url;
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                if ("".equals(jishuSupprot)) {
                    content_url = Uri.parse("http://www.indranet.cn");
                } else {
                    content_url = Uri.parse(jishuSupprot);
                }
                intent.setData(content_url);
                startActivity(intent);
                pp.dismiss();
            }
        });// TODO: 2017/1/12 技术支持
        ((TextView) v.findViewById(R.id.item_member)).setText("分享");
        Drawable d1=ContextCompat.getDrawable(this,R.drawable.fenxiang2);
        d1.setBounds(0,0,dp30,dp30);
        ((TextView) v.findViewById(R.id.item_member)).setCompoundDrawables(d1,null,null,null);
        v.findViewById(R.id.item_member).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("".equals(appUrl)&&"".equals(share)){
                    pp.dismiss();
                    return;
                }
                LogUtil.e("分享链接：：：" + appUrl + "      " + share);
                UMWeb umWeb = new UMWeb("".equals(share) ? appUrl : share);
                umWeb.setThumb(new UMImage(mApplication.getInstance(), R.drawable.indra));
                umWeb.setTitle(getResources().getString(R.string.app_name) + "App");
                umWeb.setDescription("".equals(share) ? appUrl : share);
                if (umWeb != null) {
                    new ShareManager().shareWeb(umWeb, MainActivity.this);
                }

                pp.dismiss();
            }
        });// TODO: 2017/1/12  发分享
        ((TextView) v.findViewById(R.id.item_guanli)).setVisibility(View.GONE);
        v.findViewById(R.id.item_guanli).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    //判断有没有拨打电话权限
                    if (PermissionChecker.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
                            && PermissionChecker.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                        //请求拨打电话权限
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_CONTACTS}, 222);
                        return;
                    }
                }
                Intent i = new Intent(MainActivity.this, Contact.class);
                i.putExtra("sms", ST("".equals(SMS) ? PreferenceUtil.getUserIncetance(MainActivity.this).getString("sms", "") : SMS));
                startActivity(i);
                pp.dismiss();
            }
        });// TODO: 2017/1/12 邀请好友


    }

    // TODO: 2017/5/17 初始化头部
    private void initHeader() {
        head = (ImageView) findViewById(R.id.back);
        head.setImageBitmap(ImageUtil.readBitMap(this, R.drawable.indra));
        head.setVisibility(View.VISIBLE);
        head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new LoginUtil().checkLogin(MainActivity.this)) {
                    Intent intent = new Intent(MainActivity.this, User_Detail.class);
                    intent.putExtra("type", 3);
                    intent.putExtra("id", PreferenceUtil.getUserIncetance(MainActivity.this).getString("user_id", ""));
                    startActivity(intent);
                }

            }
        });
        ((TextView) findViewById(R.id.title)).setText("谦福教育");
    }

    // TODO: 2017/5/2 获取轮播图
    private void getBanner() {
        JSONObject js = new JSONObject();
        try {
            js.put("m_id", Constants.M_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m = ApisSeUtil.i(js);
        OkGo.post(Constants.getBanner)
                .params("key", m.K())
                .params("msg", m.M())
                .execute(new AbsCallback<ArrayList<HashMap<String, String>>>() {
                    @Override
                    public void onSuccess(ArrayList<HashMap<String, String>> list, Call call, Response response) {
                        imgInfos = list;
                        if (imgInfos != null) {
                            if (images == null) {
                                images = new ArrayList<String>();
                            } else {
                                images.clear();
                            }
                            for (HashMap<String, String> map : imgInfos) {
                                images.add(map.get("image"));
                            }
                            banner.setImages(images);
                            banner.start();
                        } else {
                            getBanner();
                        }
                    }

                    @Override
                    public ArrayList<HashMap<String, String>> convertSuccess(Response response) throws Exception {
                        return AnalyticalJSON.getList_zj(response.body().string());
                    }
                });
    }

    @Override
    public boolean setEventBus() {
        return true;
    }

    @Override
    public boolean isMainColor() {
        return true;
    }

    @Override
    public void doThings() {
        getBanner();
        updateInfo();
        getSmsInviteCode();
        checkUpdate();
    }
    @Subscribe
    public void updateMainActivity(String s) {
        Glide.with(this).load(s).override(DimenUtils.dip2px(this,45),DimenUtils.dip2px(this,45))
                .centerCrop().into(head);
        updateInfo();
    }
    private void updateInfo() {
        final Intent intent = new Intent();
        if (TextUtils.isEmpty(PreferenceUtil.getUserIncetance(this).getString("user_id", ""))) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject js = new JSONObject();
                    try {
                        js.put("m_id", Constants.M_id);
                        js.put("user_id", PreferenceUtil.getUserIncetance(mApplication.getInstance()).getString("user_id", ""));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m = ApisSeUtil.i(js);
                    String data = OkGo.post(Constants.User_Info_Ip).tag(this)
                            .params("key", m.K())
                            .params("msg", m.M())
                            .execute().body().string();

                    if (!TextUtils.isEmpty(data)) {
                        final HashMap<String, String> map = AnalyticalJSON.getHashMap(data);
                        final SharedPreferences.Editor ed = PreferenceUtil.getUserIncetance(MainActivity.this).edit();
                        if (banner != null) {
                            banner.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (map != null) {
                                        if (!map.get("user_image").equals("")) {
                                            ed.putString("head_url", map.get("user_image"));
                                            Glide.with(MainActivity.this).load(map.get("user_image")).bitmapTransform(new CropCircleTransformation(MainActivity.this)).override(DimenUtils.dip2px(MainActivity.this, 45), DimenUtils.dip2px(MainActivity.this, 45))
                                                    .into(head);

                                        }
                                        if (!map.get("sex").equals("")) {
                                            ed.putString("sex", map.get("sex"));
                                        }
                                        if (!map.get("signature").equals("")) {
                                            ed.putString("signature", map.get("signature"));
                                        }
                                        if (!map.get("pet_name").equals("")) {
                                            ed.putString("pet_name", map.get("pet_name"));
                                        }
                                        if (map.get("pet_name").equals("") || map.get("sex").equals("")) {
                                            intent.setClass(MainActivity.this, Mine_gerenziliao.class);
                                            startActivity(intent);
                                        }


                                    }
                                    ed.apply();

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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        homeManager.saveMySetting();
        banner.releaseBanner();
    }

    @Override
    protected void onPause() {
        super.onPause();
        banner.stopAutoPlay();
    }

    @Override
    public void onBackPressed() {
        if (pp != null && pp.isShowing()) {
            pp.dismiss();
        }
        if (needExit) {
            finish();
            return;
        }
        Toast.makeText(MainActivity.this, ST("再按一次退出应用"), Toast.LENGTH_SHORT).show();
        needExit = true;
        head.postDelayed(new Runnable() {
            @Override
            public void run() {
                needExit = false;
            }
        }, 2000);
    }
}
