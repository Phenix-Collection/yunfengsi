package com.maimaizu.Mine;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.maimaizu.R;
import com.maimaizu.Utils.AnalyticalJSON;
import com.maimaizu.Utils.Constants;
import com.maimaizu.Utils.DimenUtils;
import com.maimaizu.Utils.StatusBarCompat;
import com.maimaizu.Utils.mApplication;

import java.util.HashMap;

import jp.wasabeef.glide.transformations.CropCircleTransformation;


public class GerenxinxiActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView mtvxinbie;     //性别
    private TextView mtvnc;        //昵称
    private TextView mtvphone;   //手机号
    //    private TextView mtvid;     //身份证号
//    private TextView mtvsimiao;  //所属寺庙
    private TextView mtvyonghuleixing;   //用户类型
    //    private TextView mtvtime;      //注册时间
    private ImageView mcircleview; //RoundedImageView头像
    private SharedPreferences sp;
    private String xb;
    private HashMap<String, String> map;
    private int screenWidth;
    private TextView tvPerfect;

    private TextView trueName, faName, shenfenzheng, address, workPlace, xiuxingjingli, job, morePhone, carId;
    private ImageView moreImg;

    public void init() {
        mtvxinbie = (TextView) findViewById(R.id.gerenxinxi_xingbie_tv);
        mtvnc = (TextView) findViewById(R.id.gerenxinxi_nichengz_tv);
        mtvphone = (TextView) findViewById(R.id.gerenxinxi_phone_tv);
//    mtvid=(TextView) findViewById(R.id.gerenxinxi_shenfenz_tv);
//    mtvsimiao=(TextView) findViewById(R.id.gerenxinxi_shimiao_tv);
//    mtvyonghuleixing=(TextView) findViewById(R.id.gerenxinxi_yonghuleixing_tv);
//    mtvtime=(TextView) findViewById(R.id.gerenxinxi_time_tv);
        mcircleview = (ImageView) findViewById(R.id.gerenxinxi_touxiang_cicleimageview);
        sp = getSharedPreferences("user", MODE_PRIVATE);
//        tvPerfect = (TextView) findViewById(R.id.perfect);
//        tvPerfect.setOnClickListener(this);
//        if (sp.getString("perfect", "1").equals("1")) {
//            tvPerfect.setText("完善资料");
//        } else {
//            tvPerfect.setVisibility(View.GONE);
//
//        }
        ((TextView) findViewById(R.id.titletv)).setText(mApplication.ST("个人信息"));
        ((TextView) findViewById(R.id.nametv)).setText(mApplication.ST("昵称"));
        ((TextView) findViewById(R.id.phonetv)).setText(mApplication.ST("手机号码"));
        ((TextView) findViewById(R.id.sextv)).setText(mApplication.ST("性别"));
        ((TextView) findViewById(R.id.signtv)).setText(mApplication.ST("个人签名"));
        trueName = (TextView) findViewById(R.id.tv_trueName);
        faName = (TextView) findViewById(R.id.tv_faMing);
        shenfenzheng = (TextView) findViewById(R.id.tv_shenfenzheng);
        address = (TextView) findViewById(R.id.tv_address);
        workPlace = (TextView) findViewById(R.id.tv_workPlace);
        xiuxingjingli = (TextView) findViewById(R.id.tv_xiuxingjingli);
        job = (TextView) findViewById(R.id.tv_job);
        morePhone = (TextView) findViewById(R.id.tv_morePhone);
        carId = (TextView) findViewById(R.id.tv_carId);
        moreImg = (ImageView) findViewById(R.id.image);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerenxinxi);
        StatusBarCompat.compat(this, ContextCompat.getColor(this, R.color.main_color));
        init();
        getdata();  //数据交互方法

        screenWidth = this.getResources().getDisplayMetrics().widthPixels;


    }



    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.gerenxinxi_back:
                finish();
                break;
//            case R.id.perfect:
//                if (sp.getString("perfect", "1").equals("1")) {
//                    Intent intent = new Intent(this, user_Info_First.class);
//                    startActivity(intent);
//                    finish();
//                }
//                break;

        }
    }

    private static final String TAG = "GerenxinxiActivity";

    public void getdata() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String data1 = null;
                try {
                    data1 = OkGo.post(Constants.User_Info_Ip).params("user_id", sp.getString("user_id", ""))
                            .params("key", Constants.safeKey).execute()
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
                                mtvnc.setText(map.get("pet_name"));
//                                mtvtime.setText(map.get("user_time"));
//                                mtvsimiao.setText(map.get("user_temple"));
//                                mtvid.setText(map.get("user_cid"));
                                mtvphone.setText(mApplication.ST(map.get("phone").equals("") ? "暂未绑定手机号" : map.get("phone")));
                                Glide.with(GerenxinxiActivity.this).load(map.get("user_image")).bitmapTransform(new CropCircleTransformation(GerenxinxiActivity.this))
                                        .override(DimenUtils.dip2px(getApplicationContext(),80),DimenUtils.dip2px(getApplicationContext(),80)).into(mcircleview);
                                if (map.get("sex").equals("1")) {
                                    mtvxinbie.setText(mApplication.ST("男"));
                                } else if (map.get("sex").equals("2")) {
                                    mtvxinbie.setText(mApplication.ST("女"));
                                }
                                ((TextView) findViewById(R.id.gerenxinxi_sign_tv)).setText(map.get("signature"));

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


}

