package com.nuoxian.kokojia.enterty;

import java.util.List;

/**
 * Created by Administrator on 2016/10/13.
 */
public class RefundRecord {

    /**
     * id : 46
     * order_id : 2810672
     * course_id : 3169
     * price : 88
     * user_id : 10058049
     * school_id : 10033725
     * reason : 2
     * content : 课程不完整
     * refuse_content :
     * annex :
     * ip : 119.130.230.240
     * time : 1473388624
     * proces_time : 1970/01/01 08:00:00
     * is_agree : 0
     * status : 2
     * admin_id : 0
     * remarks :
     * dispose_time : 0
     * refund_notice : 0
     * title : C++从基础到项目实战视频教程
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
        private String course_id;
        private String price;
        private String content;
        private String time;
        private String proces_time;
        private String status;
        private String title;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCourse_id() {
            return course_id;
        }

        public void setCourse_id(String course_id) {
            this.course_id = course_id;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getProces_time() {
            return proces_time;
        }

        public void setProces_time(String proces_time) {
            this.proces_time = proces_time;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
