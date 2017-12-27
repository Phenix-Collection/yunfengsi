package com.qianfujiaoyu.Activitys;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.request.BaseRequest;
import com.qianfujiaoyu.Base.BaseActivity;
import com.qianfujiaoyu.R;
import com.qianfujiaoyu.TouGao.TG_List;
import com.qianfujiaoyu.TouGao.TouGao;
import com.qianfujiaoyu.Utils.ApisSeUtil;
import com.qianfujiaoyu.Utils.Constants;
import com.qianfujiaoyu.Utils.DimenUtils;
import com.qianfujiaoyu.Utils.ImageUtil;
import com.qianfujiaoyu.Utils.LogUtil;
import com.qianfujiaoyu.Utils.ProgressUtil;
import com.qianfujiaoyu.Utils.ToastUtil;
import com.qianfujiaoyu.Utils.mApplication;
import com.qianfujiaoyu.View.mAudioManager;
import com.qianfujiaoyu.View.mAudioView;
import com.qianfujiaoyu.View.mItemDecoration;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;
import jp.wasabeef.glide.transformations.BlurTransformation;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 作者：因陀罗网 on 2017/5/18 17:17
 * 公司：成都因陀罗网络科技有限公司
 */

public class Home_Class extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    public static final String MEMBER = "1";//普通人
    public static final String TEACHER = "2";//老师
    public static final String Admin = "3";//班主任
    private static final String TAG = "Home_Class";
    private SwipeRefreshLayout swip;
    private RecyclerView recyclerView;
    private int page = 1;
    private int endPage = -1;
    private boolean isLoadMore = false;
    private boolean isRefresh;
    private classDongtaiAdapter adapter;
    private String classId = "";
    private ImageView more;
    private PopupWindow pp;

    private TextView title;
    private TextView num;
    private File tmpFile;
    private ImageView classBack;

    public static class ClassInfo {
//        private HashMap<String,String> map;
//
//        public HashMap<String, String> getMap() {
//            return map;
//        }
//
//        public void setMap(HashMap<String, String> map) {
//            this.map = map;
//        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.home_class;
    }

    @Override
    public void initView() {
        classId = getIntent().getStringExtra("id");


        swip = (SwipeRefreshLayout) findViewById(R.id.swip);
        swip.setColorSchemeResources(R.color.main_color);
        swip.setOnRefreshListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new mItemDecoration(this));
        adapter = new classDongtaiAdapter(this, new ArrayList<ClassModel>());
        adapter.openLoadMore(10, true);
        adapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        adapter.setEmptyView(mApplication.getLoadNothing(R.drawable.load_nothing, "暂无班级动态\n\n下拉刷新",150));
        LogUtil.e("占位控件：：" + adapter.getEmptyView());
        num = (TextView) findViewById(R.id.class_num);
        title = (TextView) findViewById(R.id.class_name);
        num.setText("人数:" + getIntent().getStringExtra("num") + "人");
        title.setText(getIntent().getStringExtra("title"));
        classBack = (ImageView) findViewById(R.id.class_back);
        //设置班级封面
        if (getIntent().getStringExtra("url").equals("")) {
            Glide.with(this).load(R.drawable.indra).override(getResources().getDisplayMetrics().widthPixels, DimenUtils.dip2px(this, 120))
                    .bitmapTransform(new BlurTransformation(this, 25)).fitCenter().into(classBack);
        } else {
            Glide.with(this).load(getIntent().getStringExtra("url")).override(getResources().getDisplayMetrics().widthPixels, DimenUtils.dip2px(this, 120))
                    .bitmapTransform(new BlurTransformation(this, 25)).centerCrop().into(classBack);
        }


        LogUtil.e("班级动态权限：：" + getIntent().getStringExtra("role"));

        //判断是否有权限修改班级信息
        if (getIntent().getStringExtra("role").equals(Admin)) {
            findViewById(R.id.handle).setVisibility(View.VISIBLE);
            findViewById(R.id.handle).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), UpdateClassInfo.class);
                    intent.putExtra("title", getIntent().getStringExtra("title"));
                    intent.putExtra("url", getIntent().getStringExtra("url"));
                    intent.putExtra("id", getIntent().getStringExtra("id"));
                    startActivityForResult(intent, 99);

                }
            });
        } else {
            findViewById(R.id.handle).setVisibility(View.GONE);
        }


