package com.yunfengsi.Models.NianFo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.sdk.android.push.AndroidPopupActivity;
import com.bumptech.glide.Glide;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.yunfengsi.R;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.LoginUtil;
import com.yunfengsi.Utils.MD5Utls;
import com.yunfengsi.Utils.ProgressUtil;
import com.yunfengsi.Utils.ShareManager;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.mApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NianFo extends AndroidPopupActivity implements View.OnClickListener {
    private ListView mgridview;
//    private ViewPager viewPager;//轮播
//    private LinearLayout PointLayou;//轮播图圆点layout
    private ArrayList<String> imageList;
    private int screeWidth, dp10, dp180, dp7;
    private PopupWindow pp;//加号弹出窗口
    private ImageView search;
    private ShareAction action;

    private String jishuSupprot = "";
    private String share = "";
    private String appUrl="";
    private static final String TAG = "NianFo";
    private SharedPreferences sp;
//    private String SMS="";

//    private Banner banner;
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            jishuSupprot = intent.getStringExtra("j");
            share = intent.getStringExtra("s");
            appUrl=intent.getStringExtra("a");
        }
    };
    private UMWeb umWeb;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.nianfo);
        mApplication.getInstance().addActivity(this);
//        SMS=getIntent().getStringExtra("sms");
//        ((TextView) findViewById(R.id.title)).setText(mApplication.ST("共修"));
        //轮播
//        viewPager = (ViewPager)findViewById(R.id.activity_detail_viewPager);
//        banner= (Banner) findViewById(R.id.banner);
//        banner.setDelayTime(3000);
//        banner.setImageLoader(new ImageLoader() {
//            @Override
//            public void displayImage(Context context, Object path, ImageView imageView) {
//                Glide.with(NianFo.this)
//                        .load(path).override(getResources().getDisplayMetrics().widthPixels,getResources().getDisplayMetrics().widthPixels*73/200)
//                        .into(imageView);
//            }
//        });
//        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
//        banner.setOnBannerListener(new OnBannerListener() {
//            @Override
//            public void OnBannerClick(int position) {
//                if(imageList!=null){
//                    ScaleImageUtil.openBigIagmeMode(NianFo.this,imageList,position);
//                }
//            }
//        });

        //轮播图圆点layout
//        PointLayou = (LinearLayout)findViewById(R.id.activity_detail_circlePoint_layout);
        //图片地址数组
        sp=getSharedPreferences("user",Context.MODE_PRIVATE);
        imageList = new ArrayList<>();
        mgridview = (ListView) findViewById(R.id.nianfo_home_gridview);
        dp10 = DimenUtils.dip2px(this, 10);
        dp180 = DimenUtils.dip2px(this, 150);
        dp7 = DimenUtils.dip2px(this, 7);
        screeWidth = getResources().getDisplayMetrics().widthPixels;
        findViewById(R.id.Share).setOnClickListener(this);
//        add = (ImageView)findViewById(R.id.main_more);
//        add.setOnClickListener(this);
        search = (ImageView)findViewById(R.id.main_search);
        search.setOnClickListener(this);
        ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();
        //为每个格子添加内容

        HashMap<String, Object> map = new HashMap<String, Object>();// 建立hashmap对象

        map.put("ItemImage", R.drawable.nianfo);
        map.put("title", "  念佛");

        HashMap<String, Object> map1 = new HashMap<String, Object>();// 建立hashmap对象
        map1.put("ItemImage", R.drawable.songjin);
        map1.put("title", "  诵经");
        HashMap<String, Object> map2 = new HashMap<String, Object>();// 建立hashmap对象
        map2.put("ItemImage", R.drawable.chizhou);
        map2.put("title", "  持咒");
        HashMap<String, Object> map5 = new HashMap<String, Object>();// 建立hashmap对象
        map5.put("ItemImage", R.drawable.fayuan);
        map5.put("title", "请发愿");
        HashMap<String, Object> map3 = new HashMap<String, Object>();// 建立hashmap对象
        map3.put("ItemImage", R.drawable.zhunian);
        map3.put("title", "  助念");
        HashMap<String, Object> map4 = new HashMap<String, Object>();// 建立hashmap对象
        map4.put("ItemImage", R.drawable.chanhui);
        map4.put("title", "  忏悔");

        lstImageItem.add(map5);
        lstImageItem.add(map);
        lstImageItem.add(map1);
        lstImageItem.add(map2);

        lstImageItem.add(map3);
        lstImageItem.add(map4);

        /**
         * 为GridView建立SimpleAdapter适配器
         */
        // SimpleAdapter()中的五个参数分别是：第一个context，第二个数据资源，第三个每一个子项的布局文件，第四个每一个子项中的Key数组
        // 第五个每一个子项中的Value数组
        mAdapter ad = new mAdapter(lstImageItem,this);
        mgridview.setAdapter(ad);// 添加适配器
        mgridview.setOnItemClickListener(new ItemClickListener());// 为每一个子项设置监听
        IntentFilter intentFilter = new IntentFilter("zixun");
        registerReceiver(receiver, intentFilter);

