package com.yunfengsi.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/4/17.
 */

public class MessagePagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> list;
    private String []type=new String []{"通知","回复"};
    public MessagePagerAdapter(FragmentManager fm,ArrayList<Fragment> list) {
        super(fm);
        this.list=list;
    }

    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list==null?0:list.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {

        return type[position];

    }
    public View getTabView(int position) {
//        View view = LayoutInflater.from(context).inflate(R.layout.home_tab_customview, null);
//        ImageView img = (ImageView) view.findViewById(R.id.home_tab_image);
//        TextView text = (TextView) view.findViewById(R.id.home_tab_text);
//        text.setText(getPageTitle(position));
//        img.setImageResource(images[position]);
        return null;
    }
}
