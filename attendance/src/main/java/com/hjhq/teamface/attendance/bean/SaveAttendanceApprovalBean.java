package com.hjhq.teamface.attendance.bean;

/**
 * 保存或修改自定义数据
 * Created by lx on 2017/9/14.
 */

public class SaveAttendanceApprovalBean {


    /**
     * beanName : bean1234567890
     * relevanceStatus : 0
     * startTimeField : startTimeField
     * endTimeField : endTimeField
     * durationField : durationField
     * durationUnit : 0
     */
    private Long id;
    private String relevance_bean;
    private String relevance_status;
    private String start_time_field;
    private String end_time_field;
    private String duration_field;
    private String duration_unit;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBeanName() {
        return relevance_bean;
    }

    public void setBeanName(String beanName) {
        this.relevance_bean = beanName;
    }

    public String getRelevanceStatus() {
        return relevance_status;
    }

    public void setRelevanceStatus(String relevanceStatus) {
        this.relevance_status = relevanceStatus;
    }

    public String getStartTimeField() {
        return start_time_field;
    }

    public void setStartTimeField(String startTimeField) {
        this.start_time_field = startTimeField;
    }

    public String getEndTimeField() {
        return end_time_field;
    }

    public void setEndTimeField(String endTimeField) {
        this.end_time_field = endTimeField;
    }

    public String getDurationField() {
        return duration_field;
    }

    public void setDurationField(String durationField) {
        this.duration_field = durationField;
    }

    public String getDurationUnit() {
        return duration_unit;
    }

    public void setDurationUnit(String durationUnit) {
        this.duration_unit = durationUnit;
    }
}
