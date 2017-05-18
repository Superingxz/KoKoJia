package com.nuoxian.kokojia.http;

/**
 * Created by chensilong on 2016/6/29.
 */
public class Urls {

    /**
     * 课程列表
     * @param id 分类id
     * @param difficulty 难度
     * @param order 综合
     * @param sort 全部、免费、付费
     * @param page
     * @return
     */
    public static String getHomeCourseContentUrl(int id,String difficulty,String order,String sort,int page){
        String path = null;
        path = "http://www.kokojia.com/?m=App&a=courseType&tid="+id+"&nandu="+difficulty+"&order="+order+"&sort="+sort+"&page="+page;
        return path;
    }

    /**
     * 分类
     */
    public static String CLASSIFICATION_URL = "http://www.kokojia.com/?m=App&a=courseType&tid=2";

    /**
     * 搜索地址
     * @param word
     * @param page
     * @return
     */
    public static String SEARCH_URL = "http://www.kokojia.com/?m=App&a=getSearchCourse";

    /**
     * 注册获取验证码地址
     * @param phone
     * @return
     */
    public static String GET_CODE_URL = "http://www.kokojia.com/?m=App&a=checkUsername";

    /**
     * 找回密码获取验证码
     */
    public static String FIND_PASSWORD_CODE = "http://www.kokojia.com/?m=App&a=findPassword";

    /**
     * 找回密码第二步
     */
    public static String FIND_PASSWORD_SECOND = "http://www.kokojia.com/?m=App&a=findPasswordStep";

    /**
     * 找回密码第三步
     */
    public static String FIND_PASSWORD_THIRD = "http://www.kokojia.com/?m=App&a=findPasswordSave";

    /**
     * 提交注册信息
     */
    public static String REGIST_URL = "http://www.kokojia.com/?m=App&a=register";

    /**
     * 登录
     */
    public static String LOGIN_URL = "http://www.kokojia.com/?m=App&a=login";

    /**
     * 我的页面
     */
    public static String MINE_URL = "http://www.kokojia.com/?m=App&a=getUserInfo";

    /**
     * 详情页概述
     * @param id
     * @param uid
     * @return
     */
    public static String getCourseDetialsUrl(String id,String uid){
        return "http://www.kokojia.com/?m=App&a=courseDetail&id="+id+"&uid="+uid;
    }

    /**
     * 详情页目录
     * @param id
     * @param uid
     * @return
     */
    public static String getCourseLessonUrl(String id,String uid,int page){
        return "http://www.kokojia.com/?m=App&a=courseLesson&id="+id+"&uid="+uid+"&page="+page;
    }

    /**
     * 详情页讨论
     * @param id
     * @param uid
     * @return
     */
    public static String courseDiscussUrls(String id,String uid,int page){
        return "http://www.kokojia.com/?m=App&a=courseDiscuss&id="+id+"&uid="+uid+"&page="+page;
    }

    /**
     * 发表讨论
     */
    public static String SEND_TALKING = "http://www.kokojia.com/?m=App&a=courseDiscussPublish";

    /**
     * 讨论中点赞
     */
    public static String COURSE_DISCUSS_ZAN = "http://www.kokojia.com/?m=App&a=courseDiscussVote";

    /**
     * 讨论中的回复
     */
    public static String COURSE_DISCUSS_REPLY = "http://www.kokojia.com/?m=App&a=courseDiscussReply";

    /**
     * 详情页评价
     * @param id
     * @param uid
     * @return
     */
    public static String getCourseCommentUrl(String id,String uid,int page){
        return "http://www.kokojia.com/?m=App&a=courseComment&id="+id+"&uid="+uid+"&page="+page;
    }

    /**
     * 详情页下载
     * @param id
     * @param uid
     * @return
     */
    public static String getCourseLectureUrl(String id,String uid){
        return "http://www.kokojia.com/?m=App&a=courseLecture&id="+id+"&uid="+uid;
    }

    /**
     * 我的课程地址
     */
    public static String myCourseUrl = "http://www.kokojia.com/?m=App&a=myCourse";

