package com.hjhq.teamface.attendance.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.util.TextUtil;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2019-1-4.
 * Describe：
 */

public class AttendanceDayDataBean extends BaseBean {

    /**
     * data : {"dataList":[{"number":1,"dataList":[{"statusList":["迟到"],"post_name":"员工","employee_id":4,"department_name":"神马公司","employee_name":"建华","picture":"","real_punchcard_time":1552266300000,"expect_punchcard_time":1552266000000,"time":1552298400000}],"name":"打卡人数","nopunchClock":{"number":0,"dataList":[{"statusList":["迟到"],"post_name":"员工","employee_id":4,"department_name":"神马公司","employee_name":"建华","picture":"","real_punchcard_time":1552266300000,"expect_punchcard_time":1552266000000,"time":1552298400000}],"name":"未打卡人数","type":8},"type":0}],"attendance_person_number":1}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean implements Serializable {
        /**
         * dataList : [{"number":1,"dataList":[{"statusList":["迟到"],"post_name":"员工","employee_id":4,"department_name":"神马公司","employee_name":"建华","picture":"","real_punchcard_time":1552266300000,"expect_punchcard_time":1552266000000,"time":1552298400000}],"name":"打卡人数","nopunchClock":{"number":0,"dataList":[{"statusList":["迟到"],"post_name":"员工","employee_id":4,"department_name":"神马公司","employee_name":"建华","picture":"","real_punchcard_time":1552266300000,"expect_punchcard_time":1552266000000,"time":1552298400000}],"name":"未打卡人数","type":8},"type":0}]
         * attendance_person_number : 1
         */

        private String attendance_person_number;
        private ArrayList<DataListBean> dataList;

        public String getAttendance_person_number() {
            return attendance_person_number;
        }

        public void setAttendance_person_number(String attendance_person_number) {
            this.attendance_person_number = attendance_person_number;
        }

        public ArrayList<DataListBean> getDataList() {
            return dataList;
        }

        public void setDataList(ArrayList<DataListBean> dataList) {
            this.dataList = dataList;
        }


    }

    public static class DataListBean implements Serializable, MultiItemEntity {
        /**
         * number : 1
         * dataList : [{"statusList":["迟到"],"post_name":"员工","employee_id":4,"department_name":"神马公司","employee_name":"建华","picture":"","real_punchcard_time":1552266300000,"expect_punchcard_time":1552266000000,"time":1552298400000}]
         * name : 打卡人数
         * nopunchClock : {"number":0,"dataList":[{"statusList":["迟到"],"post_name":"员工","employee_id":4,"department_name":"神马公司","employee_name":"建华","picture":"","real_punchcard_time":1552266300000,"expect_punchcard_time":1552266000000,"time":1552298400000}],"name":"未打卡人数","type":8}
         * type : 0
         */

        private String number;
        private String name;
        private ArrayList<String> statusList;
        private DataListBean nopunchClock;
        private String type;
        private ArrayList<DataListBean> employeeList;
        private ArrayList<DataListBean> attendanceList;
        private String real_punchcard_time;
        private String expect_punchcard_time;
        private String time;
        private String employee_id;
        private String department_name;
        private String employee_name;
        private String post_name;
        private String picture;
        private String punchcard_address;
        private String punchcard_type;
        private String start_time;
        private String end_time;
        private String duration;
        private String duration_unit;
        private String attendance_date;
        private String late_time_number;//迟到分钟数
        private String leave_early_time_number;//早退分钟数

        public String getPost_name() {
            return post_name;
        }

        public void setPost_name(String post_name) {
            this.post_name = post_name;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getDuration_unit() {
            return duration_unit;
        }

        public void setDuration_unit(String duration_unit) {
            this.duration_unit = duration_unit;
        }

        public String getAttendance_date() {
            return attendance_date;
        }

        public void setAttendance_date(String attendance_date) {
            this.attendance_date = attendance_date;
        }

        public String getStart_time() {
            return start_time;
        }

        public void setStart_time(String start_time) {
            this.start_time = start_time;
        }

        public String getEnd_time() {
            return end_time;
        }

        public void setEnd_time(String end_time) {
            this.end_time = end_time;
        }

        public String getPunchcard_type() {
            return punchcard_type;
        }

        public void setPunchcard_type(String punchcard_type) {
            this.punchcard_type = punchcard_type;
        }

        public String getPunchcard_address() {
            return punchcard_address;
        }

        public void setPunchcard_address(String punchcard_address) {
            this.punchcard_address = punchcard_address;
        }


        public String getEmployee_id() {
            return employee_id;
        }

        public void setEmployee_id(String employee_id) {
            this.employee_id = employee_id;
        }

        public String getDepartment_name() {
            return department_name;
        }

        public void setDepartment_name(String department_name) {
            this.department_name = department_name;
        }

        public String getEmployee_name() {
            return employee_name;
        }

        public void setEmployee_name(String employee_name) {
            this.employee_name = employee_name;
        }

        public String getPicture() {
            return picture;
        }

        public void setPicture(String picture) {
            this.picture = picture;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public ArrayList<String> getStatusList() {
            return statusList;
        }

        public void setStatusList(ArrayList<String> statusList) {
            this.statusList = statusList;
        }

        public DataListBean getNopunchClock() {
            return nopunchClock;
        }

        public void setNopunchClock(DataListBean nopunchClock) {
            this.nopunchClock = nopunchClock;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public ArrayList<DataListBean> getEmployeeList() {
            return employeeList;
        }

        public void setEmployeeList(ArrayList<DataListBean> employeeList) {
            this.employeeList = employeeList;
        }

        public ArrayList<DataListBean> getAttendanceList() {
            return attendanceList;
        }

        public void setAttendanceList(ArrayList<DataListBean> attendanceList) {
            this.attendanceList = attendanceList;
        }

        public String getReal_punchcard_time() {
            return real_punchcard_time;
        }

        public void setReal_punchcard_time(String real_punchcard_time) {
            this.real_punchcard_time = real_punchcard_time;
        }

        public String getExpect_punchcard_time() {
            return expect_punchcard_time;
        }

        public void setExpect_punchcard_time(String expect_punchcard_time) {
            this.expect_punchcard_time = expect_punchcard_time;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        @Override
        public int getItemType() {
            return TextUtil.parseInt(getType());
        }

        public String getLate_time_number() {
            return late_time_number;
        }

        public String getLeave_early_time_number() {
            return leave_early_time_number;
        }

        public void setLate_time_number(String late_time_number) {
            this.late_time_number = late_time_number;
        }

        public void setLeave_early_time_number(String leave_early_time_number) {
            this.leave_early_time_number = leave_early_time_number;
        }
    }
}
