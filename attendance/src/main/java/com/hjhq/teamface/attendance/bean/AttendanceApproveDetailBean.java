package com.hjhq.teamface.attendance.bean;

import com.hjhq.teamface.basis.bean.BaseBean;

/**
 * Created by Administrator on 2019/3/6.
 * Describe：
 */

public class AttendanceApproveDetailBean extends BaseBean {


    /**
     * data : {"relevance_bean":"bean1547087785927","create_time":1551671418587,"end_time_field":"datetime_modify_time","modify_time":"","del_status":"0","modify_by":"","duration_unit":"0","start_time_field":"datetime_create_time","create_by":18,"chinese_name":"回收延迟申请","relevance_status":"2","duration_field":"number_1547106365612","id":4}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * relevance_bean : bean1547087785927
         * create_time : 1551671418587
         * end_time_field : datetime_modify_time
         * modify_time :
         * del_status : 0
         * modify_by :
         * duration_unit : 0
         * start_time_field : datetime_create_time
         * create_by : 18
         * chinese_name : 回收延迟申请
         * relevance_status : 2
         * duration_field : number_1547106365612
         * id : 4
         */

        private String relevance_bean;
        private String create_time;
        private String end_time_field;
        private String modify_time;
        private String del_status;
        private String modify_by;
        private String duration_unit;
        private String start_time_field;
        private String create_by;
        private String relevance_title;
        private String relevance_status;
        private String duration_field;
        private String id;

        public String getRelevance_bean() {
            return relevance_bean;
        }

        public void setRelevance_bean(String relevance_bean) {
            this.relevance_bean = relevance_bean;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public String getEnd_time_field() {
            return end_time_field;
        }

        public void setEnd_time_field(String end_time_field) {
            this.end_time_field = end_time_field;
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

        public String getModify_by() {
            return modify_by;
        }

        public void setModify_by(String modify_by) {
            this.modify_by = modify_by;
        }

        public String getDuration_unit() {
            return duration_unit;
        }

        public void setDuration_unit(String duration_unit) {
            this.duration_unit = duration_unit;
        }

        public String getStart_time_field() {
            return start_time_field;
        }

        public void setStart_time_field(String start_time_field) {
            this.start_time_field = start_time_field;
        }

        public String getCreate_by() {
            return create_by;
        }

        public void setCreate_by(String create_by) {
            this.create_by = create_by;
        }

        public String getChinese_name() {
            return relevance_title;
        }

        public void setChinese_name(String chinese_name) {
            this.relevance_title = chinese_name;
        }

        public String getRelevance_status() {
            return relevance_status;
        }

        public void setRelevance_status(String relevance_status) {
            this.relevance_status = relevance_status;
        }

        public String getDuration_field() {
            return duration_field;
        }

        public void setDuration_field(String duration_field) {
            this.duration_field = duration_field;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
