package com.yunfengsi.View;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

import com.yunfengsi.Utils.LogUtil;

/**
 * 作者：luZheng on 2018/06/09 15:07
 */
public class IOverScrollView extends ScrollView {

    private int  mMaxOverScrollY=200;
    float density;
    public IOverScrollView(Context context) {
        this(context,null);
    }

    public IOverScrollView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public IOverScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        density=context.getResources().getDisplayMetrics().density;
        mMaxOverScrollY=Math.round(density*mMaxOverScrollY);
        LogUtil.e("距离：："+mMaxOverScrollY+"    "+density);
    }
    public void setmMaxOverScrollY(int px){
        mMaxOverScrollY=Math.round(density*px);
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        LogUtil.e(+scrollX+"   "+scrollY+"   "+clampedX+"    "+clampedY);

    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        LogUtil.e("deltaX: "+deltaX+"   deltaY:    "+deltaY+"      "+mMaxOverScrollY);

        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, mMaxOverScrollY, isTouchEvent);
    }


    @Override
    public boolean canScrollVertically(int direction) {
        return super.canScrollVertically(direction);
    }
}
