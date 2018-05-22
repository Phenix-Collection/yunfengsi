package com.yunfengsi.View;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.yunfengsi.R;
import com.yunfengsi.Utils.mApplication;

/**
 * 作者：因陀罗网 on 2018/4/10 16:57
 * 公司：成都因陀罗网络科技有限公司
 */
public class MDialogFragment extends DialogFragment {
    private Dialog dialog;
    public MDialogFragment() {
        super();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog .Builder builder=new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(mApplication.getInstance()).inflate(R.layout.dialog_album_camera, null);
        builder.setView(view);
        dialog = builder.create();
        view.findViewById(R.id.cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Window window = dialog.getWindow();
        window.getDecorView().setPadding(0,0,0,0);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.dialogWindowAnim);
        window.setBackgroundDrawableResource(R.color.vifrification);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.width=getActivity().getResources().getDisplayMetrics().widthPixels;
        wl.height=WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(wl);

        return dialog;
    }
}
