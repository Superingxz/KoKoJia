package com.nuoxian.kokojia.enterty;

/**
 * Created by Administrator on 2016/7/11.
 */
public class CourseLessonDownload {

    private String id,title,course_id,level,video_time;
    private int player_status;
    private int CheckedStatus;//选择的状态
    private int downLoadStatus;//下载的状态

    public static int CHECKED = 0;//选中了
    public static int UNCHECKED = 1;//没选中
    public static int CAN_NOT_CHECKED = 2;//不能选中

    public static int NO_DOWNLOAD = 0;//没有下载
    public static int PRAPER_DOWNLOAD = 1;//准备下载
    public static int DOWNLOADING = 3;//正在下载
    public static int DOWNLOADDED = 4;//下载完了
    public static int UNFINISH  = 5; //未完成
    public static int PAUSE = 6;//暂停

    public int getDownLoadStatus() {
        return downLoadStatus;
    }

    public void setDownLoadStatus(int downLoadStatus) {
        this.downLoadStatus = downLoadStatus;
    }

    public int getCheckedStatus() {
        return CheckedStatus;
    }

    public void setCheckedStatus(int checkedStatus) {
        CheckedStatus = checkedStatus;
    }

    public String getVideo_time() {
        return video_time;
    }

    public void setVideo_time(String video_time) {
        this.video_time = video_time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCourse_id() {
        return course_id;
    }

    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getPlayer_status() {
        return player_status;
    }

    public void setPlayer_status(int player_status) {
        this.player_status = player_status;
    }
}
