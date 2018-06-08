package com.yunfengsi.Models.Model_zhongchou;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.yunfengsi.R;
import com.yunfengsi.Setting.Mine_gerenziliao;
import com.yunfengsi.Utils.CheckNumUtil;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.LoginUtil;
import com.yunfengsi.Utils.Network;
import com.yunfengsi.Utils.TimeUtils;
import com.yunfengsi.Utils.mApplication;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.yunfengsi.R.id.days;

/**
 * Created by Administrator on 2016/9/12 0012.
 * 众筹项目列表
 */
public class FundingItemAdapter extends BaseAdapter {
    private static final String TAG = "FundingItemAdapter";
    public List<HashMap<String, String>> img_list;
    private Activity context;
    private int screenWidth;
    private SharedPreferences sp;

    private String currentFee;
    private ProgressDialog p;
    public FundingItemAdapter(List<HashMap<String, String>> img_list, Activity context) {
        this.img_list = img_list;
        this.context = context;
        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        sp=context.getSharedPreferences("user",Context.MODE_PRIVATE);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        } else {
            return 1;
        }

    }

    @Override
    public int getCount() {
        return img_list.size();
    }

    @Override
    public Object getItem(int position) {
        return img_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        ViewHolder2 holder2 = null;
        final HashMap<String, String> map = img_list.get(position);
        if (convertView == null) {
            if (getItemViewType(position) == 0) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.item_funding, null);
                holder.img_jump = (ImageView) convertView.findViewById(R.id.img_jump);
                holder.tv_funding_name = (TextView) convertView.findViewById(R.id.tv_funding_name);
                holder.tv_goal_money = (TextView) convertView.findViewById(R.id.tv_goal_money);
                holder.tv_get_money = (TextView) convertView.findViewById(R.id.tv_get_money);
                holder.tv_support_count = (TextView) convertView.findViewById(R.id.tv_support_count);
                holder.tv_rest_time = (TextView) convertView.findViewById(R.id.tv_rest_time);
                holder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
                holder.but_surpport= (TextView) convertView.findViewById(R.id.fund_list_item_Tosurpport);
                ((TextView) convertView.findViewById(R.id.fund_list_item_Tosurpport)).setText(mApplication.ST("我要助学"));
                ((TextView) convertView.findViewById(R.id.goal)).setText(mApplication.ST("目标金额"));
                ((TextView) convertView.findViewById(R.id.get)).setText(mApplication.ST("已达成"));
                ((TextView) convertView.findViewById(R.id.support_people)).setText(mApplication.ST("支持人次"));
                ((TextView) convertView.findViewById(R.id.rest_days)).setText(mApplication.ST("剩余天数"));
                convertView.setTag(holder);
            }else{
                holder2 = new ViewHolder2();
                convertView = LayoutInflater.from(context).inflate(R.layout.fund_list_item_small, null);
                holder2.tv_funding_name = (TextView) convertView.findViewById(R.id.fund_list_item_small_title);
                holder2.tv_goal_money = (TextView) convertView.findViewById(R.id.tv_goal_money);
                holder2.tv_get_money = (TextView) convertView.findViewById(R.id.tv_get_money);
                holder2.tv_support_count = (TextView) convertView.findViewById(R.id.tv_support_count);
                holder2.tv_rest_time = (TextView) convertView.findViewById(R.id.tv_rest_time);
                holder2.but_surpport= (TextView) convertView.findViewById(R.id.fund_list_item_small_Tosurpport);
                holder2.status= (TextView) convertView.findViewById(R.id.status);
                ((TextView) convertView.findViewById(R.id.fund_list_item_small_Tosurpport)).setText(mApplication.ST("我要助学"));
                ((TextView) convertView.findViewById(R.id.goal)).setText(mApplication.ST("目标金额"));
                ((TextView) convertView.findViewById(R.id.get)).setText(mApplication.ST("已达成"));
                ((TextView) convertView.findViewById(R.id.person)).setText(mApplication.ST("支持人次"));
                ((TextView) convertView.findViewById(days)).setText(mApplication.ST("剩余天数"));
                convertView.setTag(holder2);
            }

        } else {
            if(getItemViewType(position)==0){
                holder = (ViewHolder) convertView.getTag();
            }else{
                holder2= (ViewHolder2) convertView.getTag();
            }
        }

        if(getItemViewType(position)==0){
            final  ImageView imageView=holder.img_jump;
            Glide.with(context).load(map.get("image"))
                    .asBitmap()
                    .override(screenWidth - DimenUtils.dip2px(context, 40), DimenUtils.dip2px(context, 180))
                    .centerCrop().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    RoundedBitmapDrawable rbd = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                    rbd.setCornerRadius(DimenUtils.dip2px(context, 5));
                    imageView.setImageDrawable(rbd);
                }
            });
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, FundingDetailActivity.class);
                    intent.putExtra("id", map.get("id"));
                    intent.putExtra("title",map.get("title"));
                    context.startActivity(intent);
                }
            });
            holder.tv_funding_name.setText(mApplication.ST(map.get("title")));
            //将科学计数法转换成普通数字
            BigDecimal db = new BigDecimal(map.get("tar_money"));
            holder.tv_goal_money.setText("￥" + db.toPlainString());
            holder.tv_get_money.setText("￥" + map.get("sen_money"));
            holder.tv_support_count.setText(map.get("cy_people")+"人");
            //计算剩余天数

            double t = ((TimeUtils.dataOne(map.get("end_time")) - System.currentTimeMillis())) / 1000 / 60 / 60 / 24d;
            DecimalFormat df = new DecimalFormat(".0");
            double t1=Double.valueOf(df.format(t));
            int tt= ((Double) Math.ceil(t1)).intValue();
            LogUtil.e("剩余天数：："+tt);
            holder.tv_rest_time.setText(mApplication.ST(t1 <= 0 ? "已结束" : tt + "天"));

            if(holder.tv_rest_time.getText().toString().equals(mApplication.ST("已结束"))){
                holder.but_surpport.setText(mApplication.ST("已结束"));
                holder.but_surpport.setEnabled(false);
            }else{
                holder.but_surpport.setText(mApplication.ST("我要助学"));
                holder.but_surpport.setEnabled(true);
            }
            //设置进度条
            holder.progressBar.setMax(db.intValue());
            holder.progressBar.setProgress(((int) Math.floor(Double.valueOf(map.get("sen_money")))));
            holder.but_surpport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!new LoginUtil().checkLogin(context)){
                        return;
                    }
                    showPayDialog(context,map.get("id"),map.get("title"));
