package com.yunfengsi.View;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.yunfengsi.R;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.LogUtil;

/**
 * 作者：因陀罗网 on 2018/3/16 11:55
 * 公司：成都因陀罗网络科技有限公司
 */

public class TestTextView extends TextView{
    private TextPaint textPaint;
    private int screenWidth;
    private int Chars;//一行的字符数；
    public TestTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        textPaint=this.getPaint();
        textPaint.setSubpixelText(true);//使用亚像素显示，效果更好
        textPaint.setFakeBoldText(true);//仿粗体   比bold细一点
        textPaint.getFontSpacing();//字符行间距？？？
        textPaint.getFontMetricsInt(textPaint.getFontMetricsInt());//文字间距：
        screenWidth=context.getResources().getDisplayMetrics().widthPixels;
        textPaint.setShadowLayer(10,10,10, ContextCompat.getColor(context, R.color.main_color));
        float [] measuredwidth=new float[1024];
        Chars=textPaint.breakText(getText().toString(),true,screenWidth,measuredwidth);
        LogUtil.e("字符行间距：："+textPaint.getFontSpacing()
                +"\n"+"文字间距：："+textPaint.getFontMetricsInt(textPaint.getFontMetricsInt())
                +"\n"+"每行最大字数：："+Chars
                +"\n"+"实际测量值：："+measuredwidth[0]+"  "+measuredwidth[1]
                +"\n"+"字符间距：：："+getLineSpacingExtra()
                +"\n"+"行高：："+getLineHeight()
                +"\n"+textPaint.getTextSize()*Chars


        );


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        int i=(getResources().getDisplayMetrics().heightPixels- DimenUtils.dip2px(getContext(),70))
                /getLineHeight();
        if(getText().toString().length()>=i*Chars){
            setText(getText().toString().substring(0,i*Chars));
            LogUtil.e("缩减字数   行数：：："+i);
        }

    }
}
