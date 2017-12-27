package com.qianfujiaoyu.TouGao;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.HttpParams;
import com.qianfujiaoyu.Activitys.Home_Class;
import com.qianfujiaoyu.R;
import com.qianfujiaoyu.Utils.AnalyticalJSON;
import com.qianfujiaoyu.Utils.ApisSeUtil;
import com.qianfujiaoyu.Utils.Constants;
import com.qianfujiaoyu.Utils.DimenUtils;
import com.qianfujiaoyu.Utils.FileUtils;
import com.qianfujiaoyu.Utils.ImageUtil;
import com.qianfujiaoyu.Utils.Network;
import com.qianfujiaoyu.Utils.PreferenceUtil;
import com.qianfujiaoyu.Utils.ProgressUtil;
import com.qianfujiaoyu.Utils.StatusBarCompat;
import com.qianfujiaoyu.Utils.TimeUtils;
import com.qianfujiaoyu.Utils.mApplication;
import com.qianfujiaoyu.View.mAudioManager;
import com.qianfujiaoyu.View.mAudioView;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/11/3.
 */
public class TG_Detail extends AppCompatActivity implements View.OnClickListener, TouGaoGridAdapter.oncCancleListener {
    //   private ArrayList<HashMap<String ,String > > numList;
    private static final String TAG = "TG_Detail";
    private static final int RES_CODE = 1111;
    private ImageView back;
    private TextView title, time, name, commit, fabu;
    private String Id;//稿件Id;
    private EditText title_edt, content_edt;
    private ImageView head;
    private GridView grid;
    private TouGaoGridAdapter adpter;
    private ArrayList<String> list;
    private String path1 = "", path2 = "", path3 = "";
    public File file1, file2, file3;
    private String videoPath, audioPath;
    //选择的图片集合
//    private List<HashMap<String  ,String >> mImages = new ArrayList<>();
    private LinearLayout layout_file;
    private View view;// TODO: 2016/12/29 音视频附件window
    private PopupWindow p;
    private static final int CHOOSEPICTUE = 2;//相册
    private static final int TAKEPICTURE = 1;//相机
    private static final int ChooseVideoAndAudio = 3;//视频
    public String path;
    private HttpParams httpParams;
    private SharedPreferences sp;
    private int screenWidth;
    private int a = 0;
    //    //选择的图片集合
//    private ArrayList<String> mImages = new ArrayList<>();
    private ImageView addFile;//添加附件按钮
    //    private LinearLayout addFileLayout;//添加附件的空间
    private File titleFile, videoFile, audioFile;
    private static final int MAX_VIDEO_TIME = 30;
    //语音开始录制时间
    private long startTime;
    private Timer timer;
    private String voicePath;//输出地址
    private mDialog voicedialog;//录制语音时的提示dialog
    private ImageView img;//voicedialog中的图片
    private TextView text;//voicedialog中的文字
    private int times = 0;//计时
    private recordAudio ra;//录制语音对象
    private TextView t;//录音按钮
    private boolean isGrante = false;
    private TextView update;
    private RelativeLayout layout;
    BroadcastReceiver reciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String name = intent.getStringExtra("name");
            int pos = intent.getIntExtra("pos", 0);
            if (1 == pos) {
                file1 = new File(FileUtils.TEMPPAH, name);
            } else if (2 == pos) {
                file2 = new File(FileUtils.TEMPPAH, name);
            } else if (3 == pos) {
                file3 = new File(FileUtils.TEMPPAH, name);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tougao_detail);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
//        numList=new ArrayList<>();
        initView();
        checkNetwork();
        getData();
    }

    /**
     * 检查网络状态
     */
    private void checkNetwork() {
        if (!Network.HttpTest(this)) {
            Toast.makeText(this, "网络连接失败，请检查网络", Toast.LENGTH_SHORT);
            return;
        }
    }

    /**
     * 获取详情数据
     */
    private void getData() {
        ProgressUtil.show(this, "", "正在加载，请稍后");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject js=new JSONObject();
                    try {
                        js.put("id", Id);
                        js.put("m_id",Constants.M_id);
                        js.put("user_id", PreferenceUtil.getUserIncetance(mApplication.getInstance()).getString("user_id",""));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m=ApisSeUtil.i(js);
                    String data = OkGo.post(Constants.Zixun_Detail_IP)
                            .params("key",m.K())
                            .params("msg",m.M()).execute().body().string();
                    if (!data.equals("")) {
                        final HashMap<String, String> map = AnalyticalJSON.getHashMap(data);
                        if (map != null) {
                            Log.w(TAG, "run: map获取的数据" + map);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    name.setText(PreferenceUtil.getUserIncetance(TG_Detail.this).getString("pet_name", ""));// TODO: 2016/12/26 有问题
                                    time.setText(TimeUtils.getTrueTimeStr(map.get("time")));
                                    title_edt.setText(map.get("title"));
                                    content_edt.setText(map.get("contents"));
//                                    Glide.with(TG_Detail.this).load(map.get("image1")).override(DimenUtils.dip2px(getApplicationContext(),180)
//                                    ,DimenUtils.dip2px(getApplicationContext(),180)).fitCenter()
//                                            .placeholder(R.drawable.place_holder2).into(head);
                                    try {
                                        JSONArray js=new JSONArray(map.get("image"));
                                        for(int i=0;i<js.length();i++){
                                             switch (i){
                                                 case 0:
                                                     list.add(((JSONObject) js.get(i)).getString("url"));
                                                     path1 = map.get("url");
                                                     break;
                                                 case 1:
                                                     list.add(((JSONObject) js.get(i)).getString("url"));
                                                     path2 = map.get("url");
                                                     break;
                                                 case 2:
                                                     list.add(((JSONObject) js.get(i)).getString("url"));
                                                     path3 = map.get("url");
                                                     break;
                                             }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    grid.setAdapter(adpter);
                                    if (list.size() == 0) {
                                        list.add("add");
                                        adpter.notifyDataSetChanged();
                                    }
                                    if (list.size() != 0) {
                                        adpter.setmImgs(list);
                                        adpter.notifyDataSetChanged();
                                    }
                                    if (!map.get("options").equals("") ) {
                                        if (map.get("options").endsWith(".mp4")) {
                                            //视频
                                            videoPath = map.get("options");
                                            OkGo.get(videoPath).execute(new FileCallback(FileUtils.TEMPPAH, System.currentTimeMillis() + "") {
                                                @Override
                                                public void onSuccess(File file, Call call, Response response) {
                                                    videoFile = file;
                                                }
                                            });
                                            JCVideoPlayerStandard j = new JCVideoPlayerStandard(TG_Detail.this);
                                            LinearLayout.LayoutParams l1 = new LinearLayout.LayoutParams(DimenUtils.dip2px(TG_Detail.this, 310), DimenUtils.dip2px(TG_Detail.this, 180));
                                            l1.gravity = Gravity.CENTER_HORIZONTAL;
                                            j.setLayoutParams(l1);
                                            layout_file.addView(j);
                                            j.setUp(map.get("options"), JCVideoPlayer.SCREEN_LAYOUT_NORMAL, "视频");
                                            Glide.with(TG_Detail.this).load(map.get("image1")).override(DimenUtils.dip2px(TG_Detail.this, 310), DimenUtils.dip2px(TG_Detail.this, 180)).centerCrop()
                                                    .into(j.thumbImageView);
                                        } else if (map.get("options").endsWith(".mp3")) {
                                            //音频
                                            audioPath = map.get("options");
                                            Log.w(TAG, "onBindViewHolder: 显示音频");
                                            OkGo.get(audioPath).execute(new FileCallback(FileUtils.TEMPPAH, System.currentTimeMillis() + "") {
                                                @Override
                                                public void onSuccess(File file, Call call, Response response) {
                                                    audioFile = file;
                                                }

                                            });
                                            final mAudioView mAudioView = new mAudioView(TG_Detail.this);
                                            mAudioView.setOnImageClickListener(new mAudioView.onImageClickListener() {
                                                @Override
                                                public void onImageClick(final mAudioView v) {
                                                    if (mAudioManager.getAudioView() != null && mAudioManager.getAudioView().isPlaying()) {
                                                        mAudioManager.release();
                                                        mAudioManager.getAudioView().setPlaying(false);
                                                        mAudioManager.getAudioView().resetAnim();
                                                        if (v == mAudioManager.getAudioView()) {
                                                            return;
                                                        }
                                                    }
                                                    if (!mAudioView.isPlaying()) {
                                                        Log.w(TAG, "onImageClick: 开始播放");
                                                        mAudioManager.playSound(v, map.get("options"), new MediaPlayer.OnCompletionListener() {
                                                            @Override
                                                            public void onCompletion(MediaPlayer mp) {
                                                                mAudioView.resetAnim();
                                                            }
                                                        }, new MediaPlayer.OnPreparedListener() {
                                                            @Override
                                                            public void onPrepared(MediaPlayer mp) {
                                                                mAudioView.setTime(mAudioManager.mMediaplayer.getDuration() / 1000);
                                                            }
                                                        });

                                                    } else {
                                                        Log.w(TAG, "onImageClick: 停止播放");
                                                        mAudioManager.release();
                                                    }

                                                }
                                            });
                                            layout_file.addView(mAudioView);
                                        }

                                    } else {
                                        update.setVisibility(View.GONE);
                                        layout_file.addView(addFile);
                                    }

                                    ProgressUtil.dismiss();
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            p.dismiss();
                            Toast.makeText(TG_Detail.this, "加载失败，请退出页面重试", Toast.LENGTH_SHORT).show();
                        }
                    });
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x00) {
                TextView t = (TextView) msg.obj;
//                if (times % 4 == 0) {
//                    t.setText("语音录制中....");
//                } else if (times % 3 == 0) {
//                    t.setText("语音录制中...");
//                } else if (times % 2 == 0) {
//                    t.setText("语音录制中..");
//                } else {
//                    t.setText("语音录制中.");
//                }
                t.setText(" " + times + "'");
                if (times >= 60) {
                    ra.stopRecord();
                    Toast.makeText(TG_Detail.this, "最长录制1分钟语音", Toast.LENGTH_SHORT).show();
                    p.dismiss();
                    t.setText("按  住  说  话");
                    t.setVisibility(View.GONE);
                    view.findViewById(R.id.choose_layout).setVisibility(View.VISIBLE);
                    voicedialog.dismiss();
                    voicedialog = null;
                    showInfoThenUploadFile(voicePath, times + "");
                    times = 0;
                    timer.cancel();
                    timer = null;
                    startTime = 0;
                }
            }
        }
    };

    /**
     * 本地视频和语音
     */
    private void chooseVideoAndAudio() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setType("video/*,audio/*");

        startActivityForResult(intent, ChooseVideoAndAudio);
    }

    /**
     * 显示文件信息并上传
     *
     * @param path
     */
    private void showInfoThenUploadFile(final String path, String time) {
        update.setVisibility(View.GONE);
        addFile.setVisibility(View.GONE);
        String name = path.substring(path.lastIndexOf("/") + 1);
        layout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.file_item, null);
        ((TextView) layout.findViewById(R.id.file_item_name)).setText(name);
        final TextView t = (TextView) layout.findViewById(R.id.file_item_status);
        final ImageView cancle = (ImageView) layout.findViewById(R.id.file_item_cancle);
        if (time.equals("")) {
            layout.setTag("video");
            videoFile = new File(path);
            t.setText("文件大小：" + (videoFile.length() / 1000) + "KB");
            audioFile = null;
            MediaPlayer mp = new MediaPlayer();
            try {
                mp.setDataSource(path);
                mp.prepare();
                mp.start();
                DecimalFormat df = new DecimalFormat(".0");
                float f = mp.getDuration() / 1000f;
                String duration = df.format(f);
                Log.w(TAG, "getTime: 时长————————？》" + duration);
                if (Float.valueOf(duration) > MAX_VIDEO_TIME) {
                    Toast.makeText(TG_Detail.this, "暂只支持时长不超过30s的短视频", Toast.LENGTH_SHORT).show();
                    return;
                }
                mp.reset();
                mp.release();
                mp = null;
                ((TextView) layout.findViewById(R.id.file_item_time)).setText("时长:" + duration + "秒");
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(TG_Detail.this, "视频信息获取失败，请检查视频是否损坏或不存在", Toast.LENGTH_SHORT).show();
            }
        } else {
            layout.setTag("voice");
            audioFile = new File(path);
            videoFile = null;
            ((TextView) layout.findViewById(R.id.file_item_time)).setText("时长:" + time + "秒");
            t.setText("文件大小：" + (audioFile.length() / 1000) + "KB");
        }
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ra != null && ra.mPlayer != null && ra.mPlayer.isPlaying()) {
                    ra.stopPlay();
                }
                update.setVisibility(View.GONE);
                layout_file.removeView((View) v.getParent());
                addFile.setVisibility(View.VISIBLE);
                videoFile = null;
                audioFile = null;
                voicePath = "";
                if (ra != null && ra.mPlayer != null) {
                    ra.mPlayer.reset();
                }
            }
        });
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getTag().toString().equals("voice")) {
                    Log.w(TAG, "onClick: 播放路径+" + path);
                    if (ra.mPlayer == null) {
                        Toast.makeText(TG_Detail.this, "再按一次关闭试听", Toast.LENGTH_SHORT).show();
                        ra.startPlay(path);
                    } else if (!ra.mPlayer.isPlaying()) {
//                        ra.mPlayer.start();
                        ra.startPlay(path);
                    } else {
                        ra.stopPlay();
                    }
                }
            }
        });
        layout_file.addView(layout);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(reciever);
        FileUtils.deleteFile(new File(FileUtils.TEMPPAH));
    }

    private void initView() {
        IntentFilter in = new IntentFilter("TG");
        registerReceiver(reciever, in);
        addFile = new ImageView(TG_Detail.this);
        addFile.setLayoutParams(new ViewGroup.LayoutParams(DimenUtils.dip2px(TG_Detail.this, 100), DimenUtils.dip2px(TG_Detail.this, 100)));
        addFile.setImageBitmap(ImageUtil.readBitMap(TG_Detail.this, R.drawable.audio_and_video));
        addFile.setBackgroundResource(R.drawable.person_add_pic_sel);
        addFile.setPadding(DimenUtils.dip2px(TG_Detail.this, 10), DimenUtils.dip2px(TG_Detail.this, 10), DimenUtils.dip2px(TG_Detail.this, 10)
                , DimenUtils.dip2px(TG_Detail.this, 10));
        update = (TextView) findViewById(R.id.update);
        update.setOnClickListener(this);
        Id = getIntent().getStringExtra("id");
        back = (ImageView) findViewById(R.id.back);
        back.setImageResource(R.drawable.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        title = (TextView) findViewById(R.id.title);
        title.setText("动态管理详情");
        list = new ArrayList<String>();
        name = (TextView) findViewById(R.id.tougao_detail_name);
        time = (TextView) findViewById(R.id.tougao_detail_time);
        title_edt = (EditText) findViewById(R.id.tougao_detail_title);
        content_edt = (EditText) findViewById(R.id.tougao_detail_content);
        head = (ImageView) findViewById(R.id.tougao_detail_head);
        layout_file = (LinearLayout) findViewById(R.id.tougao_detail_fujian);
        commit = (TextView) findViewById(R.id.tougao_detail_commit);
//        fabu= (TextView) findViewById(R.id.tougao_detail_fabu);
        commit.setOnClickListener(this);
//        fabu.setOnClickListener(this);
        screenWidth = getResources().getDisplayMetrics().widthPixels;

        grid = (GridView) findViewById(R.id.tougao_detail_iamges);
        adpter = new TouGaoGridAdapter(this, list, true);
        adpter.setOncCancleListener(this);
        adpter.setTG(true);
        grid.setAdapter(adpter);

        String type = getIntent().getStringExtra("type");
        if (type != null && type.equals("Mine")) {
            if (!PreferenceUtil.getUserIncetance(getApplicationContext()).getString("user_status", "").equals("2")) {
                title_edt.setEnabled(false);
                content_edt.setEnabled(false);
                commit.setVisibility(View.GONE);
                fabu.setVisibility(View.GONE);
            }
        }
        addFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //添加附件
                if (p == null) {
                    view = LayoutInflater.from(TG_Detail.this).inflate(R.layout.choose_video_or_audio, null);
                    p = new PopupWindow(view);
                    p.setOutsideTouchable(true);
                    p.setWidth((getResources().getDisplayMetrics().widthPixels) - DimenUtils.dip2px(mApplication.getInstance(), 20));
                    p.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                    p.setBackgroundDrawable(getResources().getDrawable(R.drawable.window_bg));
                    p.setTouchable(true);
                    p.setAnimationStyle(R.style.dialogWindowAnim);
                }
                view.findViewById(R.id.choose_layout).setVisibility(View.VISIBLE);
                t = (TextView) view.findViewById(R.id.textview_pressToAudio);
                t.setVisibility(View.GONE);
                view.findViewById(R.id.choose_locale_file).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chooseVideoAndAudio();
                        p.dismiss();
                    }
                });
                view.findViewById(R.id.take_camera).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int p1 = PermissionChecker.checkSelfPermission(TG_Detail.this, Manifest.permission.RECORD_AUDIO);
                        int p3 = PermissionChecker.checkSelfPermission(TG_Detail.this, Manifest.permission.CAMERA);
                        int p2 = PermissionChecker.checkSelfPermission(TG_Detail.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        if (p1!= PackageManager.PERMISSION_GRANTED||p2!=PackageManager.PERMISSION_GRANTED||p3!=PackageManager.PERMISSION_GRANTED) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(new String[]{Manifest.permission.CAMERA}, TAKEPICTURE);
                                return;
                            }
                        }
                        Intent intent = new Intent(TG_Detail.this, RecordVideoActivity.class);
                        startActivityForResult(intent, 000);
                        p.dismiss();
                    }
                });

                view.findViewById(R.id.take_audio).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        view.findViewById(R.id.choose_layout).setVisibility(View.GONE);
                        t.setVisibility(View.VISIBLE);
                        t.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                int p1 = PermissionChecker.checkSelfPermission(TG_Detail.this, Manifest.permission.RECORD_AUDIO);
                                int p2 = PermissionChecker.checkSelfPermission(TG_Detail.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                                if (!(p1 == PackageManager.PERMISSION_GRANTED) || !(p2 == PackageManager.PERMISSION_GRANTED)) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
                                                0x00);
                                    }
                                    return false;
                                } else {
                                    ra = recordAudio.getInstance();
                                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                        Log.w(TAG, "onTouch: 按下   路径：" + voicePath);
                                        startTime = System.currentTimeMillis();
                                        voicedialog = new mDialog(TG_Detail.this, R.style.MyDialog);
                                        View view2 = LayoutInflater.from(mApplication.getInstance()).inflate(R.layout.alert_voice, null);
                                        voicedialog.setView(view2);
                                        voicedialog.show();
                                        Window w = voicedialog.getWindow();
                                        w.setGravity(Gravity.CENTER);
                                        WindowManager.LayoutParams wl = w.getAttributes();
                                        wl.width = DimenUtils.dip2px(mApplication.getInstance(), 200);
                                        wl.height = DimenUtils.dip2px(mApplication.getInstance(), 200);
                                        w.setDimAmount(0f);
                                        w.setAttributes(wl);
                                        img = (ImageView) view2.findViewById(R.id.alert_voice_image);
                                        text = (TextView) view2.findViewById(R.id.alert_voice_text);
                                        img.setImageResource(R.drawable.ic_settings_voice_white_48dp);
                                        text.setText("语音录制中...");
                                        t.setText("松开手指，结束录制");
                                        t.setBackgroundColor(Color.parseColor("#909b9b9b"));
                                        //开始录制语音
                                        voicePath = ra.getPath() + "/" + startTime + ".mp3";
                                        ra.startRecord(voicePath);
                                        timer = new Timer("voice");
                                        timer.schedule(new TimerTask() {
                                            @Override
                                            public void run() {
                                                times++;

                                                Log.w(TAG, "handleMessage: times____>" + times);
                                                mHandler.obtainMessage(0x00, text).sendToTarget();
                                            }
                                        }, 0, 1000);

                                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                                        //发送语音
                                        Log.w(TAG, "onTouch: 抬起    路径：" + voicePath);
                                        t.setBackgroundColor(Color.WHITE);
                                        if (times <= 1) {
                                            Toast.makeText(TG_Detail.this, "录制时间过短", Toast.LENGTH_SHORT).show();
                                            if (ra.mRecorder != null && ra.isRecording) {
                                                try {
                                                    ra.stopRecord();
                                                } catch (Exception e) {
                                                    ra.mRecorder = null;
                                                }
                                                new File(voicePath).delete();
                                            }

                                        } else if (ra.mRecorder != null && ra.isRecording) {
                                            ra.stopRecord();
                                            showInfoThenUploadFile(voicePath, times + "");
                                            t.setVisibility(View.GONE);
                                            view.findViewById(R.id.choose_layout).setVisibility(View.VISIBLE);
                                            p.dismiss();
                                        }
                                        t.setText("按  住  说  话");
                                        voicedialog.dismiss();
                                        startTime = 0;
                                        times = 0;
                                        if (timer != null)
                                            timer.cancel();
                                        timer = null;
                                    } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                                        Log.w(TAG, "onTouch: cancel    路径：" + voicePath);
                                        if (voicedialog != null && voicedialog.isShowing()) {
                                            voicedialog.dismiss();
                                        }
                                        if (times <= 1) {
                                            if (ra.mRecorder != null && ra.isRecording) {
                                                try {
                                                    ra.stopRecord();
                                                } catch (Exception e) {
                                                    ra.mRecorder = null;
                                                }
                                                new File(voicePath).delete();
                                            }

                                        } else if (ra.mRecorder != null && ra.isRecording) {
                                            ra.stopRecord();
//                                            showInfoThenUploadFile(voicePath, times + "");
                                            t.setVisibility(View.GONE);
                                            view.findViewById(R.id.choose_layout).setVisibility(View.VISIBLE);
//                                            p.dismiss();
                                        }
                                        t.setText("按  住  说  话");
                                        startTime = 0;
                                        times = 0;
                                        timer.cancel();
                                        timer = null;
                                    }

                                    return true;
                                }

                            }
                        });

                    }
                });
                p.showAtLocation(v, Gravity.BOTTOM, 0, 0);
            }
        });
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.update:

                layout_file.removeViewAt(layout_file.getChildCount()-1);
                addFile.setVisibility(View.VISIBLE);
                v.setVisibility(View.GONE);
                if(addFile.getParent()!=layout_file){
                    layout_file.addView(addFile);
                }
                break;
            case R.id.back:
                finish();
                break;
            case R.id.tougao_detail_commit://保存修改
                checkNetwork();
                if (title_edt.getText().toString().trim().equals("") || content_edt.getText().toString().trim().equals("") ||
                        (list.size() <= 1 && list.get(0).equals("add"))) {
                    Toast.makeText(this, "请将信息填写完整", Toast.LENGTH_SHORT).show();
                    return;
                }
                v.setEnabled(false);
                ProgressUtil.show(TG_Detail.this, "", "");
                final JSONObject js=new JSONObject();
                try {
                    js.put("id", Id);
                    js.put("title", title_edt.getText().toString().trim());
                    js.put("contents", content_edt.getText().toString().trim());
                    js.put("user_id", PreferenceUtil.getUserIncetance(TG_Detail.this).getString("user_id", ""));
                    js.put("m_id", Constants.M_id);
                    httpParams = new HttpParams();
                    if (list.contains("add")) {
                        list.remove("add");
                    }
                    if (file1 == null) {
                        js.put("image1", "");
                    } else {
                        httpParams.put("image1", file1);
                    }

                    js.put("image_1", path1);
                    if (file2 == null) {
                        js.put("image2", "");
                    } else {
                        httpParams.put("image2", file2);
                    }
                    js.put("image_2", path2);
                    if (file3 == null) {
                        js.put("image3", "");
                    } else {
                        httpParams.put("image3", file3);
                    }
                    js.put("image_3", path3);

                    if (videoFile == null) {
                        js.put("video", "");
                        js.put("video_1", videoPath);
                    } else {
                        httpParams.put("video", videoFile);
                        js.put("video_1", videoPath);
                    }
                    if (audioFile == null) {
                        js.put("audio", "");
                        js.put("audio_1", audioPath);
                    } else {
                        httpParams.put("audio", audioFile);
                        js.put("audio_1", audioPath);
                    }
                    Log.w(TAG, "onClick: 文件参数数组：" + httpParams);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final ApisSeUtil.M m=ApisSeUtil.i(js);


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            String data = OkGo.post(Constants.Archmage_change_tougao_IP)
                                    .params(httpParams)
                                    .params("key",m.K())
                                    .params("msg",m.M())
                                    .execute().body().string();
                            if (!data.equals("")) {
                                final HashMap<String, String> m = AnalyticalJSON.getHashMap(data);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (m != null && "000".equals(m.get("code"))) {
                                            Toast.makeText(TG_Detail.this, "信息修改成功", Toast.LENGTH_SHORT).show();
                                            v.setEnabled(true);
                                            EventBus.getDefault().post(new Home_Class.ClassInfo());
                                        } else {
                                            Toast.makeText(TG_Detail.this, "信息修改失败", Toast.LENGTH_SHORT).show();
                                            v.setEnabled(true);
                                        }
                                        ProgressUtil.dismiss();
                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ProgressUtil.dismiss();
                                        Toast.makeText(TG_Detail.this, "信息修改失败", Toast.LENGTH_SHORT).show();
                                        v.setEnabled(true);
                                    }
                                });
                            }

                        } catch (Exception e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ProgressUtil.dismiss();
                                    Toast.makeText(TG_Detail.this, "信息修改失败", Toast.LENGTH_SHORT).show();
                                    v.setEnabled(true);
                                }
                            });
                            e.printStackTrace();
                        }
                    }
                }).start();

                break;
