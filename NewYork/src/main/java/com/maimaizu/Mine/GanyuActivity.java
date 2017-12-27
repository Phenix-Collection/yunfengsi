package com.maimaizu.Mine;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.maimaizu.R;
import com.maimaizu.Utils.AnalyticalJSON;
import com.maimaizu.Utils.Constants;
import com.maimaizu.Utils.DimenUtils;
import com.maimaizu.Utils.ProgressUtil;
import com.maimaizu.Utils.StatusBarCompat;
import com.maimaizu.Utils.mApplication;

import java.util.HashMap;


public class GanyuActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "GanyuActivity";
    ProgressDialog progressDialog;
     private TextView wangzhan,qq,email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ganyu);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        wangzhan= (TextView) findViewById(R.id.guanfangwangzhan);
        qq= (TextView) findViewById(R.id.QQqun);
        email= (TextView) findViewById(R.id.user_email);
        ((TextView) findViewById(R.id.title)).setText(mApplication.ST("关于"));
        ((TextView) findViewById(R.id.name)).setText(mApplication.ST(getResources().getString(R.string.app_name)));
        ((TextView) findViewById(R.id.guanwang)).setText(mApplication.ST("官方网站"));
        ((TextView) findViewById(R.id.dianhua)).setText(mApplication.ST("热线电话"));
        ((TextView) findViewById(R.id.email)).setText(mApplication.ST("客服邮箱"));
        ((TextView) findViewById(R.id.banquan)).setText(mApplication.ST("版权归因陀罗网络科技有限公司所有\n违者必究\n    蜀ICP备16010257号-3　"));
        Glide.with(this).load(R.drawable.indra).override(DimenUtils.dip2px(this,100),DimenUtils.dip2px(this,100)).into(((ImageView) findViewById(R.id.logo)));
        getData();
    }

    /**
     * 获取数据
     */
    private void getData() {
        ProgressUtil.show(this,"","请稍等");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String data= OkGo.post(Constants.AboutUs_Ip).tag(TAG).params("key", Constants.safeKey).params("m_id",Constants.M_id)
                            .execute().body().string();
                    if(!data.equals("")&&!data.equals("null")){
                        final HashMap<String ,String >map= AnalyticalJSON.getHashMap(data);
                        Log.w(TAG, "run: "+data );
                        if(map!=null){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    wangzhan.setText(map.get("url").equals("")? mApplication.ST("即将开放"):map.get("url"));
                                    qq.setText(map.get("tel").equals("")?mApplication.ST("即将开放"):map.get("tel"));
                                    email.setText(map.get("email").equals("")?mApplication.ST("即将开放"):map.get("email"));
                                   ProgressUtil.dismiss();
                                }
                            });
                        }

                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GanyuActivity.this, mApplication.ST("加载失败"), Toast.LENGTH_SHORT).show();
                                ProgressUtil.dismiss();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(GanyuActivity.this, mApplication.ST("加载失败"), Toast.LENGTH_SHORT).show();
                            ProgressUtil.dismiss();
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
        switch (id){
            case R.id.guanyu_back:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        OkGo.getInstance().cancelTag(TAG);
        super.onDestroy();
    }
}
