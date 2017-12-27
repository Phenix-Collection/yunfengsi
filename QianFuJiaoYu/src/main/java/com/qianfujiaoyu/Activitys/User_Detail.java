package com.qianfujiaoyu.Activitys;

import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.qianfujiaoyu.Base.ScaleImageUtil;
import com.qianfujiaoyu.R;
import com.qianfujiaoyu.Utils.ACache;
import com.qianfujiaoyu.Utils.AnalyticalJSON;
import com.qianfujiaoyu.Utils.ApisSeUtil;
import com.qianfujiaoyu.Utils.Constants;
import com.qianfujiaoyu.Utils.DimenUtils;
import com.qianfujiaoyu.Utils.ImageUtil;
import com.qianfujiaoyu.Utils.LogUtil;
import com.qianfujiaoyu.Utils.Network;
import com.qianfujiaoyu.Utils.ProgressUtil;
import com.qianfujiaoyu.Utils.StatusBarCompat;
import com.qianfujiaoyu.Utils.ToastUtil;
import com.qianfujiaoyu.Utils.mApplication;
import com.qianfujiaoyu.Utils.photoUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/11/3.
 */
public class User_Detail extends AppCompatActivity implements View.OnClickListener {
    private ProgressDialog progresDialog;
    private AlertDialog dialog;
    private ImageView head;
    private RelativeLayout headlayout;
    private static final String TAG = "User_Detail";
    private EditText beizhu;
    private TextView name, sign, sex, phone, address, change_beizhu;
    private String ID;
    private boolean is1 = false, is2 = false;
    private boolean isDeleted = false;
    private HashMap<String, String> map;
    private TextView zhifu;
    private TextView sixin, guanzhu;
    private int screenWidth, screenHeight;
    private ValueAnimator va;
    private ZFAdapter zfAdapter;
    private long downtime;
    //    private SlidingPaneLayout parent;
    private ImageView banner;
    private TextView job_tv;
    private TextView guanzhu_right;
    private LinearLayout beizhulayout;
    private int type;//1   入口：粉丝列表    2   入口：关注列表 3 rukou :个人信息
    private File Headfile;
    private ACache aCache;
    private SharedPreferences sp;
    private Uri pictureUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, ContextCompat.getColor(this, R.color.main_color));
        setContentView(R.layout.user_xiangxi_zi_liao);
        aCache = ACache.get(this);
        sp = getSharedPreferences("user", MODE_PRIVATE);
        ID = getIntent().getStringExtra("id");
        initView();


        checkNetwork();
        getData();

    }


    @Override
    public void onBackPressed() {
        findViewById(R.id.xiangxiziliao_back).performClick();
    }

    /**
     * 检查网络状态
     */
    private void checkNetwork() {
        if (!Network.HttpTest(this)) {
            Toast.makeText(this, "网络连接失败，请检查网络", Toast.LENGTH_SHORT);
            return;
        }
    }

    /**
     * 获取详细信息
     */
    private void getData() {
        ProgressUtil.show(this,"","请稍等");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject js = new JSONObject();
                    try {
                        js.put("m_id", Constants.M_id);
                        js.put("user_id", ID);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m = ApisSeUtil.i(js);
                    String data = OkGo.post(Constants.User_Info_Ip).tag(TAG)
                            .params("key", m.K())
                            .params("msg", m.M())
                            .execute().body().string();
                    if (!data.equals("")) {
                        Log.w(TAG, "run: 获取的用户资料" + data);
                        map = AnalyticalJSON.getHashMap(data);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                change_beizhu.setEnabled(true);
                                if (map != null) {
                                    Glide.with(User_Detail.this).load(map.get("user_image")).override(DimenUtils.dip2px(User_Detail.this, 70), DimenUtils.dip2px(User_Detail.this, 70))
                                            .centerCrop().placeholder(R.drawable.indra).into(head);
                                    name.setText(map.get("pet_name"));
                                    sign.setText("".equals(map.get("signature")) ? "暂无个性签名" : map.get("signature"));
                                    phone.setText("".equals(map.get("phone")) ? "该用户尚未绑定手机号码" : map.get("phone"));
                                    sex.setText("1".equals(map.get("sex")) ? "男" : "女");
                                    if (type == 3 || sp.getString("user_id", "").equals(ID)) {
                                        sixin.setVisibility(View.GONE);
                                    } else {
                                        change_beizhu.setVisibility(View.GONE);
                                        beizhu.setEnabled(false);
                                        sixin.setVisibility(View.VISIBLE);
                                        head.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                ScaleImageUtil.openBigIagmeMode(User_Detail.this, map.get("user_image"));
                                            }
                                        });
                                    }
//                                    if ("".equals(map.get("job"))) {
////                                        job.setText("未认证");
////                                        job.setBackgroundResource(R.drawable.button1_shape_enabled);
////                                        findViewById(R.id.head_tip).setVisibility(View.GONE);
//                                        zhifu.setVisibility(View.GONE);
//                                        guanzhu_right.setVisibility(View.GONE);
//                                    } else {
////                                        job.setText(map.get("job"));
////                                        findViewById(R.id.head_tip).setVisibility(View.VISIBLE);
//                                        if (getIntent().getStringExtra("concern_id") == null) {
//                                            zhifu.setVisibility(View.VISIBLE);
//                                            sixin.setVisibility(View.VISIBLE);
//                                            if (type == 1) {
//                                                guanzhu_right.setVisibility(View.GONE);
//                                            }
//
//                                        } else {
//                                            zhifu.setVisibility(View.GONE);
//                                        }
//                                    }
//                                    if (type == 3) {
//                                        zhifu.setVisibility(View.GONE);
//                                        sixin.setVisibility(View.GONE);
//                                        beizhulayout.setVisibility(View.GONE);
//                                    }
//                                    if (!"".equals(map.get("user_back"))) {
//                                        Glide.with(User_Detail.this).load(map.get("user_back")).override(screenWidth, DimenUtils.dip2px(User_Detail.this, 200))
//                                                .centerCrop()
//                                                .crossFade(1500)
//                                                .into(banner);
//                                        banner.setColorFilter(Color.parseColor("#60000000"));
//                                    }
//                                    job_tv.setText("".equals(map.get("job")) ? "暂未认证" : map.get("job"));

                                }
                            }
                        });

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ProgressUtil.dismiss();
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * 初始化
     */
    private void initView() {
        beizhulayout = (LinearLayout) findViewById(R.id.beizhu_layout);
        beizhu = (EditText) findViewById(R.id.beizhu_tv);
        beizhu.setOnClickListener(this);
        if (getIntent().getStringExtra("bz_name") != null && !getIntent().getStringExtra("bz_name").equals("null") &&
                !getIntent().getStringExtra("bz_name").equals("")) {
            beizhu.setText(getIntent().getStringExtra("bz_name"));
        }
        //判断是否需要修改群昵称
        if(getIntent().getBooleanExtra("beizhu",false)){
            beizhulayout.setVisibility(View.VISIBLE);
        }else{
            beizhulayout.setVisibility(View.GONE);
        }
        change_beizhu = (TextView) findViewById(R.id.change);
        change_beizhu.setEnabled(false);
        change_beizhu.setOnClickListener(this);
        guanzhu_right = (TextView) findViewById(R.id.guanzhu_right_top);
        guanzhu_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guanzhu.performClick();
            }
        });
        job_tv = (TextView) findViewById(R.id.job_tv);
        banner = (ImageView) findViewById(R.id.banner_detail);
        screenHeight = getResources().getDisplayMetrics().heightPixels;
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        sex = (TextView) findViewById(R.id.sex_tv);
        head = (ImageView) findViewById(R.id.head_imageview);
