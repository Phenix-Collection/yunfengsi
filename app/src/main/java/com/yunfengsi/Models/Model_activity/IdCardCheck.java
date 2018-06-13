package com.yunfengsi.Models.Model_activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.ruffian.library.RTextView;
import com.yunfengsi.R;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.FileUtils;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.PhotoUtilNormal;
import com.yunfengsi.Utils.ProgressUtil;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.ToastUtil;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileWithBitmapCallback;

import java.io.File;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 作者：luZheng on 2018/06/13 09:49
 */
public class IdCardCheck extends AppCompatActivity implements View.OnClickListener {
    private static final int IDCARD   = 1;//添加身份证正面照
    private static final int PERSONAL = 2;//添加个人正面照
    private Uri pictureUri;
    public static final String accessTokenUrl = "https://aip.baidubce.com/oauth/2.0/token";
    public static final String RecognitionURL = "https://aip.baidubce.com/rest/2.0/ocr/v1/idcard";

    private ImageView img_card, img_photo;
    private EditText trueName, edtSex, edtNation, edtCard, edtAddress, edtPhone, edtReligiousName, edtCompany,
            edtPracticeExperience, edtProfession, edtMorePhone, edtPlateNumber;
    private RTextView commit;

    private int currentType = 1;//默认为添加身份证照片

    private File cardFile, personFile;

    private String currentToken = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.idcard_check);


        ((ImageView) findViewById(R.id.title_back)).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.title_title)).setText("资料完善");
        ((ImageView) findViewById(R.id.title_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        img_card = findViewById(R.id.addCard);
        img_photo = findViewById(R.id.photo);
        trueName = findViewById(R.id.trueName);
        edtSex = findViewById(R.id.sex);
        edtNation = findViewById(R.id.nation);
        edtAddress = findViewById(R.id.address);
        edtPhone = findViewById(R.id.phonenumber);
        edtReligiousName = findViewById(R.id.religiousName);
        edtCompany = findViewById(R.id.company);
        edtPracticeExperience = findViewById(R.id.practiceExperience);
        edtProfession = findViewById(R.id.profession);
        edtMorePhone = findViewById(R.id.morePhone);
        edtPlateNumber = findViewById(R.id.plateNumber);
        commit = findViewById(R.id.commit);
    }

    @Override
    protected void onStart() {
        super.onStart();
        img_card.setOnClickListener(this);
        img_photo.setOnClickListener(this);
        commit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addCard:
                PhotoUtilNormal.choosePic(this, IDCARD);
                break;
            case R.id.photo:
                PhotoUtilNormal.choosePic(this, PERSONAL);
                break;
            case R.id.commit:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri uri = null;
            if (data == null) {
                uri = PhotoUtilNormal.uri;
            } else {
                uri = data.getData();
            }
            LogUtil.e("图片uri：" + uri + "    ");
            switch (requestCode) {
                /**
                 *身份证正面照回调
                 */
                case IDCARD:
                    ProgressUtil.show(this, "", "身份证识别中");
                    ProgressUtil.canCancelAble(false);
                    if (Build.VERSION.SDK_INT >= 24) {
                        Glide.with(this).load(uri)
                                .asBitmap().override(DimenUtils.dip2px(this, 240), com.bumptech.glide.request.target.Target.SIZE_ORIGINAL)
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                        img_card.setImageBitmap(resource);

                                        File file = new File(getExternalFilesDir("pic"), System.currentTimeMillis() + ".jpg");
                                        FileUtils.saveBitmap(resource, file, 100);
                                        cardFile = file;
                                        LogUtil.e("本地压缩后file::::" + file.length()+"   图片大小：："+resource.getWidth()+"    "+resource.getHeight());
                                        hideProgress();
                                        getToken();

                                    }
                                });
                    } else {
                        Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();
                        options.config = Bitmap.Config.RGB_565;
                        options.size = 100;
                        Tiny.getInstance().source(uri)
                                .asFile().withOptions(options).compress(new FileWithBitmapCallback() {
                            @Override
                            public void callback(boolean isSuccess, Bitmap bitmap, String outfile, Throwable t) {
                                if (isSuccess) {
                                    img_card.setImageBitmap(bitmap);
                                    cardFile = new File(outfile);
                                    LogUtil.e("压缩后的文件大小：：" + cardFile.length());
                                    hideProgress();
                                    getToken();
                                } else {
                                    LogUtil.e(t.getMessage());
                                    ToastUtil.showToastShort("图片处理失败，请稍后重试");
                                    ProgressUtil.dismiss();
                                    ProgressUtil.canCancelAble(true);
                                }
                            }

                        });
                    }


                    break;
                /**
                 *个人正面照回调
                 */
                case PERSONAL:
                    break;
            }
        }
    }

    private void hideProgress() {
        ProgressUtil.dismiss();
        ProgressUtil.canCancelAble(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideProgress();
    }

    private void getToken() {
        OkGo.post(accessTokenUrl)
                .tag(this)
                .params("grant_type", "client_credentials")
                .params("client_id", "06GeKcYqWXwXvzhaaDNvFION")
                .params("client_secret", "Fmq0Nro8E7OrfYYLMxLXhKTNvahLkxbu")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        LogUtil.e("s::::" + s);
                        HashMap<String, String> map = AnalyticalJSON.getHashMap(s);
                        if (map != null) {
                            currentToken = map.get("access_token");
                            LogUtil.e("当前token：：：" + currentToken);

                            postImage();


                        } else {
                            ToastUtil.showToastShort("图片识别失败，请稍后重试");
                        }
                    }
                });
    }

    private void postImage() {
        if (currentToken != null && !currentToken.equals("")) {
            try {
                final long sTime = System.currentTimeMillis();
                OkGo.post(RecognitionURL + "?access_token=" + currentToken)
                        .headers("Content-Type", "application/x-www-form-urlencoded")
                        .params("id_card_side", "front")
                        .params("image", FileUtils.imageToBase64(cardFile.getAbsolutePath()))
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(String s, Call call, Response response) {
                                LogUtil.e("识别结果：：：" + s + "  耗时：：；" + (System.currentTimeMillis() - sTime) + "  ms");

                            }

                            @Override
                            public void onError(Call call, Response response, Exception e) {
                                super.onError(call, response, e);
                                ToastUtil.showToastShort("图片识别超时，请检查网络后重新重试");
                            }

                            @Override
                            public void onAfter(String s, Exception e) {
                                super.onAfter(s, e);

                            }
                        });

            } catch (Exception e) {
                e.printStackTrace();

            }
        } else {
            ToastUtil.showToastShort("图片识别失败，请稍后重试");
        }

    }


}
