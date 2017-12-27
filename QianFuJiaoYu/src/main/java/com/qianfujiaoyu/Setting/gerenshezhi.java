package com.qianfujiaoyu.Setting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.qianfujiaoyu.Activitys.User_Detail;
import com.qianfujiaoyu.R;
import com.qianfujiaoyu.Utils.ACache;
import com.qianfujiaoyu.Utils.CleanMessageUtil;
import com.qianfujiaoyu.Utils.LoginUtil;
import com.qianfujiaoyu.Utils.PreferenceUtil;
import com.qianfujiaoyu.Utils.StatusBarCompat;
import com.qianfujiaoyu.Utils.mApplication;


public class gerenshezhi extends AppCompatActivity implements View.OnClickListener{
     private TextView mtvhuancz;
    private AlertDialog customDia;
    private   View viewDia;
    private ACache aCache;
    private SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerenshezhi);
        StatusBarCompat.compat(this, ContextCompat.getColor(this,R.color.main_color));
        aCache=ACache.get(this);
        sp=getSharedPreferences("user",MODE_PRIVATE);
        mtvhuancz=(TextView) findViewById(R.id.shez_huanczi_tv);
        try {
            mtvhuancz.setText(CleanMessageUtil.getTotalCacheSize(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
      resetData();


    }


    protected void resetData() {
        ((TextView) findViewById(R.id.title)).setText(mApplication.ST("设置"));
        ((TextView) findViewById(R.id.shez_geren_back)).setText(mApplication.ST("个人信息"));
        ((TextView) findViewById(R.id.shez_anquan_back)).setText(mApplication.ST("账号安全"));
        ((TextView) findViewById(R.id.st)).setText(mApplication.ST("多语言"));
        ((TextView) findViewById(R.id.qingchu)).setText(mApplication.ST("清除缓存"));
        ((TextView) findViewById(R.id.shez_jubao_back)).setText(mApplication.ST("举报建议"));
        ((TextView) findViewById(R.id.shez_guanyu_back)).setText(mApplication.ST("关于我们"));
    }

    @Override
    public void onClick(View view) {
       int id =view.getId();
        switch (id){

            case R.id.shez_back:
                finish();
                break;
            case R.id.shez_geren_back:
                if(!new LoginUtil().checkLogin(this)){
                    return;
                }
                Intent intentgr=new Intent(this,User_Detail.class);
                intentgr.putExtra("id", PreferenceUtil.getUserIncetance(this).getString("user_id",""));
                startActivity(intentgr);
                break;
            case R.id.shez_anquan_back:
                if(!new LoginUtil().checkLogin(this)){
                    return;
                }
                Intent intentaq=new Intent(this,AnquanActivity.class);
                intentaq.putExtra("user_id","");
                startActivity(intentaq);
                break;
            case R.id.shez_guanyu_back:
                if(!new LoginUtil().checkLogin(this)){
                    return;
                }
                Intent intentgy=new Intent(this,GanyuActivity.class);
                intentgy.putExtra("user_id","");
                startActivity(intentgy);
                break;
            case R.id.shez_huanc_back:
                if(mtvhuancz.getText().toString().equals("0.00M")){
                    Toast.makeText(mApplication.getInstance(),mApplication.ST("当前没有缓存数据"),Toast.LENGTH_SHORT).show();
                }else {
                    viewDia= LayoutInflater.from(this).inflate(R.layout.qingchu_dialog, null);
                    ((TextView) viewDia.findViewById(R.id.info)).setText(mApplication.ST("是否清除当前缓存"));
                    ((TextView) viewDia.findViewById(R.id.qingchu_dialog_no)).setText(mApplication.ST("下次再说"));
                    ((TextView) viewDia.findViewById(R.id.qingchu_dialog_yes)).setText(mApplication.ST("清除"));
                    customDia=new AlertDialog.Builder(this).setView(viewDia).create();
                    customDia.show();
                }
                break;
            case R.id.qingchu_dialog_no:
                customDia.dismiss();
                break;
            case R.id.qingchu_dialog_yes:
                CleanMessageUtil.clearAllCache(this);
                try {
                    mtvhuancz.setText(CleanMessageUtil.getTotalCacheSize(this));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                customDia.dismiss();
                Toast.makeText(mApplication.getInstance(),mApplication.ST("缓存已清除至"+mtvhuancz.getText().toString()),Toast.LENGTH_SHORT).show();
                break;
            case R.id.shez_jubao_back:
                if(!new LoginUtil().checkLogin(this)){
                    return;
                }
                Intent intentjb=new Intent(this,JuBaoActivity.class);
                intentjb.putExtra("user_id","");
                startActivity(intentjb);
                break;
//            case R.id.tuochu_bnt:
//                if(!new LoginUtil().checkLogin(this)){
//                    return;
//                }
//                SharedPreferences liveSet=getSharedPreferences("liveSetting",MODE_PRIVATE);
//                SharedPreferences.Editor ed1=liveSet.edit();
//                ed1.putString("last_title","");
//                ed1.putString("last_info","");
//                ed1.apply();
//                SharedPreferences.Editor ed = sp.edit();
//                ed.putString("uid", "");
//                ed.putString("user_id", "");
//                ed.putString("head_path", "");
//                ed.putString("head_url", "");
//                ed.putString("Live_state", "");
//                ed.putString("sex", "");
//                ed.putString("pet_name", "");
//                aCache.remove("head_" + sp.getString("user_id", ""));
//                ed.apply();
//                Intent intent=new Intent("Mine");
//
        }
        }
    }