    /**
     * 播放页面地址
     */
    public static String playUrl = "http://www.kokojia.com/?m=App&a=courseVideo";

    /**
     * 课件地址
     * @return
     */
    public static String courseWareUrl(String id,String uid){
        return "http://www.kokojia.com/?m=App&a=courseLecture&id="+id+"&uid="+uid;
    }

    /**
     * 下载课件
     */
    public static String DOWN_LOAD_LECTURE = "http://www.kokojia.com/?m=App&a=courseLectureDownload";

    /**
     * 下载列表
     */
    public static String DOWN_LOAD_LESSON = "http://www.kokojia.com/?m=App&a=courseLessonDownload";

    /**
     * 视频下载地址
     */
    public static String VIDEO_DOWN_LOAD = "http://www.kokojia.com/?m=App&a=courseVideoDownload";

    /**
     * 确认订单界面
     */
    public static String CONFIRM_ORDER = "http://www.kokojia.com/?m=App&a=orderCart";
    /**
     * 订单提交结果
     */
    public static String CONFIRM_ORDER_RESULT = "http://www.kokojia.com/?m=App&a=orderCartSubmit";
    /**
     * 支付界面
     */
    public static String ORDER_PAY = "http://www.kokojia.com/?m=App&a=confirmOrder";
    /**
     * 余额确认支付
     */
    public static String CONFIRM_BALANCE_PAY = "http://www.kokojia.com/?m=App&a=userConfirmOrderSubmit";
    /**
     * 充值中心界面
     */
    public static String RECHARGE_CENTER = "http://www.kokojia.com/?m=App&a=userRecharge";
    /**
     * 充值
     */
    public static String RECHARGE_COMFIRM = "http://www.kokojia.com/?m=App&a=userRechargePay";
    /**
     * 支付宝支付
     */
    public static String THIRD_PAY = "http://www.kokojia.com/?m=App&a=payConfirmOrderSubmit";
    /**
     * 微信支付
     */
    public static String WX_BUY = "http://www.kokojia.com/?m=Wxpay&a=payConfirmOrderSubmit";
    /**
     * 版本更新
     */
    public static String UPDATA_URL = "http://www.kokojia.com/?m=App&a=getKoKoJiaApi";
    /**
     * 我的订单
     */
    public static String MY_ORDER = "http://www.kokojia.com/?m=App&a=myOrder";
    /**
     * 微信充值
     */
    public static String WX_PAY = "http://www.kokojia.com/?m=Wxpay&a=userRechargePay";
    /**
     * 获取微信密钥
     */
    public static String WX_KEY_URL = "http://www.kokojia.com/?m=App&a=loginWxpayKey";

    /**
     * 获取access_token地址
     * @param appId
     * @param secret
     * @param code
     * @return
     */
    public static String getAccessTokenUrl(String appId,String secret,String code){
        return "https://api.weixin.qq.com/sns/oauth2/access_token?appid="+appId+"&secret="+secret+"&code="+code+"&grant_type=authorization_code";
    }

    /**
     * 刷新或续期access_token
     * @param appId
     * @param refresh_token
     * @return
     */
    public static String refreshAccessTokenUrl(String appId,String refresh_token){
        return "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid="+appId+"&grant_type=refresh_token&refresh_token="+refresh_token;
    }

    /**
     * 检查access_token是否有效
     * @param access_token
     * @param openid
     * @return
     */
    public static String checkAccessTokenUrl(String access_token,String openid){
        return "https://api.weixin.qq.com/sns/auth?access_token="+access_token+"&openid="+openid;
    }

    /**
     * 获取微信的用户信息
     * @param access_token
     * @param openid
     * @return
     */
    public static String getWXUserInfoUrl(String access_token,String openid){
        return "https://api.weixin.qq.com/sns/userinfo?access_token="+access_token+"&openid="+openid;
    }

    /**
     * 第三方登录地址
     */
    public static String THIRD_LOGIN_URL="http://www.kokojia.com/?m=App&a=loginCallBack";

    /**
     * 绑定邮箱
     */
    public static String BIND_EMAILE = "http://www.kokojia.com/?m=App&a=regUserBind";

