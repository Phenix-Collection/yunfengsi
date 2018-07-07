package com.yunfengsi.Models.Model_activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.ruffian.library.RTextView;
import com.yunfengsi.R;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.FileUtils;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.PhotoUtilNormal;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.ProgressUtil;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.ToastUtil;
import com.yunfengsi.Utils.mApplication;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileWithBitmapCallback;

import org.json.JSONException;
import org.json.JSONObject;

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


        findViewById(R.id.title_back).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.title_title)).setText("资料完善");
        findViewById(R.id.title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        img_card = findViewById(R.id.addCard);
        img_photo = findViewById(R.id.photo);
        trueName = findViewById(R.id.trueName);
        edtCard=findViewById(R.id.IDCard);
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
                cardFile = null;
                PhotoUtilNormal.choosePic(this, IDCARD);
                break;
            case R.id.photo:
                PhotoUtilNormal.choosePic(this, PERSONAL);
                break;
            case R.id.commit:
                postCommit();
                break;
        }
    }

    private void postCommit() {
        if (!prapareCommit()) return;
        JSONObject js = new JSONObject();
        try {
            js.put("m_id", Constants.M_id);
            js.put("user_id", PreferenceUtil.getUserId(this));
            js.put("name",trueName.getText().toString().trim());
            js.put("sex",edtSex.getText().toString().trim());
            js.put("cid",edtCard.getText().toString().trim());
            js.put("volk",edtNation.getText().toString().trim());
            js.put("farmington",edtReligiousName.getText().toString().trim());
            js.put("tel",edtPhone.getText().toString().trim());
            js.put("contact",edtMorePhone.getText().toString().trim());
            js.put("address",edtAddress.getText().toString().trim());
            js.put("workunit",edtCompany.getText().toString().trim());
            js.put("practice",edtPracticeExperience.getText().toString().trim());
            js.put("work",edtProfession.getText().toString().trim());
            js.put("plate",edtPlateNumber.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m=ApisSeUtil.i(js);
        LogUtil.e("完善资料：：："+js);
        OkGo.post(Constants.UserForm)
                .params("cidimage",cardFile)
                .params("userimage",personFile)
                .params("key",m.K())
                .params("msg",m.M())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                           HashMap<String,String> map=AnalyticalJSON.getHashMap(s);
                           if(map!=null){
                               if("000".equals(map.get("code"))){
                                   ToastUtil.showToastShort(mApplication.ST("完善资料成功，可到我的>设置>个人信息中查看资料"));
                                   PreferenceUtil.getUserIncetance(getApplicationContext()).edit().putString("perfect", "2").commit();
                                   Intent intent = new Intent();
                                   intent.putExtra("baoming", true);
                                   setResult(66, intent);
                                   finish();
                                   ToastUtil.showToastShort("资料上传成功");
                               }

                           }
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        ProgressUtil.show(IdCardCheck.this,"","正在提交...");
                        ProgressUtil.canCancelAble(false);
                    }

                    @Override
                    public void onAfter(String s, Exception e) {
                        super.onAfter(s, e);
                        ProgressUtil.dismiss();
                    }
                });
    }

    private boolean prapareCommit() {
        if (cardFile == null) {
            ToastUtil.showToastShort("请上传身份证正面照");
            return false;
        }
        if (personFile == null) {
            ToastUtil.showToastShort("请上传个人正面照");
            return false;
        }
        if (trueName.getText().toString().equals("")) {
            ToastUtil.showToastShort("请填写真实姓名");
            return false;
        }
        if (edtSex.getText().toString().equals("")) {
            ToastUtil.showToastShort("请填写性别");
            return false;
        }
        if (edtNation.getText().toString().equals("")) {
            ToastUtil.showToastShort("请填写民族");
            return false;
        }
        if (edtCard.getText().toString().equals("")) {
            ToastUtil.showToastShort("请填写身份证号码");
            return false;
        }
        if (edtPhone.getText().toString().equals("")) {
            ToastUtil.showToastShort("请填写手机号码");
            return false;
        }
        if (edtReligiousName.getText().toString().equals("")) {
            ToastUtil.showToastShort("请填写法名");
            return false;
        }
        if (edtCompany.getText().toString().equals("")) {
            ToastUtil.showToastShort("请填写工作单位");
            return false;
        }
        if (edtPracticeExperience.getText().toString().equals("")) {
            ToastUtil.showToastShort("请填写修行经历");
            return false;
        }
        if (edtProfession.getText().toString().equals("")) {
            ToastUtil.showToastShort("请填写职业");
            return false;
        }
        if (edtMorePhone.getText().toString().equals("")) {
            ToastUtil.showToastShort("请填写真实姓名");
            return false;
        }
        if (edtPlateNumber.getText().toString().equals("")) {
            ToastUtil.showToastShort("请填写车牌登记");
            return false;
        }

        return true;
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
                    ProgressUtil.show(this, "", "图片处理中");
                    ProgressUtil.canCancelAble(false);
                    if (Build.VERSION.SDK_INT >= 24) {
                        Glide.with(this).load(uri)
                                .asBitmap().override(DimenUtils.dip2px(this, 240), com.bumptech.glide.request.target.Target.SIZE_ORIGINAL)
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(final Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                        img_card.setImageBitmap(resource);
                                        new Thread() {
                                            @Override
                                            public void run() {
                                                File file = new File(getExternalFilesDir("pic"), System.currentTimeMillis() + ".jpg");
                                                FileUtils.saveBitmap(resource, file, 200);
                                                cardFile = file;
                                                LogUtil.e("本地压缩后file::::" + file.length() + "   图片大小：：" + resource.getWidth() + "    " + resource.getHeight());
                                                img_card.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        hideProgress();
                                                        getToken();
                                                    }
                                                });

                                            }
                                        }.start();


                                    }
                                });
                    } else {
                        Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();
                        options.config = Bitmap.Config.RGB_565;
                        options.size = 200;
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
                    if (Build.VERSION.SDK_INT >= 24) {
                        Glide.with(this).load(uri)
                                .asBitmap().override(DimenUtils.dip2px(this, 240), com.bumptech.glide.request.target.Target.SIZE_ORIGINAL)
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(final Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                        img_photo.setImageBitmap(resource);
                                        new Thread() {
                                            @Override
                                            public void run() {
                                                File file = new File(getExternalFilesDir("pic"), System.currentTimeMillis() + ".jpg");
                                                FileUtils.saveBitmap(resource, file, 200);
                                                personFile = file;
                                                LogUtil.e("本地压缩后file::::" + file.length() + "   图片大小：：" + resource.getWidth() + "    " + resource.getHeight());
                                            }
                                        }.start();


                                    }
                                });
                    } else {
                        Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();
                        options.config = Bitmap.Config.RGB_565;
                        options.size = 200;
                        Tiny.getInstance().source(uri)
                                .asFile().withOptions(options).compress(new FileWithBitmapCallback() {
                            @Override
                            public void callback(boolean isSuccess, Bitmap bitmap, String outfile, Throwable t) {
                                if (isSuccess) {
                                    img_photo.setImageBitmap(bitmap);
                                    personFile = new File(outfile);
                                    LogUtil.e("压缩后的文件大小：：" + cardFile.length());
                                    hideProgress();
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
                            ToastUtil.showToastShort("图片识别失败，请上传正确的身份证照后重试");
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
                                HashMap<String,String > map=AnalyticalJSON.getHashMap(s);
                                if(map!=null){
                                    if(map.get("image_status")!=null&&map.get("words_result")!=null){
                                        HashMap<String,String > map1=AnalyticalJSON.getHashMap(map.get("words_result"));
                                        if(!TextUtils.isEmpty(AnalyticalJSON.getHashMap(map1.get("公民身份号码")).get("words"))){
                                            ToastUtil.showToastShort("身份证识别成功，请继续填写相关资料");
                                            trueName.setText(AnalyticalJSON.getHashMap(map1.get("姓名")).get("words"));
                                            edtCard.setText(AnalyticalJSON.getHashMap(map1.get("公民身份号码")).get("words"));
                                            edtSex.setText(AnalyticalJSON.getHashMap(map1.get("性别")).get("words"));
//                                        edtAddress.setText(AnalyticalJSON.getHashMap(map1.get("住址")).get("words"));
                                            edtNation.setText(AnalyticalJSON.getHashMap(map1.get("民族")).get("words"));
                                        }else{
                                            ToastUtil.showToastShort("图片识别失败，请上传正确的身份证照后重试");
                                        }

                                    }else{
                                        ToastUtil.showToastShort("图片识别失败，请上传正确的身份证照后重试");
                                    }
                                }
                            }

                            @Override
                            public void onError(Call call, Response response, Exception e) {
                                super.onError(call, response, e);
                                ToastUtil.showToastShort("图片识别超时，请检查网络后重新重试");
                            }

                            @Override
                            public void onAfter(String s, Exception e) {
                                super.onAfter(s, e);
                                ProgressUtil.dismiss();
                            }

                            @Override
                            public void onBefore(BaseRequest request) {
                                super.onBefore(request);
                                ProgressUtil.show(IdCardCheck.this,"","正在识别...");
                                ProgressUtil.canCancelAble(false);
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
