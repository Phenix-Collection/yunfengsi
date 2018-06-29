package com.yunfengsi.View;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.yunfengsi.R;
import com.yunfengsi.Utils.LogUtil;

import java.util.ArrayList;
import java.util.Random;

/**
 * 作者：因陀罗网 on 2017/11/14 11:38
 * 公司：成都因陀罗网络科技有限公司
 */

public class RedRainView extends View {
    private Paint paint;
    private int count;//红包数量
    private int speed;//下落速度
    private float minSize, maxSize;//红包大小
    private boolean mustRealRed;
    private boolean clickAble;//是否都是真红包,是否可以重复点击红包,控件是否还可以点击
    private ValueAnimator animator;//控制红包动画
    private long preTime;//上一次动画结束时的时间，计算单次动画消耗时间
    private ArrayList<RedPacket> redPacketArrayList;//红包对象数列
    private int mWidth;//view宽度
    private int mImgIds[] = new int[]{R.drawable.hongbao_2};
    private onRedPacketClickListener onRedPacketClickListener;
    private Context context;

    private int reCount = 0;

    public void setOnRedPacketClickListener(onRedPacketClickListener onRedPacketClickListener) {
        this.onRedPacketClickListener = onRedPacketClickListener;
    }

    public RedRainView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RedRainView);
        count = typedArray.getInt(R.styleable.RedRainView_count, 20);
        speed = typedArray.getInt(R.styleable.RedRainView_speed, 100);
        minSize = typedArray.getFloat(R.styleable.RedRainView_minSize, 0.7f);
        maxSize = typedArray.getFloat(R.styleable.RedRainView_maxSize, 1.2f);
        mustRealRed = typedArray.getBoolean(R.styleable.RedRainView_mustRealRed, false);
        boolean reSelectable = typedArray.getBoolean(R.styleable.RedRainView_reSelectable, false);
        typedArray.recycle();
        init();
    }

    private void init() {
//        setBackgroundColor(Color.parseColor("#80000000"));
        clickAble = true;
        redPacketArrayList = new ArrayList<>();
        paint = new Paint();
        paint.setFilterBitmap(true);
        paint.setAntiAlias(true);
        animator = ValueAnimator.ofFloat(0, 1);
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
        initAnimator();
    }

    private void initAnimator() {
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                long nowTime = System.currentTimeMillis();
                float secs = (nowTime - preTime) / 1000f;
                preTime = nowTime;

                for (RedPacket redPacket : redPacketArrayList) {
                    redPacket.y += redPacket.speed * secs;
                    if (redPacket.y > getHeight()) {
//                        redPacket.y=-redPacket.height;
                        redPacket.resetRedPacket();
                    }
                    redPacket.rotation += redPacket.rotationSpeed * secs;
                }
                invalidate();
                LogUtil.e("动画循环" + reCount++);
            }
        });
        //属性动画无限循环
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());

        animator.setDuration(1);
    }

    public void setmImgIds(int imgs[]) {
        this.mImgIds = imgs;
    }

    //开始红包雨
    public void startRain() {
        clear();
        setRedpacketCount(count);
        preTime = System.currentTimeMillis();

        animator.start();
        LogUtil.e("开始红包雨");

    }

    /**
     * 停止动画
     */
    public void stopRainNow() {
        //清空红包数据
        clear();
        //重绘
        invalidate();
        //动画取消
        animator.cancel();
        LogUtil.e("手动取消红包动画");
    }

    //清空红包对象,回收红包中的bitmap
    public void clear() {
        for (RedPacket redPacket : redPacketArrayList) {
            redPacket.recycle();
        }
        redPacketArrayList.clear();

    }

    /**
     * 暂停红包雨
     */
    public void pauseRain() {
        animator.cancel();
    }

    /**
     * 重新开始
     */
    public void restartRain() {
        animator.start();
    }

    public void setRedpacketCount(int count) {
        if (mImgIds == null || mImgIds.length == 0)
            return;
        for (int i = 0; i < count; ++i) {
            //获取红包原始图片
//            Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), mImgIds[i % mImgIds.length]);
//            Bitmap originalBitmap = ImageUtil.readBitMap(mApplication.getInstance(), mImgIds[i % mImgIds.length]);
            Glide.with(context).load(mImgIds[i % mImgIds.length])
                    .asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    //生成红包实体类
                    RedPacket redPacket = new RedPacket(getContext(), resource, speed, maxSize, minSize, mWidth, mustRealRed);
                    //添加进入红包数组
                    redPacketArrayList.add(redPacket);
                }
            });

        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // TODO: 2017/11/14 获取自定义的宽度
        mWidth = getMeasuredWidth();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                RedPacket redPacket = isRedPacketClick(event.getX(), event.getY());
                if (redPacket != null) {
                    redPacket.resetRedPacket();
                    if (onRedPacketClickListener != null) {
                        onRedPacketClickListener.onRePacketClick(redPacket);
                    }
//                    if(!reSelectable){
//                        stopRainNow();
//                    }
                    clickAble = false;
//                    setBackgroundColor(Color.parseColor("#00000000"));
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return clickAble;
    }

    private RedPacket isRedPacketClick(float x, float y) {
        for (RedPacket redPacket : redPacketArrayList) {
            if (redPacket.isContains(x, y)) {
                return redPacket;
            }
        }
        return null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (RedPacket redPacket : redPacketArrayList) {
            //将红包旋转redPacket.rotation角度后 移动到（redPacket.x，redPacket.y）进行绘制红包
            Matrix matrix = new Matrix();
            matrix.setTranslate(-redPacket.width / 2, -redPacket.height / 2);
            matrix.postRotate(redPacket.rotation);
            matrix.postTranslate(redPacket.width / 2 + redPacket.x, redPacket.height / 2 + redPacket.y);
            canvas.drawBitmap(redPacket.bitmap, matrix, paint);
        }
    }

    /*
            红包类
             */
    public static class RedPacket {
        private Context context;
        public float x, y;
        public float rotation;
        public float speed;
        public float rotationSpeed;
        public int width, height, viewWidth;
        public Bitmap bitmap;
        public int money;
        public boolean isRealRed, mustRealRed;

        public void resetRedPacket() {
            int mWidth = viewWidth == 0 ? context.getResources().getDisplayMetrics().widthPixels : viewWidth;
            Random random = new Random();
            //红包起始位置x:[0,mWidth-width]
            int rx = random.nextInt(mWidth) - width;
            x = rx <= 0 ? 0 : rx;
            //红包起始位置y
            y = -height;
            //初始化该红包的下落速度
            speed = 200;
            this.speed = speed + (float) Math.random() * 1000;
            //初始化该红包的初始旋转角度
            rotation = (float) Math.random() * 180 - 90;
            //初始化该红包的旋转速度
            rotationSpeed = (float) Math.random() * 90 - 45;
            //初始化是否为中奖红包
            if (mustRealRed) {
                isRealRed = true;
            } else {
                isRealRed = isRealRedPacket();
            }
        }

        public RedPacket(Context context, Bitmap originalBitmap, int speed, float maxSize, float minSize, int viewWidth, boolean mustRealRed) {
            super();
            this.context = context;
            this.viewWidth = viewWidth;
            this.mustRealRed = mustRealRed;
            double widthRandom = Math.random();
            if (widthRandom < minSize || widthRandom > maxSize) {
                widthRandom = maxSize;
            }
            width = (int) (originalBitmap.getWidth() * widthRandom);
            height = width * originalBitmap.getHeight() / originalBitmap.getWidth();
            if (originalBitmap.getWidth() == width && originalBitmap.getHeight() == height) {
                bitmap = originalBitmap;
            } else {
                bitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, true);
//                originalBitmap.recycle();
            }

            int mWidth = viewWidth == 0 ? context.getResources().getDisplayMetrics().widthPixels : viewWidth;
            Random random = new Random();
            //红包起始位置x:[0,mWidth-width]
            int rx = random.nextInt(mWidth) - width;
            x = rx <= 0 ? 0 : rx;
            //红包起始位置y
            y = -height;
            //初始化该红包的下落速度
            this.speed = speed + (float) Math.random() * 1000;
            //初始化该红包的初始旋转角度
            rotation = (float) Math.random() * 180 - 90;
            //初始化该红包的旋转速度
            rotationSpeed = (float) Math.random() * 90 - 45;
            //初始化是否为中奖红包
            if (mustRealRed) {
                isRealRed = true;
            } else {
                isRealRed = isRealRedPacket();
            }

        }

        public boolean isContains(float x, float y) {
            return this.x - 50 <= x
                    && this.y - 50 <= y
                    && this.x + 50 + width > x
                    && this.y + 50 + height > y;
        }

        /**
         * 随机 是否为中奖红包
         */
        public boolean isRealRedPacket() {
            Random random = new Random();
            int num = random.nextInt(10) + 1;
            //如果[1,10]随机出的数字是2的倍数 为中奖红包
            if (num % 2 == 0) {
                money = num * 2;//中奖金额
                return true;
            }
            return false;
        }

        /**
         * 回收图片
         */
        public void recycle() {
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
    }

    public interface onRedPacketClickListener {
        void onRePacketClick(RedPacket redPacket);
    }
}
