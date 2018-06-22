package com.yunfengsi.View;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

import com.yunfengsi.Utils.LogUtil;

/**
 * 作者：luZheng on 2018/06/22 17:49
 */
public class IAuctionRecyclerView extends RecyclerView {

    ViewGroup scrollView;
    private float lastY;
    public IAuctionRecyclerView(Context context) {
        this(context,null);
    }

    public IAuctionRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public IAuctionRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        scrollView = (ViewGroup) getParent().getParent();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {
        LogUtil.e("dispatchTouchEvent::::    "+e.getAction());
        if(e.getAction()==MotionEvent.ACTION_DOWN){
            lastY=e.getY();
            if(canScrollVertically(1)){
                if(scrollView instanceof NestedScrollView){
                    scrollView.requestDisallowInterceptTouchEvent(true);
                }
            }

        }else if(e.getAction()==MotionEvent.ACTION_UP){
            if(scrollView instanceof NestedScrollView){
                scrollView.requestDisallowInterceptTouchEvent(false);
            }
        }else if(e.getAction()==MotionEvent.ACTION_MOVE){
            if(e.getY()>lastY){
                if(canScrollVertically(-1)){
                    //如果手指向下滑 并且可以往上滑   阻止scrollview拦截事件
                    if(scrollView instanceof NestedScrollView){
                        scrollView.requestDisallowInterceptTouchEvent(true);
                    }
                }
            }
            lastY=e.getY();
        }
        return super.dispatchTouchEvent(e);
    }



    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        LogUtil.e("滑动状态：：："+state);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        LogUtil.e("onScrollChanged：：："+t);

    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
        LogUtil.e("dy:::::::    "+dy);
    }
}
