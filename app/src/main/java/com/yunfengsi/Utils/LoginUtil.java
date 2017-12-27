package com.yunfengsi.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.yunfengsi.Login;


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
            Toast.makeText(context, "请登录", Toast.LENGTH_SHORT).show();
            return false;
        }else{
            return true;
        }
    }

}
