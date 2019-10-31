package com.hjhq.teamface.custom.bean;

import java.util.List;

/**
 * @author Administrator
 * @date 2017/12/25
 */

public class TransformationRequestBean {
    private String dataId;
    private String bean;
    private List<String> ids;

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String moduleId) {
        this.dataId = moduleId;
    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    public String getBean() {
        return bean;
    }

    public void setBean(String bean) {
        this.bean = bean;
    }
}
