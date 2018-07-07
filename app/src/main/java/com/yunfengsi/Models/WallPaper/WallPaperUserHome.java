package com.yunfengsi.Models.WallPaper;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.yunfengsi.R;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.Network;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.SystemUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 作者：因陀罗网 on 2018/5/29 16:44
 * 公司：成都因陀罗网络科技有限公司
 */
public class WallPaperUserHome extends AppCompatActivity implements View.OnClickListener {
    private ViewPager viewPager;
    private ImageView head;
    private TextView  name;
    UserHomeFragment mine, verify, collection, Other;
    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.wall_paper_mine);
        findViewById(R.id.upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Network.HttpTest(WallPaperUserHome.this)) {
                    startActivity(new Intent(WallPaperUserHome.this, WallPaperUpload.class));
                }
            }
        });

        TabLayout tabLayout = findViewById(R.id.tab);
        viewPager = findViewById(R.id.viewpager);
        head = findViewById(R.id.head);
        name = findViewById(R.id.pet_name);
        userId = getIntent().getStringExtra("id");
        ArrayList<Fragment> fragments = new ArrayList<>();

        if (getIntent().getStringExtra("id") == null || getIntent().getStringExtra("id").equals(PreferenceUtil.getUserId(this))
                || getIntent().getBooleanExtra("mine", false)) {//个人中心
            viewPager.setOffscreenPageLimit(3);
            mine = new UserHomeFragment();
            Bundle bundle = new Bundle();
            bundle.putString("type", "1");
            mine.setArguments(bundle);
            verify = new UserHomeFragment();
            Bundle bundle1 = new Bundle();
            bundle1.putString("type", "2");
            verify.setArguments(bundle1);
            collection = new UserHomeFragment();
            Bundle bundle2 = new Bundle();
            bundle2.putString("type", "3");
            collection.setArguments(bundle2);
            Glide.with(this).load(PreferenceUtil.getUserIncetance(this).getString("head_url", ""))
                    .override(DimenUtils.dip2px(this, 80), DimenUtils.dip2px(this, 80))
                    .into(head);
            name.setText(PreferenceUtil.getUserIncetance(this).getString("pet_name", ""));
            fragments.add(mine);
            fragments.add(verify);
            fragments.add(collection);
        } else {//他人主页
            Other = new UserHomeFragment();
            Bundle b = new Bundle();
            b.putString("type", "4");
            Other.setArguments(b);
            fragments.add(Other);
            if (userId.equals("0")) {
                Glide.with(this).load(R.drawable.indra)
                        .override(DimenUtils.dip2px(this, 80), DimenUtils.dip2px(this, 80))
                        .into(head);
                name.setText("云峰禅院");
            } else {
                getUserInfo();
            }

        }


        WallPaperPageAdapter adapter = new WallPaperPageAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager, true);


    }

    private void getUserInfo() {
        JSONObject js = new JSONObject();
        try {
            js.put("user_id", userId);
            js.put("m_id", Constants.M_id);
            js.put("type", "1");
            js.put("phonename", SystemUtil.getSystemModel());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e("用户壁纸空间信息：：" + js);
        ApisSeUtil.M m = ApisSeUtil.i(js);
        OkGo.post(Constants.User_Info_Ip)
                .params("key", m.K())
                .params("msg", m.M()).execute(new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                HashMap<String, String> map = AnalyticalJSON.getHashMap(s);
                if (map != null) {
                    Glide.with(WallPaperUserHome.this).load(map.get("user_image"))
                            .override(DimenUtils.dip2px(WallPaperUserHome.this, 80), DimenUtils.dip2px(WallPaperUserHome.this, 80))
                            .into(head);
                    name.setText(map.get("pet_name"));
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        findViewById(R.id.back).setOnClickListener(this);

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    private class WallPaperPageAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> list;

        public WallPaperPageAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
            super(fm);
            this.list = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return list.size() == 1 ? "所有壁纸" : "我的壁纸";
            } else if (position == 1) {
                return "壁纸审核";
            } else if (position == 2) {
                return "我的收藏";
            }
            return super.getPageTitle(position);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 999 && verify != null) {
            viewPager.setCurrentItem(1);
            verify.onRefresh();
            LogUtil.e("刷新审核列表：：：");
        }
        if (resultCode == 222 && collection != null) {
            collection.onRefresh();
            LogUtil.e("刷新收藏列表：：：");
        }
    }
}