//        getBanner();
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        mApplication.getInstance().romoveActivity(this);
    }

    @Override
    protected void onSysNoticeOpened(String s, String s1, Map<String, String> map) {

    }

    static class mAdapter extends BaseAdapter {
        private ArrayList<HashMap<String, Object>> l;
        private Context context;
        private int screenWidthl;

        public mAdapter(ArrayList<HashMap<String, Object>> item, Context context) {
            super();
            l = item;
            this.context = context;
            screenWidthl = context.getResources().getDisplayMetrics().widthPixels;
        }

        @Override
        public int getCount() {
            return l == null ? 0 : l.size();
        }

        @Override
        public Object getItem(int position) {
            return l.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int hei = parent.getHeight() / 6;
            View view = LayoutInflater.from(context).inflate(R.layout.grid_itme, parent, false);
            ViewGroup.LayoutParams vl = view.getLayoutParams();
            vl.height = hei;
            view.setLayoutParams(vl);
            ImageView i = (ImageView) view.findViewById(R.id.imageview);
            Glide.with(context).load((Integer) l.get(position).get("ItemImage")).override(
                    hei - DimenUtils.dip2px(context, 20), hei - DimenUtils.dip2px(context, 20))
                    .into(i);
            ;
            ((TextView) view.findViewById(R.id.title)).setText(String.valueOf(l.get(position).get("title")));

            return view;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ProgressUtil.dismiss();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.Share:
                UMWeb umWeb=new UMWeb("http://a.app.qq.com/o/simple.jsp?pkgname=com.yunfengsi");
                umWeb.setTitle("云峰寺App");
                umWeb.setDescription("快来云峰寺共修吧");
                umWeb.setThumb(new UMImage(this,R.drawable.indra_share));
                new ShareManager().shareWeb(umWeb,this);
                break;
            case R.id.main_search:
                finish();

//                Intent intent = new Intent(mApplication.getInstance(), Search.class);
//                startActivity(intent);
                break;
//            case R.id.main_more:
//                View view1 = LayoutInflater.from(mApplication.getInstance()).inflate(R.layout.home_add_layout, null);
//                if (pp == null) {
//                    pp = new PopupWindow(view1);
//                    pp.setFocusable(true);
//                    pp.setOutsideTouchable(true);
//                    pp.setTouchable(true);
//                    ColorDrawable c = new ColorDrawable(getResources().getColor(R.color.main_color));
//                    pp.setBackgroundDrawable(c);
//                    pp.setWidth(DimenUtils.dip2px(mApplication.getInstance(), 150));
//                    pp.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
//                    pp.showAsDropDown(add, -50, 0);
//                } else {
//                    if (pp.isShowing()) pp.dismiss();
//                    pp.showAsDropDown(add, -50, 0);
//                }
//                initPopupWindow(view1);
//                break;
        }
    }

    class ItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if (new LoginUtil().checkLogin(NianFo.this)) {
                Log.d("选中的Itme:", i + "");
                Intent intent = new Intent();
                if (i == 1) {
                    intent.setClass(mApplication.getInstance(), nianfo_home_tab1.class);
                } else if (i == 2) {
                    intent.setClass(mApplication.getInstance(), nianfo_home_tab2.class);
                } else if (i == 3) {
                    intent.setClass(mApplication.getInstance(), nianfo_home_tab3.class);
                } else if (i == 0) {
                    intent.setClass(mApplication.getInstance(), nianfo_home_tab6.class);
                } else if (i == 4) {
                    intent.setClass(mApplication.getInstance(), nianfo_home_tab4.class);
                } else if (i == 5) {
                    intent.setClass(mApplication.getInstance(), nianfo_home_tab5.class);
                }
                startActivity(intent);
            }

        }
    }
