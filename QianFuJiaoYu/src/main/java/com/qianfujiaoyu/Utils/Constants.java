package com.qianfujiaoyu.Utils;

import android.os.Environment;

/**
 * Created by Administrator on 2016/5/30.
 */
public class Constants {

    //服务器测试域名
//    public static final String host_Ip = "http://yintolo.net";
    //    //服务器域名
    public static  final String host_Ip="https://indrah.cn";
    public static final String oooooo = "/api.php/EncryptApi/";
    public static final String iiiiii = "/api.php/SecretApi/";
    public static final String pppppp = "/api.php/Api/";
    public static final String M_id = "19";
    public  static final String NAME_LOW="qfjy";
    public static  final int  NAME_CHAR_NUM=12;

    public static final String Temples = host_Ip + oooooo + "Templexz";

    public static  final String cacheO=NAME_LOW;
    //外部缓存储存地址
    public static  final String ExternalCacheDir =Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+cacheO+"/";
    //启动页加载
//    public  static  final String getAD=host_Ip+oooooo+"Qdongye";
    public  static  final String getAD=host_Ip+oooooo+"Ggaoye";
    //定制更新
    public static final String Update_Ip = host_Ip + oooooo + "Dzupdate";
    //安全验证码
//    public static final String safeKey = "TGubFeLjtK8vYZFA5zYfUAsJEkJekTyG";
    //商户号
    public static final String WXPay_patnerID = "1485194072";
    //微信支付APPid
    public static final String WXPay_APPID = "wx4cad8315141b7b59";

    //举报反馈  key,user_id,title,contents
    public static final String Suggest_Ip = host_Ip + oooooo + "Suggest";
    //关于我们  key,m_id
    public static final String AboutUs_Ip = host_Ip + oooooo + "Ours";
    //微信支付下单地址
    public static final String WXPay_post_Url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    //分享域名
    public static final String FX_host_Ip = host_Ip + "/"+NAME_LOW+".php/Index/";
    //分享测试域名
    public static final String FX_CS_host_Ip = "http://yintolo.zhideng.net/wap.php/Index/";
    //在服务器生成获取订单号
    public static final String getAttachId_ip = host_Ip + oooooo + "Wxpay";
    //版本更新检测
    public static final String Check_Update_IP = host_Ip + oooooo + "Updatejc";
    //图片储存地址
    public static final String IMGDIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/fojiao/img/";
    //头像储存地址
    public static final String HEADDIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/fojiao/head_IMG/";
    //    手机验证码地址:
//    public static final String Mid_IP = host_Ip + oooooo + "Yzm";
    //新闻详情
    public static final String ZiXun_detail_Ip = host_Ip + oooooo + "newsd";
    //寺庙详情
    public static final String Simiao_detail_Ip = host_Ip + oooooo + "Templed";
    //新闻图文评论接口
    public static final String ZiXun_detail_PL_Ip = host_Ip + oooooo + "Newspl";
    //所有新闻
    public static final String ZiXun_total_Ip = host_Ip + oooooo + "news";
    //本地注册接口
//    public static final String Regist_Ip = host_Ip + oooooo + "Dzregister";
//    //本地登陆接口
//    public static final String Login_Ip = host_Ip + oooooo + "Dzlogin";
    //授权登陆接口
    public static final String Login_3_Ip = host_Ip + oooooo + "Dzloginr";
    //推荐新闻接口
    public static final String ZiXun4Tujian_IP = host_Ip + oooooo + "Rnews";
    //直播详情接口
    public static final String Live_detail_Ip = host_Ip + oooooo + "Channelsd";
    //直播供养清单接口
    public static final String Live_gongyang_Ip = host_Ip + oooooo + "Channelsgyqd";
    //视频详情接口
    public static final String Video_detail_Ip = host_Ip + oooooo + "Videod";
    //   视频列表接口
    public static final String Video_list_Ip = host_Ip + oooooo + "Video";

    //直播列表接口
    public static final String Live_list_IP = host_Ip + oooooo + "Live";
    //视频评论接口
    public static final String Video_PL_IP = host_Ip + oooooo + "Videopl";
    //直播评论接口
    public static final String Live_PL_IP = host_Ip + oooooo + "Channelspl";
    //上传头像
    public static final String uploadHead_IP = host_Ip + oooooo + "Dzhead";
    //新闻收藏信息展示接口
    public static final String news_sc_list_Ip = host_Ip + oooooo + "Keepnews";
    //新闻收藏信息展示接口
    public static final String video_sc_list_Ip = host_Ip + oooooo + "Keepvideo";

