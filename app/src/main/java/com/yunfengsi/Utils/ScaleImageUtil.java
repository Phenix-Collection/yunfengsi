package com.yunfengsi.Utils;

import android.app.Activity;
import android.content.Intent;

import com.yunfengsi.View.Photo.PhotoActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/5/3.
 */

public class ScaleImageUtil {

    public static void openBigIagmeMode(Activity activity,ArrayList<String > arrayList, int positon,boolean downloadAble){
        WeakReference<Activity> w=new WeakReference<Activity>(activity);
        Activity context=w.get();
        Intent intent = new Intent();
        intent.putExtra("array", arrayList);
        intent.putExtra("position", positon);
        intent.putExtra("download",downloadAble);
//        intent.setClass(context, ViewPagerActivity.class);
        intent.setClass(context, PhotoActivity.class);
        context.startActivity(intent);
    }
    public static void openBigIagmeMode(Activity activity,String imgurl,boolean downloadAble){
        WeakReference<Activity> w=new WeakReference<Activity>(activity);
        Activity context=w.get();
        Intent intent = new Intent();
        ArrayList<String > arrayList=new ArrayList<>();
        arrayList.add(imgurl);
        intent.putExtra("array", arrayList);
        intent.putExtra("position", 0);
        intent.putExtra("download",downloadAble);
//        intent.setClass(context, ViewPagerActivity.class);
        intent.setClass(context, PhotoActivity.class);
        context.startActivity(intent);

    }
}
