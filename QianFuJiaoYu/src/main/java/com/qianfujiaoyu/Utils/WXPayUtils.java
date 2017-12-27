package com.qianfujiaoyu.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.qianfujiaoyu.R;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.apache.http.conn.util.InetAddressUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.qianfujiaoyu.R.id.address;

/**
 * Created by Administrator on 2016/7/12.
 */
public class WXPayUtils {
    private static final String TAG = "WXPayUtils";
    // AppID：wx2a1b932e2309b306
    //https://api.mch.weixin.qq.com/pay/unifiedorder 下单地址
//1356168702


    SharedPreferences sp;
    private static String url = Constants.host_Ip + Constants.pppppp + "notify_url";
    private static String key = "aX4kYR8GU7kMX1A9c9knkmfe3FRc8xoJ";


    public String getXMl(Context context, String total_fee, String attachId, String body) {
        String ip = getLocalHostIp();
        sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        String nonce_str = RandomUtil.generateString(16);
        String out_trade_no = Constants.WXPay_APPID + System.currentTimeMillis();
        Log.w(TAG, "getXMl: 商户号-=-=-=-=-=" + out_trade_no + "随机码+——+——+" + nonce_str + "   ip地址-=-=" + ip);
        StringBuffer data = new StringBuffer();
        data.append("<xml>" + "\n");
        data.append("<appid>" + Constants.WXPay_APPID + "</appid>" + "\n").append("<attach>" + attachId + "</attach>" + "\n");//1
        data.append("<mch_id>" + Constants.WXPay_patnerID + "</mch_id>" + "\n");//3
        data.append("<nonce_str>" + nonce_str + "</nonce_str>" + "\n")//4
                .append("<body>" + body + "</body>" + "\n")//2
                .append("<out_trade_no>" + out_trade_no + "</out_trade_no>" + "\n")//6
                .append("<total_fee>" + total_fee + "</total_fee>" + "\n")//8
                .append("<spbill_create_ip>" + ip + "</spbill_create_ip>\n")//7
                .append("<trade_type>" + "APP" + "</trade_type>" + "\n")//9
                .append("<notify_url>" + url + "</notify_url>" + "\n");//5


        String a = "appid=" + Constants.WXPay_APPID + "&attach=" + attachId + "&body=" + body + "&mch_id=" + Constants.WXPay_patnerID
                + "&nonce_str=" + nonce_str + "&notify_url=" + url + "&out_trade_no=" + out_trade_no + "&spbill_create_ip=" + ip + "&total_fee=" + total_fee
                + "&trade_type=APP";
        String signTamp = a + "&key=" + key;
        String sign = MD5Utls.stringToMD5(signTamp).toUpperCase();
        data.append("<sign>" + sign + "</sign>" + "\n").append("</xml>");

        return data.toString();
    }


    // 得到本机ip地址
    public String getLocalHostIp() {
        String ipaddress = "";
        try {
            Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces();
            // 遍历所用的网络接口
            while (en.hasMoreElements()) {
                NetworkInterface nif = en.nextElement();// 得到每一个网络接口绑定的所有ip
                Enumeration<InetAddress> inet = nif.getInetAddresses();
                // 遍历每一个接口绑定的所有ip
                while (inet.hasMoreElements()) {
                    InetAddress ip = inet.nextElement();
                    if (!ip.isLoopbackAddress()
                            && InetAddressUtils.isIPv4Address(ip
                            .getHostAddress())) {
                        return ipaddress = ip.getHostAddress();
                    }
                }

            }
        } catch (SocketException e) {
            Log.e("feige", "获取本地ip地址失败");
            e.printStackTrace();
        }
        return ipaddress;

    }

