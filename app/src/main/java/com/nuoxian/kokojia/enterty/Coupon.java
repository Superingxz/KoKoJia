package com.nuoxian.kokojia.enterty;

import java.util.List;

/**
 * Created by Administrator on 2016/10/20.
 */
public class Coupon {

    /**
     * id : 10071
     * coupon_sale : 立减
     * sale_price : 1
     * user_range : 1
     * course_price : 0
     * course_id : 2926
     * end_date : 2016年10月28日 23时59分
     * coupon_date : 1477929599
     * coupon_num : 100
     * coupon_receive_num : 0
     * coupon_status : 已领完
     * course_title : 二周学会MySQL视频教程
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
        private String coupon_sale;
        private String sale_price;
        private String user_range;
        private String course_price;
        private String course_id;
        private String end_date;
        private String coupon_date;
        private String coupon_num;
        private String coupon_receive_num;
        private String coupon_status;
        private String coupon_name;
        private String course_title;

        public String getCoupon_name() {
            return coupon_name;
        }

        public void setCoupon_name(String coupon_name) {
            this.coupon_name = coupon_name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCoupon_sale() {
            return coupon_sale;
        }

        public void setCoupon_sale(String coupon_sale) {
            this.coupon_sale = coupon_sale;
        }

        public String getSale_price() {
            return sale_price;
        }

        public void setSale_price(String sale_price) {
            this.sale_price = sale_price;
        }

        public String getUser_range() {
            return user_range;
        }

        public void setUser_range(String user_range) {
            this.user_range = user_range;
        }

        public String getCourse_price() {
            return course_price;
        }

        public void setCourse_price(String course_price) {
            this.course_price = course_price;
        }

        public String getCourse_id() {
            return course_id;
        }

        public void setCourse_id(String course_id) {
            this.course_id = course_id;
        }

        public String getEnd_date() {
            return end_date;
        }

        public void setEnd_date(String end_date) {
            this.end_date = end_date;
        }

        public String getCoupon_date() {
            return coupon_date;
        }

        public void setCoupon_date(String coupon_date) {
            this.coupon_date = coupon_date;
        }

        public String getCoupon_num() {
            return coupon_num;
        }

        public void setCoupon_num(String coupon_num) {
            this.coupon_num = coupon_num;
        }

        public String getCoupon_receive_num() {
            return coupon_receive_num;
        }

        public void setCoupon_receive_num(String coupon_receive_num) {
            this.coupon_receive_num = coupon_receive_num;
        }

        public String getCoupon_status() {
            return coupon_status;
        }

        public void setCoupon_status(String coupon_status) {
            this.coupon_status = coupon_status;
        }

        public String getCourse_title() {
            return course_title;
        }

        public void setCourse_title(String course_title) {
            this.course_title = course_title;
        }
    }
}
