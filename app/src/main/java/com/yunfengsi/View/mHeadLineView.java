package com.yunfengsi.View;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.yunfengsi.Model_zhongchou.FundingDetailActivity;
import com.yunfengsi.R;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.NumUtils;
import com.yunfengsi.XuanzheActivity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 作者：因陀罗网 on 2017/10/31 16:54
 * 公司：成都因陀罗网络科技有限公司
 */

public class mHeadLineView extends LinearLayout {
    public static final int FUND = 1;
    public static final int GONGYANG = 2;
    private Context context;
    private ImageView head;
    private ViewFlipper viewFlipper;
    private static final int heightDp = 80;
    private ArrayList<HashMap<String, String>> list;//数据源
    private int type=1;
    public mHeadLineView(Context context) {
        this(context, null);
    }

    public mHeadLineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setOrientation(LinearLayout.HORIZONTAL);//设置方向
        setGravity(Gravity.CENTER_VERTICAL);//设置竖向居中
        int dp20 = dip2px(context, 20);
        int dp10 = dip2px(context, 10);
        setPadding(dp10, 0, dp20, 0);
        head = new ImageView(context);
        viewFlipper = new ViewFlipper(context);


        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dip2px(context, 60));
        viewFlipper.setLayoutParams(ll);
        LinearLayout.LayoutParams ll2 = new LinearLayout.LayoutParams(dip2px(context, 60), dip2px(context, 60));
        head.setLayoutParams(ll2);
        Glide.with(context).load(R.drawable.zuixindongtai).override(dip2px(context, 60), dip2px(context, 60)).fitCenter().into(head);

        addView(head);
        addView(viewFlipper);


    }

    public void onDataArrival(ArrayList<HashMap<String, String>> list, int type) {
        this.list = list;
        this.type=type;
        int num = 2;
        if (viewFlipper.isFlipping()) {
            viewFlipper.stopFlipping();
        }
        viewFlipper.removeAllViews();
        for (int j = 0; j < list.size(); j = j + num) {
            View child = LayoutInflater.from(context).inflate(R.layout.headline_child_view, null);
            TextView tv1 = (TextView) child.findViewById(R.id.title1);
            HashMap<String, String> map1 = list.get(j);
            if (type == FUND) {
                LogUtil.e("助学头条1：："+map1);
                ((TextView) child.findViewById(R.id.tag1)).setText(map1.get("pet_name").trim());
                SpannableString ss1=new SpannableString(" 献出爱心 "+ NumUtils.subZeroAndDot(map1.get("money")) + " 元");
                ss1.setSpan(new ForegroundColorSpan(Color.RED),6,6+NumUtils.subZeroAndDot(map1.get("money")).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv1.setText(ss1);
                child.findViewById(R.id.layout1).setTag(map1.get("shop_id"));
            } else if (type == GONGYANG) {
                LogUtil.e("第一行：：：第"+j+"     "+map1.get("pet_name")+"      "+map1.get("sut_title")+"    "+map1.get("money"));
                ((TextView) child.findViewById(R.id.tag1)).setText(map1.get("pet_name").trim());
                SpannableString ss1=new SpannableString(" "+map1.get("sut_title")+" "+ NumUtils.subZeroAndDot(map1.get("money")) + " 元");
                ss1.setSpan(new ForegroundColorSpan(Color.RED),map1.get("sut_title").length()+2,map1.get("sut_title").length()+2+NumUtils.subZeroAndDot(map1.get("money")).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv1.setText(ss1);
                child.findViewById(R.id.layout1).setTag(map1.get("shop_id"));
            }
//            ((TextView) child.findViewById(R.id.tag1)).setText("最新");
            TextView tv2 = (TextView) child.findViewById(R.id.title2);
            if (list.size() > j + 1) {
                HashMap<String, String> map2 = list.get(j + 1);
                if (type == FUND) {
                    LogUtil.e("助学头条2：："+map2);
                    ((TextView) child.findViewById(R.id.tag2)).setText(map2.get("pet_name").trim());
                    SpannableString ss2=new SpannableString(" 献出爱心 "+ NumUtils.subZeroAndDot(map2.get("money")) + " 元");
                    ss2.setSpan(new ForegroundColorSpan(Color.RED),6,6+NumUtils.subZeroAndDot(map2.get("money")).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv2.setText(ss2);
                    child.findViewById(R.id.layout2).setTag(map2.get("shop_id"));
                } else if (type == GONGYANG) {
                    LogUtil.e("第2行：：：第"+(j+1)+"     "+map2.get("pet_name")+"      "+map2.get("sut_title")+"    "+map2.get("money"));
                    ((TextView) child.findViewById(R.id.tag2)).setText(map2.get("pet_name").trim());
                    SpannableString ss2=new SpannableString(" "+map2.get("sut_title")+" "+ NumUtils.subZeroAndDot(map2.get("money")) + " 元");
                    ss2.setSpan(new ForegroundColorSpan(Color.RED),map2.get("sut_title").length()+2,2+map2.get("sut_title").length()+NumUtils.subZeroAndDot(map2.get("money")).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv2.setText(ss2);
                    child.findViewById(R.id.layout2).setTag(map2.get("shop_id"));
                }
//                ((TextView) child.findViewById(R.id.tag2)).setText("最新");
            }else{
                tv2.setTag(null);
            }
            child.findViewById(R.id.layout1).setOnClickListener(click);
            child.findViewById(R.id.layout2).setOnClickListener(click);
            viewFlipper.addView(child);
        }

        Animation out = new TranslateAnimation(0, 0, 0, -dip2px(context, 60));
        out.setDuration(200);
        out.setInterpolator(new AccelerateDecelerateInterpolator());
//        out.setInterpolator(new LinearInterpolator());
        Animation in = new TranslateAnimation(0, 0, dip2px(context, 60), 0);
        in.setInterpolator(new AccelerateDecelerateInterpolator());
//        in.setInterpolator(new LinearInterpolator());
        in.setDuration(200);
        viewFlipper.setFlipInterval(3000);
        viewFlipper.setOutAnimation(out);
        viewFlipper.setInAnimation(in);
        if(list.size()>num){
            viewFlipper.startFlipping();
        }

    }
  public   OnClickListener click=new OnClickListener() {
        @Override
        public void onClick(View view) {
            if(view.getTag()!=null){
                switch (type){
                    case FUND:
                        Intent intent =new Intent(context, FundingDetailActivity.class);
                        intent.putExtra("id",view.getTag().toString());
                        context.startActivity(intent);
                        LogUtil.e("助学跳转：：：：id:::"+view.getTag().toString());
                        break;
                    case GONGYANG:
                        Intent intent2 =new Intent(context, XuanzheActivity.class);
                        intent2.putExtra("id",view.getTag().toString());
                        context.startActivity(intent2);
                        LogUtil.e("供养跳转：：：：id:::"+view.getTag().toString());
                        break;
                }
            }

        }
    };
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = dip2px(context, heightDp);
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, heightMode));
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
