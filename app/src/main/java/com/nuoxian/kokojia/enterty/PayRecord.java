package com.nuoxian.kokojia.enterty;

import java.util.List;

/**
 * Created by Administrator on 2016/10/12.
 */
public class PayRecord {

    /**
     * id : 2807820
     * father_id : 0
     * type : 1
     * price : 1
     * user_money : 0.99
     * create_time : 1471594107
     * pay_time : 1471594118
     * status : 交易完成
     * is_part : 0
     * course_id : 1571
     * course_title : Q版人物表情视频教程
     * user_id : 1
     * school_id : 3996
     * order_sn : 20160819160827114715941071769085833000000000000000
     * ip : 119.131.76.13
     * mobilephone : null
     * transfer_accounts : 1
     * transfer_time : 1472199121
     * grade_rate : 0.6
     * income : 0.6
     * is_comment : 2
     * admin_user_id : 0
     * admin_remark :
     * is_refund_dispose : 0
     * user_coupon_id : 0
     * package_id : 0
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
        private String type;
        private String price;
        private String pay_time;
        private String status;
        private String course_id;
        private String course_title;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getPay_time() {
            return pay_time;
        }

        public void setPay_time(String pay_time) {
            this.pay_time = pay_time;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getCourse_id() {
            return course_id;
        }

        public void setCourse_id(String course_id) {
            this.course_id = course_id;
        }

        public String getCourse_title() {
            return course_title;
        }

        public void setCourse_title(String course_title) {
            this.course_title = course_title;
        }
    }
}
