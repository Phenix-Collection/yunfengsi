package com.maimaizu.Mine;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.lzy.okgo.OkGo;
import com.maimaizu.Activitys.Mine_FangYuan;
import com.maimaizu.R;
import com.maimaizu.Utils.ACache;
import com.maimaizu.Utils.AnalyticalJSON;
import com.maimaizu.Utils.Constants;
import com.maimaizu.Utils.ImageUtil;
import com.maimaizu.Utils.LogUtil;
import com.maimaizu.Utils.LoginUtil;
import com.maimaizu.Utils.mApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import okhttp3.Response;

//import com.hyphenate.EMCallBack;
//import com.hyphenate.chat.EMClient;


/**
 * Created by Administrator on 2016/5/31.
 */
public class Mine extends Fragment implements View.OnClickListener {
    private static final String TAG = "Mine";
    private static final int CHOOSEPICTUE = 2;//相册
    private static final int TAKEPICTURE = 1;//相机

    private Uri pictureUri = null;
    private AlertDialog dialog;
    public AvatarImageView head;
    public SharedPreferences sp;
    public ACache aCache;
    private int screenWidth;
    public String path;
    private File Headfile;
    private int screenHeight;
    //    private TextView tab2, tab3;
//    private ViewPager viewpager;
//    private FragmentManager fm;
    private List<Fragment> list;
    private ImageView qiehuanzhanghao;
    private TextView petname, sign;
    private LinearLayout geren, shoucang, zhifu, bangzhu, vip, address;

    private BroadcastReceiver userReseiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateInfo();
            Log.w(TAG, "onReceive: 状态改变");
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.mine, container, false);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(TAG);
        getActivity().registerReceiver(userReseiver, intentFilter);
        sp = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        LogUtil.w("onCreate: =-=-=-=-=-=-=-=-=" + sp.getString("user_id", ""));
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;
        aCache = ACache.get(getActivity());
        Glide.with(getActivity()).load(R.drawable.mine_banner).bitmapTransform(new BlurTransformation(getActivity(), 5))
                .override(screenWidth, screenWidth * 55 / 100).into((ImageView) view.findViewById(R.id.mine_back));
        ((ImageView) view.findViewById(R.id.mine_back)).setColorFilter(Color.parseColor("#40000000"));
        geren = (LinearLayout) view.findViewById(R.id.mine_gerenshezhi);
        ((TextView) geren.findViewById(R.id.settings)).setText(mApplication.ST("设置"));
        Bitmap b0 = ImageUtil.readBitMap(getActivity(), R.drawable.setting);
        ((ImageView) geren.findViewById(R.id.setting)).setImageBitmap(b0);
//        Log.e(TAG, "onCreateView: 设置的图片内存"+b0.getByteCount()+"    直接读取的内存：" );
        head = (AvatarImageView) view.findViewById(R.id.mine_head);
        head.setImageBitmap(ImageUtil.readBitMap(getActivity(), R.drawable.head_default));
        petname = (TextView) view.findViewById(R.id.mine_petName);
        sign = (TextView) view.findViewById(R.id.mine_sign);
        qiehuanzhanghao = (ImageView) view.findViewById(R.id.mine_qiehuanzhanghao);
        qiehuanzhanghao.setImageBitmap(ImageUtil.readBitMap(getActivity(), R.drawable.qiehuan));
        qiehuanzhanghao.setOnClickListener(this);
        shoucang = (LinearLayout) view.findViewById(R.id.mine_shoucang);
        ((TextView) shoucang.findViewById(R.id.shoucangtext)).setText(mApplication.ST("收藏"));
        ((ImageView) shoucang.findViewById(R.id.shoucang)).setImageBitmap(ImageUtil.readBitMap(getActivity(), R.drawable.shoucang_left));
        shoucang.setOnClickListener(this);
        address = (LinearLayout) view.findViewById(R.id.mine_address);
        ((ImageView) address.findViewById(R.id.tougao)).setImageBitmap(ImageUtil.readBitMap(getActivity(), R.drawable.huiyuanzhongxin));
        address.setOnClickListener(this);
        zhifu = (LinearLayout) view.findViewById(R.id.mine_dingdan);
        ((TextView) zhifu.findViewById(R.id.guanzhu)).setText(mApplication.ST("关注"));
        ((ImageView) zhifu.findViewById(R.id.zhifu)).setImageBitmap(ImageUtil.readBitMap(getActivity(), R.drawable.guanzhu));
        zhifu.setOnClickListener(this);
        bangzhu = (LinearLayout) view.findViewById(R.id.mine_bangzhu);
        ((TextView) bangzhu.findViewById(R.id.bangzhu)).setText(mApplication.ST("帮助"));
        ((ImageView) bangzhu.findViewById(R.id.jianjie)).setImageBitmap(ImageUtil.readBitMap(getActivity(), R.drawable.bangzhu));
        bangzhu.setOnClickListener(this);
        vip = (LinearLayout) view.findViewById(R.id.mine_Vip);
        vip.setOnClickListener(this);
