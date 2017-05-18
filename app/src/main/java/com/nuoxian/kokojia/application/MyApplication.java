package com.nuoxian.kokojia.application;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.widget.RadioGroup;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.baidu.api.Baidu;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.nuoxian.kokojia.R;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.tauth.Tencent;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2016/6/29.
 */
public class MyApplication extends Application {

    private static MyApplication app;
    private static RequestQueue queue;
    public static ImageLoader imageLoader = ImageLoader.getInstance();
    public static DisplayImageOptions options;
//    public static RadioGroup rg;
//    public static List<Fragment> fragments;
    public static List<Map<String,List<Integer>>> downLoadPosition;
    public static Map<String,List<Integer>> positionMap;
    public static Tencent mTencent;
    public static IWXAPI wx;
    public static Baidu baidu;

    @Override
    public void onCreate() {
        super.onCreate();

//        fragments = new ArrayList<>();
        downLoadPosition = new ArrayList<>();
        positionMap = new HashMap<>();

        this.app = this;
        queue = Volley.newRequestQueue(this);

        initImageLoader(getApplicationContext());
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.imgloading) //设置图片在下载期间显示的图片
                .showImageForEmptyUri(R.mipmap.imgloadfail)//设置图片Uri为空或是错误的时候显示的图片
                .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                .cacheOnDisc(true)//设置下载的图片是否缓存在SD卡中
                .build();
    }

    public static RequestQueue getRequestQueue(){
        return queue;
    }

    public static void initImageLoader(Context context) {
        File cacheDir = StorageUtils.getOwnCacheDirectory(app, "/kokojia/Cache");
        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(context)
                .memoryCacheExtraOptions(480, 800) //即保存的每个缓存文件的最大长宽
                .threadPoolSize(3)//线程池内加载的数量
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) //你可以通过自己的内存缓存实现
                .memoryCacheSize(2 * 1024 * 1024)
                .discCacheSize(50 * 1024 * 1024)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .discCacheFileCount(100) //缓存的文件数量
                .discCache(new UnlimitedDiscCache(cacheDir))//自定义缓存路径
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .imageDownloader(new BaseImageDownloader(context, 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间
                .writeDebugLogs() // Remove for release app
                .build();//开始构建
        // 初始化
        ImageLoader.getInstance().init(config);

    }

}