    //新闻收藏信息展示接口
    public static final String temple_sc_list_Ip = host_Ip + oooooo + "Keeptemple";


    //忘记密码接口
    public static final String WJMM_IP = host_Ip + oooooo + "Mpwd";
    //寺庙列表接口
    public static final String simiao_IP = host_Ip + oooooo + "Temple";
    //个人信息完善接口
//    public static final String Mine_Grzl_IP = host_Ip + oooooo + "Dzuser";
    //用户认证须知接口
    public static final String User_Need_Know_IP = host_Ip + oooooo + "Yhxz";

    /**
     * 添加评论
     */
    //图文
    public static final String News_PL_add_IP = host_Ip + oooooo + "Newstjpl";
    //视频
    public static final String Video_PL_add_IP = host_Ip + oooooo + "Videotjpl";
    /**
     * 添加评论
     */
    //评论点赞
    public static final String PL_DZ_IP = host_Ip + oooooo + "Pllike";
    //新闻点赞
    public static final String ZX_DZ_IP = host_Ip + oooooo + "Newslike";
    //直播点赞
    public static final String MDEIA_DZ_IP = host_Ip + oooooo + "Channelslike";

    //视频点赞
    public static final String Video_DZ_IP = host_Ip + oooooo + "Videolike";
    //图文收藏
    public static final String News_SC_Ip = host_Ip + oooooo + "Newskeep";
    //视频收藏
    public static final String Video_SC_Ip = host_Ip + oooooo + "Videokeep";
    //关注用户接口
    public static final String Guanzhu_IP = host_Ip + oooooo + "Userkeep";
    //关注寺庙接口
    public static final String Temple_SC_IP = host_Ip + oooooo + "Templekeep";
    //nianfo——首页——nianfo
    public static final String nianfo_home_nianfo_Get_Ip = host_Ip + oooooo + "Buddha";
    //nianfo——首页——念佛提交
    public static final String nianfo_home_nianfo_Commit_Ip = host_Ip + oooooo + "Buddhad";
    //nianfo——首页——诵经
    public static final String nianfo_home_songjing_Get_Ip = host_Ip + oooooo + "Reading";
    //nianfo——首页——诵经提交
    public static final String nianfo_home_songjing_Commit_Ip = host_Ip + oooooo + "Readingd";
    //nianfo——首页——持咒
    public static final String nianfo_home_chizhou_Get_Ip = host_Ip + oooooo + "Japa";
    //nianfo——首页——持咒提交
    public static final String nianfo_home_chizhou_Commit_Ip = host_Ip + oooooo + "Japad";
    //nianfo——首页——忏悔
    public static final String nianfo_home_chanhui_Get_Ip = host_Ip + oooooo + "Confess";
    //nianfo——首页——忏悔提交
    public static final String nianfo_home_chanhui_Commit_Ip = host_Ip + oooooo + "Confesstj";
    //nianfo——首页——忏悔点赞
    public static final String nianfo_home_chanhui_Dianzan_Ip = host_Ip + oooooo + "Confessdz";
    //nianfo——首页——佛号加载
    public static final String nianfo_home_zhunian_fohao_Ip = host_Ip + oooooo + "Fohaohq";
    //nianfo——首页——助念
    public static final String nianfo_home_zhunian_Get_Ip = host_Ip + oooooo + "Reciting";
    //nianfo——助念详情
    public static final String nianfo_zhunian_detail_Get_Ip = host_Ip + oooooo + "Recitingmx";
    //nianfo——首页——助念提交
    public static final String nianfo_home_zhunian_Commit_Ip = host_Ip + oooooo + "Recitingtj";
    //nianfo——首页——助念点赞
    public static final String nianfo_home_zhunian_ZN_Ip = host_Ip + oooooo + "Recitingzn";
    //功课明细——nianfo
    public static final String Mine_GK_NF = host_Ip + oooooo + "Buddhaset";
    //功课明细——诵经
    public static final String Mine_GK_SJ = host_Ip + oooooo + "Readingset";
    //功课明细——持咒
    public static final String Mine_GK_CZ = host_Ip + oooooo + "Japaset";

