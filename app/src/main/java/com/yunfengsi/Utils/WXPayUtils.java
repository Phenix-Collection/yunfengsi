package com.yunfengsi.Utils;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yunfengsi.Managers.Base.BasePayParams;

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

/**
 * Created by Administrator on 2016/7/12.
 */
public class WXPayUtils {
    private static final String TAG = "WXPayUtils";
    // AppID：wx2a1b932e2309b306
    //https://api.mch.weixin.qq.com/pay/unifiedorder 下单地址
//1356168702


    // 得到本机ip地址
    public String getLocalHostIp() {
        String ipaddress = "";
        try {
            Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces();
            // 遍历所用的网络接口
            while (en.hasMoreElements()) {
                NetworkInterface         nif  = en.nextElement();// 得到每一个网络接口绑定的所有ip
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
        Document            doc = null;
        try {
            doc = DocumentHelper.parseText(xml); // 将字符串转为XML
            org.dom4j.Element rootElt = doc.getRootElement(); // 获取根节点
            List<Element>     list    = rootElt.elements();//获取根节点下所有节点
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


    public static void openWXPay(final Activity context, final BasePayParams payParams) {
        if (!Network.HttpTest(context)) {
            Toast.makeText(mApplication.getInstance(), "网络连接不稳定，请稍后重试", Toast.LENGTH_SHORT).show();
            return;
        }

        IWXAPI msgApi = WXAPIFactory.createWXAPI(context, null);
        msgApi.registerApp(Constants.WXPay_APPID);

        boolean sIsWXAppInstalledAndSupported = msgApi.isWXAppInstalled()
                && msgApi.isWXAppSupportAPI();

        if (!sIsWXAppInstalledAndSupported) {
            ToastUtil.showToastShort("请安装微信");
            return;
        }

        ProgressUtil.show(context, "", "正在调起微信支付,请稍等");
        new Thread(new Runnable() {
            @Override
            public void run() {
                String money = "";

                JSONObject js = new JSONObject();
                try {
                    if (payParams.payType.equals("4")) {
                        //供养商品  祈愿信息
                        js.put("mark", payParams.wishInformation);
                    }
                    if(payParams.payType.equals("14")){
                        //快速通道额外信息
                        js.put("mark",payParams.jsonInfo);
                    }
                    if (payParams.payType.equals("13")) {
                        //义卖支付  出价列表id 和   义卖id
                        js.put("auct_user_id", payParams.payId.substring(payParams.payId.indexOf(",") + 1));
                        js.put("shop_id", payParams.payId.substring(0, payParams.payId.indexOf(",")));
                        js.put("address",payParams.addressId);
                    } else {
                        js.put("shop_id", payParams.payId);
                    }
                    js.put("type", payParams.payType);
                    js.put("money", payParams.allMoney);
                    js.put("title", payParams.title);
                    js.put("user_id", PreferenceUtil.getUserIncetance(context).getString("user_id", ""));
                    js.put("receiveid", Constants.M_id);
                    js.put("num", payParams.num);
                    // 微信1 支付宝2 QQ钱包3 银联4
                    js.put("pay_type", "1");



                    ApisSeUtil.M m1 = ApisSeUtil.i(js);
                    LogUtil.e("微信支付：：；"+js);
                    String attachIdData = OkGo.post(Constants.getAttachId_ip).tag(TAG)
                            .params("key", m1.K())
                            .params("msg", m1.M())
                            .execute().body().string();
                    if (!attachIdData.equals("")) {
                        HashMap<String, String> m = AnalyticalJSON.getHashMap(attachIdData);
                        if (m != null && ("000").equals(m.get("code"))) {
                            mApplication.sut_id = m.get("sut_id");
                            mApplication.type = payParams.payType;
                            mApplication.id = payParams.payId;
                            mApplication.title = payParams.title;
                            money = m.get("money");
                            HashMap<String, String> map = AnalyticalJSON.getHashMap(m.get("x"));
                            Log.w(TAG, "run:  map-=-=-=-=-=解析得到的map：" + map);
                            if (map != null) {
                                IWXAPI api = WXAPIFactory.createWXAPI(mApplication.getInstance(), map.get("appid"));
                                api.registerApp(map.get("appid"));
                                PayReq req = new PayReq();
                                req.appId = map.get("appid");//1
                                req.partnerId = map.get("partnerid");//4
                                req.prepayId = map.get("prepayid");//5
                                req.nonceStr = map.get("noncestr");//2
                                req.timeStamp = map.get("timestamp");//6
                                req.packageValue = "Sign=WXPay";//3
//
//                                String a = "appid=" + map.get("appid") + "&noncestr=" + map.get("noncestr") + "&package=" + "Sign=WXPay" + "&partnerid=" + map.get("partnerid")
//                                        + "&prepayid=" + map.get("prepayid") + "&timestamp=" +map.get("timestamp")+ "&key=" + key;
                                req.sign = map.get("sign");
                                Log.w(TAG, "run: sign-=-=-=-=-=" + req.sign);
//
                                ProgressUtil.dismiss();

                                api.sendReq(req);
                            }
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
//                    String data = OkGo.post(Constants.WXPay_post_Url).upString(new WXPayUtils().getXMl(mApplication.getInstance(), money, mApplication.sut_id, title)).execute().body().string();

//                    if (!data.equals("")) {


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


}
