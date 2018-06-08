package com.yunfengsi.Managers;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yunfengsi.R;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.FileUtils;
import com.yunfengsi.Utils.ImageUtil;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.ToastUtil;
import com.yunfengsi.Utils.Verification;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 作者：因陀罗网 on 2018/3/9 18:38
 * 公司：成都因陀罗网络科技有限公司
 */

public class ItemManager extends AppCompatActivity {
    private ImageView back;
    private TextView title;
    private RecyclerView myItems;
    private RecyclerView otherItems;
    private MyItemAdapter myItemAdapter, otherAdapter;
    private ArrayList<HashMap<String, Object>> myList;
    private ArrayList<HashMap<String, Object>> otherList;
    private static final String TEXT_KEY = "text";
    private static final String IMAGE_KEY = "image";
    private static final String[] DEFAULT_NAME = {"共修", "收藏", "活动", "佛经"};
    private static final int[] DEFAULT_IMAGE = {R.raw.gongxiu,R.raw.shoucang_justforleft, R.raw.mine_activity, R.raw.jinshu};

    private static final String[] ALL_NAME = {"义卖","壁纸","我的云豆","我的福利","卜事", "坐禅", "会员中心", "功德", "通知", "功课", "投稿", "祈愿树"};
    private static final int[] ALL_IMAGE = {R.drawable.auction,R.raw.icon_wallpager,R.raw.yundou,R.raw.fuli,R.raw.qian_icon, R.raw.meditation, R.raw.huiyuan, R.raw.zhifu_justforleft, R.raw.tongzhi_normal,
            R.raw.gongke, R.raw.tougao_mine, R.raw.qiyuan};


