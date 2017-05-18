package com.nuoxian.kokojia.enterty;

import java.util.List;

/**
 * Created by Administrator on 2016/10/14.
 */
public class LimitTimeDiscount {

    /**
     * id : 2337
     * title : 最全面2017年3月专用计算机《二级MS_OFFICE高级应用》培训全套—钟老师视频教程
     * price : ￥60.00
     * image_url : http://www.kokojia.com/Public/course_image/small/2016-09/57eb91b39832d.jpeg
     * trial_num : 31907
     * rate : 5
     * school_id : 10011091
     * course_type : 1
     * type_name : 等级考试
     * school_name : 钟老师课堂
     * discount_price : ￥80.00
     * coupon : 0
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
        private String image_url;
        private String trial_num;
        private String course_type;
        private String discount_price;

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

        public String getCourse_type() {
            return course_type;
        }

        public void setCourse_type(String course_type) {
            this.course_type = course_type;
        }

        public String getDiscount_price() {
            return discount_price;
        }

        public void setDiscount_price(String discount_price) {
            this.discount_price = discount_price;
        }
    }
}
