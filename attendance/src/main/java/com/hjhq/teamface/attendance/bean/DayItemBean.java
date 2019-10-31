package com.hjhq.teamface.attendance.bean;

import java.io.Serializable;

public class DayItemBean implements Serializable {
    /**
     * punchcardState : 1
     * punchcardType : 1
     * isWay : 0
     * isOutworker : 0
     * isWayInfo : 广东省深圳市南山区高新南一道58号靠近思创科技大厦
     * expectPunchcardTime : 1552525200000
     * realPunchcardTime : 1552528900058
     */

    private String punchcardState;
    private String punchcardType;
    private String isWay;
    private String isOutworker;
    private String isWayInfo;
    private String expectPunchcardTime;
    private String realPunchcardTime;

    public String getPunchcardState() {
        return punchcardState;
    }

    public void setPunchcardState(String punchcardState) {
        this.punchcardState = punchcardState;
    }

    public String getPunchcardType() {
        return punchcardType;
    }

    public void setPunchcardType(String punchcardType) {
        this.punchcardType = punchcardType;
    }

    public String getIsWay() {
        return isWay;
    }

    public void setIsWay(String isWay) {
        this.isWay = isWay;
    }

    public String getIsOutworker() {
        return isOutworker;
    }

    public void setIsOutworker(String isOutworker) {
        this.isOutworker = isOutworker;
    }

    public String getIsWayInfo() {
        return isWayInfo;
    }

    public void setIsWayInfo(String isWayInfo) {
        this.isWayInfo = isWayInfo;
    }

    public String getExpectPunchcardTime() {
        return expectPunchcardTime;
    }

    public void setExpectPunchcardTime(String expectPunchcardTime) {
        this.expectPunchcardTime = expectPunchcardTime;
    }

    public String getRealPunchcardTime() {
        return realPunchcardTime;
    }

    public void setRealPunchcardTime(String realPunchcardTime) {
        this.realPunchcardTime = realPunchcardTime;
    }
}