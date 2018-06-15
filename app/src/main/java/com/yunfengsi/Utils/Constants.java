package com.yunfengsi.Utils;

import android.os.Environment;

/**
 * Created by Administrator on 2016/5/30.
 */
public class Constants {

    //帮助网页
    public static final String Help = "https://indrah.cn/yfs.php/Index/help";

    //服务器测试域名
    public static final String host_Ip       = "http://yintolo.net";
    //    //服务器域名
//    public static  final String host_Ip="https://indrah.cn";
//    public static final String oooooo = "/api.php/Api/";
    public static final String oooooo        = "/api.php/EncryptApi/";
    public static final String iiiiii        = "/api.php/SecretApi/";
    public static final String ssssss        = "/api.php/EncryptApi/";
    public static final String pppppp        = "/api.php/Api/";
    public static final String M_id          = "1";
    public static final String NAME_LOW      = "yfs";
    public static final int    NAME_CHAR_NUM = 9;

    //统一更新
    public static final String UPDATE = host_Ip + "/download/yfs/index.php";

    public static final String cacheO           = NAME_LOW;
    //外部缓存储存地址
    public static final String ExternalCacheDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + cacheO + "/";
    //启动页加载
//    public  static  final String getAD=host_Ip+oooooo+"Qdongye";
    public static final String getAD            = host_Ip + oooooo + "Ggaoye";
    //定制更新
    public static final String Update_Ip        = host_Ip + oooooo + "Dzupdate";
    //安全验证码
//    public static final String safeKey = "TGubFeLjtK8vYZFA5zYfUAsJEkJekTyG";

    //微信支付APPid
    public static final String WXPay_APPID = "wxd33fe2dd9a8d2b6b";

    //举报反馈  key,user_id,title,contents
    public static final String Suggest_Ip         = host_Ip + oooooo + "Suggest";
    //关于我们  key,m_id
    public static final String AboutUs_Ip         = host_Ip + oooooo + "Ours";
    //微信支付下单地址
    public static final String WXPay_post_Url     = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    //分享域名
    public static final String FX_host_Ip         = Constants.host_Ip + "/" + NAME_LOW + ".php/Index/";
    //分享测试域名
    public static final String FX_CS_host_Ip      = "http://yintolo.zhideng.net/wap.php/Index/";
    //在服务器生成获取订单号
    public static final String getAttachId_ip     = host_Ip + pppppp + "Wxpay";
    //版本更新检测
    public static final String Check_Update_IP    = host_Ip + oooooo + "Updatejc";
    //图片储存地址
    public static final String IMGDIR             = Environment.getExternalStorageDirectory().getAbsolutePath() + "/fojiao/img/";
    //头像储存地址
    public static final String HEADDIR            = Environment.getExternalStorageDirectory().getAbsolutePath() + "/fojiao/head_IMG/";
    //    手机验证码地址:
//    public static final String Mid_IP = host_Ip + oooooo + "Yzm";
    //新闻详情
    public static final String ZiXun_detail_Ip    = host_Ip + oooooo + "newsd";
    //寺庙详情
    public static final String Simiao_detail_Ip   = host_Ip + oooooo + "Templed";
    //新闻图文评论接口
    public static final String ZiXun_detail_PL_Ip = host_Ip + oooooo + "Newspl";
    //所有新闻
    public static final String ZiXun_total_Ip     = host_Ip + oooooo + "news";
    //本地注册接口
//    public static final String Regist_Ip = host_Ip + oooooo + "Dzregister";
//    //本地登陆接口
//    public static final String Login_Ip = host_Ip + oooooo + "Dzlogin";
    //授权登陆接口
    public static final String Login_3_Ip         = host_Ip + oooooo + "Dzloginr";
    //推荐新闻接口
    public static final String ZiXun4Tujian_IP    = host_Ip + oooooo + "Rnews";
    //直播详情接口
    public static final String Live_detail_Ip     = host_Ip + oooooo + "Channelsd";
    //直播供养清单接口
    public static final String Live_gongyang_Ip   = host_Ip + oooooo + "Channelsgyqd";
    //视频详情接口
    public static final String Video_detail_Ip    = host_Ip + oooooo + "Videod";
    //   视频列表接口
    public static final String Video_list_Ip      = host_Ip + oooooo + "Video";

