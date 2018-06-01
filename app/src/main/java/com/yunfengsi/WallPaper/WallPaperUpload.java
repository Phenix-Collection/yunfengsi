package com.yunfengsi.WallPaper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.yunfengsi.R;
import com.yunfengsi.TouGao.TouGaoGridAdapter;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.FileUtils;
import com.yunfengsi.Utils.ImageUtil;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.ProgressUtil;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/5/29.
 */

public class WallPaperUpload extends AppCompatActivity implements View.OnClickListener, TouGaoGridAdapter.oncCancleListener {
    //选择的图片集合
    private ArrayList<String> mImages = new ArrayList<>();
    private TouGaoGridAdapter adpter;
    private GridView grid;
    private EditText userName;

    private int allowChooseNum=9;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.wall_paper_upload);


        grid = (GridView) findViewById(R.id.tougao_grid);
        mImages.add("add");
        adpter = new TouGaoGridAdapter(this, mImages, true,allowChooseNum);
        adpter.setOncCancleListener(this);
        grid.setAdapter(adpter);

        userName=findViewById(R.id.edt_user);
        userName.setText(PreferenceUtil.getUserIncetance(this).getString("pet_name",""));
    }


    @Override
    protected void onStart() {
        super.onStart();
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.upload).setOnClickListener(this);
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;

            case R.id.upload:
                // TODO: 2018/5/29 上传
                view.setEnabled(false);
                JSONObject js=new JSONObject();
                HttpParams httpParams=new HttpParams();
                try {
                    js.put("m_id", Constants.M_id);
                    js.put("user_id",PreferenceUtil.getUserId(this));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                LogUtil.e("上传壁纸：："+js+"   图片：：；"+mImages);
                ApisSeUtil.M m=ApisSeUtil.i(js);
                httpParams.put("key",m.K());
                httpParams.put("msg",m.M());
                ProgressUtil.show(WallPaperUpload.this,"","正在上传...");
                ProgressUtil.canCancelAble(false);
                for (int i = 0; i < mImages.size(); i++) {
                    if (mImages.get(i).equals("add")) {
                        continue;
                    }

                    Bitmap bm = ImageUtil.getImageThumbnail(mImages.get(i), ImageUtil.mWidth, ImageUtil.mHeight);
                    String t = System.currentTimeMillis() + ".jpg";
                    FileUtils.saveBitmap(bm, t);
                    httpParams.put(("image" + (i + 1)), new File(FileUtils.TEMPPAH, t));
                    bm.recycle();
                    bm = null;
                }
                OkGo.post(Constants.WallPaperUpLoad)
                        .params(httpParams)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(String s, Call call, Response response) {
                                HashMap<String ,String > map= AnalyticalJSON.getHashMap(s);
                                if(map!=null){
                                    if("000".equals(map.get("code"))){
                                        ToastUtil.showToastShort("壁纸上传成功，请等待审核");
                                        setResult(999);
                                        finish();

                                    }
                                }
                            }


                            @Override
                            public void onAfter(String s, Exception e) {
                                super.onAfter(s, e);
                                view.setEnabled(true);
                                ProgressUtil.dismiss();
                                ProgressUtil.canCancelAble(true);
                            }

                        });
                break;
        }
    }

    @Override
    public void onCancle(int positon) {
        if (mImages.size() == allowChooseNum && !mImages.get(mImages.size() - 1).equals("add")) {
            mImages.add("add");
            adpter.setmImgs(mImages);
        }
        mImages.remove(positon);
        adpter.notifyDataSetChanged();
    }


    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       if (resultCode == 111) {
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
                Toast.makeText(this, "系统错误，获取数据失败", Toast.LENGTH_SHORT).show();
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
}
