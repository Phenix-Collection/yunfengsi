package com.maimaizu.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.maimaizu.Mine.Login;


/**
 * Created by Administrator on 2016/7/14.
 */
public class LoginUtil {
    SharedPreferences sp;
    public boolean checkLogin(Context context) {
        if(sp==null){
            sp=context.getSharedPreferences("user",Context.MODE_PRIVATE);
        }
        if (sp.getString("user_id", "").equals("")||sp.getString("uid", "").equals("")) {
            Intent intent = new Intent(context, Login.class);
            context.startActivity(intent);
            ToastUtil.showToastShort("请登录");
            return false;
        }else{
            return true;
        }
    }

}