    //直播列表接口
    public static final String Live_list_IP     = host_Ip + oooooo + "Live";
    //视频评论接口
    public static final String Video_PL_IP      = host_Ip + oooooo + "Videopl";
    //直播评论接口
    public static final String Live_PL_IP       = host_Ip + oooooo + "Channelspl";
    //上传头像
    public static final String uploadHead_IP    = host_Ip + oooooo + "Dzhead";
    //新闻收藏信息展示接口
    public static final String news_sc_list_Ip  = host_Ip + oooooo + "Keepnews";
    //新闻收藏信息展示接口
    public static final String video_sc_list_Ip = host_Ip + oooooo + "Keepvideo";

    //新闻收藏信息展示接口
    public static final String temple_sc_list_Ip = host_Ip + oooooo + "Keeptemple";

    public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
    //忘记密码接口
    public static final String WJMM_IP             = host_Ip + oooooo + "Mpwd";
    //寺庙列表接口
    public static final String simiao_IP           = host_Ip + oooooo + "Temple";
    //个人信息完善接口
//    public static final String Mine_Grzl_IP = host_Ip + oooooo + "Dzuser";
    //用户认证须知接口
    public static final String User_Need_Know_IP   = host_Ip + oooooo + "Yhxz";

    /**
     * 添加评论
     */
    //图文
    public static final String News_PL_add_IP  = host_Ip + oooooo + "Newstjpl";
    //视频
    public static final String Video_PL_add_IP = host_Ip + oooooo + "Videotjpl";
    /**
     * 添加评论
     */
    //评论点赞
    public static final String PL_DZ_IP        = host_Ip + oooooo + "Pllike";
    //新闻点赞
    public static final String ZX_DZ_IP        = host_Ip + oooooo + "Newslike";
    //直播点赞
    public static final String MDEIA_DZ_IP     = host_Ip + oooooo + "Channelslike";

    //视频点赞
    public static final String Video_DZ_IP                    = host_Ip + oooooo + "Videolike";
    //图文收藏
    public static final String News_SC_Ip                     = host_Ip + oooooo + "Newskeep";
    //视频收藏
    public static final String Video_SC_Ip                    = host_Ip + oooooo + "Videokeep";
    //关注用户接口
    public static final String Guanzhu_IP                     = host_Ip + oooooo + "Userkeep";
    //关注寺庙接口
    public static final String Temple_SC_IP                   = host_Ip + oooooo + "Templekeep";
    //nianfo——首页——nianfo
    public static final String nianfo_home_nianfo_Get_Ip      = host_Ip + oooooo + "Buddha";
    //nianfo——首页——念佛提交
    public static final String nianfo_home_nianfo_Commit_Ip   = host_Ip + oooooo + "Buddhad";
    //nianfo——首页——诵经
    public static final String nianfo_home_songjing_Get_Ip    = host_Ip + oooooo + "Reading";
    //nianfo——首页——诵经提交
    public static final String nianfo_home_songjing_Commit_Ip = host_Ip + oooooo + "Readingd";
    //nianfo——首页——持咒
    public static final String nianfo_home_chizhou_Get_Ip     = host_Ip + oooooo + "Japa";
    //nianfo——首页——持咒提交
    public static final String nianfo_home_chizhou_Commit_Ip  = host_Ip + oooooo + "Japad";
    //nianfo——首页——忏悔
    public static final String nianfo_home_chanhui_Get_Ip     = host_Ip + oooooo + "Confess";
    //nianfo——首页——忏悔提交
    public static final String nianfo_home_chanhui_Commit_Ip  = host_Ip + oooooo + "Confesstj";
    //nianfo——首页——忏悔点赞
    public static final String nianfo_home_chanhui_Dianzan_Ip = host_Ip + oooooo + "Confessdz";
    //nianfo——首页——佛号加载
    public static final String nianfo_home_zhunian_fohao_Ip   = host_Ip + oooooo + "Fohaohq";
    //nianfo——首页——助念
    public static final String nianfo_home_zhunian_Get_Ip     = host_Ip + oooooo + "Reciting";
    //nianfo——助念详情
    public static final String nianfo_zhunian_detail_Get_Ip   = host_Ip + oooooo + "Recitingmx";
    //nianfo——首页——助念提交
    public static final String nianfo_home_zhunian_Commit_Ip  = host_Ip + oooooo + "Recitingtj";
    //nianfo——首页——助念点赞
    public static final String nianfo_home_zhunian_ZN_Ip      = host_Ip + oooooo + "Recitingzn";
    //功课明细——nianfo
    public static final String Mine_GK_NF                     = host_Ip + oooooo + "Buddhaset";
    //功课明细——诵经
    public static final String Mine_GK_SJ                     = host_Ip + oooooo + "Readingset";
    //功课明细——持咒
    public static final String Mine_GK_CZ                     = host_Ip + oooooo + "Japaset";

