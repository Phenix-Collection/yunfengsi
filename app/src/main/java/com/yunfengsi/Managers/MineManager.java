package com.yunfengsi.Managers;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemDragListener;
import com.yunfengsi.Adapter.Mine_GridAdapter;
import com.yunfengsi.R;
import com.yunfengsi.Utils.FileUtils;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.Verification;
import com.yunfengsi.Utils.mApplication;
import com.yunfengsi.View.mItemDeraction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/5/10.
 */

public class MineManager {
    public static final String img          = "mine_image";
    public static final String text         = "mine_text";
    public static final String CACHE_NAME   = "mine_cache";
    public static final String CACHE_VERSON = "cache_verson";
    private View header;

    //    private ArrayList<Integer> imageList;
//    private ArrayList<String> titles;
    private ArrayList<HashMap<String, Object>> maps;

    private Mine_GridAdapter adapter;
    private RecyclerView     recyclerView;
    private Context          context;

//    public ArrayList<Integer> getImageList() {
//        return imageList;
//    }
//
//    public ArrayList<String> getTitles() {
//        return titles;
//    }

    public List<HashMap<String, Object>> getMaps() {
        return maps;
    }

    public void notifyDataChanged() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public MineManager(Context context, RecyclerView recyclerView) {
        super();
        this.recyclerView = recyclerView;
        this.context = context;
        initMine();

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
                            R.drawable.auction,
                            R.raw.icon_wallpager,
                            R.raw.yundou,
                            R.raw.fuli,

                            R.raw.qiyuan,
                            R.raw.jinshu,
                            R.raw.qian_icon,
                            R.raw.meditation,
                            R.raw.huiyuan,
                            R.raw.ganxiexin,
                            R.raw.shoucang_justforleft,
                            R.raw.zhifu_justforleft,
                            R.raw.gongxiu,
                            R.raw.gongke,
                            R.raw.mine_activity,
                            R.raw.tongzhi_normal,
                            R.raw.tougao_mine,
                            R.raw.setting,
                            R.raw.qiehuan
                    };


            for (int i = 0; i < img.length; i++) {
                LogUtil.e("img::::" + img[i] + "  text::::" + text[i]);
                HashMap<String, Object> map = new HashMap<>();
                map.put(MineManager.img, img[i]);
                map.put(MineManager.text, text[i]);
                maps.add(map);
            }
        }
        if(PreferenceUtil.getUserIncetance(mApplication.getInstance()).getString("role","").equals("3")){
            if(!maps.get(0).get(MineManager.text).equals("管理员")){
                HashMap<String, Object> map = new HashMap<>();
                map.put(MineManager.img, R.drawable.admin_mine);
                map.put(MineManager.text, "管理员");
                maps.add(0,map);
            }
        }else{
            if(maps.get(0).get(MineManager.text).equals("管理员")){
                maps.remove(0);
            }
        }
        LogUtil.e(maps + "  @#@#@#@#@~!!@!~~是否是管理员：：："+PreferenceUtil.getUserIncetance(context).getString("role","").equals("3"));
        adapter = new Mine_GridAdapter(maps);

//        header = LayoutInflater.from(context).inflate(R.layout.fragment_mine_header,null);
//        header.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (new LoginUtil().checkLogin(context)) {
//                    context.startActivity(new Intent(context, MemberCenter.class));
//                }
//            }
//        });
//        adapter.addHeaderView(header);


        recyclerView.setLayoutManager(new GridLayoutManager(context, 4));
        mItemDeraction mItemDeraction = new mItemDeraction(1, Color.parseColor("#f2f2f2"));
        recyclerView.addItemDecoration(mItemDeraction);
        ItemDragAndSwipeCallback swipeCallback   = new ItemDragAndSwipeCallback(adapter);
        ItemTouchHelper          itemTouchHelper = new ItemTouchHelper(swipeCallback);
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

    public void setOnitemClickListener(BaseQuickAdapter.OnItemClickListener listener) {
        adapter.setOnItemClickListener(listener);
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

    public View getHeader() {
        return header;
    }
}
