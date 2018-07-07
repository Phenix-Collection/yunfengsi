package com.yunfengsi.Managers.ForManager;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.yunfengsi.Audio_BD.WakeUp.Recognizelmpl.IBDRcognizeImpl;
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
import com.yunfengsi.Utils.TimeUtils;
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
 * 作者：luZheng on 2018/07/05 13:56
 */
public class SuggestManage extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,View.OnClickListener {

    private SwipeRefreshLayout        swip;
    private MessageAdapter adapter;
    private int     pageSize   = 10;
    private int     page       = 1;
    private int     endPage    = -1;
    private boolean isLoadMore = false;
    private boolean isRefresh  = false;



    private FrameLayout        overlay;
    private TextView           audio;
    private IBDRcognizeImpl    ibdRcognize;
    private ImageView          toggle;
    private InputMethodManager imm;
    private TextView fasong;
    private EditText PLText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.suggest_manage);

        findViewById(R.id.title_back).setVisibility(View.VISIBLE);


        ((TextView) findViewById(R.id.title_title)).setText("意见反馈管理");
        findViewById(R.id.title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        swip = findViewById(R.id.swip);
        swip.setOnRefreshListener(this);
        swip.setColorSchemeResources(R.color.main_color);

        RecyclerView        recyclerView        = findViewById(R.id.recycle);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new mItemDecoration(this));

        adapter = new MessageAdapter(this, new ArrayList<HashMap<String, String>>());

        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (endPage != page) {
                    isLoadMore = true;
                    page++;
                    getComments();
                }
            }
        }, recyclerView);
        adapter.disableLoadMoreIfNotFullPage();

        recyclerView.setAdapter(adapter);


        adapter.setEmptyView(mApplication.getEmptyView(this, 150, "暂无最新用户反馈"));

        swip.post(new Runnable() {
            @Override
            public void run() {
                swip.setRefreshing(true);
                onRefresh();
            }
        });



        PLText = findViewById(R.id.apply_edt);
        fasong = findViewById(R.id.fasong);
        fasong.setText(mApplication.ST("发送"));
        fasong.setOnClickListener(this);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        overlay = findViewById(R.id.frame);
        overlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(PLText.getWindowToken(), 0);
                v.setVisibility(View.GONE);
            }
        });
        toggle = findViewById(R.id.toggle_audio_word);
        audio = findViewById(R.id.audio_button);
        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle.setSelected(!view.isSelected());
                if (toggle.isSelected()) {
                    audio.setVisibility(View.VISIBLE);
                    PLText.setVisibility(View.GONE);
                } else {
                    audio.setVisibility(View.GONE);
                    PLText.setVisibility(View.VISIBLE);
                }
            }
        });
        audio.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        if (ibdRcognize == null) {
                            ibdRcognize = new IBDRcognizeImpl(SuggestManage.this);
                            ibdRcognize.attachView(PLText, audio, toggle);
                        }
                        view.setSelected(true);
                        audio.setText("松开完成识别");
                        ibdRcognize.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        view.setSelected(false);
                        audio.setText("按住 说话");
                        ibdRcognize.stop();
                        break;
                }
                return true;
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.fasong:
                if(TextUtils.isEmpty(PLText.getText().toString().trim())){
                    ToastUtil.showToastShort("请输入处理结果");
                    return;
                }
                postAcceptAction((HashMap<String, String>) v.getTag());
                break;

        }
    }

    private void postAcceptAction(HashMap<String,String > suggestMap) {
        if(!Network.HttpTest(this)){
            return;
        }
        JSONObject js = new JSONObject();
        try {
            js.put("page", page);
            js.put("m_id", Constants.M_id);
            js.put("user_id",suggestMap.get("user_id"));
            js.put("treated",suggestMap.get("treated"));
            js.put("contents",PLText.getText().toString().trim());
            js.put("admin_id", PreferenceUtil.getUserId(this));
            js.put("id", suggestMap.get("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m = ApisSeUtil.i(js);
        LogUtil.e("受理反馈建议：：" + js);
        OkGo.post(Constants.SuggestionAccept)
                .params("key", m.K())
                .params("msg", m.M())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        HashMap<String,String > map=AnalyticalJSON.getHashMap(s);
                        if(map!=null){
                            if("000".equals(map.get("code"))){
                                PLText.setText("");
                                overlay.performClick();
                                onRefresh();
                                ToastUtil.showToastShort("处理提交成功");
                            }
                        }
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        ProgressUtil.show(SuggestManage.this,"","正在提交");
                    }

                    @Override
                    public void onAfter(String s, Exception e) {
                        super.onAfter(s, e);
                        ProgressUtil.dismiss();
                    }
                });

    }


    private class MessageAdapter extends BaseQuickAdapter<HashMap<String, String>, BaseViewHolder> {
        int dp30;


        public MessageAdapter(Context context, List<HashMap<String, String>> data) {
            super(R.layout.item_suggest_manange, data);
            dp30 = DimenUtils.dip2px(context, 45);

        }

        @Override
        protected void convert(final BaseViewHolder holder, final HashMap<String, String> map) {
            holder.setText(R.id.petname,"用户:"+map.get("phone"))
                    .setText(R.id.time,TimeUtils.getTrueTimeStr(map.get("time")))
                    .setText(R.id.content,"建议: "+map.get("title")+"\n内容: "+map.get("contents"));
            holder.getView(R.id.accept).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fasong.setTag(map);
                    overlay.setVisibility(View.VISIBLE);
                    PLText.requestFocus();
                    PLText.setHint(mApplication.ST("@用户:"+map.get("phone")));
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }
            });
        }
    }




    private void getComments() {
        if (Network.HttpTest(this)) {
            JSONObject js = new JSONObject();
            try {
                js.put("page", page);
                js.put("m_id", Constants.M_id);
                js.put("admin_id", PreferenceUtil.getUserId(this));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ApisSeUtil.M m = ApisSeUtil.i(js);
            LogUtil.e("获取建议反馈管理列表：：" + js);
            OkGo.post(Constants.SuggestionList)
                    .tag(this)
                    .params("key", m.K())
                    .params("msg", m.M())
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            HashMap<String, String> map = AnalyticalJSON.getHashMap(s);
                            if (map != null) {
                                if ("000".equals(map.get("code"))) {
                                    final ArrayList<HashMap<String, String>> list = AnalyticalJSON.getList_zj(map.get("msg"));
                                    if (list != null) {
                                        if (isRefresh) {
                                            adapter.setNewData(list);
                                            isRefresh = false;
                                            swip.setRefreshing(false);
                                        } else if (isLoadMore) {
                                            isLoadMore = false;
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
                                } else if ("005".equals(map.get("code"))) {
                                    ToastUtil.showToastShort(getString(R.string.haveNoPermission));
                                    finish();
                                }

                            }
                        }

                        @Override
                        public void onAfter(String s, Exception e) {
                            super.onAfter(s, e);
                            swip.setRefreshing(false);
                        }

                    });
        }
    }

    @Override
    public void onRefresh() {
        swip.setRefreshing(true);
        page = 1;
        isRefresh = true;
        adapter.setEnableLoadMore(true);
        getComments();
    }
}
