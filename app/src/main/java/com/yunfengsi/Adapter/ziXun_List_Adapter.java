package com.yunfengsi.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.yunfengsi.R;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.Network;
import com.yunfengsi.Utils.NumUtils;
import com.yunfengsi.Utils.ProgressUtil;
import com.yunfengsi.Utils.TimeUtils;
import com.yunfengsi.Utils.mApplication;
import com.yunfengsi.View.mAudioManager;
import com.yunfengsi.View.mAudioView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

/**
 * Created by Administrator on 2016/6/1.
 */
public class ziXun_List_Adapter extends BaseAdapter {
    public List<HashMap<String, String>> mlist;
    private static final String TAG = "ziXun_List_Adapter";
    private Context context;
    private int screenwidth;
    private Drawable ctr, like, comment;

    //    app:layout_widthPercent="30%w"     图文列表图片宽高
//    app:layout_heightPercent="20%w"
    public ziXun_List_Adapter(Context context) {
        this.context = context;
        mlist = new ArrayList<>();
        screenwidth = context.getResources().getDisplayMetrics().widthPixels;
        ctr = ContextCompat.getDrawable(context, R.drawable.ctr_small);
        like = ContextCompat.getDrawable(context, R.drawable.like_small);
        comment = ContextCompat.getDrawable(context, R.drawable.comment_small);
        ctr.setBounds(0, 0, DimenUtils.dip2px(context, 16), DimenUtils.dip2px(context, 16));
        like.setBounds(0, 0, DimenUtils.dip2px(context, 15), DimenUtils.dip2px(context, 15));
        comment.setBounds(0, 0, DimenUtils.dip2px(context, 14), DimenUtils.dip2px(context, 14));

    }

    public void addList(List<HashMap<String, String>> mlist) {
        this.mlist = mlist;
    }

    @Override
    public int getCount() {
        return mlist.size() > 0 ? mlist.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return mlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Integer.valueOf(mlist.get(position).get("id"));
    }

