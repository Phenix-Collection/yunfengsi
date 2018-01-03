package com.yunfengsi;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.callback.FileCallback;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.yunfengsi.Adapter.myHomePagerAdapter;
import com.yunfengsi.Fragment.GongYangActivity;
import com.yunfengsi.Fragment.Mine;
import com.yunfengsi.Fragment.ZiXun;
import com.yunfengsi.Managers.AudioTest;
import com.yunfengsi.Managers.MessageCenter;
import com.yunfengsi.Model_activity.activity_fragment;
import com.yunfengsi.Model_zhongchou.FundFragment;
import com.yunfengsi.Setting.PhoneCheck;
import com.yunfengsi.Setting.Search;
import com.yunfengsi.SideListview.Contact;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.FileUtils;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.LoginUtil;
import com.yunfengsi.Utils.MD5Utls;
import com.yunfengsi.Utils.PermissionUtil;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.ProgressUtil;
import com.yunfengsi.Utils.ShareManager;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.UpPayUtil;
import com.yunfengsi.Utils.Verification;
import com.yunfengsi.Utils.mApplication;
import com.yunfengsi.View.mAudioManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import okhttp3.Call;
import okhttp3.Response;

import static com.yunfengsi.Utils.mApplication.ST;
import static com.yunfengsi.Utils.mApplication.alias1;
import static com.yunfengsi.Utils.mApplication.alias2;


public class MainActivity extends UpPayUtil {
    private static final String TAG = "MainActivity";
    private ImageView back;
    private TextView title;
    private ViewPager pager;
    public TabLayout tabLayout;
    private myHomePagerAdapter adapter;
    private SharedPreferences sp;
    public List<Fragment> list;
    private ImageView add;
    private PopupWindow pp;//加号弹出窗口
    private SHARE_MEDIA[] share_list;
    private ShareAction action;
    private AlertDialog backDialog;
    private String appUrl;
    private String jishuSupprot = "";
    private String share = "";
    public String SMS = "";
    private Uri contactData;
    private UMWeb umWeb;
    private boolean needExit = false;
    public static MainActivity activity;
    public ImageView notice;
    private android.support.v7.app.AlertDialog dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.activity_main);
        IntentFilter intentFilter = new IntentFilter("st");
        registerReceiver(receiver, intentFilter);

        initView();
        checkUpdate();
//        checkUserinfo();
        getSmsInviteCode();
        showAdDialog();
        login();

