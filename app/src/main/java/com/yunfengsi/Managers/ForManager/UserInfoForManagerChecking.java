package com.yunfengsi.Managers.ForManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.yunfengsi.Fragment.Mine_GYQD;
import com.yunfengsi.Models.Model_activity.ActivityHistory;
import com.yunfengsi.R;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.Network;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.ProgressUtil;
import com.yunfengsi.Utils.ScaleImageUtil;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.ToastUtil;
import com.yunfengsi.View.ListDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 作者：luZheng on 2018/07/04 13:49
 */
public class UserInfoForManagerChecking extends AppCompatActivity implements View.OnClickListener {
    private HashMap<String, String> map;
    private TextView                gongde;
    private ImageView personalPic, idCardPic;
    private TextView content, blackUser, isBan, refugeCard;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.signact_result_user_info);
        findViewById(R.id.title_back).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.title_title)).setText("用户信息");
        findViewById(R.id.title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        gongde = findViewById(R.id.gongde);
        gongde.setOnClickListener(this);
        TextView practiceExperience = findViewById(R.id.practiceExperience);
        practiceExperience.setOnClickListener(this);


        personalPic = findViewById(R.id.pic_personal);
        personalPic.setOnClickListener(this);
        idCardPic = findViewById(R.id.pic_idCard);
        idCardPic.setOnClickListener(this);

        content = findViewById(R.id.content);
        blackUser = findViewById(R.id.userType);
        isBan = findViewById(R.id.ban);
        refugeCard = findViewById(R.id.refugeCard);

        if(getIntent().getStringExtra("msg")!=null){
            map = AnalyticalJSON.getHashMap(getIntent().getStringExtra("msg"));
        }
        LogUtil.e("map:::;" + map);
        if (map != null) {
            //签到后直接查看该用户资料
            setData();
        }
        
        if(getIntent().getStringExtra("user_id")!=null){
            //用户管理查看用户详细资料
            getUserInfo();
        }
    }

    private void getUserInfo() {
        JSONObject js = new JSONObject();
        try {
            js.put("admin_id", PreferenceUtil.getUserId(this));
            js.put("m_id", Constants.M_id);
            js.put("user_id", getIntent().getStringExtra("user_id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m = ApisSeUtil.i(js);
        LogUtil.e("管理员查看用户资料：：" + js);
        OkGo.post(Constants.UserInfoForManagerChecking)
                .params("key", m.K())
                .params("msg", m.M())
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        HashMap<String,String > m=AnalyticalJSON.getHashMap(s);
                        if(m!=null){
                            map = AnalyticalJSON.getHashMap(m.get("msg"));
                            if(map!=null){
                                setData();
                            }else{
                                ToastUtil.showToastShort("数据加载失败，请稍后重试");
                            }
                        }else{
                            ToastUtil.showToastShort("数据加载失败，请稍后重试");
                        }

                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        ProgressUtil.show(UserInfoForManagerChecking.this,"","请稍等");
                    }

                    @Override
                    public void onAfter(String s, Exception e) {
                        super.onAfter(s, e);
                        ProgressUtil.dismiss();
                    }
                });

    }

    @Override
    protected void onPause() {
        super.onPause();
        OkGo.getInstance().cancelTag(this);
    }

    private void setData() {
        gongde.setText("功德值 " + Double.valueOf(map.get("gongde")).intValue() + " →");//功德值
        Glide.with(this)
                .load(map.get("userimage"))
                .asBitmap()
                .skipMemoryCache(true)
                .fitCenter()
                .into(new BitmapImageViewTarget(personalPic) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        LogUtil.e("个人照宽高：：：" + resource.getWidth() + "   " + resource.getHeight());
                        personalPic.setImageBitmap(resource);
                    }
                });
