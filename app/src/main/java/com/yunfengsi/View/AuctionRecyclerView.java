package com.yunfengsi.View;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * 作者：luZheng on 2018/06/14 14:42
 */
public class AuctionRecyclerView extends RecyclerView {


    public AuctionRecyclerView(Context context) {
        this(context,null);
    }

    public AuctionRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);

    }

    public AuctionRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }



}
