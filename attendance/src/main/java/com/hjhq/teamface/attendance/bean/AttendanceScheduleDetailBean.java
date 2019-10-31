package com.hjhq.teamface.attendance.bean;

import com.hjhq.teamface.basis.bean.BaseBean;

import java.util.List;

/**
 * Created by Administrator on 2019/3/7.
 * Describe：
 */

public class AttendanceScheduleDetailBean extends BaseBean {

    /**
     * data : {"classes_arr":[{"class_desc":"09:00-18:00","name":"BC-02","id":14}],"group_id":"","group_name":"","attendance_month":"2019-03"}
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
         * classes_arr : [{"class_desc":"09:00-18:00","name":"BC-02","id":14}]
         * group_id :
         * group_name :
         * attendance_month : 2019-03
         */

        private String group_id;
        private String group_name;
        private String attendance_month;
        private List<ClassesArrBean> classes_arr;

        public String getGroup_id() {
            return group_id;
        }

        public void setGroup_id(String group_id) {
            this.group_id = group_id;
        }

        public String getGroup_name() {
            return group_name;
        }

        public void setGroup_name(String group_name) {
            this.group_name = group_name;
        }

        public String getAttendance_month() {
            return attendance_month;
        }

        public void setAttendance_month(String attendance_month) {
            this.attendance_month = attendance_month;
        }

        public List<ClassesArrBean> getClasses_arr() {
            return classes_arr;
        }

        public void setClasses_arr(List<ClassesArrBean> classes_arr) {
            this.classes_arr = classes_arr;
        }


    }
}