    //诵经明细
    public static final String SongJing_detail_Ip = host_Ip + oooooo + "Readingcj";
    //持咒明细
    public static final String ChiZhou_detail_Ip = host_Ip + oooooo + "Japacj";
    //念佛明细
    public static final String Nianfo_detail_Ip = host_Ip + oooooo + "Buddhacj";


    //用户关注列表
    public static final String mine_GZ_list_Ip = host_Ip + oooooo + "Keepuser";
    //按昵称和电话号码搜索
    public static final String search_People_Ip = host_Ip + oooooo + "Cxfriend";
    //收到好友邀请查询邀请人信息
    public static final String search_FriendInvite_Ip = host_Ip + oooooo + "Usermsg";
    //同意好友请求
    public static final String agreeFriend_Ip = host_Ip + oooooo + "Tjfriend";
    //请求好友列表
    public static final String getFriendList_Ip = host_Ip + oooooo + "Friendlist";
    //推荐最热接口
    public static final String Home_hot = host_Ip + oooooo + "Hot";
    //每页图文条数
    public static final String ZiXun_total_count = "10";
    //需要刷新界面时的handler。what
    public static final int needSetMsg = 1;
    //加载数据失败的handler。what
    public static final int LoadFail = 2;
    //数据变更的handler。what
    public static final int needChanged = 0;

    public static String appFileName = "fojiao.apk";
    public static String SavePath = Environment.getExternalStorageDirectory().getAbsolutePath();


    public static final String User_info_xiugainc = host_Ip + oooooo + "Dzgrnc";//昵称
    public static final String User_info_xiugaixb = host_Ip + oooooo + "Dzgrxb";//性别
    public static final String User_info_xiugaitemple = host_Ip + oooooo + "Meanstemple"; //寺庙


    //好友通知
    public static final String MsgTZ_IP = host_Ip + oooooo + "Msgtz";
    //添加好友
    public static final String QQfriend_IP = host_Ip + oooooo + "Qqfriend";
    //接收好友请求
    public static final String Jsfriend_IP = host_Ip + oooooo + "Jsfriend";
    //删除好友
    public static final String SCfriend_IP = host_Ip + oooooo + "Scfriend";
    //我的助念明细
    public static final String MyZuNian_IP = host_Ip + oooooo + "Recitingmx";

    //私聊信息接口
    public static final String Siliao_IP = host_Ip + oooooo + "Siliao";
    //私聊信息发送接口
    public static final String Siliao_fasong_IP = host_Ip + oooooo + "Fsxiaoxi";
    //私聊会话列表接口
    public static final String Siliao_List_IP = host_Ip + oooooo + "Siliaolist";
    //删除通知接口
    public static final String Tongzhi_delete_IP = host_Ip + oooooo + "Shanchutz";

    //    //用户手机号绑定
//    public static final String Phone_Commit_Ip = host_Ip + oooooo + "Dzhqsjhm";
    //用户资料获取
    public static final String User_Info_Ip = host_Ip + oooooo + "Dzyhzlhq";
    //商品列表
    public static final String ShangPin_list_Ip = host_Ip + oooooo + "Shoplist";
    //商品详情页
    public static final String ShangPin_Detail_Ip = host_Ip + oooooo + "Shopd";
    //商品详情评论页
    public static final String ShangPin_Detail_PL_Ip = host_Ip + oooooo + "Shoppl";
    //商品详情评论页
    public static final String ShangPin_Detail_PL_COmmit_Ip = host_Ip + oooooo + "Shoptjpl";
    //支付清单页
    public static final String ZhiFu_Detail_Ip = host_Ip + oooooo + "Zcqd";
    //修改签名接口
    public static  final String SignChange=host_Ip+oooooo+"Dzgxqm";
    //活动列表
    public static final String Activity_list_IP = host_Ip + oooooo + "Activity";
    //活动详情
    public static final String Activity_detail_IP = host_Ip + oooooo + "Activityd";
    //活动评论列表
    public static final String Activity_pinglun_IP = host_Ip + oooooo + "Activitypl";
    //活动评论添加
    public static final String Activity_pinglun_add_IP = host_Ip + oooooo + "Activitytjpl";
    //活动收藏添加
    public static final String Activity_Shoucang_IP = host_Ip + oooooo + "Activitykeep";
    //活动收藏列表
    public static final String Activity_Shoucang_list_IP = host_Ip + oooooo + "Keepactivity";
    //活动点赞
    public static final String Activity_DZ_IP = host_Ip + oooooo + "Activitylike";
    //活动信息报名
    public static final String Activity_BaoMing_IP = host_Ip + oooooo + "Enrollment";
    //活动报名
    public static final String Activity_BaoMing = host_Ip + oooooo + "Activitybm";
    //短信邀请接口
    public static final String little_sms_get__IP = host_Ip + oooooo + "Dxyqhq";

