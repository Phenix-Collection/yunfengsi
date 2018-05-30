package com.yunfengsi.WallPager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.ruffian.library.RTextView;
import com.yunfengsi.R;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.ToastUtil;

import uk.co.senab.photoview.PhotoView;

/**
 * 作者：因陀罗网 on 2018/5/28 14:53
 * 公司：成都因陀罗网络科技有限公司
 */
public class WallPaperDetail extends AppCompatActivity implements View.OnClickListener {
    private PhotoView photoView;

    private RTextView like, user, download, comment, collect, encourage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.wall_pager_detail);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        } else {
            ((ViewGroup.MarginLayoutParams) findViewById(R.id.back).getLayoutParams()).topMargin = DimenUtils.dip2px(this, 5);
            ((ViewGroup.MarginLayoutParams) findViewById(R.id.more).getLayoutParams()).topMargin = DimenUtils.dip2px(this, 5);
        }


        if (Build.VERSION.SDK_INT >= 21) {
            ViewCompat.setTransitionName(findViewById(R.id.paper), getString(R.string.wallPaper_ShareName));

        }

        photoView = findViewById(R.id.paper);
        byte[] b      = getIntent().getByteArrayExtra("photoBytes");
        Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
        photoView.setImageBitmap(bitmap);
//        Glide.with(this).load(getIntent().getStringExtra("image"))
//                .asBitmap().into(photoView);

        like = findViewById(R.id.like);
        user = findViewById(R.id.user);
        download = findViewById(R.id.download);
        comment = findViewById(R.id.comment);
        collect = findViewById(R.id.collect);
        encourage = findViewById(R.id.encourage);


//        Glide.with(this).load(getIntent().getStringExtra("url"))
//                .into(photoView);
        Glide.with(this).load(PreferenceUtil.getUserIncetance(this).getString("head_url", ""))
                .asBitmap()
                .override(DimenUtils.dip2px(this, 40), DimenUtils.dip2px(this, 40))
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        RoundedBitmapDrawable rbd = RoundedBitmapDrawableFactory.create(getResources(), resource);
                        rbd.setCircular(true);
                        user.setIconNormal(rbd);
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        findViewById(R.id.back).setOnClickListener(this);
        like.setOnClickListener(this);
        download.setOnClickListener(this);
        comment.setOnClickListener(this);
        collect.setOnClickListener(this);
        encourage.setOnClickListener(this);

        photoView.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                ToastUtil.showToastShort("点赞");
                return false;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                LogUtil.e("onDoubleTapEvent:::" + e.getAction());
                return false;
            }
        });
        photoView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                LogUtil.e("拖拽：：：");
                ToastUtil.showToastShort("拖拽");
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.like:
                break;
            case R.id.download:
                break;
            case R.id.comment:
                break;
            case R.id.collect:
                break;
            case R.id.encourage:
                break;
        }
    }
}
