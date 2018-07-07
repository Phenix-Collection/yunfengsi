package com.yunfengsi.Models.WallPaper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.yunfengsi.Models.TouGao.TouGaoGridAdapter;
import com.yunfengsi.R;
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

import org.json.JSONArray;
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

    private int allowChooseNum = 9;
    private Spinner             classfy;
    private ArrayList<String>   typeNames;
    private SparseArray<String> classification;
    private              int    id          = -1;
    private static final String DEFAULTTYPE = "请选择分类";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.wall_paper_upload);


        GridView grid = findViewById(R.id.tougao_grid);
        classfy = findViewById(R.id.spinner_classfy);
        mImages.add("add");
        adpter = new TouGaoGridAdapter(this, mImages, true, allowChooseNum);
        adpter.setOncCancleListener(this);
        grid.setAdapter(adpter);

        EditText userName = findViewById(R.id.edt_user);
        userName.setText(PreferenceUtil.getUserIncetance(this).getString("pet_name", ""));

        typeNames = new ArrayList<>();

        getClassification();
    }




    /**
     * 获取当前分类列表
     */
    private void getClassification() {
        final JSONObject js = new JSONObject();
        try {
            js.put("m_id", Constants.M_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m = ApisSeUtil.i(js);
        OkGo.post(Constants.WallPaperTypeList)
                .params("key", m.K())
                .params("msg", m.M())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        HashMap<String, String> map = AnalyticalJSON.getHashMap(s);
                        try {
                            JSONArray jsonArray = new JSONArray(map.get("msg"));
                            int       len       = jsonArray.length();
                            classification = new SparseArray<>();
                            for (int i = 0; i < len; i++) {
                                //保存类型列表数据
                                JSONObject js = jsonArray.getJSONObject(i);
                                typeNames.add(js.getString("name"));
                                classification.put(js.getInt("id"), js.getString("name"));
                            }

                            typeNames.add(0, DEFAULTTYPE);
                            classification.put(-1, DEFAULTTYPE);
                            LogUtil.e(classification + "");
                            classfy.setAdapter(new ArrayAdapter<String>(WallPaperUpload.this, R.layout.spinner_text_main, typeNames));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (NullPointerException e) {
                            classification=null;

                        }

                    }

                    @Override
                    public void onAfter(String s, Exception e) {
                        super.onAfter(s, e);
                        if(classification==null){
                            ToastUtil.showToastShort("分类数据加载失败，请稍后重试");
                            finish();
                        }
                    }
                });

    }


    @Override
    protected void onStart() {
        super.onStart();
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.upload).setOnClickListener(this);
        classfy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long i) {

                id = classification.keyAt(classification.indexOfValue(((String) parent.getSelectedItem())));
                LogUtil.e("选中id：：" + id + "  typeName：：：" + parent.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                LogUtil.e("onNothingSelected：：" + id);
                id = -1;
            }
        });
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;

            case R.id.upload:
                // TODO: 2018/5/29 上传
                if (mImages.size() == 1) {
                    //==1只有提示图片
                    ToastUtil.showToastShort("请选择壁纸");
                    return;
                }
                if (id == -1) {
                    ToastUtil.showToastShort("请选择分类");
                    return;
                }
                view.setEnabled(false);
                JSONObject js = new JSONObject();
                HttpParams httpParams = new HttpParams();


                try {
                    js.put("m_id", Constants.M_id);
                    js.put("user_id", PreferenceUtil.getUserId(this));
                    js.put("type_id", id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                LogUtil.e("上传壁纸：：" + js + "   图片：：；" + mImages + "  分类id::" + id);
                ApisSeUtil.M m = ApisSeUtil.i(js);
                httpParams.put("key", m.K());
                httpParams.put("msg", m.M());
                ProgressUtil.show(WallPaperUpload.this, "", "正在上传...");
                ProgressUtil.canCancelAble(false);
                for (int i = 0; i < mImages.size(); i++) {
                    if (mImages.get(i).equals("add")) {
                        continue;
                    }

                    Bitmap bm = ImageUtil.getImageThumbnail(mImages.get(i), ImageUtil.mWidth, ImageUtil.mHeight);
                    String t  = System.currentTimeMillis() + ".jpg";
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
                                HashMap<String, String> map = AnalyticalJSON.getHashMap(s);
                                if (map != null) {
                                    if ("000".equals(map.get("code"))) {
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