//        job = (TextView) findViewById(R.id.job_tip);
        name = (TextView) findViewById(R.id.username);
        sign = (TextView) findViewById(R.id.sign);
        headlayout = (RelativeLayout) findViewById(R.id.head_layout);
        headlayout.setOnClickListener(this);
        phone = (TextView) findViewById(R.id.phone_tv);
        zhifu = (TextView) findViewById(R.id.zhifu_bnt);
//        parent = (SlidingPaneLayout) zhifu.getParent().getParent().getParent().getParent().getParent().getParent();
        sixin = (TextView) findViewById(R.id.sixin_bnt);
        guanzhu = (TextView) findViewById(R.id.guanzhu_bnt);

        zhifu.setOnClickListener(this);
//        guanzhu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlertDialog.Builder b = new AlertDialog.Builder(User_Detail.this);
//                final View view = LayoutInflater.from(User_Detail.this).inflate(R.layout.guanzhu_dialog, null);
//                b.setView(view);
//                final Dialog dialog = b.create();
//                final EditText ed = (EditText) view.findViewById(R.id.guanzhu_msg);
//                ed.setText("我是" + PreferenceUtil.getUserIncetance(User_Detail.this).getString("pet_name", "") + ",请同意");
//                view.findViewById(R.id.cancle).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (dialog != null && dialog.isShowing()) dialog.dismiss();
//                    }
//                });
//                view.findViewById(R.id.commit).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View vi) {
//                        if (((EditText) view.findViewById(R.id.guanzhu_msg)).getText().toString().trim().equals("")) {
//                            Toast.makeText(User_Detail.this, "请填写认证信息", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//                        OkHttpUtils.post(Constants.little_guanzhu_IP).params("key", Constants.safeKey).params("concern_id", ID)
//                                .params("user_id", PreferenceUtil.getUserIncetance(User_Detail.this).getString("user_id", "")).params("m_id", Constants.M_id)
//                                .params("msg", ed.getText().toString().trim())
//                                .params("pet_name", PreferenceUtil.getUserIncetance(User_Detail.this).getString("pet_name", ""))
//                                .execute(new AbsCallback<Object>() {
//                                    @Override
//                                    public Object parseNetworkResponse(Response response) throws Exception {
//                                        return null;
//                                    }
//
//                                    @Override
//                                    public void onSuccess(Object o, Call call, Response response) {
//                                        try {
//                                            String data = response.body().string();
//                                            if (!data.equals("")) {
//                                                HashMap<String, String> map = AnalyticalJSON.getHashMap(data);
//                                                if (map != null) {
//                                                    if ("000".equals(map.get("code"))) {
//                                                        Toast.makeText(User_Detail.this, "关注请求发送成功，请等待对方同意", Toast.LENGTH_SHORT).show();
//                                                        dialog.dismiss();
//                                                    } else {
//                                                        dialog.dismiss();
//                                                        Toast.makeText(User_Detail.this, "您已对该用户发送过关注请求了", Toast.LENGTH_SHORT).show();
//                                                    }
//                                                } else {
//                                                    Toast.makeText(User_Detail.this, "关注请求失败，请稍后尝试", Toast.LENGTH_SHORT).show();
//                                                }
//                                            } else {
//                                                Toast.makeText(User_Detail.this, "关注请求失败，请稍后尝试", Toast.LENGTH_SHORT).show();
//                                            }
//                                        } catch (Exception e) {
//                                            Toast.makeText(User_Detail.this, "关注请求失败，请稍后尝试", Toast.LENGTH_SHORT).show();
//                                            e.printStackTrace();
//                                        }
//                                    }
//
//
//                                });
//                    }
//                });
//                dialog.show();
//                dialog.getWindow().setWindowAnimations(R.style.dialogWindowAnim);
//                WindowManager.LayoutParams wl = dialog.getWindow().getAttributes();
//                wl.width = User_Detail.this.getResources().getDisplayMetrics().widthPixels;
//                dialog.getWindow().setAttributes(wl);

