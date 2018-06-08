package com.yunfengsi.Models.NianFo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.yunfengsi.R;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.mApplication;

/**
 * Created by Administrator on 2016/8/4.
 */
public class NianFo_Detail extends FragmentActivity implements View.OnClickListener {
    private LinearLayout right_Fragment;
    private TextView tab1, tab2, tab3;
    private FragmentTransaction transaction;
    private android.support.v4.app.Fragment contentFragment;
    private FragmentManager manager;
    private ListView list;
    private String type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.niaofo_detail);
        ((TextView) findViewById(R.id.nianfo_detail_title)).setText(mApplication.ST("共修明细"));
        right_Fragment = (LinearLayout) findViewById(R.id.niaofo_detail_fragment);
        tab1 = (TextView) findViewById(R.id.nianfo_detail_tab1);
        tab1.setText(mApplication.ST("念佛"));
        tab2 = (TextView) findViewById(R.id.nianfo_detail_tab2);
        tab2.setText(mApplication.ST("诵经"));
        tab3 = (TextView) findViewById(R.id.nianfo_detail_tab3);
        tab3.setText(mApplication.ST("持咒"));
        list = (ListView) findViewById(R.id.niaofo_detail_list);
        findViewById(R.id.nianfo_detail_back).setOnClickListener(this);
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        type = getIntent().getStringExtra("type");
        //默认念佛为选中
        if (type.equals("念佛")) {
            contentFragment = new Nianfo_Detail_Fragment();
            tab1.setEnabled(false);
            tab1.setTextColor(getResources().getColor(R.color.main_color));
        }else if(type.equals("诵经")){
            contentFragment = new Songjing_Detail_Fragment();
            tab2.setEnabled(false);
            tab2.setTextColor(getResources().getColor(R.color.main_color));
        }else if(type.equals("持咒")){
            contentFragment = new Chizhou_Detail_Fragment();
            tab3.setEnabled(false);
            tab3.setTextColor(getResources().getColor(R.color.main_color));
        }
        transaction.replace(R.id.niaofo_detail_fragment, contentFragment);
        transaction.commit();

        tab1.setOnClickListener(this);
        tab2.setOnClickListener(this);
        tab3.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        switch (v.getId()) {
            case R.id.nianfo_detail_tab1:
                reSetTab();
                tab1.setEnabled(false);
                tab1.setTextColor(getResources().getColor(R.color.main_color));
                bundle.putString("click", "1");
                transaction = manager.beginTransaction();
                contentFragment = new Nianfo_Detail_Fragment();
                transaction.replace(R.id.niaofo_detail_fragment, contentFragment);
                contentFragment.setArguments(bundle);
                break;
            case R.id.nianfo_detail_tab2:
                reSetTab();
                tab2.setEnabled(false);
                tab2.setTextColor(getResources().getColor(R.color.main_color));
                bundle.putString("click", "2");
                transaction = manager.beginTransaction();
                contentFragment = new Songjing_Detail_Fragment();
                transaction.replace(R.id.niaofo_detail_fragment, contentFragment);
                contentFragment.setArguments(bundle);
                break;
            case R.id.nianfo_detail_tab3:
                reSetTab();
                tab3.setEnabled(false);
                tab3.setTextColor(getResources().getColor(R.color.main_color));
                bundle.putString("click", "3");
                transaction = manager.beginTransaction();
                contentFragment = new Chizhou_Detail_Fragment();
                transaction.replace(R.id.niaofo_detail_fragment, contentFragment);
                contentFragment.setArguments(bundle);
                break;
            case R.id.nianfo_detail_back:
                finish();
                break;
        }
        if (v.getId() != R.id.nianfo_detail_back)
            transaction.commit();
    }

    public void reSetTab() {
        //重置tab背景色
        tab1.setEnabled(true);
        tab2.setEnabled(true);
        tab3.setEnabled(true);

        tab1.setTextColor(getResources().getColor(R.color.black));
        tab3.setTextColor(getResources().getColor(R.color.black));
        tab2.setTextColor(getResources().getColor(R.color.black));


    }
}
