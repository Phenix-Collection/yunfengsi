package com.qianfujiaoyu.Activitys;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.qianfujiaoyu.R;
import com.qianfujiaoyu.Utils.AnalyticalJSON;
import com.qianfujiaoyu.Utils.ApisSeUtil;
import com.qianfujiaoyu.Utils.Constants;
import com.qianfujiaoyu.Utils.DimenUtils;
import com.qianfujiaoyu.Utils.ImageUtil;
import com.qianfujiaoyu.Utils.LogUtil;
import com.qianfujiaoyu.Utils.ProgressUtil;
import com.qianfujiaoyu.Utils.StatusBarCompat;
import com.qianfujiaoyu.Utils.ToastUtil;
import com.qianfujiaoyu.Utils.mApplication;
import com.qianfujiaoyu.Utils.photoUtil;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileWithBitmapCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 作者：因陀罗网 on 2017/8/11 18:28
 * 公司：成都因陀罗网络科技有限公司
 */

public class UpdateClassInfo extends AppCompatActivity implements View.OnClickListener {
    private ImageView addImage;
    private File tmpFile;
    private static final String TAG = "UpdateClassInfo";
    private EditText title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, ContextCompat.getColor(this, R.color.main_color));
        setContentView(R.layout.update_class_info);
        findViewById(R.id.back).setVisibility(View.VISIBLE);
        ((ImageView) findViewById(R.id.back)).setImageBitmap(ImageUtil.readBitMap(this, R.drawable.back));
        ((TextView) findViewById(R.id.title)).setText("修改班级信息");

        addImage = (ImageView) findViewById(R.id.add_image);
        addImage.setOnClickListener(this);
        findViewById(R.id.add_commit).setOnClickListener(this);
        title = (EditText) findViewById(R.id.add_name);
        title.setText(getIntent().getStringExtra("title"));
        if(!getIntent().getStringExtra("url").equals("")){
            Glide.with(this).load(getIntent().getStringExtra("url")).asBitmap().override(DimenUtils.dip2px(this,240),DimenUtils.dip2px(this,240))
                    .fitCenter().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    Tiny.getInstance().init(mApplication.getInstance());
                    Tiny.getInstance().source(resource)
                            .asFile().compress(new FileWithBitmapCallback() {
                        @Override
                        public void callback(boolean isSuccess, Bitmap bitmap, String outfile) {
                            if(isSuccess){
                                tmpFile=new File(outfile);
                                LogUtil.e("当前封面：：临时文件"+tmpFile.length());
                                addImage.setImageBitmap(bitmap);
                            }
                        }
                    });
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.add_image:
                photoUtil.choosePic(this, 111);
                break;
            case R.id.add_commit:
                if (title.getText().toString().trim().equals("")) {
                    ToastUtil.showToastShort("请输入班级名称");
                    return;
                }
                if (tmpFile == null) {
                    ToastUtil.showToastShort("请上传班级群封面");
                    return;
                }
                ProgressUtil.show(UpdateClassInfo.this, "", "正在提交");
                JSONObject js = new JSONObject();
                try {
                    js.put("id", getIntent().getStringExtra("id"));
                    js.put("title", title.getText().toString());
                    js.put("m_id", Constants.M_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ApisSeUtil.M m = ApisSeUtil.i(js);
                OkGo.post(Constants.ChangeClassInfo).params("key", Constants.safeKey)
                        .params("key", m.K())
                        .params("msg", m.M())
                        .params("image", tmpFile)
                        .execute(new AbsCallback<Object>() {
                            @Override
                            public void onSuccess(Object o, Call call, Response response) {
                                try {
                                    HashMap<String, String> map = AnalyticalJSON.getHashMap(response.body().string());
                                    if (map != null) {
                                        if ("000".equals(map.get("code"))) {
                                            ToastUtil.showToastShort("班级信息修改成功");
                                            Intent intent=new Intent();
                                            intent.putExtra("url",map.get("image"));
                                            intent.putExtra("title",title.getText().toString().trim());
                                            setResult(666,intent);
                                            finish();
                                        }
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public Object convertSuccess(Response response) throws Exception {
                                return null;
                            }

                            @Override
                            public void onAfter(Object o, Exception e) {
                                super.onAfter(o, e);
                                ProgressUtil.dismiss();
                            }
                        });

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri uri = null;
            if (data == null) {
                uri = photoUtil.uri;
            } else {
                uri = data.getData();
            }
            final Uri finalUri = uri;
            Tiny.getInstance().init(mApplication.getInstance());
            Tiny.getInstance().source(ImageUtil.getImageAbsolutePath(UpdateClassInfo.this,finalUri)).asFile().compress(new FileWithBitmapCallback() {
                @Override
                public void callback(boolean isSuccess, Bitmap bitmap, String outfile) {
                    if(isSuccess){
                        tmpFile=new File(outfile);
                        LogUtil.e("onActivityResult: temfile++++" + tmpFile);
                        addImage.setImageBitmap(bitmap);
                    }else{
                        ToastUtil.showToastShort("图片处理错误，请重新选择图片");
                    }
                }
            });
//            Bitmap bm = ImageUtil.getImageThumbnail(ImageUtil.getImageAbsolutePath(UpdateClassInfo.this, finalUri), ImageUtil.mWidth, ImageUtil.mHeight);
//            try {
//                tmpFile = new File(getExternalCacheDir(), System.currentTimeMillis() + ".jpg");
//                bm.compress(Bitmap.CompressFormat.JPEG, 60, new FileOutputStream(tmpFile));
//                Log.w(TAG, "onActivityResult: 文件大小：" + tmpFile.length());
//                bm.recycle();
//                bm = null;
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }




        }
    }
}