    //诵经明细
    public static final String SongJing_detail_Ip = host_Ip + oooooo + "Readingcj";
    //持咒明细
    public static final String ChiZhou_detail_Ip  = host_Ip + oooooo + "Japacj";
    //念佛明细
    public static final String Nianfo_detail_Ip   = host_Ip + oooooo + "Buddhacj";


    //用户关注列表
    public static final String mine_GZ_list_Ip        = host_Ip + oooooo + "Keepuser";
    //按昵称和电话号码搜索
    public static final String search_People_Ip       = host_Ip + oooooo + "Cxfriend";
    //收到好友邀请查询邀请人信息
    public static final String search_FriendInvite_Ip = host_Ip + oooooo + "Usermsg";
    //同意好友请求
    public static final String agreeFriend_Ip         = host_Ip + oooooo + "Tjfriend";
    //请求好友列表
    public static final String getFriendList_Ip       = host_Ip + oooooo + "Friendlist";
    //推荐最热接口
    public static final String Home_hot               = host_Ip + oooooo + "Hot";
    //每页图文条数
    public static final String ZiXun_total_count      = "10";
    //需要刷新界面时的handler。what
    public static final int    needSetMsg             = 1;
    //加载数据失败的handler。what
    public static final int    LoadFail               = 2;
    //数据变更的handler。what
    public static final int    needChanged            = 0;

    public static String appFileName = "fojiao.apk";
    public static String SavePath    = Environment.getExternalStorageDirectory().getAbsolutePath();


    public static final String User_info_xiugainc     = host_Ip + oooooo + "Dzgrnc";//昵称
    public static final String User_info_xiugaixb     = host_Ip + oooooo + "Dzgrxb";//性别
    public static final String User_info_xiugaitemple = host_Ip + oooooo + "Meanstemple"; //寺庙


    //好友通知
    public static final String MsgTZ_IP    = host_Ip + oooooo + "Msgtz";
    //添加好友
    public static final String QQfriend_IP = host_Ip + oooooo + "Qqfriend";
    //接收好友请求
    public static final String Jsfriend_IP = host_Ip + oooooo + "Jsfriend";
    //删除好友
    public static final String SCfriend_IP = host_Ip + oooooo + "Scfriend";
    //我的助念明细
    public static final String MyZuNian_IP = host_Ip + oooooo + "Recitingmx";

    //私聊信息接口
    public static final String Siliao_IP         = host_Ip + oooooo + "Siliao";
    //私聊信息发送接口
    public static final String Siliao_fasong_IP  = host_Ip + oooooo + "Fsxiaoxi";
    //私聊会话列表接口
    public static final String Siliao_List_IP    = host_Ip + oooooo + "Siliaolist";
    //删除通知接口
    public static final String Tongzhi_delete_IP = host_Ip + oooooo + "Shanchutz";

