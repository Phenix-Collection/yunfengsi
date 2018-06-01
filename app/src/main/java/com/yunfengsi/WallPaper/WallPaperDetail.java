package com.yunfengsi.WallPaper;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.cpiz.android.bubbleview.BubblePopupWindow;
import com.cpiz.android.bubbleview.BubbleStyle;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.ruffian.library.RTextView;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
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
import com.yunfengsi.YunDou.YunDouAwardDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Response;
import uk.co.senab.photoview.PhotoView;

/**
 * 作者：因陀罗网 on 2018/5/28 14:53
 * 公司：成都因陀罗网络科技有限公司
 */
public class WallPaperDetail extends AppCompatActivity implements View.OnClickListener {
    private PhotoView photoView;

    private RTextView like, user, download, collect, encourage, delete;
    private Bundle info;//共享元素的信息
    float scaleX;
    float scaleY;
    float translationX;
    float translationY;
    private boolean deleteAble = false;
    private HashMap<String, String> detailMap;
    private String                  id;

    private boolean hasLiked     = false;
    private boolean hasCollected = false;
    private Bitmap                   wallPaper;
    private ImageView                animateLikeView;
    private ViewGroup                root;
    private FrameLayout.LayoutParams fl;
    private boolean isAnimating = false;//是否正在点赞动画中
    private int animWidth, animHeight;
    private boolean netWork=true;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.wall_pager_detail);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
