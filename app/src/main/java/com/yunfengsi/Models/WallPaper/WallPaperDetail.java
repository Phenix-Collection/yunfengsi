package com.yunfengsi.Models.WallPaper;

import android.Manifest;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.cpiz.android.bubbleview.BubblePopupWindow;
import com.cpiz.android.bubbleview.BubbleStyle;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.ruffian.library.RTextView;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.yunfengsi.View.Photo.AlxGifHelper;
import com.yunfengsi.View.Photo.PhotoViewPager;
import com.yunfengsi.R;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.ImageUtil;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.Network;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.ProgressUtil;
import com.yunfengsi.Utils.ShareManager;
import com.yunfengsi.Utils.ToastUtil;
import com.yunfengsi.Utils.mApplication;
import com.yunfengsi.Models.YunDou.YunDouAwardDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Response;
import pl.droidsonroids.gif.GifImageView;
import uk.co.senab.photoview.PhotoView;

/**
 * 作者：因陀罗网 on 2018/5/28 14:53
 * 公司：成都因陀罗网络科技有限公司
 */
public class WallPaperDetail extends AppCompatActivity implements View.OnClickListener {
    //    private PhotoView photoView;
    private PhotoViewPager pager;
    ImageView back;
    private RTextView like, user, download, collect, encourage, delete, comment;
    private Bundle info;//共享元素的信息
    float scaleX;
    float scaleY;
    float translationX;
    float translationY;
    private boolean deleteAble = false;
    private HashMap<String, String> detailMap;
    private String                  id;//当前Id