//            }
//        });
        if (type == 3 || sp.getString("user_id", "").equals(ID)) {
            head.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    photoUtil.choosePic(User_Detail.this, 222);
                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data == null) {
                pictureUri = photoUtil.uri;
            } else {
                pictureUri = data.getData();
            }
            Bitmap bm = null;
//                    pictureUri = data.getData();// 选择照片的Uri 可能为null
            if (pictureUri != null) {
                //上传头像
                String path = ImageUtil.getImageAbsolutePath(mApplication.getInstance(), pictureUri);
                Log.w(TAG, "onActivityResult:  path++++++++-========" + path);
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
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.w(TAG, "onActivityResult: ___________." + new File(path).length());

                SharedPreferences.Editor ed = sp.edit();
                aCache.put("head_" + sp.getString("user_id", ""), bm);
                ed.putString("head_path", path);
                ed.apply();
                uploadHead(Headfile);
                bm = null;
            } else {
                Toast.makeText(mApplication.getInstance(), mApplication.ST("上传失败,请重新尝试"), Toast.LENGTH_SHORT).show();
            }


        }
    }

    public void uploadHead(final File file) {
        final String uid = sp.getString("user_id", "");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject js = new JSONObject();
                    try {
                        js.put("user_id", uid);
                        js.put("m_id", Constants.M_id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m = ApisSeUtil.i(js);
                    Response response = OkGo.post(Constants.uploadHead_IP).tag(TAG)
                            .params("head", file).params("key", m.K())
                            .params("msg", m.M()).execute();
                    String data1 = response.body().string();

                    if (!data1.equals("")) {
                        if (null != AnalyticalJSON.getHashMap(data1).get("code")) {
                            if ("000".equals(AnalyticalJSON.getHashMap(data1).get("code"))) {
                                SharedPreferences.Editor ed = sp.edit();
                                final String url = AnalyticalJSON.getHashMap(data1).get("head");
                                if (url != null) {
                                    ed.putString("head_url", url);
                                    ed.apply();

                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        EventBus.getDefault().post(url);
                                        Toast.makeText(mApplication.getInstance(), mApplication.ST("头像更改成功"), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(mApplication.getInstance(), mApplication.ST("头像更改失败"), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mApplication.getInstance(), mApplication.ST("服务器异常"), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                } catch (IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mApplication.getInstance(), mApplication.ST("服务器异常"), Toast.LENGTH_SHORT).show();
                        }
                    });
                    e.printStackTrace();
                } catch (IllegalStateException e) {

                }

            }
        }).start();

    }

    private void moveFllow(View v, int x, int y, float startX, float startY) {

        if (v.getY() >= DimenUtils.dip2px(this, 45) && v.getY() <= screenHeight - v.getHeight()) {
            v.setY(startY + y);
            if (v.getY() < DimenUtils.dip2px(this, 45)) {
                v.setY(DimenUtils.dip2px(this, 45));
            } else if (v.getY() > screenHeight - v.getHeight()) {
                v.setY(screenHeight - v.getHeight());
            }
        }

        if (v.getX() >= 0 && v.getX() <= (screenWidth - v.getWidth())) {
            v.setX(startX + x);
            if (v.getX() < DimenUtils.dip2px(this, 10)) {
                v.setX(DimenUtils.dip2px(this, 10));
            } else if (v.getX() > (screenWidth - v.getWidth())) {
                v.setX((screenWidth - v.getWidth()));
            }
        }
    }

    private void checkHozatal(final View v) {
        int left = (int) v.getX();
        Log.w(TAG, "onTouch: " + left);
        va = ValueAnimator.ofInt(60);
        va.setDuration(500);
        va.setInterpolator(new BounceInterpolator());
        if (left <= screenWidth / 2) {
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    v.setX(v.getX() - ((int) animation.getAnimatedValue()));
                    if (v.getX() <= 0) {
                        v.setX(0);
                        v.setSelected(false);
                        va.cancel();
                    }
                }
            });
        } else {
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    v.setX(v.getX() + ((int) animation.getAnimatedValue()));
                    if (v.getX() >= screenWidth - v.getWidth()) {
                        v.setX(screenWidth - v.getWidth());
                        v.setSelected(false);
                        va.cancel();
                    }
                }
            });
        }
        va.start();
    }

    /**
     * 修改昵称和签名
     */
    private void uploadNameandInfo() {
//        AlertDialog.Builder b = new AlertDialog.Builder(this);
//        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.change_name_info, null);
//        final EditText name1 = (EditText) view.findViewById(R.id.changeTitle);
//        name1.setHint("请输入昵称");
//        final EditText info = (EditText) view.findViewById(R.id.changeInfo);
//        info.setHint("请输入签名");
//        TextView commit = (TextView) view.findViewById(R.id.change_name_commit);
//        ((TextView) view.findViewById(R.id.title)).setText("昵称");
//        ((TextView) view.findViewById(R.id.info)).setText("签名");
//        name1.setText(name.getText().toString());
//        info.setText(sign.getText().toString());
//        b.setView(view);
//        dialog = b.create();
//        commit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                checkNetwork();
//                if (progresDialog == null) {
//                    progresDialog = ProgressDialog.show(User_Detail.this, null, "正在提交修改.....");
//                } else {
//                    progresDialog.setMessage("正在提交修改.....");
//                    progresDialog.show();
//                }
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            String data = OkGo.post(Constants.User_info_xiugainc)
//                                    .params("key", Constants.safeKey)
//                                    .params("user_id", PreferenceUtil.getUserIncetance(getApplicationContext()).getString("user_id", ""))
//                                    .params("pet_name", name1.getText().toString()).execute().body().string();
//                            if (!data.equals("")) {
//                                HashMap<String, String> map = AnalyticalJSON.getHashMap(data);
//                                if (map != null && "000".equals(map.get("code"))) {
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            name.setText(name1.getText().toString());
//                                            SharedPreferences sp = PreferenceUtil.getUserIncetance(getApplicationContext());
//                                            SharedPreferences.Editor ed = sp.edit();
//                                            ed.putString("pet_name", name1.getText().toString());
//                                            is1 = true;
//                                            if (is2) {
//                                                progresDialog.dismiss();
//                                                dialog.dismiss();
//                                                is1 = false;
//                                                is2 = false;
//                                            }
//                                        }
//                                    });
//                                }
//                            }
//                            String data1 = OkHttpUtils.post(Constants.SignChange)
//                                    .params("key", Constants.safeKey)
//                                    .params("user_id", PreferenceUtil.getUserIncetance(getApplicationContext()).getString("user_id", ""))
//                                    .params("signature", info.getText().toString()).execute().body().string();
//                            if (!data1.equals("")) {
//                                HashMap<String, String> map = AnalyticalJSON.getHashMap(data1);
//                                if (map != null && "000".equals(map.get("code"))) {
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            sign.setText(info.getText().toString());
//                                            SharedPreferences sp = PreferenceUtil.getUserIncetance(getApplicationContext());
//                                            SharedPreferences.Editor ed = sp.edit();
//                                            ed.putString("sign", info.getText().toString());
//                                            is2 = true;
//                                            if (is1 && is2) {
//                                                progresDialog.dismiss();
//                                                dialog.dismiss();
//                                                is1 = false;
//                                                is2 = false;
//                                            }
//                                        }
//                                    });
//                                }
//                            }
//                        } catch (IOException e) {
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    progresDialog.dismiss();
//                                    Toast.makeText(User_Detail.this, "修改失败，请稍后重试", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                            e.printStackTrace();
//                        }
//                    }
//                }).start();
//            }
//        });
//        Window window = dialog.getWindow();
//        window.setWindowAnimations(R.style.dialogWindowAnim);
//        window.setBackgroundDrawableResource(R.color.vifrification);
//        dialog.show();
    }


    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.beizhu_tv:
                beizhu.setFocusable(true);
                beizhu.requestFocus();
                beizhu.findFocus();
                break;
            case R.id.change:
                final TextView t = ((TextView) v);
