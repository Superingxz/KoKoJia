package com.nuoxian.kokojia.enterty;

import com.lidroid.xutils.http.HttpHandler;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/5/16.
 */

public class Download implements Serializable{
    private String url;
    private  String id;  //Course的id
    private String uid;  //用户id
    private String lid; //课程id
    private int position; //当前下载的item在listview中的位置
    private int currentDownload;//第几个下载任务
    private List<HttpHandler> httpHandlerList;

    public Download() {
    }

    public Download(String url, String id, String uid, String lid, int position, int currentDownload, List<HttpHandler> httpHandlerList) {
        this.url = url;
        this.id = id;
        this.uid = uid;
        this.lid = lid;
        this.position = position;
        this.currentDownload = currentDownload;
        this.httpHandlerList = httpHandlerList;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLid() {
        return lid;
    }

    public void setLid(String lid) {
        this.lid = lid;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getCurrentDownload() {
        return currentDownload;
    }

    public void setCurrentDownload(int currentDownload) {
        this.currentDownload = currentDownload;
    }

    public List<HttpHandler> getHttpHandlerList() {
        return httpHandlerList;
    }

    public void setHttpHandlerList(List<HttpHandler> httpHandlerList) {
        this.httpHandlerList = httpHandlerList;
    }
}