    //    //用户手机号绑定
//    public static final String Phone_Commit_Ip = host_Ip + oooooo + "Dzhqsjhm";
    //用户资料获取
    public static final String User_Info_Ip                 = host_Ip + oooooo + "Dzyhzlhq";
    //商品列表
    public static final String ShangPin_list_Ip             = host_Ip + oooooo + "Shoplist";
    //商品详情页
    public static final String ShangPin_Detail_Ip           = host_Ip + oooooo + "Shopd";
    //商品详情评论页
    public static final String ShangPin_Detail_PL_Ip        = host_Ip + oooooo + "Shoppl";
    //商品详情评论页
    public static final String ShangPin_Detail_PL_COmmit_Ip = host_Ip + oooooo + "Shoptjpl";
    //支付清单页
    public static final String ZhiFu_Detail_Ip              = host_Ip + oooooo + "Zcqd";
    //活动列表
    public static final String Activity_list_IP             = host_Ip + oooooo + "Activity";
    //活动详情
    public static final String Activity_detail_IP           = host_Ip + oooooo + "Activityd";
    //活动评论列表
    public static final String Activity_pinglun_IP          = host_Ip + oooooo + "Activitypl";
    //活动评论添加
    public static final String Activity_pinglun_add_IP      = host_Ip + oooooo + "Activitytjpl";
    //活动收藏添加
    public static final String Activity_Shoucang_IP         = host_Ip + oooooo + "Activitykeep";
    //活动收藏列表
    public static final String Activity_Shoucang_list_IP    = host_Ip + oooooo + "Keepactivity";
    //活动点赞
    public static final String Activity_DZ_IP               = host_Ip + oooooo + "Activitylike";
    //活动信息报名
    public static final String Activity_BaoMing_IP          = host_Ip + oooooo + "Enrollment";
    //活动报名
    public static final String Activity_BaoMing             = host_Ip + oooooo + "Activitybm";
    //短信邀请接口
    public static final String little_sms_get__IP           = host_Ip + oooooo + "Dxyqhq";


    /**
     * 商品列表
     */
    public static final String GOODS_LIST                   = host_Ip + oooooo + "Products";
    /**
     * 商品详情
     */
    public static final String GOODS_DETAIL                 = host_Ip + oooooo + "Productsd";
    /**
     * 众筹列表
     */
    public static final String FUND_LIST                    = host_Ip + oooooo + "Zhongchou";
    /**
     * 众筹详情
     */
    public static final String FUND_DETAIL                  = host_Ip + oooooo + "Zhongchoud";
    //个人信息完善接口
    public static final String Mine_Grzl_IP                 = host_Ip + oooooo + "Dzuser";
    //登录接口
    public static final String Login_Ip                     = host_Ip + oooooo + "Dzlogin";
    //安全验证码
    public static final String safeKey                      = "TGubFeLjtK8vYZFA5zYfUAsJEkJekTyG";
    //    手机验证码地址:
    public static final String Mid_IP                       = host_Ip + ssssss + "Yzm";
    //    public static final String Mid_IP = host_Ip + ssssss + "Rsa";
    //本地注册接口
    public static final String Regist_Ip                    = host_Ip + oooooo + "Dzregister";
    //用户手机号绑定
    public static final String Phone_Commit_Ip              = host_Ip + oooooo + "Dzhqsjhm";
    /**
     * 获取用户资料
     */
    public static final String USER_INFO                    = host_Ip + oooooo + "Dzyhzlhq";
    /**
     * 众筹详情的评论接口
     */
    public static final String FUNDING_DETAIL_COMMENTS      = host_Ip + oooooo + "Cfgpl";
    /**
     * 众筹详情的点赞接口
     */
    public static final String FUNDDeatail_like             = host_Ip + oooooo + "Cfglike";
    /**
     * 众筹详情的添加评论接口
     */
    public static final String FUNDING_DETAIL_ADD_COMMENTS  = host_Ip + oooooo + "Cfgtjpl";
    /**
     * 众筹详情的收藏接口
     */
    public static final String FUNDING_DETAIL_Shoucang      = host_Ip + oooooo + "Cfgkeep";
    /**
     * 众筹详情的收藏列表接口
     */
    public static final String FUNDING_DETAIL_Shoucang_List = host_Ip + oooooo + "Keepcfg";


    //搜索
    //图文搜索
    public static final String News_Search_Ip     = host_Ip + oooooo + "Newsquery";
    //活动搜索
    public static final String Activity_Search_Ip = host_Ip + oooooo + "Activityquery";
    //供养搜索
    public static final String GY_Search_Ip       = host_Ip + oooooo + "Shopquery";
    //众筹搜索
    public static final String CFG_Search_Ip      = host_Ip + oooooo + "Cfgquery";

    //众筹支持人数
    public static final String CFG_List_Ip = host_Ip + oooooo + "Cfgqd";
    public static final String WXPay_API   = "7IG4NxObuSMwtbvy9GMkDGjj4myqycqS";


    //    评论回复
    public static final String little_zixun_pl_add_IP = host_Ip + oooooo + "Pinglun_hf";
    public static final String little_pl_huifu__IP    = host_Ip + oooooo + "Pinglun_hfxs";