//            Glide.with(this)
//                    .load(map.get("cidimage"))
//                    .asBitmap()
//                    .skipMemoryCache(true)
//                    .fitCenter()
//                    .into(new BitmapImageViewTarget(idCardPic) {
//                        @Override
//                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                            LogUtil.e("身份证宽高：：：" + resource.getWidth() + "   " + resource.getHeight());
//                            idCardPic.setImageBitmap(resource);
//                        }
//                    });
        Glide.with(this).load(map.get("cidimage"))
                .skipMemoryCache(true).fitCenter().into(idCardPic);

        StringBuilder ctent = new StringBuilder();
        ctent.append("账号(手机号码):   ").append(map.get("phone")).append("\n")
                .append("昵称:   ").append(map.get("pet_name")).append("\n")
                .append("性别:   ").append(map.get("sex").equals("1") ? "男" : "女").append("\n")
                .append("姓名:   ").append(map.get("name")).append("\n")
                .append("法名:   ").append(map.get("farmington")).append("\n")
                .append("身份证号:   ").append(map.get("cid")).append("\n")
                .append("家庭住址:   ").append(map.get("address")).append("\n")
                .append("职业:   ").append(map.get("work")).append("\n")
                .append("工作单位:    ").append(map.get("workunit")).append("\n")
                .append("紧急联系人:    ").append(map.get("contact")).append("\n")
                .append("车牌号:    ").append(map.get("plate")).append("\n")
                .append("注册时间:   ").append(map.get("creat_time"));

        content.setText(ctent);
        SpannableString userType = null;
        switch (map.get("blackuser")) {
            //白名单和正常用户   绿色
            case "1":
                userType = new SpannableString("用户类型:   普通用户");
                userType.setSpan(new ForegroundColorSpan(Color.GREEN), userType.length() - 4, userType.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            case "3":
                userType = new SpannableString("用户类型:   白名单");
                userType.setSpan(new ForegroundColorSpan(Color.GREEN), userType.length() - 3, userType.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                break;
            case "2":
                //黑名单  红色
                userType = new SpannableString("用户类型:   黑名单");
                userType.setSpan(new ForegroundColorSpan(Color.RED), userType.length() - 3, userType.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
        }
        blackUser.setText(userType);

        SpannableString ban = null;
        switch (map.get("status")) {
            //正常   绿色
            case "1":
                ban = new SpannableString("是否禁言:   正常");
                ban.setSpan(new ForegroundColorSpan(Color.GREEN), ban.length() - 2, ban.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            case "2":
                //禁言  红色
                ban = new SpannableString("是否禁言:   禁言");
                ban.setSpan(new ForegroundColorSpan(Color.RED), ban.length() - 2, ban.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
        }

        isBan.setText(ban);

        if (!TextUtils.isEmpty(map.get("conversion"))) {
            setConversion(map.get("conversion"));
        } else {
            refugeCard.setOnClickListener(this);
        }
    }

    private void setConversion(String conversion) {
        SpannableString refuge = new SpannableString("皈依卡号:   " + conversion);
        refuge.setSpan(new ForegroundColorSpan(Color.BLACK), refuge.length() - conversion.length(), refuge.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        refugeCard.setText(refuge);
    }

    @Override
    public void onClick(View v) {
       switch (v.getId()) {

           case R.id.gongde:
               Intent intent=new Intent();
               intent.setClass(this, Mine_GYQD.class);
               intent.putExtra("user_id",map.get("id"));
               startActivity(intent);
               break;
           case R.id.practiceExperience:
               Intent intent2=new Intent();
               intent2.setClass(this,ActivityHistory.class);
               intent2.putExtra("user_id",map.get("id"));
               startActivity(intent2);
               break;
           case R.id.pic_personal:
               if(map!=null){
                   ScaleImageUtil.openBigIagmeMode(this,map.get("userimage"),true);
               }
               break;
           case R.id.pic_idCard:
               if(map!=null){
                   ScaleImageUtil.openBigIagmeMode(this,map.get("cidimage"),false);
               }
               break;
           case R.id.refugeCard://绑定皈依卡号
               ListDialog.create(this)
                       .setView(R.layout.dialog_title_msg_edt_cancel_commit)
                       .showSoftKeyboard(R.id.edit)
                       .setCancelViewId(R.id.cancel)
                       .setVisible(R.id.msg,false)
                       .setGravity(Gravity.CENTER)
                       .setText(R.id.title,"请输入皈依卡号")
                       .setText(R.id.commit,"确认绑定")
                       .setText(R.id.cancel,"取消")
                       .setCommitId(R.id.commit, new ListDialog.DialogCallBack() {
                           @Override
                           public void onCancelClick(AlertDialog dialog) {

                           }

                           @Override
                           public void onCommit(final AlertDialog dialog, View view) {
                               final String content = ((EditText) view.findViewById(R.id.edit)).getText().toString().trim();
                               if(TextUtils.isEmpty(content)){
                                   ToastUtil.showToastShort("请输入皈依卡号");
                                   return;
                               }
                               if(!Network.HttpTest(UserInfoForManagerChecking.this)){
                                   return;
                               }
                               JSONObject jsonObject=new JSONObject();
                               try {
                                   jsonObject.put("m_id", Constants.M_id);
                                   jsonObject.put("conversion",content);
                                   jsonObject.put("user_id", map.get("id"));
                                   jsonObject.put("admin_id", PreferenceUtil.getUserId(UserInfoForManagerChecking.this));
                               } catch (JSONException e) {
                                   e.printStackTrace();
                               }
                               ApisSeUtil.M m=ApisSeUtil.i(jsonObject);
                               LogUtil.e("绑定皈依卡号：：：："+jsonObject);
                               OkGo.post(Constants.BindRefugeCard)
                                       .params("key",m.K())
                                       .params("msg",m.M())
                                       .execute(new StringCallback() {
                                           @Override
                                           public void onSuccess(String s, Call call, Response response) {
                                               HashMap<String,String > map=AnalyticalJSON.getHashMap(s);
                                               if(map!=null){
                                                   switch (map.get("code")) {

                                                       case "000":
                                                           ToastUtil.showToastShort("绑定成功");
                                                           setConversion(content);
                                                           break;
                                                       case "003":
                                                           ToastUtil.showToastShort("该皈依卡已被绑定");
                                                           break;

                                                   }
                                               }else{
                                                   ToastUtil.showToastShort("绑定失败，请检查网络后重试");
                                               }
                                           }

                                           @Override
                                           public void onBefore(BaseRequest request) {
                                               super.onBefore(request);
                                               ProgressUtil.show(UserInfoForManagerChecking.this,"","正在绑定...");
                                           }

                                           @Override
                                           public void onAfter(String s, Exception e) {
                                               super.onAfter(s, e);
                                               ProgressUtil.dismiss();
                                               dialog.dismiss();
                                           }
                                       });
                           }
                       }).show();
               break;
       }
    }
}
