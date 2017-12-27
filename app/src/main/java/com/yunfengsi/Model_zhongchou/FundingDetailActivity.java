package com.yunfengsi.Model_zhongchou;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.umeng.socialize.UMShareAPI;
import com.yunfengsi.R;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.Network;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.UpPayUtil;
import com.yunfengsi.Utils.mApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * 众筹详情
 */
public class FundingDetailActivity extends UpPayUtil implements View.OnClickListener{
    private FragmentManager manager;
    private FragmentTransaction transaction;
    private FundingDetailFragment fragment;

    /**
     * 顶部栏
     */
    private ImageView img_titlebar_back;//后退按钮
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

        //tv_launch_fund = (TextView) findViewById(R.id.tv_launch_fund);
        img_titlebar_back = (ImageView) findViewById(R.id.img_titlebar_back);
        //tv_launch_fund.setOnClickListener(this);
        img_titlebar_back.setOnClickListener(this);

        line_first_show = (LinearLayout) findViewById(R.id.line_first_show);
//        img_love = (ImageView) findViewById(R.id.img_love);
//        btn_support = (Button) findViewById(R.id.btn_support);

        line_second_show = (LinearLayout) findViewById(R.id.line_second_show);
        img_weixin = (ImageView) findViewById(R.id.img_weixin);
        img_qq = (ImageView) findViewById(R.id.img_qq);
        img_weibo = (ImageView) findViewById(R.id.img_weibo);
        shoucang = (ImageView)findViewById(R.id.fund_detail_shoucang);
         shoucang.setOnClickListener(this);
//
//        img_share = (ImageView) findViewById(R.id.img_share);

        //设置监听
//        img_share.setOnClickListener(this);
//        img_love.setOnClickListener(this);
//        img_weixin.setOnClickListener(this);
//        img_qq.setOnClickListener(this);
//        img_weibo.setOnClickListener(this);
//        btn_support.setOnClickListener(this);

        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();

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
//            case R.id.img_share://分享按钮
//                if (!line_first_show.isShown() && line_second_show.isShown()) {//1没显示,2显示
//                    line_second_show.setVisibility(View.GONE);
//                    line_first_show.setVisibility(View.VISIBLE);
//                } else {//1显示2未显示
//                    line_second_show.setVisibility(View.VISIBLE);
//                    line_first_show.setVisibility(View.GONE);
//                }
//                break;
           /* case R.id.tv_launch_fund://发起众筹
                Intent intent = new Intent(FundingDetailActivity.this, PublishedActivity.class);
                startActivity(intent);
                break;*/
//            case R.id.img_weixin://微信分享
//                break;
//            case R.id.img_qq://qq分享
//                // 启动分享GUI
//                //oks.show(this);
//                break;
//            case R.id.img_weibo://微博分享
//                break;
            case R.id.img_titlebar_back://后退按钮
                finish();
                break;
            case R.id.fund_detail_shoucang:
                v.setEnabled(false);

                if (!Network.HttpTest(mApplication.getInstance())) {
                    Toast.makeText(mApplication.getInstance(), "请检查网络连接", Toast.LENGTH_SHORT).show();
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject js=new JSONObject();
                            try {
                                js.put("user_id", PreferenceUtil.getUserIncetance(FundingDetailActivity.this).getString("user_id", ""));
                                js.put("cfg_id", getIntent().getStringExtra("id"));
                                js.put("m_id", Constants.M_id);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            String data = OkGo.post(Constants.FUNDING_DETAIL_Shoucang).params("key", Constants.safeKey)
                                    .params("key", ApisSeUtil.getKey())
                                    .params("msg",ApisSeUtil.getMsg(js)).execute().body().string();
                            if (!data.equals("")) {
                                if (AnalyticalJSON.getHashMap(data) != null && "000".equals(AnalyticalJSON.getHashMap(data).get("code"))) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(mApplication.getInstance(), "添加收藏成功", Toast.LENGTH_SHORT).show();
                                            v.setEnabled(true);
                                            ((ImageView) v).setImageResource(R.drawable.shoucang_press);

                                        }
                                    });
                                } else if (AnalyticalJSON.getHashMap(data) != null && "002".equals(AnalyticalJSON.getHashMap(data).get("code"))) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(mApplication.getInstance(), "已取消收藏", Toast.LENGTH_SHORT).show();
                                            v.setEnabled(true);
                                            ((ImageView) v).setImageResource(R.drawable.shoucang_normal);


                                        }
                                    });
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(mApplication.getInstance(), "服务器异常", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
        }
    }
}