//            case R.id.tougao_detail_fabu://发布图文
//                checkNetwork();
//                if(title_edt.getText().toString().equals("")||content_edt.getText().toString().equals("")){
//                    Toast.makeText(this,"请将信息填写完整",Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                v.setEnabled(false);
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            String data=OkHttpUtils.post(Constants.Archmage_agree_tougao_IP)
//                                    .params("key",Constants.safeKey).params("id",Id).params("result","2")
//                                    .execute().body().string();
//                            if(!data.equals("")){
//                               final  HashMap<String ,String >m=AnalyticalJSON.getHashMap(data);
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        if(m!=null&&"000".equals(m.get("code"))){
//                                            Intent intent=new Intent(TG_Detail.this,ZiXun_List.class);
//                                            startActivity(intent);
//                                            Toast.makeText(TG_Detail.this, "稿件信息已发布", Toast.LENGTH_SHORT).show();
//
//                                            v.setEnabled(true);
//                                            finish();
//                                        }else{
//                                            Toast.makeText(TG_Detail.this, "稿件信息发布失败，请稍后重试", Toast.LENGTH_SHORT).show();
//                                            v.setEnabled(true);
//                                        }
//                                    }
//                                });
//                            }else{
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Toast.makeText(TG_Detail.this, "稿件信息已发布，请勿重复发布", Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                            }
//                        } catch (IOException e) {
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Toast.makeText(TG_Detail.this, "稿件信息发布失败，请稍后重试", Toast.LENGTH_SHORT).show();
//                                    v.setEnabled(true);
//                                }
//                            });
//                            e.printStackTrace();
//                        }
//                    }
//                }).start();
//                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case 111://从photoPicker返回图片
                ArrayList<String> list1 = data.getStringArrayListExtra("array");
                if (list1 != null) {
                    if (list.size() < 3) {
                        if (((list.size() - 1) + list1.size() < 3)) {
                            if (list.size() > 1) {
                                list.addAll(list.size() - 1, list1);
                            } else {
                                list.addAll(0, list1);
                            }
                        } else {
                            list.remove(list.size() - 1);
                            list.addAll(list1);
                        }
                        adpter.notifyDataSetChanged();
                    } else {
                        list.remove(list.size() - 1);
                        list.addAll(list1);
                        adpter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(TG_Detail.this, "系统错误，获取数据失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case ChooseVideoAndAudio:
                Log.w(TAG, "onActivityResult: 视频返回信息：" + data);
                Log.w(TAG, "onActivityResult: 视频返回信息1：" + data.getData());
                if (data.getData() != null) {
                    String path = data.getData().getPath();
                    Log.w(TAG, "onActivityResult: 路径---》" + path);
                    showInfoThenUploadFile(path, "");


                } else {
                    //未获取到文件信息
                }
                break;
            case RES_CODE:
                String path = data.getStringExtra("path");
                Log.w(TAG, "RES_CODE: " + path + "     file_____>" + videoFile + "    nuM______" + layout_file.getChildCount());
                if (path != null) {
                    if (videoFile == null && layout_file.getChildCount() != 3) {
                        showInfoThenUploadFile(path, "");
                    }
                }
                break;
        }
    }


    @Override
    public void onCancle(int positon) {

        if (!list.get(list.size() - 1).equals("add")) {
            Log.w(TAG, "onCancle: mimages" + list.toString());
            Log.w(TAG, "onCancle: fsdfsd");
            list.add("add");
            adpter.setmImgs(list);
        }
        list.remove(positon);
        Log.w(TAG, "onCancle: 数组size" + list.size());
        adpter.notifyDataSetChanged();
    }
}
