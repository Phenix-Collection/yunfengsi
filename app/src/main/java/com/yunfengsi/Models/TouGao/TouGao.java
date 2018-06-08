package com.yunfengsi.Models.TouGao;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.model.HttpParams;
import com.yunfengsi.R;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.FileUtils;
import com.yunfengsi.Utils.ImageUtil;
import com.yunfengsi.Utils.Network;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.mApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/10/19.
 */
public class TouGao extends AppCompatActivity implements View.OnClickListener, TouGaoGridAdapter.oncCancleListener {
    private ImageView back, titleImg;
    private static final int RES_CODE = 1111;
    private TextView commit, title;
    private EditText title_edt, content_edt;
    private static final int CHOOSEPICTUE = 2;//相册
    private static final int TAKEPICTURE = 1;//相机
    private static final int ChooseVideoAndAudio = 3;//视频
    private Uri pictureUri = null;
    private AlertDialog dialog;
    private static final String TAG = "TouGao";
    public String path;
    private HttpParams httpParams;
    private SharedPreferences sp;
    private int screenWidth;
    private int a = 0;
    private ImageView addFile;//添加附件按钮
    private LinearLayout addFileLayout;//添加附件的空间
    //选择的图片集合
    private ArrayList<String> mImages = new ArrayList<>();
    private TouGaoGridAdapter adpter;
    private GridView grid;
    private File titleFile, videoFile, audioFile;
    private PopupWindow p;
    private AlertDialog progressDialog;
    private static final int MAX_VIDEO_TIME = 30;
    //    //语音开始录制时间
//    private long startTime;
//    private Timer timer;
//    private String voicePath;//输出地址
//    private mDialog voicedialog;//录制语音时的提示dialog
//    private ImageView img;//voicedialog中的图片
//    private TextView text;//voicedialog中的文字
//    private int times = 0;//计时
//    private recordAudio ra;//录制语音对象
//    private TextView t;//录音按钮
    private View view;
    private boolean isGrante = false;

