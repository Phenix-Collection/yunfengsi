package com.yunfengsi.Models.NianFo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.yunfengsi.R;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.ProgressUtil;
import com.yunfengsi.Utils.ShareManager;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.mApplication;


/**
 * 作者：因陀罗网 on 2017/8/16 18:17
 * 公司：成都因陀罗网络科技有限公司
 */

public class HuiXiang extends AppCompatActivity implements View.OnClickListener {
    private static final String URL2 = Constants.host_Ip + "/" + Constants.NAME_LOW + ".php/Index/sharegx";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_huixiang);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        TextView huixiang = findViewById(R.id.huixiang);
        TextView pet_name = findViewById(R.id.pet_name);

        TextView message = findViewById(R.id.massege);

        TextView title = findViewById(R.id.title);
        huixiang.setOnClickListener(this);
        huixiang.setText(mApplication.ST("回向给众生"));
        findViewById(R.id.back).setOnClickListener(this);
        Glide.with(this).load(R.drawable.lianhua).into((ImageView) findViewById(R.id.lianhua));
        SpannableString ssj;
//        String url = URL2 + "/type/1/nf_id/" + getIntent().getStringExtra("nf_id") + "/status/" + getIntent().getIntExtra("status", 0);
        switch (getIntent().getIntExtra("status", 0)) {


            case 1:
                title.setText("念佛回向");
                pet_name.setText(mApplication.ST(PreferenceUtil.getUserIncetance(this).getString("pet_name","")));
                ssj=new SpannableString(mApplication.ST("为您念"+getIntent().getStringExtra("name")+getIntent().getStringExtra("num")+"声"));
                ssj.setSpan(new AbsoluteSizeSpan(28,true), ssj.length()-String.valueOf(getIntent().getStringExtra("num")).length()-1, ssj.length()-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssj.setSpan(new ForegroundColorSpan(Color.parseColor("#777777")), ssj.length()-String.valueOf(getIntent().getStringExtra("num")).length()-1, ssj.length()-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                message.setText(ssj);
                break;
            case 2:
                pet_name.setText(mApplication.ST(PreferenceUtil.getUserIncetance(this).getString("pet_name","")));
                title.setText("诵经回向");
                ssj=new SpannableString(mApplication.ST("为您诵"+getIntent().getStringExtra("name")+getIntent().getStringExtra("num")+"部"));
                ssj.setSpan(new AbsoluteSizeSpan(28,true), ssj.length()-String.valueOf(getIntent().getStringExtra("num")).length()-1, ssj.length()-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssj.setSpan(new ForegroundColorSpan(Color.parseColor("#777777")), ssj.length()-String.valueOf(getIntent().getStringExtra("num")).length()-1, ssj.length()-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                message.setText(ssj);
                break;
            case 3:
                pet_name.setText(mApplication.ST(PreferenceUtil.getUserIncetance(this).getString("pet_name","")));
                title.setText("持咒回向");
                ssj=new SpannableString(mApplication.ST("为您持"+getIntent().getStringExtra("name")+getIntent().getStringExtra("num")+"遍"));
                ssj.setSpan(new AbsoluteSizeSpan(28,true), ssj.length()-String.valueOf(getIntent().getStringExtra("num")).length()-1, ssj.length()-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssj.setSpan(new ForegroundColorSpan(Color.parseColor("#777777")), ssj.length()-String.valueOf(getIntent().getStringExtra("num")).length()-1, ssj.length()-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                message.setText(ssj);
                break;
            case 4:
                title.setText("助念回向");
                pet_name.setText(mApplication.ST("助念内容"));
                SpannableString ss=new SpannableString(getIntent().getStringExtra("content")+"\n\n"+getIntent().getStringExtra("pet_name")+" 请您为他助念");
                ss.setSpan(new AbsoluteSizeSpan(16,true),0,getIntent().getStringExtra("content").length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                message.setText(ss);
                break;
            case 5:
                title.setText("忏悔回向");

                pet_name.setText(mApplication.ST("忏悔内容"));
                SpannableString ss1=new SpannableString(getIntent().getStringExtra("content")+"\n\n"+getIntent().getStringExtra("pet_name")+" 请您为他随喜");
                ss1.setSpan(new AbsoluteSizeSpan(16,true),0,getIntent().getStringExtra("content").length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                message.setText(ss1);
                break;
            case 6:
                title.setText("发愿分享");
                huixiang.setText(mApplication.ST("分享发愿"));
                pet_name.setText(mApplication.ST(PreferenceUtil.getUserIncetance(HuiXiang.this).getString("pet_name","")+"请您为Ta随喜"));
                ssj=new SpannableString(mApplication.ST(PreferenceUtil.getUserIncetance(HuiXiang.this).getString("pet_name","")+"于"+getIntent().getStringExtra("time")+"发愿\n\n"+getIntent().getStringExtra("sb")+getIntent().getStringExtra("name")+getIntent().getStringExtra("num")+getIntent().getStringExtra("digit"))
                +"\n\n截止于"+getIntent().getStringExtra("target_time")+"(农历7月15日)");
//                ssj.setSpan(new AbsoluteSizeSpan(28,true), ssj.length()-String.valueOf(getIntent().getStringExtra("num")).length()-1, ssj.length()-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                ssj.setSpan(new ForegroundColorSpan(Color.parseColor("#777777")), ssj.length()-String.valueOf(getIntent().getStringExtra("num")).length()-1, ssj.length()-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                message.setText(ssj);
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ProgressUtil.dismiss();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.huixiang:
                UMWeb umWeb = new UMWeb(URL2 + "/type/0/nf_id/" + getIntent().getStringExtra("nf_id") + "/status/" + getIntent().getIntExtra("status", 0));
                switch (getIntent().getIntExtra("status", 0)) {
                    case 1:
                        umWeb.setTitle(PreferenceUtil.getUserIncetance(HuiXiang.this).getString("pet_name", "") + "为您念" + getIntent().getStringExtra("name") + getIntent().getStringExtra("num") + "声");
                        umWeb.setDescription("听说大家都在"+getResources().getString(R.string.app_name)+"共修！快来看看吧!");
                        break;
                    case 2:
                        umWeb.setTitle(PreferenceUtil.getUserIncetance(HuiXiang.this).getString("pet_name", "") + "为您诵" + getIntent().getStringExtra("name") + getIntent().getStringExtra("num") + "部");
                        umWeb.setDescription("听说大家都在"+getResources().getString(R.string.app_name)+"共修！快来看看吧!");
                        break;
                    case 3:
                        umWeb.setTitle(PreferenceUtil.getUserIncetance(HuiXiang.this).getString("pet_name", "") + "为您持" + getIntent().getStringExtra("name") + getIntent().getStringExtra("num") + "遍");
                        umWeb.setDescription("听说大家都在"+getResources().getString(R.string.app_name)+"共修！快来看看吧!");
                        break;
                    case 5:
                        umWeb.setTitle(getIntent().getStringExtra("pet_name")+"请您为他随喜");
                        umWeb.setDescription(getIntent().getStringExtra("content"));
                        break;
                    case 4:
                        umWeb.setTitle(getIntent().getStringExtra("pet_name")+"请您为他助念");
                        umWeb.setDescription(getIntent().getStringExtra("content"));
                        break;
                    case 6:
                        umWeb.setTitle(PreferenceUtil.getUserIncetance(HuiXiang.this).getString("pet_name", "")+"发愿"
                        +getIntent().getStringExtra("sb")+getIntent().getStringExtra("name")+getIntent().getStringExtra("num")
                        +getIntent().getStringExtra("digit"));
                        umWeb.setDescription("听说大家都在"+getResources().getString(R.string.app_name)+"共修！快来看看吧!");
                        break;
                }

                umWeb.setThumb(new UMImage(HuiXiang.this,R.drawable.indra_share));
                new ShareManager().shareWeb(umWeb,HuiXiang.this);
                break;
            case R.id.back:
                finish();
                break;
        }
    }
}
