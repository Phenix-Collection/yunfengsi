package com.yunfengsi.Models.Auction;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;

import com.yunfengsi.R;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.ToastUtil;

/**
 * 作者：luZheng on 2018/06/08 17:11
 */
public class AuctionDetail extends AppCompatActivity {
    float downY,oldY;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.auction_detail);

        ((NestedScrollView) findViewById(R.id.front)).setFillViewport(true);
        ((NestedScrollView) findViewById(R.id.front)).post(new Runnable() {
            @Override
            public void run() {
                ToastUtil.showToastShort( ((NestedScrollView) findViewById(R.id.front)).getChildAt(0).getHeight()+"");
               LogUtil.e( ((NestedScrollView) findViewById(R.id.front)).getHeight()+"");
            }
        });

    }
}