//        });
        adapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                String ID = ((ClassModel) adapter.getData().get(i)).getId();
                Intent intent = new Intent(Home_Class.this, Home_Zixun_detail.class);
                intent.putExtra("id", ID);
                startActivity(intent);
            }
        });
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (endPage != page) {
                    isLoadMore = true;
                    page++;
                    getData();
                }
            }
        });
        recyclerView.setAdapter(adapter);
        more = (ImageView) findViewById(R.id.right1);
        findViewById(R.id.back).setVisibility(View.VISIBLE);
        more.setVisibility(View.VISIBLE);
        more.setOnClickListener(this);
        more.setImageBitmap(ImageUtil.readBitMap(this, R.drawable.home_add_more));
        ViewGroup.MarginLayoutParams vm = (ViewGroup.MarginLayoutParams) more.getLayoutParams();
        vm.rightMargin = 0;
        more.setLayoutParams(vm);
        ((ImageView) findViewById(R.id.back)).setImageBitmap(ImageUtil.readBitMap(this, R.drawable.back));
        ((TextView) findViewById(R.id.title)).setText("班级动态");
    }

    private static class classDongtaiAdapter extends BaseMultiItemQuickAdapter<ClassModel> {
        private WeakReference<Activity> w;
        private Activity context;
        private LayoutInflater inflater;
        private int imgWidth;

        private Drawable pl_img, like_img;
        private int dp50;

        public classDongtaiAdapter(Activity context, ArrayList<ClassModel> data) {
            super(data);
            addItemType(0, R.layout.zixun_list_item);
            addItemType(1, R.layout.zixun_list_item_video);
            addItemType(2, R.layout.zixun_list_item_audio);
            w = new WeakReference<>(context);
            this.context = w.get();
            this.inflater = context.getLayoutInflater();
            imgWidth = (context.getResources().getDisplayMetrics().widthPixels - DimenUtils.dip2px(context, 40)) / 3;
            pl_img = ContextCompat.getDrawable(context, R.drawable.pinglun_16);
            like_img = ContextCompat.getDrawable(context, R.drawable.dianzan_15);
            dp50 = DimenUtils.dip2px(context, 40);
            pl_img.setBounds(0, 0, DimenUtils.dip2px(context, 15), DimenUtils.dip2px(context, 15));
            like_img.setBounds(0, 0, DimenUtils.dip2px(context, 15), DimenUtils.dip2px(context, 15));
        }


        @Override
        protected void convert(BaseViewHolder holder, ClassModel map) {
            Glide.with(context).load(map.headUrl).override(dp50
                    , dp50).placeholder(R.drawable.indra).into((ImageView) holder.getView(R.id.zixun_item_head));
            ((TextView) holder.getView(R.id.zixun_item_pl_text)).setCompoundDrawables(pl_img, null, null, null);
            ((TextView) holder.getView(R.id.zixun_item_like_text)).setCompoundDrawables(like_img, null, null, null);
            holder.setText(R.id.zixun_item_name, map.getName())
                    .setText(R.id.zixun_item_time, map.getTime())
                    .setText(R.id.zixun_item_pl_text, map.getNum_pl())
                    .setText(R.id.zixun_item_like_text, map.getNum_like());
            switch (map.getItemType()) {
                case 0://图文
                    ViewGroup layout = ((ViewGroup) holder.getView(R.id.zixun_item_image_layout));
                    layout.removeAllViews();
                    ArrayList<String> l = map.getImages();
                    for (String url : l) {
                        ImageView imageView = new ImageView(context);
                        imageView.setLayoutParams(new ViewGroup.LayoutParams(imgWidth, imgWidth));
                        Glide.with(context).load(url).override(imgWidth, imgWidth).centerCrop().placeholder(R.drawable.load_nothing).into(imageView);
                        layout.addView(imageView);
                    }

                    TextView t = holder.getView(R.id.zixun_item_title);
                    if (!map.getContent().equals("") && l.size() == 0) {
                        t.setMaxLines(5);
                        SpannableString c = new SpannableString(map.getTitle() + "\n\n" + map.getContent());
                        c.setSpan(new AbsoluteSizeSpan(DimenUtils.dip2px(context, 18)), 0, map.getTitle().length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                        c.setSpan(new AbsoluteSizeSpan(DimenUtils.dip2px(context, 14)), map.getTitle().length(), c.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                        c.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.huise)), map.getTitle().length(), c.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                        t.setText(c);
                    } else {
                        t.setText(map.getTitle());
                        t.setTextColor(Color.BLACK);
                        t.setMaxLines(2);
                    }
                    break;
                case 1://视频
                    addVideo(holder, map);
                    ((TextView) holder.getView(R.id.zixun_item_title)).setText(map.getTitle());
                    break;
                case 2://音频
                    addAudio(holder, map);
                    ((TextView) holder.getView(R.id.zixun_item_title)).setText(map.getTitle());
                    break;

            }
        }

        private void addAudio(final BaseViewHolder holder_a, final ClassModel map) {
            final mAudioView audioView = ((mAudioView) holder_a.getView(R.id.zixun_item_audio));
            audioView.setOnImageClickListener(new mAudioView.onImageClickListener() {
                @Override
                public void onImageClick(final mAudioView v) {
                    if (mAudioManager.getAudioView() != null && mAudioManager.getAudioView().isPlaying()) {
                        mAudioManager.release();
                        mAudioManager.getAudioView().setPlaying(false);
                        mAudioManager.getAudioView().resetAnim();
                        if (v == mAudioManager.getAudioView()) {
                            return;
                        }
                    }
                    if (!audioView.isPlaying()) {
                        Log.w(TAG, "onImageClick: 开始播放");
                        mAudioManager.playSound(v, map.getMediaUrl(), new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                audioView.resetAnim();
                            }
                        }, new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                audioView.setTime(mAudioManager.mMediaplayer.getDuration() / 1000);
                            }
                        });

                    } else {
                        Log.w(TAG, "onImageClick: 停止播放");
                        mAudioManager.release();
                    }

                }
            });
        }

        private void addVideo(BaseViewHolder holder, ClassModel map) {
            JCVideoPlayerStandard player = holder.getView(R.id.zixun_item_video);
            JCVideoPlayer.releaseAllVideos();
            player.setUp(map.mediaUrl, JCVideoPlayer.SCREEN_LAYOUT_LIST, "  ");
            if (map.getThumb().equals("")) {
                Glide.with(context).load(R.drawable.indra).override(context.getResources().getDisplayMetrics().widthPixels - DimenUtils.dip2px(context, 20), DimenUtils.dip2px(context, 180))
                        .fitCenter()
                        .crossFade(1000).into(player.thumbImageView);
            } else {
                Glide.with(context).load(map.getThumb()).override(context.getResources().getDisplayMetrics().widthPixels - DimenUtils.dip2px(context, 20), DimenUtils.dip2px(context, 180))
                        .centerCrop()
                        .crossFade(1000).into(player.thumbImageView);
            }

            player.backButton.setVisibility(View.GONE);

        }
    }

    private static class ClassModel extends MultiItemEntity {
        private String id;
        private String headUrl;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        private String title;
        private String name;
        private String Thumb;

        public String getContent() {
            return content;
        }

        public String getThumb() {
            return Thumb;
        }

        public void setThumb(String thumb) {
            Thumb = thumb;
        }

        public void setContent(String content) {
            this.content = content;
        }

        private String content;
        private String time;
        private String num_pl;
        private String num_like;
        private String mediaUrl;
        private ArrayList<String> images;

        public String getHeadUrl() {
            return headUrl;
        }

        public void setHeadUrl(String url) {
            this.headUrl = url;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getNum_pl() {
            return num_pl;
        }

        public void setNum_pl(String num_pl) {
            this.num_pl = num_pl;
        }

        public String getNum_like() {
            return num_like;
        }

        public void setNum_like(String num_like) {
            this.num_like = num_like;
        }

        public ArrayList<String> getImages() {
            return images;
        }

        public void setImages(ArrayList<String> images) {
            this.images = images;
        }

        public String getMediaUrl() {
            return mediaUrl;
        }

        public void setMediaUrl(String mediaUrl) {
            this.mediaUrl = mediaUrl;
        }
    }

    // TODO: 2017/5/18 获取数据
    private void getData() {
        JSONObject js = new JSONObject();
        try {
            js.put("class_id", classId);
            js.put("page", page);
            js.put("m_id", Constants.M_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m = ApisSeUtil.i(js);
        OkGo.post(Constants.ClassDongtaiList)
                .tag(this)
                .params("key", m.K())
                .params("msg", m.M())
                .execute(new AbsCallback<ArrayList<ClassModel>>() {
                    @Override
                    public void onSuccess(ArrayList<ClassModel> l, Call call, Response response) {
                        if (l != null) {
                            LogUtil.e(l + "");
                            if (isRefresh) {
                                adapter.setNewData(l);
                                isRefresh = false;
                                swip.setRefreshing(false);
                            } else if (isLoadMore) {
                                isLoadMore = false;
                                if (l.size() < 10) {
                                    ToastUtil.showToastShort("班级动态加载完毕", Gravity.CENTER);
                                    endPage = page;
                                    adapter.notifyDataChangedAfterLoadMore(l, false);
                                } else {
                                    adapter.notifyDataChangedAfterLoadMore(l, true);
                                }
                            }
                        }
                    }

                    @Override
                    public ArrayList<ClassModel> convertSuccess(Response response) throws Exception {
                        String data = response.body().string();
                        if (data.equals("") || data.equals("null")) {
                            return new ArrayList<ClassModel>();
                        }
                        JSONArray jsonArray = new JSONArray(data);
                        ArrayList<ClassModel> ac = new ArrayList<ClassModel>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject js = (JSONObject) jsonArray.get(i);
                            ClassModel cs = new ClassModel();
                            if (js.getString("options").equals("")) {
                                cs.setItemType(0);
                                cs.setMediaUrl("");
                            } else {
                                if (js.getString("options").endsWith("mp4")) {
                                    cs.setItemType(1);
                                } else if (js.getString("options").endsWith("mp3")) {
                                    cs.setItemType(2);
                                }
                                cs.setMediaUrl(js.getString("options"));
                            }
                            cs.setName(js.getString("pet_name"));
                            cs.setTitle(js.getString("title"));
                            cs.setNum_like(js.getString("likes"));
                            cs.setNum_pl(js.getString("draft_comment"));
                            cs.setHeadUrl(js.getString("user_image"));
                            cs.setContent(js.getString("contents"));
                            cs.setThumb(js.getString("image1"));
                            cs.setTime(js.getString("time"));
                            cs.setId(js.getString("id"));
                            ArrayList<String> l = new ArrayList<String>();
                            JSONArray j = js.getJSONArray("image");
                            for (int s = 0; s < j.length(); s++) {
                                l.add(((JSONObject) j.get(s)).getString("url"));
                            }
                            cs.setImages(l);
                            ac.add(cs);
                        }


                        return ac;
                    }

                    @Override
                    public void onAfter(ArrayList<ClassModel> hashMaps, Exception e) {
                        super.onAfter(hashMaps, e);
                        swip.setRefreshing(false);

                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);

                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        swip.setRefreshing(true);
                    }
                });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 666) {
            title.setText(data.getStringExtra("title"));
            Glide.with(this).load(data.getStringExtra("url")).override(getResources().getDisplayMetrics().widthPixels, DimenUtils.dip2px(this, 120))
                    .bitmapTransform(new BlurTransformation(this, 25)).centerCrop().into(classBack);
            LogUtil.e("返回信息：：" + data.getStringExtra("url") + "     " + data.getStringExtra("title"));
        }
    }

    @Subscribe
    public void updateHomeClass(ClassInfo classInfo) {
        LogUtil.e("刷新班级首页");
        swip.post(new Runnable() {
            @Override
            public void run() {
                swip.setRefreshing(true);
                onRefresh();
            }
        });
    }

    @Override
    public boolean setEventBus() {
        return true;
    }

    @Override
    public boolean isMainColor() {
        return true;
    }

    @Override
    public void doThings() {
        swip.post(new Runnable() {
            @Override
            public void run() {
                swip.setRefreshing(true);
                onRefresh();
            }
        });
    }

    @Override
    public void onRefresh() {
        page = 1;
        isRefresh = true;
        endPage = -1;
        adapter.openLoadMore(10, true);
        getData();
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.right1:
                View view = LayoutInflater.from(mApplication.getInstance()).inflate(R.layout.home_add_layout, null);
                if (pp == null) {
                    pp = new PopupWindow(view);
                    pp.setFocusable(true);
                    pp.setOutsideTouchable(true);
                    pp.setTouchable(true);
                    ColorDrawable c = new ColorDrawable(ContextCompat.getColor(this, R.color.main_color));
                    pp.setBackgroundDrawable(c);
                    pp.setWidth(DimenUtils.dip2px(mApplication.getInstance(), 150));
                    pp.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                    pp.showAsDropDown(more, -50, 0);
                } else {
                    if (pp.isShowing()) pp.dismiss();
                    pp.showAsDropDown(more, -50, 0);
                }
                initPopupWindow(view);
                break;
            case R.id.item_fabu:// TODO: 2017/5/18 发布
                intent.setClass(this, TouGao.class);
                intent.putExtra("id", classId);
                startActivity(intent);
                if(pp!=null){
                    pp.dismiss();
                }
                break;
            case R.id.item_guanli:// TODO: 2017/5/18 动态管理
                intent.setClass(this, TG_List.class);
                intent.putExtra("id", classId);
                if(pp!=null){
                    pp.dismiss();
                }
                startActivity(intent);
                break;
            case R.id.item_member:// TODO: 2017/5/18 班级成员
                intent.setClass(this, Member_List.class);
                intent.putExtra("id", classId);
                intent.putExtra("role", getIntent().getStringExtra("role"));
                LogUtil.e("权限：：：" + getIntent().getStringExtra("role"));
                if(pp!=null){
                    pp.dismiss();
                }
                startActivity(intent);
                break;
            case R.id.item_shenhe:
                if(pp!=null){
                    pp.dismiss();
                }
                intent.setClass(this, ApplyShenhe.class);
                startActivity(intent);
                break;
        }
    }

    private void initPopupWindow(View v) {
        if (getIntent().getStringExtra("role").equals(MEMBER)) {

            v.findViewById(R.id.item_fabu).setVisibility(View.GONE);
            v.findViewById(R.id.item_guanli).setVisibility(View.GONE);
            v.findViewById(R.id.item_shenhe).setVisibility(View.GONE);
            v.findViewById(R.id.item_member).setVisibility(View.VISIBLE);
            v.findViewById(R.id.item_member).setOnClickListener(this);

        } else {
            v.findViewById(R.id.item_fabu).setVisibility(View.VISIBLE);
            v.findViewById(R.id.item_guanli).setVisibility(View.VISIBLE);
            v.findViewById(R.id.item_shenhe).setVisibility(View.VISIBLE);
            v.findViewById(R.id.item_member).setVisibility(View.VISIBLE);
            v.findViewById(R.id.item_fabu).setOnClickListener(this);// TODO: 2017/1/12 发布动态
            TextView textView = (TextView) v.findViewById(R.id.item_shenhe);
            textView.setOnClickListener(this);
            textView.setVisibility(View.VISIBLE);
            v.findViewById(R.id.item_shenhe).setOnClickListener(this);
            v.findViewById(R.id.item_member).setOnClickListener(this);// TODO: 2017/1/12  班级成员
            v.findViewById(R.id.item_guanli).setOnClickListener(this);// TODO: 2017/1/12 动态管理
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        mAudioManager.pause();
        JCVideoPlayer.releaseAllVideos();
        ProgressUtil.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(this);
    }
}