    public boolean hasLiked     = false;
    public boolean hasCollected = false;
    private Bitmap                   wallPaper; //当前的壁纸bitmap
    private ImageView                animateLikeView;
    private ViewGroup                root;
    private FrameLayout.LayoutParams fl;
    private boolean isAnimating = false;//是否正在点赞动画中
    private int animWidth, animHeight;
    private boolean netWork = true;
    private photoAdapter adapter;
    private PhotoView    curentView;
    private int     currentPos = 0;
    private boolean isFirstIn  = true;
    private ArrayList<HashMap<String, String>> paths;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.wall_pager_detail);
        pager = findViewById(R.id.paper);


        deleteAble = getIntent().getBooleanExtra("delete", false);
        hasCollected = getIntent().getBooleanExtra("collect", false);


        like = findViewById(R.id.like);
        user = findViewById(R.id.user);
        download = findViewById(R.id.download);
        collect = findViewById(R.id.collect);
        encourage = findViewById(R.id.encourage);
        delete = findViewById(R.id.delete);
        comment = findViewById(R.id.comment);

        info = getIntent().getBundleExtra("info");
        currentPos = getIntent().getIntExtra("pos", 0);
        paths = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("paths");
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int pos) {
                LogUtil.e("当前：：" + currentPos + "   试图：：" + pos);
                currentPos = pos;
                id = hasCollected ? paths.get(currentPos).get("wallpaper_id") : paths.get(currentPos).get("id");
                getDetail();
                resetUI();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        adapter = new photoAdapter(this, paths);
        pager.setAdapter(adapter);
        pager.setCurrentItem(currentPos, false);
        if (currentPos == 0) {
            id = hasCollected ? paths.get(currentPos).get("wallpaper_id") : paths.get(currentPos).get("id");
            getDetail();
        }

        if (hasCollected) {
            showCollected();
        }
        if (deleteAble) {
            delete.setVisibility(View.VISIBLE);
        } else {
            delete.setVisibility(View.GONE);
        }

    }

    /**
     * 让所有Ui恢复到初始状态
     */
    private void resetUI() {
        hideCollected();
        hideLike();
        if (Network.HttpTest(this)) {
            netWork = true;
            like.setEnabled(true);
            download.setEnabled(true);
            collect.setEnabled(true);
            comment.setEnabled(true);
        } else {
            onNetWorkDown();
        }

    }

    private void hideLike() {
        hasLiked = false;
        Drawable red = ContextCompat.getDrawable(WallPaperDetail.this, R.drawable.encourage_wall_paper);
        red.setBounds(0, 0, DimenUtils.dip2px(WallPaperDetail.this, 20), DimenUtils.dip2px(WallPaperDetail.this, 20));
        like.setIconNormal(red);
        like.setTextColorNormal(Color.WHITE);
        like.setTextColorUnable(Color.parseColor("#888888"));
        like.setEnabled(true);

    }

    private class photoAdapter extends PagerAdapter {
        private ArrayList<HashMap<String, String>> paths;
        private Activity                           activity;
        private int                                screenWidth;


        public photoAdapter(Activity activity, ArrayList<HashMap<String, String>> paths) {
            super();
            this.paths = paths;
            WeakReference<Activity> w = new WeakReference<Activity>(activity);
            this.activity = w.get();
        }

        @Override
        public int getCount() {
            return paths == null ? 0 : paths.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }


        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            if (paths.get(position).get("image").endsWith("gif")) {

                Log.e("AlexGIF", "现在是gif大图");
                View          rl_gif        = LayoutInflater.from(activity).inflate(R.layout.gif_progress_group, null);//这种方式容易导致内存泄漏
                GifImageView  gifImageView  = (GifImageView) rl_gif.findViewById(R.id.gif_photo_view);
                ProgressWheel progressWheel = (ProgressWheel) rl_gif.findViewById(R.id.progress_wheel);
                TextView      tv_progress   = (TextView) rl_gif.findViewById(R.id.tv_progress);
                AlxGifHelper.displayImage(paths.get(position).get("image"), gifImageView, progressWheel, tv_progress, 0);//最后一个参数传0表示不缩放gif的大小，显示原始尺寸
                try {
                    container.addView(rl_gif);//这里要注意由于container是一个复用的控件，所以频繁的addView会导致多张相同的图片重叠，必须予以处置
                } catch (Exception e) {
                    Log.e("AlexGIF", "父控件重复！！！！，这里出现异常很正常", e);
                }
                return rl_gif;
            } else {
                final PhotoView photo;
                photo = new PhotoView(activity);

                photo.setOnDoubleTapListener(mOnDoubleTapListener);
                Glide.with(activity).load(paths.get(position).get("image"))
                        .asBitmap()
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(final Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                LogUtil.e("详情：：" + resource.getByteCount());
                                wallPaper = resource;//保存bitmap对象
                                photo.setImageBitmap(resource);
                                if (pager.getVisibility() != View.VISIBLE) {
                                    if (position == currentPos) {
                                        pager.setVisibility(View.VISIBLE);
                                        pager.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                                            @Override
                                            public boolean onPreDraw() {
                                                pager.getViewTreeObserver().removeOnPreDrawListener(this);
                                                int endWidth  = getResources().getDisplayMetrics().widthPixels;
                                                int endHeight = getResources().getDisplayMetrics().heightPixels;

                                                int startWidth  = info.getInt("width");
                                                int startHeight = info.getInt("height");


                                                scaleX = (startWidth * 1.0f / endWidth);
                                                scaleY = (startHeight * 1.0f / endHeight);
                                                translationX = info.getInt("left") - 0;
                                                translationY = info.getInt("top") - 0;
                                                photo.setPivotX(0);
                                                photo.setPivotY(0);
                                                photo.setScaleX(scaleX);
                                                photo.setScaleY(scaleY);
                                                photo.setTranslationX(translationX);
                                                photo.setTranslationY(translationY);
                                                photo.animate().setDuration(300)
                                                        .scaleX(1)
                                                        .scaleY(1)
                                                        .translationX(0)
                                                        .translationY(0)
                                                        .withEndAction(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                visibleViews(true);

                                                                if (!Network.HttpTest(mApplication.getInstance())) {
                                                                    onNetWorkDown();
                                                                }
                                                            }
                                                        }).start();
                                                return true;
                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                super.onLoadFailed(e, errorDrawable);
                                if (Network.HttpTest(mApplication.getInstance())) {
                                    ToastUtil.showToastShort("加载壁纸详情失败，请稍后重试");
                                    if (currentPos + 1 < paths.size()) {
                                        pager.setCurrentItem(++currentPos);
                                    } else if (currentPos - 1 >= 0) {
                                        pager.setCurrentItem(--currentPos);
                                    }
                                }

                            }
                        });
//            }


                container.addView(photo);
                return photo;
            }

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);

        }

    }

    GestureDetector.OnDoubleTapListener mOnDoubleTapListener = new GestureDetector.OnDoubleTapListener() {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (!isAnimating) {
                if (netWork) {
                    like.performClick();
                }
                float x = e.getRawX();
                float y = e.getRawY();

                int rotation = new Random().nextInt(100) - 50;
                if (animateLikeView == null) {
                    animateLikeView = new ImageView(WallPaperDetail.this);
                    animateLikeView.setImageBitmap(ImageUtil.readBitMap(WallPaperDetail.this, R.drawable.red_like));
                    root = (ViewGroup) getWindow().getDecorView();
                    fl = new FrameLayout.LayoutParams(animWidth, animHeight);
                }
                animateLikeView.setX(x - (animWidth >> 1));
                animateLikeView.setY(y - (animHeight >> 1));
                animateLikeView.setScaleX(0.01f);
                animateLikeView.setScaleY(0.01f);
                animateLikeView.setAlpha(1.0f);
                animateLikeView.setRotation(rotation);

                root.addView(animateLikeView, fl);
                animateLikeView.animate().scaleX(1.0f)
                        .withStartAction(new Runnable() {
                            @Override
                            public void run() {
                                isAnimating = true;
                            }
                        })
                        .scaleY(1.0f)
                        .setDuration(400)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                animateLikeView.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        animateLikeView.animate().scaleX(1.5f)
                                                .scaleY(1.5f)
                                                .alpha(0)
                                                .setDuration(300)
                                                .withEndAction(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        isAnimating = false;
                                                        root.removeView(animateLikeView);
                                                    }
                                                })
                                                .start();
                                    }
                                }, 80);
                            }
                        })
                        .start();
            }


            return false;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {

            return false;
        }
    };

    /**
     * 网络状况异常  处理显示
     */
    private void onNetWorkDown() {
        netWork = false;
        like.setEnabled(false);
        download.setEnabled(false);
        collect.setEnabled(false);
        comment.setEnabled(false);
//        findViewById(R.id.more).setVisibility(View.GONE);
//        findViewById(R.id.user_place).setVisibility(View.GONE);
    }

    private void getDetail() {

        JSONObject js = new JSONObject();
        try {
            js.put("id", hasCollected ? paths.get(currentPos).get("wallpaper_id") : paths.get(currentPos).get("id"));
            js.put("m_id", Constants.M_id);
            if (!PreferenceUtil.getUserId(this).equals("")) {
                js.put("user_id", PreferenceUtil.getUserId(this));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e("获取详情数据：：" + js);
        ApisSeUtil.M m = ApisSeUtil.i(js);
        OkGo.post(Constants.WallPaperDetail)
                .tag(this)
                .params("key", m.K())
                .params("msg", m.M())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {

                        detailMap = AnalyticalJSON.getHashMap(s);
                        if (detailMap != null) {
                            if (WallPaperDetail.this.isDestroyed()) {
                                return;
                            }
                            if ("000".equals(detailMap.get("code"))) {
                                if (detailMap.get("user_image").equals("")) {
                                    Glide.with(WallPaperDetail.this).load(R.drawable.indra)
                                            .asBitmap()
                                            .override(DimenUtils.dip2px(WallPaperDetail.this, 40), DimenUtils.dip2px(WallPaperDetail.this, 40))
                                            .into(new SimpleTarget<Bitmap>() {
                                                @Override
                                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                                    RoundedBitmapDrawable rbd = RoundedBitmapDrawableFactory.create(getResources(), resource);
                                                    rbd.setCircular(true);
                                                    user.setIconNormal(rbd);
                                                    user.setText("云峰禅院");
                                                    user.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            Intent intent = new Intent(WallPaperDetail.this, WallPaperUserHome.class);
                                                            intent.putExtra("id", detailMap.get("user_id"));
                                                            startActivity(intent);
                                                        }
                                                    });
                                                }
                                            });
                                } else {
                                    Glide.with(WallPaperDetail.this).load(detailMap.get("user_image"))
                                            .asBitmap()
                                            .override(DimenUtils.dip2px(WallPaperDetail.this, 40), DimenUtils.dip2px(WallPaperDetail.this, 40))
                                            .into(new SimpleTarget<Bitmap>() {
                                                @Override
                                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                                    RoundedBitmapDrawable rbd = RoundedBitmapDrawableFactory.create(getResources(), resource);
                                                    rbd.setCircular(true);
                                                    user.setIconNormal(rbd);
                                                    user.setText(detailMap.get("pet_name"));
                                                    user.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            Intent intent = new Intent(WallPaperDetail.this, WallPaperUserHome.class);
                                                            intent.putExtra("id", detailMap.get("user_id"));
                                                            startActivity(intent);
                                                        }
                                                    });
                                                }
                                            });
                                }
                                //判断是否收藏该壁纸  1没收藏  2收藏
                                if ("2".equals(detailMap.get("keep"))) {
                                    showCollected();
                                }
                                like.setText(detailMap.get("likes") + "人赞");
                            } else {
                                like.setEnabled(false);
                                download.setEnabled(false);
                                comment.setEnabled(false);
                                ToastUtil.showToastShort("获取详情失败，请检查网络后重试");
                            }
                        }
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        pager.requestDisallowInterceptTouchEvent(true);
                        ProgressUtil.show(WallPaperDetail.this, "", "正在加载...");
                        ProgressUtil.canCancelAbleOutSide(false);
                    }

                    @Override
                    public void onAfter(String s, Exception e) {
                        super.onAfter(s, e);
                        pager.requestDisallowInterceptTouchEvent(false);
                        ProgressUtil.dismiss();
                    }
                });
    }

    private void visibleViews(boolean flag) {
        findViewById(R.id.back).setVisibility(flag ? View.VISIBLE : View.GONE);
        findViewById(R.id.head_bg).setVisibility(flag ? View.VISIBLE : View.GONE);
        findViewById(R.id.more).setVisibility(flag ? View.VISIBLE : View.GONE);
        findViewById(R.id.place_black).setVisibility(flag ? View.VISIBLE : View.GONE);
        findViewById(R.id.user_place).setVisibility(flag ? View.VISIBLE : View.GONE);

        like.setVisibility(flag ? View.VISIBLE : View.GONE);
        user.setVisibility(flag ? View.VISIBLE : View.GONE);
        download.setVisibility(flag ? View.VISIBLE : View.GONE);
        collect.setVisibility(flag ? View.VISIBLE : View.GONE);
        encourage.setVisibility(flag ? View.VISIBLE : View.GONE);
        delete.setVisibility(deleteAble ? View.VISIBLE : View.GONE);
        comment.setVisibility(flag ? View.VISIBLE : View.GONE);
        pager.setVisibility(flag ? View.VISIBLE : View.INVISIBLE);


    }


    @Override
    protected void onStart() {
        super.onStart();
        back = findViewById(R.id.back);
        ImageView more = findViewById(R.id.more);
        back.setOnClickListener(this);
        more.setOnClickListener(this);
        like.setOnClickListener(this);
        download.setOnClickListener(this);
        collect.setOnClickListener(this);
        encourage.setOnClickListener(this);
        delete.setOnClickListener(this);
        comment.setOnClickListener(this);
//        like.setIconUnable(TintUtils.getTintDrawable(R.drawable.encourage_wall_paper, Color.parseColor("#888888"), 20));
//        download.setIconUnable(TintUtils.getTintDrawable(R.drawable.download, Color.parseColor("#888888"), 20));
//        collect.setIconUnable(TintUtils.getTintDrawable(R.drawable.collect_wall_paper, Color.parseColor("#888888"), 20));
//        comment.setIconUnable(TintUtils.getTintDrawable(R.drawable.comment_wall_paper, Color.parseColor("#888888"), 20));
        animWidth = DimenUtils.dip2px(this, 100);
        animHeight = DimenUtils.dip2px(this, 100);


//        if (deleteAble) {
//            Drawable white = ContextCompat.getDrawable(this, R.drawable.delete_wallpaper);
//            white.setBounds(0, 0, DimenUtils.dip2px(this, 16), DimenUtils.dip2px(this, 16));
//            delete.setIconNormal(white);
//            delete.setTextColorNormal(Color.WHITE);
//        } else {
//            Drawable gray = ContextCompat.getDrawable(this, R.drawable.delete_wallpaper_gray);
//            gray.setBounds(0, 0, DimenUtils.dip2px(this, 16), DimenUtils.dip2px(this, 16));
//            delete.setIconNormal(gray);
//            delete.setTextColorNormal(Color.parseColor("#888888"));
//            delete.setPressedTextColor(Color.parseColor("#888888"));
//        }
    }

    @Override
    public void onBackPressed() {
        findViewById(R.id.back).performClick();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.more:
                View root = LayoutInflater.from(this).inflate(R.layout.down_popup, null);
                BubblePopupWindow window = new BubblePopupWindow(root, (BubbleStyle) root.findViewById(R.id.layout));

                window.setCancelOnTouch(true);
                window.setCancelOnTouchOutside(true);
                window.showArrowTo(v, BubbleStyle.ArrowDirection.Up);
                root.findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        UMWeb umWeb = new UMWeb("http://a.app.qq.com/o/simple.jsp?pkgname=com.yunfengsi");
                        umWeb.setThumb(new UMImage(WallPaperDetail.this, wallPaper));
                        umWeb.setTitle("云峰寺全新壁纸功能上线啦");
                        umWeb.setDescription("我在云峰寺壁纸功能里发现一张超赞的佛系美图，快点我下载吧~");
                        new ShareManager().shareWeb(umWeb, WallPaperDetail.this);
                    }
                });
                break;
            case R.id.back:
                animateBackToPrevious();
                break;
            case R.id.like:

                Like();

                break;
            case R.id.download:
                saveImage();
                break;
            case R.id.comment:
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(WallPaperDetail.this, WallPaperComments.class));
                intent.putExtra("id", id);
                intent.putExtra("url", paths.get(currentPos).get("image"));
                startActivity(intent);

                break;

            case R.id.collect:
                collectWallPaper();
                break;
            case R.id.encourage:
