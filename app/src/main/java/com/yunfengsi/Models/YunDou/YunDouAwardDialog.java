package com.yunfengsi.Models.YunDou;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.yunfengsi.R;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.mApplication;

/**
 * 作者：因陀罗网 on 2018/5/9 17:28
 * 公司：成都因陀罗网络科技有限公司
 */
public class YunDouAwardDialog {



    public static void show(Activity activity, String title,String num){
        LogUtil.e("执行弹窗" );
        AlertDialog.Builder builder=new AlertDialog.Builder(activity);
        View view= LayoutInflater.from(activity).inflate(R.layout.dialog_yundou_award,null);
        builder.setView(view);
        final AlertDialog dialog     =builder.create();
        TextView          txt_num    =view.findViewById(R.id.awardNum);
        TextView          txt_commit =view.findViewById(R.id.commit);
        TextView          txt_msg=view.findViewById(R.id.message);
        txt_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        txt_num.setText(mApplication.ST("云豆 +"+num));
        txt_msg.setText(mApplication.ST("通过完成["+title+"]任务，即可获得云豆奖励"));
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        Window window = dialog.getWindow();

        window.setGravity(Gravity.CENTER);
        window.getDecorView().setPadding(0,0,0,0);
        window.setWindowAnimations(R.style.dialogWindowAnim);
        window.setBackgroundDrawableResource(R.color.vifrification);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.width=activity.getResources().getDisplayMetrics().widthPixels*65/100;
        wl.height=WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(wl);
        dialog.show();
    }

}
