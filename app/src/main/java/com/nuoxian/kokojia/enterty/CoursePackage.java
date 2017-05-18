package com.nuoxian.kokojia.enterty;

import java.util.List;

/**
 * Created by Administrator on 2016/10/17.
 */
public class CoursePackage {

    /**
     * id : 1
     * title : 2016软考网络工程师顺利通关套餐（最新、最全）
     * image_url : http://www.kokojia.com/Public/course_image/big/2016-08/579f2227b0e84.jpeg
     * price : ￥710.00
     * original_price : ￥1015.00
     * course_count : 5
     * class_num : 252
     */

    private List<PackageListBean> package_list;

    public List<PackageListBean> getPackage_list() {
        return package_list;
    }

    public void setPackage_list(List<PackageListBean> package_list) {
        this.package_list = package_list;
    }

    public static class PackageListBean {
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
