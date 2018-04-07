package com.yunfengsi.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.yunfengsi.R;
import com.yunfengsi.Utils.LogUtil;

/**
 * 作者：因陀罗网 on 2018/3/7 13:54
 * 公司：成都因陀罗网络科技有限公司
 */

public class BookDecoration extends RecyclerView.ItemDecoration {


    private Bitmap background;


    private Bitmap scaleBackground;
    public BookDecoration(Context context) {
        super();

        background = BitmapFactory.decodeResource(context.getResources(), R.drawable.book_shelf_cell);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        LogUtil.e("开始画线");
        int width = parent.getWidth();
        int backgroundWidth = background.getWidth();
        int backgroundHeight = background.getHeight();
        float scaleWidth = (float) width / backgroundWidth;
        if (scaleBackground == null) {
//            itemHeight= (int) (backgroundWidth*scaleWidth);
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleWidth);
            scaleBackground = Bitmap.createBitmap(background, 0, 0, backgroundWidth, backgroundHeight, matrix, true);

            background.recycle();
        }
        int count = parent.getChildCount();
        int defaultHeights=0;
        for (int y = 0; y < parent.getHeight(); y += scaleBackground.getHeight()) {
            defaultHeights++;
            c.drawBitmap(scaleBackground, 0, y, null);
        }
        for (int i = defaultHeights*3; i < count; i+=3) {
            View view=parent.getChildAt(i);
            c.drawBitmap(scaleBackground, 0, view.getTop(), null);
        }

    }
}
