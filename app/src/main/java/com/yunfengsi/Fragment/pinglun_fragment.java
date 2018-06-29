package com.yunfengsi.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.yunfengsi.Adapter.PL_List_Adapter;
import com.yunfengsi.R;
import com.yunfengsi.Setting.Mine_gerenziliao;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.LoginUtil;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.ProgressUtil;
import com.yunfengsi.Utils.ToastUtil;
import com.yunfengsi.Utils.mApplication;
import com.yunfengsi.View.LoadMoreListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/4/17.
 */

public class pinglun_fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, LoadMoreListView.OnLoadMore,
        PL_List_Adapter.onHuifuListener, View.OnClickListener {
    private View view;
    private SwipeRefreshLayout swip;
    private LoadMoreListView listView;
    private String page = "1";
    private String endPage = "";
    private boolean isRefresh=true;
    private boolean isFirstIn = true;
    private PL_List_Adapter adapter;
    //无评论时的header
    private TextView tv;
    private LinearLayout pinglun;
    private FrameLayout overlay;
    private EditText PLText;
    private InputMethodManager imm;
    private LinearLayout currentLayout;
    private int currentPosition;
    private String currentId;
    private TextView fasong;
    private boolean isLoad=false;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pinglun, container, false);
        initView(view);

        return view;

    }

    private void initView(View view) {
        swip = (SwipeRefreshLayout) view.findViewById(R.id.swip);
        swip.setOnRefreshListener(this);
        swip.setColorSchemeResources(R.color.main_color);

        listView = (LoadMoreListView) view.findViewById(R.id.listview);
        listView.setLoadMoreListen(this);
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        adapter = new PL_List_Adapter(getActivity());
        adapter.setOnHuifuListener(this);
        adapter.setToDetail(true);
        listView.setAdapter(adapter);
        PLText = (EditText) view.findViewById(R.id.zixun_detail_apply_edt);
        fasong = (TextView) view.findViewById(R.id.zixun_detail_fasong);
        fasong.setText(mApplication.ST("发送"));
        fasong.setOnClickListener(this);
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        overlay = (FrameLayout) view.findViewById(R.id.frame);
        overlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PLText.setHint(mApplication.ST("写入您的回复（300字以内）"));
                imm.hideSoftInputFromWindow(PLText.getWindowToken(), 0);
                v.setVisibility(View.GONE);
            }
        });


    }

    private void getData() {
        JSONObject js=new JSONObject();
        try {
            js.put("page",page);
            js.put("user_id", PreferenceUtil.getUserIncetance(getActivity())
                    .getString("user_id", ""));
            js.put("m_id", Constants.M_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkGo.post(Constants.getPinglunList).tag(this)
                .params("key", ApisSeUtil.getKey())
                .params("msg", ApisSeUtil.getMsg(js))
                .execute(new AbsCallback<ArrayList<HashMap<String,String>>>() {
                    @Override
                    public void onSuccess(ArrayList<HashMap<String, String>> list, Call call, Response response) {
                        if ((list != null)) {
                            if (isRefresh) {
                                adapter.mlist.clear();
                                adapter.mlist.addAll(list);
                                for (int i = 0; i < list.size(); i++) {
                                    adapter.flagList.add(false);
                                }
                                adapter.notifyDataSetChanged();
                                isRefresh=false;
                            } else {
                                adapter.mlist.addAll(list);
                                boolean flag = false;
                                for (int i = 0; i < list.size(); i++) {
                                    adapter.flagList.add(flag);
                                }
                                adapter.notifyDataSetChanged();
                            }
                            if (list.size() < 10) {
                                endPage = page;
                            }
                        } else {
                            listView.footer.setEnabled(false);
                            ToastUtil.showToastShort(mApplication.ST( "暂无更多数据"), Gravity.CENTER);
                        }
                    }

                    @Override
                    public ArrayList<HashMap<String, String>> convertSuccess(Response response) throws Exception {
                        return AnalyticalJSON.getList(response.body().string(), "comment");
                    }

                    @Override
                    public void onAfter(ArrayList<HashMap<String, String>> hashMaps, Exception e) {
                        super.onAfter(hashMaps, e);
                        swip.setRefreshing(false);
                        listView.onLoadComplete();
                    }
                } );



    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(view!=null&&isVisibleToUser&&!isLoad){
            swip.post(new Runnable() {
                @Override
                public void run() {
                    swip.setRefreshing(true);
                    getData();
                    isLoad=true;
                }
            });
        }
    }

    @Override
    public void onRefresh() {
        isRefresh = true;
        page = "1";
        endPage = "";

        final ProgressBar p = (ProgressBar) (listView.footer.findViewById(R.id.load_more_bar));
        final TextView t = (TextView) (listView.footer.findViewById(R.id.load_more_text));
        p.setVisibility(View.VISIBLE);
        t.setText(mApplication.ST("正在加载"));
        getData();

    }


    @Override
    public void loadMore() {
        isRefresh=false;
        if (!endPage.equals(page)) {
            page = String.valueOf(Integer.parseInt(page) + 1);
        } else {
            final ProgressBar p = (ProgressBar) (listView.footer.findViewById(R.id.load_more_bar));
            final TextView t = (TextView) (listView.footer.findViewById(R.id.load_more_text));
            p.setVisibility(View.GONE);
            t.setText(mApplication.ST("没有更多数据了"));
            return;
        }
        getData();
    }


    @Override
    public void onHuifuClicked(String id, int p, View v, String name) {
        // TODO: 2016/12/27 评论回复接口
        overlay.setVisibility(View.VISIBLE);
        currentLayout = (LinearLayout) v;
        currentPosition = p;
        currentId = id;
        String ss = mApplication.ST("回复 " + " :");
        PLText.setHint(ss);
        PLText.requestFocus();


        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

//
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.zixun_detail_fasong:
                if (!new LoginUtil().checkLogin(getActivity())) {
                    return;
                }
                if (PLText.getText().toString().trim().equals("")) {
                    Toast.makeText(getActivity(), mApplication.ST("请输入评论"), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (PreferenceUtil.getUserIncetance(getActivity()).getString("pet_name", "").trim().equals("")) {
                    Toast.makeText(getActivity(), mApplication.ST("请完善信息") , Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), Mine_gerenziliao.class);
                    startActivity(intent);
                    return;
                }
                v.setEnabled(false);
                ProgressUtil.show(getActivity(), "", mApplication.ST( "正在提交"));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final String content = PLText.getText().toString();
                            JSONObject js=new JSONObject();
                            try {
                                js.put("user_id", PreferenceUtil.getUserIncetance(getActivity()).getString("user_id", ""));
                                js.put("ct_contents", content);
                                js.put("ct_id", currentId);
                                js.put("m_id", Constants.M_id);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            final String data = OkGo.post(Constants.little_zixun_pl_add_IP)
                                    .params("key", ApisSeUtil.getKey())
                                    .params("msg", ApisSeUtil.getMsg(js)).execute().body().string();
                            if (!data.equals("")) {
                                final HashMap<String, String> hashMap = AnalyticalJSON.getHashMap(data);
                                if (hashMap != null && "000".equals(hashMap.get("code"))) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (currentLayout.getVisibility() == View.GONE) {
                                                currentLayout.setVisibility(View.VISIBLE);
                                            }
                                            TextView textView = new TextView(getActivity());
                                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                            layoutParams.setMargins(0, DimenUtils.dip2px(getActivity(), 5), 0, DimenUtils.dip2px(getActivity(), 5));
                                            textView.setLayoutParams(layoutParams);
                                            String pet_name = PreferenceUtil.getUserIncetance(getActivity()).getString("pet_name", "");
                                            SpannableStringBuilder ssb = new SpannableStringBuilder(pet_name + ":" + content);
                                            ssb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getActivity(), R.color.main_color)), 0, pet_name.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                                            textView.setText(ssb);
                                            currentLayout.addView(textView);
                                            PLText.setText("");
                                            PLText.setHint(mApplication.ST("写入您的评论（300字以内）"));
                                            overlay.setVisibility(View.GONE);
                                            imm.hideSoftInputFromWindow(PLText.getWindowToken(), 0);
                                            listView.setSelection(currentPosition);
                                            fasong.setEnabled(true);

                                            try {
                                                JSONArray jsonArray = new JSONArray(adapter.mlist.get(currentPosition).get("reply"));
                                                JSONObject jsonObject = new JSONObject();
                                                jsonObject.put("id", hashMap.get("id"));
                                                jsonObject.put("pet_name", pet_name);
                                                jsonObject.put("ct_contents", content);
                                                if(PreferenceUtil.getUserIncetance(getActivity()).getString("role","").equals("3")){
                                                    jsonObject.put("role", "3");
                                                }else{
                                                    jsonObject.put("role", "0");
                                                }
                                                jsonObject.put("user_id", PreferenceUtil.getUserIncetance(getActivity()).getString("user_id", ""));
                                                jsonArray.put(jsonObject);
                                                adapter.mlist.get(currentPosition).put("reply", jsonArray.toString());
                                                adapter.notifyDataSetChanged();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            ProgressUtil.dismiss();
                                        }
                                    });
                                }
                            } else {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        v.setEnabled(true);
                                        Toast.makeText(getActivity(), mApplication.ST("回复提交失败，请重新尝试"), Toast.LENGTH_SHORT).show();
                                        ProgressUtil.dismiss();
                                    }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
        }
    }
}
