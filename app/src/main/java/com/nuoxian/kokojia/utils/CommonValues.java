package com.nuoxian.kokojia.utils;

import android.os.Environment;

/**
 * Created by WIN7 on 2016/7/16.
 */
public class CommonValues {

    //上下拉刷新参数
    public static int NOT_TO_DO = 0;//不刷新也不加载
    public static int TO_REFRESH = 1;//刷新
    public static int TO_LOAD = 2;//加载

    public static String SP_NAME = "flag";
    //下载路径
    public static String DOWNLOAD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/kokojia";
    //获取读写SD卡权限返回值
    public static int READ_AND_WRITE = 1;

    public static int COARSE_AND_FINE = 2;

    public static String FLAG_UID = "data";
    //sp 下载总数
    public static String SP_DOWNLOAD_VIDEO_TOTAL = "total";
    public static String SP_FILE_NAME = "fileName";
    public static String SP_PLAY = "play";
    //上次播放路径
    public static String LAST_PLAY_PATH = "lastPath";
    //保存视频名称
    public static String FILE_NAME = "fn.txt";
    //保存进度条
    public static String SP_PROGRESS = "progress";
    //使用支付宝充值
    public static String BANK_TYPE_ZFB = "1";
    //保存微信信息
    public static String SP_WX_INFO = "weixin";
    //保存的用户信息
    public static String SP_USER_INFO = "userinfo";
    //微信APPid
    public static String APP_ID_WEIXIN = "wx97281bc0a29739ca";
    //QQ appid
    public static String APP_ID_TENCENT = "100587998";
    public static String SINA_KEY = "2704871145";
    //QQ分享
    public static String SHARE_TITLE = "title";
    public static String SHARE_IMAGE_URL = "imageUrl";
    public static String SHARE_TARGET_URL = "targetUrl";
    public static String SHARE_SUMMARY = "summary";
    public static String SHARE_APP_NAME = "appName";
    //第三方登录的类型
    public static String LOGIN_TYPE_QQ = "1";
    public static String LOGIN_TYPE_SINA = "2";
    public static String LOGIN_TYPE_BAIDU = "3";
    public static String LOGIN_TYPE_RENREN = "4";
    public static String LOGIN_TYPE_WEIXIN = "5";
}
