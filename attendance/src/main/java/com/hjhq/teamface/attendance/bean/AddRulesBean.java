package com.hjhq.teamface.attendance.bean;

import java.util.List;

/**
 * Created by Administrator on 2018/6/27.
 * Describe：
 */

public class AddRulesBean {

    /**
     * name : 考勤组1-1
     * mustAttendance : [{"name":"名字","id":"4","picture":"","type":1,"value":"1-4"}]
     * noAttendance :
     * attendanceType : 0
     * autoStatus : 0
     * mustPunchcardDate : [[{"name":"必须打卡日期","id":"21","type":"3","time":"1529995805","label":"班3-2:07:00-14:00;12:00-15:00;16:00-18:00"},{"name":"必须打卡日期","id":"21","type":"3","time":"1529999805","label":"班3-2:07:00-14:00;12:00-15:00;16:00-18:00"}]]
     * noPunchcardDate : 1529995805,1530995805,1531995805
     * reslutData : [{"name":"星期一","id":"21","type":"1","label":"班3-2:07:00-14:00;12:00-15:00;16:00-18:00"},{"name":"星期二","id":"21","type":"1","label":"班3-2:07:00-14:00;12:00-15:00;16:00-18:00"},{"name":"星期三","id":"21","type":"1","label":"班3-2:07:00-14:00;12:00-15:00;16:00-18:00"},{"name":"星期四","id":"21","type":"1","label":"班3-2:07:00-14:00;12:00-15:00;16:00-18:00"},{"name":"星期五","id":"21","type":"1","label":"班3-2:07:00-14:00;12:00-15:00;16:00-18:00;"},{"name":"星期六","id":"","type":"2","label":"休息"},{"name":"星期日","id":"","type":"2","label":"休息"}]
     * attendanceStartTime : 1529995805
     * attendanceAddress : [{"create_by":1,"effective_range":"1000","address":"广东省深圳市南山区科技园高新南一道21-2号","create_time":1529658127739,"modify_time":1529660101309,"name":"思创科技大厦","location":[{"address":"广东省深圳市南山区科技园高新南一道21-2号","lng":113.946318,"lat":22.538232}],"del_status":"0","id":31,"modify_by":1,"attendance_status":"0","attendance_type":"0"}]
     * attendanceWifi : [{"create_by":1,"effective_range":"","address":"14:75:90:95:b2:ca","create_time":1529655027235,"modify_time":"","name":"YPJ8888","location":[],"del_status":"0","id":26,"modify_by":"","attendance_status":"0","attendance_type":"1"}]
     * outwokerStatus : 0
     * faceStatus : 0
     */
    private String id;
    private String name;
    //考勤类型，0:固定班次，1排班制，2：自由打卡
    private String attendanceType;
    private String autoStatus;
    private List<TimeBean> noPunchcardDate;
    private String attendanceStartTime;
    //人脸识别打卡，0:未开启，1:开启
    private String faceStatus;
    private List<MemberOrDepartmentBean> attendanceusers;
    private List<MemberOrDepartmentBean> excludedusers;
    private List<AddDateBean> mustPunchcardDate;
    private List<Long> attendanceAddress;
    private List<Long> attendanceWIFI;

    private List<Long> workdaylist;
    //节假日自动排休，0：关闭，1:开启
    private int holidayAutoStatus;
    //外勤打卡0，未开启，1开启
    private int outworkerStatus;
    //规则生效日期，时间戳，为0表示立即生效，明日生效，传明日凌晨的时间戳
    private Long effectiveDate;

    public List<MemberOrDepartmentBean> getAttendanceusers() {
        return attendanceusers;
    }

    public void setAttendanceusers(List<MemberOrDepartmentBean> attendanceusers) {
        this.attendanceusers = attendanceusers;
    }

    public List<MemberOrDepartmentBean> getExcludedusers() {
        return excludedusers;
    }

    public void setExcludedusers(List<MemberOrDepartmentBean> excludedusers) {
        this.excludedusers = excludedusers;
    }

    public List<Long> getWorkdaylist() {
        return workdaylist;
    }

    public void setWorkdaylist(List<Long> workdaylist) {
        this.workdaylist = workdaylist;
    }

    public int getHolidayAutoStatus() {
        return holidayAutoStatus;
    }

    public void setHolidayAutoStatus(int holidayAutoStatus) {
        this.holidayAutoStatus = holidayAutoStatus;
    }

    public int getOutworkerStatus() {
        return outworkerStatus;
    }

    public void setOutworkerStatus(int outworkerStatus) {
        this.outworkerStatus = outworkerStatus;
    }

    public Long getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Long effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

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


    public String getAttendanceType() {
        return attendanceType;
    }

    public void setAttendanceType(String attendanceType) {
        this.attendanceType = attendanceType;
    }

    public String getAutoStatus() {
        return autoStatus;
    }

    public void setAutoStatus(String autoStatus) {
        this.autoStatus = autoStatus;
    }

    public List<TimeBean> getNoPunchcardDate() {
        return noPunchcardDate;
    }

    public void setNoPunchcardDate(List<TimeBean> noPunchcardDate) {
        this.noPunchcardDate = noPunchcardDate;
    }

    public String getAttendanceStartTime() {
        return attendanceStartTime;
    }

    public void setAttendanceStartTime(String attendanceStartTime) {
        this.attendanceStartTime = attendanceStartTime;
    }


    public String getFaceStatus() {
        return faceStatus;
    }

    public void setFaceStatus(String faceStatus) {
        this.faceStatus = faceStatus;
    }


    public List<AddDateBean> getMustPunchcardDate() {
        return mustPunchcardDate;
    }

    public void setMustPunchcardDate(List<AddDateBean> mustPunchcardDate) {
        this.mustPunchcardDate = mustPunchcardDate;
    }

    public List<Long> getAttendanceAddress() {
        return attendanceAddress;
    }

    public void setAttendanceAddress(List<Long> attendanceAddress) {
        this.attendanceAddress = attendanceAddress;
    }

    public List<Long> getAttendanceWifi() {
        return attendanceWIFI;
    }

    public void setAttendanceWifi(List<Long> attendanceWifi) {
        this.attendanceWIFI = attendanceWifi;
    }


}
