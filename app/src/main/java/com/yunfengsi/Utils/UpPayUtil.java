package com.yunfengsi.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.unionpay.UPPayAssistEx;
import com.yunfengsi.Models.Model_zhongchou.Fund_Share;
import com.yunfengsi.WebShare.ZhiFuShare;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/11/21.
 */
public class UpPayUtil extends AppCompatActivity {
    private static final String TAG = "UpPayUtil";
    private static String mMode = "00";//设置测试模式:01为测试 00为正式环境
    private static final String TN_URL_01 = "http://indranet.cn/"+Constants.NAME_LOW+".php/Unionpay/unionpay_app" ;//自己后台需要实现的给予我们app的tn号接口
    private WeakReference<Activity> weakReference;
    private static  String stu_id = "";
    public static String  allmoney,title,num,shop_id,type,extra;
    private   Handler mHandler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            weakReference=new WeakReference<Activity>(UpPayUtil.this);
            String tn = "";
            if (msg.obj == null || ((String) msg.obj).length() == 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(weakReference.get());
                builder.setTitle("错误提示");
                builder.setMessage("网络连接失败,请重试!");
                builder.setNegativeButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.create().show();
            } else {
                tn = (String) msg.obj;
                doStartUnionPayPlugin(weakReference.get(), tn, mMode);
            }
        }
    };

    /**
     * 启动支付界面
     */
    public static void doStartUnionPayPlugin(Activity activity, String tn, String mode) {
//        UPPayAssistEx.startPayByJAR(activity, PayActivity.class, null, null,
//                tn, mode);
        UPPayAssistEx.startPay(activity,null,null,tn,mode);
    }




    public  Runnable payRunnable = new Runnable() {
        @Override
        public void run() {
            String tn = null;

            String money = "";
            String attachIdData = null;
            UpPayUtil.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ProgressUtil.show(UpPayUtil.this,"","正在调起银联支付，请稍等");
                }
            });

            JSONObject js=new JSONObject();

            try {
                if(type.equals("4")){
                    js.put("mark",extra);
                }
                js.put("type",type);
                js.put("money", allmoney);
                js.put("title", title);
                js.put("user_id", PreferenceUtil.getUserIncetance(mApplication.getInstance()).getString("user_id", ""));
                js.put("receiveid", Constants.M_id);
                js.put("num",num);
                js.put("shop_id", shop_id);
                js.put("pay_type","4");
                attachIdData = OkGo.post(Constants.getAttachId_ip).tag(TAG)

                        .params("key",ApisSeUtil.getKey())
                        .params("msg",ApisSeUtil.getMsg(js))
                        .execute().body().string();
                if (!attachIdData.equals("")) {
                    HashMap<String, String> m = AnalyticalJSON.getHashMap(attachIdData);
                    if (m != null && ("000").equals(m.get("code"))) {
                        mApplication.sut_id = m.get("sut_id");
                        mApplication.id=shop_id;
                        money = m.get("money");
                        mApplication.title=title;
                    } else {
                        UpPayUtil.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ProgressUtil.dismiss();
                                Toast.makeText(mApplication.getInstance(), "获取订单号失败,请稍后重试", Toast.LENGTH_SHORT).show();
                            }
                        });
                        return;
                    }
                }
            } catch (Exception e) {
                UpPayUtil.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ProgressUtil.dismiss();
                        Toast.makeText(mApplication.getInstance(), "获取订单号失败,请稍后重试", Toast.LENGTH_SHORT).show();
                    }
                });
                e.printStackTrace();
            }
            OkGo.post(TN_URL_01)
                    .params("key",Constants.safeKey)
                    .params("id",mApplication.sut_id)
                    .params("money",money)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            ProgressUtil.dismiss();
                            Message msg = mHandler.obtainMessage();
                            msg.obj = s;
                            mHandler.sendMessage(msg);
                        }


            });

//            Message msg = mHandler.obtainMessage();
//            msg.obj = tn;
//            Log.w(TAG, "run: 返回码："+tn );
//            mHandler.sendMessage(msg);
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        String msg = "";
        /*
         * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
         */
        if(data!=null&&data.getExtras()!=null){
            String str = data.getExtras().getString("pay_result");
            if (str!=null&&!"".equals(str)) {
                if (str.equalsIgnoreCase("success")) {
                    msg = "支付成功！";
                    if("4".equals(type)){
                        Intent intent1 = new Intent(this, ZhiFuShare.class);
                        intent1.putExtra("stu_id",stu_id);
                        startActivity(intent1);
                    }else if(type.equals("5")){//慈善
                        Intent intent=new Intent("Mine");
                        intent.putExtra("level",true);
                        sendBroadcast(intent);
                        // TODO: 2017/5/11 进入分享页面
                        ///
                        intent.setClass(this, Fund_Share.class);
                        intent.putExtra("sut_id",mApplication.sut_id);
                        intent.putExtra("id",mApplication.id);
                        intent.putExtra("title",mApplication.title);
                        startActivity(intent);
                    }
                } else if (str.equalsIgnoreCase("fail")) {
                    msg = "支付失败！";
                } else if (str.equalsIgnoreCase("cancel")) {
                    msg = "您取消了支付";
                }
                Toast.makeText(UpPayUtil.this, msg, Toast.LENGTH_SHORT).show();
                //支付完成,处理自己的业务逻辑!
            }
        }

    }
}