//            window.setNavigationBarColor(Color.TRANSPARENT);
        } else {
            ((ViewGroup.MarginLayoutParams) findViewById(R.id.back).getLayoutParams()).topMargin = DimenUtils.dip2px(this, 5);
            ((ViewGroup.MarginLayoutParams) findViewById(R.id.more).getLayoutParams()).topMargin = DimenUtils.dip2px(this, 5);
        }

        photoView = findViewById(R.id.paper);

        id = getIntent().getStringExtra("id");
        deleteAble = getIntent().getBooleanExtra("delete", false);
        hasCollected = getIntent().getBooleanExtra("collect", false);


        like = findViewById(R.id.like);
        user = findViewById(R.id.user);
        download = findViewById(R.id.download);
        collect = findViewById(R.id.collect);
        encourage = findViewById(R.id.encourage);
        delete = findViewById(R.id.delete);

        info = getIntent().getBundleExtra("info");

        Glide.with(this).load(getIntent().getStringExtra("url"))
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(final Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        LogUtil.e("详情：：" + resource.getByteCount());

                        wallPaper = resource;//保存bitmap对象

                        photoView.setImageBitmap(resource);
                        photoView.setVisibility(View.VISIBLE);
                        photoView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                            @Override
                            public boolean onPreDraw() {
                                photoView.getViewTreeObserver().removeOnPreDrawListener(this);
                                int pos[] = new int[2];
                                photoView.getLocationOnScreen(pos);
                                int endWidth  = photoView.getWidth();
                                int endHeight = photoView.getHeight();

                                int startWidth  = info.getInt("width");
                                int startHeight = info.getInt("height");


                                scaleX = (startWidth * 1.0f / endWidth);
                                scaleY = (startHeight * 1.0f / endHeight);
                                translationX = info.getInt("left") - pos[0];
                                translationY = info.getInt("top") - pos[1];
                                photoView.setPivotX(0);
                                photoView.setPivotY(0);
                                photoView.setScaleX(scaleX);
                                photoView.setScaleY(scaleY);
                                photoView.setTranslationX(translationX);
                                photoView.setTranslationY(translationY);

                                LogUtil.e("位移：：" + scaleX + "   " + scaleY + "    " + translationX + "   " + translationY);
                                LogUtil.e("宽高：：" + startWidth + "   " + startHeight + "    " + endWidth + "   " + endHeight);
                                LogUtil.e("位置：：" + pos[0] + "   " + pos[1] + "    " + info.getInt("left") + "   " + info.getInt("top"));
                                photoView.animate().setDuration(300)
                                        .scaleX(1)
                                        .scaleY(1)
                                        .translationX(0)
                                        .translationY(0)
                                        .withEndAction(new Runnable() {
                                            @Override
                                            public void run() {
                                                visibleViews(true);
                                                if(!Network.HttpTest(mApplication.getInstance())){
                                                    netWork=false;
                                                    like.setVisibility(View.GONE);
                                                    download.setEnabled(false);
                                                    collect.setEnabled(false);
                                                    findViewById(R.id.more).setVisibility(View.GONE);
                                                    findViewById(R.id.user_place).setVisibility(View.GONE);
                                                }
                                            }
                                        })

                                        .start();

                                return true;
                            }
                        });

                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        if(Network.HttpTest(mApplication.getInstance())){
                            ToastUtil.showToastShort("加载壁纸详情失败，请稍后重试");
                        }
                        finish();

                    }
                });


        if (hasCollected) {
            Drawable red = ContextCompat.getDrawable(WallPaperDetail.this, R.drawable.collect_red);
            red.setBounds(0, 0, DimenUtils.dip2px(WallPaperDetail.this, 16), DimenUtils.dip2px(WallPaperDetail.this, 16));
            collect.setIconNormal(red);
            collect.setTextColorNormal(Color.parseColor("#F14E69"));
        }
        getDetail();


    }

    private void getDetail() {

        JSONObject js = new JSONObject();
        try {
            js.put("id", getIntent().getStringExtra("id"));
            js.put("m_id", Constants.M_id);
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

                                like.setText(detailMap.get("likes") + "人赞");
                            } else {
                                ToastUtil.showToastShort("获取详情失败，请检查网络后重试");
                            }
                        }
                    }
                });
    }

    private void visibleViews(boolean flag) {
        findViewById(R.id.back).setVisibility(flag ? View.VISIBLE : View.INVISIBLE);
        findViewById(R.id.more).setVisibility(flag ? View.VISIBLE : View.INVISIBLE);
        findViewById(R.id.place_black).setVisibility(flag ? View.VISIBLE : View.INVISIBLE);
        findViewById(R.id.user_place).setVisibility(flag ? View.VISIBLE : View.INVISIBLE);
        like.setVisibility(flag ? View.VISIBLE : View.INVISIBLE);
        user.setVisibility(flag ? View.VISIBLE : View.INVISIBLE);
        download.setVisibility(flag ? View.VISIBLE : View.INVISIBLE);
        collect.setVisibility(flag ? View.VISIBLE : View.INVISIBLE);
        encourage.setVisibility(flag ? View.VISIBLE : View.INVISIBLE);
        delete.setVisibility(flag ? View.VISIBLE : View.INVISIBLE);

    }

    @Override
    protected void onStart() {
        super.onStart();
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.more).setOnClickListener(this);
        like.setOnClickListener(this);
        download.setOnClickListener(this);
        collect.setOnClickListener(this);
        encourage.setOnClickListener(this);
        delete.setOnClickListener(this);
        animWidth = DimenUtils.dip2px(this, 100);
        animHeight = DimenUtils.dip2px(this, 100);

        photoView.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (!isAnimating) {
                    if(netWork){
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
                LogUtil.e("onDoubleTapEvent:::" + e.getAction());
                return false;
            }
        });

        if (deleteAble) {
            Drawable white = ContextCompat.getDrawable(this, R.drawable.delete_wallpaper);
            white.setBounds(0, 0, DimenUtils.dip2px(this, 16), DimenUtils.dip2px(this, 16));
            delete.setIconNormal(white);
            delete.setTextColorNormal(Color.WHITE);
        } else {
            Drawable gray = ContextCompat.getDrawable(this, R.drawable.delete_wallpaper_gray);
            gray.setBounds(0, 0, DimenUtils.dip2px(this, 16), DimenUtils.dip2px(this, 16));
            delete.setIconNormal(gray);
            delete.setTextColorNormal(Color.parseColor("#888888"));
            delete.setPressedTextColor(Color.parseColor("#888888"));
        }
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


            case R.id.collect:
                collectWallPaper();
                break;
            case R.id.encourage:
                setWallPaper();
                break;
            case R.id.delete:
                if (deleteAble) {
                    // TODO: 2018/5/30 删除
                    deleteWallPaper();
                } else {
                    ToastUtil.showToastShort("该壁纸无法删除");
                }
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
                        Drawable red = ContextCompat.getDrawable(WallPaperDetail.this, R.drawable.collect_red);
                        red.setBounds(0, 0, DimenUtils.dip2px(WallPaperDetail.this, 16), DimenUtils.dip2px(WallPaperDetail.this, 16));
                        collect.setIconNormal(red);
                        collect.setTextColorNormal(Color.parseColor("#F14E69"));
                        ToastUtil.showToastShort("收藏成功");
                    } else if ("001".equals(map.get("code"))) {
                        Drawable red = ContextCompat.getDrawable(WallPaperDetail.this, R.drawable.collect_wall_paper);
                        red.setBounds(0, 0, DimenUtils.dip2px(WallPaperDetail.this, 16), DimenUtils.dip2px(WallPaperDetail.this, 16));
                        collect.setIconNormal(red);
                        collect.setTextColorNormal(Color.WHITE);
                        ToastUtil.showToastShort("收藏已取消");
                    }

                }
            }
        });
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
        photoView.animate()
                .setDuration(300)
                .scaleX(scaleX)
                .scaleY(scaleY)
                .translationX(translationX)
                .translationY(translationY)
                .withStartAction(new Runnable() {
                    @Override
                    public void run() {
                        visibleViews(false);
                    }
                })
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        if (hasCollected) {
                            setResult(222);
                        }
                        finish();
                        overridePendingTransition(0, 0);
                    }
                })
                .withLayer().start();
    }

    private void setWallPaper() {
        AlertDialog.Builder builder = new AlertDialog.Builder(WallPaperDetail.this);
        builder.setMessage("确定要使用该壁纸吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        WallpaperManager manager = WallpaperManager.getInstance(WallPaperDetail.this);
                        if (wallPaper != null) {
                            try {
                                manager.setBitmap(wallPaper);
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
                            red.setBounds(0, 0, DimenUtils.dip2px(WallPaperDetail.this, 40), DimenUtils.dip2px(WallPaperDetail.this, 40));
                            like.setIconNormal(red);
                            like.setTextColorNormal(Color.parseColor("#FE4B73"));
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
}
