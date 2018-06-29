package com.yunfengsi.Models.BlessTree;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.moxun.tagcloudlib.view.TagCloudView;
import com.moxun.tagcloudlib.view.TagsAdapter;
import com.ruffian.library.RTextView;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.yunfengsi.R;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.LoginUtil;
import com.yunfengsi.Utils.Network;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.ProgressUtil;
import com.yunfengsi.Utils.ShareManager;
import com.yunfengsi.Utils.ToastUtil;
import com.yunfengsi.Utils.mApplication;
import com.yunfengsi.View.AutoPollRecyclerView;
import com.yunfengsi.View.ScrollSpeedLinearLayoutManger;
import com.yunfengsi.View.mItemDecoration;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 作者：因陀罗网 on 2018/3/8 13:22
 * 公司：成都因陀罗网络科技有限公司
 */

public class BlessTree extends AppCompatActivity implements View.OnClickListener {
    private RTextView                          txt_xuyuan;
    private AutoPollRecyclerView               recyclerView;
    private MessageAdapter                     messageAdapter;
    private ArrayList<HashMap<String, String>> list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        StatusBarCompat.compat(this, getResources().getColor(R.color.transparent));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.bless_tree);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            FrameLayout.LayoutParams fl = (FrameLayout.LayoutParams) findViewById(R.id.head).getLayoutParams();
            fl.topMargin = DimenUtils.dip2px(this, 25);
            findViewById(R.id.head).setLayoutParams(fl);

        }
        init();
    }

    private void init() {
        mApplication.getInstance().addActivity(this);
        ImageView    back         = (ImageView) findViewById(R.id.back);
        TextView     history      = (TextView) findViewById(R.id.history);
        RTextView txt_xuyuan = (RTextView) findViewById(R.id.xuyuan);
        TagCloudView tagCloudView = (TagCloudView) findViewById(R.id.tree_view);
        ArrayList list = new ArrayList();
        for (int i = 0; i < 20; i++) {
            list.add(new HashMap<>());
        }
        TreeAdapter adapter = new TreeAdapter(list);
        tagCloudView.setAdapter(adapter);

        recyclerView = (AutoPollRecyclerView) findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new ScrollSpeedLinearLayoutManger(this));
        messageAdapter = new MessageAdapter(list);
        recyclerView.addItemDecoration(new mItemDecoration(this));
        recyclerView.setAdapter(messageAdapter);


        back.setOnClickListener(this);
        history.setOnClickListener(this);
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(mApplication.ST("祈愿树"));
        txt_xuyuan.setOnClickListener(this);

        getContents();
    }

    private void getContents() {
        if (!Network.HttpTest(this)) {
            recyclerView.setVisibility(View.GONE);
            return;
        } else {
            recyclerView.setVisibility(View.VISIBLE);
        }
        JSONObject js = new JSONObject();
        try {
            js.put("m_id", Constants.M_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m = ApisSeUtil.i(js);
        OkGo.post(Constants.BlessContent).params("key", m.K())
                .params("msg", m.M())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        HashMap<String, String> map = AnalyticalJSON.getHashMap(s);
                        if (map != null) {
                            if (map.get("code") != null && map.get("code").equals("000")) {
                                list = AnalyticalJSON.getList_zj(map.get("msg"));
                                if (list.size() > 4) {
                                    recyclerView.start();
                                }
                                messageAdapter.setNewData(list);
                            } else {
                                LogUtil.e("未获取到祈愿动态");
                            }

                        }
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        ProgressUtil.show(BlessTree.this, "", "请稍等");
                    }

                    @Override
                    public void onAfter(String s, Exception e) {
                        super.onAfter(s, e);
                        ProgressUtil.dismiss();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.history:
                startActivity(new Intent(this, Bless_History.class));

                break;
            case R.id.xuyuan:
                if (!Network.HttpTest(this)) {
                    return;
                }

                View view = LayoutInflater.from(this).inflate(R.layout.bless_dialog, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(view);
                final AlertDialog dialog = builder.create();
                TextView tip = (TextView) view.findViewById(R.id.msg);
                tip.setText(mApplication.ST("弟子向菩萨诚心祈祷，惟愿:"));
                final EditText content = (EditText) view.findViewById(R.id.content);
                TextView commit = (TextView) view.findViewById(R.id.commit);
                commit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (new LoginUtil().checkLogin(BlessTree.this)) {
                            if (content.getText().toString().trim().equals("")) {
                                ToastUtil.showToastShort("请输入祈愿内容");
                                return;
                            }
                            JSONObject js = new JSONObject();
                            try {
                                js.put("m_id", Constants.M_id);
                                js.put("user_id", PreferenceUtil.getUserId(BlessTree.this));
                                js.put("content", content.getText().toString().trim());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ApisSeUtil.M m = ApisSeUtil.i(js);
                            OkGo.post(Constants.Bless).params("key", m.K())
                                    .params("msg", m.M())
                                    .execute(new StringCallback() {
                                        @Override
                                        public void onSuccess(String s, Call call, Response response) {
                                            final HashMap<String, String> map = AnalyticalJSON.getHashMap(s);
                                            if (map != null) {
                                                if (map.get("code") != null && map.get("code").equals("000")) {
                                                    dialog.dismiss();
                                                    ProgressUtil.dismiss();
                                                    getContents();
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(BlessTree.this);
                                                    builder.setMessage(mApplication.ST("祈愿成功，同时希望你在生活中为之努力，争取早日实现愿望，阿弥陀佛"))
                                                            .setPositiveButton(mApplication.ST("分享愿望"), new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                                    UMWeb umWeb = new UMWeb(Constants.FX_host_Ip + "wish" + "/id/" + map.get("id") + "/st/" + (mApplication.isChina ? "s" : "t"));
                                                                    umWeb.setTitle(mApplication.ST("祈愿功德回向"));
                                                                    umWeb.setDescription(mApplication.ST("一叶一菩提，一花一世界；爱出者爱返，福往者福来"));
                                                                    umWeb.setThumb(new UMImage(BlessTree.this, R.drawable.indra_share));
                                                                    new ShareManager().shareWeb(umWeb, BlessTree.this);
                                                                }
                                                            }).setNegativeButton(mApplication.ST("知道了"), new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            dialogInterface.dismiss();
                                                        }
                                                    }).create().show();

                                                }
                                            }
                                        }

                                        @Override
                                        public void onBefore(BaseRequest request) {
                                            super.onBefore(request);
                                            ProgressUtil.show(BlessTree.this, "", "正在提交祈愿");
                                        }

                                        @Override
                                        public void onAfter(String s, Exception e) {
                                            super.onAfter(s, e);
                                            ProgressUtil.dismiss();
                                        }
                                    });
                        }
                    }
                });


                Window window = dialog.getWindow();
                WindowManager.LayoutParams wl = window.getAttributes();
                window.getDecorView().setPadding(0, 0, 0, 0);
                wl.gravity = Gravity.CENTER;
                wl.width = getResources().getDisplayMetrics().widthPixels * 8 / 10;
                wl.height = getResources().getDisplayMetrics().heightPixels * 6 / 10;
                window.setDimAmount(0.7f);
                window.setWindowAnimations(R.style.dialogWindowAnim);
                window.setBackgroundDrawableResource(R.color.transparent);
                window.setAttributes(wl);
                dialog.show();
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ProgressUtil.dismiss();
    }

    @Override
    protected void onDestroy() {
        if (recyclerView != null) {
            recyclerView.stop();
        }
        super.onDestroy();
        mApplication.getInstance().romoveActivity(this);
    }

    public class MessageAdapter extends BaseQuickAdapter<HashMap<String, String>, BaseViewHolder> {
        private Drawable place;

        public MessageAdapter(List<HashMap<String, String>> data) {
            super(R.layout.item_tree_message, data);
            place = ContextCompat.getDrawable(BlessTree.this, R.drawable.placeholder_c6c6c6);
            place.setBounds(0, 0, DimenUtils.dip2px(BlessTree.this, 30), DimenUtils.dip2px(BlessTree.this, 30));
        }

        @Override
        protected void convert(BaseViewHolder holder, HashMap<String, String> map) {
            final RTextView rt = holder.getView(R.id.name);
            if (!BlessTree.this.isDestroyed()) {
                Glide.with(BlessTree.this).load(map.get("user_image"))
                        .asBitmap()
                        .override(DimenUtils.dip2px(BlessTree.this, 30)
                                , DimenUtils.dip2px(BlessTree.this, 30))
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                RoundedBitmapDrawable rbd = RoundedBitmapDrawableFactory.create(getResources(), resource);
                                rbd.setCircular(true);
                                rt.setIconNormal(rbd);
                            }

                            @Override
                            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                super.onLoadFailed(e, errorDrawable);
                                rt.setIconNormal(place);
                            }
                        });
            }


            holder.setText(R.id.name, map.get("pet_name"))
                    .setText(R.id.def, mApplication.ST("许下一个愿望"));