    /**
     * 商品列表
     */
    public static final String GOODS_LIST = host_Ip + oooooo + "Products";
    /**
     * 商品详情
     */
    public static final String GOODS_DETAIL = host_Ip + oooooo + "Productsd";
    /**
     * 众筹列表
     */
    public static final String FUND_LIST = host_Ip + oooooo + "Zhongchou";
    /**
     * 众筹详情
     */
    public static final String FUND_DETAIL = host_Ip + oooooo + "Zhongchoud";
    //个人信息完善接口
    public static final String Mine_Grzl_IP = host_Ip + oooooo + "Dzuser";
    //登录接口
    public static final String Login_Ip = host_Ip + oooooo + "Dzlogin";
    //安全验证码
    public static final String safeKey = "TGubFeLjtK8vYZFA5zYfUAsJEkJekTyG";
    //    手机验证码地址:
    public static final String Mid_IP = host_Ip + oooooo + "Yzm";
    //本地注册接口
    public static final String Regist_Ip = host_Ip + oooooo + "Dzregister";
    //用户手机号绑定
    public static final String Phone_Commit_Ip = host_Ip + oooooo + "Dzhqsjhm";
    /**
     * 获取用户资料
     */
    public static final String USER_INFO = host_Ip + oooooo + "Dzyhzlhq";
    /**
     * 众筹详情的评论接口
     */
    public static final String FUNDING_DETAIL_COMMENTS = host_Ip + oooooo + "Cfgpl";
    /**
     * 众筹详情的添加评论接口
     */
    public static final String FUNDING_DETAIL_ADD_COMMENTS = host_Ip + oooooo + "Cfgtjpl";
    /**
     * 众筹详情的收藏接口
     */
    public static final String FUNDING_DETAIL_Shoucang = host_Ip + oooooo + "Cfgkeep";
    /**
     * 众筹详情的收藏列表接口
     */
    public static final String FUNDING_DETAIL_Shoucang_List = host_Ip + oooooo + "Keepcfg";


    //搜索
    //图文搜索
    public static final String News_Search_Ip = host_Ip + oooooo + "Newsquery";
    //活动搜索
    public static final String Activity_Search_Ip = host_Ip + oooooo + "Activityquery";
    //供养搜索
    public static final String GY_Search_Ip = host_Ip + oooooo + "Shopquery";
    //众筹搜索
    public static final String CFG_Search_Ip = host_Ip + oooooo + "Cfgquery";

    //众筹支持人数
    public static final String CFG_List_Ip = host_Ip + oooooo + "Cfgqd";
    public static final String WXPay_API="7IG4NxObuSMwtbvy9GMkDGjj4myqycqS";


//    评论回复
    public static final String little_zixun_pl_add_IP =host_Ip+oooooo+"Pinglun_hf";
    public static final  String little_pl_huifu__IP= host_Ip+oooooo+"Pinglun_hfxs";


    //邮箱注册
    public static final  String email_zhuce__IP= host_Ip+oooooo+"Dzregisteryx";
    //邮箱找回密码
    public static final  String email_findpsd__IP= host_Ip+oooooo+"Mpwdyx";

    //活动列表
    public static final String mine_activity=host_Ip+oooooo+"Myactivity";
    //获取用户协议
    public static final String getUserNeedKnow=host_Ip+oooooo+"Hqyhxy";

    //用户更多资料获取
    public static final String getMoreInfo=host_Ip+oooooo+"Dzyhxxzl";
    //个人评论获取
    public static final String getPinglunList=host_Ip+oooooo+"Pinglun_gr";

