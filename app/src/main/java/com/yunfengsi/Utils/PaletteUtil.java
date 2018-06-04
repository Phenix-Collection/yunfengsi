package com.yunfengsi.Utils;

import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;

/**
 * 作者：因陀罗网 on 2018/6/4 14:36
 * 公司：成都因陀罗网络科技有限公司
 */
public class PaletteUtil implements Palette.PaletteAsyncListener {


    private static PaletteUtil instance;
    private onPaletteGeneratedListener listener;

    public static PaletteUtil getInstance() {
        if (instance == null) {
            synchronized (PaletteUtil.class) {
                if (instance == null) {
                    instance = new PaletteUtil();
                }
            }
        }

        return instance;
    }
    public synchronized PaletteUtil begin(Bitmap bitmap,onPaletteGeneratedListener listener){
        instance.listener=listener;
        Palette.from(bitmap).generate(instance);

        return instance;
    }


    @Override
    public void onGenerated(Palette palette) {
        listener.onGenerated(palette);
    }


   public interface onPaletteGeneratedListener {
        void onGenerated(Palette palette);
    }

}
