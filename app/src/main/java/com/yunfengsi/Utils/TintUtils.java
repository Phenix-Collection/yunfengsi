package com.yunfengsi.Utils;

import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;

/**
 * 作者：因陀罗网 on 2018/6/5 11:09
 * 公司：成都因陀罗网络科技有限公司
 */
public class TintUtils {


    public static Drawable getTintDrawable(Drawable src, @ColorInt int color) {
        Drawable dst = DrawableCompat.wrap(src).mutate();
        DrawableCompat.setTint(dst, color);
        return dst;
    }
    public static Drawable getTintDrawableColorRes(Drawable src, @ColorRes int ResId) {
        Drawable dst = DrawableCompat.wrap(src).mutate();
        DrawableCompat.setTint(dst, ContextCompat.getColor(mApplication.getInstance(), ResId));
        return dst;
    }
    public static Drawable getTintDrawableColorRes(int DrawableID, @ColorRes int ResId,int Sizedp) {
        Drawable dst = DrawableCompat.wrap(ContextCompat.getDrawable(mApplication.getInstance(),DrawableID)).mutate();
        DrawableCompat.setTint(dst, ContextCompat.getColor(mApplication.getInstance(), ResId));
        dst.setBounds(0,0,DimenUtils.dip2px(mApplication.getInstance(),Sizedp),DimenUtils.dip2px(mApplication.getInstance(),Sizedp));
        return dst;
    }
    public static Drawable getTintDrawable(int DrawableID, @ColorInt int color,int Sizedp) {
        Drawable dst = DrawableCompat.wrap(ContextCompat.getDrawable(mApplication.getInstance(),DrawableID)).mutate();
        DrawableCompat.setTint(dst, color);
        dst.setBounds(0,0,DimenUtils.dip2px(mApplication.getInstance(),Sizedp),DimenUtils.dip2px(mApplication.getInstance(),Sizedp));
        return dst;
    }
}
