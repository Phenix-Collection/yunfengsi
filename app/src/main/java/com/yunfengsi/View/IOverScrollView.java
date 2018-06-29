package com.yunfengsi.View;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yunfengsi.Utils.LogUtil;

/**
 * 作者：luZheng on 2018/06/09 15:07
 */
public class IOverScrollView extends LinearLayout {

    private int mMaxOverScrollY = 200;
    float density;

    private float mLastY;
    private int tipHight=100;
    private Context context;


    private View front;
    public IOverScrollView(Context context) {
        this(context, null);
    }

    public IOverScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IOverScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(LinearLayout.VERTICAL);
        this.context=context;
        density = context.getResources().getDisplayMetrics().density;
        mMaxOverScrollY = Math.round(density * mMaxOverScrollY);
        LogUtil.e("距离：：" + mMaxOverScrollY + "    " + density);
        tipHight=Math.round(density*tipHight);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        LogUtil.e("完成初始化");
        front=getChildAt(0);
        TextView tip=new TextView(context);
        tip.setTextColor(Color.BLACK);
        tip.setGravity(Gravity.CENTER);
        tip.setTextSize(18);
        addView(tip,1,new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,tipHight));
        String tips = "上拉加载图文详情";
        tip.setText(tips);

    }

    public void setmMaxOverScrollY(int px) {
        mMaxOverScrollY = Math.round(density * px);
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        LogUtil.e(+scrollX + "   " + scrollY + "   " + clampedX + "    " + clampedY);

    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        LogUtil.e("deltaX: " + deltaX + "   deltaY:    " + deltaY + "      " + mMaxOverScrollY);

        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, mMaxOverScrollY, isTouchEvent);
    }


    @Override
    public boolean canScrollVertically(int direction) {
        return super.canScrollVertically(direction);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        LogUtil.e("当前：Y:::"+ev.getY()+"    当前scrollY：：："+getScrollY());
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY=ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                scrollBy(0, -(int) (ev.getY()-mLastY));
                mLastY=ev.getY();
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        LogUtil.e(String.format("滑动中：：%d   %d   %d   %d",l,t,oldl,oldt));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        LogUtil.e("正在测量");
        int width=MeasureSpec.getSize(widthMeasureSpec);
        int height=MeasureSpec.getSize(heightMeasureSpec);
        measureFront(front);
    }

    private void measureFront(View view) {
//        if(view instanceof ViewGroup){
//            int count=((ViewGroup) view).getChildCount();
//            ViewGroup viewGroup= (ViewGroup) view;
//            for(int i=0;i<count;i++){
//               View fChild =viewGroup.getChildAt(i);
//               fChild=
//            }
//        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        LogUtil.e("正在排列"+front.getMeasuredHeight());
       int c=getChildCount();
       if(getChildCount()>3){
           try {
               throw  new Exception("the layout just can contains 3 children");
           } catch (Exception e) {
               e.printStackTrace();
           }
       }

       front.layout(getLeft(),getTop(),getRight(),front.getMeasuredHeight());
//       TextView tip = (TextView) getChildAt(1);
//
//       tip.layout(getLeft(),getBottom(),getRight(),tipHight);


    }
}
