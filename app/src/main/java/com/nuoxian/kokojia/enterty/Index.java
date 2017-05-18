package com.nuoxian.kokojia.enterty;

import java.util.List;

/**
 * Created by Administrator on 2016/9/21.
 */
public class Index {

    /**
     * type : 1
     * course_id : 3642
     * img : http://www.kokojia.com/Public/course_image/big/2016-06/577394f6deee7.jpeg
     */

    private List<IndexAdBean> index_ad;
    /**
     * id : 3461
     * title : Photoshop后期快速处理制作视频教程
     * class_num : 8
     * trial_num : 2291
     * course_type : 0
     * img : http://www.kokojia.com/Public/index_flash/5747a5de104da.jpg
     */

    private List<CourseJpBean> course_jp;
    /**
     * id : 3620
     * title : 2016软考网络工程师5套下午真题详解视频课程
     * class_num : 76
     * trial_num : 1890
     * course_type : 0
     * img : http://www.kokojia.com/Public/index_flash/576b3a3fed9a2.jpg
     */

    private List<CourseHotBean> course_hot;
    /**
     * id : 3676
     * title : 2016软考网络规划设计师基础知识教程
     * price : ￥450.00
     * image_url : http://www.kokojia.com/Public/course_image/small/2016-08/57b13c6e5e3ff.jpeg
     * trial_num : 2039
     * class_num : 117
     */

    private List<CourseListBean> course_list;

    public List<IndexAdBean> getIndex_ad() {
        return index_ad;
    }

    public void setIndex_ad(List<IndexAdBean> index_ad) {
        this.index_ad = index_ad;
    }

    public List<CourseJpBean> getCourse_jp() {
        return course_jp;
    }

    public void setCourse_jp(List<CourseJpBean> course_jp) {
        this.course_jp = course_jp;
    }

    public List<CourseHotBean> getCourse_hot() {
        return course_hot;
    }

    public void setCourse_hot(List<CourseHotBean> course_hot) {
        this.course_hot = course_hot;
    }

    public List<CourseListBean> getCourse_list() {
        return course_list;
    }

    public void setCourse_list(List<CourseListBean> course_list) {
        this.course_list = course_list;
    }

    public static class IndexAdBean {
        private String type;
        private String course_id;
        private String img;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getCourse_id() {
            return course_id;
        }

        public void setCourse_id(String course_id) {
            this.course_id = course_id;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }
    }

    public static class CourseJpBean {
        private String id;
        private String title;
        private String class_num;
        private String trial_num;
        private String course_type;
        private String img;

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

        public String getClass_num() {
            return class_num;
        }

        public void setClass_num(String class_num) {
            this.class_num = class_num;
        }

        public String getTrial_num() {
            return trial_num;
        }

        public void setTrial_num(String trial_num) {
            this.trial_num = trial_num;
        }

        public String getCourse_type() {
            return course_type;
        }

        public void setCourse_type(String course_type) {
            this.course_type = course_type;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }
    }

    public static class CourseHotBean {
        private String id;
        private String title;
        private String class_num;
        private String trial_num;
        private String course_type;
        private String img;

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

        public String getClass_num() {
            return class_num;
        }

        public void setClass_num(String class_num) {
            this.class_num = class_num;
        }

        public String getTrial_num() {
            return trial_num;
        }

        public void setTrial_num(String trial_num) {
            this.trial_num = trial_num;
        }

        public String getCourse_type() {
            return course_type;
        }

        public void setCourse_type(String course_type) {
            this.course_type = course_type;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }
    }

    public static class CourseListBean {
        private String id;
        private String title;
        private String price;
        private String image_url;
        private String trial_num;
        private String class_num;
        private String is_paid;
        private String discount_price;
        private String countdown;

        public String getIs_paid() {
            return is_paid;
        }

        public void setIs_paid(String is_paid) {
            this.is_paid = is_paid;
        }

        public String getDiscount_price() {
            return discount_price;
        }

        public void setDiscount_price(String discount_price) {
            this.discount_price = discount_price;
        }

        public String getCountdown() {
            return countdown;
        }

        public void setCountdown(String countdown) {
            this.countdown = countdown;
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
