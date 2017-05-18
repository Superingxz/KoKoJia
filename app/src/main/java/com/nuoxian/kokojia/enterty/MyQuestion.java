package com.nuoxian.kokojia.enterty;

import java.util.List;

/**
 * Created by Administrator on 2016/9/26.
 * 我的提问
 */
public class MyQuestion {

    /**
     * course_id : 3641
     * course_lesson_id : 0
     * content : 测试，请不用管我
     * time : 2016-09-26 11:42
     * vote_num : 0
     * evaluate_num : 0
     * course_title : 8节课教你学会H5制作视频教程
     * lesson_title :
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
        private String course_lesson_id;
        private String content;
        private String time;
        private String vote_num;
        private String evaluate_num;
        private String course_title;
        private String lesson_title;

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

        public String getCourse_title() {
            return course_title;
        }

        public void setCourse_title(String course_title) {
            this.course_title = course_title;
        }

        public String getLesson_title() {
            return lesson_title;
        }

        public void setLesson_title(String lesson_title) {
            this.lesson_title = lesson_title;
        }
    }
}
