package com.maimaizu.Adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.maimaizu.R;
import com.maimaizu.Utils.DimenUtils;
import com.maimaizu.Utils.NumUtils;
import com.maimaizu.Utils.mApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Administrator on 2017/4/26.
 */

public class mBaseAdapter extends BaseMultiItemQuickAdapter<mBaseAdapter.OneMulitem> {
    private Context context;

    public mBaseAdapter(Context context, List<OneMulitem> data) {
        super(data);
        this.context = context;
        addItemType(1, R.layout.home_new_double_item);
        addItemType(2, R.layout.home_new_double_item);
        addItemType(3, R.layout.home_zulin_item);
    }

    @Override
    protected void convert(BaseViewHolder holder, OneMulitem map) {

        switch (holder.getItemViewType()) {

            case 1://新房
                holder.setText(R.id.title, mApplication.ST(map.getTitle()))
                        .setText(R.id.abs, mApplication.ST(map.getVillage()));
                holder.setText(R.id.money, mApplication.ST("$"+map.getMoney() + "/平"));
                holder.setText(R.id.type,mApplication.ST("新房"));
                break;
            case 2://二手房
                holder.setText(R.id.title, mApplication.ST(map.getTitle()))
                        .setText(R.id.abs, mApplication.ST(map.getHousetype() + " / " + map.getArea() +
                                "㎡ / " + map.getPoint() + " / " + map.getVillage()));
                String money = NumUtils.getNumStr(map.getMoney());
                String per = "   $" + String.format("%.0f", (Double.valueOf(map.getMoney()) / Double.valueOf(map.getArea()))) + "/平";
                SpannableString ss = new SpannableString(mApplication.ST(money + per));
                ss.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.wordhuise)), money.length(), ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ss.setSpan(new AbsoluteSizeSpan(DimenUtils.dip2px(context, 12)), money.length(), ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.setText(R.id.money, ss);
                holder.setText(R.id.type,mApplication.ST("二手房"));
                break;
            case 3:
                holder.setText(R.id.title,mApplication.ST(map.getTitle()))
                        .setText(R.id.abs, mApplication.ST(map.getHousetype() + " / " + map.getArea() +
                                "㎡ / " + map.getPoint() + " / " + map.getVillage()))
                        .setText(R.id.xiaoqu,mApplication.ST(map.getVillage()))
                        .setText(R.id.money,mApplication.ST("$"+map.getMoney()+"/月"));
                holder.setText(R.id.type,mApplication.ST("租房"));
                break;
        }
        initTags(holder,map);
        Glide.with(context).load(map.getImage()).override(DimenUtils.dip2px(context, 120)
                , DimenUtils.dip2px(context, 90)).centerCrop().into((ImageView) holder.getView(R.id.image));
    }

    private void initTags(BaseViewHolder holder, OneMulitem one) {
        LinearLayout l = holder.getView(R.id.tags);
        l.removeAllViews();
        try {
            JSONArray jsonArray = one.getTags();
            if(jsonArray!=null){
                for (int i = 0; i < jsonArray.length(); i++) {
                    TextView textView = new TextView(context);
                    textView.setBackgroundResource(R.drawable.tag_bg);
                    textView.setTextColor(ContextCompat.getColor(context, R.color.main_color));
                    textView.setTextSize(10);
                    textView.setText(mApplication.ST(((JSONObject) jsonArray.get(i)).getString("name")));
                    l.addView(textView);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public OneMulitem getOneMulitem() {
        return new OneMulitem();
    }

    public static class OneMulitem extends MultiItemEntity {
        private String id;
        private String title;
        private String image;
        private String housetype;

        public OneMulitem() {
            super();
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getHousetype() {
            return housetype;
        }

        public void setHousetype(String housetype) {
            this.housetype = housetype;
        }

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }

        public String getVillage() {
            return village;
        }

        public void setVillage(String village) {
            this.village = village;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

//        public String getType() {
//            return type;
//        }
//
//        public void setType(String type) {
//            this.type = type;
//        }

        public String getPoint() {
            return point;
        }

        public void setPoint(String point) {
            this.point = point;
        }

        public JSONArray getTags() {
            return tags;
        }

        public void setTags(JSONArray tags) {
            this.tags = tags;
        }

        private String area;
        private String village;
        private String money;
        private String point;
        private JSONArray tags;
    }


}
