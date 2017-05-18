package com.nuoxian.kokojia.enterty;

import java.util.List;

/**
 * Created by Administrator on 2016/9/19.
 */
public class AnswerDetails {

    /**
     * id : 621
     * user_id : 10034821
     * content : 二级MS_OFFICE操作练习、无纸化真题手册—总和本.pdf打不开，其他文件都可以打开
     * image_url :
     * vote_num : 0
     * time : 1474205525
     * student_nickname : 517763840
     * student_avatar : http://www.kokojia.comhttp://q.qlogo.cn/qqapp/100587998/53761FC5014DAD337CF378E8D59DC773/100
     */

    private DataBean data;
    /**
     * user_id : 10034821
     * reply_id : 10011091
     * content : 用PDF阅读器打开，密码是www.zzhstudio.cn
     * time : 2016-09-18 21:39
     * nickname : 钟老师
     * student_avatar : http://www.kokojia.com/Public/images/upload/headnew_10011091.gif
     * student_nickname : 钟老师
     * reply_name : 517***0
     */

    private List<CommentlistBean> commentlist;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public List<CommentlistBean> getCommentlist() {
        return commentlist;
    }

    public void setCommentlist(List<CommentlistBean> commentlist) {
        this.commentlist = commentlist;
    }

    public static class DataBean {
        private String id;
        private String user_id;
        private String content;
        private String image_url;
        private String vote_num;
        private String time;
        private String student_nickname;
        private String student_avatar;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getImage_url() {
            return image_url;
        }

        public void setImage_url(String image_url) {
            this.image_url = image_url;
        }

        public String getVote_num() {
            return vote_num;
        }

        public void setVote_num(String vote_num) {
            this.vote_num = vote_num;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getStudent_nickname() {
            return student_nickname;
        }

        public void setStudent_nickname(String student_nickname) {
            this.student_nickname = student_nickname;
        }

        public String getStudent_avatar() {
            return student_avatar;
        }

        public void setStudent_avatar(String student_avatar) {
            this.student_avatar = student_avatar;
        }
    }

    public static class CommentlistBean {
        private String user_id;
        private String reply_id;
        private String content;
        private String time;
        private String nickname;
        private String student_avatar;
        private String student_nickname;
        private String reply_name;

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getReply_id() {
            return reply_id;
        }

        public void setReply_id(String reply_id) {
            this.reply_id = reply_id;
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

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getStudent_avatar() {
            return student_avatar;
        }

        public void setStudent_avatar(String student_avatar) {
            this.student_avatar = student_avatar;
        }

        public String getStudent_nickname() {
            return student_nickname;
        }

        public void setStudent_nickname(String student_nickname) {
            this.student_nickname = student_nickname;
        }

        public String getReply_name() {
            return reply_name;
        }

        public void setReply_name(String reply_name) {
            this.reply_name = reply_name;
        }
    }
}
