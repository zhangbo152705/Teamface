package com.hjhq.teamface.project.bean;

import java.util.List;

public class FilterDataBean {
    private List<ConditionBean> condition;
    private String beanName;
    private String bean;

    public List<ConditionBean> getCondition() {
        return condition;
    }

    public void setCondition(List<ConditionBean> condition) {
        this.condition = condition;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getBean() {
        return bean;
    }

    public void setBean(String bean) {
        this.bean = bean;
    }
}