    private static final String MINE = "1";//已有应用
    private static final String OTHER = "2";//其他应用
    public static final String CaCheName = "cache_HomePage";
    public static final String CaCheVersion = "cache_HomePage_Version";
    private int mFlags;
    private static int FLAG_FORCE_DEAFAULT =0x00000001;//强行使用默认配置
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.manager_item);

        ((ImageView) findViewById(R.id.title_back)).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.title_title)).setText("编辑应用");
        ((ImageView) findViewById(R.id.title_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.handle_right).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.handle_right)).setText("保存");
        findViewById(R.id.handle_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileUtils.saveStorage2SDCard(ItemManager.this, myList, CaCheName + PreferenceUtil.getUserId(ItemManager.this));
                EventBus.getDefault().post(myList);
                ToastUtil.showToastShort("应用设置成功");
                finish();
            }
        });

        myItems = (RecyclerView) findViewById(R.id.myItem);
        otherItems = (RecyclerView) findViewById(R.id.otherItem);

        myItems.setLayoutManager(new GridLayoutManager(this, 4));

        otherItems.setLayoutManager(new GridLayoutManager(this, 4));

        LogUtil.e("获取设置缓存：：；" + FileUtils.getStorageMapEntities(this, CaCheName + PreferenceUtil.getUserId(this)));
        String version= Verification.getAppVersionName(this);
        if(PreferenceUtil.getSettingIncetance(this).getString(ItemManager.CaCheVersion,"").equals(version)){
            //同一版本才允许缓存，不同版本清空缓存

        }else{
            mFlags |= FLAG_FORCE_DEAFAULT;//添加强行默认配置标志
        }
        //没有缓存
        if (FileUtils.getStorageMapEntities(this, CaCheName + PreferenceUtil.getUserId(this)) == null
                ||((mFlags & FLAG_FORCE_DEAFAULT)==FLAG_FORCE_DEAFAULT)) {
            myList = new ArrayList<>();
            otherList = new ArrayList<>();
            boolean isContinue = false;
            for (int i = 0; i < ALL_NAME.length; i++) {
                for (int j = 0; j < DEFAULT_NAME.length; j++) {
                    if (ALL_NAME[i].equals(DEFAULT_NAME[j])) {
                        isContinue = true;
                        break;
                    }
                }
                if (isContinue) {
                    isContinue = false;
                    continue;
                }
                HashMap<String, Object> map = new HashMap<>();
                map.put(TEXT_KEY, ALL_NAME[i]);
                map.put(IMAGE_KEY, ALL_IMAGE[i]);
                otherList.add(map);
            }
            for (int i = 0; i < DEFAULT_NAME.length; i++) {
                if (DEFAULT_NAME[i].equals("更多")) {
                    continue;
                }
                HashMap<String, Object> map = new HashMap<>();
                map.put(TEXT_KEY, DEFAULT_NAME[i]);
                map.put(IMAGE_KEY, DEFAULT_IMAGE[i]);
                myList.add(map);
            }

        } else {
            myList = FileUtils.getStorageMapEntities(this, CaCheName + PreferenceUtil.getUserId(this));
            ArrayList<String> names = new ArrayList<>();
            otherList = new ArrayList<>();
            boolean isContinue = false;
            for (HashMap<String, Object> s : myList) {
                if (s.get(TEXT_KEY).toString().equals("更多")) {
                    myList.remove(s);
                }
                names.add(s.get(TEXT_KEY).toString());

            }
            for (int i = 0; i < ALL_NAME.length; i++) {
                for (int j = 0; j < names.size(); j++) {
                    if (ALL_NAME[i].equals(names.get(j))) {
                        isContinue = true;
                        break;
                    }
                }
                if (isContinue) {
                    isContinue = false;
                    continue;
                }
                HashMap<String, Object> map = new HashMap<>();
                map.put(TEXT_KEY, ALL_NAME[i]);
                map.put(IMAGE_KEY, ALL_IMAGE[i]);
                otherList.add(map);
            }

        }
        myItemAdapter = new MyItemAdapter(myList, MINE);
        myItems.setAdapter(myItemAdapter);
        otherAdapter = new MyItemAdapter(otherList, OTHER);
        otherItems.setAdapter(otherAdapter);
    }

    private class MyItemAdapter extends BaseQuickAdapter<HashMap<String, Object>, BaseViewHolder> {
        private String type;

        public MyItemAdapter(List<HashMap<String, Object>> data, String type) {
            super(R.layout.item_mine_manager_mine, data);
            this.type = type;
        }

        @Override
        protected void convert(final BaseViewHolder holder, final HashMap<String, Object> map) {
            if (type.equals(MINE)) {
                if (map.get(TEXT_KEY).equals("共修") || map.get(TEXT_KEY).equals("收藏") || map.get(TEXT_KEY).equals("活动") || map.get(TEXT_KEY).equals("佛经")) {
                    holder.setVisible(R.id.handle, false);
                } else {
                    holder.setVisible(R.id.handle, true);
                    ((ImageView) holder.getView(R.id.handle)).setImageBitmap(ImageUtil.readBitMap(ItemManager.this, R.drawable.delete_gray));
                }
            } else if (type.equals(OTHER)) {
                holder.setVisible(R.id.handle, true);
                ((ImageView) holder.getView(R.id.handle)).setImageBitmap(ImageUtil.readBitMap(ItemManager.this, R.drawable.add_icon));
            }
            holder.setText(R.id.text, map.get(TEXT_KEY).toString());
            Glide.with(ItemManager.this).load(map.get(IMAGE_KEY)).override(DimenUtils.dip2px(ItemManager.this, 35)
                    , DimenUtils.dip2px(ItemManager.this, 35)).into((ImageView) holder.getView(R.id.image));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (type.equals(MINE)) {//
                        if (map.get(TEXT_KEY).equals("共修") || map.get(TEXT_KEY).equals("收藏") || map.get(TEXT_KEY).equals("活动") || map.get(TEXT_KEY).equals("佛经")) {
                            return;
                        }
                        try {
                            getData().remove(holder.getAdapterPosition());
                            notifyItemRemoved(holder.getAdapterPosition());

                            otherList.add(map);
                            otherAdapter.notifyItemInserted(otherList.size() - 1);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            LogUtil.e("动画期间连续点击 ，异常捕捉");
                        }
                    } else if (type.equals(OTHER)) {
//                        if (myList.size() == 7) {
//                            ToastUtil.showToastShort("最多选择7个应用显示到首页");
//                            return;
//                        }
                        try {
                            getData().remove(holder.getAdapterPosition());
                            notifyItemRemoved(holder.getAdapterPosition());

                            myList.add(map);
                            myItemAdapter.notifyItemInserted(myList.size() - 1);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            LogUtil.e("动画期间连续点击 ，异常捕捉");
                        }

                    }

                    LogUtil.e("当前选中：：" + myList + "\n\n未选中：：" + otherList);
                }

            });
        }
    }
}
