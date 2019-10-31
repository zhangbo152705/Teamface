package com.hjhq.teamface.attendance.bean;

import com.hjhq.teamface.basis.constants.AttendanceConstants;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/6/27.
 * Describe：
 */

public class AddDateBean extends BaseSelectBean {

    /**
     * name : 必须打卡日期
     * id : 21
     * type : 3
     * time : 1529995805
     * label : 班3-2:07:00-14:00;12:00-15:00;16:00-18:00
     */

    private String name;
    private String id;
    // type   1上班,2休息,3必须打卡,4不用打卡
    private String type;
    private String time;
    private String label;
    private String attendance_time;
    private String attendance_number;
    //0固定班制,1排班制,2自由工时
    private String attendance_type;
    private String class_type;
    private String class_desc;
    private String total_working_hours;
    private String create_time;
    private String create_by;
    private String memeber_number = "";
    private ArrayList<SelectedClass> selected_class;

    public ArrayList<SelectedClass> getSelected_class() {
        return selected_class;
    }

    public void setSelected_class(ArrayList<SelectedClass> selected_class) {
        this.selected_class = selected_class;
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

    public String getTotal_working_hours() {
        return total_working_hours;
    }

    public void setTotal_working_hours(String total_working_hours) {
        this.total_working_hours = total_working_hours;
    }

    public String getAttendance_time() {
        return attendance_time;
    }

    public void setAttendance_time(String attendance_time) {
        this.attendance_time = attendance_time;
    }

    @Override
    public int getItemType() {
        return AttendanceConstants.TYPE_SELECT_TIME;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getAttendance_number() {
        return attendance_number;
    }

    public void setAttendance_number(String attendance_number) {
        this.attendance_number = attendance_number;
    }

    public String getAttendance_type() {
        return attendance_type;
    }

    public void setAttendance_type(String attendance_type) {
        this.attendance_type = attendance_type;
    }

    public static class SelectedClass {

        /**
         * class_desc : 09:00-18:00
         * name : WEB
         * id : 9
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