//                if (t.getText().toString().equals("修改")) {
//                    beizhu.setTextColor(Color.BLACK);
//                    beizhu.setEnabled(true);
//                    beizhu.requestFocus();
//                    if (beizhu.length() != 0)
//                        beizhu.setSelection(beizhu.length() - 1);
//                    t.setText("提交");
//                } else if (t.getText().toString().equals("提交")) {
                if(beizhu.getText().toString().equals("")){
                    ToastUtil.showToastShort("请填写群昵称");
                    return;
                }
                    ProgressUtil.show(this, "", "正在提交");
                JSONObject js=new JSONObject();
                try {
                    js.put("id", getIntent().getStringExtra("class_id"));
                    js.put("name", beizhu.getText().toString().trim());
                    js.put("m_id",Constants.M_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ApisSeUtil.M m=ApisSeUtil.i(js);
                LogUtil.e("群昵称设置:"+js);
                OkGo.post(Constants.Class_Name__IP)
                        .params("key",m.K())
                            .params("msg",m.M())
                            .execute(new AbsCallback<Object>() {
                                @Override
                                public Object convertSuccess(Response response) throws Exception {
                                    return null;
                                }

                                @Override
                                public void onAfter(@Nullable Object o, @Nullable Exception e) {
                                    super.onAfter(o, e);
                                    ProgressUtil.dismiss();
                                }

                                @Override
                                public void onSuccess(Object o, Call call, Response response) {
                                    if (response != null) {
                                        try {
                                            String data = response.body().string();
                                            if (!data.equals("")) {
                                                final HashMap<String, String> map = AnalyticalJSON.getHashMap(data);
                                                if (map != null) {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            if("000".equals(map.get("code"))){
                                                                Toast.makeText(User_Detail.this, "群昵称修改成功", Toast.LENGTH_SHORT).show();
                                                                beizhu.setEnabled(false);
                                                                beizhu.setTextColor(Color.parseColor("#888888"));
                                                                t.setText("修改");
                                                                Intent intent = new Intent("bz");
                                                                sendBroadcast(intent);
                                                            }

                                                        }
                                                    });
                                                }
                                            } else {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(User_Detail.this, "备注修改失败，请检查网络稍后重试", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        } catch (Exception e) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(User_Detail.this, "备注修改失败，请检查网络稍后重试", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });


//                }
                break;
            case R.id.xiangxiziliao_back:
                finish();
                break;
            case R.id.head_layout:
//            uploadNameandInfo();
                break;
            case R.id.sixin_bnt:
                Intent intent1 = new Intent(this, Ask_Detail.class);
                intent1.putExtra("id", map.get("id"));
                startActivity(intent1);
                break;
            case R.id.zhifu_bnt:
//                View view = null;
//                getPay();
//                AlertDialog.Builder b = new AlertDialog.Builder(this);
//                view = LayoutInflater.from(this).inflate(R.layout.erweima_dialog, null);
//                ListView listView = (ListView) view.findViewById(R.id.zhifu_listview);
//                ProgressBar p = (ProgressBar) view.findViewById(R.id.zhifu_progress);
//                listView.setEmptyView(p);
//                if (zfAdapter == null)
//                    zfAdapter = new ZFAdapter();
//                listView.setAdapter(zfAdapter);
//                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        Intent intent = new Intent(User_Detail.this, QRActivity.class);
//                        intent.putExtra("url", zfAdapter.list.get(position).get("pay"));
//                        intent.putExtra("name", zfAdapter.list.get(position).get("name"));
//                        Toast.makeText(User_Detail.this, "长按图片进行操作", Toast.LENGTH_SHORT).show();
//                        startActivity(intent);
//                    }
//                });
//                b.setView(view);
//                dialog = b.create();
//                view.findViewById(R.id.cancle).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                    }
//                });
//
//                Window window = dialog.getWindow();
//                window.setWindowAnimations(R.style.dialogWindowAnim);
//                window.setBackgroundDrawableResource(R.color.vifrification);
//                window.setDimAmount(0.4f);
//                window.setGravity(Gravity.BOTTOM);
//                dialog.show();
//                WindowManager.LayoutParams wl = window.getAttributes();
//                wl.width = getResources().getDisplayMetrics().widthPixels - DimenUtils.dip2px(User_Detail.this, 20);
//                wl.height = getResources().getDisplayMetrics().heightPixels * 3 / 5;
//                window.setAttributes(wl);

                break;
//            case R.id.zixun_bnt:
//                Intent intent2 = new Intent(User_Detail.this, ZiXun_List.class);
//                intent2.putExtra("id", ID);
//                intent2.putExtra("isMe", false);
//                startActivity(intent2);

//                break;


        }
    }

    public class ZFAdapter extends BaseAdapter {
        public ArrayList<HashMap<String, String>> list;

        public void setList(ArrayList<HashMap<String, String>> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            viewHolder holder;
            if (view == null) {
                holder = new viewHolder();
                holder.text = new TextView(User_Detail.this);
                view = holder.text;
                holder.text.setTextSize(16);
                holder.text.setTextColor(getResources().getColor(R.color.main_color));
                holder.text.setGravity(Gravity.CENTER);
                holder.text.setPadding(0, DimenUtils.dip2px(User_Detail.this, 8), 0, DimenUtils.dip2px(User_Detail.this, 8));
                view.setTag(holder);
            } else {
                holder = (viewHolder) view.getTag();
            }
            holder.text.setText(list.get(position).get("name"));
            return view;
        }

        class viewHolder {
            TextView text;
        }
    }


}
