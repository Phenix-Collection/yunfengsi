package com.yunfengsi.Managers;

import android.app.Activity;
import android.view.View;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.ToastUtil;
import com.yunfengsi.Utils.mApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 作者：因陀罗网 on 2018/5/21 14:41
 * 公司：成都因陀罗网络科技有限公司
 *
 * 收藏管理
 */
public class CollectManager {

    public interface OnDeleteListener{
     void   onDelete();

    }
    public static abstract class OnDataArrivedListener{
        void   onSuccess(){

        }

        void   onError(){

        }

        void onAfter(){

        }
        public abstract  void  onUp();
        public abstract  void  onDown();

    }
    public CollectManager getInstance(){
        return new CollectManager();
    }
    /**
     *
     * @param id   收藏的id
     * @param type  收藏的类型   1图文  2 活动  3供养  4助学 5义卖
     * @param selectView 需要设置selected的view
     *
     * @return  code  000 收藏成功   001  收藏失败
     */
    public static void doCollect(final Activity activity, String id, String type, final View selectView){
        JSONObject js=new JSONObject();
        try {
            js.put("m_id", Constants.M_id);
            js.put("user_id", PreferenceUtil.getUserId(mApplication.getInstance()));
            js.put("type", type);
            js.put("collect", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m=ApisSeUtil.i(js);
        LogUtil.e("收藏接口：："+js);

        OkGo.post(Constants.Collect).tag(activity).params("key",m.K())
                .params("msg",m.M())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        HashMap<String ,String > map= AnalyticalJSON.getHashMap(s);
                        if(map!=null){
                            if("000".equals(map.get("code"))){//收藏成功
                                ToastUtil.showToastShort("收藏成功");
                                selectView.setSelected(true);

                            }else if("001".equals(map.get("code"))){//取消成功
                                ToastUtil.showToastShort("已取消收藏");
                                selectView.setSelected(false);
                            }
                        }

                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
//                        ProgressUtil.show(activity,"","请稍等");
                    }

                    @Override
                    public void onAfter(String s, Exception e) {
                        super.onAfter(s, e);
//                        ProgressUtil.dismiss();
                    }
                });
    }

    /**
     *
     * @param id   收藏的id
     * @param type  收藏的类型   1图文  2 活动  3供养  4助学 5义卖
     * @param selectView 需要设置selected的view
     *@param listener  监听
     * @return  code  000 收藏成功   001  收藏失败
     */
    public  static  void doCollect(final Activity activity, String id, String type, final View selectView, final OnDataArrivedListener listener){
        JSONObject js=new JSONObject();
        try {
            js.put("m_id", Constants.M_id);
            js.put("user_id", PreferenceUtil.getUserId(mApplication.getInstance()));
            js.put("type", type);
            js.put("collect", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m=ApisSeUtil.i(js);
        LogUtil.e("收藏接口：："+js);

        OkGo.post(Constants.Collect).tag(activity).params("key",m.K())
                .params("msg",m.M())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        HashMap<String ,String > map= AnalyticalJSON.getHashMap(s);
                        if(map!=null){

                            if("000".equals(map.get("code"))){//收藏成功
                                ToastUtil.showToastShort("收藏成功");
                                selectView.setSelected(true);
                                listener.onUp();
                            }else if("001".equals(map.get("code"))){//取消成功
                                ToastUtil.showToastShort("已取消收藏");
                                selectView.setSelected(false);
                                listener.onDown();
                            }
                        }

                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
//                        ProgressUtil.show(activity,"","请稍等");
                    }

                    @Override
                    public void onAfter(String s, Exception e) {
                        super.onAfter(s, e);
//                        ProgressUtil.dismiss();
                    }
                });
    }


    /**
     *
     * @param id   收藏的id
     * @param type  收藏的类型   1图文  2 活动  3供养  4助学 5义卖
     * @param listener 收藏列表删除收藏监听
     *
     * @return  code  000 收藏成功   001  收藏失败
     */
    public static void doCollectOnDelete(final Activity activity, String id, String type, final OnDeleteListener listener){


        JSONObject js=new JSONObject();
        try {
            js.put("m_id", Constants.M_id);
            js.put("user_id", PreferenceUtil.getUserId(mApplication.getInstance()));
            js.put("type", type);
            js.put("collect", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m=ApisSeUtil.i(js);
        LogUtil.e("收藏接口：："+js);
        OkGo.post(Constants.Collect).tag(activity).params("key",m.K())
                .params("msg",m.M())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        HashMap<String ,String > map= AnalyticalJSON.getHashMap(s);
                        if(map!=null){
                           if("001".equals(map.get("code"))) {//取消成功
                               ToastUtil.showToastShort("已取消收藏");
                               listener.onDelete();
                           }
                        }
                    }
                });
    }
}
