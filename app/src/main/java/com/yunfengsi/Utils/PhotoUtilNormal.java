package com.yunfengsi.Utils;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.yunfengsi.BuildConfig;
import com.yunfengsi.R;

import java.io.File;

/**
 * Created by Administrator on 2016/12/22.
 */
public class PhotoUtilNormal {
    public static final int CHOOSEPICTUE = 2;//相册
    public static final int TAKEPICTURE  = 1;//相机
    public static Uri uri;

    //选择照片或照相
    public static void choosePic(final Activity context, final int requestId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        View view = LayoutInflater.from(mApplication.getInstance()).inflate(R.layout.dialog_album_camera, null);
        builder.setView(view);
        builder.setCancelable(true);
        final Dialog dialog = builder.create();
        view.findViewById(R.id.cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.photograph).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int permission1 = ContextCompat.checkSelfPermission(mApplication.getInstance(), Manifest.permission.CAMERA);
                    if (permission1 != PackageManager.PERMISSION_GRANTED) {
                        context.requestPermissions(new String[]{Manifest.permission.CAMERA}, requestId);
                    } else {
                        chooseCamera(context, requestId);
                        dialog.dismiss();
                    }
                } else {
                    chooseCamera(context, requestId);
                    dialog.dismiss();
                }
            }
        });
        view.findViewById(R.id.album).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int permissionCheck  = ContextCompat.checkSelfPermission(mApplication.getInstance(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    int permissionCheck1 = ContextCompat.checkSelfPermission(mApplication.getInstance(), Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS);
                    int permissionCheck2 = ContextCompat.checkSelfPermission(mApplication.getInstance(), Manifest.permission.READ_EXTERNAL_STORAGE);
                    if (permissionCheck
                            != PackageManager.PERMISSION_GRANTED && permissionCheck1 != PackageManager.PERMISSION_GRANTED
                            && permissionCheck2 != PackageManager.PERMISSION_GRANTED) {
//
                        context.requestPermissions(
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS
                                        , Manifest.permission.READ_EXTERNAL_STORAGE},
                                requestId);
                    } else {
                        choosePhotoAlbum(context, requestId);
                        dialog.dismiss();
                    }
                } else {
                    choosePhotoAlbum(context, requestId);
                    dialog.dismiss();
                }

            }
        });
        Window window = dialog.getWindow();

        window.setGravity(Gravity.BOTTOM);
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setWindowAnimations(R.style.dialogWindowAnim);
        window.setBackgroundDrawableResource(R.color.vifrification);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.width = context.getResources().getDisplayMetrics().widthPixels;
        wl.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(wl);
        dialog.show();
    }

    /**
     * 调用相机
     */
    public static void chooseCamera(Activity context, int requestId) {

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            File   file   = new File(Environment.getExternalStorageDirectory(),  "/yfs/pic/"+System.currentTimeMillis() + ".jpg");
            File   file   = new File(context.getExternalFilesDir("pic"),  System.currentTimeMillis() + ".jpg");
            Intent intent = new Intent();
            LogUtil.e("目录存在：："+file.getParentFile().exists()+"   "+file.getParentFile().getName());
            if (!file.getParentFile().exists()) {
                boolean flag=file.getParentFile().mkdirs();
                LogUtil.e("创建目录：："+flag);

            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", file);
            } else {
                uri = Uri.fromFile(file);
            }
            LogUtil.e("uri::::"+uri+"    file:::"+file.exists());
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);//将拍取的照片保存到指定URI
            context.startActivityForResult(intent, requestId);
        } else {
            Toast.makeText(context, "请确认插入SD卡", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 相册
     */
    public static void choosePhotoAlbum(Activity activity, int requestId) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setType("image/*");
        activity.startActivityForResult(intent, requestId);
    }


}
