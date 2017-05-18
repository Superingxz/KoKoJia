package com.nuoxian.kokojia.enterty;

import java.util.List;

/**
 * Created by Administrator on 2016/9/2.
 */
public class FragmentMyCourse {

    /**
     * id : 3373
     * title : 测试20160125视频教程
     * price : 免费
     * image_url : http://www.kokojia.com/Public/course_image/small/2016-01/56a589b829d82.jpeg
     * class_num : 1
     * status : 5
     * create_time : 1453689277
     * end_date : 0
     * valid_day : 0
     * valid_type : 0
     * endtime : 永久有效
     * status_name : 已停售
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
        private String class_num;
        private String status;
        private String create_time;
        private String endtime;
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

        public String getImage_url() {
            return image_url;
        }

        public void setImage_url(String image_url) {
            this.image_url = image_url;
        }

        public String getClass_num() {
            return class_num;
        }

        public void setClass_num(String class_num) {
            this.class_num = class_num;
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

        public String getEndtime() {
            return endtime;
        }

        public void setEndtime(String endtime) {
            this.endtime = endtime;
        }

        public String getStatus_name() {
            return status_name;
        }

        public void setStatus_name(String status_name) {
            this.status_name = status_name;
        }
    }
}
