package com.yunfengsi.Setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunfengsi.R;
import com.yunfengsi.Utils.StatusBarCompat;

/**
 * 作者：luZheng on 2018/07/05 14:14
 */
public class AccountSecurity extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.account_security);
        findViewById(R.id.title_back).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.title_title)).setText("账号安全");
        findViewById(R.id.title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        findViewById(R.id.findPassword).setOnClickListener(this);
        findViewById(R.id.resetPhone).setOnClickListener(this);





    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.findPassword:
                Intent inten =new Intent(this,FindPassword.class);
                startActivity(inten);
                break;
            case R.id.resetPhone:
                Intent intent =new Intent(this,PhoneCheck.class);
                startActivity(intent);
                break;


        }
    }
}
