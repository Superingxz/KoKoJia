package com.nuoxian.kokojia.enterty;

import java.util.List;

/**
 * Created by Administrator on 2016/9/7.
 */
public class MyPackage {

    /**
     * id : 16
     * title : 2016软考网络工程师强化通关套餐
     * price : ￥660.00
     * original_price : ￥995.00
     * image_url : /Public/course_image/small/2016-09/57cb7c2523ed4.jpeg
     * course_count : 21
     * status : 2
     * create_time : 1472953447
     * time : 2016-09-04
     * status_name : 销售中
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
        private String title;
        private String price;
        private String original_price;
        private String image_url;
        private String course_count;
        private String status;
        private String create_time;
        private String time;
        private String status_name;

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

        public String getOriginal_price() {
            return original_price;
        }

        public void setOriginal_price(String original_price) {
            this.original_price = original_price;
        }

        public String getImage_url() {
            return image_url;
        }

        public void setImage_url(String image_url) {
            this.image_url = image_url;
        }

        public String getCourse_count() {
            return course_count;
        }

        public void setCourse_count(String course_count) {
            this.course_count = course_count;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getStatus_name() {
            return status_name;
        }

        public void setStatus_name(String status_name) {
            this.status_name = status_name;
        }
    }
}
