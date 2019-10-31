package com.hjhq.teamface.custom.bean;

/**
 * Created by Administrator on 2018/9/29.
 * Describe：
 */

public class TabListDataReqBean {
    String sorceBean;
    String targetBean;
    Long id;
    Integer type;

    public String getSorceBean() {
        return sorceBean;
    }

    public void setSorceBean(String sorceBean) {
        this.sorceBean = sorceBean;
    }

    public String getTargetBean() {
        return targetBean;
    }

    public void setTargetBean(String targetBean) {
        this.targetBean = targetBean;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