//                    .setText(R.id.time, TimeUtils.getTrueTimeStr(map.get("time")));
        }

        @Override
        public HashMap<String, String> getItem(int position) {
            return getData().get(position % getData().size());
        }

//        @Override
//        public int getItemCount() {
//            return Integer.MAX_VALUE;
//        }
    }

    public class TreeAdapter extends TagsAdapter {
        private ArrayList<HashMap<String, String>> list;

        public TreeAdapter(ArrayList<HashMap<String, String>> list) {
            super();
            this.list = list;
        }

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public View getView(final Context context, final int position, ViewGroup parent) {
            ImageView imageView = new ImageView(context);
            Glide.with(context).load(R.drawable.zhufu).override(DimenUtils.dip2px(context, 30), DimenUtils.dip2px(context, 25))
                    .into(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (BlessTree.this.list != null) {
                        int count = BlessTree.this.list.size();
                        int i     = new Random().nextInt(count);
                        LogUtil.e("count::;" + count + " iiiii:::" + i);
                        AlertDialog.Builder builder = new AlertDialog.Builder(BlessTree.this);
                        builder.setMessage(mApplication.ST(BlessTree.this.list.get(i).get("content")))
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                }).create().show();
                    } else {
                        if (!Network.HttpTest(context)) {
                            ToastUtil.showToastShort("网络连接异常，请稍后重试");
                        } else {
                            ToastUtil.showToastShort("暂无人祈愿");
                        }

                    }
                }
            });
            return imageView;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public int getPopularity(int position) {
            return 5;
        }

        @Override
        public void onThemeColorChanged(View view, int themeColor) {


            ColorMatrix colorMatrix = new ColorMatrix();

            float red   = Math.max(0.7f, (Color.red(themeColor / 0xff) / 255f));
            float blue  = Math.max(0.7f, (Color.blue(themeColor / 0xff) / 255f));
            float green = Math.max(0.7f, (Color.green(themeColor / 0xff) / 255f));
            colorMatrix.setScale(red, blue, green, 1);

            ((ImageView) view).setColorFilter(new ColorMatrixColorFilter(colorMatrix));
//            BigInteger bigInteger=new BigInteger(String.valueOf(themeColor),16);
//            LogUtil.e("颜色变化"+themeColor+"  透明度：："+(themeColor/0xff)+"  值  "+Color.alpha(themeColor/0xff)+"    16进制数：："+0xff);

        }
    }
}
