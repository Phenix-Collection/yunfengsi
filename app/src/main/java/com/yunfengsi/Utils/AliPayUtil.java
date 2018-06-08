package com.yunfengsi.Utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.yunfengsi.Models.Model_zhongchou.Fund_Share;
import com.yunfengsi.Models.YunDou.YunDouAwardDialog;
import com.yunfengsi.WebShare.ZhiFuShare;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

import static com.yunfengsi.Utils.UpPayUtil.extra;

/**
 * Created by Administrator on 2017/1/4.
 */
public class AliPayUtil {
    /**
     * 支付宝支付业务：入参app_id
     */
    public static final String APPID = "2016112303153908";

    /**
     * 支付宝账户登录授权业务：入参pid值
     */
    public static final String PID     = "2088421250002880";
    // 商户PID
    public static final String PARTNER = "2088421250002880";
    // 商户收款账号
    public static final String SELLER  = "2088421250002880";

    public static final String RSA_PRIVATE = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAITtvqWq1IejHA4LFd9yAHKFiNt+MZ+Zo7BV2l44ZLNBt4mLhEUrnPklL3rcOIqM57ViUVgNGC5kOHvexJS94pqkBWcbPhDsNHztiZirSKXs0ipW41oTosMuoxpQYrdaFbt5FNmBszRZDc0QRNnBXL6ypWdyiPcscf4piWa1DnWpAgMBAAECgYA/h0P69wa1gC2TRJcCgABYuxrqE4hxx0KkrpM7PmZaCUlHEgd3610M8UmcxQy8opTGaiOIGlH5MeqQwKlgkNNv9hqwgu8RRy00CBZjgLj3xaYhhTRI0b9Nj6EESrWLfEwPTTznilYN0lHV52FImKRllFD4+vee7ew5OHuQ1aIcAQJBAL1j3093ciiawXNgDbVAwzGA/6jQ8zjFECIivGQHITnQ/Gih0LpP/9Es097ReeGVYGwDTD1Veyma8J6ASgMTaYECQQCzrkBfGOJOquDkjWi87WhrYbdujsjrZEW86XpUA7hwfH4O/BBL4+tJHiqCh5I9SoJW5NLVJF1KeIJ5f0XUmpApAkBVSYmB1s+A+5gMZgAmVKDSRT5cfqRZN105khz2isNqrvNMBzrg/C++ugo7eGgDr2o5mg6WPE13gf/D0RADbJWBAkAHVuXQPJ754acADvqpRPVP9ZTdkj2Ix/bFSbAygFhnV956VDeCMhQpT28jF9CUale6nuwxwqOA6D1EIzvB/HJJAkBD7Yu8Y/7j+hs+rZQROfRtPYWQtgPtGOcbdOLYF/+RsKk9k7gNnLQ31DmxOfwIw3o93lydZzS8ae+CgdAatcl2";
    //废弃私钥

    private static final int SDK_PAY_FLAG = 1;


