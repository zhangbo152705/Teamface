package com.hjhq.teamface.attendance.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/3/8.
 * Describe：
 */

public class AttendanceDataItemBean implements Serializable{

    /**
     * id : 3
     * expect_punchcard_time : 12346
     * real_punchcard_time : 12346
     * punchcard_type : 1
     * punchcard_result : 1
     * punchcard_status : 1
     * punchcard_address : 1
     * is_outworker : 1
     * is_way : 1
     */

    private String id;
    private String expect_punchcard_time;
    private String real_punchcard_time;
    private String punchcard_type;
    private String punchcard_result;
    private String punchcard_status;
    private String punchcard_address;
    private String is_outworker;
    private String is_way;
    private boolean canUpdate;
    private String bean_name;
    private String data_id;
    private String remark;
    private String photo;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public boolean isCanUpdate() {
        return canUpdate;
    }

    public void setCanUpdate(boolean canUpdate) {
        this.canUpdate = canUpdate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExpect_punchcard_time() {
        return expect_punchcard_time;
    }

    public void setExpect_punchcard_time(String expect_punchcard_time) {
        this.expect_punchcard_time = expect_punchcard_time;
    }

    public String getReal_punchcard_time() {
        return real_punchcard_time;
    }

    public void setReal_punchcard_time(String real_punchcard_time) {
        this.real_punchcard_time = real_punchcard_time;
    }

    public String getPunchcard_type() {
        return punchcard_type;
    }

    public void setPunchcard_type(String punchcard_type) {
        this.punchcard_type = punchcard_type;
    }

    public String getPunchcard_result() {
        return punchcard_result;
    }

    public void setPunchcard_result(String punchcard_result) {
        this.punchcard_result = punchcard_result;
    }

    public String getPunchcard_status() {
        return punchcard_status;
    }

    public void setPunchcard_status(String punchcard_status) {
        this.punchcard_status = punchcard_status;
    }

    public String getPunchcard_address() {
        return punchcard_address;
    }

    public void setPunchcard_address(String punchcard_address) {
        this.punchcard_address = punchcard_address;
    }

    public String getIs_outworker() {
        return is_outworker;
    }

    public void setIs_outworker(String is_outworker) {
        this.is_outworker = is_outworker;
    }

    public String getIs_way() {
        return is_way;
    }

    public void setIs_way(String is_way) {
        this.is_way = is_way;
    }

    public String getBean_name() {
        return bean_name;
    }

    public void setBean_name(String bean_name) {
        this.bean_name = bean_name;
    }

    public String getData_id() {
        return data_id;
    }

    public void setData_id(String data_id) {
        this.data_id = data_id;
    }
}
