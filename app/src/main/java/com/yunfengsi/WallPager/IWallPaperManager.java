package com.yunfengsi.WallPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * 作者：因陀罗网 on 2018/5/28 14:17
 * 公司：成都因陀罗网络科技有限公司
 */
public class IWallPaperManager {

    public static  void goToWallPaperDetailNormal(Context context,String path){
        Intent intent=new Intent(context,WallPaperDetail.class);
        context.startActivity(intent);
    }

    public static  void goToWallPaperDetailWithAnim(Context context, Bundle bundle,String path){
        Intent intent=new Intent(context,WallPaperDetail.class);
        intent.putExtra("path",path);
        context.startActivity(intent,bundle);
    }
}
