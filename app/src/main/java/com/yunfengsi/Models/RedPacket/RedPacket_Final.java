package com.yunfengsi.Models.RedPacket;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.yunfengsi.R;
import com.yunfengsi.View.ErWeiMa.BitmapLuminanceSource;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.ToastUtil;
import com.yunfengsi.Utils.mApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Hashtable;

public class RedPacket_Final extends AppCompatActivity {
    String filename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.activity_red_packet__final);

        ((TextView) findViewById(R.id.tip)).setText("1:凡在云峰寺APP注册的用户都能参与活动。\n" +
                "2:活动时间为2018年1月24日5点整(腊八)至2018年1月24日24点整。\n" +
                "3:云峰寺保留对活动的最终解释权。");
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        // TODO: 2017/12/18 点击二维码供养
        findViewById(R.id.qr_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder b = new AlertDialog.Builder(RedPacket_Final.this);
                final View view2 = LayoutInflater.from(RedPacket_Final.this).inflate(R.layout.dialog_bottom_good_manager, null);
                b.setView(view2);
                b.setCancelable(true);
                final AlertDialog dialog = b.create();
                ((TextView) view2.findViewById(R.id.title)).setText("请选择随喜方式");
                TextView weixin = (TextView) view2.findViewById(R.id.weixin);
                TextView alipay = (TextView) view2.findViewById(R.id.alipay);
                alipay.setVisibility(View.VISIBLE);

                weixin.setText("保存微信收款码到相册");
                alipay.setText("去支付宝随喜");

                weixin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.wexin_qr);
                        String filename = Environment.getExternalStorageDirectory().getAbsolutePath() + "/suixi_weixin.jpg";
                        try {
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, new FileOutputStream(new File(filename)));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            LogUtil.e("微信随喜图片保存失败" + e);
                        }
                        //把文件插入到系统图库
                        try {
                            MediaStore.Images.Media.insertImage(getContentResolver(), filename, "suixi_weixin.jpg", null);
                            //保存图片后发送广播通知更新数据库
                            Uri uri = Uri.fromFile(new File(filename));
                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            LogUtil.e("微信插入图片库失败");
                        }
                        ToastUtil.showToastShort("已保存收款码到相册，请去微信扫一扫随喜");
                        dialog.dismiss();
                    }
                });
                alipay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ali_qr);
                        String filename = Environment.getExternalStorageDirectory().getAbsolutePath() + "/suixi_ali.jpg";
                        try {
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, new FileOutputStream(new File(filename)));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            LogUtil.e("支付宝随喜图片保存失败" + e);
                        }
                        //把文件插入到系统图库
                        try {
                            MediaStore.Images.Media.insertImage(getContentResolver(), filename, "suixi_ali.jpg", null);
                            //保存图片后发送广播通知更新数据库
                            Uri uri = Uri.fromFile(new File(filename));
                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            LogUtil.e("支付宝插入图片库失败");
                        }
                        saveCurrentImage(RedPacket_Final.this, new File(filename));
                        dialog.dismiss();
                    }
                });
                view2.findViewById(R.id.cancle).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                Window window = dialog.getWindow();
                window.setGravity(Gravity.BOTTOM);
                window.getDecorView().setPadding(0, 0, 0, 0);
                window.setWindowAnimations(R.style.dialogWindowAnim);
                window.setBackgroundDrawableResource(R.color.vifrification);
                WindowManager.LayoutParams wl = window.getAttributes();
                wl.width = getResources().getDisplayMetrics().widthPixels;
                wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                window.setAttributes(wl);
                dialog.show();
            }
        });
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        // TODO: 2017/12/18 保存随喜供养二维码
//
//        Bitmap bitmap=BitmapFactory.decodeResource(getResources(),R.drawable.red_suixi);
//        filename = Environment.getExternalStorageDirectory().getAbsolutePath() + "/suixi.jpg";
//        LogUtil.e(new File(filename)+""+bitmap);
//        try {
//            bitmap.compress(Bitmap.CompressFormat.JPEG,80,new FileOutputStream(new File(filename)));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            LogUtil.e("随喜图片保存失败"+e);
//        }
//
//        //把文件插入到系统图库
//        try {
//            MediaStore.Images.Media.insertImage(getContentResolver(), filename, "suixi.jpg", null);
//            //保存图片后发送广播通知更新数据库
//            Uri uri = Uri.fromFile(new File(filename));
//            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            LogUtil.e("插入图片库失败");
//        }
//
//
//    }


    //这种方法状态栏是空白，显示不了状态栏的信息
    public static void saveCurrentImage(final Activity activity, final File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int p = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int permissionCheck1 = ContextCompat.checkSelfPermission(mApplication.getInstance(), Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS);
            int permissionCheck2 = ContextCompat.checkSelfPermission(mApplication.getInstance(), Manifest.permission.READ_EXTERNAL_STORAGE);
            if (p != PackageManager.PERMISSION_GRANTED && permissionCheck1 != PackageManager.PERMISSION_GRANTED
                    && permissionCheck2 != PackageManager.PERMISSION_GRANTED) {
//
                activity.requestPermissions(
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS
                                , Manifest.permission.READ_EXTERNAL_STORAGE},
                        0);
                return;
            }
        }

        new Thread(new Runnable() {
            @Override
            public void run() {

                final Result result = parseQRcodeBitmap(file.getAbsolutePath());
                LogUtil.e("扫描文件++" + file + "  扫描结果：：" + result);
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        String url = result.getText();
                        if (url.contains("HTTPS")) {
                            url = url.replace("HTTPS", "https");
                        }
                        if (url.contains("HTTP")) {
                            url = url.replace("HTTP", "http");
                        }
                        if (null != result) {
                            Uri uri = Uri.parse(url);
                            LogUtil.e("  处理后扫描结果：：" + url + "  链接  " + uri);
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(uri);
                            activity.startActivity(intent);
                        } else {
                            Toast.makeText(activity, "无法识别", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }).start();


    }

    //解析二维码图片,返回结果封装在Result对象中
    private static com.google.zxing.Result parseQRcodeBitmap(String bitmapPath) {
        //解析转换类型UTF-8
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
        //获取到待解析的图片
        BitmapFactory.Options options = new BitmapFactory.Options();
        //如果我们把inJustDecodeBounds设为true，那么BitmapFactory.decodeFile(String path, Options opt)
        //并不会真的返回一个Bitmap给你，它仅仅会把它的宽，高取回来给你
        options.inJustDecodeBounds = true;
        //此时的bitmap是null，这段代码之后，options.outWidth 和 options.outHeight就是我们想要的宽和高了
        Bitmap bitmap = BitmapFactory.decodeFile(bitmapPath, options);
        //我们现在想取出来的图片的边长（二维码图片是正方形的）设置为400像素
        //以上这种做法，虽然把bitmap限定到了我们要的大小，但是并没有节约内存，如果要节约内存，我们还需要使用inSimpleSize这个属性
        options.inSampleSize = options.outHeight / 400;
        if (options.inSampleSize <= 0) {
            options.inSampleSize = 1; //防止其值小于或等于0
        }
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(bitmapPath, options);
        //新建一个RGBLuminanceSource对象，将bitmap图片传给此对象
        BitmapLuminanceSource rgbLuminanceSource = new BitmapLuminanceSource(bitmap);
        //将图片转换成二进制图片
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(rgbLuminanceSource));
        //初始化解析对象
        QRCodeReader reader = new QRCodeReader();
        //开始解析
        Result result = null;
        try {
            result = reader.decode(binaryBitmap, hints);
        } catch (Exception e) {
        }
        return result;
    }
}
