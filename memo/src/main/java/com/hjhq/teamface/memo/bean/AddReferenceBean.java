package com.hjhq.teamface.memo.bean;

/**
 * Created by Administrator on 2018-12-25.
 * Describe：知识库提交引用数据
 */

public class AddReferenceBean {
    private String bean_name;
    private String relation_id;
    private String projectId;

    public String getBean_name() {
        return bean_name;
    }

    public void setBean_name(String bean_name) {
        this.bean_name = bean_name;
    }

    public String getRelation_id() {
        return relation_id;
    }

    public void setRelation_id(String relation_id) {
        this.relation_id = relation_id;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
}
