package com.yunfengsi.WallPager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.yunfengsi.Utils.ImageUtil;

import java.util.HashMap;

/**
 * 作者：因陀罗网 on 2018/5/28 14:17
 * 公司：成都因陀罗网络科技有限公司
 */
public class IWallPaperManager {

    public static void goToWallPaperDetailNormal(Context context, Bitmap bitmap, HashMap<String,String >map) {
        Intent intent = new Intent(context, WallPaperDetail.class);
        intent.putExtra("photoBytes", ImageUtil.Bitmap2StrByBase64(bitmap));
        intent.putExtra("map", map);
        context.startActivity(intent);
    }

    public static void goToWallPaperDetailWithAnim(Context context, Bundle bundle, Bitmap bitmap,HashMap<String,String >map) {
        Intent intent = new Intent(context, WallPaperDetail.class);
        Bundle b      = new Bundle();
        intent.putExtra("photoBytes", ImageUtil.Bitmap2StrByBase64(bitmap));
        intent.putExtra("map", map);

//intent.putExtra("path",path);

        context.startActivity(intent, bundle);
    }
}
