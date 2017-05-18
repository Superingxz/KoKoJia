package com.nuoxian.kokojia.enterty;

import java.util.List;

/**
 * Created by Administrator on 2016/9/30.
 */
public class MyCoupon {

    /**
     * coupon_code : fd9fe261dc581b606e
     * time : 0
     * status : 3
     * is_frozen : 0
     * coupon_sale : 实付￥38.00
     * sale_price : 38
     * user_range : 0
     * course_price : 38
     * course_id : 0
     * coupon_date : 1474473599
     * school_id : 10018836
     * coupon_status : 0
     * date : 2016-09-21 23:59:59
     * used_date :
     * user_range_name : 全部课程
     * status_name : 已过期
     */

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        private String coupon_code;
        private String time;
        private String status;
        private String is_frozen;
        private String coupon_sale;
        private String sale_price;
        private String user_range;
        private String course_price;
        private String course_id;
        private String coupon_date;
        private String school_id;
        private String coupon_status;
        private String date;
        private String used_date;
        private String user_range_name;
        private String status_name;

        public String getCoupon_code() {
            return coupon_code;
        }

        public void setCoupon_code(String coupon_code) {
            this.coupon_code = coupon_code;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getIs_frozen() {
            return is_frozen;
        }

        public void setIs_frozen(String is_frozen) {
            this.is_frozen = is_frozen;
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

        public String getCoupon_date() {
            return coupon_date;
        }

        public void setCoupon_date(String coupon_date) {
            this.coupon_date = coupon_date;
        }

        public String getSchool_id() {
            return school_id;
        }

        public void setSchool_id(String school_id) {
            this.school_id = school_id;
        }

        public String getCoupon_status() {
            return coupon_status;
        }

        public void setCoupon_status(String coupon_status) {
            this.coupon_status = coupon_status;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getUsed_date() {
            return used_date;
        }

        public void setUsed_date(String used_date) {
            this.used_date = used_date;
        }

        public String getUser_range_name() {
            return user_range_name;
        }

        public void setUser_range_name(String user_range_name) {
            this.user_range_name = user_range_name;
        }

        public String getStatus_name() {
            return status_name;
        }

        public void setStatus_name(String status_name) {
            this.status_name = status_name;
        }
    }
}