//        //跳转router调用
//        if (getIntent().getData() != null) {
//            MLinkAPIFactory.createAPI(this).router(getIntent().getData());
//            //跳转后结束当前activity
//            finish();
//        }
    }

    // TODO: 2017/12/5 弹出广告弹窗
    private void showAdDialog() {
        if (!mApplication.gg_image.equals("")&&mApplication.changeIcon) {
            android.support.v7.app.AlertDialog.Builder b = new android.support.v7.app.AlertDialog.Builder(this);
            View view = LayoutInflater.from(this).inflate(R.layout.main_ad_dialog, null);
            ImageView cancle = (ImageView) view.findViewById(R.id.cancle);
            final ImageView image = (ImageView) view.findViewById(R.id.main_ad_image);
            b.setView(view);
            dialog = b.create();
            cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            //点击广告页
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!mApplication.gg_url.equals("")) {
                        Intent intent = new Intent(MainActivity.this, ZhiFuShare.class);
                        intent.putExtra("url", mApplication.gg_url);
                        startActivity(intent);
                        dialog.dismiss();
                    } else {
                        dialog.dismiss();
                    }
                }
            });

            Glide.with(this).load(mApplication.gg_image)
                    .asBitmap()
                    .skipMemoryCache(true)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                            RoundedBitmapDrawable tbd = RoundedBitmapDrawableFactory.create(getResources(), resource);
                            tbd.setCornerRadius(15);
                            image.setImageDrawable(tbd);
                        }
                    });

            Window window = dialog.getWindow();
            WindowManager.LayoutParams wl = window.getAttributes();
            window.getDecorView().setPadding(0, 0, 0, 0);
            wl.gravity = Gravity.CENTER;
            wl.width = getResources().getDisplayMetrics().widthPixels * 8 / 10;
            wl.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setDimAmount(0.7f);
            window.setWindowAnimations(R.style.dialogWindowAnim);
            window.setBackgroundDrawableResource(R.color.transparent);
            window.setAttributes(wl);
            dialog.show();
        }
    }

    // TODO: 2017/4/21 登录
    private void login() {
        if (!PreferenceUtil.getUserIncetance(this).getString("user_id", "").equals("")) {
            final CloudPushService pushService = PushServiceFactory.getCloudPushService();
            pushService.addAlias(PreferenceUtil.getUserIncetance(this).getString("user_id", ""), new CommonCallback() {
                @Override
                public void onSuccess(String s) {
                    LogUtil.e("别名绑定成功，哈哈哈哈哈哈哈哈");
                }

                @Override
                public void onFailed(String s, String s1) {
                    LogUtil.e("别名绑定失败");
                }
            });
        }
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            title.setText(ST(title.getText().toString()));
            for (int i = 0; i < list.size(); i++) {
                TabLayout.Tab tab = tabLayout.getTabAt(i);
                if (tab != null)
                    ((TextView) tab.getCustomView().findViewById(R.id.home_tab_text)).setText(adapter.getPageTitle(i));

            }
        }
    };

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
        OkGo.post(Constants.little_sms_get__IP).tag(TAG)
                .params("key", ApisSeUtil.getKey())
                .params("msg", ApisSeUtil.getMsg(js))
                .execute(new AbsCallback<HashMap<String, String>>() {
                    @Override
                    public void onSuccess(HashMap<String, String> map, Call call, Response response) {
                        SMS = map.get("sms");
                    }

                    @Override
                    public HashMap<String, String> convertSuccess(Response response) throws Exception {
                        return AnalyticalJSON.getHashMap(response.body().string());
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.e("主页销毁");
        UMShareAPI.get(this).release();
        unregisterReceiver(receiver);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
//        LogUtil.e("到达首页");
//        if (resultCode == RESULT_OK) {
//            if (requestCode == PhoneSMSManager.REQUEST_CODE_CONTENT) {
//                if (Build.VERSION.SDK_INT >= 23) {
//
//                    //判断有没有拨打电话权限
//                    if (PermissionChecker.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
//                            || PermissionChecker.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
//                        //请求拨打电话权限
//                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_CONTACTS}, 222);
//                        return;
//                    }

//                }
//                contactData = data.getData();
//                LogUtil.w("onActivityResult: " + data + "    contactData:" + data.getData());
//                Cursor cursor = getContentResolver().query(contactData, null, null, null, null);
//                cursor.moveToFirst();
//                LogUtil.w("onActivityResult: " + cursor.moveToFirst());
////                String num = PhoneSMSManager.getContactPhone(this,cursor);
//                //打开短信app
//                if (cursor.moveToFirst()) {
//                    String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
//                    String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
//                    String phonenum = "此联系人暂未输入电话号码";
//                    Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);
//
//                    if (phones.moveToFirst()) {
//                        phonenum = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                    }
//                    LogUtil.w("联系人：" + name + "\n电话：" + phonenum);
//                    if (Build.VERSION.SDK_INT < 14) {
//                        phones.close();
//                    }
//                    Uri uri = Uri.parse("smsto:" + phonenum);
//                    Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);
//                    sendIntent.putExtra("sms_body", mApplication.ST("".equals(SMS) ? sp.getString("sms", "") : SMS));
//                    startActivity(sendIntent);
//                }
//                if (Build.VERSION.SDK_INT < 14) {//不添加的话Android4.0以上系统运行会报错
//                    cursor.close();
//                }
//
//
//            }
//
//        }

    }

    private void checkUserinfo() {
        if (!sp.getString("user_id", "").equals("")) {
//            if (sp.getString("pet_name", "").equals("") || sp.getString("sex", "").equals("")) {
//                Intent intent = new Intent(this, Mine_gerenziliao.class);
//                startActivity(intent);
//            }
            if (sp.getString("phone", "").equals("")) {
                Intent intent = new Intent(this, PhoneCheck.class);
                startActivity(intent);
            }
        }
    }


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
                    String data = OkGo.post(Constants.Update_Ip)
                            .params("key", ApisSeUtil.getKey()).params("msg", ApisSeUtil.getMsg(js))
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

    private void initView() {
        //分享
        share_list = new SHARE_MEDIA[]{
                SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE,
                SHARE_MEDIA.SINA
        };
        add = (ImageView) findViewById(R.id.title_image3);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(mApplication.getInstance()).inflate(R.layout.home_add_layout, null);
                if (pp == null) {
                    pp = new PopupWindow(view);
                    pp.setFocusable(true);
                    pp.setOutsideTouchable(true);
                    pp.setTouchable(true);
                    ColorDrawable c = new ColorDrawable(getResources().getColor(R.color.main_color));
                    pp.setBackgroundDrawable(c);
                    pp.setWidth(DimenUtils.dip2px(mApplication.getInstance(), 150));
                    pp.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                    pp.showAsDropDown(add, -50, 0);
                } else {
                    if (pp.isShowing()) pp.dismiss();
                    pp.showAsDropDown(add, -50, 0);
                }
                initPopupWindow(view);
            }
        });
        notice = (ImageView) findViewById(R.id.mine_pinglun);
        notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new LoginUtil().checkLogin(MainActivity.this)) {
                    if (v.isSelected()) {
                        v.setSelected(false);
                    }
                    // TODO: 2017/11/21 消息中心入口
                    Intent intent = new Intent(MainActivity.this, MessageCenter.class);
                    startActivity(intent);
                }

            }
        });
        sp = getSharedPreferences("user", MODE_PRIVATE);
        back = (ImageView) findViewById(R.id.title_back);
        title = (TextView) findViewById(R.id.title_title);
        pager = (ViewPager) findViewById(R.id.home_viewpager);
        tabLayout = (TabLayout) findViewById(R.id.home_bottom_tablayout);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        ImageView img = ((ImageView) findViewById(R.id.title_image2));
        Glide.with(this).load(R.drawable.search).override(DimenUtils.dip2px(this, 40), DimenUtils.dip2px(this, 40)).into(img);
        img.setVisibility(View.VISIBLE);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mApplication.getInstance(), Search.class);
                startActivity(intent);
            }
        });
        //初始化fragment
        list = new ArrayList<>();

        Fragment f = new ZiXun();
        Fragment f1 = new activity_fragment();
        Fragment f2 = new GongYangActivity();
        Fragment f3 = new FundFragment();
        Fragment f4 = new Mine();
        list.add(f);
        list.add(f1);
        list.add(f2);
        list.add(f3);
        list.add(f4);


        //加载到adapter
        adapter = new myHomePagerAdapter(this, getSupportFragmentManager(), list);
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(5);
        //关联tablayout
        tabLayout.setupWithViewPager(pager);
        //设置自定义tab
        for (int i = 0; i < list.size(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null)
                tab.setCustomView(adapter.getTabView(i));
        }

        //为tab注册选中事件
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ((TextView) tab.getCustomView().findViewById(R.id.home_tab_text)).setTextColor(getResources().getColor(R.color.main_color));
                pager.setCurrentItem(tab.getPosition());
                switch (tab.getPosition()) {
                    case 0:
                        title.setText(ST("图文"));
                        break;
                    case 1:
                        title.setText(ST("活动"));
                        break;
                    case 2:
                        title.setText(ST("供养"));
                        break;
                    case 3:
                        title.setText(ST("助学"));
                        break;
                    case 4:
                        title.setText(ST("我的"));
                        break;


                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                ((TextView) tab.getCustomView().findViewById(R.id.home_tab_text)).setTextColor(getResources().getColor(R.color.gray));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        pager.setCurrentItem(0);
        title.setText(ST("图文"));
        tabLayout.getTabAt(0).getCustomView().setSelected(true);
        ((TextView) tabLayout.getTabAt(0).getCustomView().findViewById(R.id.home_tab_text)).setTextColor(getResources().getColor(R.color.main_color));


        // TODO: 2017/12/19 测试使用
        findViewById(R.id.test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ZhiFuShare.class);
                intent.putExtra("type", "5");
                startActivity(intent);
            }
        });
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

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.e("主页进入后台");
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        JCVideoPlayer.releaseAllVideos();
        mAudioManager.release();
        ProgressUtil.dismiss();

