package com.yunfengsi.Models.YunDou;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.yunfengsi.R;

import java.util.ArrayList;

public class tabPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> list;
    private ArrayList<String> titles;
    private LayoutInflater inflater;
    public tabPagerAdapter(Context context,FragmentManager fm, ArrayList<Fragment> list) {
        super(fm);
        this.list = list;
        Context context1 = context;
        inflater=LayoutInflater.from(context);
    }

    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    public void setTitles(ArrayList<String> titles) {
        this.titles = titles;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

    public View getCustomView(int pos){
        View view= inflater.inflate(R.layout.quan_tab,null);
        ((TextView) view).setText(titles.get(pos));
        return view;
    }

}