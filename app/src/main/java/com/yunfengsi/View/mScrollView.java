package com.yunfengsi.View;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by Administrator on 2016/6/8.
 */
public class mScrollView extends ScrollView {
    public interface  onIScrollChangedListener{
        void onScrollChanged(int l, int t, int oldl, int oldt) ;
    }

    onIScrollChangedListener listener;
    public mScrollView(Context context) {
        this(context,null);

    }

    public mScrollView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public mScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    public void setListener(onIScrollChangedListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if(listener!=null){
            listener.onScrollChanged(l,t,oldl,oldt);
        }
    }
}
