package com.maimaizu.Fragments.search_fragments;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.maimaizu.Base.BaseFragment;
import com.maimaizu.R;

/**
 * Created by Administrator on 2017/4/30.
 */

public class fragment_fangxing extends BaseFragment implements View.OnClickListener {
    private TextView buxian, one, two, three, four, five;


    private int i = 0;
    private View root;
    private onSelectedListener onSelectedListener;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_fangxing;
    }

    @Override
    public void initView(View view) {
        root = view;
        buxian = (TextView) view.findViewById(R.id.buxian);
        one = (TextView) view.findViewById(R.id.one);
        two = (TextView) view.findViewById(R.id.two);
        three = (TextView) view.findViewById(R.id.three);
        four = (TextView) view.findViewById(R.id.four);
        five = (TextView) view.findViewById(R.id.five);


        onSelectedListener = (com.maimaizu.Fragments.search_fragments.onSelectedListener) getActivity();
    }

    @Override
    public void setOnClick() {
        one.setOnClickListener(this);
        buxian.setOnClickListener(this);
        two.setOnClickListener(this);
        three.setOnClickListener(this);
        four.setOnClickListener(this);
        five.setOnClickListener(this);

        resetColor(buxian);
    }

    // TODO: 2017/4/30 重置颜色
    private void resetColor(View v) {
        if(i!= Integer.valueOf(v.getTag().toString())){
            TextView textView = (TextView) root.findViewWithTag(String.valueOf(i));
            textView.setTextColor(Color.BLACK);
            i = Integer.valueOf(v.getTag().toString()) ;
            ((TextView) v).setTextColor(ContextCompat.getColor(getActivity(), R.color.main_color));
            onSelectedListener.onFangXingSelected(((TextView) v).getText().toString());
        }
    }

    @Override
    public boolean setEventBus() {
        return false;
    }

    @Override
    public void doThings() {

    }

    @Override
    public void onClick(View view) {
        resetColor(view);
    }


}
