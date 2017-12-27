package com.maimaizu.Activitys;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.PermissionChecker;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.callback.StringCallback;
import com.maimaizu.Fragments.HomeFragment;
import com.maimaizu.Fragments.TuiJianFragment;
import com.maimaizu.Fragments.ZiXun;
import com.maimaizu.Mine.Login;
import com.maimaizu.Mine.Mine;
import com.maimaizu.R;
import com.maimaizu.Utils.AnalyticalJSON;
import com.maimaizu.Utils.Constants;
import com.maimaizu.Utils.LogUtil;
import com.maimaizu.Utils.PreferenceUtil;
import com.maimaizu.Utils.ProgressUtil;
import com.maimaizu.Utils.StatusBarCompat;
import com.maimaizu.Utils.ToastUtil;
import com.maimaizu.Utils.mApplication;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/4/24.
 */

public class HomeActivity extends AppCompatActivity implements View.OnClickListener ,OnMapReadyCallback{
    private ViewPager pager;
    private TabLayout tabLayout;
    private mHomePageAdapter adapter;
    private ArrayList<Fragment>list;
    private boolean needExit=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.activity_main);
        getGuanZhuAndZixun(new Login.LoginNeedLoadModel());
        initView();

    }

    // TODO: 2017/5/3 获取资讯收藏和房源关注
    @Subscribe (threadMode = ThreadMode.MAIN)
    public void getGuanZhuAndZixun(Login.LoginNeedLoadModel l){
        l=null;
        OkGo.post(Constants.news_sc_list_Ip).tag(this).params("user_id", PreferenceUtil.getUserIncetance(this).getString("user_id", "")).params("key", Constants.safeKey)
                .execute(new AbsCallback<ArrayList<String>>() {
                    @Override
                    public ArrayList<String> convertSuccess(Response response) throws Exception {
                        ArrayList<HashMap<String,String>> list=AnalyticalJSON.getList(response.body().string(), "news");
                        for(HashMap<String,String> map:list){
                            String id=map.get("id");
                            if(!mApplication.ZiXun.contains(id)){
                                mApplication.ZiXun.add(id);
                            }

                        }
                        return mApplication.ZiXun;
                    }

                    @Override
                    public void onSuccess(ArrayList<String> arrayList, Call call, Response response) {
                        LogUtil.w("收藏列表::;;"+mApplication.ZiXun);
                    }


                });
        OkGo.post(Constants.getHouseKeeps).tag(this).params("key", Constants.safeKey).params("user_id", PreferenceUtil.getUserIncetance(this).getString("user_id", ""))
                .params("m_id",Constants.M_id)
                .execute(new AbsCallback<ArrayList<String>>() {
                    @Override
                    public ArrayList<String> convertSuccess(Response response) throws Exception {
                        if (response != null) {
                            String data = response.body().string();
                            JSONArray jsonArray = new JSONArray(data);
                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject js= (JSONObject) jsonArray.get(i);
                                if(!mApplication.FangWu.contains(js.getString("id"))){
                                    mApplication.FangWu.add(js.getString("id"));
                                }
                            }
                            return mApplication.FangWu;
                        }
                        return null;
                    }

                    @Override
                    public void onSuccess(ArrayList<String> l, Call call, Response response) {
                        LogUtil.w("关注列表::;;"+mApplication.FangWu);

                    }


                });



    }
    // TODO: 2017/4/27 获取城市标签
    private void getCitys() {
        OkGo.post(Constants.getCitys).params("key",Constants.safeKey)
                .params("m_id",Constants.M_id).execute(new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
//                EventBus.getDefault().post(s);
                mApplication.citys=s;
            }
        });

//        String s="天津";
//        ToastUtil.showToastShort(this,"词组解析:"+ CharacterParser.getInstance().getSelling(s));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mApplication.citys.equals("")){
            getCitys();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(this);
        if(ToastUtil.toast!=null){
            ToastUtil.toast.cancel();
            ToastUtil.toast=null;
        }
        EventBus.getDefault().unregister(this);
    }

    /*
                    初始化
                 */
    private void initView() {
        EventBus.getDefault().register(this);
        pager = (ViewPager) findViewById(R.id.home_viewpager);
        tabLayout = (TabLayout) findViewById(R.id.home_bottom_tablayout);
        HomeFragment home=new HomeFragment();
        TuiJianFragment tuijian=new TuiJianFragment();
        ZiXun zixun=new ZiXun();
        Mine mine=new Mine();
        list=new ArrayList<>();
        list.add(home);
        list.add(tuijian);
        list.add(zixun);
        list.add(mine);
        adapter=new mHomePageAdapter(getSupportFragmentManager(),list);
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(list.size());
        tabLayout.setupWithViewPager(pager);
        //设置自定义tab
        for (int i = 0; i < list.size(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null)
                tab.setCustomView(adapter.getTabView(i));
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ((TextView) tab.getCustomView().findViewById(R.id.home_tab_text)).setTextColor(getResources().getColor(R.color.main_color));
                pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                ((TextView) tab.getCustomView().findViewById(R.id.home_tab_text)).setTextColor(getResources().getColor(R.color.gray));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        pager.setCurrentItem(0);
        tabLayout.getTabAt(0).getCustomView().setSelected(true);
        ((TextView) tabLayout.getTabAt(0).getCustomView().findViewById(R.id.home_tab_text)).setTextColor(getResources().getColor(R.color.main_color));
//
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || PermissionChecker.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE}, 0);
                return;
            }
        }
        LatLng sydney = new LatLng(-33.867, 151.206);

        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));

        map.addMarker(new MarkerOptions()
                .title("Sydney")
                .snippet("The most populous city in Australia.")
                .position(sydney));
    }

    // TODO: 2017/4/25 首页 页面适配器
    public  static class mHomePageAdapter extends FragmentPagerAdapter {
        private int images[]={R.drawable.selector_shouye,R.drawable.selector_tuijian,
                R.drawable.selector_zixun,R.drawable.selector_mine};
        private ArrayList<Fragment> list;
        private String titls[]=new String[]{"首页","推荐","资讯","我的"};
        public mHomePageAdapter(FragmentManager fm, ArrayList<Fragment> list) {
            super(fm);
            this.list = list;
        }

        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mApplication.ST(titls[position]);
        }

        public   View getTabView(int position){
            View view = LayoutInflater.from(mApplication.getInstance()).inflate(R.layout.home_tab_customview, null);
            ImageView img = (ImageView) view.findViewById(R.id.home_tab_image);
            TextView text = (TextView) view.findViewById(R.id.home_tab_text);
            text.setText(getPageTitle(position));
            img.setImageResource(images[position]);
            return view;
        }

    }

    public static String sHA1(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i])
                        .toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
                hexString.append(":");
            }
            String result = hexString.toString();
            return result.substring(0, result.length() - 1);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onStop() {
        super.onStop();
        JCVideoPlayer.releaseAllVideos();
        ProgressUtil.dismiss();
    }
    @Override
    public void onBackPressed() {
        if (needExit) {
            finish();
            return ;
        }
        Toast.makeText(this, mApplication.ST("再按一次退出应用"), Toast.LENGTH_SHORT).show();
        needExit = true;
        tabLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                needExit = false;
            }
        }, 2000);
    }
}
