package com.yunfengsi.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.widget.GridView;

import com.yunfengsi.R;

public class MyGridView extends GridView {
  
    private Bitmap background;
    private Bitmap scaleBackground;
    public int itemHeight;
    public MyGridView(Context context, AttributeSet attrs) {
        super(context, attrs);  
        background = BitmapFactory.decodeResource(getResources(),
                R.drawable.book_shelf_cell);
    }  
  
    @Override  
    protected void dispatchDraw(Canvas canvas) {
        int count = getChildCount();  
        int top = 0;
        int backgroundWidth = background.getWidth();  
        int backgroundHeight = background.getHeight();
        int width = getWidth();  
        int height = getHeight();
        float scaleWidth=(float)width/backgroundWidth;
        if(scaleBackground==null){
            itemHeight= (int) (backgroundHeight*scaleWidth);
            Matrix matrix=new Matrix();
            matrix.postScale(scaleWidth,scaleWidth);
            scaleBackground=Bitmap.createBitmap(background,0,0,backgroundWidth,backgroundHeight,matrix,true);
//            scaleBackground=Bitmap.createScaledBitmap(background,width, (int) (backgroundHeight*scaleWidth),true);
            background.recycle();
        }
        for (int y = top; y < height; y += scaleBackground.getHeight()) {
//            for (int x = 0; x < width; x += backgroundWidth) {
                canvas.drawBitmap(scaleBackground, 0, y, null);
//            }
        }  
  
        super.dispatchDraw(canvas);  
    }  
  
}  