    /**
     * 获取新浪微博的appid,appkey
     */
    public static String SINA_ID_KEY_URL = "http://www.kokojia.com/?m=App&a=loginSinaKey";

    /**
     * 我的学院中的我的课程
     */
    public static String MY_SCHOOL_MY_COURSE = "http://www.kokojia.com/?m=App&a=getCourseManage";

    /**
     * 我的学院的销售订单
     */
    public static String MY_SCHOOL_SELL_ORDER = "http://www.kokojia.com/?m=App&a=getSchoolOrder";

    /**
     * 我的学院的我的套餐
     */
    public static String MY_SCHOOL_MY_PACKAGE = "http://www.kokojia.com/?m=App&a=getPackageManage";

    /**
     * 绑定邮箱或手机号码
     */
    public static String BIND_EMAIL_OR_PHONE = "http://www.kokojia.com/?m=App&a=regUserBind";

    /**
     *  绑定已有账号
     */
    public static String BIND_ACCOUNT = "http://www.kokojia.com/?m=App&a=userBindLogin";

    /**
     * 获取百度appid
     */
    public static String BAIDU_APPID = "http://www.kokojia.com/?m=App&a=loginbaiduKey";

    /**
     * 课课家服务协议
     */
    public static String SERVICE_PROVISION = "http://www.kokojia.com/?m=App&a=getAboutProtocol";

    /**
     * 套餐详情
     */
    public static String packageDetailsUrl(String id,String uid){
        return "http://www.kokojia.com/?m=App&a=packageDetail&id="+id+"&uid="+uid;
    }

    /**
     * 学院首页
     * @param id
     * @return
     */
    public static String schoolIndexUrl(String id){
        return "http://www.kokojia.com/?m=App&a=getSchoolMain&id="+id;
    }

    /**
     * 讲师答疑
     */
    public static String TEACHER_ANSWER = "http://www.kokojia.com/?m=App&a=getSchoolTeacherAsk";

    /**
     * 讲师答疑详情
     */
    public static String teacherAnswerDetails(String id,String uid){
        return "http://www.kokojia.com/?m=App&a=courseDiscussAsk&id="+id+"&uid="+uid;
    }

    /**
     * 首页
     */
    public static String indexUrl(int page){
        return "http://www.kokojia.com/?m=App&a=getIndex&page="+page;
    }

    /**
     * 我的提问
     */
    public static String MY_QUESTION = "http://www.kokojia.com/?m=App&a=myDiscuss";

    /**
     * 收藏
     */
    public static String COURSE_COLLECT = "http://www.kokojia.com/?m=App&a=courseCollect";

    /**
     * 我的收藏
     */
    public static String MY_COLLECT = "http://www.kokojia.com/?m=App&a=myFavorite";

    /**
     * 退款管理
     */
    public static String MANAGE_REFUND = "http://www.kokojia.com/?m=App&a=getSchoolRefund";

    /**
     * 退款详情
     */
    public static String REFUND_DETAILS = "http://www.kokojia.com/?m=App&a=getSchoolRefundDetail";

    /**
     * 退款处理接口
     */
    public static String SOLVE_REFUND = "http://www.kokojia.com/?m=App&a=getSchoolRefundSave";

    /**
     * 我的优惠券
     */
    public static String MY_COUPON = "http://www.kokojia.com/?m=App&a=myCoupon";

    /**
     * 创建优惠券的老师的优惠券
     */
    public static String TEACHER_COUPON = "http://www.kokojia.com/?m=App&a=getSchoolCoupon";

    /**
     * 创建优惠券，显示所有课程
     */
    public static String COUPON_ALL_COURSE = "http://www.kokojia.com/?m=App&a=getSchoolCouponInfo";

    /**
     * 创建优惠券
     */
    public static String CREATE_COUPON = "http://www.kokojia.com/?m=App&a=getSchoolCouponSave";

    /**
     * 支付中心
     */
    public static String PAY_CENTER = "http://www.kokojia.com/?m=App&a=getPayCenter";

