package com.maimaizu.Utils;

import android.os.Environment;

/**
 * Created by Administrator on 2016/5/30.
 */
public class Constants {

    //服务器测试域名
//    public static final String host_Ip = "http://yintolo.zhideng.net/amf";
    //    //服务器域名
    public static  final String host_Ip="https://indrah.cn";
    public static final String oooooo = "/api.php/Api/";
    public static final String iiiiii= "/api.php/Api1/";
    public static final String M_id = "18";
    public  static final String NAME_LOW="mmz";
    public static  final int  NAME_CHAR_NUM=8;

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
    public static final String WXPay_patnerID = "1393151502";
    //微信支付APPid
    public static final String WXPay_APPID = "wxd33fe2dd9a8d2b6b";

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
    //新闻资讯评论接口
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
    //资讯
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
    //资讯收藏
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
    //每页资讯条数
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
    //资讯搜索
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
}
