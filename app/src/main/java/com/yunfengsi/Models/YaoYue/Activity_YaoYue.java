package com.yunfengsi.Models.YaoYue;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.yunfengsi.R;
import com.yunfengsi.Utils.StatusBarCompat;

public class Activity_YaoYue extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.activity_yaoyue);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //发布邀约
        findViewById(R.id.fabu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Activity_YaoYue.this,YaoYue_Fabu.class);
                intent.putExtra("id",getIntent().getStringExtra("id"));
                startActivity(intent);
            }
        });




    }


}