    @Override
    public int getItemViewType(int position) {
        if (mlist.get(position).get("videourl").equals("")) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder = null;
        final HashMap<String, String> bean = mlist.get(position);
        if (getItemViewType(position) == 0) {
            if (view == null) {
                holder = new ViewHolder();
                view = LayoutInflater.from(context).inflate(R.layout.hot_two_item, parent, false);
                holder.img = view.findViewById(R.id.hot_two_item_image);
                holder.title = view.findViewById(R.id.hot_two_item_title);
                holder.time = view.findViewById(R.id.hot_two_item_time);
                holder.user = view.findViewById(R.id.hot_two_item_Plnum);
                holder.type = view.findViewById(R.id.hot_two_item_type);
                holder.ctr = view.findViewById(R.id.hot_two_item_ctr);
                holder.abs = view.findViewById(R.id.hot_two_item_abs);
                holder.comments = view.findViewById(R.id.hot_two_item_comments);
                holder.likes = view.findViewById(R.id.hot_two_item_likes);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            final ImageView imageView = holder.img;
            Glide.with(context).load(bean.get("image"))
                    .asBitmap()
                    .skipMemoryCache(true)
                    .override(DimenUtils.dip2px(context,136), DimenUtils.dip2px(context,102))
                    .centerCrop()
                    .into(new BitmapImageViewTarget(holder.img) {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            RoundedBitmapDrawable rbd = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                            rbd.setCornerRadius(DimenUtils.dip2px(context, 2));
                            imageView.setImageDrawable(rbd);

                        }
                    });
            holder.title.setText(mApplication.ST(bean.get("title")));
            String time = bean.get("time");
            holder.time.setText(mApplication.ST(TimeUtils.getTrueTimeStr(time)));
            holder.user.setText(mApplication.ST(bean.get("issuer")));
            holder.type.setText(mApplication.ST(bean.get("tag")));
            holder.ctr.setText(mApplication.ST(NumUtils.getNumStr(bean.get("ctr"))));
            holder.ctr.setCompoundDrawables(ctr, null, null, null);
            holder.likes.setText(NumUtils.getNumStr(bean.get("likes")));
            holder.likes.setCompoundDrawables(like, null, null, null);
            holder.comments.setText(NumUtils.getNumStr(bean.get("news_comment")));
            holder.comments.setCompoundDrawables(comment, null, null, null);
            holder.user.setTag(bean.get("active"));
            holder.abs.setText(mApplication.ST(bean.get("abstract")));

        } else if (getItemViewType(position) == 1) {
            if (view == null) {
                holder = new ViewHolder();
                view = LayoutInflater.from(context).inflate(R.layout.zixun_video_item, parent, false);
//                holder.player = (JCVideoPlayerStandard) view.findViewById(R.id.zixun_video_player);
                holder.stub = view.findViewById(R.id.zixun_video_stub);
                holder.title = view.findViewById(R.id.zixun_video_title);
                holder.time = view.findViewById(R.id.zixun_video_time);
                holder.user = view.findViewById(R.id.zixun_video_user);
                holder.ctr = view.findViewById(R.id.zixun_video_Num);
                holder.type = view.findViewById(R.id.zixun_video_tag);
                holder.likes = view.findViewById(R.id.zixun_video_likes);
                holder.comments = view.findViewById(R.id.zixun_video_comment);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            if (!bean.get("videourl").endsWith("mp3")) {
                JCVideoPlayer.releaseAllVideos();
                if (holder.stub.getChildAt(0) instanceof JCVideoPlayerStandard) {
                    JCVideoPlayerStandard jc0 = (JCVideoPlayerStandard) holder.stub.getChildAt(0);
                    Glide.with(context).load(bean.get("image")).override(screenwidth - DimenUtils.dip2px(context, 10), (screenwidth - DimenUtils.dip2px(context, 10)) / 2)
                            .centerCrop().into(jc0.thumbImageView);
                    jc0.setUp(bean.get("videourl"), bean.get("title"));
                    jc0.titleTextView.setVisibility(View.GONE);
                    jc0.fullscreenButton.setVisibility(View.GONE);
                } else {
                    holder.stub.removeAllViews();
                    JCVideoPlayerStandard jc = new JCVideoPlayerStandard(context);
                    Glide.with(context).load(bean.get("image")).override(screenwidth - DimenUtils.dip2px(context, 30), (screenwidth - DimenUtils.dip2px(context, 30)) / 2)
                            .centerCrop().into(jc.thumbImageView);
                    jc.setUp(bean.get("videourl"), bean.get("title"));
                    jc.titleTextView.setVisibility(View.GONE);
                    jc.fullscreenButton.setVisibility(View.GONE);
                    holder.stub.addView(jc);
                }
            } else if (bean.get("videourl").endsWith("mp3")) {
                final mAudioView m;
                mAudioManager.release();
                if (holder.stub.getChildAt(0) instanceof mAudioView) {
                    m = (mAudioView) holder.stub.getChildAt(0);
                    m.setTime(0);
                } else {
                    holder.stub.removeAllViews();
                    m = new mAudioView(context);
                    holder.stub.addView(m);
                }
                m.setOnImageClickListener(new mAudioView.onImageClickListener() {
                    @Override
                    public void onImageClick(mAudioView v) {
                        if (!Network.HttpTest(context)) {
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
                            mAudioManager.playSound(context, v, bean.get("videourl"), new MediaPlayer.OnCompletionListener() {
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
//            Glide.with(context).load(bean.get("image")).override(screenwidth- DimenUtils.dip2px(context,10),(screenwidth- DimenUtils.dip2px(context,10))/2)
//                    .centerCrop().into(holder.player.thumbImageView);
//            holder.player.setUp(bean.get("videourl").trim(),bean.get("title"));
//            holder.player.titleTextView.setVisibility(View.GONE);
//            holder.player.fullscreenButton.setVisibility(View.GONE);
            holder.title.setText(mApplication.ST(bean.get("title")));
            holder.title.setTag(bean.get("videourl"));
            holder.user.setTag(bean.get("active"));
            holder.ctr.setTag(bean.get("image"));
            holder.type.setText(mApplication.ST(bean.get("tag")));
            holder.user.setText(mApplication.ST(bean.get("issuer")));
            holder.ctr.setText(mApplication.ST(NumUtils.getNumStr(bean.get("ctr"))));
            holder.ctr.setCompoundDrawables(ctr, null, null, null);
            holder.likes.setText(NumUtils.getNumStr(bean.get("likes")));
            holder.likes.setCompoundDrawables(like, null, null, null);
            holder.comments.setText(NumUtils.getNumStr(bean.get("news_comment")));
            holder.comments.setCompoundDrawables(comment, null, null, null);
            holder.time.setText(mApplication.ST(TimeUtils.getTrueTimeStr(bean.get("time"))));
        }


        return view;
    }

    static class ViewHolder {
        ImageView img;
        TextView title;
        TextView likes;
        TextView comments;
        TextView time;
        TextView user;
        TextView ctr;
        TextView type;
        TextView abs;
        //        JCVideoPlayerStandard player;
        FrameLayout stub;
    }

}