//      changeLauncher(changeIcon);


    }

    /**
     * 改变应用入口，图标，名称
     */
    private void changeLauncher(boolean flag) {
        LogUtil.e("应用关闭  切换图标~!~!" + mApplication.componentName);
        if (flag) {
            if (new ComponentName(this, alias2) != mApplication.componentName) {
                LogUtil.e("红包入口");
                PackageManager pm = getPackageManager();

                pm.setComponentEnabledSetting(new ComponentName(this,alias1),
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                pm.setComponentEnabledSetting(new ComponentName(this, alias2),
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
//                Intent 重启 Launcher 应用
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                List<ResolveInfo> resolves = pm.queryIntentActivities(intent, 0);
                for (ResolveInfo res : resolves) {
                    if (res.activityInfo != null) {
                        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                        am.killBackgroundProcesses(res.activityInfo.packageName);
                    }
                }

            }

        } else {
            if (new ComponentName(this, alias1) != mApplication.componentName) {
                LogUtil.e("正常入口");
                PackageManager pm = getPackageManager();
                pm.setComponentEnabledSetting(new ComponentName(this,alias2),
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                pm.setComponentEnabledSetting(new ComponentName(this, alias1),
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                //Intent 重启 Launcher 应用
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                List<ResolveInfo> resolves = pm.queryIntentActivities(intent, 0);
                for (ResolveInfo res : resolves) {
                    if (res.activityInfo != null) {
                        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                        am.killBackgroundProcesses(res.activityInfo.packageName);
                    }
                }


            }

        }
    }


    private void initPopupWindow(View v) {
        LinearLayout layout1 = (LinearLayout) v.findViewById(R.id.layout1);
        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri content_url;
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                if (sp.getString("user_id", "").equals("")) {
                    content_url = Uri.parse(Constants.host_Ip + "/" + Constants.NAME_LOW + ".php");
                    intent.setData(content_url);
                    startActivity(intent);
                    pp.dismiss();
                } else {
                    JSONObject js = new JSONObject();
                    try {
                        js.put("id", sp.getString("user_id", "") + MD5Utls.stringToMD5(MD5Utls.stringToMD5(Constants.M_id)));
                        content_url = Uri.parse("http://indrah.cn" + "/" + Constants.NAME_LOW + ".php/Index/index/login/" + android.util.Base64.encodeToString(js.toString().getBytes(), android.util.Base64.DEFAULT));
                        Log.w(TAG, "onClick: 加密地址" + content_url);
                        intent.setData(content_url);
                        startActivity(intent);
                        pp.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }
        });
        LinearLayout layout2 = (LinearLayout) v.findViewById(R.id.layout2);
        layout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                QpayUtil.openQQPay(MainActivity.this,"52","fdsfljdk","100","1","4");
                createShortCut();
                pp.dismiss();
            }
        });
        LinearLayout layout3 = (LinearLayout) v.findViewById(R.id.layout3);
        ((TextView) layout3.findViewById(R.id.jishuzhichi)).setText(ST("技术支持"));
        layout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri content_url;
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                if (jishuSupprot.equals("")) {
                    content_url = Uri.parse("http://www.indranet.cn");
                } else {
                    content_url = Uri.parse(jishuSupprot);
                }
                intent.setData(content_url);
                startActivity(intent);
//                startActivity(new Intent(MainActivity.this, AudioTest.class));


                pp.dismiss();
            }
        });
        LinearLayout layout4 = (LinearLayout) v.findViewById(R.id.layout4);
        ((TextView) layout4.findViewById(R.id.fenxiang)).setText(ST("分享"));
        layout4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                umWeb = new UMWeb("".equals(share) ? appUrl : share);
                umWeb.setThumb(new UMImage(mApplication.getInstance(), R.drawable.indra_share));
                umWeb.setTitle(getResources().getString(R.string.app_name) + "App");
                umWeb.setDescription(
                        "雅安云峰寺佛教信息一手掌握（自由发布资讯）\n" +
                                "雅安云峰寺寺院活动一键报名（轻松管理信众）\n" +
                                "雅安云峰寺各类供养一步到位（方便在线支付）\n" +
                                "雅安云峰寺佛教用品线上流通（在线运营商城）\n" +
                                "雅安云峰寺功课普皆回向十方（打造同修社群）");
                if (umWeb != null) {
                    new ShareManager().shareWeb(umWeb, MainActivity.this);
                }

                pp.dismiss();
            }
        });
        LinearLayout layout5 = (LinearLayout) v.findViewById(R.id.layout5);
        ((TextView) layout5.findViewById(R.id.invite)).setText(ST("邀请好友"));
        layout5.setOnClickListener(new View.OnClickListener() {
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
                i.putExtra("sms", ST("".equals(SMS) ? sp.getString("sms", "") : SMS));
                startActivity(i);
                pp.dismiss();
//                if (debug) {
//                    ToastUtil.showToastShort(MainActivity.this,"测试 ");
//                    debug=false;
//                }else{
//                    ToastUtil.showToastShort(MainActivity.this,"哈哈哈 ");
//                    debug=true;
//                }

            }
        });
    }

    //    boolean debug=false;
    public void createShortCut() {
        //创建快捷方式的Intent
        Intent shortcutintent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        //不允许重复创建
        shortcutintent.putExtra("duplicate", false);
        //需要现实的名称
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getResources().getString(R.string.app_name) + "WEB");
        //快捷图片
        Parcelable icon = Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.drawable.indra);
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(Constants.host_Ip + "/" + Constants.NAME_LOW + ".php");
        intent.setData(content_url);
        //点击快捷图片，运行的程序主入口
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        //发送广播。OK
        sendBroadcast(shortcutintent);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        ToastUtil.showToastShort(ST("迦叶一笑")+"    "+isChina);
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
        tabLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                needExit = false;
            }
        }, 2000);
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//
//        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//            if (backDialog == null) {
//                backDialog = new AlertDialog.Builder(this).setMessage("确认要退出吗").setNegativeButton("确认",
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                finish();
//                            }
//                        }).setPositiveButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        backDialog.dismiss();
//                    }
//                }).create();
//                backDialog.setCanceledOnTouchOutside(false);
//            }
//            if (backDialog.isShowing())
//                return false;
//            backDialog.show();
//
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    private static final String TAG_EXIT = "exit";

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
//            LogUtil.e("主页：：：" + intent.getExtras() + "    " + intent.getStringExtra("type")+"  id:::"+intent.getStringExtra("id"));
//            String type = intent.getStringExtra("type");//type  1,资讯，2活动，3，供养，4，助学，5红包
//            if (type != null) {
//                switch (type) {
//                    case "1":
//                        intent.setClass(this, ZiXun_Detail.class);
//                        break;
//                    case "2":
//                        intent.setClass(this, activity_Detail.class);
//                        break;
//                    case "3":
//                        intent.setClass(this, XuanzheActivity.class);
//                        break;
//                    case "4":
//                        intent.setClass(this, FundingDetailActivity.class);
//                        break;
//                    case "5":
//                        intent.setClass(this, ZhiFuShare.class);
//                        break;
//
//                }
//                //TODO：动画等耗时操作结束后再调用checkYYB(),一般写在starActivity前即可
//                MLinkAPIFactory.createAPI(this).checkYYB(this, new YYBCallback() {
//                    @Override
//                    public void onFailed(Context context) {
//
//                    }
//
//                    @Override
//                    public void onSuccess() {
//
//                    }
//                });
//                startActivity(intent);
//                return;
//            }

            boolean isExit = intent.getBooleanExtra(TAG_EXIT, false);
            if (isExit) {
                this.finish();
            }
            int pos = intent.getIntExtra("pos", -1);
            if (pos != -1) {
                pager.setCurrentItem(pos);
            }
        }
//        else {
//            MLinkAPIFactory.createAPI(this).checkYYB();
//        }
    }

}