    /**
     * 账户余额
     */
    public static String USER_BALANCE = "http://www.kokojia.com/?m=App&a=getPayCenterAccount";

    /**
     * 提现
     */
    public static String GET_MONEY = "http://www.kokojia.com/?m=App&a=getPayCenterDeposit";

    /**
     * 获取用户绑定的支付宝
     */
    public static String BIND_ZFB_URL = "http://www.kokojia.com/?m=App&a=getPayCenterBindAlipay";

    /**
     * 绑定支付宝
     */
    public static String BIND_ZFB = "http://www.kokojia.com/?m=App&a=getPayCenterBindAlipayHandle";

    /**
     * 解绑支付宝
     */
    public static String CANCLE_BIND_ZFB = "http://www.kokojia.com/?m=App&a=getPayCenterDelBindAlipay";

    /**
     * 设置支付密码
     */
    public static String SET_PAY_PASSWORD = "http://www.kokojia.com/?m=App&a=getPayCenterSetPassword";

    /**
     * 修改支付密码
     */
    public static String UPDATA_PAY_PASSWORD = "http://www.kokojia.com/?m=App&a=getPayCenterModifyPassword";

    /**
     * 支出记录
     */
    public static String PAY_CENTER_PAY = "http://www.kokojia.com/?m=App&a=getPayCenterBuyLog";

    /**
     * 充值记录
     */
    public static String PAY_CENTER_RECHARGE = "http://www.kokojia.com/?m=App&a=getPayCenterRechargeLog";

    /**
     * 退款记录
     */
    public static String PAY_CENTER_REFUND = "http://www.kokojia.com/?m=App&a=getPayCenterRefundLog";

    /**
     * 提现记录
     */
    public static String PAY_CENTER_WITHDRAWALS = "http://www.kokojia.com/?m=App&a=getPayCenterDepositLog";

    /**
     * 全部套餐
     */
    public static String allpackageUrl(String tid,String order,int page){
        return "http://www.kokojia.com/?m=App&a=getCoursePackageType"+"&tid="+tid+"&order="+order+"&page="+page;
    }

    /**
     * 限时折扣
     * @param page
     * @return
     */
    public static String limitTimeDiscount(int page){
        return "http://www.kokojia.com/?m=App&a=getCourseSpecial&page="+page;
    }

    /**
     * 发表评论
     */
    public static String SEND_COMMENT = "http://www.kokojia.com/?m=App&a=courseCommentPublish";

    /**
     * 领取优惠券信息
     * @param id
     * @param uid
     * @return
     */
    public static String getCoupon(String id,String uid){
        return "http://www.kokojia.com/?m=App&a=courseCoupon&id="+id+"&uid="+uid;
    }

    /**
     * 领取优惠券
     */
    public static String RECEIVE_COUPON = "http://www.kokojia.com/?m=App&a=courseCouponReceive";

    /**
     * 验证验证码
     */
    public static String VERIFICATION_CODE = "http://www.kokojia.com/?m=App&a=checkUserCoupon";

    /**
     * 播放页面 讨论
     * @param id
     * @param uid
     * @param page
     * @return
     */
    public static String playDiscuss(String id,String uid,int page){
     return "http://www.kokojia.com/?m=App&a=courseVideoDiscuss&id="+id+"&uid="+uid+"&page="+page;
    }

    /**
     *  我的笔记
     * @param id
     * @param uid
     * @param lid
     * @param page
     * @return
     */
    public static String myNote(String id,String uid,String lid,int page){
        return "http://www.kokojia.com/?m=App&a=courseVideoNote&id="+id+"&uid="+uid+"&lid="+lid+"&page="+page;
    }

    /**
     * 提交或编辑笔记
     */
    public static String COMMIT_NOTE = "http://www.kokojia.com/?m=App&a=courseNotePublish";

    /**
     * 删除笔记
     */
    public static String DELETE_NOTE = "http://www.kokojia.com/?m=App&a=courseNoteDel";

    /**
     * 笔记点赞
     */
    public static String DING_NOTE = "http://www.kokojia.com/?m=App&a=courseNoteVote";
}
