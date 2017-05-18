package com.nuoxian.kokojia.enterty;

import java.util.List;

/**
 * Created by Administrator on 2016/9/30.
 */
public class TeacherCoupon {

    /**
     * id : 10059
     * coupon_num : 20
     * coupon_used_num : 0
     * coupon_receive_num : 0
     * coupon_sale : ￥10.00
     * sale_price : 10
     * user_range : 0
     * course_price : 20
     * course_id : 0
     * start_date : 1473523200
     * end_date : 2016-09-20
     * doled : 0
     * coupon_code :
     * coupon_date : 2016-09-30
     * user_id : 1831
     * school_id : 1831
     * create_time : 2016-09-11
     * status : 正常
     * user_range_name : 全部课程(适用于课程价格不低于20元)
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
        private String coupon_num;
        private String coupon_used_num;
        private String coupon_receive_num;
        private String coupon_sale;
        private String sale_price;
        private String user_range;
        private String course_price;
        private String course_id;
        private String start_date;
        private String end_date;
        private String doled;
        private String coupon_code;
        private String coupon_date;
        private String user_id;
        private String school_id;
        private String create_time;
        private String status;
        private String user_range_name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCoupon_num() {
            return coupon_num;
        }

        public void setCoupon_num(String coupon_num) {
            this.coupon_num = coupon_num;
        }

        public String getCoupon_used_num() {
            return coupon_used_num;
        }

        public void setCoupon_used_num(String coupon_used_num) {
            this.coupon_used_num = coupon_used_num;
        }

        public String getCoupon_receive_num() {
            return coupon_receive_num;
        }

        public void setCoupon_receive_num(String coupon_receive_num) {
            this.coupon_receive_num = coupon_receive_num;
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

        public String getStart_date() {
            return start_date;
        }

        public void setStart_date(String start_date) {
            this.start_date = start_date;
        }

        public String getEnd_date() {
            return end_date;
        }

        public void setEnd_date(String end_date) {
            this.end_date = end_date;
        }

        public String getDoled() {
            return doled;
        }

        public void setDoled(String doled) {
            this.doled = doled;
        }

        public String getCoupon_code() {
            return coupon_code;
        }

        public void setCoupon_code(String coupon_code) {
            this.coupon_code = coupon_code;
        }

        public String getCoupon_date() {
            return coupon_date;
        }

        public void setCoupon_date(String coupon_date) {
            this.coupon_date = coupon_date;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getSchool_id() {
            return school_id;
        }

        public void setSchool_id(String school_id) {
            this.school_id = school_id;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getUser_range_name() {
            return user_range_name;
        }

        public void setUser_range_name(String user_range_name) {
            this.user_range_name = user_range_name;
        }
    }
}
