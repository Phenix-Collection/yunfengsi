package com.yunfengsi.Push;

import android.content.Context;
import android.content.Intent;

import com.alibaba.sdk.android.push.MessageReceiver;
import com.alibaba.sdk.android.push.notification.CPushMessage;
import com.yunfengsi.BlessTree.BlessTree;
import com.yunfengsi.E_Book.BookList;
import com.yunfengsi.MainActivity;
import com.yunfengsi.Managers.MessageCenter;
import com.yunfengsi.Model_activity.Mine_activity_list;
import com.yunfengsi.Model_activity.activity_Detail;
import com.yunfengsi.Model_zhongchou.FundingDetailActivity;
import com.yunfengsi.More.Fortune;
import com.yunfengsi.More.Meditation;
import com.yunfengsi.NianFo.NianFo;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.mApplication;
import com.yunfengsi.XuanzheActivity;
import com.yunfengsi.ZiXun_Detail;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/16.
 */
public class mReceiver
        extends MessageReceiver
{
   public static final String ZIXUN = "1";
   public static final String GOngyang = "4";
   public static final String ZHONGCHou = "5";
   public static final String HUODong = "6";
   public static final String GONGXIU = "14";
   public static final String BaoMing = "10";
   public static final String TongZhi = "17";
   public static final String Pinglun = "18";
   public static final String QiYuan = "19";
   public static final String Fojin = "20";
   public static final String Bushi = "21";
   public static final String ZuoChan = "22";
   public static final String AD = "99";

    @Override
    protected void onMessage(Context context, CPushMessage cPushMessage) {// TODO: 2016/12/16 透传
        super.onMessage(context, cPushMessage);
        LogUtil.e("onNotificationOpened:消息收到" + cPushMessage.getContent());
    }

    @Override
    protected void onNotification(Context context, String s, String s1, Map<String, String> map) {// TODO: 2016/12/16 通知接收回调

        super.onNotification(context, s, s1, map);
        LogUtil.e("onNotificationOpened: 通知收到" + s + "    " + s1 + "  数据" + map);

        String type=AnalyticalJSON.getHashMap(map.get("msg")).get("type");
        LogUtil.e("type:::"+type);
        if(Pinglun.equals(type)||TongZhi.equals(type)){
            EventBus.getDefault().post(new MainActivity.NoticeEvent());
        }
    }


    @Override
    protected void onNotificationOpened(Context context, String s, String s1, String s2) {// TODO: 2016/12/16 通知打开回调
        super.onNotificationOpened(context, s, s1, s2);
        LogUtil.e("onNotificationOpened: 通知打开" + s + "    " + s1 + "  " + s2);
        String msg = AnalyticalJSON.getHashMap(s2).get("msg");
        Intent intent = new Intent(context, MainActivity.class);
        HashMap<String, String> map = AnalyticalJSON.getHashMap(msg);


        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        intent.putExtra("id", map.get("id"));
        if (ZIXUN.equals(map.get("type"))) {
            intent.setClass(mApplication.getInstance(), ZiXun_Detail.class);
        } else if (GOngyang.equals(map.get("type"))) {
            intent.setClass(mApplication.getInstance(), XuanzheActivity.class);
        } else if (ZHONGCHou.equals(map.get("type"))) {
            intent.setClass(mApplication.getInstance(), FundingDetailActivity.class);
        } else if (HUODong.equals(map.get("type"))) {
            intent.setClass(mApplication.getInstance(), activity_Detail.class);
        } else if (GONGXIU.equals(map.get("type"))) {
            intent.setClass(mApplication.getInstance(), NianFo.class);
        } else if (BaoMing.equals(map.get("type"))) {
            intent.setClass(mApplication.getInstance(), Mine_activity_list.class);
        } else if (TongZhi.equals(map.get("type"))) {
            intent.setClass(mApplication.getInstance(), MessageCenter.class);
        }else if (Pinglun.equals(map.get("type"))) {
            intent.setClass(mApplication.getInstance(), MessageCenter.class);
        }else if (QiYuan.equals(map.get("type"))) {
            intent.setClass(mApplication.getInstance(), BlessTree.class);
        }else if (Fojin.equals(map.get("type"))) {
            intent.setClass(mApplication.getInstance(), BookList.class);
        }else if (Bushi.equals(map.get("type"))) {
            intent.setClass(mApplication.getInstance(), Fortune.class);
        }else if (ZuoChan.equals(map.get("type"))) {
            intent.setClass(mApplication.getInstance(), Meditation.class);
        }
        context.startActivity(intent);


//        //判断app进程是否存活
//        if(SystemUtils.isAppAlive(context, "com.liangzili.notificationlaunch")){
//            //如果存活的话，就直接启动DetailActivity，但要考虑一种情况，就是app的进程虽然仍然在
//            //但Task栈已经空了，比如用户点击Back键退出应用，但进程还没有被系统回收，如果直接启动
//            //DetailActivity,再按Back键就不会返回MainActivity了。所以在启动
//            //DetailActivity前，要先启动MainActivity。
//            Log.i("NotificationReceiver", "the app process is alive");
//            Intent mainIntent = new Intent(context, MainActivity.class);
//            //将MainAtivity的launchMode设置成SingleTask, 或者在下面flag中加上Intent.FLAG_CLEAR_TOP,
//            //如果Task栈中有MainActivity的实例，就会把它移到栈顶，把在它之上的Activity都清理出栈，
//            //如果Task栈不存在MainActivity实例，则在栈顶创建
//            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//            Intent detailIntent = new Intent(context, DetailActivity.class);
//            detailIntent.putExtra("name", "电饭锅");
//            detailIntent.putExtra("price", "58元");
//            detailIntent.putExtra("detail", "这是一个好锅, 这是app进程存在，直接启动Activity的");
//
//            Intent[] intents = {mainIntent, detailIntent};
//
//            context.startActivities(intents);
//        }else {
//            //如果app进程已经被杀死，先重新启动app，将DetailActivity的启动参数传入Intent中，参数经过
//            //SplashActivity传入MainActivity，此时app的初始化已经完成，在MainActivity中就可以根据传入             //参数跳转到DetailActivity中去了
//            Log.i("NotificationReceiver", "the app process is dead");
//            Intent launchIntent = context.getPackageManager().
//                    getLaunchIntentForPackage("com.liangzili.notificationlaunch");
//            launchIntent.setFlags(
//                    Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//            Bundle args = new Bundle();
//            args.putString("name", "电饭锅");
//            args.putString("price", "58元");
//            args.putString("detail", "这是一个好锅, 这是app进程不存在，先启动应用再启动Activity的");
//            launchIntent.putExtra(Constants.EXTRA_BUNDLE, args);
//            context.startActivity(launchIntent);
//        }
//    }
    }
}
