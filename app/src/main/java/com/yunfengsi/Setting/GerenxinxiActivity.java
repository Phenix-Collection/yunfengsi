package com.yunfengsi.Setting;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.yunfengsi.R;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.ImageUtil;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.ProgressUtil;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.SystemUtil;
import com.yunfengsi.Utils.ToastUtil;
import com.yunfengsi.Utils.Verification;
import com.yunfengsi.Utils.mApplication;
import com.yunfengsi.ZhiFuShare;

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

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import okhttp3.Call;
import okhttp3.Response;

import static com.yunfengsi.Utils.photoUtil.CHOOSEPICTUE;
import static com.yunfengsi.Utils.photoUtil.TAKEPICTURE;

public class GerenxinxiActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView mtvxinbie;     //性别
    private TextView mtvnc;        //昵称
    private TextView mtvphone;   //手机号
    //    private TextView mtvid;     //身份证号
//    private TextView mtvsimiao;  //所属寺庙
    private TextView mtvyonghuleixing;   //用户类型
    //    private TextView mtvtime;      //注册时间
    private AvatarImageView mcircleview; //RoundedImageView头像
    private SharedPreferences sp;
    private String xb;
    private HashMap<String, String> map, moreMap = null;
    private int screenWidth;
    private TextView tvPerfect;
    private Uri pictureUri = null;
    private AlertDialog dialog;
    private File Headfile;
    private TextView more, trueName, faName, shenfenzheng, address, workPlace, xiuxingjingli, job, morePhone, carId;
    private JSONObject jsonObject;

    public void init() {
        ((TextView) findViewById(R.id.titletv)).setText(mApplication.ST("个人信息"));
        ((TextView) findViewById(R.id.nametv)).setText(mApplication.ST("昵称"));
        ((TextView) findViewById(R.id.phonetv)).setText(mApplication.ST("手机号码"));
        ((TextView) findViewById(R.id.sextv)).setText(mApplication.ST("性别"));
        ((TextView) findViewById(R.id.signtv)).setText(mApplication.ST("个人签名"));
        ((TextView) findViewById(R.id.tnametv)).setText(mApplication.ST("真实姓名"));
        ((TextView) findViewById(R.id.fnametv)).setText(mApplication.ST("法名"));
        ((TextView) findViewById(R.id.midtv)).setText(mApplication.ST("身份证号码"));
        ((TextView) findViewById(R.id.addresstv)).setText(mApplication.ST("家庭住址"));
        ((TextView) findViewById(R.id.worktv)).setText(mApplication.ST("工作单位"));
        ((TextView) findViewById(R.id.lasttv)).setText(mApplication.ST("修行经历"));
        ((TextView) findViewById(R.id.jobtv)).setText(mApplication.ST("职业"));
        ((TextView) findViewById(R.id.morePhone)).setText(mApplication.ST("紧急联系人手机"));
        ((TextView) findViewById(R.id.cartv)).setText(mApplication.ST("车牌登记"));
        mtvxinbie = (TextView) findViewById(R.id.gerenxinxi_xingbie_tv);
        mtvnc = (TextView) findViewById(R.id.gerenxinxi_nichengz_tv);
        mtvphone = (TextView) findViewById(R.id.gerenxinxi_phone_tv);
//    mtvid=(TextView) findViewById(R.id.gerenxinxi_shenfenz_tv);
//    mtvsimiao=(TextView) findViewById(R.id.gerenxinxi_shimiao_tv);
//    mtvyonghuleixing=(TextView) findViewById(R.id.gerenxinxi_yonghuleixing_tv);
//    mtvtime=(TextView) findViewById(R.id.gerenxinxi_time_tv);
        mcircleview = (AvatarImageView) findViewById(R.id.gerenxinxi_touxiang_cicleimageview);
        sp = getSharedPreferences("user", MODE_PRIVATE);
        tvPerfect = (TextView) findViewById(R.id.perfect);
        tvPerfect.setText(mApplication.ST("完善资料"));
        tvPerfect.setOnClickListener(this);
        more = (TextView) findViewById(R.id.geren_more);
        more.setText(mApplication.ST("更多资料"));
        if (sp.getString("perfect", "1").equals("1")) {
            tvPerfect.setText(mApplication.ST("完善资料"));
        } else {
            tvPerfect.setVisibility(View.GONE);
//            more.append(mApplication.ST("\n[修改个人信息请联系:15397639879或106889@qq.com]"));
            getMore();
        }

        trueName = (TextView) findViewById(R.id.tv_trueName);
        faName = (TextView) findViewById(R.id.tv_faMing);
        shenfenzheng = (TextView) findViewById(R.id.tv_shenfenzheng);
        address = (TextView) findViewById(R.id.tv_address);
        workPlace = (TextView) findViewById(R.id.tv_workPlace);
        xiuxingjingli = (TextView) findViewById(R.id.tv_xiuxingjingli);
        job = (TextView) findViewById(R.id.tv_job);
        morePhone = (TextView) findViewById(R.id.tv_morePhone);
        carId = (TextView) findViewById(R.id.tv_carId);

        screenWidth = this.getResources().getDisplayMetrics().widthPixels;
        final RelativeLayout r = (RelativeLayout) findViewById(R.id.back_bg);
        Glide.with(this).load(R.drawable.mine_banner)
                .asBitmap().override(screenWidth, DimenUtils.dip2px(this, 150))
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        r.setBackground(new BitmapDrawable(resource));
                    }
                });
        findViewById(R.id.jiatingzhuzhi).setOnClickListener(changeClickListener);
        findViewById(R.id.faming).setOnClickListener(changeClickListener);
        findViewById(R.id.gongzuodanwei).setOnClickListener(changeClickListener);
        findViewById(R.id.xiuxingjingli).setOnClickListener(changeClickListener);
        findViewById(R.id.zhiye).setOnClickListener(changeClickListener);
        findViewById(R.id.jinjilianxirenshouji).setOnClickListener(changeClickListener);
        findViewById(R.id.chepaidengji).setOnClickListener(changeClickListener);
        findViewById(R.id.name).setOnClickListener(this);
        findViewById(R.id.xingbie).setOnClickListener(this);
        findViewById(R.id.sign).setOnClickListener(this);
        mcircleview.setOnClickListener(this);
        findViewById(R.id.bangzhu).setOnClickListener(this);
    }

    private View.OnClickListener changeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view2) {
            AlertDialog.Builder builder = new AlertDialog.Builder(GerenxinxiActivity.this);
            View view1 = LayoutInflater.from(GerenxinxiActivity.this).inflate(R.layout.user_info_change_dialog, null);
            builder.setView(view1);
            final EditText editText = (EditText) view1.findViewById(R.id.edittext);
            TextView title = (TextView) view1.findViewById(R.id.title);
            TextView cancle = (TextView) view1.findViewById(R.id.cancle);
            TextView commit = (TextView) view1.findViewById(R.id.commit);
            final AlertDialog dialog = builder.create();
            cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            jsonObject = new JSONObject();
            commit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (editText.getText().toString().equals("")) {
                        ToastUtil.showToastShort("请输入修改信息");
                        return;
                    }

                    commit(view2, dialog, jsonObject, editText);
                }


            });

            String hint = "";
            switch (view2.getId()) {
                case R.id.jiatingzhuzhi://家庭住址
                    hint = "请输入您的家庭住址";
                    title.setText("修改家庭住址");

                    editText.setText(moreMap == null ? "" : moreMap.get("address"));
                    break;
                case R.id.faming://法名
                    hint = "请输入您的法名";
                    title.setText("修改法名");
                    editText.setText(moreMap == null ? "" : moreMap.get("farmington"));
                    break;
                case R.id.gongzuodanwei://工作单位
                    hint = "请输入您的工作单位";
                    title.setText("修改工作单位");
                    editText.setText(moreMap == null ? "" : moreMap.get("workunit"));
                    break;
                case R.id.xiuxingjingli://修行经历
                    hint = "请输入您的修行经历";
                    title.setText("修改修行经历");
                    editText.setText(moreMap == null ? "" : moreMap.get("practice"));
                    break;
                case R.id.zhiye://职业
                    hint = "请输入您的的职业";
                    title.setText("修改职业");
                    editText.setText(moreMap == null ? "" : moreMap.get("work"));
                    break;
                case R.id.jinjilianxirenshouji://紧急联系人手机号码
                    hint = "请输入您的紧急联系人手机号码";
                    title.setText("修改紧急联系人号码");
                    editText.setInputType(InputType.TYPE_CLASS_PHONE);
                    editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
                    editText.setText(moreMap == null ? "" : moreMap.get("contact"));
                    break;
                case R.id.chepaidengji://车牌登记
                    hint = "请输入您的车牌";
                    editText.setText(moreMap == null ? "" : moreMap.get("plate"));
                    break;
            }
            editText.setHint(hint);
            dialog.show();
        }
    };

    private void commit(View view, final AlertDialog dialog, JSONObject jsonObject, EditText editText) {
        try {
            jsonObject.put("user_id", PreferenceUtil.getUserId(GerenxinxiActivity.this));
            jsonObject.put("m_id", Constants.M_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        switch (view.getId()) {
            case R.id.jiatingzhuzhi://家庭住址
                try {
                    jsonObject.put("address", editText.getText().toString());
                } catch (JSONException e) {
                    LogUtil.e("jsong错误啦啦啦啦啦啦啦啦啦");
                    e.printStackTrace();
                }
                break;
            case R.id.faming://法名
                try {
                    jsonObject.put("farmington", editText.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.gongzuodanwei://工作单位
                try {
                    jsonObject.put("workunit", editText.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.xiuxingjingli://修行经历
                try {
                    jsonObject.put("practice", editText.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.zhiye://职业
                try {
                    jsonObject.put("work", editText.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.jinjilianxirenshouji://紧急联系人手机号码
                if (!Verification.isMobileNO(editText.getText().toString().trim())) {
                    ToastUtil.showToastShort("请输入正确的手机号码");
                    return;
                }
                try {
                    jsonObject.put("contact", editText.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.chepaidengji://车牌登记
                try {
                    jsonObject.put("plate", editText.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }

        ApisSeUtil.M m = ApisSeUtil.i(jsonObject);
        LogUtil.e("js!@!@!@!@!@!" + jsonObject);
        OkGo.post(Constants.Dzgrzlxg).params("key", m.K())
                .params("msg", m.M()).execute(new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                HashMap<String, String> map = AnalyticalJSON.getHashMap(s);
                if (map != null) {
                    if ("000".equals(map.get("code"))) {
                        ToastUtil.showToastShort("修改成功");
                        dialog.dismiss();
                        getMore();
                    }
                }
            }

            @Override
            public void onBefore(BaseRequest request) {
                super.onBefore(request);
                ProgressUtil.show(GerenxinxiActivity.this, "", "正在提交修改，请稍等");
            }

            @Override
            public void onAfter(String s, Exception e) {
                super.onAfter(s, e);
                ProgressUtil.dismiss();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerenxinxi);
        StatusBarCompat.compat(this, ContextCompat.getColor(this, R.color.main_color));
        init();
        getdata();  //数据交互方法


    }

    /*
    获取更多资料
     */
    private void getMore() {
        JSONObject js = new JSONObject();
        try {
            js.put("user_id", sp.getString("user_id", ""));
            js.put("m_id", Constants.M_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkGo.post(Constants.getMoreInfo).params("key", ApisSeUtil.getKey())
                .params("msg", ApisSeUtil.getMsg(js)).execute(new AbsCallback<Object>() {
            @Override
            public Object convertSuccess(Response response) throws Exception {
                return null;
            }

            @Override
            public void onSuccess(Object o, Call call, Response response) {
                if (response != null) {
                    try {
                        String data = response.body().string();
                        if (!"".equals(data)) {
                            moreMap = AnalyticalJSON.getHashMap(data);
                            if (moreMap != null) {
                                findViewById(R.id.moreInfo).setVisibility(View.VISIBLE);
                                ((TextView) findViewById(R.id.gerenxinxi_sign_tv)).setText("".equals(moreMap.get("signature")) ? "未填写" : moreMap.get("signature"));
                                trueName.setText("".equals(moreMap.get("name")) ? "未填写" : moreMap.get("name"));
                                faName.setText(mApplication.ST(moreMap.get("farmington")));
                                shenfenzheng.setText(moreMap.get("cid"));
                                address.setText(mApplication.ST(moreMap.get("address")));
                                workPlace.setText(mApplication.ST(moreMap.get("workunit")));
                                xiuxingjingli.setText(mApplication.ST(moreMap.get("practice")));
                                job.setText(mApplication.ST(moreMap.get("work")));
                                morePhone.setText(mApplication.ST(moreMap.get("contact")));
                                carId.setText(moreMap.get("plate"));
//                                Glide.with(GerenxinxiActivity.this).load(map.get("cidimage")).thumbnail(0.1f)
//                                        .into(moreImg);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }


        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.gerenxinxi_back:
                finish();
                break;
            case R.id.perfect:
                if (sp.getString("perfect", "1").equals("1")) {
                    Intent intent = new Intent(this, ZhiFuShare.class);
                    intent.putExtra(ZhiFuShare.ISFORM, true);
                    startActivityForResult(intent, 666);
//                    finish();
                }
                break;
            case R.id.name:
                Intent intent = new Intent(GerenxinxiActivity.this, NiCTemple_Activity.class);
                intent.putExtra("title", "昵称");
                startActivityForResult(intent, 999);
                break;
            case R.id.xingbie:
                startActivityForResult(new Intent(GerenxinxiActivity.this, XinBieActivity.class), 999);
                break;
            case R.id.sign:
                startActivityForResult(new Intent(GerenxinxiActivity.this, Sign.class), 999);
                break;
            case R.id.gerenxinxi_touxiang_cicleimageview:
                choosePic();
                break;
            case R.id.bangzhu:
                Intent i = new Intent(GerenxinxiActivity.this, GanyuActivity.class);
                startActivity(i);
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.e("resultCode:::" + resultCode);
        if (resultCode == 66) {//完善资料成功 刷新界面
            tvPerfect.setVisibility(View.GONE);
//            more.append(mApplication.ST("\n[修改个人信息请联系:15397639879或106889@qq.com]"));
            getMore();
        }
        if (requestCode == 999) {
            getdata();
        }

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CHOOSEPICTUE:
                    Bitmap bm = null;
                    if (dialog != null)
                        dialog.dismiss();
                    pictureUri = data.getData();// 选择照片的Uri 可能为null
                    if (pictureUri != null) {
                        //上传头像
                        String path = ImageUtil.getImageAbsolutePath(mApplication.getInstance(), pictureUri);
                        if (path.endsWith("webp") || path.endsWith("WEBP")) {
                            Toast.makeText(this, mApplication.ST("暂不支持该图片格式，请重新选择"), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        bm = ImageUtil.getImageThumbnail(path, screenWidth / 4, screenWidth / 4);
                        mcircleview.setImageBitmap(bm);
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
                        String path = ImageUtil.getRealPathFromURI(this, pictureUri);
                        bm = ImageUtil.getImageThumbnail(path, screenWidth / 4, screenWidth / 4);
                        mcircleview.setImageBitmap(bm);
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

                        ed.putString("head_path", path);
                        ed.apply();
                        uploadHead(Headfile);
                    } else {
                        Toast.makeText(mApplication.getInstance(), mApplication.ST("上传失败,请重新尝试"), Toast.LENGTH_SHORT).show();
                    }


                    break;
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
                    js.put("user_id", uid);
                    js.put("m_id",Constants.M_id);
                    Response response = OkGo.post(Constants.uploadHead_IP).tag(TAG)
                            .params("head", file)
                            .params("key", ApisSeUtil.getKey())
                            .params("msg", ApisSeUtil.getMsg(js)).execute();
                    String data1 = response.body().string();

                    if (!data1.equals("")) {
                        HashMap<String, String> map = AnalyticalJSON.getHashMap(data1);
                        if (map != null && null != map.get("code")) {
                            if ("000".equals(map.get("code"))) {
                                SharedPreferences.Editor ed = sp.edit();
                                String url = map.get("head");
                                if (url != null) {
                                    ed.putString("head_url", url);
                                    ed.apply();
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent inte = new Intent("Mine");
                                        sendBroadcast(inte);
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
                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
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

    private static final String TAG = "GerenxinxiActivity";

    public void getdata() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String data1 = null;
                try {
                    JSONObject js = new JSONObject();
                    try {
                        js.put("user_id", sp.getString("user_id", ""));
                        js.put("m_id", Constants.M_id);
                        js.put("type", "1");
                        js.put("phonename", SystemUtil.getSystemModel());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    LogUtil.e("个人资料：：个人信息：："+js);
                    ApisSeUtil.M m=ApisSeUtil.i(js);
                    data1 = OkGo.post(Constants.User_Info_Ip)
                            .params("key", m.K())
                            .params("msg", m.M()).execute()
                            .body().string();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (data1 != null && !data1.equals("")) {

                    map = AnalyticalJSON.getHashMap(data1);
                    if (map != null && map.get("code") == null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mtvnc.setText("".equals(map.get("pet_name")) ? "未填写" : map.get("pet_name"));
//                                mtvtime.setText(map.get("user_time"));
//                                mtvsimiao.setText(map.get("user_temple"));
//                                mtvid.setText(map.get("user_cid"));
                                mtvphone.setText(map.get("phone").equals("") ? mApplication.ST("暂未绑定手机号") : map.get("phone"));
                                Glide.with(GerenxinxiActivity.this).load(map.get("user_image")).into(mcircleview);
                                if (map.get("sex").equals("1")) {
                                    mtvxinbie.setText(mApplication.ST("男"));
                                } else if (map.get("sex").equals("2")) {
                                    mtvxinbie.setText(mApplication.ST("女"));
                                } else {
                                    mtvxinbie.setText(mApplication.ST("未填写"));
                                }
                                ((TextView) findViewById(R.id.gerenxinxi_sign_tv)).setText("".equals(map.get("signature")) ? "未填写" : map.get("signature"));

                            }
                        });
                    } else if (map != null && map.get("code") != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GerenxinxiActivity.this, mApplication.ST("用户信息获取失败"), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }

            }
        }).start();
    }

    //选择照片或照相
    private void choosePic() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(mApplication.getInstance()).inflate(R.layout.dialog_album_camera, null);
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
                    int permissionCheck = ContextCompat.checkSelfPermission(mApplication.getInstance(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
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
        window.getDecorView().setPadding(0,0,0,0);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.dialogWindowAnim);
        window.setBackgroundDrawableResource(R.color.vifrification);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.width = screenWidth;
        window.setAttributes(wl);
        dialog.show();
    }

    /**
     * 调用相机
     */
    private void chooseCamera() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss",
                Locale.CHINA);
        // Standard Intent action that can be sent to have the camera
        // application capture an image and return it.
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ContentValues attrs = new ContentValues();
        attrs.put(MediaStore.Images.Media.DISPLAY_NAME,
                dateFormat.format(new Date(System.currentTimeMillis())));// 添加照片名字
        attrs.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");// 图片类型
        attrs.put(MediaStore.Images.Media.DESCRIPTION, "");// 图片描述
        // //插入图片 成功则返回图片所对应的URI 当然我们可以自己指定图片的位置
        pictureUri = getContentResolver().insert(
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