    //邮箱注册
    public static final String email_zhuce__IP   = host_Ip + oooooo + "Dzregisteryx";
    //邮箱找回密码
    public static final String email_findpsd__IP = host_Ip + oooooo + "Mpwdyx";

    //活动列表
    public static final String mine_activity   = host_Ip + oooooo + "Myactivity";
    //获取用户协议
    public static final String getUserNeedKnow = host_Ip + oooooo + "Hqyhxy";

    //用户更多资料获取
    public static final String getMoreInfo    = host_Ip + oooooo + "Dzyhxxzl";
    //个人评论获取
    public static final String getPinglunList = host_Ip + oooooo + "Pinglun_gr";
    //修改签名接口
    public static final String SignChange     = host_Ip + oooooo + "Dzgxqm";


    //首页轮播图
    public static final String getBanner = host_Ip + iiiiii + "Banner";

    //获取会员资料    key mid  userid
    public static final String getLevelInfo     = host_Ip + iiiiii + "Insidersave";
    //会员等级进度    key mid  userid
    public static final String getLevelProgress = host_Ip + iiiiii + "Level";
    //会员成长记录    key mid  userid
    public static final String getLevelList     = host_Ip + iiiiii + "Growth";
    //助学回向备注提交    key mid  userid

    public static final String CfgCommit = host_Ip + iiiiii + "Cfghxtj";


    //上传图文
    public static final String upload_image   = host_Ip + ssssss + "Draft";
    //通知中心
    public static final String tongzhi_center = host_Ip + ssssss + "Dope";
    //通知中心详情
    public static final String tongzhi_Detail = host_Ip + ssssss + "Doped";
    //通知删除
    public static final String tongzhi_Delete = host_Ip + ssssss + "Dopedelete";

    //用户协议获取接口
    public static final String little_yhxy__IP          = host_Ip + oooooo + "Yhxyhq";
    //用户协议同意
    public static final String yhxy_agree               = host_Ip + oooooo + "Privacy";
    //发愿公示列表
    public static final String Fayuan_List_Ip           = host_Ip + oooooo + "Fayuan";
    //发愿提交
    public static final String Fayuan_Commit_Ip         = host_Ip + oooooo + "Fayuand";
    //发愿提交
    public static final String Fayuan_TargetTime_Get_Ip = host_Ip + oooooo + "Fayuansjhq";
    //发愿信息统计
    public static final String Fayuan_Info_Ip           = host_Ip + oooooo + "Fayuansettj";
    //发愿信息列表
    public static final String Fayuan_Info_List_Ip      = host_Ip + oooooo + "Fayuansetlist";
    //发愿信息详情
    public static final String Fayuan_Info_Detail_Ip    = host_Ip + oooooo + "Fayuansetd";

    //众筹最新消息
    public static final String Fund_HeadLine     = host_Ip + oooooo + "Cfgtrends";
    //消息中心
    public static final String Notice            = host_Ip + oooooo + "Notice";
    //供养最新消息
    public static final String GongYang_HeadLine = host_Ip + oooooo + "Shoptrends";
    //红包链接
    public static final String RedMoney          = host_Ip + iiiiii + "Redmoney";
    //红包信息，图片
    public static final String RedBack           = host_Ip + iiiiii + "Redback";
    //微信授权
    public static final String WeChatRed         = host_Ip + iiiiii + "Apphb";
    //个人认证资料更换
    public static final String Dzgrzlxg          = host_Ip + oooooo + "Dzgrzlxg";
    //首页广告弹窗
    public static final String App_GGY           = host_Ip + oooooo + "App_ggy";

    //抽签
    public static final String Fortune        = host_Ip + iiiiii + "Draw";
    //抽签历史
    public static final String FortuneHistory = host_Ip + iiiiii + "Drawlist";

    //通讯录上传
    public static final String Contacts = host_Ip + iiiiii + "Txlupload";

    //坐禅提交
    public static final String Muse = host_Ip + iiiiii + "Muse";

    //坐禅记录
    public static final String MuseList = host_Ip + iiiiii + "Muselist";

    //判断用户是否报名
    public static final String IsEnrolled = host_Ip + oooooo + "Actpd";


    //发布约车
    public static final String SubmitCar = host_Ip + oooooo + "Fbcar";

