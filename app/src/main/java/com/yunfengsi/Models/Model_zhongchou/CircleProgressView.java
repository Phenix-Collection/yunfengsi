package com.yunfengsi.Models.Model_zhongchou;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.yunfengsi.R;

/**
 * Created by Administrator on 2016/9/9 0009.
 * 自定义圆环进度条
 */
public class CircleProgressView extends View {

    private static final String TAG = "CircleProgressBar";

    private int mMaxProgress = 100;//最大的进度条值

    private int mProgress = 0;//设置的进度条值

    private Object progress=0;//进度条的递增值

    //画圆所在的矩形区域
    private final RectF mRectF;

    private Paint mPaint;

    private String mTxHint1;

    private String mTxHint2;

    private String mTxCenterEnd="";//末尾文字

    private RollThread rollThread;//绘制线程

    private boolean isTrend=true;

    private boolean isIntText=true;
    public void setIsINT(boolean flag){
        this.isIntText=flag;
    }

    public CircleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);

        Context mContext = context;
        mRectF = new RectF();
        mPaint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width=this.getWidth();
        int height=this.getHeight();

        if (width!=height){
            int min=Math.min(width,height);
            width=min;
            height=min;
        }

        //设置画笔相关属性 圆环的颜色
        mPaint.setAntiAlias(true);//抗锯齿效果
        mPaint.setColor(Color.rgb(0xe9,0xe9,0xe9));
        canvas.drawColor(Color.TRANSPARENT);
        int mCircleLineStrokeWidth = 8;
        mPaint.setStrokeWidth(mCircleLineStrokeWidth);//设置画笔宽度
        mPaint.setStyle(Paint.Style.STROKE);
        //位置
        mRectF.left= mCircleLineStrokeWidth /2;//左上角x
        mRectF.top= mCircleLineStrokeWidth /2;//左上角y
        mRectF.right=width- mCircleLineStrokeWidth /2;//左下角x
        mRectF.bottom=height- mCircleLineStrokeWidth /2;//右下角y

        //绘制圆圈，进度条圆环的颜色
        //圆弧外轮廓矩形区域，起始角度，扫过的角度，true(绘制扇形),画板属性
        canvas.drawArc(mRectF,-90,360,false,mPaint);
        mPaint.setColor(getResources().getColor(R.color.main_color));
//        mPaint.setColor(getResources().getColor(R.color.colorPrimary));
        if (isTrend){//动态绘制
            canvas.drawArc(mRectF,-90,(((int) progress)*1.0f/mMaxProgress)*360,false,mPaint);
        }else {//静态绘制
            canvas.drawArc(mRectF,-90,(mProgress/mMaxProgress)*360,false,mPaint);
        }
        //绘制进度文案显示
        String text="";
        int    mTxtStrokeWidth = 2;
        mPaint.setStrokeWidth(mTxtStrokeWidth);
        if(isIntText){
            if(progress instanceof Integer){
                text=progress+mTxCenterEnd;
            }else if(progress instanceof  Double){
                text=(((Double) progress)+0)+mTxCenterEnd;
            }
        }else{
            text=progress+mTxCenterEnd;
        }
        int textHeight=height/4;
        mPaint.setTextSize(textHeight);
        int textWidth=(int)mPaint.measureText(text,0,text.length());
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawText(text,width/2-textWidth/2,height/2+textHeight/2,mPaint);

        if (!TextUtils.isEmpty(mTxHint1)){
            mPaint.setStrokeWidth(mTxtStrokeWidth);
            text=mTxHint1;
            textHeight=height/8;
            mPaint.setColor(Color.rgb(0x99,0x99,0x99));
            textWidth=(int)mPaint.measureText(text,0,text.length());
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawText(text,width/2-textWidth/2,height/4+textHeight/2,mPaint);
        }

        if (!TextUtils.isEmpty(mTxHint2)){
            mPaint.setStrokeWidth(mTxtStrokeWidth);
            text=mTxHint2;
            textHeight=height/8;
            mPaint.setTextSize(textHeight);
            textWidth=(int)mPaint.measureText(text,0,text.length());
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawText(text,width/2-textWidth/2,3*height/4+textHeight/2,mPaint);
        }
        if (isTrend&&rollThread==null){
            Log.e(TAG, "onDraw: 开始绘制线程"+"  isTrend+=-="+isTrend+"  rollThred=-=-=>"+rollThread +"  progeress=-=->"+progress
            +"   mProgress-=-=>"+mProgress);
            rollThread=new RollThread();
            rollThread.start();
        }
    }

    public int getmMaxProgress(){
        return mMaxProgress;
    }

    public void setMaxProgress(int maxProgress){
        this.mMaxProgress=maxProgress;
    }

    public void setProgress(int progress){
        this.mProgress=progress;
        this.invalidate();
    }

    public void setProgressNotInUiThread(int progress){
        this.mProgress=progress;
        this.postInvalidate();
    }

    public String getmTxtHint1(){
        return mTxHint1;
    }

    public void setmTxHint1(String mTxHint1){
        this.mTxHint1=mTxHint1;
    }

    public String getmTxHint2(){
        return mTxHint2;
    }

    public void setmTxHint2(String mTxHint2){
        this.mTxHint2=mTxHint2;
    }

    public void setDrawProgress(Object count){
        this.progress=count;
        this.invalidate();
    }

    public String getmTxCenterEnd() {
        return mTxCenterEnd;
    }

    public void setmTxCenterEnd(String mTxCenterEnd) {
        this.mTxCenterEnd = mTxCenterEnd;
    }

    //设置圆环进度条是否动态加载
    public void setTrendsProgress(boolean flag){
        isTrend=flag;
    }

    //开启线程绘制
    class RollThread extends Thread{
        private Object mPauseLock;//线程锁

        RollThread(){
            mPauseLock=new Object();
        }

        @Override
        public void run() {

            while (((int) progress)<mProgress){
                Log.i("Fund","progress-->"+progress);
                Log.i("Fund","mProgress-->"+mProgress);
                progress= ((int) progress)+1;
                postInvalidate();
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            rollThread=null;
        }
    }
}
