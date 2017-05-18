package com.nuoxian.kokojia.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.jingchen.pulltorefresh.PullToRefreshLayout;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.enterty.LocalVideo;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 常用方法类
 * Created by chensilong on 2016/6/17.
 */
public class CommonMethod {

    /**
     * 跳转页面的方法
     *
     * @param context
     * @param cls
     */
    public static void startActivity(Context context, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        context.startActivity(intent);
    }

    /**
     * 验证手机格式
     */
    public static boolean isMobileNO(String mobiles) {
    /*
    移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
    联通：130、131、132、152、155、156、185、186
    电信：133、153、180、189、（1349卫通）
    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
    */
        String telRegex = "[1][3456789]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobiles)) return false;
        else return mobiles.matches(telRegex);
    }

    //判断email格式是否正确
    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);

        return m.matches();
    }

    /**
     * 验证是否为正数
     *
     * @param number
     * @return
     */
    public static boolean isPositiveDecimal(String number) {
        String num = "^[0-9].*$";
        if (TextUtils.isEmpty(number)) {
            return false;
        } else {
            return number.matches(num);
        }
    }

    /**
     * 隐藏软键盘
     *
     * @param context
     * @param view
     */
    public static void hideInput(Context context, View view) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 显示软键盘
     *
     * @param context
     * @param view
     */
    public static void showInput(Context context, View view) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInputFromInputMethod(view.getWindowToken(), 0);
    }

    /*
    * 获取当前程序的版本号
    */
    public static String getVersionName(Context context) throws Exception {
        //获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        //getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        return packInfo.versionName;
    }

    /**
     * 项目SD卡路径
     */
    public static String KOKOJIA_SD_PATH = Environment.getExternalStorageDirectory() + File.separator + "kokojia";

    /**
     * 创建下载文件的文件夹
     *
     * @param dirName
     */
    public static void makeDownLoadDir(String dirName) {
        File file = new File(KOKOJIA_SD_PATH + File.separator + dirName);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    /**
     * 判断文件是否存在
     *
     * @param path
     * @return
     */
    public static boolean fileIsExists(String path) {
        File file = new File(path);
        return file.exists();
    }

    /**
     * 获取uid
     *
     * @param context
     * @return
     */
    public static String getUid(Context context) {
        SharedPreferences sp = context.getSharedPreferences("flag", context.MODE_PRIVATE);
        return sp.getString("data", "0");
    }

    /**
     * 吐司
     *
     * @param context
     * @param msg
     */
    public static void toast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 加载失败
     *
     * @param context
     */
    public static void loadFailureToast(Context context) {
        Toast.makeText(context, "加载失败，请检查网络!", Toast.LENGTH_SHORT).show();
    }

    /**
     * 上下拉刷新加载失败
     *
     * @param TO_DO
     * @param pullToRefreshLayout
     */
    public static void pullToRefreshFail(int TO_DO, PullToRefreshLayout pullToRefreshLayout) {
        switch (TO_DO) {
            case 0://不需要通知加载和刷新完成

                break;
            case 1://通知刷新完成
                pullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                break;
            case 2://通知加载完成
                pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.FAIL);
                break;

            default:
                break;
        }
    }

    private static ProgressDialog proDialog;
    /**
     * 显示正在加载的dialog
     * @param str
     * @param context
     */
    public static void showLoadingDialog(String str, Context context) {
        proDialog = new ProgressDialog(context);
        proDialog.setCancelable(true);
        proDialog.setMessage(str);
        Window window = proDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = 0.5f;// 透明度
        lp.dimAmount = 0.0f;// 黑暗度
        window.setAttributes(lp);
        proDialog.show();
    }

    /**
     * 隐藏正在加载的dialog
     */
    public static void dismissLoadingDialog(){
        if(proDialog!=null){
            proDialog.dismiss();
        }
    }

    /**
     * md5加密，32位
     * @param sSecret
     * @return
     */
    public static String getMd5Value(String sSecret) {
        try {
            MessageDigest bmd5 = MessageDigest.getInstance("MD5");
            bmd5.update(sSecret.getBytes());
            int i;
            StringBuffer buf = new StringBuffer();
            byte[] b = bmd5.digest();
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            return buf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 显示提示框
     * @param title
     * @param content
     * @param context
     */
    public static void showAlerDialog(String title,String content,Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(content);
        final AlertDialog dialog = builder.create();
        dialog.show();
        //3秒后弹窗消失
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dialog.isShowing()){
                    dialog.dismiss();
                }
            }
        },3000);
    }

    /**
     * 上下拉刷新加载成功
     *
     * @param TO_DO
     * @param pullToRefreshLayout
     */
    public static void pullToRefreshSuccess(int TO_DO, PullToRefreshLayout pullToRefreshLayout) {
        switch (TO_DO) {
            case 0://不需要通知加载和刷新完成
                break;
            case 1://通知刷新完成
                pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                break;
            case 2://通知加载完成
                pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                break;
            default:
                break;
        }
    }

    /**
     * 设置标题栏颜色
     * @param activity
     * @param color
     */
    public static void setTitleBarBackground(Activity activity,int color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            CommonMethod.setTranslucentStatus(true,activity);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(activity);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(color);
    }

    public static void setTranslucentStatus(boolean on,Activity activity) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    /**
     * 是否连接WIFI
     *
     * @param context
     * @return
     */
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetworkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    /**
     * 下载
     *
     * @param url
     * @param targetPath
     */
    public static void downLoad(String url, String targetPath) {
        HttpUtils http = new HttpUtils();
        HttpHandler handler = http.download(url, targetPath,
                true,// 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
                true, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
                new RequestCallBack<File>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {
                        super.onLoading(total, current, isUploading);
                    }

                    @Override
                    public void onSuccess(ResponseInfo<File> responseInfo) {
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                    }
                });
    }

    /**
     * 加载本地图片
     *
     * @param url
     * @return
     */
    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 保存视频名称
     *
     * @param path
     * @param content
     * @throws IOException
     */
    public static void saveVideoName(String path, String content) throws IOException {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.write(content);
        bw.flush();
        bw.close();
    }

    /**
     * 获取视频名称
     * @param path
     * @return
     * @throws IOException
     */
    public static String getVideoName(String path) throws IOException {
        String name = "";
        File file = new File(path);
        if (file.exists()) {
            BufferedReader br = new BufferedReader(new FileReader(file));
            name = br.readLine();
            br.close();
        }
        return name;
    }

    /**
     * 设置FONTAWESOME
     * @param view
     * @param context
     */
    public static void setFontAwesome(View view,Context context){
        Typeface typeface = FontManager.getTypeface(context,FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(view,typeface);
    }

    /**
     * 获取视频文件
     * @param path
     * @return
     */
    public static List<LocalVideo> getM3U8Files(String path, Context context) {
        List<LocalVideo> list = new ArrayList<>();
        File file = new File(path);
        File[] lidFiles = file.listFiles();
        for (int i = 0; i < lidFiles.length; i++) {
            if (lidFiles[i].isDirectory()) {
                //如果是目录，就查询目录里面是否有m3u8文件
                File[] files = lidFiles[i].listFiles();
                SharedPreferences sharedPreferences = context.getSharedPreferences(CommonValues.SP_DOWNLOAD_VIDEO_TOTAL, context.MODE_PRIVATE);
                int filesTotal = sharedPreferences.getInt(lidFiles[i].getName(), -1);
                if (filesTotal <= files.length) {
                    video:
                    for (int j = 0; j < files.length; j++) {
                        if (files[j].isFile()) {
                            //判断该文件是否为m3u8文件
                            String fileName = files[j].getName();
                            if ("m3u8".equals(fileName.substring(fileName.lastIndexOf(".") + 1))) {
                                String lid = lidFiles[i].getName();
                                //是m3u8文件，保存文件名和路径
                                LocalVideo localVideo = new LocalVideo();
                                //SharedPreferences sp1 = context.getSharedPreferences(CommonValues.SP_FILE_NAME,context.MODE_PRIVATE);
                                try {
                                    localVideo.setName(getVideoName(path + File.separator + lid + File.separator + CommonValues.FILE_NAME));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                localVideo.setPath(files[j].getAbsolutePath());
                                localVideo.setDirPath(lidFiles[i].getPath());
                                localVideo.setCheck(false);
                                localVideo.setVisible(false);
                                SharedPreferences sp2 = context.getSharedPreferences(CommonValues.SP_PLAY, context.MODE_PRIVATE);
                                if (sp2.getString(CommonValues.LAST_PLAY_PATH, "").equals(files[j].getAbsolutePath())) {
                                    //如果是上次播放的视频
                                    localVideo.setSelect(true);
                                } else {
                                    localVideo.setSelect(false);
                                }
                                list.add(localVideo);
                                break video;
                            }
                        }
                    }
                }
            }
        }
        return list;
    }
}
