package com.qianfujiaoyu.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Administrator on 2017/4/26.
 */

public class mItemDecoration extends RecyclerView.ItemDecoration {
    private Context context;
    private Paint paint;

    public mItemDecoration(Context context) {
        super();
        this.context = context;
        paint = new Paint();
        paint.setColor(Color.parseColor("#eeeeee"));
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getChildCount();
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        for (int i = 0; i < childCount; i++) {
//            if(i==0){
//                continue;
//            }
            View view = parent.getChildAt(i);
            if(view instanceof TextView &&childCount==1){
                break;
            }

            float top = view.getBottom();
            float bottom = view.getBottom() + 2;
            c.drawRect(left, top, right, bottom, paint);

        }

    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = 2;
    }
}
