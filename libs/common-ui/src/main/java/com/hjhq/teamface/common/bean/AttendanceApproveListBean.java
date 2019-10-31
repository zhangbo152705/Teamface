package com.hjhq.teamface.common.bean;

import com.hjhq.teamface.basis.bean.BaseBean;

import java.util.List;

/**
 * Created by Administrator on 2019/3/6.
 * Describe：
 */

public class AttendanceApproveListBean extends BaseBean {

    /**
     * data : {"dataList":[{"id":"2","chinese_name":"请假","beanName":"bean1539743117977","relevance_status":0,"start_time_field":111,"end_time_field ":222,"create_by ":123456,"create_time ":123456,"modify_by ":123456,"modify_time ":123456,"del_status ":0}]}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private List<DataListBean> dataList;

        public List<DataListBean> getDataList() {
            return dataList;
        }

        public void setDataList(List<DataListBean> dataList) {
            this.dataList = dataList;
        }

        public static class DataListBean {
            /**
             * id : 2
             * chinese_name : 请假
             * beanName : bean1539743117977
             * relevance_status : 0
             * start_time_field : 111
             * end_time_field  : 222
             * create_by  : 123456
             * create_time  : 123456
             * modify_by  : 123456
             * modify_time  : 123456
             * del_status  : 0
             */

            private String id;
            private String relevance_title;
            private String relevance_bean;
            private String relevance_status;
            private String start_time_field;
            private String end_time_field;
            private String create_by;
            private String create_time;
            private String modify_by;
            private String modify_time;
            private String del_status;
            private String icon_color;
            private String icon_type;
            private String icon_url;



            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getChinese_name() {
                return relevance_title;
            }

            public void setChinese_name(String chinese_name) {
                this.relevance_title = chinese_name;
            }

            public String getBeanName() {
                return relevance_bean;
            }

            public void setBeanName(String beanName) {
                this.relevance_bean = beanName;
            }

            public String getRelevance_status() {
                return relevance_status;
            }

            public void setRelevance_status(String relevance_status) {
                this.relevance_status = relevance_status;
            }

            public String getStart_time_field() {
                return start_time_field;
            }

            public void setStart_time_field(String start_time_field) {
                this.start_time_field = start_time_field;
            }

            public String getEnd_time_field() {
                return end_time_field;
            }

            public void setEnd_time_field(String end_time_field) {
                this.end_time_field = end_time_field;
            }

            public String getCreate_by() {
                return create_by;
            }

            public void setCreate_by(String create_by) {
                this.create_by = create_by;
            }

            public String getCreate_time() {
                return create_time;
            }

            public void setCreate_time(String create_time) {
                this.create_time = create_time;
            }

            public String getModify_by() {
                return modify_by;
            }

            public void setModify_by(String modify_by) {
                this.modify_by = modify_by;
            }

            public String getModify_time() {
                return modify_time;
            }

            public void setModify_time(String modify_time) {
                this.modify_time = modify_time;
            }

            public String getDel_status() {
                return del_status;
            }

            public void setDel_status(String del_status) {
                this.del_status = del_status;
            }

            public String getRelevance_title() {
                return relevance_title;
            }

            public String getRelevance_bean() {
                return relevance_bean;
            }

            public void setRelevance_title(String relevance_title) {
                this.relevance_title = relevance_title;
            }

            public void setRelevance_bean(String relevance_bean) {
                this.relevance_bean = relevance_bean;
            }

            public String getIcon_color() {
                return icon_color;
            }

            public String getIcon_type() {
                return icon_type;
            }

            public String getIcon_url() {
                return icon_url;
            }

            public void setIcon_color(String icon_color) {
                this.icon_color = icon_color;
            }

            public void setIcon_type(String icon_type) {
                this.icon_type = icon_type;
            }

            public void setIcon_url(String icon_url) {
                this.icon_url = icon_url;
            }
        }
    }
}
