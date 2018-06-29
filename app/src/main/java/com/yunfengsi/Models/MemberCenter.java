package com.yunfengsi.Models;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.yunfengsi.R;
import com.yunfengsi.View.PieChartView;

/**
 * 作者：luZheng on 2018/06/26 09:38
 * 会员中心
 */
public class MemberCenter extends AppCompatActivity{
    private PieChartView pieChartView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_center);
//        pieChartView=findViewById(R.id.pie_chart);
        pieChartView.setColors(new int[]{Color.parseColor("#FEE40D"),Color.parseColor("#0BD2F6"),
        Color.RED, ContextCompat.getColor(this,R.color.main_color)});
//                ,
//                Color.parseColor("#FD7632")});
        pieChartView.setPostions(new float[]{0.2f,0.4f,0.67f,0.88f});
        pieChartView.setTitle("886790");
        pieChartView.setInfo("我的积分");

        pieChartView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pieChartView.setTextThenStart("8867","我的云豆");

//                pieChartView.setDrawMode(PieChartView.DrawMode.STOP);
//                pieChartView.setPostionsThenStart(new float[]{0.2f,0.4f,0.67f,0.88f});
            }
        });
    }


}