    /**
     * @param xml
     * @return Map
     * @description 将xml字符串转换成map
     */
    public static Map<String, String> readStringXmlOut(String xml) {
        Map<String, String> map = new HashMap<String, String>();
        Document doc = null;
        try {
            doc = DocumentHelper.parseText(xml); // 将字符串转为XML
            org.dom4j.Element rootElt = doc.getRootElement(); // 获取根节点
            List<Element> list = rootElt.elements();//获取根节点下所有节点
            for (Element element : list) {  //遍历节点
                map.put(element.getName(), element.getText()); //节点的name为map的key，text为map的value
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public static String getXmlByDom4j() throws IOException {

        Document document = DocumentHelper.createDocument();
//创建root
        Element root = document.addElement("xml");
        //生成root的一个接点
        Element param = root.addElement("appid");
        param.addText("");

        return document.getRootElement().asXML();
    }


    public static void openWXPay(final Activity context, final String allmoney, final String attachId, final String title, final String num, final String type, final String number) {
        if (!Network.HttpTest(context)) {
            Toast.makeText(mApplication.getInstance(), "网络连接不稳定，请稍后重试", Toast.LENGTH_SHORT).show();
            return;
        }
        ProgressUtil.show(context, "", "正在调起微信支付,请稍等");
        new Thread(new Runnable() {
            @Override
            public void run() {
                String stu_id = "";
                String money = "";
                HttpParams httpParams = new HttpParams();
//                if(type.equals("4")){
//                    httpParams.put("mark",extra);
//                }
                JSONObject js = new JSONObject();
                String u = "";
                try {
//                    if(type.equals("7")){
//                        u=Constants.Goods_pay;
//                        js.put("shop_id","1");
//                        js.put("msg",attachId);
//                        js.put("address",num);
//                    }else{
//                        if(type.equals("11")){
//                            u=Constants.getPingTuan_Pay_ip;
//                            js.put("number",(number.equals("0"))?System.currentTimeMillis()/1000+RandomUtil.generateNumber(5):number);
//                            js.put("shop_id",attachId);
//                            js.put("address",num);
//                        }else{
                    u = Constants.getAttachId_ip;
                    js.put("m_id", Constants.M_id);
                    js.put("address", address);
                    js.put("shop_id", attachId);
                    js.put("money", allmoney);
                    js.put("title", title);
                    js.put("user_id", PreferenceUtil.getUserIncetance(context).getString("user_id", ""));
                    js.put("receiveid", Constants.M_id);
                    js.put("num", num);
                    js.put("type", type);
                    js.put("pay_type", "1");

                    ApisSeUtil.M m1 = ApisSeUtil.i(js);
                    LogUtil.e("js:" + js);
                    String attachIdData = OkGo.post(u).tag(TAG)
                            .params("key", m1.K())
                            .params("msg", m1.M())
                            .execute().body().string();
                    if (!attachIdData.equals("")) {
                        HashMap<String, String> m = AnalyticalJSON.getHashMap(attachIdData);
                        if (m != null && ("000").equals(m.get("code"))) {
                            stu_id = m.get("sut_id");
                            money = m.get("money");
                        } else if (m != null && "003".equals(m.get("code"))) {
                            context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ProgressUtil.dismiss();
                                    Toast.makeText(mApplication.getInstance(), "手慢了，该团已被抢走了", Toast.LENGTH_SHORT).show();
                                }
                            });
                            return;
                        } else {
                            context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ProgressUtil.dismiss();
                                    Toast.makeText(mApplication.getInstance(), "获取订单号失败,请稍后重试", Toast.LENGTH_SHORT).show();
                                }
                            });
                            return;
                        }
                    }
                    String data = OkGo.post(Constants.WXPay_post_Url).upString(new WXPayUtils().getXMl(mApplication.getInstance(), money, stu_id, context.getResources().getString(R.string.app_name) + "-" + title)).execute().body().string();

                    if (!data.equals("")) {
                        HashMap<String, String> map = (HashMap<String, String>) WXPayUtils.readStringXmlOut(data);
                        Log.w(TAG, "run:  map-=-=-=-=-=解析得到的map：" + map);
                        if (map.containsKey("result_code")) {
                            IWXAPI api = WXAPIFactory.createWXAPI(mApplication.getInstance(), Constants.WXPay_APPID);
                            api.registerApp(Constants.WXPay_APPID);
                            PayReq req = new PayReq();
                            req.appId = map.get("appid");//1
                            req.partnerId = map.get("mch_id");//4
                            req.prepayId = map.get("prepay_id");//5
                            req.nonceStr = map.get("nonce_str");//2
                            req.timeStamp = System.currentTimeMillis() / 1000 + "";//6
                            req.packageValue = "Sign=WXPay";//3
//
                            String a = "appid=" + map.get("appid") + "&noncestr=" + map.get("nonce_str") + "&package=" + "Sign=WXPay" + "&partnerid=" + map.get("mch_id")
                                    + "&prepayid=" + map.get("prepay_id") + "&timestamp=" + System.currentTimeMillis() / 1000 + "&key=" + key;
                            req.sign = MD5Utls.stringToMD5(a).toUpperCase();
                            Log.w(TAG, "run: sign-=-=-=-=-=" + req.sign);
//
                            ProgressUtil.dismiss();

                            api.sendReq(req);

                        } else {
                            context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ProgressUtil.dismiss();
                                    Toast.makeText(mApplication.getInstance(), "返回错误", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    } else {
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ProgressUtil.dismiss();
                                Toast.makeText(mApplication.getInstance(), "服务器请求错误", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                } catch (Exception e) {
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ProgressUtil.dismiss();
                        }
                    });
                    e.printStackTrace();
                }
            }
        }).start();
    }

    ;
}
