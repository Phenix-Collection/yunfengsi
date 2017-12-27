package com.maimaizu.Activitys;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.maimaizu.Utils.NumUtils;
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

public class ZuFangActivity extends BaseActivity implements View.OnClickListener{
    private static final String TAG = "renthouse";
    private Banner banner;
    private TextView title, money, fangxing, mianji;
    private LinearLayout tags;
    private TextView jun_gua, use_floor, chao_zhuang, type_niandai, xiaoqu;
    private ImageView image;

    private TextView guanzhu, fenxiang, zixun;
    private Intent mapIntent;
    @Override
    public int getLayoutId() {
        return R.layout.detail_zufang;
    }
    private HashMap<String ,String  >  brokerInfo;
    private String id;
    private UMWeb umWeb;
    private Drawable gz;
    @Override
    public void initView() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS |     WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
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

//        red3= (SuperTextView) findViewById(R.id.redtext3);
        tags = (LinearLayout) findViewById(R.id.tags);
        jun_gua = (TextView) findViewById(R.id.per_time);
        use_floor = (TextView) findViewById(R.id.use_floor);
        chao_zhuang = (TextView) findViewById(R.id.chaoxiang_zhuangxiu);
        type_niandai = (TextView) findViewById(R.id.type_niandai);
        xiaoqu = (TextView) findViewById(R.id.xiaoqu);
        image = (ImageView) findViewById(R.id.weizhi_image);

        guanzhu = (TextView) findViewById(R.id.guanzhu);
        fenxiang = (TextView) findViewById(R.id.fenxiang);
        zixun = (TextView) findViewById(R.id.zixun);
        Drawable fenx=ActivityCompat.getDrawable(this,R.drawable.share_btn);
        fenx.setBounds(0,0,DimenUtils.dip2px(this,25),DimenUtils.dip2px(this,25));
        gz= ActivityCompat.getDrawable(this,R.drawable.guanzhu_normal);
        gz.setBounds(0,0,DimenUtils.dip2px(this,25),DimenUtils.dip2px(this,25));
        guanzhu.setCompoundDrawables(null,gz,null,null);
        fenxiang.setCompoundDrawables(null,fenx,null,null);
        mapIntent=new Intent(this,MapActivity.class);
    }

    @Override
    public void setOnClick() {
        guanzhu.setOnClickListener(this);
        fenxiang.setOnClickListener(this);
        zixun.setOnClickListener(this);
        image.setOnClickListener(this);
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
                        brokerInfo= PersonUtil.getBrokerInfo(map.get("broker_id"));
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
                ProgressUtil.show(ZuFangActivity.this, "", "正在加载");
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
        umWeb=new UMWeb(Constants.FX_host_Ip+TAG+"/id/"+id);
        umWeb.setTitle(map.get("title"));
        umWeb.setDescription(map.get("title")+"\n面积:"+map.get("area")+"平米\n"+map.get("money")+"美元/月");
        umWeb.setThumb(new UMImage(this,map.get("image")));
        title.setText(mApplication.ST(map.get("title")));

        SpannableString s1 = new SpannableString(mApplication.ST("租金\n" + NumUtils.getNumStr(map.get("money"))+"美元/月"));
        s1.setSpan(new ForegroundColorSpan(Color.parseColor("#333333")), 0, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        s1.setSpan(new AbsoluteSizeSpan(DimenUtils.dip2px(this, 16)), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ((TextView) findViewById(R.id.money)).setText(s1);
        SpannableString s2 = new SpannableString(mApplication.ST("房型\n" + map.get("housetype")));
        s2.setSpan(new ForegroundColorSpan(Color.parseColor("#333333")), 0, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        s2.setSpan(new AbsoluteSizeSpan(DimenUtils.dip2px(this, 16)), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ((TextView) findViewById(R.id.fangxing)).setText(s2);
        SpannableString s3 = new SpannableString(mApplication.ST("面积\n" + map.get("area")) + "㎡");
        s3.setSpan(new ForegroundColorSpan(Color.parseColor("#333333")), 0, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        s3.setSpan(new AbsoluteSizeSpan(DimenUtils.dip2px(this, 16)), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ((TextView) findViewById(R.id.mianji)).setText(s3);

        try {
            initTags(new JSONArray(map.get("bq")),tags);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String per = "楼层: " + mApplication.ST(map.get("floor").equals("")?"--":map.get("floor"));
        jun_gua.setText(mApplication.ST(per));
        ((TextView) findViewById(R.id.per_time2)).setText(mApplication.ST("朝向: "+mApplication.ST(map.get("point").equals("")?"--":map.get("point"))));
        use_floor.setText("装修: "+mApplication.ST(map.get("fixtrue").equals("")?"--":map.get("fixture")));
        ((TextView) findViewById(R.id.use_floor2))  .setText("供暖: "+mApplication.ST(map.get("heating").equals("")?"--":map.get("heating")));
        chao_zhuang.setText("年代: "+mApplication.ST(map.get("yeas").equals("")?"--":map.get("yeas")));
        ((TextView) findViewById(R.id.chaoxiang_zhuangxiu2)).setText("装修: "+mApplication.ST(map.get("floortype").equals("")?"--":map.get("floortype")));
        type_niandai.setText("方式: "+mApplication.ST(map.get("transtype").equals("")?"--":map.get("transtype")));
        ((TextView) findViewById(R.id.type_niandai2)).setText("现状: "+mApplication.ST(map.get("actuality").equals("")?"--":map.get("actuality")));
        xiaoqu.setText("小区: "+mApplication.ST(map.get("village")));
        Glide.with(this).load(map.get("addressimage")).thumbnail(0.5f).centerCrop().into(image);
        mapIntent.putExtra("lng",map.get("lng"));
        mapIntent.putExtra("lat",map.get("lat"));
        final ArrayList<String > images=new ArrayList<>();
        try {
            JSONArray pics=new JSONArray(map.get("pic"));
            for(int i=0;i<pics.length();i++){
                images.add(((JSONObject) pics.get(i)).getString("image"));
            }
            images.add(0,map.get("image"));
            banner.setOnBannerListener(new OnBannerListener() {
                @Override
                public void OnBannerClick(int position) {
                    ScaleImageUtil.openBigIagmeMode(ZuFangActivity.this,images,position);
                }
            });
            banner.setImages(images);
            banner.start();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private void initTags(JSONArray jsonArray, LinearLayout l) {
        for (int i = 0; i < jsonArray.length(); i++) {
            TextView textView = new TextView(this);
            textView.setBackgroundResource(R.drawable.tag_bg);
            textView.setTextColor(ContextCompat.getColor(this, R.color.main_color));
            textView.setTextSize(14);
            try {
                textView.setText(mApplication.ST(((JSONObject) jsonArray.get(i)).getString("name")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            l.addView(textView);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fenxiang:
                if(umWeb!=null){
                    new ShareManager().shareWeb(umWeb,this);
                }
                break;
            case R.id.guanzhu:
                KeepHouseUtil.houseKeep(this,id, (TextView) view);
                break;
            case R.id.zixun:
                if(brokerInfo==null||brokerInfo.size()==1){
                    ToastUtil.showToastShort("很抱歉，该房源暂无经纪人\n我们会尽快安排，请耐心等待", Gravity.CENTER);

                    return;
                }
                PersonUtil.openPersonDialog(this,brokerInfo);
                break;
            case R.id.back:
                finish();
                break;
            case R.id.weizhi_image:
                if(mapIntent.getStringExtra("lng")!=null&&!mapIntent.getStringExtra("lng").equals("")){
                    startActivity(mapIntent);
                }
                break;
        }
    }
}
