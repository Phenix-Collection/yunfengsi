package com.yunfengsi.Models.YunDou;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.yunfengsi.R;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.Network;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.ScaleImageUtil;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.TimeUtils;
import com.yunfengsi.Utils.ToastUtil;
import com.yunfengsi.Utils.mApplication;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 作者：因陀罗网 on 2018/4/20 16:47
 * 公司：成都因陀罗网络科技有限公司
 */
public class QuanDetail extends AppCompatActivity {
    HashMap<String, String> map;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.quan_detail);

        ((ImageView) findViewById(R.id.title_back)).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.title_title)).setText(mApplication.ST("兑换券"));
        ((ImageView) findViewById(R.id.title_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        map = (HashMap<String, String>) getIntent().getSerializableExtra("map");
        Glide.with(this).load(map.get("image")).animate(R.anim.left_in)
                .into((ImageView) findViewById(R.id.image));
        ((TextView) findViewById(R.id.title)).setText(mApplication.ST(map.get("title")));
        ((TextView) findViewById(R.id.cost)).setText(mApplication.ST("兑换需消耗" + map.get("cost") + "云豆，库存" + map.get("stock") + "份"));
        ((TextView) findViewById(R.id.info)).setText(mApplication.ST(map.get("abstract")));
        ((TextView) findViewById(R.id.date)).setText(mApplication.ST("有效期至：" + TimeUtils.getTrueTimeStr(map.get("end_time"))));
        findViewById(R.id.image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScaleImageUtil.openBigIagmeMode(QuanDetail.this, map.get("image"),true);
            }
        });


//        if (getIntent().getBooleanExtra("own", false)) {
//            findViewById(R.id.duihuan).setVisibility(View.GONE);
//        }



        final long delayTime = TimeUtils.dataOne(map.get("end_time"));
        LogUtil.e("过期时间：：" + delayTime + "\n" + System.currentTimeMillis());
        if (delayTime < System.currentTimeMillis()) {//该券已过期

            findViewById(R.id.duihuan).setEnabled(false);
            ((TextView) findViewById(R.id.duihuan)).setText("已过期");
        }



        findViewById(R.id.duihuan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder b=new AlertDialog.Builder(QuanDetail.this);
               AlertDialog dialog= b.setMessage("确认消耗"+map.get("cost")+"云豆兑换该券吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                postDuiHuan();
                                dialog.dismiss();
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
               dialog.show();
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(QuanDetail.this,R.color.main_color));
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.DKGRAY);
                ((TextView) dialog.getDelegate().findViewById(android.R.id.message)).setTextSize(18);
            }
        });
    }

    private void postDuiHuan() {
        if(!Network.HttpTest(this)){
            return;
        }
        JSONObject js = new JSONObject();
        try {
            js.put("m_id", Constants.M_id);
            js.put("id", map.get("id"));
            js.put("user_id", PreferenceUtil.getUserId(QuanDetail.this));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e("兑换券：：" + js);
        ApisSeUtil.M m = ApisSeUtil.i(js);
        OkGo.post(Constants.Exchange).params("key", m.K())
                .params("msg", m.M())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        try {
                            JSONObject js = new JSONObject(s);
                            if (js != null) {
                                if (js.getString("code") != null) {
                                    switch (js.getString("code")) {
                                        case "000":
                                            int stock=Integer.valueOf(map.get("stock"));
                                            map.put("stock",String.valueOf(stock-1));
                                            ToastUtil.showToastShort("兑换成功");
                                            ((TextView) findViewById(R.id.cost)).setText(mApplication.ST("兑换需消耗" + map.get("cost") + "云豆，库存" + map.get("stock") + "份"));
                                            EventBus.getDefault().post(new YunDouHome.YunDouEvent());
                                            break;
                                        case "002":
                                            ToastUtil.showToastShort("您没有足够的云豆进行兑换哦");
                                            break;
                                        case "003":
                                            ToastUtil.showToastShort("抱歉，该福利券已经没有啦");
                                            break;
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
