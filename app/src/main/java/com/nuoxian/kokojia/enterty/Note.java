package com.nuoxian.kokojia.enterty;

import java.util.List;

/**
 * Created by Administrator on 2016/10/24.
 */
public class Note {

    /**
     * id : 10665
     * user_id : 1
     * course_id : 247
     * course_lesson_id : 14613
     * content : Android
     * time : 1477278114
     * vote_num : 0
     * evaluate_num : 0
     * player_time : 7
     * player_time_format : 00:07
     * show_id : 10000001
     * nickname : 课课
     * student_avatar : http://www.kokojia.com/Public/images/upload/head_1.gif
     * student_nickname : 课课
     */

    private List<MynoteBean> mynote;
    /**
     * id : 10455
     * user_id : 10049096
     * course_id : 247
     * course_lesson_id : 14613
     * content : Android学习路线图：
     1.java基础知识：集合、线程、和IO流主要掌握
     2.web开发:servlet和Jsp和Html相关内容
     3.服务器端的开发：jdbc使用结合mysql的使用
     4.Android的入门介绍
     5.Android常用布局
     6..Android常用UI控件:ListView、Fragment
     7.Android文件操作包括数据库等
     8.Android网络协议部分。包含json和xml的数据解析
     9.Android输入事件、菜单、对话框和通知等等
     10.关于Android的UI的美化
     11.Andriod自定义的 控件等等
     12.Andriod的传感器开发等等
     13.Android的地图开发等
     14.Android线程和异步任务开发等
     * time : 1463967756
     * vote_num : 0
     * evaluate_num : 0
     * player_time : 264
     * player_time_format : 04:24
     * show_id : 10049096
     * nickname : 1314社会复杂
     * student_avatar : http://www.kokojia.com/Public/images/upload/head_10049096.jpeg
     * student_nickname : 1314社会复杂
     */

    private List<CoursenoteBean> coursenote;

    public List<MynoteBean> getMynote() {
        return mynote;
    }

    public void setMynote(List<MynoteBean> mynote) {
        this.mynote = mynote;
    }

    public List<CoursenoteBean> getCoursenote() {
        return coursenote;
    }

    public void setCoursenote(List<CoursenoteBean> coursenote) {
        this.coursenote = coursenote;
    }

    public static class MynoteBean {
        private String id;
        private String user_id;
        private String course_id;
        private String course_lesson_id;
        private String content;
        private String time;
        private String vote_num;
        private String evaluate_num;
        private String player_time;
        private String player_time_format;
        private String show_id;
        private String nickname;
        private String student_avatar;
        private String student_nickname;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getCourse_id() {
            return course_id;
        }

        public void setCourse_id(String course_id) {
            this.course_id = course_id;
        }

        public String getCourse_lesson_id() {
            return course_lesson_id;
        }

        public void setCourse_lesson_id(String course_lesson_id) {
            this.course_lesson_id = course_lesson_id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getVote_num() {
            return vote_num;
        }

        public void setVote_num(String vote_num) {
            this.vote_num = vote_num;
        }

        public String getEvaluate_num() {
            return evaluate_num;
        }

        public void setEvaluate_num(String evaluate_num) {
            this.evaluate_num = evaluate_num;
        }

        public String getPlayer_time() {
            return player_time;
        }

        public void setPlayer_time(String player_time) {
            this.player_time = player_time;
        }

        public String getPlayer_time_format() {
            return player_time_format;
        }

        public void setPlayer_time_format(String player_time_format) {
            this.player_time_format = player_time_format;
        }

        public String getShow_id() {
            return show_id;
        }

        public void setShow_id(String show_id) {
            this.show_id = show_id;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getStudent_avatar() {
            return student_avatar;
        }

        public void setStudent_avatar(String student_avatar) {
            this.student_avatar = student_avatar;
        }

        public String getStudent_nickname() {
            return student_nickname;
        }

        public void setStudent_nickname(String student_nickname) {
            this.student_nickname = student_nickname;
        }
    }

    public static class CoursenoteBean {
        private String id;
        private String user_id;
        private String course_id;
        private String course_lesson_id;
        private String content;
        private String time;
        private String vote_num;
        private String evaluate_num;
        private String player_time;
        private String player_time_format;
        private String show_id;
        private String nickname;
        private String student_avatar;
        private String student_nickname;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getCourse_id() {
            return course_id;
        }

        public void setCourse_id(String course_id) {
            this.course_id = course_id;
        }

        public String getCourse_lesson_id() {
            return course_lesson_id;
        }

        public void setCourse_lesson_id(String course_lesson_id) {
            this.course_lesson_id = course_lesson_id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getVote_num() {
            return vote_num;
        }

        public void setVote_num(String vote_num) {
            this.vote_num = vote_num;
        }

        public String getEvaluate_num() {
            return evaluate_num;
        }

        public void setEvaluate_num(String evaluate_num) {
            this.evaluate_num = evaluate_num;
        }

        public String getPlayer_time() {
            return player_time;
        }

        public void setPlayer_time(String player_time) {
            this.player_time = player_time;
        }

        public String getPlayer_time_format() {
            return player_time_format;
        }

        public void setPlayer_time_format(String player_time_format) {
            this.player_time_format = player_time_format;
        }

        public String getShow_id() {
            return show_id;
        }

        public void setShow_id(String show_id) {
            this.show_id = show_id;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getStudent_avatar() {
            return student_avatar;
        }

        public void setStudent_avatar(String student_avatar) {
            this.student_avatar = student_avatar;
        }

        public String getStudent_nickname() {
            return student_nickname;
        }

        public void setStudent_nickname(String student_nickname) {
            this.student_nickname = student_nickname;
        }
    }
}