    //念佛、诵经、持咒 删除
    public static final String Buddha_Delete   = host_Ip + oooooo + "Buddhadelete";
    //发愿 删除
    public static final String Fayuan_Delete   = host_Ip + oooooo + "Fayuandelete";
    //助念 删除
    public static final String Reciting_Delete = host_Ip + oooooo + "Recitingdelete";
    //忏悔 删除
    public static final String Confess_Delete  = host_Ip + oooooo + "Confessdelete";

    //约车列表
    public static final String CarList      = host_Ip + oooooo + "Yuecarlist";
    //删除约车
    public static final String DeleteYueCar = host_Ip + oooooo + "Deleteyuecar";
    //申请约车
    public static final String SubmitYueCar = host_Ip + oooooo + "Sqyuecar";

    //约车管理列表
    public static final String YuecarManageList = host_Ip + oooooo + "Yuecargl";

    //活动签到
    public static final String Signact = host_Ip + oooooo + "Signact";

    //经书加载
    public static final String JingShu       = host_Ip + iiiiii + "Ebooklist";
    //获取许愿动态
    public static final String BlessContent  = host_Ip + iiiiii + "Wishlist";
    //许愿
    public static final String Bless         = host_Ip + iiiiii + "Wishadd";
    //我的祈愿记录
    public static final String BlessHistory  = host_Ip + iiiiii + "Mywish";
    //评论回复页信息获取
    public static final String PingLunDetail = host_Ip + iiiiii + "Pinglund";

    //大藏经顶级目录接口
    public static final String DaZang_Chapter_Top = host_Ip + iiiiii + "Dzjlist";

    //大藏经次级目录及内容接口
    public static final String DaZang_Chapter_Detail = host_Ip + iiiiii + "Dzjd";
    //大藏经搜索接口
    public static final String DaZang_Search         = host_Ip + iiiiii + "Dzjquery";
    //经书纠错接口
    public static final String JiuCuo                = host_Ip + iiiiii + "Ebookadd";

    //大藏经足迹列表接口
    public static final String DaZangHistory        = host_Ip + iiiiii + "Dzjkeep";
    //大藏经足迹删除接口
    public static final String DaZangHistory_Delete = host_Ip + iiiiii + "Dzjkeepdelete";

    //修行经历列表接口
    public static final String Practice   = host_Ip + iiiiii + "Practice";
    //云豆界面接口
    public static final String YunDouHome = host_Ip + iiiiii + "Yundou";
    //云豆列表接口
    public static final String YunDouList = host_Ip + iiiiii + "Yundoulist";

    //兑换中心列表接口
    public static final String ExchangeList       = host_Ip + iiiiii + "Welfarelist";
    //兑换中心列表标题接口
    public static final String ExchangeListTitles = host_Ip + iiiiii + "Welfaretype";

    //券详情兑换接口
    public static final String Exchange  = host_Ip + iiiiii + "Welfarebuy";
    //我的券接口
    public static final String MyWelfare = host_Ip + iiiiii + "Mywelfare";


    //每日供养回调接口
    public static final String GY_YUNDOU = host_Ip + iiiiii + "Yundougy";

    //每日助学回调接口
    public static final String ZX_YUNDOU = host_Ip + iiiiii + "Yundouzx";


    //统一收藏接口
    public static final String Collect = host_Ip + iiiiii + "Collect";

    //收藏列表接口
    public static final String Collect_List = host_Ip + iiiiii + "Collectlist";

    //收藏搜索接口
    public static final String Collect_Search_List = host_Ip + iiiiii + "Collectquery";

    //云豆排行接口
    public static final String Yundou_PaiHang_List = host_Ip + iiiiii + "Yundouph";


    //祈福，排位券使用接口
    public static final String Quan_Use = host_Ip + iiiiii + "Welfareuse";


    //壁纸列表接口
    public static final String WallPaperList = host_Ip + iiiiii + "Wallpaper";

    //壁纸分类接口
    public static final String WallPaperTypeList = host_Ip + iiiiii + "Wallpapertype";

    //壁纸分类细分列表接口
    public static final String WallPaperTypeListClassfied = host_Ip + iiiiii + "Wallpapertypelist";

    //我的壁纸列表接口
    public static final String WallPaperMine = host_Ip + iiiiii + "Wallpapermy";

    //我的待审核壁纸列表接口
    public static final String WallPaperMineWaitingForVerified = host_Ip + iiiiii + "Wallpapermykeep";

