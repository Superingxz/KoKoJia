package com.nuoxian.kokojia.enterty;

/**
 * Created by Administrator on 2016/9/29.
 */
public class RefundDetails {

    /**
     * id : 47
     * order_id : 2811645
     * user_id : 10058845
     * course_id : 507
     * price : ￥35.00
     * reason : 其他
     * content : 画面不清晰！
     * refuse_content :
     * time : 2016年09月23日 10时54分45秒
     * status : 1
     * title : 【插件专区】《Ghost Town 鬼镇 插件完全自学手册》建筑漫游强力插件视频教程
     * image_url : http://www.kokojia.com/Public/course_image/small/2015-05/5551f0e23477d.jpeg
     * create_time : 2016-09-20 10:43:26
     * student_name : 吾影
     * status_name : 待处理
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private String id;
        private String order_id;
        private String user_id;
        private String course_id;
        private String price;
        private String reason;
        private String content;
        private String refuse_content;
        private String time;
        private String status;
        private String title;
        private String image_url;
        private String create_time;
        private String student_name;
        private String status_name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getOrder_id() {
            return order_id;
        }

        public void setOrder_id(String order_id) {
            this.order_id = order_id;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
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

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getRefuse_content() {
            return refuse_content;
        }

        public void setRefuse_content(String refuse_content) {
            this.refuse_content = refuse_content;
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

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public String getStudent_name() {
            return student_name;
        }

        public void setStudent_name(String student_name) {
            this.student_name = student_name;
        }

        public String getStatus_name() {
            return status_name;
        }

        public void setStatus_name(String status_name) {
            this.status_name = status_name;
        }
    }
}