//                if (Build.VERSION.SDK_INT >= 24) {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                    builder.setItems(new String[]{"桌面壁纸", "锁屏壁纸"}, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                            switch (which) {
//                                case 0:
//                                    setWallPaper();
//                                    break;
//                                case 1:
//                                    SetLockWallPaper();
//                                    break;
//                            }
//                        }
//                    }).create().show();
//                } else {
                    setWallPaper();
//                }


                break;
            case R.id.delete:

                // TODO: 2018/5/30 删除
                deleteWallPaper();

                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        OkGo.getInstance().cancelTag(this);
        ProgressUtil.dismiss();
        Glide.get(this).clearMemory();
    }

    private void deleteWallPaper() {
        if (!Network.HttpTest(this)) {
            return;
        }
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage("确认删除该壁纸吗?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("m_id", Constants.M_id);
                            jsonObject.put("user_id", PreferenceUtil.getUserId(WallPaperDetail.this));
                            jsonObject.put("wallpaper_id", id);
                            jsonObject.put("type", "1");//1 审核过的   2  正在审核的
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        LogUtil.e("删除自己的壁纸：；" + jsonObject);
                        ApisSeUtil.M m = ApisSeUtil.i(jsonObject);
                        OkGo.post(Constants.WallPaperTMineDelete).params("key", m.K())
                                .params("msg", m.M())
                                .execute(new StringCallback() {
                                    @Override
                                    public void onSuccess(String s, Call call, Response response) {
                                        HashMap<String, String> map = AnalyticalJSON.getHashMap(s);
                                        if (map != null) {
                                            if ("000".equals(map.get("code"))) {
                                                ToastUtil.showToastShort("删除成功");

                                                LocalBroadcastManager.getInstance(mApplication.getInstance())
                                                        .sendBroadcast(new Intent("wall_mine"));
                                                finish();

                                            } else {
                                                ToastUtil.showToastShort("删除失败，请稍后重试");
                                            }
                                        }
                                    }

                                    @Override
                                    public void onBefore(BaseRequest request) {
                                        super.onBefore(request);
                                        ProgressUtil.show(WallPaperDetail.this, "", "正在删除...");
                                    }

                                    @Override
                                    public void onAfter(String s, Exception e) {
                                        super.onAfter(s, e);
                                        ProgressUtil.dismiss();
                                    }
                                });
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        }).create().show();
    }

    private void collectWallPaper() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("m_id", Constants.M_id);
            jsonObject.put("user_id", PreferenceUtil.getUserId(WallPaperDetail.this));
            jsonObject.put("wallpaper_id", id);
            jsonObject.put("image", detailMap.get("image"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e("壁纸收藏：：：" + jsonObject);
        ApisSeUtil.M m = ApisSeUtil.i(jsonObject);
        OkGo.post(Constants.WallPaperCollectInterface).params("key", m.K())
                .params("msg", m.M()).execute(new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                HashMap<String, String> map = AnalyticalJSON.getHashMap(s);
                if (map != null) {
                    if ("000".equals(map.get("code"))) {
                        showCollected();
                        ToastUtil.showToastShort("收藏成功");
                    } else if ("001".equals(map.get("code"))) {
                        hideCollected();
                        ToastUtil.showToastShort("收藏已取消");
                    }

                }
            }
        });
    }

    private void hideCollected() {
        Drawable red = ContextCompat.getDrawable(WallPaperDetail.this, R.drawable.collect_wall_paper);
        red.setBounds(0, 0, DimenUtils.dip2px(WallPaperDetail.this, 20), DimenUtils.dip2px(WallPaperDetail.this, 20));
        collect.setIconNormal(red);
        collect.setTextColorNormal(Color.WHITE);
    }

    private void showCollected() {
        Drawable red = ContextCompat.getDrawable(WallPaperDetail.this, R.drawable.collect_red);
        red.setBounds(0, 0, DimenUtils.dip2px(WallPaperDetail.this, 20), DimenUtils.dip2px(WallPaperDetail.this, 20));
        collect.setIconNormal(red);
        collect.setTextColorNormal(Color.parseColor("#F14E69"));
    }

    private void saveImage() {
        final String TAG = "图片下载";
        Log.e("图片下载", "run: " + detailMap.get("image"));
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                Log.e("图片下载权限检测", "run: " + detailMap.get("image"));
                return;
            }
        }

        Glide.with(this).load(detailMap.get("image"))
                .asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                Log.e(TAG, "onResourceReady:内存大小 " + resource.getByteCount());
                File file = new File(Environment.getExternalStorageDirectory(), "wallPaper" + id + ".jpg");
                Log.e(TAG, "文件地址 " + file.length());
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    resource.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                    if (fos != null) {
                        fos.close();
                    }
                    ToastUtil.showToastShort("文件已下载至" + file.getAbsolutePath() + ",共消耗" + (file.length() / 1000) + "KB");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