    //首页房产推荐以及更多列表
    public static  final String getHomeMore=host_Ip+iiiiii+"Fc_houselist";
    //房产分类以及更多列表
    public static  final String getHouses=host_Ip+iiiiii+"Fc_house";

    //城市标签
    public static final String getCitys=host_Ip+iiiiii+"Fc_city";
    //二手房详情
    public static final String getHouse2Detail=host_Ip+iiiiii+"Fc_housed";
    //无筛选搜索
    public static final String Query=host_Ip+iiiiii+"Fc_query";
    //价格筛选搜索
    public static final String JiaGeQuery=host_Ip+iiiiii+"Fc_moneyquery";
    //房型筛选搜索
    public static final String FangXingQuery=host_Ip+iiiiii+"Fc_housequery";
    //获取经纪人信息
    public static final String getBrokerInfo=host_Ip+iiiiii+"Fc_broker";
    //房源收藏与取消
    public static final String keepHouseNo=host_Ip+iiiiii+"Housekeep";
    //房源收藏调用
    public static final String getHouseKeeps = host_Ip + iiiiii + "Keephouse";

    //首页轮播图
    public static final String getBanner=host_Ip+iiiiii+"Banner";
    //房源发布
    public static final String FangYuanCommit=host_Ip+iiiiii+"Fc_housefb";
    //删除我的房源
    public static final String FangYuanDelete=host_Ip+iiiiii+"Fc_housedelete";
    //我的房源审核信息
    public static final String FangYuanList=host_Ip+iiiiii+"Fc_housesh";


    //全部点餐
    public static final String Order_total = host_Ip + oooooo + "Order";
    //分类点餐列表
    public static final String Order_special = host_Ip + oooooo + "Ordercx";
    //点餐类目
    public static final String Order_type = host_Ip + oooooo + "Ordertype";
    //加入购物车
    public static final String Order_add_car = host_Ip + oooooo + "Ordercar";
    //商品详情
    public static final String Order_detail = host_Ip + oooooo + "Orderd";
    //商品收藏取消详情
    public static final String Order_shoucang = host_Ip + oooooo + "Orderkeep";
    //商品收藏列表
    public static final String Order_shoucang_list = host_Ip + oooooo + "Keeporder";
    //商品点赞
    public static final String Order_like = host_Ip + oooooo + "Orderlike";


    //书城我的收获地址
    public static final String Shucheng_shouhuo_list_Ip = host_Ip + oooooo + "Address";
    //书城添加收货地址
    public static final String Shucheng_shouhuo_add_Ip = host_Ip + oooooo + "Addresstj";
    //书城修改收货地址
    public static final String Shucheng_shouhuo_update_Ip = host_Ip + oooooo + "Addressxg";
    //书城删除收货地址
    public static final String Shucheng_shouhuo_delete_Ip = host_Ip + oooooo + "Addresssc";


    //我的订单页面
    public static final String MyOrders=host_Ip+oooooo+"Myorders";
    //订单详情页面
    public static final String MyOrders_detail=host_Ip+oooooo+"Myordersd";
    //园介绍页面
    public static final String getQianFuInfo=host_Ip+iiiiii+"Home";
    //教师团队介绍页面
    public static final String getTeachers=host_Ip+iiiiii+"Teacherlist";
    //教师团队详情页面
    public static final String getTeacherDetail=host_Ip+iiiiii+"Teacherd";
    //我的班级列表
    public static final String getMyClassList=host_Ip+iiiiii+"Classlist";
    //班级用户列表
    public static final String getClassUserList=host_Ip+iiiiii+"Classuser";
    //管理员发布动态
    public static final String CommitDongtai=host_Ip+iiiiii+"Trends";
    //设置班级昵称
    public static final String setClassNickName=host_Ip+iiiiii+"Nickname";
    //管理员修改班级信息
    public static final String ChangeClassInfo=host_Ip+iiiiii+"Classupdate";
    //移除或退出班级
    public static final String QuitClass=host_Ip+iiiiii+"Classmove";
    //申请结果修改
    public static final String CommitApplyResult=host_Ip+iiiiii+"Applyresult";
    //申请结果删除
    public static final String DeleteApplyResult=host_Ip+iiiiii+"Applydelete";
    //我的申请列表
    public static final String MyApply=host_Ip+iiiiii+"Applylist";
    //图文收藏展示接口
    public static final String Zixun_shoucang_list_IP = host_Ip + oooooo + "Keepdraft";



