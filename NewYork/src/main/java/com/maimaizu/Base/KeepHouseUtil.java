package com.maimaizu.Base;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v4.app.ActivityCompat;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.maimaizu.R;
import com.maimaizu.Utils.AnalyticalJSON;
import com.maimaizu.Utils.Constants;
import com.maimaizu.Utils.DimenUtils;
import com.maimaizu.Utils.LoginUtil;
import com.maimaizu.Utils.PreferenceUtil;
import com.maimaizu.Utils.ToastUtil;
import com.maimaizu.Utils.mApplication;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/5/2.
 */

public class KeepHouseUtil {

    public static  void houseKeep(Activity activity, final String houseId, final TextView targetView){
        WeakReference<Activity> weakReference = new WeakReference<Activity>(activity);
        final Activity context = weakReference.get();
        if(new LoginUtil().checkLogin(context)){
            OkGo.post(Constants.keepHouseNo).params("key",Constants.safeKey)
                    .params("m_id",Constants.M_id)
                    .params("house_id",houseId)
                    .params("user_id", PreferenceUtil.getUserIncetance(context).getString("user_id",""))//preferenceUtil.getUserInstance.getString ("user_id",Mode.private)
                    .execute(new AbsCallback<HashMap<String ,String  >>() {
                        @Override
                        public void onSuccess(HashMap<String, String> map, Call call, Response response) {
                            String code=map.get("code");
                            switch (code){
                                case "000":
                                    ToastUtil.showToastShort("已关注该房源");
                                    targetView.setText("关注");
                                    Drawable g= ActivityCompat.getDrawable(context, R.drawable.guanzhu_pressed);
                                    g.setBounds(0,0, DimenUtils.dip2px(context,25),DimenUtils.dip2px(context,25));
                                    targetView.setCompoundDrawables(null,g,null,null);
                                    targetView.setTextColor(ActivityCompat.getColor(context,R.color.main_color));
                                    if(!mApplication.FangWu.contains(houseId)){
                                        mApplication.FangWu.add(houseId);
                                    }
                                    break;
                                case "002":
                                    ToastUtil.showToastShort("已取消关注该房源");
                                    targetView.setText("关注");
                                    Drawable gz= ActivityCompat.getDrawable(context, R.drawable.guanzhu_normal);
                                    gz.setBounds(0,0, DimenUtils.dip2px(context,25),DimenUtils.dip2px(context,25));
                                    targetView.setCompoundDrawables(null,gz,null,null);
                                    targetView.setTextColor(ActivityCompat.getColor(context,R.color.black));
                                    if(mApplication.FangWu.contains(houseId)){
                                        mApplication.FangWu.remove(houseId);
                                    }
                                    break;
                            }
                        }

                        @Override
                        public HashMap<String, String> convertSuccess(Response response) throws Exception {
                            return AnalyticalJSON.getHashMap(response.body().string());
                        }
                    });
        }


    }

}
