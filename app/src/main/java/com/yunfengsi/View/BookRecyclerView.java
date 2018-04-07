package com.yunfengsi.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * 作者：因陀罗网 on 2018/3/7 12:12
 * 公司：成都因陀罗网络科技有限公司
 */

public class BookRecyclerView extends RecyclerView {


    private Bitmap background;
    private Bitmap scaleBackground;



    public BookRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

    }

//
//    @Override
//    protected void dispatchDraw(Canvas canvas) {
//
//        int count = getChildCount();
////        int top = count > 0 ? getChildAt(0).getTop() : 0;
//        int top=0;
//        int backgroundWidth = background.getWidth();
//        int backgroundHeight = background.getHeight();
//        int width = getWidth();
//        int height = getHeight();
//        float scaleWidth=(float)width/backgroundWidth;
//        LogUtil.e(scaleWidth+"              dsfsdfdsfds");
//        if(scaleBackground==null){
//            itemHeight= (int) (backgroundWidth*scaleWidth);
//            Matrix matrix=new Matrix();
//            matrix.postScale(scaleWidth,scaleWidth);
//            scaleBackground=Bitmap.createBitmap(background,0,0,backgroundWidth,backgroundHeight,matrix,true);
//
//            background.recycle();
//        }
//
//
//        for (int y = top; y < height; y += scaleBackground.getHeight()) {
////            for (int x = 0; x < width; x += backgroundWidth) {
//                canvas.drawBitmap(scaleBackground,0, y, null);
////            }
//        }
//        super.dispatchDraw(canvas);
//    }
}
