package com.nuoxian.kokojia.enterty;

import java.util.List;

/**
 * Created by Administrator on 2016/9/5.
 */
public class SellOrder {

    /**
     * status : 1
     * data : [{"id":"2807562","type":"3","price":"￥1.00","create_time":"1471333678","status":"交易完成","course_id":"1","time":"2016-08-16","image_url":"http://www.kokojia.com/Public/material_image/small/2016-08/57b28440014cc.jpeg","course_title":"落地灯具系列素材"},{"id":"6603","type":"1","price":"￥0.01","create_time":"1429497300","status":"已关闭","course_id":"1595","time":"2015-04-20","image_url":"http://www.kokojia.com/Public/course_image/small/2014-08/53e18634e4629.jpeg","course_title":"Q版道具 视频教程"},{"id":"6170","type":"1","price":"免费","create_time":"1407489735","status":"交易完成","course_id":"1466","time":"2014-08-08","image_url":"http://www.kokojia.com/Public/course_image/small/2014-07/53c8c49cdf1a4.jpeg","course_title":"直播测试-免费课程视频"},{"id":"6169","type":"1","price":"免费","create_time":"1407489577","status":"交易完成","course_id":"1466","time":"2014-08-08","image_url":"http://www.kokojia.com/Public/course_image/small/2014-07/53c8c49cdf1a4.jpeg","course_title":"直播测试-免费课程视频"},{"id":"6168","type":"1","price":"免费","create_time":"1407488640","status":"交易完成","course_id":"1466","time":"2014-08-08","image_url":"http://www.kokojia.com/Public/course_image/small/2014-07/53c8c49cdf1a4.jpeg","course_title":"直播测试-免费课程视频"},{"id":"6167","type":"1","price":"免费","create_time":"1407488425","status":"交易完成","course_id":"1466","time":"2014-08-08","image_url":"http://www.kokojia.com/Public/course_image/small/2014-07/53c8c49cdf1a4.jpeg","course_title":"直播测试-免费课程视频"},{"id":"6164","type":"1","price":"免费","create_time":"1407485148","status":"交易完成","course_id":"1466","time":"2014-08-08","image_url":"http://www.kokojia.com/Public/course_image/small/2014-07/53c8c49cdf1a4.jpeg","course_title":"直播测试-免费课程视频"},{"id":"5701","type":"1","price":"免费","create_time":"1407392801","status":"交易完成","course_id":"1466","time":"2014-08-07","image_url":"http://www.kokojia.com/Public/course_image/small/2014-07/53c8c49cdf1a4.jpeg","course_title":"直播测试-免费课程视频"},{"id":"5700","type":"1","price":"免费","create_time":"1407391847","status":"交易完成","course_id":"1466","time":"2014-08-07","image_url":"http://www.kokojia.com/Public/course_image/small/2014-07/53c8c49cdf1a4.jpeg","course_title":"直播测试-免费课程视频"},{"id":"4422","type":"1","price":"免费","create_time":"1406798218","status":"交易完成","course_id":"1466","time":"2014-07-31","image_url":"http://www.kokojia.com/Public/course_image/small/2014-07/53c8c49cdf1a4.jpeg","course_title":"直播测试-免费课程视频"}]
     * pages : 7
     */

    private int pages;
    /**
     * id : 2807562
     * type : 3
     * price : ￥1.00
     * create_time : 1471333678
     * status : 交易完成
     * course_id : 1
     * time : 2016-08-16
     * image_url : http://www.kokojia.com/Public/material_image/small/2016-08/57b28440014cc.jpeg
     * course_title : 落地灯具系列素材
     */

    private List<DataBean> data;

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

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
        private String create_time;
        private String status;
        private String course_id;
        private String time;
        private String image_url;
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

        public String getCourse_id() {
            return course_id;
        }

        public void setCourse_id(String course_id) {
            this.course_id = course_id;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getImage_url() {
            return image_url;
        }

        public void setImage_url(String image_url) {
            this.image_url = image_url;
        }

        public String getCourse_title() {
            return course_title;
        }

        public void setCourse_title(String course_title) {
            this.course_title = course_title;
        }
    }
}
