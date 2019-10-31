package com.hjhq.teamface.attendance.bean;

import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.database.Member;

import java.util.List;

/**
 * Created by Administrator on 2019/3/7.
 * Describe：
 */

public class AttendanceGroupDetailBean extends BaseBean {

    /**
     * data : {"no_punchcard_date":[],"excluded_group_users":"1-50,1-19,1-39,null,1-1,1-29,1-14,1-31,1-43,1-21,1-54,1-52","memeber_number":2,"create_time":1551867535351,"selected_class":[{"class_desc":"09:00-16:35","name":"自动下班","id":35}],"attendance_users":[{"sign_id":0,"name":"基拉","id":9,"type":1,"value":"1-9","picture":""},{"sign_id":0,"name":"杨联刚","id":6,"type":1,"value":"1-6","picture":""}],"outworker_status":0,"modify_time":1551954463060,"must_punchcard_date":[],"face_status":0,"del_status":"0","modify_by":18,"attendance_start_time":"","create_by":31,"holiday_auto_status":0,"name":"自动下班","work_day_list":[0,0,35,35,0,0,0],"attendance_address":[{"create_by":18,"effective_range":100,"address":"广东省深圳市南山区深南大道9028号益田假日广场B2层B2-25-26","create_time":1551863330286,"modify_time":"","name":"噩梦","way_type":"0","location":[{"address":"广东省深圳市南山区深南大道9028号益田假日广场B2层B2-25-26","lng":113.975273,"lat":22.53766}],"del_status":"0","id":33,"modify_by":"","way_status":"0"}],"effective_date":0,"id":45,"attendance_wifi":[],"excluded_users":[],"attendance_type":0}
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
         * no_punchcard_date : []
         * excluded_group_users : 1-50,1-19,1-39,null,1-1,1-29,1-14,1-31,1-43,1-21,1-54,1-52
         * memeber_number : 2
         * create_time : 1551867535351
         * selected_class : [{"class_desc":"09:00-16:35","name":"自动下班","id":35}]
         * attendance_users : [{"sign_id":0,"name":"基拉","id":9,"type":1,"value":"1-9","picture":""},{"sign_id":0,"name":"杨联刚","id":6,"type":1,"value":"1-6","picture":""}]
         * outworker_status : 0
         * modify_time : 1551954463060
         * must_punchcard_date : []
         * face_status : 0
         * del_status : 0
         * modify_by : 18
         * attendance_start_time :
         * create_by : 31
         * holiday_auto_status : 0
         * name : 自动下班
         * work_day_list : [0,0,35,35,0,0,0]
         * attendance_address : [{"create_by":18,"effective_range":100,"address":"广东省深圳市南山区深南大道9028号益田假日广场B2层B2-25-26","create_time":1551863330286,"modify_time":"","name":"噩梦","way_type":"0","location":[{"address":"广东省深圳市南山区深南大道9028号益田假日广场B2层B2-25-26","lng":113.975273,"lat":22.53766}],"del_status":"0","id":33,"modify_by":"","way_status":"0"}]
         * effective_date : 0
         * id : 45
         * attendance_wifi : []
         * excluded_users : []
         * attendance_type : 0
         */

        private String excluded_group_users;
        private String memeber_number;
        private String create_time;
        private String outworker_status;
        private String modify_time;
        private String face_status;
        private String del_status;
        private String modify_by;
        private String attendance_start_time;
        private String create_by;
        private String holiday_auto_status;
        private String name;
        private String effective_date;
        private String id;
        private String attendance_type;
        private List<TimeBean> no_punchcard_date;
        private List<SelectedClass> selected_class;
        private List<Member> attendance_users;
        private List<AddDateBean> must_punchcard_date;
        private List<Long> work_day_list;
        private List<AttendanceTypeListBean> attendance_address;
        private List<AttendanceTypeListBean> attendance_wifi;
        private List<Member> excluded_users;

        public String getExcluded_group_users() {
            return excluded_group_users;
        }

        public void setExcluded_group_users(String excluded_group_users) {
            this.excluded_group_users = excluded_group_users;
        }

        public String getMemeber_number() {
            return memeber_number;
        }

        public void setMemeber_number(String memeber_number) {
            this.memeber_number = memeber_number;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public String getOutworker_status() {
            return outworker_status;
        }

        public void setOutworker_status(String outworker_status) {
            this.outworker_status = outworker_status;
        }

        public String getModify_time() {
            return modify_time;
        }

        public void setModify_time(String modify_time) {
            this.modify_time = modify_time;
        }

        public String getFace_status() {
            return face_status;
        }

        public void setFace_status(String face_status) {
            this.face_status = face_status;
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

        public String getAttendance_start_time() {
            return attendance_start_time;
        }

        public void setAttendance_start_time(String attendance_start_time) {
            this.attendance_start_time = attendance_start_time;
        }

        public String getCreate_by() {
            return create_by;
        }

        public void setCreate_by(String create_by) {
            this.create_by = create_by;
        }

        public String getHoliday_auto_status() {
            return holiday_auto_status;
        }

        public void setHoliday_auto_status(String holiday_auto_status) {
            this.holiday_auto_status = holiday_auto_status;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEffective_date() {
            return effective_date;
        }

        public void setEffective_date(String effective_date) {
            this.effective_date = effective_date;
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

        public List<TimeBean> getNo_punchcard_date() {
            return no_punchcard_date;
        }

        public void setNo_punchcard_date(List<TimeBean> no_punchcard_date) {
            this.no_punchcard_date = no_punchcard_date;
        }

        public List<SelectedClass> getSelected_class() {
            return selected_class;
        }

        public void setSelected_class(List<SelectedClass> selected_class) {
            this.selected_class = selected_class;
        }

        public List<Member> getAttendance_users() {
            return attendance_users;
        }

        public void setAttendance_users(List<Member> attendance_users) {
            this.attendance_users = attendance_users;
        }

        public List<AddDateBean> getMust_punchcard_date() {
            return must_punchcard_date;
        }

        public void setMust_punchcard_date(List<AddDateBean> must_punchcard_date) {
            this.must_punchcard_date = must_punchcard_date;
        }

        public List<Long> getWork_day_list() {
            return work_day_list;
        }

        public void setWork_day_list(List<Long> work_day_list) {
            this.work_day_list = work_day_list;
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

        public List<Member> getExcluded_users() {
            return excluded_users;
        }

        public void setExcluded_users(List<Member> excluded_users) {
            this.excluded_users = excluded_users;
        }


    }

    public static class SelectedClass {

        /**
         * class_desc : 09:00-18:00
         * name : 亮1
         * id : 29
         */

        private String class_desc;
        private String name;
        private String id;

        public String getClass_desc() {
            return class_desc;
        }

        public void setClass_desc(String class_desc) {
            this.class_desc = class_desc;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
