package com.yunfengsi.Setting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yunfengsi.Managers.AboutPay.MyShouHuoAddress;
import com.yunfengsi.Managers.Base.BaseSTActivity;
import com.yunfengsi.R;
import com.yunfengsi.Utils.ACache;
import com.yunfengsi.Utils.CleanMessageUtil;
import com.yunfengsi.Utils.LoginUtil;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.mApplication;


public class Setting extends BaseSTActivity implements View.OnClickListener{
     private TextView mtvhuancz;
    private AlertDialog customDia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerenshezhi);
        mApplication.getInstance().addActivity(this);
        StatusBarCompat.compat(this, ContextCompat.getColor(this,R.color.main_color));
        ACache            aCache = ACache.get(this);
        SharedPreferences sp     = getSharedPreferences("user", MODE_PRIVATE);
        findViewById(R.id.title_back).setVisibility(View.VISIBLE);
        findViewById(R.id.title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mtvhuancz= findViewById(R.id.shez_huanczi_tv);
        try {
            mtvhuancz.setText(CleanMessageUtil.getTotalCacheSize(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
      resetData();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mApplication.getInstance().romoveActivity(this);
    }

    @Override
    protected void resetData() {
        ((TextView) findViewById(R.id.title_title)).setText(mApplication.ST("設置"));
        ((TextView) findViewById(R.id.shez_geren_back)).setText(mApplication.ST("個人信息"));
        ((TextView) findViewById(R.id.shez_anquan_back)).setText(mApplication.ST("信息修改"));
        ((TextView) findViewById(R.id.findPassword)).setText(mApplication.ST("账号安全"));
        ((TextView) findViewById(R.id.st)).setText(mApplication.ST("多語言"));
        ((TextView) findViewById(R.id.bangzhu)).setText(mApplication.ST("帮助"));
        ((TextView) findViewById(R.id.qingchu)).setText(mApplication.ST("清除緩存"));
        ((TextView) findViewById(R.id.shez_jubao_back)).setText(mApplication.ST("建议反馈"));
        ((TextView) findViewById(R.id.shez_guanyu_back)).setText(mApplication.ST("关于我们"));
        ((TextView) findViewById(R.id.address)).setText(mApplication.ST("收货地址"));
    }

    @Override
    public void onClick(View view) {
       int id =view.getId();
        switch (id){
            case R.id.address:
                Intent address=new Intent(this, MyShouHuoAddress.class);
                startActivityForResult(address, 0);
                break;
            case R.id.resetPhone:
                Intent phone=new Intent(this, PhoneCheck.class);
                startActivity(phone);
                break;
            case R.id.bangzhu:
                Intent intent1=new Intent(this, AD.class);
                intent1.putExtra("bangzhu",true);
                startActivity(intent1);
                break;
            case R.id.st:
                Intent intent=new Intent(this,ST.class);
                startActivity(intent);
                break;
            case R.id.title_back:
                finish();
                break;
            case R.id.findPassword:
                Intent inten =new Intent(this,AccountSecurity.class);
                startActivity(inten);
                break;
            case R.id.shez_geren_back:
                if(!new LoginUtil().checkLogin(this)){
                    return;
                }
                Intent intentgr=new Intent(this,GerenxinxiActivity.class);
                intentgr.putExtra("user_id","");
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
                    View viewDia = LayoutInflater.from(this).inflate(R.layout.qingchu_dialog, null);
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

