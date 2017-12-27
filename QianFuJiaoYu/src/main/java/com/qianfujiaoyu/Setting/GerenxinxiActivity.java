package com.qianfujiaoyu.Setting;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.qianfujiaoyu.R;
import com.qianfujiaoyu.Utils.AnalyticalJSON;
import com.qianfujiaoyu.Utils.ApisSeUtil;
import com.qianfujiaoyu.Utils.Constants;
import com.qianfujiaoyu.Utils.PreferenceUtil;
import com.qianfujiaoyu.Utils.StatusBarCompat;
import com.qianfujiaoyu.Utils.mApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import okhttp3.Call;
import okhttp3.Response;

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
    private HashMap<String, String> map;
    private int screenWidth;
    private TextView tvPerfect;

    private TextView more,trueName, faName, shenfenzheng, address, workPlace, xiuxingjingli, job, morePhone, carId;
    private ImageView moreImg;

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
        more= (TextView) findViewById(R.id.geren_more);
        more.setText(mApplication.ST("更多资料"));
        if (sp.getString("perfect", "1").equals("1")) {
            tvPerfect.setText(mApplication.ST("完善资料"));
        } else {
            tvPerfect.setVisibility(View.GONE);
            more.append(mApplication.ST("\n[修改个人信息请联系:15397639879或106889@qq.com]"));
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
        moreImg = (ImageView) findViewById(R.id.image);
        screenWidth = this.getResources().getDisplayMetrics().widthPixels;
        final RelativeLayout r= (RelativeLayout) findViewById(R.id.back_bg);
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
        JSONObject js=new JSONObject();
        try {
            js.put("m_id",Constants.M_id);
            js.put("user_id", PreferenceUtil.getUserIncetance(this).getString("user_id",""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m=ApisSeUtil.i(js);
        OkGo.post(Constants.getMoreInfo)
                .params("key",m.K())
                .params("msg",m.M()).execute(new AbsCallback<HashMap<String,String>>() {
            @Override
            public HashMap<String,String> convertSuccess(Response response) throws Exception {
                return  AnalyticalJSON.getHashMap(response.body().string());
            }

            @Override
            public void onSuccess(HashMap<String,String> map, Call call, Response response) {
                findViewById(R.id.moreInfo).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.gerenxinxi_sign_tv)).setText(map.get("signature"));
                trueName.setText(map.get("name"));
                faName.setText(mApplication.ST(map.get("farmington")));
                shenfenzheng.setText(map.get("cid"));
                address.setText(mApplication.ST(map.get("address")));
                workPlace.setText(mApplication.ST(map.get("workunit")));
                xiuxingjingli.setText(mApplication.ST(map.get("practice")));
                job.setText(mApplication.ST(map.get("work")));
                morePhone.setText(mApplication.ST(map.get("contact")));
                carId.setText(map.get("plate"));
                Glide.with(GerenxinxiActivity.this).load(map.get("cidimage")).thumbnail(0.1f)
                        .into(moreImg);
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


        }
    }

    private static final String TAG = "GerenxinxiActivity";

    public void getdata() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String data1 = null;
                try {
                    JSONObject js=new JSONObject();
                    try {
                        js.put("m_id",Constants.M_id);
                        js.put("user_id", PreferenceUtil.getUserIncetance(mApplication.getInstance()).getString("user_id",""));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m=ApisSeUtil.i(js);
                    data1 = OkGo.post(Constants.User_Info_Ip)
                            .params("key",m.K())
                            .params("msg",m.M()).execute()
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
                                mtvphone.setText(map.get("phone").equals("") ? mApplication.ST("暂未绑定手机号") : map.get("phone"));
                                Glide.with(GerenxinxiActivity.this).load(map.get("user_image")).into(mcircleview);
                                if (map.get("sex").equals("1")) {
                                    mtvxinbie.setText(mApplication.ST("男"));
                                } else if (map.get("sex").equals("2")) {
                                    mtvxinbie.setText(mApplication.ST("女"));
                                }

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