    //我的收藏壁纸列表接口
    public static final String WallPaperMyCollection = host_Ip + iiiiii + "Mywallpaper";

    //我的壁纸删除接口
    public static final String WallPaperTMineDelete = host_Ip + iiiiii + "Wallpapermydelete";

    //壁纸收藏、删除接口
    public static final String WallPaperCollectInterface = host_Ip + iiiiii + "Wallpaperkeep";

    //壁纸点赞接口
    public static final String WallPaperLike = host_Ip + iiiiii + "Wallpaperlike";

    //壁纸评论提交接口
    public static final String WallPaperCommentUpload = host_Ip + iiiiii + "Wallpapertjpl";

    //壁纸上传接口
    public static final String WallPaperUpLoad = host_Ip + iiiiii + "Wallpaperadd";

    //壁纸详情接口
    public static final String WallPaperDetail = host_Ip + iiiiii + "Wallpaperd";


    //壁纸评论列表接口
    public static final String WallPaperCommentList = host_Ip + iiiiii + "Wallpaperpl";

    //待审核评论列表接口
    public static final String PendingCommentList = host_Ip + iiiiii + "Plshenhe";

    //待审核评论删除接口
    public static final String PendingCommentDelete = host_Ip + iiiiii + "Pldelete";

    //待审核评论显示接口
    public static final String PendingCommentDisplay = host_Ip + iiiiii + "Plsave";


    //用户禁言列表接口
    public static final String UserBanCommentList = host_Ip + iiiiii + "Userabatelist";

    //用户禁言接口
    public static final String UserBanComment = host_Ip + iiiiii + "Userabate";

    //祈愿管理列表接口
    public static final String WishManageList = host_Ip + iiiiii + "Wishgllist";

    //祈愿删除列表接口
    public static final String WishManageDelete = host_Ip + iiiiii + "Wishgldelete";

    //义卖出价排行榜接口
    public static final String AuctionBidList = host_Ip + iiiiii + "Auction_userlist";


    //义卖留言显示接口
    public static final String AuctionCommentList = host_Ip + iiiiii + "Auctionpl";

    //义卖评论提交接口
    public static final String AuctionCommentPost = host_Ip + iiiiii + "Auctiontjpl";

    //义卖出价接口
    public static final String AuctionOffer = host_Ip + iiiiii + "Auction_offer";

    //义卖详情接口
    public static final String AuctionDetail = host_Ip + iiiiii + "Auctiond";

    //义卖我的竞拍记录接口
    public static final String AuctionMyBid = host_Ip + iiiiii + "Myauction";



    //所有义卖列表接口
    public static final String AuctionList = host_Ip + iiiiii + "Auctionlist";

    //发货管理列表接口
    public static final String DeliveryMangeList = host_Ip + iiiiii + "Deliverylist";


    //用户资料完善接口
    public static final String UserForm = host_Ip + iiiiii + "Userform";

    //众筹列表换一换接口
    public static final String Fund_Change = host_Ip + oooooo + "Zhongchouhyh";

    //关于云峰寺接口
    public static final String AboutApp = host_Ip + oooooo + "Aboutapp";


    //我的收获地址
    public static final String Shucheng_shouhuo_list_Ip = host_Ip + oooooo + "Address";
    //添加收货地址
    public static final String Shucheng_shouhuo_add_Ip = host_Ip + oooooo + "Addresstj";
    //修改收货地址
    public static final String Shucheng_shouhuo_update_Ip = host_Ip + oooooo + "Addressxg";
    //删除收货地址
    public static final String Shucheng_shouhuo_delete_Ip = host_Ip + oooooo + "Addresssc";


    //个人订单详情页面
    public static final String MyOrders_detail=host_Ip+iiiiii+"Mybilld";


    //快递信息查询
    public static final String Expresses = host_Ip + iiiiii + "Express";
    //快递公司列表查询
    public static final String ExpressesList= host_Ip + iiiiii + "Expresslist";
    //绑定快递信息
    public static final String Bdexpress= host_Ip + iiiiii + "Bdexpress";


    //绑定快递单号同时发货
    public static final String Deliveryexpress= host_Ip + iiiiii + "Deliveryexpress";



//    //管理员订单详情接口
//    public static final String GoodsAdminDdxq= host_Ip + iiiiii + "Myddxq";
}