//    // TODO: 2017/5/2 获取轮播图
//    private void getBanner() {
//        JSONObject js=new JSONObject();
//        try {
//            js.put("m_id","1000");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        ApisSeUtil.M m= ApisSeUtil.i(js);
//        OkGo.post(Constants.getBanner)
//                .params("key", m.K())
//                .params("msg", m.M())
//                .execute(new AbsCallback<ArrayList<HashMap<String, String>>>() {
//                    @Override
//                    public void onSuccess(ArrayList<HashMap<String, String>> list, Call call, Response response) {
//
//                        if (list != null) {
//                            for (HashMap<String, String> map : list) {
//                                if(!imageList.contains(map.get("image"))){
//                                    imageList.add(map.get("image"));
//                                }
//                            }
////                           setUrlToImage();
//                        } else {
//                            getBanner();
//                        }
//                    }
//
//                    @Override
//                    public ArrayList<HashMap<String, String>> convertSuccess(Response response) throws Exception {
//                        return AnalyticalJSON.getList_zj(response.body().string());
//                    }
//                });
//    }

//    /**
//     * 加载轮播图
//     */
//    private void setUrlToImage() {
//
//        banner.setImages(imageList);
//        banner.start();
//    }



    private void initPopupWindow(View v) {
        LinearLayout layout1 = (LinearLayout) v.findViewById(R.id.layout1);
        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri content_url;
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                if (sp.getString("user_id", "").equals("")) {
                    content_url = Uri.parse(Constants.host_Ip + "/" + Constants.NAME_LOW + ".php");
                    intent.setData(content_url);
                    startActivity(intent);
                    pp.dismiss();
                } else {
                    JSONObject js = new JSONObject();
                    try {
                        js.put("id", sp.getString("user_id", "") + MD5Utls.stringToMD5(MD5Utls.stringToMD5(Constants.M_id)));
                        content_url = Uri.parse(Constants.host_Ip + "/" + Constants.NAME_LOW + ".php/Index/index/login/" + android.util.Base64.encodeToString(js.toString().getBytes(), android.util.Base64.DEFAULT));
                        Log.w(TAG, "onClick: 加密地址" + content_url);
                        intent.setData(content_url);
                        startActivity(intent);
                        pp.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        });
        LinearLayout layout2 = (LinearLayout) v.findViewById(R.id.layout2);
        layout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createShortCut();
                pp.dismiss();
            }
        });
        LinearLayout layout3 = (LinearLayout) v.findViewById(R.id.layout3);
        layout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri content_url;
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                if(jishuSupprot.equals("")){
                    content_url = Uri.parse("http://www.indranet.cn");
                }else{
                    content_url= Uri.parse(jishuSupprot);
                }
                intent.setData(content_url);
                startActivity(intent);
                pp.dismiss();
            }
        });
        LinearLayout layout4 = (LinearLayout) v.findViewById(R.id.layout4);
        layout4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                umWeb = new UMWeb(share.equals("") ? appUrl : share);
                umWeb.setThumb(new UMImage(mApplication.getInstance(), R.drawable.indra_share));
                umWeb.setTitle(getResources().getString(R.string.app_name) + "App");
                umWeb.setDescription(share.equals("") ? appUrl : share);
                if (umWeb != null) {
                    new  ShareManager().shareWeb(umWeb,NianFo.this);
                }
                pp.dismiss();
            }
        });
        LinearLayout layout5= (LinearLayout) v.findViewById(R.id.layout5);
        layout5.setVisibility(View.GONE);
//        layout5.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i=new Intent(NianFo.this, Contact.class);
//                i.putExtra("sms","".equals(SMS)?sp.getString("sms",""):SMS);
//                startActivity(i);
//                pp.dismiss();
//            }
//        });
    }

    public void createShortCut() {
        //创建快捷方式的Intent
        Intent shortcutintent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        //不允许重复创建
        shortcutintent.putExtra("duplicate", false);
        //需要现实的名称
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getResources().getString(R.string.app_name) + "WEB");
        //快捷图片
        Parcelable icon = Intent.ShortcutIconResource.fromContext(NianFo.this, R.drawable.indra);
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(Constants.host_Ip + "/" + Constants.NAME_LOW + ".php");
        intent.setData(content_url);
        //点击快捷图片，运行的程序主入口
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        //发送广播。OK
        sendBroadcast(shortcutintent);
    }

}
