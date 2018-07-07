package com.yunfengsi.Fragment;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;
import com.yunfengsi.Adapter.PingLunActivity;
import com.yunfengsi.MainActivity;
import com.yunfengsi.Managers.ItemManager;
import com.yunfengsi.Managers.MessageCenter;
import com.yunfengsi.Models.Auction.AuctionList;
import com.yunfengsi.Models.BlessTree.BlessTree;
import com.yunfengsi.Models.E_Book.BookList;
import com.yunfengsi.Models.GongYangDetail;
import com.yunfengsi.Models.Model_activity.ActivityDetail;
import com.yunfengsi.Models.Model_activity.Mine_activity_list;
import com.yunfengsi.Models.Model_zhongchou.FundingDetailActivity;
import com.yunfengsi.Models.More.Fortune;
import com.yunfengsi.Models.More.Meditation;
import com.yunfengsi.Models.NianFo.NianFo;
import com.yunfengsi.Models.TouGao.TouGao;
import com.yunfengsi.Models.WallPaper.WallPapaerHome;
import com.yunfengsi.Models.YunDou.MyQuan;
import com.yunfengsi.Models.YunDou.YunDouHome;
import com.yunfengsi.Models.ZiXun_Detail;
import com.yunfengsi.R;
import com.yunfengsi.Setting.AD;
import com.yunfengsi.Setting.Activity_ShouCang;
import com.yunfengsi.Setting.Mine_HuiYuan;
import com.yunfengsi.Setting.Month_Detail;
import com.yunfengsi.ThirdPart.Push.mReceiver;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.FileUtils;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.LoginUtil;
import com.yunfengsi.Utils.Network;
import com.yunfengsi.Utils.NumUtils;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.ProgressUtil;
import com.yunfengsi.Utils.TimeUtils;
import com.yunfengsi.Utils.ToastUtil;
import com.yunfengsi.Utils.Verification;
import com.yunfengsi.Utils.mApplication;
import com.yunfengsi.View.mAudioManager;
import com.yunfengsi.View.mAudioView;
import com.yunfengsi.View.mItemDeraction;
import com.yunfengsi.WebShare.WebInteraction;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;
import okhttp3.Call;
import okhttp3.Response;

import static com.yunfengsi.Fragment.OrignalEntity.VideoUrl;

/**
 * 作者：因陀罗网 on 2018/3/2 13:24
 * 公司：成都因陀罗网络科技有限公司
 */

public class HomePage extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private static final String TEXT_KEY  = "text";
    private static final String IMAGE_KEY = "image";


    public static final String CACKE_BANNER  = "cache_banner";
    public static final String CACKE_ZIXUN   = "cache_zixun";
    public static final String CACKE_HUODONG = "cache_huodong";


    private View         rootView;
    //    private LoadMoreListView2 listView2;
