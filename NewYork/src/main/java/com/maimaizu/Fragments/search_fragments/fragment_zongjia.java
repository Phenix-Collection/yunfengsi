package com.maimaizu.Fragments.search_fragments;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.maimaizu.Base.BaseFragment;
import com.maimaizu.R;
import com.maimaizu.Utils.NumUtils;
import com.maimaizu.Utils.ToastUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Administrator on 2017/4/30.
 */

public class fragment_zongjia extends BaseFragment implements View.OnClickListener {
    private TextView buxian, four, f_s, s_e, e_100, han_150;
    private EditText min, max;
    private String minum = "";
    private String maxnum = "";
    private TextView commit;
    private String text = "";
    private onSelectedListener onSelectedListener;
    private View root;
    private boolean isZuLin = false;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_zongjia;
    }

    @Override
    public void initView(View view) {
        root = view;
        buxian = (TextView) view.findViewById(R.id.buxian);
        four = (TextView) view.findViewById(R.id.four);
        f_s = (TextView) view.findViewById(R.id.four_six);
        s_e = (TextView) view.findViewById(R.id.six_eight);
        e_100 = (TextView) view.findViewById(R.id.eight_100);
        han_150 = (TextView) view.findViewById(R.id.ten_fif);
        min = (EditText) view.findViewById(R.id.min);
        max = (EditText) view.findViewById(R.id.max);
        commit = (TextView) view.findViewById(R.id.commit);
        onSelectedListener = (com.maimaizu.Fragments.search_fragments.onSelectedListener) getActivity();


    }

    @Override
    public void setOnClick() {
        buxian.setOnClickListener(this);
        four.setOnClickListener(this);
        f_s.setOnClickListener(this);
        s_e.setOnClickListener(this);
        e_100.setOnClickListener(this);
        han_150.setOnClickListener(this);
        commit.setOnClickListener(this);
        root.findViewById(R.id.five_h).setOnClickListener(this);
        root.findViewById(R.id.buxian1).setOnClickListener(this);
        root.findViewById(R.id.five_thourend).setOnClickListener(this);
        root.findViewById(R.id.one_two).setOnClickListener(this);
        root.findViewById(R.id.two_three).setOnClickListener(this);
        root.findViewById(R.id.three_five).setOnClickListener(this);
        root.findViewById(R.id.five_eight).setOnClickListener(this);
        root.findViewById(R.id.eight).setOnClickListener(this);
    }

    @Override
    public boolean setEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setZuLin(ZongjiaType type) {
        isZuLin = type.isZulin();
        root.findViewById(R.id.fangjia).setVisibility(View.GONE);
        root.findViewById(R.id.zujin).setVisibility(View.VISIBLE);

    }
    public static class ZongjiaType{
        private boolean isZulin;

        public boolean isZulin() {
            return isZulin;
        }

        public void setZulin(boolean zulin) {
            isZulin = zulin;
        }
    }


    @Override
    public void doThings() {

    }

    @Override
    public void onClick(View view) {
        minum = min.getText().toString();
        maxnum = max.getText().toString();
        switch (view.getId()) {
            case R.id.commit:
                TextView t = (TextView) root.findViewWithTag(text);
                if (t != null) {
                    t.setTextColor(Color.BLACK);
                }
                if (!minum.equals("") && maxnum.equals("")) {//自定 最小价格
                    text = NumUtils.getNumPrice(minum) + "以上";
                } else if (minum.equals("") && !maxnum.equals("")) {
                    text = NumUtils.getNumPrice(maxnum) + "以下";
                } else if (!minum.equals("") && !maxnum.equals("")) {
                    int max = Integer.valueOf(maxnum);
                    int min = Integer.valueOf(minum);
                    if (max <= min) {
                        ToastUtil.showToastShort("最大价格应大于最小价格");
                        return;
                    }
                    text = NumUtils.getNumPrice(minum) + "-" + NumUtils.getNumPrice(maxnum);
                }
                onSelectedListener.onZongJiaSelected(text, min.getText().toString(), max.getText().toString());
                break;
            case R.id.buxian:
            case R.id.four:
            case R.id.four_six:
            case R.id.six_eight:
            case R.id.eight_100:
            case R.id.ten_fif:
                TextView textView = (TextView) root.findViewWithTag(text);
                if (textView != null) {
                    textView.setTextColor(Color.BLACK);
                }
                text = (String) view.getTag();
                ((TextView) view).setTextColor(ContextCompat.getColor(getActivity(), R.color.main_color));
                minum = "";
                maxnum = "";
                min.setText("");
                max.setText("");
                String s = text;
                String money1 = "", money2 = "";
                if (s.equals("不限")) {
                    money1 = "0";
                    money2 = "0";
                } else if (s.equals("40万以下")) {
                    money1 = "0";
                    money2 = "400000";
                } else {
                    money1 = Integer.valueOf(s.substring(0, s.indexOf("-"))) * 10000 + "";
                    money2 = Integer.valueOf(s.substring(s.indexOf("-") + 1, s.indexOf("万"))) * 10000 + "";
                }
                onSelectedListener.onZongJiaSelected(text, money1, money2);
                break;

            case R.id.buxian1:
            case R.id.five_h:
            case R.id.five_thourend:
            case R.id.one_two:
            case R.id.two_three:
            case R.id.three_five:
            case R.id.five_eight:
            case R.id.eight:
                TextView t2 = (TextView) root.findViewWithTag(text);
                if (t2 != null) {
                    t2.setTextColor(Color.BLACK);
                }
                text = (String) view.getTag();
                ((TextView) view).setTextColor(ContextCompat.getColor(getActivity(), R.color.main_color));
                minum = "";
                maxnum = "";
                min.setText("");
                max.setText("");
                String s2 = text;
                String m1 = "", m2 = "";
                if (s2.equals("不限")) {
                    m1 = "0";
                    m2 = "0";
                } else if (s2.equals("500以下")) {
                    m1 = "0";
                    m2 = "500";
                }else if(s2.equals("8000以上")){
                    m1 = "8000";
                    m2 = "0";
                } else {
                    m1 = Integer.valueOf(s2.substring(0, s2.indexOf("-"))) + "";
                    m2 = Integer.valueOf(s2.substring(s2.indexOf("-") + 1, s2.indexOf("美元"))) + "";
                }
                onSelectedListener.onZongJiaSelected(text, m1, m2);
                break;
        }
//        if (view != commit) {//选择
//            TextView textView = (TextView) root.findViewWithTag(text);
//            if (textView != null) {
//                textView.setTextColor(Color.BLACK);
//            }
//            text = (String) view.getTag();
//            ((TextView) view).setTextColor(ContextCompat.getColor(getActivity(), R.color.main_color));
//            minum = "";
//            maxnum = "";
//            min.setText("");
//            max.setText("");
//
//        } else {//提交
//            if (minum.equals("") && maxnum.equals("")) {//非自定义
//                if (text.equals("")) {
//                    return;
//                }
//                String s = text;
//                String money1 = "", money2 = "";
//                if (s.equals("不限")) {
//                    money1 = "0";
//                    money2 = "0";
//                } else if (s.equals("40万以下")) {
//                    money1 = "0";
//                    money2 = "400000";
//                } else {
//                    money1 = Integer.valueOf(s.substring(0, s.indexOf("-"))) * 10000 + "";
//                    money2 = Integer.valueOf(s.substring(s.indexOf("-") + 1, s.indexOf("万"))) * 10000 + "";
//                }
//                onSelectedListener.onZongJiaSelected(text, money1, money2);
//            } else {//自定价格
//
//            }
//        }

    }
}
