package com.nuoxian.kokojia.enterty;

import java.util.List;

/**
 * Created by Administrator on 2016/9/19.
 */
public class TeacherAnswer {

    /**
     * id : 621
     * course_id : 2337
     * content : 二级MS_OFFICE操作练习、无纸化真题手册—总和本.pdf打不开，其他文件都可以打开
     * time : 2016-09-18 21:32:05
     * teacher_reply : 2
     * title : 最全面2016年9月专用计算机《二级MS_OFFICE高级应用》培训全套—钟老师视频教程
     * nickname : 517763840
     */

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        private String id;
        private String course_id;
        private String content;
        private String time;
        private String teacher_reply;
        private String title;
        private String nickname;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCourse_id() {
            return course_id;
        }

        public void setCourse_id(String course_id) {
            this.course_id = course_id;
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

        public String getTeacher_reply() {
            return teacher_reply;
        }

        public void setTeacher_reply(String teacher_reply) {
            this.teacher_reply = teacher_reply;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }
    }
}
