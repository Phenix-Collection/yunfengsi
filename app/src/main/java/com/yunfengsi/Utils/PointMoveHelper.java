package com.yunfengsi.Utils;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;

import java.lang.ref.WeakReference;


/**
 * 作者：因陀罗网 on 2017/6/19 16:33
 * 公司：成都因陀罗网络科技有限公司
 */

public class PointMoveHelper {
    private static final String TAG = "PointMoveHelper";
    private View  mView; //需要滑动的View
    private float
            downRawX, downRawY;
    //手指和view远点的距离    手指的原点
    private float perRawDistanceX, perRawDistanceY;
    private long          downTime;
    private Activity      activity;
    private ValueAnimator va;
    private ViewGroup unMoveableView;
    private int marginRight  = 0;//dp
    private int marginLeft   = 0;//dp
    private int marginTop    = 0;//dp
    private int marginBottom = 0;//dp

    public PointMoveHelper(Activity activity, View view) {
        super();
        WeakReference<Activity> a = new WeakReference<Activity>(activity);
        this.activity = a.get();
        mView = view;
        init();
    }

    public PointMoveHelper setViewUnMoveable(ViewGroup unMoveableView) {
        this.unMoveableView = unMoveableView;//// TODO: 2016/12/12 禁止拦截事件 ,由子控件处理事件
        return this;
    }

    /**
     * 单位Dp
     *
     * @param marginTop
     * @param marginLeft
     * @param marginRight
     * @param marginBottom
     * @return 距离屏幕最小上边距，左边距，右边距，下边距
     */
    public PointMoveHelper setMargins(int marginTop, int marginLeft, int marginRight, int marginBottom) {
        this.marginTop = DimenUtils.dip2px(activity,marginTop);
        this.marginLeft = DimenUtils.dip2px(activity,marginLeft);
        this.marginRight = DimenUtils.dip2px(activity,marginRight);
        this.marginBottom = DimenUtils.dip2px(activity,marginBottom);
        return this;
    }

    private void init() {

        int screenWidth  = activity.getResources().getDisplayMetrics().widthPixels;
        int screenHeight = activity.getResources().getDisplayMetrics().heightPixels;
        mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, MotionEvent event) {


                float tranX=v.getTranslationX();
                float tranY=v.getTranslationY();


                LogUtil.e("\n"+
                        "downRawX：：："+downRawX+"\n"+
                        "downRawY：：："+downRawY+"\n"+
                        "viewRawX：：："+perRawDistanceX+"\n"+
                        "viewRawY：：："+perRawDistanceY+"\n"+
                        "translationX：：："+tranX+"\n"+
                        "translationY：：："+tranY);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downTime = System.currentTimeMillis();
                        downRawX=event.getRawX();
                        downRawY=event.getRawY();
                        /**
                         * 禁止有滑动冲突的控件拦截时间
                         * 一般为父控件
                         */
                        if (unMoveableView != null) {
                            unMoveableView.requestDisallowInterceptTouchEvent(true);
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        perRawDistanceX = event.getRawX()-downRawX;
                        perRawDistanceY =event.getRawY()-downRawY;
                        downRawX = event.getRawX();
                        downRawY = event.getRawY();
                        moveFllow(v, perRawDistanceX, perRawDistanceY, (View) mView.getParent());//// TODO: 2016/12/12 跟随手指滑动
                        break;
                    case MotionEvent.ACTION_UP:
                        if (unMoveableView != null) {
                            unMoveableView.requestDisallowInterceptTouchEvent(false);
                        }
                        checkHozatal(v);//// TODO: 2016/12/12判断左右边距
                        if (System.currentTimeMillis() - downTime <= ViewConfiguration.getTapTimeout()) {
                            v.performClick();
                        }
                        break;
                }
                return true;
            }
        });
    }


    /**
     * 以屏幕左右边 作为轴线
     */
    private void checkHozatal(final View v) {
        final View parent = (View) v.getParent();

        Log.w(TAG, "onTouch: " + parent+"    X:::"+v.getLeft());
        if (v.getX()<= parent.getWidth() / 2) {
            va = ValueAnimator.ofFloat(v.getX(),marginLeft);
            va.setDuration(500);
            va.setInterpolator(new BounceInterpolator());
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    v.setX((Float) animation.getAnimatedValue());
                    if (v.getX() <= marginLeft) {
                        v.setX(marginLeft);
//                        v.setSelected(false);
                        va.cancel();
                    }
                }
            });
        } else {
            va=ValueAnimator.ofFloat(parent.getWidth()-v.getX()-v.getWidth(),marginRight);
            va.setDuration(500);
            va.setInterpolator(new BounceInterpolator());
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    v.setX(parent.getWidth()-(Float) animation.getAnimatedValue()-v.getWidth());
                    if (v.getX() >= parent.getWidth() - marginRight - v.getWidth()) {
                        v.setX(parent.getWidth() - marginRight - v.getWidth());
//                        v.setSelected(false);
                        va.cancel();
                    }
                }
            });
        }
        va.start();
    }

    /**
     * 父布局为屏幕宽高或者父布局的父布局为屏幕宽高最好
     * @param v
     * @param viewRawX
     * @param viewRawY
     * @param parent
     */
    private void moveFllow(View v, float viewRawX, float viewRawY, View parent) {
        LogUtil.e("父布局的Y："+parent.getY()+"   高度：：；"+parent.getHeight());
        if (v.getY() >= marginTop && v.getY() <= parent.getHeight() - marginBottom - v.getHeight()) {
            v.setTranslationY(v.getTranslationY()+viewRawY);
            /**
             * property amend   属性修正
             */
            if (v.getY() <marginTop) {
                v.setY(marginTop);
            } else if (v.getY() >parent.getHeight() - marginBottom - v.getHeight()) {
                v.setY( parent.getHeight() - marginBottom- v.getHeight());
            }
        }

        if (v.getX() >= marginLeft && v.getX() <= (parent.getWidth() -marginRight- v.getWidth())) {
            v.setTranslationX(v.getTranslationX()+viewRawX);
            /**
             * property amend   属性修正
             */
            if (v.getX() < marginLeft) {
                v.setX(marginLeft);
            } else if (v.getX() > (parent.getWidth()  - marginRight - v.getWidth())) {
                v.setX((parent.getWidth() - marginRight - v.getWidth()));
            }
        }
    }

}
