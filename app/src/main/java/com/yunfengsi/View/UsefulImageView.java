package com.yunfengsi.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;

/**
 * 作者：因陀罗网 on 2018/5/26 11:28
 * 公司：成都因陀罗网络科技有限公司
 */
public class UsefulImageView extends android.support.v7.widget.AppCompatImageView {
    private static final int FLAG_MODE_PRESSED = 0x00000001;//是否开启触摸反馈模式
    private static final int FLAG_MODE_CIRCLE  = 0x00000002;//是否开启圆形模式


    private int mFlags;

    private ColorMatrix            colorMatrix;//颜色暗化矩阵
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
        mFlags |= FLAG_MODE_PRESSED;//默认开启按钮反馈
//        mFlags |= FLAG_MODE_CIRCLE;//默认开启圆形按钮模式


    }

    private void refreshCircleDrawable() {
        if (getDrawable() != null) {
            Drawable drawable = getDrawable();
            Bitmap   bitmap   = null;
            if (drawable instanceof CircleImageDrawable) {

            } else if (drawable instanceof GlideBitmapDrawable) {
                bitmap = ((GlideBitmapDrawable) drawable).getBitmap();

            } else {
                bitmap = ((BitmapDrawable) drawable).getBitmap();
            }
            if (bitmap != null) {
                CircleImageDrawable circleImageDrawable = new CircleImageDrawable(bitmap);
                setImageDrawable(circleImageDrawable);
            }
        }
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
        if ((mFlags & FLAG_MODE_CIRCLE) == FLAG_MODE_CIRCLE) {
//            refreshCircleDrawable();
        }
    }

    public boolean AllowPressedFeedback(boolean allow) {
        if (!allow) {
            mFlags &= ~FLAG_MODE_PRESSED;
        } else {
            mFlags |= FLAG_MODE_PRESSED;
        }
        return allow;
    }

    public boolean isCircle(boolean flag) {
        if (flag) {
            mFlags |= FLAG_MODE_CIRCLE;
        } else {
            mFlags &= ~FLAG_MODE_CIRCLE;
        }
        return flag;
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
        ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);
        setColorFilter(colorFilter);

    }

    private void clearFilter() {
        setColorFilter(null);
//        colorMatrix.setScale(1f, 1f, 1f, 1);
//        colorFilter = new ColorMatrixColorFilter(colorMatrix);
//        setColorFilter(colorFilter);
    }

    @Override
    public boolean isInEditMode() {
        return true;
    }

    /**
     * 圆形图片处理
     */
    public static class CircleImageDrawable extends Drawable {
        private Paint  paint;
        private Bitmap bitmap;
        private int    radius2, radius;

        public CircleImageDrawable(Bitmap bitmap) {
            paint = new Paint();
            paint.setFilterBitmap(true);
            paint.setAntiAlias(true);
            this.bitmap = bitmap;
            BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            radius2 = Math.min(bitmap.getWidth(), bitmap.getHeight());
            radius = radius2 >> 1;
            paint.setShader(bitmapShader);
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            canvas.drawCircle(radius, radius, radius, paint);
        }

        @Override
        public int getIntrinsicHeight() {
            return radius2;
        }

        @Override
        public int getIntrinsicWidth() {
            return radius2;
        }

        @Override
        public void setAlpha(int alpha) {
            paint.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {
            paint.setColorFilter(colorFilter);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }


    }
}
