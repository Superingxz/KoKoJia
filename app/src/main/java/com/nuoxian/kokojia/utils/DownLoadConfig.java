package com.nuoxian.kokojia.utils;

import com.lidroid.xutils.http.HttpHandler;
import com.nuoxian.kokojia.enterty.Download;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/15.
 */

public class DownLoadConfig {
    public static Map<Integer, List<HttpHandler>> mDownLoadHandlerMap = new HashMap<>();
    public static List<Download> downloadList = new ArrayList<>();

    /**
     *
     * @param position  当前下载的item在listview中的位置
     * @param id   Course 的id
     * @param uid 用户id
     * @return
     */
    public static Download findDownLoadByPositionAndCourseId(int position,String id,String uid) {
        if (DownLoadConfig.downloadList != null && DownLoadConfig.downloadList.size() > 0) {
            for (int i = 0; i < DownLoadConfig.downloadList.size(); i++) {
                Download download = DownLoadConfig.downloadList.get(i);
                if (download.getPosition() == position
                        && id.equals(download.getId())
                        &&uid.equals(download.getUid())) {
                    return download;
                }
            }
        }
        return null;
    }

    /**
     *
     * @param position  当前下载的item在listview中的位置
     * @param id   Course 的id
     * @param uid 用户id
     * @return
     */
    public static void RemoveDownLoadByPositionAndCourseId(int position,String id,String uid) {
        if (DownLoadConfig.downloadList != null && DownLoadConfig.downloadList.size() > 0) {
            for (int i = 0; i < DownLoadConfig.downloadList.size(); i++) {
                Download download = DownLoadConfig.downloadList.get(i);
                if (download.getPosition() == position
                        && id.equals(download.getId())
                        &&uid.equals(download.getUid())) {
                    DownLoadConfig.downloadList.remove(i);
                }
            }
        }
    }
}
