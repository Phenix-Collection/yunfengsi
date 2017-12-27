package com.maimaizu.Activitys;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.request.BaseRequest;
import com.maimaizu.Base.BaseActivity;
import com.maimaizu.Base.KeepHouseUtil;
import com.maimaizu.Base.PersonUtil;
import com.maimaizu.Base.ScaleImageUtil;
import com.maimaizu.R;
import com.maimaizu.Utils.AnalyticalJSON;
import com.maimaizu.Utils.Constants;
import com.maimaizu.Utils.DimenUtils;
import com.maimaizu.Utils.LogUtil;
import com.maimaizu.Utils.ProgressUtil;
import com.maimaizu.Utils.ShareManager;
import com.maimaizu.Utils.ToastUtil;
import com.maimaizu.Utils.mApplication;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/5/1.
 */

public class NewHouseActivity extends BaseActivity implements View.OnClickListener {
    private Banner banner;
    private ImageView geju_image, weizhi_image;
    private TextView title;

    private TextView jiage, address, use, beginTime, huxing, kaifashang, nianxian, phone;

    private static final String TAG = "newhouse";
    private TextView guanzhu, fenxiang, zixun;

    private String id;
    private HashMap<String, String> brokerInfo;
    private UMWeb umWeb;
    private Intent mapIntent;
    @Override
    public int getLayoutId() {
        return R.layout.detail_newhouse;
    }

    @Override
    public void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
        banner = (Banner) findViewById(R.id.home_detail_banner);
        banner.setImageLoader(new ImageLoader() {
            @Override
            public void displayImage(Context context, Object path, ImageView imageView) {
                Glide.with(context).load(path).override(getResources().getDisplayMetrics().widthPixels, DimenUtils.dip2px(context, 240))
                        .into(imageView);
            }
        }).setIndicatorGravity(BannerConfig.RIGHT).setDelayTime(3000)
                .setBannerStyle(BannerConfig.NUM_INDICATOR);

        title = (TextView) findViewById(R.id.home_detail_title);

        jiage = (TextView) findViewById(R.id.jiage);
        address = (TextView) findViewById(R.id.address);
        use = (TextView) findViewById(R.id.use);
        beginTime = (TextView) findViewById(R.id.beginTime);
        huxing = (TextView) findViewById(R.id.huxing);
        kaifashang = (TextView) findViewById(R.id.kaifangshang);
        nianxian = (TextView) findViewById(R.id.nianxian);
        phone = (TextView) findViewById(R.id.phone);

        geju_image = (ImageView) findViewById(R.id.geju_image);
        weizhi_image = (ImageView) findViewById(R.id.weizhi_image);