    //班级动态接口
    public static final String ClassDongtaiList = host_Ip + iiiiii + "Trendslist";
    //申请加入班级
    public static final String ApplyIn = host_Ip + iiiiii + "Applyin";
    //查找班级
    public static final String SearchClasses = host_Ip + iiiiii + "Classquery";


    //上传图文
    public static final String upload_image = host_Ip + iiiiii + "Trends";
    //投稿列表接口
    public static final String Tougao_List_IP = host_Ip + iiiiii + "Trendslist";
    //申请加入列表
    public static final String ShengQingList = host_Ip + iiiiii + "Applyshenhe";
    //食谱列表
    public static final String Shipu = host_Ip + iiiiii + "Recipe";
    //食谱详情
    public static final String Shipu_Detail = host_Ip + iiiiii + "Reciped";
    //投稿详情接口
    public static final String Zixun_Detail_IP = host_Ip + oooooo + "Draftxq";
    //投稿修改保存接口
    public static final String Archmage_change_tougao_IP = host_Ip + oooooo + "Draftxg";

    //图文点赞接口
    public static final String Zixun_dianzan_IP = host_Ip + oooooo + "Draftlike";
    //图文收藏取消接口
    public static final String Zixun_shoucang_cancle_IP = host_Ip + oooooo + "Draftkeep";
    //图文评论展示接口
    public static final String Zixun_PL_IP = host_Ip + oooooo + "Draftpl";
    //图文评论提交接口
    public static final String Zixun_commitPL_IP = host_Ip + oooooo + "Drafttjpl";
    //咨询详情接口
    public static final String little_advice_detail_IP = host_Ip + oooooo + "Xcx_adviced";
    //咨询详情聊天列表接口
    public static final String little_advice_PL_IP = host_Ip + oooooo + "Xcx_advicepl";
    //咨询详情聊天文字回复接口
    public static final String little_advice_PL_text_IP = host_Ip + oooooo + "Xcx_adviceplwz";
    //咨询详情聊天图片回复接口
    public static final String little_advice_PL_img_IP = host_Ip + oooooo + "Xcx_advicepltp";
    //咨询天假接口
    public static final String little_advice_add_IP = host_Ip + oooooo + "Xcx_adviceadd";
    //私信音频接口
    public static final String little_addvice_audio__IP = host_Ip + oooooo + "Xcx_adviceplyp";
    //私信视频接口
    public static final String little_addvice_video__IP = host_Ip + oooooo + "Xcx_adviceplsp";


    //关注通知同意拒绝接口
    public static final String little_agree_diny_IP = host_Ip + oooooo + "Xcx_keepjg";
    //关注通知移除接口
    public static final String little_tongzhi_delete_IP = host_Ip + oooooo + "Xcx_sckeep";
    //判断是否关注过的接口
    public static final String little_had_Guanzhu_IP = host_Ip + oooooo + "Xcx_pdkeep";

    //私聊列表接口
    public static final String Chat_List = host_Ip + oooooo + "Siliao";
    //私聊文字接口
    public static final String Chat_Content_UpLoad = host_Ip + oooooo + "Fsxiaoxi";
    //私聊对象
    public static final String Chat_Message = host_Ip + oooooo + "Siliaolist";
    //帮助接口
    public static final String BangZhu=host_Ip+oooooo+"Bzzxhq";
    //开店用户协议获取
    public static final String Store_Prol = host_Ip + oooooo + "Yhxyhq";
    //商城搜索
    public static final String Good_Search = host_Ip + oooooo + "Orderquery";
    //课程搜索
    public static final String Activity_Search = host_Ip + oooooo + "Activityquery";
    //动态管理删除接口
    public static final String Archmage_delete_zixun_IP = host_Ip + oooooo + "Draftdelete";
    //修改备注接口
    public static final String little_beizhu__IP = host_Ip + oooooo + "Xcx_user_name";
    //修改群昵称接口
    public static final String Class_Name__IP = host_Ip + iiiiii + "Nickname";
}

