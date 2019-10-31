package com.hjhq.teamface.attendance.bean;

import com.hjhq.teamface.basis.bean.BaseBean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2019/3/8.
 * Describe：
 */

public class AttendanceInfoBean extends BaseBean implements Serializable {

    /**
     * data : {"attendance_start_time":"","outworker_status":1,"name":"徐兵的排班，不要动","class_info":{"time2_end_limit":0,"modify_time":1552018330867,"time1_end_after_limit":1552050000000,"time2_end":"","del_status":"0","modify_by":31,"time2_end_status":"0","rest1_start":"12:00","total_working_hours":"0","create_by":31,"class_type":"1","class_desc":"11:40-18:00","time1_start":1552016400000,"time3_start":"","time1_end_status":"0","time3_end_limit":0,"id":36,"time3_end":"","time3_end_status":"0","time2_start_limit":0,"create_time":1551950602521,"time1_end_limit":180,"time1_start_limit":30,"time3_start_limit":0,"time1_start_after_limit":1552039200000,"time2_start":"","rest1_end":"13:00","name":"徐兵班次","time1_end":1552039200000},"work_day_list":"[35,36]","attendance_address":[],"face_status":1,"id":70,"attendance_wifi":[],"attendance_type":1}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean implements Serializable{
        /**
         * attendance_start_time :
         * outworker_status : 1
         * name : 徐兵的排班，不要动
         * class_info : {"time2_end_limit":0,"modify_time":1552018330867,"time1_end_after_limit":1552050000000,"time2_end":"","del_status":"0","modify_by":31,"time2_end_status":"0","rest1_start":"12:00","total_working_hours":"0","create_by":31,"class_type":"1","class_desc":"11:40-18:00","time1_start":1552016400000,"time3_start":"","time1_end_status":"0","time3_end_limit":0,"id":36,"time3_end":"","time3_end_status":"0","time2_start_limit":0,"create_time":1551950602521,"time1_end_limit":180,"time1_start_limit":30,"time3_start_limit":0,"time1_start_after_limit":1552039200000,"time2_start":"","rest1_end":"13:00","name":"徐兵班次","time1_end":1552039200000}
         * work_day_list : [35,36]
         * attendance_address : []
         * face_status : 1
         * id : 70
         * attendance_wifi : []
         * attendance_type : 1
         */

        private String attendance_start_time;
        private String outworker_status;
        private String name;
        private ClassInfoBean class_info;
        private String work_day_list;
        private String face_status;
        private String id;
        private String attendance_type;
        private List<AttendanceTypeListBean> attendance_address;
        private List<AttendanceTypeListBean> attendance_wifi;
        private List<AttendanceDataItemBean> clock_record_list;
        private List<AttendanceTypeListBean> relation_module;

        public List<AttendanceDataItemBean> getClock_record_list() {
            return clock_record_list;
        }

        public void setClock_record_list(List<AttendanceDataItemBean> clock_record_list) {
            this.clock_record_list = clock_record_list;
        }

        public String getAttendance_start_time() {
            return attendance_start_time;
        }

        public void setAttendance_start_time(String attendance_start_time) {
            this.attendance_start_time = attendance_start_time;
        }

        public String getOutworker_status() {
            return outworker_status;
        }

