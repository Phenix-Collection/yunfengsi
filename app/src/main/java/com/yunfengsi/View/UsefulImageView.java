package com.yunfengsi.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 作者：因陀罗网 on 2018/5/26 11:28
 * 公司：成都因陀罗网络科技有限公司
 */
public class UsefulImageView extends android.support.v7.widget.AppCompatImageView {
    private static final int FLAG_MODE_PRESSED = 0x00000001;//是否是触摸反馈模式
    private static final int FLAG_MODE_CIRCLE = 0x00000002;//是否是圆形模式


    private int mFlags;

    private ColorMatrix            colorMatrix;//颜色暗化矩阵
    private Matrix                 matrix;//缩放矩阵
    private ColorMatrixColorFilter colorFilter;
//    GestureDetector gestureDetector;

    public UsefulImageView(Context context) {
        this(context, null);
    }

    public UsefulImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UsefulImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        colorMatrix = new ColorMatrix();
        matrix=new Matrix();
        mFlags |= FLAG_MODE_PRESSED;//默认开启按钮反馈
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

    }

    private void AllowPressedFeedback(boolean allow) {
        if (!allow) {
            mFlags &= ~FLAG_MODE_PRESSED;
        } else {
            mFlags |= FLAG_MODE_PRESSED;
        }
    }

    private void isCircle(boolean flag){
        if(flag){
            mFlags |= FLAG_MODE_CIRCLE;
        }else{
            mFlags &= ~FLAG_MODE_CIRCLE;
        }
    }

    @Override
    public void setPressed(boolean pressed) {
        if ((mFlags & FLAG_MODE_PRESSED) == FLAG_MODE_PRESSED) {
            if (pressed) {
                darkenImage();
            } else {
                clearFilter();
            }
        }

        super.setPressed(pressed);
    }

    private void darkenImage() {
        colorMatrix.setScale(0.6f, 0.6f, 0.6f, 1);
        colorFilter = new ColorMatrixColorFilter(colorMatrix);
        setColorFilter(colorFilter);

    }

    private void clearFilter() {
        colorMatrix.setScale(1f, 1f, 1f, 1);
        colorFilter = new ColorMatrixColorFilter(colorMatrix);
        setColorFilter(colorFilter);
    }

    @Override
    public boolean isInEditMode() {
        return true;
    }
}