//                    Intent intent = new Intent("Mine_GY");
//                    context.sendBroadcast(intent);
                }
            });
        }else{
            holder2.but_surpport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!new LoginUtil().checkLogin(context)){
                        return;
                    }
                    showPayDialog(context,map.get("id"),map.get("title"));
//                    Intent intent = new Intent("Mine_GY");
//                    context.sendBroadcast(intent);
                }
            });
            if("2".equals(map.get("finish"))){
                holder2.status.setVisibility(VISIBLE);
            }else{
                holder2.status.setVisibility(GONE);
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, FundingDetailActivity.class);
                    intent.putExtra("id", map.get("id"));
                    intent.putExtra("title",map.get("title"));
                    context.startActivity(intent);
                }
            });
            holder2.tv_funding_name.setText(mApplication.ST(map.get("title")));
            //将科学计数法转换成普通数字
            BigDecimal db = new BigDecimal(map.get("tar_money"));
            holder2.tv_goal_money.setText("￥" + db.toPlainString());
            holder2.tv_get_money.setText("￥" + map.get("sen_money"));
            holder2.tv_support_count.setText(map.get("cy_people")+"人");
            //计算剩余天数

            double t = ((TimeUtils.dataOne(map.get("end_time")) - System.currentTimeMillis())) / 1000 / 60 / 60 / 24d;
            DecimalFormat df = new DecimalFormat(".0");//转换成一位小数
            double t1=Double.valueOf(df.format(t));
            int tt= ((Double) Math.ceil(t1)).intValue();
            LogUtil.e("剩余天数：："+tt);
            holder2.tv_rest_time.setText(mApplication.ST(t1 <= 0 ? "已结束" : tt + "天"));

            if(holder2.tv_rest_time.getText().toString().equals(mApplication.ST("已结束"))){
                holder2.but_surpport.setText(mApplication.ST("已结束"));
                holder2.but_surpport.setEnabled(false);
            }else{
                holder2.but_surpport.setText(mApplication.ST("我要助学"));
                holder2.but_surpport.setEnabled(true);
            }
        }


        return convertView;
    }

    public void setList(List<HashMap<String, String>> list) {
        img_list = list;
    }


    static class ViewHolder {
        private ImageView img_jump;
        private TextView tv_funding_name;
        private TextView tv_goal_money;
        private TextView tv_get_money;
        private TextView tv_support_count;
        private TextView tv_rest_time;
        private ProgressBar progressBar;
        private TextView but_surpport;
    }

    static class ViewHolder2 {
        private TextView tv_funding_name;
        private TextView tv_goal_money;
        private TextView tv_get_money;
        private TextView tv_support_count;
        private TextView tv_rest_time;
        private TextView but_surpport;

        private TextView status;
    }
    //调起支付  需要在上下文设置currentFee变量，并为每个item设置tag识别
    public void showPayDialog(final Activity context, final String fundId, final String title) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final View view = LayoutInflater.from(context).inflate(R.layout.pay_choose_num_dialog, null);
        final TextView one = (TextView) view.findViewById(R.id.one);
        final TextView five = (TextView) view.findViewById(R.id.five);
        final TextView ten = (TextView) view.findViewById(R.id.ten);
        final TextView twelve = (TextView) view.findViewById(R.id.twelve);
        final TextView fifty = (TextView) view.findViewById(R.id.fifty);
        final TextView one_han = (TextView) view.findViewById(R.id.han);
        final TextView two_han = (TextView) view.findViewById(R.id.two_han);
        final TextView five_han = (TextView) view.findViewById(R.id.five_han);
        final TextView others = (TextView) view.findViewById(R.id.others);
        final EditText otherNum = (EditText) view.findViewById(R.id.otherNum);
        final Button agreeTOPay = (Button) view.findViewById(R.id.agreeToPay);
        final RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.otherlayout);
       int dimens = DimenUtils.dip2px(context, 10);
        builder.setView(view,0, dimens, 0, dimens);

        final AlertDialog dialog = builder.create();
        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(0x00000000));
        window.setWindowAnimations(R.style.dialogWindowAnim);
        WindowManager.LayoutParams wl = window.getAttributes();
        window.setAttributes(wl);
        dialog.show();
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                switch (v.getId()) {
                    case R.id.one:
                        view.findViewWithTag(currentFee).setEnabled(true);
                        currentFee = (String) v.getTag();
                        v.setEnabled(false);
                        break;
                    case R.id.five:
                        view.findViewWithTag(currentFee).setEnabled(true);
                        currentFee = (String) v.getTag();
                        v.setEnabled(false);
                        break;
                    case R.id.ten:
                        view.findViewWithTag(currentFee).setEnabled(true);
                        currentFee = (String) v.getTag();
                        v.setEnabled(false);
                        break;
                    case R.id.twelve:
                        view.findViewWithTag(currentFee).setEnabled(true);
                        currentFee = (String) v.getTag();
                        v.setEnabled(false);
                        break;
                    case R.id.fifty:
                        view.findViewWithTag(currentFee).setEnabled(true);
                        currentFee = (String) v.getTag();
                        v.setEnabled(false);
                        break;
                    case R.id.han:
                        view.findViewWithTag(currentFee).setEnabled(true);
                        currentFee = (String) v.getTag();
                        v.setEnabled(false);
                        break;
                    case R.id.two_han:
                        view.findViewWithTag(currentFee).setEnabled(true);
                        currentFee = (String) v.getTag();
                        v.setEnabled(false);
                        break;
                    case R.id.five_han:
                        view.findViewWithTag(currentFee).setEnabled(true);
                        currentFee = (String) v.getTag();
                        v.setEnabled(false);
                        break;
                    case R.id.others:
                        view.findViewWithTag(currentFee).setEnabled(true);
                        currentFee = (String) v.getTag();
                        v.setEnabled(false);
                        break;
                    case R.id.canclePay:
                        dialog.dismiss();
                        break;
                    case R.id.agreeToPay:
                        if (!Network.HttpTest(context) ){
                            Toast.makeText(mApplication.getInstance(), mApplication.ST("网络连接不稳定，请稍后重试"), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (sp.getString("pet_name", "").trim().equals("")) {
                            Toast.makeText(context, mApplication.ST("请完善信息"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context, Mine_gerenziliao.class);
                            context.startActivity(intent);
                            return;
                        }

                        dialog.dismiss();
                        mApplication.openPayLayout
                                (context,currentFee.equals(others.getTag().toString()) ?
                                        String.format("%.2f",(Double.valueOf(otherNum.getText().toString()))) : String.valueOf(Integer.valueOf(currentFee)),fundId,title,"1","5","");

                        break;
                }
                if (v.getId() != R.id.canclePay && v.getId() != R.id.agreeToPay) {
                    if (!currentFee.equals(others.getTag().toString())) {
                        if (layout.getVisibility() == VISIBLE) {
                            layout.setVisibility(GONE);
                        }
                    } else {
                        if (layout.getVisibility() == GONE) {
                            layout.setVisibility(VISIBLE);
                        }

                    }
                    if (v instanceof TextView && v.getId() != R.id.agreeToPay && v.getId() != R.id.canclePay) {
                        ((TextView) v).setTextColor(context.getResources().getColor(R.color.main_color));
                    }
                    if (view.findViewWithTag(currentFee) instanceof TextView) {
                        ((TextView) view.findViewWithTag(currentFee)).setTextColor(Color.GRAY);
                    }
                }
                if (v.getId() == R.id.others) {
                    if (otherNum.getText().toString().equals("")) {
                        agreeTOPay.setEnabled(false);
                        agreeTOPay.setTextColor(context.getResources().getColor(R.color.umeng_socialize_text_friends_list));
                    }
                } else {
                    if (!agreeTOPay.isEnabled()) {
                        agreeTOPay.setEnabled(true);
                        agreeTOPay.setTextColor(context.getResources().getColor(R.color.white));
                    }
                }

            }
        };
        currentFee = (String) one.getTag();
        one.setEnabled(false);
        one.setTextColor(Color.GRAY);
        ten.setOnClickListener(onClickListener);
        twelve.setOnClickListener(onClickListener);
        one.setOnClickListener(onClickListener);
        fifty.setOnClickListener(onClickListener);
        five.setOnClickListener(onClickListener);
        two_han.setOnClickListener(onClickListener);
        five_han.setOnClickListener(onClickListener);
        one_han.setOnClickListener(onClickListener);
        others.setOnClickListener(onClickListener);
        view.findViewById(R.id.canclePay).setOnClickListener(onClickListener);
        agreeTOPay.setOnClickListener(onClickListener);
        otherNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    agreeTOPay.setEnabled(false);
                    agreeTOPay.setTextColor(context.getResources().getColor(R.color.umeng_socialize_text_friends_list));
                }
                if(CheckNumUtil.checkNum(s.toString())){
                    agreeTOPay.setEnabled(true);
                    agreeTOPay.setTextColor(Color.WHITE);
                }else{
                    agreeTOPay.setEnabled(false);
                    agreeTOPay.setTextColor(context.getResources().getColor(R.color.umeng_socialize_text_friends_list));
                }
//                if (Integer.valueOf(s.toString()) > 0.01) {
//                    if (!agreeTOPay.isEnabled()) {
//                        agreeTOPay.setEnabled(true);
//                        agreeTOPay.setTextColor(context.getResources().getColor(R.color.white));
//                    }
//                } else {
//                    if (agreeTOPay.isEnabled()) {
//                        agreeTOPay.setEnabled(false);
//                        agreeTOPay.setTextColor(context.getResources().getColor(R.color.umeng_socialize_text_friends_list));
//                    }
//                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
