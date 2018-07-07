package com.yunfengsi.Models.Model_zhongchou;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.sdk.android.push.AndroidPopupActivity;
import com.umeng.socialize.UMShareAPI;
import com.yunfengsi.Managers.CollectManager;
import com.yunfengsi.R;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.LoginUtil;
import com.yunfengsi.Utils.Network;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.mApplication;

import java.util.Map;

/**
 * 众筹详情
 */
public class FundingDetailActivity extends AndroidPopupActivity implements View.OnClickListener{
    private FundingDetailFragment           fragment;

    private ImageView shoucang;
    /**
     * 底部栏
     */
    private LinearLayout line_first_show;//第一个要显示的
//    private ImageView img_love;
    private Button btn_support;//支持

    private LinearLayout line_second_show;//第二个要显示的
    private ImageView img_weixin;//微信分享
    private ImageView img_qq;//qq分享
    private ImageView img_weibo;//微博分享

//    private ImageView img_share;//分享按钮

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.activity_funding_detail);

        initView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UMShareAPI.get(this).release();
//        ShareManager.release();
    }

    private void initView() {


        ImageView img_titlebar_back = findViewById(R.id.title_back);
        img_titlebar_back.setVisibility(View.VISIBLE);
        //tv_launch_fund.setOnClickListener(this);
        img_titlebar_back.setOnClickListener(this);



        shoucang = findViewById(R.id.title_image2);
        shoucang.setVisibility(View.VISIBLE);
        shoucang.setImageResource(R.drawable.shoucang_selector);
         shoucang.setOnClickListener(this);

        android.app.FragmentManager     manager     = getFragmentManager();
        android.app.FragmentTransaction transaction = manager.beginTransaction();

        fragment = new FundingDetailFragment();
        transaction.replace(R.id.content, fragment);
        transaction.commit();

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {

            case R.id.title_back://后退按钮
                finish();
                break;
            case R.id.title_image2:
                if(!new LoginUtil().checkLogin(this)){
                    return;
                }
                if (!Network.HttpTest(mApplication.getInstance())) {
                    return;
                }
                CollectManager.doCollect(this,getIntent().getStringExtra("id"),"4",shoucang);

                break;
        }
    }

    @Override
    protected void onSysNoticeOpened(String s, String s1, Map<String, String> map) {
        fragment.fundId=AnalyticalJSON.getHashMap(map.get("msg")).get("id");
        if (!TextUtils.isEmpty( fragment.fundId)) {
            fragment.getFundingDetail(fragment.fundId);
            fragment.getFundingComments(fragment.fundId);
        }

    }
}
