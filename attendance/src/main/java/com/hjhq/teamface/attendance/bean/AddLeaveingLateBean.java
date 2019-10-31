package com.hjhq.teamface.attendance.bean;

/**
 * Created by Administrator on 2018/7/26.
 * Describe：
 */

public class AddLeaveingLateBean {

    /**
     * id : 1
     * remindCockBeforeWork : 15
     * remindClockAfterWork : 30
     */

    //晚走时间
    private String nigthwalkmin;
    //晚到时间
    private String lateMin;


    public String getNigthwalkmin() {
        return nigthwalkmin;
    }

    public void setNigthwalkmin(String nigthwalkmin) {
        this.nigthwalkmin = nigthwalkmin;
    }

    public String getLateMin() {
        return lateMin;
    }

    public void setLateMin(String lateMin) {
        this.lateMin = lateMin;
    }
}
