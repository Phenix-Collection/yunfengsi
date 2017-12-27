package com.maimaizu.Base;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.maimaizu.R;
import com.maimaizu.Utils.AnalyticalJSON;
import com.maimaizu.Utils.Constants;
import com.maimaizu.Utils.DimenUtils;
import com.maimaizu.Utils.LogUtil;
import com.maimaizu.Utils.LoginUtil;
import com.maimaizu.Utils.TimeUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by Administrator on 2017/5/1.
 */

public class PersonUtil {
    /**
     * 打电话
     *
     * @param tel 电话号码
     */
    public static void callPhone1(Activity context, String tel) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.CALL_PHONE}, 0);
            return;
        }
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + tel));
        context.startActivity(intent);
    }

    public static void openPersonDialog(Activity activity, final HashMap<String, String> map) {

        WeakReference<Activity> weakReference = new WeakReference<Activity>(activity);
        final Activity context = weakReference.get();
        if(!new LoginUtil().checkLogin(context)){
            return;
        }
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_person, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setView(view);
        AlertDialog dialog = builder.create();
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wl = window.getAttributes();
        window.setBackgroundDrawableResource(R.color.transparent);
        window.setAttributes(wl);
        dialog.show();
        if (map != null) {
            Glide.with(context).load(map.get("image"))
                    .thumbnail(0.5f)
                    .bitmapTransform(new CropCircleTransformation(context))
                    .override(DimenUtils.dip2px(context,70),DimenUtils.dip2px(context,70)).into((ImageView) view.findViewById(R.id.person_head));
            ((TextView) view.findViewById(R.id.person_name)).setText(map.get("name"));
            long time = TimeUtils.dataOne(map.get("years"));
            long now = System.currentTimeMillis();
            int i = (int) ((now - time) / 1000 / 60 / 60 / 24 / 365);
            if (i >= 1) {
                ((TextView) view.findViewById(R.id.person_years)).setText("入职: "+String.valueOf(i) + "年");
            } else {
                ((TextView) view.findViewById(R.id.person_years)).setText("入职: 不满1年");
            }
            LogUtil.e("time::!:  " + time + "  ~!~!~!  " + now + "    f!@  " + i);
            view.findViewById(R.id.person_phone).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callPhone1(context, map.get("tel"));

                }
            });
            view.findViewById(R.id.person_sms).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendMessage3(context, map.get("tel"));
                }
            });
        }

    }

    public static HashMap<String, String> getBrokerInfo(String broker_id) {
        try {
            String data = OkGo.post(Constants.getBrokerInfo)
                    .params("key", Constants.safeKey)
                    .params("m_id", Constants.M_id)
                    .params("broker_id", broker_id)
                    .execute().body().string();
            if (!TextUtils.isEmpty(data)) {
                return AnalyticalJSON.getHashMap(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 直接打电话
     *
     * @param tel 电话号码
     */
    private static void callPhone2(Activity context, String tel) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tel));
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.CALL_PHONE}, 0);
            return;
        }
        context.startActivity(intent);
    }

    /**
     * 发送短信(掉起发短信页面)
     *
     * @param tel 电话号码
     */
    private static void sendMessage3(Activity context, String tel) {
        if (PhoneNumberUtils.isGlobalPhoneNumber(tel)) {
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + tel));
            context.startActivity(intent);
        }
    }
}
