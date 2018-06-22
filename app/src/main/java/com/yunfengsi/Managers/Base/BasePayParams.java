package com.yunfengsi.Managers.Base;

public class BasePayParams {

    //支付的总金额
    public  String allMoney;
    //支付商品的id  多个用，链接
    public  String payId;
    //支付标题   多个用，链接
    public String title;
    //购买数量  默认1
    public String num="1";
    // 支付type  1视频    2直播 3 寺庙 4供养 5助学 6 书城 7点餐  8预约 9业务购买 11 拼团12 约参与费用 13义卖支付  14 活动快速通道
    public   String payType;
    // type=4 时使用  供养时填写的祈愿信息
    public    String wishInformation;
    // 收货地址Id
    public  String  addressId;

    //快速通道额外字段  json格式   如果活动需要时间限制 则传入两个time，不需要则只传act_type字段，json包裹

    //{"act_type":快速通道Id，"start_time":活动参与的时间，"end_time":活动参与结束的时间}
    //支付时使用mark字段作为key 传入jsonInfo
    public String jsonInfo;
}

