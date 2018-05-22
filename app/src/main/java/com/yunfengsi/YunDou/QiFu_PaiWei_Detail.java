package com.yunfengsi.YunDou;

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
    private String type,address,person,time;//祈福  或者  牌位
    private Bitmap originalSrc;
    private int defSize=20;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.qifu_paiwei_quan_detail);

        type=getIntent().getStringExtra("type");
        address=getIntent().getStringExtra("address");
        person=getIntent().getStringExtra("person");
        time=getIntent().getStringExtra("time");
        ((ImageView) findViewById(R.id.title_back)).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.title_title)).setText(mApplication.ST((type.equals("2")?"我的祈福":"我的牌位")));
        ((ImageView) findViewById(R.id.title_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if(type.equals("2")){//祈福券
            originalSrc= ImageUtil.readBitMap(this,R.drawable.qifu);
        }else if(type.equals("3")){//牌位券
            originalSrc= ImageUtil.readBitMap(this,R.drawable.paiwei);
        }
        imageView=findViewById(R.id.image);
        imageView.setImageBitmap(originalSrc);
        Bitmap.Config config=originalSrc.getConfig();

        final Bitmap newSrc =originalSrc.copy(config,true);
        final Canvas canvas =new Canvas(newSrc);
        imageView.post(new Runnable() {
            @Override
            public void run() {
                Paint paint       = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint.setFilterBitmap(true);
                paint.setColor(Color.BLACK);
                paint.setAntiAlias(true);
                int   defWidth    =432;
                int   defHeight   =1240;
                int   imageHeight =originalSrc.getHeight();
                int   imageWidth  = originalSrc.getWidth();
                float scale=getResources().getDisplayMetrics().density;
                int left=imageWidth*120/432;
                int top=imageHeight*540/1240;
                int bottom=imageHeight*820/1240;
                int right=imageWidth*310/432;
                Rect rect=new Rect();
//                int textSize= DimenUtils.dip2px(QiFu_PaiWei_Detail.this,defSize);
                int textSize= defSize;

                paint.setTextSize(textSize);
                paint.getTextBounds(address,0,1,rect);
                int singleHeight=rect.height();
                while (address.length()*singleHeight>(bottom-top)){
                    defSize-=1;
                    textSize=defSize;
                    paint.setTextSize(textSize);
                    paint.getTextBounds(address,0,1,rect);
                    singleHeight=rect.height();
                }
                for(int i=0;i<address.length();i++){
                    canvas.drawText(address.toCharArray()[i]+"\n", right-rect.width() , top +i*singleHeight+20, paint);
                }
                for(int i=0;i<person.length();i++){
                    canvas.drawText(person.toCharArray()[i]+"\n", (left+(right-left)/2)-rect.width()/2 , top +i*singleHeight+40, paint);
                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy年MM月dd日");
                String t= TimeUtils.getStrTime_spe(sdf,time,sdf2);
                for(int i=0;i<t.length();i++){
                    canvas.drawText(t.toCharArray()[i]+"\n", left , top +i*singleHeight+20, paint);
                }
                LogUtil.e("默认dP:::"+defSize);
                imageView.setImageBitmap(newSrc);


                LogUtil.e("高度：：；"+imageHeight+"  宽度：："+originalSrc.getWidth());
                LogUtil.e("矩形各点位：：；"+left+"    "+top+"   "+bottom+"   "+right);
                LogUtil.e("矩形高宽：：；"+rect.height()+"    "+rect.width());
            }
        });









    }
}