//    private ziXun_List_Adapter adapter;
    private static final String   CACHE_NAME    = ItemManager.CaCheName;
    private static final String[] DEFAULT_NAME  = {"共修", "收藏", "活动", "佛经"};
    //            , "更多"};
    private static final int[]    DEFAULT_IMAGE = {R.raw.gongxiu, R.raw.shoucang_justforleft, R.raw.mine_activity, R.raw.jinshu};
    //        , R.raw.more};
    private ItemAdapter                        itemAdapter;
    private ArrayList<HashMap<String, Object>> itemList;

    private Banner   banner;
    //    private NewAdapter newAdapter;
    private Drawable ctrDra, like, comment;
    private int screenwidth;
    private ArrayList bannerList = new ArrayList<>();
    private ArrayList Huodonglist;

    private ImageView tip;

    private ValueAnimator      valueAnimator;
    private SwipeRefreshLayout swip;

    private int     pageSize   = 10;
    private int     page       = 1;
    private int     endPage    = -1;
    private boolean isLoadMore = false;
    private boolean isRefresh  = false;
    private HomeAdapter  adapter;
    private LinearLayout head;

    @Subscribe
    public void RefreshItems(ArrayList<HashMap<String, Object>> itemList) {
        LogUtil.e("item刷新  " + itemList);
        this.itemList = itemList;

        itemAdapter.setNewData(this.itemList);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.home_page, container, false);
        initView(inflater);

        return rootView;

    }

    private void initView(LayoutInflater inflater) {
        EventBus.getDefault().register(this);//注册事件管理器
        head = (LinearLayout) inflater.inflate(R.layout.head_homepage, null);
        RecyclerView items = head.findViewById(R.id.items);


        swip = rootView.findViewById(R.id.swip);
        swip.setOnRefreshListener(this);


        screenwidth = getResources().getDisplayMetrics().widthPixels;

        rootView.findViewById(R.id.audio_tip).setOnClickListener(this);

        items.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mItemDeraction mItemDeraction = new mItemDeraction(1, Color.parseColor("#f2f2f2"));
        items.addItemDecoration(mItemDeraction);
        getCacheOrDefaultItems();//获取缓存或默认item
        if (itemList == null) {
            itemList = new ArrayList<>();
        }
        itemAdapter = new ItemAdapter(itemList);
        items.setAdapter(itemAdapter);
        banner = head.findViewById(R.id.banner);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels,
                getResources().getDisplayMetrics().widthPixels * 3 / 8);
        banner.setLayoutParams(layoutParams);
        banner.setImageLoader(new ImageLoader() {
            @Override
            public void displayImage(Context context, Object path, ImageView imageView) {
                LogUtil.e("加载轮播图");
                Glide.with(getActivity()).load(path).override(getResources().getDisplayMetrics().widthPixels,
                        getResources().getDisplayMetrics().widthPixels * 3 / 8)
                        .into(imageView);
            }
        });
        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                String url = ((HashMap) bannerList.get(position)).get("url").toString();
                if (url == null || url.equals("")) {

                } else {

                    if (url.contains("yfs.php") && url.contains("red")) {
                        Intent intent = new Intent();
                        intent.setClass(getActivity(), WebInteraction.class);
                        intent.putExtra("url", url);
                        startActivity(intent);

                        return;
                    }
                    if (url.equals(Constants.Help)) {
                        Intent intent = new Intent();
                        intent.setClass(getActivity(), AD.class);
                        intent.putExtra("bangzhu", true);
                        startActivity(intent);
                        return;
                    }
                    if (!url.equals("") && url.contains("yfs.php")) {
                        if (url.contains("?")) {
                            int    index = url.lastIndexOf("?");
                            String arg   = url.substring(index + 1, url.length());
                            LogUtil.e("截取后的参数字段：" + arg);
                            if (arg.contains("&")) {
                                String[]     args = arg.split("&");
                                final String id   = args[0].substring(args[0].lastIndexOf("=") + 1);
                                final String type = args[1].substring(args[1].lastIndexOf("=") + 1);
                                LogUtil.e("字段信息：  id::" + id + "  type::" + type);
                                if (type == null) {
                                    return;
                                }
                                Intent intent1 = new Intent();
                                switch (type) {
                                    case mReceiver.HUODong:
                                        intent1.setClass(getActivity(), ActivityDetail.class);
                                        intent1.putExtra("id", id);
                                        startActivity(intent1);
                                        break;
                                    case mReceiver.GOngyang:
                                        intent1.setClass(getActivity(), GongYangDetail.class);
                                        intent1.putExtra("id", id);
                                        startActivity(intent1);
                                        break;
                                    case mReceiver.ZHONGCHou:
                                        intent1.setClass(getActivity(), FundingDetailActivity.class);
                                        intent1.putExtra("id", id);
                                        startActivity(intent1);
                                        break;
                                    case mReceiver.ZIXUN:
                                        intent1.setClass(getActivity(), ZiXun_Detail.class);
                                        intent1.putExtra("id", id);
                                        startActivity(intent1);
                                        break;
                                    case mReceiver.BaoMing:
                                        if (new LoginUtil().checkLogin(getActivity())) {
                                            intent1.setClass(getActivity(), Mine_activity_list.class);
                                            startActivity(intent1);
                                        }
                                        break;
                                    case mReceiver.GONGXIU:
                                        intent1.setClass(getActivity(), NianFo.class);
                                        startActivity(intent1);
                                        break;
                                    case mReceiver.TongZhi:
                                        if (new LoginUtil().checkLogin(getActivity())) {
                                            intent1.setClass(getActivity(), MessageCenter.class);
                                            startActivity(intent1);
                                        }
                                        break;
                                    case mReceiver.ZuoChan:
                                        if (new LoginUtil().checkLogin(getActivity())) {
                                            intent1.setClass(getActivity(), Meditation.class);
                                            startActivity(intent1);
                                        }
                                        break;
                                    case mReceiver.Bushi:
                                        if (new LoginUtil().checkLogin(getActivity())) {
                                            intent1.setClass(getActivity(), Fortune.class);
                                            startActivity(intent1);
                                        }
                                        break;
                                    case mReceiver.Fojin:
                                        if (new LoginUtil().checkLogin(getActivity())) {
                                            intent1.setClass(getActivity(), BookList.class);
                                            startActivity(intent1);
                                        }
                                        break;
                                    case mReceiver.QiYuan:
                                        if (new LoginUtil().checkLogin(getActivity())) {
                                            intent1.setClass(getActivity(), BlessTree.class);
                                            startActivity(intent1);
                                        }
                                        break;
                                    case mReceiver.Pinglun:
                                        if (new LoginUtil().checkLogin(getActivity())) {
                                            intent1.putExtra("id", id);
                                            intent1.setClass(getActivity(), PingLunActivity.class);
                                        }
                                    case mReceiver.WallPaper:

                                        intent1.putExtra("id", id);
                                        intent1.setClass(getActivity(), WallPapaerHome.class);
                                        startActivity(intent1);

                                        break;
                                    case mReceiver.AD:
                                        intent1.setClass(getActivity(), AD.class);
                                        intent1.putExtra("url", url);
                                        startActivity(intent1);
                                        break;

                                }


                            }
                        }


                    }
                }
            }
        });

        itemAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent   intent   = new Intent();
                TextView textView = view.findViewById(R.id.text);
                LogUtil.e("首页item点击：：：" + textView.getTag());
                switch (textView.getTag().toString()) {
                    case "义卖":
                        startActivity(new Intent(getActivity(), AuctionList.class));
                        break;
                    case "壁纸":
                        startActivity(new Intent(getActivity(), WallPapaerHome.class));
                        break;
                    case "我的云豆":
                        if (new LoginUtil().checkLogin(getActivity())) {
                            startActivity(new Intent(getActivity(), YunDouHome.class));
                        }
                        break;
                    case "我的福利":
                        if (new LoginUtil().checkLogin(getActivity())) {
                            startActivity(new Intent(getActivity(), MyQuan.class));
                        }
                        break;
                    case "功课":
                        ((Mine) ((MainActivity) getActivity()).list.get(4)).openGongke();
                        break;
                    case "祈愿树":
                        if (new LoginUtil().checkLogin(getActivity())) {
                            startActivity(new Intent(getActivity(), BlessTree.class));
                        }
                        break;
                    case "佛经":
                        if (new LoginUtil().checkLogin(getActivity())) {
                            startActivity(new Intent(getActivity(), BookList.class));
                        }
                        break;
                    case "更多":
                        if (new LoginUtil().checkLogin(getActivity())) {
                            startActivity(new Intent(getActivity(), ItemManager.class));
                        }
                        break;
                    case "卜事":
                        if (new LoginUtil().checkLogin(getActivity())) {
                            startActivity(new Intent(getActivity(), Fortune.class));
                        }
                        break;
                    case "坐禅":
                        if (new LoginUtil().checkLogin(getActivity())) {
                            startActivity(new Intent(getActivity(), Meditation.class));
                        }
                        break;
                    case "通知":
                        if (new LoginUtil().checkLogin(getActivity())) {
                            intent.setClass(getActivity(), MessageCenter.class);
                            startActivity(intent);
                        }
                        break;
                    case "投稿":
                        if (new LoginUtil().checkLogin(getActivity())) {
                            intent.setClass(getActivity(), TouGao.class);
                            startActivity(intent);
                        }
                        break;

                    case "感谢信":
                        if (!new LoginUtil().checkLogin(getActivity())) {
                            return;
                        }
                        intent.setClass(getActivity(), Month_Detail.class);
                        startActivity(intent);
                        break;
                    case "功德":
                        if (!new LoginUtil().checkLogin(getActivity())) {
                            return;
                        }
                        intent.setClass(getActivity(), Mine_GYQD.class);
                        startActivity(intent);
                        break;
                    case "活动":
                        if (new LoginUtil().checkLogin(getActivity())) {
                            intent.setClass(getActivity(), Mine_activity_list.class);
                            startActivity(intent);
                        }

                        break;
                    case "共修":
                        intent.setClass(getActivity(), NianFo.class);

                        startActivity(intent);
                        break;
                    case "收藏":
                        if (!new LoginUtil().checkLogin(getActivity())) {
                            return;
                        }
                        intent.setClass(getActivity(), Activity_ShouCang.class);
                        startActivity(intent);
                        break;
                    case "会员中心":
                        if (!new LoginUtil().checkLogin(getActivity())) {
                            return;
                        }
                        if (view.findViewById(R.id.badge).getVisibility() == View.VISIBLE) {
                            PreferenceUtil.getUserIncetance(getActivity()).edit().putLong("time", System.currentTimeMillis()).apply();
                            view.findViewById(R.id.badge).setVisibility(View.GONE);
                            ((MainActivity) getActivity()).tabLayout.getTabAt(4)
                                    .getCustomView().findViewById(R.id.badge)
                                    .setVisibility(View.GONE);
                        }
                        intent.setClass(getActivity(), Mine_HuiYuan.class);
                        startActivity(intent);
                        break;


                }
            }
        });
        itemAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(mApplication.ST("想调整首页应用？去“更多”进行编辑吧"))
                        .setPositiveButton("去编辑", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                if (new LoginUtil().checkLogin(getActivity())) {
                                    startActivity(new Intent(getActivity(), ItemManager.class));
                                }

                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();
                return true;
            }


        });
        LinearLayout more = head.findViewById(R.id.more);
        more.setOnClickListener(this);
        ((TextView) more.findViewById(R.id.text)).setText("更多");
        Glide.with(getActivity()).load(R.raw.more).override(DimenUtils.dip2px(getActivity(), 30), DimenUtils.dip2px(getActivity(), 30))
                .into(((ImageView) more.findViewById(R.id.image)));
        TextView               textView = new TextView(getActivity());
        ViewGroup.LayoutParams vl       = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        textView.setLayoutParams(vl);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(16);
        textView.setTextColor(Color.BLACK);
        textView.setText("暂无最新图文");

        RecyclerView recyclerView = rootView.findViewById(R.id.recycle);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState==RecyclerView.SCROLL_STATE_IDLE){
                    //静止状态  开始加载图片
                    Glide.with(getActivity()).resumeRequests();
                }else{
                    //停止加载
                    Glide.with(getActivity()).pauseRequests();
                }
            }
        });

        ArrayList<OrignalEntity> orignalEntities = new ArrayList<>();
        adapter = new HomeAdapter(orignalEntities);
        adapter.setEmptyView(textView);
        adapter.setHeaderView(head);
        adapter.setHeaderFooterEmpty(true, false);
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (endPage != page) {
                    isLoadMore = true;
                    page++;
                    getZiXun();
                }
            }
        }, recyclerView);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(mApplication.getInstance(), ZiXun_Detail.class);
                intent.putExtra("id", ((OrignalEntity) adapter.getData().get(position)).getId());
                if (null != view.findViewById(R.id.zixun_video_title)) {
                    String videourl = view.findViewById(R.id.zixun_video_title).getTag().toString();
                    String title    = ((TextView) view.findViewById(R.id.zixun_video_title)).getText().toString();
                    String image    = view.findViewById(R.id.zixun_video_Num).getTag().toString();
                    if (!videourl.equals("")) {
                        intent.putExtra("video_url", videourl);
                        intent.putExtra("title", title);
                        intent.putExtra("image", image);
                        LogUtil.e("onItemClick: title-=-=" + title + " url=-=" + videourl + " image-=-=" + image);
                    }
                }
                if (null != view.findViewById(R.id.zixun_video_user)) {
                    String active = view.findViewById(R.id.zixun_video_user).getTag().toString();
                    if (!active.equals("")) {
                        intent.putExtra("active_url", active);
                    }
                }


                startActivity(intent);
            }
        });
        adapter.disableLoadMoreIfNotFullPage();
        adapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        recyclerView.setAdapter(adapter);



        onRefresh();
    }

    @Override
    public void onRefresh() {

        isRefresh = true;
        page = 1;
        endPage = -1;
        getBanner();
        getZiXun();
        getHuodong();
        adapter.setEnableLoadMore(true);
        if (Network.HttpTest(getActivity())) {
            swip.postDelayed(new Runnable() {
                @Override
                public void run() {
                    swip.setRefreshing(false);
                }
            }, 2000);
        } else {
            swip.setRefreshing(false);
        }

    }

    private class HomeAdapter extends BaseMultiItemQuickAdapter<OrignalEntity, BaseViewHolder> {
        private int      screenwidth;
        private Drawable ctr, like, comment;

        /**
         * Same as QuickAdapter#QuickAdapter(Context,int) but with
         * some initialization data.
         *
         * @param data A new list is created out of this one to avoid mutable list
         */
        public HomeAdapter(ArrayList<OrignalEntity> data) {
            super(data);
            addItemType(OrignalEntity.NORMAL, R.layout.hot_two_item);
            addItemType(OrignalEntity.MEDIA, R.layout.zixun_video_item);
            screenwidth = getActivity().getResources().getDisplayMetrics().widthPixels;
            ctr = ContextCompat.getDrawable(getActivity(), R.drawable.ctr_small);
            like = ContextCompat.getDrawable(getActivity(), R.drawable.like_small);
            comment = ContextCompat.getDrawable(getActivity(), R.drawable.comment_small);
            ctr.setBounds(0, 0, DimenUtils.dip2px(getActivity(), 16), DimenUtils.dip2px(getActivity(), 16));
            like.setBounds(0, 0, DimenUtils.dip2px(getActivity(), 15), DimenUtils.dip2px(getActivity(), 15));
            comment.setBounds(0, 0, DimenUtils.dip2px(getActivity(), 14), DimenUtils.dip2px(getActivity(), 14));
        }

        @Override
        protected void convert(final BaseViewHolder holder, final OrignalEntity bean) {
            switch (holder.getItemViewType()) {
                case OrignalEntity.NORMAL:
                    Glide.with(getActivity()).load(bean.getImage())
                            .asBitmap()
//                            .skipMemoryCache(true)
                            .placeholder(R.color.light_huise)
                            .override(DimenUtils.dip2px(getActivity(), 120), DimenUtils.dip2px(getActivity(), 90))
                            .centerCrop()
                            .into(new BitmapImageViewTarget(((ImageView) holder.getView(R.id.hot_two_item_image))) {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    RoundedBitmapDrawable rbd = RoundedBitmapDrawableFactory.create(getActivity().getResources(), resource);
                                    rbd.setCornerRadius(DimenUtils.dip2px(getActivity(), 2));
                                    ((ImageView) holder.getView(R.id.hot_two_item_image)).setImageDrawable(rbd);

                                }
                            });
                    holder.setText(R.id.hot_two_item_title, mApplication.ST(bean.getTitle()));
                    holder.setText(R.id.hot_two_item_time, mApplication.ST(TimeUtils.getTrueTimeStr(bean.getTime())));
                    holder.setText(R.id.hot_two_item_Plnum, mApplication.ST(bean.getIssuer()));
                    holder.setText(R.id.hot_two_item_type, mApplication.ST(bean.getTag()));
                    holder.setText(R.id.hot_two_item_ctr, mApplication.ST(NumUtils.getNumStr(bean.getCtr())));
                    ((TextView) holder.getView(R.id.hot_two_item_ctr)).setCompoundDrawables(ctr, null, null, null);
                    holder.setText(R.id.hot_two_item_likes, NumUtils.getNumStr(bean.getLikes()));
                    ((TextView) holder.getView(R.id.hot_two_item_likes)).setCompoundDrawables(like, null, null, null);
                    holder.setText(R.id.hot_two_item_comments, NumUtils.getNumStr(bean.getNews_comment()));
                    ((TextView) holder.getView(R.id.hot_two_item_comments)).setCompoundDrawables(comment, null, null, null);
                    holder.getView(R.id.hot_two_item_Plnum).setTag(bean.getActive());
                    holder.setText(R.id.hot_two_item_abs, mApplication.ST(bean.getInfo()));
                    break;
                case OrignalEntity.MEDIA:

                    if (!bean.getVideoUrl().endsWith("mp3")) {
                        JCVideoPlayer.releaseAllVideos();
                        if (((FrameLayout) holder.getView(R.id.zixun_video_stub)).getChildAt(0) instanceof JCVideoPlayerStandard) {
                            JCVideoPlayerStandard jc0 = (JCVideoPlayerStandard) ((FrameLayout) holder.getView(R.id.zixun_video_stub)).getChildAt(0);
                            Glide.with(getActivity()).load(bean.getImage()).override(screenwidth - DimenUtils.dip2px(getActivity(), 30), (screenwidth - DimenUtils.dip2px(getActivity(), 30)) / 2)
                                    .centerCrop().into(jc0.thumbImageView);
                            jc0.setUp(bean.getVideoUrl(), bean.getTitle());
                            jc0.titleTextView.setVisibility(View.GONE);
                            jc0.fullscreenButton.setVisibility(View.GONE);
                        } else {
                            ((FrameLayout) holder.getView(R.id.zixun_video_stub)).removeAllViews();
                            JCVideoPlayerStandard jc = new JCVideoPlayerStandard(getActivity());
                            Glide.with(getActivity()).load(bean.getImage()).override(screenwidth - DimenUtils.dip2px(getActivity(), 30), (screenwidth - DimenUtils.dip2px(getActivity(), 30)) / 2)
                                    .centerCrop().into(jc.thumbImageView);
                            jc.setUp(bean.getVideoUrl(), bean.getTitle());
                            jc.titleTextView.setVisibility(View.GONE);
                            jc.fullscreenButton.setVisibility(View.GONE);
                            ((FrameLayout) holder.getView(R.id.zixun_video_stub)).addView(jc);
                        }
                    } else if (bean.getVideoUrl().endsWith("mp3")) {
                        final mAudioView m;
                        mAudioManager.release();
                        if (((FrameLayout) holder.getView(R.id.zixun_video_stub)).getChildAt(0) instanceof mAudioView) {
                            m = (mAudioView) ((FrameLayout) holder.getView(R.id.zixun_video_stub)).getChildAt(0);
                            m.setTime(0);
                        } else {
                            ((FrameLayout) holder.getView(R.id.zixun_video_stub)).removeAllViews();
                            m = new mAudioView(getActivity());
                            ((FrameLayout) holder.getView(R.id.zixun_video_stub)).addView(m);
                        }
                        m.setOnImageClickListener(new mAudioView.onImageClickListener() {
                            @Override
                            public void onImageClick(mAudioView v) {
                                if (!Network.HttpTest(getActivity())) {
                                    return;
                                }
                                if (mAudioManager.getAudioView() != null && mAudioManager.getAudioView().isPlaying()) {
                                    mAudioManager.release();
                                    mAudioManager.getAudioView().setPlaying(false);
                                    mAudioManager.getAudioView().resetAnim();
                                    if (v == mAudioManager.getAudioView()) {
                                        return;
                                    }
                                }
                                if (!m.isPlaying()) {
                                    LogUtil.w("onImageClick: 开始播放");
                                    mAudioManager.playSound(getActivity(), v, bean.getVideoUrl(), new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mp) {
                                            m.resetAnim();
                                        }
                                    }, new MediaPlayer.OnPreparedListener() {
                                        @Override
                                        public void onPrepared(MediaPlayer mp) {
                                            m.setTime(mAudioManager.mMediaplayer.getDuration() / 1000);
                                            ProgressUtil.dismiss();
                                        }
                                    });

                                } else {
                                    LogUtil.w("onImageClick: 停止播放");
                                    mAudioManager.release();
                                }

                            }
                        });

                    }

                    holder.setText(R.id.zixun_video_title, mApplication.ST(bean.getTitle()));
                    holder.getView(R.id.zixun_video_title).setTag(bean.getVideoUrl());
                    holder.getView(R.id.zixun_video_user).setTag(bean.getActive());
                    holder.getView(R.id.zixun_video_Num).setTag(bean.getImage());
                    ((TextView) holder.getView(R.id.zixun_video_tag)).setText(mApplication.ST(bean.getTag()));
                    holder.setText(R.id.zixun_video_user, mApplication.ST(bean.getIssuer()));
                    holder.setText(R.id.zixun_video_Num, mApplication.ST(NumUtils.getNumStr(bean.getCtr())));
                    ((TextView) holder.getView(R.id.zixun_video_Num)).setCompoundDrawables(ctr, null, null, null);
                    holder.setText(R.id.zixun_video_likes, NumUtils.getNumStr(bean.getLikes()));
                    ((TextView) holder.getView(R.id.zixun_video_likes)).setCompoundDrawables(like, null, null, null);
                    holder.setText(R.id.zixun_video_comment, NumUtils.getNumStr(bean.getNews_comment()));
                    ((TextView) holder.getView(R.id.zixun_video_comment)).setCompoundDrawables(comment, null, null, null);
                    holder.setText(R.id.zixun_video_time, mApplication.ST(TimeUtils.getTrueTimeStr(bean.getTime())));
                    break;
            }
        }


    }

    private void getZiXun() {

        JSONObject js = new JSONObject();
        try {
            js.put("m_id", Constants.M_id);
            js.put("page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m = ApisSeUtil.i(js);
        LogUtil.e("资讯：：" + js);
        OkGo.post(Constants.ZiXun_total_Ip).tag(this)
                .params("key", m.K())
                .params("msg", m.M()).execute(new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                if (head.findViewById(R.id.layout1).getVisibility() != View.VISIBLE) {
                    head.findViewById(R.id.layout1).setVisibility(View.VISIBLE);
                }
                try {
                    JSONObject js = new JSONObject(s);
                    if (js != null) {


                        JSONArray jsonArray = js.getJSONArray("news");
                        if (jsonArray != null) {
                            ArrayList<OrignalEntity> list = new ArrayList();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject    j      = jsonArray.getJSONObject(i);
                                OrignalEntity entity = new OrignalEntity();
                                if (j.getString(VideoUrl).equals("")) {
                                    entity.setItemType(OrignalEntity.NORMAL);
                                } else {
                                    entity.setItemType(OrignalEntity.MEDIA);
                                }
                                entity.setActive(j.getString(OrignalEntity.Active));
                                entity.setCtr(j.getString(OrignalEntity.Ctr));
                                entity.setId(j.getString(OrignalEntity.ID));
                                entity.setImage(j.getString(OrignalEntity.Image));
                                entity.setInfo(j.getString(OrignalEntity.Abstract));
                                entity.setIssuer(j.getString(OrignalEntity.Issuer));
                                entity.setLikes(j.getString(OrignalEntity.Likes));
                                entity.setNews_comment(j.getString(OrignalEntity.News_comment));
                                entity.setTag(j.getString(OrignalEntity.Tag));
                                entity.setTime(j.getString(OrignalEntity.Time));
                                entity.setTitle(j.getString(OrignalEntity.Title));
                                entity.setVideoUrl(j.getString(OrignalEntity.VideoUrl));
                                list.add(entity);
                            }
                            if (isRefresh) {
                                isRefresh = false;
                                adapter.setNewData(list);
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
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

//

            }

            @Override
            public void onAfter(String s, Exception e) {
                super.onAfter(s, e);
                if (adapter.getData() == null || adapter.getData().size() == 0) {
                    ArrayList list = FileUtils.getStorageMapEntitiesEasy(getActivity(), CACKE_ZIXUN);
                    LogUtil.e("加载资讯缓存" + list);
                    if (adapter != null && list != null) {
                        if (head.findViewById(R.id.layout1).getVisibility() != View.VISIBLE) {
                            head.findViewById(R.id.layout1).setVisibility(View.VISIBLE);
                        }
                        adapter.setNewData(list);
                    }
                }
            }


        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (this != null && !isVisibleToUser) {
            JCVideoPlayer.releaseAllVideos();
            mAudioManager.release();
        }

    }

    private void getHuodong() {
        JSONObject js = new JSONObject();

        try {
            js.put("m_id", Constants.M_id);
            js.put("page", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m = ApisSeUtil.i(js);
        LogUtil.e("活动：：" + js);
        OkGo.post(Constants.Activity_list_IP).tag(this)
                .params("key", m.K())
                .params("msg", m.M()).execute(new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                ArrayList list = AnalyticalJSON.getList(s, "activity");
                if (list != null) {
                    LinearLayout layout = head.findViewById(R.id.layout2);

                    layout.removeViews(1, layout.getChildCount() - 1);

                    Huodonglist = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        if ("2".equals(((HashMap) list.get(i)).get("status"))) {
                            Huodonglist.add(list.get(i));
                        }
                    }
                    drawHuoDong(layout);
                }
            }

            private void drawHuoDong(LinearLayout layout) {
                View view = null;
                if (Huodonglist == null) {
                    head.findViewById(R.id.layout2).setVisibility(View.GONE);
                    return;
                } else {
                    head.findViewById(R.id.layout2).setVisibility(View.VISIBLE);
                }
                for (final Object map : Huodonglist) {
                    view = LayoutInflater.from(getActivity()).inflate(R.layout.activity_header2, null);
                    TextView      title     = view.findViewById(R.id.activity_item_title);
                    TextView      content   = view.findViewById(R.id.activity_item_content);
                    ImageView     imageView = view.findViewById(R.id.activity_item_img);
                    TextView      peopleNum = view.findViewById(R.id.activity_item_peopleNum);
                    TextView      time      = view.findViewById(R.id.activity_item_time);
                    final HashMap map1      = (HashMap) map;
                    Glide.with(getActivity()).load(map1.get("image1")).asBitmap().centerCrop().override(screenwidth * 9 / 25, screenwidth * 27 / 100).into(new BitmapImageViewTarget(imageView) {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            super.onResourceReady(resource, glideAnimation);
                            RoundedBitmapDrawable rbd = RoundedBitmapDrawableFactory.create(getActivity().getResources(), resource);
                            rbd.setCornerRadius(7);
                            setDrawable(rbd);
                        }
                    });
                    title.setText(mApplication.ST(map1.get("title").toString()));
                    title.setTag(map1.get("id"));
                    content.setText(mApplication.ST(map1.get("abstract").toString()));
                    peopleNum.setText(mApplication.ST("报名人数:" + map1.get("enrollment")));
                    time.setText(mApplication.ST("报名时间:" + map1.get("start_time") + " 至 " + map1.get("end_time")));

                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(mApplication.getInstance(), ActivityDetail.class);
                            String Id     = map1.get("id").toString();
                            if (!TextUtils.isEmpty(Id)) {
                                intent.putExtra("id", Id);
                                startActivity(intent);
                            }
                        }
                    });
                    layout.findViewById(R.id.more2).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ((MainActivity) getActivity()).pager.setCurrentItem(1);
                        }
                    });
                    layout.addView(view);
                    layout.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onAfter(String s, Exception e) {
                super.onAfter(s, e);
                if (Huodonglist == null || Huodonglist.size() == 0) {
                    LogUtil.e("加载活动缓存");
                    Huodonglist = FileUtils.getStorageMapEntities(getActivity(), CACKE_HUODONG);
                    drawHuoDong((LinearLayout) head.findViewById(R.id.layout2));
                }
            }
        });
    }

    private void getCacheOrDefaultItems() {
        String version = Verification.getAppVersionName(getActivity());
        if (PreferenceUtil.getSettingIncetance(getActivity()).getString(ItemManager.CaCheVersion, "").equals(version)) {//同一版本才允许缓存，不同版本清空缓存
            if (FileUtils.getStorageMapEntities(getActivity(), CACHE_NAME + PreferenceUtil.getUserId(getActivity())) == null) {
                LogUtil.e("首页默认设置");
                itemList = new ArrayList<>();
                for (int i = 0; i < DEFAULT_NAME.length; i++) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put(TEXT_KEY, DEFAULT_NAME[i]);
                    map.put(IMAGE_KEY, DEFAULT_IMAGE[i]);
                    itemList.add(map);
                }

            } else {
                LogUtil.e("首页获取缓存设置");
                itemList = FileUtils.getStorageMapEntities(getActivity(), CACHE_NAME + PreferenceUtil.getUserId(getActivity()));
            }
        } else {//不同版本
            LogUtil.e("首页默认设置");
            itemList = new ArrayList<>();
            for (int i = 0; i < DEFAULT_NAME.length; i++) {
                HashMap<String, Object> map = new HashMap<>();
                map.put(TEXT_KEY, DEFAULT_NAME[i]);
                map.put(IMAGE_KEY, DEFAULT_IMAGE[i]);
                itemList.add(map);
            }
        }


    }

    private void initAnimator() {
        if (valueAnimator == null) {
            valueAnimator = ValueAnimator.ofFloat(10);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    rootView.findViewById(R.id.audio_tip).setTranslationX((Float) valueAnimator.getAnimatedValue());
                }
            });
            valueAnimator.setDuration(1000);
            valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
            valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
            valueAnimator.start();
        } else {
            if (!valueAnimator.isStarted()) {
                valueAnimator.start();
            }
        }

    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.audio_tip:
                v.setVisibility(View.GONE);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final ImageView imageView = new ImageView(getActivity());
                builder.setView(imageView);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                final AlertDialog dialog = builder.create();
                final MediaPlayer mediaPlayer = MediaPlayer.create(getActivity(), R.raw.audio_voice);
                ViewGroup.LayoutParams vl = new ViewGroup.LayoutParams(DimenUtils.dip2px(getActivity(), 300), ViewGroup.LayoutParams.WRAP_CONTENT);
                imageView.setLayoutParams(vl);
                final ValueAnimator valueAnimator = ValueAnimator.ofInt(100);
                valueAnimator.setDuration(300);
                valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
                valueAnimator.setRepeatMode(ValueAnimator.RESTART);
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        int i = (int) valueAnimator.getAnimatedValue();
                        LogUtil.e("i:::::::" + i);
                        if (i <= 50) {
                            imageView.setImageResource(R.drawable.audio_tip1);
                        } else if (i <= 100 && i > 50) {
                            imageView.setImageResource(R.drawable.audio_tip2);
                        }
                    }
                });

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        valueAnimator.cancel();
                        dialog.dismiss();
                        Intent intent = new Intent(getActivity(), AD.class);
                        intent.putExtra("bangzhu", true);
                        startActivity(intent);
                    }
                });

                Window window = dialog.getWindow();
                WindowManager.LayoutParams wl = window.getAttributes();
                window.getDecorView().setPadding(0, 0, 0, 0);
                wl.gravity = Gravity.CENTER;
                wl.width = getResources().getDisplayMetrics().widthPixels * 8 / 10;
                wl.height = WindowManager.LayoutParams.WRAP_CONTENT;
                window.setDimAmount(0.5f);
                window.setWindowAnimations(R.style.dialogWindowTip);
                window.setBackgroundDrawableResource(R.color.transparent);
                window.setAttributes(wl);
                dialog.show();
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        v.setVisibility(View.VISIBLE);
                        valueAnimator.cancel();
                        mediaPlayer.stop();
                        mediaPlayer.release();
                    }
                });
                valueAnimator.start();
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        imageView.performClick();

                    }
                });
                break;
            case R.id.more:
