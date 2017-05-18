package com.nuoxian.kokojia.enterty;

import java.util.List;

/**
 * Created by  陈思龙 on 2016/6/30.
 */
public class CourseCatalog {

    /**
     * course_total : 81
     * course_type_id : 3
     * course_type_name : 编程语言
     * type_two : [{"id":"201","name":"Java","seoname":"java","seo_title":"","seo_keyword":"","seo_desc":"","have_child":"0","father_id":"3","level":"3","position":"1","is_show":"1","type_color":"0"},{"id":"202","name":"C/C++","seoname":"c","seo_title":"","seo_keyword":"","seo_desc":"","have_child":"0","father_id":"3","level":"3","position":"2","is_show":"1","type_color":"0"},{"id":"203","name":"Python","seoname":"python","seo_title":"","seo_keyword":"","seo_desc":"","have_child":"0","father_id":"3","level":"3","position":"3","is_show":"1","type_color":"0"},{"id":"208","name":"VB","seoname":"vb","seo_title":"","seo_keyword":"","seo_desc":"","have_child":"0","father_id":"3","level":"3","position":"8","is_show":"1","type_color":"0"},{"id":"210","name":"其他","seoname":"bianchengqita","seo_title":"","seo_keyword":"","seo_desc":"","have_child":"0","father_id":"3","level":"3","position":"10","is_show":"1","type_color":"0"}]
     */

    private String course_total;
    private String course_type_id;
    private String course_type_name;
    private String icon_font;
    private String icon_color;
    /**
     * id : 201
     * name : Java
     * seoname : java
     * seo_title :
     * seo_keyword :
     * seo_desc :
     * have_child : 0
     * father_id : 3
     * level : 3
     * position : 1
     * is_show : 1
     * type_color : 0
     */

    private List<TypeTwoBean> type_two;

    public String getCourse_total() {
        return course_total;
    }

    public void setCourse_total(String course_total) {
        this.course_total = course_total;
    }

    public String getCourse_type_id() {
        return course_type_id;
    }

    public void setCourse_type_id(String course_type_id) {
        this.course_type_id = course_type_id;
    }

    public String getCourse_type_name() {
        return course_type_name;
    }

    public String getIcon_font() {
        return icon_font;
    }

    public void setIcon_font(String icon_font) {
        this.icon_font = icon_font;
    }

    public String getIcon_color() {
        return icon_color;
    }

    public void setIcon_color(String icon_color) {
        this.icon_color = icon_color;
    }

    public void setCourse_type_name(String course_type_name) {
        this.course_type_name = course_type_name;
    }

    public List<TypeTwoBean> getType_two() {
        return type_two;
    }

    public void setType_two(List<TypeTwoBean> type_two) {
        this.type_two = type_two;
    }

    public static class TypeTwoBean {
        private String id;
        private String name;
        private String seoname;
        private String seo_title;
        private String seo_keyword;
        private String seo_desc;
        private String have_child;
        private String father_id;
        private String level;
        private String position;
        private String is_show;
        private String type_color;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSeoname() {
            return seoname;
        }

        public void setSeoname(String seoname) {
            this.seoname = seoname;
        }

        public String getSeo_title() {
            return seo_title;
        }

        public void setSeo_title(String seo_title) {
            this.seo_title = seo_title;
        }

        public String getSeo_keyword() {
            return seo_keyword;
        }

        public void setSeo_keyword(String seo_keyword) {
            this.seo_keyword = seo_keyword;
        }

        public String getSeo_desc() {
            return seo_desc;
        }

        public void setSeo_desc(String seo_desc) {
            this.seo_desc = seo_desc;
        }

        public String getHave_child() {
            return have_child;
        }

        public void setHave_child(String have_child) {
            this.have_child = have_child;
        }

        public String getFather_id() {
            return father_id;
        }

        public void setFather_id(String father_id) {
            this.father_id = father_id;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public String getIs_show() {
            return is_show;
        }

        public void setIs_show(String is_show) {
            this.is_show = is_show;
        }

        public String getType_color() {
            return type_color;
        }

        public void setType_color(String type_color) {
            this.type_color = type_color;
        }
    }
}
