package com.nuoxian.kokojia.enterty;

import java.util.List;

/**
 * Created by Administrator on 2016/10/13.
 */
public class AllPackage {

    /**
     * id : 336
     * name : 考试认证
     * seoname : kaoshirenzheng
     * seo_title :
     * seo_keyword :
     * seo_desc :
     * have_child : 1
     * father_id : 2
     * level : 2
     * position : 1
     * is_show : 1
     * type_color : 0
     * icon_font : f12e
     * icon_color : f98181
     */

    private List<CourseTypeBean> course_type;
    /**
     * id : 1
     * title : 2016软考网络工程师顺利通关套餐（最新、最全）
     * image_url : http://www.kokojia.com/Public/course_image/big/2016-08/579f2227b0e84.jpeg
     * price : ￥710.00
     * original_price : ￥1015.00
     * course_count : 5
     * class_num : 252
     */

    private List<CourseListBean> course_list;

    public List<CourseTypeBean> getCourse_type() {
        return course_type;
    }

    public void setCourse_type(List<CourseTypeBean> course_type) {
        this.course_type = course_type;
    }

    public List<CourseListBean> getCourse_list() {
        return course_list;
    }

    public void setCourse_list(List<CourseListBean> course_list) {
        this.course_list = course_list;
    }

    public static class CourseTypeBean {
        private String id;
        private String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class CourseListBean {
        private String id;
        private String title;
        private String image_url;
        private String price;
        private String original_price;
        private String course_count;
        private int class_num;

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

        public String getImage_url() {
            return image_url;
        }

        public void setImage_url(String image_url) {
            this.image_url = image_url;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getOriginal_price() {
            return original_price;
        }

        public void setOriginal_price(String original_price) {
            this.original_price = original_price;
        }

        public String getCourse_count() {
            return course_count;
        }

        public void setCourse_count(String course_count) {
            this.course_count = course_count;
        }

        public int getClass_num() {
            return class_num;
        }

        public void setClass_num(int class_num) {
            this.class_num = class_num;
        }
    }
}
