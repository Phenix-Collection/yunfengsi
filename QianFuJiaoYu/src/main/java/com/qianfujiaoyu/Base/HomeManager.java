package com.qianfujiaoyu.Base;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemDragListener;
import com.qianfujiaoyu.Adapter.Mine_GridAdapter;
import com.qianfujiaoyu.R;
import com.qianfujiaoyu.Utils.FileUtils;
import com.qianfujiaoyu.Utils.LogUtil;
import com.qianfujiaoyu.Utils.Verification;
import com.qianfujiaoyu.Utils.mApplication;
import com.qianfujiaoyu.View.mItemDeraction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/5/10.
 */

public class HomeManager {
    //keys
    public static final String img = "mine_image";
    public static final String text = "mine_text";

    // cacheNa,=me
    public static final String CACHE_NAME = "mine_cache";
    public static final String CACHE_VERSON = "cache_verson";

    private ArrayList<HashMap<String, Object>> maps;

    private Mine_GridAdapter adapter;
    private RecyclerView recyclerView;
    private Context context;


    public List<HashMap<String, Object>> getMaps() {
        return maps;
    }

    public void notifyDataChanged() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public HomeManager(Context context, RecyclerView recyclerView) {
        super();
        this.recyclerView = recyclerView;
        initMine();
        this.context = context;

    }

    public void initMine() {
        maps = FileUtils.getStorageMapEntities(mApplication.getInstance(), CACHE_NAME);

        if (FileUtils.getStorageIntEntities(mApplication.getInstance(), CACHE_VERSON) == null || FileUtils.getStorageIntEntities(mApplication.getInstance(), CACHE_VERSON).get(0) != Verification.getVersionCode(mApplication.getInstance())) {
            maps = null;
        }
        if (maps == null) {
            maps = new ArrayList<>();
            String text[] = mApplication.getInstance().getResources().getStringArray(R.array.mine_text);
            int img[] = new int[]
                    {
                            R.drawable.yuanjieshao_icon,
                            R.drawable.shipu_icon,
                            R.drawable.huodong_icon,
                            R.drawable.shangcheng_icon,
                            R.drawable.wodebanji_icon,
                            R.drawable.sixin_icon,
                            R.drawable.tongzhi_icon,
                            R.drawable.dingdan_icon,
                            R.drawable.shoucang_icon,
                            R.drawable.yaoqing_icon,
                            R.drawable.shezhi_icon,
                            R.drawable.qiehuan_icon,

                    };

            for (int i = 0; i < img.length; i++) {
                LogUtil.e("img::::" + img[i] + "  text::::" + text[i]);
                HashMap<String, Object> map = new HashMap<>();
                map.put(HomeManager.img, img[i]);
                map.put(HomeManager.text, text[i]);
                maps.add(map);
            }
        }
        LogUtil.e(maps + "  @#@#@#@#@~!!@!~~");
        adapter = new Mine_GridAdapter(maps);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 4));
        mItemDeraction mItemDeraction = new mItemDeraction(1, Color.parseColor("#f0f0f0"));
        recyclerView.addItemDecoration(mItemDeraction);
        ItemDragAndSwipeCallback swipeCallback = new ItemDragAndSwipeCallback(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        adapter.enableDragItem(itemTouchHelper);
        adapter.setOnItemDragListener(new OnItemDragListener() {
            @Override
            public void onItemDragStart(RecyclerView.ViewHolder viewHolder, int i) {
                LogUtil.e("起始：：" + i);
                LogUtil.e("     onItemDragStart    " + maps);
            }

            @Override
            public void onItemDragMoving(RecyclerView.ViewHolder viewHolder, int i, RecyclerView.ViewHolder viewHolder1, int i1) {
                LogUtil.e("拖动：：" + i + "     交换:::" + i1);

            }

            @Override
            public void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int i) {
                LogUtil.e("最后位置：：" + i);
                LogUtil.e("onItemDragEnd      " + maps);
            }
        });

        recyclerView.setAdapter(adapter);

    }

    public void setOnitemClickListener(BaseQuickAdapter.OnRecyclerViewItemClickListener listener) {
        adapter.setOnRecyclerViewItemClickListener(listener);
    }

    public void saveMySetting() {
        ArrayList<Integer> arrayList = new ArrayList<>();
        arrayList.add(Verification.getVersionCode(context));
        FileUtils.saveStorage2SDCard(mApplication.getInstance(), arrayList, CACHE_VERSON);
        FileUtils.saveStorage2SDCard(mApplication.getInstance(), maps, CACHE_NAME);
    }

    public void chageRedPoint() {
        for (int i = 0; i < maps.size(); i++) {
            if (maps.get(i).get(text).equals("会员中心")) {
                recyclerView.getChildAt(i).findViewById(R.id.badge).setVisibility(View.VISIBLE);
                break;
            }

        }

    }




}
