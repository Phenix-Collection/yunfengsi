package com.yunfengsi.Models.YunDou;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunfengsi.R;
import com.yunfengsi.Utils.ImageUtil;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.TimeUtils;
import com.yunfengsi.Utils.mApplication;

import java.text.SimpleDateFormat;

/**
 * 作者：因陀罗网 on 2018/5/22 17:42
 * 公司：成都因陀罗网络科技有限公司
 */
public class QiFu_PaiWei_Detail extends AppCompatActivity {
    private ImageView imageView;
    private String    address;
    private String person;
    private String time;//祈福  或者  牌位
    private Bitmap originalSrc;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.qifu_paiwei_quan_detail);

        String type = getIntent().getStringExtra("type");
        address = getIntent().getStringExtra("address");
        person = getIntent().getStringExtra("person");
        time = getIntent().getStringExtra("time");
        ((ImageView) findViewById(R.id.title_back)).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.title_title)).setText(mApplication.ST((type.equals("3") ? "我的祈福" : "我的牌位")));
        ((ImageView) findViewById(R.id.title_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if (type.equals("3")) {//祈福券
            originalSrc = ImageUtil.readBitMap(this, R.drawable.qifu);
        } else if (type.equals("2")) {//牌位券
            originalSrc = ImageUtil.readBitMap(this, R.drawable.paiwei);
        }
        imageView = findViewById(R.id.image);
        imageView.setImageBitmap(originalSrc);
        Bitmap.Config config = originalSrc.getConfig();

        final Bitmap newSrc = originalSrc.copy(config, true);
        final Canvas canvas = new Canvas(newSrc);
        imageView.post(new Runnable() {
            @Override
            public void run() {
                canvas.save();
                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint.setFilterBitmap(true);
                paint.setColor(Color.BLACK);
                paint.setAntiAlias(true);
                int   defWidth    = 432;//图片宽高   固定
                int   defHeight   = 1240;
                int   imageHeight = originalSrc.getHeight();
                int   imageWidth  = originalSrc.getWidth();
                float scale       = getResources().getDisplayMetrics().density;
                float left        = (float) (imageWidth * 120d / 432d);
                float top         = (float) (imageHeight * 570d / 1240d);
                float bottom      = (float) (imageHeight * 820d / 1240d);
                float right       = (float) (imageWidth * 310d / 432d);
                Rect  rect        = new Rect();


                int textSize = calculateTextSize(top, bottom,0);//初始化textsize  每次绘制都需要初始化
                drawCenterDoubleLine(paint, left, top, right, rect, textSize, canvas);//绘制中间两列或一列


                textSize=calculateTextSize(top, bottom,address.length());//初始化textsize  每次绘制都需要初始化

                drawAddress(paint, top, right, rect, textSize, canvas);



                drawTime(paint, left, top, bottom, textSize, canvas);

                canvas.restore();
                imageView.setImageBitmap(newSrc);


                LogUtil.e("高度：：；" + imageHeight + "  宽度：：" + originalSrc.getWidth());
                LogUtil.e("矩形各点位：：；" + left + "    " + top + "   " + bottom + "   " + right);
                LogUtil.e("矩形高宽：：；" + rect.height() + "    " + rect.width());
            }
        });


    }

    private void drawTime(Paint paint, float left, float top, float bottom, int textSize, Canvas canvas) {
        SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy年M月d日");
        paint.setTextSize(textSize);
        String           t    = TimeUtils.getChinneseTime(TimeUtils.getStrTime_spe(sdf, time, sdf2));
        textSize=calculateTextSize(top, bottom,t.length());//初始化textsize  每次绘制都需要初始化
        LogUtil.e("当前绘制时间的字体大小：：："+textSize);
        for (int i = 0; i < t.length(); i++) {
            String s=String.valueOf(t.toCharArray()[i]);

            canvas.drawText(s, left, top + i * (textSize + 5), paint);
        }
    }

    private void drawAddress(Paint paint, float top, float right, Rect rect, int textSize, Canvas canvas) {
        paint.setTextSize(textSize);
        for (int i = 0; i < address.length(); i++) {
            String s=String.valueOf(address.toCharArray()[i]);
//            int    space = calculateSpace(paint, rect, textSize, s);//处理英文字母不居中问题

            canvas.drawText(s, (right - rect.width()), top + i * (textSize + 5), paint);
        }
    }

    private void drawCenterDoubleLine(Paint paint, float left, float top, float right, Rect rect, int textSize, Canvas canvas) {
        int l1       =person.length();
        paint.setTextSize(textSize);
        for (int i = 0; i <l1; i++) {
            String s     =String.valueOf(person.toCharArray()[i]);
            int    space = calculateSpace(paint, rect, textSize, s);//处理英文字母不居中问题
            if(l1>5){//大于5字  两列显示
                if(i<5){
                    canvas.drawText(s, (left + (right - left) / 2) +7+space, top + 25 + i * (textSize + 5), paint);
                }else{
                    canvas.drawText(s, (left + (right - left) / 2) -3-rect.width()-space, top + 25 + (i-5) * (textSize + 5), paint);
                }
            }else{//小于5个字  居中绘制
                canvas.drawText(s, (left + (right - left) / 2) - rect.width() / 2+space, top + 25 + i * (textSize + 5), paint);
            }

        }
    }

    private int calculateSpace(Paint paint, Rect rect, int textSize, String s) {
        paint.getTextBounds(s,0,1,rect);

        int space=0;
        if(rect.width()<textSize*2/3){//处理英文字母不居中的问题   为space赋值填充
            space=(textSize>>1)-(rect.width()>>1);
        }
        return space;
    }

    private int calculateTextSize(float top, float bottom,int length) {
        int defSize  = 20;
        int textSize = defSize;
        int space    =5;
        if(length==0){
            while ((textSize+space) *5> (bottom - top-50)) {//最大10个字  5个字换行  从右到左  上下各留白25
                textSize--;
//                if(space>0) space--;
            }
        }
        else {
            LogUtil.e("左边：："+length);
            LogUtil.e("右边：："+ (bottom - top));
            while ((textSize+space) *length> (bottom - top)) {//
                textSize--;
//                if(space>0) space--;
            }
        }

        return textSize;
    }
}