    /**
     * 支付宝支付业务
     */
    public static void openAliPay(final Activity context, final String allmoney, final String attachId, final String title, final String num, final String address, final String type) {
        /**
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * orderInfo的获取必须来自服务端；
         */
        if (!Network.HttpTest(context)) {
            Toast.makeText(mApplication.getInstance(), "网络连接不稳定，请稍后重试", Toast.LENGTH_SHORT).show();
            return;
        }


        Uri           uri           = Uri.parse("alipays://platformapi/startApp");
        Intent        intent        = new Intent(Intent.ACTION_VIEW, uri);
        ComponentName componentName = intent.resolveActivity(context.getPackageManager());
        if (componentName == null) {
            ToastUtil.showToastShort("请安装支付宝软件");
            return;
        }


        ProgressUtil.show(context, "", "正在调起支付宝,请稍等");

        new Thread(new Runnable() {
            @Override
            public void run() {
//                String stu_id = "";
                String money = "";
                String url;
                url = Constants.getAttachId_ip;
                JSONObject js = new JSONObject();

                try {
                    if (type.equals("4")) {
                        js.put("mark", extra);
                    }
                    js.put("shop_id", attachId);
                    js.put("money", allmoney);
                    js.put("title", title);
                    js.put("user_id", PreferenceUtil.getUserIncetance(context).getString("user_id", ""));
                    js.put("receiveid", Constants.M_id);
                    js.put("num", num);
                    js.put("type", type);
                    js.put("pay_type", "2");
                    ApisSeUtil.M m1 = ApisSeUtil.i(js);
                    String attachIdData = OkGo.post(url)
                            .params("key", m1.K())
                            .params("msg", m1.M())
                            .execute().body().string();
                    if (!attachIdData.equals("")) {
                        final HashMap<String, String> m = AnalyticalJSON.getHashMap(attachIdData);
                        if (m != null && ("000").equals(m.get("code"))) {
                            mApplication.sut_id = m.get("sut_id");
                            mApplication.id = attachId;
                            mApplication.title = title;

                            HashMap<String, String> map = AnalyticalJSON.getHashMap(attachIdData);
                            if (map != null) {
                                final String payInfo = map.get("x");
                                LogUtil.e("最后的请求参数：" + payInfo);
                                Runnable payRunnable = new Runnable() {

                                    @Override
                                    public void run() {
                                        // 构造PayTask 对象
                                        PayTask alipay = new PayTask(context);
                                        // 调用支付接口，获取支付结果
                                        final String result = alipay.pay(payInfo, true);

                                        context.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                PayResult payResult = new PayResult(result);
                                                /**
                                                 * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                                                 * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                                                 * docType=1) 建议商户依赖异步通知
                                                 */
//                                      String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                                                LogUtil.e(payResult.toString());
                                                String resultStatus = payResult.getResultStatus();
                                                // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                                                if (TextUtils.equals(resultStatus, "9000")) {
                                                    Toast.makeText(context, "支付成功", Toast.LENGTH_SHORT).show();
                                                    if (type.equals("4")) {//供养支付
                                                        Intent intent1 = new Intent(context, ZhiFuShare.class);
                                                        intent1.putExtra("stu_id", mApplication.sut_id);
                                                        context.startActivity(intent1);
                                                    } else if (type.equals("5")) {//慈善
                                                        Intent intent = new Intent("Mine");
                                                        intent.putExtra("level", true);
                                                        context.sendBroadcast(intent);
                                                        // TODO: 2017/5/11 进入分享页面
                                                        ///
                                                        intent.setClass(context, Fund_Share.class);
                                                        intent.putExtra("sut_id", mApplication.sut_id);
                                                        intent.putExtra("id", mApplication.id);
                                                        intent.putExtra("title", mApplication.title);
                                                        context.startActivity(intent);
                                                    }
                                                } else {
                                                    // 判断resultStatus 为非"9000"则代表可能支付失败
                                                    // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                                                    if (TextUtils.equals(resultStatus, "8000")) {
                                                        Toast.makeText(context, "支付结果确认中", Toast.LENGTH_SHORT).show();

                                                    } else {
                                                        // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
//                                              Toast.makeText(context, "支付失败", Toast.LENGTH_SHORT).show();

                                                    }
                                                }
                                            }
                                        });
                                    }
                                };

                                // 必须异步调用
                                Thread payThread = new Thread(payRunnable);
                                payThread.start();
                                ProgressUtil.dismiss();
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

//                        String orderInfo = getOrderInfo(title, mApplication.sut_id, allmoney);
//                        LogUtil.e("参数————————》" + orderInfo);
//                        /**
//                         * 特别注意，这里的签名逻辑需要放在服务端，切勿将私钥泄露在代码中！
//                         */
//                        String sign = sign(orderInfo);
//                        try {
//                            /**
//                             * 仅需对sign 做URL编码
//                             */
//                            sign = URLEncoder.encode(sign, "UTF-8");
//                        } catch (UnsupportedEncodingException e) {
//                            e.printStackTrace();
//                        }
//
//                        /**
//                         * 完整的符合支付宝参数规范的订单信息
//                         */
//                        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();


                    }
                } catch (Exception e) {

                }
            }
        }).start();

    }

    public static void postYundouGY(final Activity context) {
        JSONObject js = new JSONObject();
        try {
            js.put("m_id", Constants.M_id);
            js.put("user_id", PreferenceUtil.getUserId(mApplication.getInstance()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e("每日供养   回调确认：：" + js);
        ApisSeUtil.M m = ApisSeUtil.i(js);
        OkGo.post(Constants.GY_YUNDOU).params("key", m.K())
                .params("msg", m.M())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        try {
                            JSONObject js = new JSONObject(s);
                            if (js != null) {
                                if (js.getString("yundousum") != null && !js.getString("yundousum").equals("0")) {
                                    YunDouAwardDialog.show(context, "每日供养", js.getString("yundousum"));
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });
    }

    public static void postYundouZX(final Activity context) {
        JSONObject js = new JSONObject();
        try {
            js.put("m_id", Constants.M_id);
            js.put("user_id", PreferenceUtil.getUserId(mApplication.getInstance()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e("每日助学   回调确认：：" + js);
        ApisSeUtil.M m = ApisSeUtil.i(js);
        OkGo.post(Constants.ZX_YUNDOU).params("key", m.K())
                .params("msg", m.M())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        try {
                            JSONObject js = new JSONObject(s);
                            if (js != null) {
                                if (js.getString("yundousum") != null && !js.getString("yundousum").equals("0")) {
                                    YunDouAwardDialog.show(context, "每日助学", js.getString("yundousum"));
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });
    }

    /**
     * create the order info. 创建订单信息
     */
    private static String getOrderInfo(String subject, String body, String price) {

        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + PARTNER + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + System.currentTimeMillis() + "" + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\"";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + "http://indrah.cn/api.php/Api/Aliypay_url" + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";

        return orderInfo;
    }

    /**
     * sign the order info. 对订单信息进行签名
     *
     * @param content 待签名订单信息
     */
    private static String sign(String content) {
        return SignUtils.sign(content, RSA_PRIVATE);
    }

    /**
     * get the sign type we use. 获取签名方式
     */
    private static String getSignType() {
        return "sign_type=\"RSA\"";
    }
}