//                MDialogFragment  dialogFragment=new MDialogFragment();
//                dialogFragment.show(getChildFragmentManager(),"test");
                if (new LoginUtil().checkLogin(getActivity())) {
                    startActivity(new Intent(getActivity(), ItemManager.class));
                }
                break;
        }
    }

    private class ItemAdapter extends BaseQuickAdapter<HashMap<String, Object>, BaseViewHolder> {
        public ItemAdapter(List<HashMap<String, Object>> data) {
            super(R.layout.item_homepage, data);
        }

        @Override
        protected void convert(BaseViewHolder holder, final HashMap<String, Object> map) {
            LogUtil.e("当前功能列表：：" + itemList);
            holder.setText(R.id.text, map.get(TEXT_KEY).toString());
            holder.getView(R.id.text).setTag(map.get(TEXT_KEY).toString());
            Glide.with(getActivity()).load(map.get(IMAGE_KEY)).override(DimenUtils.dip2px(getActivity(), 40)
                    , DimenUtils.dip2px(getActivity(), 40))
                    .into((ImageView) holder.getView(R.id.image));


        }
    }


    private void getBanner() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("m_id", Constants.M_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m = ApisSeUtil.i(jsonObject);
        OkGo.post(Constants.getBanner).params("key", m.K())
                .params("msg", m.M())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        bannerList = AnalyticalJSON.getList_zj(s);

                        if (bannerList != null) {
                            ArrayList<String> images = new ArrayList<>();
                            for (int i = 0; i < bannerList.size(); i++) {
                                images.add(((HashMap) bannerList.get(i)).get("image").toString());
                            }
                            LogUtil.e("获取轮播图：：：" + images);
                            banner.setImages(images);
                            banner.start();
                        }
                    }

                    @Override
                    public void onAfter(String s, Exception e) {
                        super.onAfter(s, e);
                        if (bannerList == null || bannerList.size() == 0) {
                            ToastUtil.showToastShort("请检查网络连接");
                            bannerList = FileUtils.getStorageMapEntities(getActivity(), CACKE_BANNER);
                            if (bannerList != null) {
                                ArrayList<String> images = new ArrayList<>();
                                for (int i = 0; i < bannerList.size(); i++) {
                                    images.add(((HashMap) bannerList.get(i)).get("image").toString());
                                }
                                LogUtil.e("获取轮播图：：：" + images);
                                banner.setImages(images);
                                banner.start();
                            }
                        }
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PreferenceUtil.getSettingIncetance(getActivity()).edit().putString(ItemManager.CaCheVersion, Verification.getAppVersionName(getActivity())).apply();
        saveLists();
        EventBus.getDefault().unregister(this);
    }

    private void saveLists() {
        if (bannerList != null && bannerList.size() != 0) {
            LogUtil.e("缓存轮播图");
            FileUtils.saveStorage2SDCard(getActivity(), bannerList, CACKE_BANNER);
        }
        if (Huodonglist != null && Huodonglist.size() != 0) {
            LogUtil.e("缓存活动");
            FileUtils.saveStorage2SDCard(getActivity(), Huodonglist, CACKE_HUODONG);
        }
        if (adapter != null && adapter.getData() != null && adapter.getData().size() != 0) {
            LogUtil.e("缓存资讯");
            FileUtils.saveStorage2SDCard(getActivity(), (ArrayList) adapter.getData(), CACKE_ZIXUN);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        if (banner != null) {
            banner.releaseBanner();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Glide.with(this).load(R.drawable.small_head).skipMemoryCache(true).fitCenter().into((ImageView) rootView.findViewById(R.id.audio_tip));
        initAnimator();
        if (banner != null) {
            banner.startAutoPlay();
        }
    }
}
