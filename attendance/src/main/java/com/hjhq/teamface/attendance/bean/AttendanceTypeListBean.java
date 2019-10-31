package com.hjhq.teamface.attendance.bean;

import com.hjhq.teamface.basis.constants.AttendanceConstants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AttendanceTypeListBean extends BaseSelectBean {
    /**
     * create_by : 1
     * effective_range : 100
     * address : 广东省深圳市南山区科技园高新南一道21-2号
     * create_time : 1529650771591
     * modify_time :
     * name : 公司2
     * location : [{"address":"广东省深圳市南山区科技园高新南一道21-2号","lng":113.946318,"lat":22.538232}]
     * del_status : 0
     * id : 13
     * modify_by :
     * attendance_status : 0
     * attendance_type : 0
     */

    private String create_by;
    private String effective_range;
    private String address;
    private String create_time;
    private String modify_time;
    private String name;
    private String del_status;
    private String id;
    private String modify_by;
    private String attendance_status;
    private String attendance_type;
    private ArrayList<LocationBean> location;

    //zzh->ad:新增字段
    private String chinese_name;
    private long start_time;
    private long end_time;
    private String bean_name;
    private String data_id;
    private String relevance_status;//0("缺卡"), 1("请假"), 2("加班"), 3("出差"), 4("销假"), 5("外出");
    private String employee_name;



    @Override
    public int getItemType() {
        if (getLocation() == null || getLocation().size() <= 0) {
            return AttendanceConstants.TYPE_SELECT_WIFI;
        } else {
            return AttendanceConstants.TYPE_SELECT_LOCATION;
        }
    }

    public String getCreate_by() {
        return create_by;
    }

    public void setCreate_by(String create_by) {
        this.create_by = create_by;
    }

    public String getEffective_range() {
        return effective_range;
    }

    public void setEffective_range(String effective_range) {
        this.effective_range = effective_range;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getModify_time() {
        return modify_time;
    }

    public void setModify_time(String modify_time) {
        this.modify_time = modify_time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDel_status() {
        return del_status;
    }

    public void setDel_status(String del_status) {
        this.del_status = del_status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModify_by() {
        return modify_by;
    }

    public void setModify_by(String modify_by) {
        this.modify_by = modify_by;
    }

    public String getAttendance_status() {
        return attendance_status;
    }

    public void setAttendance_status(String attendance_status) {
        this.attendance_status = attendance_status;
    }

    public String getAttendance_type() {
        return attendance_type;
    }

    public void setAttendance_type(String attendance_type) {
        this.attendance_type = attendance_type;
    }

    public List<LocationBean> getLocation() {
        return location;
    }

    public void setLocation(ArrayList<LocationBean> location) {
        this.location = location;
    }

    public String getChinese_name() {
        return chinese_name;
    }

    public long getStart_time() {
        return start_time;
    }

    public long getEnd_time() {
        return end_time;
    }

    public void setChinese_name(String chinese_name) {
        this.chinese_name = chinese_name;
    }

    public void setStart_time(long start_time) {
        this.start_time = start_time;
    }

    public void setEnd_time(long end_time) {
        this.end_time = end_time;
    }

    public String getBean_name() {
        return bean_name;
    }

    public String getData_id() {
        return data_id;
    }

    public String getRelevance_status() {
        return relevance_status;
    }

    public String getEmployee_name() {
        return employee_name;
    }

    public void setBean_name(String bean_name) {
        this.bean_name = bean_name;
    }

    public void setData_id(String data_id) {
        this.data_id = data_id;
    }

    public void setRelevance_status(String relevance_status) {
        this.relevance_status = relevance_status;
    }

    public void setEmployee_name(String employee_name) {
        this.employee_name = employee_name;
    }

    public static class LocationBean implements Serializable {
        /**
         * address : 广东省深圳市南山区科技园高新南一道21-2号
         * lng : 113.946318
         * lat : 22.538232
         */

        private String address;
        private String lng;
        private String lat;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getLng() {
            return lng;
        }

        public void setLng(String lng) {
            this.lng = lng;
        }

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }
    }
}