package com.hjhq.teamface.attendance.bean;

import java.util.List;

public class MonthDataItem {
    /**
     * date : 14
     * groupName : 采购部
     * attendanceList : [{"punchcardState":1,"punchcardType":1,"isWay":"0","isOutworker":"0","isWayInfo":"广东省深圳市南山区高新南一道58号靠近思创科技大厦","expectPunchcardTime":1552525200000,"realPunchcardTime":1552528900058},{"punchcardState":2,"punchcardType":2,"isWay":"0","isOutworker":"0","isWayInfo":"广东省深圳市南山区高新南一道58号靠近思创科技大厦","expectPunchcardTime":1552525200000,"realPunchcardTime":1552528907549}]
     * state : 1
     */

    private String date;
    private String groupName;
    private String state;
    private List<DayItemBean> attendanceList;
    boolean isToday;
    boolean selected;
    int dataState;

    public int getDataState() {
        return dataState;
    }

    public void setDataState(int dataState) {
        this.dataState = dataState;
    }

    public boolean isToday() {
        return isToday;
    }

    public void setToday(boolean today) {
        isToday = today;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<DayItemBean> getAttendanceList() {
        return attendanceList;
    }

    public void setAttendanceList(List<DayItemBean> attendanceList) {
        this.attendanceList = attendanceList;
    }


}