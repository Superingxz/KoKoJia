package com.nuoxian.kokojia.enterty;

import java.util.List;

/**
 * Created by Administrator on 2016/10/14.
 */
public class FreeCourse {

    /**
     * id : 1292
     * title : java教程基础300集_史上最全最深最细java视频教程(1-98集)
     * price : 免费
     * image_url : http://www.kokojia.com/Public/course_image/small/2016-02/56c68448a0b2d.jpeg
     * trial_num : 52242
     * class_num : 98
     */

    private List<CourseListBean> course_list;

    public List<CourseListBean> getCourse_list() {
        return course_list;
    }

    public void setCourse_list(List<CourseListBean> course_list) {
        this.course_list = course_list;
    }

    public static class CourseListBean {
        private String id;
        private String title;
        private String price;
        private String image_url;
        private String trial_num;
        private String class_num;

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

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getImage_url() {
            return image_url;
        }

        public void setImage_url(String image_url) {
            this.image_url = image_url;
        }

        public String getTrial_num() {
            return trial_num;
        }

        public void setTrial_num(String trial_num) {
            this.trial_num = trial_num;
        }

        public String getClass_num() {
            return class_num;
        }

        public void setClass_num(String class_num) {
            this.class_num = class_num;
        }
    }
}
