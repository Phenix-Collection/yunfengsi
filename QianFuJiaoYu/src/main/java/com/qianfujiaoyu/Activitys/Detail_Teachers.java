package com.qianfujiaoyu.Activitys;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.request.BaseRequest;
import com.qianfujiaoyu.Base.BaseActivity;
import com.qianfujiaoyu.R;
import com.qianfujiaoyu.Utils.AnalyticalJSON;
import com.qianfujiaoyu.Utils.ApisSeUtil;
import com.qianfujiaoyu.Utils.Constants;
import com.qianfujiaoyu.Utils.DimenUtils;
import com.qianfujiaoyu.Utils.ImageUtil;
import com.qianfujiaoyu.Utils.ProgressUtil;
import com.qianfujiaoyu.Utils.TimeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 作者：因陀罗网 on 2017/5/18 14:28
 * 公司：成都因陀罗网络科技有限公司
 */

public class Detail_Teachers extends BaseActivity implements View.OnClickListener{
    private AvatarImageView head;
    private TextView name;
    private TextView time,type,abs;
    @Override
    public int getLayoutId() {
        return R.layout.detail_teacher;
    }

    @Override
    public void initView() {
        head= (AvatarImageView) findViewById(R.id.head);
        name= (TextView) findViewById(R.id.name);
        time= (TextView) findViewById(R.id.time);
        type= (TextView) findViewById(R.id.type);
        abs= (TextView) findViewById(R.id.abs);
        findViewById(R.id.back).setVisibility(View.VISIBLE);
        ((ImageView) findViewById(R.id.back)).setImageBitmap(ImageUtil.readBitMap(this,R.drawable.back));
        ((TextView) findViewById(R.id.title)).setText("教师详情");

    }

    private void getData() {
        JSONObject js=new JSONObject();
        try {
            js.put("id",getIntent().getStringExtra("id"));
            js.put("m_id",Constants.M_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m=ApisSeUtil.i(js);
        OkGo.post(Constants.getTeacherDetail)
                .tag(this)
                .params("key",m.K())
                .params("msg",m.M())
                .execute(new AbsCallback<HashMap<String,String>>() {
                    @Override
                    public void onSuccess(HashMap<String, String> map, Call call, Response response) {
                        if(map!=null){
                            Glide.with(Detail_Teachers.this).
                                    load(map.get("user_image")).override(DimenUtils.dip2px(Detail_Teachers.this,80),DimenUtils.dip2px(Detail_Teachers.this,80))
                                    .into(head);
                            name.setText("姓名:  "+map.get("pet_name"));
                            long t = TimeUtils.dataOne(map.get("years"));
                            long now = System.currentTimeMillis();
                            int i = (int) ((now - t) / 1000 / 60 / 60 / 24 / 365);
                            time.setText(i>=1?"工作年限: "+String.valueOf(i) + "年":"工作年限: 不满1年");
                            type.setText("岗位:  "+map.get("vocation"));
                            abs.setText("简介:  "+map.get("abstract"));

                        }
                    }

                    @Override
                    public HashMap<String, String> convertSuccess(Response response) throws Exception {
                        return AnalyticalJSON.getHashMap(response.body().string());
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        ProgressUtil.show(Detail_Teachers.this,"","请稍等");
                    }

                    @Override
                    public void onAfter(HashMap<String, String> map, Exception e) {
                        super.onAfter(map, e);
                        ProgressUtil.dismiss();
                    }
                });
    }

    @Override
    public boolean setEventBus() {
        return false;
    }

    @Override
    public boolean isMainColor() {
        return true;
    }

    @Override
    public void doThings() {
        getData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
        }
    }


}
