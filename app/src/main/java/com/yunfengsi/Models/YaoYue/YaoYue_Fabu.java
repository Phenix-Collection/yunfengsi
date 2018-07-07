package com.yunfengsi.Models.YaoYue;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.yunfengsi.R;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.Network;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.ProgressUtil;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.TimeUtils;
import com.yunfengsi.Utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 作者：因陀罗网 on 2017/6/13 15:57
 * 公司：成都因陀罗网络科技有限公司
 */

public class YaoYue_Fabu extends AppCompatActivity implements View.OnClickListener {
    private EditText edt_xuqiu;
    private EditText edt_peolle;
    private EditText edt_time;
    private EditText edt_place;
    private EditText edt_phone;
    private TextView haveCar;
    private TextView Nocar;
    private Date currentDate;
    private String money, address;
    //    private boolean canCommit = false;
    private String status = "2";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, ContextCompat.getColor(this, R.color.main_color));
        setContentView(R.layout.yue);
        ImageView back = findViewById(R.id.title_back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        ((TextView) findViewById(R.id.title_title)).setText("发布");
        initView();
//        getSetting();
    }


    private void initView() {
        EditText edt_content = findViewById(R.id.edt_content);
        EditText edt_money   = findViewById(R.id.edt_money);
        edt_peolle = findViewById(R.id.edt_people_num);
        edt_place = findViewById(R.id.edt_place);
        edt_time = findViewById(R.id.edt_time);
        edt_xuqiu = findViewById(R.id.edt_xuqiu);
        edt_phone = findViewById(R.id.edt_phone);

        haveCar = findViewById(R.id.haveCar);
        Nocar = findViewById(R.id.noCar);
        haveCar.setOnClickListener(this);
        Nocar.setOnClickListener(this);

        TextView tv_commit = findViewById(R.id.tv_commit);
        tv_commit.setOnClickListener(this);
        edt_time.setOnClickListener(this);
        edt_time.setFocusable(false);

//        edt_content.setText(PreferenceUtil.getYaoYueIncetance(this).getString("contents", ""));
//        edt_money.setText(PreferenceUtil.getYaoYueIncetance(this).getString("money", ""));
//        edt_time.setText(PreferenceUtil.getYaoYueIncetance(this).getString("end_time", ""));
//        edt_peolle.setText(PreferenceUtil.getYaoYueIncetance(this).getString("num", ""));
//        edt_xuqiu.setText(PreferenceUtil.getYaoYueIncetance(this).getString("title", ""));


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.haveCar:
                status = "2";
                haveCar.setEnabled(false);
                Nocar.setEnabled(true);
                ((TextView) findViewById(R.id.txt_people)).setText("空余座位:");
                edt_peolle.setHint("请输入空余座位数量");
                break;
            case R.id.noCar:
                status = "1";
                haveCar.setEnabled(true);
                Nocar.setEnabled(false);
                ((TextView) findViewById(R.id.txt_people)).setText("需求座位:");
                edt_peolle.setHint("请输入需求座位的数量");
                break;
            case R.id.title_back:
                finish();
                break;
            case R.id.tv_commit:
                commitYaoYue();
                break;
            case R.id.edt_time:
                final Calendar calendar1 = Calendar.getInstance();
                Calendar calendar = Calendar.getInstance();
                calendar.set(calendar1.get(Calendar.YEAR) + 1, 11, 31);
                TimePickerView pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        LogUtil.e("选择出发时间：：" + calendar.getTime().toString() + "  当前时间：：" + calendar1.getTime().toString());


                        currentDate = date;
                        edt_time.setText(TimeUtils.getYMDTime(date));


                    }
                })
                        .setType(new boolean[]{true, true, true, false, false, false})
                        .setRangDate(calendar1, calendar)
                        .setTitleText("请选择出发时间")
                        .isCenterLabel(true)
                        .setContentSize(20)
                        .setLabel("年", "月", "日", "", "", "")
                        .setLineSpacingMultiplier(1.5f)
                        .build();
                if (currentDate == null) {
                    pvTime.setDate(Calendar.getInstance());
                } else {
                    Calendar calender = Calendar.getInstance();
                    calender.setTime(currentDate);
                    pvTime.setDate(calender);
                }

                pvTime.show();
                break;
        }

    }

