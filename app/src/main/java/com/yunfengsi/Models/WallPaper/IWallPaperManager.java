package com.yunfengsi.Models.WallPaper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.yunfengsi.Utils.ImageUtil;

import java.util.ArrayList;
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


    public static void goToWallPaperDetailCompat(Activity context, int  pos, ArrayList<HashMap<String,String >> list, View view, boolean deleteAble, boolean isFromCollection){
        Intent intent = new Intent(context, WallPaperDetail.class);

//        intent.putExtra("url",list.get(pos).get("image"));
        intent.putExtra("collect",isFromCollection);
        intent.putExtra("id",isFromCollection?list.get(pos).get("wallpaper_id"):list.get(pos).get("id"));
        intent.putExtra("info",captureValues(view));
        intent.putExtra("pos",pos);
        intent.putExtra("delete",deleteAble);
        intent.putExtra("paths",list);
        if(isFromCollection){
            context.startActivityForResult(intent,111);
        }else{
            context.startActivity(intent);
        }
        context.overridePendingTransition(0,0);
    }

    private static Bundle captureValues(@NonNull View view) {
        Bundle b = new Bundle();
        int[] screenLocation = new int[2];
        view.getLocationOnScreen(screenLocation);
        b.putInt("left", screenLocation[0]);
        b.putInt("top", screenLocation[1]);
        b.putInt("width", view.getWidth());
        b.putInt("height", view.getHeight());
        return b;
    }


}