//        ((ImageView) vip.findViewById(R.id.Vip)).setImageBitmap(ImageUtil.readBitMap(getActivity(),R.drawable.huiyuanzhongxin));

        LinearLayout fangyuan= (LinearLayout) view.findViewById(R.id.mine_fangyuan);
        ((ImageView) fangyuan.findViewById(R.id.fangyuan)).setImageBitmap(ImageUtil.readBitMap(getActivity(), R.drawable.fangyuan));
        ((TextView) fangyuan.findViewById(R.id.fangyuantext)).setText(mApplication.ST("我的房源"));
        fangyuan.setOnClickListener(this);
        list = new ArrayList<>();

        SetHead();
        if (!sp.getString("pet_name", "").equals("")) {
            petname.setText(sp.getString("pet_name", ""));
        } else {
            petname.setText(mApplication.ST("点击设置昵称"));
        }
        if (!sp.getString("sign", "").equals("")) {
            sign.setText(sp.getString("sign", ""));
        } else {
            sign.setText(mApplication.ST("我是个性签名"));
        }
//            resetData();

        head.setOnClickListener(this);
        geren.setOnClickListener(this);
        sign.setOnClickListener(this);
        petname.setOnClickListener(this);
        updateInfo();

        return view;
    }


//    protected void resetData() {
//        ((TextView) shoucang.findViewById(R.id.shoucangtext)).setText(mApplication.ST("收藏"));
//        ((TextView) zhifu.findViewById(R.id.guanzhu)).setText(mApplication.ST("关注"));
//        ;
//        ((TextView) geren.findViewById(R.id.settings)).setText(mApplication.ST("设置"));
//        ((TextView) bangzhu.findViewById(R.id.bangzhu)).setText(mApplication.ST("帮助"));
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(userReseiver);
    }


    Handler handler = new Handler();

    private void updateInfo() {
        final Intent intent = new Intent();
        if (TextUtils.isEmpty(sp.getString("user_id", ""))) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String data = OkGo.post(Constants.User_Info_Ip).tag(TAG).params("user_id", sp.getString("user_id", "")).params("key", Constants.safeKey)
                            .execute().body().string();

                    if (!TextUtils.isEmpty(data)) {
                        Log.w(TAG, "run: data-=-=" + data);
                        final HashMap<String, String> map = AnalyticalJSON.getHashMap(data);
                        final SharedPreferences.Editor ed = sp.edit();
                        if (handler != null) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (map != null) {
                                        if (!map.get("user_image").equals("")) {
                                            ed.putString("head_url", map.get("user_image"));
                                            Glide.with(Mine.this).load(map.get("user_image")).bitmapTransform(new CropCircleTransformation(getActivity())).placeholder(R.drawable.head_default).override(screenWidth / 4, screenWidth / 4).into(head);

                                        }
                                        if (!map.get("sex").equals("")) {
                                            ed.putString("sex", map.get("sex"));
                                        }
                                        if (!map.get("signature").equals("")) {
                                            ed.putString("signature", map.get("signature"));
                                            sign.setText(map.get("signature"));
                                        }
                                        if (!map.get("pet_name").equals("")) {
                                            ed.putString("pet_name", map.get("pet_name"));
                                            petname.setText(map.get("pet_name"));
                                        }
                                        if (map.get("pet_name").equals("") || map.get("sex").equals("")) {
                                            intent.setClass(getActivity(), Mine_gerenziliao.class);
                                            startActivity(intent);
                                        }
                                    }
                                    ed.apply();

                                }
                            });


                        }
                    }
                } catch (Exception e) {

                }
            }
        }).start();
    }

    public void SetHead() {
        Bitmap bm = aCache.getAsBitmap("head_" + sp.getString("user_id", ""));
        if (bm != null) {
            Log.w(TAG, "SetHead:  Cache");
            head.setImageBitmap(bm);
            return;
        }
        if (path != null && !("").equals(path)) {
            File file = new File(sp.getString("head_path", ""));
            if (file.exists() && file.isFile()) {
                Log.w(TAG, "SetHead:  file");
                Glide.with(this).load(path).asBitmap().into(new BitmapImageViewTarget(head) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        super.onResourceReady(resource, glideAnimation);
                        aCache.put("head_" + sp.getString("user_id", ""), resource);
                        head.setImageBitmap(resource);
                    }
                });
                return;
            }
        }
        if (!sp.getString("head_url", "").equals("")) {
            Glide.with(getActivity()).load(sp.getString("head_url", "")).bitmapTransform(new CropCircleTransformation(getActivity()))
                    .override(screenWidth / 4, screenWidth / 4).into(head);

        }
    }


    public void uploadHead(final File file) {
        final String uid = sp.getString("user_id", "");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = OkGo.post(Constants.uploadHead_IP).tag(TAG)
                            .params("head", file).params("user_id", uid).params("key", Constants.safeKey).execute();
                    String data1 = response.body().string();

                    if (!data1.equals("")) {
                        if (null != AnalyticalJSON.getHashMap(data1).get("code")) {
                            if ("000".equals(AnalyticalJSON.getHashMap(data1).get("code"))) {
                                SharedPreferences.Editor ed = sp.edit();
                                String url = AnalyticalJSON.getHashMap(data1).get("head");
                                if (url != null) {
                                    ed.putString("head_url", url);
                                    ed.apply();
                                }
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(mApplication.getInstance(), mApplication.ST("头像更改成功"), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(mApplication.getInstance(), mApplication.ST("头像更改失败"), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        } else {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mApplication.getInstance(), mApplication.ST("服务器异常"), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                } catch (IOException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mApplication.getInstance(), mApplication.ST("服务器异常"), Toast.LENGTH_SHORT).show();
                        }
                    });
                    e.printStackTrace();
                } catch (IllegalStateException e) {

                }

            }
        }).start();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            switch (requestCode) {
                case CHOOSEPICTUE:
                    Bitmap bm = null;
                    dialog.dismiss();
                    pictureUri = data.getData();// 选择照片的Uri 可能为null
                    if (pictureUri != null) {
                        //上传头像
                        path = ImageUtil.getImageAbsolutePath(mApplication.getInstance(), pictureUri);
                        Log.w(TAG, "onActivityResult:  path++++++++-========" + path);
                        bm = ImageUtil.getImageThumbnail(path, screenWidth / 4, screenWidth / 4);
                        head.setImageBitmap(bm);
                        FileOutputStream faos = null;
                        try {
                            faos = new FileOutputStream(path);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        bm.compress(Bitmap.CompressFormat.JPEG, 60, faos);
                        Log.w(TAG, "onActivityResult: size-=-=-=" + bm.getByteCount());
                        try {
                            if (faos != null) {
                                faos.flush();
                                Headfile = new File(path);
                                faos.close();
                            } else {
                                Toast.makeText(mApplication.getInstance(), mApplication.ST("上传失败,请重新尝试"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.w(TAG, "onActivityResult: ___________." + new File(path).length());

                        SharedPreferences.Editor ed = sp.edit();
                        aCache.put("head_" + sp.getString("user_id", ""), bm);
                        ed.putString("head_path", path);
                        ed.apply();
                        uploadHead(Headfile);
                        bm=null;
                    } else {
                        Toast.makeText(mApplication.getInstance(), mApplication.ST("上传失败,请重新尝试"), Toast.LENGTH_SHORT).show();
                    }


                    break;
                case TAKEPICTURE:
                    dialog.dismiss();
                    if (pictureUri != null) {
                        //上传头像
                        path = ImageUtil.getRealPathFromURI(getActivity(), pictureUri);
                        bm = ImageUtil.getImageThumbnail(path, screenWidth / 4, screenWidth / 4);
                        head.setImageBitmap(bm);
                                FileOutputStream faos = null;
                        try {
                            faos = new FileOutputStream(path);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        bm.compress(Bitmap.CompressFormat.JPEG, 60, faos);

                        try {
                            if (faos != null) {
                                faos.flush();
                                Headfile = new File(path);
                                faos.close();
                            } else {
                                Toast.makeText(mApplication.getInstance(), mApplication.ST("上传失败,请重新尝试"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.w(TAG, "onActivityResult: ___________." + new File(path).length());
                        SharedPreferences.Editor ed = sp.edit();
                        aCache.put("head_" + sp.getString("user_id", ""), bm);
                        ed.putString("head_path", path);
                        ed.apply();
                        bm=null;
                        uploadHead(Headfile);
                    } else {
                        Toast.makeText(mApplication.getInstance(), mApplication.ST("上传失败,请重新尝试"), Toast.LENGTH_SHORT).show();
                    }


                    break;
            }
        } else if (resultCode == 4) {
            sign.setText(data.getStringExtra("sign"));
        }

    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.mine_qiehuanzhanghao:
                mApplication.FangWu.clear();
                mApplication.ZiXun.clear();
                SharedPreferences.Editor ed = sp.edit();
                SharedPreferences.Editor address = getActivity().getSharedPreferences("address", Context.MODE_PRIVATE).edit();
                address.clear();
                address.apply();
                ed.putString("uid", "");
                ed.putString("user_id", "");
                ed.putString("head_path", "");
                ed.putString("head_url", "");
                ed.putString("sign", "");
                ed.putString("phone", "");
                ed.putString("sex", "");
                ed.putString("pet_name", "");
                aCache.remove("head_" + sp.getString("user_id", ""));
                ed.commit();
                Glide.with(this).load(R.drawable.head_default).into(head);
                petname.setText("请登录");
                sign.setText("");
                intent.setClass(mApplication.getInstance(), Login.class);
                startActivity(intent);
//                Intent intent1 = new Intent("Mine");
//                getActivity().sendBroadcast(intent1);
//                getActivity().finish();

                break;
            /*
            昵称点击修改昵称
             */
            case R.id.mine_petName:
                if (!new LoginUtil().checkLogin(getActivity())) {
                    return;
                }
                Intent intentnc = new Intent(getActivity(), NiCTemple_Activity.class);
                intentnc.putExtra("title", "昵称");
                intentnc.putExtra("petname", petname.getText().toString());
                startActivityForResult(intentnc, 1);
                break;
                /*
            签名点击修改签名
             */
            case R.id.mine_sign:
                if (!new LoginUtil().checkLogin(getActivity())) {
                    return;
                }
                intent = new Intent(getActivity(), Sign.class);
                intent.putExtra("sign", sign.getText().toString());
                startActivityForResult(intent, 4);
                break;
            case R.id.mine_head:
                if (sp.getString("uid", "").equals("") || sp.getString("user_id", "").equals("")) {
                    intent = new Intent(getActivity(), Login.class);
                    startActivity(intent);
                } else {
                    choosePic();
                }
                break;
            case R.id.mine_fangyuan:
                if (!new LoginUtil().checkLogin(getActivity())) {
                    return;
                }
                intent.setClass(getActivity(), Mine_FangYuan.class);
                startActivity(intent);
                break;
            case R.id.mine_gerenshezhi:
                intent.setClass(getActivity(), gerenshezhi.class);
                startActivity(intent);
                break;
            case R.id.mine_shoucang:
                if (!new LoginUtil().checkLogin(getActivity())) {
                    return;
                }
                intent.setClass(getActivity(), Activity_ShouCang.class);
                startActivity(intent);
                break;
//            case R.id.mine_tougao:
//                if (!new LoginUtil().checkLogin(getActivity())) {
//                    return;
//                }
//
//                break;
            case R.id.mine_dingdan:
                if (!new LoginUtil().checkLogin(getActivity())) {
                    return;
                }
                intent.setClass(getActivity(), Mine_GZ.class);
                startActivity(intent);
                break;
            case R.id.mine_bangzhu:
                intent.setClass(getActivity(), BangZhu.class);
                startActivity(intent);
                break;
//            case R.id.mine_Vip:
//                if(new LoginUtil().checkLogin(getActivity())){
//                    intent.setClass(getActivity(),Vip.class);
//                    startActivity(intent);
//                }
//                break;

        }
    }

    //选择照片或照相
    private void choosePic() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(mApplication.getInstance()).inflate(R.layout.dialog_album_camera, null);
        view.findViewById(R.id.photograph).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int permission1 = ContextCompat.checkSelfPermission(mApplication.getInstance(), Manifest.permission.CAMERA);
                    if (permission1 != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, TAKEPICTURE);
                    } else {
                        chooseCamera();
                        dialog.dismiss();
                    }
                } else {
                    chooseCamera();
                    dialog.dismiss();
                }
            }
        });
        view.findViewById(R.id.album).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int permissionCheck = ContextCompat.checkSelfPermission(mApplication.getInstance(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    int permissionCheck1 = ContextCompat.checkSelfPermission(mApplication.getInstance(), Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS);
                    int permissionCheck2 = ContextCompat.checkSelfPermission(mApplication.getInstance(), Manifest.permission.READ_EXTERNAL_STORAGE);
                    if (permissionCheck
                            != PackageManager.PERMISSION_GRANTED && permissionCheck1 != PackageManager.PERMISSION_GRANTED
                            && permissionCheck2 != PackageManager.PERMISSION_GRANTED) {
//
                        requestPermissions(
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS
                                        , Manifest.permission.READ_EXTERNAL_STORAGE},
                                CHOOSEPICTUE);
                    } else {
                        choosePhotoAlbum();
                        dialog.dismiss();
                    }
                } else {
                    choosePhotoAlbum();
                    dialog.dismiss();
                }

            }
        });
        builder.setView(view);
        dialog = builder.create();
        view.findViewById(R.id.cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Window window = dialog.getWindow();
//        window.setWindowAnimations(R.style.dialogWindowAnim);
        window.setBackgroundDrawableResource(R.color.vifrification);
        WindowManager.LayoutParams wl = window.getAttributes();
        window.setAttributes(wl);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CHOOSEPICTUE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    choosePhotoAlbum();
                } else {
                    Toast.makeText(mApplication.getInstance(), mApplication.ST("无相册权限将无法使用该功能"), Toast.LENGTH_SHORT).show();
                }
                break;
            case TAKEPICTURE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    chooseCamera();
                } else {
                    Toast.makeText(mApplication.getInstance(), mApplication.ST("无相册权限将无法使用该功能"), Toast.LENGTH_SHORT).show();
                }

        }
    }

    /**
     * 调用相机
     */
    private void chooseCamera() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss",
                Locale.CHINA);
        // Standard Intent action that can be sent to have the camera
        // application capture an image and return it.
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ContentValues attrs = new ContentValues();
        attrs.put(MediaStore.Images.Media.DISPLAY_NAME,
                dateFormat.format(new Date(System.currentTimeMillis())));// 添加照片名字
        attrs.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");// 图片类型
        attrs.put(MediaStore.Images.Media.DESCRIPTION, "");// 图片描述
        // //插入图片 成功则返回图片所对应的URI 当然我们可以自己指定图片的位置
        pictureUri = getActivity().getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, attrs);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);// 指定照片的位置
        startActivityForResult(takePictureIntent, TAKEPICTURE);

    }

    /**
     * 相册
     */
    private void choosePhotoAlbum() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setType("image/*");
//        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, CHOOSEPICTUE);

    }


}
