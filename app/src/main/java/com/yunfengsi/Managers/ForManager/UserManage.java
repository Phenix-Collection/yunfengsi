package com.yunfengsi.Managers.ForManager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.yunfengsi.R;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.Network;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.ProgressUtil;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.ToastUtil;
import com.yunfengsi.Utils.mApplication;
import com.yunfengsi.View.mItemDecoration;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 作者：luZheng on 2018/07/04 17:25
 */
public class UserManage extends AppCompatActivity implements View.OnClickListener {

    private EditText input;
    private TextView phoneType;
    private TextView petNameType;
    private TextView trueNameType;
    private RecyclerView recyclerView;


    private int type = 1;//1   手机  2  昵称  3  真实姓名
    private UserAdapter adapter;

    private int pageSize = 10;
    private int page     = 1;
    private int endPage  = -1;

    private boolean isNetting = false;//正在进行网络请求

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.user_manage);


        input = findViewById(R.id.input);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView sousuo = findViewById(R.id.sousuo);
        sousuo.setOnClickListener(this);

        phoneType = findViewById(R.id.phone);
        petNameType = findViewById(R.id.petName);
        trueNameType = findViewById(R.id.trueName);

        phoneType.setOnClickListener(this);
        petNameType.setOnClickListener(this);
        trueNameType.setOnClickListener(this);


        phoneType.performClick();


        recyclerView = findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new mItemDecoration(this));


        adapter = new UserAdapter(new ArrayList<HashMap<String, String>>());
        adapter.setEmptyView(mApplication.getEmptyView(this, 150, "请选择搜索类型搜索"));
        adapter.openLoadAnimation();
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (endPage != page) {
                    page++;
                    getData(false);
                }
            }
        }, recyclerView);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if(isNetting){
                    ToastUtil.showToastShort("正在进行网络请求，请稍等");
                    return;
                }
                Intent intent =new Intent();
                intent.setClass(UserManage.this, UserInfoForManagerChecking.class);
                intent.putExtra("user_id", ((HashMap<String, String>) adapter.getData().get(position)).get("id"));
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);

    }

    private class UserAdapter extends BaseQuickAdapter<HashMap<String, String>, BaseViewHolder> {
        public UserAdapter(@Nullable List<HashMap<String, String>> data) {
            super(R.layout.item_user_manage, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, HashMap<String, String> item) {
            Glide.with(UserManage.this).load(item.get("user_image"))
                    .override(DimenUtils.dip2px(UserManage.this,50),DimenUtils.dip2px(UserManage.this,50))
                    .centerCrop()
                    .into((ImageView) helper.getView(R.id.userImage));

            helper.setText(R.id.petName,item.get("pet_name"))
                    .setText(R.id.trueName,item.get("name"))
                    .setText(R.id.phone,item.get("phone"));

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.sousuo:
                getData(true);
                break;
            case R.id.phone:
                resetStatus();
                phoneType.setSelected(true);
                type = 1;
                if (!"".equals(input.getText().toString())) {
                    getData(true);
                }
                break;
            case R.id.petName:
                resetStatus();
                petNameType.setSelected(true);
                type = 2;
                if (!"".equals(input.getText().toString())) {
                    getData(true);
                }
                break;
            case R.id.trueName:
                resetStatus();
                trueNameType.setSelected(true);
                type = 3;
                if (!"".equals(input.getText().toString())) {
                    getData(true);
                }
                break;
        }
    }


    private void getData(final boolean isRefresh) {
        if (isRefresh) {
            page = 1;
            endPage = -1;
            adapter.setEnableLoadMore(true);
        }
        if (!Network.HttpTest(this)) {
            return;
        }
        if (TextUtils.isEmpty(input.getText())) {
            ToastUtil.showToastShort("请输入关键字搜索");
            return;
        }
        JSONObject js = new JSONObject();
        try {
            js.put("page", page);
            js.put("m_id", Constants.M_id);
            js.put("type", type);
            js.put("contents", input.getText());
            js.put("user_id", PreferenceUtil.getUserId(this));
            js.put("admin_id", PreferenceUtil.getUserId(this));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m = ApisSeUtil.i(js);
        LogUtil.e("管理员用户搜索：：" + js + "   类型：：；" + type);
        OkGo.post(Constants.UserlistQuery)
                .params("key", m.K())
                .params("msg", m.M())
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        HashMap<String, String> map = AnalyticalJSON.getHashMap(s);
                        if (map != null) {
                            ArrayList<HashMap<String, String>> list = AnalyticalJSON.getList_zj(map.get("msg"));
                            if (list != null) {
                                if (isRefresh) {
                                    adapter.setNewData(list);
                                } else {
                                    if (list.size() < pageSize) {
                                        endPage = page;
                                        adapter.addData(list);
                                        adapter.loadMoreEnd(false);
                                    } else {
                                        adapter.addData(list);
                                        adapter.loadMoreComplete();
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        isNetting = true;
                        ProgressUtil.show(UserManage.this, "", "正在搜索");
                    }

                    @Override
                    public void onAfter(String s, Exception e) {
                        super.onAfter(s, e);
                        isNetting = false;
                        ProgressUtil.dismiss();
                    }
                });


    }

    @Override
    protected void onPause() {
        super.onPause();
        OkGo.getInstance().cancelTag(this);
    }

    /**
     * 重置类别
     */
    private void resetStatus() {
        phoneType.setTextColor(Color.BLACK);
        petNameType.setTextColor(Color.BLACK);
        trueNameType.setTextColor(Color.BLACK);


        phoneType.setSelected(false);
        petNameType.setSelected(false);
        trueNameType.setSelected(false);
    }
}