//            }
//        }).start();

    }

    private void animateBackToPrevious() {
//        adapter.mSparseArray.get(currentPos).animate()
//                .setDuration(300)
//                .scaleX(scaleX)
//                .scaleY(scaleY)
//                .translationX(translationX)
//                .translationY(translationY)
//                .withStartAction(new Runnable() {
//                    @Override
//                    public void run() {
//                        visibleViews(false);
//                    }
//                })
//                .withEndAction(new Runnable() {
//                    @Override
//                    public void run() {
        if (hasCollected) {
            setResult(222);
        }
        finish();
//                        overridePendingTransition(0, 0);
//                    }
//                })
//                .withLayer().start();
    }

    private void setWallPaper() {
        AlertDialog.Builder builder = new AlertDialog.Builder(WallPaperDetail.this);
        builder.setMessage("确定要使用该壁纸作为桌面壁纸吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Glide.with(WallPaperDetail.this)
                                .load(paths.get(currentPos).get("image"))
                                .asBitmap().into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                wallPaper = resource;
                                WallpaperManager manager = WallpaperManager.getInstance(WallPaperDetail.this);
                                if (wallPaper != null) {
                                    try {
                                        manager.setBitmap(wallPaper);
//                                manager.setBitmap(wallPaper,new Rect(0,0,getResources().getDisplayMetrics().widthPixels,getResources().getDisplayMetrics().heightPixels)
//                                ,true,WallpaperManager.FLAG_LOCK);
                                        ToastUtil.showToastShort("壁纸设置成功");
                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(WallPaperDetail.this);
                                        builder1.setMessage("是否需要回到桌面查看壁纸？")
                                                .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                        moveTaskToBack(true);
                                                    }
                                                }).setNegativeButton("不需要，谢谢", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).create().show();
                                    } catch (IOException e) {
                                        ToastUtil.showToastShort("壁纸设置失败，请稍后重试");
                                        e.printStackTrace();
                                    }
                                } else {
                                    ToastUtil.showToastShort("壁纸设置失败，请稍后重试");
                                }
                            }
                        });

                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }

    private void Like() {
        if (hasLiked) {
            return;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("m_id", Constants.M_id);
            jsonObject.put("user_id", PreferenceUtil.getUserId(WallPaperDetail.this));
            jsonObject.put("wallpaper_id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e("壁纸点赞：：：" + jsonObject);
        ApisSeUtil.M m = ApisSeUtil.i(jsonObject);
        OkGo.post(Constants.WallPaperLike).params("key", m.K())
                .params("msg", m.M())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        HashMap<String, String> map = AnalyticalJSON.getHashMap(s);
                        if (map != null) {
                            Drawable red = ContextCompat.getDrawable(WallPaperDetail.this, R.drawable.red_like);
                            red.setBounds(0, 0, DimenUtils.dip2px(WallPaperDetail.this, 20), DimenUtils.dip2px(WallPaperDetail.this, 20));
                            like.setIconNormal(red);
                            like.setTextColorUnable(Color.parseColor("#FE4B73"));
                            like.setEnabled(false);
                            hasLiked = true;
                            if ("000".equals(map.get("code"))) {
                                int old = Integer.valueOf(detailMap.get("likes"));
                                detailMap.put("likes", String.valueOf(old + 1));
                                like.setText(detailMap.get("likes") + "人赞");
                                if (!"0".equals(map.get("yundousum"))) {
                                    YunDouAwardDialog.show(WallPaperDetail.this, "每日点赞", map.get("yundousum"));
                                }
                            } else if ("002".equals(map.get("code"))) {//已经点过赞了
                                ToastUtil.showToastShort("你已经点过赞啦");
                            }
                        }
                    }
                });
    }


    private void SetLockWallPaper() {
        AlertDialog.Builder builder = new AlertDialog.Builder(WallPaperDetail.this);
        builder.setMessage("确定要使用该壁纸作为锁屏壁纸吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Glide.with(WallPaperDetail.this)
                                .load(paths.get(currentPos).get("image"))
                                .asBitmap().into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                wallPaper = resource;
                                WallpaperManager manager = WallpaperManager.getInstance(WallPaperDetail.this);
                                if (wallPaper != null) {
                                    try {
//                                        manager.setBitmap(wallPaper);
                                        manager.setBitmap(wallPaper, new Rect(0, 0, getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels)
                                                , true, WallpaperManager.FLAG_LOCK);
                                        ToastUtil.showToastShort("锁屏壁纸设置成功");
                                    } catch (IOException e) {
                                        ToastUtil.showToastShort("壁纸设置失败，请稍后重试");
                                        e.printStackTrace();
                                    }
                                } else {
                                    ToastUtil.showToastShort("壁纸设置失败，请稍后重试");
                                }
                            }
                        });

                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();

    }
}
