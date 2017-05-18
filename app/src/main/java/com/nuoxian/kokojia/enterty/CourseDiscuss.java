package com.nuoxian.kokojia.enterty;

import java.util.List;

/**
 * 课程详情页——讨论
 */
public class CourseDiscuss {

    /**
     * id : 605
     * type : 1
     * user_id : 10058427
     * course_id : 2337
     * course_lesson_id : 27684
     * content : 老师，我找不到下载的资料在哪里呀
     * image_url :
     * time : 2016-09-14 08:46
     * vote_num : 0
     * evaluate_num : 1
     * player_time : 169
     * player_time_format : 02:49
     * teacher_reply : 2
     * crond_status : 0
     * show_id : 10058427
     * nickname : 金乐
     * student_avatar : http://www.kokojia.com/Public/images/user_default/16.jpg
     * lesson_title : 《二级MS_OFFICE高级应用》课程介绍——学前必看 20分钟
     * student_nickname : 金乐
     * reply : [{"id":"360","user_id":"10058427","discuss_id":"605","reply_id":"10011091","content":"就在课程介绍页面上方有一个\u201c资料下载\u201d的图标，点击它就可以看到了哦\n所有练习素材和打印的文档都在这个压缩包里面：\n钟老师课堂《MS_OFFICE高级应用》视频教程－配套学习资料.rar","time":"2016-09-14 09:10","show_id":"10011091","nickname":"钟老师","student_avatar":"http://www.kokojia.com/Public/images/upload/headnew_10011091.gif","reply_show_id":"10058427","student_nickname":"钟老师","reply_name":"金乐"}]
     */

    private List<CourseDiscussBean> course_discuss;

    public List<CourseDiscussBean> getCourse_discuss() {
        return course_discuss;
    }

    public void setCourse_discuss(List<CourseDiscussBean> course_discuss) {
        this.course_discuss = course_discuss;
    }

    public static class CourseDiscussBean {
        private String id;
        private String user_id;
        private String course_id;
        private String course_lesson_id;
        private String content;
        private String image_url;
        private String time;
        private String vote_num;
        private String evaluate_num;
        private String nickname;
        private String student_avatar;
        private String lesson_title;
        /**
         * id : 360
         * user_id : 10058427
         * discuss_id : 605
         * reply_id : 10011091
         * content : 就在课程介绍页面上方有一个“资料下载”的图标，点击它就可以看到了哦
         所有练习素材和打印的文档都在这个压缩包里面：
         钟老师课堂《MS_OFFICE高级应用》视频教程－配套学习资料.rar
         * time : 2016-09-14 09:10
         * show_id : 10011091
         * nickname : 钟老师
         * student_avatar : http://www.kokojia.com/Public/images/upload/headnew_10011091.gif
         * reply_show_id : 10058427
         * student_nickname : 钟老师
         * reply_name : 金乐
         */

        private List<ReplyBean> reply;

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

        public String getCourse_id() {
            return course_id;
        }

        public void setCourse_id(String course_id) {
            this.course_id = course_id;
        }

        public String getCourse_lesson_id() {
            return course_lesson_id;
        }

        public void setCourse_lesson_id(String course_lesson_id) {
            this.course_lesson_id = course_lesson_id;
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

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getVote_num() {
            return vote_num;
        }

        public void setVote_num(String vote_num) {
            this.vote_num = vote_num;
        }

        public String getEvaluate_num() {
            return evaluate_num;
        }

        public void setEvaluate_num(String evaluate_num) {
            this.evaluate_num = evaluate_num;
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

        public String getLesson_title() {
            return lesson_title;
        }

        public void setLesson_title(String lesson_title) {
            this.lesson_title = lesson_title;
        }

        public List<ReplyBean> getReply() {
            return reply;
        }

        public void setReply(List<ReplyBean> reply) {
            this.reply = reply;
        }

        public static class ReplyBean {
            private String id;
            private String user_id;
            private String discuss_id;
            private String reply_id;
            private String content;
            private String time;
            private String nickname;
            private String student_avatar;
            private String reply_name;

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

            public String getDiscuss_id() {
                return discuss_id;
            }

            public void setDiscuss_id(String discuss_id) {
                this.discuss_id = discuss_id;
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

            public String getReply_name() {
                return reply_name;
            }

            public void setReply_name(String reply_name) {
                this.reply_name = reply_name;
            }
        }
    }
}