        guanzhu = (TextView) findViewById(R.id.guanzhu);
        fenxiang = (TextView) findViewById(R.id.fenxiang);
        zixun = (TextView) findViewById(R.id.zixun);
        Drawable fenx = ActivityCompat.getDrawable(this, R.drawable.share_btn);
        fenx.setBounds(0, 0, DimenUtils.dip2px(this, 25), DimenUtils.dip2px(this, 25));
        Drawable gz = ActivityCompat.getDrawable(this, R.drawable.guanzhu_normal);
        gz.setBounds(0, 0, DimenUtils.dip2px(this, 25), DimenUtils.dip2px(this, 25));
        guanzhu.setCompoundDrawables(null, gz, null, null);
        fenxiang.setCompoundDrawables(null, fenx, null, null);
        mapIntent=new Intent(this,MapActivity.class);
    }

    @Override
    public void setOnClick() {
        guanzhu.setOnClickListener(this);
        fenxiang.setOnClickListener(this);
        zixun.setOnClickListener(this);

        weizhi_image.setOnClickListener(this);
    }

    @Override
    public boolean setEventBus() {
        return false;
    }

    @Override
    public void doThings() {
        id = getIntent().getStringExtra("id");
        if(mApplication.FangWu.contains(id)){
            guanzhu.setText("关注");
            Drawable g= ActivityCompat.getDrawable(this, R.drawable.guanzhu_pressed);
            g.setBounds(0,0, DimenUtils.dip2px(this,25),DimenUtils.dip2px(this,25));
            guanzhu.setCompoundDrawables(null,g,null,null);
            guanzhu.setTextColor(ActivityCompat.getColor(this,R.color.main_color));
        }
        getData(id);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fenxiang:
                if (umWeb != null) {
                    new ShareManager().shareWeb(umWeb, this);
                }
                break;
            case R.id.guanzhu:
                KeepHouseUtil.houseKeep(this, id, (TextView) view);
                break;
            case R.id.zixun:
                if(brokerInfo==null||brokerInfo.size()==1){
                    ToastUtil.showToastShort("很抱歉，该房源暂无经纪人\n我们会尽快安排，请耐心等待", Gravity.CENTER);

                    return;
                }
                PersonUtil.openPersonDialog(this, brokerInfo);
                break;
            case R.id.back:
                finish();
                break;
            case R.id.geju_image:
                ScaleImageUtil.openBigIagmeMode(NewHouseActivity.this, view.getTag(R.id.image_url).toString());

                break;
            case R.id.weizhi_image:// TODO: 2017/5/3 地图跳转
                if(mapIntent.getStringExtra("lng")!=null&&!mapIntent.getStringExtra("lng").equals("")){
                    startActivity(mapIntent);
                }
                break;
        }
    }

    // TODO: 2017/4/29 获取数据
    private void getData(String id) {
        OkGo.post(Constants.getHouse2Detail)
                .params("key", Constants.safeKey)
                .params("m_id", Constants.M_id)
                .params("id", id).execute(new AbsCallback<HashMap<String, String>>() {
            @Override
            public void onSuccess(final HashMap<String, String> map, Call call, Response response) {
                LogUtil.e(map + "");
                setData(map);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        brokerInfo = PersonUtil.getBrokerInfo(map.get("broker_id"));
                    }
                }).start();
            }

            @Override
            public HashMap<String, String> convertSuccess(Response response) throws Exception {
                HashMap<String, String> map = AnalyticalJSON.getHashMap(response.body().string());
                return map;
            }

            @Override
            public void onBefore(BaseRequest request) {
                super.onBefore(request);
                ProgressUtil.show(NewHouseActivity.this, "", "正在加载");
            }

            @Override
            public void onAfter(HashMap<String, String> map, Exception e) {
                super.onAfter(map, e);
                ProgressUtil.dismiss();
            }
        });
    }

    // TODO: 2017/4/29 设置数据
    private void setData(HashMap<String, String> map) {
        umWeb = new UMWeb(Constants.FX_host_Ip + TAG + "/id/" + id);
        umWeb.setTitle(map.get("title"));
        umWeb.setThumb(new UMImage(this, map.get("image")));
        umWeb.setDescription("均价:" + map.get("money") + "美元/平\n户型:" + map.get("housetype") + "\n地址:" + map.get("address"));
        title.setText(mApplication.ST(map.get("title")));

        String per = "价格: " + mApplication.ST(map.get("money").equals("") ? "--" : map.get("money"));
        jiage.setText(mApplication.ST(per));
        address.setText(mApplication.ST("地址: " + mApplication.ST(map.get("address").equals("") ? "--" : map.get("address"))));
        use.setText("用途: " + mApplication.ST(map.get("use").equals("") ? "--" : map.get("use")));
        beginTime.setText("开盘时间: " + mApplication.ST(map.get("grounding").equals("") ? "--" : map.get("grounding")));
        huxing.setText("主力户型: " + mApplication.ST(map.get("housetype").equals("") ? "--" : map.get("housetype")));
        kaifashang.setText("开发商: " + mApplication.ST(map.get("developer").equals("") ? "--" : map.get("developer")));
        nianxian.setText("产权年限: " + mApplication.ST(map.get("yeas").equals("") ? "--" : map.get("yeas")));
        phone.setText("咨询电话: " + mApplication.ST(map.get("phone").equals("") ? "--" : map.get("phone")));
        Glide.with(this).load(map.get("addressimage")).thumbnail(0.5f).centerCrop().into(weizhi_image);
        Glide.with(this).load(map.get("houseimage")).thumbnail(0.5f).centerCrop().into(geju_image);
        if (map.get("houseimage") != null && !map.get("houseimage").equals("")) {
            geju_image.setOnClickListener(this);
            geju_image.setTag(R.id.image_url, map.get("houseimage"));
        }
        mapIntent.putExtra("lng",map.get("lng"));
        mapIntent.putExtra("lat",map.get("lat"));
        final ArrayList<String> images = new ArrayList<>();
        try {
            JSONArray pics = new JSONArray(map.get("pic"));
            for (int i = 0; i < pics.length(); i++) {
                images.add(((JSONObject) pics.get(i)).getString("image"));
            }
            images.add(0,map.get("image"));
            banner.setOnBannerListener(new OnBannerListener() {
                @Override
                public void OnBannerClick(int position) {
                    ScaleImageUtil.openBigIagmeMode(NewHouseActivity.this, images, position);
                }
            });
            banner.setImages(images);
            banner.start();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
