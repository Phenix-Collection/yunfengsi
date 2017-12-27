package com.qianfujiaoyu.Model_Order;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.request.BaseRequest;
import com.qianfujiaoyu.Model_activity.activity_Detail;
import com.qianfujiaoyu.R;
import com.qianfujiaoyu.Utils.AnalyticalJSON;
import com.qianfujiaoyu.Utils.ApisSeUtil;
import com.qianfujiaoyu.Utils.Constants;
import com.qianfujiaoyu.Utils.DimenUtils;
import com.qianfujiaoyu.Utils.ImageUtil;
import com.qianfujiaoyu.Utils.LoginUtil;
import com.qianfujiaoyu.Utils.PreferenceUtil;
import com.qianfujiaoyu.Utils.ProgressUtil;
import com.qianfujiaoyu.Utils.StatusBarCompat;
import com.qianfujiaoyu.Utils.TimeUtils;
import com.qianfujiaoyu.Utils.mApplication;
import com.qianfujiaoyu.View.mItemDecoration;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/10/10.
 */
public class Search_Good extends AppCompatActivity implements View.OnClickListener {
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.search_back://返回
                finish();
                break;
            case R.id.search_sousuo://搜索
                getData();
                break;
            case R.id.search_zixun://资讯分类
                resetStatus();
                zixun.setSelected(true);
                type="news";
                break;
            case R.id.search_good://商品分类
                resetStatus();
                good.setSelected(true);
                type="good";
                break;
            case R.id.search_kecheng://课程分类
                resetStatus();
                kecheng.setSelected(true);
                type="activity";
                break;
//            case R.id.search_huodong://活动分类
//                resetStatus();
//                huodong.setSelected(true);
//                type="activity";
//                break;
//            case R.id.search_book://供养分类
//                resetStatus();
//                gongyang.setSelected(true);
//                type="shop";
//                break;
//            case R.id.search_cishan://慈善分类
//                resetStatus();
//                zhongchou.setSelected(true);
//                type="cfg";
//                break;
        }


    }

    /**
     * 搜索获取数据并保存搜索记录
     */
    private void getData() {

//        else if(type.equals("activity")){
//            url=Constants.Activity_Search_Ip;
//        }else if(type.equals("shop")){
//            url=Constants.BOOk_Search_Ip;
//        }else if(type.equals("cfg")){
//                url=Constants.CFG_Search_Ip;
//        }
        if(input.getText().toString().equals("")){
            Toast.makeText(Search_Good.this, "请输入关键字", Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject js=new JSONObject();
        try {
            if(type.equals("news")){
                url= Constants.News_Search_Ip;
            }else if(type.equals("good")){
                url=Constants.Good_Search;
            }else if(type.equals("activity")){
                url=Constants.Activity_Search;
                js.put("type","2");
            }
            js.put("m_id", Constants.M_id);
            js.put("msg",input.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final ApisSeUtil.M m=ApisSeUtil.i(js);
        ProgressUtil.show(this,"","请稍等");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {


                    final String data= OkGo.post(url).params("key",m.K()).params("msg", m.M())
                            .execute().body().string();
//                    gridList.add(input.getText().toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!data.equals("")){
                                if(type.equals("good")){
                                    listList= AnalyticalJSON.getList_zj(data);
                                }else {
                                    listList=AnalyticalJSON.getList(data,type);
                                }

                                if(listList!=null){
                                    adapter=new OrderAdapter(Search_Good.this,listList,type.equals("activity")?true:false);
                                    listView.setAdapter(adapter);
                                    adapter.setEmptyView(mApplication.getLoadNothing(R.drawable.load_nothing,"未查询到相关信息",200));
                                    adapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
                                        @Override
                                        public void onItemClick(View view, int i) {
                                            Intent intent=new Intent();

                                            HashMap<String,String > map=adapter.getData().get(i);
                                            if(map.get("enrollment")!=null){
                                                intent.setClass(Search_Good.this,activity_Detail.class);
                                                intent.putExtra("kecheng",true);
                                            }else{
                                                intent.setClass(Search_Good.this,Order_detail.class);
                                            }
                                            intent.putExtra("id",map.get("id"));
                                            startActivity(intent);
                                        }
                                    });
                                        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(input.getWindowToken(),0);
                                    ProgressUtil.dismiss();
                                }else{
                                    Toast.makeText(Search_Good.this, "未搜索到相关信息", Toast.LENGTH_SHORT).show();
                                    ProgressUtil.dismiss();
                                }
                            }else{
                                Toast.makeText(Search_Good.this, "未搜索到相关信息", Toast.LENGTH_SHORT).show();
                                ProgressUtil.dismiss();
                            }
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private static final String TAG = "Search";
    private String url;//搜索的接口地址
    private ImageView back;
    private EditText input;
    private TextView sousuo,zixun,removeAll,good,kecheng;
    private RecyclerView listView;
    private GridView grid;
    private List<String > gridList;
    private ArrayList<HashMap<String ,String >>listList;
    private String type;//搜索类别标示
    private OrderAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.search_good);
        initView();
//        loadHistroy();
    }

    /**
     * 重置类别
     */
    private void resetStatus(){
        zixun.setSelected(false);
        good.setSelected(false);
        kecheng.setSelected(false);
//        huodong.setSelected(false);
//        gongyang.setSelected(false);
//        zhongchou.setSelected(false);
    }
    /**
     * 初始化数据
     */
    private void initView() {
        good= (TextView) findViewById(R.id.search_good);
        kecheng= (TextView) findViewById(R.id.search_kecheng);
        listList=new ArrayList<>();
        back= (ImageView) findViewById(R.id.search_back);
        back.setOnClickListener(this);
        input= (EditText) findViewById(R.id.search_edit);//搜索输入框
        Drawable d= ContextCompat.getDrawable(this,R.drawable.search_black);
        d.setBounds(0,0, DimenUtils.dip2px(this,20),DimenUtils.dip2px(this,20));
        input.setCompoundDrawables(d,null,null,null);
        sousuo= (TextView) findViewById(R.id.search_sousuo);//搜索按钮
        zixun= (TextView) findViewById(R.id.search_zixun);
//        huodong= (TextView) findViewById(R.id.search_huodong);
//        gongyang= (TextView) findViewById(R.id.search_book);
//        zhongchou= (TextView) findViewById(R.id.search_cishan);
//        removeAll= (TextView) findViewById(R.id.search_removeHistory);//清空记录
        listView= (RecyclerView) findViewById(R.id.search_listview);//搜索结果展示listview
        listView.addItemDecoration(new mItemDecoration(this));
        listView.setLayoutManager(new LinearLayoutManager(this));

//        grid= (GridView) findViewById(R.id.search_grid);//搜索历史gridView
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                TextView view1 = (TextView) view.findViewById(R.id.mine_shoucang_item_type);
//                String id1 = view1.getTag().toString();
//                Intent intent = new Intent();
//
//                if (view1.getText().toString().equals("课程")) {
//                    intent.setClass(mApplication.getInstance(),activity_Detail.class);
//                }else if(view1.getText().toString().equals("商品")){
//                    intent.setClass(mApplication.getInstance(),Order_detail.class);
//                }
//                intent.putExtra("id", id1);
//                startActivity(intent);
//            }
//        });

        sousuo.setOnClickListener(this);
        zixun.setOnClickListener(this);
        good.setOnClickListener(this);
        kecheng.setOnClickListener(this);
//        huodong.setOnClickListener(this);
//        gongyang.setOnClickListener(this);
//        zhongchou.setOnClickListener(this);
//        removeAll.setOnClickListener(this);
        good.performClick();
    }

    /**
     * 搜索历史适配器
     */
    static  class  gridAdapter extends BaseAdapter{
        private List<String >list1;
        private Context context;

        public gridAdapter(Context context,List<String >list) {
            super();
            this.context=context;
            this.list1=list;

        }

        @Override
        public int getCount() {
            return list1==null?0:list1.size();
        }

        @Override
        public Object getItem(int position) {
            return list1.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView=new TextView(context);
            textView.setTextSize(14);
            textView.setMaxLines(1);
            textView.setTextColor(Color.BLACK);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setText(list1.get(position));
            return textView;
        }
    }

    /**
     * 搜索结果适配器
     */
    static  class  listAdapter extends BaseAdapter{
        public  List<HashMap<String ,String>>list;
        private Context context;

        private LayoutInflater inflater;
        private int screenWidth;
        public listAdapter(Context context ,List<HashMap<String ,String>>list1) {
            super();
            this.context=context;
            this.list=list1;
            inflater = LayoutInflater.from(context);
            screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        }
        public List<HashMap<String ,String>> getList(){
            return list;
        }
        public  void addList(List<HashMap<String ,String>>list1){
            this.list=list1;
        }
        @Override
        public int getCount() {
            return list==null?0:list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            View view = convertView;
            HashMap<String, String> map = list.get(position);
            if (view == null) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.mine_shoucang_item, parent, false);
                holder.image = (ImageView) view.findViewById(R.id.mine_shoucang_item_image);
                holder.title = (TextView) view.findViewById(R.id.mine_shoucang_item_title);
                holder.time = (TextView) view.findViewById(R.id.mine_shoucang_item_time);
                holder.user = (TextView) view.findViewById(R.id.mine_shoucang_item_user);
                holder.type= (TextView) view.findViewById(R.id.mine_shoucang_item_type);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
//            if(map.get("end_time")==null&&map.get("product")==null&&map.get("author")==null&&map.get("time")!=null){
//                holder.time.setText(map.get("time"));
//                holder.title.setText(map.get("title"));
//                holder.user.setText(map.get("issuer"));
//                holder.type.setText("资讯");
//                holder.type.setTag(map.get("id"));
//                Glide.with(context).load(map.get("image")).override(screenWidth * 3 / 10, screenWidth*6/ 25).centerCrop().into(holder.image);
//            }
//            else if(map.get("cy_people")==null&&map.get("end_time")!=null){
//                holder.type.setText("活动");
//                holder.type.setTag(map.get("id"));
//                holder.user.setText(map.get("author"));
//                holder.title.setText(map.get("title"));
//                Glide.with(context).load(map.get("image1")).override(screenWidth * 3 / 10, screenWidth*6/ 25).centerCrop().into(holder.image);
//                holder.time.setText("结束时间："+map.get("end_time"));
//            }else if(map.get("cy_people")!=null){
//                holder.type.setText("慈善");
//                holder.type.setTag(map.get("id"));
//                holder.user.setText("参与人数："+map.get("cy_people"));
//                holder.title.setText(map.get("title"));
//                Glide.with(context).load(map.get("image")).override(screenWidth * 3 / 10, screenWidth*6/ 25).centerCrop().into(holder.image);
//                holder.time.setText("结束时间："+map.get("end_time"));
//            }else if(map.get("product")!=null){
//                holder.type.setText("供养");
//                holder.type.setTag(map.get("id"));
//                Glide.with(context).load(map.get("image")).override(screenWidth/ 5, screenWidth*6/ 25).centerCrop().into(holder.image);
//                holder.user.setText(map.get("product"));
//                holder.title.setText(map.get("type1"));
//                holder.time.setText("￥"+map.get("money1"));
//            }else if(map.get("author")!=null){
//                holder.type.setText("图书");
//                holder.type.setTag(map.get("id"));
//                Glide.with(context).load(map.get("image1")).override(screenWidth/ 5, screenWidth*6/ 25).centerCrop().into(holder.image);
//                holder.user.setText(map.get("author"));
//                holder.title.setText(map.get("title"));
//                holder.time.setText("￥"+map.get("money"));
//            }
            if(map.get("enrollment")!=null){//课程
                holder.type.setText("课程");

                holder.type.setTag(map.get("id"));
                holder.user.setText(map.get("title"));
                holder.title.setText(map.get("abstract"));
                Glide.with(context).load(map.get("image1")).override(screenWidth * 3 / 10, screenWidth * 6 / 25).centerCrop().into(holder.image);
                holder.time.setText("结束时间：" + map.get("end_time"));
            }else{
                holder.type.setText("商品");
                holder.type.setTag(map.get("id"));
                Glide.with(context).load(map.get("image1")).override(screenWidth/ 5, screenWidth*6/ 25).centerCrop().into(holder.image);
                holder.time.setText("￥"+map.get("money"));
                holder.title.setText(map.get("abstract"));
                holder.user.setText(map.get("title"));
            }


            return view;
        }
        static class ViewHolder {
            ImageView image;
            TextView title, user, time,type;
        }
    }
    private static class OrderAdapter extends BaseQuickAdapter<HashMap<String, String>> {

        private WeakReference<Activity> w;
        private Activity context;
        private int dp120;
        private boolean isActivity = false;

        OrderAdapter(Activity a, ArrayList<HashMap<String, String>> list1, boolean isActivity) {
            super(R.layout.order_item, list1);

            w = new WeakReference<Activity>(a);
            this.context = w.get();
            dp120 = DimenUtils.dip2px(context, 120);
            this.isActivity = isActivity;
        }


        @Override
        protected void convert(BaseViewHolder holder, final HashMap<String, String> map) {
            if (isActivity) {
                holder.setImageBitmap(R.id.order_item_car_img, ImageUtil.readBitMap(context, R.drawable.yuyue));
                Glide.with(context).load(map.get("image1")).asBitmap().override(dp120, dp120).centerCrop()
                        .error(R.drawable.load_nothing)
                        .into(new BitmapImageViewTarget((ImageView) holder.getView(R.id.order_item_image)) {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                RoundedBitmapDrawable rb = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                                rb.setCornerRadius(10);
                                setDrawable(rb);
                            }
                        });
                holder.setVisible(R.id.order_item_type,false);
                holder.setText(R.id.order_item_type, map.get("name"))
                        .setText(R.id.order_item_name, map.get("title"))
                        .setText(R.id.order_item_abstract, map.get("abstract"))
                        .setText(R.id.order_item_sales, "截止:" + map.get("end_time"))
                        .setVisible(R.id.order_item_likes, false)
                        .setText(R.id.order_item_true_money, (map.get("money").equals("")||Double.valueOf(map.get("money")).intValue()<=0)?"免费":("¥ " + map.get("money")))
                        .setText(R.id.order_item_false_money, (map.get("quota").equals("")||Double.valueOf(map.get("quota")).intValue()<=0)?"人数不限":("人数:" + map.get("enrollment") + "/" + map.get("quota")))
                        .setTag(R.id.order_item_car, map.get("id"))
                ;
                if(TimeUtils.dataOne(map.get("end_time"))<System.currentTimeMillis()){
                    holder.setText(R.id.order_item_car_text,"已结束");
                    holder.setVisible(R.id.order_item_car_img,false);
                    holder.getView(R.id.order_item_car_text).setEnabled(false);
                }else{
                    holder.setVisible(R.id.order_item_car_img,true);
                    holder.setText(R.id.order_item_car_text,"立即预约");
                    holder.getView(R.id.order_item_car_text).setEnabled(true);
                }
                holder.convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String id = map.get("id");
                        Intent intent = new Intent(context, activity_Detail.class);
                        intent.putExtra("id", id);
                        intent.putExtra("kecheng",true);
                        context.startActivity(intent);
                    }
                });

                //// TODO: 2016/11/30 立即预约
                holder.setOnClickListener(R.id.order_item_car, new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        if (!new LoginUtil().checkLogin(context)) {
                            return;
                        }
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                        builder1.setCancelable(true);
                        builder1.setPositiveButton(mApplication.ST("取消"), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setTitle(mApplication.ST("确定报名参加" + map.get("title") + "吗？")).setNegativeButton(mApplication.ST("确定"), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                JSONObject js = new JSONObject();
                                try {
                                    js.put("act_id", map.get("id"));
                                    js.put("m_id", Constants.M_id);
                                    js.put("user_id", PreferenceUtil.getUserIncetance(mApplication.getInstance()).getString("user_id", ""));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                ApisSeUtil.M m = ApisSeUtil.i(js);
                                OkGo.post(Constants.Activity_BaoMing).tag(TAG)
                                        .params("key", m.K())
                                        .params("msg", m.M()).execute(new AbsCallback<HashMap<String, String>>() {
                                    @Override
                                    public HashMap<String, String> convertSuccess(okhttp3.Response response) throws Exception {
                                        return AnalyticalJSON.getHashMap(response.body().string());
                                    }


                                    @Override
                                    public void onBefore(BaseRequest request) {
                                        super.onBefore(request);
                                        ProgressUtil.show(context, "", mApplication.ST("正在报名，请稍等"));
                                    }

                                    @Override
                                    public void onSuccess(HashMap<String, String> map, Call call, okhttp3.Response response) {
                                        View view = LayoutInflater.from(context).inflate(R.layout.baoming_alert, null);
                                        AlertDialog.Builder b = new AlertDialog.Builder(context).
                                                setView(view);
                                        final AlertDialog d = b.create();
                                        d.getWindow().setDimAmount(0.2f);
                                        view.findViewById(R.id.commit).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                d.dismiss();
                                            }
                                        });
                                        if ("000".equals(map.get("code"))) {
                                            ((TextView) view.findViewById(R.id.result_msg)).setText(mApplication.ST("您已成功预约"));
                                            view.findViewById(R.id.commit).setBackgroundColor(Color.parseColor("#40d976"));
                                        } else {
                                            ((TextView) view.findViewById(R.id.result_msg)).setText(mApplication.ST("您已经预约过了哟~"));
                                            view.findViewById(R.id.commit).setBackgroundColor(Color.parseColor("#e75e5e"));
                                        }
//                                        ((TextView) view.findViewById(R.id.phone)).setText(mApplication.ST("审核结果请及时关注App我的活动：\n[我的]->[活动]"));
                                        ((TextView) view.findViewById(R.id.phone)).setText(mApplication.ST(""));

                                        d.show();
                                    }

                                    @Override
                                    public void onAfter(HashMap<String, String> map, Exception e) {
                                        super.onAfter(map, e);
                                        ProgressUtil.dismiss();
                                    }


                                });

                            }
                        }).create().show();
                    }
                });
            } else {//购买
                holder.setImageBitmap(R.id.order_item_car_img, ImageUtil.readBitMap(context, R.drawable.shopcar));
                Glide.with(context).load(map.get("image1")).asBitmap().override(dp120, dp120).centerCrop()
                        .error(R.drawable.load_nothing)
                        .into(new BitmapImageViewTarget((ImageView) holder.getView(R.id.order_item_image)) {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                RoundedBitmapDrawable rb = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                                rb.setCornerRadius(10);
                                setDrawable(rb);
                            }
                        });
                ((TextView) holder.getView(R.id.order_item_false_money)).getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                holder.setText(R.id.order_item_type, map.get("name"))
                        .setText(R.id.order_item_name, map.get("title"))
                        .setText(R.id.order_item_abstract, map.get("abstract"))
                        .setText(R.id.order_item_score, "每" + map.get("convert") + "积分可抵用1元")
                        .setText(R.id.order_item_sales, "总销量" + map.get("sales"))
                        .setText(R.id.order_item_true_money, "¥ " + map.get("money"))
                        .setText(R.id.order_item_likes, "赞" + map.get("likes"))
                        .setText(R.id.order_item_false_money, "¥ " + map.get("cost"))
                        .setTag(R.id.order_item_car, map.get("id"));


                //// TODO: 2016/11/30 立即购买
                holder.setOnClickListener(R.id.order_item_car, new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        ArrayList<HashMap<String, String>> l = new ArrayList<HashMap<String, String>>();
                        l.add(map);
                        Intent intent = new Intent(context, Dingdan_commit.class);
                        intent.putExtra("list", l);
                        context.startActivity(intent);

                    }
                });
                holder.convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String id = map.get("id");
                        Intent intent = new Intent(context, Order_detail.class);
                        intent.putExtra("id", id);
                        context.startActivity(intent);
                    }
                });


            }

        }


    }

}