        public void setOutworker_status(String outworker_status) {
            this.outworker_status = outworker_status;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public ClassInfoBean getClass_info() {
            return class_info;
        }

        public void setClass_info(ClassInfoBean class_info) {
            this.class_info = class_info;
        }

        public String getWork_day_list() {
            return work_day_list;
        }

        public void setWork_day_list(String work_day_list) {
            this.work_day_list = work_day_list;
        }

        public String getFace_status() {
            return face_status;
        }

        public void setFace_status(String face_status) {
            this.face_status = face_status;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getAttendance_type() {
            return attendance_type;
        }

        public void setAttendance_type(String attendance_type) {
            this.attendance_type = attendance_type;
        }

        public List<AttendanceTypeListBean> getAttendance_address() {
            return attendance_address;
        }

        public void setAttendance_address(List<AttendanceTypeListBean> attendance_address) {
            this.attendance_address = attendance_address;
        }

        public List<AttendanceTypeListBean> getAttendance_wifi() {
            return attendance_wifi;
        }

        public void setAttendance_wifi(List<AttendanceTypeListBean> attendance_wifi) {
            this.attendance_wifi = attendance_wifi;
        }

        public List<AttendanceTypeListBean> getRelation_module() {
            return relation_module;
        }

        public void setRelation_module(List<AttendanceTypeListBean> relation_module) {
            this.relation_module = relation_module;
        }

        public static class ClassInfoBean implements Serializable{
            /**
             * time2_end_limit : 0
             * modify_time : 1552018330867
             * time1_end_after_limit : 1552050000000
             * time2_end :
             * del_status : 0
             * modify_by : 31
             * time2_end_status : 0
             * rest1_start : 12:00
             * total_working_hours : 0
             * create_by : 31
             * class_type : 1
             * class_desc : 11:40-18:00
             * time1_start : 1552016400000
             * time3_start :
             * time1_end_status : 0
             * time3_end_limit : 0
             * id : 36
             * time3_end :
             * time3_end_status : 0
             * time2_start_limit : 0
             * create_time : 1551950602521
             * time1_end_limit : 180
             * time1_start_limit : 30
             * time3_start_limit : 0
             * time1_start_after_limit : 1552039200000
             * time2_start :
             * rest1_end : 13:00
             * name : 徐兵班次
             * time1_end : 1552039200000
             */

            private String time2_end_limit;
            private String modify_time;
            private String time1_start;
            private String time1_end;
            private String time1_end_status;
            private String time1_end_after_limit;
            private String time1_end_limit;
            private String time1_start_limit;
            private String time1_start_after_limit;
            private String time2_end;
            private String del_status;
            private String modify_by;
            private String time2_end_status;
            private String rest1_start;
            private String total_working_hours;
            private String create_by;
            private String class_type;
            private String class_desc;
            private String time3_start;
            private String time3_end_limit;
            private String id;
            private String time3_end;
            private String time3_end_status;
            private String time2_start_limit;
            private String create_time;
            private String time3_start_limit;
            private String time2_start;
            private String rest1_end;
            private String name;

            public String getTime2_end_limit() {
                return time2_end_limit;
            }

            public void setTime2_end_limit(String time2_end_limit) {
                this.time2_end_limit = time2_end_limit;
            }

            public String getModify_time() {
                return modify_time;
            }

            public void setModify_time(String modify_time) {
                this.modify_time = modify_time;
            }

            public String getTime1_end_after_limit() {
                return time1_end_after_limit;
            }

            public void setTime1_end_after_limit(String time1_end_after_limit) {
                this.time1_end_after_limit = time1_end_after_limit;
            }

            public String getTime2_end() {
                return time2_end;
            }

            public void setTime2_end(String time2_end) {
                this.time2_end = time2_end;
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

            public String getTime2_end_status() {
                return time2_end_status;
            }

            public void setTime2_end_status(String time2_end_status) {
                this.time2_end_status = time2_end_status;
            }

            public String getRest1_start() {
                return rest1_start;
            }

            public void setRest1_start(String rest1_start) {
                this.rest1_start = rest1_start;
            }

            public String getTotal_working_hours() {
                return total_working_hours;
            }

            public void setTotal_working_hours(String total_working_hours) {
                this.total_working_hours = total_working_hours;
            }

            public String getCreate_by() {
                return create_by;
            }

            public void setCreate_by(String create_by) {
                this.create_by = create_by;
            }

            public String getClass_type() {
                return class_type;
            }

            public void setClass_type(String class_type) {
                this.class_type = class_type;
            }

            public String getClass_desc() {
                return class_desc;
            }

            public void setClass_desc(String class_desc) {
                this.class_desc = class_desc;
            }

            public String getTime1_start() {
                return time1_start;
            }

            public void setTime1_start(String time1_start) {
                this.time1_start = time1_start;
            }

            public String getTime3_start() {
                return time3_start;
            }

            public void setTime3_start(String time3_start) {
                this.time3_start = time3_start;
            }

            public String getTime1_end_status() {
                return time1_end_status;
            }

            public void setTime1_end_status(String time1_end_status) {
                this.time1_end_status = time1_end_status;
            }

            public String getTime3_end_limit() {
                return time3_end_limit;
            }

            public void setTime3_end_limit(String time3_end_limit) {
                this.time3_end_limit = time3_end_limit;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getTime3_end() {
                return time3_end;
            }

            public void setTime3_end(String time3_end) {
                this.time3_end = time3_end;
            }

            public String getTime3_end_status() {
                return time3_end_status;
            }

            public void setTime3_end_status(String time3_end_status) {
                this.time3_end_status = time3_end_status;
            }

            public String getTime2_start_limit() {
                return time2_start_limit;
            }

            public void setTime2_start_limit(String time2_start_limit) {
                this.time2_start_limit = time2_start_limit;
            }

            public String getCreate_time() {
                return create_time;
            }

            public void setCreate_time(String create_time) {
                this.create_time = create_time;
            }

            public String getTime1_end_limit() {
                return time1_end_limit;
            }

            public void setTime1_end_limit(String time1_end_limit) {
                this.time1_end_limit = time1_end_limit;
            }

            public String getTime1_start_limit() {
                return time1_start_limit;
            }

            public void setTime1_start_limit(String time1_start_limit) {
                this.time1_start_limit = time1_start_limit;
            }

            public String getTime3_start_limit() {
                return time3_start_limit;
            }

            public void setTime3_start_limit(String time3_start_limit) {
                this.time3_start_limit = time3_start_limit;
            }

            public String getTime1_start_after_limit() {
                return time1_start_after_limit;
            }

            public void setTime1_start_after_limit(String time1_start_after_limit) {
                this.time1_start_after_limit = time1_start_after_limit;
            }

            public String getTime2_start() {
                return time2_start;
            }

            public void setTime2_start(String time2_start) {
                this.time2_start = time2_start;
            }

            public String getRest1_end() {
                return rest1_end;
            }

            public void setRest1_end(String rest1_end) {
                this.rest1_end = rest1_end;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getTime1_end() {
                return time1_end;
            }

            public void setTime1_end(String time1_end) {
                this.time1_end = time1_end;
            }
        }
    }
}