    private boolean isCommited = false;
    private int allowChooseNum=3;
    @Override
    protected void onDestroy() {
        mImages.clear();
        mImages = null;
//        if (ra != null) {
//            if (null != ra.mRecorder) {
//                ra.stopRecord();
//            }
//            if (null != ra.mPlayer && ra.mPlayer.isPlaying()) {
//                ra.stopPlay();
//            }
//            if (ra != null) {
//                ra = null;
//            }
//        }
        super.onDestroy();
        if (!isCommited) {
            SharedPreferences.Editor ed = PreferenceUtil.getUserIncetance(this).edit();
            ed.putString("tougao_title", title_edt.getText().toString());
            ed.putString("tougao_msg", content_edt.getText().toString());
            ed.apply();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tougao);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        back = (ImageView) findViewById(R.id.title_back);
        back.setOnClickListener(this);
        back.setVisibility(View.VISIBLE);
        back.setImageResource(R.drawable.back);

        title = (TextView) findViewById(R.id.title_title);
        title.setText("投稿");

        ((TextView) findViewById(R.id.tip)).setText(mApplication.ST("" +
                "您还可以将您的感悟分享到我们的邮箱:\n" +
                "3377543986@qq.com"));

//        titleImg = (ImageView) findViewById(R.id.tougao_addTitleImg);
//        titleImg.setOnClickListener(this);

        sp = getSharedPreferences("user", MODE_PRIVATE);

        title_edt = (EditText) findViewById(R.id.tougao_title);
        title_edt.setText(mApplication.ST(sp.getString("tougao_title", "")));
        content_edt = (EditText) findViewById(R.id.tougao_content);
        content_edt.setText(mApplication.ST(sp.getString("tougao_content", "")));
        addFile = (ImageView) findViewById(R.id.tougao_addFile);
        addFile.setOnClickListener(this);
        addFileLayout = (LinearLayout) findViewById(R.id.tougao_addFile_layout);

        commit = (TextView) findViewById(R.id.tougao_commit);
        commit.setOnClickListener(this);
        screenWidth = getResources().getDisplayMetrics().widthPixels;


        grid = (GridView) findViewById(R.id.tougao_grid);
        mImages.add("add");
        adpter = new TouGaoGridAdapter(this, mImages, true,allowChooseNum);
        adpter.setOncCancleListener(this);
        grid.setAdapter(adpter);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tougao_commit:
                if (!Network.HttpTest(this)) {
                    Toast.makeText(TouGao.this, "请检查网络", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (title_edt.getText().toString().equals("")) {
                    Toast.makeText(this, "请填写标题", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (content_edt.getText().toString().equals("")
                        ) {
                    Toast.makeText(this, "请填写内容", Toast.LENGTH_SHORT).show();
                    return;
                }
//                !title_edt.getText().toString().trim().equals("") && !content_edt.getText().toString().trim().equals("") &&
//                        titleFile != null
                if (true) {
                    if (progressDialog == null) {
                        progressDialog = new AlertDialog.Builder(TouGao.this).create();
                    }
                    View view1 = LayoutInflater.from(this).inflate(R.layout.upload_dialog_progress, null);
                    progressDialog.setView(view1);
                    progressDialog.setCancelable(false);
                    progressDialog.setCanceledOnTouchOutside(false);

                    final TextView speed = (TextView) view1.findViewById(R.id.upload_dialog_speed);
                    final TextView percent = (TextView) view1.findViewById(R.id.upload_dialog_percent);
                    final ProgressBar progressBar = (ProgressBar) view1.findViewById(R.id.upload_dialog_progressBar);
                    progressBar.setMax(100);
                    progressDialog.show();
                    JSONObject js = new JSONObject();
                    try {
                        js.put("user_id", sp.getString("user_id", ""));
                        js.put("pet_name", sp.getString("pet_name", ""));
                        js.put("m_id", Constants.M_id);
                        js.put("title", title_edt.getText().toString());
                        js.put("contents", content_edt.getText().toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    httpParams = new HttpParams();
                    httpParams.put("key", ApisSeUtil.getKey());
                    httpParams.put("msg", ApisSeUtil.getMsg(js));
//                    if (mImages.contains("add")) {
//                        mImages.remove("add");
//                    }
                    for (int i = 0; i < mImages.size(); i++) {
                        if (mImages.get(i).equals("add")) {
                            continue;
                        }
                        Bitmap bm = ImageUtil.getImageThumbnail(mImages.get(i), ImageUtil.mWidth, ImageUtil.mHeight);
                        String t = System.currentTimeMillis() + ".jpg";
                        FileUtils.saveBitmap(bm, t);
                        httpParams.put(("image" + (i + 1)), new File(FileUtils.TEMPPAH, t));
                        bm = null;
                    }

                    Log.w(TAG, "onClick: httpparams-__+_+_+_+_+." + httpParams);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            OkGo.post(Constants.upload_image).params(httpParams).execute(new AbsCallback<HashMap<String, String>>() {
                                @Override
                                public HashMap<String, String> convertSuccess(Response response) throws Exception {
                                    return AnalyticalJSON.getHashMap(response.body().string());
                                }


                                @Override
                                public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                                    super.upProgress(currentSize, totalSize, progress, networkSpeed);
                                    progressBar.setProgress((int) (progress * 100));
                                    speed.setText(networkSpeed / 1000 + "kb/s");
                                    percent.setText((int) (progress * 100) + "%");
                                }

                                @Override
                                public void onAfter(HashMap<String, String> map, Exception e) {
                                    super.onAfter(map, e);
                                    if (progressDialog != null && progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                        progressDialog = null;
                                    }
                                    try {
                                        Log.e(TAG, "onAfter: 临时文件夹大小" + FileUtils.getFileSize(new File(FileUtils.TEMPPAH)));
                                    } catch (Exception e1) {
                                        e1.printStackTrace();
                                    }
                                    try {
                                        Log.e(TAG, "onAfter: 临时文件夹大小" + FileUtils.getFileSize(new File(FileUtils.TEMPPAH)));
                                    } catch (Exception e1) {
                                        e1.printStackTrace();
                                    }
                                }


                                @Override
                                public void onSuccess(HashMap<String, String> map, Call call, Response response) {

                                    if (map != null) {
                                        if ("000".equals(map.get("code"))) {
                                            progressDialog.dismiss();
                                            Toast.makeText(TouGao.this, "上传成功,请等待审核", Toast.LENGTH_SHORT).show();
                                            isCommited = true;
                                            finish();
                                        } else if ("004".equals(map.get("code"))) {
                                            Toast.makeText(TouGao.this, "上传失败，请稍后重试", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        progressDialog.dismiss();
                                        progressDialog = null;
                                        Toast.makeText(TouGao.this, "信息提交失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onError(Call call, Response response, Exception e) {
                                    super.onError(call, response, e);
                                    Log.e(TAG, "onError: 错误信息:" + e);
                                    Toast.makeText(TouGao.this, "信息提交失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                                }


                            });
                        }
                    }).start();
                }
                break;
            case R.id.title_back:
                finish();
                break;
//            case R.id.tougao_addTitleImg:
//                choosePic();
//                break;
//            case R.id.tougao_addFile:
//                //添加附件
//                if (p == null) {
//                    view = LayoutInflater.from(TouGao.this).inflate(R.layout.choose_video_or_audio, null);
//                    p = new PopupWindow(view);
//                    p.setOutsideTouchable(true);
//                    p.setWidth((getResources().getDisplayMetrics().widthPixels) - DimenUtils.dip2px(mApplication.getInstance(), 20));
//                    p.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
//                    p.setBackgroundDrawable(getResources().getDrawable(R.drawable.window_bg));
//                    p.setTouchable(true);
//                    p.setAnimationStyle(R.style.dialogWindowAnim);
//                }
//                view.findViewById(R.id.choose_layout).setVisibility(View.VISIBLE);
//                t = (TextView) view.findViewById(R.id.textview_pressToAudio);
//                t.setVisibility(View.GONE);
//                view.findViewById(R.id.choose_locale_file).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        chooseVideoAndAudio();
//                        p.dismiss();
//                    }
//                });
//                view.findViewById(R.id.take_camera).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        int p1 = PermissionChecker.checkSelfPermission(TouGao.this, Manifest.permission.CAMERA);
//                        int p2 = PermissionChecker.checkSelfPermission(TouGao.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//                        int p3 = PermissionChecker.checkSelfPermission(TouGao.this, Manifest.permission.RECORD_AUDIO);
//                        if (p1 != PackageManager.PERMISSION_GRANTED || p3 != PackageManager.PERMISSION_GRANTED) {
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, TAKEPICTURE);
//                                return;
//                            }
//                        }
//                        Intent intent = new Intent(TouGao.this, RecordVideoActivity.class);
//                        startActivityForResult(intent, 000);
////                        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
////                        if (intent.resolveActivity(getPackageManager()) != null) {
////                            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0.8);
////                            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);
////                            videoFile = new File(FileUtils.TEMPPAH, System.currentTimeMillis() + ".mp4");
////                            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(videoFile));
////                            startActivityForResult(intent, 6666);
////                        }
//                        p.dismiss();
//                    }
//                });
//
//                view.findViewById(R.id.take_audio).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        view.findViewById(R.id.choose_layout).setVisibility(View.GONE);
//                        t.setVisibility(View.VISIBLE);
//                        t.setOnTouchListener(new View.OnTouchListener() {
//                            @Override
//                            public boolean onTouch(View v, MotionEvent event) {
//                                int p1 = PermissionChecker.checkSelfPermission(TouGao.this, Manifest.permission.RECORD_AUDIO);
//                                int p2 = PermissionChecker.checkSelfPermission(TouGao.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//                                if (!(p1 == PackageManager.PERMISSION_GRANTED) || !(p2 == PackageManager.PERMISSION_GRANTED)) {
//                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
//                                                0x00);
//                                    }
//                                    return false;
//                                } else {
//                                    ra = recordAudio.getInstance();
//                                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                                        Log.w(TAG, "onTouch: 按下   路径：" + voicePath);
//                                        startTime = System.currentTimeMillis();
//                                        voicedialog = new mDialog(TouGao.this, R.style.MyDialog);
//                                        View view2 = LayoutInflater.from(mApplication.getInstance()).inflate(R.layout.alert_voice, null);
//                                        voicedialog.setView(view2);
//                                        voicedialog.show();
//                                        Window w = voicedialog.getWindow();
//                                        w.setGravity(Gravity.CENTER);
//                                        WindowManager.LayoutParams wl = w.getAttributes();
//                                        wl.width = DimenUtils.dip2px(mApplication.getInstance(), 200);
//                                        wl.height = DimenUtils.dip2px(mApplication.getInstance(), 200);
//                                        w.setDimAmount(0f);
//                                        w.setAttributes(wl);
//                                        img = (ImageView) view2.findViewById(R.id.alert_voice_image);
//                                        text = (TextView) view2.findViewById(R.id.alert_voice_text);
//                                        img.setImageResource(R.drawable.ic_settings_voice_white_48dp);
//                                        text.setText("语音录制中...");
//                                        t.setText("松开手指，结束录制");
//                                        t.setBackgroundColor(Color.parseColor("#909b9b9b"));
//                                        //开始录制语音
//                                        voicePath = ra.getPath() + "/" + startTime + ".mp3";
//                                        ra.startRecord(voicePath);
//                                        timer = new Timer("voice");
//                                        timer.schedule(new TimerTask() {
//                                            @Override
//                                            public void run() {
//                                                times++;
//                                                Log.w(TAG, "handleMessage: times____>" + times);
//                                                mHandler.obtainMessage(0x00, text).sendToTarget();
//                                            }
//                                        }, 0, 1000);
//
//                                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
//                                        //发送语音
//                                        Log.w(TAG, "onTouch: 抬起    路径：" + voicePath);
//                                        t.setBackgroundColor(Color.WHITE);
//                                        if (times <= 1) {
//                                            Toast.makeText(TouGao.this, "录制时间过短", Toast.LENGTH_SHORT).show();
//                                            if (ra.mRecorder != null && ra.isRecording) {
//                                                try {
//                                                    ra.stopRecord();
//                                                } catch (Exception e) {
//                                                    ra.mRecorder = null;
//                                                }
//                                                new File(voicePath).delete();
//                                            }
//
//                                        } else if (ra.mRecorder != null && ra.isRecording) {
//                                            ra.stopRecord();
//                                            showInfoThenUploadFile(voicePath, times + "");
//                                            t.setVisibility(View.GONE);
//                                            view.findViewById(R.id.choose_layout).setVisibility(View.VISIBLE);
//                                            p.dismiss();
//                                        }
//                                        t.setText("按  住  说  话");
//                                        voicedialog.dismiss();
//                                        startTime = 0;
//                                        times = 0;
//                                        if (timer != null)
//                                            timer.cancel();
//                                        timer = null;
//                                    } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
//                                        Log.w(TAG, "onTouch: cancel    路径：" + voicePath);
//                                        if (voicedialog != null && voicedialog.isShowing()) {
//                                            voicedialog.dismiss();
//                                        }
//                                        if (times <= 1) {
//                                            if (ra.mRecorder != null && ra.isRecording) {
//                                                try {
//                                                    ra.stopRecord();
//                                                } catch (Exception e) {
//                                                    ra.mRecorder = null;
//                                                }
//                                                new File(voicePath).delete();
//                                            }
//
//                                        } else if (ra.mRecorder != null && ra.isRecording) {
//                                            ra.stopRecord();
////                                            showInfoThenUploadFile(voicePath, times + "");
//                                            t.setVisibility(View.GONE);
//                                            view.findViewById(R.id.choose_layout).setVisibility(View.VISIBLE);
////                                            p.dismiss();
//                                        }
//                                        t.setText("按  住  说  话");
//                                        startTime = 0;
//                                        times = 0;
//                                        timer.cancel();
//                                        timer = null;
//                                    }
//
//                                    return true;
//                                }
//
//                            }
//                        });
//
//                    }
//                });
//                p.showAtLocation(v, Gravity.BOTTOM, 0, 0);
//                break;
        }
    }

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            if (msg.what == 0x00) {
//                TextView t = (TextView) msg.obj;
////                if (times % 4 == 0) {
////                    t.setText("语音录制中....");
////                } else if (times % 3 == 0) {
////                    t.setText("语音录制中...");
////                } else if (times % 2 == 0) {
////                    t.setText("语音录制中..");
////                } else {
////                    t.setText("语音录制中.");
////                }
//                t.setText(" " + times + "'");
//                if (times >= 60) {
//                    ra.stopRecord();
//                    Toast.makeText(TouGao.this, "最长录制1分钟语音", Toast.LENGTH_SHORT).show();
//                    p.dismiss();
//                    t.setText("按  住  说  话");
//                    t.setVisibility(View.GONE);
//                    view.findViewById(R.id.choose_layout).setVisibility(View.VISIBLE);
//                    voicedialog.dismiss();
//                    voicedialog = null;
//                    showInfoThenUploadFile(voicePath, times + "");
//                    times = 0;
//                    timer.cancel();
//                    timer = null;
//                    startTime = 0;
//                }
//            }
        }
    };

    @Override
    public void onBackPressed() {
        if (p != null && p.isShowing()) {
            p.dismiss();
            return;
        }
        super.onBackPressed();
    }

    //选择照片或照相
    private void choosePic() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(mApplication.getInstance()).inflate(R.layout.dialog_album_camera, null);
        view.findViewById(R.id.photograph).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int permission1 = ContextCompat.checkSelfPermission(mApplication.getInstance(), Manifest.permission.CAMERA);
                    int permissionCheck = ContextCompat.checkSelfPermission(mApplication.getInstance(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (permission1 != PackageManager.PERMISSION_GRANTED && permissionCheck != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, TAKEPICTURE);
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
        window.setWindowAnimations(R.style.dialogWindowAnim);
        window.setBackgroundDrawableResource(R.color.vifrification);
        dialog.show();
    }

    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CHOOSEPICTUE:
                    Bitmap bm = null;
                    pictureUri = data.getData();// 选择照片的Uri 可能为null
                    if (pictureUri != null) {
                        //上传头像
                        path = ImageUtil.getImageAbsolutePath(mApplication.getInstance(), pictureUri);
//                        Glide.with(this).load(path).override(DimenUtils.dip2px(this, 120), DimenUtils.dip2px(this, 120))
//                                .fitCenter().into(titleImg);
                        bm = ImageUtil.getImageThumbnail(path, ImageUtil.mWidth, ImageUtil.mHeight);
                        ////
                        if (path != null) {
                            String tempTime = System.currentTimeMillis() + ".jpg";
                            FileUtils.saveBitmap(bm, tempTime);
                            titleFile = new File(FileUtils.TEMPPAH, tempTime);
                            Log.w(TAG, "onActivityResult: ___________." + new File(FileUtils.TEMPPAH + tempTime).length()
                                    + "内存占用:" + bm.getByteCount());

                        }


                    } else {
                        Toast.makeText(mApplication.getInstance(), "上传失败,请重新尝试", Toast.LENGTH_SHORT).show();
                    }


                    break;
                case TAKEPICTURE:
                    if (pictureUri != null) {
                        //上传头像
                        path = ImageUtil.getRealPathFromURI(this, pictureUri);
                        bm = ImageUtil.getImageThumbnail(path, ImageUtil.mWidth, ImageUtil.mHeight);
//                        Glide.with(this).load(path).asBitmap().override(DimenUtils.dip2px(this, 120), DimenUtils.dip2px(this, 120))
//                                .fitCenter().into(new BitmapImageViewTarget(titleImg) {
//                            @Override
//                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                                super.onResourceReady(resource, glideAnimation);
//                                Log.w(TAG, "onResourceReady: glide加载的内存占用" + resource.getByteCount() + "宽：" + resource.getWidth() +
//                                        "高：" + resource.getHeight());
//                            }
//                        });
                        ////
                        if (path != null) {
                            String tempTime = System.currentTimeMillis() + ".jpg";
                            FileUtils.saveBitmap(bm, tempTime);
                            titleFile = new File(FileUtils.TEMPPAH, tempTime);
                            Log.w(TAG, "onActivityResult: ___________." + new File(FileUtils.TEMPPAH + tempTime).length()
                                    + "内存占用:" + bm.getByteCount());
                        }

                    } else {
                        Toast.makeText(mApplication.getInstance(), "上传失败,请重新尝试", Toast.LENGTH_SHORT).show();
                    }
                    break;
//                case ChooseVideoAndAudio:
//                    Log.w(TAG, "onActivityResult: 视频返回信息：" + data);
//                    Log.w(TAG, "onActivityResult: 视频返回信息1：" + data.getData());
//                    if (data.getData() != null) {
//                        String path = data.getData().getPath();
//                        Log.w(TAG, "onActivityResult: 路径---》" + path);
//                        showInfoThenUploadFile(path, "");
//                    } else {
//                        //未获取到文件信息
//                    }
//
//                    break;
//                case 6666:
//                    Uri uri = data.getData();
//                    showInfoThenUploadFile(uri.getPath(), "");
//                    Log.w(TAG, "onActivityResult: " + data.getData());
//                    break;
            }
        } else if (resultCode == 111) {
            ArrayList<String> list = data.getStringArrayListExtra("array");
            if (list != null) {
                if (mImages.size() < allowChooseNum) {
                    if (((mImages.size() - 1) + list.size() < allowChooseNum)) {
                        if (mImages.size() > 1) {
                            mImages.addAll(mImages.size() - 1, list);
                        } else {
                            mImages.addAll(0, list);
                        }
                    } else {
                        mImages.remove(mImages.size() - 1);
                        mImages.addAll(list);
                    }
                    adpter.notifyDataSetChanged();
                } else {
                    mImages.remove(mImages.size() - 1);
                    mImages.addAll(list);
                    adpter.notifyDataSetChanged();
                }
            } else {
                Toast.makeText(TouGao.this, "系统错误，获取数据失败", Toast.LENGTH_SHORT).show();
            }

        }
//        else if (resultCode == RES_CODE) {
//            String path = data.getStringExtra("path");
//            Log.w(TAG, "RES_CODE: " + path + "     file_____>" + videoFile + "    nuM______" + addFileLayout.getChildCount());
//            if (path != null) {
//                if (videoFile == null && addFileLayout.getChildCount() != 2) {
//                    showInfoThenUploadFile(path, "");
//                }
//            }
    }


//    }

//    /**
//     * 显示文件信息并上传
//     *
//     * @param path
//     */
//    private void showInfoThenUploadFile(final String path, String time) {
//
//        addFile.setVisibility(View.GONE);
//        String name = path.substring(path.lastIndexOf("/") + 1);
//        final RelativeLayout layout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.file_item, null);
//        ((TextView) layout.findViewById(R.id.file_item_name)).setText(name);
//        final TextView t = (TextView) layout.findViewById(R.id.file_item_status);
//        final ImageView cancle = (ImageView) layout.findViewById(R.id.file_item_cancle);
//        if (time.equals("")) {
//            layout.setTag("video");
//            videoFile = new File(path);
//            t.setText("文件大小：" + (videoFile.length() / 1000) + "KB");
//            audioFile = null;
//            MediaPlayer mp = new MediaPlayer();
//            try {
//                mp.setDataSource(path);
//                mp.prepare();
//                mp.start();
//                DecimalFormat df = new DecimalFormat(".0");
//                float f = mp.getDuration() / 1000f;
//                String duration = df.format(f);
//                Log.w(TAG, "getTime: 时长————————？》" + duration);
////                if (Float.valueOf(duration) > MAX_VIDEO_TIME) {
////                    Toast.makeText(TouGao.this, "暂只支持时长不超过30s的短视频", Toast.LENGTH_SHORT).show();
////                    return;
////                }
//                mp.reset();
//                mp.release();
//                mp = null;
//                ((TextView) layout.findViewById(R.id.file_item_time)).setText("时长:" + duration + "秒");
//            } catch (Exception e) {
//                e.printStackTrace();
//                Toast.makeText(TouGao.this, "视频信息获取失败，请检查视频是否损坏或不存在", Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            layout.setTag("voice");
//            audioFile = new File(path);
//            videoFile = null;
//            ((TextView) layout.findViewById(R.id.file_item_time)).setText("时长:" + time + "秒");
//            t.setText("文件大小：" + (audioFile.length() / 1000) + "KB");
//        }
//        cancle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (ra != null && ra.mPlayer != null && ra.mPlayer.isPlaying()) {
//                    ra.stopPlay();
//                }
//                addFileLayout.removeView((View) v.getParent());
//                addFile.setVisibility(View.VISIBLE);
//                videoFile = null;
//                audioFile = null;
//                voicePath = "";
//                if (ra != null && ra.mPlayer != null) {
//                    ra.mPlayer.reset();
//                }
//            }
//        });
//        layout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (v.getTag().toString().equals("voice")) {
//                    Log.w(TAG, "onClick: 播放路径+" + path);
//                    if (ra.mPlayer == null) {
//                        Toast.makeText(TouGao.this, "再按一次关闭试听", Toast.LENGTH_SHORT).show();
//                        ra.startPlay(path);
//                    } else if (!ra.mPlayer.isPlaying()) {
////                        ra.mPlayer.start();
//                        ra.startPlay(path);
//                    } else {
//                        ra.stopPlay();
//                    }
//                }
//            }
//        });
//        addFileLayout.addView(layout, 0);
//
//    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CHOOSEPICTUE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    choosePhotoAlbum();
                } else {
                    Toast.makeText(mApplication.getInstance(), "无相册权限将无法使用该功能", Toast.LENGTH_SHORT).show();
                }
                break;
            case TAKEPICTURE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(mApplication.getInstance(), "无相机权限将无法使用该功能", Toast.LENGTH_SHORT).show();
                }
                break;
            case 0x00:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(TouGao.this, "录音权限已开启", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    /**
     * 调用相机
     */
    private void chooseCamera() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
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
            pictureUri = getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, attrs);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);// 指定照片的位置
            startActivityForResult(takePictureIntent, TAKEPICTURE);
        } else {
            Toast.makeText(TouGao.this, "请确认插入SD卡", Toast.LENGTH_SHORT).show();
        }
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

    /**
     * 本地视频和语音
     */
    private void chooseVideoAndAudio() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setType("video/*,audio/*");

        startActivityForResult(intent, ChooseVideoAndAudio);
    }

    /**
     * 本地音频
     */
    private void chooseAudio() {
        ///调用系统音频相关
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setType("audio/*");

    }


    /**
     * 删除gridView图片时的回调
     *
     * @param positon
     */
    @Override
    public void onCancle(int positon) {
        if (mImages.size() == allowChooseNum && !mImages.get(mImages.size() - 1).equals("add")) {
            Log.w(TAG, "onCancle: mimages" + mImages.toString());
            Log.w(TAG, "onCancle: fsdfsd");
            mImages.add("add");
            adpter.setmImgs(mImages);
        }
        mImages.remove(positon);
        adpter.notifyDataSetChanged();
    }


}
