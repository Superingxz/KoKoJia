package com.nuoxian.kokojia.enterty;

/**
 * Created by Administrator on 2016/7/11.
 */
public class CourseLesson {

    private String id,title,course_id,level,video_time;
    private int player_status;

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