//    private void getSetting() {
//        if (Network.HttpTest(this)) {
//            JSONObject js = new JSONObject();
//            try {
//                js.put("m_id", Constants.M_id);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            ApisSeUtil.M m = ApisSeUtil.i(js);
//            OkGo.post(Constants.CarList)
//                    .params("key", m.K())
//                    .params("msg", m.M())
//                    .execute(new StringCallback() {
//                        @Override
//                        public void onSuccess(String s, Call call, Response response) {
//                            HashMap<String, String> map = AnalyticalJSON.getHashMap(s);
//                            if (map != null) {
//                                address = map.get("address");
//                                money = map.get("money");
//                                edt_place.setText(address);
//                                edt_money.setText(money);
//                                edt_money.setHint("金额不能小于" + money + "元");
////                                canCommit = true;
//                            } else {
//                                getSetting();
//                            }
//                        }
//
//
//                    });
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        SharedPreferences.Editor ed = PreferenceUtil.getYaoYueIncetance(this).edit();
//        ed.putString("title", edt_xuqiu.getText().toString().trim());
//        ed.putString("num", edt_peolle.getText().toString().trim());
//        ed.putString("contents", edt_content.getText().toString().trim());
////        ed.putString("money", edt_money.getText().toString().trim());
//        ed.putString("end_time", edt_time.getText().toString().trim());
////        ed.putString("address", edt_place.getText().toString().trim());
//        ed.apply();
        OkGo.getInstance().cancelTag(this);
    }

    private void commitYaoYue() {//发布邀约
        if (!Network.HttpTest(this)) {
            return;
        }
        if (edt_xuqiu.getText().toString().trim().equals("")
                        || edt_peolle.getText().toString().trim().equals("")
                        || edt_time.getText().toString().trim().equals("")
                        || edt_place.getText().toString().trim().equals("")
                        || edt_phone.getText().toString().trim().equals("")
                ) {
            ToastUtil.showToastShort("请填写所有邀约信息");
            return;
        }
//        if (edt_money.getText().toString().startsWith(".") || edt_money.getText().toString().endsWith(".") ||
//                (edt_money.getText().toString().startsWith("0") && !edt_money.getText().toString().contains("."))
//                || (edt_money.getText().toString().equals("0.0"))
//                || (edt_money.getText().toString().equals("0.00"))
//                ) {
//            ToastUtil.showToastShort("请输入正确格式的押金");
//            return;
//        }
//        if (edt_money.getText().toString().trim().contains(".")) {
//            int lastnum = edt_money.getText().toString().trim().substring(edt_money.getText().toString().trim().lastIndexOf("."))
//                    .length() - 1;
//            LogUtil.e("小数点后位数:" + lastnum);
//            if (lastnum > 2) {
//                ToastUtil.showToastShort("押金小数点不能超过两位");
//                return;
//            }
//        }
//        if (Double.valueOf(edt_money.getText().toString().trim()) < Double.valueOf(money)) {
//            ToastUtil.showToastShort("押金不能小于初始值");
//            return;
//        }
//        if (!canCommit) {
//            ToastUtil.showToastShort("数据获取失败，请稍后重试");
//            finish();
//            return;
//        }
        JSONObject js = new JSONObject();
        try {
            js.put("m_id", Constants.M_id);
            js.put("act_id", getIntent().getStringExtra("id"));
            js.put("user_id", PreferenceUtil.getUserIncetance(YaoYue_Fabu.this).getString("user_id", ""));
            js.put("title", edt_xuqiu.getText().toString().trim());
            js.put("phone", edt_phone.getText().toString().trim());
            js.put("passenger", edt_peolle.getText().toString().trim());
            js.put("status", status);
            js.put("time", edt_time.getText().toString().trim());
            js.put("address", edt_place.getText().toString().trim());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m = ApisSeUtil.i(js);
        LogUtil.e("发布邀约：：；" + js);
        OkGo.post(Constants.SubmitCar).tag(this)
                .params("key", m.K())
                .params("msg", m.M())
                .execute(new StringCallback() {


                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        ProgressUtil.show(YaoYue_Fabu.this, "", "正在提交信息");
                    }

                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        HashMap<String, String> map = AnalyticalJSON.getHashMap(s);
                        if (map != null) {
                            if ("000".equals(map.get("code"))) {
                                ToastUtil.showToastLong("发布成功,如果您已约车成功，请将您发布的信息及时删除", Gravity.CENTER);
                                Intent intent=new Intent("yaoyue");
                                sendBroadcast(intent);
                                finish();

                            } else if("003".equals(map.get("code"))){
                                ToastUtil.showToastShort("您已发布过信息了");
                            }
                        } else {
                            ToastUtil.showToastShort("信息发布失败，请稍后重试");
                        }
                    }

                    @Override
                    public void onAfter(@Nullable String s, @Nullable Exception e) {
                        super.onAfter(s, e);
                        ProgressUtil.dismiss();
                    }


                });
    }
}
